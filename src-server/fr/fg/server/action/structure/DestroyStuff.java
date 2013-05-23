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

package fr.fg.server.action.structure;

import java.util.Map;

import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DestroyStuff extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		long idStructure = (Long) params.get("id");
		int index = (Integer) params.get("index");
		long count = (Long) params.get("count");
		String type = (String) params.get("type");
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
			idStructure, player.getId());
		
		if (type.equals("ship")) {
			if (structure.getType() != Structure.TYPE_SPACESHIP_YARD)
				throw new IllegalOperationException(
					"La structure ne comporte pas de vaisseaux.");
			
			StructureSpaceshipYard spaceshipYard = structure.getSpaceshipYard();
			
			// Destruction d'un vaisseau
			Slot slot = spaceshipYard.getSlot(index);
			
			synchronized (spaceshipYard.getLock()) {
				spaceshipYard = DataAccess.getEditable(spaceshipYard);
				spaceshipYard.update();
				spaceshipYard.setSlot(new Slot(slot.getId(),
					Math.max(0, slot.getCount() - count),
					slot.isFront()), index);
				spaceshipYard.save();
			}
		} else if (type.equals("resource") && index < 4) {
			if (structure.getType() != Structure.TYPE_STOREHOUSE)
				throw new IllegalOperationException("La structure " +
					"ne comporte pas de ressources.");
			
			StorehouseResources resources = structure.getArea(
				).getStorehouseResourcesByPlayer(player.getId());
			
			if (resources != null) {
				// Destruction de ressources
				synchronized (resources.getLock()) {
					resources = DataAccess.getEditable(resources);
					resources.addResource(-count, index);
					resources.save();
				}
			}
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate()
		);
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
