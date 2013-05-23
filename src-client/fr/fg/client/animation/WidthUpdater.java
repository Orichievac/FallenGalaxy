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

public class WidthUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //

	private Element element;
	
	private double currentWidth;
	
	private double targetWidth;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public WidthUpdater(Element element, double currentWidth) {
		this.element = element;
		this.currentWidth = currentWidth;
		this.targetWidth = currentWidth;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void setTargetWidth(double targetWidth) {
		this.targetWidth = targetWidth;
	}
	
	public void setCurrentWidth(double currentWidth) {
		this.currentWidth = currentWidth;
		
		this.element.getStyle().setProperty("width",
				Math.floor(currentWidth * 100) + "%");
	}
	
	public void destroy() {
		// Non utilisé
	}
	
	public boolean isFinished() {
		return false;
	}
	
	public void update(int interpolation) {
		if (currentWidth != targetWidth) {
			if (currentWidth > targetWidth) {
				if (Math.abs(currentWidth - targetWidth) < .02)
					setCurrentWidth(targetWidth);
				else
					setCurrentWidth(currentWidth - ((currentWidth - targetWidth) / 2.));
			} else {
				if (Math.abs(currentWidth - targetWidth) < .02)
					setCurrentWidth(targetWidth);
				else
					setCurrentWidth(currentWidth + ((targetWidth - currentWidth) / 2.));
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
