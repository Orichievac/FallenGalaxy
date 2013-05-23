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

public class MarkerBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		VISIBILITY_PLAYER = "player",
		VISIBILITY_ALLY = "ally",
		VISIBILITY_ALLIED = "allied";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int x;
	private int y;
	private String message;
	private String visibility;
	private boolean galaxy;
	private long date;
	private int idArea;
	private int idOwner;
	private long idContract;
	
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
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.y = y;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (message == null)
			throw new IllegalArgumentException("Invalid value: '" +
				message + "' (must not be null).");
		else
			this.message = message;
	}
	
	public String getVisibility() {
		return visibility;
	}
	
	public void setVisibility(String visibility) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (visibility == null)
			throw new IllegalArgumentException("Invalid value: '" +
				visibility + "' (must not be null).");
		else if (visibility.equals(VISIBILITY_PLAYER))
			this.visibility = VISIBILITY_PLAYER;
		else if (visibility.equals(VISIBILITY_ALLY))
			this.visibility = VISIBILITY_ALLY;
		else if (visibility.equals(VISIBILITY_ALLIED))
			this.visibility = VISIBILITY_ALLIED;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + visibility + "'.");
	}
	
	public boolean isGalaxy() {
		return galaxy;
	}
	
	public void setGalaxy(boolean galaxy) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.galaxy = galaxy;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idArea = idArea;
	}
	
	public int getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(int idOwner) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idOwner = idOwner;
	}
	
	public long getIdContract() {
		return idContract;
	}
	
	public void setIdContract(long idContract) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idContract = idContract;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
