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

package fr.fg.client.openjwt.core;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.RootPanel;

public class EventManager implements EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
	  KEY_BACKSPACE = 8,
	  KEY_TAB =       9,
	  KEY_RETURN =   13,
	  KEY_ESC =      27,
	  KEY_LEFT =     37,
	  KEY_UP =       38,
	  KEY_RIGHT =    39,
	  KEY_DOWN =     40,
	  KEY_DELETE =   46,
	  KEY_HOME =     36,
	  KEY_END =      35,
	  KEY_PAGEUP =   33,
	  KEY_PAGEDOWN = 34,
	  KEY_INSERT =   45;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static EventManager instance = new EventManager();
	
	public ArrayList<EventPreview> hooks;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private EventManager() {
		hooks = new ArrayList<EventPreview>();
		DOM.addEventPreview(this);
		
		RootPanel.get().sinkEvents(Event.KEYEVENTS);
		RootPanel.get().sinkEvents(Event.MOUSEEVENTS);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static void addEventHook(EventPreview hook) {
		instance.hooks.add(hook);
	}

	public static void removeEventHook(EventPreview hook) {
		instance.hooks.remove(hook);
	}
	
	public static ArrayList<EventPreview> getEventHooks() {
		return instance.hooks;
	}
	
	public boolean onEventPreview(Event event) {
		for (EventPreview hook : hooks)
			hook.onEventPreview(event);
		
		return true;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
