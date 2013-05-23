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

package fr.fg.client.core.tactics;

import com.google.gwt.user.client.Event;

import fr.fg.client.data.ShipData;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class FleetScheme extends JSRowLayout {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private TacticsSlot[] slots;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetScheme(boolean editable) {
		JSLabel schemeHeader = new JSLabel("<b unselectable=\"on\">Position des vaisseaux</b>");
		schemeHeader.setPixelWidth(270);
		
		this.slots = new TacticsSlot[5];
		for (int i = 0; i < slots.length; i++)
			if (editable) {
				slots[i] = new TacticsSlot() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (event.getTypeInt()) {
						case Event.ONCLICK:
							if (getShipId() == -1)
								return;
							
							// Pour les cargos, échange la position avec un
							// autre cargo
							if (ShipData.SHIPS[getShipId()].getShipClass() ==
										ShipData.FREIGHTER) {
								for (TacticsSlot slot : slots) {
									if (slot.getShipId() != getShipId() &&
											slot.getShipId() != 0 &&
											ShipData.SHIPS[slot.getShipId()].getShipClass() ==
											ShipData.FREIGHTER &&
											slot.isFront() != isFront()) {
										slot.setFront(isFront());
										setFront(!isFront());
										break;
									}
								}
								return;
							}
							
							int frontSlots = 0, backSlots = 0;
							for (TacticsSlot slot : slots) {
								if (slot.getShipId() != 0) {
									if (slot.isFront())
										frontSlots++;
									else
										backSlots++;
								}
							}
							
							if (isFront() && (frontSlots - 1 < backSlots + 1))
								return;
							
							setFront(!isFront());
							break;
						}
					}
				};
			} else {
				slots[i] = new TacticsSlot();
			}
		
		addComponent(JSRowLayout.createHorizontalSeparator(51));
		addComponent(schemeHeader);
		addRow();
		addComponent(JSRowLayout.createHorizontalSeparator(51));
		for (int i = 0; i < slots.length; i++)
			addComponent(slots[i]);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isFront(int index) {
		return slots[index].isFront();
	}
	
	public void setFront(int index, boolean front) {
		slots[index].setFront(front);
	}
	
	public int getShipId(int index) {
		return slots[index].getShipId();
	}
	
	public long getCount(int index) {
		return slots[index].getCount();
	}
	
	public void setShip(int index, int shipId, long count, boolean front) {
		slots[index].setShip(shipId, count, front);
	}
	
	public int getPower() {
		int power = 0;
		for (int i = 0; i < slots.length; i++)
			power += slots[i].getShipId() != 0 ?
					ShipData.CLASSES_POWER[ShipData.SHIPS[slots[i].getShipId()].getShipClass()] *
					slots[i].getCount() : 0;
		return power;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
