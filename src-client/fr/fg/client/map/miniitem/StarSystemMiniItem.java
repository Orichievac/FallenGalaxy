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

import fr.fg.client.data.StarSystemData;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.core.Point;

public class StarSystemMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private StarSystemData systemData;
	
	private LineOfSight los;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StarSystemMiniItem(StarSystemData systemData,
			UIMiniItemRenderingHints hints) {
		super(systemData.getX(), systemData.getY(), hints);
		
		this.systemData = systemData;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(systemData);
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
		StarSystemData newSystemData = (StarSystemData) newData;
		
		if (!systemData.getTreaty().equals(newSystemData.getTreaty()))
			updateData(newSystemData);
		
		systemData = newSystemData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		systemData = null;
		los = null;
	}
	
	public void setBrightLineOfSight(boolean bright) {
		if (los != null && systemData.isPlayerStarSystem())
			los.setType(bright ? LineOfSight.TYPE_OVER : LineOfSight.TYPE_PLAYER);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(StarSystemData systemData) {
		getElement().setClassName("system system-" + systemData.getPact());
		
		if (systemData.isPlayerStarSystem() ||
				systemData.isAllyStarSystem()) { 
			if (los == null) {
				los = new LineOfSight(new Point(systemData.getX(),
					systemData.getY()), getRenderingHints(), systemData.getLineOfSight(),
					systemData.isPlayerStarSystem() ?
					LineOfSight.TYPE_PLAYER : LineOfSight.TYPE_ALLY);
				
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
