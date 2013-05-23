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

package fr.fg.server.action.skill;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StellarObject;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterMiningEvent;
import fr.fg.server.i18n.Formatter;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class Mining extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui crée le lien
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		// Vérifie que la flotte a des cargos
		long totalWeight = (long) Math.ceil(fleet.getItemContainer().getTotalWeight());
		
		long payload = fleet.getPayload();
		if (totalWeight >= payload)
			throw new IllegalOperationException("La flotte n'a pas " +
				"d'espace pour recevoir des ressources.");
		
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir miner.");
		
		Area area = fleet.getArea();
		List<StellarObject> objects = new ArrayList<StellarObject>(area.getObjects());
		
		for (StellarObject object : objects) {
			String type = object.getType();
			Rectangle bounds = object.getBounds();
			
			if (type.startsWith(StellarObject.TYPE_ASTEROID)) {
				if (bounds.contains(fleet.getCurrentX(), fleet.getCurrentY())) {
					int resourceIndex = 0;
					int coef = 0;
					
					
					if (type.equals(StellarObject.TYPE_ASTEROID)) {
						resourceIndex = Utilities.random(0, 2);
						coef = 9;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_DENSE)) {
						resourceIndex = Utilities.random(0, 2);
						coef = 12;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_LOW_TITANIUM)) {
						resourceIndex = 0;
						coef = 60;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_LOW_CRYSTAL)) {
						resourceIndex = 1;
						coef = 60;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_LOW_ANDIUM)) {
						resourceIndex = 2;
						coef = 60;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_VEIN_TITANIUM )) {
						resourceIndex = 0;
						coef = 100;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_VEIN_CRYSTAL )) {
						resourceIndex = 1;
						coef = 100;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_VEIN_ANDIUM )) {
						resourceIndex = 2;
						coef = 100;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_LOWC_TITANIUM )) {
						resourceIndex = 0;
						coef = 200;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_LOWC_CRYSTAL )) {
						resourceIndex = 1;
						coef = 200;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_LOWC_ANDIUM )) {
						resourceIndex = 2;
						coef = 200;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_MEDIUMC_TITANIUM  )) {
						resourceIndex = 0;
						coef = 400;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_MEDIUMC_CRYSTAL  )) {
						resourceIndex = 1;
						coef = 400;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_MEDIUMC_ANDIUM  )) {
						resourceIndex = 2;
						coef = 400;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_IMPORTANT_TITANIUM  )) {
						resourceIndex = 0;
						coef = 750;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_IMPORTANT_CRYSTAL  )) {
						resourceIndex = 1;
						coef = 750;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_IMPORTANT_ANDIUM  )) {
						resourceIndex = 2;
						coef = 750;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_AVG_TITANIUM)) {
						resourceIndex = 0;
						coef = 1500;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_AVG_CRYSTAL)) {
						resourceIndex = 1;
						coef = 1500;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_AVG_ANDIUM)) {
						resourceIndex = 2;
						coef = 1500;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_HIGH_TITANIUM)) {
						resourceIndex = 0;
						coef = 3000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_HIGH_CRYSTAL)) {
						resourceIndex = 1;
						coef = 3000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_HIGH_ANDIUM)) {
						resourceIndex = 2;
						coef = 3000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_ABONDANT_TITANIUM   )) {
						resourceIndex = 0;
						coef = 6000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_ABONDANT_CRYSTAL   )) {
						resourceIndex = 1;
						coef = 6000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_ABONDANT_ANDIUM   )) {
						resourceIndex = 2;
						coef = 6000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_PURE_TITANIUM   )) {
						resourceIndex = 0;
						coef = 10000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_PURE_CRYSTAL   )) {
						resourceIndex = 1;
						coef = 10000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_PURE_ANDIUM   )) {
						resourceIndex = 2;
						coef = 10000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_CONCENTRATE_TITANIUM   )) {
						resourceIndex = 0;
						coef = 15000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_CONCENTRATE_CRYSTAL   )) {
						resourceIndex = 1;
						coef = 15000;
					} else if (type.equals(StellarObject.TYPE_ASTEROID_CONCENTRATE_ANDIUM   )) {
						resourceIndex = 2;
						coef = 15000;
					}
					
					if (coef > 0) {
						ItemContainer itemContainer = fleet.getItemContainer();
						if (itemContainer.getCompatibleOrFreePosition(
								Item.TYPE_RESOURCE, resourceIndex) == -1)
							throw new IllegalOperationException("La flotte n'a pas " +
								"d'espace pour recevoir des ressources.");
						
						int skillLevel = fleet.getSkillLevel(Skill.SKILL_MINING);
						
						// Détermine le nombre de ressources gagnées
						long resources = Utilities.random(
							coef * 75 * Skill.SKILL_MINING_RESOURCES_COEF[skillLevel],
							coef * 135 * Skill.SKILL_MINING_RESOURCES_COEF[skillLevel]);
						long newResources = Math.min(resources, payload - totalWeight);
						
						int length = (int) Math.ceil(Skill.SKILL_MINING_MOVEMENT_RELOAD *
							Math.pow(.95, Advancement.getAdvancementLevel(
								player.getId(), Advancement.TYPE_MINING_UPGRADE)));
						
						// 30 Minutes minimum
						length = Math.max(length, GameConstants.MINIMUM_MINING_TIME);
						
						// Ajoute les ressources à la flotte
						synchronized (fleet.getLock()) {
							fleet = DataAccess.getEditable(fleet);
							fleet.doAction(Fleet.CURRENT_ACTION_MINE, Math.max(
								fleet.getMovementReload(),
								Utilities.now() + length));
							fleet.addXp(Skill.SKILL_MINING_XP_BONUS);
							fleet.save();
						}
						
						synchronized (itemContainer.getLock()) {
							itemContainer = DataAccess.getEditable(itemContainer);
							itemContainer.addResource(newResources, resourceIndex);
							itemContainer.save();
						}
						
						boolean depleted = false;
						if (Math.random() < .11) {
							depleted = true;
							StellarObject asteroid;
							int objectsCount = area.getObjects().size();
							
							do {
								asteroid = area.getObjects().get((int) (Math.random() * objectsCount));
							} while (!asteroid.getType().equals(StellarObject.TYPE_ASTEROID));
							
							synchronized (asteroid.getLock()) {
								asteroid = DataAccess.getEditable(asteroid);
								asteroid.setType(object.getType());
								asteroid.save();
							}
							
							synchronized (object.getLock()) {
								object = DataAccess.getEditable(object);
								object.setType(StellarObject.TYPE_ASTEROID);
								object.save();
							}
						}
						
						GameEventsDispatcher.fireGameEvent(new AfterMiningEvent(
								fleet, resourceIndex, newResources));
						
						UpdateTools.queueAreaUpdate(area.getId(), player.getId());
						
						String sentence = "Vous avez découvert " +
							Formatter.formatNumber(resources) + "&nbsp;" +
							Utilities.getResourceImg(resourceIndex) + " ! " +
							(depleted ? "Le filon a été épuisé..." :
							"L'extraction commence...");
						
						return UpdateTools.formatUpdates(
							player,
							Update.getPlayerFleetUpdate(fleet.getId()),
							Update.getAreaUpdate(),
							Update.getInformationUpdate(sentence)
						);
					}
				}
			}
		}
		
		throw new IllegalOperationException(
			"La flotte doit être placée sur un astéroïde pour pouvoir miner.");
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
