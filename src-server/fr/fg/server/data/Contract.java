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

package fr.fg.server.data;

import java.util.List;

import fr.fg.server.data.base.ContractBase;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.ContractStateUpdateEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Contract extends ContractBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
	TYPE_SOLO = "player",
	TYPE_PVP = "pvp",
	TYPE_MULTIPLAYER = "multiplayer",
	TYPE_GVG = "groupe_vs_groupe",
	TYPE_MULTIALLY = "multially",
	TYPE_AVA = "ava",
	TYPE_ALLY_SOLO = "ally",
	TYPE_ALL = "all";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Contract() {
		// Nécessaire pour la construction par réflection
	}

	public Contract(String type, String variant, String target,
			int maxAttendees, long difficulty) {
		setType(type);
		setVariant(variant);
		setMaxAttendees(maxAttendees);
		setState(STATE_WAITING);
		setStep(0);
		setTarget(target);
		setDifficulty(difficulty);
		setStateDate(Utilities.now());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isAttendee(Player player) {
		if (getTarget().equals(TARGET_PLAYER)) {
			// Contrat entre joueurs
			List<ContractAttendee> attendees = getAttendees();
			
			synchronized (attendees) {
				for (ContractAttendee attendee : attendees)
					if (attendee.getIdPlayer() == player.getId())
						return true;
			}
			
			return false;
		} else if (getTarget().equals(TARGET_ALLY)) {
			// Contrat entre alliances
			if (player.getIdAlly() == 0)
				return false;
			
			List<ContractAttendee> attendees = getAttendees();
			
			synchronized (attendees) {
				for (ContractAttendee attendee : attendees)
					if (attendee.getIdAlly() == player.getIdAlly())
						return true;
			}
			
			return false;
		} else if (getTarget().equals(TARGET_ALL)) {
			return true;
		}
		return false;
	}
	
	public List<ContractArea> getAreas() {
		return DataAccess.getAreasByContract(getId());
	}
	
	public List<ContractAttendee> getAttendees() {
		return DataAccess.getAttendeesByContract(getId());
	}
	
	public List<ContractReward> getRewards() {
		return DataAccess.getRewardsByContract(getId());
	}

	public List<ContractRelationship> getRelationships() {
		return DataAccess.getRelationshipsByContract(getId());
	}
	
	public void setRunningState() {
		if (getState() != STATE_WAITING)
			throw new IllegalArgumentException("Etat invalide.");

		String oldState = getState();
		setState(STATE_RUNNING);
		setStateDate(Utilities.now());
		GameEventsDispatcher.fireGameNotification(
			new ContractStateUpdateEvent(getId(), oldState, getState()));
	}
	
	public void setFinalizingState() {
		if (getState() != STATE_RUNNING)
			throw new IllegalArgumentException("Etat invalide.");
		
		String oldState = getState();
		
		setState(STATE_FINALIZING);
		setStateDate(Utilities.now());
		
		GameEventsDispatcher.fireGameNotification(
			new ContractStateUpdateEvent(getId(), oldState, getState()));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
