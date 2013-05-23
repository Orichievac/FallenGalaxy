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

package fr.fg.server.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import fr.fg.server.dao.DataLayer;
import fr.fg.server.dao.DbMapping;
import fr.fg.server.dao.db.Column;
import fr.fg.server.dao.db.Table;
import fr.fg.server.data.DataAccess;
import fr.fg.server.util.Config;
import fr.fg.server.util.DBManager;

public class Main {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public static void main(String[] args) throws Exception {
		// Test code goes here
		Connection connection = DBManager.getConnection();
		DatabaseMetaData metadata = connection.getMetaData();
		
		DbMapping dbMapping = DataLayer.getInstance().getDbMapping();
		
		StringBuffer sql = new StringBuffer();
		
		for (Table table : dbMapping.getTables()) {
			String tableName = Config.getTablePrefix() + table.getName();
			
			// Vérifie que la table existe
			ResultSet result = metadata.getTables(null, null, tableName, null);
			boolean existingTable = true;
			
			if (!result.next()) {
				// Table inexistante
				existingTable = false;
				sql.append("CREATE TABLE " + tableName + " (\n");
			}
			
			// Vérifie que l'ensemble des colonnes de la table existent
			for (int i = 0; i < table.getColumns().size(); i++) {
				Column column = table.getColumns().get(i);
				
				if (existingTable) {
					result = metadata.getColumns(null, null, tableName, column.getName());
					
					if (!result.next()) {
						// Colonne manquante dans la table
						sql.append("ALTER TABLE " + tableName + " ADD " + column.getName() + " " +
							 " " + getFullDbType(column) +  (i == 0 ?
							"FIRST" : "AFTER " + table.getColumns().get(i - 1)) + ";\n");
					} else {
						// Vérifie que le type de la colonne est valide
						String type = result.getString("TYPE_NAME").toLowerCase();
						String expectedType = column.getDbType() + (!column.isSigned() ? " unsigned" : "");
						
						int nullable = result.getInt("NULLABLE");
						int expectNullable = column.isNullable() ? 1 : 0;
						
						if (!expectedType.equals(type) || nullable != expectNullable) {
							sql.append("ALTER TABLE " + tableName + " CHANGE " + column.getName() +
								" " + column.getName() + " " + getFullDbType(column) + ";\n");
//							System.out.println(tableName + "." + column.getName() + " = " + result.getString("TYPE_NAME").toLowerCase());
						}
					}
				} else {
					// Ajoute la colonne à la requête de création de table
					sql.append("  " + column.getName() + " " + getFullDbType(column) + "\n,");
				}
			}
			
			// Vérifie que des colonnes inutiles ne sont pas à supprimer
			if (existingTable) {
				result = metadata.getColumns(null, null, tableName, null);
				
				while (result.next()) {
					String columnName = result.getString("COLUMN_NAME").toLowerCase();
					
					if (table.getColumnByName(columnName) == null)
						sql.append("ALTER TABLE " + tableName + " DROP " + columnName + ";\n");
				}
			}
			
			
			// TODO Rechercher les tables inutiles
			// TODO Rechercher les index inutiles
			// TODO Rechercher les index manquants
			// TODO contrôle de cohérence pour refuser de sauver les valeurs <0 qd signed = true
			// TODO définir un type timestamp (int unsigned)
			// TODO supprimer le fichier XML et gérer la configuration par annotation
			// TODO remettre les colonnes en ordre
			// TODO vérifier la cohérence des types avec les clés étrangères (auto déterminer le type ?)
			
			if (!existingTable) {
				// Table inexistante
				sql.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
			}
		}
		
		System.out.println(sql);
		
		DataAccess.flush();
		System.exit(0);
	}
	
	public static String getFullDbType(Column column) {
		return column.getDbType() + (column.isSigned() ? "" : " unsigned") +
			" " + (column.isNullable() ? "null" : "not null") +
			(column.isCharType() ? " collate utf8_unicode_ci" : "");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
