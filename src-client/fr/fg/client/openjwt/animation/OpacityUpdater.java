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

package fr.fg.client.openjwt.animation;

import com.google.gwt.user.client.Element;

import fr.fg.client.openjwt.OpenJWT;

public class OpacityUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		NO_ACTION_WHEN_TRANSPARENT = 0,
		HIDE_WHEN_TRANSPARENT = 1,
		REMOVE_WHEN_TRANSPARENT = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element element;
	
	private double currentOpacity;
	
	private double targetOpacity;
	
	private double increment;
	
	private int actionWhenTransparent;
	
	private boolean destroyOnTarget;
	
	private boolean finished;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public OpacityUpdater(Element element, double currentOpacity,
			double increment, int actionWhenTransparent) {
		this.element = element;
		this.currentOpacity = currentOpacity;
		this.targetOpacity = currentOpacity;
		this.increment = increment;
		this.actionWhenTransparent = actionWhenTransparent;
		
		OpenJWT.setElementOpacity(element, currentOpacity);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public double getTargetOpacity() {
		return targetOpacity;
	}
	
	public void setIncrement(double increment) {
		this.increment = increment;
	}
	
	public void setTargetOpacity(double targetOpacity) {
		this.setTargetOpacity(targetOpacity, false);
	}
	
	public void setTargetOpacity(double targetOpacity, boolean destroyOnTarget) {
		this.targetOpacity = targetOpacity;
		this.destroyOnTarget = destroyOnTarget;
	}
	
	public void update(int interpolation) {
		boolean finished = false;
		
		if (currentOpacity != targetOpacity) {
			double coef = interpolation / 1000.;
			
			if (actionWhenTransparent == HIDE_WHEN_TRANSPARENT &&
					currentOpacity == 0 && targetOpacity > 0)
				element.getStyle().setProperty("display", "");
			
			if (currentOpacity > targetOpacity) {
				currentOpacity -= increment * coef;
				if (currentOpacity <= targetOpacity) {
					currentOpacity = targetOpacity;
					if (actionWhenTransparent == HIDE_WHEN_TRANSPARENT &&
							currentOpacity == 0)
						element.getStyle().setProperty("display", "none");
					if (actionWhenTransparent == REMOVE_WHEN_TRANSPARENT &&
							currentOpacity == 0 &&
							element.getParentElement() != null)
						element.getParentElement().removeChild(element);
					if (destroyOnTarget)
						finished = true;
				}
			} else if (currentOpacity < targetOpacity) {
				currentOpacity += increment * coef;
				if (currentOpacity > targetOpacity) {
					currentOpacity = targetOpacity;
					if (destroyOnTarget)
						finished = true;
				}
			}
			
			OpenJWT.setElementOpacity(element, currentOpacity);
		} else {
			if (destroyOnTarget)
				finished = true;
		}
		
		this.finished = finished;
		
		if (finished)
			element = null;
	}
	
	public void destroy() {
		element = null;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
