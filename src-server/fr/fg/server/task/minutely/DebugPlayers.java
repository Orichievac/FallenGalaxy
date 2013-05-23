/*
Copyright 2012 jgottero

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

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.util.LoggingSystem;

public class DebugPlayers implements Runnable {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		for (Player player : players) {
			List<Fleet> fleets = player.getFleets();
			
			synchronized (fleets) {
				for (Fleet fleet : fleets) {
					ItemContainer container = fleet.getItemContainer();
					
					Item[] items = container.getItems();
					for (int i = 0; i < items.length; i++) {
						if (items[i].getType() == Item.TYPE_STRUCTURE) {
							if (DataAccess.getStructureById(items[i].getIdStructure()) == null) {
								container = DataAccess.getEditable(container);
								container.setItem(new Item(), i);
								container.save();
								LoggingSystem.getServerLogger().info(
									"Buggy structure spotted in fleet container [fleet=" +
									fleet.getId() + ",container=" + container.getId() +
									",structure=" + items[i].getIdStructure() + "].");
							}
						}
					}
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
