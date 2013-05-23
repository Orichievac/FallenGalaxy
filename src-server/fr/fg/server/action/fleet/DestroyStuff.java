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

package fr.fg.server.action.fleet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Slot;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DestroyStuff extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("id");
		int index = (Integer) params.get("index");
		long count = (Long) params.get("count");
		String type = (String) params.get("type");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
			idFleet, player.getId(),
			FleetTools.ALLOW_HYPERSPACE | FleetTools.ALLOW_DELUDE);

		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir détruire " +
				"des vaisseaux ou des ressources.");
		
		List<Update> updates = new ArrayList<Update>();
		
		if (type.equals("ship")) {
			int slotsCount = 0;
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
				if (fleet.getSlot(i).getId() != 0) {
					slotsCount++;
				}
			
			if (slotsCount == 1 && fleet.getSlot(index).getId() != 0 &&
					count >= fleet.getSlot(index).getCount()) {
				// Destruction de la flotte
				fleet.delete();
				
				Effect effect = new Effect(Effect.TYPE_FLEET_DESTRUCTION,
					fleet.getCurrentX(), fleet.getCurrentY(),
					fleet.getIdCurrentArea());
				
				updates.add(Update.getEffectUpdate(effect));
				
				UpdateTools.queueEffectUpdate(effect, player.getId(), false);
				UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
						new Point(fleet.getCurrentX(), fleet.getCurrentY()));
				
				updates.add(Update.getPlayerFleetsUpdate());
			} else {
				// Destruction d'un vaisseau
				Slot slot = fleet.getSlot(index);
				
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.setSlot(new Slot(slot.getId(),
						Math.max(0, slot.getCount() - count),
						slot.isFront()), index);
					fleet.save();
				}

				updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
			}
		} else if (type.equals("resource") && index < 4) {
			// Destruction de ressources
			ItemContainer itemContainer = fleet.getItemContainer();
			Item item = itemContainer.getItem(index);
			item.addCount(-count);
			
			synchronized (itemContainer.getLock()) {
				itemContainer = DataAccess.getEditable(itemContainer);
				itemContainer.setItem(item, index);
				itemContainer.save();
			}
			
			updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		}
		
		updates.add(Update.getAreaUpdate());
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
