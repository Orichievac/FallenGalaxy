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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.continuation.Continuation;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;

public final class UpdateManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static UpdateManager instance = new UpdateManager();
	
	// Mises à jour en attente d'être envoyées
	private Map<Integer, List<Update>> pendingUpdates;
	
	// Joueurs dont la socket est en attente d'être reveillée
	private Map<Long, List<Integer>> pendingWakeUps;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private UpdateManager() {
		// TODO jgottero supprimer automatiquement les updates au bout d'un
		// certain temps + à la connexion d'un joueur (?)
		this.pendingUpdates = Collections.synchronizedMap(
				new HashMap<Integer, List<Update>>());
		this.pendingWakeUps = Collections.synchronizedMap(
				new HashMap<Long, List<Integer>>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addUpdate(int idPlayer, Update data) {
		addUpdate(idPlayer, data, true);
	}
	
	public void addUpdate(int idPlayer, Update data, boolean highPriority) {
		if (!ConnectionManager.getInstance().isConnected(idPlayer))
			return;
		
		getUpdatesByPlayer(idPlayer).add(data);
		
		if (highPriority) {
			long idThread = Thread.currentThread().getId();
			
			List<Integer> players = pendingWakeUps.get(idThread);
			
			if (players == null) {
				players = Collections.synchronizedList(new ArrayList<Integer>());
				pendingWakeUps.put(idThread, players);
			}
			
			if (!players.contains(idPlayer))
				players.add(idPlayer);
		}
	}
	
	public synchronized List<Update> getUpdatesByPlayer(int idPlayer) {
		List<Update> playerUpdates = pendingUpdates.get(idPlayer);
		
		if (playerUpdates == null) {
			playerUpdates = Collections.synchronizedList(
					new LinkedList<Update>());
			pendingUpdates.put(idPlayer, playerUpdates);
		}
		
		return playerUpdates;
	}
	
	// Reveille toutes les sockets des joueurs qui ont une mise à jour en
	// attente sur le thread actif
	public void flushUpdates() {
		long idThread = Thread.currentThread().getId();
		
		List<Integer> players = pendingWakeUps.get(idThread);
		
		if (players != null) {
			synchronized (players) {
				for (Integer idPlayer : players) {
					Player player = DataAccess.getPlayerById(idPlayer);
					Continuation continuation = player.getContinuation();
					if (continuation != null)
						continuation.resume();
				}
			}
			players.clear();
		}
	}
	
	public static UpdateManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
