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

package fr.fg.client.openjwt.core;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;


public class BaseWidget extends Widget {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BaseWidget() {
		setElement(DOM.createDiv());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setToolTipText(String toolTipText) {
		setToolTipText(toolTipText, -1);
	}
	
	public void setToolTipText(String toolTipText, int toolTipWidth) {
		if (toolTipText == null)
			ToolTipManager.getInstance().unregister(getElement());
		else
			ToolTipManager.getInstance().register(
					getElement(), toolTipText, toolTipWidth);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
