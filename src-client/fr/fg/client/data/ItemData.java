/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

public class ItemData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private long id;
	
	private double count;
	
	private int structureType;
	
	private int structureLevel;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ItemData(int type, long id, double count, int structureType,
			int structureLevel) {
		this.type = count <= 0 ? ItemInfoData.TYPE_NONE : type;
		this.id = count <= 0 || type == ItemInfoData.TYPE_NONE ? 0 : id;
		this.count = count <= 0 || type == ItemInfoData.TYPE_NONE ? 0 : count;
		this.structureType = structureType;
		this.structureLevel = structureLevel;
	}
	
	public ItemData(ItemInfoData item) {
		this.type = item.getType();
		this.id = item.getId();
		this.count = item.getCount();
		this.structureType = item.getStructureType();
		this.structureLevel = item.getStructureLevel();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public long getWeight() {
		return ItemInfoData.getWeight(type, id, structureType, structureLevel);
	}
	
	public int getStructureLevel() {
		return structureLevel;
	}
	
	public int getStructureType() {
		return structureType;
	}
	
	
	public void setStructureLevel(int structureLevel) {
		this.structureLevel = structureLevel;
	}
	
	public void setStructureType(int structureType) {
		this.structureType = structureType;
	}
	
	public boolean isStackable() {
		switch (type) {
		case ItemInfoData.TYPE_NONE:
			return false;
		case ItemInfoData.TYPE_RESOURCE:
			return true;
		case ItemInfoData.TYPE_STUFF:
			return true;
		case ItemInfoData.TYPE_STRUCTURE:
			return false;
		default:
			throw new IllegalStateException("Invalid type: '" + type + "'.");
		}
	}
	
	public int getType() {
		return type;
	}

	public long getId() {
		return id;
	}

	public double getCount() {
		return count;
	}
	
	public void setType(int type) {
		if (type == ItemInfoData.TYPE_NONE) {
			this.type = ItemInfoData.TYPE_NONE;
			this.id = 0;
			this.count = 0;
		} else {
			this.type = type;
		}
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setCount(double count) {
		if (count <= 0) {
			this.type = ItemInfoData.TYPE_NONE;
			this.id = 0;
			this.count = 0;
		} else {
			this.count = count;
		}
	}
	
	public void addCount(double count) {
		setCount(getCount() + count);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
