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

package fr.fg.server.data;

import fr.fg.server.data.base.StructureSpaceshipYardBase;
import fr.fg.server.util.Utilities;

public class StructureSpaceshipYard extends StructureSpaceshipYardBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StructureSpaceshipYard() {
		// Nécessaire pour la construction par réflection
	}
	
	public StructureSpaceshipYard(long idStructure) {
		setIdStructure(idStructure);
		setSlot0Id(0);
		setSlot0Count(0);
		setSlot1Id(0);
		setSlot1Count(0);
		setSlot2Id(0);
		setSlot2Count(0);
		setSlot3Id(0);
		setSlot3Count(0);
		setSlot4Id(0);
		setSlot4Count(0);
		setBuildSlot0Id(0);
		setBuildSlot0Count(0);
		setBuildSlot0Ordered(0);
		setBuildSlot1Id(0);
		setBuildSlot1Count(0);
		setBuildSlot1Ordered(0);
		setBuildSlot2Id(0);
		setBuildSlot2Count(0);
		setBuildSlot2Ordered(0);
		setLastBoughtFleet(Utilities.now());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Slot getSlot(int index) {
		switch (index) {
		case 0:
			return new Slot(getSlot0Id(), getSlot0Count(), true);
		case 1:
			return new Slot(getSlot1Id(), getSlot1Count(), true);
		case 2:
			return new Slot(getSlot2Id(), getSlot2Count(), true);
		case 3:
			return new Slot(getSlot3Id(), getSlot3Count(), true);
		case 4:
			return new Slot(getSlot4Id(), getSlot4Count(), true);
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public Slot[] getSlots(){
		return new Slot[]{
			getSlot(0), getSlot(1), getSlot(2), getSlot(3), getSlot(4)
		};
	}
	
	public void setSlot(Slot slot, int index) {
		switch (index) {
		case 0:
			setSlot0Id(slot.getId());
			setSlot0Count((long) slot.getCount());
			break;
		case 1:
			setSlot1Id(slot.getId());
			setSlot1Count((long) slot.getCount());
			break;
		case 2:
			setSlot2Id(slot.getId());
			setSlot2Count((long) slot.getCount());
			break;
		case 3:
			setSlot3Id(slot.getId());
			setSlot3Count((long) slot.getCount());
			break;
		case 4:
			setSlot4Id(slot.getId());
			setSlot4Count((long) slot.getCount());
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public void setSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++)
			setSlot(slots[i], i);
	}

	public Slot getBuildSlot(int index) {
		switch (index) {
		case 0:
			return new Slot(getBuildSlot0Id(), getBuildSlot0Count(), true);
		case 1:
			return new Slot(getBuildSlot1Id(), getBuildSlot1Count(), true);
		case 2:
			return new Slot(getBuildSlot2Id(), getBuildSlot2Count(), true);
		default:
			throw new IllegalArgumentException(
					"Invalid build slot index: '" + index + "'.");
		}
	}
	
	public void setBuildSlot(Slot buildSlot, int index, boolean newOrder) {
		switch (index) {
		case 0:
			setBuildSlot0Id(buildSlot.getId());
			setBuildSlot0Count(buildSlot.getCount());
			if (newOrder)
				setBuildSlot0Ordered((long) buildSlot.getCount());
			break;
		case 1:
			setBuildSlot1Id(buildSlot.getId());
			setBuildSlot1Count(buildSlot.getCount());
			if (newOrder)
				setBuildSlot1Ordered((long) buildSlot.getCount());
			break;
		case 2:
			setBuildSlot2Id(buildSlot.getId());
			setBuildSlot2Count(buildSlot.getCount());
			if (newOrder)
				setBuildSlot2Ordered((long) buildSlot.getCount());
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid build slot index: '" + index + "'.");
		}
	}
	
	public long getBuildSlotOrdered(int index) {
		switch (index) {
		case 0:
			return getBuildSlot0Ordered();
		case 1:
			return getBuildSlot1Ordered();
		case 2:
			return getBuildSlot2Ordered();
		default:
			throw new IllegalArgumentException(
					"Invalid build slot index: '" + index + "'.");
		}
	}
	
	public void setBuildSlotOrdered(long ordered, int index) {
		switch (index) {
		case 0:
			setBuildSlot0Ordered(ordered);
			break;
		case 1:
			setBuildSlot1Ordered(ordered);
			break;
		case 2:
			setBuildSlot2Ordered(ordered);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid build slot index: '" + index + "'.");
		}
	}
	
	public Structure getStructure() {
		return DataAccess.getStructureById(getIdStructure());
	}
	
	public void update() {
		// Construction de vaisseaux
		long now = Utilities.now();
    	long shipProductionTime = now - getLastUpdate();
    	double shipProduction = getShipProduction();
    	
    	for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
    	    Slot buildSlot = getBuildSlot(i);
    	    int shipId = buildSlot.getId();
    	    
    	    if (shipId != 0) {
    	    	if (shipProduction > 0) {
	    	        // Cherche un emplacement sur le système qui peut recevoir
	    	    	// les vaisseaux en cours de production. S'il n'y a pas
	    	    	// d'emplacement disponible la production est bloquée
	    	        int slotIndex = -1;
	    	        for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
	    	            if (getSlot(j).getId() == shipId) {
	        	        	// Emplacement contenant des vaisseaux du même type
	    	            	// que ceux qui sont en production
	    	                slotIndex = j;
	    	                break;
	    	            }
	    	        }
	    	        
	    	        if (slotIndex == -1) {
	    	            for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
	    	                if (getSlot(j).getId() == 0) {
	        	            	// Emplacement vide dans le système
	    	                    slotIndex = j;
	    	                    break;
	    	                }
	    	            }
	    	        }
	    	        
	    	        // Pas d'emplacement disponible ?
	    	        if (slotIndex == -1)
	    	            continue;
	    	        
	    			// Calcule le nombre de vaisseaux construits
	    			double ships = shipProduction * shipProductionTime /
	    				Ship.SHIPS[shipId].getBuildTime();
	    			long newShips;
	    			
	    			if (ships > buildSlot.getCount()) {
	    				// Le slot de construction est terminé
	    				shipProductionTime *= 1 - (buildSlot.getCount() / ships);
	    				newShips = (long) Math.ceil(buildSlot.getCount());
	    				setBuildSlot(new Slot(), i, true);
	    			} else {
	    				shipProductionTime = 0;
	    				newShips = (long) (Math.ceil(buildSlot.getCount()) -
	    					Math.ceil(buildSlot.getCount() - ships));
	    				buildSlot.setCount(buildSlot.getCount() - ships);
	    				setBuildSlot(buildSlot, i, false);
	    			}
	    			
	    			if (newShips > 0) {
	    				Slot oldSlot = getSlot(slotIndex);
	    				Slot slot = new Slot(shipId,
	    					oldSlot.getId() == shipId ?
	    					oldSlot.getCount() + newShips : newShips, true);
	    				setSlot(slot, slotIndex);
	    				
//						GameEventsDispatcher.fireGameNotification(new BuiltShipsEvent(
//							system, shipId, newShips));
	    			}
    	    	}
    		}
    		
    		if (shipProductionTime <= 0)
    			break;
    	}
    	
    	setLastUpdate(now);
	}
	
	public boolean isBuilding() {
		for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
    	    Slot buildSlot = getBuildSlot(i);
    	    int shipId = buildSlot.getId();
    	    
    	    if (shipId != 0)
    	    	return true;
		}
		
		return false;
	}
	
	
	public boolean isFull(){
		
		long actualCapacity=0;
		for(int i=0;i<5;i++)
		{
			
			Slot slot=getSlot(i);
			if(slot.getCount()>0)
			{
				actualCapacity+=slot.getCount()*slot.getShip().getPower();
				if(actualCapacity>=getStructure().getMaxShips())
				{
					return true;
				}
			}
		}
		
		for(int i=0;i<GameConstants.SHIPS_QUEUE_LENGTH-1;i++)
		{
			Slot buildSlot = getBuildSlot(i);
			if(buildSlot.getCount()>0)
			{
				actualCapacity+=buildSlot.getShip().getPower()*buildSlot.getCount();
				if(actualCapacity>=getStructure().getMaxShips())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isEmpty(){
		
		for(int i=0;i<5;i++)
		{
			Slot slot=getSlot(i);
			if(slot.getCount()>0)
			{
					return false;
			}
		}
		return true;
	}
	
	public int getBuildEnd() {
		for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
    	    Slot buildSlot = getBuildSlot(i);
    	    int shipId = buildSlot.getId();
    	    
    	    if (shipId != 0) {
    	    	Ship ship = Ship.SHIPS[shipId];
    	    	double shipProduction = getShipProduction();
    	    	
    	    	if (shipProduction == 0)
    	    		return Integer.MAX_VALUE;
    	    	
    	    	return (int) Math.ceil(ship.getBuildTime() *
    	    			buildSlot.getCount() / shipProduction);
    	    }
		}
		
		return 0;
	}
	
	public double getShipProduction() {
		return Math.pow(1.7, getStructure().getModuleLevel(
				StructureModule.TYPE_HANGAR));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
