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

package fr.fg.server.action.contract;

import java.util.List;
import java.util.Map;

import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Apply extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		long idContract = (Long) params.get("id");
		
		Contract contract = DataAccess.getContractById(idContract);

		// Vérifie que le contrat existe
		if (contract == null)
			throw new IllegalOperationException("Mission inexistante.");

		// Vérifie que le contrat est en attente
		if (!contract.getState().equals(Contract.STATE_WAITING))
			throw new IllegalOperationException("Mission invalide.");

		// Vérifie que le joueur est affecté sur le contrat
		if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
			List<ContractAttendee> attendees = contract.getAttendees();
			ContractAttendee playerAttendee = null;
			
			for (ContractAttendee attendee : attendees) {
				if (attendee.getIdPlayer() == player.getId()) {
					if (attendee.isRegistered())
						throw new IllegalOperationException("Vous êtes déjà inscrit à cette mission.");
					
					playerAttendee = attendee;
					break;
				}
			}
			
			if (playerAttendee == null)
				throw new IllegalOperationException("Mission invalide.");
			
			synchronized (playerAttendee.getLock()) {
				playerAttendee = DataAccess.getEditable(playerAttendee);
				playerAttendee.setRegistered(true);
				playerAttendee.save();
			}
		} else if (contract.getTarget().equals(Contract.TARGET_ALLY)) {
			Ally ally = player.getAlly();
			if (ally == null)
				throw new IllegalOperationException("Vous n'avez pas d'alliance.");
			
			if (player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_MANAGE_CONTRACTS))
				throw new IllegalOperationException("Vous n'avez pas les " +
					"droits nécessaires pour effectuer cette action.");
			
			List<ContractAttendee> attendees = contract.getAttendees();
			ContractAttendee allyAttendee = null;
			
			for (ContractAttendee attendee : attendees) {
				if (attendee.getIdAlly() == ally.getId()) {
					if (attendee.isRegistered())
						throw new IllegalOperationException("Votre alliance est déjà inscrite à cette mission.");
					
					allyAttendee = attendee;
					break;
				}
			}
			
			if (allyAttendee == null)
				throw new IllegalOperationException("Mission invalide.");
			
			synchronized (allyAttendee.getLock()) {
				allyAttendee = DataAccess.getEditable(allyAttendee);
				allyAttendee.setRegistered(true);
				allyAttendee.save();
			}
		} else if (contract.getTarget().equals(Contract.TARGET_ALL)) {
			throw new IllegalOperationException("Mission invalide.");
		}
		
		// Démarre le contrat si c'est un contrat solo
		if (contract.getMaxAttendees() == 1) {
			synchronized (contract.getLock()) {

				contract = DataAccess.getEditable(contract);
				contract.setRunningState();
				contract.save();

			}
		}
		//Début du contrat PvP
		else
		{
			List<ContractAttendee> attendees = contract.getAttendees();
			int numberOfAttendees = 0;
			
			for (ContractAttendee attendee : attendees) {
				
				if (attendee.isRegistered()) {
					numberOfAttendees++;
				}
			}
			
			 if(numberOfAttendees>=contract.getMaxAttendees()){
				 
					for (ContractAttendee attendee : attendees) {
						
						if (!attendee.isRegistered()) {
							attendees.remove(attendee);
							synchronized (attendee.getLock()) {
								attendee = DataAccess.getEditable(attendee);
								attendee.delete();
							}
						}
					}
					
					
				synchronized (contract.getLock()) {
	
					contract = DataAccess.getEditable(contract);
					contract.setRunningState();
					contract.save();
	
				}
			 }
		}
		
		if (contract.getTarget().equals(Contract.TARGET_ALLY)) {
			List<Player> members = player.getAlly().getMembers();
			
			synchronized (members) {
				for (Player member : members)
					UpdateTools.queueContractsUpdate(member.getId());
			}
		}

		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerContractsUpdate(),
			Update.getContractStateUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
