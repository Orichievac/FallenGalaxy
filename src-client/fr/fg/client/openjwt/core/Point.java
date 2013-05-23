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

public class Point {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int x, y;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Point() {
		this.x = 0;
		this.y = 0;
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}
	
	// --------------------------------------------------------- METHODES -- //

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
	
	public void addX(int dx) {
		this.x += dx;
	}

	public void addY(int dy) {
		this.y += dy;
	}
	
	public Point add(Point point) {
		this.x += point.x;
		this.y += point.y;
		return this;
	}
	
	public Point subtract(Point point) {
		this.x -= point.x;
		this.y -= point.y;
		return this;
	}
	
	public double norm() {
		return Math.sqrt(x * x + y * y);
	}

	public double normSq() {
		return x * x + y * y;
	}
	
	public boolean equals(Object object) {
		if (object instanceof Point) {
			Point point = (Point) object;
			return this.x == point.x && this.y == point.y;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Point [x=" + x + ",y=" + y + "]";
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
