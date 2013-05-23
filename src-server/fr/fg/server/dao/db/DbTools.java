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

package fr.fg.server.dao.db;

public class DbTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static String getJavaFieldName(String dbFieldName) {
		String javaName = dbFieldName.toLowerCase();
		
		while (javaName.contains("_")) {
			int index = javaName.indexOf("_");
			
			javaName = javaName.substring(0, index) +
				javaName.substring(index + 1, index + 2).toUpperCase() +
				javaName.substring(index + 2);
		}
		
		return javaName;
	}
	
	public static String getJavaClassName(String dbTableName) {
		String javaName = getJavaFieldName(dbTableName);
		
		return javaName.substring(0, 1).toUpperCase() + javaName.substring(1);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
