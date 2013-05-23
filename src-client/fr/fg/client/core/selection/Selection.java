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

package fr.fg.client.core.selection;

public class Selection {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_NONE = 0,
		TYPE_FLEET = 1,
		TYPE_SYSTEM = 2,
		TYPE_SPACE_STATION = 3,
		TYPE_STRUCTURE = 4;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private long[] idSelections;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Selection() {
		this.type = TYPE_NONE;
		this.idSelections = new long[0];
	}
	
	public Selection(int type, long idSelection) {
		this.type = type;
		this.idSelections = new long[]{idSelection};
	}
	
	public Selection(int type, long[] idSelections) {
		this.type = type;
		this.idSelections = idSelections;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getType() {
		return type;
	}
	
	public long getFirstIdSelection() {
		return idSelections.length > 0 ? idSelections[0] : -1;
	}
	
	public long[] getIdSelections() {
		return idSelections;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
