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

package fr.fg.server.action.systems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Product;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class BuildShips extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idSystem = (Integer) params.get("system");
		int slot = (Integer) params.get("slot");
		int id = (Integer) params.get("id");
		long count = (Long) params.get("count");
		
		StarSystem system = DataAccess.getSystemById(idSystem);
		
		// Vérifie que le système existe
		if (system == null)
			throw new IllegalOperationException("Le système n'existe pas.");
		
		// Vérifie que le système appartient au joueur
		if (system.getIdOwner() != player.getId())
			throw new IllegalOperationException("Le système ne vous appartient pas.");
		
		// Vérifie que le type de vaisseau est valide
		if (id != 0 && (id >= Ship.SHIPS.length || (Ship.SHIPS[id] == null)))
			throw new IllegalOperationException("Type de vaisseau invalide.");
		
		if (slot==3 && !player.isPremium()) {
			throw new IllegalOperationException("La file d'attente est pleine!");
		}
		
		// Vérifie que le joueur a accès aux produits nécessaires
		if (id != 0) {
			Product[] requiredProducts = Ship.SHIPS[id].getRequiredProducts();
			if (requiredProducts.length > 0) {
				if (player.getIdAlly() == 0)
					throw new IllegalOperationException("Vous n'avez pas accès à ce vaisseau.");
				
				HashMap<Integer, Integer> products = new HashMap<Integer, Integer>();
				List<Area> areas = DataAccess.getAllAreas();
				
				synchronized (areas) {
					for (Area area : areas) {
						if (area.getIdDominatingAlly() == player.getIdAlly())
							if (products.containsKey(area.getProduct()))
								products.put(area.getProduct(), 1 + products.get(area.getProduct()));
							else
								products.put(area.getProduct(), 1);
					}
				}
				
				for (Product requiredProduct : requiredProducts) {
					Integer productCount = products.get(requiredProduct.getType());
					
					if (productCount == null || productCount < requiredProduct.getCount())
						throw new IllegalOperationException("Vous n'avez pas accès à ce vaisseau.");
				}
			}
		}
		
		player = Player.updateCredits(player);
		player.updateResearch();
		
		long creditsCost = 0;
		
		system = StarSystem.updateSystem(system);
		
		synchronized (system.getLock()) {
			system = DataAccess.getEditable(system);
			
			// Réorganise les slots de production de vaisseaux
			for (int i = 1; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
				Slot buildSlot = system.getBuildSlot(i);
				Slot previousBuildSlot = system.getBuildSlot(i - 1);
				
				if (buildSlot.getId() != 0 && previousBuildSlot.getId() == 0) {
					for (int j = 0; j < i; j++)
						if (system.getBuildSlot(j).getId() == 0) {
							long ordered = system.getBuildSlotOrdered(i);
							system.setBuildSlot(buildSlot, j, false);
							system.setBuildSlotOrdered(ordered, j);
							system.setBuildSlot(new Slot(), i, true);
							break;
						}
				}
			}
			
			// Ajoute un ordre de construction dans le premier slot libre
			if (slot == -1) {
				// Recherche un emplacement libre
				for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++)
					if (system.getBuildSlot(i).getId() == 0) {
						slot = i;
						break;
					}
				
				if (slot == -1)
					throw new IllegalOperationException("La file d'attente est pleine.");
			}
			
			// Vérifie que l'emplacement est valide
			if (slot > 0 && system.getBuildSlot(slot - 1).getId() == 0)
				throw new IllegalOperationException("Emplacement invalide.");
			
			// Annule les vaisseaux en cours de construction et restitue les ressources
			// utilisées
			Slot buildSlot = system.getBuildSlot(slot);
			
			if (buildSlot.getId() != 0) {
				long slotCount = (long) Math.ceil(buildSlot.getCount());
				int[] cost = Ship.SHIPS[buildSlot.getId()].getCost();
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
					system.addResource(slotCount * cost[i], i);
				
				system.setBuildSlot(new Slot(), slot, true);
				
				creditsCost -= slotCount * (int) Math.ceil(cost[4] *
					system.getProduction(Building.TRADE_PORT));
			}
			
			if (id != 0 && count > 0) {
				// Vérifie que le système comporte les ressources nécessaires
				int[] cost = Ship.SHIPS[id].getCost();
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
					if (system.getResource(i) < cost[i] * count)
						throw new IllegalOperationException("Vous n'avez pas les " +
							"ressources nécessaires à la construction des " +
							"vaisseaux.");
					else
						// Décrémente les ressources du système
						system.addResource(-cost[i] * count, i);
				}
				
				int shipCreditsCost = (int) Math.ceil(cost[4] *
						system.getProduction(Building.TRADE_PORT));
				
				if (player.getCredits() < shipCreditsCost * count)
					throw new IllegalOperationException("Vous n'avez pas les " +
							"ressources nécessaires à la construction des " +
							"vaisseaux.");
				
				creditsCost += shipCreditsCost * count;
				
				// Vérifie que le joueur a développé les technologies nécessaires
				int[] requiredTechnologies = Ship.SHIPS[id].getTechnologies();
				
				for (int idTechnology : requiredTechnologies)
					if (!player.hasResearchedTechnology(idTechnology))
						throw new IllegalOperationException("Vous n'avez " +
							"pas recherché les technologies nécessaires.");
				
				// Vérifie que le joueur a un chantier spatial de niveau nécessaire
				int[] spaceshipYards = system.getBuildings(Building.SPACESHIP_YARD);
				int maxLevel = -1;
				
				for (int i = 0; i < spaceshipYards.length; i++)
					if (spaceshipYards[i] > 0)
						maxLevel = i;
				
				if (maxLevel < Building.getRequiredSpaceshipYardLevel(Ship.SHIPS[id].getShipClass()))
					throw new IllegalOperationException("Votre chantier " +
						"spatial n'est pas d'un niveau suffisant pour " +
						"construire cette classe de vaisseaux.");
				
				system.setBuildSlot(new Slot(id, count, true), slot, true);
			} else {
				// Décale les slots pour qu'il n'y ait pas de "trou"
				for (int i = slot + 1; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
					long ordered = system.getBuildSlotOrdered(i);
					system.setBuildSlot(system.getBuildSlot(i), i - 1, false);
					system.setBuildSlotOrdered(ordered, i - 1);
					system.setBuildSlot(new Slot(), i, true);
				}
			}
			
			// Enregistre le système dans la base de données
			system.save();
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-creditsCost);
			player.save();
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerSystemsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
