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
import fr.fg.client.openjwt.core.Point;

public class BackgroundUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int MODE_SINGLE = 1, MODE_LOOP = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int mode;
	
	private Element element;
	
	private Point start, current, end, increment;
	
	private double currentOffset;
	
	private double speed;
	
	private boolean finished;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BackgroundUpdater(Element element, Point start, Point end,
			Point increment) {
		this(element, start, end, increment, .01);
	}
	
	public BackgroundUpdater(Element element, Point start, Point end,
			Point increment, double speed) {
		this.element = element;
		this.start = start;
		this.current = new Point(start);
		this.end = end;
		this.increment = increment;
		this.speed = speed;
		this.mode = MODE_LOOP;
		this.finished = false;
		
		element.getStyle().setProperty("backgroundPosition", "-" + //$NON-NLS-1$ //$NON-NLS-2$
				current.getX() + "px -" + current.getY() + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setCurrentOffset(double offset) {
		currentOffset = offset;
		update(0);
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void destroy() {
		finished = true;
		element = null;
		start = null;
		current = null;
		end = null;
		increment = null;
	}
	
	public void update(int interpolation) {
		currentOffset += interpolation * speed;
		current.addX(increment.getX() * (int) Math.floor(currentOffset));
		current.addY(increment.getY() * (int) Math.floor(currentOffset));
		currentOffset -= (int) Math.floor(currentOffset);
		
		if (current.getX() > end.getX() || current.getY() > end.getY()) {
			if (mode == MODE_SINGLE) {
				finished = true;
			} else {
				current = new Point(start);
			}
		}
		
		if (!finished)
			element.getStyle().setProperty("backgroundPosition", "-" + //$NON-NLS-1$ //$NON-NLS-2$
				current.getX() + "px -" + current.getY() + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public boolean isFinished() {
		return finished;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getCurrent() {
		return current;
	}

	public void setCurrent(Point current) {
		this.current = current;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
