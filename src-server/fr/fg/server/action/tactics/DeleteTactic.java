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

package fr.fg.server.action.tactics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.TacticTools;
import fr.fg.server.data.Player;
import fr.fg.server.data.Tactic;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DeleteTactic extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String hash = (String) params.get("hash");
		
		List<Tactic> tactics = new ArrayList<Tactic>(player.getTactics());
		
		for (Tactic tactic : tactics)
			if (tactic.getHash().equals(hash)) {
				tactic.delete();
				break;
			}
		
		return TacticTools.getPlayerTactics(null, player.getId()).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
