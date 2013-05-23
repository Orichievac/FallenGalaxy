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

public class StructureSpaceshipYardBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long idStructure;
	private int slot0Id;
	private long slot0Count;
	private int slot1Id;
	private long slot1Count;
	private int slot2Id;
	private long slot2Count;
	private int slot3Id;
	private long slot3Count;
	private int slot4Id;
	private long slot4Count;
	private int buildSlot0Id;
	private double buildSlot0Count;
	private long buildSlot0Ordered;
	private int buildSlot1Id;
	private double buildSlot1Count;
	private long buildSlot1Ordered;
	private int buildSlot2Id;
	private double buildSlot2Count;
	private long buildSlot2Ordered;
	private long lastBoughtFleet;
	private long lastUpdate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getIdStructure() {
		return idStructure;
	}
	
	public void setIdStructure(long idStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idStructure = idStructure;
	}
	
	public int getSlot0Id() {
		return slot0Id;
	}
	
	public void setSlot0Id(int slot0Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot0Id = slot0Id;
	}
	
	public long getSlot0Count() {
		return slot0Count;
	}
	
	public void setSlot0Count(long slot0Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot0Count = slot0Count;
	}
	
	public int getSlot1Id() {
		return slot1Id;
	}
	
	public void setSlot1Id(int slot1Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot1Id = slot1Id;
	}
	
	public long getSlot1Count() {
		return slot1Count;
	}
	
	public void setSlot1Count(long slot1Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot1Count = slot1Count;
	}
	
	public int getSlot2Id() {
		return slot2Id;
	}
	
	public void setSlot2Id(int slot2Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot2Id = slot2Id;
	}
	
	public long getSlot2Count() {
		return slot2Count;
	}
	
	public void setSlot2Count(long slot2Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot2Count = slot2Count;
	}
	
	public int getSlot3Id() {
		return slot3Id;
	}
	
	public void setSlot3Id(int slot3Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot3Id = slot3Id;
	}
	
	public long getSlot3Count() {
		return slot3Count;
	}
	
	public void setSlot3Count(long slot3Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot3Count = slot3Count;
	}
	
	public int getSlot4Id() {
		return slot4Id;
	}
	
	public void setSlot4Id(int slot4Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot4Id = slot4Id;
	}
	
	public long getSlot4Count() {
		return slot4Count;
	}
	
	public void setSlot4Count(long slot4Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot4Count = slot4Count;
	}
	
	public int getBuildSlot0Id() {
		return buildSlot0Id;
	}
	
	public void setBuildSlot0Id(int buildSlot0Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot0Id = buildSlot0Id;
	}
	
	public double getBuildSlot0Count() {
		return buildSlot0Count;
	}
	
	public void setBuildSlot0Count(double buildSlot0Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot0Count = buildSlot0Count;
	}
	
	public long getBuildSlot0Ordered() {
		return buildSlot0Ordered;
	}
	
	public void setBuildSlot0Ordered(long buildSlot0Ordered) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot0Ordered = buildSlot0Ordered;
	}
	
	public int getBuildSlot1Id() {
		return buildSlot1Id;
	}
	
	public void setBuildSlot1Id(int buildSlot1Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot1Id = buildSlot1Id;
	}
	
	public double getBuildSlot1Count() {
		return buildSlot1Count;
	}
	
	public void setBuildSlot1Count(double buildSlot1Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot1Count = buildSlot1Count;
	}
	
	public long getBuildSlot1Ordered() {
		return buildSlot1Ordered;
	}
	
	public void setBuildSlot1Ordered(long buildSlot1Ordered) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot1Ordered = buildSlot1Ordered;
	}
	
	public int getBuildSlot2Id() {
		return buildSlot2Id;
	}
	
	public void setBuildSlot2Id(int buildSlot2Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot2Id = buildSlot2Id;
	}
	
	public double getBuildSlot2Count() {
		return buildSlot2Count;
	}
	
	public void setBuildSlot2Count(double buildSlot2Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot2Count = buildSlot2Count;
	}
	
	public long getBuildSlot2Ordered() {
		return buildSlot2Ordered;
	}
	
	public void setBuildSlot2Ordered(long buildSlot2Ordered) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.buildSlot2Ordered = buildSlot2Ordered;
	}
	
	public long getLastBoughtFleet() {
		return lastBoughtFleet;
	}
	
	public void setLastBoughtFleet(long lastBoughtFleet) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.lastBoughtFleet = lastBoughtFleet;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.lastUpdate = lastUpdate;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
