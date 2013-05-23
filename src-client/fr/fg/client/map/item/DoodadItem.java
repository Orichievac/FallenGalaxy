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
import com.google.gwt.user.client.Element;

import fr.fg.client.data.DoodadData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;

public class DoodadItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private DoodadData doodadData;
	
	private Element doodad;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DoodadItem(DoodadData doodadData, UIItemRenderingHints hints) {
		super(doodadData.getX(), doodadData.getY(), hints);
		
		this.doodadData = doodadData;
		
		doodad = getElement();
		doodad.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		setStylePrimaryName("doodad"); //$NON-NLS-1$
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		setToolTipText("<div class=\"title\">" + dynamicMessages.getString( //$NON-NLS-1$
			"doodad" + doodadData.getType()) + "</div>" +
			"<div>" + messages.doodadDesc() + "</div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Hack pour décaler le background-position (FF ne reconnait pas
		// background-position-y)
		int y = hints.getZoom() == 1 ? -441 : -501;
		doodad.getStyle().setProperty("backgroundPosition", "-" + (int) Math.round( //$NON-NLS-1$ //$NON-NLS-2$
			doodadData.getType() * hints.getZoom() * 60) + "px " + y + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		DoodadData newDoodadData = (DoodadData) newData;
		
		if (doodadData.getX() != newDoodadData.getX() ||
			doodadData.getY() != newDoodadData.getY())
			setLocation(newDoodadData.getX(), newDoodadData.getY());
		
		doodadData = newDoodadData;
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		int y = hints.getZoom() == 1 ? -441 : -501;
		doodad.getStyle().setProperty("backgroundPosition", "-" + (int) Math.round( //$NON-NLS-1$ //$NON-NLS-2$
			doodadData.getType() * hints.getZoom() * 60) + "px " + y + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		doodadData = null;
		doodad = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
