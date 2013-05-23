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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

import fr.fg.client.data.AbilityData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.WeaponGroupData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipManager;

public class TacticsAction extends FlowPanel {
	// ------------------------------------------------------- CONSTANTES -- //
	
	protected int index;
	
	protected boolean selected;
	
	protected int shipId, ability;
	
	protected long count;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	public TacticsAction(int index) {
		this.index = index;
		this.selected = false;
		this.shipId = -1;
		
		OpenJWT.setElementFloat(getElement(), "left"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on");
		setPixelSize(51, 101);
		
		sinkEvents(Event.ONCLICK);
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		setAction(shipId, count, ability);
	}
	
	public void setAction(int shipId, long count, int ability) {
		this.shipId = shipId;
		this.count = count;
		this.ability = ability;
		
		if (shipId == -1) {
			getElement().setInnerHTML("<div class=\"control" +
					(selected ? " selected" : "") + "\"></div>");
			
			ToolTipManager.getInstance().register(getElement(), "Aucune action");
		} else {
			ShipData ship = ShipData.SHIPS[shipId];
			getElement().setInnerHTML(
				"<div class=\"control" + (selected ? " selected" : "") + "\">" +
				"<div unselectable=\"on\" class=\"spaceship\" style=\"background-position: -" +
				(shipId * 50) + "px 0\">" + Formatter.formatNumber(count, true) +
				"</div></div><div class=\"ability\" style=\"margin-top: 1px; " +
				"background-position: -" + (50 * (ability == -1 ? 0 :
				AbilityData.GRAPHICS[ship.getAbilities()[ability].getType()])) +
				"px 0; clear: both;\"></div>"
			);
			
			DynamicMessages dynamicMessages =
				(DynamicMessages) GWT.create(DynamicMessages.class);
			
			// Tooltip
			String toolTip = "<div class=\"title\">" +
				dynamicMessages.getString("ship" + shipId) + "</div><div><b>";
			
			if (ability == -1) {
				toolTip += "Tir";
			} else {
				toolTip += ship.getAbilities()[ability].getName();
			}
			
			toolTip += "</b></div><div class=\"justify\">";
			
			if (ability == -1) {
				// Armement du vaisseau
				for (WeaponGroupData weaponGroup : ship.getWeapons()) {
					toolTip += "<div>" + weaponGroup.getCount() + "x " + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
						"weapon" + weaponGroup.getIdWeapon()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
						"<img class=\"stat s-damage\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
						"images/misc/blank.gif\"/> <b>" + weaponGroup.getWeapon().getDamageMin() + //$NON-NLS-1$
						"-" + weaponGroup.getWeapon().getDamageMax() + "</b></div>"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else {
				toolTip += ship.getAbilities()[ability].getDesc(shipId);
			}
			
			toolTip += "</div>";
			
			ToolTipManager.getInstance().register(getElement(), toolTip, 220);
		}
		
		Element indexElement = DOM.createDiv();
		indexElement.setClassName("actionIndex");
		indexElement.appendChild(
			TextManager.getText(String.valueOf(index + 1), true).getElement());
		getElement().getFirstChildElement().appendChild(indexElement);
	}
	
	public void setNoAction() {
		this.shipId = -1;
		
		setAction(shipId, count, ability);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
