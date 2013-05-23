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

package fr.fg.client.animation;

import fr.fg.client.map.UIItem;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.core.Point;

public class DelayedActionUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private UIItem fleet;
	
	private Point location;
	
	private Callback callback;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DelayedActionUpdater(UIItem fleet, Point location, Callback callback) {
		this.fleet = fleet;
		this.location = location;
		this.callback = callback;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isFinished() {
		return fleet.isDestroyed() || (
			fleet.getLocation().getX() /
			fleet.getRenderingHints().getTileSize() == location.getX() &&
			fleet.getLocation().getY() /
			fleet.getRenderingHints().getTileSize() == location.getY());
	}
	
	public void update(int interpolation) {
		if (fleet.getLocation().getX() /
				fleet.getRenderingHints().getTileSize() == location.getX() &&
				fleet.getLocation().getY() /
				fleet.getRenderingHints().getTileSize() == location.getY())
			callback.run();
	}
	
	public void destroy() {
		fleet = null;
		location = null;
		callback = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
