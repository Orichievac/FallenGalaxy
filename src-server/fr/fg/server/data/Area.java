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

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.fg.server.data.base.AreaBase;
import fr.fg.server.util.Utilities;

public class Area extends AreaBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		AREA_START = 1,
		AREA_MINING_0_5 = 2,
		AREA_PVP_0_10 = 3,
		AREA_COLONY_10_20 = 4,
		AREA_MINING_10_15 = 5,
		AREA_BANK_0_10 = 6,
		AREA_BANK_10_20 = 7,
		AREA_COLONY_20_30 = 8,
		AREA_MINING_20_25 = 9,
		AREA_BANK_20_30 = 10,
		AREA_COLONY_30_40 = 11,
		AREA_MINING_30_35 = 12,
		AREA_BANK_30_40 = 13,
		AREA_COLONY_40_50 = 14,
		AREA_MINING_40_45 = 15,
		AREA_BANK_40_50 = 16,
		AREA_BATTLE_0_10 = 17,
		AREA_BATTLE_10_20 = 18,
		AREA_BATTLE_20_30 = 19,
		AREA_BATTLE_30_40 = 20,
		AREA_BATTLE_40_50 = 21,
		AREA_MINING_5_10 = 22,
		AREA_MINING_15_20 = 23,
		AREA_MINING_25_30 = 24,
		AREA_MINING_35_40 = 25,
		AREA_MINING_45_50 = 26,
		AREA_MINING_50_55 = 27,
	    AREA_MINING_55_60 = 28,
	    AREA_BANK_50_60 = 29,
	    AREA_BATTLE_50_60 = 30,
	    AREA_COLONY_50_60 = 31;
	
	
	public final static byte
		VISIBILITY_NONE = 0,
		VISIBILITY_VISITED = 1,
		VISIBILITY_ALLY = 2,
		VISIBILITY_PLAYER = 3,
		VISIBILITY_UNKNOWN = 4;
	
	public final static int
		NO_PRODUCT = 0,
		PRODUCT_IRIDIUM = 1,
		PRODUCT_NECTAR = 2,
		PRODUCT_HEPTANIUM = 3,
		PRODUCT_SELENIUM = 4,
		PRODUCT_SULFARIDE = 5,
		PRODUCT_ANTILIUM = 6,
		PRODUCT_UNREACHABLE = 999;
	
	public final static String[] VISIBILITY_LABELS = {
		"none", "visited", "ally", "player", "unknown"
	};
	
	public final static int
		NO_FLEETS = 1 << 0,
		NO_SYSTEMS = 1 << 1,
		NO_OBJECTS = 1 << 2,
		NO_OCCUPIED_SYSTEM = 1 << 3,
		NO_NEUTRAL_SYSTEM = 1 << 4,
		NO_STRUCTURES = 1 << 5,
		NO_BLACKHOLES_IN_RANGE = 1 << 6,
		NO_GATES_IN_RANGE = 1 << 7,
		EXCEPT_PASSABLE_OBJECTS = 1 << 8,
		EXCEPT_VALID_HYPERJUMP_OUTPUT_OBJECTS = 1 << 9,
		EXCEPT_DOODADS = 1 << 10,
		EXCEPT_BLACKHOLES = 1 << 11;
	
	public final static int
		CHECK_HYPERJUMP_OUTPUT = NO_FLEETS | NO_SYSTEMS | NO_OBJECTS |
			EXCEPT_VALID_HYPERJUMP_OUTPUT_OBJECTS,
		CHECK_OBJECT_SPAWN = NO_SYSTEMS | NO_OBJECTS | NO_STRUCTURES,
		CHECK_FLEET_MOVEMENT = NO_FLEETS | NO_NEUTRAL_SYSTEM |
			NO_OBJECTS | EXCEPT_PASSABLE_OBJECTS;
	
	public final static int
		AREA_GENERAL_COLONY = 0,
		AREA_GENERAL_MINING = 1,
		AREA_GENERAL_BANK = 2,
		AREA_GENERAL_BATTLE = 3,
		AREA_GENERAL_MINING_X5 = 4;


		
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private List<Integer> neighboursCache;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Area() {
		// Nécessaire pour la construction par réflection
	}
	
	public Area(String name, int width, int height, int x, int y, int type,
			int spaceStationsLimit, int idSector) {
		setName(name);
		setWidth(width);
		setHeight(height);
		setX(x);
		setY(y);
		setType(type);
		setSpaceStationsLimit(spaceStationsLimit);
		setProduct(0);
		setFull(false);
		setIdDominatingAlly(0);
		setIdSector(idSector);
		setEnvironment(Area.ENVIRONMENT_NORMAL);
	}
	
	public Area(String name, int width, int height, int x, int y, int type,
			int spaceStationsLimit, int idSector, int product) {
		setName(name);
		setWidth(width);
		setHeight(height);
		setX(x);
		setY(y);
		setType(type);
		setSpaceStationsLimit(spaceStationsLimit);
		setProduct(product);
		setFull(false);
		setIdDominatingAlly(0);
		setIdSector(idSector);
		setEnvironment(Area.ENVIRONMENT_NORMAL);
	}
	
	public Area(String name, int width, int height, int x, int y, int type,
			int spaceStationsLimit, int idSector, int product, String environment) {
		setName(name);
		setWidth(width);
		setHeight(height);
		setX(x);
		setY(y);
		setType(type);
		setSpaceStationsLimit(spaceStationsLimit);
		setProduct(product);
		setFull(false);
		setIdDominatingAlly(0);
		setIdSector(idSector);
		setEnvironment(environment);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public List<StorehouseResources> getStorehouseResources() {
		return DataAccess.getStorehouseResourcesByArea(getId());
	}
	
	public StorehouseResources getStorehouseResourcesByPlayer(int idPlayer) {
		List<StorehouseResources> storehouses = getStorehouseResources();
		
		synchronized (storehouses) {
			for (StorehouseResources storehouse : storehouses)
				if (storehouse.getIdPlayer() == idPlayer)
					return storehouse;
		}
		
		return null;
	}
	
	public List<StarSystem> getSystems() {
		return DataAccess.getSystemsByArea(getId());
	}
	
	public List<Marker> getMarkers() {
		return DataAccess.getMarkersByArea(getId());
	}
	
	// Renvoie les systèmes qui ont un propriétaire
	public List<StarSystem> getColonizedSystems() {
		List<StarSystem> systems = new ArrayList<StarSystem>(getSystems());
		Iterator<StarSystem> i = systems.iterator();
		
		while (i.hasNext()) {
			StarSystem system = i.next();
			
			if (system.getIdOwner() == 0)
				i.remove();
		}
		
		return systems;
	}
	
	public List<StellarObject> getObjects() {
		return DataAccess.getObjectsByArea(getId());
	}
	
	public List<StellarObject> getGates() {
		List<StellarObject> objects = getObjects();
		List<StellarObject> gates = new LinkedList<StellarObject>();
		
		synchronized (objects) {
			for (StellarObject object : objects) {
				if (object.getType().equals(StellarObject.TYPE_GATE))
					gates.add(object);
			}
		}
		
		return gates;
	}
	
	public List<Fleet> getFleets() {
		return DataAccess.getFleetsByArea(getId());
	}
	
	public List<Structure> getStructures() {
		return DataAccess.getStructuresByArea(getId());
	}
	
	// Recherche une structure susceptible d'apporter une certain quantité
	// d'énergie à un endroit donné
	public Structure getEnergySupplierStructure(Player player,
			int requiredEnergy, int x, int y) {
		List<Structure> areaStructures = getStructures();
		
		synchronized (areaStructures) {
			for (Structure structure : areaStructures) {
				int maxEnergy = structure.getMaxEnergy();
				
				if (maxEnergy > 0 && (structure.getIdOwner() == player.getId() || (
						structure.isShared() && player.getIdAlly() != 0 &&
						structure.getOwner().getIdAlly() == player.getIdAlly())) &&
						structure.getUsedEnergy() + requiredEnergy <= maxEnergy) {
					int dx = structure.getX() - x;
					int dy = structure.getY() - y;
					

					
					int radius = Structure.getEnergyDiffusionRange(structure.getType())+ (int)
					Product.getProductEffect(Product.PRODUCT_SELENIUM, structure.getOwner().getProductsCount(Product.PRODUCT_SELENIUM));
					
					if (dx * dx + dy * dy <= radius * radius)
						return structure;
				}
			}
		}
		
		return null;
	}
	
	public boolean contains(int x, int y) {
		return contains(x, y, 1, 1);
	}
	
	// Type de l'area
	public int getGeneralType() {
		int generalType=0;

		switch(getType()){
			case 2:
			case 5:
			case 9:
			case 12:
			case 15:
			case 27:
				generalType=AREA_GENERAL_MINING;
				break;
			case 1:
			case 4:
			case 8:
			case 11:
			case 14:
			case 31:
				generalType=AREA_GENERAL_COLONY;
				break;
			case 6:
			case 7:
			case 10:
			case 13:
			case 16:
			case 29:
				generalType=AREA_GENERAL_BANK;
				break;
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 30:
				generalType=AREA_GENERAL_BATTLE;
				break;
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 28:
				generalType=AREA_GENERAL_MINING_X5;
				break;
		}
		return generalType;
	}
	
	public int getLvlMin(){
		if (getType()==22 || getType()==23 ||
				getType()==24 || getType()==25 ||
				getType()==26 || getType()==28)
			return getSector().getLvlMin()+5;
		
		return getSector().getLvlMin();
	}
	
	public boolean contains(int x, int y, int width, int height) {
		return x >= 0 && x + width <= getWidth() &&
			   y >= 0 && y + height <= getHeight();
	}
	
	public Point getRandomFreeTile(int checks, Player player) {
		return getRandomFreeTiles(1, 1, checks, player);
	}
	
	public Point getRandomFreeTiles(int width, int height, int checks, Player player) {
		Point location;
		int tries = 0;
		do {
			location = new Point(
				(int) Math.floor(Math.random() * (getWidth() - width + 1)),
				(int) Math.floor(Math.random() * (getHeight() - height + 1)));
			tries++;
		} while (!areFreeTiles(location.x, location.y, width, height, checks, player) || tries > 500);
		
		return location;
	}
	
	public void checkValidStructureLocation(int structureType, int x, int y)
			throws IllegalOperationException {
		switch (structureType) {
		case Structure.TYPE_GENERATOR:
			List<StellarObject> objects = getObjects();
			StellarObject gravityWell = null;
			
			synchronized (objects) {
				for (StellarObject object : objects) {
					if (object.getType().equals(StellarObject.TYPE_GRAVITY_WELL)) {
						if (object.getX() == x && object.getY() == y) {
							gravityWell = object;
							break;
						}
					}
				}
			}
			
			if (gravityWell == null)
				throw new IllegalOperationException("Le générateur doit " +
					"être construit sur un puit gravitationnel.");
			
			if (!isFreeTile(x, y, Area.NO_STRUCTURES, null))
				throw new IllegalOperationException("Un générateur a " +
					"déjà été construit sur le puit graviationnel.");
			break;
		default:
			Dimension size = Structure.getSize(structureType);
			
			if (!areFreeTiles(
					x - size.width / 2,
					y - size.height / 2,
					size.width,
					size.height,
					Area.NO_SYSTEMS | Area.NO_OBJECTS | Area.NO_STRUCTURES |
					Area.NO_BLACKHOLES_IN_RANGE | Area.NO_GATES_IN_RANGE, null))
				throw new IllegalOperationException("Il ne doit pas y avoir " +
					"d'objets à proximité de la flotte (astéroïdes, systèmes...).");
			break;
		}
	}
	
	public boolean isFreeTile(int x, int y, int checks, Player player) {
		return areFreeTiles(x, y, 1, 1, checks, player);
	}
	
	// Vérifie que *toutes* les cases du rectangle à la position (x, y) et de
	// dimensions (width, height) sont libres
	// Renvoie false si les cases sont partiellement ou totallement en dehors
	// des limites du secteur
	public boolean areFreeTiles(int x, int y, int width, int height, int checks, Player player) {
		if (!contains(x, y, width, height))
			return false;
		
		// Vérifie qu'il n'y pas de case occupée par une flotte
		if ((checks & NO_FLEETS) != 0) {
			List<Fleet> fleets = getFleets();
			
			synchronized (fleets) {
				for (Fleet fleet : fleets) {
					if (fleet.getCurrentX() >= x && fleet.getCurrentX() < x + width &&
						fleet.getCurrentY() >= y && fleet.getCurrentY() < y + height)
						return false;
				}
			}
			
			// Vérifie le point d'arrivée des flottes en hyperespace
			List<Fleet> hyperspaceFleets = DataAccess.getHyperspaceFleets(getId());
			
			synchronized (hyperspaceFleets) {
				for (Fleet fleet : hyperspaceFleets) {
					if (fleet.getHyperspaceX() >= x && fleet.getHyperspaceX() < x + width &&
						fleet.getHyperspaceY() >= y && fleet.getHyperspaceY() < y + height)
						return false;
				}
			}
		}
		
		// Vérifie qu'il n'y a pas de case occupée par une structure
		if ((checks & NO_STRUCTURES) != 0) {
			List<Structure> structures = getStructures();
			
			synchronized (structures) {
				for (Structure structure : structures) {
					if (structure.getBounds().intersects(x, y, width, height))
						return false;
				}
			}
		}
		
		// Vérifie qu'il n'y a pas de case occupée par un objet
		if ((checks & NO_OBJECTS) != 0) {
			List<StellarObject> objects = getObjects();
			
			synchronized (objects) {
				for (StellarObject object : objects) {
					if ((object.isValidHyperjumpOutput() && (checks &
							EXCEPT_VALID_HYPERJUMP_OUTPUT_OBJECTS) != 0) ||
						(object.isPassable() && (
								checks & EXCEPT_PASSABLE_OBJECTS) != 0) ||
						(object.getType().equals(StellarObject.TYPE_BLACKHOLE
								) && (checks & EXCEPT_BLACKHOLES) != 0) ||
						(object.getType().equals(StellarObject.TYPE_DOODAD
								) && (checks & EXCEPT_DOODADS) != 0)) {
						continue;
					}
					
					if (object.getBounds().intersects(x, y, width, height))
						return false;
				}
			}
		}
		
		// Vérifie qu'il n'y a pas de case à portée d'un trou noir
		if ((checks & NO_BLACKHOLES_IN_RANGE) != 0) {
			List<StellarObject> objects = getObjects();
			
			synchronized (objects) {
				for (StellarObject object : objects) {
					if (object.getType() == StellarObject.TYPE_BLACKHOLE) {
						// Premier test pour éliminer le trou noir s'il est trop éloigné
						int dx = object.getX() - x;
						int dy = object.getY() - y;
						int radius = GameConstants.BLACKHOLE_GRAVITY_RADIUS +
							width + height;
						
						if (dx * dx + dy * dy > radius * radius)
							continue;
						
				    	for (int j = 0 ; j < width; j++)
				        	for (int k = 0; k < height; k++) {
								dx = object.getX() - (x + j);
								dy = object.getY() - (y + k);
								
								if (dx * dx + dy * dy <=
										GameConstants.BLACKHOLE_GRAVITY_RADIUS *
										GameConstants.BLACKHOLE_GRAVITY_RADIUS) {
									return false;
								}
				        	}
					}
				}
			}
		}
		
		// Vérifie qu'il n'y a pas de case à portée d'une porte HE
		if ((checks & NO_GATES_IN_RANGE) != 0) {
			List<StellarObject> objects = getObjects();
			
			synchronized (objects) {
				for (StellarObject object : objects) {
					if (object.getType() == StellarObject.TYPE_GATE) {
						// Premier test pour éliminer la porte si elle est trop éloignée
						int dx = object.getX() - x;
						int dy = object.getY() - y;
						int radius = GameConstants.HYPERGATE_JUMP_RADIUS +
							width + height;
						
						if (dx * dx + dy * dy > radius * radius)
							continue;
						
				    	for (int j = 0 ; j < width; j++)
				        	for (int k = 0; k < height; k++) {
								dx = object.getX() - (x + j);
								dy = object.getY() - (y + k);
								
								if (dx * dx + dy * dy <=
										GameConstants.HYPERGATE_JUMP_RADIUS *
										GameConstants.HYPERGATE_JUMP_RADIUS) {
									return false;
								}
				        	}
					}
				}
			}
		}
		
		// Vérifie qu'il n'y pas de case à l'intérieur des frontières d'un
		// système
		if ((checks & NO_SYSTEMS) != 0 ||
				(checks & NO_OCCUPIED_SYSTEM) != 0 ||
				(checks & NO_NEUTRAL_SYSTEM) != 0) {
			List<StarSystem> systems = getSystems();
			
			synchronized (systems) {
				for (StarSystem system : systems) {
					if (system.contains(x, y, width, height)) {
						if ((checks & NO_OCCUPIED_SYSTEM ) != 0) {
							if (system.getIdOwner() != 0)
								return false;
						} else if ((checks & NO_NEUTRAL_SYSTEM) != 0) {
							if (system.getIdOwner() != 0 &&
									system.getOwner().getTreatyWithPlayer(
											player).equals(Treaty.NEUTRAL))
								return false;
						} else {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Génère une position d'arrivé alétoire dans un secteur autour du point (x,y)
	 * 
	 * @param x
	 * @param y
	 * @param filter
	 * @return
	 * @throws IllegalOperationException 
	 */
	public int[] generateExitPosition(int x, int y, int mean, 
			int stdDev, int filter, Player player) throws IllegalOperationException{
		// Détermine aléatoirement un emplacement près de la porte
		// hyperspatiale d'arrivée
		int count = 0;
		int result[] = new int[2];
		
		do {
			double distance = Utilities.randn(mean, stdDev);
			double angle = Math.random() * 2 * Math.PI;
			
			result[0] = (int) Math.round(Math.cos(angle) * distance) + x;
			result[1] = (int) Math.round(Math.sin(angle) * distance) + y;
			count++;
		} while (!isFreeTile(result[0], result[1], filter, player)  && count < 100);
		
		if (count == 100)
			throw new IllegalOperationException("Impossible d'utiliser la porte " +
					"hyperspatiale. Le secteur d'arrivé semble surchargé.");
		
		return result;
	}
	
	// Renvoie true si les zones sont voisines
	public boolean isNeighbour(int idArea) {
		return getNeighbours().contains(idArea);
	}
	
	/*
	 * Attention :
	 * La 1e fois que cette méthode est appelée, un cache est construit pour y
	 * mettre les zones voisines. Si une coordonnée d'une zone est modifiée, le
	 * cache n'est pas remis à jour, il faut explicitement appeler
	 * getNeighbours(true) pour remettre le cache à jour.
	 */
	public List<Integer> getNeighbours() {
		return getNeighbours(false);
	}
	
	public synchronized List<Integer> getNeighbours(boolean updateCache) {
		if (neighboursCache == null)
			updateCache = true;
		
		if (updateCache)
			updateNeighboursCache();
		
		return neighboursCache;
	}
	
	public String getDominatingAllyName() {
		int idAlly = getIdDominatingAlly();
		
		if (idAlly == 0)
			return "";
		return DataAccess.getAllyById(idAlly).getName();
	}
	
	public Sector getSector() {
		return DataAccess.getSectorById(getIdSector());
	}

	public List<Ward> getWards() {
		return DataAccess.getWardsByArea(getId());
	}
	
	public List<SpaceStation> getSpaceStations() {
		return DataAccess.getSpaceStationsByArea(getId());
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateNeighboursCache() {
		List<Integer> neighbours = Collections.synchronizedList(
				new LinkedList<Integer>());
		List<Area> areas = DataAccess.getAreasBySector(getIdSector());
		
		for (Area area : areas) {
			int dx = area.getX() - getX();
			int dy = area.getY() - getY();
			
			double distance = Math.sqrt(dx * dx + dy * dy);
			
			if (area.getId() != getId()) {
				if (distance <= GameConstants.HYPERSPACE_DISTANCE_MAX)
					neighbours.add(area.getId());
			}
		}
		
		Area cachedArea = DataAccess.getAreaById(getId());
		
		if (cachedArea != this)
			cachedArea.neighboursCache = neighbours;
		neighboursCache = neighbours;
	}
}
