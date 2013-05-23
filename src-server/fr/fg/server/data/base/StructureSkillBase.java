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

public class StructureSkillBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long idStructure;
	private int type;
	private long lastUse;
	private long reload;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getIdStructure() {
		return idStructure;
	}
	
	public void setIdStructure(long idStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idStructure = idStructure;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.type = type;
	}
	
	public long getLastUse() {
		return lastUse;
	}
	
	public void setLastUse(long lastUse) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.lastUse = lastUse;
	}
	
	public long getReload() {
		return reload;
	}
	
	public void setReload(long reload) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.reload = reload;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
