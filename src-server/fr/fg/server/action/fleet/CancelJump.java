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
import java.util.ArrayList;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class CancelJump extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		// Compte le nombre de flottes à faire agir
		int fleetsCount = 0;
		for (int i = 0; i < 9; i++) {
			if (params.get("fleet" + i) == null)
				break;
			fleetsCount++;
		}
		
		// Charge les flottes
		Fleet[] fleets = new Fleet[fleetsCount];
		
		for (int i = 0; i < 9; i++) {
			if (params.get("fleet" + i) == null)
				break;
			
			int idFleet = (Integer) params.get("fleet" + i);
			Fleet fleet = FleetTools.getFleetByIdWithChecks(
					idFleet, player.getId(), FleetTools.ALLOW_HYPERSPACE);
			
			if (i > 0 && fleet.getIdCurrentArea() != fleets[0].getIdCurrentArea())
				throw new IllegalOperationException(
					"Sélection de flottes invalide.");
			
			fleets[i] = fleet;
		}
		
		Point[] jumpLocations = new Point[fleetsCount];
		
		for (int i = 0; i < fleetsCount; i++) {
			Fleet fleet = fleets[i];
			
			int hyperspaceIdArea = fleet.getHyperspaceIdArea();
			
			jumpLocations[i] = new Point(fleet.getX(), fleet.getY());
			Point targetLocation = new Point(fleet.getHyperspaceX(), fleet.getHyperspaceY());
			
			boolean updateTarget = fleet.isEndingJump();
			
			// Tente d'annuler le saut hyperspatial
			synchronized (fleet.getLock()) {
				fleet = DataAccess.getEditable(fleet);
				fleet.cancelJump();
				fleet.save();
			}
			
			if (updateTarget)
				UpdateTools.queueAreaUpdate(hyperspaceIdArea, player.getId(), false, targetLocation);
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleets[0].getIdCurrentArea(), player.getId(), jumpLocations);
		
		ArrayList<Update> updates = new ArrayList<Update>();
		
		updates.add(Update.getAreaUpdate());
		for (Fleet fleet : fleets)
			updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
