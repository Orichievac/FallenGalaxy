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

public class Index implements Comparable<Index> {
	// ------------------------------------------------------- CONSTANTES -- //

	// Types d'index
	public final static int UNIQUE = 1, MULTIPLE = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //

	private String column;
	
	private int type;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Index(String column, int type) {
		this.column = column;
		this.type = type;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int compareTo(Index i) {
		return getColumn().compareToIgnoreCase(i.getColumn());
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
