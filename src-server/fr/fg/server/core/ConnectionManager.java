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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import fr.fg.client.data.ChatMessageData;
import fr.fg.server.action.chat.SendMessage;
import fr.fg.server.data.Connection;
import fr.fg.server.data.Contact;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.Structure;
import fr.fg.server.data.Treaty;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class ConnectionManager {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Temps en sec au bout duquel un joueur est déconnecté s'il n'a envoyé
	// aucune information
	public final static int PING_TIMEOUT = 120;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static final ConnectionManager instance = new ConnectionManager();
	
	private Set<Integer> connectedPlayers;
	private Set<Integer> connectedModerators;
	
	private Map<Integer, List<Integer>> playersByArea;
	
	private TimeoutThread timeoutThread;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private ConnectionManager() {
		this.connectedPlayers = new CopyOnWriteArraySet<Integer>();
		this.connectedModerators = new CopyOnWriteArraySet<Integer>();
		this.playersByArea = Collections.synchronizedMap(
				new HashMap<Integer, List<Integer>>());
		this.timeoutThread = new TimeoutThread();
		this.timeoutThread.start();
		
		// Vérifie qu'il n'y pas de connexion active qui serait restés suite à
		// un mauvais arrêt du serveur
		List<Connection> connections = new ArrayList<Connection>(
				DataAccess.getAllConnections());
		int errors = 0;
		
		for (Connection connection : connections) {
			if (connection.getEnd() == 0) {
				Connection newConnection = DataAccess.getEditable(connection);
				newConnection.setEnd(newConnection.getStart());
				newConnection.save();
				
				errors++;
			}
		}
		
		LoggingSystem.getServerLogger().info("Connection manager initialized [" +
				errors + " connections were badly closed].");
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isConnected(int idPlayer) {
		return connectedPlayers.contains(idPlayer);
	}
	
	public void connect(Player player, int ip) {
		player.setLastPing(Utilities.now());
		player.setConnected(true);
		player.setAway(false);
		this.connectedPlayers.add(player.getId());
		if(player.isModerator() || player.isAdministrator()) {
			this.connectedModerators.add(player.getId());
		}
		UpdateManager.getInstance().getUpdatesByPlayer(player.getId()).clear();
		
		// Log la connexion du joueur
		Connection connection = new Connection(ip, player.getId());
		connection.save();
		
		// Signale la connexion aux amis du joueur
		JSONStringer json = new JSONStringer();
		json.object().
			key(ChatMessageData.FIELD_CONTENT).		value("").
			key(ChatMessageData.FIELD_DATE).		value(Utilities.now()).
			key(ChatMessageData.FIELD_TYPE).		value(SendMessage.TYPE_CONNECTION).
			key(ChatMessageData.FIELD_CHANNEL).		value("").
			key(ChatMessageData.FIELD_AUTHOR).		value(player.getLogin()).
			key(ChatMessageData.FIELD_RIGHTS).		value("").
			key(ChatMessageData.FIELD_ALLY_TAG).	value(player.getAllyTag()).
			key(ChatMessageData.FIELD_ALLY_NAME).	value(player.getAllyName()).
			endObject();
		String jsonString = json.toString();
		
		List<Contact> contacts =
			DataAccess.getContactsByContact(player.getId());
		
		synchronized (contacts) {
			for (Contact contact : contacts) {
				if (contact.getType().equals(Contact.TYPE_FRIEND) &&
						!player.getTreatyWithPlayer(
							contact.getIdPlayer()).equals(Treaty.ENEMY)) {
					UpdateTools.queueChatUpdate(contact.getIdPlayer(), jsonString);
				}
			}
		}
		updateConnectionStatus(player);
	}
	
	
	public void disconnect(Player player) {
		disconnect(player, true);
	}
	
	public void disconnectAll() {
		while (connectedPlayers.size() > 0) {
			Player player = DataAccess.getPlayerById(
					connectedPlayers.iterator().next());
			connectedModerators.iterator().next();
			disconnect(player, false);
		}
	}
	
	public Set<Integer> getConnectedPlayers() {
		return connectedPlayers;
	}
	
	public Set<Integer> getConnectedModerators() {
		return connectedModerators;
	}
	
	public void joinArea(Player player, int idArea) {
		if (player.getIdCurrentArea() != idArea) {
			leaveArea(player);
			
			getPlayersByArea(idArea).add(player.getId());
			player.setIdCurrentArea(idArea);
		}
	}
	
	public void leaveArea(Player player) {
		if (player.getIdCurrentArea() != 0) {
			getPlayersByArea(player.getIdCurrentArea()).remove((Integer) player.getId());
			player.setIdCurrentArea(0);
		}
	}
	
	public List<Integer> getPlayersByArea(int idArea) {
		List<Integer> players;
		
		players = playersByArea.get(idArea);
		
		if (players == null) {
			players = new CopyOnWriteArrayList<Integer>();
			playersByArea.put(idArea, players);
		}
		
		return players;
	}
	
	public static ConnectionManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void disconnect(Player player, boolean notify) {
		player.setConnected(false);
		player.setAway(false);
		this.connectedPlayers.remove(player.getId());
		if(this.connectedModerators.contains(player.getId())) {
			this.connectedModerators.remove(player.getId());
		}
		ChatManager.getInstance().leaveAllChannels(player.getId());
		leaveArea(player);
		UpdateManager.getInstance().getUpdatesByPlayer(player.getId()).clear();
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.setLastConnection(Utilities.now());
			player.save();
		}
		
		// Log la fin de la connexion du joueur
		Connection activeConnection =
			DataAccess.getActiveConnectionByPlayer(player.getId());
		
		if (activeConnection != null) {
			activeConnection = DataAccess.getEditable(activeConnection);
			activeConnection.setEnd(Utilities.now());
			activeConnection.save();
		}
		
		if (notify) {
			// Signale la déconnexion aux amis du joueur
			JSONStringer json = new JSONStringer();
			json.object().
				key(ChatMessageData.FIELD_CONTENT).		value("").
				key(ChatMessageData.FIELD_DATE).		value(Utilities.now()).
				key(ChatMessageData.FIELD_TYPE).		value(SendMessage.TYPE_DISCONNECTION).
				key(ChatMessageData.FIELD_AUTHOR).		value(player.getLogin()).
				key(ChatMessageData.FIELD_RIGHTS).		value("").
				key(ChatMessageData.FIELD_ALLY_TAG).	value(player.getAllyTag()).
				key(ChatMessageData.FIELD_ALLY_NAME).	value(player.getAllyName()).
				endObject();
			String jsonString = json.toString();
			
			List<Contact> contacts = new ArrayList<Contact>(
				DataAccess.getContactsByContact(player.getId()));
			
			for (Contact contact : contacts) {
				if (contact.getType().equals(Contact.TYPE_FRIEND) &&
						!player.getTreatyWithPlayer(
							contact.getIdPlayer()).equals(Treaty.ENEMY)) {
					UpdateTools.queueChatUpdate(contact.getIdPlayer(), jsonString);
				}
			}
			updateConnectionStatus(player);
		}
	}
	
	private void updateConnectionStatus(Player player) {
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
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class TimeoutThread extends Thread {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public TimeoutThread() {
			setName("TimeoutThread");
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void run() {
			while (!isInterrupted()) {
				long now = Utilities.now();
				
				for (Integer idPlayer : connectedPlayers) {
					// Déconnecte tous les joueurs dont le dernier ping date de
					// plus d'une minute
					Player player = DataAccess.getPlayerById(idPlayer);
					
					if (player.getLastPing() + PING_TIMEOUT < now) {
						disconnect(player);
					}
				}
				
				try {
					sleep(PING_TIMEOUT * 100);
				} catch (InterruptedException e) {
					// Ignoré
				}
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
