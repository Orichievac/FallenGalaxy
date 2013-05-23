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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fr.fg.client.data.PlayerAreaData;
import fr.fg.client.data.PlayerGeneratorData;
import fr.fg.client.data.PlayerSectorData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.StructureModuleData;
import fr.fg.client.data.StructureSkillData;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureModule;
import fr.fg.server.data.StructureSkill;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.data.Treaty;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class StructureTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getAreaStructures(JSONStringer json, Area area,
			Player player) throws Exception {
		return getAreaStructures(json, area, player, null, null, null, null, null, null, null);
	}
	
	public static JSONStringer getAreaStructures(JSONStringer json, Area area,
			Player player, HashMap<Long, String> treatiesCache,
			List<Point> allySystems, List<Point> allyFleets,
			List<Point> allyObserverWards, List<Point> allySpaceStations,
			List<Structure> allyStructures,
			List<Point> allySentryWards) throws Exception {
		if (json == null)
			json = new JSONStringer();
		
		if (treatiesCache == null)
			treatiesCache = new HashMap<Long, String>();
		if (allySystems == null)
			allySystems = player.getAllySystems(area);
		if (allyFleets == null)
			allyFleets = player.getAllyFleets(area);
		if (allyObserverWards == null)
			allyObserverWards = player.getAllyObserverWards(area);
		if (allySentryWards == null)
			allySentryWards = player.getAllySentryWards(area);
		if (allySpaceStations == null)
			allySpaceStations = player.getAllySpaceStations(area);
		
		List<Structure> structures = area.getStructures();
		
		json.array();
		
		long now = Utilities.now();
		StorehouseResources storehouseResources =
			area.getStorehouseResourcesByPlayer(player.getId());
		
		double payload = 0;
		synchronized (structures) {
			for (Structure structure : structures)
				if (structure.getIdOwner() == player.getId())
					payload += structure.getPayload();
		}
		
		synchronized (structures) {
			for (Structure structure : structures) {
				if ((structure.getIdOwner() == player.getId()) ||
						player.isLocationVisible(area,
							structure.getX(), structure.getY(),
							structure.getBounds().width / 2,
							allySystems, allyFleets, allyObserverWards,
							allySpaceStations, allyStructures)) {
					// Détermine si la structure appartient a un joueur ou une alliance
					Player owner = structure.getOwner();
					String treaty = owner.getTreatyWithPlayer(treatiesCache, player);
					
					json.object().
						key(StructureData.FIELD_ID).		value(structure.getId()).
						key(StructureData.FIELD_TYPE).		value(structure.getType()).
						key(StructureData.FIELD_NAME).		value(structure.getName()).
						key(StructureData.FIELD_TREATY).	value(treaty).
						key(StructureData.FIELD_OWNER).		value(owner != null ? owner.getLogin() : "").
						key(StructureData.FIELD_ALLY_NAME).	value(owner.getAllyName()).
						key(StructureData.FIELD_ALLY_TAG).	value(owner.getAllyTag()).
						key(StructureData.FIELD_X).			value(structure.getX()).
						key(StructureData.FIELD_Y).			value(structure.getY()).
						key(StructureData.FIELD_HULL).		value(structure.getHull()).
						key(StructureData.FIELD_MAX_HULL).	value(structure.getMaxHull()).
						key(StructureData.FIELD_LEVEL).		value(structure.getLevel()).
						key(StructureData.FIELD_AI).		value(owner.isAi()).
						key(StructureData.FIELD_ACTIVATED).	value(structure.isActivated()).
						key(StructureData.FIELD_WITHIN_FORCE_FIELD_RANGE).value(structure.getType() != Structure.TYPE_FORCE_FIELD ? structure.getActivatedForceFieldsWithinRange().size() > 0 : false);
					if (owner.isAway()){
						json.key(StructureData.FIELD_AWAY).					value(true);
						}
					if (owner.isConnected()){
						json.key(StructureData.FIELD_CONNECTED).			value(true);
						}
					if (structure.getType() == Structure.TYPE_SPACESHIP_YARD)
						json.key(StructureData.FIELD_SPACESHIP_YARD_DECKS).value(structure.getSpaceshipYardDecks());
					
					json.key(StructureData.FIELD_AFFECTED_STRUCTURES).array();
					if (structure.getType() == Structure.TYPE_FORCE_FIELD) {
						// Structures proches bénéficiant de l'effet du champ de force
						List<Structure> affectedStructures =
							structure.getAllyStructuresWithinRange(Structure.FORCE_FIELD_RADIUS);
						
						for (Structure affectedStructure : affectedStructures)
							json.value(affectedStructure.getId());
					}
					json.endArray();
					
					if (structure.getIdOwner() == player.getId() ||
							owner.getTreatyWithPlayer(player).equals(Treaty.ALLY)) {
						switch (structure.getType()) {
						case Structure.TYPE_GENERATOR:
							json.key(StructureData.FIELD_SHARED).		value(structure.isShared()).
								key(StructureData.FIELD_USED_ENERGY).	value(structure.getUsedEnergy()).
								key(StructureData.FIELD_MAX_ENERGY).	value(structure.getMaxEnergy());
							break;
						default:
							json.key(StructureData.FIELD_SHARED).		value(structure.isShared()).
								key(StructureData.FIELD_USED_ENERGY).	value(0).
								key(StructureData.FIELD_MAX_ENERGY).	value(0);
							break;
						}
					}
					
					if (structure.getIdOwner() == player.getId()) {
						json.key(StructureData.FIELD_PAYLOAD).		value(structure.getAreaPayload()).
							key(StructureData.FIELD_LAST_UPDATE).	value(0).
							key(StructureData.FIELD_MAX_SHIPS).		value(structure.getMaxShips());
						
						json.key(StructureData.FIELD_SKILLS).		array();
						List<StructureSkill> skills = structure.getSkills();
						
						synchronized (skills) {
							for (StructureSkill skill : skills) {
								json.object().
									key(StructureSkillData.FIELD_TYPE).		value(skill.getType()).
									key(StructureSkillData.FIELD_LAST_USE).	value(now - skill.getLastUse()).
									key(StructureSkillData.FIELD_RELOAD).	value(Math.max(0, skill.getReload() - now)).
									endObject();
							}
						}
						json.endArray();
						
						// Ressources disponibles
						json.key(StructureData.FIELD_RESOURCES).	array();
						for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
							json.value(storehouseResources == null ? 0 : storehouseResources.getResource(i));
						json.endArray();
						
						List<StructureModule> modules = structure.getModules();
						
						// Modules construits
						json.key(StructureData.FIELD_MODULES).		array();
						synchronized (modules) {
							for (StructureModule module : modules) {
								json.object().
									key(StructureModuleData.FIELD_TYPE).	value(module.getType()).
									key(StructureModuleData.FIELD_LEVEL).	value(module.getLevel()).
									endObject();
							}
						}
						json.endArray();
						
						if (structure.getType() == Structure.TYPE_SPACESHIP_YARD) {
							StructureSpaceshipYard spaceshipYard = structure.getSpaceshipYard();
							
							// Vaisseaux stockés
							json.key(StructureData.FIELD_BUY_FLEET_REMAINING_TIME).	value(Math.max(0, GameConstants.BUY_FLEET_COOLDOWN + spaceshipYard.getLastBoughtFleet() - Utilities.now())).
								key(StructureData.FIELD_SLOTS).					array();
							Slot[] slots = spaceshipYard.getSlots();
							
							for (int i = 0; i < slots.length; i++) {
								json.object().
									key(SlotInfoData.FIELD_ID).					value(slots[i].getId()).
									key(SlotInfoData.FIELD_COUNT).				value(slots[i].getCount()).
									endObject();
							}
							json.endArray();
							
							// Vaisseaux en cours de construction sur le chantier spatial
							json.key(StructureData.FIELD_BUILD_SLOTS).	array();
							
							for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
								Slot buildSlot = spaceshipYard.getBuildSlot(i);
								
								json.object().
									key(SlotInfoData.FIELD_ID).						value(buildSlot.getId()).
									key(SlotInfoData.FIELD_COUNT).					value(buildSlot.getCount()).
									key(StructureData.FIELD_BUILD_SLOT_ORDERED).	value(spaceshipYard.getBuildSlotOrdered(i)).
									endObject();
							}
							json.endArray();
						} else {
							json.key(StructureData.FIELD_SLOTS).		array().endArray().
								key(StructureData.FIELD_BUILD_SLOTS).	array().endArray();
						}
					} else {
						json.key(StructureData.FIELD_SKILLS).	array();
						List<StructureSkill> skills = structure.getSkills();
						
						synchronized (skills) {
							for (StructureSkill skill : skills) {
								json.object().
									key(StructureSkillData.FIELD_TYPE).		value(skill.getType()).
									key(StructureSkillData.FIELD_RELOAD).	value(skill.getReload() > now ? 1 : 0).
									endObject();
							}
						}
						json.endArray();
					}
					
					json.endObject();
				}
			}
		}
		
		json.endArray();
		
		return json;
	}
	
	public static JSONStringer getPlayerGenerators(JSONStringer json,
			Player player) {
		if (json == null)
			json = new JSONStringer();
		
		List<Structure> structures = player.getStructures();
		json.array();
		
		List<Structure> generators = new ArrayList<Structure>();
		

		synchronized (structures) {
			for (Structure structure : structures) {
				if (structure.getType() == Structure.TYPE_GENERATOR &&
						structure.getIdArea() != 0)
					generators.add(structure);
			}
		}
		
		Collections.sort(generators, new Comparator<Structure>() {
			public int compare(Structure s1, Structure s2) {
				if (s1.getLevel() == s2.getLevel())
					return s1.getId() < s2.getId() ? -1 : 1;
				return s1.getLevel() > s2.getLevel() ? -1 : 1;
			}
		});
		
		for (Structure structure : generators) {
			Area area = structure.getArea();
			Sector sector = area.getSector();
			
			json.object().
				key(PlayerGeneratorData.FIELD_ID).			value(structure.getId()).
				key(PlayerGeneratorData.FIELD_X).			value(structure.getX()).
				key(PlayerGeneratorData.FIELD_Y).			value(structure.getY()).
				key(PlayerGeneratorData.FIELD_NAME).		value(structure.getName()).
				key(PlayerGeneratorData.FIELD_HULL).		value(structure.getHull()).
				key(PlayerGeneratorData.FIELD_MAX_HULL).	value(structure.getMaxHull()).
				key(PlayerGeneratorData.FIELD_USED_ENERGY).	value(structure.getUsedEnergy()).
				key(PlayerGeneratorData.FIELD_MAX_ENERGY).	value(structure.getMaxEnergy()).
				key(PlayerGeneratorData.FIELD_LEVEL).		value(structure.getLevel()).
				key(PlayerGeneratorData.FIELD_SHORTCUT).	value(structure.getShortcut()).
				key(PlayerGeneratorData.FIELD_AREA).		object().
					key(PlayerAreaData.FIELD_ID).				value(area.getId()).
					key(PlayerAreaData.FIELD_NAME).				value(area.getName()).
					key(PlayerAreaData.FIELD_X).				value(area.getX()).
					key(PlayerAreaData.FIELD_Y).				value(area.getY()).
					key(PlayerAreaData.FIELD_DOMINATION).		value(area.getDominatingAllyName()).
					key(PlayerAreaData.FIELD_SECTOR).
					object().
						key(PlayerSectorData.FIELD_ID).			value(sector.getId()).
						key(PlayerSectorData.FIELD_NAME).		value(sector.getName()).
						key(PlayerSectorData.FIELD_X).			value(sector.getX()).
						key(PlayerSectorData.FIELD_Y).			value(sector.getY()).
						key(PlayerSectorData.FIELD_TYPE).		value(sector.getType()).
					endObject().
					key(PlayerAreaData.FIELD_SPACE_STATIONS_COUNT).	value(area.getSpaceStations().size()).
					key(PlayerAreaData.FIELD_MAX_SPACE_STATIONS).	value(area.getSpaceStationsLimit()).
					
					endObject().
				endObject();
				}
		
		
		json.endArray();
		
		return json;
	}
	
	public static Structure getStructureByIdWithChecks(long idStructure,
			int idOwner) throws IllegalOperationException {
		Structure structure = DataAccess.getStructureById(idStructure);
		
		if (structure == null)
			throw new IllegalOperationException(
				"La structure n'existe pas.");
		
		if (structure.getIdOwner() != idOwner)
			throw new IllegalOperationException(
				"Cette structure ne vous appartient pas.");
		
		return structure;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
