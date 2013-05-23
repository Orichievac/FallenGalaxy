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
import java.util.Map;


import fr.fg.server.core.ContainerTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.SystemTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class SwapFleetSystem extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		int idSystem = (Integer) params.get("system");
		
		// Récupère la flotte et le système
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());
		StarSystem system = SystemTools.getSystemByIdWithChecks(
				idSystem, player.getId());
		
		// Vérifie que la flotte est dans les frontières du système
		if (fleet.getIdCurrentArea() != system.getIdArea())
			throw new IllegalOperationException("La flotte n'est pas dans le même " +
					"secteur que le système.");
		
		if(fleet.getCurrentAction()!=Fleet.CURRENT_ACTION_NONE)
			throw new IllegalOperationException("La flotte est immobilisée.");
		
		if (!system.contains(fleet.getCurrentX(), fleet.getCurrentY()))
			throw new IllegalOperationException("La flotte est trop éloignée du " +
					"système pour pouvoir échanger des vaisseaux.");
		
		system = StarSystem.updateSystem(system);
		
		ItemContainer fleetContainerBefore = fleet.getItemContainer();
		ItemContainer systemContainerBefore = new ItemContainer(
			ItemContainer.CONTAINER_SYSTEM, system.getId());
		systemContainerBefore.setResources(system.getResources());
		ItemContainer systemContainerAfter;
		
		// Nouvelles ressources
		ItemContainer fleetContainerAfter = new ItemContainer(
			ItemContainer.CONTAINER_FLEET, fleet.getId());
		
		for (int i = 0; i < fleetContainerBefore.getMaxItems(); i++) {
			int type = (Integer) params.get("item" + i + "_type");
			long id = (Long) params.get("item" + i + "_id");
			long count = (Long) params.get("item" + i + "_count");
			
			Item item = new Item(type, id, count);
			fleetContainerAfter.setItem(item, i);
		}
		
		Fleet newFleet;
		StarSystem newSystem;
		synchronized (fleet.getLock()) {
			synchronized (system.getLock()) {
				newFleet = DataAccess.getEditable(fleet);
				newSystem = DataAccess.getEditable(system);
				
				// Efface les slots du système
				for (int i = 0; i < GameConstants.SYSTEM_SLOT_COUNT; i++)
					newSystem.setSlot(new Slot(), i);
				
				// Construit les nouveaux slots de la flotte
				long[] newShips = new long[Ship.SHIPS.length];
				
				boolean updateTactics = false;
				
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
					int id = (Integer) params.get("slot1" + i + "_id");
					
					if (newFleet.getSlot(i).getId() != id)
						updateTactics = true;
					
					Slot slot = new Slot(id, id == 0 ? 0 :
						(Long) params.get("slot1" + i + "_count"),
						newFleet.getSlot(i).isFront()
					);
					
					if (slot.getId() != 0)
						newShips[slot.getId()] += (long) slot.getCount();
					
					newFleet.setSlot(slot, i);
				}
				
				// Vérifie qu'il n'y a pas 2x le même type de vaisseau dans
				// 2 slots différents
				for (int k = 0; k < GameConstants.FLEET_SLOT_COUNT; k++) {
					if (newFleet.getSlot(k).getId() != 0) {
						for (int j = k + 1; j < GameConstants.FLEET_SLOT_COUNT; j++) {
							if (newFleet.getSlot(j).getId() == newFleet.getSlot(k).getId())
								throw new IllegalOperationException(
										"Le même vaisseau est présent sur deux slots différents.");
						}
					}
				}
				
				// Vérifie que le niveau de la flotte ne dépasse pas le niveau du joueur
				if (newFleet.getPowerLevel() > player.getLevel())
					throw new IllegalOperationException(
						"La puissance d'une flotte ne peut dépasser le niveau d'XP.");
				
//				// Vérifie que le niveaux des flottes ne dépasse pas le niveau du quadrant
//				if (newFleet.getPowerLevel() >
//					newFleet.getArea().getSector().getLvlMax())
//							throw new IllegalOperationException(
//								"La puissance d'une flotte ne peut dépasser la limite" +
//								" du quadrant : "+newFleet.getArea().getSector().getLvlMax()+
//								".");
				
				
				// Compte le nombre de vaisseaux avant transfert
				long[] oldFleetShips = new long[Ship.SHIPS.length];
				long[] oldSystemShips = new long[Ship.SHIPS.length];
				
				for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
					Slot oldSlot = fleet.getSlot(i);
					if (oldSlot.getId() != 0)
						oldFleetShips[oldSlot.getId()] +=
							(long) oldSlot.getCount();
				}

				for (int i = 0; i < GameConstants.SYSTEM_SLOT_COUNT; i++) {
					Slot oldSlot = system.getSlot(i);
					if (oldSlot.getId() != 0)
						oldSystemShips[oldSlot.getId()] +=
							(long) oldSlot.getCount();
				}
				
				// Calcule le nombre de vaisseaux restants sur le système
				int currentSlot = 0;
				for (int j = 1; j < Ship.SHIPS.length; j++) {
					if (newShips[j] != 0 || oldSystemShips[j] != 0 ||
							oldFleetShips[j] != 0) {
						long systemShips = oldSystemShips[j] -
							newShips[j] + oldFleetShips[j];
						
						//if (systemShips > oldSystemShips[j] || systemShips < 0)
						if (systemShips < 0)
							throw new IllegalOperationException("Nombre de vaisseaux " +
								"à transférer invalide.");
						
						if (systemShips > 0)
							newSystem.setSlot(new Slot(j, systemShips, true), currentSlot++);
					}
				}
				
				// Vérifie qu'il reste un vaisseau sur la flotte
				if (newFleet.getShipsCount() <= 0)
					throw new IllegalOperationException(
							"Une flotte ne peut pas être vide.");
				
				// Met à jour la tactique au besoin
				if (updateTactics)
					newFleet.updateTactics();
				
				systemContainerAfter = ContainerTools.swap(
					fleetContainerBefore, fleetContainerAfter,
					Fleet.getPayload(newFleet.getSlots()),
					systemContainerBefore,
					systemContainerBefore.getPayload());
				
				// Met à jour les ressources sur le système
				newSystem.setResources(systemContainerAfter.getResources());
				
				// Met à jour la flotte et le système
				newFleet.save();
				newSystem.save();
			}
		}
		
		synchronized (fleetContainerBefore.getLock()) {
			fleetContainerBefore = DataAccess.getEditable(fleetContainerBefore);
			fleetContainerBefore.copy(fleetContainerAfter);
			fleetContainerBefore.save();
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(newFleet.getIdCurrentArea(),
			player.getId(), new Point(newFleet.getX(), newFleet.getY()));
		
		JSONStringer json = new JSONStringer();
		
		FleetTools.getPlayerFleet(json, newFleet.getId());
		UpdateTools.queuePlayerFleetsUpdate(newFleet.getIdOwner());
		UpdateTools.queuePlayerSystemsUpdate(newFleet.getIdOwner());
		UpdateTools.queueAreaUpdate(newFleet.getOwner());

		return json.toString();
		
		/*return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getPlayerSystemsUpdate(),
			Update.getAreaUpdate()
		);*/
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
