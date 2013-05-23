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

public class PathFinder {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
//	public static void main(String[] args) {
//		List<Integer> path =
//			getPath(DataAccess.getPlayerById(1),
//				DataAccess.getAreaById(808),
//				DataAccess.getAreaById(992),
//				true);
//		
//		for (Integer idArea : path) {
//			System.out.println(DataAccess.getAreaById(idArea).getName());
//		}
//		
//		System.exit(0);
//	}
	
//	public static List<Integer> getPath(Player player, Area from, Area to,
//			boolean allowLongJump) {
//		// Quadrant différent
//		if (from.getIdSector() != to.getIdSector())
//			return null;
//		
//		ArrayList<Node> openList = new ArrayList<Node>();
//		ArrayList<Area> closedList = new ArrayList<Area>();
//		
//		openList.add(new Node(from, null, 0));
//		
//		// Détermine la visibilité des zones du joueur dans le cas de sauts
//		// longue distance
//		byte[] areasVisibility = null;
//		if (allowLongJump)
//			areasVisibility = player.getAreasVisibility();
//		
//		boolean finished = false;
//		
//		Node selectedNode = null;
//		
//		// Cout d'un saut hyperspatial, en fonction de la longueur du saut
//		int jumpValue = 100;
//		int longJumpValue = jumpValue * (GameConstants.HYPERSPACE_LONG_JUMP +
//				GameConstants.HYPERSPACE_OUT + GameConstants.HYPERSPACE_RELOAD) /
//			(GameConstants.HYPERSPACE_TRAVEL_LENGTH + GameConstants.HYPERSPACE_OUT +
//					GameConstants.HYPERSPACE_RELOAD);
//		
//		do {
//			// Sélectionne le secteur avec le coût le plus faible (somme du coût
//			// pour aller jusqu'au secteur et de la distance séparant le secteur
//			// du secteur de destination)
//			selectedNode = null;
//			int lowestCost = Integer.MAX_VALUE;
//			
//			for (Node node : openList) {
//				int dx = node.area.getX() - to.getX();
//				int dy = node.area.getY() - to.getY();
//				int cost = node.cost + dx * dx + dy * dy;
//				
//				if (cost < lowestCost) {
//					selectedNode = node;
//					lowestCost = cost;
//				}
//			}
//			
//			// Le chemin optimal a été trouvé
//			if (selectedNode.area.getId() == to.getId()) {
//				finished = true;
//				continue;
//			}
//			
//			// Ajoute le secteur sélectionné à la liste fermée
//			openList.remove(selectedNode);
//			closedList.add(selectedNode.area);
//			
//			// Ajoute les secteurs voisins du secteur à la liste ouverte
//			areas:for (Integer idArea : selectedNode.area.getNeighbours()) {
//				Area area = DataAccess.getAreaById(idArea);
//				
//				boolean farNeighbour = area.isFarNeighbour(selectedNode.area.getId());
//				
//				// Vérifie que le secteur peut être atteind
//				if (farNeighbour) {
//					if (allowLongJump) {
//						if (areasVisibility[area.getId()] == Area.VISIBILITY_NONE)
//							continue;
//					} else {
//						continue;
//					}
//				}
//				
//				// Vérifie que le secteur n'est pas dans liste fermée
//				for (Area closedArea : closedList)
//					if (idArea == closedArea.getId())
//						continue areas;
//				
//				// Calcule le coût du secteur
//				int cost = selectedNode.cost +
//					(farNeighbour ? longJumpValue : jumpValue);
//				
//				// Vérifie que le noeud ne figure pas déjà dans la liste ouverte
//				for (Node node : openList) {
//					if (node.area.getId() == selectedNode.area.getId()) {
//						if (cost < node.cost) {
//							node.cost = cost;
//							node.parent = selectedNode.parent;
//						}
//						continue areas;
//					}
//				}
//				
//				// Ajoute le noeud à la liste ouverte
//				openList.add(new Node(area, selectedNode, cost));
//			}
//
//			// Pas de chemin entre les secteurs
//			if (openList.size() == 0) {
//				selectedNode = null;
//				finished = true;
//				continue;
//			}
//		} while (!finished);
//		
//		// Pas de chemin
//		if (selectedNode == null)
//			return null;
//		
//		// Construit le chemin
//		ArrayList<Integer> path = new ArrayList<Integer>();
//		buildPath(selectedNode, path);
//		
//		return path;
//	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
//	private static void buildPath(Node node, ArrayList<Integer> path) {
//		if (node != null) {
//			buildPath(node.parent, path);
//			path.add(node.area.getId());
//		}
//	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
//	private static class Node {
//		// ------------------------------------------------------- CONSTANTES -- //
//		// -------------------------------------------------------- ATTRIBUTS -- //
//		
//		private Area area;
//		private Node parent;
//		private int cost;
//		
//		// ---------------------------------------------------- CONSTRUCTEURS -- //
//		
//		public Node(Area area, Node parent, int cost) {
//			this.area = area;
//			this.parent = parent;
//			this.cost = cost;
//		}
//		
//		// --------------------------------------------------------- METHODES -- //
//		// ------------------------------------------------- METHODES PRIVEES -- //
//	}
}
