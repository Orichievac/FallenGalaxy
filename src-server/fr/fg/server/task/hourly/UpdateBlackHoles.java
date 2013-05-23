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

package fr.fg.server.task.hourly;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Event;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StellarObject;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.BlackHoleLossesEvent;

public class UpdateBlackHoles extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateBlackHoles (hourly)");
		List<StellarObject> blackholes = new ArrayList<StellarObject>(
				DataAccess.getObjectsByType(StellarObject.TYPE_BLACKHOLE));
		
		for (StellarObject blackhole : blackholes) {
			Area area = blackhole.getArea();
			Rectangle bounds =
				new Rectangle(blackhole.getBounds());
			
			// Endommage les flottes qui stationnent au dessus du trou noir
			List<Fleet> fleets = new ArrayList<Fleet>(area.getFleets());
			
			for (Fleet fleet : fleets) {
				if (bounds.contains(fleet.getCurrentX(), fleet.getCurrentY()) &&
						!fleet.getOwner().isAi()){
					damageFleet(fleet);
				
				Event event = new Event(
						Event.EVENT_FLEET_CAPTURED_BLACKHOLE,
						Event.TARGET_PLAYER,
						fleet.getIdOwner(), 
						fleet.getIdCurrentArea(), 
						fleet.getCurrentX(), 
						fleet.getCurrentY(),
						fleet.getName()
						);
					event.save();
					
					UpdateTools.queueNewEventUpdate(fleet.getIdOwner());
				}
			}
		}
		// TODO update
		
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void damageFleet(Fleet fleet) {
		ItemContainer fleetContainer =
			DataAccess.getItemContainerByFleet(fleet.getId());
		
		double fleetPayload= fleet.getPayload();
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			
			long lossesValue = 0;
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
				Slot slot = fleet.getSlot(i);
				
				// % de vaisseaux perdus
				double losses = GameConstants.BLACKHOLE_MIN_DAMAGE / 100 +
					Math.random() * (GameConstants.BLACKHOLE_MAX_DAMAGE -
							GameConstants.BLACKHOLE_MIN_DAMAGE) / 100;
				
				// Note : le ceil garantit qu'il reste au moins 1 vaisseau
				// dans la flotte
				if (slot.getShip() != null) {
					lossesValue += (slot.getCount() -
						Math.ceil(slot.getCount() * (1 - losses))) *
						slot.getShip().getPower();
					
					slot.setCount(Math.ceil(slot.getCount() * (1 - losses)));
					fleet.setSlot(slot, i);
				}
			}
			
			
			// Vérifie si les flottes n'ont pas plus d'item que possible
			fleet.updateContainerMax();
			
			
			fleet.save();
			
			
			GameEventsDispatcher.fireGameNotification(
				new BlackHoleLossesEvent(fleet, lossesValue));
			
		}

		
	}
}