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

package fr.fg.client.openjwt.animation;

import fr.fg.client.core.Utilities;

public class CallbackHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Callback callback;
	
	private long triggerTime;
	
	private boolean finished;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public CallbackHandler(Callback callback, long remainingTime) {
		this.callback = callback;
		this.triggerTime = 1000 * Utilities.getCurrentTime() + remainingTime;
		this.finished = false;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Callback getCallback() {
		return callback;
	}
	
	public long getTriggerTime() {
		return triggerTime;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	protected void setFinished(boolean finished) {
		this.finished = finished;
	}
}
