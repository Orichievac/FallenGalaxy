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
import java.util.Iterator;
import java.util.List;


import fr.fg.client.data.FleetData;
import fr.fg.client.data.ItemInfoData;
import fr.fg.client.data.PlayerAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerSectorData;
import fr.fg.client.data.ShipInfoData;
import fr.fg.client.data.SkillData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.FleetLink;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.data.Treaty;
import fr.fg.server.data.Ward;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class FleetTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		ALLOW_DELUDE = 1 << 0,
		ALLOW_HYPERSPACE = 1 << 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static Fleet getFleetByIdWithChecks(int idFleet, int idOwner)
			throws IllegalOperationException {
		return getFleetByIdWithChecks(idFleet, idOwner, 0);
	}
	
	public static Fleet getFleetByIdWithChecks(int idFleet, int idOwner,
			int checks) throws IllegalOperationException {
		Fleet fleet = DataAccess.getFleetById(idFleet);
		
		// Vérifie que la flotte existe
		if (fleet == null)
			throw new IllegalOperationException("La flotte n'existe pas.");
		
		// Vérifie que la flotte appartient au joueur
		if (idOwner != 0 && fleet.getIdOwner() != idOwner)
			throw new IllegalOperationException("La flotte ne vous appartient pas.");
		
		// Vérifie que la flotte n'est pas un leurre
		if ((checks & ALLOW_DELUDE) == 0 && fleet.isDelude())
			throw new IllegalOperationException("La flotte est un leurre.");
		
		// Vérifie que la flotte n'est pas en hyperespace
		if ((checks & ALLOW_HYPERSPACE) == 0 && (
				fleet.isInHyperspace()))
			throw new IllegalOperationException("La flotte est en hyperespace.");
		
		return fleet;
	}
	
	public static void checkLinks(Player player) {
		List<Fleet> fleets = player.getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				List<FleetLink> links = fleet.getLinks();
				
				// Récupère les liens de la flotte
				for (FleetLink link : links)
					if (link.isOffensive() || link.isDefensive()) {
						Fleet otherFleet = link.getOtherFleet(fleet.getId());
						
						if (otherFleet.getIdOwner() != player.getId()) {
							
							// Casse le lien si les joueurs ne sont plus alliés
							if (!(player.isAlliedWithPlayer(otherFleet.getOwner()))) {
								link.delete();
							}
						}
					}
			}
		}
	}
	
	public static boolean checkTriggeredCharges(Fleet fleet, List<Update> updates) {
		Area area = fleet.getArea();
		List<Ward> wards = area.getWards();
		
		boolean update = false;
		boolean fleetDestroyed = false;
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		// Teste si la flotte comporte uniquement des cargos
		boolean onlyFreighters = true;
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Ship ship = fleet.getSlot(i).getShip();
			if (ship != null && ship.getShipClass() != Ship.FREIGHTER)
				onlyFreighters = false;
		}
		
		// Recherche les mines qui peuvent potentiellement exploser
		if (!onlyFreighters) {
			List<Ward> triggeredMines = new ArrayList<Ward>();
			
			synchronized (wards) {
				for (Ward ward : wards) {
					if ((ward.getType().equals(Ward.TYPE_MINE) ||
							ward.getType().equals(Ward.TYPE_MINE_INVISIBLE))) {
						int dx = ward.getX() - fleetX;
						int dy = ward.getY() - fleetY;
						
						if (dx * dx + dy * dy <= Ward.MINE_TRIGGER_RADIUS * Ward.MINE_TRIGGER_RADIUS) {
							String treaty = ward.getOwner().getTreatyWithPlayer(fleet.getOwner());
							
							if ((treaty.equals(Treaty.ENEMY) ||
									(treaty.equals(Treaty.NEUTRAL) &&
									fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)) &&
									ward.getPower() <= fleet.getPowerLevel()) {
								triggeredMines.add(ward);
							}
						}
					}
				}
			}
			
			// Fait exploser des mines
			if (triggeredMines.size() > 0) {
				int fleetPower = fleet.getPower();
				int minesPower = 0;
				List<Ward> explodedMines = new ArrayList<Ward>();
				
				while (fleetPower > minesPower && triggeredMines.size() > 0) {
					int mineIndex = 0;
					int maxPower = triggeredMines.get(0).getMinePower();
					
					for (int i = 1; i < triggeredMines.size(); i++) {
						Ward mine = triggeredMines.get(i);
						int minePower = mine.getMinePower();
						
						if (minePower > maxPower) {
							// Mine plus puissante sélectionnée si cela ne conduit
							// pas à gacher de la puissance
							if (minesPower + maxPower < fleetPower) {
								mineIndex = i;
								maxPower = minePower;
							}
						} else {
							// Mine moins puissante sélectionnée si cela détruit
							// quand même la flotte, pour ne pas gâcher de puissance
							if (minesPower + minePower >= fleetPower) {
								mineIndex = i;
								maxPower = minePower;
							}
						}
					}
					
					minesPower += maxPower;
					explodedMines.add(triggeredMines.get(mineIndex));
					triggeredMines.remove(mineIndex);
				}
				
				if (minesPower >= fleetPower) {
					boolean freighters = false;
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						Ship ship = fleet.getSlot(i).getShip();
						if (ship != null && ship.getShipClass() == Ship.FREIGHTER)
							freighters = true;
					}
					
					if (freighters) {
						// Destruction des slots
						synchronized (fleet.getLock()) {
							fleet = DataAccess.getEditable(fleet);
							for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
								Ship ship = fleet.getSlot(i).getShip();
								if (ship != null && ship.getShipClass() != Ship.FREIGHTER)
									fleet.setSlot(new Slot(), i);
							}
							fleet.save();
						}
					} else {
						// Destruction de la flotte
						fleet.delete();
						fleetDestroyed = true;
					}
				} else {
					double coef = 1 - (minesPower / (double) fleetPower);
					
					// Destruction des slots
					synchronized (fleet.getLock()) {
						fleet = DataAccess.getEditable(fleet);
						for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
							Slot slot = fleet.getSlot(i);
							if (slot.getShip() != null && slot.getShip(
									).getShipClass() != Ship.FREIGHTER)
								fleet.setSlot(new Slot(slot.getId(),
									(long) Math.ceil(slot.getCount() * coef),
									slot.isFront()), i);
						}
						fleet.save();
					}
				}
				
				// Ajoute de l'XP aux propriétaires des mines
				int remainingPower = fleetPower;
				ArrayList<Integer> owners = new ArrayList<Integer>();
				
				for (Ward mine : explodedMines) {
					Player owner = mine.getOwner();
					
					if (!owners.contains(owner.getId()))
						owners.add(owner.getId());
					
					int minePower = mine.getMinePower();
					
					if (owner != null) {
						synchronized (mine.getOwner()) {
							owner = DataAccess.getEditable(owner);
							owner.addXp((int) Math.floor(Math.min(
								remainingPower, minePower) *
								GameConstants.XP_MINE_SHIP_DESTROYED));
							owner.save();
						}
						
						UpdateTools.queueXpUpdate(owner.getId(), false);
					}
					
					remainingPower = Math.max(0, remainingPower - minePower);
				}
				
				// Evènements
				for (Integer idPlayer : owners) {
					int count = 0;
					
					for (Ward mine : explodedMines)
						if (mine.getIdOwner() == idPlayer)
							count++;
					
					Event event = new Event(fleetDestroyed ?
							Event.EVENT_CHARGE_FLEET_DESTROYED :
							Event.EVENT_CHARGE_TRIGGERED,
						Event.TARGET_PLAYER,
						idPlayer,
						fleet.getIdCurrentArea(),
						fleetX,
						fleetY,
						fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? fleet.getName() : "???",
						fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? fleet.getOwner().getLogin() : "??? (flotte pirate)",
						Ward.TYPE_MINE,
						String.valueOf(count));
					event.save();
					
					UpdateTools.queueNewEventUpdate(idPlayer, false);
				}
				
				Event event = new Event(fleetDestroyed ?
						Event.EVENT_CHARGE_FLEET_LOST :
						Event.EVENT_CHARGE_BLOWED,
					Event.TARGET_PLAYER,
					fleet.getIdOwner(),
					fleet.getIdCurrentArea(),
					fleetX,
					fleetY,
					fleet.getName(),
					Ward.TYPE_MINE,
					String.valueOf(explodedMines.size()));
				event.save();
				
				UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
				
				// Effets spéciaux
				Effect effect = new Effect(Effect.TYPE_FLEET_DESTRUCTION,
						fleetX, fleetY, area.getId());
				
				if (updates != null) {
					updates.add(Update.getEffectUpdate(effect));
					UpdateTools.queueEffectUpdate(effect, fleet.getIdOwner(), false);
				} else {
					UpdateTools.queueEffectUpdate(effect, 0, false);
				}
				
				for (Ward mine : explodedMines) {
					effect = new Effect(Effect.TYPE_WARD_DESTRUCTION,
							mine.getX(), mine.getY(), area.getId());
					
					if (updates != null) {
						updates.add(Update.getEffectUpdate(effect));
						UpdateTools.queueEffectUpdate(effect, fleet.getIdOwner(), false);
					} else {
						UpdateTools.queueEffectUpdate(effect, 0, false);
					}
					
					mine.delete();
				}
				
				update = true;
			}
		}
		
		if (!fleetDestroyed) {
			// Recherche les charges IEM qui peuvent potentiellement exploser
			List<Ward> triggeredStuns = new ArrayList<Ward>();
			
			synchronized (wards) {
				for (Ward ward : wards) {
					if ((ward.getType().equals(Ward.TYPE_STUN) ||
							ward.getType().equals(Ward.TYPE_STUN_INVISIBLE))) {
						int dx = ward.getX() - fleetX;
						int dy = ward.getY() - fleetY;
						
						if (dx * dx + dy * dy <= Ward.STUN_TRIGGER_RADIUS * Ward.STUN_TRIGGER_RADIUS) {
							String treaty = ward.getOwner().getTreatyWithPlayer(fleet.getOwner());
							
							if ((treaty.equals(Treaty.ENEMY) ||
									(treaty.equals(Treaty.NEUTRAL) &&
									fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)) &&
									ward.getPower() <= fleet.getPowerLevel()) {
								triggeredStuns.add(ward);
							}
						}
					}
				}
			}
			
			// Recherche la charge IEM de puissance minimale
			if (triggeredStuns.size() > 0) {
				int stunIndex = 0;
				int minPower = triggeredStuns.get(0).getPower();
				
				for (int i = 0; i < triggeredStuns.size(); i++)
					if (triggeredStuns.get(i).getPower() < minPower) {
						minPower = triggeredStuns.get(i).getPower();
						stunIndex = i;
					}
				
				// Déclenche la charge
				Ward stun = triggeredStuns.get(stunIndex);
				
				Effect effect = new Effect(Effect.TYPE_WARD_DESTRUCTION,
						stun.getX(), stun.getY(), area.getId());
				
				if (updates != null) {
					updates.add(Update.getEffectUpdate(effect));
					UpdateTools.queueEffectUpdate(effect, fleet.getIdOwner(), false);
				} else {
					UpdateTools.queueEffectUpdate(effect, 0, false);
				}
				
				stun.delete();
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.setMovement(0, true);
					fleet.setMovementReload(Math.max(fleet.getMovementReload(),
						Utilities.now() + Ward.STUN_LENGTH));
					fleet.save();
				}
				
				// Evenements
				Event event = new Event(
					Event.EVENT_CHARGE_TRIGGERED,
					Event.TARGET_PLAYER,
					stun.getIdOwner(),
					fleet.getIdCurrentArea(),
					fleetX,
					fleetY,
					fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? fleet.getName() : "???",
					fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? fleet.getOwner().getLogin() : "??? (flotte pirate)",
					stun.getType(),
					"1");
				event.save();
				
				UpdateTools.queueNewEventUpdate(stun.getIdOwner(), false);
				
				event = new Event(
					Event.EVENT_CHARGE_BLOWED,
					Event.TARGET_PLAYER,
					fleet.getIdOwner(),
					fleet.getIdCurrentArea(),
					fleetX,
					fleetY,
					fleet.getName(),
					stun.getType(),
					"1");
				event.save();
				
				UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
				
				
				// Effets spéciaux
				effect = new Effect(Effect.TYPE_EMP,
						fleetX, fleetY, area.getId());
				
				if (updates != null) {
					updates.add(Update.getEffectUpdate(effect));
					UpdateTools.queueEffectUpdate(effect, fleet.getIdOwner(), false);
				} else {
					UpdateTools.queueEffectUpdate(effect, 0, false);
				}
				
				update = true;
			}
		}
		
		return update;
	}
	
	public static JSONStringer getAreaFleets(JSONStringer json, Area area,
			Player player) throws Exception {
		return getAreaFleets(json, area, player, null, null, null, null, null, null, null);
	}
	
	public static JSONStringer getAreaFleets(JSONStringer json, Area area,
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
		
		long now = Utilities.now();
		Ally ally = player.getAlly();
		
		List<StarSystem> systems = area.getColonizedSystems();
		List<Fleet> spyFleets = getSpyFleets(player, area);
		
		List<Fleet> fleets = area.getFleets();
		
		json.array();
		for (Fleet fleet : fleets) {
			getFleetData(json, area, fleet, player, ally,
				systems, spyFleets, allySystems, allyFleets,
				allyObserverWards, allySpaceStations, allyStructures,
				allySentryWards, now, treatiesCache);
		}
		json.endArray();
		
		return json;
	}
	
	public static JSONStringer getPlayerFleets(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		json.array();
		
		List<Fleet> fleets = new ArrayList<Fleet>(player.getFleets());
		
		// Premier tri par ordre de capacité de déplacement des flottes
		try {
			Collections.sort(fleets, new Comparator<Fleet>() {
				public int compare(Fleet f1, Fleet f2) {
					if (f1.isInHyperspace()) {
						if (f2.isInHyperspace()) {
							long remainingTime1 = f1.getEndJumpRemainingTime();
							long remainingTime2 = f2.getEndJumpRemainingTime();
							if (remainingTime1 == remainingTime2)
								return f1.getName().compareToIgnoreCase(f2.getName());
							return remainingTime1 < remainingTime2 ? -1 : 1;
						} else {
							return 1;
						}
					} else {
						if (f2.isInHyperspace()) {
							return -1;
						} else {
							// Aucune des 2 flottes n'est en hyperespace
							if (f1.getMovement() == 0) {
								if (f2.getMovement() == 0) {
									long movementReload1 = f1.getMovementReload();
									long movementReload2 = f2.getMovementReload();
									if (movementReload1 == movementReload2)
										return f1.getName().compareToIgnoreCase(f2.getName());
									return movementReload1 < movementReload2 ? -1 : 1;
								} else {
									return f1.getName().compareToIgnoreCase(f2.getName());
								}
							} else {
								if (f2.getMovement() == 0) {
									return -1;
								} else {
									double m1 = f1.getMovement() / (double) f1.getMovementMax();
									double m2 = f2.getMovement() / (double) f2.getMovementMax();
									
									if (m1 == m2) {
										if (f1.getTag() == f2.getTag())
											return f1.getName().compareToIgnoreCase(f2.getName());
										else
											return f1.getTag() < f2.getTag() ? -1 : 1;
									} else {
										return m1 > m2 ? -1 : 1;
									}
								}
							}
						}
					}
				}
			});
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error("Fleets sort error", e);
		}
		
		// Flottes en espace
		for (Fleet fleet : fleets) {
			getPlayerFleet(json, fleet);
		}
		
		json.endArray();
		
		return json;
	}
	
	public static JSONStringer getPlayerFleet(JSONStringer json, int idFleet) {
		return getPlayerFleet(json, DataAccess.getFleetById(idFleet));
	}
	
	public static JSONStringer getPlayerFleet(JSONStringer json, Fleet fleet) {
		if (json == null)
			json = new JSONStringer();
		
		long now = Utilities.now();
		Area area = fleet.getArea();
		Sector sector = area.getSector();
		
		StarSystem system = null;
		List<StarSystem> areaSystems = DataAccess.getSystemsByArea(area.getId());
		synchronized (areaSystems) {
			for (StarSystem areaSystem : areaSystems)
				if (areaSystem.contains(fleet.getCurrentX(), fleet.getCurrentY())) {
					system = areaSystem;
					break;
				}
		}
		
		String jumpTarget = "";
		if (fleet.isStartingJump()) {
			Area hyperspaceArea = fleet.getHyperspaceArea();
			if (hyperspaceArea.getIdSector() != area.getIdSector())
			{
				if(!fleet.getOwner().isPremium())
					jumpTarget = hyperspaceArea.getSector().getName();
				else 
					jumpTarget = hyperspaceArea.getName()+ " ("+
					hyperspaceArea.getSector().getName() +")";
			}
			else
				jumpTarget = hyperspaceArea.getName();
		}
		
		json.object().
			key(PlayerFleetData.FIELD_ID).						value(fleet.getId()).
			key(PlayerFleetData.FIELD_VERSION).					value(fleet.getVersion() * 17 + fleet.getItemContainer().getVersion()).
			key(PlayerFleetData.FIELD_NAME).					value(fleet.getName()).
			key(PlayerFleetData.FIELD_TAG).						value(fleet.getTag()).
			key(PlayerFleetData.FIELD_SHORTCUT).				value(fleet.getShortcut()).
			key(PlayerFleetData.FIELD_X).						value(fleet.getCurrentX()).
			key(PlayerFleetData.FIELD_Y).						value(fleet.getCurrentY()).
			key(PlayerFleetData.FIELD_SCHEDULED_MOVE).			value(fleet.isScheduledMove()).
			key(PlayerFleetData.FIELD_MOVEMENT).				value(fleet.getMovement()).
			key(PlayerFleetData.FIELD_MOVEMENT_MAX).			value(fleet.getMovementMax()).
			key(PlayerFleetData.FIELD_MOVEMENT_RELOAD).			value(fleet.getMovementReloadRemainingTime()).
			key(PlayerFleetData.FIELD_START_JUMP).				value(fleet.getStartJumpRemainingTime()).
			key(PlayerFleetData.FIELD_END_JUMP).				value(fleet.getEndJumpRemainingTime()).
			key(PlayerFleetData.FIELD_CAN_JUMP).				value(fleet.getJumpReloadRemainingTime()).
			key(PlayerFleetData.FIELD_JUMP_TARGET).				value(jumpTarget).
			key(PlayerFleetData.FIELD_XP).						value(fleet.getXp()).
			key(PlayerFleetData.FIELD_STEALTH).					value(fleet.isStealth()).
			key(PlayerFleetData.FIELD_DELUDE).					value(fleet.isDelude()).
			key(PlayerFleetData.FIELD_SKILL_POINTS).			value(fleet.getAvailableSkillPoints()).
			key(PlayerFleetData.FIELD_LAST_MOVE).				value(now - fleet.getLastMove()).
			key(PlayerFleetData.FIELD_COLONIZING).				value(fleet.getColonizationRemainingTime()).
			key(PlayerFleetData.FIELD_CAPTURE).					value(fleet.isCapturing()).
			key(PlayerFleetData.FIELD_SYSTEM).					value(system != null ? system.getId() : 0).
			key(PlayerFleetData.FIELD_SYSTEM_TREATY).			value(system != null ? (system.getIdOwner() != 0 ? system.getOwner().getTreatyWithPlayer(fleet.getOwner()) : Treaty.UNINHABITED) : Treaty.UNKNOWN).
			key(PlayerFleetData.FIELD_POWER_LEVEL).				value(fleet.getPowerLevel()).
			key(PlayerFleetData.FIELD_OFFENSIVE_LINK).			value(fleet.getOffensiveLinkedFleetId()).
			key(PlayerFleetData.FIELD_DEFENSIVE_LINK).			value(fleet.getDefensiveLinkedFleetId()).
			key(PlayerFleetData.FIELD_MIGRATION).				value(fleet.isMigrating()).
			key(PlayerFleetData.FIELD_SKILLS).					array().
				object().
					key(SkillData.FIELD_TYPE).					value(fleet.getBasicSkill(0).getType()).
					key(SkillData.FIELD_LEVEL).					value(fleet.getBasicSkill(0).getLevel()).
					key(SkillData.FIELD_RELOAD).				value(Math.max(0, fleet.getBasicSkillReload(0) - now)).
					key(SkillData.FIELD_LAST_USE).				value(now - fleet.getBasicSkillLastUse(0)).
				endObject().
				object().
					key(SkillData.FIELD_TYPE).					value(fleet.getBasicSkill(1).getType()).
					key(SkillData.FIELD_LEVEL).					value(fleet.getBasicSkill(1).getLevel()).
					key(SkillData.FIELD_RELOAD).				value(Math.max(0, fleet.getBasicSkillReload(1) - now)).
					key(SkillData.FIELD_LAST_USE).				value(now - fleet.getBasicSkillLastUse(1)).
				endObject().
				object().
					key(SkillData.FIELD_TYPE).					value(fleet.getBasicSkill(2).getType()).
					key(SkillData.FIELD_LEVEL).					value(fleet.getBasicSkill(2).getLevel()).
					key(SkillData.FIELD_RELOAD).				value(Math.max(0, fleet.getBasicSkillReload(2) - now)).
					key(SkillData.FIELD_LAST_USE).				value(now - fleet.getBasicSkillLastUse(2)).
				endObject().
				object().
					key(SkillData.FIELD_TYPE).					value(fleet.getUltimateSkill().getType()).
					key(SkillData.FIELD_LEVEL).					value(fleet.getUltimateSkill().getLevel()).
					key(SkillData.FIELD_RELOAD).				value(Math.max(0, fleet.getSkillUltimateReload() - now)).
					key(SkillData.FIELD_LAST_USE).				value(now - fleet.getSkillUltimateLastUse()).
				endObject().
				endArray().
			key(PlayerFleetData.FIELD_SLOTS).					array().
				object().
					key(SlotInfoData.FIELD_ID).					value(fleet.getSlot(0).getId()).
					key(SlotInfoData.FIELD_COUNT).				value(fleet.getSlot(0).getCount()).
					key(SlotInfoData.FIELD_FRONT).				value(fleet.getSlot(0).isFront()).
				endObject().
				object().
					key(SlotInfoData.FIELD_ID).					value(fleet.getSlot(1).getId()).
					key(SlotInfoData.FIELD_COUNT).				value(fleet.getSlot(1).getCount()).
					key(SlotInfoData.FIELD_FRONT).				value(fleet.getSlot(1).isFront()).
				endObject().
				object().
					key(SlotInfoData.FIELD_ID).					value(fleet.getSlot(2).getId()).
					key(SlotInfoData.FIELD_COUNT).				value(fleet.getSlot(2).getCount()).
					key(SlotInfoData.FIELD_FRONT).				value(fleet.getSlot(2).isFront()).
				endObject().
				object().
					key(SlotInfoData.FIELD_ID).					value(fleet.getSlot(3).getId()).
					key(SlotInfoData.FIELD_COUNT).				value(fleet.getSlot(3).getCount()).
					key(SlotInfoData.FIELD_FRONT).				value(fleet.getSlot(3).isFront()).
				endObject().
				object().
					key(SlotInfoData.FIELD_ID).					value(fleet.getSlot(4).getId()).
					key(SlotInfoData.FIELD_COUNT).				value(fleet.getSlot(4).getCount()).
					key(SlotInfoData.FIELD_FRONT).				value(fleet.getSlot(4).isFront()).
				endObject().
				endArray().
			key(PlayerFleetData.FIELD_AREA).
				object().
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
					key(PlayerAreaData.FIELD_SPACE_STATIONS_COUNT).value(area.getSpaceStations().size()).
					key(PlayerAreaData.FIELD_MAX_SPACE_STATIONS).value(area.getSpaceStationsLimit()).
				endObject();
		
		// Items embarquées
		ItemContainer itemContainer = fleet.getItemContainer();
		
		json.key(PlayerFleetData.FIELD_ITEMS).				array();
		for (int i = 0; i < itemContainer.getMaxItems(); i++) {
			Item item = itemContainer.getItem(i);
			
			json.object().
				key(ItemInfoData.FIELD_TYPE).	value(item.getType()).
				key(ItemInfoData.FIELD_ID).		value(item.getId()).
				key(ItemInfoData.FIELD_COUNT).	value(item.getCount());
			
			if (item.getType() == Item.TYPE_STRUCTURE) {
				Structure structure = item.getStructure();
				json.key(ItemInfoData.FIELD_STRUCTURE_TYPE).	value(structure.getType()).
					key(ItemInfoData.FIELD_STRUCTURE_LEVEL).	value(structure.getLevel());
			}
			
			json.endObject();
		}
		json.endArray();
		
		// Tactique
		json.key(PlayerFleetData.FIELD_SKIRMISH_ACTION_SLOTS).	array();
		for (int action : fleet.getSkirmishActionSlots())
			json.value(action);
		json.endArray();
		
		json.key(PlayerFleetData.FIELD_SKIRMISH_ACTION_ABILITIES).	array();
		for (int action : fleet.getSkirmishActionAbilities())
			json.value(action);
		json.endArray();

		json.key(PlayerFleetData.FIELD_BATTLE_ACTION_SLOTS).		array();
		for (int action : fleet.getBattleActionSlots())
			json.value(action);
		json.endArray();

		json.key(PlayerFleetData.FIELD_BATTLE_ACTION_ABILITIES).	array();
		for (int action : fleet.getBattleActionAbilities())
			json.value(action);
		json.endArray();
		
		if (fleet.hasOffensiveLink()) {
			Fleet offensiveLinkedFleet = fleet.getOffensiveLinkedFleet();
			boolean deludeLink = fleet.isDelude() || offensiveLinkedFleet.isDelude();
			
			// Détermine le niveau du lien
			int skillLevel = 0;
			List<FleetLink> links = fleet.getLinks();
			
			for (FleetLink link : links) {
				if (link.isOffensive()) {
					skillLevel = link.getSrcFleet(
						).getSkillLevel(Skill.SKILL_OFFENSIVE_LINK);
					break;
				}
			}
			
			json.key(PlayerFleetData.FIELD_OFFENSIVE_LINKED_COUNT).	array();
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
				long count = 0;
				
				if (fleet.getSlot(i).getId() != 0 && !deludeLink) {
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
						Slot linkedSlot = offensiveLinkedFleet.getSlot(j);
						
						if (fleet.getSlot(i).getId() == linkedSlot.getId()) {
							// Ajoute les vaisseaux compatibles de la flotte lié
							// aux vaisseaux de la flotte qui attaque
							count = (long) Math.min(linkedSlot.getCount() - 1,
								Math.floor(fleet.getSlot(i).getCount() *
								Skill.SKILL_OFFENSIVE_LINK_COEF[skillLevel]));
							break;
						}
					}
				}
				
				json.value(count);
			}
			
			json.endArray();
		}
		
		if (fleet.hasDefensiveLink()) {
			Fleet defensiveLinkedFleet = fleet.getDefensiveLinkedFleet();
			boolean deludeLink = fleet.isDelude() || defensiveLinkedFleet.isDelude();
			
			// Détermine le niveau du lien
			int skillLevel = 0;
			List<FleetLink> links = fleet.getLinks();
			
			for (FleetLink link : links) {
				if (link.isDefensive()) {
					skillLevel = link.getSrcFleet(
						).getSkillLevel(Skill.SKILL_DEFENSIVE_LINK);
					break;
				}
			}
			
			json.key(PlayerFleetData.FIELD_DEFENSIVE_LINKED_COUNT).	array();
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
				long count = 0;
				
				if (fleet.getSlot(i).getId() != 0 && !deludeLink) {
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
						Slot linkedSlot = defensiveLinkedFleet.getSlot(j);
						
						if (fleet.getSlot(i).getId() == linkedSlot.getId()) {
							// Ajoute les vaisseaux compatibles de la flotte lié
							// aux vaisseaux de la flotte qui attaque
							count = (long) Math.min(linkedSlot.getCount() - 1,
								Math.floor(fleet.getSlot(i).getCount() *
								Skill.SKILL_DEFENSIVE_LINK_COEF[skillLevel]));
							break;
						}
					}
				}
				
				json.value(count);
			}
			
			json.endArray();
		}
		
		json.endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static List<Fleet> getSpyFleets(Player player, Area area) {
		List<Fleet> fleets = new ArrayList<Fleet>(area.getFleets());
		Iterator<Fleet> i = fleets.iterator();
		
		while (i.hasNext()) {
			Fleet fleet = i.next();
			
			if (fleet.getSkillLevel(Skill.SKILL_SPY) == -1 ||
					(fleet.getIdOwner() != player.getId() &&
					(player.getIdAlly() == 0 ||
					fleet.getOwner().getIdAlly() != player.getIdAlly())) ||
					fleet.isDelude())
				i.remove();
		}
		
		return fleets;
	}
	
	// Note : cette méthode est optimisée pour la méthode getAreaFleets
	private static JSONStringer getFleetData(JSONStringer json, Area area,
			Fleet fleet, Player player, Ally ally, List<StarSystem> systems,
			List<Fleet> spyFleets, List<Point> allySystems,
			List<Point> allyFleets, List<Point> allyObserverWards,
			List<Point> allySpaceStations, List<Structure> allyStructures,
			List<Point> allySentryWards, long now,
			HashMap<Long, String> treatiesCache) throws Exception {
		if (json == null)
			json = new JSONStringer();
		
		if (fleet.getIdOwner() == player.getId()) {
			// Flotte appartenant au joueur
			String jumpTarget = "";
			if (fleet.isStartingJump()) {
				Area hyperspaceArea = fleet.getHyperspaceArea();
				if (hyperspaceArea.getIdSector() != area.getIdSector())
				{
					if(!fleet.getOwner().isPremium())
						jumpTarget = hyperspaceArea.getSector().getName();
					else 
						jumpTarget = hyperspaceArea.getName()+ " ("+
							hyperspaceArea.getSector().getName() +")";
				}
				else
					jumpTarget = hyperspaceArea.getName();
			}
			
			json.object().
				key(FleetData.FIELD_ID).					value(fleet.getId()).
				key(FleetData.FIELD_VERSION).				value(fleet.getVersion()).
				key(FleetData.FIELD_X).						value(fleet.getCurrentX()).
				key(FleetData.FIELD_Y).						value(fleet.getCurrentY()).
				key(FleetData.FIELD_SCHEDULED_MOVE).		value(fleet.isScheduledMove()).
				key(FleetData.FIELD_RESERVED).				value(false).
				key(FleetData.FIELD_NPC_ACTION).			value(Fleet.NPC_ACTION_NONE).
				key(FleetData.FIELD_POWER_LEVEL).			value(fleet.getPowerLevel()).
				key(FleetData.FIELD_AI).					value(false).
				key(FleetData.FIELD_NAME).					value(fleet.getName()).
				key(FleetData.FIELD_OWNER).					value(player.getLogin()).
				key(FleetData.FIELD_IMMOBILIZED).			value(fleet.getMovement() == 0).
				key(FleetData.FIELD_MOVEMENT).				value(fleet.getMovement()).
				key(FleetData.FIELD_MOVEMENT_MAX).			value(fleet.getMovementMax()).
				key(FleetData.FIELD_MOVEMENT_RELOAD).		value(fleet.getMovementReloadRemainingTime()).
				key(FleetData.FIELD_START_JUMP).			value(fleet.getStartJumpRemainingTime()).
				key(FleetData.FIELD_END_JUMP).				value(fleet.getEndJumpRemainingTime()).
				key(FleetData.FIELD_JUMP_TARGET).			value(jumpTarget).
				key(FleetData.FIELD_COLONIZING).			value(fleet.getColonizationRemainingTime()).
				key(FleetData.FIELD_CAPTURE).				value(fleet.isCapturing()).
				key(FleetData.FIELD_CAN_JUMP).				value(fleet.getJumpReloadRemainingTime()).
				key(FleetData.FIELD_TREATY).				value(Treaty.PLAYER).
				key(FleetData.FIELD_PIRATE).				value(fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0).
				key(FleetData.FIELD_STEALTH).				value(fleet.isStealth()).
				key(FleetData.FIELD_DELUDE).				value(fleet.isDelude()).
				key(FleetData.FIELD_ALLY).					value(ally != null ? ally.getName() : "").
				key(FleetData.FIELD_ALLY_TAG).				value(ally != null ? ally.getTag() : "").
				key(FleetData.FIELD_LAST_MOVE).				value(now - fleet.getLastMove()).
				key(FleetData.FIELD_SKIN).					value(fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? 0 : player.getSettingsFleetSkin()).
				key(FleetData.FIELD_XP).					value(fleet.getXp()).
				key(FleetData.FIELD_SHIPS).					value(false).
				key(FleetData.FIELD_CLASSES).				value(false).
				key(FleetData.FIELD_OFFENSIVE_LINK).		value(fleet.getOffensiveLinkedFleetId()).
				key(FleetData.FIELD_DEFENSIVE_LINK).		value(fleet.getDefensiveLinkedFleetId()).
				key(FleetData.FIELD_LINE_OF_SIGHT).			value(GameConstants.LOS_FLEET + 2 * Advancement.getAdvancementLevel(fleet.getIdOwner(), Advancement.TYPE_LINE_OF_SIGHT)).
				key(FleetData.FIELD_SKIRMISH_ABILITIES).	array().endArray().
				key(FleetData.FIELD_BATTLE_ABILITIES).		array().endArray().
				key(FleetData.FIELD_MIGRATION).				value(fleet.isMigrating()).
				key(FleetData.FIELD_SKILLS).				array().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getBasicSkill(0).getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getBasicSkill(0).getLevel()).
					endObject().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getBasicSkill(1).getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getBasicSkill(1).getLevel()).
					endObject().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getBasicSkill(2).getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getBasicSkill(2).getLevel()).
					endObject().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getUltimateSkill().getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getUltimateSkill().getLevel()).
					endObject().
				endArray().
				key(FleetData.FIELD_SLOTS).					array().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(0).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(0).getCount()).
						key(SlotInfoData.FIELD_FRONT).		value(fleet.getSlot(0).isFront()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(1).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(1).getCount()).
						key(SlotInfoData.FIELD_FRONT).		value(fleet.getSlot(1).isFront()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(2).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(2).getCount()).
						key(SlotInfoData.FIELD_FRONT).		value(fleet.getSlot(2).isFront()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(3).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(3).getCount()).
						key(SlotInfoData.FIELD_FRONT).		value(fleet.getSlot(3).isFront()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(4).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(4).getCount()).
						key(SlotInfoData.FIELD_FRONT).		value(fleet.getSlot(4).isFront()).
					endObject().
				endArray();
			
			if (player.isAway()){
				json.key(FleetData.FIELD_AWAY).					value(true);
				}
			if (fleet.hasOffensiveLink()) {
				Fleet offensiveLinkedFleet = fleet.getOffensiveLinkedFleet();
				boolean deludeLink = fleet.isDelude() || offensiveLinkedFleet.isDelude();
				
				// Détermine le niveau du lien
				int skillLevel = 0;
				List<FleetLink> links = fleet.getLinks();
				
				for (FleetLink link : links) {
					if (link.isOffensive()) {
						skillLevel = link.getSrcFleet(
							).getSkillLevel(Skill.SKILL_OFFENSIVE_LINK);
						break;
					}
				}
				
				json.key(FleetData.FIELD_OFFENSIVE_LINKED_COUNT).	array();
				
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
					long count = 0;
					
					if (fleet.getSlot(i).getId() != 0 && !deludeLink) {
						for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
							Slot linkedSlot = offensiveLinkedFleet.getSlot(j);
							
							if (fleet.getSlot(i).getId() == linkedSlot.getId()) {
								// Ajoute les vaisseaux compatibles de la flotte lié
								// aux vaisseaux de la flotte qui attaque
								count = (long) Math.min(linkedSlot.getCount() - 1,
									Math.floor(fleet.getSlot(i).getCount() *
									Skill.SKILL_OFFENSIVE_LINK_COEF[skillLevel]));
								break;
							}
						}
					}
					
					json.value(count);
				}
				
				json.endArray();
			}
			
			if (fleet.hasDefensiveLink()) {
				Fleet defensiveLinkedFleet = fleet.getDefensiveLinkedFleet();
				boolean deludeLink = fleet.isDelude() || defensiveLinkedFleet.isDelude();
				
				// Détermine le niveau du lien
				int skillLevel = 0;
				List<FleetLink> links = fleet.getLinks();
				
				for (FleetLink link : links) {
					if (link.isDefensive()) {
						skillLevel = link.getSrcFleet(
							).getSkillLevel(Skill.SKILL_DEFENSIVE_LINK);
						break;
					}
				}
				
				json.key(FleetData.FIELD_DEFENSIVE_LINKED_COUNT).	array();
				
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
					long count = 0;
					
					if (fleet.getSlot(i).getId() != 0 && !deludeLink) {
						for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
							Slot linkedSlot = defensiveLinkedFleet.getSlot(j);
							
							if (fleet.getSlot(i).getId() == linkedSlot.getId()) {
								// Ajoute les vaisseaux compatibles de la flotte lié
								// aux vaisseaux de la flotte qui attaque
								count = (long) Math.min(linkedSlot.getCount() - 1,
									Math.floor(fleet.getSlot(i).getCount() *
									Skill.SKILL_DEFENSIVE_LINK_COEF[skillLevel]));
								break;
							}
						}
					}
					
					json.value(count);
				}
				
				json.endArray();
			}
			
			json.endObject();
		} else {
			// Flotte appartenant a un autre joueur
			Player owner = fleet.getOwner();
			if (owner == null) {
				LoggingSystem.getServerLogger().warn("Fleet without owner: " + fleet.getId());
				return json;
			}
			
			Ally ownerAlly = owner.getAlly();
			boolean pirate = fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0;
			String treaty = owner.getTreatyWithPlayer(treatiesCache, player);
			boolean allied = owner.isAlliedWithPlayer(player);
			if (pirate) {
				if (allied)
					pirate = false;
				else
					treaty = Treaty.PIRATE;
			}
			
			// Vérifie que la flotte n'est pas à l'intérieur des frontières
			// d'un système neutre
			if (treaty.equals(Treaty.NEUTRAL)) {
				synchronized (systems) {
					for (StarSystem system : systems) {
						if (system.getIdOwner() == fleet.getIdOwner() &&
								system.contains(fleet.getCurrentX(), fleet.getCurrentY()) &&
								player.getTreatyWithPlayer(treatiesCache, system.getOwner(
										)).equals(Treaty.NEUTRAL))
							return json;
					}
				}
			}
			
			// Vérifie que la flotte n'est pas dans le brouillard de guerre
			if (!treaty.equals(Treaty.ALLY) &&
					!player.isLocationVisible(area,
						fleet.getCurrentX(), fleet.getCurrentY(),
						allySystems, allyFleets, allyObserverWards,
						allySpaceStations, allyStructures)) {
				return json;
			}
			
			// Vérifie que la flotte n'est pas dans le brouillard de guerre
			if ((treaty.equals(Treaty.NEUTRAL) ||
					treaty.equals(Treaty.ENEMY) ||
					treaty.equals(Treaty.PIRATE)) &&
					fleet.isStealth() && !player.isLocationRevealed(
						fleet.getCurrentX(), fleet.getCurrentY(), allySentryWards)) {
				return json;
			}
			
			int spyLevel = -1;
			for (Fleet spyFleet : spyFleets) {
				int dx = spyFleet.getCurrentX() - fleet.getCurrentX();
				int dy = spyFleet.getCurrentY() - fleet.getCurrentY();
				
				if (dx * dx + dy * dy <= Skill.SPY_DETECTION_RANGE *
						Skill.SPY_DETECTION_RANGE) {
					spyLevel = Math.max(spyLevel,
							spyFleet.getSkillLevel(Skill.SKILL_SPY));
				}
			}
			
			boolean available = ContractManager.isNpcAvailable(player, fleet);
			String npcAction = ContractManager.getNpcAction(player, fleet);
			
			boolean delude = false;
			if (treaty.equals(Treaty.PLAYER) || allied) {
				delude = fleet.isDelude();
			} else {
				if (fleet.isDelude()) {
					synchronized (allyObserverWards) {
						for (Point ward : allyObserverWards) {
							int dx = ward.x - fleet.getCurrentX();
							int dy = ward.y - fleet.getCurrentY();
							
							if (dx * dx + dy * dy <= Ward.OBSERVER_DETECTION_RADIUS *
									Ward.OBSERVER_DETECTION_RADIUS) {
								delude = true;
								break;
							}
						}
					}
				}
			}
			
			json.object().
				key(FleetData.FIELD_ID).					value(fleet.getId()).
				key(FleetData.FIELD_VERSION).				value(fleet.getVersion()).
				key(FleetData.FIELD_X).						value(fleet.getCurrentX()).
				key(FleetData.FIELD_Y).						value(fleet.getCurrentY()).
				key(FleetData.FIELD_RESERVED).				value(!available).
				key(FleetData.FIELD_NPC_ACTION).			value(npcAction).
				key(FleetData.FIELD_POWER_LEVEL).			value(fleet.getPowerLevel()).
				key(FleetData.FIELD_NAME).					value(pirate ? "" : fleet.getName()).
				key(FleetData.FIELD_LEVEL).					value(owner.getLevel()).
				key(FleetData.FIELD_OWNER).					value(pirate ? "" : owner.getLogin()).
				key(FleetData.FIELD_IMMOBILIZED).			value(fleet.getMovement() == 0).
				key(FleetData.FIELD_ALLY).					value(pirate || ownerAlly == null ? "" : ownerAlly.getName()).
				key(FleetData.FIELD_ALLY_TAG).				value(pirate || ownerAlly == null ? "" : ownerAlly.getTag()).
				key(FleetData.FIELD_AI).					value(owner.isAi()).
				key(FleetData.FIELD_TREATY).				value(treaty).
				key(FleetData.FIELD_STEALTH).				value(fleet.isStealth()).
				key(FleetData.FIELD_DELUDE).				value(delude).
				key(FleetData.FIELD_PIRATE).				value(fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0).
				key(FleetData.FIELD_OFFENSIVE_LINK).		value(fleet.getOffensiveLinkedFleetId()).
				key(FleetData.FIELD_DEFENSIVE_LINK).		value(fleet.getDefensiveLinkedFleetId()).
				key(FleetData.FIELD_START_JUMP).			value(fleet.isStartingJump() ? -1 : 0).
				key(FleetData.FIELD_END_JUMP).				value(fleet.isEndingJump() ? -1 : 0).
				key(FleetData.FIELD_JUMP_TARGET).			value("").
				key(FleetData.FIELD_CAPTURE).				value(treaty.equals(Treaty.NEUTRAL) ? false : fleet.isCapturing()).
				key(FleetData.FIELD_SKIN).					value(fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? 0 : owner.getSettingsFleetSkin());
			   if (owner.isAway()){
				json.key(FleetData.FIELD_AWAY).					value(true);
				}
			   if (owner.isConnected()){
					json.key(FleetData.FIELD_CONNECTED).					value(true);
					}
			
			if (treaty.equals(Treaty.ALLY)) {
				json.key(FleetData.FIELD_XP).					value(fleet.getXp()).
					key(FleetData.FIELD_SKILLS).			array().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getBasicSkill(0).getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getBasicSkill(0).getLevel()).
					endObject().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getBasicSkill(1).getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getBasicSkill(1).getLevel()).
					endObject().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getBasicSkill(2).getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getBasicSkill(2).getLevel()).
					endObject().
					object().
						key(SkillData.FIELD_TYPE).			value(fleet.getUltimateSkill().getType()).
						key(SkillData.FIELD_LEVEL).			value(fleet.getUltimateSkill().getLevel()).
					endObject().
					endArray();
			}
			
			// Estimation du contenu de la flotte par classes
			if ((spyLevel == 0 && treaty.equals(Treaty.ENEMY)) ||
					(spyLevel == 1 && (treaty.equals(Treaty.NEUTRAL) ||
							treaty.equals(Treaty.PIRATE)))) {
				json.key(FleetData.FIELD_CLASSES).	array();
				int[] classes = fleet.getClassesQuantityQualifiers();
				for (int i = 0; i < classes.length; i++)
					json.value(classes[i]);
				json.endArray();
			} else {
				json.key(FleetData.FIELD_CLASSES).	value(false);
			}
			
			// Estimation du contenu de la flotte par vaisseaux
			if ((spyLevel == 1 && treaty.equals(Treaty.ENEMY)) ||
					(spyLevel == 2 && (treaty.equals(Treaty.NEUTRAL) ||
							treaty.equals(Treaty.PIRATE)))) {
				json.key(FleetData.FIELD_SHIPS).
					array();
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
					Slot slot = fleet.getSlot(i);
					
					json.object().
						key(ShipInfoData.FIELD_ID).			value(slot.getId()).
						key(ShipInfoData.FIELD_CLASSES).	value(Fleet.getShipQuantityQualifier((long) slot.getCount())).
						endObject();
					
				}
				json.endArray();
			} else {
				json.key(FleetData.FIELD_SHIPS).		value(false);
			}
			
			// Contenu de la flotte
			json.key(FleetData.FIELD_SLOTS);
			if ((spyLevel >= 2 && treaty.equals(Treaty.ENEMY)) ||
					(spyLevel >= 3 && (treaty.equals(Treaty.NEUTRAL) ||
						treaty.equals(Treaty.PIRATE))) ||
					treaty.equals(Treaty.ALLY) ||
					treaty.equals(Treaty.ALLIED) ||
					treaty.equals(Treaty.DEFENSIVE) ||
					treaty.equals(Treaty.TOTAL)
					) {
				json.array().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(0).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(0).getCount()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(1).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(1).getCount()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(2).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(2).getCount()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(3).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(3).getCount()).
					endObject().
					object().
						key(SlotInfoData.FIELD_ID).			value(fleet.getSlot(4).getId()).
						key(SlotInfoData.FIELD_COUNT).		value(fleet.getSlot(4).getCount()).
					endObject().
					endArray();
			} else {
				json.value(false);
			}
			
			// Actions visibles si la flotte est immobilisée
			if (fleet.getMovement() == 0 && !fleet.isInHyperspace() &&
					((spyLevel >= 2 && treaty.equals(Treaty.ENEMY)) ||
					(spyLevel >= 3 && (treaty.equals(Treaty.NEUTRAL) ||
						treaty.equals(Treaty.PIRATE))))) {
				json.key(FleetData.FIELD_SKIRMISH_ABILITIES).	array();
				
				if (spyLevel >= 3 && treaty.equals(Treaty.ENEMY)) {
					// Actions en escarmouche
					int[] skirmishSlots = fleet.getSkirmishActionSlots();
					int[] skirmishAbilities = fleet.getSkirmishActionAbilities();
					
					json.value(skirmishSlots[0] == -1 ? -1 : (skirmishAbilities[0] == -1 ?
						0 : fleet.getSlot(skirmishSlots[0]).getShip(
							).getAbilities()[skirmishAbilities[0]].getType()));
				}
				
				json.endArray();
				
				// Actions en bataille
				int[] battleSlots = fleet.getBattleActionSlots();
				int[] battleAbilities = fleet.getBattleActionAbilities();
				
				json.key(FleetData.FIELD_BATTLE_ABILITIES).		array();
				
				int displayedAbilities = spyLevel >= 3 && treaty.equals(Treaty.ENEMY) ? 3 : 2;
				
				for (int i = 0; i < displayedAbilities; i++)
					json.value(battleSlots[i] == -1 ? -1 : (battleAbilities[i] == -1 ?
						0 : fleet.getSlot(battleSlots[i]).getShip(
							).getAbilities()[battleAbilities[i]].getType()));
				
				json.endArray();
			} else {
				json.key(FleetData.FIELD_SKIRMISH_ABILITIES).	array().endArray().
					key(FleetData.FIELD_BATTLE_ABILITIES).		array().endArray();
			}
			
			if (treaty.equals(Treaty.ALLY))
				json.key(FleetData.FIELD_LINE_OF_SIGHT).	value(GameConstants.LOS_FLEET +
					2 * Advancement.getAdvancementLevel(fleet.getIdOwner(), Advancement.TYPE_LINE_OF_SIGHT));
			else
				json.key(FleetData.FIELD_LINE_OF_SIGHT).	value(0);
			
			json.endObject();
		}
		
		return json;
	}	
}
