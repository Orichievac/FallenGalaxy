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

package fr.fg.server.action.systems;

import java.util.Map;


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DestroyBuilding extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idSystem = (Integer) params.get("system");
		String building = (String) params.get("id");
		int level = (Integer) params.get("level");
		
		int type = -1;
		
		if (building.length() > 0) {
			for (int i = 0; i < Building.BUILDING_LABELS.length; i++)
				if (building.equals(Building.BUILDING_LABELS[i])) {
					type = i;
					break;
				}
		}
		
		if (type == -1)
			throw new IllegalOperationException("Construction invalide.");
		
		StarSystem system = DataAccess.getSystemById(idSystem);
		
		// Vérifie que le système existe
		if (system == null)
			throw new IllegalOperationException("Le système n'existe pas.");
		
		// Vérifie que le système appartient au joueur
		if (system.getIdOwner() != player.getId())
			throw new IllegalOperationException("Le système ne vous appartient pas.");
		
		int extraCredits;
		
		system = StarSystem.updateSystem(system);
		
		synchronized (system.getLock()) {
			system = DataAccess.getEditable(system);
			
			// Vérifie qu'il y a au moins un bâtiment à détruire
			int[] buildings = system.getBuildings(type);
			
			if (buildings[level] == 0)
				throw new IllegalOperationException(
						"Impossible de détruire le bâtiment.");
			
			// Vérifie que les bâtiments ne sont pas en cours d'amélioration
			int upgrading = 0;
			Building currentBuilding = system.getCurrentBuilding();
			Building nextBuilding	 = system.getNextBuilding();
			Building thirdBuilding	 = system.getThirdBuilding();
			
			if (currentBuilding != null &&
					currentBuilding.getType() == type &&
					currentBuilding.getLevel() == level + 1)
				upgrading++;
			if (nextBuilding != null &&
					nextBuilding.getType() == type &&
					nextBuilding.getLevel() == level + 1)
				upgrading++;
			if (thirdBuilding != null &&
					thirdBuilding.getType() == type &&
					thirdBuilding.getLevel() == level + 1)
				upgrading++;
			
			if (buildings[level] <= upgrading)
				throw new IllegalOperationException("Vous ne pouvez détruire un " +
						"bâtiment en cours d'amélioration.");
			
			// Si le bâtiment est un chantier spatial, vérifie qu'il n'est pas
			// utilisé
			if (type == Building.SPACESHIP_YARD) {
				int required = -1;
				for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
					Slot buildSlot = system.getBuildSlot(i);
					
					if (buildSlot.getId() != 0)
						required = Math.max(required,
							Building.getRequiredSpaceshipYardLevel(
								Ship.SHIPS[buildSlot.getId()].getShipClass()));
				}
				
				// Compte le nombre de chantiers spatiaux permettant de
				// construire les vaisseaux en cours de construction
				if (required >= 0) {
					int count = 0;
					for (int i = required; i < 5; i++)
						count += buildings[i];
					
					if (count <= 1)
						throw new IllegalOperationException("Le chantier spatial est " +
								"en cours d'utilisation et ne peut être " +
								"détruit.");
				}
			}
			
			// Détruit le bâtiment
			int[] cost = Building.getCost(type, level);
			extraCredits = cost[GameConstants.CREDITS] / 2;
			
			
			//Si c'est un trade port
			//Détruit et rembourse les vaisseaux en cours de construction
		if (type == Building.TRADE_PORT){
			int slot;
			int creditsCost=0;
			for(slot=0;slot<3;slot++){
			Slot buildSlot = system.getBuildSlot(slot);
				if (buildSlot.getId() != 0) {
					long slotCount = (long) Math.ceil(buildSlot.getCount());
					int[] cost1 = Ship.SHIPS[buildSlot.getId()].getCost();
					for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
						system.addResource(slotCount * cost1[i], i);
					
					system.setBuildSlot(new Slot(), slot, true);
					
					creditsCost -= slotCount * (int) Math.ceil(cost1[4] *
						system.getProduction(Building.TRADE_PORT));
					
					synchronized (player.getLock()) {
						player = DataAccess.getEditable(player);
						player.addCredits(-creditsCost);
						player.save();
					}
				}
			}
		}
		
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				system.addResource(cost[i] / 2, i);
			
			buildings[level]--;
			system.setBuildings(buildings, type);
			
			system.save();
		}
		
		system = StarSystem.updateSystem(system);
		player = DataAccess.getPlayerById(player.getId());
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(extraCredits);
			player.save();
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerSystemsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
