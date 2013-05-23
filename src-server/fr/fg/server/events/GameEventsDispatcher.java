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

package fr.fg.server.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.util.LoggingSystem;

public class GameEventsDispatcher {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static GameEventsDispatcher instance =
		new GameEventsDispatcher();
	
	private Map<Class<? extends GameEvent>, List<GameEventListener>> listeners;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private GameEventsDispatcher() {
		this.listeners = Collections.synchronizedMap(
			new HashMap<Class<? extends GameEvent>, List<GameEventListener>>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static <T extends GameEvent> void fireGameEvent(T event) throws Exception {
		List<GameEventListener> listeners = getEventListeners(event.getClass());
		
		synchronized (listeners) {
			for (int i = listeners.size() - 1; i >= 0; i--)
				listeners.get(i).onGameEvent(event);
		}
	}
	
	public static <T extends GameEvent> void fireGameNotification(T event) {
		List<GameEventListener> listeners = getEventListeners(event.getClass());
		synchronized (listeners) {
			for (int i = listeners.size() - 1; i >= 0; i--)
				try {
					listeners.get(i).onGameEvent(event);
				} catch (Exception e) {
					LoggingSystem.getServerLogger().warn("Unexpected event exception.", e);
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addGameEventListener(GameEventListener listener,
			Class... eventClasses) {
		for (Class<? extends GameEvent> eventClass : eventClasses)
			getEventListeners(eventClass).add(listener);
	}
	
	@SuppressWarnings("unchecked")
	public static void removeGameEventListener(GameEventListener listener,
			Class... eventClasses) {
		for (Class<? extends GameEvent> eventClass : eventClasses)
			getEventListeners(eventClass).remove(listener);
	}
	
	public static void removeGameEventListener(GameEventListener listener) {
		synchronized (instance.listeners) {
			for (List<GameEventListener> listeners : instance.listeners.values())
				listeners.remove(listener);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static <T extends GameEvent> List<GameEventListener> getEventListeners(Class<T> c) {
		synchronized (instance.listeners) {
			if (instance.listeners.get(c) == null) {
				instance.listeners.put(c,
					Collections.synchronizedList(
							new ArrayList<GameEventListener>()));
			}
			
			return instance.listeners.get(c);
		}
	}
}
