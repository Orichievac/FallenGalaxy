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

package fr.fg.server.task.minutely;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class SwitchDiplomacyState extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("SwitchDiplomacyState (minutely)");
		LoggingSystem.getServerLogger().trace("Task Name: "+this.getName());
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		long now = Utilities.now();
		
		for (Player player : players) {
			if (player.getSwitchDiplomacyDate() != 0 &&
					player.getSwitchDiplomacyDate() < now) {
				// Change le mode de diplomatie du joueur
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setDiplomacyActivated(!player.isDiplomacyActivated());
					player.setSwitchDiplomacyDate(0);
					player.save();
				}
				
				// Efface les traités du joueur
				if (!player.isDiplomacyActivated()) {
					List<Treaty> treaties =
						new ArrayList<Treaty>(player.getTreaties());
					
					for (Treaty treaty : treaties) {
						treaty.delete();
						UpdateTools.queueAreaUpdate(
								treaty.getOtherPlayer(player.getId()));
					}
					
					UpdateTools.queueAreaUpdate(player.getId());
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
