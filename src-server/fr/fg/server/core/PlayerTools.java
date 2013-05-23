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

package fr.fg.server.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.awt.Point;
import java.util.HashMap;

import fr.fg.server.data.AccountAction;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Connection;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.util.LoggingSystem;

public class PlayerTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	 public static Player setPlayerAway(Player player, boolean away) {
         if (player.isAway() == away) {
                 if (!away)
                 return player;
         }
        
         synchronized (player.getLock()) {
                 player = DataAccess.getEditable(player);
                 player.setAway(away);
                 if (!away)
                 player.save();
         }
        
        
         Map<Integer, List<Point>> areas = new HashMap<Integer, List<Point>>();
          
         // Liste des flottes du joueur
         List<Fleet> fleets = player.getFleets();
         synchronized (fleets) {
                 for (Fleet fleet : fleets) {
                         List<Point> locations = areas.get(fleet.getIdArea());
                        
                         if (locations == null) {
                                 locations = new LinkedList<Point>();
                                 areas.put(fleet.getIdArea(), locations);
                         }
                        
                         locations.add(new Point(fleet.getX(), fleet.getY()));
                 }
         }
        
         // Liste des structures du joueur
         List<Structure> structures = player.getStructures();
         synchronized (structures) {
                 for (Structure structure : structures) {
                         List<Point> locations = areas.get(structure.getIdArea());
                        
                         if (locations == null) {
                                 locations = new LinkedList<Point>();
                                 areas.put(structure.getIdArea(), locations);
                         }
                        
                         locations.add(new Point(structure.getX(), structure.getY()));
                 }
         }
        
         for (Integer idArea : areas.keySet())
                 UpdateTools.queueAreaUpdate(idArea, 0, areas.get(idArea));
        
         return player;
 }

	// Ferme le compte du joueur
	public static void closeAccount(Player player, boolean idle) {
		List<Connection> connections =
			DataAccess.getConnectionsByPlayer(player.getId());
		Connection lastConnection = null;
		long lastConnectionDate = 0;
		
		synchronized (connections) {
			for (Connection connection : connections)
				if (connection.getStart() > lastConnectionDate) {
					lastConnection = connection;
					lastConnectionDate = connection.getStart();
				}
		}
		
		int ip = lastConnection != null ? lastConnection.getIp() : 0;
		
		if(DataAccess.getAccountActionByPlayer(player.getLogin())!=null)
			DataAccess.getAccountActionByPlayer(player.getLogin()).delete();
			
			
			AccountAction accountAction = new AccountAction(player.getLogin(),
				idle ? AccountAction.ACTION_IDLE : AccountAction.ACTION_CLOSED,
				player.getEmail(), player.getBirthday(), ip,
				player.getPlayedTime(Player.SCALE_OVERALL),
				player.getCloseAccountReason());
			accountAction.save();
		
			
			
		
		// Efface les missions en cours
		// FIXME
		
		// Quitte l'alliance
		if (player.getIdAlly() != 0) {
			Ally ally = player.getAlly();
			
			synchronized (player.getLock()) {
				player = DataAccess.getEditable(player);
				player.setIdAlly(0);
				player.setAllyRank(0);
				player.save();
			}
			
			if (ally != null) {
				if (ally.getMembers().size() == 1) {
					ally.delete();
					
					// Mise à jour des influences dans le quadrant
					List<Sector> sectors = new ArrayList<Sector>(DataAccess.getAllSectors());
					for (Sector sector : sectors)
						sector.updateInfluences();
				} else {
					ally.updateInfluences();
					
					// TODO jgottero si leader, vote ?
				}
			}
		}
		
		// Réinitialise les systèmes
		List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
		
		for (StarSystem system : systems) {
			synchronized (system.getLock()) {
				system = DataAccess.getEditable(system);
				system.resetSettings();
				system.save();
			}
		}
		
		// Efface le joueur
		synchronized (player.getLock()) {
			player.delete();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
