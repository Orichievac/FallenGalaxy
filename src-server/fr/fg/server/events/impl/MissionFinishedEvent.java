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

package fr.fg.server.events.impl;

import fr.fg.server.data.Player;
import fr.fg.server.events.GameEvent;

public class MissionFinishedEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Player player;
	
	private boolean success;
	
	private int missionId;

	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public MissionFinishedEvent(Player player, boolean success, int missionId) {
		super();
		this.player = player;
		this.success = success;
		this.missionId = missionId;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public Player getPlayer() {
		return player;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getMissionId() {
		return missionId;
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
