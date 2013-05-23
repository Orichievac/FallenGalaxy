/*
Copyright 2010 Jeremie Gottero

This file is part of Fallen Galaxy.

Fallen Galaxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Fallen Galaxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Fallen Galaxy. If not, see <http://www.gnu.org/licenses/>.
*/

package fr.fg.server.dao;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import fr.fg.server.dao.db.Column;
import fr.fg.server.dao.db.ColumnType;
import fr.fg.server.dao.db.DbTools;
import fr.fg.server.dao.db.ForeignKey;
import fr.fg.server.dao.db.Index;
import fr.fg.server.dao.db.PrimaryKey;
import fr.fg.server.dao.db.Table;
import fr.fg.server.util.LoggingSystem;

public class DbMappingParser {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@SuppressWarnings("unchecked")
	public static DbMapping parse(URL url) throws Exception {
		SAXBuilder builder = new SAXBuilder(true);
		Document doc = builder.build(url);
		
		Element rootNode = doc.getRootElement();
		
		DbMapping mapping = new DbMapping();
		mapping.setBasePackage(rootNode.getAttributeValue("package"));
		
		// Liste des tables
		List<Element> tableElements = rootNode.getChildren("table");
		List<ForeignKey> foreignKeys = new LinkedList<ForeignKey>();
		
		for (int i = 0; i < tableElements.size(); i++) {
			Element tableElement = tableElements.get(i);
			String tableName = tableElement.getAttributeValue("name");
			
			try {
				mapping.addTable(parseTable(tableElement,
						mapping.getBasePackage(), foreignKeys));
				LoggingSystem.getServerLogger().trace(
						"Db table registered: '" + tableName + "'.");
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn(
						"Could not load db table: '" + tableName + "'.", e);
			}
		}
		
		// Lie les clés étrangères aux tables qu'elles référencent
		for (ForeignKey foreignKey : foreignKeys) {
			Table table = null;
			for (Table t : mapping.getTables())
				if (t.getName().equals(foreignKey.getReferencedTable())) {
					table = t;
					break;
				}
			
			if (table != null) {
				table.addForeignKey(foreignKey);
				
				// Génère automatiquement un index pour les colonnes qui sont des clés étrangères
				if (foreignKey.getTable().getIndexByColumn(foreignKey.getName()) == null)
					foreignKey.getTable().addIndex(new Index(foreignKey.getName(), Index.MULTIPLE));
			} else {
				// REMIND jgottero logger erreur
			}
		}
		
		return mapping;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	@SuppressWarnings("unchecked")
	private static Table parseTable(Element tableElement, String basePackage,
			List<ForeignKey> foreignKeys) throws Exception {
		String tableName =
			tableElement.getAttributeValue("name").toLowerCase();
		
		// Class Java qui map la table de base de données
		String javaName;
		String classAttribute = tableElement.getAttributeValue("class");
		if (classAttribute == null)
			javaName = DbTools.getJavaFieldName(tableName);
		else
			javaName = classAttribute;
		javaName = javaName.substring(0, 1).toUpperCase() +
			javaName.substring(1);
		String packageAttribute = tableElement.getAttributeValue("package");
		String className;
		if (packageAttribute == null)
			className = basePackage + "." + javaName;
		else
			className = packageAttribute + "." + javaName;
		
		Class<?> mappedClass = Class.forName(className);
		
		if (!PersistentData.class.isAssignableFrom(mappedClass))
			throw new IllegalArgumentException("Class '" + className +
					"' mapping DB table '" + tableName + "' must extends " +
					"fr.fg.server.dao.PersistentData.");
		
		Table table = new Table(tableName,
			(Class<? extends PersistentData>) mappedClass,
			tableElement.getAttribute("cache").getBooleanValue());
		
		// Colonnes de la table
		List<Element> columnElements = tableElement.getChildren("column");
		
		for (int i = 0; i < columnElements.size(); i++) {
			Element columnElement = columnElements.get(i);
			
			String columnName = columnElement.getAttributeValue("name");
			
			String rawColumnType, rawNullable, rawSigned, rawEnumValues,
				javaType, dbType;
			String refType = columnElement.getAttributeValue("reftype");
			
			if (refType != null) {
				// Recherche la colonne référencée
				String[] refTypeSplit = refType.split("\\.");
				if (refTypeSplit.length != 2) {
					LoggingSystem.getServerLogger().warn("Invalid reference " +
						"type for table column " + tableName + "." + columnName +
						": '" + refType + "'.");
					continue;
				}
				
				String refTableName = refTypeSplit[0];
				String refColumnName = refTypeSplit[1];
				
				boolean found = false;
				
				search:for (Element t : (List<Element>) tableElement.
						getParentElement().getChildren("table")) {
					if (t.getAttributeValue("name").equals(refTableName))
						for (Element c : (List<Element>) t.getChildren("column"))
							if (c.getAttributeValue("name").equals(refColumnName)) {
								columnElement = c;
								found = true;
								break search;
							}
				}
					
				if (!found) {
					LoggingSystem.getServerLogger().warn("Reference not " +
						"found for table column " + tableName + "." + columnName +
						": '" + refType + "'.");
					continue;
				}
			}
			
			rawColumnType = columnElement.getAttributeValue("type");
			rawNullable = columnElement.getAttributeValue("nullable");
			rawSigned = columnElement.getAttributeValue("signed");
			rawEnumValues = columnElement.getAttributeValue("enum-values");
			javaType = columnElement.getAttributeValue("java-type");
			dbType = columnElement.getAttributeValue("db-type");
			
			boolean nullable, signed;
			ColumnType columnType = ColumnType.valueOf(rawColumnType);
			
			if (columnType == null) {
				LoggingSystem.getServerLogger().warn("Invalid type for table " +
					"column " + tableName + "." + columnName +
					": '" + rawColumnType + "'.");
				continue;
			}
			
			nullable = Boolean.parseBoolean(rawNullable);
			
			if (rawSigned == null)
				signed = columnType == ColumnType.TIMESTAMP ? false : true;
			else
				signed = Boolean.parseBoolean(rawSigned);
			
			String[] enumValues;
			if (rawEnumValues == null)
				enumValues = new String[0];
			else
				enumValues = rawEnumValues.split(",");
			
			if (javaType == null)
				javaType = Column.getDefaultJavaType(
					columnType, nullable, signed, enumValues);
			
			if (javaType == null) {
				LoggingSystem.getServerLogger().warn("No java type " +
					"found for table column " + tableName + "." + columnName +
					": '" + rawColumnType + "'.");
				continue;
			}
			
			if (dbType == null)
				dbType = Column.getDefaultJavaType(
					columnType, nullable, signed, enumValues);
			
			if (dbType == null) {
				LoggingSystem.getServerLogger().warn("No db type " +
					"found for table column " + tableName + "." + columnName +
					": '" + rawColumnType + "'.");
				continue;
			}
			
			table.addColumn(new Column(columnName, columnType,
					javaType, dbType, nullable, signed, enumValues));
		}
		
		// Clés primaires
		List<Element> primaryKeyElements =
			tableElement.getChildren("primary-key");
		int primaryKeyIndexType = primaryKeyElements.size() == 1 ?
				Index.UNIQUE : Index.MULTIPLE;
		
		for (int i = 0; i < primaryKeyElements.size(); i++) {
			Element primaryKeyElement = primaryKeyElements.get(i);
			String primaryKeyName =
				primaryKeyElement.getAttributeValue("name");
			int primaryKeyGenerator =
				primaryKeyElement.getAttributeValue("generator").equals("none") ?
						PrimaryKey.GENERATOR_NONE : PrimaryKey.GENERATOR_INCREMENT;
			
			table.addPrimaryKey(new PrimaryKey(primaryKeyName, primaryKeyGenerator));
			
			// Génère automatiquement les index pour les clés primaires
			table.addIndex(new Index(primaryKeyName, primaryKeyIndexType));
		}
		
		// Indexes
		List<Element> indexesElements = tableElement.getChildren("index");
		
		for (int i = 0; i < indexesElements.size(); i++) {
			Element indexElement = indexesElements.get(i);
			table.addIndex(new Index(
					indexElement.getAttributeValue("name"),
					indexElement.getAttributeValue("type").equals("unique") ?
							Index.UNIQUE : Index.MULTIPLE));
		}
		
		// Clés étrangères
		List<Element> foreignKeyElements =
			tableElement.getChildren("foreign-key");
		
		for (int i = 0; i < foreignKeyElements.size(); i++) {
			Element foreignKeyElement = foreignKeyElements.get(i);
			Element references = (Element) foreignKeyElement.getChildren(
					"references").get(0);
			String onDelete = references.getAttributeValue("on-delete");
			int action;
			if (onDelete.equals("set-null"))
				action = ForeignKey.SET_NULL;
			else if (onDelete.equals("set-zero"))
				action = ForeignKey.SET_ZERO;
			else if (onDelete.equals("cascade"))
				action = ForeignKey.CASCADE;
			else if (onDelete.equals("restrict"))
				action = ForeignKey.RESTRICT;
			else
				action = ForeignKey.NO_ACTION;
			
			foreignKeys.add(new ForeignKey(
					foreignKeyElement.getAttributeValue("name"),
					table,
					references.getAttributeValue("table"),
					references.getAttributeValue("column"),
					action));
		}
		
		// REMIND jgottero vérifier la cohérence des données
		
		return table;
	}
}
