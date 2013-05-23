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

import fr.fg.client.data.ContractMarkerData;
import fr.fg.client.map.UIMiniItemRenderingHints;

public class MissionMarkerMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ContractMarkerData missionMarkerData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public MissionMarkerMiniItem(ContractMarkerData missionMarkerData,
			UIMiniItemRenderingHints hints) {
		super(missionMarkerData.getX(), missionMarkerData.getY(), hints);
		
		this.missionMarkerData = missionMarkerData;
		
		setStylePrimaryName("missionMarker"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// --------------------------------------------------------- METHODES -- //

	@Override
	public void onDataUpdate(Object newData) {
		ContractMarkerData newMissionMarkerData = (ContractMarkerData) newData;
		
		if (missionMarkerData.getX() != newMissionMarkerData.getX() ||
				missionMarkerData.getY() != newMissionMarkerData.getY())
			setLocation(newMissionMarkerData.getX(), newMissionMarkerData.getY());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		missionMarkerData = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
