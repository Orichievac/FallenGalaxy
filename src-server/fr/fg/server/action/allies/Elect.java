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

package fr.fg.server.action.allies;

import java.util.Map;


import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Election;
import fr.fg.server.data.ElectionVoter;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Elect extends Action {

	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {

		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartanez à aucune alliance.");
		
		Player changed = DataAccess.getPlayerById((Integer)params.get("id"));
		
		if( changed.getIdAlly()!=player.getIdAlly() )
			throw new IllegalOperationException("Ce joueur ne fait pas partie de votre alliance.");
		
		if( player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_ELECT) )
			throw new IllegalOperationException("Vous n'avez pas les droits nécessaires pour élire un membre.");
		
		Election election = DataAccess.getElectionByAlly(ally.getId());
		
		if( election==null )
			throw new IllegalOperationException("Il n'y a aucune élection en cours dans votre alliance.");
		
		if( player.hasVotedElection(election.getId()) )
			throw new IllegalOperationException("Vous avez déjà voté pour cette élection.");
		
		ElectionVoter voter = new ElectionVoter(election.getId(),player.getId(),(Integer)params.get("id"));
		voter.save();
		
		return FORWARD_SUCCESS;
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
