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

public class ContractRewardBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_XP = "xp",
		TYPE_FLEET_XP = "fleet_xp",
		TYPE_RESOURCE = "resource",
		TYPE_CREDIT = "credit",
		TYPE_SHIP = "ship";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private long idContract;
	private String type;
	private int keyName;
	private long value;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public long getIdContract() {
		return idContract;
	}
	
	public void setIdContract(long idContract) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idContract = idContract;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (type == null)
			throw new IllegalArgumentException("Invalid value: '" +
				type + "' (must not be null).");
		else if (type.equals(TYPE_XP))
			this.type = TYPE_XP;
		else if (type.equals(TYPE_FLEET_XP))
			this.type = TYPE_FLEET_XP;
		else if (type.equals(TYPE_RESOURCE))
			this.type = TYPE_RESOURCE;
		else if (type.equals(TYPE_CREDIT))
			this.type = TYPE_CREDIT;
		else if (type.equals(TYPE_SHIP))
			this.type = TYPE_SHIP;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
	}
	
	public int getKeyName() {
		return keyName;
	}
	
	public void setKeyName(int keyName) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.keyName = keyName;
	}
	
	public long getValue() {
		return value;
	}
	
	public void setValue(long value) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.value = value;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
