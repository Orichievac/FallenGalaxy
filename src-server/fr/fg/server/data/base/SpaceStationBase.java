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

public class SpaceStationBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String name;
	private int level;
	private int hull;
	private int x;
	private int y;
	private long credits;
	private long resource0;
	private long resource1;
	private long resource2;
	private long resource3;
	private long date;
	private int idAlly;
	private int idArea;
	
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (name == null)
			throw new IllegalArgumentException("Invalid value: '" +
				name + "' (must not be null).");
		else
			this.name = name;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.level = level;
	}
	
	public int getHull() {
		return hull;
	}
	
	public void setHull(int hull) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.hull = hull;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.y = y;
	}
	
	public long getCredits() {
		return credits;
	}
	
	public void setCredits(long credits) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.credits = credits;
	}
	
	public long getResource0() {
		return resource0;
	}
	
	public void setResource0(long resource0) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource0 = resource0;
	}
	
	public long getResource1() {
		return resource1;
	}
	
	public void setResource1(long resource1) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource1 = resource1;
	}
	
	public long getResource2() {
		return resource2;
	}
	
	public void setResource2(long resource2) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource2 = resource2;
	}
	
	public long getResource3() {
		return resource3;
	}
	
	public void setResource3(long resource3) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.resource3 = resource3;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public int getIdAlly() {
		return idAlly;
	}
	
	public void setIdAlly(int idAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idAlly = idAlly;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idArea = idArea;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
