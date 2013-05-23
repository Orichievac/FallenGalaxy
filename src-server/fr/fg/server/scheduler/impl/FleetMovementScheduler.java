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

package fr.fg.server.scheduler.impl;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSkill;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.data.Treaty;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.FleetMovementReloadUpdateEvent;
import fr.fg.server.events.impl.JumpFinishedEvent;
import fr.fg.server.scheduler.JobScheduler;
import fr.fg.server.util.LoggingSystem;

public class FleetMovementScheduler extends JobScheduler<Integer> implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetMovementScheduler() {
		List<Fleet> fleets = new ArrayList<Fleet>(DataAccess.getAllFleets());
		
		for (Fleet fleet : fleets)
			if (fleet.getMovementReload() != 0)
				addJob(fleet.getId(), fleet.getMovementReload());
		
		GameEventsDispatcher.addGameEventListener(this,
			FleetMovementReloadUpdateEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof FleetMovementReloadUpdateEvent) {
			FleetMovementReloadUpdateEvent gameEvent =
				(FleetMovementReloadUpdateEvent) event;
			
			if (gameEvent.getSource().getMovementReload() != 0)
				addJob(gameEvent.getSource().getId(), gameEvent.getSource(
					).getMovementReload());
		}
	}
	
	public void process(Integer idFleet, long now) {
		Fleet fleet = DataAccess.getFleetById(idFleet);
		
		if (fleet != null && fleet.getMovementReload() <= now) {
			Player owner = fleet.getOwner();
			
			synchronized (owner.getLock()) {
				if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_COLONIZE)) {
					StarSystem colonizedSystem = fleet.getSystemOver();
					
					if (colonizedSystem != null) {
						if (colonizedSystem.getIdOwner() != 0) {
							if (owner.getColonizationPoints() >=
									GameConstants.SYSTEM_COST) {
								// Le système est capturé
								synchronized (colonizedSystem.getLock()) {
									colonizedSystem = DataAccess.getEditable(colonizedSystem);
									colonizedSystem.setOwner(fleet.getIdOwner());
									colonizedSystem.setLastUpdate(fleet.getMovementReload());
									colonizedSystem.setLastResearchUpdate(fleet.getMovementReload());
									colonizedSystem.setLastPopulationUpdate(fleet.getMovementReload());
									colonizedSystem.setColonizationDate(fleet.getMovementReload());
									colonizedSystem.setCaptureSettings();
									colonizedSystem.save();
								}
								
								// Met à jour l'influence de l'alliance
								int idAlly = owner.getIdAlly();
								if (idAlly != 0)
									DataAccess.getAllyById(idAlly).updateInfluences();
								
								new Event(
									Event.EVENT_SYSTEM_LOST,
									Event.TARGET_PLAYER,
									colonizedSystem.getIdOwner(),
									colonizedSystem.getIdArea(),
									colonizedSystem.getX(),
									colonizedSystem.getY(),
									fleet.getName(),
									owner.getLogin(),
									colonizedSystem.getName()).save();
								new Event(
									Event.EVENT_SYSTEM_CAPTURED,
									Event.TARGET_PLAYER,
									fleet.getIdOwner(),
									colonizedSystem.getIdArea(),
									colonizedSystem.getX(),
									colonizedSystem.getY(),
									fleet.getName(),
									colonizedSystem.getOwner().getLogin(),
									colonizedSystem.getName()).save();
								
								UpdateTools.queueNewEventUpdate(colonizedSystem.getIdOwner(), false);
								UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
								UpdateTools.queuePlayerSystemsUpdate(colonizedSystem.getIdOwner(), false);
								UpdateTools.queuePlayerSystemsUpdate(fleet.getIdOwner(), false);
								UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea());
							} else {
								// FIXME jgottero génère des valeurs négatives !
								// Le système est ravagé
								synchronized (colonizedSystem.getLock()) {
									colonizedSystem = DataAccess.getEditable(colonizedSystem);
									for (int i = 0; i < Building.BUILDING_COUNT; i++) {
										int[] buildings = colonizedSystem.getBuildings(i);
										
										for (int j = 0; j < Building.BUILDING_LEVEL_COUNT; j++)
											buildings[j] = (int) Math.round(Math.random() * buildings[j]);
										
										colonizedSystem.setBuildings(buildings, i);
									}
									colonizedSystem.save();
								}
								
								new Event(
									Event.EVENT_SYSTEM_DEVASTATED,
									Event.TARGET_PLAYER,
									colonizedSystem.getIdOwner(),
									colonizedSystem.getIdArea(),
									colonizedSystem.getX(),
									colonizedSystem.getY(),
									fleet.getName(),
									owner.getLogin(),
									colonizedSystem.getName()).save();
								new Event(
									Event.EVENT_DEVASTATE_SYSTEM,
									Event.TARGET_PLAYER,
									fleet.getIdOwner(),
									colonizedSystem.getIdArea(),
									colonizedSystem.getX(),
									colonizedSystem.getY(),
									fleet.getName(),
									colonizedSystem.getOwner().getLogin(),
									colonizedSystem.getName()).save();
								
								UpdateTools.queueNewEventUpdate(colonizedSystem.getIdOwner(), false);
								UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
								UpdateTools.queuePlayerSystemsUpdate(colonizedSystem.getIdOwner(), false);
							}
						} else {
							// Le système est colonisé
							synchronized (colonizedSystem.getLock()) {
								colonizedSystem = DataAccess.getEditable(colonizedSystem);
								colonizedSystem.setOwner(fleet.getIdOwner());
								colonizedSystem.setLastUpdate(fleet.getMovementReload());
								colonizedSystem.setLastResearchUpdate(fleet.getMovementReload());
								colonizedSystem.setLastPopulationUpdate(fleet.getMovementReload());
								colonizedSystem.setColonizationDate(fleet.getMovementReload());
								colonizedSystem.setColonizationSettings();
								colonizedSystem.save();
							}
							
							// Met à jour l'influence de l'alliance
							int idAlly = owner.getIdAlly();
							if (idAlly != 0)
								DataAccess.getAllyById(idAlly).updateInfluences();
							
							new Event(
								Event.EVENT_COLONIZATION,
								Event.TARGET_PLAYER,
								fleet.getIdOwner(),
								colonizedSystem.getIdArea(),
								colonizedSystem.getX(),
								colonizedSystem.getY(),
								fleet.getName(),
								colonizedSystem.getName()).save();
							
							UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
							UpdateTools.queuePlayerSystemsUpdate(fleet.getIdOwner(), false);
							UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea());
						}
					}
				}
				else
					if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_MIGRATE)) {
						StarSystem colonizedSystem = fleet.getSystemOver();
						StarSystem systemToMigrate = 
							DataAccess.getSystemById(fleet.getIdSystemMigrate());
						
						if (colonizedSystem != null && 
								systemToMigrate.getOwner()==fleet.getOwner()) {
							if(!colonizedSystem.isAi())
							{
							synchronized (colonizedSystem.getLock()){
								synchronized (systemToMigrate.getLock())
								{
										colonizedSystem = DataAccess.getEditable(colonizedSystem);
										systemToMigrate = DataAccess.getEditable(systemToMigrate);
										
										
									//colonizedSystem.setAvailableSpace(systemToMigrate.getAvailableSpace());
									colonizedSystem.setCivilianInfrastructures(systemToMigrate.getCivilianInfrastructures());
									colonizedSystem.setPopulation(systemToMigrate.getPopulation());
									colonizedSystem.setCorporations(systemToMigrate.getCorporations());
									colonizedSystem.setDefensiveDeck(systemToMigrate.getDefensiveDeck());
									//colonizedSystem.setEncodedAvailableResources(systemToMigrate.getEncodedAvailableResources());
									colonizedSystem.setExploitation0(systemToMigrate.getExploitation0());
									colonizedSystem.setExploitation1(systemToMigrate.getExploitation1());
									colonizedSystem.setExploitation2(systemToMigrate.getExploitation2());
									colonizedSystem.setExploitation3(systemToMigrate.getExploitation3());
									colonizedSystem.setExtractorCenter(systemToMigrate.getExtractorCenter());
									colonizedSystem.setSpaceshipYard(systemToMigrate.getSpaceshipYard());
									colonizedSystem.setRefinery(systemToMigrate.getRefinery());
									colonizedSystem.setLaboratory(systemToMigrate.getLaboratory());
									colonizedSystem.setFactory(systemToMigrate.getFactory());
									colonizedSystem.setTradePort(systemToMigrate.getTradePort());
									colonizedSystem.setStorehouse(systemToMigrate.getStorehouse());
									colonizedSystem.setResearchCenter(systemToMigrate.getResearchCenter());
									colonizedSystem.setOwner(fleet.getIdOwner());
									colonizedSystem.setShortcut(systemToMigrate.getShortcut());
									
									colonizedSystem.setSlot0Id(systemToMigrate.getSlot0Id());
									colonizedSystem.setSlot0Count(systemToMigrate.getSlot0Count());
									colonizedSystem.setSlot1Id(systemToMigrate.getSlot1Id());
									colonizedSystem.setSlot1Count(systemToMigrate.getSlot1Count());
									colonizedSystem.setSlot2Id(systemToMigrate.getSlot2Id());
									colonizedSystem.setSlot2Count(systemToMigrate.getSlot2Count());
									colonizedSystem.setSlot3Id(systemToMigrate.getSlot3Id());
									colonizedSystem.setSlot3Count(systemToMigrate.getSlot3Count());
									colonizedSystem.setSlot4Id(systemToMigrate.getSlot4Id());
									colonizedSystem.setSlot4Count(systemToMigrate.getSlot4Count());
									colonizedSystem.setSlot5Id(systemToMigrate.getSlot5Id());
									colonizedSystem.setSlot5Count(systemToMigrate.getSlot5Count());
									colonizedSystem.setSlot6Id(systemToMigrate.getSlot6Id());
									colonizedSystem.setSlot6Count(systemToMigrate.getSlot6Count());
									colonizedSystem.setSlot7Id(systemToMigrate.getSlot7Id());
									colonizedSystem.setSlot7Count(systemToMigrate.getSlot7Count());
									colonizedSystem.setSlot8Id(systemToMigrate.getSlot8Id());
									colonizedSystem.setSlot8Count(systemToMigrate.getSlot8Count());
									colonizedSystem.setSlot9Id(systemToMigrate.getSlot9Id());
									colonizedSystem.setSlot9Count(systemToMigrate.getSlot9Count());
									LoggingSystem.getServerLogger().info("ressource0 : "+systemToMigrate.getResource0());
									colonizedSystem.setResource0(systemToMigrate.getResource0());
									colonizedSystem.setResource1(systemToMigrate.getResource1());
									colonizedSystem.setResource2(systemToMigrate.getResource2());
									colonizedSystem.setResource3(systemToMigrate.getResource3());
									

									
									//Gestion du nombre d'exploitation
									
									
									for(int i =4;i<8;i++)
									{
										int count=0;
										int countTarget=0;
										
										for(int j=0;j<5;j++)
										{
											count+=colonizedSystem.getBuildings(i)[j];
										}
										countTarget=colonizedSystem.getAvailableResources()[i-4];

										while(count>countTarget)
										{
											if(colonizedSystem.getBuildings(i)[0]>0)
											{
												colonizedSystem.setBuildings(new int[]{
													colonizedSystem.getBuildings(i)[0]-1,colonizedSystem.getBuildings(i)[1],
													colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3],
													colonizedSystem.getBuildings(i)[4]}, i);
											}
											else if(colonizedSystem.getBuildings(i)[1]>0)
												colonizedSystem.setBuildings(new int[]{
														colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1]-1,
														colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3],
														colonizedSystem.getBuildings(i)[4]}, i);
											else if(colonizedSystem.getBuildings(i)[2]>0)
												colonizedSystem.setBuildings(new int[]{
														colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1],
														colonizedSystem.getBuildings(i)[2]-1,colonizedSystem.getBuildings(i)[3],
														colonizedSystem.getBuildings(i)[4]}, i);
											else if(colonizedSystem.getBuildings(i)[3]>0)
												colonizedSystem.setBuildings(new int[]{
														colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1],
														colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3]-1,
														colonizedSystem.getBuildings(i)[4]}, i);
											else if(colonizedSystem.getBuildings(i)[4]>0)
												colonizedSystem.setBuildings(new int[]{
														colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1],
														colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3],
														colonizedSystem.getBuildings(i)[4]-1}, i);
											count--;
										}
										count=0;
										countTarget=0;
									}
//									
									

									int i = Building.CIVILIAN_INFRASTRUCTURES;
									int count=0;
									int countTarget=0;
									for(int j=0;j<5;j++)
									{
										count+=colonizedSystem.getBuildings(i)[j];
									}
									countTarget=colonizedSystem.getAvailableSpace();
									while(count>countTarget)
									{
										if(colonizedSystem.getBuildings(i)[0]>0)
										{
										colonizedSystem.setBuildings(new int[]{
												colonizedSystem.getBuildings(i)[0]-1,colonizedSystem.getBuildings(i)[1],
												colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3],
												colonizedSystem.getBuildings(i)[4]}, i);
										}
										else if(colonizedSystem.getBuildings(i)[1]>0)
											colonizedSystem.setBuildings(new int[]{
													colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1]-1,
													colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3],
													colonizedSystem.getBuildings(i)[4]}, i);
										else if(colonizedSystem.getBuildings(i)[2]>0)
											colonizedSystem.setBuildings(new int[]{
													colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1],
													colonizedSystem.getBuildings(i)[2]-1,colonizedSystem.getBuildings(i)[3],
													colonizedSystem.getBuildings(i)[4]}, i);
										else if(colonizedSystem.getBuildings(i)[3]>0)
											colonizedSystem.setBuildings(new int[]{
													colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1],
													colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3]-1,
													colonizedSystem.getBuildings(i)[4]}, i);
										else if(colonizedSystem.getBuildings(i)[4]>0)
											colonizedSystem.setBuildings(new int[]{
													colonizedSystem.getBuildings(i)[0],colonizedSystem.getBuildings(i)[1],
													colonizedSystem.getBuildings(i)[2],colonizedSystem.getBuildings(i)[3],
													colonizedSystem.getBuildings(i)[4]-1}, i);
										count--;
									}	
									
											
									// perte des batiments en cours, et des vaisseaux en cours de prod
										
										
										systemToMigrate.resetSettings();

										colonizedSystem.setLastUpdate(now);
										colonizedSystem.setLastResearchUpdate(now);
										colonizedSystem.setLastPopulationUpdate(now);
										systemToMigrate.setLastUpdate(now);
										systemToMigrate.setLastResearchUpdate(now);
										systemToMigrate.setLastPopulationUpdate(now);
										colonizedSystem.setColonizationDate(now);
										systemToMigrate.setColonizationDate(now);
										
										
										systemToMigrate.save();
										colonizedSystem.save();
									
								}
							}
								// Met à jour l'influence de l'alliance
								int idAlly = owner.getIdAlly();
								if (idAlly != 0)
									DataAccess.getAllyById(idAlly).updateInfluences();

								synchronized (fleet.getLock()){
									fleet= DataAccess.getEditable(fleet);
									fleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
									fleet.setIdSystemMigrate(0);
									fleet.save();
								}
								
								// Evenement pour signaler la fin de migration du système
								DataAccess.save(new Event(
										Event.EVENT_MIGRATION_END,
										Event.TARGET_PLAYER,
										fleet.getIdOwner(),
										fleet.getIdCurrentArea(),
										fleet.getX(),
										fleet.getY(),
										fleet.getName(),
										systemToMigrate.getName(),
										colonizedSystem.getName()));
														
								
								UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
								UpdateTools.queuePlayerSystemsUpdate(fleet.getIdOwner(), false);
								UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea());
							}
							else
							{
								synchronized (fleet.getLock()){
									fleet= DataAccess.getEditable(fleet);
									fleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
									fleet.setIdSystemMigrate(0);
									fleet.save();
								}
							}
						}
					}
				
				else if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_ATTACK_STRUCTURE)) {
					// Attaque de structure
					Structure attackedStructure = fleet.getStructureUnderFleet();
					
					if (attackedStructure != null) {
//						if (attackedStructure.getType() != Structure.TYPE_FORCE_FIELD) {
//							List<Structure> forceFields = attackedStructure.getActivatedForceFieldsWithinRange();
//							if (forceFields.size() > 0)
//								attackedStructure = forceFields.get(0);
//						}
						
						String treaty = attackedStructure.getOwner().getTreatyWithPlayer(owner);
						
						if (treaty.equals(Treaty.ENEMY) || (treaty.equals(Treaty.NEUTRAL) &&
								fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)) {
							if (attackedStructure.getHull() <= fleet.getPowerLevel()) {
								attackedStructure.delete();
								
								// Si une flotte était en train de démonter la structure, l'action est
								// annulée
								List<Fleet> fleets = attackedStructure.getArea().getFleets();
								
								synchronized (fleets) {
									for (Fleet areaFleet : fleets) {
										if (attackedStructure.getBounds().contains(
												areaFleet.getCurrentX(), areaFleet.getCurrentY()) &&
												areaFleet.getCurrentAction().equals(
													Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE)) {
											synchronized (areaFleet.getLock()) {
												areaFleet = DataAccess.getEditable(areaFleet);
												areaFleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
												areaFleet.save();
											}
											UpdateTools.queuePlayerFleetUpdate(areaFleet);
										}
									}
								}
								
								Effect effect = new Effect(attackedStructure.getSize().getWidth() > 1 ||
									attackedStructure.getSize().getHeight() > 1 ?
										Effect.TYPE_LARGE_STRUCTURE_DESTRUCTION :
										Effect.TYPE_SMALL_STRUCTURE_DESTRUCTION,
									attackedStructure.getX(), attackedStructure.getY(),
									attackedStructure.getIdArea());
								
								Event event = new Event(
									Event.EVENT_STRUCTURE_LOST,
									Event.TARGET_PLAYER,
									attackedStructure.getIdOwner(),
									attackedStructure.getIdArea(),
									attackedStructure.getX(),
									attackedStructure.getY(),
									attackedStructure.getName(),
									fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : fleet.getName(),
									fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "??? (flotte pirate)" : owner.getLogin());
								event.save();
								
								event = new Event(
									Event.EVENT_STRUCTURE_DESTROYED,
									Event.TARGET_PLAYER,
									fleet.getIdOwner(),
									attackedStructure.getIdArea(),
									attackedStructure.getX(),
									attackedStructure.getY(),
									attackedStructure.getName(),
									attackedStructure.getOwner().getLogin(),
									fleet.getName());
								event.save();
								
								UpdateTools.queueNewEventUpdate(attackedStructure.getIdOwner());
								UpdateTools.queueNewEventUpdate(fleet.getIdOwner());
								
								if (attackedStructure.getType() == Structure.TYPE_GENERATOR)
									UpdateTools.queuePlayerGeneratorsUpdate(attackedStructure.getIdOwner());
								UpdateTools.queueEffectUpdate(effect, 0, false);
							} else {
								int hullBefore = attackedStructure.getHull();
								double maxHull = attackedStructure.getMaxHull();
								
								synchronized (attackedStructure.getLock()) {
									attackedStructure = DataAccess.getEditable(attackedStructure);
									attackedStructure.setHull(attackedStructure.getHull() - fleet.getPowerLevel());
									attackedStructure.save();
								}
								
								if (hullBefore / maxHull > .5 && attackedStructure.getHull() / maxHull <= .5) {
									Event event = new Event(
										Event.EVENT_STRUCTURE_DAMAGED,
										Event.TARGET_PLAYER,
										attackedStructure.getIdOwner(),
										attackedStructure.getIdArea(),
										attackedStructure.getX(),
										attackedStructure.getY(),
										attackedStructure.getName(),
										String.valueOf(attackedStructure.getHull() / maxHull));
									event.save();
									
									UpdateTools.queueNewEventUpdate(attackedStructure.getIdOwner());
								}
							}
						}
					}
				} else if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE)) {
					// Démontage de structure
					boolean dismount = true;
					
					Structure overStructure = fleet.getStructureUnderFleet();
					if (overStructure == null)
						dismount = false;
					
					// Vérifie que la flotte a la capacité suffisante
					ItemContainer itemContainer = fleet.getItemContainer();
					if (dismount)
						if (fleet.getPayload() < itemContainer.getTotalWeight() +
								overStructure.getWeight())
							dismount = false;
					
					// Vérifie que la flotte a un emplacement libre
					if (dismount) {
						boolean availableSpace = false;
						for (int i = 0; i < itemContainer.getMaxItems(); i++) {
							if (itemContainer.getItem(i).getType() == Item.TYPE_NONE) {
								availableSpace = true;
								break;
							}
						}
						
						if (!availableSpace)
							dismount = false;
					}
					
					if (dismount) {
						synchronized (overStructure.getLock()) {
							overStructure = DataAccess.getEditable(overStructure);
							overStructure.setIdArea(0);
							overStructure.setIdItemContainer(itemContainer.getId());
							overStructure.save();
						}
						
						synchronized (itemContainer.getLock()) {
							itemContainer = DataAccess.getEditable(itemContainer);
							for (int i = 0; i < itemContainer.getMaxItems(); i++)
								if (itemContainer.getItem(i).getType() == Item.TYPE_NONE) {
									itemContainer.setItem(new Item(Item.TYPE_STRUCTURE,
										overStructure.getId(), 1), i);
										break;
								}
							itemContainer.save();
						}
						
						Event event = new Event(
							Event.EVENT_STRUCTURE_DISMOUNTED,
							Event.TARGET_PLAYER,
							overStructure.getIdOwner(),
							fleet.getIdCurrentArea(),
							overStructure.getX(),
							overStructure.getY(),
							overStructure.getName(),
							fleet.getName());
						event.save();
						
						UpdateTools.queueNewEventUpdate(fleet.getIdOwner());
						
						if (overStructure.getType() == Structure.TYPE_GENERATOR)
							UpdateTools.queuePlayerGeneratorsUpdate(overStructure.getIdOwner());
					}
				} else if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_MOUNT_STRUCTURE)) {
					// Assemblage de structure
					ItemContainer itemContainer = fleet.getItemContainer();
					Item structureItem = itemContainer.getItem(0);
					
					if (structureItem.getType() == Item.TYPE_STRUCTURE) {
						Structure structure = structureItem.getStructure();
						Area area = fleet.getArea();
						int fleetX = fleet.getCurrentX();
						int fleetY = fleet.getCurrentY();
						
						boolean validLocation = true;
						try {
							area.checkValidStructureLocation(structure.getType(), fleetX, fleetY);
						} catch (IllegalOperationException e) {
							validLocation = false;
						}
						
						if (validLocation) {
							// Recherche une source d'énergie
							long idEnergySupplierStructure;
							int requiredEnergy = structure.getEnergyConsumption();
							
							if (requiredEnergy > 0) {
								Structure energySupplierStructure = area.getEnergySupplierStructure(
									owner, requiredEnergy, fleetX, fleetY);
								idEnergySupplierStructure = energySupplierStructure == null ?
									0 : energySupplierStructure.getId();
							} else {
								idEnergySupplierStructure = structure.getId();
							}
							
							synchronized (structure.getLock()) {
								structure = DataAccess.getEditable(structure);
								structure.setX(fleetX);
								structure.setY(fleetY);
								structure.setIdArea(fleet.getIdCurrentArea());
								structure.setIdEnergySupplierStructure(idEnergySupplierStructure);
								structure.setIdItemContainer(0);
								structure.save();
							}
							
							// Relance le rechargement des compétences
							List<StructureSkill> skills = new ArrayList<StructureSkill>(structure.getSkills());
							
							for (StructureSkill skill : skills) {
								synchronized (skill.getLock()) {
									skill = DataAccess.getEditable(skill);
									skill.setLastUse(now);
									skill.setReload(now + skill.getReloadLength());
									skill.save();
								}
							}
							
							switch (structure.getType()) {
							case Structure.TYPE_SPACESHIP_YARD:
								StructureSpaceshipYard spaceshipYard =
									structure.getSpaceshipYard();
								synchronized (spaceshipYard.getLock()) {
									spaceshipYard = DataAccess.getEditable(spaceshipYard);
									spaceshipYard.setLastBoughtFleet(now);
									spaceshipYard.save();
								}
								break;
							}
							
							synchronized (itemContainer.getLock()) {
								itemContainer = DataAccess.getEditable(itemContainer);
								itemContainer.setItem(new Item(Item.TYPE_NONE, 0, 0), 0);
								itemContainer.save();
							}
							
							Event event = new Event(
								Event.EVENT_STRUCTURE_MOUNTED,
								Event.TARGET_PLAYER,
								structure.getIdOwner(),
								structure.getIdArea(),
								structure.getX(),
								structure.getY(),
								structure.getName(),
								fleet.getName());
							event.save();
							
							UpdateTools.queueNewEventUpdate(fleet.getIdOwner());
							
							if (structure.getType() == Structure.TYPE_GENERATOR)
								UpdateTools.queuePlayerGeneratorsUpdate(structure.getIdOwner());
						}
						// TODO event pas possible de placer la structure
					}
				} else if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_REPAIR_STRUCTURE)) {
					// Réparation de structure
					Structure repairedStructure = fleet.getStructureUnderFleet();
					
					if (repairedStructure != null) {
						String treaty = repairedStructure.getOwner().getTreatyWithPlayer(owner);
						
						if (!(treaty.equals(Treaty.ENEMY) || treaty.equals(Treaty.NEUTRAL))) {
							int engineerLevel = fleet.getSkillLevel(Skill.SKILL_ENGINEER);
							
							if (repairedStructure.getHull() <= repairedStructure.getMaxHull() &&
									engineerLevel >= 0) {
								synchronized (repairedStructure.getLock()) {
									repairedStructure = DataAccess.getEditable(repairedStructure);
									repairedStructure.setHull(Math.min(repairedStructure.getMaxHull(),
										repairedStructure.getHull() + Math.max(1,
										(int) (fleet.getPowerLevel() * .25 * (engineerLevel + 1)))));
									repairedStructure.save();
								}
								
								// Si la structure est complètement réparée et
								// qu'une flotte était en train de réparer la
								// structure, l'action est annulée
								if (repairedStructure.getHull() == repairedStructure.getMaxHull()) {
									List<Fleet> fleets = repairedStructure.getArea().getFleets();
									
									synchronized (fleets) {
										for (Fleet areaFleet : fleets) {
											if (repairedStructure.getBounds().contains(
													areaFleet.getCurrentX(), areaFleet.getCurrentY()) &&
													areaFleet.getCurrentAction().equals(
														Fleet.CURRENT_ACTION_REPAIR_STRUCTURE)) {
												synchronized (areaFleet.getLock()) {
													areaFleet = DataAccess.getEditable(areaFleet);
													areaFleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
													areaFleet.save();
												}
												UpdateTools.queuePlayerFleetUpdate(areaFleet);
											}
										}
									}
								}
								
								if (repairedStructure.getType() == Structure.TYPE_GENERATOR)
									UpdateTools.queuePlayerGeneratorsUpdate(repairedStructure.getIdOwner());
							}
						}
					}
				}
				
				boolean checkCharges = false;
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					if (fleet.getHyperspaceIdArea() != 0) {
						int fromArea = fleet.getIdArea();
						
						fleet.setIdArea(fleet.getHyperspaceIdArea());
						fleet.setX(fleet.getHyperspaceX());
						fleet.setY(fleet.getHyperspaceY());
						fleet.setHyperspaceIdArea(0);
						fleet.setHyperspaceX(0);
						fleet.setHyperspaceY(0);
						
						GameEventsDispatcher.fireGameNotification(
							new JumpFinishedEvent(fleet, fromArea));
					}
					fleet.setMovementReload(0);
					fleet.setCurrentAction(Fleet.CURRENT_ACTION_NONE);
					if (fleet.isScheduledMove()) {
						checkCharges = true;
						fleet.updateScheduledMove();
					}
					fleet.save();
				}
				
				if (checkCharges)
					FleetTools.checkTriggeredCharges(fleet, null);
				
				UpdateTools.queuePlayerFleetUpdate(fleet.getIdOwner(), fleet.getId());
				UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea());
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
