/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier, Bosc Nicolas

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

package fr.fg.server.task.daily;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Election;
import fr.fg.server.data.Event;
import fr.fg.server.util.Utilities;

public class OrganizeElection extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("OrganizeElection (daily)");
		List<Ally> allies = new ArrayList<Ally>(DataAccess.getAllAllies());
		
		for(Ally ally:allies){
			if(!ally.getOrganization().equals(Ally.ORGANIZATION_DEMOCRACY) && 
					!ally.getOrganization().equals(Ally.ORGANIZATION_WARMONGER)) continue;
			
			if(ally.getDaysFromCreation()%Election.FREQUENCY==0 &&
					ally.getDaysFromCreation()!=0 &&
					DataAccess.getElectionByAlly(ally.getId())==null){
				DataAccess.save(new Election(Utilities.now(),ally.getId()));
			
			
			//TODO bmoyet Event : Avertir l'alliance du démarrage du vote
			Event event = new Event(
					Event.EVENT_ELECTION_START,
					Event.TARGET_ALLY,
					ally.getId(), 
					0, 
					-1, 
					-1
					);
				event.save();
				
				UpdateTools.queueNewEventUpdate(ally.getMembers(), false);

			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
