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

package fr.fg.server.events.impl;

import java.awt.Point;

import fr.fg.server.data.Fleet;
import fr.fg.server.events.GameEvent;

public class AfterMoveEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet fleet;
	
	private Point locationBefore;
	
	private Point locationAfter;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AfterMoveEvent(Fleet fleet, Point locationBefore,
			Point locationAfter) {
		this.fleet = fleet;
		this.locationBefore = locationBefore;
		this.locationAfter = locationAfter;
	}

	// --------------------------------------------------------- METHODES -- //

	public Fleet getFleet() {
		return fleet;
	}

	public Point getLocationBefore() {
		return locationBefore;
	}

	public Point getLocationAfter() {
		return locationAfter;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
