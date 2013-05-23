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

package fr.fg.client.empire.view;

import com.google.gwt.user.client.Event;

import fr.fg.client.core.Client;
import fr.fg.client.core.GalaxyMap;
import fr.fg.client.empire.View;

public class GalaxyView extends View {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public GalaxyView() {
		setStyleName("view galaxy");
		getElement().setAttribute("unselectable", "on");
		
		setToolTipText("Galaxie");
		
		getElement().setInnerHTML(
			"<div class=\"type\" unselectable=\"on\"></div>"
		);
		
		sinkEvents(Event.ONCLICK);
	}
	
	// ----------------------------------------------------- METHODES -- //

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			Client.getInstance().getGalaxyMap().showGalaxy(
				GalaxyMap.MODE_DEFAULT);
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
