/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

import java.awt.Point;
import java.util.List;

import fr.fg.server.data.base.StarSystemBase;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.BuiltShipsEvent;
import fr.fg.server.util.Config;
import fr.fg.server.util.Utilities;

public class StarSystem extends StarSystemBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TITANIUM = 0,
		CRYSTAL = 1,
		ANDIUM = 2,
		ANTI_MATTER = 3;
	
	private boolean updatedAgain = false;
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StarSystem() {
		// Nécessaire pour la construction par réflection
	}
	
	public StarSystem(String name, int x, int y, boolean ai, int starImage,
			int asteroidBelt, int availableSpace, int[] availableResources,
			int idArea, int idOwner) {
		setName(name);
		setX(x);
		setY(y);
		setShortcut(-1);
		setAi(ai);
		setStarImage(starImage);
		setAsteroidBelt(asteroidBelt);
		setAvailableSpace(availableSpace);
		setAvailableResources(availableResources);
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
		setLaboratory(0);
		setStorehouse(0);
		setSpaceshipYard(0);
		setDefensiveDeck(0);
		setExploitation0(0);
		setExploitation1(0);
		setExploitation2(0);
		setExploitation3(0);
		setExtractorCenter(0);
		setCivilianInfrastructures(0);
		setCorporations(0);
		setTradePort(0);
		setResearchCenter(0);
		setFactory(0);
		setRefinery(0);
		setEncodedCurrentBuilding(0);
		setCurrentBuildingEnd(0);
		setEncodedNextBuilding(0);
		setNextBuildingEnd(0);
		setEncodedThirdBuilding(0);
		setThirdBuildingEnd(0);
		setLastUpdate(Utilities.now());
		setLastResearchUpdate(getLastUpdate());
		setLastPopulationUpdate(getLastUpdate());
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
		setSlot5Id(0);
		setSlot5Count(0);
		setSlot6Id(0);
		setSlot6Count(0);
		setSlot7Id(0);
		setSlot7Count(0);
		setSlot8Id(0);
		setSlot8Count(0);
		setSlot9Id(0);
		setSlot9Count(0);
		setBuildSlot0Id(0);
		setBuildSlot0Count(0);
		setBuildSlot0Ordered(0);
		setBuildSlot1Id(0);
		setBuildSlot1Count(0);
		setBuildSlot1Ordered(0);
		setBuildSlot2Id(0);
		setBuildSlot2Count(0);
		setBuildSlot2Ordered(0);
		setColonizationDate(0);
		setIdArea(idArea);
		setIdOwner(idOwner);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public List<Planet> getPlanets() {
		return DataAccess.getPlanetsBySystem(getId());
	}
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public Building getCurrentBuilding() {
		return decodeBuilding(getEncodedCurrentBuilding(), getCurrentBuildingEnd());
	}
	
	public void setCurrentBuilding(Building building) {
		this.setEncodedCurrentBuilding(encodeBuilding(building));
		this.setCurrentBuildingEnd(building == null ? 0 : building.getEnd());
	}
	
	public Building getNextBuilding() {
		return decodeBuilding(getEncodedNextBuilding(), getNextBuildingEnd());
	}
	
	public void setNextBuilding(Building building) {
		this.setEncodedNextBuilding(encodeBuilding(building));
		this.setNextBuildingEnd(building == null ? 0 : building.getEnd());
	}
	
	public Building getThirdBuilding() {
		return decodeBuilding(getEncodedThirdBuilding(), getThirdBuildingEnd());
	}
	
	public void setThirdBuilding(Building building) {
		this.setEncodedThirdBuilding(encodeBuilding(building));
		this.setThirdBuildingEnd(building == null ? 0 : building.getEnd());
	}
	public double[] getResources() {
		return new double[]{
			getResource0(),
			getResource1(),
			getResource2(),
			getResource3()
		};
	}

	public double getResourcesCount() {
		double count = 0;
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			count += getResource(i);
		return count;
	}

	public double getResource(int type) {
		switch (type) {
		case 0:
			return getResource0();
		case 1:
			return getResource1();
		case 2:
			return getResource2();
		case 3:
			return getResource3();
		}
		throw new IllegalArgumentException(
				"Invalid resource index: '" + type + "'.");
	}
	
	public void setResources(double[] resources) {
		for (int i = 0; i < resources.length; i++)
			setResource(resources[i], i);
	}
	
	public void addResource(double resources, int type) {
		setResource(Math.max(0, getResource(type) + resources), type);
	}
	
	public void setResource(double resources, int type) {
		switch (type) {
		case 0:
			setResource0(resources);
			break;
		case 1:
			setResource1(resources);
			break;
		case 2:
			setResource2(resources);
			break;
		case 3:
			setResource3(resources);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid resource index: '" + type + "'.");
		}
	}
	
	public long getMaxResources() {
		return (long) getProduction(Building.STOREHOUSE);
	}
	
	public int[] getAvailableResources() {
		return decodeAvailableResources(getEncodedAvailableResources());
	}
	
	public void setAvailableResources(int[] availableResources) {
		setEncodedAvailableResources(
				encodeAvailableResources(availableResources));
	}
	
	public int getBuildingLand() {
		return GameConstants.SYSTEM_BUILDING_LAND +
			Advancement.getAdvancementLevel(getIdOwner(),
				Advancement.TYPE_BUILDING_LAND);
	}
	
    public int getBuiltLand() {
    	int built = 0;
    	
    	for (int i = 0; i < Building.BUILDING_COUNT; i++) {
        	int[] buildings = getBuildings(i);
    		for (int j = 0; j < buildings.length; j++)
    			built += buildings[j];
    	}
    	
        return built;
    }
    
    // Renvoie true si le système masque la flotte fleet au joueur player
    public boolean isHidingFleet(Fleet fleet, Player player) {
    	return getIdOwner() == fleet.getIdOwner() && // NB : vérifie que l'id est != 0
			contains(fleet.getCurrentX(), fleet.getCurrentY()) &&
			player.getTreatyWithPlayer(getIdOwner()).equals(Treaty.NEUTRAL);
    }
    
	public int[] getBuildings(int type) {
		switch (type) {
		case Building.LABORATORY:
			return decodeBuildings(getLaboratory());
		case Building.STOREHOUSE:
			return decodeBuildings(getStorehouse());
		case Building.SPACESHIP_YARD:
			return decodeBuildings(getSpaceshipYard());
		case Building.DEFENSIVE_DECK:
			return decodeBuildings(getDefensiveDeck());
		case Building.EXPLOITATION0:
			return decodeBuildings(getExploitation0());
		case Building.EXPLOITATION1:
			return decodeBuildings(getExploitation1());
		case Building.EXPLOITATION2:
			return decodeBuildings(getExploitation2());
		case Building.EXPLOITATION3:
			return decodeBuildings(getExploitation3());
		case Building.RESEARCH_CENTER:
			return decodeBuildings(getResearchCenter());
		case Building.FACTORY:
			return decodeBuildings(getFactory());
		case Building.REFINERY:
			return decodeBuildings(getRefinery());
		case Building.EXTRACTOR_CENTER:
			return decodeBuildings(getExtractorCenter());
		case Building.CIVILIAN_INFRASTRUCTURES:
			return decodeBuildings(getCivilianInfrastructures());
		case Building.CORPORATIONS:
			return decodeBuildings(getCorporations());
		case Building.TRADE_PORT:
			return decodeBuildings(getTradePort());
		default:
			throw new IllegalArgumentException(
					"Invalid building type: '" + type + "'.");
		}
	}
	
	public void setBuildings(int[] buildings, int type) {
		switch (type) {
		case Building.LABORATORY:
			setLaboratory(encodeBuildings(buildings));
			break;
		case Building.STOREHOUSE:
			setStorehouse(encodeBuildings(buildings));
			break;
		case Building.SPACESHIP_YARD:
			setSpaceshipYard(encodeBuildings(buildings));
			break;
		case Building.DEFENSIVE_DECK:
			setDefensiveDeck(encodeBuildings(buildings));
			break;
		case Building.EXPLOITATION0:
			setExploitation0(encodeBuildings(buildings));
			break;
		case Building.EXPLOITATION1:
			setExploitation1(encodeBuildings(buildings));
			break;
		case Building.EXPLOITATION2:
			setExploitation2(encodeBuildings(buildings));
			break;
		case Building.EXPLOITATION3:
			setExploitation3(encodeBuildings(buildings));
			break;
		case Building.EXTRACTOR_CENTER:
			setExtractorCenter(encodeBuildings(buildings));
			break;
		case Building.CIVILIAN_INFRASTRUCTURES:
			setCivilianInfrastructures(encodeBuildings(buildings));
			break;
		case Building.CORPORATIONS:
			setCorporations(encodeBuildings(buildings));
			break;
		case Building.TRADE_PORT:
			setTradePort(encodeBuildings(buildings));
			break;
		case Building.RESEARCH_CENTER:
			setResearchCenter(encodeBuildings(buildings));
			break;
		case Building.FACTORY:
			setFactory(encodeBuildings(buildings));
			break;
		case Building.REFINERY:
			setRefinery(encodeBuildings(buildings));
			break;
		}
	}
	
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
		case 5:
			return new Slot(getSlot5Id(), getSlot5Count(), true);
		case 6:
			return new Slot(getSlot6Id(), getSlot6Count(), true);
		case 7:
			return new Slot(getSlot7Id(), getSlot7Count(), true);
		case 8:
			return new Slot(getSlot8Id(), getSlot8Count(), true);
		case 9:
			return new Slot(getSlot9Id(), getSlot9Count(), true);
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
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
		case 5:
			setSlot5Id(slot.getId());
			setSlot5Count((long) slot.getCount());
			break;
		case 6:
			setSlot6Id(slot.getId());
			setSlot6Count((long) slot.getCount());
			break;
		case 7:
			setSlot7Id(slot.getId());
			setSlot7Count((long) slot.getCount());
			break;
		case 8:
			setSlot8Id(slot.getId());
			setSlot8Count((long) slot.getCount());
			break;
		case 9:
			setSlot9Id(slot.getId());
			setSlot9Count((long) slot.getCount());
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
	
    public Player getOwner() {
    	if (getIdOwner() == 0)
    		return null;
    	return DataAccess.getPlayerById(getIdOwner());
    }
    
    // Renvoie true si le point (x, y) est à l'intérieur des frontières du
    // système
    // NB : Penser à vérifier que les objets sont dans le meme secteur !
    public boolean contains(int x, int y) {
    	int dx = getX() - x;
    	int dy = getY() - y;
    	
    	return dx * dx + dy * dy <= GameConstants.SYSTEM_RADIUS *
    		GameConstants.SYSTEM_RADIUS;
    }
    
    // Renvoie true si le rectangle à la position (x, y) et de dimensions
    // (width, height) est partiellement ou totalement à l'intérieur des
    // frontières du système
    public boolean contains(int x, int y, int width, int height) {
    	if (width == 1 && height == 1)
    		return contains(x, y);
    	
    	// Premier test pour éliminer le système s'il est trop éloigné
		int dx = x - getX();
		int dy = y - getY();
		int radius = GameConstants.SYSTEM_RADIUS + width + height;
		
		if (dx * dx + dy * dy > radius * radius)
			return false;
    	
    	for (int i = x; i < x + width; i++)
        	for (int j = y; j < y + height; j++)
        		if (contains(i, j))
        			return true;
    	return false;
    }
    
    public Point getFreeTile() {
    	boolean found = false;
    	int x = 0, y = 0;
    	
    	Area area = getArea();
    	
    	for (int step = 0; step < GameConstants.SYSTEM_RADIUS && !found; step++)
    		for (int i = -step; i <= step && !found; i++) {
    			if (area.isFreeTile(getX() + i, getY() - step, Area.NO_FLEETS, null)
    					&& contains(getX() + i, getY() - step)) {
    				x = getX() + i;
    				y = getY() - step;
    				found = true;
    			} else if (area.isFreeTile(getX() - step, getY() + i, Area.NO_FLEETS, null)
    					&& contains(getX() - step, getY() + i)) {
    				x = getX() - step;
    				y = getY() + i;
    				found = true;
    			} else if (area.isFreeTile(getX() + i, getY() + step, Area.NO_FLEETS, null)
    					&& contains(getX() + i, getY() + step)) {
    				x = getX() + i;
    				y = getY() + step;
    				found = true;
    			} else if (area.isFreeTile(getX() + step, getY() + i, Area.NO_FLEETS, null)
    					&& contains(getX() + step, getY() + i)) {
    				x = getX() + step;
    				y = getY() + i;
    				found = true;
    			}
    		}
    	
    	if (!found)
    		return null;
    	
    	return new Point(x, y);
    }
    
    public double getProduction(int type) {
    	return Building.getProduction(type, getBuildings(type));
    }
    
	// Note : le système doit etre editable
    public static StarSystem updateSystem(StarSystem system) {

    	long now = Utilities.now();
    	long lastUpdate = system.getLastUpdate();
    	
    	// Temps écoulé depuis la dernière mise à jour
    	long length = now - lastUpdate;
    	long extraCredits;
    	
    	boolean updateAgain = false; //Pour déterminer si on doit appeler updateSystem une 2e fois
    	
    	if (length <= 0 && system.isUpdatedAgain() == false)
    		return system;
    	// Vérifie que la planète appartient à un joueur
    	Player owner = system.getOwner();
    	if (owner == null)
    		return system;
    	
    	synchronized (system.getLock()) {
    		system = DataAccess.getEditable(system);
	    	
	    	// Population et crédits générés sur le système
	    	double population = system.getPopulation(), maxPopulation;
	    	double growth, growthCoef, credits = 0;
	    	long frame;
	    	
	    	// Calcule la production des bâtiments construits
	    	double[] production = new double[Building.BUILDING_COUNT];
	    	for (int i = 0; i < production.length; i++)
	    		production[i] = system.getProduction(i);
	    	
	    	// Teste si un batiment est en cours de construction
	    	Building currentBuilding = system.getCurrentBuilding();
	    	
	    	if (currentBuilding != null) {
	    		// Teste si le batiment en cours de construction est terminé
	    		if (currentBuilding.getEnd() < now) {
	    			// Met à jour la population du système, jusqu'à l'heure de la
	    			// fin de la construction du bâtiment
	    			frame = currentBuilding.getEnd() -
	    				system.getLastPopulationUpdate();
	    			maxPopulation = system.getProduction(
	    					Building.CIVILIAN_INFRASTRUCTURES);
	    			growth = GameConstants.POPULATION_GROWTH * frame *
	    					maxPopulation;
	    			
	    			growthCoef = .5;
	    			if (population + growth > maxPopulation) {
	    				growthCoef = .5 * (maxPopulation - population) / growth +
	    					(1 - (maxPopulation - population) / growth);
	    				growth = maxPopulation - population;
	    			}
	    			
	    			if (growth < 0)
	    				growth = 0;
	    			if (growthCoef < 0)
	    				growthCoef = 0;
	    			
	    			// Calcule les crédits gagnés avec les corporations
	    			credits += (population + growth * growthCoef) * frame *
	    				system.getProduction(Building.CORPORATIONS) *
	    				GameConstants.EXPLOITATION_RATE;
	    			
	    			population += growth;
	    			
	    			// Ajoute le batiment aux constructions du système
	    			int[] buildings = system.getBuildings(currentBuilding.getType());
	    			
	    			if (currentBuilding.getLevel() > 0)  // Amélioration d'un batiment ?
	    				buildings[currentBuilding.getLevel() - 1]--;
	    			buildings[currentBuilding.getLevel()]++;
	    			system.setBuildings(buildings, currentBuilding.getType());
	    			
	    			// Teste si un batiment est en attente
	    			Building nextBuilding = system.getNextBuilding();
					if (nextBuilding != null) {
	    				// Teste si le batiment en attente est terminé
	    				if (nextBuilding.getEnd() < now) {
	    					// REMINDER ici le premier batiment vient de se finir, et le 2nd aussi
	    					double tmpProduction =
	    						system.getProduction(currentBuilding.getType());
	    					
	    	    			// Met à jour la population du système, jusqu'à l'heure
	    					// de la fin de la construction en attente
	    	    			frame = nextBuilding.getEnd() -
	    						currentBuilding.getEnd();
	    	    			maxPopulation = system.getProduction(
	    	    				Building.CIVILIAN_INFRASTRUCTURES);
	    	    			growth = GameConstants.POPULATION_GROWTH * frame *
	    	    				maxPopulation;
	    	    			
	    	    			growthCoef = .5;
	    	    			if (population + growth > maxPopulation) {
	    	    				growthCoef = .5 * (maxPopulation - population) / growth +
	    	    					(1 - (maxPopulation - population) / growth);
	    	    				growth = maxPopulation - population;
	    	    			}
	    	    			
	    	    			if (growth < 0)
	    	    				growth = 0;
	    	    			if (growthCoef < 0)
	    	    				growthCoef = 0;
	    	    			
	    	    			// Calcule les crédits gagnés avec les corporations
	    	    			credits += (population + growth * growthCoef) * frame *
	    	    				system.getProduction(Building.CORPORATIONS) *
	    	    				GameConstants.EXPLOITATION_RATE;
	    	    			
	    	    			population += growth;
	    	    			
	    	    			if (population > maxPopulation)
	    	    				population = maxPopulation;
	    	    			
	    					// Ajoute le batiment aux constructions du système
	    					buildings = system.getBuildings(nextBuilding.getType());
	    					
	    					//FIXME
	    					if (nextBuilding.getLevel() > 0) // Amélioration d'un batiment ?
	    						buildings[nextBuilding.getLevel() - 1]--;
	    					buildings[nextBuilding.getLevel()]++;
	    					system.setBuildings(buildings, nextBuilding.getType());
	    					
	    					// Met à jour la production
	    					if (currentBuilding.getType() ==
	    							nextBuilding.getType()) {
	    						// % du temps passé à produire avant que le
	    						// batiment en cours de construction ne soit fini
	    						double coef1 = (currentBuilding.getEnd() -
	    								lastUpdate) / (double) length;
	    						
	    						// % du temps passé à produire avant que le
	    						// batiment en attente ne soit fini et depuis que
	    						// le batiment en cours de construction est fini
	    						double coef2 = (nextBuilding.getEnd() -
	    								currentBuilding.getEnd ()) / (double) length;
	    						
	    						production[currentBuilding.getType()] =
	    							coef1 * production[currentBuilding.getType()] +
	    							coef2 * tmpProduction + (1 - coef1 - coef2) *
	    							system.getProduction(currentBuilding.getType());
	    					} else {
	    						// % du temps passé à produire avant que le
	    						// batiment en cours de construction ne soit fini
	    						double coef1 = (currentBuilding.getEnd() -
	    								lastUpdate) / (double) length;
	    						
	    						// % du temps passé à produire avant que le
	    						// batiment en attente ne soit fini
	    						double coef2 = (nextBuilding.getEnd() -
	    								lastUpdate) / (double) length;
	    						
	    						production[currentBuilding.getType()] =
	    							coef1 * production[currentBuilding.getType()] +
	    							(1 - coef1) * tmpProduction ;
	    						production[nextBuilding.getType()] =
	    							coef2 * production[nextBuilding.getType()] +
	    							(1 - coef2) * system.getProduction(nextBuilding.getType());
	    					}
	    					
	    	    			// Calcule la croissance de la population du système,
	    					// jusqu'à l'heure de la mise à jour
	        				frame = now - nextBuilding.getEnd();
	            			maxPopulation = system.getProduction(
	            					Building.CIVILIAN_INFRASTRUCTURES);
	    	    			growth = GameConstants.POPULATION_GROWTH * frame *
	    	    					maxPopulation;
	    	    			
	    	    			if(system.getThirdBuilding()!=null) {
	    						if(system.getOwner().isPremium()) {
	    							//FIXME
	    							system.setCurrentBuilding(system.getThirdBuilding());
	    							system.setThirdBuilding(null); //FIXME DONE - 
	    							//building qui apparaissent lors de la construction + length ==0 (plus haut)
	    							updateAgain = true;
	    						}
	    						else {
	    							system.setCurrentBuilding(null);
	    						}
	    						system.setThirdBuilding(null);
	    						system.setNextBuilding(null);
	    	    			}
	    	    			else
	    	    			{
	    	    				system.setCurrentBuilding(null);
		    					system.setNextBuilding(null);
	    	    			}
	    				} else {
	    					// REMINDER ici le premier batiment vient de se finir, et le 2nd est en production
							// % du temps passé à produire avant que le
							// batiment en attente ne soit fini
	    					double coef = (currentBuilding.getEnd() -
	    							lastUpdate) / (double) length;
	    					
	    					// Met à jour la production
	    					production[currentBuilding.getType()] =
	    						coef * production[currentBuilding.getType()] +
	    						(1 - coef) * system.getProduction(currentBuilding.getType());
	    					
	    	    			// Calcule la croissance de la population du système,
	    					// jusqu'à l'heure de la mise à jour
	        				frame = now - currentBuilding.getEnd();
	            			maxPopulation = system.getProduction(
	            					Building.CIVILIAN_INFRASTRUCTURES);
	    					growth = GameConstants.POPULATION_GROWTH * frame *
	    							maxPopulation;
	            			
	    					// Le batiment en attente devient le batiment en cours
	    					// de construction
	    					system.setCurrentBuilding(nextBuilding);
	    					if(system.getThirdBuilding()!=null) {
	    						if(system.getOwner().isPremium()) {
		    						system.setNextBuilding(system.getThirdBuilding());
		    						updateAgain = true;
	    						}
	    						else {
	    							system.setNextBuilding(null);
	    						}
	    						system.setThirdBuilding(null);
	    					}
	    					else {
	    						system.setNextBuilding(null);
	    					}
	    				}
	    			} else {
	    				// REMINDER ici le premier batiment vient de se finir, et il n'y a rien après
						// % du temps passé à produire avant que le
						// batiment en attente ne soit fini
	    				double coef = (currentBuilding.getEnd() -
	    						lastUpdate) / (double) length;
	    				
	    				// Met à jour la production
	    				production[currentBuilding.getType()] =
	    					coef * production[currentBuilding.getType()] +
	    					(1 - coef) * system.getProduction(currentBuilding.getType());
	    				
		    			// Calcule la croissance de la population du système,
						// jusqu'à l'heure de la mise à jour
	    				frame = now - currentBuilding.getEnd();
	        			maxPopulation = system.getProduction(
	        					Building.CIVILIAN_INFRASTRUCTURES);
	        			growth = GameConstants.POPULATION_GROWTH * frame *
	        					maxPopulation;
	        			
	        			system.setCurrentBuilding(null);
	    			}
	    		} else {
	    			// REMINDER ici le premier batiment est en cours de prod
	    			// Calcule la croissance de la population du système, jusqu'à
	    			// l'heure de la mise à jour
	    			frame = now - system.getLastPopulationUpdate();
	    			maxPopulation = system.getProduction(
	    					Building.CIVILIAN_INFRASTRUCTURES);
	    			growth = GameConstants.POPULATION_GROWTH * frame *
	    					maxPopulation;
	    		}
	    	} else {
	    		// REMINDER Ici aucun bâtiment en cours de prod
				// Calcule la croissance de la population du système, jusqu'à
	    		// l'heure de la mise à jour
				frame = now - system.getLastPopulationUpdate();
				maxPopulation = system.getProduction(
						Building.CIVILIAN_INFRASTRUCTURES);
				growth = GameConstants.POPULATION_GROWTH * frame * maxPopulation;
	    	}
	    	
	    	// Limite la population en fonction du nombre d'infrastructures civiles
	    	growth *= 1 + Math.pow(1.06, Advancement.getAdvancementLevel(
				system.getIdOwner(), Advancement.TYPE_POPULATION_GROWTH)) *
				system.getPopulationGrowthModifier();
			growthCoef = .5;
			if (population + growth > maxPopulation) {
				growthCoef = .5 * (maxPopulation - population) / growth +
					(1 - (maxPopulation - population) / growth);
				growth = maxPopulation - population;
			}
			
			if (growth < 0)
				growth = 0;
			if (growthCoef < 0)
				growthCoef = 0;
			
			// Affecte la recherche, les exploitations et les crédits
			double productionModifier = system.getProductionModifier();
			
			// Calcule les crédits gagnés avec les corporations
			credits += (population + growth * growthCoef) * frame *
				system.getProduction(Building.CORPORATIONS) *
				GameConstants.EXPLOITATION_RATE * productionModifier;
			population += growth;
			
			if (population > maxPopulation)
				population = maxPopulation;
			
			extraCredits = (long) credits;
			
			system.setPopulation(population);
			
	    	// Calcule la quantité de ressources gagnées depuis la dernière mise
	    	// à jour
	    	double[] earnedResources = new double[GameConstants.RESOURCES_COUNT];
	    	for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
	    		earnedResources[i] = length * GameConstants.EXPLOITATION_RATE *
	    			production[Building.EXPLOITATION0 + i] *
	    			production[Building.REFINERY] * productionModifier;
	    	
	    	// Ajoute les ressources au système, dans la limite de la capacité des dépots
	    	double totalEarned = 0;
	    	double totalResources = 0;
	    	double[] currentResources = system.getResources();
	    	for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
	    		totalEarned += earnedResources[i];
	    		totalResources += currentResources[i];
	    	}
	    	
	    	double maxResources = production[Building.STOREHOUSE];
	    	
	    	if (totalEarned > 0) {
		    	if (totalEarned + totalResources <= maxResources) {
		    		// Ajoute les ressources produites au stock
		    		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
		    			currentResources[i] += earnedResources[i];
		    	} else if (totalResources <= maxResources) {
		    		// Ajoute les ressources produites jusqu'au maximum de la capacité des dépots
		    		double coef = (maxResources - totalResources) / totalEarned;
		    		
		    		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
		    			// Note: ceil pour éviter que la somme des ressources soient
		    			// inférieure au stock max à cause des arrondis
		    			currentResources[i] += Math.ceil(earnedResources[i] * coef);
		    	}
	    	}
	    	
	    	system.setResources(currentResources);
	    	
	    	// Points de recherché générés par le système
	    	double researchPoints = production[Building.LABORATORY] * 
	    		production[Building.RESEARCH_CENTER] *
	    		(now - system.getLastResearchUpdate()) * productionModifier *
	    		GameConstants.RESEARCH_RATE;
	    	if (researchPoints > 0)
	    		owner.addResearchPoints(researchPoints);
	    	
	    	// Construction de vaisseaux
	    	double shipProduction = production[Building.SPACESHIP_YARD] *
	    		system.getShipProductionModifier();
	    	long shipProductionTime = length;
	    	
	    	if (shipProduction > 0) {
		    	for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++) {
		    	    Slot buildSlot = system.getBuildSlot(i);
		    	    int shipId = buildSlot.getId();
		    	    
		    	    if (shipId != 0) {
		    	        // Cherche un emplacement sur le système qui peut recevoir
		    	    	// les vaisseaux en cours de production. S'il n'y a pas
		    	    	// d'emplacement disponible la production est bloquée
		    	        int slotIndex = -1;
		    	        for (int j = 0; j < GameConstants.SYSTEM_SLOT_COUNT; j++) {
		    	            if (system.getSlot(j).getId() == shipId) {
		        	        	// Emplacement contenant des vaisseaux du même type
		    	            	// que ceux qui sont en production
		    	                slotIndex = j;
		    	                break;
		    	            }
		    	        }
		    	        
		    	        if (slotIndex == -1) {
		    	            for (int j = 0; j < GameConstants.SYSTEM_SLOT_COUNT; j++) {
		    	                if (system.getSlot(j).getId() == 0) {
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
		    				system.setBuildSlot(new Slot(), i, true);
		    			} else {
		    				shipProductionTime = 0;
		    				newShips = (long) (Math.ceil(buildSlot.getCount()) -
		    					Math.ceil(buildSlot.getCount() - ships));
		    				buildSlot.setCount(buildSlot.getCount() - ships);
		    				system.setBuildSlot(buildSlot, i, false);
		    			}
		    			
		    			if (newShips > 0) {
		    				Slot oldSlot = system.getSlot(slotIndex);
		    				Slot slot = new Slot(shipId,
		    					oldSlot.getId() == shipId ?
		    					oldSlot.getCount() + newShips : newShips, true);
		    				system.setSlot(slot, slotIndex);
		    				
							GameEventsDispatcher.fireGameNotification(new BuiltShipsEvent(
								system, shipId, newShips));
		    			}
		    		}
		    		
		    		if (shipProductionTime <= 0)
		    			break;
		    	}
	    	}
	    	
	    	system.setLastUpdate(now);
	    	system.setLastResearchUpdate(now);
	    	system.setLastPopulationUpdate(now);
	    	system.save();
    	}
    	
		// Ajoute les crédits au joueur
		synchronized (owner.getLock()) {
			owner = DataAccess.getEditable(owner);
			owner.addCredits(extraCredits);
			owner.save();
		}
		
		if (updateAgain) system.setUpdatedAgain(true); else system.setUpdatedAgain(false);
    	return updateAgain? updateSystem(system): system;
    }
    
    
    

  
    public double getProductionModifier() {
    	Player owner = getOwner();
    	
    	if (owner.getIdAlly() != 0) {
    		Sector sector = getArea().getSector();
    		
    		return Math.min(1.5, 1 +
    			sector.getAllyInfluence(owner.getIdAlly()) *
    			sector.getStrategicValue() / 100);
    	}
    	
    	return 1;
    }
    
    public double getShipProductionModifier() {
    	return getIdOwner() == 0 ? 1 : Product.getProductEffect(
    		Product.PRODUCT_IRIDIUM,
    		getOwner().getProductsCount(Product.PRODUCT_IRIDIUM));
    }

    public double getPopulationGrowthModifier() {
    	return getIdOwner() == 0 ? 1 : Product.getProductEffect(
    		Product.PRODUCT_NECTAR,
    		getOwner().getProductsCount(Product.PRODUCT_NECTAR));
    }
    
    public void setOwner(int idOwner) {
    	long now = Utilities.now();
    	
		setIdOwner(idOwner);
		setColonizationDate(now);
    }
    
    // REMIND jgottero a améliorer (+ de ressources pour un joueur arrivé plus tard...)
    // Configuration par défaut d'un système affecté à un nouveau joueur
    public void setStartSettings() {
    	long now = Utilities.now();
    	
    	long timeElapsed = now - Config.getOpeningDate();
    	long minutesElapsed = timeElapsed / 60;
    	
    	
    	setAvailableSpace(4);
    	setAvailableResources(new int[]{3, 3, 3, 0});
    	// Un joueur a une prod de +25/min a départ, on prend donc 12.
    	if(minutesElapsed>0){ 
    	setResource0(20000+12*minutesElapsed);
    	setResource1(20000+12*minutesElapsed);
    	setResource2(20000+12*minutesElapsed);
    	}
    	else // Gestion des serveurs non ouvert
    	{
        	setResource0(20000);
        	setResource1(20000);
        	setResource2(20000);
    	}
    	
    	if(getResource0()+getResource1()+getResource2()<1000000)
    		setBuildings(new int[]{1, 0, 1, 0, 0}, Building.STOREHOUSE);
    	else if(getResource0()+getResource1()+getResource2()<2000000)
    		setBuildings(new int[]{0, 0, 2, 0, 0}, Building.STOREHOUSE);
    	else if(getResource0()+getResource1()+getResource2()<11000000)
    		setBuildings(new int[]{0, 0, 1, 1, 0}, Building.STOREHOUSE);
    	else if(getResource0()+getResource1()+getResource2()<101000000)
    		setBuildings(new int[]{0, 0, 1, 0, 1}, Building.STOREHOUSE);
    	else
    		setBuildings(new int[]{0, 0, 0, 1, 1}, Building.STOREHOUSE);
    	
    	setBuildings(new int[]{0, 0, 1, 0, 0}, Building.EXPLOITATION0);
    	setBuildings(new int[]{0, 0, 1, 0, 0}, Building.EXPLOITATION1);
    	setBuildings(new int[]{0, 0, 1, 0, 0}, Building.EXPLOITATION2);
    	setBuildings(new int[]{0, 0, 1, 0, 0}, Building.CIVILIAN_INFRASTRUCTURES);
    	setBuildings(new int[]{0, 1, 0, 0, 0}, Building.CORPORATIONS);
    	
    	setBuildings(new int[]{0, 1, 0, 0, 0}, Building.LABORATORY);
    	setBuildings(new int[]{0, 1, 0, 0, 0}, Building.SPACESHIP_YARD);
		setLastUpdate(now);
		setLastPopulationUpdate(now);
		setLastResearchUpdate(now);
		setPopulation(5);
    }
    
    // REMIND jgottero a regrouper avec setOwner ?
    // Configuration par défaut d'un système qui vient d'être colonisé
    public void setColonizationSettings() {
    	setResource0(1000);
    	setResource1(500);
    	setBuildings(new int[]{1, 0, 0, 0, 0}, Building.EXPLOITATION0);
    }
    
    // Configuration par défaut d'un système qui vient d'être capturé
    public void setCaptureSettings() {
		setResource(1000, 0);
		setResource(500, 1);
		for (int i = 2; i < GameConstants.RESOURCES_COUNT; i++)
			setResource(0, i);
		for (int i = 0; i < GameConstants.SYSTEM_SLOT_COUNT; i++)
			setSlot(new Slot(), i);
		for (int i = 0; i < GameConstants.SHIPS_QUEUE_LENGTH; i++)
			setBuildSlot(new Slot(), i, true);
		setCurrentBuilding(null);
		setNextBuilding(null);
    }
    
    public boolean isUpdatedAgain() {
    	return updatedAgain;
    }
    
    public void setUpdatedAgain(boolean updatedAgain) {
    	this.updatedAgain = updatedAgain;
    }
    
    public void resetSettings() {
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
		setLaboratory(0);
		setStorehouse(0);
		setSpaceshipYard(0);
		setDefensiveDeck(0);
		setExploitation0(0);
		setExploitation1(0);
		setExploitation2(0);
		setExploitation3(0);
		setExtractorCenter(0);
		setCivilianInfrastructures(0);
		setCorporations(0);
		setTradePort(0);
		setResearchCenter(0);
		setFactory(0);
		setRefinery(0);
		setEncodedCurrentBuilding(0);
		setCurrentBuildingEnd(0);
		setEncodedNextBuilding(0);
		setNextBuildingEnd(0);
		setEncodedThirdBuilding(0);
		setThirdBuildingEnd(0);
		setLastUpdate(Utilities.now());
		setLastResearchUpdate(getLastUpdate());
		setLastPopulationUpdate(getLastUpdate());
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
		setSlot5Id(0);
		setSlot5Count(0);
		setSlot6Id(0);
		setSlot6Count(0);
		setSlot7Id(0);
		setSlot7Count(0);
		setSlot8Id(0);
		setSlot8Count(0);
		setSlot9Id(0);
		setSlot9Count(0);
		setBuildSlot0Id(0);
		setBuildSlot0Count(0);
		setBuildSlot1Id(0);
		setBuildSlot1Count(0);
		setBuildSlot2Id(0);
		setBuildSlot2Count(0);
		setColonizationDate(0);
		setIdOwner(0);
    }
    
	// ------------------------------------------------- METHODES PRIVEES -- //
    
	/**
	 * Décode le champ de la table dénombrant les bâtiments d'un type sur un
	 * système. Le résultat est renvoyé sous la forme d'un tableau, dont la 1e
	 * case représente le nombre de bâtiments de niveau 1, la 2e le nombre de
	 * batiments niveau 2, jusqu'au niveau 5.
	 *
	 * @param data Le champ du résultat d'une requête SQL représentant le
	 * nombre de bâtiments d'un type donné construits sur un système.
	 *
	 * @return Le nombre de bâtiments par niveau dans un tableau de 5 cases,
	 * chaque case du tableau représentant un niveau.
	 */
	private int[] decodeBuildings(long data) {
		return new int[]{
			(int) (data &       0x3f)      ,
			(int) (data &      0xfc0) >>  6,
			(int) (data &    0x3f000) >> 12,
			(int) (data &   0xfc0000) >> 18,
			(int) (data & 0x3f000000) >> 24};
	}
	

	/**
	 * Encode un tableau représentant le nombre de bâtiments d'un type donné
	 * par niveau. Cette méthode permet de compresser un tableau en un entier
	 * afin d'économiser de la place dans la base de données.
	 *
	 * @param buildings Un tableau de 5 cases, dont chaque case représentente
	 * le nombre de bâtiments d'un niveau donné. La 1e case compte les
	 * bâtiments de niveau 1, la seconde les bâtiments de niveau 2, etc.
	 * jusqu'au niveau 5. Il doit y avoir 63 bâtiments ou moins pour un niveau
	 * donné.
	 *
	 * @return Un entier codant le nombre de bâtiments. Cet entier peut ensuite
	 * être enregistré dans la base de données.
	 */
	private long encodeBuildings(int[] buildings) {
		return  ((		 buildings[0])      ) |
				(((long) buildings[1]) <<  6) |
				(((long) buildings[2]) << 12) |
				(((long) buildings[3]) << 18) |
				(((long) buildings[4]) << 24);
	}
	
	/**
	 * Décode le champ de la table dénombrant les ressources disponibles sur un
	 * système. Les résultat est renvoyé sous la forme d'un tableau, dont la 1e
	 * case représente la quantité disponible de la 1e ressource, la 2e case la
	 * quantité de la seconde ressource, jusqu'à la 4e ressource.
	 * 
	 * @param resources Le champ du résultat d'une requête SQL représentant
	 * les ressources disponibles sur un système.
	 *
	 * @return La quantité de ressources disponibles pour chaque type de
	 * ressources.
	 */
	private int[] decodeAvailableResources(long resources) {
		int[] availableResources = new int[GameConstants.RESOURCES_COUNT];
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			availableResources[i] = (int) ((resources & (7 << (3 * i))) >> (3 * i));
		return availableResources;
	}
	
	private long encodeAvailableResources(int[] resources) {
		long availableResources = 0;
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			availableResources |= resources[i] << (3 * i);
		return availableResources;
	}
	
	private Building decodeBuilding(int data, long end) {
		if (data == 0)
			return null;
		return new Building((data & 0x1f) - 1, (data & 0xe0) >> 5, end);
	}
	
	private int encodeBuilding(Building building) {
		if (building == null)
			return 0;
		return (building.getType() + 1) | (building.getLevel() << 5);
	}
}
