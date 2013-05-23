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

/**
 * Formate les variables en entier dans un intervalle donné.
 */
public class IntegerRangeFormat extends IntegerFormat {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int mode;
	private int bound1;
	private int bound2;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void setArgs(Object... args) {
		if (args.length > 0) {
			mode = Integer.parseInt(args[0].toString());
			
			switch (mode) {
			case BETWEEN:
				this.bound1 = Integer.parseInt(args[1].toString());
				this.bound2 = Integer.parseInt(args[2].toString());
			break;
			default:
				this.bound1 = Integer.parseInt(args[1].toString());
				break;
			}
		}
	}
	
	public Object format(String var) {
		Object result = super.format(var);
		
		if (result != null) {
			int value = (Integer) result;
			
			switch (mode) {
			case BETWEEN:
				return value >= bound1 && value <= bound2 ? value : null;
			case SUPERIOR_EQUAL:
				return value >= bound1 ? value : null;
			case INFERIOR_EQUAL:
				return value <= bound1 ? value : null;
			case NOT_EQUAL:
				return value != bound1 ? value : null;
			}
		}
		return null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
