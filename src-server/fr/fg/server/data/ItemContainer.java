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

import java.util.List;

import fr.fg.server.data.base.ItemContainerBase;

public class ItemContainer extends ItemContainerBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		CONTAINER_FLEET = 0,
		CONTAINER_SYSTEM = 1,
		CONTAINER_STRUCTURE = 2,
		CONTAINER_SPACE_STATION = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idSystem;
	
	private int idSpaceStation;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ItemContainer() {
		// Nécessaire pour la construction par réflection
	}
	
	public ItemContainer(int containerType, int id) {
		this(containerType, (long) id);
	}
	
	public ItemContainer(int containerType, long id) {
		setItem0Type(Item.TYPE_NONE);
		setItem0Id(0);
		setItem0IdStructure(0);
		setItem0Count(0);
		setItem1Type(Item.TYPE_NONE);
		setItem1Id(0);
		setItem1IdStructure(0);
		setItem1Count(0);
		setItem2Type(Item.TYPE_NONE);
		setItem2Id(0);
		setItem2IdStructure(0);
		setItem2Count(0);
		setItem3Type(Item.TYPE_NONE);
		setItem3Id(0);
		setItem3IdStructure(0);
		setItem3Count(0);
		setIdFleet(0);
		setIdStructure(0);
		
		switch (containerType) {
		case CONTAINER_FLEET:
			setIdFleet((int) id);
			break;
		case CONTAINER_SYSTEM:
			idSystem = (int) id;
			break;
		case CONTAINER_STRUCTURE:
			setIdStructure(id);
			break;
		case CONTAINER_SPACE_STATION:
			idSpaceStation = (int) id;
			break;
		default:
			throw new IllegalArgumentException("Invalid container: '" + containerType + "'.");
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getMaxItems() {
		return 4;
	}
	
	public Item getItem(int index) {
		switch (index) {
		case 0:
			return new Item(getItem0Type(), getItem0Id(), getItem0IdStructure(), getItem0Count());
		case 1:
			return new Item(getItem1Type(), getItem1Id(), getItem1IdStructure(), getItem1Count());
		case 2:
			return new Item(getItem2Type(), getItem2Id(), getItem2IdStructure(), getItem2Count());
		case 3:
			return new Item(getItem3Type(), getItem3Id(), getItem3IdStructure(), getItem3Count());
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public Item[] getItems() {
		return new Item[]{
			getItem(0), getItem(1), getItem(2), getItem(3)
		};
	}
	
	public void setItem(Item item, int index) {
		switch (index) {
		case 0:
			setItem0Type(item.getType());
			setItem0Id(item.getId());
			setItem0IdStructure(item.getIdStructure());
			setItem0Count((long) item.getCount());
			break;
		case 1:
			setItem1Type(item.getType());
			setItem1Id(item.getId());
			setItem1IdStructure(item.getIdStructure());
			setItem1Count((long) item.getCount());
			break;
		case 2:
			setItem2Type(item.getType());
			setItem2Id(item.getId());
			setItem2IdStructure(item.getIdStructure());
			setItem2Count((long) item.getCount());
			break;
		case 3:
			setItem3Type(item.getType());
			setItem3Id(item.getId());
			setItem3IdStructure(item.getIdStructure());
			setItem3Count((long) item.getCount());
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public void setItems(Item[] items) {
		for (int i = 0; i < items.length; i++)
			setItem(items[i], i);
	}
	
	public int getContainerType() {
		if (getIdFleet() != 0)
			return CONTAINER_FLEET;
		else if (idSystem != 0)
			return CONTAINER_SYSTEM;
		else if (getIdStructure() != 0)
			return CONTAINER_STRUCTURE;
		else if (idSpaceStation != 0)
			return CONTAINER_SPACE_STATION;
		else
			throw new IllegalStateException("Unknown container type.");
	}
	
	public double getTotalWeight() {
		double sum = 0;
		for (int i = 0; i < getMaxItems(); i++) {
			Item item = getItem(i);
			sum += item.getWeight() * item.getCount();
		}
		return sum;
	}
	
	public double[] getResources() {
		double[] resources = new double[GameConstants.RESOURCES_COUNT];
		
		for (int i = 0; i < getMaxItems(); i++) {
			Item item = getItem(i);
			
			if (item.getType() == Item.TYPE_RESOURCE)
				resources[(int) item.getId()] = item.getCount();
		}
		
		return resources;
	}
	
	public long[] getResourcesAsLong() {
		long[] resources = new long[GameConstants.RESOURCES_COUNT];
		
		for (int i = 0; i < getMaxItems(); i++) {
			Item item = getItem(i);
			
			if (item.getType() == Item.TYPE_RESOURCE)
				resources[(int) item.getId()] = (long) item.getCount();
		}
		
		return resources;
	}
	
	public double getResource(int index) {
		for (int i = 0; i < getMaxItems(); i++) {
			Item item = getItem(i);
			
			if (item.getType() == Item.TYPE_RESOURCE && item.getId() == index)
				return item.getCount();
		}
		
		return 0;
	}
	
	public void addResource(double count, int index) throws IllegalOperationException {
		int position = getCompatibleOrFreePosition(Item.TYPE_RESOURCE, index);
		
		if (position == -1)
			throw new IllegalOperationException("Le container ne peut pas recevoir les ressources.");
		
		Item item = getItem(position);
		item.setType(Item.TYPE_RESOURCE);
		item.setId(index);
		item.addCount(count);
		
		setItem(item, position);
	}
	
	public void setResource(double count, int index) throws IllegalOperationException {
		int position = getCompatibleOrFreePosition(Item.TYPE_RESOURCE, index);
		
		if (position == -1) {
			if (count > 0)
				throw new IllegalOperationException("Le container ne " +
						"peut pas recevoir les ressources.");
			else
				return;
		}
		
		Item item = getItem(position);
		
		if (item.getType() == Item.TYPE_NONE) {
			item.setType(Item.TYPE_RESOURCE);
			item.setId(index);
			item.setCount(count);
		} else {
			item.setCount(count);
		}
		
		setItem(item, position);
	}
	
	public long getPayload() {
		switch (getContainerType()) {
		case ItemContainer.CONTAINER_FLEET:
			return getFleet().getPayload();
		case ItemContainer.CONTAINER_SYSTEM:
			return getSystem().getMaxResources();
		case ItemContainer.CONTAINER_STRUCTURE:
			Structure structure = getStructure();
			
			Area area = structure.getArea();
			List<Structure> structures = area.getStructures();
			
			long storehousesPayload = 0;
			synchronized (structures) {
				for (Structure areaStructure : structures)
					if (areaStructure.getIdOwner() == structure.getIdOwner())
						storehousesPayload += areaStructure.getPayload();
			}
			
			return storehousesPayload;
		case ItemContainer.CONTAINER_SPACE_STATION:
			return 0;
		default:
			throw new IllegalStateException(
				"Invalid container type: '" + getContainerType() + "'.");
		}
	}
	
	public void setResources(double[] resources) throws IllegalOperationException {
		for (int i = 0; i < resources.length; i++)
			setResource(resources[i], i);
	}
	
	// Recherche une position dans le container compatible avec un type d'item,
	// ou une position libre
	public int getCompatibleOrFreePosition(int type, int id) {
		for (int i = 0; i < getMaxItems(); i++) {
			Item item = getItem(i);
			if (item.isSameType(type, id))
				return i;
		}
		
		for (int i = 0; i < getMaxItems(); i++) {
			Item item = getItem(i);
			
			if (item.getType() == Item.TYPE_NONE)
				return i;
		}
		
		return -1;
	}
	
	public Fleet getFleet() {
		if (getContainerType() == CONTAINER_FLEET)
			return DataAccess.getFleetById(getIdFleet());
		else
			throw new IllegalStateException("Not a fleet container.");
	}
	
	public int getIdSystem() {
		return idSystem;
	}
	
	public StarSystem getSystem() {
		if (getContainerType() == CONTAINER_SYSTEM)
			return DataAccess.getSystemById(idSystem);
		else
			throw new IllegalStateException("Not a system container.");
	}
	
	public int getIdSpaceStation() {
		return idSpaceStation;
	}
	
	public StarSystem getSpaceStation() {
		if (getContainerType() == CONTAINER_SPACE_STATION)
			return DataAccess.getSystemById(idSpaceStation);
		else
			throw new IllegalStateException("Not a space station container.");
	}
	
	public Structure getStructure() {
		if (getContainerType() == CONTAINER_STRUCTURE)
			return DataAccess.getStructureById(getIdStructure());
		else
			throw new IllegalStateException("Not a structure container.");
	}
	
	public void copy(ItemContainer itemContainer) {
		setItem0Type(itemContainer.getItem0Type());
		setItem0Id(itemContainer.getItem0Id());
		setItem0IdStructure(itemContainer.getItem0IdStructure());
		setItem0Count(itemContainer.getItem0Count());
		setItem1Type(itemContainer.getItem1Type());
		setItem1Id(itemContainer.getItem1Id());
		setItem1IdStructure(itemContainer.getItem1IdStructure());
		setItem1Count(itemContainer.getItem1Count());
		setItem2Type(itemContainer.getItem2Type());
		setItem2Id(itemContainer.getItem2Id());
		setItem2IdStructure(itemContainer.getItem2IdStructure());
		setItem2Count(itemContainer.getItem2Count());
		setItem3Type(itemContainer.getItem3Type());
		setItem3Id(itemContainer.getItem3Id());
		setItem3IdStructure(itemContainer.getItem3IdStructure());
		setItem3Count(itemContainer.getItem3Count());
		setIdFleet(itemContainer.getIdFleet());
		setIdStructure(itemContainer.getIdStructure());
	}
	
	@Override
	public void save() {
		switch (getContainerType()) {
		case CONTAINER_FLEET:
		case CONTAINER_STRUCTURE:
			super.save();
			break;
		default:
			throw new IllegalStateException(
				"Container type is not persistable: '" +
				getContainerType() + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
