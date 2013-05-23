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

package fr.fg.client.map.miniitem;

import fr.fg.client.animation.LocationUpdater;
import fr.fg.client.map.UIMiniItem;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Point;

public class AnimatedMiniItem extends UIMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private LocationUpdater locationUpdater;
	
	private Point location, oldLocation, realLocation;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AnimatedMiniItem(int x, int y, UIMiniItemRenderingHints hints) {
		super(hints);
		setLocation(x, y, false);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Point getLocation() {
		return locationUpdater != null && !locationUpdater.isFinished() ?
				locationUpdater.getCurrentLocation() : realLocation;
	}
	
	public void setLocation(int x, int y) {
		setLocation(x, y, false);
	}
	
	public void setLocation(int x, int y, boolean animated) {
		stopTimers();
		
		this.oldLocation = location;
		this.location = new Point(x, y);
		
		if (animated) {
			this.realLocation = new Point(
				(int) (hints.getOffset().getX() + hints.getZoom() * (hints.getMargin().getX() + (int) Math.floor(location.getX() * hints.getScaleX()))),
				(int) (hints.getOffset().getY() + hints.getZoom() * (hints.getMargin().getY() + (int) Math.floor(location.getY() * hints.getScaleY())))
			);
			
			Point start = new Point(
				(int) (hints.getOffset().getX() + hints.getZoom() * (hints.getMargin().getX() + (int) Math.floor(oldLocation.getX() * hints.getScaleX()))),
				(int) (hints.getOffset().getY() + hints.getZoom() * (hints.getMargin().getY() + (int) Math.floor(oldLocation.getY() * hints.getScaleY())))
			);
			
			Point end = new Point(
				(int) (hints.getOffset().getX() + hints.getZoom() * (hints.getMargin().getX() + (int) Math.floor(location.getX() * hints.getScaleX()))),
				(int) (hints.getOffset().getY() + hints.getZoom() * (hints.getMargin().getY() + (int) Math.floor(location.getY() * hints.getScaleY())))
			);
			
			locationUpdater = new LocationUpdater(getElement(),
					start, end, new Point(start).subtract(end).norm() / 2);
			TimerManager.register(locationUpdater);
		} else {
			this.realLocation = new Point(
				(int) (hints.getOffset().getX() + hints.getZoom() * (hints.getMargin().getX() + (int) Math.floor(location.getX() * hints.getScaleX()))),
				(int) (hints.getOffset().getY() + hints.getZoom() * (hints.getMargin().getY() + (int) Math.floor(location.getY() * hints.getScaleY())))
			);
			
			getElement().getStyle().setProperty("left", realLocation.getX() + "px");
			getElement().getStyle().setProperty("top",  realLocation.getY() + "px");
		}
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		if (locationUpdater != null && !locationUpdater.isFinished()) {
			Point start = new Point(
				hints.getMargin().getX() + (int) Math.floor(oldLocation.getX() * hints.getScaleX()),
				hints.getMargin().getY() + (int) Math.floor(oldLocation.getY() * hints.getScaleY()));
			Point end = new Point(
				hints.getMargin().getX() + (int) Math.floor(location.getX() * hints.getScaleX()),
				hints.getMargin().getY() + (int) Math.floor(location.getY() * hints.getScaleY()));
			
			locationUpdater.setStart(start);
			locationUpdater.setEnd(end);
		} else {
			setLocation(location.getX(), location.getY());
		}
	}
	
	public boolean isAnimated() {
		return locationUpdater != null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopTimers();
		
		locationUpdater = null;
		location = null;
		oldLocation = null;
		realLocation = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void stopTimers() {
		if (locationUpdater != null) {
			TimerManager.unregister(locationUpdater);
			locationUpdater.destroy();
			locationUpdater = null;
		}
	}
}
