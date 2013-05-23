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

package fr.fg.server.action.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Update extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		UPDATE_FLEETS_AND_AREA = "fleets+area",
		UPDATE_XP = "xp",
		UPDATE_SYSTEMS = "systems";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		String type = (String) params.get("type");
		
		String updates;
		
		if (type.equals(UPDATE_FLEETS_AND_AREA)) {
			updates = UpdateTools.formatUpdates(
				player,
				fr.fg.server.core.Update.getAreaUpdate(),
				fr.fg.server.core.Update.getPlayerFleetsUpdate()
			);
		} else if (type.equals(UPDATE_XP)) {
			updates = UpdateTools.formatUpdates(
				player,
				fr.fg.server.core.Update.getXpUpdate()
			);
		} else if (type.equals(UPDATE_SYSTEMS)) {
			List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
			
			for (StarSystem system : systems)
				StarSystem.updateSystem(system);
			
			updates = UpdateTools.formatUpdates(
				player,
				fr.fg.server.core.Update.getPlayerSystemsUpdate()
			);
		} else {
			updates = UpdateTools.formatUpdates(player);
		}
		
		return updates;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
