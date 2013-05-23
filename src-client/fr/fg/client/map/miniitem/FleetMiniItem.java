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

import fr.fg.client.data.FleetData;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.core.Point;

public class FleetMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private FleetData fleetData;
	
	private LineOfSight los;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetMiniItem(FleetData fleetData, UIMiniItemRenderingHints hints) {
		super(fleetData.getX(), fleetData.getY(), hints);
		
		this.fleetData = fleetData;
		
		updateData(fleetData);
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
		FleetData newFleetData = (FleetData) newData;
		
		if (fleetData.getX() != newFleetData.getX() ||
			fleetData.getY() != newFleetData.getY()) {
			setLocation(newFleetData.getX(), newFleetData.getY(), true);
			
			if (los != null)
				los.setLocation(newFleetData.getX(), newFleetData.getY(), true);
		}
		
		if (!fleetData.getTreaty().equals(newFleetData.getTreaty()) ||
			!fleetData.getOwner().equals(newFleetData.getOwner()) ||
			!fleetData.getAlly().equals(newFleetData.getAlly()) ||
			fleetData.getLineOfSight() != newFleetData.getLineOfSight())
			updateData(newFleetData);
		
		fleetData = newFleetData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		fleetData = null;
		los = null;
	}
	
	public void setBrightLineOfSight(boolean bright) {
		if (los != null && fleetData.isPlayerFleet())
			los.setType(bright ? LineOfSight.TYPE_OVER : LineOfSight.TYPE_PLAYER);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(FleetData fleetData) {
		getElement().getStyle().setProperty("display", fleetData.isReserved() ? "none" : "");
		
		getElement().setClassName("fleet fleet-" + //$NON-NLS-1$
				(fleetData.isPirateFleet() ?
				(fleetData.isAi() ? "pirate" : "enemy") :
				(fleetData.isNeutralFleet() &&
				!fleetData.getNpcAction().equals("none") || (
					fleetData.isNeutralFleet() &&
					fleetData.isAi() && !fleetData.isReserved()) ?
				"npc" : fleetData.getPact())));
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (fleetData.isPlayerFleet() ||
				fleetData.isAllyFleet()) {
			if (los == null) {
				los = new LineOfSight(new Point(fleetData.getX(),
					fleetData.getY()), getRenderingHints(), fleetData.getLineOfSight(),
					fleetData.isPlayerFleet() ?
					LineOfSight.TYPE_PLAYER : LineOfSight.TYPE_ALLY);
				
				if (isAttached())
					((Panel) getParent()).add(los);
			} else {
				los.setRadius(fleetData.getLineOfSight());
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
