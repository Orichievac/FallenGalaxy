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

package fr.fg.server.contract;

import java.awt.Point;

public class ContractState {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String name;
	
	private String status;
	
	private String description;
	
	private int idArea;
	
	private Point location;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ContractState(String name, String status, String description,
			int idArea, Point location) {
		this.name = name;
		this.status = status;
		this.description = description;
		this.idArea = idArea;
		this.location = location;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public String getName() {
		return name;
	}
	
	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public int getIdArea() {
		return idArea;
	}

	public Point getLocation() {
		return location;
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
