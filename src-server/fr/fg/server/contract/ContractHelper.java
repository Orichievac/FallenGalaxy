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

package fr.fg.server.contract;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.Ally;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractArea;
import fr.fg.server.data.ContractRelationship;
import fr.fg.server.data.ContractReward;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Technology;

public class ContractHelper {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Prix moyen d'un point de puissance
	public final static int POWER_COST = 75;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static void applyRelationships(Contract contract, Player player,
			boolean success) {
		List<ContractRelationship> relationships = contract.getRelationships();
		
		synchronized (relationships) {
			for (ContractRelationship relationship : relationships) {
				if (success) {
					player.addRelationshipValue(relationship.getIdAlly(),
							relationship.getModifier());
				} else {
					if (relationship.getModifier() > 0)
						player.addRelationshipValue(relationship.getIdAlly(),
							-relationship.getModifier());
				}
			}
		}
	}
	
	public static void applyRelationships(Contract contract, Ally ally,
			boolean success) {
		List<ContractRelationship> relationships = contract.getRelationships();
		
		synchronized (relationships) {
			for (ContractRelationship relationship : relationships) {
					if (success) {
						ally.addRelationshipValue(relationship.getIdAlly(),
								relationship.getModifier());
					} else {
						if (relationship.getModifier() > 0)
							ally.addRelationshipValue(relationship.getIdAlly(),
								-relationship.getModifier());
				}
			}
		}
	}
	
	
	public static String formatContractAreas(Contract contract) {
		StringBuffer buffer = new StringBuffer();
		List<ContractArea> contractAreas =
			DataAccess.getAreasByContract(contract.getId());
		
		boolean first = true;
		synchronized (contractAreas) {
			for (ContractArea contractArea : contractAreas) {
				if (first)
					first = false;
				else
					buffer.append(", ");
				
				buffer.append(contractArea.getArea().getName());
			}
		}
		
		return buffer.toString();
	}
	
	// Renvoie la valeur de la production / min estimée en fonction du nombre
	// de points du joueur
	public static double getEstimatedProduction(long difficulty) {
		return difficulty * (.036 / Math.pow(2,
				Math.log10(difficulty / 1000))) / 60.;
	}
	
	public static void addXpReward(Contract contract, int l) {
		int coef = contract.getMaxAttendees() > 1 ? 2 : 1;
		
		ContractReward reward = new ContractReward(contract.getId(),
			ContractReward.TYPE_XP, 0, l * coef);
		reward.save();
	}
	
	public static void addXpReward(Contract contract, long l) {
		int coef = contract.getMaxAttendees() > 1 ? 2 : 1;
		
		ContractReward reward = new ContractReward(contract.getId(),
			ContractReward.TYPE_XP, 0, l * coef);
		reward.save();
	}
	
	public static void addCreditReward(Contract contract, long l) {
		int coef = contract.getMaxAttendees() > 1 ? 2 : 1;
		
		ContractReward reward = new ContractReward(contract.getId(),
			ContractReward.TYPE_CREDIT, 0, l * coef);
		reward.save();
	}
	
	public static void addFleetXpReward(Contract contract, int xp, int fleetsCount) {
		int coef = contract.getMaxAttendees() > 1 ? 2 : 1;
		
		ContractReward reward = new ContractReward(contract.getId(),
			ContractReward.TYPE_FLEET_XP, fleetsCount, xp * coef);
		reward.save();
	}
	
	public static void addRelationship(Contract contract, String faction, int modifier) {
		ContractRelationship relationship = new ContractRelationship(
			contract.getId(), NpcHelper.getAllyFaction(faction).getId(), modifier);
		relationship.save();
	}
	
	
	// Renvoie les vaisseaux qui sont censés avoir été débloqués à partir d'une
	// certaine quantité de points
	public static int[] getEstimatedAvailableShips(long difficulty) {
		long threshold = 10000;
		int maxTechLevel = 0;
		while (threshold < difficulty) {
			threshold *= 4.5;
			maxTechLevel++;
		}
		
		ArrayList<Integer> availableShipsList = new ArrayList<Integer>();
		
		ships:for (int i = 0; i < Ship.SHIPS.length; i++) {
			Ship ship = Ship.SHIPS[i];
			
			if (ship == null || ship.isAltered())
				continue;
			
			int[] requirements = ship.getTechnologies();
			
			for (int requirement : requirements) {
				if (Technology.getTechnologyById(requirement).getLevel() > maxTechLevel)
					continue ships;
			}
			
			availableShipsList.add(i);
		}
		
		int[] availableShips = new int[availableShipsList.size()];
		for (int i = 0; i < availableShips.length; i++)
			availableShips[i] = availableShipsList.get(i);
		
		return availableShips;
	}
	
	// Renvoie les vaisseaux qui sont censés avoir été débloqués à partir d'une
	// certaine quantité de points
	public static int[] getEstimatedAvailableShipsForAlly(long difficulty) {
		long threshold = 10000;
		int maxTechLevel = 0;
		while (threshold < difficulty*0.75 && maxTechLevel<Technology.getMaxTechLevel()+1) {
			threshold *= 4.5;
			maxTechLevel++;
		}
		
		ArrayList<Integer> availableShipsList = new ArrayList<Integer>();
		
		ships:for (int i = 0; i < Ship.SHIPS.length; i++) {
			Ship ship = Ship.SHIPS[i];
			
			if (ship == null || ship.isAltered())
				continue;
			
			int[] requirements = ship.getTechnologies();
			
			for (int requirement : requirements) {
				if (Technology.getTechnologyById(requirement).getLevel() > maxTechLevel)
					continue ships;
			}
			
			availableShipsList.add(i);
		}
		
		int[] availableShips = new int[availableShipsList.size()];
		for (int i = 0; i < availableShips.length; i++)
			availableShips[i] = availableShipsList.get(i);
		
		return availableShips;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
