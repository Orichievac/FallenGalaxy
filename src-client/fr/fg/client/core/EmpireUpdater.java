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

package fr.fg.client.core;

import java.util.HashMap;

import fr.fg.client.ajax.Action;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.CallbackHandler;
import fr.fg.client.openjwt.animation.TimerManager;

public class EmpireUpdater {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int DEFAULT_UPDATE_COUNTDOWN = 1500;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static EmpireUpdater instance = new EmpireUpdater();
	
	private CallbackHandler systemsUpdater, fleetsUpdater;
	
	private UpdaterCallback systemsUpdaterCallback, fleetsUpdaterCallback;
	
	private long lastFleetsUpdate, lastSystemsUpdate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private EmpireUpdater() {
		this.lastSystemsUpdate = 0;
		this.lastFleetsUpdate = 0;
		this.systemsUpdater = null;
		this.fleetsUpdater = null;
		this.systemsUpdaterCallback = new UpdaterCallback(UpdaterCallback.TYPE_SYSTEMS);
		this.fleetsUpdaterCallback = new UpdaterCallback(UpdaterCallback.TYPE_FLEETS);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void queueSystemsUpdate() {
		if (Utilities.getCurrentTime() <= lastSystemsUpdate)
			return;
		
		if (systemsUpdater == null || systemsUpdater.isFinished()) {
			if (systemsUpdaterCallback.currentAction != null &&
					systemsUpdaterCallback.currentAction.isPending()) {
				systemsUpdater = new CallbackHandler(
					systemsUpdaterCallback, DEFAULT_UPDATE_COUNTDOWN);
				TimerManager.registerCallback(systemsUpdater);
			} else {
				systemsUpdaterCallback.run();
			}
		}
	}
	
	public void queueFleetsUpdate() {
		if (Utilities.getCurrentTime() <= lastFleetsUpdate)
			return;
		
		if (fleetsUpdater == null || fleetsUpdater.isFinished()) {
			if (fleetsUpdaterCallback.currentAction != null &&
					fleetsUpdaterCallback.currentAction.isPending()) {
				fleetsUpdater = new CallbackHandler(
					fleetsUpdaterCallback, DEFAULT_UPDATE_COUNTDOWN);
				TimerManager.registerCallback(fleetsUpdater);
			} else {
				fleetsUpdaterCallback.run();
			}
		}
	}
	
	public final static EmpireUpdater getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private class UpdaterCallback implements Callback {
		// --------------------------------------------------- CONSTANTES -- //
		
		public final static int TYPE_SYSTEMS = 0, TYPE_FLEETS = 1;
		
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int type;
		
		private Action currentAction;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public UpdaterCallback(int type) {
			this.type = type;
			this.currentAction = null;
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
		
		public void run() {
			switch (type) {
			case TYPE_SYSTEMS:
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("type", "systems");
				
				lastSystemsUpdate = Utilities.getCurrentTime();
				currentAction = new Action("update", params, UpdateManager.UPDATE_CALLBACK);
				break;
				
			case TYPE_FLEETS:
				params = new HashMap<String, String>();
				params.put("type", "fleets+area");
				
				lastFleetsUpdate = Utilities.getCurrentTime();
				currentAction = new Action("update", params, UpdateManager.UPDATE_CALLBACK);
				break;
			}
		}
	}
}
