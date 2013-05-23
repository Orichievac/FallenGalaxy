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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.SlotState;
import fr.fg.server.data.Ability;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.Area;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.FleetLink;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Report;
import fr.fg.server.data.ReportAction;
import fr.fg.server.data.ReportActionAbility;
import fr.fg.server.data.ReportDamage;
import fr.fg.server.data.ReportSlot;
import fr.fg.server.data.ReportSlotState;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Slot;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Treaty;
import fr.fg.server.data.Ward;
import fr.fg.server.data.Weapon;
import fr.fg.server.data.WeaponGroup;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterBattleEvent;
import fr.fg.server.events.impl.BeforeBattleEvent;
import fr.fg.server.events.impl.CriticalHitEvent;
import fr.fg.server.events.impl.DodgeEvent;
import fr.fg.server.events.impl.RetreatEvent;
import fr.fg.server.events.impl.StolenResourcesEvent;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.ArrayUtils;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class BattleTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		MODE_SKIRMISH = 0,
		MODE_BATTLE = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static Report battle(int mode, Fleet attackingFleet,
			Fleet defendingFleet, boolean bombing) throws Exception {
		Player attacker = attackingFleet.getOwner();
		Player defender = defendingFleet.getOwner();
		
		attacker.updateResearch();
		defender.updateResearch();
		
		// Evenement de début de bataille
		GameEventsDispatcher.fireGameEvent(new BeforeBattleEvent(attackingFleet, defendingFleet));
		
		// Met à jour l'activité du traité
		if (attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 &&
				defendingFleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 &&
				attacker.getTreatyWithPlayer(defender.getId()) == Treaty.ENEMY) {
			List<Treaty> treaties = new ArrayList<Treaty>(attacker.getTreaties());
			
			for (Treaty treaty : treaties) {
				if (treaty.getOtherPlayerId(attacker.getId()) == defender.getId()) {
					synchronized (treaty.getLock()) {
						treaty = DataAccess.getEditable(treaty);
						treaty.setLastActivity(Utilities.now());
						treaty.save();
					}
				}
			}
			
			if (attacker.getIdAlly() != 0 && defender.getIdAlly() != 0) {
				List<AllyTreaty> allyTreaties = new ArrayList<AllyTreaty>(
						attacker.getAlly().getTreaties());
				
				for (AllyTreaty allyTreaty : allyTreaties) {
					if (allyTreaty.getOtherAllyId(attacker.getIdAlly()) ==
							defender.getIdAlly()) {
						synchronized (allyTreaty.getLock()) {
							allyTreaty = DataAccess.getEditable(allyTreaty);
							allyTreaty.setLastActivity(Utilities.now());
							allyTreaty.save();
						}
					}
				}
			}
		}
		
		// Détermine s'il faut masquer l'arrivé du rapport de combat dans les
		// évènements pour l'attaquant
		List<Event> events = attacker.getEvents();
		boolean updateEventsDate = true;
		
		if (defendingFleet.isDelude()) {
			if (!defender.isAi())
				DataAccess.save(new Event(
					Event.EVENT_DELUDE_LOST,
					Event.TARGET_PLAYER,
					defender.getId(),
					attackingFleet.getIdCurrentArea(),
					attackingFleet.getX(),
					attackingFleet.getY(),
					defendingFleet.getName(),
					attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : attackingFleet.getName(),
					attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "??? (flotte pirate)" : attacker.getLogin()));
			
			defendingFleet.delete();
			
			synchronized (attackingFleet.getLock()) {
				attackingFleet = DataAccess.getEditable(attackingFleet);
				attackingFleet.doAction(Fleet.CURRENT_ACTION_BATTLE,
					Utilities.now() + (mode == MODE_SKIRMISH ?
						GameConstants.SKIRMISH_MOVEMENT_RELOAD :
						GameConstants.BATTLE_MOVEMENT_RELOAD));
				attackingFleet.save();
			}
			
			return null;
		}
		
		Report report = BattleTools.getBattleReport(mode, attackingFleet, defendingFleet, bombing);
		report.save();
		
		// Récupère la dernière version des flottes
		Fleet newAttacking = DataAccess.getFleetById(attackingFleet.getId());
		Fleet newDefending = DataAccess.getFleetById(defendingFleet.getId());
		
		attacker = DataAccess.getPlayerById(attacker.getId());
		defender = DataAccess.getPlayerById(defender.getId());
		
		synchronized (events) {
			for (Event event : events) {
				if (event.getDate() > attacker.getEventsReadDate()) {
					updateEventsDate = false;
					break;
				}
			}
		}
		
		if (!attacker.isAi())
			DataAccess.save(new Event(
				Event.EVENT_FLEET_ATTACK,
				Event.TARGET_PLAYER,
				attacker.getId(),
				attackingFleet.getIdCurrentArea(),
				attackingFleet.getX(),
				attackingFleet.getY(),
				attackingFleet.getName(),
				defendingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : defendingFleet.getName(),
				defendingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "??? (flotte pirate)" : defender.getLogin()));
		
		if (!defender.isAi())
			DataAccess.save(new Event(
				Event.EVENT_FLEET_UNDER_ATTACK,
				Event.TARGET_PLAYER,
				defender.getId(),
				attackingFleet.getIdCurrentArea(),
				attackingFleet.getX(),
				attackingFleet.getY(),
				defendingFleet.getName(),
				attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : attackingFleet.getName(),
				attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "??? (flotte pirate)" : attacker.getLogin()));
		
		if (newDefending == null) {
			// Dégâts collatéraux - suppression des balises / mines sur
			// laquelle la flotte se trouvait si elle est détruite
			List<Ward> wards = DataAccess.getWardsByArea(defendingFleet.getIdCurrentArea());
			int fleetX = defendingFleet.getCurrentX();
			int fleetY = defendingFleet.getCurrentY();
			
			synchronized (wards) {
				for (Ward ward : wards) {
					if (ward.getX() == fleetX && ward.getY() == fleetY) {
						ward.delete();
						break;
					}
				}
			}
			
			if (!defender.isAi())
				DataAccess.save(new Event(
					Event.EVENT_FLEET_LOST,
					Event.TARGET_PLAYER,
					defender.getId(),
					0, -1, -1,
					defendingFleet.getName()));
			
			if (!attacker.isAi())
				DataAccess.save(new Event(
					Event.EVENT_FLEET_DESTROYED,
					Event.TARGET_PLAYER,
					attacker.getId(),
					0, -1, -1,
					defendingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : defendingFleet.getName()));
		}
		
		if (newAttacking == null) {
			if (!attacker.isAi())
				DataAccess.save(new Event(
					Event.EVENT_FLEET_LOST,
					Event.TARGET_PLAYER,
					attacker.getId(),
					0, -1, -1,
					attackingFleet.getName()));
			
			if (!defender.isAi())
				DataAccess.save(new Event(
					Event.EVENT_FLEET_DESTROYED,
					Event.TARGET_PLAYER,
					defender.getId(),
					0, -1, -1,
					attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : attackingFleet.getName()));
		}
		
		if (!attacker.isAi())
			DataAccess.save(new Event(
				Event.EVENT_BATTLE_REPORT,
				Event.TARGET_PLAYER,
				attacker.getId(),
				0, -1, -1,
				String.valueOf(report.getId()),
				Config.getServerURL() + "battle/" +
				DataAccess.getReportById(report.getId()).getHash()));
		
		if (!defender.isAi())
			DataAccess.save(new Event(
				Event.EVENT_BATTLE_REPORT,
				Event.TARGET_PLAYER,
				defender.getId(),
				0, -1, -1,
				String.valueOf(report.getId()),
				Config.getServerURL() + "battle/" +
				DataAccess.getReportById(report.getId()).getHash()));
		
		if (updateEventsDate) {
			synchronized (attacker.getLock()) {
				attacker = DataAccess.getEditable(attacker);
				attacker.setEventsReadDate(Utilities.now() + 1);
				attacker.save();
			}
		}
		
		// Evenement de fin de bataille
		GameEventsDispatcher.fireGameEvent(new AfterBattleEvent(
				attackingFleet, defendingFleet, newAttacking, newDefending));
		
		return report;
	}
	
	public static Report getBattleReport(int mode, Fleet attackingFleet,
			Fleet defendingFleet, boolean bombing) {
		Report report;
		
		synchronized (attackingFleet.getLock()) {
			synchronized (defendingFleet.getLock()) {
				Fleet newAttackingFleet = DataAccess.getEditable(attackingFleet);
				Fleet newDefendingFleet = DataAccess.getEditable(defendingFleet);
				Fleet newOffensiveLinkedFleet = null;
				Fleet newDefensiveLinkedFleet = null;
				
				if (attackingFleet.hasOffensiveLink()) {
					Fleet offensiveLinkedFleet = attackingFleet.getOffensiveLinkedFleet();
					
					if (!offensiveLinkedFleet.isDelude()) {
						synchronized (offensiveLinkedFleet.getLock()) {
							newOffensiveLinkedFleet = DataAccess.getEditable(offensiveLinkedFleet);
						}
					}
				}
				
				if (defendingFleet.hasDefensiveLink()) {
					Fleet defensiveLinkedFleet = defendingFleet.getDefensiveLinkedFleet();
					
					if (!defensiveLinkedFleet.isDelude()) {
						synchronized (defensiveLinkedFleet.getLock()) {
							newDefensiveLinkedFleet = DataAccess.getEditable(defensiveLinkedFleet);
						}
					}
				}
				
				StringBuffer log = new StringBuffer();
				
				try {
					report = battle(mode, newAttackingFleet, newDefendingFleet,
						newOffensiveLinkedFleet, newDefensiveLinkedFleet, bombing, log);
				} catch (RuntimeException e) {
					throw e;
				} finally {
					//LoggingSystem.getServerLogger().info(log.toString());
				}
				
				// Sauvegarde l'attaquant
				if (isEmpty(newAttackingFleet.getSlots()))
					newAttackingFleet.delete();
				else
					newAttackingFleet.save();
				
				// Sauvegarde le défenseur
				if (isEmpty(newDefendingFleet.getSlots()))
					newDefendingFleet.delete();
				else
					newDefendingFleet.save();
				
				// Sauvegarde la flotte liée offensivement
				if (newOffensiveLinkedFleet != null) {
					if (isEmpty(newOffensiveLinkedFleet.getSlots()))
						newOffensiveLinkedFleet.delete();
					else
						newOffensiveLinkedFleet.save();
				}
				
				// Sauvegarde la flotte liée défensivement
				if (newDefensiveLinkedFleet != null) {
					if (isEmpty(newDefensiveLinkedFleet.getSlots()))
						newDefensiveLinkedFleet.delete();
					else
						newDefensiveLinkedFleet.save();
				}
			}
		}
		
		return report;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static Report battle(int mode, Fleet attackingFleet,
			Fleet defendingFleet, Fleet offensiveLinkedFleet,
			Fleet defensiveLinkedFleet, boolean bombing, StringBuffer log) {
		// Modificateur de dégâts en fonction de l'environnement, en prenant la
		// case du défenseur comme référentiel
		int fleetX = defendingFleet.getCurrentX();
		int fleetY = defendingFleet.getCurrentY();
		
		double attackingDamageFactor = getEnvironmentDamageModifier(
			attackingFleet, fleetX, fleetY);
		double defendingDamageFactor = getEnvironmentDamageModifier(
			defendingFleet, fleetX, fleetY);
		
		// Dégâts diminués lors d'un bombardement
		if (bombing) {
			attackingDamageFactor *= Skill.SKILL_BOMBING_COEF[attackingFleet.getSkillLevel(Skill.SKILL_BOMBING)];
			defendingDamageFactor *= Skill.SKILL_BOMBING_COEF[attackingFleet.getSkillLevel(Skill.SKILL_BOMBING)];
		}
		
		// Dégâts subis augmentés de 20% quand la flotte est en train de passer
		// en hyperespace
		if (defendingFleet.isStartingJump())
			attackingDamageFactor *= 1.2;
		
		// Copie les slots des deux flottes, pour résoudre le combat dessus
		Slot[] attackingSlots = attackingFleet.getSlots();
		Slot[] defendingSlots = defendingFleet.getSlots();
		
		// Compétence embuscade
		// Les vaisseaux mis de côtés sont sauvegardés dans defendingSlotsSaved
		// Si aucun vaisseau n'est mis de côté, cette variable vaut null
		Slot[] defendingSlotsSaved = null;
		boolean[] defendingSlotsSavedFront = null;
		int ambushSkillLevel = attackingFleet.getSkillLevel(Skill.SKILL_ULTIMATE_AMBUSH);
		
		if (ambushSkillLevel != -1) {
			log.append("---------------------------[AMBUSH]----\n");
			double ratio = Math.max(1, defendingFleet.getPower()) /
				(double) Math.max(1, attackingFleet.getPower());
			
			if (ratio > Skill.SKILL_ULTIMATE_AMBUSH_VALUE_LIMIT[ambushSkillLevel]) {
				ratio = Skill.SKILL_ULTIMATE_AMBUSH_VALUE_LIMIT[ambushSkillLevel] / ratio;
				defendingSlotsSaved = new Slot[GameConstants.FLEET_SLOT_COUNT];
				defendingSlotsSavedFront = new boolean[GameConstants.FLEET_SLOT_COUNT];
				
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
					defendingSlotsSavedFront[i] = defendingSlots[i].isFront();
					
					if (defendingSlots[i].getId() != 0 &&
							defendingSlots[i].getShip().getShipClass() != Ship.FREIGHTER) {
						long count = (long) Math.ceil(ratio * defendingSlots[i].getCount());
						
						defendingSlotsSaved[i] = new Slot(
							defendingSlots[i].getId(),
							defendingSlots[i].getCount() - count,
							defendingSlots[i].isFront());
						
						log.append(i);
						log.append(". ");
						logSlot(log, true, defendingSlotsSaved[i], 0);
						log.append("\n");
						
						defendingSlots[i].setCount(count);
					} else {
						defendingSlotsSaved[i] = new Slot();
					}
				}
			}
		}
		
		// Flotte liée offensivement
		long attackingShipsPower = attackingFleet.getPower();
		long offensiveLinkedShipsPower = 0;
		long[] offensiveLinkedShipsCount = new long[GameConstants.FLEET_SLOT_COUNT];
		
		if (offensiveLinkedFleet != null) {
			log.append("-----------------------[LINKED ATT]----\n");
			logFleetOwner(log, offensiveLinkedFleet);
			
			// Détermine le niveau du lien
			int skillLevel = 0;
			List<FleetLink> links = attackingFleet.getLinks();
			
			for (FleetLink link : links) {
				if (link.isOffensive()) {
					skillLevel = link.getSrcFleet(
						).getSkillLevel(Skill.SKILL_OFFENSIVE_LINK);
					break;
				}
			}
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
				if (attackingSlots[i].getId() != 0) {
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
						Slot linkedSlot = offensiveLinkedFleet.getSlot(j);
						
						if (attackingSlots[i].getId() == linkedSlot.getId()) {
							// Ajoute les vaisseaux compatibles de la flotte lié
							// aux vaisseaux de la flotte qui attaque
							long count = (long) Math.min(linkedSlot.getCount() - 1,
								Math.floor(attackingSlots[i].getCount() *
								Skill.SKILL_OFFENSIVE_LINK_COEF[skillLevel]));
							
							offensiveLinkedShipsPower += count *
								linkedSlot.getShip().getPower();
							attackingSlots[i].addCount(count);
							offensiveLinkedShipsCount[j] = count;
							
							log.append(i);
							log.append(". ");
							logSlot(log, true, new Slot(
								linkedSlot.getId(), count, true), 0);
							log.append("\n");
							break;
						}
					}
				}
		}
		
		// Flotte liée défensivement
		long defendingShipsPower = defendingFleet.getPower();
		long defensiveLinkedShipsPower = 0;
		long[] defensiveLinkedShipsCount = new long[GameConstants.FLEET_SLOT_COUNT];
		
		if (defensiveLinkedFleet != null) {
			log.append("-----------------------[LINKED DEF]----\n");
			logFleetOwner(log, defensiveLinkedFleet);
			
			// Détermine le niveau du lien
			int skillLevel = 0;
			List<FleetLink> links = defendingFleet.getLinks();
			
			for (FleetLink link : links) {
				if (link.isDefensive()) {
					skillLevel = link.getSrcFleet(
							).getSkillLevel(Skill.SKILL_DEFENSIVE_LINK);
					break;
				}
			}
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
				if (defendingSlots[i].getId() != 0) {
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
						Slot linkedSlot = defensiveLinkedFleet.getSlot(j);
						
						if (defendingSlots[i].getId() == linkedSlot.getId()) {
							// Ajoute les vaisseaux compatibles de la flotte lié
							// aux vaisseaux de la flotte qui défend
							long count = (long) Math.min(linkedSlot.getCount() - 1,
								Math.floor(defendingSlots[i].getCount() *
								Skill.SKILL_DEFENSIVE_LINK_COEF[skillLevel]));
							
							defensiveLinkedShipsPower += count *
								linkedSlot.getShip().getPower();
							defendingSlots[i].addCount(count);
							defensiveLinkedShipsCount[j] = count;
							
							log.append(i);
							log.append(". ");
							logSlot(log, true, new Slot(
								linkedSlot.getId(), count, true), 0);
							log.append("\n");
							break;
						}
					}
				}
		}
		
		if(defendingFleet.getItemContainer()==null)
		{
			LoggingSystem.getServerLogger().info("itemContainer null ! ");
			LoggingSystem.getServerLogger().info("DefendingFleetId : "+defendingFleet.getId());
		}
		// Ressources transportées par chaque flotte
		double[] attackingFleetResources = attackingFleet.getItemContainer().getResources();
		double[] defendingFleetResources = defendingFleet.getItemContainer().getResources();
		
		// Puissance des flottes, en prenant en compte les liens
		int attackingFleetPowerLevel = attackingFleet.getPowerLevel();
		int defendingFleetPowerLevel = defendingFleet.getPowerLevel();
		

		int xpAttFactor = (int)(Fleet.getXpFactor(attackingFleetPowerLevel, defendingFleetPowerLevel)*100);
		int xpDefFactor = (int)(Fleet.getXpFactor(defendingFleetPowerLevel, attackingFleetPowerLevel)*100);
		
		// Résoud le combat
		Report report = attack(mode, attackingFleet, attackingSlots,
			attackingFleetResources, defendingFleet, defendingSlots,
			defendingFleetResources, attackingDamageFactor,
			defendingDamageFactor, log, xpAttFactor, xpDefFactor);
		

		// Copie les slots pour calculer l'XP gagnée
		Slot[] attackingSlotsBefore = attackingFleet.getSlots();
		Slot[] defendingSlotsBefore = defendingFleet.getSlots();
		
		// Applique les dégâts sur les flottes
		applyLosses(attackingFleet, attackingSlots, offensiveLinkedFleet, offensiveLinkedShipsCount, null, null);
		applyLosses(defendingFleet, defendingSlots, defensiveLinkedFleet, defensiveLinkedShipsCount, defendingSlotsSaved, defendingSlotsSavedFront);
		
		// Copie les slots pour calculer l'XP gagnée
		Slot[] attackingSlotsAfter = attackingFleet.getSlots();
		Slot[] defendingSlotsAfter = defendingFleet.getSlots();
		
		// Vol de ressources
		double[][] stolenResources = new double[2][GameConstants.RESOURCES_COUNT];
		if (report.getReportActions() != null) {
			for (ReportAction reportAction : report.getReportActions()) {
				if (reportAction.getReportDamages() != null) {
					for (ReportDamage reportDamage : reportAction.getReportDamages()) {
						for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
							stolenResources[reportDamage.getTargetPosition() / 5 == 0 ? 1 : 0][i] += reportDamage.getStealedResource(i);
					}
				}
			}
		}
		
		for (int j = 0; j < 2; j++) {
			log.append("------------------------[");
			log.append(j == 0 ? "ATT" : "DEF");
			log.append(" STEAL]----\n");
			
			Fleet stealingFleet = j == 0 ? attackingFleet : defendingFleet;
			Fleet stolenFleet  = j == 0 ? defendingFleet : attackingFleet;
			
			long totalStolenResources = 0;
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				totalStolenResources += (long) stolenResources[j][i];
			
			if (totalStolenResources == 0)
				continue;
			
			double coef = 1;
			
			ItemContainer itemContainer = stealingFleet.getItemContainer();
			long totalWeight = (long) Math.ceil(itemContainer.getTotalWeight());
			
			if (totalWeight + totalStolenResources > stealingFleet.getPayload())
				coef = (stealingFleet.getPayload() - totalWeight) / (double) totalStolenResources;
			
			long[] stolenResourcesCount = new long[GameConstants.RESOURCES_COUNT];
			
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
				int availablePosition = itemContainer.getCompatibleOrFreePosition(Item.TYPE_RESOURCE, i);
				
				if (availablePosition == -1)
					coef = 0;
				
				stolenResourcesCount[i] = (long) (stolenResources[j][i] * coef);
				
				log.append("Resource ");
				log.append(i);
				log.append(": ");
				log.append((long) stolenResources[j][i]);
				log.append("\n");
			}
			
			GameEventsDispatcher.fireGameNotification(new StolenResourcesEvent(
					stealingFleet, stolenFleet, stolenResourcesCount));
		}
		
		// Met à jour les ressources sur les deux flottes
		ItemContainer attackingItemContainer = attackingFleet.getItemContainer();
		
		synchronized (attackingItemContainer.getLock()) {
			attackingItemContainer = DataAccess.getEditable(attackingFleet.getItemContainer());
			
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				try {
					attackingItemContainer.setResource(Math.max(0,
						(long) Math.floor(attackingFleetResources[i])), i);
				} catch (IllegalOperationException e) {
					LoggingSystem.getServerLogger().warn("Failed to update " +
						"resources: " + i + ", " + attackingFleetResources[i] + ".", e);
				}
			
			// Jette les items en trop
			if (attackingFleet.getPayload() < attackingItemContainer.getTotalWeight()) {
				double coef = attackingFleet.getPayload() / attackingItemContainer.getTotalWeight();
				
				for (int i = 0; i < attackingItemContainer.getMaxItems(); i++) {
					Item item = attackingItemContainer.getItem(i);
					item.setCount((long) Math.floor(item.getCount() * coef));
					attackingItemContainer.setItem(item, i);
				}
			}
			
			attackingItemContainer.save();
		}
		
		ItemContainer defendingItemContainer = defendingFleet.getItemContainer();
		
		synchronized (defendingItemContainer.getLock()) {
			defendingItemContainer = DataAccess.getEditable(defendingFleet.getItemContainer());
			
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				try {
					defendingItemContainer.setResource(Math.max(0,
						(long) Math.floor(defendingFleetResources[i])), i);
				} catch (IllegalOperationException e) {
					LoggingSystem.getServerLogger().warn("Failed to update " +
						"resources: " + i + ", " + attackingFleetResources[i] + ".", e);
				}
			
			// Jette les items en trop
			if (defendingFleet.getPayload() < defendingItemContainer.getTotalWeight()) {
				double coef = defendingFleet.getPayload() / defendingItemContainer.getTotalWeight();
				
				for (int i = 0; i < defendingItemContainer.getMaxItems(); i++) {
					Item item = defendingItemContainer.getItem(i);
					item.setCount((long) Math.floor(item.getCount() * coef));
					defendingItemContainer.setItem(item, i);
				}
			}
			
			defendingItemContainer.save();
		}
		
		// Calcule l'XP gagnée par chacun des joueurs
		log.append("-------------------------------[XP]----\n");
		
		double xp = getBattleXp(defendingSlotsBefore, defendingSlotsAfter);
		
		if (offensiveLinkedFleet != null &&
				offensiveLinkedFleet.getIdOwner() != attackingFleet.getIdOwner()) {
			double coef = attackingShipsPower / (double) (
				offensiveLinkedShipsPower + attackingShipsPower);
			long extraLinkedXp = (long) Math.floor((1 - coef) * xp *
				Fleet.getXpFactor(attackingFleetPowerLevel, defendingFleetPowerLevel));
			xp -= extraLinkedXp;
			long extraXp = (long) Math.floor(xp *
				Fleet.getXpFactor(attackingFleetPowerLevel, defendingFleetPowerLevel));
			
			// Ajoute l'XP a l'attaquant
			Player owner = attackingFleet.getOwner();
			
			int level = owner.getLevel();
			double fleetXpCoef = 100 / ((Fleet.getPowerAtLevel(level + 1) - 1) *
					GameConstants.XP_SHIP_DESTROYED);
			attackingFleet.addXp((long) Math.floor(extraXp * fleetXpCoef *
				(attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ?
					Skill.SKILL_PIRATE_BATTLE_XP_COEF : 1)));
			
			synchronized (owner.getLock()) {
				owner = DataAccess.getEditable(owner);
				owner.addXp(extraXp);
				owner.save();
			}
			
			log.append("[ATT] Player +");
			log.append(extraXp);
			log.append("XP, fleet +");
			log.append((long) Math.floor(extraXp * fleetXpCoef));
			log.append("XP\n");
			
			// Ajoute l'XP au joueur lié offensivement
			Player linkedOwner = offensiveLinkedFleet.getOwner();
			
			level = linkedOwner.getLevel();
			fleetXpCoef = 100 / ((Fleet.getPowerAtLevel(level + 1) - 1) *
					GameConstants.XP_SHIP_DESTROYED);
			offensiveLinkedFleet.addXp((long) Math.floor(extraLinkedXp * fleetXpCoef *
				(offensiveLinkedFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ?
					Skill.SKILL_PIRATE_BATTLE_XP_COEF : 1)));
			
			synchronized (linkedOwner.getLock()) {
				linkedOwner = DataAccess.getEditable(linkedOwner);
				linkedOwner.addXp(extraLinkedXp);
				linkedOwner.save();
			}
			
			log.append("[LINKED ATT] Player +");
			log.append(extraLinkedXp);
			log.append("XP, fleet +");
			log.append((long) Math.floor(extraLinkedXp * fleetXpCoef));
			log.append("XP\n");
		} else {
			long extraXp = (long) Math.floor(xp *
				Fleet.getXpFactor(attackingFleetPowerLevel, defendingFleetPowerLevel));
			
			// Ajoute l'XP a l'attaquant
			Player owner = attackingFleet.getOwner();
			
			int level = owner.getLevel();
			double fleetXpCoef = 100 / ((Fleet.getPowerAtLevel(level + 1) - 1) *
					GameConstants.XP_SHIP_DESTROYED);
			attackingFleet.addXp((long) Math.floor(extraXp * fleetXpCoef *
				(attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ?
					Skill.SKILL_PIRATE_BATTLE_XP_COEF : 1)));
			
			synchronized (owner.getLock()) {
				owner = DataAccess.getEditable(owner);
				owner.addXp(extraXp);
				owner.save();
			}

			log.append("[ATT] Player +");
			log.append(extraXp);
			log.append("XP, fleet +");
			log.append((long) Math.floor(extraXp * fleetXpCoef));
			log.append("XP\n");
		}
		
		xp = getBattleXp(attackingSlotsBefore, attackingSlotsAfter);
		
		if (defensiveLinkedFleet != null &&
				defensiveLinkedFleet.getIdOwner() != defendingFleet.getIdOwner()) {
			double coef = defendingShipsPower / (double) (
				defensiveLinkedShipsPower + defendingShipsPower);
			long extraLinkedXp = (long) Math.floor((1 - coef) * xp *
					Fleet.getXpFactor(defendingFleetPowerLevel, attackingFleetPowerLevel));
			xp -= extraLinkedXp;
			long extraXp = (long) Math.floor(xp *
				Fleet.getXpFactor(defendingFleetPowerLevel, attackingFleetPowerLevel));
		
			// Ajoute l'XP au défenseur
			Player owner = defendingFleet.getOwner();
			
			int level = owner.getLevel();
			double fleetXpCoef = 100 / ((Fleet.getPowerAtLevel(level + 1) - 1) *
					GameConstants.XP_SHIP_DESTROYED);
			defendingFleet.addXp((long) Math.floor(extraXp * fleetXpCoef *
				(defendingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ?
					Skill.SKILL_PIRATE_BATTLE_XP_COEF : 1)));
			
			synchronized (owner.getLock()) {
				owner = DataAccess.getEditable(owner);
				owner.addXp(extraXp);
				owner.save();
			}
			
			log.append("[DEF] Player +");
			log.append(extraXp);
			log.append("XP, fleet +");
			log.append((long) Math.floor(extraXp * fleetXpCoef));
			log.append("XP\n");
			
			// Ajoute l'XP au joueur lié défensivement
			Player linkedOwner = defensiveLinkedFleet.getOwner();
			
			level = linkedOwner.getLevel();
			fleetXpCoef = 100 / ((Fleet.getPowerAtLevel(level + 1) - 1) *
					GameConstants.XP_SHIP_DESTROYED);
			defensiveLinkedFleet.addXp((long) Math.floor(extraLinkedXp * fleetXpCoef *
				(defensiveLinkedFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ?
					Skill.SKILL_PIRATE_BATTLE_XP_COEF : 1)));
			
			synchronized (linkedOwner.getLock()) {
				linkedOwner = DataAccess.getEditable(linkedOwner);
				linkedOwner.addXp(extraLinkedXp);
				linkedOwner.save();
			}
			
			log.append("[LINKED DEF] Player +");
			log.append(extraLinkedXp);
			log.append("XP, fleet +");
			log.append((long) Math.floor(extraLinkedXp * fleetXpCoef));
			log.append("XP\n");
		} else {
			long extraXp = (long) Math.floor(xp *
				Fleet.getXpFactor(defendingFleetPowerLevel, attackingFleetPowerLevel));

			
			// Ajoute l'XP au défenseur
			Player owner = defendingFleet.getOwner();
			
			int level = owner.getLevel();
			double fleetXpCoef = 100 / ((Fleet.getPowerAtLevel(level + 1) - 1) *
					GameConstants.XP_SHIP_DESTROYED);
			defendingFleet.addXp((long) Math.floor(extraXp * fleetXpCoef *
				(defendingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ?
					Skill.SKILL_PIRATE_BATTLE_XP_COEF : 1)));
			
			synchronized (owner.getLock()) {
				owner = DataAccess.getEditable(owner);
				owner.addXp(extraXp);
				owner.save();
			}
			
			log.append("[DEF] Player +");
			log.append(extraXp);
			log.append("XP, fleet +");
			log.append((long) Math.floor(extraXp * fleetXpCoef));
			log.append("XP\n");
		}
		
		attackingFleet.doAction(Fleet.CURRENT_ACTION_BATTLE,
			Utilities.now() + (mode == MODE_SKIRMISH ?
				GameConstants.SKIRMISH_MOVEMENT_RELOAD :
				GameConstants.BATTLE_MOVEMENT_RELOAD));
		
		return report;
	}
	
	private static double getEnvironmentDamageModifier(Fleet fleet, int x, int y) {
		double damageFactor = 1;
		Area area = fleet.getArea();
		
		// Dégats réduits de 10% dans les astéroides et 25% dans les asteroides
		// denses / miniers. La flotte qui défend est utilisée comme référence
		List<StellarObject> objects = area.getObjects();
		
		synchronized (objects) {
			for (StellarObject object : objects) {
				String type = object.getType();
				Rectangle bounds = object.getBounds();
				
				if (type.equals(StellarObject.TYPE_ASTEROID)) {
					if (bounds.contains(x, y))
						damageFactor = Math.min(damageFactor, .9);
				} else if (type.startsWith(StellarObject.TYPE_ASTEROID)) {
					if (bounds.contains(x, y))
						damageFactor = Math.min(damageFactor, .75);
				}
			}
		}
		
		if (fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1) {
			List<StarSystem> systems = area.getSystems();
			
			synchronized (systems) {
				for (StarSystem system : systems) {
					if (system.contains(x, y)) {
						if (system.getIdOwner() != 0) {
							String treaty = fleet.getOwner().getTreatyWithPlayer(system.getOwner());
							
							if (treaty.equals(Treaty.PLAYER) ||
									treaty.equals(Treaty.ALLY) ||
									treaty.equals(Treaty.ALLIED)) {
								double defense = Building.getProduction(Building.DEFENSIVE_DECK,
									system.getBuildings(Building.DEFENSIVE_DECK));
								
								damageFactor *= defense;
							}
						}
						break;
					}
				}
			}
		}
		
		// Bonus d'attaque de +20% sur les stations spatiales
		if (fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1) {
			List<SpaceStation> spaceStations = area.getSpaceStations();
			
			synchronized (spaceStations) {
				for (SpaceStation spaceStation : spaceStations) {
					if (spaceStation.getIdAlly() == fleet.getOwner().getIdAlly()) {
						int dx = spaceStation.getX() - x;
						int dy = spaceStation.getY() - y;
						
						if (dx * dx + dy * dy <= GameConstants.SPACE_STATION_RADIUS *
								GameConstants.SPACE_STATION_RADIUS) {
							damageFactor *= 1.2;
						}
					}
				}
			}
		}
		
		return damageFactor;
	}
	
	private static String getEnvironment(Fleet fleet, int x, int y) {
		String environment = "";
		
		if (fleet.isStartingJump())
			environment += "hyperspace";
		
		Area area = fleet.getArea();
		List<StellarObject> objects = area.getObjects();
		
		synchronized (objects) {
			for (StellarObject object : objects) {
				String type = object.getType();
				Rectangle bounds = object.getBounds();
				
				if (type.startsWith(StellarObject.TYPE_ASTEROID)) {
					if (bounds.contains(x, y)) {
						environment += (environment.length() > 0 ? "," : "") + type;
						break;
					}
				}
			}
		}
		
		List<StarSystem> systems = area.getSystems();
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				if (system.contains(x, y)) {
					environment += (environment.length() > 0 ? "," : "") +
						"system" + system.getStarImage();
					break;
				}
			}
		}
		
		// Stations spatiales
		List<SpaceStation> spaceStations = area.getSpaceStations();
		
		synchronized (spaceStations) {
			for (SpaceStation spaceStation : spaceStations) {
				int dx = spaceStation.getX() - x;
				int dy = spaceStation.getY() - y;
				
				if (dx * dx + dy * dy <= GameConstants.SPACE_STATION_RADIUS *
						GameConstants.SPACE_STATION_RADIUS) {
					environment += (environment.length() > 0 ? "," : "") + "spaceStation";
					break;
				}
			}
		}
		
		return environment;
	}
	
	private static Report attack(int mode, Fleet attackingFleet,
			Slot[] attackingSlots, double[] attackingFleetResources,
			Fleet defendingFleet, Slot[] defendingSlots,
			double[] defendingFleetResources, double attackingDamageFactor,
			double defendingDamageFactor, StringBuffer log, 
			int attackerXpGain, int defenderXpGain) {
		
		int fleetX = defendingFleet.getCurrentX();
		int fleetY = defendingFleet.getCurrentY();
		
		Report report = new Report(attackingFleet.getIdOwner(),
			defendingFleet.getIdOwner(), Utilities.now(), 0, Utilities.now(),
			getEnvironment(attackingFleet, fleetX, fleetY),
			getEnvironment(defendingFleet, fleetX, fleetY),
			attackingDamageFactor, defendingDamageFactor,
			attackingFleet.getIdCurrentArea(), 
			attackerXpGain, defenderXpGain);
		
		// Note : slots 0 à 4 = attaquant, 5 à 9 = défenseur
		boolean attacker = report.getIdPlayerAttacking() ==
			attackingFleet.getIdOwner();
		int attackingFleetSlotOffset = attacker ? 0 : 5;
		int defendingFleetSlotOffset = attacker ? 5 : 0;
		
		// Actions entreprises par chacune des flottes
		int[] attackingActionSlots = mode == MODE_SKIRMISH ?
				attackingFleet.getSkirmishActionSlots() :
				attackingFleet.getBattleActionSlots();
		int[] attackingActionAbilities = mode == MODE_SKIRMISH ?
				attackingFleet.getSkirmishActionAbilities() :
				attackingFleet.getBattleActionAbilities();
		
		int[] defendingActionSlots = mode == MODE_SKIRMISH ?
				defendingFleet.getSkirmishActionSlots() :
				defendingFleet.getBattleActionSlots();
		int[] defendingActionAbilities = mode == MODE_SKIRMISH ?
				defendingFleet.getSkirmishActionAbilities() :
				defendingFleet.getBattleActionAbilities();
		
		// Modificateurs des caractéristiques des vaisseaux (buffs, debuffs...)
		SlotState[] attackingSlotsState =
			new SlotState[GameConstants.FLEET_SLOT_COUNT];
		SlotState[] defendingSlotsState =
			new SlotState[GameConstants.FLEET_SLOT_COUNT];
		
		for (int i = 0; i < attackingSlotsState.length; i++) {
			attackingSlotsState[i] = new SlotState();
			defendingSlotsState[i] = new SlotState();
		}
		
		// Dégâts encaissés par les vaisseaux
		int[] attackingSlotsDamage = new int[GameConstants.FLEET_SLOT_COUNT];
		int[] defendingSlotsDamage = new int[GameConstants.FLEET_SLOT_COUNT];
		
		// Nombres initiaux de vaisseaux
		long[] attackingSlotsStartCount = new long[GameConstants.FLEET_SLOT_COUNT];
		long[] defendingSlotsStartCount = new long[GameConstants.FLEET_SLOT_COUNT];
		
		// Enregistre les vaisseaux de chaque slot
		log.append("------------------------------[ATT]----\n");
		logFleetOwner(log, attackingFleet);
		
		log.append("Damage factor: ");
		log.append(attackingDamageFactor);
		log.append("\n");
		
		Player attackingPlayer = attackingFleet.getOwner();
		
		for (int i = 0; i < attackingSlots.length; i++) {
			Slot slot = attackingSlots[i];
			
			attackingSlotsStartCount[i] = (long) slot.getCount();
			updateCountDependentAbilities(attackingFleet,
				slot, attackingSlotsState[i]);
			
			int availableAbilities = 0;
			
			if (slot.getId() != 0) {
				Ship ship = Ship.SHIPS[slot.getId()];
				
				for (int j = 0; j < ship.getAbilities().length; j++) {
					int[] requirements = ship.getAbilities()[j].getRequirements();
					boolean available = true;
					
					for (int requirement : requirements)
						if (!attackingPlayer.hasResearchedTechnology(requirement)) {
							available = false;
							break;
						}
					
					if (available)
						availableAbilities |= 1 << j;
				}
				
				log.append(i);
				log.append(". ");
				logSlot(log, true, slot, attackingSlotsDamage[i]);
				log.append("\n");
			}
			
			ReportSlot reportSlot = new ReportSlot(
				i + attackingFleetSlotOffset, slot.getId(),
				(long) slot.getCount(), slot.isFront(), availableAbilities);
			report.addReportSlot(reportSlot);
		}
		
		log.append("------------------------------[DEF]----\n");
		logFleetOwner(log, defendingFleet);
		
		log.append("Damage factor: ");
		log.append(defendingDamageFactor);
		log.append("\n");
		
		Player defendingPlayer = defendingFleet.getOwner();
		
		for (int i = 0; i < defendingSlots.length; i++) {
			Slot slot = defendingSlots[i];
			
			defendingSlotsStartCount[i] = (long) slot.getCount();
			updateCountDependentAbilities(defendingFleet,
				slot, defendingSlotsState[i]);
			
			int availableAbilities = 0;
			
			if (slot.getId() != 0) {
				Ship ship = Ship.SHIPS[slot.getId()];
				
				for (int j = 0; j < ship.getAbilities().length; j++) {
					int[] requirements = ship.getAbilities()[j].getRequirements();
					boolean available = true;
					
					for (int requirement : requirements)
						if (!defendingPlayer.hasResearchedTechnology(requirement)) {
							available = false;
							break;
						}
					
					if (available)
						availableAbilities |= 1 << j;
				}
				
				log.append(i);
				log.append(". ");
				logSlot(log, false, slot, defendingSlotsDamage[i]);
				log.append("\n");
			}
			
			ReportSlot reportSlot = new ReportSlot(
				i + defendingFleetSlotOffset, slot.getId(),
				(long) slot.getCount(), slot.isFront(), availableAbilities);
			report.addReportSlot(reportSlot);
		}
		
		// Désactive le déphasage en escarmouche
		if (mode == MODE_SKIRMISH) {
			for (int i = 0; i < attackingSlots.length; i++) {
				Ship ship = attackingSlots[i].getShip();
				
				if (ship != null && ship.hasAbility(Ability.TYPE_PHASE, attackingFleet.getOwner()))
					attackingSlotsState[i].setPhased(true);
			}
			
			for (int i = 0; i < defendingSlots.length; i++) {
				Ship ship = defendingSlots[i].getShip();
				
				if (ship != null && ship.hasAbility(Ability.TYPE_PHASE, defendingFleet.getOwner()))
					defendingSlotsState[i].setPhased(true);
			}
		}
		
		boolean defenderMerchant = true;
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			if (defendingFleet.getSlot(i).getId() != 0 &&
					defendingFleet.getSlot(i).getShip(
							).getShipClass() != Ship.FREIGHTER) {
				defenderMerchant = false;
			}
		
		for (int action = 0; action < (mode == MODE_SKIRMISH ? 5 : 15); action++) {
			// Compétence - repli
			int retreatSkillLevel = defendingFleet.getSkillLevel(Skill.SKILL_RETREAT);
			if ((defendingFleet.getMovement() > 0 || defendingFleet.isInHyperspace()) &&
					retreatSkillLevel >= 0 && (action % 5) == 0) {
				double random = Math.random();
				
				if (random < Skill.SKILL_RETREAT_EFFECT[action / 5][retreatSkillLevel] *
						(defenderMerchant ? 2 : 1)) {
					report.setRetreat(true);
					
					GameEventsDispatcher.fireGameNotification(new RetreatEvent(
							attackingFleet, defendingFleet, action));
					break;
				}
			}
			
			updateRoundDependantAbilities(attackingFleet, attackingSlots, attackingSlotsState, action);
			updateRoundDependantAbilities(defendingFleet, defendingSlots, defendingSlotsState, action);
			
			// La flotte attaquée joue
			play(defendingFleet, defendingSlots, defendingSlotsState,
				defendingSlotsDamage, defendingSlotsStartCount,
				defendingFleetResources, attackingFleet, attackingSlots,
				attackingSlotsState, attackingSlotsDamage,
				attackingSlotsStartCount, attackingFleetResources, report, action,
				defendingActionSlots[action], defendingActionAbilities[action],
				defendingDamageFactor, log);
			
			// La flotte qui attaque joue
			play(attackingFleet, attackingSlots, attackingSlotsState,
				attackingSlotsDamage, attackingSlotsStartCount,
				attackingFleetResources, defendingFleet, defendingSlots,
				defendingSlotsState, defendingSlotsDamage,
				defendingSlotsStartCount, defendingFleetResources, report, action,
				attackingActionSlots[action], attackingActionAbilities[action],
				attackingDamageFactor, log);
			
			if (isEmpty(attackingSlots) || isEmpty(defendingSlots))
				break;
		}
		
		// Affiche l'état des flottes après la bataille
		log.append("------------------------------[ATT]----\n");
		
		for (int i = 0; i < attackingSlots.length; i++) {
			Slot slot = attackingSlots[i];
			
			if (slot.getId() != 0) {
				log.append(i);
				log.append(". ");
				logSlot(log, true, slot, attackingSlotsDamage[i]);
				log.append("\n");
			}
		}
		
		log.append("------------------------------[DEF]----\n");
		
		for (int i = 0; i < defendingSlots.length; i++) {
			Slot slot = defendingSlots[i];
			
			if (slot.getId() != 0) {
				log.append(i);
				log.append(". ");
				logSlot(log, false, slot, defendingSlotsDamage[i]);
				log.append("\n");
			}
		}
		
		
		
		return report;
	}
	
	private static void play(Fleet attackingFleet, Slot[] attackingSlots,
			SlotState[] attackingSlotsState, int[] attackingSlotsDamage,
			long[] attackingSlotsStartCount, double[] attackingFleetResources,
			Fleet defendingFleet, Slot[] defendingSlots,
			SlotState[] defendingSlotsState, int[] defendingSlotsDamage,
			long[] defendingSlotsStartCount, double[] defendingFleetResources,
			Report report, int actionIndex, int actionSlot, int actionAbility,
			double damageFactor, StringBuffer log) {
		
		// Fin du combat si la flotte ennemie n'a plus de vaisseaux
		if (isEmpty(defendingSlots))
			return;
		
		for (SlotState attackingSlotState : attackingSlotsState)
			attackingSlotState.newAction();
		
		// Note : slots 0 à 4 = attaquant, 5 à 9 = défenseur
		boolean attacker = report.getIdPlayerAttacking() ==
			attackingFleet.getIdOwner();
		int attackingFleetSlotOffset = attacker ? 0 : 5;
		int defendingFleetSlotOffset = attacker ? 5 : 0;
		
		log.append("------------------------------[");
		log.append(attacker ? "ATT " : "DEF ");
		log.append(actionIndex);
		log.append("]--\n");
		
		// Aucune action
		if (actionSlot == -1)
			return;
		
		Slot attackingSlot = attackingSlots[actionSlot];
		SlotState attackingSlotState = attackingSlotsState[actionSlot];
		Ship attackingShip = attackingSlot.getShip();
		
		// Vérifie qu'il reste au moins un vaisseau
		if (attackingSlot.getId() == 0) {
			log.append("No ships left, aborting action.\n");
			return;
		}
		
		ReportAction reportAction = new ReportAction(actionIndex,
				actionSlot + attackingFleetSlotOffset);
		report.addReportAction(reportAction);
		
		ReportActionAbility reportActionAbility =
			new ReportActionAbility(0, attackingSlot.getId(), actionAbility);
		reportAction.addReportActionAbility(reportActionAbility);
		
		// Enregistre l'état des vaisseaux
		for (int i = 0; i < 5; i++) {
			if (attackingSlots[i].getCount() > 0)
				reportAction.addReportSlotState(new ReportSlotState(
					i + attackingFleetSlotOffset,
					attackingSlotsState[i].getTotalDamageMultiplier(),
					attackingSlotsState[i].getProtectionModifier(),
					1 / attackingSlotsState[i].getSufferedDamageMultiplier()));
			if (defendingSlots[i].getCount() > 0)
				reportAction.addReportSlotState(new ReportSlotState(
					i + defendingFleetSlotOffset,
					defendingSlotsState[i].getTotalDamageMultiplier(),
					defendingSlotsState[i].getProtectionModifier(),
					1 / defendingSlotsState[i].getSufferedDamageMultiplier()));
		}
		
		// Nom de l'action
		log.append("Slot: ");
		logSlot(log, attacker, attackingSlot, attackingSlotsDamage[actionSlot]);
		log.append("\nAction: ");
		log.append(actionAbility == -1 ? "Fire!" : Messages.getString(
			"ability" + attackingFleet.getSlot(actionSlot).getShip(
				).getAbilities()[actionAbility].getType()));
		log.append("\n");
		
		// Détermine si l'action a été inhibée
		boolean inhibited = false;
		for (int i = report.getReportActions().size() - 2; i >= 0; i--) {
			ReportAction previousReportAction = report.getReportActions().get(i);
			ReportActionAbility previousAbility =
				previousReportAction.getLastReportActionAbility();
			
			int offset = (previousReportAction.getSlotIndex() / 5) * 5;
			if (offset == defendingFleetSlotOffset) {
				if (previousAbility.getAbility() != -1 &&
						(previousReportAction.getModifiers() & ReportAction.INHIBITED) == 0) {
					if (Ship.SHIPS[previousAbility.getSlotId()].getAbilities(
							)[previousAbility.getAbility()].getType() == Ability.TYPE_INHIBITOR_FIELD) {
						if (actionAbility == -1) {
							reportAction.addModifier(ReportAction.INHIBITED);
							log.append("Inhibited!\n");
							inhibited = true;
						}
						break;
					}
				}
			} else if (offset == attackingFleetSlotOffset) {
				break;
			}
		}
		
		// Résoud l'action
		if (actionAbility == -1) {
			if (!inhibited) {
				// Tir normal
				shoot(attackingFleet, attackingSlots, attackingSlotsState,
					attackingSlotsDamage, attackingSlotsStartCount,
					attackingFleetResources, defendingFleet, defendingSlots,
					defendingSlotsState, defendingSlotsDamage,
					defendingSlotsStartCount, defendingFleetResources,
					actionSlot, reportAction, report, damageFactor, false, log);
			}
		} else {
			// Capacité spéciale
			Ability ability = attackingShip.getAbilities()[actionAbility];
			boolean finished = false;
			
			while (!finished) {
				finished = true;
				
				switch (ability.getType()) {
				case Ability.TYPE_LOTTERY:
					// Lotterie - lance une capacité au hasard pami celles auxquelles le joueur a accès
					Player player = attackingFleet.getOwner();
					ArrayList<Integer> idShips = new ArrayList<Integer>();
					ArrayList<Integer> availableAbilities = new ArrayList<Integer>();
					
					for (int i = 0; i < Ship.SHIPS.length; i++) {
						Ship ship = Ship.SHIPS[i];
						
						if (ship != null && player.hasResearchedShip(ship)) {
							abilities:for (int j = 0; j < ship.getAbilities().length; j++) {
								Ability shipAbility = ship.getAbilities()[j];
								
								if (shipAbility.isPassive() ||
										shipAbility.getType() == Ability.TYPE_LOTTERY)
									continue;
								
								int[] requirements = shipAbility.getRequirements();
								
								for (int requirement : requirements)
									if (!player.hasResearchedTechnology(requirement))
										continue abilities;
								
								idShips.add(i);
								availableAbilities.add(j);
							}
						}
					}
					
					if (availableAbilities.size() > 0) {
						int index = (int) (Math.random() * availableAbilities.size());
						finished = false;
						int shipId = idShips.get(index);
						int abilityIndex = availableAbilities.get(index);
						ability = Ship.SHIPS[shipId].getAbilities()[abilityIndex];
						
						reportActionAbility = new ReportActionAbility(
							reportActionAbility.getPosition() + 1,
							shipId, abilityIndex);
						reportAction.addReportActionAbility(reportActionAbility);
						
						log.append("It's lottery time!\n");
						log.append("Action: ");
						log.append(Messages.getString("ability" + ability.getType()));
						log.append("\n");
					}
					break;
				case Ability.TYPE_MIMICRY:
					// Mimétisme - relance la dernière capacité ennemie
					for (int i = report.getReportActions().size() - 1; i >= 0; i--) {
						ReportAction previousReportAction = report.getReportActions().get(i);
						
						int offset = (previousReportAction.getSlotIndex() / 5) * 5;
						
						if (offset == defendingFleetSlotOffset) {
							log.append("Mimicry!\n");
							
							if (previousReportAction.getLastReportActionAbility().getAbility() == -1) {
								// Tir
								log.append("Action: Fire!\n");
								
								shoot(attackingFleet, attackingSlots, attackingSlotsState,
									attackingSlotsDamage, attackingSlotsStartCount,
									attackingFleetResources, defendingFleet, defendingSlots,
									defendingSlotsState, defendingSlotsDamage,
									defendingSlotsStartCount, defendingFleetResources,
									actionSlot, reportAction, report, damageFactor, true, log);
							} else {
								// Capacité
								int mimicriedShipId = defendingFleet.getSlot(previousReportAction.getSlotIndex() % 5).getId();
								int mimicriedAbilityIndex = previousReportAction.getLastReportActionAbility().getAbility();
								ability = Ship.SHIPS[mimicriedShipId].getAbilities()[mimicriedAbilityIndex];
								finished = false;
								
								log.append("Action: ");
								log.append(Messages.getString("ability" + ability.getType()));
								log.append("\n");
								
								reportActionAbility = new ReportActionAbility(
									reportActionAbility.getPosition() + 1,
									mimicriedShipId, mimicriedAbilityIndex);
								reportAction.addReportActionAbility(reportActionAbility);
							}
							break;
						}
					}
					break;
				case Ability.TYPE_FORCE_FIELD:
					// Champ de force - augmente la protection des vaisseaux amis
					long hullSum = (long) attackingSlot.getCount() * attackingShip.getHull();
					
					for (int i = 0; i < attackingSlots.length; i++)
						if (attackingSlots[i].getId() != 0 &&
								attackingSlots[i].getCount() > 0) {
							long attackingHull = (long) attackingSlots[i].getCount() *
								attackingSlots[i].getShip().getHull();
							
							int protectionModifier =
								attackingSlotsState[i].getProtectionModifier();
							
							if (hullSum >= attackingHull)
								attackingSlotsState[i].addProtectionModifier(
										ability.getForceFieldHighProtectionModifier());
							else
								attackingSlotsState[i].addProtectionModifier(
										ability.getForceFieldLowProtectionModifier());
							
							logProtectionModifier(log, attacker, attackingSlots[i],
								attackingSlotsDamage[i], protectionModifier,
								attackingSlotsState[i].getProtectionModifier());
						}
					break;
				case Ability.TYPE_SACRIFICE:
					// Sacrifice - diminue la protection des vaisseaux ennemis
					// sur la ligne de front
					if (attackingSlot.isFront()) {
						hullSum = (long) attackingSlot.getCount() *
							attackingShip.getHull();
						
						for (int i = 0; i < defendingSlots.length; i++)
							if (defendingSlots[i].isFront() && defendingSlots[i].getId() != 0 &&
									defendingSlots[i].getCount() > 0) {
								long defendingHull = (long) defendingSlots[i].getCount() *
									defendingSlots[i].getShip().getHull();
								
								int protectionModifier =
									defendingSlotsState[i].getProtectionModifier();
								
								if (hullSum >= defendingHull)
									defendingSlotsState[i].addProtectionModifier(
											ability.getSacrificeHighProtectionModifier());
								else
									defendingSlotsState[i].addProtectionModifier(
											ability.getSacrificeLowProtectionModifier());
								
								logProtectionModifier(log, !attacker, defendingSlots[i],
									defendingSlotsDamage[i], protectionModifier,
									defendingSlotsState[i].getProtectionModifier());
							}
						
						attackingSlotState.addProtectionModifier(
							ability.getSacrificeSelfProtectionModifier());
					}
					break;
				case Ability.TYPE_RETRIBUTION:
					// Vengeance - renvoie les dégâts des tirs
					attackingSlotState.setRetribution(ability.getRetributionLength(),
						(long) Math.floor((attackingSlot.getCount() *
						attackingShip.getHull() - attackingSlotsDamage[actionSlot]) *
						ability.getRetributionMaxReturnedDamagePercentage()));
					break;
				case Ability.TYPE_ACTIVE_LEECH:
					// Vol de vaisseaux - regagne des points de vie en fonction des
					// dégâts infligés
					attackingSlotState.multiplyNextDamage(ability.getActiveLeechValue());
					attackingSlotState.addLeech(1);
					
					shoot(attackingFleet, attackingSlots, attackingSlotsState,
						attackingSlotsDamage, attackingSlotsStartCount,
						attackingFleetResources, defendingFleet, defendingSlots,
						defendingSlotsState, defendingSlotsDamage,
						defendingSlotsStartCount, defendingFleetResources,
						actionSlot, reportAction, report, damageFactor, true, log);
					
					attackingSlotState.addLeech(-1);
					break;
				case Ability.TYPE_ARTILLERY_SHOT:
					// Tir d'artillerie
					attackingSlotState.multiplyNextDamage(ability.getArtilleryShotDamageMultiplier());
					
					int targetIndex = 0;
					
					int backCount = 0;
					int frontCount = 0;
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
						if (defendingSlots[i].getShip() != null &&
								!defendingSlots[i].isFront())
							backCount++;
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
						if (defendingSlots[i].getShip() != null &&
								defendingSlots[i].isFront())
							frontCount++;
					
					//test si les 2 slots arriere sont des FREIGHTER
					int isFullCargo = 1;
					int isFullNoCargo = 1;
					int tempBackIndex = 0;
					int backTarget = 0;
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						if (defendingSlots[i].getShip() != null && 
								defendingSlots[i].getShip().getShipClass() 
								!= Ship.FREIGHTER &&
								!defendingSlots[i].isFront()){
							isFullCargo=0;
							tempBackIndex++;
							backTarget=tempBackIndex;
						}
						if(defendingSlots[i].getShip() != null && 
								defendingSlots[i].getShip().getShipClass() 
								== Ship.FREIGHTER &&
								!defendingSlots[i].isFront()){
							isFullNoCargo=0;
							tempBackIndex++;
						}

					}
					
					if (backCount > 0) {
						// Reporte les dégâts sur un slot à l'arrière aléatoire
						int backIndex=0;
						//S'il n'y a que des FREIGHTER ou que des non-FREIGHTER
						if(isFullCargo==1 || isFullNoCargo==1)
						backIndex = (int) (Math.random() * backCount);
						else backIndex=backTarget-1;
						
						backCount = 0;
						
						for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
							if (defendingSlots[i].getShip() != null &&
									!defendingSlots[i].isFront()) {
								if (backCount == backIndex) {
									targetIndex = i;
									break;
								}
								backCount++;
							}
					}
					
					attackingSlotState.setLockedTarget(targetIndex, false);
					
					shoot(attackingFleet, attackingSlots, attackingSlotsState,
						attackingSlotsDamage, attackingSlotsStartCount,
						attackingFleetResources, defendingFleet, defendingSlots,
						defendingSlotsState, defendingSlotsDamage,
						defendingSlotsStartCount, defendingFleetResources,
						actionSlot, reportAction, report, damageFactor, true, log);
					
					attackingSlotState.setLockedTarget(-1, true);
					break;
				case Ability.TYPE_FOCUSED_FIRE:
					// Tir concentré - augmente les dégâts du prochain tir
					SlotState slotState = attackingSlotState;
					double before = slotState.getNextDamageMultiplier();
					
					slotState.multiplyNextDamage(ability.getFocusFireBonus());
					
					logNextDamageMultiplier(log, true, attackingSlot,
						attackingSlotsDamage[actionSlot], before,
						slotState.getNextDamageMultiplier(),
						slotState.getTotalDamageMultiplier());
					break;
				case Ability.TYPE_FIERCENESS:
					// Acharnement - augmente les dégâts de l'Hadès face aux croiseurs
					attackingSlotState.multiplyNextDamage(ability.getFiercenessBonus());
					double before2 = attackingSlotState.getNextDamageMultiplier();
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						if (defendingSlots[i].getShip() != null && 
								defendingSlots[i].getShip().getShipClass() 
								== Ship.DREADNOUGHT){
							logNextDamageMultiplier(log, true, attackingSlot,
									attackingSlotsDamage[actionSlot], before2,
									ability.getFiercenessBonus(),
									attackingSlotState.getTotalDamageMultiplier());
						}
					
					}
					break;
				case Ability.TYPE_FRAG_CHARGE:
					// Charges à fragmentation - augmente les dégâts des vaisseaux
					// sur la même ligne que le vaisseau qui agit
					for (int i = 0; i < attackingSlots.length; i++) {
						if (attackingSlots[i].getId() != 0 &&
								attackingSlots[i].isFront() ==
									attackingSlot.isFront()) {
							slotState = attackingSlotsState[i];
							before = slotState.getDamageMultiplier();
							
							slotState.multiplyDamage(ability.getFragChargeBonus());
							
							logDamageMultiplier(log, attacker, attackingSlots[i],
								attackingSlotsDamage[i], before,
								slotState.getDamageMultiplier(),
								slotState.getTotalDamageMultiplier(), false);
						}
					}
					break;
				case Ability.TYPE_BARRAGE:
					// Tir de barrage - diminue les dégâts d'une classe de
					// vaisseaux ennemis
					for (int i = 0; i < defendingSlots.length; i++) {
						Ship ship = defendingSlots[i].getShip();
						
						if (ship != null && ship.getShipClass() ==
								ability.getBarrageClassTarget()) {
							slotState = defendingSlotsState[i];
							before = slotState.getDamageMultiplier();
							
							if (ship.hasAbility(Ability.TYPE_PHASE))
								slotState.setPhased(true);
							
							slotState.multiplyDamage(attackingSlot.isFront() ?
									ability.getBarrageFrontMalus() :
									ability.getBarrageBackMalus());
							
							logDamageMultiplier(log, !attacker, defendingSlots[i],
								defendingSlotsDamage[i], before,
								slotState.getDamageMultiplier(),
								slotState.getTotalDamageMultiplier(), false);
						}
					}
					break;
				case Ability.TYPE_BARRAGE_V2:
					// Tir de barrage V2 - diminue les dégâts des
					// vaisseaux ennemis
					for (int i = 0; i < defendingSlots.length; i++) {
						Ship ship = defendingSlots[i].getShip();
						
						if (ship != null) {
							slotState = defendingSlotsState[i];
							before = slotState.getDamageMultiplier();
							
							if (ship.hasAbility(Ability.TYPE_PHASE))
								slotState.setPhased(true);
							
							slotState.multiplyDamage(attackingSlot.isFront() ?
									ability.getBarrageV2FrontMalus() :
									ability.getBarrageV2BackMalus());
							
							logDamageMultiplier(log, !attacker, defendingSlots[i],
								defendingSlotsDamage[i], before,
								slotState.getDamageMultiplier(),
								slotState.getTotalDamageMultiplier(), false);
						}
					}
					break;
				case Ability.TYPE_REPAIR:
					// Réparation - répare le vaisseaux le plus endommagé
					long heal = (long) Math.floor(
							attackingShip.getHull() * attackingSlot.getCount() *
							ability.getRepairValue());
					
					// Recherche le vaisseau qui a été le plus endommagé
					long maxDamage = 0;
					int healedSlot = -1;
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						if (attackingSlots[i].getCount() > 0 && i != actionSlot) {
							long damage = (long) (
								attackingSlotsStartCount[i] -
								attackingSlots[i].getCount()) *
								attackingSlots[i].getShip().getHull() +
								attackingSlotsDamage[i];
							
							if (damage > maxDamage) {
								healedSlot = i;
								maxDamage = damage;
							}
						}
					}
					
					if (maxDamage > 0) {
						log.append("Heal: ");
						logSlot(log, attacker, attackingSlots[healedSlot],
								attackingSlotsDamage[healedSlot]);
						
						Ship healedShip = attackingSlots[healedSlot].getShip();
						if (heal > maxDamage)
							heal = maxDamage;
						
						long healValue = heal;
						heal -= attackingSlotsDamage[healedSlot];
						if (heal < 0) {
							heal = 0;
							attackingSlotsDamage[healedSlot] -= healValue;
						} else {
							attackingSlotsDamage[healedSlot] = 0;
						}
						
						int healedCount = (int) Math.ceil(heal / (double) healedShip.getHull());
						
						if (heal > 0)
							attackingSlotsDamage[healedSlot] = (int) (
								(healedCount * healedShip.getHull()) - heal);
						
						attackingSlots[healedSlot].setCount(
							attackingSlots[healedSlot].getCount() + healedCount);
						updateCountDependentAbilities(attackingFleet,
							attackingSlots[healedSlot], attackingSlotsState[healedSlot]);
						
						log.append(" => ");
						logSlot(log, attacker, attackingSlots[healedSlot],
								attackingSlotsDamage[healedSlot]);
						log.append(" [+");
						log.append(healValue);
						log.append("]\n");
						
						ReportDamage reportDamage = new ReportDamage(
							healedSlot + attackingFleetSlotOffset, -healValue,
							-healedCount, attackingSlotsDamage[healedSlot]);
						reportAction.addReportDamage(reportDamage);
					}
					break;
				case Ability.TYPE_REPERCUTE:
					// Répercussion - Inflige des dégats proportionnelement aux dégats encaissés
					
					// Dégats encaissés
					long damageReceived = (long) (
							attackingSlotsStartCount[actionSlot] -
							attackingSlots[actionSlot].getCount()) *
							attackingSlots[actionSlot].getShip().getHull() +
							attackingSlotsDamage[actionSlot];
					
					damageReceived *= ability.getRepercuteDamagePercent();
					
					List<Integer> potentialTarget = new ArrayList<Integer>();
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						if (defendingSlots[i].getCount() > 0 && defendingSlots[i].isFront()) {
							potentialTarget.add(i);
						}
					}
					
					int targetRepercute = Utilities.random(0, potentialTarget.size()-1);
					
					int realTarget = potentialTarget.get(targetRepercute);
					
					Ship targetShip = defendingSlots[realTarget].getShip();
					int killCount = (int) Math.ceil(damageReceived / (double) targetShip.getHull());
					
					if(defendingSlots[realTarget].getCount()-killCount>0)
					{
						defendingSlots[realTarget].setCount(
								defendingSlots[realTarget].getCount() - killCount);
					}
					else
					{
						killCount=(int) defendingSlots[realTarget].getCount();
						defendingSlots[realTarget].setCount(0);
					}
						updateCountDependentAbilities(defendingFleet,
								defendingSlots[realTarget], defendingSlotsState[realTarget]);
	
						if(damageReceived>0){
						ReportDamage reportDamage = new ReportDamage(
								realTarget + defendingFleetSlotOffset, damageReceived,
								killCount , defendingSlotsDamage[realTarget]);
						reportAction.addReportDamage(reportDamage);
				}

					break;
				case Ability.TYPE_DAMAGE_REPARTITION:
						// Capacité - Répartition
							long damageFromStart =(long) ((attackingSlotsStartCount[actionSlot] -
							attackingSlots[actionSlot].getCount()) *
							attackingSlots[actionSlot].getShip().getHull() +
							attackingSlotsDamage[actionSlot]);
							
							//TODO il semblerai que ceci bug et déclenche des interblocage... 
							//	Vérifier que chacun des objets est bien valoriser et agir en conséquence	
						long healMax = (long) Math.floor(damageFromStart * ability.getDamageRepartionDamages());
						heal = healMax;
						boolean full = false;
						int healedCountRepartition = 0;
						
						for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
							if (attackingSlots[i].getCount() > 0 
									&& attackingSlots[i].getShip() != attackingShip
									&& attackingSlots[i].isFront()) {
								full = false;
								double damageDef = (long) (
										 attackingSlotsStartCount[i]+ -
										 attackingSlots[i].getCount()) *
										 attackingSlots[i].getShip().getHull() +
										 attackingSlotsDamage[i];
								 
								

								 if(damageDef > 0 && damageDef<healMax)
									full = true;
								 
								 if(full == true){
									 attackingSlots[i].setCount(
											 attackingSlotsStartCount[i]);
									 
									 healedCountRepartition =  (int) Math.floor(attackingSlotsStartCount[i] -
											 attackingSlots[i].getCount());
									 
									 heal = healedCountRepartition* attackingSlots[i].getShip().getHull();
								 }
								 else if(damageDef > 0)
								 {
									 healedCountRepartition = (int) Math.floor(
											 healMax/attackingSlots[i].getShip().getHull());
									 attackingSlots[i].setCount(
											 attackingSlots[i].getCount()+
											 healedCountRepartition);
									 heal = healMax;
									 
									
								 }
								 
								 
								 updateCountDependentAbilities(attackingFleet,
										 attackingSlots[i], attackingSlotsState[i]);
								 
								 
								 if(damageDef > 0){
									ReportDamage reportDamage = new ReportDamage(
											i + attackingFleetSlotOffset, -heal,
											 -healedCountRepartition,attackingSlotsDamage[i]);
									reportAction.addReportDamage(reportDamage);
								 }

							}
						}
						
						
					break;
				case Ability.TYPE_RECUP:
					// Récupération  - répare tout les vaisseaux excepté les valkyries
					long recupSaved = (long) Math.floor(
							attackingShip.getHull() * attackingSlot.getCount() *
							ability.getRepairValue());
					long recup = recupSaved;
					
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						if (attackingSlots[i].getCount() > 0 && i != actionSlot) {
							recup = recupSaved;
							long damage = (long) (
								attackingSlotsStartCount[i] -
								attackingSlots[i].getCount()) *
								attackingSlots[i].getShip().getHull() +
								attackingSlotsDamage[i];
							
							if (damage > 0) {
								
								log.append("Recup: ");
								logSlot(log, attacker, attackingSlots[i],
										attackingSlotsDamage[i]);
								
								if(recup>damage)
									recup = damage;
								
								long healValue = recup;
								recup -= attackingSlotsDamage[i];
								if (recup < 0) {
									recup = 0;
									attackingSlotsDamage[i] -= healValue;
								} else {
									attackingSlotsDamage[i] = 0;
								}
								
								int healedCount = (int) Math.ceil(recup /
										(double) attackingSlots[i].getShip().getHull());
								
								if (recup > 0)
									attackingSlotsDamage[i] = (int) (
									(healedCount *  attackingSlots[i].getShip().getHull()) - recup);
								
								attackingSlots[i].setCount(
										attackingSlots[i].getCount() + healedCount);
									updateCountDependentAbilities(attackingFleet,
										attackingSlots[i], attackingSlotsState[i]);
									

									log.append(" => ");
									logSlot(log, attacker, attackingSlots[i],
											attackingSlotsDamage[i]);
									log.append(" [+");
									log.append(healValue);
									log.append("]\n");
									
									ReportDamage reportDamage = new ReportDamage(
										i + attackingFleetSlotOffset, -healValue,
										-healedCount, attackingSlotsDamage[i]);
									reportAction.addReportDamage(reportDamage);
	
							}
						}
					}
					break;
				case Ability.TYPE_RAPID_FIRE:
					// Tir en rafale - execute un tir sur la ligne de front ennemie
					for (int i = 0; i < defendingSlots.length; i++) {
						Ship ship = defendingSlots[i].getShip();
						
						if (ship != null && defendingSlots[i].isFront()) {
							attackingSlotState.multiplyNextDamage(ability.getRapidFireDamageModifier());
							attackingSlotState.setLockedTarget(i, false);
							
							shoot(attackingFleet, attackingSlots,
								attackingSlotsState, attackingSlotsDamage,
								attackingSlotsStartCount, attackingFleetResources,
								defendingFleet, defendingSlots, defendingSlotsState,
								defendingSlotsDamage, defendingSlotsStartCount,
								defendingFleetResources, actionSlot, reportAction,
								report, damageFactor, true, log);
						}
					}
					
					attackingSlotState.setLockedTarget(-1, true);
					break;
				case Ability.TYPE_RAPID_FIRE_V2:
					// Tir en rafale V2- execute un tir sur la ligne de front ennemie (et la ligne arrière)
					for (int i = 0; i < defendingSlots.length; i++) {
						Ship ship = defendingSlots[i].getShip();

						if (ship != null && (defendingSlots[i].isFront() ||
								ability.getRapidFireV2AllLines()==true)) {
							attackingSlotState.multiplyNextDamage(ability.getRapidFireV2DamageModifier());
							attackingSlotState.setLockedTarget(i, false);
							
							shoot(attackingFleet, attackingSlots,
								attackingSlotsState, attackingSlotsDamage,
								attackingSlotsStartCount, attackingFleetResources,
								defendingFleet, defendingSlots, defendingSlotsState,
								defendingSlotsDamage, defendingSlotsStartCount,
								defendingFleetResources, actionSlot, reportAction,
								report, damageFactor, true, log);
						}
					}
					
					attackingSlotState.setLockedTarget(-1, true);
					break;
				case Ability.TYPE_RESISTANCE:
					// Résistance - réduit les dégâts subis contre une classe
					attackingSlotState.setResistance(
						ability.getResistanceClassTarget(),
						ability.getResistanceDamageModifier());
					
					log.append("Against class: ");
					log.append(ability.getResistanceClassTarget());
					log.append("\n");
					break;
				case Ability.TYPE_ALL_RESISTANCE:
					// Résistance globale - réduit les dégâts subis
					// contre toutes les classes pendant X tours
					attackingSlotState.setAllResistance(
							ability.getAllResistanceLength(),
							ability.getAllResistanceValue());
					break;
				case Ability.TYPE_TAUNT:
					// Persécution - force l'attaque
					attackingSlotState.setTaunt(ability.getTauntLength());
					attackingSlotState.addTauntProtectionModifier(
							ability.getTauntProtectionModifier());
					break;
				case Ability.TYPE_DAMAGE_RETURN_AURA:
					// Pénitence - renvoie de dégâts
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						if (attackingSlots[i].getShip() != null &&
								attackingSlots[i].isFront() ==
									attackingSlot.isFront()) {
							attackingSlotsState[i].addDamageReturn(
									ability.getDamageReturnAuraValue());
						}
					}
					break;
				case Ability.TYPE_PROXIMITY_CHARGE:
					// charge de proximité - renvoie de dégâts
							attackingSlotState.addDamageReturn(
									ability.getProximityChargeValue());
					break;
				case Ability.TYPE_TIME_SHIFT:
					// Faille temporelle
					int totalLosses = 0;
					long totalDamage = 0;
					int hull = attackingShip.getHull();
					
					if (report.getReportActions() != null) {
						search:for (int i = report.getReportActions().size() - 1; i >= 0; i--) {
							ReportAction oldReportAction = report.getReportActions().get(i);
							
							if (oldReportAction.getActionIndex() + ability.getTimeShiftActionsCancelled() < actionIndex || (
								oldReportAction.getActionIndex() + ability.getTimeShiftActionsCancelled() == actionIndex && attacker &&
								(oldReportAction.getSlotIndex() / 5) * 5 != attackingFleetSlotOffset)) {
								// Recherche la coque du vaisseau il y a X actions
								if (oldReportAction.getReportDamages() != null) {
									for (int j = oldReportAction.getReportDamages().size() - 1; j >= 0; j--) {
										ReportDamage oldReportDamage = oldReportAction.getReportDamages().get(j);
										if (oldReportDamage.getTargetPosition() ==
												actionSlot + attackingFleetSlotOffset) {
											hull = oldReportDamage.getHullDamage();
											break search;
										}
									}
								}
							} else {
								// Détermine les dégats subis par le vaisseau depuis X actions
								if (oldReportAction.getReportDamages() != null) {
									for (ReportDamage oldReportDamage : oldReportAction.getReportDamages()) {
										if (oldReportDamage.getTargetPosition() ==
												actionSlot + attackingFleetSlotOffset) {
											totalDamage += oldReportDamage.getDamage();
											totalLosses += oldReportDamage.getKills();
										}
									}
								}
							}
						}
					}
					
					if (totalLosses > 0) {
						attackingSlot.setCount(attackingSlot.getCount() + totalLosses);
						attackingSlotsDamage[actionSlot] = hull;
						updateCountDependentAbilities(attackingFleet,
							attackingSlot, attackingSlotState);
					}
					
					ReportDamage reportDamage = new ReportDamage(
						actionSlot + attackingFleetSlotOffset, -totalDamage,
						-totalLosses, hull);
					reportAction.addReportDamage(reportDamage);
					break;
				case Ability.TYPE_DEVASTATE:
					// Ravage
					shoot(attackingFleet, attackingSlots, attackingSlotsState,
						attackingSlotsDamage, attackingSlotsStartCount,
						attackingFleetResources, defendingFleet, defendingSlots,
						defendingSlotsState, defendingSlotsDamage,
						defendingSlotsStartCount, defendingFleetResources,
						actionSlot, reportAction, report, damageFactor, true, log);
					
					// Calcule les dégâts infligés
					totalDamage = 0;
					if (reportAction.getReportDamages() != null) {
						for (ReportDamage damage : reportAction.getReportDamages()) {
							if ((damage.getTargetPosition() / 5) != attackingFleetSlotOffset) {
								totalDamage += damage.getDamage();
							}
						}
					}
					
					if (totalDamage > 0) {
						// Compte le nombre de slots à l'arrière
						backCount = 0;
						frontCount = 0;
						for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
							if (defendingSlots[i].getShip() != null &&
									!defendingSlots[i].isFront())
								backCount++;
						
						for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
							if (defendingSlots[i].getShip() != null &&
									defendingSlots[i].isFront())
								frontCount++;
						
						//test si les 2 slots arriere sont des FREIGHTER
						isFullCargo = 1;
						isFullNoCargo = 1;
						tempBackIndex = 0;
						backTarget = 0;
						for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
							if (defendingSlots[i].getShip() != null && 
									defendingSlots[i].getShip().getShipClass() 
									!= Ship.FREIGHTER &&
									!defendingSlots[i].isFront()){
								isFullCargo=0;
								tempBackIndex++;
								backTarget=tempBackIndex;
							}
							if(defendingSlots[i].getShip() != null && 
									defendingSlots[i].getShip().getShipClass() 
									== Ship.FREIGHTER &&
									!defendingSlots[i].isFront()){
								isFullNoCargo=0;
								tempBackIndex++;
							}

						}
						
						if (backCount > 0) {
							// Reporte les dégâts sur un slot à l'arrière aléatoire
							int backIndex=0;
							//S'il n'y a que des FREIGHTER ou que des non-FREIGHTER
							if(isFullCargo==1 || isFullNoCargo==1)
							backIndex = (int) (Math.random() * backCount);
							else backIndex=backTarget-1;

							backCount = 0;
							
							for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
								if (defendingSlots[i].getShip() != null &&
										!defendingSlots[i].isFront()) {
									if (backCount == backIndex) {
										log.append("Breach: ");
										logSlot(log, !attacker, defendingSlots[i], defendingSlotsDamage[i]);
										
										Ship defendingShip = defendingSlots[i].getShip();
										
										totalDamage *= ability.getBreachExtraDamage();
										
										int losses = (int) Math.floor(
											(totalDamage + defendingSlotsDamage[i]) /
											defendingSlots[i].getShip().getHull());
										if (losses > defendingSlots[i].getCount())
											losses = (int) defendingSlots[i].getCount();
										long count = (long) Math.max(0,
											defendingSlots[i].getCount() - losses);
										
										defendingSlotsDamage[i] = count > 0 ?
											defendingSlots[i].getShip().getHull() -
											(int) ((losses + 1) * defendingSlots[i].getShip().getHull() -
											(totalDamage + defendingSlotsDamage[i])) : 0;
										defendingSlots[i].setCount(count);
										updateCountDependentAbilities(defendingFleet,
											defendingSlots[i], defendingSlotsState[i]);
										
										reportDamage = new ReportDamage(
											i + defendingFleetSlotOffset, totalDamage,
											losses, defendingSlotsDamage[i]);
										reportAction.addReportDamage(reportDamage);
										
										stealResources(attackingFleet, attackingShip,
											attackingFleetResources, defendingFleet,
											defendingShip, defendingFleetResources,
											losses, reportDamage);
										
										log.append(" > ");
										logSlot(log, !attacker, defendingSlots[i], defendingSlotsDamage[i]);
										log.append("\n");
										break;
									}
									backCount++;
								}
						}
					}
					break;
				case Ability.TYPE_FUSION:
					// Fusion - absorbe les dégâts
					attackingSlotState.setFusion(ability.getFusionLength());
					break;
				case Ability.TYPE_RIFT:
					// Faille - inversion de tous les bonus et malus !
					for (int i = 0; i < 5; i++) {
						attackingSlotsState[i].revert();
						defendingSlotsState[i].revert();
					}
					break;
				case Ability.TYPE_PARTICLE_PROJECTION:
					// Projection de particules - dégats fixes
					long cost = (long) attackingSlot.getCount() *
						ability.getParticleProjectionAntimatterCost();
					
					if (cost <= attackingFleetResources[3]) {
						attackingFleetResources[3] -= cost;
						
						attackingSlotState.setDamageMode(SlotState.FIXED_DAMAGE);
						attackingSlotState.setFixedDamageValue(ability.getParticleProjectionDamage());
						
						shoot(attackingFleet, attackingSlots, attackingSlotsState, attackingSlotsDamage,
							attackingSlotsStartCount, attackingFleetResources, defendingFleet,
							defendingSlots, defendingSlotsState, defendingSlotsDamage,
							defendingSlotsStartCount, defendingFleetResources, actionSlot,
							reportAction, report, damageFactor, true, log);
						
						attackingSlotState.setDamageMode(SlotState.NORMAL_DAMAGE);
						attackingSlotState.setFixedDamageValue(0);
					} else {
						// Antimatière insuffisante pour utiliser la capacité
						shoot(attackingFleet, attackingSlots, attackingSlotsState, attackingSlotsDamage,
							attackingSlotsStartCount, attackingFleetResources, defendingFleet,
							defendingSlots, defendingSlotsState, defendingSlotsDamage,
							defendingSlotsStartCount, defendingFleetResources, actionSlot,
							reportAction, report, damageFactor, true, log);
					}
					break;
				case Ability.TYPE_DEFLECTOR:
					// Déflecteur
					for (int i = 0; i < attackingSlots.length; i++) {
						Ship ship = attackingSlots[i].getShip();
						
						if (ship != null &&
								ship.getShipClass() ==
									ability.getDeflectorClassesTarget()) {
							slotState = attackingSlotsState[i];
							int protectionBefore = slotState.getProtectionModifier();
							
							slotState.addProtectionModifier(
								ability.getDeflectorProtectionModifier());
							
							logProtectionModifier(log, attacker,
								attackingSlots[i], attackingSlotsDamage[i],
								protectionBefore, slotState.getProtectionModifier());
						}
					}
					break;
				case Ability.TYPE_RAGE:
					// Rage - dégats maximum, diminue la protection
					attackingSlotState.setDamageMode(SlotState.MAXIMUM_DAMAGE);
					
					shoot(attackingFleet, attackingSlots, attackingSlotsState,
						attackingSlotsDamage, attackingSlotsStartCount,
						attackingFleetResources, defendingFleet, defendingSlots,
						defendingSlotsState, defendingSlotsDamage,
						defendingSlotsStartCount, defendingFleetResources,
						actionSlot, reportAction, report, damageFactor, true, log);
					
					attackingSlotState.setDamageMode(SlotState.NORMAL_DAMAGE);
					
					int protectionBefore = attackingSlotState.getProtectionModifier();
					attackingSlotState.addProtectionModifier(ability.getRageProtectionModifier());
					
					logProtectionModifier(log, attacker,
						attackingSlots[actionSlot], attackingSlotsDamage[actionSlot],
						protectionBefore, attackingSlotState.getProtectionModifier());
					break;

				case Ability.TYPE_OVERHEAT:
					// Surchauffe - dégats +X %, diminue les PV
					attackingSlotState.multiplyNextDamage(ability.getOverHeatDamageModifier());
					
					attackingSlotState.multiplySufferedDamage(1/ability.getOverHeatHullModifier());
					
					shoot(attackingFleet, attackingSlots, attackingSlotsState,
						attackingSlotsDamage, attackingSlotsStartCount,
						attackingFleetResources, defendingFleet, defendingSlots,
						defendingSlotsState, defendingSlotsDamage,
						defendingSlotsStartCount, defendingFleetResources,
						actionSlot, reportAction, report, damageFactor, true, log);
					
					attackingSlotState.setDamageMode(SlotState.NORMAL_DAMAGE);
					break;
					
				case Ability.TYPE_CONFUSION:
					// Confusion - dégâts minimum au prochain tir
					boolean frontLine = ability.isConfusionFrontLine();
					int slotsCount = 0;
					
					for (int i = 0; i < defendingSlots.length; i++) {
						if (defendingSlots[i].isFront() == frontLine &&
								defendingSlots[i].getShip() != null &&
								defendingSlots[i].getShip().getShipClass() != Ship.FREIGHTER) {
							slotsCount++;
						}
					}
					
					if (slotsCount > 0) {
						int slot = (int) (Math.random() * slotsCount);
						slotsCount = 0;
						
						for (int i = 0; i < defendingSlots.length; i++) {
							if (defendingSlots[i].isFront() == frontLine &&
									defendingSlots[i].getShip() != null &&
									defendingSlots[i].getShip().getShipClass() != Ship.FREIGHTER) {
								
								if (slotsCount == slot) {
									defendingSlotsState[i].setConfused(true);
									
									reportDamage = new ReportDamage(
										i + defendingFleetSlotOffset, 0,
										0, defendingSlotsDamage[i]);
									reportAction.addReportDamage(reportDamage);
									
									log.append("Confused: ");
									logSlot(log, !attacker, defendingSlots[i],
											defendingSlotsDamage[i]);
									log.append("\n");
									break;
								}
								slotsCount++;
							}
						}
					}
					break;
				case Ability.TYPE_HULL_ENERGY_TRANSFER:
				case Ability.TYPE_DAMAGE_ENERGY_TRANSFER:
					// Transfert d'énergie - énergie vers les PV / les dégâts
					before = attackingSlotState.getDamageMultiplier();
					attackingSlotState.multiplyDamage(ability.getEnergyTransferDamageModifier());

					logDamageMultiplier(log, attacker, attackingSlot,
						attackingSlotsDamage[actionSlot], before,
						attackingSlotState.getDamageMultiplier(),
						attackingSlotState.getTotalDamageMultiplier(), false);
					
					before = attackingSlotState.getSufferedDamageMultiplier();
					attackingSlotState.multiplySufferedDamage(1 / ability.getEnergyTransferHullModifier());
					
					logDamageMultiplier(log, attacker, attackingSlot,
						attackingSlotsDamage[actionSlot], before,
						attackingSlotState.getSufferedDamageMultiplier(),
						attackingSlotState.getSufferedDamageMultiplier(), true);
					break;
				}
			}
			
			//TODO KEEPER
		}

		// Position des slots
		int frontSlots = 0;
		for (int i = 0; i < attackingSlots.length; i++)
			if (attackingSlots[i].isFront())
				frontSlots |= 1 << (attacker ? i : i + 5);
		for (int i = 0; i < defendingSlots.length; i++)
			if (defendingSlots[i].isFront())
				frontSlots |= 1 << (attacker ? i + 5 : i);
		
		reportAction.setFrontSlots(frontSlots);
	}
	
	private static void shoot(Fleet attackingFleet, Slot[] attackingSlots,
			SlotState[] attackingSlotsState, int[] attackingSlotsDamage,
			long[] attackingSlotsStartCount, double[] attackingFleetResources,
			Fleet defendingFleet, Slot[] defendingSlots,
			SlotState[] defendingSlotsState, int[] defendingSlotsDamage,
			long[] defendingSlotsStartCount, double[] defendingFleetResources,
			int actionSlot, ReportAction reportAction, Report report,
			double damageFactor, boolean isAbility, StringBuffer log) {
		// Note : slots 0 à 4 = attaquant, 5 à 9 = défenseur
		boolean attacker = reportAction.getSlotIndex() < GameConstants.FLEET_SLOT_COUNT;
		int attackingFleetSlotOffset = attacker ? 0 : 5;
		int defendingFleetSlotOffset = attacker ? 5 : 0;
		
		Slot attackingSlot = attackingSlots[actionSlot];
		Ship attackingShip = attackingSlot.getShip();
		
		// Vérifie que le vaisseau a des armes
		if (attackingShip.getWeapons().length == 0)
			return;
		
		// Vérifie qu'il reste des vaisseaux
		if (attackingSlot.getCount() == 0)
			return;
		
		// Recherche une cible dans la flotte ennemie
		int target;
		if (attackingSlotsState[actionSlot].getLockedTarget() != -1) {
			target = attackingSlotsState[actionSlot].getLockedTarget();
		} else {
			target = getShipTarget(attackingShip, defendingFleet,
					defendingSlots, defendingSlotsState);
		}
		
		if (target == -1)
			return;
		
		Slot defendingSlot = defendingSlots[target];
		Ship defendingShip = defendingSlot.getShip();
		
		ReportDamage reportDamage = new ReportDamage(
			target + defendingFleetSlotOffset, 0, 0,
			defendingSlotsDamage[target]);
		reportAction.addReportDamage(reportDamage);
		
		ReportDamage selfDamage = null;
		boolean newSelfDamage = false;
		if (reportAction.getReportDamages() != null) {
			for (ReportDamage oldReportDamage : reportAction.getReportDamages()) {
				if (oldReportDamage.getTargetPosition() ==
						actionSlot + attackingFleetSlotOffset) {
					selfDamage = oldReportDamage;
					break;
				}
			}
		}
		
		if (selfDamage == null) {
			selfDamage = new ReportDamage(
				actionSlot + attackingFleetSlotOffset, 0, 0,
				attackingSlotsDamage[actionSlot]);
			newSelfDamage = true;
		}
		
		// Capacité - escadrille
		if (attackingShip != null && attackingShip.hasAbility(
				Ability.TYPE_SQUADRON, attackingFleet.getOwner())) {
			attackingSlotsState[actionSlot].setSquadronDamageModifier(0);
			
			Ability ability = attackingShip.getAbility(Ability.TYPE_SQUADRON);
			
			for (int j = 0; j < attackingSlotsState.length; j++) {
				if (actionSlot != j && attackingSlots[j].getShip() != null &&
						attackingSlots[j].getShip().getShipClass() ==
						ability.getSquadronTargetClasses())
					attackingSlotsState[actionSlot].addSquadronDamageModifier(
						ability.getSquadronExtraDamage());
			}
			
			log.append("Squadron damage modifier: +");
			log.append(attackingSlotsState[actionSlot].getSquadronDamageModifier());
			log.append("\n");
		}
		
		// Capacité - esquive
		boolean dodged = false;
		
		if (defendingShip.hasAbility(Ability.TYPE_DODGE, defendingFleet.getOwner())) {
			Ability dodge = defendingShip.getAbility(Ability.TYPE_DODGE);
			
			if (Math.random() < dodge.getDodgeChances()) {
				reportDamage.addModifier(ReportDamage.DODGED);
				dodged = true;
				log.append("Dodged!\n");
				
				GameEventsDispatcher.fireGameNotification(new DodgeEvent(
					attackingFleet, defendingFleet, actionSlot, target));
			}
		}
		
		// Capacité - esquive V2
		
		if (defendingShip.hasAbility(Ability.TYPE_DODGE_V2, defendingFleet.getOwner())) {
			Ability dodgeV2 = defendingShip.getAbility(Ability.TYPE_DODGE_V2);
			
			double startCount = defendingSlotsStartCount[target] ;
			double actualCount = defendingSlots[target].getCount();
			
			double ratio = actualCount / startCount;
			
			double dodge = dodgeV2.getDodgeV2Lower() +
			((dodgeV2.getDodgeV2Upper()-dodgeV2.getDodgeV2Lower())*(1-ratio));
			

			
			if (Math.random() < dodge) {
				reportDamage.addModifier(ReportDamage.DODGED);
				dodged = true;
				log.append("Dodged!\n");
				
				GameEventsDispatcher.fireGameNotification(new DodgeEvent(
					attackingFleet, defendingFleet, actionSlot, target));
			}
		}
		
		

		// Capacité - Anticipation : esquive selon le nombre de vaisseau d'une classe de la flotte ennemie
		if (defendingShip.hasAbility(Ability.TYPE_ANTICIPATE, defendingFleet.getOwner())) {
			Ability ability = defendingShip.getAbility(Ability.TYPE_ANTICIPATE);
			
			int targetsNumber = 0;
			
			for (int j = 0; j < attackingSlotsState.length; j++) {
				if (attackingSlots[j].getShip() != null &&
						attackingSlots[j].getShip().getShipClass() ==
							ability.getAnticipationClasses()){
					targetsNumber++;
				}
			}
			
			if (Math.random() < ability.getAnticipationChances()*targetsNumber) {
				
				reportDamage.addModifier(ReportDamage.DODGED);
				dodged = true;
				log.append("Dodged (Anticipation)!\n");
				
				GameEventsDispatcher.fireGameNotification(new DodgeEvent(
					attackingFleet, defendingFleet, actionSlot, target));
			}
		}
		
		
		// Capacité - évasion
		if (defendingShip.hasAbility(Ability.TYPE_EVADE, defendingFleet.getOwner())) {
			Ability evade = defendingShip.getAbility(Ability.TYPE_EVADE);
			
			int fighter = 0;
			
			for (int j = 0; j < defendingSlotsState.length; j++) {
				if (defendingSlots[j].getShip() != null &&
						defendingSlots[j].getShip().getShipClass() ==
						Ship.FIGHTER && defendingSlots[j].getShip()!=defendingShip){
					fighter++;
				}
			}
			
			if (Math.random() < evade.getEvadePercent()*fighter) {
				
				reportDamage.addModifier(ReportDamage.DODGED);
				dodged = true;
				log.append("Dodged!\n");
				
				GameEventsDispatcher.fireGameNotification(new DodgeEvent(
					attackingFleet, defendingFleet, actionSlot, target));
			}
		}
		
		// Effets des capacités appliqué ?
		boolean effectHarassment = false, effectHeat = false, effectBreach = false,
			effectCurse = false, effectIncoherence = false;
		
		// Pour chaque arme du vaisseau
		weapons:for (WeaponGroup weaponGroup : attackingShip.getWeapons()) {
			if (target == -1 || dodged)
				break;
			
			Weapon weapon = weaponGroup.getWeapon();
			
			long shotsLeft = (weaponGroup.getCount() *
					(long) attackingSlot.getCount());
			
			log.append("Shots: ");
			log.append(shotsLeft);
			log.append("x ");
			log.append(Messages.getString("weapon" + weaponGroup.getIdWeapon()));
			log.append("\nTarget: ");
			logSlot(log, !attacker, defendingSlot, defendingSlotsDamage[target]);
			log.append("\n");
			
			// Continue à tirer tant qu'il reste des vaisseaux pouvant
			// tirer
			do {
				
				// Capacité - harcèlement
				if (!effectHarassment && attackingShip.hasAbility(
						Ability.TYPE_HARASSMENT, attackingFleet.getOwner())) {
					Ability harassment = attackingShip.getAbility(Ability.TYPE_HARASSMENT);
					
					double before = defendingSlotsState[target].getDamageMultiplier();
					defendingSlotsState[target].multiplyDamage(harassment.getHarassmentDamageModifier());
					
					effectHarassment = true;
					
					logDamageMultiplier(log, !attacker, defendingSlot,
						defendingSlotsDamage[target], before,
						defendingSlotsState[target].getDamageMultiplier(),
						defendingSlotsState[target].getTotalDamageMultiplier(), false);
				}
				
				// Capacité - rupture
				if (!effectBreach && attackingShip.hasAbility(
						Ability.TYPE_BREACH, attackingFleet.getOwner())) {
					Ability breach = attackingShip.getAbility(Ability.TYPE_BREACH);
					double before = defendingSlotsState[target].getSufferedDamageMultiplier();
					defendingSlotsState[target].multiplySufferedDamage(breach.getBreachExtraDamage());
					
					effectBreach = true;
					
					logDamageMultiplier(log, !attacker, defendingSlot,
						defendingSlotsDamage[target], before,
						defendingSlotsState[target].getSufferedDamageMultiplier(),
						defendingSlotsState[target].getSufferedDamageMultiplier(), true);
				}
				
				// Capacité - charges creuses
				if (!effectHeat && attackingShip.hasAbility(
						Ability.TYPE_HEAT, attackingFleet.getOwner())) {
					Ability heat = attackingShip.getAbility(Ability.TYPE_HEAT);
					
					int before = defendingSlotsState[target].getProtectionModifier();
					defendingSlotsState[target].addProtectionModifier(
							heat.getHeatProtectionModifier());
					
					effectHeat = true;
					
					logProtectionModifier(log, !attacker, defendingSlot,
						defendingSlotsDamage[target], before,
						defendingSlotsState[target].getProtectionModifier());
				}
				
				// Capacité - Incohérence
				if (!effectIncoherence && attackingShip.hasAbility(
						Ability.TYPE_INCOHERENCE, attackingFleet.getOwner())) {
					Ability incoherence = attackingShip.getAbility(Ability.TYPE_INCOHERENCE);
					
					int before = defendingSlotsState[target].getProtectionModifier();
					
					double chance = incoherence.getIncoherenceChance();

					
					if(Math.random()<chance){
					defendingSlotsState[target].addProtectionModifier(
							-incoherence.getIncoherenceProtectionModifier());
					
					attackingSlotsState[actionSlot].addProtectionModifier(
							incoherence.getIncoherenceProtectionModifier());
					
				}
					
					effectIncoherence = true;
					
//					logProtectionModifier(log, !attacker, defendingSlot,
//						defendingSlotsDamage[target], before,
//						defendingSlotsState[target].getProtectionModifier());
				}
				
				// Capacité - déphasage
				if (defendingShip.hasAbility(Ability.TYPE_PHASE,
						defendingFleet.getOwner())) {
					if (!defendingSlotsState[target].hasPhased()) {
						log.append("Phased!\n");
						reportDamage.addModifier(ReportDamage.PHASED);
						defendingSlotsState[target].setPhased(true);
						break weapons;
					}
				}
				
				// Détermine si le vaisseau ciblé est une cible prioritaire
				boolean priorityTarget = ArrayUtils.contains(
						attackingShip.getTargets(),
						defendingShip.getShipClass());
				
				// Coefficient de dégats, en fonction des buffs placés sur
				// le vaisseau
				double coef = attackingSlotsState[actionSlot].getTotalDamageMultiplier();
				
				coef *= defendingSlotsState[target].getSufferedDamageMultiplier();
				
				// Capacité - résistance
				if (defendingSlotsState[target].getResistanceTargetClass() ==
						attackingShip.getShipClass()) {
					coef *= defendingSlotsState[target].getResistanceDamageModifier();
				}
				
				// Bonus contre les cibles prioritaires
				if (priorityTarget)
					coef += GameConstants.PRIORITY_TARGET_DAMAGE /
							attackingShip.getTargets().length;
				
				// Dégâts infligés
				int damageMode = attackingSlotsState[actionSlot].getDamageMode();
				
				if (damageMode == SlotState.NORMAL_DAMAGE &&
						attackingSlotsState[actionSlot].isConfused())
					damageMode = SlotState.MINIMUM_DAMAGE;
				
				if (defendingShip.hasAbility(Ability.TYPE_UNBREAKABLE,
						defendingFleet.getOwner()))
					damageMode = SlotState.MINIMUM_DAMAGE;
				
				double damage;
				switch (damageMode) {
				case SlotState.NORMAL_DAMAGE:
					damage = Utilities.random(
						weapon.getDamageMin(), weapon.getDamageMax());
					break;
				case SlotState.MINIMUM_DAMAGE:
					damage = weapon.getDamageMin();
					break;
				case SlotState.MAXIMUM_DAMAGE:
					damage = weapon.getDamageMax();
					break;
				case SlotState.FIXED_DAMAGE:
					damage = 0;
					break;
				default:
					throw new IllegalArgumentException("Invalid damage mode.");
				}
				
				// Capacité - escadrille
				damage += attackingSlotsState[actionSlot].getSquadronDamageModifier();
				
				// Capacité - coup critique
				if (attackingShip.hasAbility(Ability.TYPE_CRITICAL_HIT,
						attackingFleet.getOwner())) {
					Ability criticalHit = attackingShip.getAbility(
							Ability.TYPE_CRITICAL_HIT);
					
					if (Math.random() < criticalHit.getCriticalHitChances()) {
						damage *= criticalHit.getCriticalHitMultiplier();
						reportDamage.addModifier(ReportDamage.CRITICAL_HIT);
						
						log.append("Critical hit!\n");
						
						GameEventsDispatcher.fireGameNotification(new CriticalHitEvent(
							attackingFleet, defendingFleet, actionSlot, target));
					}
				}
				
				// Capacité - coup critique V2
				if (attackingShip.hasAbility(Ability.TYPE_CRITICALV2,
						attackingFleet.getOwner())) {
					Ability criticalHitV2 = attackingShip.getAbility(
							Ability.TYPE_CRITICALV2);
					
					if (Math.random() < criticalHitV2.getCriticalV2Chance2()) {
						damage *= criticalHitV2.getCriticalV2Multiplier2();
						reportDamage.addModifier(ReportDamage.CRITICAL_HIT);
						
						log.append("Critical hit!\n");
						
						GameEventsDispatcher.fireGameNotification(new CriticalHitEvent(
							attackingFleet, defendingFleet, actionSlot, target));
					}
					else
					{
						if (Math.random() < criticalHitV2.getCriticalV2Chance1()) {
							damage *= criticalHitV2.getCriticalV2Multiplier1();
							reportDamage.addModifier(ReportDamage.CRITICAL_HIT);
							
							log.append("Critical hit!\n");
							
							GameEventsDispatcher.fireGameNotification(new CriticalHitEvent(
								attackingFleet, defendingFleet, actionSlot, target));
						}
					}
				}
				
				int protection = defendingShip.getProtection() +
					defendingSlotsState[target].getProtectionModifier();
				
				log.append("Base damage: ");
				log.append(damage);
				log.append("\n");
				
				// Capacité - sublimation
				if (attackingShip.hasAbility(Ability.TYPE_SUBLIMATION,
						attackingFleet.getOwner())) {
					Ability sublimation = attackingShip.getAbility(Ability.TYPE_SUBLIMATION);
					
					if (Math.random() < sublimation.getSublimationChances()) {
						reportDamage.addModifier(ReportDamage.SUBLIMATION);
						protection = Math.min(0, protection);
						log.append("Sublimation!\n");
					}
				}
				
				// Capacité - Faiblesse
				if (attackingShip.hasAbility(Ability.TYPE_WEAKNESS,
						attackingFleet.getOwner())) {
					Ability weakness = attackingShip.getAbility(Ability.TYPE_WEAKNESS);
					defendingSlotsState[target].multiplyDamage(weakness.getWeaknessDamageModifier());
				}
				
				// Capacité - sublimation V2
				if (attackingShip.hasAbility(Ability.TYPE_SUBLIMATION_V2,
						attackingFleet.getOwner())) {
					Ability sublimation = attackingShip.getAbility(Ability.TYPE_SUBLIMATION_V2);
					
					double chance1 = sublimation.getSublimationV2Chances1();
					double chance2 = sublimation.getSublimationV2Chances2();
					double value1 = sublimation.getSublimationV2Value1();
					double value2 = sublimation.getSublimationV2Value1();
					
					if (Math.random() < chance2) {
						reportDamage.addModifier(ReportDamage.SUBLIMATION);
						protection =  (int) Math.min(0, protection-Math.floor(value2*protection));
						log.append("Sublimation v2 1st try!\n");
					}
					else if (Math.random() < chance1)
					{
						reportDamage.addModifier(ReportDamage.SUBLIMATION);
						protection =  (int) Math.min(0, protection-Math.floor(value1*protection));
						log.append("Sublimation V2 2nd try!\n");
					}
				}
				
				// Capacité - malédiction
				if (!effectCurse && defendingShip.hasAbility(
						Ability.TYPE_CURSE, defendingFleet.getOwner())) {
					effectCurse = true;
					Ability curse = defendingShip.getAbility(Ability.TYPE_CURSE);
					
					int protectionModifier = attackingSlotsState[actionSlot].getProtectionModifier();
					attackingSlotsState[actionSlot].addProtectionModifier(
						curse.getCurseProtectionModifier());
					
					logProtectionModifier(log, attacker, attackingSlot,
						attackingSlotsDamage[actionSlot], protectionModifier,
						attackingSlotsState[actionSlot].getProtectionModifier());
				}
				
				// Capacité - immatériel
				if (defendingShip.hasAbility(Ability.TYPE_IMMATERIAL,
						defendingFleet.getOwner())) {
					Ability ability = defendingShip.getAbility(Ability.TYPE_IMMATERIAL);
					
					coef *= isAbility ?
						ability.getImmaterialAbilityDamageModifier() :
						ability.getImmaterialShotDamageModifier();
				}
				

				
				// Capacité - Absorbtion : augmente les PV lors des dégats reçus
				if (defendingShip.hasAbility(Ability.TYPE_ABSORBTION,
						defendingFleet.getOwner())) {
					Ability ability = defendingShip.getAbility(Ability.TYPE_ABSORBTION);
					if(damage>0){
						double sufferedDamage = ability.getAbsorbtionHullModifier();
						defendingSlotsState[target].multiplySufferedDamage(1/(1+
							sufferedDamage));
					}
				}
				
				// Capacité - terminator
				if (attackingShip.hasAbility(Ability.TYPE_TERMINATOR) &&
						reportAction.getActionIndex() == 14 &&
						attackingSlotsStartCount[actionSlot] ==
						attackingSlot.getCount()) {
					Ability ability = attackingShip.getAbility(Ability.TYPE_TERMINATOR);
					
					coef *= ability.getTerminatorDamageModifier();
				}
				
				damage = coef > 0 ? (coef * damage * damageFactor) - protection : 0;
				
				// Capacité - Projection de particules (dégats fixes)
				if (attackingSlotsState[actionSlot].getDamageMode() == SlotState.FIXED_DAMAGE) {
					if (weaponGroup.getIdWeapon() == Weapon.PPC) {
						damage = attackingSlotsState[actionSlot].getFixedDamageValue() *
							defendingSlotsState[target].getSufferedDamageMultiplier();
						reportDamage.addModifier(ReportDamage.PARTICLES);
					} else {
						damage = 0;
					}
				}
				
				log.append("Real damage: ");
				log.append(damage);
				log.append(" [coef x");
				log.append(coef);
				log.append(", protection ");
				log.append(protection);
				log.append("]\n");
				
				if (damage > 0) {
					// Total des dégats infligés
					long totalDamage = (long) Math.floor(damage * shotsLeft);
					
					// Capacité - Resistance globale
					if (defendingSlotsState[target].isAllResistanceActivated()) {
						double resistance = 
							defendingSlotsState[target].getAllResistanceValue();
						
						log.append("Total damage before global resistance: ");
						log.append((long) Math.floor(totalDamage));
						
						totalDamage *= resistance;
						
						log.append("\nTotal damage after global resistance: ");
						log.append((long) Math.floor(totalDamage));
						log.append("\n");
						
					}
					
					// Capacité - vengeance
					if (defendingSlotsState[target].isRetributionActivated()) {
						long maxReturnedDamage = defendingSlotsState[target].getRetributionMaxReturnedDamage();
						
						if (totalDamage <= maxReturnedDamage) {
							defendingSlotsState[target].addRetributionMaxReturnedDamage(-totalDamage);
							
							shotsLeft = 0;
							
							int losses = (int) Math.floor((totalDamage +
								attackingSlotsDamage[actionSlot]) / attackingShip.getHull());
							if (losses > attackingSlot.getCount())
								losses = (int) attackingSlot.getCount();
							long count = (long) Math.max(0,
									attackingSlot.getCount() - losses);
							
							attackingSlotsDamage[actionSlot] = count > 0 ?
								attackingShip.getHull() - (int) ((losses + 1) *
								attackingShip.getHull() - (totalDamage +
								attackingSlotsDamage[actionSlot])) : 0;
							attackingSlot.setCount(count);
							updateCountDependentAbilities(attackingFleet,
								attackingSlot, attackingSlotsState[actionSlot]);
							
							selfDamage.addDamage(totalDamage, losses,
								attackingSlotsDamage[actionSlot]);
							
							checkSlotsPosition(attackingSlots);
							
							log.append("Total damage: ");
							log.append((long) Math.floor(totalDamage));
							log.append("\nRetribution ships destroyed: ");
							log.append(losses);
							log.append("\n");
							
							if (count > 0)
								continue weapons;
							else
								break weapons;
						} else {
							totalDamage -= maxReturnedDamage;
							shotsLeft -= Math.ceil(maxReturnedDamage / damage);
							
							defendingSlotsState[target].setRetribution(0, 0);
							
							int losses = (int) Math.floor((maxReturnedDamage +
								attackingSlotsDamage[actionSlot]) / attackingShip.getHull());
							if (losses > attackingSlot.getCount())
								losses = (int) attackingSlot.getCount();
							long count = (long) Math.max(0,
									attackingSlot.getCount() - losses);
							
							attackingSlotsDamage[actionSlot] = count > 0 ?
								attackingShip.getHull() - (int) ((losses + 1) *
								attackingShip.getHull() - (maxReturnedDamage +
								attackingSlotsDamage[actionSlot])) : 0;
							attackingSlot.setCount(count);
							updateCountDependentAbilities(attackingFleet,
								attackingSlot, attackingSlotsState[actionSlot]);
							
							selfDamage.addDamage(maxReturnedDamage, losses,
								attackingSlotsDamage[actionSlot]);
							
							checkSlotsPosition(attackingSlots);
							
							log.append("Total damage: ");
							log.append((long) Math.floor(maxReturnedDamage));
							log.append("\nRetribution ships destroyed: ");
							log.append(losses);
							log.append("\n");
							
							if (count == 0)
								break weapons;
						}
					}
					
					

					
					
					
						
					// Capacité - fusion
					if (defendingSlotsState[target].isFusionActivated()) {
						if (defendingSlotsStartCount[target] >
								defendingSlot.getCount()) {
							log.append("Fusion: ");
							logSlot(log, !attacker, defendingSlot,
									defendingSlotsDamage[target]);
							
							int losses = (int) Math.floor((totalDamage +
								defendingSlotsDamage[target]) / defendingShip.getHull());
							if (losses > defendingSlot.getCount())
								totalDamage = defendingShip.getHull() * losses +
									defendingSlotsDamage[target];
							
							long damageLeeched = totalDamage;
							totalDamage -= defendingSlotsDamage[target];
							if (totalDamage < 0) {
								totalDamage = 0;
								defendingSlotsDamage[target] -= damageLeeched;
							} else {
								defendingSlotsDamage[target] = 0;
							}
							
							long oldCount = (long) defendingSlot.getCount();
							long newCount = oldCount +
								(long) Math.ceil(totalDamage / (double) defendingShip.getHull());
							
							if (newCount > defendingSlotsStartCount[target]) {
								newCount = defendingSlotsStartCount[target];
								defendingSlotsDamage[target] = 0;
							} else if (totalDamage > 0) {
								defendingSlotsDamage[target] = (int) (
									((newCount - oldCount) *
									defendingShip.getHull()) - totalDamage);
							}
							
							defendingSlot.setCount(newCount);
							updateCountDependentAbilities(defendingFleet,
								defendingSlot, defendingSlotsState[target]);
							
							log.append(" => ");
							logSlot(log, !attacker, defendingSlot,
									defendingSlotsDamage[target]);
							log.append(" [+");
							log.append(damageLeeched);
							log.append("]\n");
							
							// Sauvegarde la quantité regenérée en "dégâts négatifs"
							reportDamage.addDamage(-damageLeeched,
								(int) (oldCount - newCount),
								defendingSlotsDamage[target]);
						}
						
						continue weapons;
					}
					
					// Nombre de vaisseaux détruits
					int shipsDestroyed = (int) Math.floor(
						(totalDamage + defendingSlotsDamage[target]) /
						defendingShip.getHull());
					
					log.append("Total damage: ");
					log.append((long) Math.floor(totalDamage));
					log.append("\nShips destroyed: ");
					log.append(shipsDestroyed);
					log.append("\n");
					
					// Capacité - pénitence / charge de proximité //TODO log + séparer
					if (defendingSlotsState[target].getDamageReturn() > 0) {
						long realDamage;
						if (shipsDestroyed >= (long) defendingSlot.getCount())
							realDamage = (long) defendingSlot.getCount() *
								defendingShip.getHull() -
								defendingSlotsDamage[target];
						else
							realDamage = totalDamage;
						realDamage *= defendingSlotsState[target].getDamageReturn();
						
						int losses = (int) Math.floor(
							(realDamage + attackingSlotsDamage[actionSlot]) /
							attackingShip.getHull());
						if (losses > attackingSlot.getCount())
							losses = (int) attackingSlot.getCount();
						long count = (long) Math.max(
							0, attackingSlot.getCount() - losses);
						
						attackingSlotsDamage[actionSlot] = count > 0 ?
							attackingShip.getHull() -
							(int) ((losses + 1) * attackingShip.getHull() -
							(realDamage + attackingSlotsDamage[actionSlot])) : 0;
						attackingSlot.setCount(count);
						updateCountDependentAbilities(attackingFleet,
								attackingSlot, attackingSlotsState[actionSlot]);
						
						selfDamage.addDamage(realDamage, losses,
							attackingSlotsDamage[actionSlot]);
						
						
						checkSlotsPosition(attackingSlots);
						
						log.append("Penitence damage return: ");
						log.append((int) (defendingSlotsState[target].getDamageReturn() * 100));
						log.append("%\nPenitence ships destroyed: ");
						log.append(losses);
						log.append("\n");
						
					}
					
					
					
					
					// Capacité - pénitence passive
					if (defendingShip.hasAbility(Ability.TYPE_DAMAGE_RETURN_PASSIF)) {
						Ability ability = defendingShip.getAbility(
								Ability.TYPE_DAMAGE_RETURN_PASSIF);
						
						long realDamage;
						if (shipsDestroyed >= (long) defendingSlot.getCount())
							realDamage = (long) defendingSlot.getCount() *
								defendingShip.getHull() -
								defendingSlotsDamage[target];
						else
							realDamage = totalDamage;
						realDamage *= ability.getDamageRetunPassifValue();
						
						int losses = (int) Math.floor(
							(realDamage + attackingSlotsDamage[actionSlot]) /
							attackingShip.getHull());
						if (losses > attackingSlot.getCount())
							losses = (int) attackingSlot.getCount();
						long count = (long) Math.max(
							0, attackingSlot.getCount() - losses);
						
						attackingSlotsDamage[actionSlot] = count > 0 ?
							attackingShip.getHull() -
							(int) ((losses + 1) * attackingShip.getHull() -
							(realDamage + attackingSlotsDamage[actionSlot])) : 0;
						attackingSlot.setCount(count);
						updateCountDependentAbilities(attackingFleet,
								attackingSlot, attackingSlotsState[actionSlot]);
						
						selfDamage.addDamage(realDamage, losses,
							attackingSlotsDamage[actionSlot]);
						
						
						checkSlotsPosition(attackingSlots);
						
						log.append("Penitence passive damage return");
						log.append("\nPenitence ships destroyed: ");
						log.append(losses);
						log.append("\n");
						
					}
					
					// Dégats effectivement infligés, limités par le nombre
					// de vaisseaux ennemis
					if (shipsDestroyed >= (long) defendingSlot.getCount()) {
						shipsDestroyed = (int) defendingSlot.getCount();
						long realDamage = (long) defendingSlot.getCount() *
							defendingShip.getHull() - defendingSlotsDamage[target];
						
						reportDamage.addDamage(realDamage, shipsDestroyed, 0);
						
						shotsLeft = (long) Math.floor(
								(totalDamage - realDamage) / damage);
						
						defendingSlotsDamage[target] = 0;
						defendingSlot.setCount(0);
						
						// Vol de ressources
						stealResources(attackingFleet, attackingShip,
							attackingFleetResources, defendingFleet,
							defendingShip, defendingFleetResources,
							shipsDestroyed, reportDamage);
						
						// Capacité - détonation
						if (defendingShip.hasAbility(Ability.TYPE_DETONATION,
								defendingFleet.getOwner())) {
							Ability detonation = defendingShip.getAbility(Ability.TYPE_DETONATION);
							double before = attackingSlotsState[actionSlot].getSufferedDamageMultiplier();
							attackingSlotsState[actionSlot].multiplySufferedDamage(1 / detonation.getDetonationHullModifier());
							
							log.append("Detonation: ");
							logDamageMultiplier(log, attacker, attackingSlot,
								attackingSlotsDamage[actionSlot], before,
								attackingSlotsState[actionSlot].getSufferedDamageMultiplier(),
								attackingSlotsState[actionSlot].getSufferedDamageMultiplier(), true);
						}
						
						
						if (attackingShip.hasAbility(
								Ability.TYPE_COLLATERAL, attackingFleet.getOwner())) {
							Ability collateral = attackingShip.getAbility(Ability.TYPE_COLLATERAL);
							
							for (int i = 0; i < defendingSlots.length; i++) {
								if(defendingSlots[i].getShip()!=null){
									int before = defendingSlotsState[i].getProtectionModifier();
									defendingSlotsState[i].addProtectionModifier(
											-collateral.getCollateralProtectionModifier());
									
									logProtectionModifier(log, !attacker, defendingSlot,
											defendingSlotsDamage[target], before,
											defendingSlotsState[target].getProtectionModifier());
								}
							}
							
							
						}
						
						// Capacité - débordement
						if (attackingShip.hasAbility(Ability.TYPE_OUTFLANKING,
								attackingFleet.getOwner()) &&
								(attackingSlotsDamage[actionSlot] > 0 ||
								attackingSlot.getCount() <
								attackingSlotsStartCount[actionSlot])) {
							// Compte le nombre initial de vaisseaux sur le slot détruit
							long kills = 0;
							
							if (report.getReportActions() != null) {
								for (ReportAction oldReportAction : report.getReportActions()) {
									if (oldReportAction.getReportDamages() != null) {
										for (ReportDamage oldReportDamage : oldReportAction.getReportDamages()) {
											if (oldReportDamage.getTargetPosition() ==
													target + defendingFleetSlotOffset)
												kills += oldReportDamage.getKills();
										}
									}
								}
							}
							// Redonne les points de vie au vaisseau
							log.append("Outflanking: ");
							logSlot(log, attacker, attackingSlot,
									attackingSlotsDamage[actionSlot]);
							
							Ability ability = attackingShip.getAbility(Ability.TYPE_OUTFLANKING);
							long leeched = (long) Math.floor(kills *
								defendingShip.getHull() *
								ability.getOutflankingLeechedValue());
							
							long damageLeeched = leeched;
							leeched -= attackingSlotsDamage[actionSlot];
							if (leeched < 0) {
								leeched = 0;
								attackingSlotsDamage[actionSlot] -= damageLeeched;
							} else {
								attackingSlotsDamage[actionSlot] = 0;
							}
							
							long oldCount = (long) attackingSlot.getCount();
							long newCount = oldCount +
								(long) Math.ceil(leeched / (double) attackingShip.getHull());
							
							if (newCount > attackingSlotsStartCount[actionSlot]) {
								newCount = attackingSlotsStartCount[actionSlot];
								attackingSlotsDamage[actionSlot] = 0;
							} else if (leeched > 0) {
								attackingSlotsDamage[actionSlot] = (int) (
									((newCount - oldCount) * attackingShip.getHull()) - leeched);
							}
							
							attackingSlot.setCount(newCount);
							updateCountDependentAbilities(attackingFleet,
									attackingSlot, attackingSlotsState[actionSlot]);
							
							log.append(" => ");
							logSlot(log, attacker, attackingSlot,
									attackingSlotsDamage[actionSlot]);
							log.append(" [+");
							log.append(damageLeeched);
							log.append("]\n");
							
							// Sauvegarde la quantité regenérée en "dégâts négatifs"
							selfDamage.addDamage(-damageLeeched,
								(int) (oldCount - newCount),
								attackingSlotsDamage[actionSlot]);
						}
						
						checkSlotsPosition(defendingSlots);
						
						if (attackingSlot.getCount() == 0)
							break weapons;
						
						if (attackingSlotsState[actionSlot].getLockedTarget() == -1 ||
								attackingSlotsState[actionSlot].areOtherTargetsAllowed()) {
							attackingSlotsState[actionSlot].setLockedTarget(-1, true);
							
							// Recherche une cible dans la flotte ennemie
							target = getShipTarget(attackingShip, defendingFleet,
									defendingSlots, defendingSlotsState);
							
							effectHarassment = false;
							effectHeat = false;
							
							if (target == -1) {
								shotsLeft = 0;
								break weapons;
							} else {
								defendingSlot = defendingSlots[target];
								defendingShip = defendingSlot.getShip();
								
								log.append("Shots: ");
								log.append(shotsLeft);
								log.append("x shots ");
								log.append("\nTarget: ");
								logSlot(log, !attacker, defendingSlot,
										defendingSlotsDamage[target]);
								log.append("\n");
								
								reportDamage = new ReportDamage(
									target + defendingFleetSlotOffset, 0, 0,
									defendingSlotsDamage[target]);
								reportAction.addReportDamage(reportDamage);
							}
						} else {
							shotsLeft = 0;
							break weapons;
						}
					} else {
						defendingSlotsDamage[target] = defendingShip.getHull() -
 							(int) ((shipsDestroyed + 1) * defendingShip.getHull() -
 							(totalDamage + defendingSlotsDamage[target]));
						defendingSlot.addCount(-shipsDestroyed);
						shotsLeft = 0;
						
						updateCountDependentAbilities(defendingFleet,
								defendingSlot, defendingSlotsState[target]);
						
						// Vol de ressources
						stealResources(attackingFleet, attackingShip,
							attackingFleetResources, defendingFleet,
							defendingShip, defendingFleetResources,
							shipsDestroyed, reportDamage);
						
						reportDamage.addDamage(totalDamage,
							shipsDestroyed, defendingSlotsDamage[target]);
						
						if (attackingSlot.getCount() == 0)
							break weapons;
					}
				} else {
					shotsLeft = 0;
				}
			} while (shotsLeft > 0);
		}
		
		// Capacité - contre-mesures
		if (defendingSlot.getCount() > 0 &&
				defendingShip.hasAbility(Ability.TYPE_ECM,
					defendingFleet.getOwner()) &&
				reportDamage.getKills() > 0) {
			Ability ability = defendingShip.getAbility(Ability.TYPE_ECM);
			int maxLosses = (int) Math.floor((defendingSlot.getCount() +
				reportDamage.getKills()) * ability.getEcmMaxLosses());
			
			if (reportDamage.getKills() > maxLosses) {
				log.append("ECM: ");
				logSlot(log, attacker, defendingSlot, defendingSlotsDamage[target]);
				log.append(" => ");
				
				defendingSlot.setCount(defendingSlot.getCount() +
					reportDamage.getKills() - maxLosses);
				reportDamage.setKills(maxLosses);
				updateCountDependentAbilities(defendingFleet,
						defendingSlot, defendingSlotsState[target]);
				
				// Vol de ressources
				stealResources(attackingFleet, attackingShip,
					attackingFleetResources, defendingFleet,
					defendingShip, defendingFleetResources,
					maxLosses, reportDamage);
				
				logSlot(log, attacker, defendingSlot, defendingSlotsDamage[target]);
				log.append("\n");
			}
		}
		

		// Capacité - Canalisation : si le vaisseau subit trop de gétas, il gagne du bouclier
		if (defendingSlot.getCount() > 0 &&
				defendingShip.hasAbility(Ability.TYPE_CANALISE,
						defendingFleet.getOwner())) {
			Ability ability = defendingShip.getAbility(Ability.TYPE_CANALISE);

			double percentKill = ability.getCalanisePercentKill();
			if(defendingSlot.getCount()*percentKill<reportDamage.getKills())
				defendingSlotsState[target].addProtectionModifier(
						ability.getCalaniseProtectionBonus());

		}
		
		
		checkSlotsPosition(attackingSlots);
		checkSlotsPosition(defendingSlots);
		
		attackingSlotsState[actionSlot].resetNextDamageMultiplier();
		
		// Leech - récupération de vaisseaux en fonction des dégâts infligés
		double leechValue = attackingSlotsState[actionSlot].getLeech();
		
		if (attackingShip.hasAbility(Ability.TYPE_PASSIVE_LEECH,
				attackingFleet.getOwner())) {
			leechValue += attackingShip.getAbility(
					Ability.TYPE_PASSIVE_LEECH).getPassiveLeechValue();
		}
		
		if (leechValue > 0) {
			if (attackingSlotsStartCount[actionSlot] > attackingSlot.getCount()) {
				log.append("Leech: ");
				logSlot(log, attacker, attackingSlot,
						attackingSlotsDamage[actionSlot]);
				
				int damage = 0;
				
				for (ReportDamage tmpReportDamage : reportAction.getReportDamages())
					damage += tmpReportDamage.getDamage();
				
				long leeched = (long) Math.floor(damage * leechValue);
				
				long damageLeeched = leeched;
				leeched -= attackingSlotsDamage[actionSlot];
				if (leeched < 0) {
					leeched = 0;
					attackingSlotsDamage[actionSlot] -= damageLeeched;
				} else {
					attackingSlotsDamage[actionSlot] = 0;
				}
				
				long oldCount = (long) attackingSlot.getCount();
				long newCount = oldCount +
					(long) Math.ceil(leeched / (double) attackingShip.getHull());
				
				if (newCount > attackingSlotsStartCount[actionSlot]) {
					newCount = attackingSlotsStartCount[actionSlot];
					attackingSlotsDamage[actionSlot] = 0;
				} else if (leeched > 0) {
					attackingSlotsDamage[actionSlot] = (int) (
						((newCount - oldCount) * attackingShip.getHull()) - leeched);
				}
				
				attackingSlot.setCount(newCount);
				updateCountDependentAbilities(attackingFleet,
						attackingSlot, attackingSlotsState[actionSlot]);
				
				log.append(" => ");
				logSlot(log, attacker, attackingSlot,
						attackingSlotsDamage[actionSlot]);
				log.append(" [+");
				log.append(damageLeeched);
				log.append("]\n");
				
				// Sauvegarde la quantité regenérée en "dégâts négatifs"
				selfDamage.addDamage(-damageLeeched,
					(int) (oldCount - newCount), attackingSlotsDamage[actionSlot]);
			}
		}
		
		if (newSelfDamage && selfDamage.getDamage() != 0)
			reportAction.addReportDamage(selfDamage);
	}
	
	// Renvoie true si tous les slots de la flotte sont vides
	private static boolean isEmpty(Slot[] slots) {
		for (Slot slot : slots)
			if (slot.getId() != 0)
				return false;
		
		return true;
	}
	
	private static void stealResources(Fleet attackingFleet, Ship attackingShip,
			double[] attackingFleetResources, Fleet defendingFleet,
			Ship defendingShip, double[] defendingFleetResources, int losses,
			ReportDamage reportDamage) {
		double payload = defendingShip.getPayload();
		
		double stealCoef = 0;
		if (attackingFleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)
			stealCoef = Skill.SKILL_PIRATE_EFFECT[attackingFleet.getSkillLevel(Skill.SKILL_PIRATE)];
		
		if (payload > 0 && losses > 0 && stealCoef > 0) {
			for (int j = 0; j < GameConstants.RESOURCES_COUNT; j++) {
				double stolenResources = getResourcePerPayload(
					defendingFleet, defendingFleetResources, j) *
					payload * losses * stealCoef;
				defendingFleetResources[j] -= stolenResources;
				attackingFleetResources[j] += stolenResources;
				reportDamage.setStealedResource(stolenResources, j);
			}
		}
	}
	
	// Renvoie un coefficient qui indique la quantité de ressources présente
	// dans la flotte pour 1 point de capacité
	private static double getResourcePerPayload(Fleet fleet, double[] fleetResources, int type) {
		return fleetResources[type] / fleet.getPayload();
	}
	
	private static int getShipTarget(Ship attackingShip, Fleet defendingFleet,
			Slot[] defendingSlots, SlotState[] defendingSlotsState) {
		// Taunt - force l'attaque
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			if (defendingSlots[i].getId() != 0 &&
					defendingSlots[i].isFront() &&
					defendingSlotsState[i].isTauntActivated()) {
				return i;
			}
		}
		
		// Sélection d'un slot parmi les slots qui sont sur la ligne de front
		// et qui correspondent aux cibles prioritaire du vaisseau qui attaque
		if (attackingShip.getTargets().length > 0) {
			int targetableSlotsCount = 0;
			int[] targetableSlotsIndex = new int[GameConstants.FLEET_SLOT_COUNT];
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
				if (defendingSlots[i].getId() != 0 &&
					defendingSlots[i].isFront() &&
					ArrayUtils.contains(attackingShip.getTargets(),
							defendingSlots[i].getShip().getShipClass())) {
					targetableSlotsIndex[targetableSlotsCount] = i;
					targetableSlotsCount++;
				}
			
			if (targetableSlotsCount > 0)
				return targetableSlotsIndex[(int) Math.floor(
						Math.random() * targetableSlotsCount)];
		}
		
		// Sélection aléatoire d'un slot parmi les slots de la ligne de front,
		// en excluant les cargos
		int targetableSlotsCount = 0;
		int[] targetableSlotsIndex = new int[GameConstants.FLEET_SLOT_COUNT];
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			if (defendingSlots[i].getId() != 0 &&
				defendingSlots[i].isFront() &&
				defendingSlots[i].getShip().getShipClass() != Ship.FREIGHTER) {
				targetableSlotsIndex[targetableSlotsCount] = i;
				targetableSlotsCount++;
			}
		
		if (targetableSlotsCount > 0)
			return targetableSlotsIndex[(int) Math.floor(
					Math.random() * targetableSlotsCount)];
		
		// Sélection aléatoire d'un slot parmi les slots de la ligne de front,
		// avec les cargos
		targetableSlotsCount = 0;
		targetableSlotsIndex = new int[GameConstants.FLEET_SLOT_COUNT];
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			if (defendingSlots[i].getId() != 0 &&
				defendingSlots[i].isFront()) {
				targetableSlotsIndex[targetableSlotsCount] = i;
				targetableSlotsCount++;
			}
		
		if (targetableSlotsCount > 0)
			return targetableSlotsIndex[(int) Math.floor(
					Math.random() * targetableSlotsCount)];
		
		// Aucune cible n'a été trouvé
		return -1;
	}
	
	private static void applyLosses(Fleet fleet, Slot[] slots,
			Fleet linkedFleet, long[] linkedShipsCount, Slot[] savedSlots,
			boolean[] savedSlotsFront) {
		// % de vaisseaux fournis par chaque flotte
		double[] lossesRatios = new double[GameConstants.FLEET_SLOT_COUNT];
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			if (fleet.getSlot(i).getCount() > 0)
				if (savedSlots != null)
					lossesRatios[i] = 1 - (slots[i].getCount() / (fleet.getSlot(i).getCount() - savedSlots[i].getCount()));
				else
					lossesRatios[i] = 1 - (slots[i].getCount() / fleet.getSlot(i).getCount());
		
		// Applique les dégâts sur la flotte liée offensivement / défensivement
		if (linkedFleet != null) {
			int linkedFleetRepairLevel = linkedFleet.getSkillLevel(Skill.SKILL_REPAIR);
			
			for (int i = 0; i < slots.length; i++) {
				if (fleet.getSlot(i).getCount() > 0) {
					// Supprime les vaisseaux perdus de la flotte liée
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
						if (linkedFleet.getSlot(j).getId() == fleet.getSlot(i).getId()) {
							// Calcule le % de vaisseaux détruits
							double losses;
							if (savedSlots != null)
								losses = 1 - (slots[i].getCount() / (
									(long) fleet.getSlot(i).getCount() + linkedShipsCount[j] -
									savedSlots[i].getCount()));
							else
								losses = 1 - (slots[i].getCount() / (
									(long) fleet.getSlot(i).getCount() + linkedShipsCount[j]));
							lossesRatios[i] = losses;
							
							// Répare les vaisseaux s'il en reste encore sur le slot
							if (linkedFleetRepairLevel != -1) {
								// % de vaisseaux réparés
								double repaired =
									Skill.SKILL_REPAIR_MIN[linkedFleetRepairLevel] +
									Math.random() * (
									Skill.SKILL_REPAIR_MAX[linkedFleetRepairLevel] -
									Skill.SKILL_REPAIR_MIN[linkedFleetRepairLevel]);
								
								losses -= losses * repaired;
								if (losses < 0)
									losses = 0;
							}
							
							// Applique les pertes
							if (losses > 0) {
								Slot slot = linkedFleet.getSlot(j);
								slot.addCount(-Math.floor(losses * linkedShipsCount[j]));
								linkedFleet.setSlot(slot, j);
							}
							break;
						}
				}
			}
		}
		
		// Applique les dégâts sur la flotte
		int repairLevel = fleet.getSkillLevel(Skill.SKILL_REPAIR);
		
		for (int i = 0; i < slots.length; i++) {
			if (fleet.getSlot(i).getCount() > 0) {
				// Calcule le % de vaisseaux détruits
				double losses = lossesRatios[i];
				
				// Répare les vaisseaux s'il en reste encore sur le slot
				if (repairLevel != -1 && (long) slots[i].getCount() > 0) {
					// % de vaisseaux réparés
					double repaired =
							Skill.SKILL_REPAIR_MIN[repairLevel] +
							Math.random() * (
							Skill.SKILL_REPAIR_MAX[repairLevel] -
							Skill.SKILL_REPAIR_MIN[repairLevel]);
					
					losses -= losses * repaired;
					if (losses < 0)
						losses = 0;
				}
				
				// Applique les pertes
				if (losses == 1) {
					if (savedSlots != null)
						fleet.setSlot(savedSlots[i], i);
					else
						fleet.setSlot(new Slot(), i);
				} else if (losses > 0) {
					Slot slot = fleet.getSlot(i);
					if (savedSlots != null)
						slot.addCount(-Math.ceil(losses * (slot.getCount() - savedSlots[i].getCount())));
					else
						slot.addCount(-Math.ceil(losses * slot.getCount()));
					fleet.setSlot(slot, i);
				}
			}
		}
		
		// Essaye de rétablir les slots sauvegardés à l'arrière
		for (int i = 0; i < slots.length; i++)
			if (savedSlotsFront != null)
				fleet.setSlotFront(savedSlotsFront[i], i);
			else
				fleet.setSlotFront(slots[i].isFront(), i);
		
		slots = fleet.getSlots();
		checkSlotsPosition(slots);
		fleet.setSlots(slots);
	}
	
	private static int getBattleXp(Slot[] before, Slot[] after) {
		int xp = 0;
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			if (before[i].getId() != 0)
				xp += ((long) before[i].getCount() - (long) after[i].getCount()) *
						before[i].getShip().getPower();
		
		return (int) Math.round(xp * GameConstants.XP_SHIP_DESTROYED);
	}
	
	
	
	private static void checkSlotsPosition(Slot[] slots) {
		// Déplace un slot sur la ligne de front s'il y a en trop à  l'arrière
		int frontCount = 0, backCount = 0;
		for (int j = 0; j < slots.length; j++)
			if (slots[j].getId() != 0)
				if (slots[j].isFront())
					frontCount++;
				else
					backCount++;
		
		loop:while (backCount > frontCount) {
			for (int j = 0; j < slots.length; j++)
				if (slots[j].getId() != 0 &&
						!slots[j].isFront() &&
						slots[j].getShip().getShipClass() != Ship.FREIGHTER) {
					slots[j].setFront(true);
					backCount--;
					frontCount++;
					continue loop;
				}
			
			for (int j = 0; j < slots.length; j++)
				if (slots[j].getId() != 0 && !slots[j].isFront()) {
					slots[j].setFront(true);
					backCount--;
					frontCount++;
					continue loop;
				}
		}
	}
	
	
	private static void updateCountDependentAbilities(Fleet fleet, Slot slot,
			SlotState slotsState) {
		// Capacités - surcharge & erreur quantique
		if (slot.getCount() > 0 && slot.getShip().hasAbility(
				Ability.TYPE_OVERLOAD, fleet.getOwner())) {
			Ability ability = slot.getShip().getAbility(Ability.TYPE_OVERLOAD);
			
			if ((slot.getCount() % ability.getOverloadDivisor()) == 0)
				slotsState.setOverloadProtectionModifier(
					ability.getOverloadProtectionModifier());
			else
				slotsState.setOverloadProtectionModifier(0);
		}
		
		if (slot.getCount() > 0 && slot.getShip().hasAbility(
				Ability.TYPE_QUANTUM_ERROR, fleet.getOwner())) {
			Ability ability = slot.getShip().getAbility(Ability.TYPE_QUANTUM_ERROR);
			
			if ((slot.getCount() % ability.getQuantumErrorDivisor()) == 0)
				slotsState.setQuantumErrorDamageMultiplier(
					ability.getQuantumErrorDamageModifier());
			else
				slotsState.setQuantumErrorDamageMultiplier(1);
		}
	}
	
	private static void updateRoundDependantAbilities(Fleet fleet, Slot[] slots,
			SlotState[] slotsState, int round) {
		round = round + 1; // Les rounds commencent à 1
		
		// Capacité - incohésion
		for (int i = 0; i < slots.length; i++) {
			Ship ship = slots[i].getShip();
			if (ship != null && ship.hasAbility(Ability.TYPE_INCOHESION, fleet.getOwner())) {
				Ability incohesion = ship.getAbility(Ability.TYPE_INCOHESION);
				
				slotsState[i].setIncohesionDamageModifier(round % 2 == 0 ?
					incohesion.getIncohesionEvenRoundDamageModifier() :
					incohesion.getIncohesionOddRoundDamageModifier());
				slotsState[i].setIncohesionProtectionModifier(round % 2 == 0 ?
					incohesion.getIncohesionEvenRoundProtectionModifier() :
					incohesion.getIncohesionOddRoundProtectionModifier());
			}
		}
	}
	
	// Helpers pour logger des informations
	private static void logNextDamageMultiplier(StringBuffer log, boolean attacker,
			Slot slot, int slotDamage, double before, double after, double total) {
		logSlot(log, attacker, slot, slotDamage);
		log.append(" next damage multiplier: ");
		log.append((int) Math.round(100 * before));
		log.append("% > ");
		log.append((int) Math.round(100 * after));
		log.append("% [total damage multiplier: ");
		log.append((int) Math.round(100 * total));
		log.append("%]\n");
	}
	
	private static void logDamageMultiplier(StringBuffer log, boolean attacker,
			Slot slot, int slotDamage, double before, double after,
			double total, boolean sufferedDamage) {
		logSlot(log, attacker, slot, slotDamage);
		log.append((sufferedDamage ? " suffered" : "") + " damage multiplier: ");
		log.append((int) Math.round(100 * before));
		log.append("% > ");
		log.append((int) Math.round(100 * after));
		log.append("% [total damage multiplier: ");
		log.append((int) Math.round(100 * total));
		log.append("%]\n");
	}
	
	private static void logSlot(StringBuffer log, boolean attacker, Slot slot,
			int slotDamage) {
		if (slot.getCount() == 0) {
			log.append("0");
		} else {
			log.append(attacker ? "[ATT] " : "[DEF] ");
			log.append((long) slot.getCount());
			log.append("x ");
			log.append(Messages.getString("ships" + slot.getId()));
			log.append(slot.isFront() ? " ^" : " v");
			if (slotDamage > 0) {
				int hull = slot.getShip().getHull();
				log.append(" [");
				log.append(hull - slotDamage);
				log.append("/");
				log.append(hull);
				log.append("]");
			}
		}
	}
	
	private static void logFleetOwner(StringBuffer log, Fleet fleet) {
		log.append("Player [id=");
		log.append(fleet.getOwner().getId());
		log.append(",login=");
		log.append(fleet.getOwner().getLogin());
		log.append("], fleet [id=");
		log.append(fleet.getId());
		log.append(",name=");
		log.append(fleet.getName());
		log.append("]\n");
	}
	
	private static void logProtectionModifier(StringBuffer log, boolean attacker,
			Slot slot, int slotDamage, int before, int after) {
		logSlot(log, attacker, slot, slotDamage);
		log.append(" protection modifier: ");
		log.append(before);
		log.append(" > ");
		log.append(after);
		log.append("\n");
	}
}
