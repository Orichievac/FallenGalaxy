/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.data.Structure;
import fr.fg.server.core.ChatManager;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Election;
import fr.fg.server.data.ElectionVoter;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class Leave extends Action {

	
	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {

		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		if (ally.getMembers().size() == 1) {
			// Quitte les canaux de discussion de l'alliance
			ChatManager.getInstance().leaveAllyChannels(player);
			
			synchronized (player.getLock()) {
				player = DataAccess.getEditable(player);
				player.setIdAlly(0);
				player.setAllyRank(0);
				player.setLastAllyIn(Utilities.now());
				player.save();
			}
			
			List<AllyTreaty> treaties = new ArrayList<AllyTreaty>(ally.getTreaties());
			ally.delete();
			
			// Mise à jour des influences dans le quadrant
			List<Sector> sectors = new ArrayList<Sector>(DataAccess.getAllSectors());
			for (Sector sector : sectors)
				sector.updateInfluences();
			
			// Mise à jour du secteur consulté par le joueur
			UpdateTools.queueAreaUpdate(player);
			
			// Mise à jour des joueurs appartenant à des alliances avec qui
			// l'alliance détruite avait passé des traités
			synchronized (treaties) {
				for (AllyTreaty treaty : treaties) {
					UpdateTools.queueAreaUpdate(
						treaty.getOtherAlly(ally.getId()).getMembers());
				}
			}
			

			
			return UpdateTools.formatUpdates(
				player,
				Update.getAllyUpdate(0),
				Update.getChatChannelsUpdate(),
				Update.getAreaUpdate()
			);
		}
		
		
		Player newLeader = DataAccess.getPlayerByLogin((String)params.get("newleader"));
		
		List<Player> members = ally.getMembers();
		
		//calcul des energysupplier coté ally
		synchronized (members){
			for (Player member : members){
				if(member!=player){
				List<Structure> MemberStruct= new ArrayList<Structure>( member.getStructures());
				for (Structure structures : MemberStruct){
					long IdSource = structures.getIdEnergySupplierStructure();
					if(IdSource!=0){
					Player OwnerSource=DataAccess.getStructureById(IdSource).getOwner();
					if(OwnerSource==player){
						synchronized(structures.getLock()){
							structures = DataAccess.getEditable(structures);
							structures.setIdEnergySupplierStructure(0);
							structures.save();
						}
					}
						}
					}
				}
			}
		}
		
		//calcul des energysupplier coté joueur
		List<Structure> LeaverStruct= new ArrayList<Structure>(player.getStructures());
		for (Structure structures : LeaverStruct){
			long IdSource = structures.getIdEnergySupplierStructure();
			if(IdSource!=0){
			Player OwnerSource=DataAccess.getStructureById(IdSource).getOwner();
			if(OwnerSource!=player){
				synchronized(structures.getLock()){
					structures = DataAccess.getEditable(structures);
					structures.setIdEnergySupplierStructure(0);
					structures.save();
				}
			}
			}
		}
		
		if( !ally.getOrganization().equals(Ally.ORGANIZATION_DEMOCRACY) && 
				!ally.getOrganization().equals(Ally.ORGANIZATION_WARMONGER) ) {
			int leadersCount = 0;
			
			synchronized (members) {
				for (Player member : members)
					if (member.getAllyRank() == ally.getLeaderRank())
						leadersCount++;
			}
			
			if (leadersCount <= 1 && player.getAllyRank() == ally.getLeaderRank() && newLeader == null)
				throw new IllegalOperationException("Vous devez choisir un nouveau leader.");
			
			if (newLeader != null)
				if (newLeader.getIdAlly() != player.getIdAlly())
					throw new IllegalOperationException("Le joueur choisi doit appartenir à l'alliance.");
			
			AllyVote allyVote = DataAccess.getAllyVoteByTarget(player.getId());
			if (allyVote != null)
				allyVote.delete();
			
			Election election = DataAccess.getElectionByAlly(ally.getId());
			
			if( election!=null ){
				List<ElectionVoter> votes = DataAccess.getElectionVoterById(election.getId());
				synchronized (votes) {
					for( ElectionVoter vote : votes ){
						if( vote.getIdCandidate()==player.getId() )
							vote.delete();
					}
				}				
			}
		} else {
			// TODO bmoyet warmonger - s'il n'y pas d'état major ?
			AllyVote voteKick = DataAccess.getAllyVoteByTarget(player.getId());
			
			if(voteKick!=null)
				voteKick.delete();
			
			Election election = DataAccess.getElectionByAlly(ally.getId());
			
			if( election!=null ){
				List<ElectionVoter> votes = DataAccess.getElectionVoterById(election.getId());
				synchronized (votes) {
					for( ElectionVoter vote : votes ){
						if( vote.getIdCandidate()==player.getId() )
							vote.delete();
					}					
				}				
			} else {
				election = new Election(Utilities.now(),ally.getId());
				election.save();
			}
		}
		
		// Quitte les canaux de discussion de l'alliance
		ChatManager.getInstance().leaveAllyChannels(player);
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.setIdAlly(0);
			player.setAllyRank(0);
			player.setLastAllyIn(Utilities.now());
			player.save();
		}
		
		ally.updateInfluences();
		
		// Vérifie les liens entre flottes des membres de l'alliance
		synchronized (members) {
			for (Player member : members)
				FleetTools.checkLinks(member);
		}
		
		Event event = new Event(Event.EVENT_ALLY_MEMBER_LEFT,
			Event.TARGET_ALLY, ally.getId(), 0, -1, -1, player.getLogin());
		event.save();
		
		// Mise à jour des secteurs consultés pour les joueurs de l'alliance,
		// afin que le départ du membre soit pris en compte, au besoin
		UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
		
		UpdateTools.queueAreaUpdate(ally.getMembers());
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(0),
			Update.getChatChannelsUpdate(),
			Update.getAreaUpdate()
		);
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
