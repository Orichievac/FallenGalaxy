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

import com.google.gwt.user.client.ui.Panel;

import fr.fg.client.data.SpaceStationData;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.core.Point;

public class SpaceStationMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private SpaceStationData spaceStationData;
	
	private LineOfSight los;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SpaceStationMiniItem(SpaceStationData spaceStationData,
			UIMiniItemRenderingHints hints) {
		super(spaceStationData.getX(), spaceStationData.getY(), hints);
		
		this.spaceStationData = spaceStationData;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(spaceStationData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onLoad() {
		if (los != null)
			((Panel) getParent()).add(los);
	}
	
	@Override
	public void onUnload() {
		if (los != null)
			los.removeFromParent();
	}
	
	@Override
	public void onDataUpdate(Object newData) {
		SpaceStationData newSpaceStationData = (SpaceStationData) newData;
		
		if (!spaceStationData.getTreaty().equals(newSpaceStationData.getTreaty()))
			updateData(newSpaceStationData);
		
		spaceStationData = newSpaceStationData;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(SpaceStationData spaceStationData) {
		getElement().setClassName("spaceStation spaceStation-" +
				spaceStationData.getPact()); //$NON-NLS-1$
		
		if (spaceStationData.isAllySpaceStation()) {
			if (los == null) {
				los = new LineOfSight(new Point(spaceStationData.getX(),
					spaceStationData.getY()), getRenderingHints(),
					spaceStationData.getLineOfSight(),
					LineOfSight.TYPE_ALLY);
				
				if (isAttached())
					((Panel) getParent()).add(los);
			}
		} else {
			if (los != null) {
				if (isAttached())
					los.removeFromParent();
				
				los = null;
			}
		}
	}
}
