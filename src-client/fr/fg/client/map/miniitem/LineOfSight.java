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

import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;

public class LineOfSight extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_PLAYER = "player",
		TYPE_ALLY = "ally",
		TYPE_OVER = "over";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	
	private int radius;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public LineOfSight(Point location,
			UIMiniItemRenderingHints hints, int radius, String type) {
		super(location.getX(), location.getY(), hints);
		
		this.radius = radius;
		this.type = type;
		
		updateData();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		updateData();
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
		
		updateData();
	}
	
	public void setType(String type) {
		this.type = type;
		
		updateData();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	public void updateData() {
		int width = (int) Math.round(hints.getZoom() * (2 * radius + 1) * getRenderingHints().getScaleX());
		int height = (int) Math.round(hints.getZoom() * (2 * radius + 1) * getRenderingHints().getScaleY());
		
		getElement().setClassName("lineOfSight lineOfSight-" + type);
		getElement().getStyle().setProperty("background", "url('" + Config.getServerUrl() + "los/" + width + "-" + height + "-" + type + ".png') 0 0 no-repeat");
		
		getElement().getStyle().setProperty("margin",
			"-" + (int) Math.ceil(hints.getZoom() * radius * getRenderingHints().getScaleY()) + "px 0 0 " +
			"-" + (int) Math.ceil(hints.getZoom() * radius * getRenderingHints().getScaleX()) + "px");
		
		setSize(width + "px", height + "px");
	}
}
