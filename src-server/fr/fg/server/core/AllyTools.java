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

package fr.fg.server.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import fr.fg.client.data.AllyApplicantData;
import fr.fg.client.data.AllyData;
import fr.fg.client.data.AllyDescriptionData;
import fr.fg.client.data.AllyMemberData;
import fr.fg.client.data.AllyMemberFleetData;
import fr.fg.client.data.AllyNewsData;
import fr.fg.client.data.AllyVoteData;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Election;
import fr.fg.server.data.ElectionVoter;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Treaty;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class AllyTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getAllyDescription(JSONStringer json, Ally ally) {
		if (json == null)
			json = new JSONStringer();
		
		json.object().
			key(AllyDescriptionData.FIELD_NAME).		value(ally.getName()).
			key(AllyDescriptionData.FIELD_TAG).			value(ally.getTag()).
			key(AllyDescriptionData.FIELD_FOUNDER).		value(ally.getCreatorName()).
			key(AllyDescriptionData.FIELD_DESCRIPTION).	value(ally.getDescription()).
			key(AllyDescriptionData.FIELD_LEADERS).		array();
		
		List<Player> members = ally.getMembers();
		int leaderRank = ally.getLeaderRank();
		
		synchronized (members) {
			for (Player member : members) {
				if (member.getAllyRank() == leaderRank)
					json.value(member.getLogin());
			}
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	public static JSONStringer getAlly(JSONStringer json, Player player,
			long lastUpdate) throws Exception {
		if (json == null)
			json = new JSONStringer();
		
		Ally ally = player.getAlly();
		
		// Détermine si le joueur a une alliance
		if (ally == null) {
			Applicant application = DataAccess.getApplicantByPlayer(player.getId());
			
			if (application != null) {
				// Détermine si la candidature est en train d'être votée
				List<AllyVote> votes = DataAccess.getAllyVotesByAlly(application.getIdAlly());
				boolean applicationVote = false;
				
				synchronized (votes) {
					for (AllyVote vote : votes) {
						if (vote.getType().equals(AllyVote.TYPE_ACCEPT) &&
								vote.getIdPlayer() == player.getId())
							applicationVote = true;
					}
				}
				
				json.object().
					key(AllyData.FIELD_ID).					value(0).
					key(AllyData.FIELD_NAME).				value(application.getAlly().getName()).
					key(AllyData.FIELD_APPLICANT).			value(true).
					key(AllyData.FIELD_APPLICATION_VOTE).	value(applicationVote).
					key(AllyData.FIELD_LAST_UPDATE).		value(Utilities.now()).
				endObject();
			} else {
				json.object().
					key(AllyData.FIELD_ID).				value(0).
					key(AllyData.FIELD_APPLICANT).		value(false).
					key(AllyData.FIELD_LAST_UPDATE).	value(Utilities.now()).
				endObject();
			}
			
			return json;
		}
		
		Player founder = DataAccess.getPlayerById(ally.getIdCreator());
		
		json.object().
			key(AllyData.FIELD_ID).				value(ally.getId()).
			key(AllyData.FIELD_NAME).			value(ally.getName()).
			key(AllyData.FIELD_APPLICANT).		value(false).
			key(AllyData.FIELD_ORGANIZATION).	value(ally.getOrganization()).
			key(AllyData.FIELD_DESCRIPTION).	value(ally.getDescription()).
			key(AllyData.FIELD_FOUNDER).		value(founder != null ? founder.getLogin() : "").
			key(AllyData.FIELD_BIRTHDATE).		value(ally.getBirthdate()).
			key(AllyData.FIELD_TERRITORY_COLOR).value(ally.getColor()).
			key(AllyData.FIELD_LAST_UPDATE).	value(Utilities.now());
		
		// News
		List<AllyNews> news = new ArrayList<AllyNews>(DataAccess.getAllyNewsByAlly(ally.getId()));
		
		json.key(AllyData.FIELD_NEWS).array();
		for (AllyNews an : news) {
			if (an.getDate() > lastUpdate) {
				Player author = DataAccess.getPlayerById(an.getIdAuthor());
				
				boolean read;
				int idApplicant;
				
				if (an.getIdParent() == 0) {
					read = DataAccess.getAllyNewsReadByNewsAndPlayer(
							an.getId(), player.getId()) != null;
					idApplicant = an.getIdApplicant();
				} else {
					AllyNews parent = DataAccess.getAllyNewsById(an.getIdParent());
					
					if (parent != null) {
						read = DataAccess.getAllyNewsReadByNewsAndPlayer(
								parent.getId(), player.getId()) != null;
						idApplicant = parent.getIdApplicant();
					} else {
						read = true;
						idApplicant = 0;
					}
				}
				
				json.object().
					key(AllyNewsData.FIELD_ID).				value(an.getId()).
					key(AllyNewsData.FIELD_TITLE).			value(an.getTitle()).
					key(AllyNewsData.FIELD_CONTENT).		value(an.getContent()).
					key(AllyNewsData.FIELD_DATE).			value(an.getDate()).
					key(AllyNewsData.FIELD_PARENT).			value(an.getIdParent()).
					key(AllyNewsData.FIELD_STICKY).			value(an.isSticky()).
					key(AllyNewsData.FIELD_ID_APPLICANT).	value(idApplicant).
					key(AllyNewsData.FIELD_AUTHOR).			value(author == null ? "" : author.getLogin()).
					key(AllyNewsData.FIELD_ALLY_TAG).		value(author == null || author.getIdAlly() == 0 ? "" : author.getAlly().getTag()).
					key(AllyNewsData.FIELD_TREATY).			value(author == null ? Treaty.NEUTRAL : author.getTreatyWithPlayer(player)).
					key(AllyNewsData.FIELD_READ).			value(read).
					endObject();
			}
		}
		json.endArray();
		
		List<Player> members = new ArrayList<Player>(ally.getMembers());
		
		long now = Utilities.now();
		json.key(AllyData.FIELD_MEMBERS).array();
		for(Player member : members){
			int idleTime = 0;
			long lastPing = Math.max(member.getLastConnection(),
					member.getLastPing());
			if (now - lastPing > 3 * 3600 * 24) // 3 jours
				idleTime = (int) (now - lastPing) / (3600 * 24);
			
			json.object().
				key(AllyMemberData.FIELD_ID).			value(member.getId()).
				key(AllyMemberData.FIELD_LOGIN).		value(member.getLogin()).
				key(AllyMemberData.FIELD_RANK).			value(member.getAllyRank()).
				key(AllyMemberData.FIELD_POINTS).		value(member.getPoints()).
				key(AllyMemberData.FIELD_IDLE_TIME).	value(idleTime).
				key(AllyMemberData.FIELD_FLEETS).		array();
			
			long totalFleetsPower = 0;
			List<Fleet> fleets = new ArrayList<Fleet>(member.getFleets());
			for (Fleet fleet : fleets) {
				totalFleetsPower += fleet.getPower();
				json.object().
					key(AllyMemberFleetData.FIELD_AREA).	value(fleet.getArea().getName()).
					key(AllyMemberFleetData.FIELD_POWER).	value(fleet.getPowerLevel()).
					endObject();
			}
			
			json.endArray().
				key(AllyMemberData.FIELD_SYSTEMS).		array();
			
			List<StarSystem> systems = new ArrayList<StarSystem>(member.getSystems());
			for (StarSystem system : systems)
				json.value(system.getArea().getName());
			
			json.endArray().
				key(AllyMemberData.FIELD_FLEETS_POWER).	value(totalFleetsPower).
				endObject();
		}
		json.endArray();
		
		// Postulants
		List<Applicant> applicants = DataAccess.getApplicantsByAlly(ally.getId());
		
		json.key(AllyData.FIELD_APPLICANTS).array();
		synchronized (applicants) {
			for(Applicant applicant : applicants){
				json.object().
					key(AllyApplicantData.FIELD_ID).		value(applicant.getIdPlayer()).
					key(AllyApplicantData.FIELD_LOGIN).		value(applicant.getPlayer().getLogin()).
					key(AllyApplicantData.FIELD_POINTS).	value(applicant.getPlayer().getPoints()).
					key(AllyApplicantData.FIELD_DATE).		value(applicant.getDate()).
					endObject();
			}
		}
		json.endArray();
		
		// Droits
		json.key(AllyData.FIELD_RIGHTS).	object().
			key(Ally.RIGHT_ACCEPT).			value(ally.getRequiredRank(Ally.RIGHT_ACCEPT)).
			key(Ally.RIGHT_KICK + 1).			value(ally.getRequiredRank(Ally.RIGHT_KICK, 1)).
			key(Ally.RIGHT_KICK + 2).			value(ally.getRequiredRank(Ally.RIGHT_KICK, 2)).
			key(Ally.RIGHT_KICK + 3).			value(ally.getRequiredRank(Ally.RIGHT_KICK, 3)).
			key(Ally.RIGHT_PROMOTE + 2).		value(ally.getRequiredRank(Ally.RIGHT_PROMOTE, 2)).
			key(Ally.RIGHT_PROMOTE + 3).		value(ally.getRequiredRank(Ally.RIGHT_PROMOTE, 3)).
			key(Ally.RIGHT_VOTE_ACCEPT).		value(ally.getRequiredRank(Ally.RIGHT_VOTE_ACCEPT)).
			key(Ally.RIGHT_VOTE_KICK + 1).		value(ally.getRequiredRank(Ally.RIGHT_VOTE_KICK, 1)).
			key(Ally.RIGHT_VOTE_KICK + 2).		value(ally.getRequiredRank(Ally.RIGHT_VOTE_KICK, 2)).
			key(Ally.RIGHT_VOTE_KICK + 3).		value(ally.getRequiredRank(Ally.RIGHT_VOTE_KICK, 3)).
			key(Ally.RIGHT_ELECT).				value(ally.getRequiredRank(Ally.RIGHT_ELECT)).
			key(Ally.RIGHT_MANAGE_NEWS).		value(ally.getRequiredRank(Ally.RIGHT_MANAGE_NEWS)).
			key(Ally.RIGHT_MANAGE_STATIONS).	value(ally.getRequiredRank(Ally.RIGHT_MANAGE_STATIONS)).
			key(Ally.RIGHT_MANAGE_DIPLOMACY).	value(ally.getRequiredRank(Ally.RIGHT_MANAGE_DIPLOMACY)).
			key(Ally.RIGHT_MANAGE_DESCRIPTION).	value(ally.getRequiredRank(Ally.RIGHT_MANAGE_DESCRIPTION)).
			key(Ally.RIGHT_MANAGE_CONTRACTS).	value(ally.getRequiredRank(Ally.RIGHT_MANAGE_CONTRACTS)).
			endObject();
		
		// Votes
		List<AllyVote> votes = DataAccess.getAllyVotesByAlly(ally.getId());
		
		json.key(AllyData.FIELD_VOTES).array();
		if (votes != null) {
			synchronized (votes) {
				for (AllyVote vote : votes) {
					Integer level;
					if (vote.getType().equals(AllyVote.TYPE_KICK))
						level = ally.getRequiredRank(Ally.RIGHT_VOTE_KICK, vote.getPlayer().getAllyRank());
					else
						level = ally.getRequiredRank(Ally.RIGHT_VOTE_ACCEPT);
					
					if (level != null && player.getAllyRank() >= level) {
						Long daysLeft = ((((vote.getDate() / 86400) * 86400) +
								Ally.TIME_TO_VOTE - Utilities.now()) / 86400) + 1;
						
						json.
							object().
							key(AllyVoteData.FIELD_ID).			value(vote.getId()).
							key(AllyVoteData.FIELD_TYPE).		value(vote.getType()).
							key(AllyVoteData.FIELD_HAS_VOTED).	value(player.hasVoted(vote.getId())).
							key(AllyVoteData.FIELD_ID_PLAYER).	value(vote.getIdPlayer()).
							key(AllyVoteData.FIELD_DAYS_LEFT).	value(daysLeft).
							key(AllyVoteData.FIELD_DATE).		value(vote.getDate()).
							endObject();
					}
				}
			}
		}
		
		// Elections
		Election election = DataAccess.getElectionByAlly(ally.getId());
		
		if (election != null) {
			Long daysLeft = ((((election.getDate() / 86400) * 86400) +
					Ally.TIME_TO_ELECT - Utilities.now()) / 86400) + 1;
			json.
				object().
				key(AllyVoteData.FIELD_ID).			value(election.getId()).
				key(AllyVoteData.FIELD_TYPE).		value("elect").
				key(AllyVoteData.FIELD_HAS_VOTED).	value(player.hasVotedElection(election.getId())).
				key(AllyVoteData.FIELD_ID_PLAYER).	value(0).
				key(AllyVoteData.FIELD_DAYS_LEFT).	value(daysLeft).
				key(AllyVoteData.FIELD_DATE).		value(election.getDate()).
				endObject();
		}
		json.endArray().
			endObject();
		
		return json;
	}
	
	public static void publishVoteResult(AllyVote vote){	
		
		String result = "";
		
		if(vote.getType().equals("accept")){
			if(vote.getYes()>vote.getNo()){
				result += "<p>La candidature de "+vote.getPlayer().getLogin()+" a été accepté " +
						"dans l'alliance suite à un vote</p><br/><p>Les résultats du vote sont :</p>" +
						getFigures(vote);
				
				DataAccess.save(new AllyNews("Candidature accepté",result,0,vote.getIdAlly(),0));
			} else {
				result += "<p>La candidature de "+vote.getPlayer().getLogin()+" a été refusée " +
						"suite à un vote</p><br/><p>Les résultats du vote sont :</p>" +
						getFigures(vote);
				
				DataAccess.save(new AllyNews("Candidature refusée",result,0,vote.getIdAlly(),0));
			}
		} else {
			if(vote.getYes()>vote.getNo()){
				result += "<p>L'éjection de "+vote.getPlayer().getLogin()+" a été décidée " +
						"suite à un vote</p><br/><p>Les résultats du vote sont :</p>" +
						getFigures(vote);
				
				DataAccess.save(new AllyNews("Membre éjecté",result,0,vote.getIdAlly(),0));
			} else {
				result += "<p>L'éjection de "+vote.getPlayer().getLogin()+" a été refusée " +
						"suite à un vote</p><br/><p>Les résultats du vote sont :</p>" +
						getFigures(vote);
		
				DataAccess.save(new AllyNews("Ejection refusée",result,0,vote.getIdAlly(),0));
			}
		}
		
		UpdateTools.queueNewAllyNewsUpdate(vote.getAlly(), 0, false);
	}
	
	public static String publishElectionResult(Election vote){	
		
		String result = "";
		Player winner = vote.getWinner();
		result += "<p>Suite au vote, "+winner.getLogin()+" a été élu " +
				"à la tête de l'alliance</p><br/><p>Les résultats du vote sont :</p>" +
				getFigures(vote);	
		
		DataAccess.save(new AllyNews(winner.getLogin()+" élu",result,0,vote.getIdAlly(),0));
		
		UpdateTools.queueNewAllyNewsUpdate(vote.getAlly(), 0, false);
		
		return result;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static String getFigures(Object voteObject){	
		String result = "";
		if (voteObject instanceof AllyVote) {
			AllyVote vote = (AllyVote) voteObject;
			
			//Yes
			result +=	"<p><span style=\"width:100px; float:left;\">Yes</span>";
			result +=	"<span style=\"width:220px; float:left;\">";
			
			int pourcent =  Math.round(vote.getYes()*100/(vote.getYes()+vote.getNo()));
			result += "<img src=\"" + Config.getMediaURL()+ "images/vote/voteYesG.png\" style=\"padding:0px; margin:-3px\"/>";
			
			result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteYesC.png\" style=\"padding:0px; margin:-3px; width:" +pourcent*2+ "px; height:13px; margin-left:3px;\"/>";
			result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteYesD.png\" style=\"padding:0px; margin:-3px\"/></span>" +pourcent+ "%</p>";
			
			
			//No
			
			 result +=	"<p><span style=\"width:100px; float:left;\">No</span>";
			result +=	"<span style=\"width:220px; float:left;\">";
			
			pourcent =  Math.round(vote.getNo()*100/(vote.getYes()+vote.getNo()));
				
			result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteNoG.png\" style=\"padding:0px; margin:-3px\"/>";
			result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteNoC.png\" style=\"padding:0px; margin:-3px; width:" +pourcent*2+ "px; height:13px; margin-left:3px;\"/>";
			result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteNoD.png\" style=\"padding:0px; margin:-3px\"/></span>" +pourcent+ "%</p>";
			 
		}
		else if (voteObject instanceof Election) {
			Election vote = (Election) voteObject;
			
			List<ElectionVoter> votes = DataAccess.getElectionVoterById(vote.getId());
			
			HashMap<String, Integer> candidats = new HashMap<String, Integer>();
			
			for(int i=0;i<votes.size();i++){
				String login = DataAccess.getPlayerById(votes.get(i).getIdCandidate()).getLogin();				
				
				candidats.put(login, (candidats.get(login)!=null?candidats.get(login)+1:1));
			}

			final HashMap<String, Integer> map = new HashMap<String, Integer>(candidats);
			
			TreeMap<String, Integer> tm = new TreeMap<String, Integer>(new Comparator<Object>() {
				public int compare(Object obj, Object obj1)
	            {
	                Integer val1 = map.get(obj);
	                Integer val2 = map.get(obj1);
	                if (val1  > val2 ) 
	                     return -1;
	                else if (val1  < val2)
	                     return +1 ; 
	                else
	                     return 0;
	            }
			});
			
			tm.putAll(map);
			
			for(Iterator<Entry<String, Integer>> it = tm.entrySet().iterator(); it.hasNext();){
				Entry<String, Integer> entry = it.next();
				
				result +=	"<p><span style=\"width:100px; float:left;\">" +entry.getKey()+ "</span>";
		
				result +=	"<span style=\"width:220px; float:left;\">";
				
				int pourcent = Math.round(entry.getValue()*100/votes.size()); 
				
				result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteG.png\" style=\"padding:0px; margin:-3px\"/>";
				result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteC.png\" style=\"padding:0px; margin:-3px; width:" +pourcent*2+ "px; height:13px; margin-left:3px;\"/>";
				result +=	"<img src=\"" + Config.getMediaURL()+ "images/vote/voteD.png\" style=\"padding:0px; margin:-3px\"/></span>" +pourcent+ "%</p>";
				 
			}	
		}
		
		return result;
	}
}
