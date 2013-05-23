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
package fr.fg.server.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.base.AllyBase;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.Utilities;

public class Ally  extends AllyBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String 
		RIGHT_ACCEPT				= "accept", 
		RIGHT_KICK					= "kick", 
		RIGHT_PROMOTE				= "promote", 
		RIGHT_VOTE_ACCEPT			= "vote_accept", 
		RIGHT_VOTE_KICK				= "vote_kick", 
		RIGHT_ELECT					= "elect",
		RIGHT_MANAGE_NEWS			= "manage_news",
		RIGHT_MANAGE_STATIONS		= "manage_stations",
		RIGHT_MANAGE_DIPLOMACY		= "manage_diplomacy",
		RIGHT_MANAGE_DESCRIPTION	= "manage_description",
		RIGHT_MANAGE_CONTRACTS		= "manage_contracts";
	
	public final static int TIME_TO_VOTE = 259200;
	public final static int TIME_TO_ELECT = 388800;
	
	public final static Color[] TERRITORY_COLORS = {
		new Color(0x1583db), // Bleu
		new Color(0x5fd8f3), // Cyan
		new Color(0x1de386), // Vert bleu
		new Color(0x09fb23), // Vert
		new Color(0xd7e840), // Jaune vert
		new Color(0xe8b640), // Jaune Orange
		new Color(0xfb7509), // Orange
		new Color(0xf07468), // Rose
		new Color(0xf40b00), // Rouge
		new Color(0xf71780), // Mauve
		new Color(0xb717f7), // Violet
		new Color(0x7568f0), // Violet bleu
		new Color(0x808080), // Gris
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //	

	private final static Map<String,Integer> REQUIRED_RANKS;

	static{
		REQUIRED_RANKS = Collections.synchronizedMap(new HashMap<String,Integer>());

		// Tyrannie
		REQUIRED_RANKS.put("TYRANNY_ACCEPT",				2);
		REQUIRED_RANKS.put("TYRANNY_KICK_1",				2);
		REQUIRED_RANKS.put("TYRANNY_KICK_2",				3);
		REQUIRED_RANKS.put("TYRANNY_KICK_3",				5);
		REQUIRED_RANKS.put("TYRANNY_PROMOTE_2",				3);
		REQUIRED_RANKS.put("TYRANNY_PROMOTE_3",				5);
		REQUIRED_RANKS.put("TYRANNY_VOTE_ACCEPT",			5);
		REQUIRED_RANKS.put("TYRANNY_VOTE_KICK_1",			5);
		REQUIRED_RANKS.put("TYRANNY_VOTE_KICK_2",			5);
		REQUIRED_RANKS.put("TYRANNY_VOTE_KICK_3",			5);
		REQUIRED_RANKS.put("TYRANNY_ELECT",					5);
		REQUIRED_RANKS.put("TYRANNY_MANAGE_NEWS",			2);
		REQUIRED_RANKS.put("TYRANNY_MANAGE_STATIONS",		3);
		REQUIRED_RANKS.put("TYRANNY_MANAGE_DIPLOMACY",		3);
		REQUIRED_RANKS.put("TYRANNY_MANAGE_DESCRIPTION",	3);
		REQUIRED_RANKS.put("TYRANNY_MANAGE_CONTRACTS",		3);
		
		// Militariste
		REQUIRED_RANKS.put("WARMONGER_ACCEPT",				2);
		REQUIRED_RANKS.put("WARMONGER_KICK_1",				2);
		REQUIRED_RANKS.put("WARMONGER_KICK_2",				3);
		REQUIRED_RANKS.put("WARMONGER_KICK_3",				4);
		REQUIRED_RANKS.put("WARMONGER_PROMOTE_2",			3);
		REQUIRED_RANKS.put("WARMONGER_PROMOTE_3",			4);
		REQUIRED_RANKS.put("WARMONGER_VOTE_ACCEPT",			2);
		REQUIRED_RANKS.put("WARMONGER_VOTE_KICK_1",			2);
		REQUIRED_RANKS.put("WARMONGER_VOTE_KICK_2",			2);
		REQUIRED_RANKS.put("WARMONGER_VOTE_KICK_3",			3);
		REQUIRED_RANKS.put("WARMONGER_ELECT",				3);
		REQUIRED_RANKS.put("WARMONGER_MANAGE_NEWS",			3);
		REQUIRED_RANKS.put("WARMONGER_MANAGE_STATIONS",		3);
		REQUIRED_RANKS.put("WARMONGER_MANAGE_DIPLOMACY",	4);
		REQUIRED_RANKS.put("WARMONGER_MANAGE_DESCRIPTION",	4);
		REQUIRED_RANKS.put("WARMONGER_MANAGE_CONTRACTS",	4);
		
		// Démocratie
		REQUIRED_RANKS.put("DEMOCRACY_ACCEPT",				2);
		REQUIRED_RANKS.put("DEMOCRACY_KICK_1",				2);
		REQUIRED_RANKS.put("DEMOCRACY_KICK_2",				3);
		REQUIRED_RANKS.put("DEMOCRACY_KICK_3",				5);
		REQUIRED_RANKS.put("DEMOCRACY_PROMOTE_2",			3);
		REQUIRED_RANKS.put("DEMOCRACY_PROMOTE_3",			5);
		REQUIRED_RANKS.put("DEMOCRACY_VOTE_ACCEPT",			1);
		REQUIRED_RANKS.put("DEMOCRACY_VOTE_KICK_1",			1);
		REQUIRED_RANKS.put("DEMOCRACY_VOTE_KICK_2",			2);
		REQUIRED_RANKS.put("DEMOCRACY_VOTE_KICK_3",			5);
		REQUIRED_RANKS.put("DEMOCRACY_ELECT",				1);
		REQUIRED_RANKS.put("DEMOCRACY_MANAGE_NEWS",			2);
		REQUIRED_RANKS.put("DEMOCRACY_MANAGE_STATIONS",		2);
		REQUIRED_RANKS.put("DEMOCRACY_MANAGE_DIPLOMACY",	3);
		REQUIRED_RANKS.put("DEMOCRACY_MANAGE_DESCRIPTION",	3);
		REQUIRED_RANKS.put("DEMOCRACY_MANAGE_CONTRACTS",	3);
		
		// Oligarchie
		REQUIRED_RANKS.put("OLIGARCHY_ACCEPT",				2);
		REQUIRED_RANKS.put("OLIGARCHY_KICK_1",				2);
		REQUIRED_RANKS.put("OLIGARCHY_KICK_2",				3);
		REQUIRED_RANKS.put("OLIGARCHY_KICK_3",				5);
		REQUIRED_RANKS.put("OLIGARCHY_PROMOTE_2",			3);
		REQUIRED_RANKS.put("OLIGARCHY_PROMOTE_3",			3);
		REQUIRED_RANKS.put("OLIGARCHY_VOTE_ACCEPT",			1);
		REQUIRED_RANKS.put("OLIGARCHY_VOTE_KICK_1",			1);
		REQUIRED_RANKS.put("OLIGARCHY_VOTE_KICK_2",			2);
		REQUIRED_RANKS.put("OLIGARCHY_VOTE_KICK_3",			5);
		REQUIRED_RANKS.put("OLIGARCHY_ELECT",				5);
		REQUIRED_RANKS.put("OLIGARCHY_MANAGE_NEWS",			2);
		REQUIRED_RANKS.put("OLIGARCHY_MANAGE_STATIONS",		3);
		REQUIRED_RANKS.put("OLIGARCHY_MANAGE_DIPLOMACY",	3);
		REQUIRED_RANKS.put("OLIGARCHY_MANAGE_DESCRIPTION",	3);
		REQUIRED_RANKS.put("OLIGARCHY_MANAGE_CONTRACTS",	3);
		
		// Anarchie
		REQUIRED_RANKS.put("ANARCHY_ACCEPT",				1);
		REQUIRED_RANKS.put("ANARCHY_KICK_1",				1);
		REQUIRED_RANKS.put("ANARCHY_KICK_2",				5);
		REQUIRED_RANKS.put("ANARCHY_KICK_3",				5);
		REQUIRED_RANKS.put("ANARCHY_PROMOTE_2",				5);
		REQUIRED_RANKS.put("ANARCHY_PROMOTE_3",				5);
		REQUIRED_RANKS.put("ANARCHY_VOTE_ACCEPT",			1);
		REQUIRED_RANKS.put("ANARCHY_VOTE_KICK_1",			1);
		REQUIRED_RANKS.put("ANARCHY_VOTE_KICK_2",			5);
		REQUIRED_RANKS.put("ANARCHY_VOTE_KICK_3",			5);
		REQUIRED_RANKS.put("ANARCHY_ELECT",					5);
		REQUIRED_RANKS.put("ANARCHY_MANAGE_NEWS",			1);
		REQUIRED_RANKS.put("ANARCHY_MANAGE_STATIONS",		1);
		REQUIRED_RANKS.put("ANARCHY_MANAGE_DIPLOMACY",		1);
		REQUIRED_RANKS.put("ANARCHY_MANAGE_DESCRIPTION",	1);
		REQUIRED_RANKS.put("ANARCHY_MANAGE_CONTRACTS",		1);
	}
	
	private int achievementsCache;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

    public Ally() {
		// Nécessaire pour la construction par réflection
    	this.achievementsCache = -1;
    }

    public Ally(String name, String tag, String organization,
    		String description, long points, int idCreator) {
    	this.achievementsCache = -1;
		    	
		setName(name);
		setTag(tag.toUpperCase());
		setColor(Math.abs(name.hashCode()) % TERRITORY_COLORS.length);
		setAi(false);
		setOrganization(organization);
		setDescription(description);
		setBirthdate(Utilities.now());
		setPoints(points);
		setRightAccept(getDefaultRequiredRank(organization, RIGHT_ACCEPT));
		setRightManageNews(getDefaultRequiredRank(organization, RIGHT_MANAGE_NEWS));
		setRightManageStations(getDefaultRequiredRank(organization, RIGHT_MANAGE_STATIONS));
		setRightManageDiplomacy(getDefaultRequiredRank(organization, RIGHT_MANAGE_DIPLOMACY));
		setRightManageDescription(getDefaultRequiredRank(organization, RIGHT_MANAGE_DESCRIPTION));
		setRightManageContracts(getDefaultRequiredRank(organization, RIGHT_MANAGE_CONTRACTS));
		setIdCreator(idCreator);
    }
    
	// --------------------------------------------------------- METHODES -- //
    
    public List<Player> getMembers() {
    	return DataAccess.getPlayersByAlly(getId());
    }
    
    public List<AllyTreaty> getTreaties() {
    	return DataAccess.getAllyTreaties(getId());
    }
    
    public List<AllyTreaty> getTotalPacts() {
    	List<AllyTreaty> allTreaties = DataAccess.getAllyTreaties(getId());
    	List<AllyTreaty> totalPacts = new ArrayList<AllyTreaty>();
    	for(AllyTreaty treaty : allTreaties) {
    		if(treaty.getType().equals(AllyTreaty.TYPE_TOTAL)  && treaty.getSource()==0) {
    			totalPacts.add(treaty);
    		}
    	}
    	return totalPacts;
    }
    
    public List<AllyTreaty> getDefensivePacts() {
    	List<AllyTreaty> allTreaties = DataAccess.getAllyTreaties(getId());
    	List<AllyTreaty> defensivePacts = new ArrayList<AllyTreaty>();
    	for(AllyTreaty treaty : allTreaties) {
    		if(treaty.getType().equals(AllyTreaty.TYPE_DEFENSIVE)  && treaty.getSource()==0) {
    			defensivePacts.add(treaty);
    		}
    	}
    	return defensivePacts;
    }
    
    public List<AllyTreaty> getNonAgressionPacts() {
    	List<AllyTreaty> allTreaties = DataAccess.getAllyTreaties(getId());
    	List<AllyTreaty> nonAgressionPacts = new ArrayList<AllyTreaty>();
    	for(AllyTreaty treaty : allTreaties) {
    		if(treaty.getType().equals(AllyTreaty.TYPE_ALLY)  && treaty.getSource()==0) {
    			nonAgressionPacts.add(treaty);
    		}
    	}
    	return nonAgressionPacts;
    }
    
    public List<AllyTreaty> getWars() {
    	List<AllyTreaty> allTreaties = DataAccess.getAllyTreaties(getId());
    	List<AllyTreaty> wars = new ArrayList<AllyTreaty>();
    	for(AllyTreaty treaty : allTreaties) {
    		if(treaty.getType().equals(AllyTreaty.TYPE_WAR)) {
    			wars.add(treaty);
    		}
    	}
    	return wars;
    }
    
    public void checkNewRelationships(AllyTreaty newTreaty, boolean hasDeclaredWar) {
    	if(newTreaty.getType().equals(AllyTreaty.TYPE_WAR)) {
			List<AllyTreaty> totalPacts = this.getTotalPacts();
			for(AllyTreaty pact : totalPacts){ //Pour chaque pacte total
				Ally allied = pact.getOtherAlly(this.getId());
				String alliedTreaty = allied.getTreatyWithAlly(newTreaty.getOtherAllyId(this.getId()));
				//Si le pacte conclu avec le nouvel ennemi n'est pas Total, et si l'allié n'est pas déjà en guerre
				if(!AllyTreaty.isPact(alliedTreaty) && !alliedTreaty.equals(Treaty.TYPE_WAR)) {
					new AllyTreaty(allied.getId(),
							newTreaty.getOtherAllyId(this.getId()),
							AllyTreaty.TYPE_WAR,0).save();
					DataAccess.save(new Event(
							Event.EVENT_ALLY_DECLARE_WAR_TOTAL,
							Event.TARGET_ALLY, allied.getId(), 0, -1, -1,
							getName(),newTreaty.getOtherAlly(this.getId()).getName()));
					DataAccess.save(new Event(
							Event.EVENT_ALLY_WAR_DECLARED_TOTAL,
							Event.TARGET_ALLY,newTreaty.getOtherAllyId(this.getId()), 0, -1, -1,
							getName(),allied.getName()));
					UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
				}
			}
			/*
			 * Si la déclaration de guerre n'a pas été déclenchée par le joueur
			 * alors on regarde ses pactes défensif
			 */
			if(!hasDeclaredWar) {
				List<AllyTreaty> defensivePacts = this.getDefensivePacts();
				for(AllyTreaty pact : defensivePacts){ //Pour chaque pacte défensif
					Ally allied = pact.getOtherAlly(this.getId());
					String alliedTreaty = allied.getTreatyWithAlly(newTreaty.getOtherAllyId(this.getId()));
					//Si l'ennemi potentiel est neutre
					if(!AllyTreaty.isPact(alliedTreaty) && !alliedTreaty.equals(Treaty.TYPE_WAR)) {
						new AllyTreaty(allied.getId(),
								newTreaty.getOtherAllyId(this.getId()),
								AllyTreaty.TYPE_WAR,0).save();
						DataAccess.save(new Event(
								Event.EVENT_ALLY_DECLARE_WAR_DEFENSIVE,
								Event.TARGET_ALLY, allied.getId(), 0, -1, -1,
								getName(),newTreaty.getOtherAlly(this.getId()).getName()));
						DataAccess.save(new Event(
								Event.EVENT_ALLY_WAR_DECLARED_DEFENSIVE,
								Event.TARGET_ALLY,newTreaty.getOtherAllyId(this.getId()), 0, -1, -1,
								getName(),allied.getName()));
						UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
					}
				}
			}
		}
	}
    
    public void cancelAllMembersTreatiesWith(Ally enemyAlly) {
    	//On supprime tous les pactes conclus par les membres de l'alliance avec l'ennemi
		List<Player> members = this.getMembers();
		List<Player> membersEnemy = enemyAlly.getMembers();
		for(Player member : members) {
			for(Player memberEnemy : membersEnemy) {
				Treaty treaty = member.getPlayerTreatyWithPlayer(memberEnemy);
				if(treaty!=null) {
					treaty.delete();
				}
			}
		}
    }
    
	public String getTreatyWithAlly(int idAlly) {
		Ally otherAlly = DataAccess.getAllyById(idAlly);
		return otherAlly != null ?
				getTreatyWithAlly(otherAlly) : Treaty.NEUTRAL;
	}
	
    public String getTreatyWithAlly(Ally otherAlly) {
    	if (getId() == otherAlly.getId())
    		return AllyTreaty.ALLY;
    	
		// Traité entre alliances
		List<AllyTreaty> allyTreaties = otherAlly.getTreaties();
		
		for (AllyTreaty allyTreaty : allyTreaties) {
			if (allyTreaty.implyAlly(getId())) {
				if (allyTreaty.getType().equals(AllyTreaty.TYPE_WAR))
					return AllyTreaty.ENEMY;
				else if(allyTreaty.getSource()==0)
					if(allyTreaty.getType().equals(AllyTreaty.TYPE_ALLY))
						return AllyTreaty.ALLIED;
					else
						return allyTreaty.getType();
			}
		}
		
		return Treaty.NEUTRAL;
    }
    
    public int getLeaderRank(){
    	return getLeaderRank(getOrganization());
    }
    
    public List<Player> getLeaders() {
    	List<Player> members = getMembers();
    	ArrayList<Player> leaders = new ArrayList<Player>();
    	
    	synchronized (members) {
	    	for (Player member : members)
	    		if (member.getAllyRank() == getLeaderRank())
	    			leaders.add(member);
		}
    	
    	return leaders;
    }
    
	public double getInfluence(int idSector) {
		List<AllyInfluence> allyInfluences =
			DataAccess.getAllyInfluencesByAlly(getId());
		
		for (AllyInfluence allyInfluence : allyInfluences)
			if (allyInfluence.getIdSector() == idSector)
				return allyInfluence.getInfluenceValue() *
					allyInfluence.getInfluenceCoef() /
					allyInfluence.getSystemsCount();
		
		return 0;
	}

    public String getCreatorName() {
    	return getIdCreator() == 0 ? "???" : DataAccess.getPlayerById(getIdCreator()).getLogin();
    }
    
    public Player getCreator() {
    	return DataAccess.getPlayerById(getIdCreator());
    }
    
	public List<SpaceStation> getSpaceStations() {
		return DataAccess.getSpaceStationsByAlly(getId());
	}
	
    public List<AllyNews> getNews(){
    	return DataAccess.getAllyNewsByAlly(getId());
    }
    
    public int getStickyNewsCount(){
    	List<AllyNews> news =  new ArrayList<AllyNews>(DataAccess.getAllyNewsByAlly(getId()));
    	
    	int count = 0;
    	for(AllyNews n:news){
    		if( n.isSticky() )
    			count++;
    	}
    	
    	return count;
    }
    
    public int getProductsCount(int type) {
    	List<Area> areas = DataAccess.getAllAreas();
    	int count = 0;
    	
    	synchronized (areas) {
			for (Area area : areas) {
				if (area.getIdDominatingAlly() == getId() &&
						area.getProduct() == type)
					count++;
			}
		}
    	
    	return count;
    }
    
    public Integer getRequiredRank(String action) {
    	if (action.equals(RIGHT_ACCEPT))
    		return getRightAccept();
    	else if (action.equals(RIGHT_MANAGE_NEWS))
    		return getRightManageNews();
    	else if (action.equals(RIGHT_MANAGE_STATIONS))
			return getRightManageStations();
		else if (action.equals(RIGHT_MANAGE_DIPLOMACY))
			return getRightManageDiplomacy();
    	else if (action.equals(RIGHT_MANAGE_DESCRIPTION))
    		return getRightManageDescription();
    	else if (action.equals(RIGHT_MANAGE_CONTRACTS))
    		return getRightManageContracts();
    	else
    		return getDefaultRequiredRank(getOrganization(), action);
    }
    
    public Integer getRequiredRank(String action, int targetRank) {
    	return getDefaultRequiredRank(getOrganization(), action, targetRank);
    }
    
	public long getDaysFromCreation(){
		return ((Utilities.now()-getBirthdate())/Utilities.SEC_IN_DAY);
	}
	
	/**
     * Calcul le nombre de messages reçus depuis date
     * (Pourra peut etre renvoyer List<> si nécessaire
     * on utilisera alors un size())
     * @param	Data a partir de laquelle regardée
     * @return 	Nombre de messages
     */
    public int countNewsSince(long date){
    	List<AllyNews> messages = getNews();
		int count = 0;
		
    	synchronized (messages) {
    		for(AllyNews message:messages){
    			if(message.getDate()>date)
    				count++;
    		}
		}
		
    	return count;
    }
    
    public void updateInfluences() {
    	// Compte le nombre de systèmes par secteur des membres de l'alliance
    	HashMap<Integer, Integer> systemsBySector =
    		new HashMap<Integer, Integer>();
    	
    	List<Player> members = new ArrayList<Player>(getMembers());
    	
    	for (Player member : members) {
    		List<StarSystem> systems = member.getSystems();
    		
    		synchronized (systems) {
				for (StarSystem system : systems) {
					int idSector = system.getArea().getIdSector();
					Integer systemsCount = systemsBySector.get(idSector);
					
					if (systemsCount == null)
						systemsBySector.put(idSector, 1);
					else
						systemsBySector.put(idSector, systemsCount + 1);
				}
			}
    	}
    	
    	// Met à jour l'influence de l'alliance
    	List<AllyInfluence> allyInfluences = new ArrayList<AllyInfluence>(
    		DataAccess.getAllyInfluencesByAlly(getId()));
    	List<Integer> playersToUpdate = new ArrayList<Integer>();
    	
    	for (AllyInfluence allyInfluence : allyInfluences) {
    		Integer systemsCount = systemsBySector.get(allyInfluence.getIdSector());
    		if (systemsCount == null)
    			systemsCount = 0;
    		
    		if (allyInfluence.getSystemsCount() != systemsCount) {
    			// Met à jour les système des membres dans le secteur
    			for (Player member : members) {
    	    		List<StarSystem> systems = new ArrayList<StarSystem>(member.getSystems());
    	    		
					for (StarSystem system : systems) {
						int idSector = system.getArea().getIdSector();
						
						system = StarSystem.updateSystem(system);
						
						if (idSector == allyInfluence.getIdSector()) {
							if (!playersToUpdate.contains(member.getId()))
								playersToUpdate.add(member.getId());
						}
					}
    			}
    			
    			// Met à jour l'influence de l'alliance
    			synchronized (allyInfluence.getLock()) {
					AllyInfluence newAllyInfluence = DataAccess.getEditable(allyInfluence);
					newAllyInfluence.setSystemsCount(systemsCount);
					newAllyInfluence.save();
				}
    		}
    	}
    	
    	// Signale les modifications d'influence aux joueurs concernés
    	for (Integer idPlayer : playersToUpdate)
    		UpdateTools.queuePlayerSystemsUpdate(idPlayer, false);
    }
    
	public int getAchievementLevelsSum() {
		if (achievementsCache == -1)
			updateAchievementsCache();
		return achievementsCache;
	}
	
	public void updateAchievementsCache() {
		List<Player> members = getMembers();
		
		int achievementsCache = 0;
		
		synchronized (members) {
			for (Player member : members) {
				List<Achievement> achievements = member.getAchievements();
				
				synchronized (achievements) {
					for (Achievement achievement : achievements)
						achievementsCache += achievement.getLevel();
				}
			}
		}
		
		this.achievementsCache = achievementsCache;
	}
	
    public static int getLeaderRank(String organization) {
    	if (organization.equals(ORGANIZATION_TYRANNY))
			return 3;
		if (organization.equals(ORGANIZATION_WARMONGER))
			return 4;
		if (organization.equals(ORGANIZATION_DEMOCRACY))
			return 3;
		if (organization.equals(ORGANIZATION_OLIGARCHY))
			return 3;
		if (organization.equals(ORGANIZATION_ANARCHY))
			return 1;
		else
			return 5;
    }
    
    public static String getRankName(String organization, int rank) {
    	return Messages.getString("organization." + organization + ".rank" + rank);
    }
    
    
	public List<AllyRelationship> getRelationships() {
		return DataAccess.getAllyRelationshipsByAlly(getId());
	}
	
	public double getRelationshipValue(int idAlly) {
		List<AllyRelationship> relationships = getRelationships();
		
		synchronized (relationships) {
			for (AllyRelationship relationship : relationships)
				if (relationship.getIdAlly() == idAlly)
					return relationship.getValue();
		}
		
		return 0;
	}
	
	public void setRelationshipValue(int idAlly, double value) {
		List<AllyRelationship> relationships =
			new ArrayList<AllyRelationship>(getRelationships());
		
		for (AllyRelationship relationship : relationships)
			if (relationship.getIdAlly() == idAlly) {
				synchronized (relationship.getLock()) {
					relationship = DataAccess.getEditable(relationship);
					relationship.setValue(value);
					relationship.save();
				}
				return;
			}
		
		AllyRelationship relationship = new AllyRelationship(getId(), idAlly, value);
		relationship.save();
	}
	
	public void addRelationshipValue(int idAlly, double value) {
		setRelationshipValue(idAlly, getRelationshipValue(idAlly) + value);
	}
	
	public int getMediumLevel(){
		List<Player> members=getMembers();
		int mediumLevel=0;
		for(Player player : members){
			mediumLevel+=player.getLevel();
		}
		return (int)(mediumLevel/members.size());
	}
	
	public int[] getMembersLevels(){
		int[] arrayLevel=new int[getMembers().size()];
		int i=0;
		for(Player player : getMembers())
		{
			arrayLevel[i]=player.getLevel();
			i++;
		}
		return arrayLevel;
	}
	
	public int getLevel(){
		List<Player> members=getMembers();
		int level=0;
		for(Player player : members){
			level+=player.getLevel();
		}
		return level;
	}
	
	public List<StarSystem> getMembersSystems(){
		List<StarSystem> systems=new ArrayList<StarSystem>();
		for(Player player : getMembers())
		{
			for(StarSystem system : player.getSystems())
			{
				//if(systems==null) systems= system;
				//else
					systems.add(system);
			}
		}
		if(systems.size()>0)
		return systems;
		else
			return null;
	}
	
	public List<Contract> getRunningContractsByType(String type) {
		ArrayList<Contract> contracts = new ArrayList<Contract>();
		List<ContractAttendee> attendees = DataAccess.getAttendeesByAlly(this);
		
		synchronized (attendees) {
			for (ContractAttendee attendee : attendees) {
				Contract contract = attendee.getContract();
				
				if (contract.getState() == Contract.STATE_RUNNING &&
						contract.getType().equals(type))
					contracts.add(contract);
			}
		}
		
		return contracts;
	}
	
	public List<Contract> getWaitingContractsByType(String type) {
		ArrayList<Contract> contracts = new ArrayList<Contract>();
		List<ContractAttendee> attendees = DataAccess.getAttendeesByAlly(this);
		
		synchronized (attendees) {
			for (ContractAttendee attendee : attendees) {
				Contract contract = attendee.getContract();
				
				if (contract.getState() == Contract.STATE_WAITING &&
						contract.getType().equals(type))
					contracts.add(contract);
			}
		}
		
		return contracts;
	}
	
	public List<StarSystem> getSystems() {
		ArrayList<StarSystem> systems = new ArrayList<StarSystem>();

		for(Player player : getMembers())
		{
			for(StarSystem system : player.getSystems())
				systems.add(system);
		}
		
		return systems;
	}
	
	
    // ------------------------------------------------- METHODES PRIVEES -- //

    private static Integer getDefaultRequiredRank(String organization,String action){
    	return REQUIRED_RANKS.get(organization.toUpperCase()+"_"+action.toUpperCase());
    }
    
    private static Integer getDefaultRequiredRank(String organization,String action, Integer targetRank){
    	return REQUIRED_RANKS.get(organization.toUpperCase()+"_"+action.toUpperCase()+"_"+targetRank.toString());
    }

	public List<Integer> getIdMembers() {
		List<Integer> idMembers = new ArrayList<Integer>();
		for(Player player : getMembers())
		{
			idMembers.add(player.getId());
		}
		return idMembers;
	}
}


