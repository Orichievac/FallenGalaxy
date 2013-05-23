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

public class StarSystemBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String name;
	private int x;
	private int y;
	private int shortcut;
	private boolean ai;
	private int starImage;
	private int asteroidBelt;
	private double population;
	private int availableSpace;
	private long encodedAvailableResources;
	private double resource0;
	private double resource1;
	private double resource2;
	private double resource3;
	private long laboratory;
	private long storehouse;
	private long spaceshipYard;
	private long defensiveDeck;
	private long exploitation0;
	private long exploitation1;
	private long exploitation2;
	private long exploitation3;
	private long extractorCenter;
	private long civilianInfrastructures;
	private long corporations;
	private long tradePort;
	private long researchCenter;
	private long factory;
	private long refinery;
	private int encodedCurrentBuilding;
	private long currentBuildingEnd;
	private int encodedNextBuilding;
	private long nextBuildingEnd;
	private int encodedThirdBuilding;
	private long thirdBuildingEnd;
	private long lastUpdate;
	private long lastResearchUpdate;
	private long lastPopulationUpdate;
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
	private int slot5Id;
	private long slot5Count;
	private int slot6Id;
	private long slot6Count;
	private int slot7Id;
	private long slot7Count;
	private int slot8Id;
	private long slot8Count;
	private int slot9Id;
	private long slot9Count;
	private int buildSlot0Id;
	private double buildSlot0Count;
	private long buildSlot0Ordered;
	private int buildSlot1Id;
	private double buildSlot1Count;
	private long buildSlot1Ordered;
	private int buildSlot2Id;
	private double buildSlot2Count;
	private long buildSlot2Ordered;
	private long colonizationDate;
	private int idArea;
	private int idOwner;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
			this.id = id;
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
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (x < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				x + "' (must be >= 0).");
		else
			this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (y < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				y + "' (must be >= 0).");
		else
			this.y = y;
	}
	
	public int getShortcut() {
		return shortcut;
	}
	
	public void setShortcut(int shortcut) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.shortcut = shortcut;
	}
	
	public boolean isAi() {
		return ai;
	}
	
	public void setAi(boolean ai) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.ai = ai;
	}
	
	public int getStarImage() {
		return starImage;
	}
	
	public void setStarImage(int starImage) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (starImage < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				starImage + "' (must be >= 0).");
		else
			this.starImage = starImage;
	}
	
	public int getAsteroidBelt() {
		return asteroidBelt;
	}
	
	public void setAsteroidBelt(int asteroidBelt) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (asteroidBelt < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				asteroidBelt + "' (must be >= 0).");
		else
			this.asteroidBelt = asteroidBelt;
	}
	
	public double getPopulation() {
		return population;
	}
	
	public void setPopulation(double population) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (population < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				population + "' (must be >= 0).");
		else
			this.population = population;
	}
	
	public int getAvailableSpace() {
		return availableSpace;
	}
	
	public void setAvailableSpace(int availableSpace) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (availableSpace < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				availableSpace + "' (must be >= 0).");
		else
			this.availableSpace = availableSpace;
	}
	
	public long getEncodedAvailableResources() {
		return encodedAvailableResources;
	}
	
	public void setEncodedAvailableResources(long encodedAvailableResources) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedAvailableResources < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedAvailableResources + "' (must be >= 0).");
		else
			this.encodedAvailableResources = encodedAvailableResources;
	}
	
	public double getResource0() {
		return resource0;
	}
	
	public void setResource0(double resource0) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource0 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource0 + "' (must be >= 0).");
		else
			this.resource0 = resource0;
	}
	
	public double getResource1() {
		return resource1;
	}
	
	public void setResource1(double resource1) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource1 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource1 + "' (must be >= 0).");
		else
			this.resource1 = resource1;
	}
	
	public double getResource2() {
		return resource2;
	}
	
	public void setResource2(double resource2) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource2 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource2 + "' (must be >= 0).");
		else
			this.resource2 = resource2;
	}
	
	public double getResource3() {
		return resource3;
	}
	
	public void setResource3(double resource3) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource3 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource3 + "' (must be >= 0).");
		else
			this.resource3 = resource3;
	}
	
	public long getLaboratory() {
		return laboratory;
	}
	
	public void setLaboratory(long laboratory) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (laboratory < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				laboratory + "' (must be >= 0).");
		else
			this.laboratory = laboratory;
	}
	
	public long getStorehouse() {
		return storehouse;
	}
	
	public void setStorehouse(long storehouse) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (storehouse < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				storehouse + "' (must be >= 0).");
		else
			this.storehouse = storehouse;
	}
	
	public long getSpaceshipYard() {
		return spaceshipYard;
	}
	
	public void setSpaceshipYard(long spaceshipYard) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (spaceshipYard < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				spaceshipYard + "' (must be >= 0).");
		else
			this.spaceshipYard = spaceshipYard;
	}
	
	public long getDefensiveDeck() {
		return defensiveDeck;
	}
	
	public void setDefensiveDeck(long defensiveDeck) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (defensiveDeck < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				defensiveDeck + "' (must be >= 0).");
		else
			this.defensiveDeck = defensiveDeck;
	}
	
	public long getExploitation0() {
		return exploitation0;
	}
	
	public void setExploitation0(long exploitation0) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (exploitation0 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				exploitation0 + "' (must be >= 0).");
		else
			this.exploitation0 = exploitation0;
	}
	
	public long getExploitation1() {
		return exploitation1;
	}
	
	public void setExploitation1(long exploitation1) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (exploitation1 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				exploitation1 + "' (must be >= 0).");
		else
			this.exploitation1 = exploitation1;
	}
	
	public long getExploitation2() {
		return exploitation2;
	}
	
	public void setExploitation2(long exploitation2) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (exploitation2 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				exploitation2 + "' (must be >= 0).");
		else
			this.exploitation2 = exploitation2;
	}
	
	public long getExploitation3() {
		return exploitation3;
	}
	
	public void setExploitation3(long exploitation3) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (exploitation3 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				exploitation3 + "' (must be >= 0).");
		else
			this.exploitation3 = exploitation3;
	}
	
	public long getExtractorCenter() {
		return extractorCenter;
	}
	
	public void setExtractorCenter(long extractorCenter) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (extractorCenter < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				extractorCenter + "' (must be >= 0).");
		else
			this.extractorCenter = extractorCenter;
	}
	
	public long getCivilianInfrastructures() {
		return civilianInfrastructures;
	}
	
	public void setCivilianInfrastructures(long civilianInfrastructures) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (civilianInfrastructures < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				civilianInfrastructures + "' (must be >= 0).");
		else
			this.civilianInfrastructures = civilianInfrastructures;
	}
	
	public long getCorporations() {
		return corporations;
	}
	
	public void setCorporations(long corporations) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (corporations < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				corporations + "' (must be >= 0).");
		else
			this.corporations = corporations;
	}
	
	public long getTradePort() {
		return tradePort;
	}
	
	public void setTradePort(long tradePort) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (tradePort < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				tradePort + "' (must be >= 0).");
		else
			this.tradePort = tradePort;
	}
	
	public long getResearchCenter() {
		return researchCenter;
	}
	
	public void setResearchCenter(long researchCenter) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (researchCenter < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				researchCenter + "' (must be >= 0).");
		else
			this.researchCenter = researchCenter;
	}
	
	public long getFactory() {
		return factory;
	}
	
	public void setFactory(long factory) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (factory < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				factory + "' (must be >= 0).");
		else
			this.factory = factory;
	}
	
	public long getRefinery() {
		return refinery;
	}
	
	public void setRefinery(long refinery) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (refinery < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				refinery + "' (must be >= 0).");
		else
			this.refinery = refinery;
	}
	
	public int getEncodedCurrentBuilding() {
		return encodedCurrentBuilding;
	}
	
	public void setEncodedCurrentBuilding(int encodedCurrentBuilding) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedCurrentBuilding < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedCurrentBuilding + "' (must be >= 0).");
		else
			this.encodedCurrentBuilding = encodedCurrentBuilding;
	}
	
	public long getCurrentBuildingEnd() {
		return currentBuildingEnd;
	}
	
	public void setCurrentBuildingEnd(long currentBuildingEnd) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (currentBuildingEnd < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				currentBuildingEnd + "' (must be >= 0).");
		else
			this.currentBuildingEnd = currentBuildingEnd;
	}
	
	public int getEncodedNextBuilding() {
		return encodedNextBuilding;
	}
	
	public void setEncodedNextBuilding(int encodedNextBuilding) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedNextBuilding < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedNextBuilding + "' (must be >= 0).");
		else
			this.encodedNextBuilding = encodedNextBuilding;
	}
	
	public long getNextBuildingEnd() {
		return nextBuildingEnd;
	}
	
	public void setNextBuildingEnd(long nextBuildingEnd) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (nextBuildingEnd < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				nextBuildingEnd + "' (must be >= 0).");
		else
			this.nextBuildingEnd = nextBuildingEnd;
	}
	
	public int getEncodedThirdBuilding() {
		return encodedThirdBuilding;
	}
	
	public void setEncodedThirdBuilding(int encodedThirdBuilding) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedThirdBuilding < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedThirdBuilding + "' (must be >= 0).");
		else
			this.encodedThirdBuilding = encodedThirdBuilding;
	}
	
	public long getThirdBuildingEnd() {
		return thirdBuildingEnd;
	}
	
	public void setThirdBuildingEnd(long thirdBuildingEnd) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (thirdBuildingEnd < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				thirdBuildingEnd + "' (must be >= 0).");
		else
			this.thirdBuildingEnd = thirdBuildingEnd;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastUpdate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastUpdate + "' (must be >= 0).");
		else
			this.lastUpdate = lastUpdate;
	}
	
	public long getLastResearchUpdate() {
		return lastResearchUpdate;
	}
	
	public void setLastResearchUpdate(long lastResearchUpdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastResearchUpdate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastResearchUpdate + "' (must be >= 0).");
		else
			this.lastResearchUpdate = lastResearchUpdate;
	}
	
	public long getLastPopulationUpdate() {
		return lastPopulationUpdate;
	}
	
	public void setLastPopulationUpdate(long lastPopulationUpdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastPopulationUpdate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastPopulationUpdate + "' (must be >= 0).");
		else
			this.lastPopulationUpdate = lastPopulationUpdate;
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
	
	public int getBuildSlot0Id() {
		return buildSlot0Id;
	}
	
	public void setBuildSlot0Id(int buildSlot0Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot0Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot0Id + "' (must be >= 0).");
		else
			this.buildSlot0Id = buildSlot0Id;
	}
	
	public double getBuildSlot0Count() {
		return buildSlot0Count;
	}
	
	public void setBuildSlot0Count(double buildSlot0Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot0Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot0Count + "' (must be >= 0).");
		else
			this.buildSlot0Count = buildSlot0Count;
	}
	
	public long getBuildSlot0Ordered() {
		return buildSlot0Ordered;
	}
	
	public void setBuildSlot0Ordered(long buildSlot0Ordered) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot0Ordered < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot0Ordered + "' (must be >= 0).");
		else
			this.buildSlot0Ordered = buildSlot0Ordered;
	}
	
	public int getBuildSlot1Id() {
		return buildSlot1Id;
	}
	
	public void setBuildSlot1Id(int buildSlot1Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot1Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot1Id + "' (must be >= 0).");
		else
			this.buildSlot1Id = buildSlot1Id;
	}
	
	public double getBuildSlot1Count() {
		return buildSlot1Count;
	}
	
	public void setBuildSlot1Count(double buildSlot1Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot1Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot1Count + "' (must be >= 0).");
		else
			this.buildSlot1Count = buildSlot1Count;
	}
	
	public long getBuildSlot1Ordered() {
		return buildSlot1Ordered;
	}
	
	public void setBuildSlot1Ordered(long buildSlot1Ordered) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot1Ordered < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot1Ordered + "' (must be >= 0).");
		else
			this.buildSlot1Ordered = buildSlot1Ordered;
	}
	
	public int getBuildSlot2Id() {
		return buildSlot2Id;
	}
	
	public void setBuildSlot2Id(int buildSlot2Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot2Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot2Id + "' (must be >= 0).");
		else
			this.buildSlot2Id = buildSlot2Id;
	}
	
	public double getBuildSlot2Count() {
		return buildSlot2Count;
	}
	
	public void setBuildSlot2Count(double buildSlot2Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot2Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot2Count + "' (must be >= 0).");
		else
			this.buildSlot2Count = buildSlot2Count;
	}
	
	public long getBuildSlot2Ordered() {
		return buildSlot2Ordered;
	}
	
	public void setBuildSlot2Ordered(long buildSlot2Ordered) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (buildSlot2Ordered < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				buildSlot2Ordered + "' (must be >= 0).");
		else
			this.buildSlot2Ordered = buildSlot2Ordered;
	}
	
	public long getColonizationDate() {
		return colonizationDate;
	}
	
	public void setColonizationDate(long colonizationDate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (colonizationDate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				colonizationDate + "' (must be >= 0).");
		else
			this.colonizationDate = colonizationDate;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idArea < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idArea + "' (must be >= 0).");
		else
			this.idArea = idArea;
	}
	
	public int getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(int idOwner) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idOwner < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idOwner + "' (must be >= 0).");
		else
			this.idOwner = idOwner;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
