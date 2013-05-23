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

public class AllyVoteBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_ACCEPT = "accept",
		TYPE_KICK = "kick";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String type;
	private int yes;
	private int no;
	private long date;
	private int idPlayer;
	private int idAlly;
	private int idInitiator;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
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
		else if (type.equals(TYPE_ACCEPT))
			this.type = TYPE_ACCEPT;
		else if (type.equals(TYPE_KICK))
			this.type = TYPE_KICK;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
	}
	
	public int getYes() {
		return yes;
	}
	
	public void setYes(int yes) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.yes = yes;
	}
	
	public int getNo() {
		return no;
	}
	
	public void setNo(int no) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.no = no;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	public int getIdAlly() {
		return idAlly;
	}
	
	public void setIdAlly(int idAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idAlly = idAlly;
	}
	
	public int getIdInitiator() {
		return idInitiator;
	}
	
	public void setIdInitiator(int idInitiator) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idInitiator = idInitiator;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
