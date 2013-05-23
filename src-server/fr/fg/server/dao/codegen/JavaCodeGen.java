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

package fr.fg.server.dao.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import fr.fg.server.dao.DataLayer;
import fr.fg.server.dao.DbMapping;
import fr.fg.server.dao.DbMappingParser;
import fr.fg.server.dao.db.Column;
import fr.fg.server.dao.db.DbTools;
import fr.fg.server.dao.db.Table;

public class JavaCodeGen {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static String[]
		PRIMITIVE_NUMERIC_TYPES = {"byte", "short", "int", "long", "float", "double"},
		OBJECT_NUMERIC_TYPES = {
			"java.lang.Byte", "java.lang.Short", "java.lang.Integer",
			"java.lang.Long", "java.lang.Float", "java.lang.Double",
		};
	
	private final static JavaCodeGen INSTANCE = new JavaCodeGen();
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private JavaCodeGen() {
		// Constructeur privé
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final static JavaCodeGen getInstance() {
		return INSTANCE;
	}
	
	public void generate(String dbMapping, String srcFolder,
			String targetPackage, String classSuffix, String classHeader)
			throws Exception {
		DbMapping mapping = DbMappingParser.parse(DataLayer.class.getClassLoader(
			).getResource(dbMapping));
		
		String baseFolder = srcFolder + File.separator +
			mapping.getBasePackage().replace(".", File.separator) +
			File.separator + targetPackage;
		
		File file = new File(baseFolder);
		if (!file.exists())
			file.mkdirs();
		
		for (Table table : mapping.getTables()) {
			PrintWriter writer = new PrintWriter(new FileOutputStream(
				baseFolder + File.separator + table.getMappedClass().getSimpleName() +
				classSuffix + ".java"));
			
			writer.println(classHeader);
			writer.println();
			
			writer.println("package " + mapping.getBasePackage() +
				"." + targetPackage + ";");
			writer.println();
			writer.println("import fr.fg.server.dao.PersistentData;");
			writer.println();
			writer.println("public class " + table.getMappedClass().getSimpleName() +
				classSuffix + " extends PersistentData {");
			
			// Génération des constantes pour les valeurs énumérées
			writer.println("\t// ------------------------------------------------------- CONSTANTES -- //");
			
			boolean hasConstants = false;
			
			for (Column column : table.getColumns()) {
				if (column.isEnum()) {
					hasConstants = true;
					
					writer.println("\t");
					writer.println("\tpublic final static String");
					
					for (int i = 0; i < column.getEnumValues().length; i++) {
						String value = column.getEnumValues()[i];
						writer.println("\t\t" + column.getName().toUpperCase() +
								"_" + value.toUpperCase() + " = \"" + value + "\"" +
								(i == column.getEnumValues().length - 1 ? ";" : ","));
					}
				}
			}
			
			if (hasConstants)
				writer.println("\t");
			
			// Génération des attributs
			writer.println("\t// -------------------------------------------------------- ATTRIBUTS -- //");
			
			if (table.getColumns().size() > 0)
				writer.println("\t");
			
			for (Column column : table.getColumns())
				writer.println("\tprivate " + truncateJavaType(
					column.getJavaType()) + " " + column.getJavaName() + ";");

			if (table.getColumns().size() > 0)
				writer.println("\t");
			
			writer.println("\t// ---------------------------------------------------- CONSTRUCTEURS -- //");

			// Génération des getters/setters
			writer.println("\t// --------------------------------------------------------- METHODES -- //");
			
			for (Column column : table.getColumns()) {
				String javaType = column.getJavaType();
				String javaName = column.getJavaName();
				String javaClassName = DbTools.getJavaClassName(column.getName());
				
				// Génération du getter
				writer.println("\t");
				writer.println("\tpublic " + truncateJavaType(javaType) +
					" " + (javaType.equals("boolean") ||
					javaType.equals("java.lang.Boolean") ? "is" : "get") +
					javaClassName + "() {");
				writer.println("\t\treturn " + javaName + ";");
				writer.println("\t}");
				
				// Génération du setter
				writer.println("\t");
				writer.println("\tpublic void set" + javaClassName + "(" +
					truncateJavaType(javaType) + " " + javaName + ") {");
				writer.println("\t\tif (!isEditable())");
				writer.println("\t\t\tthrowDataUneditableException();");
				writer.println("\t\t");
				
				boolean firstIf = true;
				
				// Cas valeur null
				if (isJavaTypeNullable(javaType)) {
					if (column.isNullable()) {
						if (column.isEnum()) {
							writer.println("\t\tif (" + javaName + " == null)");
							writer.println("\t\t\tthis." + javaName + " = null;");
							
							firstIf = false;
						}
					} else {
						writer.println("\t\tif (" + javaName + " == null)");
						writer.println("\t\t\tthrow new " +
							"IllegalArgumentException(" +
							"\"Invalid value: '\" +");
						writer.println("\t\t\t\t" + javaName +
							" + \"' (must not be null).\");");
						
						firstIf = false;
					}
				}
				
				if (column.isEnum()) {
					// Cas valeurs enumérées
					for (String value : column.getEnumValues()) {
						String constantName = column.getName().toUpperCase() +
						"_" + value.toUpperCase();
						writer.println("\t\t" + (firstIf ? "" : "else ") +
								"if (" + javaName + ".equals(" + constantName + "))");
						writer.println("\t\t\tthis." + javaName + " = " + constantName + ";");
						
						firstIf = false;
					}
					
					// Cas autre valeur
					writer.println("\t\telse");
					writer.println("\t\t\tthrow new IllegalArgumentException(");
					writer.println("\t\t\t\t\"Invalid value: '\" + " + javaName + " + \"'.\");");
				} else {
					// Valeur signée
					if (!column.isSigned() && isNumericType(javaType)) {
						writer.println("\t\t" + (firstIf ? "" : "else ") +
							"if (" + javaName + " < 0)");
						writer.println("\t\t\tthrow new IllegalArgumentException(" +
							"\"Invalid value: '\" +");
						writer.println("\t\t\t\t" + javaName +
							" + \"' (must be >= 0).\");");
						
						firstIf = false;
					}
					
					if (!firstIf)
						writer.println("\t\telse");
					
					writer.println("\t\t" + (firstIf ? "" : "\t") +
						"this." + javaName + " = " + javaName + ";");
				}
				
				writer.println("\t}");
				
				// Génération de setters additionnels
				if (javaType.equals("byte") || javaType.equals("short")) {
					writer.println("\t");
					writer.println("\tpublic void set" + javaClassName +
						"(int " + javaName + ") {");
					writer.println("\t\tset" + javaClassName +
						"((" + javaType + ") " + javaName + ");");
					writer.println("\t}");
				}
//				if (isPrimitiveNumericType(javaType)) {
//					for (String type : PRIMITIVE_NUMERIC_TYPES)
//						if (!type.equals(javaType)) {
//							writer.println("\t");
//							writer.println("\tpublic void set" + javaClassName + "(" +
//								type + " " + javaName + ") {");
//							writer.println("\t\tset" + javaClassName +
//								"((" + javaType + ") " + javaName + ");");
//							writer.println("\t}");
//						}
//				}
			}
			
			if (table.getColumns().size() > 0)
				writer.println("\t");
			
			writer.println("\t// ------------------------------------------------- METHODES PRIVEES -- //");
			writer.println("}");
			writer.close();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	// Tronque le package java.lang. pour rendre le code plus lisible
	private String truncateJavaType(String javaType) {
		if (javaType.startsWith("java.lang."))
			return javaType.substring("java.lang.".length());
		else
			return javaType;
	}
	
	private boolean isJavaTypeNullable(String javaType) {
		if (javaType.equals("byte") || javaType.equals("short") ||
				javaType.equals("int") || javaType.equals("long") ||
				javaType.equals("float") || javaType.equals("double") ||
				javaType.equals("boolean") || javaType.equals("char"))
			return false;
		else
			return true;
	}
	
	private boolean isNumericType(String javaType) {
		return isPrimitiveNumericType(javaType) ||
			isObjectNumericType(javaType);
	}
	
	private boolean isPrimitiveNumericType(String javaType) {
		for (String primitiveNumericType : PRIMITIVE_NUMERIC_TYPES)
			if (javaType.equals(primitiveNumericType))
				return true;
		
		return false;
	}
	
	private static boolean isObjectNumericType(String javaType) {
		for (String objectNumericType : OBJECT_NUMERIC_TYPES)
			if (javaType.equals(objectNumericType))
				return true;
		
		return false;
	}
}
