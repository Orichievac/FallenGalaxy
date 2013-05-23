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

package fr.fg.client.core.tactics;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.core.Client;
import fr.fg.client.data.ShipData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.ToolTipManager;

public class TacticsSlot extends Widget {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int shipId;
	private long count;
	private boolean front;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public TacticsSlot() {
		setElement(DOM.createDiv());
		
		OpenJWT.setElementFloat(getElement(), "left"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on");
		setPixelSize(51, 50);
		
		sinkEvents(Event.ONCLICK);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isFront() {
		return front;
	}
	
	public void setFront(boolean front) {
		this.front = front;
		
		updateGraphics();
	}
	
	public int getShipId() {
		return shipId;
	}
	
	public long getCount() {
		return count;
	}
	
	public void setShip(int shipId, long count, boolean front) {
		this.shipId = shipId;
		this.count = count;
		this.front = front;
		
		updateGraphics();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateGraphics() {
		if (shipId != 0) {
			getElement().setInnerHTML("<div class=\"control\">" +
				"<div unselectable=\"on\" class=\"" +
				(front ? "frontLine" : "backLine") + "\"></div>" +
				"<div unselectable=\"on\" class=\"spaceship\" style=\"" +
				"background-position: -" + (shipId * 50) + "px 0\">" +
				Formatter.formatNumber(count, true) + "</div></div>");
			
			ToolTipManager.getInstance().register(getElement(),
				ShipData.getDesc(shipId, count,
					Client.getInstance().getResearchManager(
					).getShipAvailableAbilities(shipId)) +
				"<div style=\"color: " +
				(front ? "#7aff01" : "#ff7901") + ";\">Position : <b>" +
				(front ? "Ligne de front" : "Arrière-ligne") +
				"</b></div>", 230);
		} else {
			getElement().setInnerHTML("<div class=\"control\"></div>");
			
			ToolTipManager.getInstance().unregister(getElement());
		}
	}
}
