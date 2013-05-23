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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.StellarObject;
import fr.fg.server.util.LoggingSystem;

public class UpdateBlackHolesGravity extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void run() {
		this.setName("UpdateBlackHolesGravity (minutely)");
		LoggingSystem.getServerLogger().trace("Task Name: "+this.getName());
		List<StellarObject> blackholes = new ArrayList<StellarObject>(
				DataAccess.getObjectsByType(StellarObject.TYPE_BLACKHOLE));
		
		List<Integer> areasToUpdate = new ArrayList<Integer>();
		Map<Integer, List<Point>> locationsToUpdate = new HashMap<Integer, List<Point>>();
		
		for (StellarObject blackhole : blackholes) {
			Area area = blackhole.getArea();
			
			// Endommage les flottes qui stationnent au dessus du trou noir
			List<Fleet> fleets = new ArrayList<Fleet>(area.getFleets());
			
			for (Fleet fleet : fleets) {
				int fleetX = fleet.getCurrentX();
				int fleetY = fleet.getCurrentY();
				int dx = blackhole.getX() - fleetX;
				int dy = blackhole.getY() - fleetY;
				int distance = dx * dx + dy * dy;
				
				if (distance < GameConstants.BLACKHOLE_GRAVITY_RADIUS *
						GameConstants.BLACKHOLE_GRAVITY_RADIUS) {
					if (Math.sqrt(distance) > 1) {
						double angle = Math.atan2(dy, dx);
						int newX = fleetX + (int) Math.round(Math.cos(angle) * 1.2);
						int newY = fleetY + (int) Math.round(Math.sin(angle) * 1.2);
						
						if (area.isFreeTile(newX, newY, Area.CHECK_FLEET_MOVEMENT, fleet.getOwner())) {
							synchronized (fleet.getLock()) {
								Fleet newFleet = DataAccess.getEditable(fleet);
								newFleet.setLocation(newX, newY);
								newFleet.save();
							}
							
							if (!areasToUpdate.contains(blackhole.getIdArea())) {
								areasToUpdate.add(blackhole.getIdArea());
								locationsToUpdate.put(blackhole.getIdArea(),
										new ArrayList<Point>());
							}
							
							locationsToUpdate.get(blackhole.getIdArea()).add(
									new Point(fleetX, fleetY));
						}
					}
				}
			}
		}
		
		for (Integer idArea : areasToUpdate) {
			List<Point> locations = locationsToUpdate.get(idArea);
			
			UpdateTools.queueAreaUpdate(idArea,
					locations.toArray(new Point[locations.size()]));
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
