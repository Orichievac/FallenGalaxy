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

public class WardBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_OBSERVER = "observer",
		TYPE_OBSERVER_INVISIBLE = "observer_invisible",
		TYPE_SENTRY = "sentry",
		TYPE_SENTRY_INVISIBLE = "sentry_invisible",
		TYPE_STUN = "stun",
		TYPE_STUN_INVISIBLE = "stun_invisible",
		TYPE_MINE = "mine",
		TYPE_MINE_INVISIBLE = "mine_invisible";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int x;
	private int y;
	private String type;
	private int power;
	private long date;
	private int idArea;
	private int idOwner;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
			this.id = id;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (x < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				x + "' (must be >= 0).");
		else
			this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (y < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				y + "' (must be >= 0).");
		else
			this.y = y;
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
		else if (type.equals(TYPE_OBSERVER))
			this.type = TYPE_OBSERVER;
		else if (type.equals(TYPE_OBSERVER_INVISIBLE))
			this.type = TYPE_OBSERVER_INVISIBLE;
		else if (type.equals(TYPE_SENTRY))
			this.type = TYPE_SENTRY;
		else if (type.equals(TYPE_SENTRY_INVISIBLE))
			this.type = TYPE_SENTRY_INVISIBLE;
		else if (type.equals(TYPE_STUN))
			this.type = TYPE_STUN;
		else if (type.equals(TYPE_STUN_INVISIBLE))
			this.type = TYPE_STUN_INVISIBLE;
		else if (type.equals(TYPE_MINE))
			this.type = TYPE_MINE;
		else if (type.equals(TYPE_MINE_INVISIBLE))
			this.type = TYPE_MINE_INVISIBLE;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
	}
	
	public int getPower() {
		return power;
	}
	
	public void setPower(int power) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (power < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				power + "' (must be >= 0).");
		else
			this.power = power;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (date < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				date + "' (must be >= 0).");
		else
			this.date = date;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idArea < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idArea + "' (must be >= 0).");
		else
			this.idArea = idArea;
	}
	
	public int getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(int idOwner) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idOwner < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idOwner + "' (must be >= 0).");
		else
			this.idOwner = idOwner;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
