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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class AdvancementBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idPlayer;
	private int type;
	private int level;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.type = type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.level = level;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
