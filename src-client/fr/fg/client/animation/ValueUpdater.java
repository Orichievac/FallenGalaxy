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

import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class ValueUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private OutlineText element;
	
	private String text;
	
	private double increment, currentValue, end;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ValueUpdater(OutlineText element, String text, double start,
			double end, double increment) {
		this.element = element;
		this.text = text;
		this.currentValue = start;
		this.end = end;
		this.increment = increment;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void update(int interpolation) {
		currentValue += increment * interpolation / 1000.;
		if (currentValue >= end)
			currentValue = end;
		element.setText(text.replace("%value%", String.valueOf((int) Math.floor(currentValue)))); //$NON-NLS-1$
	}
	
	public boolean isFinished() {
		return currentValue >= end;
	}
	
	public void destroy() {
		element = null;
		text = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
