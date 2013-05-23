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

import fr.fg.client.data.WardData;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.core.Point;

public class WardMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private WardData wardData;
	
	private LineOfSight los;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public WardMiniItem(WardData wardData, UIMiniItemRenderingHints hints) {
		super(wardData.getX(), wardData.getY(), hints);
		
		this.wardData = wardData;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(wardData);
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
		WardData newWardData = (WardData) newData;
		
		if (!wardData.getTreaty().equals(newWardData.getTreaty()) ||
			wardData.getLineOfSight() != newWardData.getLineOfSight())
			updateData(newWardData);
		
		wardData = newWardData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		wardData = null;
		los = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(WardData wardData) {
		getElement().setClassName("ward ward-" + wardData.getTreaty() +
				" ward-" + wardData.getType().replace("_", "-")); //$NON-NLS-1$
		
		if (wardData.getType().startsWith("observer") &&
				(wardData.isPlayerWard() ||
				wardData.isAllyWard())) { //$NON-NLS-1$
			if (los == null) {
				los = new LineOfSight(new Point(wardData.getX(),
					wardData.getY()), getRenderingHints(),
					wardData.getLineOfSight(),
					wardData.isPlayerWard() ?
					LineOfSight.TYPE_PLAYER : LineOfSight.TYPE_ALLY);
				
				if (isAttached())
					((Panel) getParent()).add(los);
			} else {
				los.setRadius(wardData.getLineOfSight());
			}
			getElement().getStyle().setProperty("display",  "");
		} else {
			if (los != null) {
				if (isAttached())
					los.removeFromParent();
				
				los = null;
			}
			getElement().getStyle().setProperty("display", "none");
		}
	}
}
