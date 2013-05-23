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
import fr.fg.client.data.PlayerSectorData;
import fr.fg.client.empire.View;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class SectorView extends View {
	// --------------------------------------------------- CONSTANTES -- //
	// ---------------------------------------------------- ATTRIBUTS -- //
	
	private PlayerSectorData sectorData;
	
	// ------------------------------------------------ CONSTRUCTEURS -- //
	
	public SectorView(PlayerSectorData sectorData) {
		this.sectorData = sectorData;
		
		setStyleName("view sector");
		getElement().setAttribute("unselectable", "on");
		
		getElement().setInnerHTML(
			"<div class=\"type\" unselectable=\"on\"></div>"
		);
		
		OutlineText name = TextManager.getText(
				"<span class=\"name\" unselectable=\"on\">" +
				sectorData.getName() + "</span>");

		getElement().appendChild(name.getElement());
		
		setToolTipText("<div class=\"owner-player\"><b>" + sectorData.getName() + "</b> " +
			"(" + sectorData.getX() + ", " + sectorData.getY() + ")</div>");
		
		sinkEvents(Event.ONCLICK);
	}
	
	// ----------------------------------------------------- METHODES -- //
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			Client.getInstance().getGalaxyMap().showSector(
				GalaxyMap.MODE_DEFAULT, sectorData.getId());
			break;
		}
	}
	
	public void destroy() {
		sectorData = null;
	}
	
	// --------------------------------------------- METHODES PRIVEES -- //
}
