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

import fr.fg.client.data.HyperspaceSignatureData;
import fr.fg.client.data.ShipInfoData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;

public class HyperspaceSignatureItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private HyperspaceSignatureData hyperspaceSignatureData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public HyperspaceSignatureItem(
			HyperspaceSignatureData hyperspaceSignatureData,
			UIItemRenderingHints hints) {
		super(hyperspaceSignatureData.getX(),
				hyperspaceSignatureData.getY(), hints);
		
		this.hyperspaceSignatureData = hyperspaceSignatureData;
		
		setStylePrimaryName("hyperspaceSignature"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(hyperspaceSignatureData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		HyperspaceSignatureData newHyperspaceSignatureData =
			(HyperspaceSignatureData) newData;
		
		boolean updateData = false;
		
		// Cherche les modifications dans les estimations des
		// quantités de classes de vaisseaux
		if (hyperspaceSignatureData.hasClasses() &&
				newHyperspaceSignatureData.hasClasses()) {
			if (hyperspaceSignatureData.getClassesCount() !=
					newHyperspaceSignatureData.getClassesCount()) {
				updateData = true;
			} else {
				for (int k = 0; k < hyperspaceSignatureData.getClassesCount(); k++)
					if (hyperspaceSignatureData.getClassAt(k) !=
							newHyperspaceSignatureData.getClassAt(k)) {
						updateData = true;
						break;
					}
			}
		}
		
		// Cherche les modifications dans les estimations des
		// quantités de vaisseaux
		if (hyperspaceSignatureData.hasShips() &&
				newHyperspaceSignatureData.hasShips()) {
			if (hyperspaceSignatureData.getShipsCount() !=
					newHyperspaceSignatureData.getShipsCount()) {
				updateData = true;
			} else {
				for (int k = 0; k < hyperspaceSignatureData.getShipsCount(); k++)
					if (hyperspaceSignatureData.getShipAt(k).getId() !=
							newHyperspaceSignatureData.getShipAt(k).getId() ||
							hyperspaceSignatureData.getShipAt(k).getClasses() !=
								newHyperspaceSignatureData.getShipAt(k).getClasses()) {
						updateData = true;
						break;
					}
			}
		}
		
		if (updateData ||
			!hyperspaceSignatureData.getTreaty().equals(
					newHyperspaceSignatureData.getTreaty()) ||
			!hyperspaceSignatureData.getAllyTag().equals(
					newHyperspaceSignatureData.getAllyTag()) ||
			!hyperspaceSignatureData.getOwner().equals(
					newHyperspaceSignatureData.getOwner()) ||
			!hyperspaceSignatureData.getTreaty().equals(
					newHyperspaceSignatureData.getTreaty()) ||
			(!hyperspaceSignatureData.hasClasses() &&  newHyperspaceSignatureData.hasClasses()) ||
			( hyperspaceSignatureData.hasClasses() && !newHyperspaceSignatureData.hasClasses()) ||
			(!hyperspaceSignatureData.hasShips()   &&  newHyperspaceSignatureData.hasShips()) ||
			( hyperspaceSignatureData.hasShips()   && !newHyperspaceSignatureData.hasShips()))
			updateData(newHyperspaceSignatureData);
		
		this.hyperspaceSignatureData = newHyperspaceSignatureData;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(HyperspaceSignatureData hyperspaceSignatureData) {
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		String tooltip = "";
		
		if (hyperspaceSignatureData.hasOwner()) {
			tooltip += "<div class=\"owner-" + ((hyperspaceSignatureData.isAlliedHyperspaceSignature()
					&& !hyperspaceSignatureData.isAllyHyperspaceSignature())?
							"allied" : hyperspaceSignatureData.getTreaty()) + "\"><b>" + //$NON-NLS-1$ //$NON-NLS-2$
				(hyperspaceSignatureData.hasAllyTag() ? "[" + hyperspaceSignatureData.getAllyTag() + "] " : "") +
				hyperspaceSignatureData.getOwner() + "</b>" + //$NON-NLS-1$
				"</div>"; //$NON-NLS-1$
			
			// Estimation du nombre de vaisseaux
			if (hyperspaceSignatureData.hasShips()) {
				for (int i = 0; i < hyperspaceSignatureData.getShipsCount(); i++) {
					ShipInfoData shipData = hyperspaceSignatureData.getShipAt(i);
					
					if (shipData.getId() > 0)
						tooltip += "<div>" + FleetItem.getQualifier(shipData.getClasses(), //$NON-NLS-1$
							dynamicMessages.getString("ships" + //$NON-NLS-1$
								shipData.getId()).toLowerCase()) + "</div>"; //$NON-NLS-1$
				}
			}
			
			// Estimation du nombre de vaisseaux par classes de vaisseaux
			if (hyperspaceSignatureData.hasClasses()) {
				for (int i = 0; i < hyperspaceSignatureData.getClassesCount(); i++) {
					int classData = hyperspaceSignatureData.getClassAt(i);
					
					if (classData > 0)
						tooltip += "<div>" + FleetItem.getQualifier(classData, //$NON-NLS-1$
							dynamicMessages.getString("shipClasses" + //$NON-NLS-1$
								(i + 1))) + "</div>"; //$NON-NLS-1$
				}
			}
		}
		
		tooltip = "<div class=\"title\">" + messages.hyperspaceSignature() + "</div>" +
			"<div class=\"justify\">" + messages.hyperspaceSignatureDesc() + "</div>" +
			tooltip;
		
		setToolTipText(tooltip, 200);
	}
}
