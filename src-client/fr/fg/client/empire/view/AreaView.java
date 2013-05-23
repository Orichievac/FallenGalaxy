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
import fr.fg.client.data.AllyData;
import fr.fg.client.data.PlayerAreaData;
import fr.fg.client.empire.View;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class AreaView extends View {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idArea;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AreaView(PlayerAreaData areaData) {
		this.idArea = areaData.getId();
		
		setStyleName("view area");
		getElement().setAttribute("unselectable", "on");
		
		String dominationImage = "<img class=\"domination\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" unselectable=\"on\"/>";
		
		getElement().setInnerHTML(
			"<div class=\"type\" unselectable=\"on\"></div>"
		);
		
		AllyData ally = Client.getInstance().getAllyDialog().getAlly();
		
		OutlineText name = TextManager.getText(
			"<span class=\"name\" unselectable=\"on\">" +
			(areaData.hasDomination() && ally.getId() != 0 &&
				areaData.getDomination().equals(ally.getName()) ? dominationImage : "") +
			areaData.getName() + (areaData.hasDomination() ?
			"&nbsp;&nbsp;" : "") + "</span>");
		
		getElement().appendChild(name.getElement());
		
		// Tooltip
		String tooltip = "<div class=\"owner-player\"><b>" + areaData.getName() + "</b> " +
			"(" + areaData.getX() + ", " + areaData.getY() + ")</div>" +
			 "</b></div>";
//		String tooltip = "<div class=\"owner-player\"><b>" + areaData.getName() + "</b> " +
//		"(" + areaData.getX() + ", " + areaData.getY() + ")</div>" +
//		"<div>Stations spatiales : <b>" + areaData.getSpaceStationsCount() + "/" +
//		areaData.getMaxSpaceStations() + "</b></div>";
		
		if (areaData.hasDomination())
			tooltip += "<div>" + dominationImage + " Domination <b>" +
				areaData.getDomination() + "</b>";
		
		setToolTipText(tooltip);
		
		sinkEvents(Event.ONCLICK);
	}
	
	// ----------------------------------------------------- METHODES -- //
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			Client.getInstance().getAreaContainer().setIdArea(idArea);
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
