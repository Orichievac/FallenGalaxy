/*
Copyright 2010 Nicolas Bosc

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
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

// ------------------------------------------------------- CONSTANTES -- //
// -------------------------------------------------------- ATTRIBUTS -- //
// ---------------------------------------------------- CONSTRUCTEURS -- //
// --------------------------------------------------------- METHODES -- //

public class CancelDismount extends Action {

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());

		// Vérifie que la flotte est en train de démonter une structure
		if (!fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE))
			throw new IllegalOperationException("La flotte n'est pas en " +
					"train de démonter une structure.");
		
		// Recherche la structure sur laquelle la flotte se trouve
		Structure overStructure = null;
		List<Structure> structures = fleet.getArea().getStructures();
		
		synchronized (structures) {
			for (Structure structure : structures) {
				if (structure.getBounds().contains(fleet.getCurrentX(), fleet.getCurrentY())) {
					overStructure = structure;
					break;
				}
			}
		}
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
			fleet.save();
		}
		
		long cost = overStructure.getDismountCost();
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(cost);
			player.save();
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getXpUpdate()
		);
	}


	// ------------------------------------------------- METHODES PRIVEES -- //
}
