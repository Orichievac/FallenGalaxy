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

package fr.fg.server.action.structure;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Structure;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class DismountStructure extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(idFleet, player.getId());
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException(
				"La flotte n'a pas suffisament de mouvement.");
		
		// Vérifie que la flotte a la compétence ingénieur
		if (fleet.getSkillLevel(Skill.SKILL_ENGINEER) == -1)
			throw new IllegalOperationException("La flotte n'a pas " +
					"la compétence ingénieur.");
		
		// Recherche la structure sur laquelle la flotte se trouve
		Structure overStructure = null;
		List<Structure> structures = fleet.getArea().getStructures();
		
		synchronized (structures) {
			for (Structure structure : structures) {
				if (structure.getBounds().contains(fleetX, fleetY)) {
					overStructure = structure;
					break;
				}
			}
		}
		
		if (overStructure == null)
			throw new IllegalOperationException("La flotte " +
				"ne se trouve pas sur une structure.");
		
		if (!overStructure.getOwner().getTreatyWithPlayer(player).equals(Treaty.PLAYER))
			throw new IllegalOperationException("La structure ne vous appartient pas.");
		
		if (overStructure.isActivated())
			throw new IllegalOperationException("La structure doit " +
				"être désactivée avant de pouvoir être démontée.");
		
		// Vérifie que la flotte a la capacité pour transporter la structure
		ItemContainer itemContainer = fleet.getItemContainer();
		if (fleet.getPayload() < itemContainer.getTotalWeight() +
				overStructure.getWeight())
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de capacité pour transporter la structure.");
		
		// Vérifie que la flotte a un emplacement libre pour recevoir la structure
		boolean availableSpace = false;
		for (int i = 0; i < itemContainer.getMaxItems(); i++) {
			if (itemContainer.getItem(i).getType() == Item.TYPE_NONE) {
				availableSpace = true;
				break;
			}
		}
		
		if (!availableSpace)
			throw new IllegalOperationException("La flotte n'a pas " +
				"d'emplacement libre pour transporter la structure.");
		
		// Vérifie que le joueur dispose de suffisament de crédits
		long cost = overStructure.getDismountCost();
		
		player = Player.updateCredits(player);
		if (player.getCredits() < cost)
			throw new IllegalOperationException("Vous ne disposez pas de " +
				"suffisament de crédits pour démonter la structure.");
		
		// Vérifie que la structure n'est pas en cours de démontage
		List<Fleet> fleets = fleet.getArea().getFleets();
		
		synchronized (fleets) {
			for (Fleet areaFleet : fleets) {
				if (areaFleet.getIdOwner() == player.getId() &&
					overStructure.getBounds().contains(
						areaFleet.getCurrentX(), areaFleet.getCurrentY()) &&
						areaFleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE))
					throw new IllegalOperationException("Une flotte est " +
						"déjà en train de démonter la structure.");
			}
		}
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE,
				Utilities.now() + (3 + overStructure.getLevel()) * 3600);
			fleet.save();
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-cost);
			player.save();
		}
		
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
				player.getId(), new Point(fleetX, fleetY));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
