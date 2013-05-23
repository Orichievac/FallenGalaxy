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

public class Slot {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static Slot EMPTY_SLOT = new Slot();
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private double count;
	private boolean front;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Slot() {
		this.id = 0;
		this.count = 0;
		this.front = true;
	}
	
	public Slot(int id, double count, boolean front) {
		this.id = count <= 0 ? 0 : id;
		this.count = id == 0 ? 0 : count;
		this.front = front;
	}

	public Slot(Slot slot) {
		this.id = slot.getId();
		this.count = slot.getCount();
		this.front = slot.isFront();
	}
	
	// --------------------------------------------------------- METHODES -- //

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Ship getShip() {
		if (id == 0)
			return null;
		return Ship.SHIPS[id];
	}
	
	public double getCount() {
		return count;
	}
	
	public void setCount(double count) {
		if (count <= 0) {
			this.count = 0;
			this.id = 0;
		} else {
			this.count = count;
		}
	}
	
	public void addCount(double count){
		this.count += count;
		
		if (this.count <= 0) {
			this.count = 0;
			this.id = 0;
		}
	}
	
	public boolean isFront() {
		return front;
	}
	
	public void setFront(boolean front) {
		this.front = front;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Slot))
			return false;
		Slot slot = (Slot) object;
		return slot.getId() == id && slot.getCount() == count;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
