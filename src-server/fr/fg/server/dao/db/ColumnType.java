/*
Copyright 2010 GOTTERO

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

public class ColumnType {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static ColumnType
		BOOLEAN = new ColumnType("boolean"),
		BYTE = new ColumnType("byte"),
		SHORT = new ColumnType("short"),
		INT = new ColumnType("int"),
		LONG = new ColumnType("long"),
		FLOAT = new ColumnType("float"),
		DOUBLE = new ColumnType("double"),
		STRING = new ColumnType("string"),
		TIMESTAMP = new ColumnType("timestamp");
	
	private final static ColumnType[] ALL_TYPES = {
		BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, TIMESTAMP
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private ColumnType(String type) {
		this.type = type;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static ColumnType valueOf(String type) {
		for (int i = 0; i < ALL_TYPES.length; i++)
			if (ALL_TYPES[i].type.equals(type))
				return ALL_TYPES[i];
		return null;
	}
	
	public static ColumnType valueOf(String type, ColumnType defaultValue) {
		ColumnType value = valueOf(type);
		if (value == null)
			return defaultValue;
		return value;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
