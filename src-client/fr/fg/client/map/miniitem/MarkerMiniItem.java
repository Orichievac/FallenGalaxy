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

package fr.fg.client.map.miniitem;

import fr.fg.client.data.MarkerData;
import fr.fg.client.map.UIMiniItemRenderingHints;

public class MarkerMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private MarkerData markerData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public MarkerMiniItem(MarkerData markerData,
			UIMiniItemRenderingHints hints) {
		super(markerData.getX(), markerData.getY(), hints);
		
		this.markerData = markerData;
		
		if (markerData.isContractMarker())
			setStylePrimaryName("missionMarker"); //$NON-NLS-1$
		else
			setStylePrimaryName("marker"); //$NON-NLS-1$
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
			!markerData.getTreaty().equals(newMarkerData.getTreaty()))
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
		setToolTipText("<div class=\"justify owner-" + markerData.getPact() + //$NON-NLS-1$
				"\">" + markerData.getMessage() + "</div>", 240); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
