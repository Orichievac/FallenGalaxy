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

import com.google.gwt.core.client.JavaScriptObject;

import fr.fg.client.core.Utilities;
import fr.fg.client.openjwt.core.Dimension;


public class StructureData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "StructureData"; //$NON-NLS-1$
	
	public final static String
	PLAYER = "player",
	ENEMY = "enemy",
	ALLY = "ally",
	DEFENSIVE = "defensive",
	TOTAL = "total",
	ALLIED = "allied",
	NEUTRAL = "neutral",
	PIRATE = "pirate";
	
	public final static int
		TYPE_STOREHOUSE = 0,
		TYPE_SPACESHIP_YARD = 1,
		TYPE_FORCE_FIELD = 2,
		TYPE_STASIS_CHAMBER = 3,
		TYPE_HYPERSPACE_RELAY = 4,
		TYPE_GENERATOR = 5,
		TYPE_EXPLOITATION_TITANE=6,
		TYPE_EXPLOITATION_CRISTAL=7,
		TYPE_EXPLOITATION_ANDIUM=8,
		TYPE_EXPLOITATION_ANTIMATIERE=9,
		TYPE_INFRASTRUCTURE=10,
		TYPE_LABORATORY=11,
		TYPE_DEFENSE_HANGAR= 12;
	
	public final static String
		FIELD_ID = "a",
		FIELD_NAME = "b",
		FIELD_HULL = "c",
		FIELD_X = "d",
		FIELD_Y = "e",
		FIELD_TREATY = "f",
		FIELD_OWNER = "g",
		FIELD_ALLY_NAME = "h",
		FIELD_ALLY_TAG = "i",
		FIELD_TYPE = "j",
		FIELD_MAX_HULL = "k",
		FIELD_AI = "l",
		FIELD_RESOURCES = "n",
		FIELD_LEVEL = "p",
		FIELD_MODULES = "q",
		FIELD_PAYLOAD = "r",
		FIELD_SLOTS = "s",
		FIELD_BUILD_SLOTS = "t",
		FIELD_BUILD_SLOT_ORDERED = "u",
		FIELD_LAST_UPDATE = "v",
		FIELD_MAX_SHIPS = "w",
		FIELD_WITHIN_FORCE_FIELD_RANGE = "x",
		FIELD_AFFECTED_STRUCTURES = "y",
		FIELD_SKILLS = "z",
		FIELD_ACTIVATED = "A",
		FIELD_BUY_FLEET_REMAINING_TIME = "B",
		FIELD_SHARED = "C",
		FIELD_MAX_ENERGY = "D",
		FIELD_USED_ENERGY = "E",
		FIELD_SPACESHIP_YARD_DECKS = "F",
		FIELD_AWAY = "G",
		FIELD_CONNECTED = "H";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static int[] backgroundOffsets;
	
	static {
		// Compte le nombre de bâtiments différents
		int count = 0;
		boolean stop = false;
		do {
			try {
				getSize(count);
				count++;
			} catch (Exception e) {
				stop = true;
			}
		} while (!stop);
		
		count++;
		
		backgroundOffsets = new int[count];
		for (int i = 0; i < count; i++) {
			if (i > 0)
				backgroundOffsets[i] = backgroundOffsets[i - 1] + getSize(i - 1).getHeight();
			else
				backgroundOffsets[i] = 0;
		}
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected StructureData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getId() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_ID];
	}-*/;
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_NAME];
	}-*/;
	
	public final native int getHull() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_HULL];
	}-*/;
	
	public final native int getMaxHull() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_MAX_HULL];
	}-*/;
	
	public final native int getX() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_X];
	}-*/;
	
	public final native int getY() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_Y];
	}-*/;
	
	public final native int getType() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_TYPE];
	}-*/;
	
	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_TREATY];
	}-*/;
	
	public final native String getOwner() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_OWNER];
	}-*/;
	
	public final native String getAllyName() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_ALLY_NAME];
	}-*/;
	
	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_ALLY_TAG];
	}-*/;
	
	public final native boolean isAi() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_AI];
	}-*/;
	
	public final native int getResourcesCount() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_RESOURCES].length;
	}-*/;
	
	public final native double getResourceAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_RESOURCES][index];
	}-*/;
	
	public final native int getLevel() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_LEVEL];
	}-*/;
	
	public final native double getPayload() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_PAYLOAD];
	}-*/;
	
	public final native int getModulesCount() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_MODULES].length;
	}-*/;
	
	public final native StructureModuleData getModuleAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_MODULES][index];
	}-*/;
	
	public native final int getSlotsCount() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_SLOTS].length;
	}-*/;
	
	public native final SlotInfoData getSlotAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_SLOTS][index];
	}-*/;

	public native final int getBuildSlotsCount() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_BUILD_SLOTS].length;
	}-*/;

	public native final SlotInfoData getBuildSlotAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_BUILD_SLOTS][index];
	}-*/;
	
	public native final int getLastUpdate() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_LAST_UPDATE];
	}-*/;
	
	public native final int getMaxShips() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_MAX_SHIPS];
	}-*/;
	
	public native final boolean isWithinForceFieldRange() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_WITHIN_FORCE_FIELD_RANGE];
	}-*/;
	
	public native final int getIdAffectedStructuresCount() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_AFFECTED_STRUCTURES].length;
	}-*/;
	
	public native final int getSkillsCount() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_SKILLS].length;
	}-*/;
	
	public native final StructureSkillData getSkillAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_SKILLS][index];
	}-*/;
	
	public native final double getIdAffectedStructureAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_AFFECTED_STRUCTURES][index];
	}-*/;
	
	public native final boolean isActivated() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_ACTIVATED];
	}-*/;

	public native final int getBuyFleetRemainingTime() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_BUY_FLEET_REMAINING_TIME];
	}-*/;

	public native final boolean isShared() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_SHARED];
	}-*/;
	
	public native final int getMaxEnergy() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_MAX_ENERGY];
	}-*/;
	
	public native final int getUsedEnergy() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_USED_ENERGY];
	}-*/;

	public native final int getSpaceshipYardDecks() /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_SPACESHIP_YARD_DECKS];
	}-*/;
	
	 public native final boolean isConnected() /*-{
     return this[@fr.fg.client.data.StructureData::FIELD_CONNECTED] !== undefined ?
             this[@fr.fg.client.data.StructureData::FIELD_CONNECTED] :
             (this[@fr.fg.client.data.StructureData::FIELD_TREATY] ==
                     @fr.fg.client.data.TreatyData::PLAYER ? true : false);
}-*/;

public native final boolean isAway() /*-{
     return this[@fr.fg.client.data.StructureData::FIELD_AWAY] !== undefined ?
             this[@fr.fg.client.data.StructureData::FIELD_AWAY] : false;
}-*/;
	
	public final int getInterpolatedSlotIdAt(int index, long lastUpdate) {
		if (getSlotAt(index).getCount() == 0) {
			// Détermine si le slot est le premier slot libre, et donc peut
			// potentiellement recevoir les vaisseaux en cours de construction
			for (int i = 0; i < index; i++)
				if (getSlotAt(i).getId() == 0)
					return 0;
			
			for (int j = 0; j < getBuildSlotsCount(); j++) {
				if (getBuildSlotAt(j).getId() != 0) {
					// Détermine si les vaisseaux en cours de construction
					// sont déjà présents sur un autre slot
					for (int k = 0; k < getSlotsCount(); k++)
						if (getBuildSlotAt(j).getId() == getSlotAt(k).getId())
							return 0;
					
					// Interpolation du nombre de vaisseaux construits
					double shipProduction = getShipProduction();
					
					double newSlotCount = Math.max(0,
						getBuildSlotAt(j).getCount() -
						(Utilities.getCurrentTime() - lastUpdate +
						getLastUpdate()) * (shipProduction > 0 ?
						shipProduction / ShipData.SHIPS[getBuildSlotAt(
								j).getId()].getBuildTime() : 0));
					
					long builtSlotCount = (long) (Math.ceil(
						getBuildSlotAt(j).getCount()) -
						Math.ceil(newSlotCount));
					
					return builtSlotCount == 0 ? 0 : getBuildSlotAt(j).getId();
				}
				
				// Seul le slot en cours de construction est pris en compte
				if (getBuildSlotAt(j).getId() != 0)
					return 0;
			}
			
			return 0;
		} else {
			return getSlotAt(index).getId();
		}
	}
	
	public final long getInterpolatedSlotCountAt(int index, long lastUpdate) {
		if (getSlotAt(index).getCount() == 0) {
			// Détermine si le slot est le premier slot libre, et donc peut
			// potentiellement recevoir les vaisseaux en cours de construction
			for (int i = 0; i < index; i++)
				if (getSlotAt(i).getId() == 0)
					return 0;
			
			for (int j = 0; j < getBuildSlotsCount(); j++) {
				if (getBuildSlotAt(j).getId() != 0) {
					// Détermine si les vaisseaux en cours de construction
					// sont déjà présents sur un autre slot
					for (int k = 0; k < getSlotsCount(); k++)
						if (getBuildSlotAt(j).getId() == getSlotAt(k).getId())
							return 0;
					
					// Interpolation du nombre de vaisseaux construits
					double shipProduction = getShipProduction();
					
					double newSlotCount = Math.max(0,
						getBuildSlotAt(j).getCount() -
						(Utilities.getCurrentTime() - lastUpdate +
						getLastUpdate()) * (shipProduction > 0 ?
						shipProduction / ShipData.SHIPS[getBuildSlotAt(
								j).getId()].getBuildTime() : 0));
					
					return (long) (Math.ceil(getBuildSlotAt(j).getCount()) -
						Math.ceil(newSlotCount));
				}
				
				// Seul le slot en cours de construction est pris en compte
				if (getBuildSlotAt(j).getId() != 0)
					return 0;
			}
			
			return 0;
		} else {
			long slotCount = (long) getSlotAt(index).getCount();
			
			for (int j = 0; j < getBuildSlotsCount(); j++) {
				if (getBuildSlotAt(j).getId() == getSlotAt(index).getId()) {
					// Interpolation du nombre de vaisseaux construits
					double shipProduction = getShipProduction();
					
					double slotsBuiltCount = Math.max(0,
						getBuildSlotAt(j).getCount() -
						(Utilities.getCurrentTime() - lastUpdate +
						getLastUpdate()) * (shipProduction > 0 ?
						shipProduction / ShipData.SHIPS[getSlotAt(
							index).getId()].getBuildTime() : 0));
					
					slotCount += Math.ceil(getBuildSlotAt(j).getCount()) -
						Math.ceil(slotsBuiltCount);
					break;
				}
				
				// Seul le slot en cours de construction est pris en compte
				if (getBuildSlotAt(j).getId() != 0)
					break;
			}
			
			return slotCount;
		}
	}
	
	public native final double getBuildSlotOrderedAt(int index) /*-{
		return this[@fr.fg.client.data.StructureData::FIELD_BUILD_SLOTS][index]
			[@fr.fg.client.data.StructureData::FIELD_BUILD_SLOT_ORDERED];
	}-*/;
	
	public final long getWeight() {
		return getWeight(getType(), getLevel());
	}
	
	public final long getTotalResources() {
		long total = 0;
		for (int i = 0; i < getResourcesCount(); i++)
			total += (long) getResourceAt(i);
		return total;
	}
	
	public final long getPayload2() {
		long payload = 0;
			payload = (long) Math.floor(Math.pow(2.15, getModuleLevel(1)) * 100000.);
		return payload;
	}
	
	public final double getShipProduction() {
		return Math.pow(1.7, getModuleLevel(StructureModuleData.TYPE_HANGAR));
	}
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final Dimension getSize() {
		return getSize(getType());
	}
	
	public final int getModuleLevel(int moduleType) {
		for (int i = 0; i < getModulesCount(); i++)
			if (getModuleAt(i).getType() == moduleType)
				return getModuleAt(i).getLevel();
		
		return 0;
	}
	
	public final long[] getModuleCost(int moduleType, int level) {
		double[] moduleCostCoef = StructureModuleData.getCostCoef(moduleType);
		double structureCostCoef;
		if (moduleType >= StructureModuleData.TYPE_DECK_FIGHTER &&
				moduleType <= StructureModuleData.TYPE_DECK_BATTLECRUISER)
			structureCostCoef = 1;
		else
			structureCostCoef = getCostCoef(moduleType);
		
		int[] structureCost = StructureData.getBaseCost(getType());
		int baseCost = 0;
		for (int i = 0; i < 4; i++)
			baseCost += structureCost[i];
		
		// Calcule le prix de l'amélioration du module
		long[] cost = new long[moduleCostCoef.length];
		for (int i = 0; i < cost.length; i++)
			cost[i] = (long) ((( Math.floor(baseCost * structureCostCoef * moduleCostCoef[i])) / 100) * 100);
		
		return cost;
	}
	
	public static long getWeight(int type, int level) {
		long dimension = getSize(type).getWidth() * getSize(type).getHeight();
		
		int coef;
		if (dimension <= 1)
			coef = 1500;
		else if (dimension <= 4)
			coef = 3000;
		else
			coef = 5000;
		
		return (long) (coef * Math.pow(1.25, level));
	}
	
	public static int[] getValidModules(int type) {
		switch (type) {
		case TYPE_STOREHOUSE:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_RESOURCES_PAYLOAD
			};
		case TYPE_SPACESHIP_YARD:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_HANGAR,
				StructureModuleData.TYPE_DECK_FIGHTER,
				StructureModuleData.TYPE_DECK_CORVETTE,
				StructureModuleData.TYPE_DECK_FRIGATE,
				StructureModuleData.TYPE_DECK_DESTROYER,
				StructureModuleData.TYPE_DECK_DREADNOUGHT,
				StructureModuleData.TYPE_DECK_BATTLECRUISER,
			};
		case TYPE_FORCE_FIELD:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_REPAIR,
			};
		case TYPE_STASIS_CHAMBER:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_SKILL_RELOAD,
			};
		case TYPE_HYPERSPACE_RELAY:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_WARP_FIELD,
			};
		case TYPE_GENERATOR:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_REACTOR,
			};
		case TYPE_EXPLOITATION_TITANE:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_PROD_TITANE,
			};
		case TYPE_EXPLOITATION_CRISTAL:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_PROD_CRISTAL
			};
		case TYPE_EXPLOITATION_ANDIUM:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_PROD_ANDIUM,
			};
		case TYPE_EXPLOITATION_ANTIMATIERE:
			return new int[]{
				StructureModuleData.TYPE_HULL,
				StructureModuleData.TYPE_PROD_ANTIMATIERE,
			};
		case TYPE_LABORATORY:
			return new int[]{
					StructureModuleData.TYPE_HULL,
					StructureModuleData.TYPE_PROD_IDEA,
				};
		case TYPE_INFRASTRUCTURE:
			return new int[]{
					StructureModuleData.TYPE_HULL,
					StructureModuleData.TYPE_PROD_CREDIT,
				};
		case TYPE_DEFENSE_HANGAR:
			return new int[]{
					StructureModuleData.TYPE_HULL,
					StructureModuleData.TYPE_LVL_PNJ,
					StructureModuleData.TYPE_NUMBER_PNJ,
					StructureModuleData.TYPE_PROD_PNJ,
				};
		}
		
		return new int[0];
	}
	
	public final double getCostCoef(int type) {
		for (int i = 0; i < getModulesCount(); i++)
			if (getModuleAt(i).getType() == type)
				return Math.pow(2, getModuleAt(i).getLevel()) *
					(getLevel() + 1) / (getModuleAt(i).getLevel() + 1);
		return getLevel() + 1;
	}
	
	public final long getDismountCost() {
		long coef = 0;
		int[] baseCost = StructureData.getBaseCost(getType());
		for (int i = 0; i < 4; i++)
			coef += baseCost[i];
		coef /= 7;
		return (long) (coef * Math.pow(1.25, getLevel()));
	}
	
	public final int getEnergyConsumption() {
		return getEnergyConsumption(getType());
	}
	
	public static int[] getBaseCost(int type) {
		switch (type) {
		case TYPE_STOREHOUSE:
			return new int[]{10000, 0, 0, 0, 0};
		case TYPE_SPACESHIP_YARD:
			return new int[]{0, 15000, 0, 0, 0};
		case TYPE_FORCE_FIELD:
			return new int[]{0, 0, 5000, 0, 0};
		case TYPE_STASIS_CHAMBER:
			return new int[]{0, 0, 0, 5000, 0};
		case TYPE_HYPERSPACE_RELAY:
			return new int[]{0, 50000, 0, 0, 0};
		case TYPE_GENERATOR:
			return new int[]{25000, 0, 0, 0, 0};
		case TYPE_EXPLOITATION_TITANE:
			return new int[]{0, 25000, 0, 0, 0};
		case TYPE_EXPLOITATION_CRISTAL:
			return new int[]{0, 0, 25000, 0, 0};
		case TYPE_EXPLOITATION_ANDIUM:
			return new int[]{25000, 0, 0, 0, 0};
		case TYPE_EXPLOITATION_ANTIMATIERE:
			return new int[]{25000, 0, 10000, 0, 0};
		case TYPE_INFRASTRUCTURE:
			return new int[]{15000, 0, 15000, 0, 0};
		case TYPE_LABORATORY:
			return new int[]{0, 0, 25000, 0, 0};
		case TYPE_DEFENSE_HANGAR:
			return new int[]{20000, 10000, 0, 0, 0};
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static Dimension getSize(int type) {
		switch (type) {
		case TYPE_EXPLOITATION_TITANE:
		case TYPE_EXPLOITATION_CRISTAL:
		case TYPE_EXPLOITATION_ANDIUM:
		case TYPE_INFRASTRUCTURE:
		case TYPE_GENERATOR:
			return new Dimension(5, 5);
		case TYPE_SPACESHIP_YARD:
			return new Dimension(5, 4);
		case TYPE_STOREHOUSE:
		case TYPE_DEFENSE_HANGAR:
			return new Dimension(4, 4);
		case TYPE_STASIS_CHAMBER:
		case TYPE_HYPERSPACE_RELAY:
			return new Dimension(2, 2);
		case TYPE_FORCE_FIELD:
			return new Dimension(1, 1);
		case TYPE_EXPLOITATION_ANTIMATIERE:
		case TYPE_LABORATORY:
			return new Dimension(6,4);
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static int getDefaultHull(int type) {
		switch (type) {
		case TYPE_GENERATOR:
			return 20;
		case TYPE_STOREHOUSE:
		case TYPE_SPACESHIP_YARD:
		case TYPE_EXPLOITATION_TITANE:
		case TYPE_EXPLOITATION_CRISTAL:
		case TYPE_EXPLOITATION_ANDIUM:
		case TYPE_EXPLOITATION_ANTIMATIERE:
		case TYPE_LABORATORY:
		case TYPE_INFRASTRUCTURE:
		case TYPE_DEFENSE_HANGAR:
			return 10;
		case TYPE_STASIS_CHAMBER:
		case TYPE_HYPERSPACE_RELAY:
		case TYPE_FORCE_FIELD:
			return 5;
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static int getEnergyConsumption(int type) {
		switch (type) {
		case TYPE_STOREHOUSE:
			return 2;
		case TYPE_SPACESHIP_YARD:
			return 4;
		case TYPE_FORCE_FIELD:
			return 4;
		case TYPE_STASIS_CHAMBER:
			return 2;
		case TYPE_HYPERSPACE_RELAY:
			return 20;
		case TYPE_EXPLOITATION_TITANE:
		case TYPE_EXPLOITATION_CRISTAL:
		case TYPE_EXPLOITATION_ANDIUM:
		case TYPE_EXPLOITATION_ANTIMATIERE:
		case TYPE_LABORATORY:
		case TYPE_INFRASTRUCTURE:
			return 8;
		case TYPE_GENERATOR:
			return 0;
		case TYPE_DEFENSE_HANGAR:
			return 10;
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static int getEnergyDiffusionRange(int type) {
		switch (type) {
		case TYPE_GENERATOR:
			return 40;
		default:
			return 0;
		}
	}
	
	public static int getBackgroundOffset(int type) {
		if (type >= 0 && type < backgroundOffsets.length)
			return backgroundOffsets[type];
		else
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
	}
	
	public final boolean isAlliedStructure() {
		return (this.getTreaty().equals(ALLIED) || this.getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL));
	}
	
	public final boolean isAllyStructure() {
		return this.getTreaty().equals(ALLY);
	}
	
	public final boolean isEnemyStructure() {
		return this.getTreaty().equals(ENEMY);
	}
	
	public final boolean isPirateStructure() {
		return this.getTreaty().equals(PIRATE);
	}
	
	public final boolean isPlayerStructure() {
		return this.getTreaty().equals(PLAYER);
	}
	
	public final boolean isNeutralStructure() {
		return this.getTreaty().equals(NEUTRAL);
	}
	
	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
