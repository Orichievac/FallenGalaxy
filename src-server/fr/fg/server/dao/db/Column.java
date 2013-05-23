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

import java.lang.reflect.Method;

public class Column {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static PrimitiveLoader loader = new PrimitiveLoader();
	
	private String name;
	
	private ColumnType type;
	
	private String javaType;
	
	private String dbType;
	
	private boolean nullable;
	
	private boolean signed;
	
	private Method getAccessor, setAccessor;
	
	private Table parentTable;
	
	private String[] enumValues;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Column(String name, ColumnType type, String javaType, String dbType,
			boolean nullable, boolean signed, String[] enumValues) throws Exception {
		this.name = name;
		this.type = type;
		this.javaType = javaType;
		this.dbType = dbType;
		this.nullable = nullable;
		this.signed = signed;
		this.enumValues = enumValues;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getName() {
		return name;
	}
	
	public String getJavaName() {
		return DbTools.getJavaFieldName(name);
	}
	
	public Method getGetAccessor() throws Exception {
		if (getAccessor == null && parentTable != null) {
			getAccessor = parentTable.getMappedClass().getMethod(
					(javaType.equals("boolean") ||
							javaType.equals("java.lang.Boolean") ? "is" : "get") +
							DbTools.getJavaClassName(name));
		}
		return getAccessor;
	}
	
	public Method getSetAccessor() throws Exception {
		if (setAccessor == null && parentTable != null) {
			setAccessor = parentTable.getMappedClass().getMethod(
					"set" + DbTools.getJavaClassName(name),
					loader.loadClass(javaType));
		}
		return setAccessor;
	}
	
	public ColumnType getType() {
		return type;
	}
	
	public String getJavaType() {
		return javaType;
	}
	
	public String getDbType() {
		return dbType;
	}
	
	public Table getParentTable() {
		return parentTable;
	}
	
	public void setParentTable(Table parentTable) {
		this.parentTable = parentTable;
		updateCachedData();
	}
	
	public String[] getEnumValues() {
		return enumValues;
	}
	
	public boolean isSigned() {
		return signed;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public boolean isCharType() {
		return type == ColumnType.STRING;
	}
	
	public boolean isEnum() {
		return type == ColumnType.STRING && enumValues.length > 0;
	}
	
	public static String getDefaultJavaType(ColumnType columnType,
			boolean nullable, boolean signed, String[] enumValues) {
		if (columnType == ColumnType.BOOLEAN)
			return nullable ? "java.lang.Boolean" : "boolean";
		else if (columnType == ColumnType.BYTE)
			return nullable ? "java.lang.Byte" : "byte";
		else if (columnType == ColumnType.SHORT)
			return nullable ? "java.lang.Short" : "short";
		else if (columnType == ColumnType.INT)
			return nullable ? "java.lang.Integer" : "int";
		else if (columnType == ColumnType.LONG)
			return nullable ? "java.lang.Long" : "long";
		else if (columnType == ColumnType.FLOAT)
			return nullable ? "java.lang.Float" : "float";
		else if (columnType == ColumnType.DOUBLE)
			return nullable ? "java.lang.Double" : "double";
		else if (columnType == ColumnType.STRING)
			return "java.lang.String";
		else if (columnType == ColumnType.TIMESTAMP)
			return nullable ? "java.lang.Integer" : "int";
		else
			return null;
	}
	
	public static String getDefaultDbType(ColumnType columnType,
			boolean nullable, boolean signed, String[] enumValues) {
		if (columnType == ColumnType.BOOLEAN)
			return "BIT";
		else if (columnType == ColumnType.BYTE)
			return "TINYINT";
		else if (columnType == ColumnType.SHORT)
			return "SMALLINT";
		else if (columnType == ColumnType.INT)
			return "INT";
		else if (columnType == ColumnType.LONG)
			return "BIGINT";
		else if (columnType == ColumnType.FLOAT)
			return "FLOAT";
		else if (columnType == ColumnType.DOUBLE)
			return "DOUBLE";
		else if (columnType == ColumnType.STRING)
			return enumValues.length > 0 ? "ENUM" : "VARCHAR";
		else if (columnType == ColumnType.TIMESTAMP)
			return "INT";
		else
			return null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateCachedData() {
		this.setAccessor = null;
		this.getAccessor = null;
	}
}
