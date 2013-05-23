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

import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.animation.TimerHandler;

public class CountdownUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String id;
	
	private double value;
	
	private double speed;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public CountdownUpdater(String id, double value, double speed) {
		this.id = id;
		this.value = value;
		this.speed = speed;
		
		Element timedElement = DOM.getElementById(id);
		
		if (timedElement != null)
			timedElement.setInnerHTML(Formatter.formatNumber(Math.ceil(value), true));
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isFinished() {
		return value == 0;
	}
	
	public void update(int interpolation) {
		double oldValue = value;
		value -= speed * interpolation;
		
		if (value < 0)
			value = 0;
		
		if (Math.ceil(oldValue) == Math.ceil(value))
			return;
		
		Element timedElement = DOM.getElementById(id);
		
		if (timedElement != null)
			timedElement.setInnerHTML(Formatter.formatNumber(Math.ceil(value), true));
	}
	
	public void destroy() {
		// Non utilisé
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
