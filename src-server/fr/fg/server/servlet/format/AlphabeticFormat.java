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

public class AlphabeticFormat extends StringFormat {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public Object format(String var) {
		Object result = super.format(var);
		
		if (result != null) {
			String value = (String) result;
			
			// Supprime les espaces en début et fin de chaîne
			value = value.trim();
			
			// Supprime les espaces
			value = value.replaceAll(" ", "");
			
			// Vérifie que la chaine ne contient que des caractères autorisés
			String alphabetic = "a-zA-Z";
			String regexp = "^[" + alphabetic + "]+$";
			
			if (!value.matches(regexp))
				return null;
			return value;
		}
		return null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
