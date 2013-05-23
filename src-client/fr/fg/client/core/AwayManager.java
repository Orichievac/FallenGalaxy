/*
Copyright 2011 jgottero

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

package fr.fg.client.core;

import java.util.HashMap;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;

import fr.fg.client.ajax.Action;
import fr.fg.client.core.Utilities;
import fr.fg.client.openjwt.animation.PeriodicAnimation;
import fr.fg.client.openjwt.core.EventManager;

public class AwayManager implements EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static AwayManager INSTANCE = new AwayManager();
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int inactivity;
	
	private boolean away;
	
	private AwayStateUpdater updater;
	
	private long lastEventDate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AwayManager() {
		inactivity = 0;
		away = false;
		updater = new AwayStateUpdater();

		EventManager.addEventHook(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	

	public boolean onEventPreview(Event event) {
		if (event.getTypeInt() == Event.ONMOUSEMOVE ||
				event.getTypeInt() == Event.ONKEYDOWN) {
			lastEventDate = Utilities.getCurrentTime();
			
			if (away) {
				inactivity = 0;
				away = false;
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("away", "false");
				new Action("setaway", params);
			}
		}
		return true;
	}
	
	public static void start() {
		INSTANCE.updater.run(1000);
	}
	
	public static void stop() {
		INSTANCE.updater.cancel();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	private class AwayStateUpdater extends PeriodicAnimation {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public AwayStateUpdater() {
			super(60000);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		
		protected void onPeriodicUpdate(double elapsedTime) {
			if (lastEventDate + 60 < Utilities.getCurrentTime()) {
				inactivity++;
				
				if (inactivity >= 4) {
					if (!away) {
						away = true;
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("away", "true");
						new Action("setaway", params);
					}
				}
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}