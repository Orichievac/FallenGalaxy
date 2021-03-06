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

public class TreatyBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_WAR = "war",
		TYPE_ALLY = "ally",
		TYPE_DEFENSIVE = "defensive",
		TYPE_TOTAL = "total";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idPlayer1;
	private int idPlayer2;
	private String type;
	private long date;
	private long lastActivity;
	private int source;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdPlayer1() {
		return idPlayer1;
	}
	
	public void setIdPlayer1(int idPlayer1) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer1 = idPlayer1;
	}
	
	public int getIdPlayer2() {
		return idPlayer2;
	}
	
	public void setIdPlayer2(int idPlayer2) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer2 = idPlayer2;
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
		else if (type.equals(TYPE_WAR))
			this.type = TYPE_WAR;
		else if (type.equals(TYPE_ALLY))
			this.type = TYPE_ALLY;
		else if (type.equals(TYPE_DEFENSIVE))
			this.type = TYPE_DEFENSIVE;
		else if (type.equals(TYPE_TOTAL))
			this.type = TYPE_TOTAL;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public long getLastActivity() {
		return lastActivity;
	}
	
	public void setLastActivity(long lastActivity) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.lastActivity = lastActivity;
	}
	
	public int getSource() {
		return source;
	}
	
	public void setSource(int source) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.source = source;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
