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

package fr.fg.server.core;

import java.util.HashMap;
import java.util.Map;

import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Ship;
import fr.fg.server.data.ShipContainer;
import fr.fg.server.data.Slot;
import fr.fg.server.data.Structure;

public class ContainerTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		RIGHT_RECEIVE_ITEMS = 1 << 0,
		RIGHT_GIVE_ITEMS = 1 << 1,
		RIGHT_RECEIVE_RESOURCES = 1 << 2,
		RIGHT_GIVE_RESOURCES = 1 << 3,
		RIGHT_EXCEED_PAYLOAD = 1 << 4,
		RIGHT_RECEIVE_SHIPS = 1 << 5,
		RIGHT_GIVE_SHIPS = 1 << 6,
		RIGHT_TRANSFER_ALL_SHIPS = 1 << 7,
		RIGHT_EXCEED_MAX_SHIPS = 1 << 8;
	
	public final static int
		FLEET_RIGHTS =
			RIGHT_GIVE_ITEMS |
			RIGHT_RECEIVE_ITEMS |
			RIGHT_GIVE_SHIPS |
			RIGHT_RECEIVE_SHIPS,
		
		SYSTEM_RIGHTS =
			RIGHT_GIVE_RESOURCES |
			RIGHT_RECEIVE_RESOURCES |
			RIGHT_EXCEED_PAYLOAD |
			RIGHT_GIVE_SHIPS |
			RIGHT_EXCEED_MAX_SHIPS |
			RIGHT_TRANSFER_ALL_SHIPS,
		
		SPACE_STATION_RIGHTS =
			RIGHT_RECEIVE_RESOURCES |
			RIGHT_EXCEED_PAYLOAD,
			
		STOREHOUSE_RIGHTS =
			RIGHT_GIVE_RESOURCES |
			RIGHT_RECEIVE_RESOURCES,
			
		SPACESHIP_RIGHTS =
			RIGHT_GIVE_SHIPS |
			RIGHT_RECEIVE_SHIPS |
			RIGHT_TRANSFER_ALL_SHIPS;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static ItemContainer swap(ItemContainer itemContainer1Before,
			ItemContainer itemContainer1After, long payload1,
			ItemContainer itemContainer2Before, long payload2)
			throws IllegalOperationException {
		int[] rights = new int[2];
		long[] payload = {payload1, payload2};
		
		ItemContainer[] itemContainersBefore = {itemContainer1Before, itemContainer2Before};
		
		// Détermine les items qui peuvent être échangés et la charge maximale
		for (int i = 0; i < 2; i++) {
			switch (itemContainersBefore[i].getContainerType()) {
			case ItemContainer.CONTAINER_FLEET:
				rights[i] = FLEET_RIGHTS;
				break;
			case ItemContainer.CONTAINER_SYSTEM:
				rights[i] = SYSTEM_RIGHTS;
				break;
			case ItemContainer.CONTAINER_STRUCTURE:
				Structure structure = itemContainersBefore[i].getStructure();
				
				switch (structure.getType()) {
				case Structure.TYPE_STOREHOUSE:
					rights[i] = STOREHOUSE_RIGHTS;
					break;
				case Structure.TYPE_SPACESHIP_YARD:
					rights[i] = SPACESHIP_RIGHTS;
					break;
				default:
					rights[i] = 0;
					break;
				}
				break;
			case ItemContainer.CONTAINER_SPACE_STATION:
				rights[i] = SPACE_STATION_RIGHTS;
				break;
			default:
				throw new IllegalOperationException("Invalid container type: '" +
					itemContainersBefore[i].getContainerType() + "'.");
			}
		}
		
		// Calcule ItemContainer2After
		long id;
		switch (itemContainer2Before.getContainerType()) {
		case ItemContainer.CONTAINER_FLEET:
			id = itemContainer2Before.getIdFleet();
			break;
		case ItemContainer.CONTAINER_SYSTEM:
			id = itemContainer2Before.getIdSystem();
			break;
		case ItemContainer.CONTAINER_STRUCTURE:
			id = itemContainer2Before.getIdStructure();
			break;
		case ItemContainer.CONTAINER_SPACE_STATION:
			id = itemContainer2Before.getIdSpaceStation();
			break;
		default:
			throw new IllegalStateException("Invalid container type: '" +
				itemContainer2Before.getContainerType() + "'.");
		}
		
		ItemContainer itemContainer2After = new ItemContainer(
			itemContainer2Before.getContainerType(), id);
		
		int index = 0;
		for (int i = 0; i < itemContainer1Before.getMaxItems(); i++) {
			Item item1Before = itemContainer1Before.getItem(i);
			
			if (item1Before.getType() == Item.TYPE_NONE)
				continue;
			
	        // Dénombre les items qui ont été transférés sur le 2e container
			double count = item1Before.getCount();
			
	        for (int j = 0; j < itemContainer2Before.getMaxItems(); j++) {
	        	Item item2Before = itemContainer2Before.getItem(j);
	        	
	        	if (item1Before.isSameType(item2Before)) {
	        		count += item2Before.getCount();
	        		break;
	        	}
	        }
	        
	        for (int j = 0; j < itemContainer1After.getMaxItems(); j++) {
	        	Item item1After = itemContainer1After.getItem(j);
	        	
	        	if (item1Before.isSameType(item1After)) {
	        		count -= item1After.getCount();
	        		break;
	        	}
	        }
	        
	        if (count > 0) {
	        	if (index >= itemContainer2After.getMaxItems())
	        		throw new IllegalOperationException("Transfert d'équipement invalide.");
	        	
	        	itemContainer2After.setItem(new Item(item1Before.getType(),
	        		item1Before.getId(), item1Before.getIdStructure(), count), index);
	        	index++;
	        } else if (count < 0) {
	        	throw new IllegalOperationException("Equipement à transférer invalide.");
	        }
		}
		
		loop:for (int i = 0; i < itemContainer2Before.getMaxItems(); i++) {
			// Dénombre les items qui ont été transférés sur le 2e container
			Item item2Before = itemContainer2Before.getItem(i);
			
			if (item2Before.getType() == Item.TYPE_NONE)
				continue;
			
			for (int j = 0; j < itemContainer1Before.getMaxItems(); j++)
				if (item2Before.isSameType(itemContainer1Before.getItem(j)))
					continue loop;
			
			// Dénombre les items qui ont été transférés sur le 2e container
			double count = item2Before.getCount();
			
			for (int j = 0; j < itemContainer1After.getMaxItems(); j++) {
	        	Item item1After = itemContainer1After.getItem(j);
	        	
	        	if (item2Before.isSameType(item1After)) {
	        		count -= item1After.getCount();
	        		break;
	        	}
	        }
			
			 if (count > 0) {
	        	if (index >= itemContainer2After.getMaxItems())
	        		throw new IllegalOperationException("Transfert d'équipement invalide.");
	        	
	        	itemContainer2After.setItem(new Item(item2Before.getType(),
	        		item2Before.getId(), item2Before.getIdStructure(), count), index);
	        	index++;
	        } else if (count < 0) {
	        	throw new IllegalOperationException("Equipement à transférer invalide.");
	        }
		}
		
		ItemContainer[] itemContainersAfter = {itemContainer1After, itemContainer2After};
		
		// Vérifie que le nombre total d'items n'a pas changé
		Map<Long, Double> itemsBefore = new HashMap<Long, Double>();
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < itemContainersBefore[i].getMaxItems(); j++) {
				Item item = itemContainersBefore[i].getItem(j);
				
				if (item.getType() == Item.TYPE_NONE)
					continue;
				
				long key = item.getKey();
				if (itemsBefore.containsKey(key))
					itemsBefore.put(key, itemsBefore.get(key) + item.getCount());
				else
					itemsBefore.put(key, item.getCount());
			}
		}
		
		Map<Long, Double> itemsAfter = new HashMap<Long, Double>();
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < itemContainersAfter[i].getMaxItems(); j++) {
				Item item = itemContainersAfter[i].getItem(j);
				
				if (item.getType() == Item.TYPE_NONE)
					continue;
				
				long key = item.getKey();
				if (itemsAfter.containsKey(key))
					itemsAfter.put(key, itemsAfter.get(key) + item.getCount());
				else
					itemsAfter.put(key, item.getCount());
			}
		}
		
		for (long key : itemsBefore.keySet()) {
			if (itemsAfter.get(key) == null || (double) itemsAfter.get(key) != (double) itemsBefore.get(key))
				throw new IllegalOperationException("Le nombre d'items ne peut pas changer.");
		}
		
		for (long key : itemsAfter.keySet()) {
			if (itemsBefore.get(key) == null || (double) itemsBefore.get(key) != (double) itemsAfter.get(key))
				throw new IllegalOperationException("Le nombre d'items ne peut pas changer.");
		}
		
		// Vérifie qu'il n'y a pas 2x le meme objet dans un container
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < itemContainersAfter[i].getMaxItems(); j++) {
				Item itemAfter = itemContainersAfter[i].getItem(j);
				
	            if (itemAfter.getType() == Item.TYPE_NONE)
	            	continue;
	            
	            for (int k = 0; k < itemContainersAfter[i].getMaxItems(); k++)
	            	if (j != k && itemAfter.isSameType(itemContainersAfter[i].getItem(k)))
	            		throw new IllegalOperationException("Le même équipement ne " +
	            			"peut pas être présent sur deux emplacements.");
			}
		}
		
		// Vérifie qu'il n'y a pas d'objets non stackable en quantité != 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < itemContainersAfter[i].getMaxItems(); j++) {
				Item item = itemContainersAfter[i].getItem(j);
				
				if (item.getType() == Item.TYPE_NONE)
					continue;
				
				if (!item.isStackable() && item.getCount() != 1)
					throw new IllegalOperationException(
						"Un équipement n'est pas empilable.");
			}
		}
		
		// Vérifie que les containers n'ont pas reçu d'items s'ils n'y sont pas
		// autorisés
		for (int i = 0; i < 2; i++) {
			int j = i == 0 ? 1 : 0;
			
			boolean allowItemTransferToContainer =
				(rights[i] & RIGHT_RECEIVE_ITEMS) != 0 &&
				(rights[j] & RIGHT_GIVE_ITEMS) != 0;
			boolean allowResourceTransferToContainer =
				((rights[i] & RIGHT_RECEIVE_RESOURCES) != 0 || (rights[i] & RIGHT_RECEIVE_ITEMS) != 0) &&
				((rights[j] & RIGHT_GIVE_RESOURCES) != 0 || (rights[j] & RIGHT_GIVE_ITEMS) != 0);
			
			if (!allowItemTransferToContainer) {
				for (int k = 0; k < itemContainersAfter[i].getMaxItems(); k++) {
					Item itemAfter = itemContainersAfter[i].getItem(k);
					
					if (allowResourceTransferToContainer &&
							itemAfter.getType() == Item.TYPE_RESOURCE)
						continue;
					
					double countBefore = 0;
					for (int l = 0; l < itemContainersBefore[i].getMaxItems(); l++) {
						Item itemBefore = itemContainersBefore[i].getItem(l);
						
						if (itemBefore.isSameType(itemAfter)) {
							countBefore = itemBefore.getCount();
							break;
						}
					}
					
					if (countBefore < itemAfter.getCount())
						throw new IllegalOperationException("Un container " +
							"ne peut pas recevoir d'équipement.");
				}
			}
		}
		
		// Vérifie la capacité
		for (int i = 0; i < 2; i++) {
			if ((rights[i] & RIGHT_EXCEED_PAYLOAD) == 0 &&
					itemContainersAfter[i].getTotalWeight() > payload[i])
				throw new IllegalOperationException("Capacité insuffisante " +
						"pour transporter l'équipement."+ payload1 +" 2: "+ payload2);
		}
		
		return itemContainer2After;
	}
	
	public static ShipContainer swap(
			ShipContainer shipContainer1Before,
			ShipContainer shipContainer1After,
			ShipContainer shipContainer2Before)
			throws IllegalOperationException {
		int[] rights = new int[2];
		long[] maxShips = new long[2];
		
		ShipContainer[] shipContainersBefore = {shipContainer1Before, shipContainer2Before};
		
		// Détermine les vaisseaux qui peuvent être échangés et la charge
		// maximale
		for (int i = 0; i < 2; i++) {
			switch (shipContainersBefore[i].getContainerType()) {
			case ShipContainer.CONTAINER_FLEET:
				rights[i] = FLEET_RIGHTS;
				maxShips[i] = Fleet.getPowerAtLevel(
					shipContainersBefore[i].getFleet(
						).getOwner().getLevel() + 1) - 1;
				break;
			case ShipContainer.CONTAINER_SYSTEM:
				rights[i] = SYSTEM_RIGHTS;
				maxShips[i] = 0;
				break;
			case ShipContainer.CONTAINER_STRUCTURE:
				Structure structure = shipContainersBefore[i].getStructure();
				maxShips[i] = structure.getMaxShips();
				
				switch (structure.getType()) {
				case Structure.TYPE_STOREHOUSE:
					rights[i] = STOREHOUSE_RIGHTS;
					break;
				case Structure.TYPE_SPACESHIP_YARD:
					rights[i] = SPACESHIP_RIGHTS;
					break;
				default:
					rights[i] = 0;
					break;
				}
				break;
			default:
				throw new IllegalOperationException(
					"Invalid container type: '" +
					shipContainersBefore[i].getContainerType() + "'.");
			}
		}
		
		// Calcule ShipContainer2After
		long id;
		switch (shipContainer2Before.getContainerType()) {
		case ShipContainer.CONTAINER_FLEET:
			id = shipContainer2Before.getIdFleet();
			break;
		case ShipContainer.CONTAINER_SYSTEM:
			id = shipContainer2Before.getIdSystem();
			break;
		case ShipContainer.CONTAINER_STRUCTURE:
			id = shipContainer2Before.getIdStructure();
			break;
		default:
			throw new IllegalStateException("Invalid container type: '" +
				shipContainer2Before.getContainerType() + "'.");
		}
		
		// Variables pour faciliter le calcul
		ShipContainer shipContainer2After = new ShipContainer(
			shipContainer2Before.getContainerType(), id);
		
		ShipContainer[] shipContainersAfter = {shipContainer1After, shipContainer2After};
		
		Slot[] slots1Before = shipContainer1Before.getSlots();
		Slot[] slots2Before = shipContainer2Before.getSlots();
		Slot[] slots1After = shipContainer1After.getSlots();
		Slot[] slots2After = new Slot[shipContainer2Before.getMaxSlots()];
		
		for (int i = 0; i < slots2After.length; i++)
			slots2After[i] = new Slot();
		
		long[] ships1Before = new long[Ship.SHIPS.length];
		long[] ships2Before = new long[Ship.SHIPS.length];
		long[] ships1After = new long[Ship.SHIPS.length];
		long[] ships2After = new long[Ship.SHIPS.length];
		
		// Vérifie qu'il n'y a pas 2x le même type de vaisseau dans
		// 2 slots différents
		for (int i = 0; i < shipContainer1After.getMaxSlots(); i++) {
			if (slots1After[i].getId() != 0) {
				for (int j = i + 1; j < shipContainer1After.getMaxSlots(); j++) {
					if (slots1After[j].getId() == slots1After[i].getId())
						throw new IllegalOperationException(
							"Le même vaisseau est présent sur deux slots différents.");
				}
			}
		}
		
		// Compte le nombre de vaisseaux avant transfert
		for (int i = 0; i < shipContainer1Before.getMaxSlots(); i++) {
			if (slots1Before[i].getId() != 0)
				ships1Before[slots1Before[i].getId()] += slots1Before[i].getCount();
		}
		
		for (int i = 0; i < shipContainer2Before.getMaxSlots(); i++) {
			if (slots2Before[i].getId() != 0)
				ships2Before[slots2Before[i].getId()] += slots2Before[i].getCount();
		}
		
		for (int i = 0; i < shipContainer1After.getMaxSlots(); i++) {
			if (slots1After[i].getId() != 0)
				ships1After[slots1After[i].getId()] += slots1After[i].getCount();
		}
		
		// Calcule le nombre de vaisseaux restants sur le 2e container
		int currentSlot = 0;
		
		for (int i = 1; i < Ship.SHIPS.length; i++) {
			if (ships1After[i] != 0 || ships1Before[i] != 0 || ships2Before[i] != 0) {
				long ships = ships2Before[i] - ships1After[i] + ships1Before[i];
				
				if (ships < 0)
					throw new IllegalOperationException("Nombre de vaisseaux " +
						"à transférer invalide.");
				
				if (ships > 0) {
					if (currentSlot >= shipContainer2Before.getMaxSlots())
						throw new IllegalOperationException("Types de vaisseaux " +
							"à transférer invalide.");
					
					slots2After[currentSlot] = new Slot(i, ships, true);
					currentSlot++;
				}
				
				ships2After[i] = ships;
			}
		}
		
		// Essaye de rearranger les slots pour qu'ils se retrouvent dans la
		// même position qu'avant, afin de ne pas casser la tactique
		for (int i = 0; i < shipContainer2Before.getMaxSlots(); i++) {
			Slot slotBefore = slots2Before[i];
			Slot slotAfter = slots2After[i];
			
			if (slotBefore.getId() != 0) {
				if (slotBefore.getId() != slots2After[i].getId()) {
					for (int j = 0; j < shipContainer2Before.getMaxSlots(); j++) {
						if (slotBefore.getId() == slots2After[j].getId()) {
							slots2After[j].setFront(slotBefore.isFront());
							slots2After[i] = slots2After[j];
							slots2After[j] = slotAfter;
							break;
						}
					}
				} else {
					slotAfter.setFront(slotBefore.isFront());
				}
			}
		}
		
		shipContainer2After.copy(shipContainer2Before);
		shipContainer2After.setSlots(slots2After);
		
		// Vérifie que les vaisseaux ont été transférés en respectant les
		// contraintes
		long[][] shipsBefore = {ships1Before, ships2Before};
		long[][] shipsAfter = {ships1After, ships2After};
		
		for (int i = 0; i < 2; i++) {
			int j = i == 0 ? 1 : 0;
			
			boolean allowShipTransferToContainer =
				(rights[i] & RIGHT_RECEIVE_SHIPS) != 0 &&
				(rights[j] & RIGHT_GIVE_SHIPS) != 0;
			
			if (!allowShipTransferToContainer) {
				for (int k = 1; k < shipsAfter[i].length; k++)
					if (shipsAfter[i][k] > shipsBefore[i][k])
						throw new IllegalOperationException(
							"Opération invalide.");
			}
		}
		
		// Vérifie qu'il reste un vaisseau sur les containers
		for (int i = 0; i < 2; i++) {
			if ((rights[i] & RIGHT_TRANSFER_ALL_SHIPS) == 0) {
				boolean containsShips = false;
				for (int j = 1; j < shipsAfter[i].length; j++)
					if (shipsAfter[i][j] > 0) {
						containsShips = true;
						break;
					}
				
				if (!containsShips)
					throw new IllegalOperationException(
						"Une flotte ne peut pas être vide.");
			}
		}
		
		// Vérifie que le niveau de la flotte ne dépasse pas le niveau du joueur
		for (int i = 0; i < 2; i++) {
			if ((rights[i] & RIGHT_EXCEED_MAX_SHIPS) == 0) {
				if (shipContainersAfter[i].getShipsCount() > maxShips[i])
					throw new IllegalOperationException("Le nombre " +
						"de vaisseaux dépasse la capacité maximale.");
			}
		}
		
		return shipContainer2After;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
