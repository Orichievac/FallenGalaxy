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

import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.animation.TimerHandler;

public class LoopClassNameUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int MODE_TARGET = 1, MODE_LOOP = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Widget widget;
	
	private int mode;
	
	private String classPrefix;
	
	private double currentClass;
	
	private double targetClass;
	
	private double minTargetClass;
	
	private double maxTargetClass;
	
	private double increment;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public LoopClassNameUpdater(Widget widget, String classPrefix,
			double currentClass, double increment) {
		this.mode = MODE_TARGET;
		this.widget = widget;
		this.classPrefix = classPrefix;
		this.currentClass = currentClass;
		this.targetClass = currentClass;
		this.increment = increment;
		
		widget.addStyleName(classPrefix + (int) Math.floor(currentClass));
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setIncrement(double increment) {
		this.increment = increment;
	}

	public void setCurrentClass(double currentClass) {
		int startClass = (int) Math.floor(this.currentClass);
		this.currentClass = currentClass;
		int endClass = (int) Math.floor(currentClass);
		
		if (startClass != endClass) {
			widget.removeStyleName(classPrefix + startClass);
			widget.addStyleName(classPrefix + endClass);
		}
	}
	
	public void setTargetClass(double targetClass) {
		this.targetClass = targetClass;
		this.mode = MODE_TARGET;
	}
	
	public void loopTargetClass(double minTargetClass, double maxTargetClass) {
		this.minTargetClass = minTargetClass;
		this.maxTargetClass = maxTargetClass;
		this.targetClass = maxTargetClass;
		this.mode = MODE_LOOP;
	}
	
	public void update(int interpolation) {
		if (currentClass != targetClass) {
			double coef = interpolation / 1000.;
			int startClass = (int) Math.floor(currentClass);
			
			if (currentClass > targetClass) {
				currentClass -= increment * coef;
				if (currentClass < targetClass)
					currentClass = targetClass;
			} else if (currentClass < targetClass) {
				currentClass += increment * coef;
				if (currentClass > targetClass)
					currentClass = targetClass;
			}
			
			int endClass = (int) Math.floor(currentClass);
			
			if (startClass != endClass) {
				widget.removeStyleName(classPrefix + startClass);
				widget.addStyleName(classPrefix + endClass);
			}
		}
		
		switch (mode) {
		case MODE_LOOP:
			if (currentClass == targetClass)
				targetClass = targetClass == minTargetClass ? maxTargetClass : minTargetClass;
			break;
		}
	}
	
	public void destroy() {
		widget = null;
		classPrefix = null;
	}
	
	public boolean isFinished() {
		return false;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
