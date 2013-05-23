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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;


public class ClassNameUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element element;
	
	private String primaryStyleName;
	
	private String classPrefix;
	
	private double currentClass;
	
	private double targetClass;
	
	private double increment;
	
	private boolean destroyOnTarget;
	
	private boolean finished;
	
	private String lastStyleName;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ClassNameUpdater(Widget widget, String classPrefix,
			double currentClass, double increment) {
		this(widget.getElement(), widget.getStylePrimaryName(), classPrefix,
				currentClass, increment);
	}
	
	public ClassNameUpdater(Element element, String primaryStyleName,
			String classPrefix, double currentClass, double increment) {
		this.element = element;
		this.primaryStyleName = primaryStyleName;
		this.classPrefix = classPrefix;
		this.currentClass = currentClass;
		this.targetClass = currentClass;
		this.increment = increment;
		this.lastStyleName = primaryStyleName + "-" + classPrefix +
			(int) Math.floor(currentClass);
		
		element.setClassName(element.getClassName() + " " + lastStyleName);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setIncrement(double increment) {
		this.increment = increment;
	}
	
	public void setTargetClass(double targetClass) {
		setTargetClass(targetClass, false);
	}
	
	public void setTargetClass(double targetClass, boolean destroyOnTarget) {
		this.targetClass = targetClass;
		this.destroyOnTarget = destroyOnTarget;
	}
	
	public void setCurrentClass(double currentClass) {
		this.currentClass = currentClass;
		
		element.setClassName(primaryStyleName + " " + primaryStyleName +
				"-" + classPrefix + (int) Math.floor(this.currentClass));
	}
	
	public void update(int interpolation) {
		boolean finished = false;
		
		if (currentClass != targetClass) {
			double coef = interpolation / 1000.;
			int startClass = (int) Math.floor(currentClass);
			
			if (currentClass > targetClass) {
				currentClass -= increment * coef;
				if (currentClass < targetClass) {
					currentClass = targetClass;
					if (destroyOnTarget)
						finished = true;
				}
			} else if (currentClass < targetClass) {
				currentClass += increment * coef;
				if (currentClass > targetClass) {
					currentClass = targetClass;
					if (destroyOnTarget)
						finished = true;
				}
			}
			
			int endClass = (int) Math.floor(currentClass);
			
			if (startClass != endClass) {
				removeStyleName(element, lastStyleName);
				lastStyleName = primaryStyleName + "-" + classPrefix +
						(int) Math.floor(endClass);
				element.setClassName(element.getClassName() + " " +
						lastStyleName);
			}
		} else {
			if (destroyOnTarget)
				finished = true;
		}
		
		this.finished = finished;
		
		if (finished) {
			removeStyleName(element, lastStyleName);
			element = null;
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	@Override
	public String toString() {
		return "ClassNameUpdater [widget=" + element.getInnerHTML() +
			",classes:" + currentClass + ">" + targetClass +
			",destroy:" + destroyOnTarget + "]";
	}
	
	public void destroy() {
		element = null;
		primaryStyleName = null;
		classPrefix = null;
		lastStyleName = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void removeStyleName(Element element, String style) {
		// Code extrait de com.google.gwt.user.client.ui.UIObject
		style = style.trim();
		if (style.length() == 0)
			throw new IllegalArgumentException("Style names cannot be empty");
		
		// Get the current style string.
		String oldStyle = element.getClassName();
		int index = oldStyle.indexOf(style);
		
		// Calculate matching index.
		while (index != -1) {
			if (index == 0 || oldStyle.charAt(index - 1) == ' ') {
				int last = index + style.length();
				int lastPos = oldStyle.length();
				if ((last == lastPos) || ((last < lastPos) &&
						(oldStyle.charAt(last) == ' '))) {
					break;
				}
			}
			index = oldStyle.indexOf(style, index + 1);
		}
		
		// Don't try to remove the style if it's not there.
		if (index != -1) {
			// Get the leading and trailing parts, without the removed name.
			String begin = oldStyle.substring(0, index).trim();
			String end = oldStyle.substring(index + style.length()).trim();
			
			// Some contortions to make sure we don't leave extra spaces.
			String newClassName;
			if (begin.length() == 0) {
				newClassName = end;
			} else if (end.length() == 0) {
				newClassName = begin;
			} else {
				newClassName = begin + " " + end;
			}
			
			element.setClassName(newClassName);
		}
	}
}
