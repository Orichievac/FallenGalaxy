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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.util.LoggingSystem;

public class CheckUnstuckableFleets extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("CheckUnstuckableFleets (minutely)");
		LoggingSystem.getServerLogger().trace("Task Name: "+this.getName());
		List<Fleet> fleets = new ArrayList<Fleet>(DataAccess.getUnstuckableFleets());
		
		for (Fleet fleet : fleets) {
			// Compte le nombre de flottes voisines
			Area area = fleet.getArea();
			List<Fleet> areaFleets = area.getFleets();
			int count = 0;
			
			synchronized (areaFleets) {
				for (Fleet areaFleet : areaFleets) {
					if (Math.abs(areaFleet.getCurrentX() - fleet.getX()) <= 1 &&
							Math.abs(areaFleet.getCurrentY() - fleet.getY()) <= 1)
						count++;
				}
			}
			
			// La flotte est coincée...
			if (count == 9) {
				if (fleet.getStuckCount() < 2) {
					synchronized (fleet.getLock()) {
						fleet = DataAccess.getEditable(fleet);
						fleet.setStuckCount(fleet.getStuckCount() + 1);
						fleet.save();
					}
				} else {
					// Déplace le PNJ
					Point newLocation = new Point();
					do {
						newLocation.x = (int) Math.round(fleet.getX() + Math.random() * 80 - 40);
						newLocation.y = (int) Math.round(fleet.getY() + Math.random() * 80 - 40);
					} while (!area.areFreeTiles(newLocation.x - 2, newLocation.y - 2, 5, 5,
							Area.NO_FLEETS | Area.NO_SYSTEMS |
							Area.NO_OBJECTS | Area.EXCEPT_PASSABLE_OBJECTS, null));
					
					synchronized (fleet.getLock()) {
						fleet = DataAccess.getEditable(fleet);
						fleet.setStuckCount(0);
						fleet.setLocation(newLocation.x, newLocation.y);
						fleet.save();
					}
					
					try {
						UpdateTools.queueAreaUpdate(area.getId());
					} catch (Exception e) {
						// Ignoré
					}
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
