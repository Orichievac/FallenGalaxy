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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class PlanetBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private double angle;
	private int distance;
	private double rotationSpeed;
	private int image;
	private int idSystem;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public void setAngle(double angle) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.angle = angle;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.distance = distance;
	}
	
	public double getRotationSpeed() {
		return rotationSpeed;
	}
	
	public void setRotationSpeed(double rotationSpeed) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.rotationSpeed = rotationSpeed;
	}
	
	public int getImage() {
		return image;
	}
	
	public void setImage(int image) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.image = image;
	}
	
	public int getIdSystem() {
		return idSystem;
	}
	
	public void setIdSystem(int idSystem) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idSystem = idSystem;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
