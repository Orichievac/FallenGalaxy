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
import java.util.List;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterMoveEvent;
import fr.fg.server.events.impl.BeforeMoveEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class MoveFleets extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Décalage dans le mouvement pour éviter les embouteillages
	private final static int[][] OFFSET = {
		{ 0,  0},
		{-1,  0},
		{ 0, -1},
		{ 1,  0},
		{ 0,  1},
		{-1, -1},
		{ 1, -1},
		{-1,  1},
		{ 1,  1},
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		// Paramètres de l'action
		int x		= (Integer) params.get("x");
		int y		= (Integer) params.get("y");
		
		// Compte le nombre de flottes à déplacer
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
					idFleet, player.getId(), FleetTools.ALLOW_DELUDE);
			
			if (fleet.isDelude() && fleet.getSkillLevel(Skill.SKILL_ULTIMATE_DELUDE) == 0)
				throw new IllegalOperationException(
					"Vous n'avez pas le niveau requis pour déplacer un leurre.");
			
			if (i > 0 && fleet.getIdCurrentArea() != fleets[0].getIdCurrentArea())
				throw new IllegalOperationException(
					"Sélection de flottes invalide.");
			
			fleets[i] = fleet;
		}
		
		List<Update> updates = new ArrayList<Update>();
		Point[] locations = new Point[fleets.length * 2];
		
		for (int i = 0; i < fleets.length; i++) {
			Fleet fleet = fleets[i];
			Point startLocation = new Point(fleet.getX(), fleet.getY());
			Point endLocation = new Point(x, y);
			
			GameEventsDispatcher.fireGameEvent(new BeforeMoveEvent(
					fleet, startLocation, endLocation));
			
			// Tente de déplacer la flotte à la case indiquée
			synchronized (fleet.getLock()) {
				fleet = DataAccess.getEditable(fleet);
				fleet.move(x + OFFSET[i][0], y + OFFSET[i][1]);
				fleet.save();
			}
			
			GameEventsDispatcher.fireGameEvent(new AfterMoveEvent(
					fleet, startLocation, endLocation));
			
			FleetTools.checkTriggeredCharges(fleet, updates);
			
			locations[i * 2] = startLocation;
			locations[i * 2 + 1] = endLocation;
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleets[0].getIdCurrentArea(),
				player.getId(), locations);
		
		updates.add(Update.getAreaUpdate());
		for (Fleet fleet : fleets)
			updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
