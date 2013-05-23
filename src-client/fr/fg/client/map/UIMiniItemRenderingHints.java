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

package fr.fg.client.map;

import fr.fg.client.openjwt.core.Point;

public class UIMiniItemRenderingHints {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private double scaleX, scaleY;
	
	private double zoom;
	
	private Point margin;
	
	private Point offset;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public UIMiniItemRenderingHints(double scaleX, double scaleY, double zoom,
			Point offset, Point margin) {
		super();
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.margin = margin;
		this.zoom = zoom;
		this.offset = offset;
	}

	// --------------------------------------------------------- METHODES -- //

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public Point getMargin() {
		return margin;
	}

	public void setMargin(Point margin) {
		this.margin = margin;
	}

	public Point getOffset() {
		return offset;
	}

	public void setOffset(Point offset) {
		this.offset = offset;
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
