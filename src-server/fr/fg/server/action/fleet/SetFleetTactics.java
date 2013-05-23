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

package fr.fg.server.action.fleet;

import java.util.List;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ability;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;
import fr.fg.server.data.Ship;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.LoggingSystem;

public class SetFleetTactics extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Paramètres de l'action
		int idFleet = (Integer) params.get("id");
		
		boolean[] frontSlots = new boolean[GameConstants.FLEET_SLOT_COUNT];
		for (int i = 0; i < frontSlots.length; i++)
			frontSlots[i] = (Boolean) params.get("slot" + i + "_front");
		
		int[] skirmishActionsSlots = new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		int[] skirmishActionsAbilities = new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		for (int i = 0; i < skirmishActionsSlots.length; i++) {
			skirmishActionsSlots[i] = (Integer) params.get("skirmish_action" + i + "_slot");
			skirmishActionsAbilities[i] = (Integer) params.get("skirmish_action" + i + "_ability");
		}
		
		int[] battleActionsSlots = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		int[] battleActionsAbilities = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		for (int i = 0; i < battleActionsSlots.length; i++) {
			battleActionsSlots[i] = (Integer) params.get("battle_action" + i + "_slot");
			battleActionsAbilities[i] = (Integer) params.get("battle_action" + i + "_ability");
		}
		
		// Récupère la flotte modifiée
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId(), FleetTools.ALLOW_HYPERSPACE);
		
		// Vérifie qu'il y a plus de vaisseaux devant que derrière
		int frontSlotsCount = 0, backSlotsCount = 0, freightersCount = 0,
			frontFreightersCount = 0, backFreightersCount = 0;
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			if (fleet.getSlot(i).getShip() != null) {
				if (frontSlots[i])
					frontSlotsCount++;
				else
					backSlotsCount++;
				
				if (fleet.getSlot(i).getShip().getShipClass() == Ship.FREIGHTER) {
					freightersCount++;
					if (frontSlots[i])
						frontFreightersCount++;
					else
						backFreightersCount++;
				}
			}
		}
		
		if (backSlotsCount > frontSlotsCount)
			throw new IllegalOperationException("Il n'y a pas suffisament " +
					"de slots sur la ligne de front.");
		
		int slotsCount = frontSlotsCount + backSlotsCount;
		if (freightersCount > backSlotsCount) {
			// Vérifie que le maximum de cargos sont placés derrière
			if (backFreightersCount != backSlotsCount || backSlotsCount < slotsCount / 2)
				throw new IllegalOperationException("Emplacement des cargos invalide.");
		} else {
			// Vérifie que tous les cargos sont derrière
			if (frontFreightersCount > 0)
				throw new IllegalOperationException("Emplacement des cargos invalide.");
		}
		
		// Met à jour la recherche du joueur
		player.updateResearch();
		List<Research> researches =
			DataAccess.getResearchesByPlayer(player.getId());
		
		// Vérifie que le joueur a développé les technologies pour débloquer
		// les capacités
		for (int j = 0; j < 2; j++) {
			int[] actionsSlots = j == 0 ? skirmishActionsSlots : battleActionsSlots;
			int[] actionsAbilities = j == 0 ? skirmishActionsAbilities : battleActionsAbilities;
			
			for (int i = 0; i < actionsSlots.length; i++) {
				if (actionsSlots[i] != -1) {
					Ship ship = fleet.getSlot(actionsSlots[i]).getShip();
					
					if (ship == null) {
						// Slot sans vaisseau
						actionsSlots[i] = -1;
					} else if (actionsAbilities[i] >= 0) {
						if (actionsAbilities[i] < ship.getAbilities().length) {
							Ability ability = ship.getAbilities()[actionsAbilities[i]];
							
							// Capacité passive
							if (ability.isPassive())
								throw new IllegalOperationException(
									"Impossible d'utiliser une capacité passive.");
							
							requirements:for (int requirement : ability.getRequirements()) {
								synchronized (researches) {
									for (Research research : researches) {
										if (research.getIdTechnology() == requirement) {
											if (research.getProgress() < 1)
												throw new IllegalOperationException(
													"Vous n'avez pas développé les " +
													"technologies pour utiliser une " +
													"capacité.");
											else
												continue requirements;
										}
									}
								}
								
								throw new IllegalOperationException("Vous " +
										"n'avez pas développé les technologies " +
										"pour utiliser une capacité.");
							}
						} else {
							// Capacité qui n'existe pas
							throw new IllegalOperationException("Capacité invalide.");
						}
					}
				}
			}
		}
		
		// Vérifie la cohérence des actions
		if (!checkActions(fleet, skirmishActionsSlots, skirmishActionsAbilities) ||
			!checkActions(fleet, battleActionsSlots, battleActionsAbilities))
			throw new IllegalOperationException("Configuration invalide.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setSkirmishActions(skirmishActionsSlots, skirmishActionsAbilities);
			fleet.setBattleActions(battleActionsSlots, battleActionsAbilities);
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
				fleet.setSlotFront(frontSlots[i], i);
			fleet.setTacticsDefined(true);
			fleet.save();
		}
		
		return UpdateTools.formatUpdates(player, Update.getPlayerFleetUpdate(idFleet));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private boolean checkActions(Fleet fleet, int[] actionsSlots, int[] actionsAbilities) {
		for (int i = 0; i < actionsSlots.length; i++) {
			int slot = actionsSlots[i];
			
			if (slot != -1) {
				// Vérifie que le vaisseau est disponible
				if (getShipUnavailabilityTime(fleet, actionsSlots,
						actionsAbilities, slot, i) != 0) {
					LoggingSystem.getServerLogger().info(i + ". " + slot + " unav");
					return false;
				}
				
				// Vérifie que le cooldown de l'action ne dépasse pas (4 +
				// nombre d'actions restantes avant la fin du combat)
				if (actionsAbilities[i] != -1) {
					Ship ship = fleet.getSlot(actionsSlots[i]).getShip();
					if (ship.getAbilities()[actionsAbilities[i]].getCooldown(
							) > 4 + actionsSlots.length - i - 1) {
						LoggingSystem.getServerLogger().info(i + ". " + slot + " too long");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private int getShipUnavailabilityTime(Fleet fleet, int[] actionsSlots,
			int[] actionsAbilities, int slot, int step) {
		if (slot == -1 || fleet.getSlot(slot).getShip() == null)
			throw new RuntimeException("Invalid slot: '" + slot + "'.");
		
		// Si le vaisseau n'a pas d'armes ni de compétences, il ne peut pas
		// agir
		if (fleet.getSlot(slot).getShip().getWeapons().length == 0) {
			Ability[] abilities = fleet.getSlot(slot).getShip().getAbilities();
			boolean hasActiveAbilities = false;
			for (Ability ability : abilities) {
				if (!ability.isPassive()) {
					hasActiveAbilities = true;
					break;
				}
			}
			
			if (!hasActiveAbilities)
				return 1;
		}
		
		// Vérifie que le vaisseau est disponible
		for (int k = 0; k < step; k++) {
			int currentSlot = actionsSlots[k];
			
			if (currentSlot == slot) {
				Ship ship = fleet.getSlot(slot).getShip();
				
				int ability = actionsAbilities[k];
				int cooldown = ability == -1 ?
					4 : // Tir
					ship.getAbilities()[ability].getCooldown();
				
				if (k + cooldown >= step)
					return 1 + k + cooldown - step;
			}
		}
		
		return 0;
	}
}
