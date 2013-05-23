/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.server.task.hourly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;

public class CleanUpEvents extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void run() {
		this.setName("CleanUpEvents (hourly)");
		// Efface les évènements quand un joueur en a plus de 50
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		for (Player player : players) {
			List<Event> events = DataAccess.getEventByTargetId(player.getId(), Event.TARGET_PLAYER);
			
			// Supprime les rapports de combat périmés
			for (int i = events.size() - 1; i >= 0; i--) {
				Event event = events.get(i);
				
				if (event.getType() == Event.EVENT_BATTLE_REPORT &&
						DataAccess.getReportById(Integer.parseInt(event.getArg1())) == null) {
					events.remove(i);
					event.delete();
				}
			}
			
			if (events.size() > GameConstants.MAX_PLAYER_EVENTS) {
				Collections.sort(events, new Comparator<Event>() {
					public int compare(Event e1, Event e2) {
						if (e1.getDate() > e2.getDate())
							return -1;
						if (e1.getDate() == e2.getDate())
							return e1.getId() < e2.getId() ? -1 : 1;
						return 1;
					}
				});
				
				while (events.size() > GameConstants.MAX_PLAYER_EVENTS) {
					Event event = events.remove(events.size() - 1);
					event.delete();
				}
			}
		}
		
		// Efface les évènements quand une alliance en a plus de 15
		List<Ally> allies = new ArrayList<Ally>(DataAccess.getAllAllies());
		
		for (Ally ally : allies) {
			List<Event> events = DataAccess.getEventByTargetId(ally.getId(), Event.TARGET_ALLY);
			
			if (events.size() > GameConstants.MAX_ALLY_EVENTS) {
				Collections.sort(events, new Comparator<Event>() {
					public int compare(Event e1, Event e2) {
						if (e1.getDate() > e2.getDate())
							return -1;
						if (e1.getDate() == e2.getDate())
							return e1.getId() < e2.getId() ? -1 : 1;
						return 1;
					}
				});
				
				while (events.size() > GameConstants.MAX_ALLY_EVENTS) {
					Event event = events.remove(events.size() - 1);
					event.delete();
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
