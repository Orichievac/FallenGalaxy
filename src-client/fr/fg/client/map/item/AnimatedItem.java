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

package fr.fg.client.map.item;

import fr.fg.client.animation.LocationUpdater;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Point;

public class AnimatedItem extends UIItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ItemLocationUpdater locationUpdater;
	
	private Point oldLocation, location, realLocation;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AnimatedItem(int x, int y, UIItemRenderingHints hints) {
		super(hints);
		this.locationUpdater = null;
		setLocation(x, y);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Point getLocation() {
		if (locationUpdater != null && !locationUpdater.isFinished()) {
			Point location = new Point(locationUpdater.getCurrentLocation());
			
			location.setX((int) Math.round(location.getX() * hints.getZoom()));
			location.setY((int) Math.round(location.getY() * hints.getZoom()));
			
			return location;
		}
		
		return realLocation;
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
				(int) Math.floor(location.getX() * hints.getTileSize()),
				(int) Math.floor(location.getY() * hints.getTileSize()));
			
			Point start = new Point(
				(int) Math.floor(oldLocation.getX() * hints.getTileSize() * hints.getZoom()),
				(int) Math.floor(oldLocation.getY() * hints.getTileSize() * hints.getZoom()));
			Point end = new Point(
				(int) Math.floor(location.getX() * hints.getTileSize() * hints.getZoom()),
				(int) Math.floor(location.getY() * hints.getTileSize() * hints.getZoom()));
			
			locationUpdater = new ItemLocationUpdater(this,
				start, end, new Point(start).subtract(end).norm() / 2);
			TimerManager.register(locationUpdater);
		} else {
			this.realLocation = new Point(
				(int) Math.floor(location.getX() * hints.getTileSize()),
				(int) Math.floor(location.getY() * hints.getTileSize()));
			
			getElement().getStyle().setProperty("left", (int) Math.floor(
				x * hints.getTileSize() * hints.getZoom()) + "px");
			getElement().getStyle().setProperty("top",  (int) Math.floor(
				y * hints.getTileSize() * hints.getZoom()) + "px");
		}
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		if (locationUpdater != null && !locationUpdater.isFinished()) {
			Point start = new Point(
				(int) Math.floor(oldLocation.getX() * hints.getTileSize() * hints.getZoom()),
				(int) Math.floor(oldLocation.getY() * hints.getTileSize() * hints.getZoom()));
			Point end = new Point(
				(int) Math.floor(location.getX() * hints.getTileSize() * hints.getZoom()),
				(int) Math.floor(location.getY() * hints.getTileSize() * hints.getZoom()));
			
			locationUpdater.setStart(start);
			locationUpdater.setEnd(end);
			locationUpdater.setSpeed(new Point(start).subtract(end).norm() / 2);
		} else {
			setLocation(location.getX(), location.getY());
		}
	}
	
	public boolean isAnimated() {
		return locationUpdater != null && !locationUpdater.isFinished();
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
	
	private class ItemLocationUpdater extends LocationUpdater {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private UIItem item;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ItemLocationUpdater(UIItem item, Point start, Point end,
				double speed) {
			super(item.getElement(), start, end, speed);
			
			this.item = item;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public void update(int interpolation) {
			super.update(interpolation);
			
			item.updateCulledState();
		}
		
		@Override
		public void destroy() {
			super.destroy();
			
			item = null;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
