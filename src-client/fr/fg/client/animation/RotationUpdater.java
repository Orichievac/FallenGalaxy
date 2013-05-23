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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import fr.fg.client.openjwt.animation.FastMath;
import fr.fg.client.openjwt.animation.TimerHandler;

public class RotationUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element element;
	
	private double startAngle, radius, rotationSpeed;
	
	private int dx, dy;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public RotationUpdater(Element element, double startAngle, double radius,
			double rotationSpeed, int dx, int dy) {
		this.element = element;
		this.startAngle = startAngle;
		this.radius = radius;
		this.rotationSpeed = rotationSpeed % (2 * Math.PI);
		this.dx = dx;
		this.dy = dy;
		
		updateCoords();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setRadius(double radius) {
		this.radius = radius;
		
		updateCoords();
	}
	
	public void setOffset(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
		
		updateCoords();
	}
	
	public void update(int interpolation) {
		startAngle += rotationSpeed * interpolation / 1000.;
		
		updateCoords();
	}
	
	public boolean isFinished() {
		return false;
	}
	
	public void destroy() {
		element = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateCoords() {
		DOM.setStyleAttribute(element, "left", (int) Math.floor( //$NON-NLS-1$
				dx + FastMath.cos(startAngle) * radius) + "px"); //$NON-NLS-1$
		DOM.setStyleAttribute(element, "top",  (int) Math.floor( //$NON-NLS-1$
				dy + FastMath.sin(startAngle) * radius) + "px"); //$NON-NLS-1$
	}
}
