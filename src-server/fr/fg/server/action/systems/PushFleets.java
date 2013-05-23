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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.SystemTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class PushFleets extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idSystem = (Integer) params.get("system");
		StarSystem system = SystemTools.getSystemByIdWithChecks(idSystem, player.getId());
		
		Area area = system.getArea();
		
		List<Fleet> fleets = new ArrayList<Fleet>(area.getFleets());
		
		for (Fleet fleet : fleets) {
			if (fleet.getIdContract() == 0 &&
					system.contains(fleet.getCurrentX(), fleet.getCurrentY()) &&
					fleet.getOwner().getTreatyWithPlayer(player).equals(Treaty.NEUTRAL)) {
				int[] location = area.generateExitPosition(system.getX(), system.getY(), 
						8, 5, Area.CHECK_FLEET_MOVEMENT, fleet.getOwner());
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.setLocation(location[0], location[1]);
					fleet.save();
				}
				
				UpdateTools.queuePlayerFleetUpdate(fleet.getIdOwner(), fleet.getId());
				UpdateTools.queueAreaUpdate(system.getIdArea(), player.getId());
			}
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
