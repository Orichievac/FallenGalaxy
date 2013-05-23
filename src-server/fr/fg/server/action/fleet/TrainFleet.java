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

import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterTrainingEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class TrainFleet extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("id");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId(), FleetTools.ALLOW_HYPERSPACE);
		
		if (fleet.getLevel() > 2 + Advancement.getAdvancementLevel(player.getId(),
				Advancement.TYPE_TRAINING_MAX_LEVEL) || fleet.getLevel() == 15)
			throw new IllegalOperationException(
				"La flotte a déjà été entrainée à son niveau maximal.");
		
		// Met à jour les crédits disponibles
		player = Player.updateCredits(player);
		
		long price = Fleet.TRAINING_COST[fleet.getLevel()];
		
		if (player.getCredits() < price)
			throw new IllegalOperationException(
				"Vous n'avez pas assez de crédits pour entraîner la flotte.");
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-price);
			player.save();
		}
		
		// Augmente le niveau de la flotte de 1
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setXp(Fleet.getLevelXp(fleet.getLevel() + 1));
			fleet.updateSkillsCache();
			fleet.save();
		}
		
		GameEventsDispatcher.fireGameEvent(new AfterTrainingEvent(fleet));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getPlayerSystemsUpdate(),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
