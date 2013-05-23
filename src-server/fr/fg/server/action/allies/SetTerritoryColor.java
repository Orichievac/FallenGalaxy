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

package fr.fg.server.action.allies;

import java.util.Map;

import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetTerritoryColor extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int color = (Integer) params.get("color");
		long update = (Long) params.get("update");
		Ally ally = player.getAlly();
		
		if (ally == null)
			throw new IllegalOperationException("Vous n'avez pas d'alliance.");
		
		if (player.getAllyRank() != ally.getLeaderRank())
			throw new IllegalOperationException("Seul le leader peut " +
					"modifier la couleur du territoire.");
		
		synchronized (ally.getLock()) {
			ally = DataAccess.getEditable(ally);
			ally.setColor(color);
			ally.save();
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(update),
			Update.getInformationUpdate("Le changement de couleur sera effectif dans 1 heure.")
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
