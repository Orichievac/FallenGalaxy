/*
Copyright 2010 Nicolas Bosc

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

package fr.fg.server.admin.impl;

import java.util.ArrayList;
import java.util.List;

import fr.fg.client.data.ScriptData;
import fr.fg.server.admin.ScriptModel;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;

public class DebugContracts extends ScriptModel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	@Override
	public JSONStringer execute() {
		
		LoggingSystem.getActionLogger().info(this.getClass().getName()+" script launched");
		String msg = new String();
		String logger = new String();
		
		
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		// Test des attendees qu n'ont pas de contract attaché
		for(Player player : players){
			
			List<ContractAttendee> attendees=new ArrayList<ContractAttendee>(DataAccess.getAttendeesByPlayer(player));
		
			for (ContractAttendee attendee : attendees) {
				if(DataAccess.getContractById(attendee.getIdContract())==null)
				{
					logger="Un contrat ("+attendee.getIdContract()+") n'existe pas.";
					LoggingSystem.getActionLogger().info(logger);
					msg+=logger+"<br/>";
					
					attendee.delete();
				}
			}
			
		}
		
		// Test des contracts qui n'ont aucun attendees
		List<Contract> contracts = new ArrayList<Contract>(DataAccess.getAllContracts());
		for(Contract contract : contracts)
		{
			if(DataAccess.getAttendeesByContract(contract.getId()).size()==0)
			{
				logger="Un contrat ("+contract.getId()+") n'a pas de participant.";
				LoggingSystem.getActionLogger().info(logger);
				msg+=logger+"<br/>";
				contract.delete();
			}
		}
		
		msg+="Script terminé avec succès.";
		
		JSONStringer json = new JSONStringer();
		
		
		json.object().
		key(ScriptData.FIELD_MESSAGE)			.value(msg).
		key(ScriptData.FIELD_NAME)				.value(this.getClass().getName()).
		endObject();
		
		LoggingSystem.getActionLogger().info(this.getClass().getName()+" script finish");
		
		return json;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
