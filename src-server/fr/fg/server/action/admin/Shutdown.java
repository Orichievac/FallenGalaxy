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

package fr.fg.server.action.admin;

import java.util.Map;


import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.ServerController;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class Shutdown extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String key = (String) params.get("key");
		int length = (Integer) params.get("length");
		
		if (!Utilities.encryptPassword(key, "SHA1").equals(
				"3622b9a94701ed751380c8bcf847c0bf151a9afc"))
			throw new IllegalOperationException("Invalid key.");
		
		ServerController.setShutdownDate(Utilities.now() + length);
		
		UpdateTools.queueServerShutdownUpdate();
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
