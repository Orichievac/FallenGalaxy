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
import fr.fg.server.core.Update;
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
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SwapFleets extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		// Paramètres de l'action
		int[] idFleets = {
			(Integer) params.get("fleet0"),
			(Integer) params.get("fleet1")
		};
		
		// Vérifie que les flottes sont différentes
		if (idFleets[0] == idFleets[1])
			throw new IllegalOperationException("Transfert invalide.");
		
		// Recupère les flottes
		Fleet[] fleets = new Fleet[2];
		
		for (int i = 0; i < 2; i++) {
			Fleet fleet = FleetTools.getFleetByIdWithChecks(
					idFleets[i], player.getId());
			
			fleets[i] = fleet;
		}
		
		Fleet fleet0 = fleets[0], fleet1 = fleets[1];
		
		// Vérifie que les flottes sont adjacentes
		if (fleet0.getIdCurrentArea() != fleet1.getIdCurrentArea()) 
			throw new IllegalOperationException("Les deux flottes ne sont pas dans " +
					"le même secteur.");
		
		if (Math.abs(fleet0.getCurrentX() - fleet1.getCurrentX()) > 1 ||
				Math.abs(fleet0.getCurrentY() - fleet1.getCurrentY()) > 1)
			throw new IllegalOperationException("Les deux flottes doivent être " +
					"adjacentes pour pouvoir échanger des vaisseaux.");
		
		// Vérifie que les flottes ne sont pas en hyperespace
		if (fleet0.isInHyperspace() || fleet1.isInHyperspace())
			throw new IllegalOperationException("Une des deux flottes est " +
					"en hyperespace.");
		
		// Vérifie que les flottes ont suffisament de mouvement
		int distance = 
			Math.abs(fleet0.getCurrentX() - fleet1.getCurrentX()) == 1 &&
			Math.abs(fleet0.getCurrentY() - fleet1.getCurrentY()) == 1 ?
					2 : 1;
		
		if (fleet0.getMovement() < distance || fleet1.getMovement() < distance)
			throw new IllegalOperationException("Une des deux flotte n'a pas assez " +
					"de mouvement pour procéder à l'échange.");
		
		int playerLevel = player.getLevel();
		
		ItemContainer itemContainer0Before = fleet0.getItemContainer();
		ItemContainer itemContainer0After;
		ItemContainer itemContainer1Before = fleet1.getItemContainer();
		ItemContainer itemContainer1After = new ItemContainer(ItemContainer.CONTAINER_FLEET, fleet1.getId());
		
		for (int i = 0; i < itemContainer1Before.getMaxItems(); i++) {
			int type = (Integer) params.get("item" + i + "_type");
			long id = (Long) params.get("item" + i + "_id");
			long count = (Long) params.get("item" + i + "_count");
			
			Item item = new Item(type, id, count);
			itemContainer1After.setItem(item, i);
		}
		
		// Construit les nouveaux slots de chaque flotte
		Fleet[] newFleets;
		synchronized (fleet0.getLock()) {
			synchronized (fleet1.getLock()) {
				newFleets = new Fleet[]{
					DataAccess.getEditable(fleet0),
					DataAccess.getEditable(fleet1),
				};
				
				for (int i = 0; i < 2; i++) {
					boolean updateTactics = false;
					
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
						
						int id = (Integer) params.get("slot" + i + j + "_id");
						
						if (newFleets[i].getSlot(j).getId() != id)
							updateTactics = true;
						
						newFleets[i].setSlot(new Slot(id,
							id == 0 ? 0 : (Long) params.get(
									"slot" + i + j + "_count"),
									newFleets[i].getSlot(j).isFront()
						), j);
					}
					
					// Vérifie qu'il n'y a pas 2x le même type de vaisseau dans
					// 2 slots différents
					for (int k = 0; k < GameConstants.FLEET_SLOT_COUNT; k++) {
						if (newFleets[i].getSlot(k).getId() == 0)
							continue;
						
						for (int j = k + 1; j < GameConstants.FLEET_SLOT_COUNT; j++) {
							if (newFleets[i].getSlot(j).getId() == newFleets[i].getSlot(k).getId())
								throw new IllegalOperationException(
										"Le même vaisseau est présent sur deux slots différents.");
						}
					}
					
					// Vérifie qu'il y a au moins un vaisseau dans chaque
					// flotte
					if (newFleets[i].getShipsCount() <= 0)
						throw new IllegalOperationException(
								"Une flotte ne peut pas être vide.");
					
					// Vérifie que le niveaux des flottes ne dépasse pas le niveau du joueur
					if (newFleets[i].getPowerLevel() > playerLevel)
						throw new IllegalOperationException(
							"La puissance d'une flotte ne peut dépasser le niveau d'XP.");
					
//					// Vérifie que le niveaux des flottes ne dépasse pas le niveau du quadrant
//					if (newFleets[i].getPowerLevel() >
//							newFleets[i].getArea().getSector().getLvlMax())
//								throw new IllegalOperationException(
//									"La puissance d'une flotte ne peut dépasser la limite" +
//									" du quadrant : "+newFleets[i].getArea().getSector().getLvlMax()+
//									".");
//					
					
					// Met à jour la tactique au besoin
					if (updateTactics)
						newFleets[i].updateTactics();
				}
				
				// Vérifie que le nombre de vaisseaux n'a pas changé
				long[] oldShips = new long[Ship.SHIPS.length];
				long[] newShips = new long[Ship.SHIPS.length];
				
				for (int i = 0; i < 2; i++)
					for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
						Slot oldSlot = fleets[i].getSlot(j);
						Slot newSlot = newFleets[i].getSlot(j);
						
						if (oldSlot.getId() != 0)
							oldShips[oldSlot.getId()] +=
								(long) oldSlot.getCount();
						if (newSlot.getId() != 0)
							newShips[newSlot.getId()] +=
								(long) newSlot.getCount();
					}
				
				for (int i = 0; i < Ship.SHIPS.length; i++)
					if (oldShips[i] != newShips[i])
						throw new IllegalOperationException("La quantité totale de " +
								"vaisseaux ne peut changer.");
				
				itemContainer0After = ContainerTools.swap(
					itemContainer1Before, itemContainer1After,
					Fleet.getPayload(newFleets[1].getSlots()),
					itemContainer0Before,
					Fleet.getPayload(newFleets[0].getSlots()));
				
				// Met à jours les flottes
				for (int i = 0; i < 2; i++) {
					DataAccess.save(newFleets[i]);
				}
			}
		}
		
		synchronized (itemContainer0Before.getLock()) {
			itemContainer0Before = DataAccess.getEditable(itemContainer0Before);
			itemContainer0Before.copy(itemContainer0After);
			itemContainer0Before.save();
		}
		
		synchronized (itemContainer1Before.getLock()) {
			itemContainer1Before = DataAccess.getEditable(itemContainer1Before);
			itemContainer1Before.copy(itemContainer1After);
			itemContainer1Before.save();
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(newFleets[0].getIdCurrentArea(),
				player.getId(),
				new Point(fleet0.getX(), fleet0.getY()),
				new Point(fleet1.getX(), fleet1.getY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet0.getId()),
			Update.getPlayerFleetUpdate(fleet1.getId()),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
