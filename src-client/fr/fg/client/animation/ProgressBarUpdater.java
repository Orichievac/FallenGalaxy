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

import com.google.gwt.dom.client.Element;

import fr.fg.client.openjwt.animation.TimerHandler;

public class ProgressBarUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element element;
	
	private double value;
	
	private int maxValue;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ProgressBarUpdater(Element element, int value, int maxValue) {
		this.element = element;
		this.value = value;
		this.maxValue = maxValue;
		
		element.getStyle().setProperty("width", //$NON-NLS-1$
			(maxValue == 0 ? 100 : 100 * value / maxValue) + "%"); //$NON-NLS-1$
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void update(int interpolation) {
		value += interpolation / 1000.;
		
		if (value < maxValue) {
			element.getStyle().setProperty("width", //$NON-NLS-1$
					(int) Math.round(100 * value / maxValue) + "%"); //$NON-NLS-1$
		}
	}
	
	public boolean isFinished() {
		return value >= maxValue;
	}
	
	public void destroy() {
		element = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
