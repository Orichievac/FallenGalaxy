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

package fr.fg.server.servlet;

import fr.fg.server.servlet.format.Format;

public class Parameter {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String name;
	private boolean required;
	private Format[] formats;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Parameter(String name, boolean required) {
		this.name = name;
		this.required = required;
		this.formats = new Format[0];
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getName() {
		return name;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void addFormat(Format format) {
		// Aggrandit le tableau des formats
		Format[] tmp = new Format[formats.length + 1];
		for (int i = 0; i < formats.length; i++)
			tmp[i] = formats[i];
		formats = tmp;
		formats[formats.length - 1] = format;
	}
	
	public Format[] getFormats() {
		return formats;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
