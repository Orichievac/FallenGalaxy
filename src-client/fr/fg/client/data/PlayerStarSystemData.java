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

package fr.fg.client.data;

import com.google.gwt.core.client.JavaScriptObject;

import fr.fg.client.core.Utilities;

public class PlayerStarSystemData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Population maximale des systèmes
	public final static int SYSTEM_POPULATION_CAPACITY = 5;
	
	// Capacité de stockage initiale des systèmes
	public final static int SYSTEM_STORAGE_CAPACITY = 5000;
	
	public final static double POPULATION_GROWTH = 0.0000011574074;
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_STAR_IMAGE = "b", //$NON-NLS-1$
		FIELD_NAME = "c", //$NON-NLS-1$
		FIELD_AREA = "d", //$NON-NLS-1$
		FIELD_BUILD = "e", //$NON-NLS-1$
		FIELD_BUILDINGS = "f", //$NON-NLS-1$
		FIELD_RESOURCES = "g", //$NON-NLS-1$
		FIELD_CREDITS = "h", //$NON-NLS-1$
		FIELD_POPULATION = "i", //$NON-NLS-1$
		FIELD_LAST_UPDATE = "j", //$NON-NLS-1$
		FIELD_LAST_POPULATION_UPDATE = "k", //$NON-NLS-1$
		FIELD_SLOTS = "l", //$NON-NLS-1$
		FIELD_X = "m", //$NON-NLS-1$
		FIELD_Y = "n", //$NON-NLS-1$
		FIELD_BUILD_SLOTS = "o", //$NON-NLS-1$
		FIELD_BUILD_SLOT_ORDERED = "p", //$NON-NLS-1$
		FIELD_AVAILABLE_RESOURCES = "q", //$NON-NLS-1$
		FIELD_AVAILABLE_SPACE = "r", //$NON-NLS-1$
		FIELD_PRODUCTION_MODIFIER = "s",
		FIELD_BUILDING_LAND = "t",
		FIELD_SHORTCUT = "u",
		FIELD_SHIP_PRODUCTION_MODIFIER = "v",
		FIELD_POPULATION_GROWTH_MODIFIER = "w"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected PlayerStarSystemData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_ID];
	}-*/;
	
	public native final int getStarImage() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_STAR_IMAGE];
	}-*/;
	
	public native final String getName() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_NAME];
	}-*/;
	
	public native final PlayerAreaData getArea() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_AREA];
	}-*/;
	
	public native final int getBuildsCount() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILD].length;
	}-*/;

	public native final BuildData getBuildAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILD][index];
	}-*/;
	
	public native final int getBuildingsCount(String type, int level) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILDINGS][type][level];
	}-*/;
	
	public native final int getResourcesCount() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_RESOURCES].length;
	}-*/;
	
	public native final double getResourceAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_RESOURCES][index];
	}-*/;
	
	public native final double getCredits() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_CREDITS];
	}-*/;
	
	public native final double getPopulation() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_POPULATION];
	}-*/;
	
	public native final int getLastUpdate() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_LAST_UPDATE];
	}-*/;

	public native final int getLastPopulationUpdate() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_LAST_POPULATION_UPDATE];
	}-*/;

	public native final int getSlotsCount() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_SLOTS].length;
	}-*/;
	
	public native final SlotInfoData getSlotAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_SLOTS][index];
	}-*/;

	public native final int getX() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_X];
	}-*/;

	public native final int getY() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_Y];
	}-*/;

	public native final int getBuildSlotsCount() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILD_SLOTS].length;
	}-*/;

	public native final SlotInfoData getBuildSlotAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILD_SLOTS][index];
	}-*/;

	public native final double getBuildSlotOrderedAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILD_SLOTS][index]
			[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILD_SLOT_ORDERED];
	}-*/;

	public native final int getAvailableResource(int index) /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_AVAILABLE_RESOURCES][index];
	}-*/;

	public native final int getAvailableSpace() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_AVAILABLE_SPACE];
	}-*/;

	public native final int getShortcut() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_SHORTCUT];
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
					double shipProduction = BuildingData.getProduction(
						BuildingData.SPACESHIP_YARD, this) *
						getShipProductionModifier();
					
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
					double shipProduction = BuildingData.getProduction(
						BuildingData.SPACESHIP_YARD, this) *
						getShipProductionModifier();
					
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
					double shipProduction = BuildingData.getProduction(
						BuildingData.SPACESHIP_YARD, this) *
						getShipProductionModifier();
					
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
	
	public final long getTotalResources() {
		long total = 0;
		for (int i = 0; i < getResourcesCount(); i++)
			total += (long) getResourceAt(i);
		return total;
	}
	
	public native final double getProductionModifier() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_PRODUCTION_MODIFIER];
	}-*/;

	public native final int getBuildingLand() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_BUILDING_LAND];
	}-*/;

	public native final double getShipProductionModifier() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_SHIP_PRODUCTION_MODIFIER];
	}-*/;
	
	public native final double getPopulatinGrowthModifier() /*-{
		return this[@fr.fg.client.data.PlayerStarSystemData::FIELD_POPULATION_GROWTH_MODIFIER];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
