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

import java.util.Arrays;
import java.util.List;

import fr.fg.server.data.base.ShipContainerBase;

public class ShipContainer extends ShipContainerBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		CONTAINER_FLEET = 0,
		CONTAINER_SYSTEM = 1,
		CONTAINER_STRUCTURE = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idSystem;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ShipContainer() {
		// Nécessaire pour la construction par réflection
	}

	public ShipContainer(int containerType, int id) {
		this(containerType, (long) id);
	}
	
	public ShipContainer(int containerType, long id) {
		setSlot0Id(0);
		setSlot0Count(0);
		setSlot0Front(true);
		setSlot1Id(0);
		setSlot1Count(0);
		setSlot1Front(true);
		setSlot2Id(0);
		setSlot2Count(0);
		setSlot2Front(true);
		setSlot3Id(0);
		setSlot3Count(0);
		setSlot3Front(true);
		setSlot4Id(0);
		setSlot4Count(0);
		setSlot4Front(true);
		setSlot5Id(0);
		setSlot5Count(0);
		setSlot5Front(true);
		setSlot6Id(0);
		setSlot6Count(0);
		setSlot6Front(true);
		setSlot7Id(0);
		setSlot7Count(0);
		setSlot7Front(true);
		setSlot8Id(0);
		setSlot8Count(0);
		setSlot8Front(true);
		setSlot9Id(0);
		setSlot9Count(0);
		setSlot9Front(true);
		setEncodedSkirmishSlots("99999");
		setEncodedSkirmishAbilities("99999");
		setEncodedBattleSlots("999999999999999");
		setEncodedBattleAbilities("999999999999999");
		setTacticsDefined(false);
		setIdFleet(0);
		setIdStructure(0);
		
		switch (containerType) {
		case CONTAINER_FLEET:
			setIdFleet((int) id);
			break;
		case CONTAINER_SYSTEM:
			idSystem = (int) id;
			break;
		case CONTAINER_STRUCTURE:
			setIdStructure(id);
			break;
		default:
			throw new IllegalArgumentException("Invalid container: '" + containerType + "'.");
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean mayReceiveTactics() {
		return getContainerType() == CONTAINER_FLEET;
	}
	
	public int getContainerType() {
		if (getIdFleet() != 0)
			return CONTAINER_FLEET;
		else if (idSystem != 0)
			return CONTAINER_SYSTEM;
		else if (getIdStructure() != 0)
			return CONTAINER_STRUCTURE;
		else
			throw new IllegalStateException("Unknown container type.");
	}
	
	public Fleet getFleet() {
		if (getContainerType() == CONTAINER_FLEET)
			return DataAccess.getFleetById(getIdFleet());
		else
			throw new IllegalStateException("Not a fleet container.");
	}
	
	public Structure getStructure() {
		if (getContainerType() == CONTAINER_STRUCTURE)
			return DataAccess.getStructureById(getIdStructure());
		else
			throw new IllegalStateException("Not a structure container.");
	}
	
	public int getIdSystem() {
		return idSystem;
	}
	
	public StarSystem getSystem() {
		if (getContainerType() == CONTAINER_SYSTEM)
			return DataAccess.getSystemById(idSystem);
		else
			throw new IllegalStateException("Not a system container.");
	}
	
	public int getMaxSlots() {
		switch (getContainerType()) {
		case CONTAINER_SYSTEM:
		case CONTAINER_STRUCTURE:
			return 10;
		default:
			return 5;
		}
	}
	
	public Slot getSlot(int index) {
		switch (index) {
		case 0:
			return new Slot(getSlot0Id(), getSlot0Count(), isSlot0Front());
		case 1:
			return new Slot(getSlot1Id(), getSlot1Count(), isSlot1Front());
		case 2:
			return new Slot(getSlot2Id(), getSlot2Count(), isSlot2Front());
		case 3:
			return new Slot(getSlot3Id(), getSlot3Count(), isSlot3Front());
		case 4:
			return new Slot(getSlot4Id(), getSlot4Count(), isSlot4Front());
		case 5:
			return new Slot(getSlot5Id(), getSlot5Count(), isSlot5Front());
		case 6:
			return new Slot(getSlot6Id(), getSlot6Count(), isSlot6Front());
		case 7:
			return new Slot(getSlot7Id(), getSlot7Count(), isSlot7Front());
		case 8:
			return new Slot(getSlot8Id(), getSlot8Count(), isSlot8Front());
		case 9:
			return new Slot(getSlot9Id(), getSlot9Count(), isSlot9Front());
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public Slot[] getSlots() {
		return new Slot[]{
			getSlot(0), getSlot(1), getSlot(2), getSlot(3), getSlot(4),
			getSlot(5), getSlot(6), getSlot(7), getSlot(8), getSlot(9),
		};
	}
	
	public void setSlot(Slot slot, int index) {
		int oldSlotId;
		
		switch (index) {
		case 0:
			oldSlotId = getSlot0Id();
			setSlot0Id(slot.getId());
			setSlot0Count((long) slot.getCount());
			setSlot0Front(slot.isFront());
			break;
		case 1:
			oldSlotId = getSlot1Id();
			setSlot1Id(slot.getId());
			setSlot1Count((long) slot.getCount());
			setSlot1Front(slot.isFront());
			break;
		case 2:
			oldSlotId = getSlot2Id();
			setSlot2Id(slot.getId());
			setSlot2Count((long) slot.getCount());
			setSlot2Front(slot.isFront());
			break;
		case 3:
			oldSlotId = getSlot3Id();
			setSlot3Id(slot.getId());
			setSlot3Count((long) slot.getCount());
			setSlot3Front(slot.isFront());
			break;
		case 4:
			oldSlotId = getSlot4Id();
			setSlot4Id(slot.getId());
			setSlot4Count((long) slot.getCount());
			setSlot4Front(slot.isFront());
			break;
		case 5:
			oldSlotId = getSlot5Id();
			setSlot5Id(slot.getId());
			setSlot5Count((long) slot.getCount());
			setSlot5Front(slot.isFront());
			break;
		case 6:
			oldSlotId = getSlot6Id();
			setSlot6Id(slot.getId());
			setSlot6Count((long) slot.getCount());
			setSlot6Front(slot.isFront());
			break;
		case 7:
			oldSlotId = getSlot7Id();
			setSlot7Id(slot.getId());
			setSlot7Count((long) slot.getCount());
			setSlot7Front(slot.isFront());
			break;
		case 8:
			oldSlotId = getSlot8Id();
			setSlot8Id(slot.getId());
			setSlot8Count((long) slot.getCount());
			setSlot8Front(slot.isFront());
			break;
		case 9:
			oldSlotId = getSlot9Id();
			setSlot9Id(slot.getId());
			setSlot9Count((long) slot.getCount());
			setSlot9Front(slot.isFront());
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
		
		if (mayReceiveTactics() && oldSlotId != slot.getId()) {
			if (slot.getId() == 0)
				resetSlotActions(index);
			else
				updateTactics();
		}
	}
	
	public void setSlotFront(boolean front, int index) {
		switch (index) {
		case 0:
			setSlot0Front(front);
			break;
		case 1:
			setSlot1Front(front);
			break;
		case 2:
			setSlot2Front(front);
			break;
		case 3:
			setSlot3Front(front);
			break;
		case 4:
			setSlot4Front(front);
			break;
		case 5:
			setSlot5Front(front);
			break;
		case 6:
			setSlot6Front(front);
			break;
		case 7:
			setSlot7Front(front);
			break;
		case 8:
			setSlot8Front(front);
			break;
		case 9:
			setSlot9Front(front);
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
	
	public void setSkirmishActions(int[] slots, int[] abilities) {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		setEncodedSkirmishSlots(encodeActions(slots));
		setEncodedSkirmishAbilities(encodeActions(abilities));
	}
	
	public void setBattleActions(int[] slots, int[] abilities) {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		setEncodedBattleSlots(encodeActions(slots));
		setEncodedBattleAbilities(encodeActions(abilities));
	}
	
	public int[] getSkirmishActionSlots() {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		return decodeActions(getEncodedSkirmishSlots());
	}

	public int[] getSkirmishActionAbilities() {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		return decodeActions(getEncodedSkirmishAbilities());
	}

	public int[] getBattleActionSlots() {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		return decodeActions(getEncodedBattleSlots());
	}

	public int[] getBattleActionAbilities() {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		return decodeActions(getEncodedBattleAbilities());
	}
	
	public void updateTactics() {
		if (!mayReceiveTactics())
			throw new IllegalStateException(
				"Container not eligible to tactics.");
		
		int[] skirmishSlots		= new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		int[] skirmishAbilities = new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		
		scheduleActions(skirmishSlots, skirmishAbilities);
		setSkirmishActions(skirmishSlots, skirmishAbilities);
		
		int[] battleSlots	  = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		int[] battleAbilities = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		
		scheduleActions(battleSlots, battleAbilities);
		setBattleActions(battleSlots, battleAbilities);
		
		// Place les cargos derrière
		int slotsCount = 0, freightersCount = 0;
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Ship ship = getSlot(i).getShip();
			
			if (ship != null) {
				slotsCount++;
				
				if (ship.getShipClass() == Ship.FREIGHTER)
					freightersCount++;
			}
		}
		
		int backFreightersCount = Math.min(slotsCount / 2, freightersCount);
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Ship ship = getSlot(i).getShip();
			
			if (ship != null)
				setSlotFront(true, i);
		}
		
		if (backFreightersCount > 0) {
			int count = 0;
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
				Ship ship = getSlot(i).getShip();
				
				if (ship != null && ship.getShipClass() == Ship.FREIGHTER) {
					setSlotFront(false, i);
					
					if (++count == backFreightersCount)
						break;
				}
			}
		}
		
		setTacticsDefined(false);
	}
	
	public long getShipsCount() {
		return getSlot0Count() +
			getSlot1Count() + getSlot2Count() + getSlot3Count() +
			getSlot4Count() + getSlot5Count() + getSlot6Count() +
			getSlot7Count() + getSlot8Count() + getSlot9Count();
	}
	
	public void copy(ShipContainer shipContainer) {
		setSlot0Id(shipContainer.getSlot0Id());
		setSlot0Count(shipContainer.getSlot0Count());
		setSlot0Front(shipContainer.isSlot0Front());
		setSlot1Id(shipContainer.getSlot1Id());
		setSlot1Count(shipContainer.getSlot1Count());
		setSlot1Front(shipContainer.isSlot1Front());
		setSlot2Id(shipContainer.getSlot2Id());
		setSlot2Count(shipContainer.getSlot2Count());
		setSlot2Front(shipContainer.isSlot2Front());
		setSlot3Id(shipContainer.getSlot3Id());
		setSlot3Count(shipContainer.getSlot3Count());
		setSlot3Front(shipContainer.isSlot3Front());
		setSlot4Id(shipContainer.getSlot4Id());
		setSlot4Count(shipContainer.getSlot4Count());
		setSlot4Front(shipContainer.isSlot4Front());
		setSlot5Id(shipContainer.getSlot5Id());
		setSlot5Count(shipContainer.getSlot5Count());
		setSlot5Front(shipContainer.isSlot5Front());
		setSlot6Id(shipContainer.getSlot6Id());
		setSlot6Count(shipContainer.getSlot6Count());
		setSlot6Front(shipContainer.isSlot6Front());
		setSlot7Id(shipContainer.getSlot7Id());
		setSlot7Count(shipContainer.getSlot7Count());
		setSlot7Front(shipContainer.isSlot7Front());
		setSlot8Id(shipContainer.getSlot8Id());
		setSlot8Count(shipContainer.getSlot8Count());
		setSlot8Front(shipContainer.isSlot8Front());
		setSlot9Id(shipContainer.getSlot9Id());
		setSlot9Count(shipContainer.getSlot9Count());
		setSlot9Front(shipContainer.isSlot9Front());
		setEncodedSkirmishSlots(shipContainer.getEncodedSkirmishSlots());
		setEncodedSkirmishAbilities(shipContainer.getEncodedSkirmishAbilities());
		setEncodedBattleSlots(shipContainer.getEncodedBattleSlots());
		setEncodedBattleAbilities(shipContainer.getEncodedBattleAbilities());
		setTacticsDefined(shipContainer.isTacticsDefined());
	}
	
	@Override
	public void save() {
		switch (getContainerType()) {
		case CONTAINER_FLEET:
		case CONTAINER_STRUCTURE:
			super.save();
			break;
		default:
			throw new IllegalStateException(
				"Container type is not persistable: '" +
				getContainerType() + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	// Efface les actions d'un vaisseau sans affecter les actions des autres
	// vaisseaux
	private void resetSlotActions(int slot) {
		int[] skirmishActions = getSkirmishActionSlots();
		int[] skirmishAbilities = getSkirmishActionAbilities();
		int[] battleActions = getBattleActionSlots();
		int[] battleAbilities = getBattleActionAbilities();
		
		for (int i = 0; i < skirmishActions.length; i++)
			if (skirmishActions[i] == slot) {
				skirmishActions[i] = -1;
				skirmishAbilities[i] = -1;
			}
		
		for (int i = 0; i < battleActions.length; i++)
			if (battleActions[i] == slot) {
				battleActions[i] = -1;
				battleAbilities[i] = -1;
			}
		
		setSkirmishActions(skirmishActions, skirmishAbilities);
		setBattleActions(battleActions, battleAbilities);
		
		// Déplace un slot sur la ligne de front s'il y a en trop à l'arrière
		int frontCount = 0, backCount = 0;
		for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
			if (getSlot(j).getId() != 0)
				if (getSlot(j).isFront())
					frontCount++;
				else
					backCount++;
		
		loop:while (backCount > frontCount) {
			for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
				if (getSlot(j).getId() != 0 &&
						!getSlot(j).isFront() &&
						getSlot(j).getShip().getShipClass() != Ship.FREIGHTER) {
					setSlotFront(true, j);
					backCount--;
					frontCount++;
					continue loop;
				}
			
			for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
				if (getSlot(j).getId() != 0 && !getSlot(j).isFront()) {
					setSlotFront(true, j);
					backCount--;
					frontCount++;
					continue loop;
				}
		}
	}
	
	private void scheduleActions(int[] slots, int[] abilities) {
		List<Research> researches = DataAccess.getResearchesByPlayer(
				getFleet().getIdOwner());
		
		Arrays.fill(slots, -1);
		Arrays.fill(abilities, -1);
		
		// Regénère les actions
		for (int i = 0; i < slots.length; i++) {
			for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
				int shipId = getSlot(j).getId();
				
				if (shipId == 0)
					continue;
				
				if (getShipUnavailabilityTime(slots, abilities, j, i) > 0)
					continue;
				
				int ability = -2;
				
				if (Ship.SHIPS[shipId].getWeapons().length > 0) {
					ability = -1;
				} else {
					// Recherche une compétence active pour laquelle le joueur
					// a développé tous les prérequis
					Ability[] shipAbilities = Ship.SHIPS[shipId].getAbilities();
					abilities:for (int k = 0; k < shipAbilities.length; k++) {
						Ability shipAbility = shipAbilities[k];
						
						if (!shipAbility.isPassive()) {
							requirements:for (int requirement : shipAbility.getRequirements()) {
								synchronized (researches) {
									for (Research research : researches) {
										if (research.getIdTechnology() == requirement) {
											if (research.getProgress() < 1) {
												continue abilities;
											} else {
												continue requirements;
											}
										}
									}
								}
								
								continue abilities;
							}
							
							if (shipAbility.getCooldown() <= slots.length + 4 - i - 1) {
								ability = k;
								break abilities;
							}
						}
					}
				}
				
				if (ability != -2) {
					slots[i] = j;
					abilities[i] = ability;
					break;
				}
			}
		}
	}
	
	private int getShipUnavailabilityTime(int[] slots, int[] abilities, int slotIndex, int step) {
		// Vérifie que le vaisseau est disponible
		for (int k = 0; k < step; k++) {
			int actionSlot = slots[k];
			
			if (actionSlot == slotIndex) {
				Ship ship = Ship.SHIPS[getSlot(slotIndex).getId()];
				
				int ability = abilities[k];
				int cooldown = ability == -1 ? 4 :
					ship.getAbilities()[ability].getCooldown();
				
				if (k + cooldown >= step)
					return 1 + k + cooldown - step;
			}
		}
		
		return 0;
	}
	
	private String encodeActions(int[] actions) {
		StringBuffer result = new StringBuffer(actions.length);
		for (int i = 0; i < actions.length; i++)
			result.append(actions[i] == -1 ? "9" : String.valueOf(actions[i]));
		return result.toString();
	}
	
	private int[] decodeActions(String actions) {
		int[] result = new int[actions.length()];
		for (int i = 0; i < actions.length(); i++) {
			result[i] = Integer.parseInt(String.valueOf(actions.charAt(i)));
			result[i] = result[i] == 9 ? -1 : result[i];
		}
		return result;
	}
}
