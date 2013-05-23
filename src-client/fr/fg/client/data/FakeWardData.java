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

package fr.fg.client.data;

public class FakeWardData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "FakeWardData"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	
	private int x;
	
	private int y;
	
	private boolean valid;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FakeWardData(String type, int x, int y, boolean valid) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.valid = valid;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public String getType() {
		return type;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
