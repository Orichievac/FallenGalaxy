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

import java.awt.Dimension;
import java.awt.Rectangle;

import fr.fg.server.data.base.StellarObjectBase;

public class StellarObject extends StellarObjectBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	

	// Bords de l'objet (mis en cache pour un accès rapide)
	private Rectangle bounds;
	
	// Indique si une flotte peut s'arreter sur l'objet (mis en cache)
	private boolean passable;
	
	// Indique si une flotte peut être placée sur l'objet en sortie
	// d'hyperespace (mis en cache)
	private boolean validHyperjumpOutput;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public StellarObject() {
		// Nécessaire pour la construction par réflection
		this.bounds = new Rectangle();
	}

	public StellarObject(int x, int y, String type, int variant, int idArea) {
		this.bounds = new Rectangle();
		
		super.setX(x);
		super.setY(y);
		super.setType(type);
		setVariant(variant);
		setIdArea(idArea);
		
		updateObject();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void setX(int x) {
		super.setX(x);
		updateObject();
	}

	public void setY(int y) {
		super.setY(y);
		updateObject();
	}
	
	public void setType(String type) {
		super.setType(type);
		updateObject();
	}
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public boolean isValidHyperjumpOutput() {
		return validHyperjumpOutput;
	}
	
	// Indique si une flotte peut rester au dessus de l'objet ou non
	public boolean isPassable() {
		return passable;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public static Dimension getSize(String type){
		if (type.equals(TYPE_GATE) || type.startsWith(TYPE_ASTEROID)) {
			// Object 3x3
			return new Dimension(3,3);
		} else if (type.equals(TYPE_BLACKHOLE)) {
			// Objet 5x5
			return new Dimension(5,5);
		} else if (type.equals(TYPE_BANK) || type.equals(TYPE_TRADECENTER) || type.equals(TYPE_LOTTERY) ||
				type.equals(TYPE_PIRATES)) {
			// Object 9x9
			return new Dimension(9,9);
		} else {
			// Object 1x1
			return new Dimension(1,1);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateObject() {
		String type = getType();
		
		if (type == null)
			return;
		
		int x, y, width, height;
		
		if (type.equals(TYPE_GATE) || type.startsWith(TYPE_ASTEROID)) {
			// Object 3x3
			x = getX() - 1;
			y = getY() - 1;
			width = height = 3;
		} else if (type.equals(TYPE_BLACKHOLE) || type.equals(TYPE_GRAVITY_WELL)) {
			// Objet 5x5
			x = getX() - 2;
			y = getY() - 2;
			width = height = 5;
		} else if (type.equals(TYPE_BANK) || type.equals(TYPE_TRADECENTER) || type.equals(TYPE_LOTTERY) ||
				type.equals(TYPE_PIRATES)) {
			// Object 9x9
			x = getX() - 4;
			y = getY() - 4;
			width = height = 9;
		} else {
			// Object 1x1
			x = getX();
			y = getY();
			width = height = 1;
		}
		
		this.bounds.setBounds(x, y, width, height);
		
		this.passable = !(type.equals(TYPE_GATE));
		this.validHyperjumpOutput = type.startsWith(TYPE_ASTEROID);
	}
	
}
