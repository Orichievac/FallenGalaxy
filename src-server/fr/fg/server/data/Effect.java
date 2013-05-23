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

package fr.fg.server.data;

public class Effect {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_FLEET_DESTRUCTION = "fleetDestruction",
		TYPE_WARD_DESTRUCTION = "wardDestruction",
		TYPE_STATION_DESTRUCTION = "stationDestruction",
		TYPE_EMP = "emp",
		TYPE_SMALL_STRUCTURE_DESTRUCTION = "smallStructureDestruction",
		TYPE_LARGE_STRUCTURE_DESTRUCTION = "largeStructureDestruction";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	private int x;
	private int y;
	private int idArea;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Effect(String type, int x, int y, int idArea) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.idArea = idArea;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public int getRadius() {
		if (getType().equals(TYPE_FLEET_DESTRUCTION) ||
				getType().equals(TYPE_WARD_DESTRUCTION) ||
				getType().equals(TYPE_EMP) ||
				getType().equals(TYPE_SMALL_STRUCTURE_DESTRUCTION))
			return 1;
		else if (getType().equals(TYPE_STATION_DESTRUCTION) ||
				getType().equals(TYPE_LARGE_STRUCTURE_DESTRUCTION))
			return (int) Math.ceil(GameConstants.SPACE_STATION_RADIUS);
		else
			throw new IllegalArgumentException("Invalid type: '" + type + "'.");
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public int getIdArea() {
		return idArea;
	}

	public void setIdArea(int idArea) {
		this.idArea = idArea;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
