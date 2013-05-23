/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.AllyTools;
import fr.fg.server.core.ChatManager;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Election;
import fr.fg.server.data.ElectionVoter;
import fr.fg.server.data.Player;
import fr.fg.server.util.Utilities;

public class ApplyVotes extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void run() {
		this.setName("ApplyVote (daily)");
		List<Integer> toDelete = new ArrayList<Integer>();
		
		List<Election> elections = new ArrayList<Election>(DataAccess.getAllElections());
		
		synchronized (elections) {
			for(Election election:elections){
				if(election.isEnded()){
					List<ElectionVoter> votes = new ArrayList<ElectionVoter>(DataAccess.getElectionVoterById(election.getId()));
					
					Map<Integer,Integer> candidates = new HashMap<Integer,Integer>();
					
					if(votes.size()!=0){
						for(ElectionVoter vote:votes){
							if( candidates.containsKey(vote.getIdCandidate()) )
								candidates.put(vote.getIdCandidate(), candidates.get(vote.getIdCandidate())+1);
							else
								candidates.put(vote.getIdCandidate(), 1);
						}
						
						int max = 0;
						int elu = 0;
						for(int key:candidates.keySet()){
							if(candidates.get(key)>max){
								max = candidates.get(key);
								elu = key;
							}
						}
						
						List<Player> leaders = election.getAlly().getLeaders();
						if (leaders.size() > 0 && leaders.get(0).getId() == elu) {
							AllyTools.publishElectionResult(election);
							DataAccess.getElectionById(election.getId()).delete();
							continue;
						}
						
						//On démet tous les ministres si le président change
						if( election.getAlly().getOrganization().equals("democracy") ){
							List<Player> members = new ArrayList<Player>(election.getAlly().getMembers());
							
							for(Player member:members){
								if(member.getAllyRank()==2)
									synchronized (member.getLock()) {
										Player newCitizen = DataAccess.getEditable(member);
										newCitizen.setAllyRank(1);
										DataAccess.save(newCitizen);
									}
							}
						}
						
						if (leaders.size() > 0) {
							Player leader = leaders.get(0);
							synchronized (leader.getLock()) {
								Player newTemp = DataAccess.getEditable(leader);
								newTemp.setAllyRank(election.getAlly().getLeaderRank()-1);
								DataAccess.save(newTemp);
							}
						}
						
						Player temp = DataAccess.getPlayerById(elu);
						synchronized (temp.getLock()) {
							Player newTemp = DataAccess.getEditable(temp);
							newTemp.setAllyRank(election.getAlly().getLeaderRank());
							DataAccess.save(newTemp);
						}
						
						AllyTools.publishElectionResult(election);
						DataAccess.getElectionById(election.getId()).delete();
					}
				}
			}
		}
		
		List<AllyVote> votes = DataAccess.getAllAllyVotes();
		
		toDelete.clear();
		
		if(votes!=null){
			synchronized (votes) {
				for(AllyVote vote:votes){
					if(vote.isEnded()){
						
						toDelete.add(vote.getId());
						
						if (vote.getType().equals(AllyVote.TYPE_KICK) &&
								vote.getYes() > vote.getNo()) {
							List<Player> members = vote.getAlly().getMembers();
							
							Player kicked = vote.getPlayer();
							
							// Quitte les canaux de discussion de l'alliance
							ChatManager.getInstance().leaveAllyChannels(kicked);
							
							synchronized (kicked.getLock()) {
								kicked = DataAccess.getEditable(kicked);
								kicked.setIdAlly(0);
								kicked.setAllyRank(0);
								kicked.setLastAllyIn(Utilities.now());
								kicked.save();
							}
							
							// Vérifie les liens entre flottes des membres de l'alliance
							synchronized (members) {
								for (Player member : members)
									FleetTools.checkLinks(member);
							}
							
							// Met à jour l'influence de l'alliance
							vote.getAlly().updateInfluences();
							
							// Mise à jour des secteurs consultés pour les joueurs
							// de l'alliance, afin que le membre éjecté soit pris
							// en compte, au besoin
							UpdateTools.queueAllyUpdate(kicked.getId(), 0, false);
							UpdateTools.queueChatChannelsUpdate(kicked.getId(), false);
							UpdateTools.queueAreaUpdate(kicked);
							
							UpdateTools.queueAreaUpdate(vote.getAlly().getMembers());
						}
						
						if (vote.getType().equals(AllyVote.TYPE_ACCEPT) &&
								vote.getYes() > vote.getNo()) {
							Player accepted = vote.getPlayer();
							
							Applicant application =
								DataAccess.getApplicantByPlayer(accepted.getId());
							
							if (application != null &&
									application.getIdAlly() == vote.getIdAlly()) {
								synchronized (accepted.getLock()) {
									accepted = DataAccess.getEditable(accepted);
									accepted.setIdAlly(application.getIdAlly());
									accepted.setAllyRank(1);
									accepted.save();
								}
								
								application.delete();
								
								// Rejoint les canaux de discussion de l'alliance
								ChatManager.getInstance().joinAllyChannels(accepted);
								
								// Met à jour l'influence de l'alliance
								vote.getAlly().updateInfluences();
							}
							
							// Mise à jour des secteurs consultés pour les joueurs
							// de l'alliance, afin que le nouveau membre soit pris en
							// compte, au besoin
							UpdateTools.queueAllyUpdate(accepted.getId(), 0, false);
							UpdateTools.queueChatChannelsUpdate(accepted.getId(), false);
							
							UpdateTools.queueAreaUpdate(vote.getAlly().getMembers());
						} else {
							Applicant applicant = DataAccess.getApplicantByPlayer(vote.getIdPlayer());
							
							if (applicant != null)
								applicant.delete();
						}
						
						AllyTools.publishVoteResult(vote);
					}
				}
				
				for(Integer delete:toDelete){
					AllyVote vote = DataAccess.getAllyVoteById(delete);
					if( vote!=null ) vote.delete();
				}
			}
		}

	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
