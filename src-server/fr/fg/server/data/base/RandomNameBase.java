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

public class RandomNameBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_LOCATION = "location",
		TYPE_MALE_FNAME = "male_fname",
		TYPE_FEMALE_FNAME = "female_fname",
		TYPE_LNAME = "lname",
		TYPE_ALLY = "ally";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	private String name;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (type == null)
			throw new IllegalArgumentException("Invalid value: '" +
				type + "' (must not be null).");
		else if (type.equals(TYPE_LOCATION))
			this.type = TYPE_LOCATION;
		else if (type.equals(TYPE_MALE_FNAME))
			this.type = TYPE_MALE_FNAME;
		else if (type.equals(TYPE_FEMALE_FNAME))
			this.type = TYPE_FEMALE_FNAME;
		else if (type.equals(TYPE_LNAME))
			this.type = TYPE_LNAME;
		else if (type.equals(TYPE_ALLY))
			this.type = TYPE_ALLY;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
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
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
