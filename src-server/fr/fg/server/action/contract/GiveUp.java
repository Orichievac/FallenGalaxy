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

import fr.fg.server.contract.ContractHelper;
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

public class GiveUp extends Action {
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
		
		// Vérifie que le contrat est en cours
		if (!contract.getState().equals(Contract.STATE_RUNNING))
			throw new IllegalOperationException("Mission invalide.");
		
		// Vérifie que le joueur est affecté sur le contrat
		boolean found = false;
		
		if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
			List<ContractAttendee> attendees = contract.getAttendees();
			for (ContractAttendee attendee : attendees) {
				if (attendee.getIdPlayer() == player.getId()) {
					attendee.delete();
					found = true;
					
					// Applique les modificateurs de relations
					ContractHelper.applyRelationships(contract, player, false);
					break;
				}
			}
		} else if (contract.getTarget().equals(Contract.TARGET_ALLY)) {
			Ally ally = player.getAlly();
			if (ally == null)
				throw new IllegalOperationException("Vous n'avez pas d'alliance.");
			
			if (player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_MANAGE_CONTRACTS))
				throw new IllegalOperationException("Vous n'avez pas les " +
					"droits nécessaires pour effectuer cette action.");
			
			List<ContractAttendee> attendees = contract.getAttendees();
			for (ContractAttendee attendee : attendees) {
				if (attendee.getIdAlly() == ally.getId()) {
					attendee.delete();
					found = true;
					
					// Applique les modificateurs de relations
					ContractHelper.applyRelationships(contract, ally, false);
					break;
				}
			}
		} else if (contract.getTarget().equals(Contract.TARGET_ALL)) {
			throw new IllegalOperationException("Mission invalide.");
		}
		
		if (!found)
			throw new IllegalOperationException("Mission invalide.");
		
		// Supprime le contrat s'il s'agit d'un contrat solo
		if (contract.getMaxAttendees() == 1)
			contract.delete();
		
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
