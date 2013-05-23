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

package fr.fg.client.openjwt.core;

public class Dimension {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int width, height;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Dimension() {
		this.width = 0;
		this.height = 0;
	}
	
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Dimension(Dimension dimension) {
		this.width = dimension.width;
		this.height = dimension.height;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
