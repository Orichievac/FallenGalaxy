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

public class SectorBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String name;
	private int x;
	private int y;
	private int type;
	private int nebula;
	private int strategicValue;
	
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (name == null)
			throw new IllegalArgumentException("Invalid value: '" +
				name + "' (must not be null).");
		else
			this.name = name;
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
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (type < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				type + "' (must be >= 0).");
		else
			this.type = type;
	}
	
	public int getNebula() {
		return nebula;
	}
	
	public void setNebula(int nebula) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (nebula < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				nebula + "' (must be >= 0).");
		else
			this.nebula = nebula;
	}
	
	public int getStrategicValue() {
		return strategicValue;
	}
	
	public void setStrategicValue(int strategicValue) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (strategicValue < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				strategicValue + "' (must be >= 0).");
		else
			this.strategicValue = strategicValue;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
