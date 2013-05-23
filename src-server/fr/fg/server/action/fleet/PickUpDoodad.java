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

import java.awt.Point;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.AiTools;
import fr.fg.server.core.BattleTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.VisitedArea;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterDoodadPickedUpEvent;
import fr.fg.server.events.impl.AlienDoodadEvent;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.Utilities;

public class PickUpDoodad extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("id");
		
		if (!player.hasRight(Player.PREMIUM))
			throw new IllegalOperationException("Fonctionnalité uniquement " +
					"disponible avec un compte premium.");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(idFleet, player.getId());
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		List<StellarObject> objects = fleet.getArea().getObjects();
		StellarObject doodad = null;
		
		synchronized (objects) {
			for (StellarObject object : objects) {
				if (object.getType().equals(StellarObject.TYPE_DOODAD) &&
						object.getVariant() != 5 &&
						object.getX() == fleetX &&
						object.getY() == fleetY) {
					doodad = object;
					break;
				}
			}
		}
		
		if (doodad == null)
			throw new IllegalOperationException("La flotte n'est pas stationnée sur un objet.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_PICK_UP, Utilities.now() + 300);
			fleet.save();
		}
		
		doodad.delete();
		
		String updates = "";
		
		switch (doodad.getVariant()) {
		case 2:
			// Anomalie spatiale - téléportation dans un autre secteur visité, ou pas
			int oldIdArea = fleet.getIdCurrentArea();
			int oldX = fleet.getCurrentX();
			int oldY = fleet.getCurrentY();
			
			switch (Utilities.random(1, 3)) {
			case 1:
				// Il ne se passe rien
				UpdateTools.queueAreaUpdate(oldIdArea, player.getId(),
					new Point(oldX, oldY));
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate(
						"Il y a une tension presque palpable dans la " +
						"flotte lors de l'examen de l'anomalie... " +
						"Mais il ne se passe rien.")
				);
				break;
				
			case 2:
				// Téléporation dans le même secteur
				Point tile = fleet.getArea().getRandomFreeTile(
						Area.CHECK_FLEET_MOVEMENT, fleet.getOwner());
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.setLocation(tile.x, tile.y);
					fleet.setScheduledX(0);
					fleet.setScheduledY(0);
					fleet.setScheduledMove(false);
					fleet.save();
				}
				
				UpdateTools.queueAreaUpdate(oldIdArea, player.getId(),
					new Point(oldX, oldY),
					new Point(fleet.getCurrentX(), fleet.getCurrentY()));
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate("Oups ! Votre flotte a été téléportée quelque part dans le secteur !")
				);
				break;
				
			case 3:
				// Téléporation dans un autre secteur visité
				List<VisitedArea> visitedAreas = player.getVisitedAreas();
				VisitedArea randomVisitedArea;
				
				do {
					randomVisitedArea = visitedAreas.get((int) (Math.random() * visitedAreas.size()));
				} while (randomVisitedArea.getDate() > Utilities.now());
				
				Area randomArea = DataAccess.getAreaById(randomVisitedArea.getIdArea());
				
				tile = randomArea.getRandomFreeTile(
						Area.CHECK_FLEET_MOVEMENT, fleet.getOwner());
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.setIdArea(randomArea.getId());
					fleet.setLocation(tile.x, tile.y);
					fleet.setScheduledX(0);
					fleet.setScheduledY(0);
					fleet.setScheduledMove(false);
					fleet.save();
				}
				
				UpdateTools.queueAreaUpdate(oldIdArea, player.getId(),
					new Point(oldX, oldY));
				UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
					new Point(fleet.getCurrentX(), fleet.getCurrentY()));
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate("Ouch ! Votre flotte a été téléportée quelque part dans la galaxie !")
				);
				break;
			}
			break;
		case 1:
			// Comètes
			switch (Utilities.random(1, 2)) {
			case 1:
				// Dégâts
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						Slot slot = fleet.getSlot(i);
						
						// % de vaisseaux perdus (entre 5% et 10%)
						double losses = .05 + Math.random() * .1;
						
						// Note : le ceil garantit qu'il reste au moins 1 vaisseau
						// dans la flotte
						slot.setCount(Math.ceil(slot.getCount() * (1 - losses)));
						fleet.setSlot(slot, i);
					}
					
					fleet.save();
				}
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate("Une comète a percuté notre flotte... " +
						"Les recherches ont du être abandonnées pour ne pas aggraver les pertes.")
				);
				break;
				
			case 2:
				// Anti-matière bonus
				ItemContainer itemContainer = fleet.getItemContainer();
				long availableSpace = Math.max(0, fleet.getPayload() - (long) Math.ceil(itemContainer.getTotalWeight()));
				int availablePosition = itemContainer.getCompatibleOrFreePosition(Item.TYPE_RESOURCE, 3);
				
				if (availableSpace == 0 || availablePosition == -1) {
					updates = UpdateTools.formatUpdates(
						player,
						Update.getPlayerFleetUpdate(fleet.getId()),
						Update.getAreaUpdate(),
						Update.getInformationUpdate("Des traces d'antimatière " +
							"ont été découvertes dans une poche de gaz. Malheureusement, " +
							"notre flotte n'a pas la capacité pour l'entreposer.")
					);
				} else {
					List<StarSystem> systems = player.getSystems();
					double production = 0;
					
					synchronized (systems) {
						for (StarSystem system : systems) {
							production += system.getPopulation() *
								system.getProduction(Building.CORPORATIONS) *
								system.getProductionModifier();
						}
					}
					
					long antimatter = 2 * Math.min(availableSpace,
						(long) Math.floor(production * fleet.getPowerLevel() *
								(.8 + Math.random() * .4)));
					
					Item item = itemContainer.getItem(availablePosition);
					item.setType(Item.TYPE_RESOURCE);
					item.setId(3);
					item.addCount(antimatter);
					
					synchronized (itemContainer.getLock()) {
						itemContainer = DataAccess.getEditable(itemContainer);
						itemContainer.setItem(item, availablePosition);
						itemContainer.save();
					}
					
					updates = UpdateTools.formatUpdates(
						player,
						Update.getPlayerFleetUpdate(fleet.getId()),
						Update.getAreaUpdate(),
						Update.getInformationUpdate("Nous avons extrait de l'antimatière piégée dans une poche de gaz.<br/>" +
							"<span class=\"emphasize\">+" + antimatter + " <img src=\"" +
							Config.getMediaURL() + "images/misc/blank.gif\" class=\"resource r3\"/></span>")
					);
				}
				break;
			}
			
			UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
					new Point(fleet.getCurrentX(), fleet.getCurrentY()));
			break;
		default:
//-----------------------------------------------------------
//Switch des possibilités
			switch (Utilities.random(1, 6)) {
			case 1:
				// Points de recherche bonus
				int extraResearchPoints = Utilities.random(15, 35) * player.getResearchPoints();
				
				player.addResearchPoints(extraResearchPoints);
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate("Génial ! Vous avez trouvé un bidule " +
						"technologique inconnu qui vous sera bien utile dans vos recherches !<br/>" +
						"<span class=\"emphasize\">+" + extraResearchPoints + " <img src=\"" +
						Config.getMediaURL() + "images/misc/blank.gif\" class=\"resource research\"/></span>")
				);
				break;
				
			case 2:
				// Crédits bonus
				List<StarSystem> systems = player.getSystems();
				double production = 0;
				
				synchronized (systems) {
					for (StarSystem system : systems) {
						production += system.getPopulation() *
							system.getProduction(Building.CORPORATIONS) *
							system.getProductionModifier();
					}
				}
				
				int extraCredits = (int) Math.floor(Utilities.random(10, 20) * production);
				
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.addCredits(extraCredits);
					player.save();
				}
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerSystemsUpdate(),
					Update.getAreaUpdate(),
					Update.getInformationUpdate("Vous avez trouvé quelques crédits " +
						"dont le propriétaire n'a certainement plus l'usage.<br/>" +
						"<span class=\"emphasize\">+" + extraCredits + " <img src=\"" +
						Config.getMediaURL() + "images/misc/blank.gif\" class=\"resource credits\"/></span>")
				);
				break;
				
			case 3:
				// Alien - un vaisseau perdu
				int slotsCount = 0;
				int shipsCount = 0;
				
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
					if (fleet.getSlot(i).getShip() != null) {
						slotsCount++;
						shipsCount += fleet.getSlot(i).getCount();
					}
				
				int shipId = 0;
				
				if (shipsCount > 1) {
					int randomSlot = (int) (Math.random() * slotsCount);
					
					int index = 0;
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						Slot slot = fleet.getSlot(i);
						
						if (slot.getShip() != null) {
							if (randomSlot == index) {
								synchronized (fleet) {
									fleet = DataAccess.getEditable(fleet);
									fleet.setSlot(new Slot(slot.getId(),
										slot.getCount() - 1, slot.isFront()), i);
									fleet.save();
								}
								shipId = slot.getId();
								break;
							}
							
							index++;
						}
					}
					
					GameEventsDispatcher.fireGameNotification(
							new AlienDoodadEvent(fleet, shipId));
					
					updates = UpdateTools.formatUpdates(
						player,
						Update.getPlayerFleetUpdate(fleet.getId()),
						Update.getAreaUpdate(),
						Update.getInformationUpdate("Aïe ! Un alien s'est infiltré " +
							"sur l'un de vos vaisseaux ! Vous n'avez d'autre choix " +
							"que de le saborder.<br/><span class=\"emphasize\">-1 " +
							Messages.getString("ships" + shipId + "[one]") + "</span>")
						);
				} else {
					updates = UpdateTools.formatUpdates(
						player,
						Update.getPlayerFleetUpdate(fleet.getId()),
						Update.getAreaUpdate(),
						Update.getInformationUpdate("Il ne reste rien à piller ici...")
					);
				}
				break;
				
			case 4:
				// Apparition flotte pirate
				fleetX = fleet.getCurrentX();
				fleetY = fleet.getCurrentY();
				Area area = fleet.getArea();
				
				boolean spawn = false;
				
				// TODO jgottero try/catch a enlever quand les FP de tout niveau auront été définies
				try {
					Fleet pirate = AiTools.createPirateFleet(fleet.getPowerLevel() + 1,
							fleetX, fleetY, area.getId());
					
					BattleTools.battle(BattleTools.MODE_BATTLE, pirate, fleet, false);
					
					pirate = DataAccess.getFleetById(pirate.getId());
					if (pirate != null)
						pirate.delete();
					
					spawn = true;
				} catch (IllegalArgumentException e) {
					// Non utilisé
				}
				
				if (spawn) {
					updates = UpdateTools.formatUpdates(
						player,
						Update.getXpUpdate(),
						Update.getNewEventUpdate(),
						Update.getPlayerFleetsUpdate(),
						Update.getAreaUpdate(),
						Update.getInformationUpdate("Notre flotte est attaquée par des pirates en embuscade !")
					);
				} else {
					updates = UpdateTools.formatUpdates(
						player,
						Update.getPlayerFleetUpdate(fleet.getId()),
						Update.getAreaUpdate(),
						Update.getInformationUpdate("Il ne reste rien à piller ici...")
					);
				}
				break;
				
			case 5:
				// Rien
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate("Il ne reste rien à piller ici...")
				);
				break;

			
			case 6:
				//bonus XP pour la flotte
				// gain entre un dixième de l'xp du joueur (minimum) et la moitié (maximum)
				
				//random de bonusXp en valeur absolue (positif)
				long fleetXp = fleet.getXp();
				long bonusXp = Utilities.random(0, fleetXp/10);
				
				//coefficient pour savoir si le bonus est un bonus ou un malus
				//si le random tombe sur 0: c'est un malus
				if(Utilities.random(0, 1) == 0)
					bonusXp = bonusXp * -1;
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.setXp(fleetXp + bonusXp);
					fleet.save();
				}
				
				String msg = "La fouille de ce bidule vous à ";
				if(bonusXp > 0)
					msg += "fait gagné " + bonusXp + "xp pour votre flotte";
				else if(bonusXp < 0)
					msg += "fait perdre " + -1*bonusXp + "xp pour votre flotte";
				else
					msg += "fait perdre du temps pour rien";
				msg += ".";
				
				updates = UpdateTools.formatUpdates(
					player,
					Update.getPlayerFleetUpdate(fleet.getId()),
					Update.getAreaUpdate(),
					Update.getInformationUpdate(msg)						
				);
				break;
			}
			UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
					new Point(fleet.getCurrentX(), fleet.getCurrentY()));
		}

			
		GameEventsDispatcher.fireGameEvent(new AfterDoodadPickedUpEvent(fleet, doodad));
		
		return updates;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
