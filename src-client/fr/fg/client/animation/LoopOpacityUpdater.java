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

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerHandler;

public class LoopOpacityUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int MODE_TARGET = 1, MODE_LOOP = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element element;
	
	private int mode;
	
	private double currentOpacity;
	
	private double targetOpacity;
	
	private double minTargetOpacity;
	
	private double maxTargetOpacity;
	
	private double increment;
	
	private boolean hiddenWhenTransparent;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public LoopOpacityUpdater(Element element, double currentOpacity,
			double increment, boolean hiddenWhenTransparent) {
		this.mode = MODE_TARGET;
		this.element = element;
		this.currentOpacity = currentOpacity;
		this.targetOpacity = currentOpacity;
		this.increment = increment;
		this.hiddenWhenTransparent = hiddenWhenTransparent;
		
		OpenJWT.setElementOpacity(element, currentOpacity);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setIncrement(double increment) {
		this.increment = increment;
	}
	
	public void setCurrentOpacity(double currentOpacity) {
		this.currentOpacity = currentOpacity;
		OpenJWT.setElementOpacity(element, currentOpacity);
	}
	
	public void setTargetOpacity(double targetOpacity) {
		this.targetOpacity = targetOpacity;
		this.mode = MODE_TARGET;
	}

	public void loopTargetOpacity(double minTargetOpacity, double maxTargetOpacity) {
		this.minTargetOpacity = minTargetOpacity;
		this.maxTargetOpacity = maxTargetOpacity;
		this.targetOpacity = maxTargetOpacity;
		this.mode = MODE_LOOP;
	}
	
	public void update(int interpolation) {
		if (currentOpacity != targetOpacity) {
			double coef = interpolation / 1000.;
			
			if (hiddenWhenTransparent && currentOpacity == 0 && targetOpacity > 0)
				element.getStyle().setProperty("display", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			if (currentOpacity > targetOpacity) {
				currentOpacity -= increment * coef;
				if (currentOpacity <= targetOpacity) {
					currentOpacity = targetOpacity;
					if (hiddenWhenTransparent && currentOpacity == 0)
						element.getStyle().setProperty("display", "none"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else if (currentOpacity < targetOpacity) {
				currentOpacity += increment * coef;
				if (currentOpacity > targetOpacity)
					currentOpacity = targetOpacity;
			}
			
			OpenJWT.setElementOpacity(element, currentOpacity);
		}
		
		switch (mode) {
		case MODE_LOOP:
			if (currentOpacity == targetOpacity)
				targetOpacity = targetOpacity == minTargetOpacity ? maxTargetOpacity : minTargetOpacity;
			break;
		}
	}
	
	public boolean isFinished() {
		return false;
	}
	
	public void destroy() {
		element = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
