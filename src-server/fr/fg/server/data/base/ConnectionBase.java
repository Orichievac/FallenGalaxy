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

public class ConnectionBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int ip;
	private long start;
	private long end;
	private int idPlayer;
	
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
	
	public int getIp() {
		return ip;
	}
	
	public void setIp(int ip) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.ip = ip;
	}
	
	public long getStart() {
		return start;
	}
	
	public void setStart(long start) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.start = start;
	}
	
	public long getEnd() {
		return end;
	}
	
	public void setEnd(long end) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.end = end;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
