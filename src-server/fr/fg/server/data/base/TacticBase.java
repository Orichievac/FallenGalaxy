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

public class TacticBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idPlayer;
	private String name;
	private String hash;
	private long date;
	private int slot_id_0;
	private int slot_id_1;
	private int slot_id_2;
	private int slot_id_3;
	private int slot_id_4;
	private long slot0_count;
	private long slot1_count;
	private long slot2_count;
	private long slot3_count;
	private long slot4_count;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (name == null)
			throw new IllegalArgumentException("Invalid value: '" +
				name + "' (must not be null).");
		else
			this.name = name;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (hash == null)
			throw new IllegalArgumentException("Invalid value: '" +
				hash + "' (must not be null).");
		else
			this.hash = hash;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public int getSlotId0(){
		 return slot_id_0;
	}
	
	public void setSlotId0(int slot_id_0){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot_id_0 = slot_id_0;
	}
	
	public long getTacticSlot0Count(){
		 return slot0_count;
	}
	
	public void setTacticSlot0Count(long slot0_count){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot0_count = slot0_count;
	}
	
	public int getSlotId1(){
		 return slot_id_1;
	}
	
	public void setSlotId1(int slot_id_1){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot_id_1 = slot_id_1;
	}
	
	public long getTacticSlot1Count(){
		 return slot1_count;
	}
	
	public void setTacticSlot1Count(long slot1_count){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot1_count = slot1_count;
	}
	
	public int getSlotId2(){
		 return slot_id_2;
	}
	
	public void setSlotId2(int slot_id_2){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot_id_2 = slot_id_2;
	}
	
	public long getTacticSlot2Count(){
		 return slot2_count;
	}
	
	public void setTacticSlot2Count(long slot2_count){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot2_count = slot2_count;
	}
	
	public int getSlotId3(){
		 return slot_id_3;
	}
	
	public void setSlotId3(int slot_id_3){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot_id_3 = slot_id_3;
	}
	
	public long getTacticSlot3Count(){
		 return slot3_count;
	}
	
	public void setTacticSlot3Count(long slot3_count){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot3_count = slot3_count;
	}
	
	public int getSlotId4(){
		 return slot_id_4;
	}
	
	public void setSlotId4(int slot_id_4){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot_id_4 = slot_id_4;
	}
	
	public long getTacticSlot4Count(){
		 return slot4_count;
	}
	
	public void setTacticSlot4Count(long slot4_count){
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot4_count = slot4_count;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
