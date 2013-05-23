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

public class ForeignKey {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		SET_NULL = 1,
		SET_ZERO = 2,
		CASCADE = 3,
		RESTRICT = 4,
		NO_ACTION = 5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String name;
	
	private Table table;
	
	private String referencedTable;
	
	private String referencedColumn;
	
	private int action;

	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public ForeignKey(String name, Table table, String referencedTable,
			String referencedColumn, int action) {
		this.name = name;
		this.table = table;
		this.referencedTable = referencedTable;
		this.referencedColumn = referencedColumn;
		this.action = action;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Column getColumn() {
		return this.table.getColumnByName(this.name);
	}
	
	public String getReferencedTable() {
		return referencedTable;
	}

	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	public String getReferencedColumn() {
		return referencedColumn;
	}

	public void setReferencedColumn(String referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
