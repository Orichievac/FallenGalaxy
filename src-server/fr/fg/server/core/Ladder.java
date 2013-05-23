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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.data.Ally;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;
import fr.fg.server.data.Slot;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.util.Utilities;

public class Ladder {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Ladder instance = null;
	
	// Classement de chaque joueur
	private Map<Integer, Integer> playersRanks;
	
	// Joueurs triés par ordre décroissant de points
	private List<Integer> playersLadder;

	// Classement de chaque alliance
	private Map<Integer, Integer> alliesRanks;

	// Alliances triées par ordre décroissant de points
	private List<Integer> alliesLadder;

	// Date de dernière mise à jour du classement
	private long lastUpdateDate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Ladder() {
		this.update();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getPlayerRank(int idPlayer) {
		Integer rank = playersRanks.get(idPlayer);
		
		if (rank == null)
			return playersRanks.size() + 1;
		return rank;
	}
	
	public List<Integer> getPlayersLadder() {
		return this.playersLadder;
	}
	
	public int getAllyRank(int idAlly) {
		Integer rank = alliesRanks.get(idAlly);
		
		if (rank == null)
			return alliesRanks.size() + 1;
		return rank;
	}
	
	public List<Integer> getAlliesLadder() {
		return this.alliesLadder;
	}
	
	public long getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public synchronized void update() {
		// Crée une copie de la liste des joueurs pour éviter des problèmes de
		// concurrence
		List<Player> players = new ArrayList<Player>(
				DataAccess.getAllPlayers());
		
		for (Player player : players) {
			if (player.isAi())
				continue;
			
			double points = 0;
			
			// Met à jour les recherches du joueur
			player.updateResearch();
			
			// Points pour les technologies recherchées (1 point par 40
			// points de recherche pour chaque technologie recherchée)
			List<Research> researches =
				DataAccess.getResearchesByPlayer(player.getId());
			
			synchronized (researches) {
				for (Research research : researches) {
					if (research.getProgress() == 1) {
						points += research.getLength() / 100.;
					}
				}
			}
			
			// Points pour les structures construites (1 point par 100 ressources
			// investies)
			List<Structure> structures = new ArrayList<Structure>(
				DataAccess.getStructuresByOwner(player.getId()));
			
			for (Structure structure : structures) {
				if (structure.getType() == Structure.TYPE_SPACESHIP_YARD) {
					StructureSpaceshipYard spaceshipYard = structure.getSpaceshipYard();
					synchronized (spaceshipYard.getLock()) {
						spaceshipYard = DataAccess.getEditable(spaceshipYard);
						spaceshipYard.update();
						spaceshipYard.save();
					}
				}
				int[] baseCost = Structure.getBaseCost(structure.getType());
				for (int r = 0; r < baseCost.length; r++)
					points += baseCost[r] / 100f;
				
				int[] moduleTypes = structure.getValidModules();
				
				for (int moduleType : moduleTypes) {
					int level = structure.getModuleLevel(moduleType);
					
					if (level > 0) {
						long[] cost = structure.getModuleCost(moduleType, level);
						
						for (int r = 0; r < cost.length; r++)
							points += cost[r] / 100f;
					}
				}
			}
			
			// Points pour les bâtiments construits (1 point par 100 ressources
			// investies)
			List<StarSystem> systems = new ArrayList<StarSystem>(
				DataAccess.getSystemsByOwner(player.getId()));
			
			for (StarSystem system : systems) {
				system = StarSystem.updateSystem(system);
				
				// Batiments
				for (int type = 0; type < Building.BUILDING_COUNT; type++) {
					int[] buildings = system.getBuildings(type);
					
					for (int level = 0; level < Building.BUILDING_LEVEL_COUNT; level++) {
						if (buildings[level] > 0) {
							int[] cost = Building.getCost(type, level);
							
							for (int r = 0; r < GameConstants.RESOURCES_COUNT + 1; r++)
								points += buildings[level] * cost[r] / 100f;
						}
					}
				}
				
				// Vaisseaux
				for (int i = 0; i < 5; i++) {
					Slot slot = system.getSlot(i);
					
					if (slot.getId() != 0) {
						int[] cost = slot.getShip().getCost();
						
						for (int r = 0; r < GameConstants.RESOURCES_COUNT; r++)
							points += slot.getCount() * cost[r] / 100f;
					}
				}
			}
			
			// Points pour les vaisseaux construits (1 point par 100 ressources
			// investies)
			List<Fleet> fleets = DataAccess.getFleetsByOwner(player.getId());
			
			synchronized (fleets) {
				for (Fleet fleet : fleets) {
					// Ajoute +250 points par flotte
					points += 250;
					
					for (int i = 0; i < 5; i++) {
						Slot slot = fleet.getSlot(i);
						
						if (slot.getId() != 0) {
							int[] cost = slot.getShip().getCost();
							
							for (int r = 0; r < GameConstants.RESOURCES_COUNT; r++)
								points += slot.getCount() * cost[r] / 100f;
						}
					}
				}
			}
			
			synchronized (player.getLock()) {
				Player newPlayer = DataAccess.getEditable(player);
				newPlayer.setPoints((long) Math.floor(points));
				DataAccess.save(newPlayer);
			}
		}
		
		// Recharge la liste des joueurs pour avoir les points mis à jour
		players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		// Tri les joueurs par points
		Collections.sort(players, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				if (p1.getPoints() > p2.getPoints())
					return -1;
				if (p1.getPoints() == p2.getPoints())
					return 0;
				return 1;
			}
		});
		
		// Détermine le rang de chaque joueur
		Map<Integer, Integer> playersRanks = new HashMap<Integer, Integer>();
		long previousPoints =
			players.size() > 0 ? players.get(0).getPoints() : 0;
		int previousRank = 1;
		int i = 1;
		
		for (Player player : players) {
			previousRank = previousPoints == player.getPoints() ?
					previousRank : i;
			previousPoints = player.getPoints();
			playersRanks.put(player.getId(), previousRank);
			i++;
		}
		
		List<Integer> playersLadder = new ArrayList<Integer>();
		
		for (Player player : players)
			playersLadder.add(player.getId());
		
		// Calcule les points des alliances
		List<Ally> allies = new ArrayList<Ally>(DataAccess.getAllAllies());
		
		for (Ally ally : allies) {
			if (ally.isAi())
				continue;
			
			long points = 0;
			
			List<Player> members = ally.getMembers();
			
			synchronized (members) {
				for (Player member : members)
					points += member.getPoints();
			}
			
			// Points pour les stations spatiales
			List<SpaceStation> spaceStations = ally.getSpaceStations();
			
			synchronized (spaceStations) {
				for (SpaceStation spaceStation : spaceStations) {
					points += (long) Math.floor(spaceStation.getValue() / 100.);
				}
			}
			
			synchronized (ally.getLock()) {
				Ally newAlly = DataAccess.getEditable(ally);
				newAlly.setPoints(points);
				DataAccess.save(newAlly);
			}
		}
		
		// Recharge la liste des alliances pour avoir les points mis à jour
		allies = new ArrayList<Ally>(DataAccess.getAllAllies());
		
		// Tri les alliances par points
		Collections.sort(allies, new Comparator<Ally>() {
			public int compare(Ally a1, Ally a2) {
				if (a1.getPoints() > a2.getPoints())
					return -1;
				if (a1.getPoints() == a2.getPoints())
					return 0;
				return 1;
			}
		});
		
		// Détermine le rang de chaque alliance
		Map<Integer, Integer> alliesRanks = new HashMap<Integer, Integer>();
		previousPoints = allies.size() > 0 ? allies.get(0).getPoints() : 0;
		previousRank = 1;
		i = 1;
		
		for (Ally ally : allies) {
			previousRank = previousPoints == ally.getPoints() ?
					previousRank : i;
			previousPoints = ally.getPoints();
			alliesRanks.put(ally.getId(), previousRank);
			i++;
		}
		
		List<Integer> alliesLadder = new ArrayList<Integer>();
		
		for (Ally ally : allies)
			alliesLadder.add(ally.getId());
		
		// Met à jour les attributs de la classe
		this.playersRanks  = Collections.synchronizedMap(playersRanks);
		this.playersLadder = Collections.synchronizedList(playersLadder);
		this.alliesRanks  = Collections.synchronizedMap(alliesRanks);
		this.alliesLadder = Collections.synchronizedList(alliesLadder);
		
		lastUpdateDate = Utilities.now();
	}
	
	public static void init(){
		if(instance == null){
			instance = new Ladder();
		}
	}
	
	public static Ladder getInstance() {
		init();
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
