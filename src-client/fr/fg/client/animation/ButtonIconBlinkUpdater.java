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

public class ButtonIconBlinkUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Widget widget;
	
	private int visibleLength, invisibleLength;
	
	private int time;
	
	private boolean visible;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ButtonIconBlinkUpdater(Widget widget, int visibleLength, int invisibleLength) {
		this.widget = widget;
		this.visibleLength = visibleLength;
		this.invisibleLength = invisibleLength;
		widget.removeStyleName("blinking"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void update(int interpolation) {
		time += interpolation;
		if (visible && time >= visibleLength) {
			time -= visibleLength;
			visible = false;
			widget.addStyleName("blinking");
		} else if (!visible && time >= invisibleLength) {
			time -= invisibleLength;
			visible = true;
			widget.removeStyleName("blinking");
		}
	}
	
	public boolean isFinished() {
		return false;
	}
	
	public void destroy() {
		widget = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
