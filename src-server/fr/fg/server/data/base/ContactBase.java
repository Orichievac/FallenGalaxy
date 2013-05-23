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

public class ContactBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_FRIEND = "friend",
		TYPE_IGNORE = "ignore";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idPlayer;
	private int idContact;
	private String type;
	
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
	
	public int getIdContact() {
		return idContact;
	}
	
	public void setIdContact(int idContact) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idContact = idContact;
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
		else if (type.equals(TYPE_FRIEND))
			this.type = TYPE_FRIEND;
		else if (type.equals(TYPE_IGNORE))
			this.type = TYPE_IGNORE;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
