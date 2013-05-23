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

import fr.fg.client.core.ResourcesManager;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.ItemData;
import fr.fg.client.data.ItemInfoData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StructureData;

public class GroupData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		ALLOW_RECEIVE_SHIPS			= 1 << 0,
		ALLOW_RECEIVE_ITEMS			= 1 << 1,
		ALLOW_GIVE_SHIPS			= 1 << 2,
		ALLOW_GIVE_ITEMS			= 1 << 3,
		ALLOW_EXCEED_POWER			= 1 << 4,
		ALLOW_EXCEED_PAYLOAD		= 1 << 5,
		ALLOW_TRANSFER_ALL_SHIPS	= 1 << 6,
		ALLOW_EXCEED_POWER_LEVEL	= 1 << 7;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	
	private String name;
	
	private int rights;
	
	private int starImage;

	private int fleetTag;
	
	private ItemData[] items;
	
	private int[] referenceSlotsId;
	
	private long[] referenceSlotsCount;
	
	private int[] slotsId;
	
	private long[] slotsCount;
	
	private long payload;
	
	private long powerLimit;
	
	private int powerLevelLimit;
	
	private PlayerFleetData fleetData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public GroupData(int playerLevel) {
		this.id = 0;
		this.name = "Nouvelle flotte";
		this.fleetTag = 0;
		this.payload = -1;
		this.powerLevelLimit = playerLevel;

		this.rights = ALLOW_RECEIVE_SHIPS | ALLOW_RECEIVE_ITEMS |
			ALLOW_GIVE_SHIPS | ALLOW_EXCEED_POWER | ALLOW_GIVE_ITEMS;
		
		this.items = new ItemData[4];
		for (int i = 0; i < items.length; i++)
			items[i] = new ItemData(ItemInfoData.TYPE_NONE, 0, 0, 0, 0);
		
		this.slotsId = new int[5];
		this.slotsCount = new long[5];
		this.referenceSlotsId = new int[5];
		this.referenceSlotsCount = new long[5];
	}
	
	public GroupData(PlayerFleetData fleetData, int playerLevel) {
		this.fleetData = fleetData;
		this.id = fleetData.getId();
		this.name = fleetData.getName();
		this.fleetTag = fleetData.getTag();
		this.payload = -1;
		this.powerLimit = -1;
		this.powerLevelLimit = playerLevel;
		
		this.rights = ALLOW_RECEIVE_SHIPS | ALLOW_RECEIVE_ITEMS |
			ALLOW_GIVE_SHIPS | ALLOW_EXCEED_POWER | ALLOW_GIVE_ITEMS;
		
		this.items = new ItemData[fleetData.getItemsCount()];
		for (int i = 0; i < items.length; i++)
			items[i] = new ItemData(fleetData.getItemAt(i));
		
		this.slotsId = new int[fleetData.getSlotsCount()];
		this.slotsCount = new long[fleetData.getSlotsCount()];
		this.referenceSlotsId = new int[fleetData.getSlotsCount()];
		this.referenceSlotsCount = new long[fleetData.getSlotsCount()];
		
		for (int i = 0; i < slotsId.length; i++) {
			slotsCount[i] = Math.round(fleetData.getSlotAt(i).getCount());
			slotsId[i] = slotsCount[i] == 0 ? 0 : fleetData.getSlotAt(i).getId();
			referenceSlotsId[i] = slotsId[i];
			referenceSlotsCount[i] = slotsCount[i];
		}
		tidySlots();
	}
	
	public GroupData(PlayerStarSystemData systemData, ResourcesManager resourcesManager, long lastUpdate) {
		this.id = systemData.getId();
		this.name = systemData.getName();
		this.starImage = systemData.getStarImage();
		this.payload = (long) BuildingData.getProduction(BuildingData.STOREHOUSE, systemData);
		this.powerLimit = -1;
		this.powerLevelLimit = -1;
		
		this.rights = ALLOW_RECEIVE_ITEMS | ALLOW_GIVE_SHIPS |
			ALLOW_GIVE_ITEMS | ALLOW_EXCEED_POWER | ALLOW_EXCEED_POWER_LEVEL |
			 ALLOW_TRANSFER_ALL_SHIPS | ALLOW_RECEIVE_SHIPS;
		
		this.items = new ItemData[systemData.getResourcesCount()];
		for (int i = 0; i < items.length; i++)
			items[i] = new ItemData(ItemInfoData.TYPE_RESOURCE, i, Math.floor(systemData.getResourceAt(i)), 0, 0);
		
		this.slotsId = new int[systemData.getSlotsCount()];
		this.slotsCount = new long[systemData.getSlotsCount()];
		this.referenceSlotsId = new int[systemData.getSlotsCount()];
		this.referenceSlotsCount = new long[systemData.getSlotsCount()];
		
		for (int i = 0; i < slotsId.length; i++) {
			slotsId[i] = systemData.getInterpolatedSlotIdAt(i, lastUpdate);
			slotsCount[i] = systemData.getInterpolatedSlotCountAt(i, lastUpdate);
			referenceSlotsId[i] = slotsId[i];
			referenceSlotsCount[i] = slotsCount[i];
		}
		
		tidySlots();
	}
	
	public GroupData(SpaceStationData spaceStationData) {
		this.id = spaceStationData.getId();
		this.name = spaceStationData.getName();
		this.powerLimit = -1;
		this.powerLevelLimit = -1;
		this.payload = -1;
		this.rights = ALLOW_RECEIVE_ITEMS | ALLOW_EXCEED_PAYLOAD |
			ALLOW_EXCEED_POWER | ALLOW_EXCEED_POWER_LEVEL;
		
		this.items = new ItemData[spaceStationData.getResourcesCount()];
		for (int i = 0; i < items.length; i++)
			items[i] = new ItemData(ItemInfoData.TYPE_RESOURCE, i, Math.floor(spaceStationData.getResourceAt(i)), 0, 0);
		
		this.slotsId = new int[5];
		this.slotsCount = new long[5];
		this.referenceSlotsId = new int[5];
		this.referenceSlotsCount = new long[5];
	}
	
	public GroupData(StructureData structureData, long lastUpdate) {
		this.id = structureData.getId();
		this.name = structureData.getName();
		this.payload = 0;
		this.powerLimit = structureData.getMaxShips();
		this.powerLevelLimit = -1;
		
		this.slotsId = new int[5];
		this.slotsCount = new long[5];
		this.referenceSlotsId = new int[5];
		this.referenceSlotsCount = new long[5];
		this.items = new ItemData[structureData.getResourcesCount()];
		
		switch (structureData.getType()) {
		case StructureData.TYPE_STOREHOUSE:
			this.payload = (long)structureData.getPayload();
			this.rights = ALLOW_RECEIVE_ITEMS | ALLOW_GIVE_ITEMS |
				ALLOW_EXCEED_POWER_LEVEL | ALLOW_EXCEED_POWER;
			for (int i = 0; i < items.length; i++)
				items[i] = new ItemData(ItemInfoData.TYPE_RESOURCE, i, Math.floor(structureData.getResourceAt(i)), 0, 0);
			break;
		case StructureData.TYPE_SPACESHIP_YARD:
			this.rights = ALLOW_RECEIVE_SHIPS | ALLOW_GIVE_SHIPS |
				ALLOW_EXCEED_POWER_LEVEL | ALLOW_TRANSFER_ALL_SHIPS;
			for (int i = 0; i < slotsId.length; i++) {
				slotsId[i] = structureData.getInterpolatedSlotIdAt(i, lastUpdate);
				slotsCount[i] = structureData.getInterpolatedSlotCountAt(i, lastUpdate);
				referenceSlotsId[i] = slotsId[i];
				referenceSlotsCount[i] = slotsCount[i];
			}
			for (int i = 0; i < items.length; i++)
				items[i] = new ItemData(ItemInfoData.TYPE_NONE, 0, 0, 0, 0);
			break;
		default:
			this.rights = ALLOW_EXCEED_POWER_LEVEL | ALLOW_EXCEED_POWER;
			break;
		}
	}
	
	// --------------------------------------------------------- METHODES -- //

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getStarImage() {
		return starImage;
	}

	public int getFleetTag() {
		return fleetTag;
	}
	
	public PlayerFleetData getFleetData(){
		return fleetData;
	}
	
	public double getTotalWeight() {
		double sum = 0;
		for (int i = 0; i < items.length; i++)
			sum += items[i].getWeight() * items[i].getCount();
		return sum;
	}
	
	public double getPayload() {
		if (this.payload != -1)
			return this.payload;
		
		double payload = 0;
		for (int i = 0; i < slotsId.length; i++)
			if (slotsId[i] != 0)
				payload += slotsCount[i] * ShipData.SHIPS[slotsId[i]].getPayload();
		return payload;
	}
	
	public long getPowerLimit() {
		return powerLimit;
	}
	
	public int getPowerLevelLimit() {
		return powerLevelLimit;
	}
	
	public int getSlotsCount() {
		return slotsId.length;
	}
	
	public int getSlotIdAt(int index) {
		return slotsId[index];
	}

	public void setSlotIdAt(int index, int id) {
		slotsId[index] = id;
		if (id == 0)
			slotsCount[index] = 0;
	}

	public long getSlotCountAt(int index) {
		return slotsCount[index];
	}

	public void setSlotCountAt(int index, long count) {
		slotsCount[index] = count;
		if (count == 0)
			slotsId[index] = 0;
	}

	public int getReferenceSlotIdAt(int index) {
		return referenceSlotsId[index];
	}

	public long getReferenceSlotCountAt(int index) {
		return referenceSlotsCount[index];
	}

	public int getItemsCount() {
		return items.length;
	}
	
	public ItemData getItemAt(int index) {
		return items[index];
	}
	
	public void setItemAt(int index, ItemData item) {
		this.items[index] = item;
	}
	
	public int getPower() {
		int power = 0;
		for (int i = 0; i < slotsId.length; i++)
			power += slotsId[i] != 0 ?
				ShipData.CLASSES_POWER[ShipData.SHIPS[slotsId[i]].getShipClass()] *
				slotsCount[i] : 0;
		return power;
	}
	
	public int getPowerLevel() {
		return getLevelAtPower(getPower());
	}
	
	public static int getLevelAtPower(int power) {
		int level = 1;
		while (true) {
			if (power < getPowerAtLevel(level + 1))
				return level;
			level++;
			
			if (level >= 999)
				return 999;
		}
	}
	
	public static int getPowerAtLevel(int level) {
		if (level <= 1) {
			return 0;
		} else {
			// Voir materials/simulation development.xlsx
			double value = 500;
			double prod = 25;
			
			for (int i = 0; i < level - 2; i++) {
				double prodBefore = prod;
				prod = prod * (1.02 + .53 * Math.pow(.95, i + 1));
				
				// Coefficient fonction de la production
				double coef1 = prod / prodBefore;
				
				// Coefficient fonction du nombre de flottes à détruire pour
				// atteindre le niveau suivant
				double coef2 = (3 + 2 * (i + 2)) / (double) (3 + 2 * (i + 1));
				value = value * coef1 / coef2;
			}
			return (int) Math.floor(value / 10) * 10 + 1;
		}
	}
	
	public void tidySlots() {
		for (int i = 0; i < slotsId.length; i++) {
			if (slotsId[i] == 0) {
				for (int j = i + 1; j < slotsId.length; j++) {
					if (slotsId[j] != 0) {
						int tmpId = slotsId[j];
						long tmpCount = slotsCount[j];
						slotsId[j] = slotsId[i];
						slotsCount[j] = slotsCount[i];
						slotsId[i] = tmpId;
						slotsCount[i] = tmpCount;
						break;
					}
				}
			}
		}
	}
	
	public boolean hasRight(int right) {
		return (rights & right) != 0;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
