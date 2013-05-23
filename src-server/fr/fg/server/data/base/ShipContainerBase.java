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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class ShipContainerBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int slot0Id;
	private long slot0Count;
	private boolean slot0Front;
	private int slot1Id;
	private long slot1Count;
	private boolean slot1Front;
	private int slot2Id;
	private long slot2Count;
	private boolean slot2Front;
	private int slot3Id;
	private long slot3Count;
	private boolean slot3Front;
	private int slot4Id;
	private long slot4Count;
	private boolean slot4Front;
	private int slot5Id;
	private long slot5Count;
	private boolean slot5Front;
	private int slot6Id;
	private long slot6Count;
	private boolean slot6Front;
	private int slot7Id;
	private long slot7Count;
	private boolean slot7Front;
	private int slot8Id;
	private long slot8Count;
	private boolean slot8Front;
	private int slot9Id;
	private long slot9Count;
	private boolean slot9Front;
	private String encodedSkirmishSlots;
	private String encodedSkirmishAbilities;
	private String encodedBattleSlots;
	private String encodedBattleAbilities;
	private boolean tacticsDefined;
	private int idFleet;
	private long idStructure;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
			this.id = id;
	}
	
	public int getSlot0Id() {
		return slot0Id;
	}
	
	public void setSlot0Id(int slot0Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot0Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot0Id + "' (must be >= 0).");
		else
			this.slot0Id = slot0Id;
	}
	
	public long getSlot0Count() {
		return slot0Count;
	}
	
	public void setSlot0Count(long slot0Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot0Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot0Count + "' (must be >= 0).");
		else
			this.slot0Count = slot0Count;
	}
	
	public boolean isSlot0Front() {
		return slot0Front;
	}
	
	public void setSlot0Front(boolean slot0Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot0Front = slot0Front;
	}
	
	public int getSlot1Id() {
		return slot1Id;
	}
	
	public void setSlot1Id(int slot1Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot1Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot1Id + "' (must be >= 0).");
		else
			this.slot1Id = slot1Id;
	}
	
	public long getSlot1Count() {
		return slot1Count;
	}
	
	public void setSlot1Count(long slot1Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot1Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot1Count + "' (must be >= 0).");
		else
			this.slot1Count = slot1Count;
	}
	
	public boolean isSlot1Front() {
		return slot1Front;
	}
	
	public void setSlot1Front(boolean slot1Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot1Front = slot1Front;
	}
	
	public int getSlot2Id() {
		return slot2Id;
	}
	
	public void setSlot2Id(int slot2Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot2Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot2Id + "' (must be >= 0).");
		else
			this.slot2Id = slot2Id;
	}
	
	public long getSlot2Count() {
		return slot2Count;
	}
	
	public void setSlot2Count(long slot2Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot2Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot2Count + "' (must be >= 0).");
		else
			this.slot2Count = slot2Count;
	}
	
	public boolean isSlot2Front() {
		return slot2Front;
	}
	
	public void setSlot2Front(boolean slot2Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot2Front = slot2Front;
	}
	
	public int getSlot3Id() {
		return slot3Id;
	}
	
	public void setSlot3Id(int slot3Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot3Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot3Id + "' (must be >= 0).");
		else
			this.slot3Id = slot3Id;
	}
	
	public long getSlot3Count() {
		return slot3Count;
	}
	
	public void setSlot3Count(long slot3Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot3Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot3Count + "' (must be >= 0).");
		else
			this.slot3Count = slot3Count;
	}
	
	public boolean isSlot3Front() {
		return slot3Front;
	}
	
	public void setSlot3Front(boolean slot3Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot3Front = slot3Front;
	}
	
	public int getSlot4Id() {
		return slot4Id;
	}
	
	public void setSlot4Id(int slot4Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot4Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot4Id + "' (must be >= 0).");
		else
			this.slot4Id = slot4Id;
	}
	
	public long getSlot4Count() {
		return slot4Count;
	}
	
	public void setSlot4Count(long slot4Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot4Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot4Count + "' (must be >= 0).");
		else
			this.slot4Count = slot4Count;
	}
	
	public boolean isSlot4Front() {
		return slot4Front;
	}
	
	public void setSlot4Front(boolean slot4Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot4Front = slot4Front;
	}
	
	public int getSlot5Id() {
		return slot5Id;
	}
	
	public void setSlot5Id(int slot5Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot5Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot5Id + "' (must be >= 0).");
		else
			this.slot5Id = slot5Id;
	}
	
	public long getSlot5Count() {
		return slot5Count;
	}
	
	public void setSlot5Count(long slot5Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot5Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot5Count + "' (must be >= 0).");
		else
			this.slot5Count = slot5Count;
	}
	
	public boolean isSlot5Front() {
		return slot5Front;
	}
	
	public void setSlot5Front(boolean slot5Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot5Front = slot5Front;
	}
	
	public int getSlot6Id() {
		return slot6Id;
	}
	
	public void setSlot6Id(int slot6Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot6Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot6Id + "' (must be >= 0).");
		else
			this.slot6Id = slot6Id;
	}
	
	public long getSlot6Count() {
		return slot6Count;
	}
	
	public void setSlot6Count(long slot6Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot6Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot6Count + "' (must be >= 0).");
		else
			this.slot6Count = slot6Count;
	}
	
	public boolean isSlot6Front() {
		return slot6Front;
	}
	
	public void setSlot6Front(boolean slot6Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot6Front = slot6Front;
	}
	
	public int getSlot7Id() {
		return slot7Id;
	}
	
	public void setSlot7Id(int slot7Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot7Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot7Id + "' (must be >= 0).");
		else
			this.slot7Id = slot7Id;
	}
	
	public long getSlot7Count() {
		return slot7Count;
	}
	
	public void setSlot7Count(long slot7Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot7Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot7Count + "' (must be >= 0).");
		else
			this.slot7Count = slot7Count;
	}
	
	public boolean isSlot7Front() {
		return slot7Front;
	}
	
	public void setSlot7Front(boolean slot7Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot7Front = slot7Front;
	}
	
	public int getSlot8Id() {
		return slot8Id;
	}
	
	public void setSlot8Id(int slot8Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot8Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot8Id + "' (must be >= 0).");
		else
			this.slot8Id = slot8Id;
	}
	
	public long getSlot8Count() {
		return slot8Count;
	}
	
	public void setSlot8Count(long slot8Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot8Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot8Count + "' (must be >= 0).");
		else
			this.slot8Count = slot8Count;
	}
	
	public boolean isSlot8Front() {
		return slot8Front;
	}
	
	public void setSlot8Front(boolean slot8Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot8Front = slot8Front;
	}
	
	public int getSlot9Id() {
		return slot9Id;
	}
	
	public void setSlot9Id(int slot9Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot9Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot9Id + "' (must be >= 0).");
		else
			this.slot9Id = slot9Id;
	}
	
	public long getSlot9Count() {
		return slot9Count;
	}
	
	public void setSlot9Count(long slot9Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot9Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot9Count + "' (must be >= 0).");
		else
			this.slot9Count = slot9Count;
	}
	
	public boolean isSlot9Front() {
		return slot9Front;
	}
	
	public void setSlot9Front(boolean slot9Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot9Front = slot9Front;
	}
	
	public String getEncodedSkirmishSlots() {
		return encodedSkirmishSlots;
	}
	
	public void setEncodedSkirmishSlots(String encodedSkirmishSlots) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkirmishSlots == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkirmishSlots + "' (must not be null).");
		else
			this.encodedSkirmishSlots = encodedSkirmishSlots;
	}
	
	public String getEncodedSkirmishAbilities() {
		return encodedSkirmishAbilities;
	}
	
	public void setEncodedSkirmishAbilities(String encodedSkirmishAbilities) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkirmishAbilities == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkirmishAbilities + "' (must not be null).");
		else
			this.encodedSkirmishAbilities = encodedSkirmishAbilities;
	}
	
	public String getEncodedBattleSlots() {
		return encodedBattleSlots;
	}
	
	public void setEncodedBattleSlots(String encodedBattleSlots) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedBattleSlots == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedBattleSlots + "' (must not be null).");
		else
			this.encodedBattleSlots = encodedBattleSlots;
	}
	
	public String getEncodedBattleAbilities() {
		return encodedBattleAbilities;
	}
	
	public void setEncodedBattleAbilities(String encodedBattleAbilities) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedBattleAbilities == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedBattleAbilities + "' (must not be null).");
		else
			this.encodedBattleAbilities = encodedBattleAbilities;
	}
	
	public boolean isTacticsDefined() {
		return tacticsDefined;
	}
	
	public void setTacticsDefined(boolean tacticsDefined) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.tacticsDefined = tacticsDefined;
	}
	
	public int getIdFleet() {
		return idFleet;
	}
	
	public void setIdFleet(int idFleet) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idFleet < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idFleet + "' (must be >= 0).");
		else
			this.idFleet = idFleet;
	}
	
	public long getIdStructure() {
		return idStructure;
	}
	
	public void setIdStructure(long idStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idStructure < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idStructure + "' (must be >= 0).");
		else
			this.idStructure = idStructure;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
