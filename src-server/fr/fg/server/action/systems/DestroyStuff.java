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

package fr.fg.server.action.systems;

import java.util.Map;


import fr.fg.server.core.SystemTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DestroyStuff extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idSystem = (Integer) params.get("id");
		int index = (Integer) params.get("index");
		long count = (Long) params.get("count");
		String type = (String) params.get("type");
		
		StarSystem system = SystemTools.getSystemByIdWithChecks(idSystem, player.getId());
		system = StarSystem.updateSystem(system);
		
		if (type.equals("ship")) {
			// Destruction d'un vaisseau
			Slot slot = system.getSlot(index);
			
			synchronized (system.getLock()) {
				system = DataAccess.getEditable(system);
				system.setSlot(new Slot(slot.getId(),
					Math.max(0, slot.getCount() - count),
					slot.isFront()), index);
				system.save();
			}
		} else if (type.equals("resource") && index < 4) {
			// Destruction de ressources
			synchronized (system.getLock()) {
				system = DataAccess.getEditable(system);
				system.addResource(-count, index);
				system.save();
			}
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerSystemsUpdate()
		);
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
