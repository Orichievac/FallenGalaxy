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

package fr.fg.server.servlet.format;

public class EnumFormat extends StringFormat {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String[] values;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void setArgs(Object... args) {
		values = new String[args.length];
		for (int i = 0; i < args.length; i++)
			values[i] = args[i].toString();
	}
	
	public Object format(String var) {
		Object result = super.format(var);
		
		if (result != null) {
			String value = (String) result;
			
			for (int i = 0; i < values.length; i++)
				if (value.equals(values[i]))
					return value;
		}
		return null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
