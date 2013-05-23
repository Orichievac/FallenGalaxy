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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.base.SectorBase;

public class Sector extends SectorBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		SECTOR_START = 1,
		SECTOR_COLONIES_10_20 = 2,
		SECTOR_COLONIES_20_30 = 3,
		SECTOR_COLONIES_30_40 = 4,
		SECTOR_COLONIES_40_50 = 5,
	    SECTOR_COLONIES_50_60 = 6;
	
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private List<Integer> neighboursCache;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
    public Sector() {
		// Nécessaire pour la construction par réflection
    }
    
    public Sector(String name, int x, int y, int type, int nebula) {
		setName(name);
		setX(x);
		setY(y);
		setType(type);
		setNebula(nebula);
		setStrategicValue(getBaseStrategicValue(type));
    }
    
	// --------------------------------------------------------- METHODES -- //
    
    public List<Area> getAreas() {
    	return DataAccess.getAreasBySector(getId());
    }

	// Renvoie true si les quadrants sont voisins
	public boolean isNeighbour(int idSector) {
		return getNeighbours().contains(idSector);
	}
	
	// Renvoie un secteur aléatoire dans le quadrant
	public Area getRandomArea() {
		List<Area> areas = getAreas();
		
		return areas.get((int) (Math.random() * areas.size()));
	}
	
	// Renvoie le premier secteur d'un type donné dans le quadrant
	public Area getAreaByType(int type) {
		List<Area> result = getAreasByType(type);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	// Renvoie les secteurs d'un type donné dans le quadrant
	public List<Area> getAreasByType(int type) {
		List<Area> areas = getAreas();
		ArrayList<Area> result = new ArrayList<Area>();
		
		synchronized (areas) {
			for (Area area : areas)
				if (area.getType() == type)
					result.add(area);
		}
		
		return result;
	}
	
	public int getLvlMin(){
		return ((getType()-1)*10);
	}
	
	
	public int getLvlMax(){
		return (getType()*10);
	}

	
	/*
	 * Attention :
	 * La 1e fois que cette méthode est appelée, un cache est construit pour y
	 * mettre les quadrants voisins. Si une coordonnée d'un quadrant est modifiée, le
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
	
	public static int getBaseStrategicValue(int type) {
		switch (type) {
		case SECTOR_START:
			return 25;
		case SECTOR_COLONIES_10_20:
			return 75;
		case SECTOR_COLONIES_20_30:
			return 75;
		case SECTOR_COLONIES_30_40:
			return 75;
		case SECTOR_COLONIES_40_50:
			return 75;
		case SECTOR_COLONIES_50_60:
			return 75;
		default:
			throw new IllegalArgumentException(
				"Invalid sector type : '" + type + "'.");
		}
	}
	
	public static int getSystemsStrategicValueRange(int type) {
		switch (type) {
		case SECTOR_START:
			return 150;
		case SECTOR_COLONIES_10_20:
			return 200;
		case SECTOR_COLONIES_20_30:
			return 200;
		case SECTOR_COLONIES_30_40:
			return 200;
		case SECTOR_COLONIES_40_50:
			return 200;
		case SECTOR_COLONIES_50_60:
			return 75;
		default:
			throw new IllegalArgumentException(
				"Invalid sector type : '" + type + "'.");
		}
	}
	
	public double getAllyInfluence(int idAlly) {
		List<AllyInfluence> allyInfluences =
			DataAccess.getAllyInfluencesBySector(getId());
		
		for (AllyInfluence allyInfluence : allyInfluences)
			if (allyInfluence.getIdAlly() == idAlly)
				return allyInfluence.getInfluenceValue() *
					allyInfluence.getInfluenceCoef() /
					allyInfluence.getSystemsCount();
		
		return 0;
	}
	
	public void updateInfluences() {
		// Calcule l'influence totale et par alliance des stations
		int influenceSum = 0;
		List<Area> areas = new ArrayList<Area>(getAreas());
		Map<Integer, Integer> influencesByAllies =
			new HashMap<Integer, Integer>();
		
		for (Area area : areas) {
			List<SpaceStation> spaceStations = area.getSpaceStations();
			
			synchronized (spaceStations) {
				for (SpaceStation spaceStation : spaceStations) {
					int influence = SpaceStation.INFLUENCE_LEVELS[spaceStation.getLevel()];
					
					if (influence > 0) {
						if (influencesByAllies.get(spaceStation.getIdAlly()) != null)
							influencesByAllies.put(spaceStation.getIdAlly(),
								influencesByAllies.get(spaceStation.getIdAlly()) + influence);
						else
							influencesByAllies.put(spaceStation.getIdAlly(), influence);
						influenceSum += influence;
					}
				}
			}
		}
		
		// Met à jour l'influence des alliances
		List<AllyInfluence> allyInfluences = new ArrayList<AllyInfluence>(
				DataAccess.getAllyInfluencesBySector(getId()));
		
		for (AllyInfluence allyInfluence : allyInfluences) {
			Integer influenceValue = influencesByAllies.get(allyInfluence.getIdAlly());
			
			if (influenceValue == null) {
				// L'alliance n'a plus d'influence dans le secteur
				updateAllySystems(allyInfluence.getAlly());
				
				allyInfluence.delete();
			} else {
				double newInfluenceCoef = influenceValue / (double) influenceSum;
				
				if (allyInfluence.getInfluenceValue() != influenceValue ||
						allyInfluence.getInfluenceCoef() != newInfluenceCoef) {
					// L'influence de l'alliance dans le secteur a été modifiée
					updateAllySystems(allyInfluence.getAlly());
					
					synchronized (allyInfluence.getLock()) {
						allyInfluence = DataAccess.getEditable(allyInfluence);
						allyInfluence.setInfluenceCoef(newInfluenceCoef);
						allyInfluence.setInfluenceValue(influenceValue);
						allyInfluence.save();
					}
				}
			}
		}
		
		allies:for (Integer idAlly : influencesByAllies.keySet()) {
			for (AllyInfluence allyInfluence : allyInfluences)
				if (allyInfluence.getIdAlly() == idAlly)
					continue allies;
			
			// L'alliance de l'alliance dans le secteur est nouvelle
			updateAllySystems(DataAccess.getAllyById(idAlly));
			
			Ally ally = DataAccess.getAllyById(idAlly);
			int systemsCount = 0;
			List<Player> members = new ArrayList<Player>(ally.getMembers());
			
			for (Player member : members) {
				List<StarSystem> systems = member.getSystems();
				
				for (StarSystem system : systems)
					if (system.getArea().getIdSector() == getId())
						systemsCount++;
			}
			
			int influenceValue = influencesByAllies.get(idAlly);
			
			AllyInfluence allyInfluence = new AllyInfluence(idAlly,
				getId(), influenceValue / (double) influenceSum,
				influenceValue, systemsCount);
			allyInfluence.save();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
    
	private void updateAllySystems(Ally ally) {
		// Met à jour les systèmes des membres de l'alliance dans le secteur
		List<Player> members = new ArrayList<Player>(ally.getMembers());
		
		for (Player member : members) {
			List<StarSystem> systems = new ArrayList<StarSystem>(member.getSystems());
			
			for (StarSystem system : systems) {
				if (system.getArea().getIdSector() == getId())
					StarSystem.updateSystem(system);
			}
			
			UpdateTools.queuePlayerSystemsUpdate(member.getId(), false);
		}
	}
	
	private void updateNeighboursCache() {
		List<Integer> neighbours = Collections.synchronizedList(
				new LinkedList<Integer>());
		List<Sector> sectors = DataAccess.getAllSectors();
		
		for (Sector sector : sectors) {
			int dx = sector.getX() - getX();
			int dy = sector.getY() - getY();
			
			double distance = Math.sqrt(dx * dx + dy * dy);
			
			if (sector.getId() != getId()) {
				if (distance <= GameConstants.HYPERSPACE_DISTANCE_MAX)
					neighbours.add(sector.getId());
			}
		}
		
		Sector cachedSector = DataAccess.getSectorById(getId());
		
		if (cachedSector != this)
			cachedSector.neighboursCache = neighbours;
		neighboursCache = neighbours;
	}
}
