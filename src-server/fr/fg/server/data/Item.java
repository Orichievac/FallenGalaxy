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

public class Item {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_NONE = 0,
		TYPE_RESOURCE = 1,
		TYPE_STUFF = 2,
		TYPE_STRUCTURE = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	private long id;
	private long idStructure;
	private double count;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Item() {
		this.type = TYPE_NONE;
		this.id = 0;
		this.idStructure = 0;
		this.count = 0;
	}
	
	public Item(int type, long id, double count) {
		this.type = count <= 0 ? TYPE_NONE : type;
		this.id = count <= 0 || type == TYPE_NONE ? 0 : id;
		this.idStructure = count <= 0 || type == TYPE_NONE ? 0 : (type == TYPE_STRUCTURE ? id : 0);
		this.count = type == TYPE_NONE ? 0 : count;
	}
	
	public Item(int type, long id, long idStructure, double count) {
		this.type = count <= 0 ? TYPE_NONE : type;
		this.id = count <= 0 || type == TYPE_NONE ? 0 : id;
		this.idStructure = count <= 0 || type == TYPE_NONE ? 0 : idStructure;
		this.count = type == TYPE_NONE ? 0 : count;
	}
	
	public Item(Item item) {
		this.type = item.getType();
		this.id = item.getId();
		this.idStructure = item.getIdStructure();
		this.count = item.getCount();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public long getKey() {
		return type + 10 * getId();
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (type == TYPE_NONE) {
			this.type = TYPE_NONE;
			this.id = 0;
			this.idStructure = 0;
			this.count = 0;
		} else {
			this.type = type;
		}
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Structure getStructure() {
		return DataAccess.getStructureById(idStructure);
	}
	
	public long getIdStructure() {
		return idStructure;
	}
	
	public void setIdStructure(long idStructure) {
		this.idStructure = idStructure;
	}
	
	public double getCount() {
		return count;
	}
	
	public void setCount(double count) {
		if (count <= 0) {
			this.type = TYPE_NONE;
			this.id = 0;
			this.idStructure = 0;
			this.count = 0;
		} else {
			this.count = count;
		}
	}
	
	public void addCount(double count) {
		setCount(getCount() + count);
	}
	
	public boolean isSameType(Item item) {
		return getType() == item.getType() && getId() == item.getId();
	}
	
	public boolean isSameType(int type, int id) {
		return getType() == type && getId() == id;
	}
	
	public boolean isStackable() {
		switch (type) {
		case TYPE_RESOURCE:
			return true;
		case TYPE_STUFF:
			return true;
		case TYPE_STRUCTURE:
			return false;
		default:
			return false;
		}
	}
	
	public int getWeight() {
		switch (type) {
		case TYPE_NONE:
			return 0;
		case TYPE_RESOURCE:
			return 1;
		case TYPE_STUFF:
			return 1;
		case TYPE_STRUCTURE:
			return getStructure().getWeight();
		default:
			throw new IllegalStateException("Invalid type: '" + type + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
