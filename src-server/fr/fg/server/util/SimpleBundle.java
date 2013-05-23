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

package fr.fg.server.util;

import java.util.ResourceBundle;

public class SimpleBundle {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ResourceBundle resources;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SimpleBundle(String file) {
		resources = ResourceBundle.getBundle(file);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getString(String key) {
		return resources.getString(key);
	}

	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(resources.getString(key));
	}

	public int getInt(String key) {
		return Integer.parseInt(resources.getString(key));
	}

	public long getLong(String key) {
		return Long.parseLong(resources.getString(key));
	}

	public float getFloat(String key) {
		return Float.parseFloat(resources.getString(key));
	}
	
	public double getDouble(String key) {
		return Double.parseDouble(resources.getString(key));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
