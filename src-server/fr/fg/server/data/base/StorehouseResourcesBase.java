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

public class StorehouseResourcesBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idArea;
	private int idPlayer;
	private long resource0;
	private long resource1;
	private long resource2;
	private long resource3;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idArea = idArea;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	public long getResource0() {
		return resource0;
	}
	
	public void setResource0(long resource0) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource0 = resource0;
	}
	
	public long getResource1() {
		return resource1;
	}
	
	public void setResource1(long resource1) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource1 = resource1;
	}
	
	public long getResource2() {
		return resource2;
	}
	
	public void setResource2(long resource2) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource2 = resource2;
	}
	
	public long getResource3() {
		return resource3;
	}
	
	public void setResource3(long resource3) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource3 = resource3;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
