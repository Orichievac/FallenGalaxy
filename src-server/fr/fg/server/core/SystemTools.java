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

package fr.fg.server.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import fr.fg.client.data.BuildData;
import fr.fg.client.data.PlayerAreaData;
import fr.fg.client.data.PlayerSectorData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.server.data.Area;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class SystemTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	// TODO jgottero à utiliser dans toutes les actions
	public static StarSystem getSystemByIdWithChecks(int idSystem,
			int idOwner) throws IllegalOperationException {
		StarSystem system = DataAccess.getSystemById(idSystem);
		
		// Vérifie que le système existe
		if (system == null)
			throw new IllegalOperationException("Le système n'existe pas.");

		// Vérifie que le système appartient au joueur
		if (system.getIdOwner() != idOwner)
			throw new IllegalOperationException("Le système ne vous appartient pas.");
		
		return system;
	}
	
	public static JSONStringer getPlayerSystems(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		// Met à jour le joueur, pour avoir la dernière valeur des crédits
		player = DataAccess.getPlayerById(player.getId());
		
		long now = Utilities.now();
		json.array();
		
		List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
		
		Collections.sort(systems, new Comparator<StarSystem>() {
			public int compare(StarSystem s1, StarSystem s2) {
				if (s1.getColonizationDate() < s2.getColonizationDate())
					return -1;
				return 1;
			}
		});
		
		for (StarSystem system : systems) {
			Area area = system.getArea();
			Sector sector = area.getSector();
			
			
			json.object().
				key(PlayerStarSystemData.FIELD_ID).					value(system.getId()).
				key(PlayerStarSystemData.FIELD_LAST_UPDATE).		value(now - system.getLastUpdate()).
				key(PlayerStarSystemData.FIELD_LAST_POPULATION_UPDATE).value(now - system.getLastPopulationUpdate()).
				key(PlayerStarSystemData.FIELD_POPULATION).			value(system.getPopulation()).
				key(PlayerStarSystemData.FIELD_CREDITS).			value(player.getCredits()).
				key(PlayerStarSystemData.FIELD_NAME).				value(system.getName()).
				key(PlayerStarSystemData.FIELD_X).					value(system.getX()).
				key(PlayerStarSystemData.FIELD_Y).					value(system.getY()).
				key(PlayerStarSystemData.FIELD_SHORTCUT).			value(system.getShortcut()).
				key(PlayerStarSystemData.FIELD_STAR_IMAGE).			value(system.getStarImage()).
				key(PlayerStarSystemData.FIELD_AVAILABLE_SPACE).	value(system.getAvailableSpace()).
				key(PlayerStarSystemData.FIELD_BUILDING_LAND).		value(system.getBuildingLand()).
				key(PlayerStarSystemData.FIELD_PRODUCTION_MODIFIER).value(system.getProductionModifier()).
				key(PlayerStarSystemData.FIELD_SHIP_PRODUCTION_MODIFIER).value(system.getShipProductionModifier()).
				key(PlayerStarSystemData.FIELD_POPULATION_GROWTH_MODIFIER).value(system.getPopulationGrowthModifier());
			
			int[] availableResources = system.getAvailableResources();
			json.key(PlayerStarSystemData.FIELD_AVAILABLE_RESOURCES).array().
				value(availableResources[0]).
				value(availableResources[1]).
				value(availableResources[2]).
				value(availableResources[3]).
				endArray();
			
			json.key(PlayerStarSystemData.FIELD_AREA).
				object().
					key(PlayerAreaData.FIELD_ID).			value(area.getId()).
					key(PlayerAreaData.FIELD_NAME).			value(area.getName()).
					key(PlayerAreaData.FIELD_X).			value(area.getX()).
					key(PlayerAreaData.FIELD_Y).			value(area.getY()).
					key(PlayerAreaData.FIELD_DOMINATION).	value(area.getDominatingAllyName()).
					key(PlayerAreaData.FIELD_SECTOR).
					object().
						key(PlayerSectorData.FIELD_ID).		value(sector.getId()).
						key(PlayerSectorData.FIELD_NAME).	value(sector.getName()).
						key(PlayerSectorData.FIELD_X).		value(sector.getX()).
						key(PlayerSectorData.FIELD_Y).		value(sector.getY()).
						key(PlayerSectorData.FIELD_TYPE).		value(sector.getType()).
					endObject().
					key(PlayerAreaData.FIELD_SPACE_STATIONS_COUNT).	value(area.getSpaceStations().size()).
					key(PlayerAreaData.FIELD_MAX_SPACE_STATIONS).	value(area.getSpaceStationsLimit()).
				endObject();
			
			// Bâtiments construits
			json.key(PlayerStarSystemData.FIELD_BUILDINGS).object();
			for (int i = 0; i < Building.BUILDING_COUNT; i++) {
				int[] buildings = system.getBuildings(i);
				
				json.key(Building.BUILDING_LABELS[i]).array();
				for (int j = 0; j < Building.BUILDING_LEVEL_COUNT; j++)
					json.value(buildings[j]);
				json.endArray();
			}
			json.endObject();
			
			// Ressources, sous la forme d'un tableau
			json.key(PlayerStarSystemData.FIELD_RESOURCES).array();
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				json.value(Math.floor(system.getResource(i)));
			json.endArray();
			
			// Vaisseaux en orbite sur le système
			json.key(PlayerStarSystemData.FIELD_SLOTS).array();
			
			for (int i = 0; i < GameConstants.SYSTEM_SLOT_COUNT; i++) {
				Slot slot = system.getSlot(i);
				
				json.object().
					key(SlotInfoData.FIELD_ID).		value(slot.getId()).
					key(SlotInfoData.FIELD_COUNT).	value(slot.getCount()).
					key(SlotInfoData.FIELD_FRONT).	value(slot.isFront()).
					endObject();
			}
			json.endArray();
			
			// Vaisseaux en cours de construction sur le système
			json.key(PlayerStarSystemData.FIELD_BUILD_SLOTS).	array();
			for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
				Slot buildSlot = system.getBuildSlot(i);
				
				json.object().
					key(SlotInfoData.FIELD_ID).							value(buildSlot.getId()).
					key(SlotInfoData.FIELD_COUNT).						value(buildSlot.getCount()).
					key(PlayerStarSystemData.FIELD_BUILD_SLOT_ORDERED).	value(system.getBuildSlotOrdered(i)).
					endObject();
			}
			json.endArray();
			
			// Récupère le bâtiment en cours de construction
			json.key(PlayerStarSystemData.FIELD_BUILD).
				array();
			Building currentBuilding = system.getCurrentBuilding();
			Building nextBuilding = system.getNextBuilding();
			Building thirdBuilding = system.getThirdBuilding();
			
			if (currentBuilding != null &&
					(currentBuilding.getEnd() > now ||
					 currentBuilding.getEnd() == 0)) {
				// Batiment en cours de construction
				json.object().
					key(BuildData.FIELD_TYPE).		value(Building.BUILDING_LABELS[currentBuilding.getType()]).
					key(BuildData.FIELD_LEVEL).		value(currentBuilding.getLevel()).
					key(BuildData.FIELD_END).		value(currentBuilding.getEnd() - now).
					key(BuildData.FIELD_PROGRESS).	value(1 - ((currentBuilding.getEnd() - now) /
							(Building.getBuildTime(currentBuilding) *
							system.getProduction(Building.FACTORY)))).
					endObject();
				
				// Bâtiment en attente
				if (nextBuilding != null) {
					json.object().
							key(BuildData.FIELD_TYPE).		value(Building.BUILDING_LABELS[nextBuilding.getType()]).
							key(BuildData.FIELD_LEVEL).		value(nextBuilding.getLevel()).
							key(BuildData.FIELD_END).		value(nextBuilding.getEnd() - now).
						endObject();
				}
				
				//si on a 2e batiment dans la file d'attente et que l'utilisateur est premium
				if (thirdBuilding != null && player.isPremium()) {
					json.object().
							key(BuildData.FIELD_TYPE).		value(Building.BUILDING_LABELS[thirdBuilding.getType()]).
							key(BuildData.FIELD_LEVEL).		value(thirdBuilding.getLevel()).
							key(BuildData.FIELD_END).		value(thirdBuilding.getEnd() - now).
						endObject();
				}
			} else if (currentBuilding != null &&
					currentBuilding.getEnd() != 0 &&
					currentBuilding.getEnd() <= now) {
				
				if(nextBuilding != null  &&
					(nextBuilding.getEnd() > now ||
					 nextBuilding.getEnd() == 0)) {
				// Batiment en cours de construction terminé, le batiment
				// en attente devient le batiment en cours de construction
				json.object().
					key(BuildData.FIELD_TYPE).		value(Building.BUILDING_LABELS[nextBuilding.getType()]).
					key(BuildData.FIELD_LEVEL).		value(nextBuilding.getLevel()).
					key(BuildData.FIELD_END).		value(nextBuilding.getEnd() - now).
					endObject();
					//Si on a un batiment en 2e position dans la file d'attente alors il est maintenant en
					// 1ere position de la file.
					if (thirdBuilding != null && player.isPremium() &&
							(thirdBuilding.getEnd() > now ||
							thirdBuilding.getEnd() == 0)) {
						json.object().
						key(BuildData.FIELD_TYPE).		value(Building.BUILDING_LABELS[thirdBuilding.getType()]).
						key(BuildData.FIELD_LEVEL).		value(thirdBuilding.getLevel()).
						key(BuildData.FIELD_END).		value(thirdBuilding.getEnd() - now).
						endObject();
					}
				}
			}
			
			json.endArray().
				endObject();
		}
		
		json.endArray();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
