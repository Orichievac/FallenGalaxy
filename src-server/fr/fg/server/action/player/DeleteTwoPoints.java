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



import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DeleteTwoPoints extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		
		for (int type = 0; type <= Advancement.TYPE_LINE_OF_SIGHT; type ++) {
			
				Advancement.setAdvancementLevel(player.getId(), type, 0);
			
		}

		List<Update> updates = new ArrayList<Update>();
		updates.add(Update.getAdvancementsUpdate());
		updates.add(Update.getAreaUpdate()); // Mise à jour de la ligne de mire
		
		
		return UpdateTools.formatUpdates(
			player,
			updates
		);
		
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}