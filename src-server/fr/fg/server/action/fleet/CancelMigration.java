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


import java.awt.Point;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class CancelMigration extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idFleet = (Integer) params.get("fleet");
		
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());
		
		long price = player.getMigrationCost();
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(price);
			player.save();
		}
		

		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
			fleet.setIdSystemMigrate(0);
			fleet.save();
		}
		
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
				player.getId(), new Point(fleet.getX(),fleet.getY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getXpUpdate(),
			Update.getPlayerSystemsUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
