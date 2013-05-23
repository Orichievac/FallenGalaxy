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

import java.util.List;
import java.util.Map;



import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.AllyVoter;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class VoteAccept extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		
		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		Player toAccept = DataAccess.getPlayerById((Integer)params.get("id"));
		
		if( toAccept.getAlly()!=null || DataAccess.getApplicantByPlayer(toAccept.getId())==null )
			throw new IllegalOperationException("Ce joueur ne postule pas à votre alliance.");
		
		if( player.getAllyRank()<ally.getRequiredRank(Ally.RIGHT_VOTE_ACCEPT) )
			throw new IllegalOperationException("Vous n'avez pas les droits suffisants pour voter.");
		
		AllyVote vote = DataAccess.getAllyVoteByTarget(toAccept.getId());
		
		JSONStringer json = new JSONStringer();
		
		json.object();
		
		if( vote != null ){
			List<AllyVoter> listVotes = DataAccess.getVoteVoter(vote.getId());
			synchronized (listVotes) {
				for(AllyVoter votes:listVotes){
					if(votes.getIdPlayer()==player.getId())
						throw new IllegalOperationException("Vous avez déjà voté pour ce vote.");
				}
			}
			
			synchronized (vote.getLock()) {
				AllyVote newVote = DataAccess.getEditable(vote);
				if( ((String)params.get("vote")).equals("yes") )
					newVote.setYes(newVote.getYes()+1);
				else
					newVote.setNo(newVote.getNo()+1);
				DataAccess.save(newVote);
			}
		}
		else{
			if( ((String)params.get("vote")).equals("yes") ){
				vote = new AllyVote(AllyVote.TYPE_ACCEPT,1,0,Utilities.today(),toAccept.getId(),ally.getId(),player.getId());
				DataAccess.save(vote);
			}
			else{
				vote = new AllyVote(AllyVote.TYPE_ACCEPT,0,1,Utilities.today(),toAccept.getId(),ally.getId(),player.getId());
				DataAccess.save(vote);	
			}
			json.	key("player").	value(toAccept.getId());
			
			DataAccess.save(new Event(Event.EVENT_ALLY_NEW_VOTEACCEPT,Event.TARGET_ALLY,ally.getId(),
					0,-1,-1,player.getLogin(),toAccept.getLogin()));
		}
		
		DataAccess.save( new AllyVoter(vote.getId(),player.getId()) );
		
		
		json.	key("id").	value(vote.getId()).
			endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
