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

import com.google.gwt.user.client.ui.AbsolutePanel;

import fr.fg.client.map.UIItemRenderingHints;
import gwt.canvas.client.Canvas;

public class DrawableItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Canvas canvas;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DrawableItem(int x, int y, UIItemRenderingHints hints) {
		super(x, y, hints);
		
		this.canvas = null;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		hideCanvas();
	}
	
	public void drawCircle(int x, int y, int radius, String color) {
		drawCircles(x, y, new int[]{radius}, new String[]{color});
	}
	
	public void drawCircles(int x, int y, int[] radius, String[] colors) {
		hideCanvas();
		
		if (!isAttached())
			return;
		
		int coef = (int) (hints.getTileSize() * hints.getZoom());
		
		int maxRadius = radius[0];
		for (int i = 1; i < radius.length; i++)
			if (maxRadius < radius[i])
				maxRadius = radius[i];
		
		canvas = new Canvas(
			(int) (2 * (maxRadius + .5) * coef + 4),
			(int) (2 * (maxRadius + .5) * coef + 4));
		canvas.getElement().setId("canvas");
		canvas.getElement().setAttribute("unselectable", "on");
		canvas.setBackgroundColor(Canvas.TRANSPARENT);
		
		for (int i = 0; i < radius.length; i++) {
			canvas.beginPath();
			canvas.arc(
				2 + (int) Math.floor((maxRadius + .5) * coef),
				2 + (int) Math.floor((maxRadius + .5) * coef),
				(int) Math.floor((radius[i] + .5) * coef),
				0, 2 * Math.PI, true);
			canvas.closePath();
			canvas.setStrokeStyle(colors[i]);
			canvas.setLineWidth(3);
			canvas.stroke();
		}
		
		((AbsolutePanel) getParent()).add(canvas, (x - maxRadius) * coef - 2, (y - maxRadius) * coef - 2);
	}
	
	public void hideCanvas() {
		if (canvas != null) {
			canvas.clear();
			canvas.removeFromParent();
			canvas = null;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
