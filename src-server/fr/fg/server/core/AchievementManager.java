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

package fr.fg.server.core;

import java.util.List;

import fr.fg.server.data.Achievement;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterBattleEvent;
import fr.fg.server.events.impl.AfterDoodadPickedUpEvent;
import fr.fg.server.events.impl.AfterEmpEvent;
import fr.fg.server.events.impl.AfterMiningEvent;
import fr.fg.server.events.impl.AfterSpaceStationFund;
import fr.fg.server.events.impl.AfterSwapEvent;
import fr.fg.server.events.impl.AfterTradeEvent;
import fr.fg.server.events.impl.AfterTrainingEvent;
import fr.fg.server.events.impl.AfterWardBuiltEvent;
import fr.fg.server.events.impl.AlienDoodadEvent;
import fr.fg.server.events.impl.BlackHoleLossesEvent;
import fr.fg.server.events.impl.BuiltShipsEvent;
import fr.fg.server.events.impl.ContractStateUpdateEvent;
import fr.fg.server.events.impl.CriticalHitEvent;
import fr.fg.server.events.impl.DodgeEvent;
import fr.fg.server.events.impl.JumpFinishedEvent;
import fr.fg.server.events.impl.RetreatEvent;
import fr.fg.server.events.impl.StolenResourcesEvent;

public class AchievementManager implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static AchievementManager instance = new AchievementManager();
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private AchievementManager() {
		// Pas d'attributs
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof AfterBattleEvent) {
			// Flottes détruites en combat
			AfterBattleEvent gameEvent = (AfterBattleEvent) event;
			
			if (gameEvent.isAttackingFleetDestroyed()) {
				addScore(gameEvent.getDefendingFleetBefore().getOwner(),
						Achievement.TYPE_FLEETS_DESTOYED, 1);
				addScore(gameEvent.getAttackingFleetBefore().getOwner(),
						Achievement.TYPE_FLEETS_LOST, 1);
			}
			
			if (gameEvent.isDefendingFleetDestroyed()) {
				addScore(gameEvent.getAttackingFleetBefore().getOwner(),
						Achievement.TYPE_FLEETS_DESTOYED, 1);
				addScore(gameEvent.getDefendingFleetBefore().getOwner(),
						Achievement.TYPE_FLEETS_LOST, 1);
			}
		} else if (event instanceof AfterSwapEvent) {
			// Echange de position
			AfterSwapEvent gameEvent = (AfterSwapEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_SWAPS, 1);
		} else if (event instanceof AfterMiningEvent) {
			// Mineur
			AfterMiningEvent gameEvent = (AfterMiningEvent) event;
			
			addScore(gameEvent.getSource().getOwner(),
				Achievement.TYPE_MINED_RESOURCES, gameEvent.getQuantity());
		} else if (event instanceof AfterEmpEvent) {
			// EMP
			AfterEmpEvent gameEvent = (AfterEmpEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_EMP, 1);
		} else if (event instanceof AfterWardBuiltEvent) {
			// Construction de balise
			AfterWardBuiltEvent gameEvent = (AfterWardBuiltEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_BUILT_WARDS, 1);
		} else if (event instanceof AfterDoodadPickedUpEvent) {
			// Ramassage de bidules
			AfterDoodadPickedUpEvent gameEvent = (AfterDoodadPickedUpEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_PICKED_DOODADS, 1);
		} else if (event instanceof AfterTrainingEvent) {
			// Entrainement de flotte
			AfterTrainingEvent gameEvent = (AfterTrainingEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_TRAININGS, 1);
		} else if (event instanceof CriticalHitEvent) {
			// Coup critique
			CriticalHitEvent gameEvent = (CriticalHitEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_CRITICAL_HITS, 1);
		} else if (event instanceof DodgeEvent) {
			// Esquive
			DodgeEvent gameEvent = (DodgeEvent) event;
			
			addScore(gameEvent.getAttacker().getOwner(), Achievement.TYPE_DODGES, 1);
		} else if (event instanceof AfterTradeEvent) {
			// Commerce
			AfterTradeEvent gameEvent = (AfterTradeEvent) event;
			
			addScore(gameEvent.getSource().getOwner(),
				Achievement.TYPE_TRADED_RESOURCES, gameEvent.getQuantity());
		} else if (event instanceof BlackHoleLossesEvent) {
			// Pertes dans un trou noir
			BlackHoleLossesEvent gameEvent = (BlackHoleLossesEvent) event;
			
			addScore(gameEvent.getSource().getOwner(),
				Achievement.TYPE_BLACKHOLES_LOSSES, gameEvent.getLossesValue());
		} else if (event instanceof JumpFinishedEvent) {
			// Saut HE terminé
			JumpFinishedEvent gameEvent = (JumpFinishedEvent) event;
			
			addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_HYPERJUMPS, 1);
		} else if (event instanceof BuiltShipsEvent) {
			// Vaisseaux construits
			BuiltShipsEvent gameEvent = (BuiltShipsEvent) event;
			
			if (gameEvent.getSource().getIdOwner() != 0 &&
					gameEvent.getShipId() != 0 && gameEvent.getCount() > 0)
				addScore(gameEvent.getSource().getOwner(), Achievement.TYPE_BUILT_SHIPS,
					Ship.SHIPS[gameEvent.getShipId()].getPower() * gameEvent.getCount());
		} else if (event instanceof StolenResourcesEvent) {
			// Vol de ressources
			StolenResourcesEvent gameEvent = (StolenResourcesEvent) event;
			
			long total = 0;
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				total += gameEvent.getStolenResources()[i];
			
			addScore(gameEvent.getSource().getOwner(),
					Achievement.TYPE_STOLEN_RESOURCES, total);
		} else if (event instanceof AfterSpaceStationFund) {
			// Transfert de ressources sur une station
			AfterSpaceStationFund gameEvent = (AfterSpaceStationFund) event;
			
			long total = gameEvent.getCredits();
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				total += gameEvent.getResources()[i];
			
			addScore(gameEvent.getPlayer(),
					Achievement.TYPE_SPACE_STATION_FUNDS, total);
		} else if (event instanceof AlienDoodadEvent) {
			// Aliens
			AlienDoodadEvent gameEvent = (AlienDoodadEvent) event;
			
			addScore(gameEvent.getFleet().getOwner(), Achievement.TYPE_ALIENS, 1);
		} else if (event instanceof RetreatEvent) {
			// Replis
			RetreatEvent gameEvent = (RetreatEvent) event;
			
			addScore(gameEvent.getDefendingFleet().getOwner(), Achievement.TYPE_RETREATS, 1);
		} else if (event instanceof ContractStateUpdateEvent) {
			// Missions réussies
			ContractStateUpdateEvent gameEvent = (ContractStateUpdateEvent) event;
			
			if (gameEvent.getNewState() == Contract.STATE_FINALIZING) {
				Contract contract = gameEvent.getContract();
				if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
					List<ContractAttendee> attendees = contract.getAttendees();
					synchronized (attendees) {
						for (ContractAttendee attendee : attendees) {
							Player player = attendee.getPlayer();
							addScore(player, Achievement.TYPE_MISSIONS, 1);
						}
					}
				} else if (contract.getType().equals(Contract.TARGET_ALLY)) {
					List<ContractAttendee> attendees = contract.getAttendees();
					synchronized (attendees) {
						for (ContractAttendee attendee : attendees) {
							List<Player> members = attendee.getAlly().getMembers();
							synchronized (members) {
								for (Player member : members)
									addScore(member, Achievement.TYPE_MISSIONS, 1);
							}
						}
					}
				}
			}
		}
	}
	
	public static void start() {
		GameEventsDispatcher.addGameEventListener(instance,
			AfterBattleEvent.class,
			AfterSwapEvent.class,
			AfterMiningEvent.class,
			AfterEmpEvent.class,
			AfterWardBuiltEvent.class,
			AfterDoodadPickedUpEvent.class,
			AfterTrainingEvent.class,
			CriticalHitEvent.class,
			DodgeEvent.class,
			AfterTradeEvent.class,
			BlackHoleLossesEvent.class,
			JumpFinishedEvent.class,
			BuiltShipsEvent.class,
			StolenResourcesEvent.class,
			AfterSpaceStationFund.class,
			AlienDoodadEvent.class,
			RetreatEvent.class,
			ContractStateUpdateEvent.class);
	}
	
	public static void stop() {
		GameEventsDispatcher.removeGameEventListener(instance,
			AfterBattleEvent.class,
			AfterSwapEvent.class,
			AfterMiningEvent.class,
			AfterEmpEvent.class,
			AfterWardBuiltEvent.class,
			AfterDoodadPickedUpEvent.class,
			AfterTrainingEvent.class,
			CriticalHitEvent.class,
			DodgeEvent.class,
			AfterTradeEvent.class,
			BlackHoleLossesEvent.class,
			JumpFinishedEvent.class,
			BuiltShipsEvent.class,
			StolenResourcesEvent.class,
			AfterSpaceStationFund.class,
			AlienDoodadEvent.class,
			RetreatEvent.class,
			ContractStateUpdateEvent.class);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void addScore(Player player, int type, long increment) {
		if (player == null || player.isAi())
			return;
		
		List<Achievement> achievements = player.getAchievements();
		
		synchronized (achievements) {
			for (Achievement achievement : achievements)
				if (achievement.getType() == type) {
					synchronized (achievement.getLock()) {
						achievement = DataAccess.getEditable(achievement);
						achievement.addScore(increment);
						achievement.save();
					}
					
					updateAchievementsAchievement(player);
					player.updateAchievementsCache();
					return;
				}
		}
		
		Achievement achievement = new Achievement(player.getId(), type, increment);
		achievement.save();
		
		updateAchievementsAchievement(player);
		player.updateAchievementsCache();
	}
	
	private void updateAchievementsAchievement(Player player) {
		int score = 0;
		List<Achievement> achievements = player.getAchievements();
		
		synchronized (achievements) {
			for (Achievement achievement : achievements) {
				score += achievement.getLevel();
			}
			
			for (Achievement achievement : achievements) {
				if (achievement.getType() == Achievement.TYPE_ACHIEVEMENTS) {
					synchronized (achievement.getLock()) {
						achievement = DataAccess.getEditable(achievement);
						achievement.setScore(score);
						achievement.save();
					}
					return;
				}
			}
			
			Achievement achievement = new Achievement(
				player.getId(), Achievement.TYPE_ACHIEVEMENTS, score);
			achievement.save();
		}
	}
}
