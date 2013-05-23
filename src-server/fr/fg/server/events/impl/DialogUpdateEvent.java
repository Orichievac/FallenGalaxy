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

import fr.fg.server.data.Contract;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.events.GameEvent;

public class DialogUpdateEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long idContract;
	private int idPlayer;
	private int idSourceFleet;
	private int idTargetFleet;
	private String previousEntry;
	private String targetEntry;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DialogUpdateEvent(long idContract, int idPlayer, int idSourceFleet,
			int idTargetFleet, String previousEntry, String targetEntry) {
		this.idContract = idContract;
		this.idPlayer = idPlayer;
		this.idSourceFleet = idSourceFleet;
		this.idTargetFleet = idTargetFleet;
		this.previousEntry = previousEntry;
		this.targetEntry = targetEntry;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public long getIdContract() {
		return idContract;
	}
	
	public Contract getContract() {
		return DataAccess.getContractById(idContract);
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public Player getPlayer() {
		return DataAccess.getPlayerById(idPlayer);
	}
	
	public int getIdSourceFleet() {
		return idSourceFleet;
	}
	
	public Fleet getSourceFleet() {
		return DataAccess.getFleetById(idSourceFleet);
	}
	
	public int getIdTargetFleet() {
		return idTargetFleet;
	}
	
	public Fleet getTargetFleet() {
		return DataAccess.getFleetById(idTargetFleet);
	}
	
	public String getPreviousEntry() {
		return previousEntry;
	}
	
	public String getTargetEntry() {
		return targetEntry;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
