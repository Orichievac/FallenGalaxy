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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import fr.fg.client.core.Utilities;
import fr.fg.client.data.GravityWellData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;

public class GravityWellItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private GravityWellData gravityWellData;
	
	private Element graphicsElement;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public GravityWellItem(GravityWellData gravityWellData,
			UIItemRenderingHints hints) {
		super(gravityWellData.getX(), gravityWellData.getY(), hints);
		
		this.gravityWellData = gravityWellData;
		
		setStylePrimaryName("gravityWell"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		graphicsElement = DOM.createDiv();
		graphicsElement.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		graphicsElement.setClassName("gravityWell-graphics");
		getElement().appendChild(graphicsElement);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText("<div class=\"title\">" + messages.gravityWell() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
			"<div class=\"justify\">" + messages.gravityWellDesc() + "</div>" +
			"<div class=\"emphasize\"><div class=\"float: right;\">" +
			gravityWellData.getEnergy() + "&nbsp;" +
			Utilities.getEnergyImage() +
			"</div>Energie maximum</div>", 200); //$NON-NLS-1$
		
		updateRendering();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.graphicsElement = null;
		this.gravityWellData = null;
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		updateRendering();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateRendering() {
		int offsetX = 0;
		if (hints.getZoom() == .5)
			offsetX = 160;
		else if (hints.getZoom() == .25 ||
				hints.getZoom() == .125)
			offsetX = 240;
		
		graphicsElement.getStyle().setProperty("backgroundPosition",
			"-" + offsetX + "px -" + (2238 +
			160 * gravityWellData.getGraphics()) + "px");
	}
}
