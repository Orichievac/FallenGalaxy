/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc, Thierry Chevalier

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

import fr.fg.client.core.Utilities;
import fr.fg.client.data.MarkerData;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.map.item.AnimatedItem;

public class MarkerItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static String
	VISIBILITY_PLAYER = "player",
	VISIBILITY_ALLY = "ally",
	VISIBILITY_ALLIED = "allied";

	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private MarkerData markerData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public MarkerItem(MarkerData markerData, UIItemRenderingHints hints) {
		super(markerData.getX(), markerData.getY(), hints);
		
		this.markerData = markerData;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(markerData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		MarkerData newMarkerData = (MarkerData) newData;
		
		if (markerData.getX() != newMarkerData.getX() ||
			markerData.getY() != newMarkerData.getY())
			setLocation(newMarkerData.getX(), newMarkerData.getY());
		
		if (!markerData.getMessage().equals(newMarkerData.getMessage()) ||
			!markerData.getTreaty().equals(newMarkerData.getPact()) ||
			!markerData.getAlly().equals(newMarkerData.getAlly()) ||
			!markerData.getOwner().equals(newMarkerData.getOwner()) ||
			!markerData.getContract().equals(newMarkerData.getContract()))
			updateData(newMarkerData);
		
		markerData = newMarkerData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		markerData = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(MarkerData markerData) {
		setStyleName("marker" + (markerData.isContractMarker() ? " missionMarker" : "")); //$NON-NLS-1$
		
		if (markerData.isContractMarker()) {
			setToolTipText("<div class=\"missionMarkerName\">" + //$NON-NLS-1$ //$NON-NLS-2$
				markerData.getContract() + "</div>" + //$NON-NLS-1$
				"<div class=\"justify\">" + markerData.getMessage() + "</div>", 240);
		} else {
			String visibility = new String();
			if (markerData.getVisibility().equals(VISIBILITY_PLAYER))
				visibility = "vous seulement";
			else if (markerData.getVisibility().equals(VISIBILITY_ALLY))
				visibility = "les alliés";
			else if (markerData.getVisibility().equals(VISIBILITY_ALLIED))
				visibility = "la coalition";

			setToolTipText("<div class=\"owner-" + markerData.getPact() + "\">" + //$NON-NLS-1$ //$NON-NLS-2$
				"<b>" + markerData.getOwner() + "</b>" + (markerData.hasAlly() ? //$NON-NLS-1$ //$NON-NLS-2$
				" (" + markerData.getAlly() + ")" : "") + "<br />Visible par "+visibility+"</div>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"<div class=\"justify\">" + Utilities.parseSmilies(markerData.getMessage()) + "</div>", 240);
		}
	}
}
