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

import com.google.gwt.user.client.Element;

import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.core.Point;

public class LocationUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element element;
	
	private Point start, end;
	
	private double speed, dx, dy;
	
	private double increment;
	
	private double maxIncrement;
	
	private Point current;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public LocationUpdater(Element element, Point start, Point end,
			double speed) {
		this.increment = 0;
		this.element = element;
		this.start = new Point(start);
		this.end = new Point(end);
		this.speed = speed;
		this.current = new Point(start);
		
		updateData();
		
		element.getStyle().setProperty("left", start.getX() + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		element.getStyle().setProperty("top",  start.getY() + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Point getCurrentLocation() {
		return current;
	}
	
	public void setStart(Point start) {
		this.start = start;
		updateData();
	}

	public void setEnd(Point end) {
		this.end = end;
		updateData();
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
		updateData();
	}
	
	public void update(int interpolation) {
		increment += interpolation / 1000.;
		
		if (increment >= maxIncrement) {
			current = new Point(end);
		} else {
			current = new Point(start);
			current.addX((int) Math.round(dx * increment));
			current.addY((int) Math.round(dy * increment));
		}
		
		element.getStyle().setProperty("left", current.getX() + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		element.getStyle().setProperty("top",  current.getY() + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public boolean isFinished() {
		return increment >= maxIncrement;
	}
	
	public void destroy() {
		element = null;
		start = null;
		end = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData() {
		this.maxIncrement = Math.ceil(new Point(start).subtract(end).norm() / speed);
		
		double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
		this.dx = Math.cos(angle) * speed;
		this.dy = Math.sin(angle) * speed;
	}
}
