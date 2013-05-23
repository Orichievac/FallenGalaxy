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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class LeaveSystem extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idSystem = (Integer) params.get("system");
		String password = (String) params.get("password");
		
		StarSystem system = DataAccess.getSystemById(idSystem);
		if (system == null)
			throw new IllegalOperationException("Ce système n'existe pas.");
		
		if (system.getIdOwner() != player.getId())
			throw new IllegalOperationException("Ce système ne vous appartient pas.");
		
		if (player.getSystems().size() <= 1)
			throw new IllegalOperationException("Vous devez garder au moins un système.");
		
		List<Fleet> playerFleets = DataAccess.getFleetsByOwner(player.getId());
		
		for(Fleet fleet : playerFleets)
		{
			if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_MIGRATE) &&
					fleet.getIdSystemMigrate() == system.getId())
				throw new IllegalOperationException("Vous êtes en train de migrer " +
						"ce système.");
				
		}

		
		if (!player.getPassword().equals(Utilities.encryptPassword(password)))
			throw new IllegalOperationException("Mot de passe invalide.");
		
		// Si une flotte est en train de capturer le système, la capture est
		// annulée
		List<Fleet> fleets = new ArrayList<Fleet>(
				DataAccess.getFleetsByArea(system.getIdArea()));
		
		for (Fleet fleet : fleets) {
			if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_COLONIZE) &&
					fleet.getSystemOver().getId() == system.getId()) {
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
					fleet.save();
				}
				UpdateTools.queuePlayerFleetUpdate(fleet.getIdOwner(), fleet.getId(), false);
			}
		}
		
		// Réinitialise le système
		synchronized (system.getLock()) {
			system = DataAccess.getEditable(system);
			system.resetSettings();
			system.save();
		}
		
		UpdateTools.queueAreaUpdate(system.getIdArea());
		
		// Met à jour l'influence de l'alliance
		if (player.getIdAlly() != 0)
			player.getAlly().updateInfluences();
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerSystemsUpdate(),
			Update.getPlayerSystemsUpdate(),
			Update.getXpUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
