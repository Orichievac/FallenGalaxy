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
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetFleetName extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		// Paramètres de l'action
		int idFleet = (Integer) params.get("id");
		String name = (String) params.get("name");
		
		// Vérifie que le nom n'est pas blacklisté
		if (Badwords.containsBadwords(name))
			throw new IllegalOperationException("Ce nom n'est pas autorisé.");
		
		// Vérifie que le nom est unique
		List<Fleet> fleets = player.getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				if (fleet.getName().equals(name))
					throw new IllegalOperationException("Une de vos flottes porte déjà  ce nom.");
			}
		}
		
		//Verifie qu'elle n'est pas en train de faire un combat
		if(DataAccess.getFleetById(idFleet).getCurrentAction()=="ACTION_BATTLE"){
			throw new IllegalOperationException("Vous ne pouvez pas changer de nom durant l'immobilisation suite à une attaque");
		}
		
		//TODO? No rename after swap ?

		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId(),
				FleetTools.ALLOW_HYPERSPACE | FleetTools.ALLOW_DELUDE);
		
		// Modifie le nom de la flotte
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setName(name);
			fleet.save();
		}
		
		// Met à  jour le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(), false);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}

