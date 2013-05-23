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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.continuation.Continuation;

import fr.fg.client.data.PlayerInfosData;
import fr.fg.server.core.AiTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.base.PlayerBase;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Player extends PlayerBase {
	// ------------------------------------------------------- CONSTANTES -- //

	// Droits d'accès
	public final static int USER = 0;
	public final static int PREMIUM = 1;
	public final static int MODERATOR = 2;
	public final static int ADMINISTRATOR = 4;
	public final static int SUPER_ADMINISTRATOR = 8;
	
	public final static boolean SEX_MALE = false, SEX_FEMALE = true;
	
	// Luminosité
	public final static byte BRIGHTNESS_DARK = 0;
	public final static byte BRIGHTNESS_LIGHT = 1;
	
	public final static int
		SCALE_DAY = 0,
		SCALE_MONTH = 1,
		SCALE_OVERALL = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Continuation continuation;
	
	private int idCurrentArea;
	
	private long lastPing;
	
	// Clé pour éviter les attaques par XSS
	private String securityKey;
	
	private int achievementsCache;
	
    private boolean connected;
	
	private boolean away;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Player() {
		// Nécessaire pour la construction par réflection
		this.achievementsCache = -1;
	}
	
	public Player(String login, String password, String email, String ekey, String hash) {
		this.achievementsCache = -1;
		
		setLogin(login);
		setPassword(password);
		setEkey(ekey);
	    setAi(false);
		setRights(Player.USER);
		setEmail(email);
		setBanChat(0);
		setBanGame(0);
		setAvatar("");
		setAllyRank(0);
		setPoints(0);
		setXp(0);
		setCredits(0);
		setEventsReadDate(0);
		setTutorial(0);
		setDiplomacyActivated(true);
		setSwitchDiplomacyDate(0);
		setSettingsGridVisible(false);
		setSettingsBrightness(0);
		setSettingsFleetSkin(1);
		setSettingsMusic(0);
		setSettingsTheme(GameConstants.THEMES[0]);
		setSettingsChat(7);
		setSettingsCensorship(true);
		setSettingsGeneralVolume(0);
		setSettingsSoundVolume(0);
		setSettingsMusicVolume(0);
		setSettingsGraphics(0);
		setSettingsOptimizeConnection(false);
		setSettingsFleetNamePrefix("Flotte");
		setSettingsFleetNameSuffix(0);
		setIdAlly(0);
		setRegistrationDate(Utilities.now());
		setRegistration(hash);
		setRecoverEmail("");
		setRecoverPassword("");
		setCloseAccountHash("");
		setCloseAccountReason("");
		setLastConnection(0);
		setIdContract(0);
		setDescription("");
	}

	// --------------------------------------------------------- METHODES -- //
	
	public List<Contract> getRunningContractsByType(String type) {
		ArrayList<Contract> contracts = new ArrayList<Contract>();
		List<ContractAttendee> attendees = DataAccess.getAttendeesByPlayer(this);
		
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
		List<ContractAttendee> attendees = DataAccess.getAttendeesByPlayer(this);
		
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
	
	public List<Relationship> getRelationships() {
		return DataAccess.getRelationshipsByPlayer(getId());
	}
	
	public double getRelationshipValue(int idAlly) {
		List<Relationship> relationships = getRelationships();
		
		synchronized (relationships) {
			for (Relationship relationship : relationships)
				if (relationship.getIdAlly() == idAlly)
					return relationship.getValue();
		}
		
		return 0;
	}
	
	public void setRelationshipValue(int idAlly, double value) {
		List<Relationship> relationships =
			new ArrayList<Relationship>(getRelationships());
		
		for (Relationship relationship : relationships)
			if (relationship.getIdAlly() == idAlly) {
				synchronized (relationship.getLock()) {
					relationship = DataAccess.getEditable(relationship);
					relationship.setValue(value);
					relationship.save();
				}
				return;
			}
		
		Relationship relationship = new Relationship(getId(), idAlly, value);
		relationship.save();
	}
	
	public void addRelationshipValue(int idAlly, double value) {
		setRelationshipValue(idAlly, getRelationshipValue(idAlly) + value);
	}
	
	public int getAdvancementPoints() {
		int points = getLevel() * 5;
		List<Advancement> advancements = DataAccess.getAdvancementsByPlayer(getId());
		
		synchronized (advancements) {
			for (Advancement advancement : advancements)
				points -= advancement.getCost() * advancement.getLevel();
		}
		
		return points;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean connected) {
		Player cachedPlayer = DataAccess.getPlayerById(getId());
		
		if (cachedPlayer != this)
			cachedPlayer.connected = connected;
		this.connected = connected;
	}

	public boolean isAway() {
		return away;
	}
	
	public void setAway(boolean away) {
		Player cachedPlayer = DataAccess.getPlayerById(getId());
		
		if (cachedPlayer != this)
			cachedPlayer.away = away;
		this.away = away;
	}
	
	public boolean isMutualFriend(int idOtherPlayer) {
		Contact contact = DataAccess.getContactByContact(getId(), idOtherPlayer);
		if (contact == null || contact.getType().equals(Contact.TYPE_IGNORE))
			return false;
		
		contact = DataAccess.getContactByContact(idOtherPlayer, getId());
		if (contact == null || contact.getType().equals(Contact.TYPE_IGNORE))
			return false;
		
		return true;
	}
	
	public boolean isIgnoring(int idOtherPlayer) {
		Contact contact = DataAccess.getContactByContact(getId(), idOtherPlayer);
		
		return contact != null && contact.getType().equals(Contact.TYPE_IGNORE);
	}
	
	public Continuation getContinuation() {
		return continuation;
	}
	
	public void setContinuation(Continuation continuation) {
		Player cachedPlayer = DataAccess.getPlayerById(getId());
		
		if (cachedPlayer != this)
			cachedPlayer.continuation = continuation;
		this.continuation = continuation;
	}
	
	public String getSecurityKey() {
		return securityKey;
	}
	
	public void setSecurityKey(String securityKey) {
		Player cachedPlayer = DataAccess.getPlayerById(getId());
		
		if (cachedPlayer != this)
			cachedPlayer.securityKey = securityKey;
		this.securityKey = securityKey;
	}
	
	public void generateSecurityKey() {
		setSecurityKey(RandomStringUtils.randomAlphanumeric(20));
	}
	
	public int getIdCurrentArea() {
		return idCurrentArea;
	}

	public void setIdCurrentArea(int idCurrentArea) {
		Player cachedPlayer = DataAccess.getPlayerById(getId());
		
		if (cachedPlayer != this)
			cachedPlayer.idCurrentArea = idCurrentArea;
		this.idCurrentArea = idCurrentArea;
	}
	
	public long getLastPing() {
		return lastPing;
	}
	
	public void setLastPing(long lastPing) {
		Player cachedPlayer = DataAccess.getPlayerById(getId());
		
		if (cachedPlayer != this)
			cachedPlayer.lastPing = lastPing;
		this.lastPing = lastPing;
	}
	
	public List<Tactic> getTactics() {
		return DataAccess.getTacticsByPlayer(getId());
	}
	
	public LotteryTicket getLotteryTicket() {
        return DataAccess.getLotteryTicketByPlayer(getId());
}
	
	/**
	 * Teste si le joueur a un droit d'accès donné.
	 * 
	 * @param right Le droit d'accès testé, parmi {@link Player#USER},
	 * {@link Player#PREMIUM}, {@link Player#MODERATOR},
	 * {@link Player#ADMINISTRATOR} ou {@link Player#SUPER_ADMINISTRATOR}. Il
	 * est également possible de tester si le joueur a plusieurs droits en
	 * utilisant l'opérateur | (ou logique).<br/>Exemple :
	 * <code>hasRight(Player.PREMIUM | Player.MODERATOR)</code>
	 * 
	 * @return Renvoie <code>true</code> si l'utilisateur a le droit d'accès.
	 */
	public boolean hasRight(int right) {
		if (right == USER)
			return true;
		return (getRights() & right) != 0;
	}
	
	/**
	 * Identique à hasRight(Player.PREMIUM) , utilisé pour une meilleure lisibilité
	 * @return Renvoie <code>true</code> si l'utilisateur a un compte premium
	 */
	public boolean isPremium() {
		return hasRight(Player.PREMIUM);
	}
	
	public boolean isModerator() {
		return hasRight(Player.MODERATOR);
	}
	
	public boolean isAdministrator() {
		return hasRight(Player.ADMINISTRATOR);
	}
	
	public List<Message> getMessages() {
		return DataAccess.getMessagesByPlayer(getId());
	}
	
	public List<Message> getSentMessages() {
		return DataAccess.getMessagesByAuthor(getId());
	}
	
	public int getBookmarkedMessagesCount(){
		List<Message> list = new ArrayList<Message>(DataAccess.getMessagesByPlayer(getId()));
		int count = 0;
		
		for(Message message:list){
			if(message.isBookmarked())
				count++;
		}
		
		return count;
	}
	
	public int getProductsCount(int type) {
		return getIdAlly() == 0 ? 0 : getAlly().getProductsCount(type);
	}
	
	public List<Fleet> getFleets() {
		return DataAccess.getFleetsByOwner(getId());
	}

	public StarSystem getFirstSystem() {
		// Recherche le système mère du joueur
		long minDate = Long.MAX_VALUE;
		StarSystem firstSystem = null;
		List<StarSystem> systems = getSystems();
		
		synchronized (systems) {
			for (StarSystem system : systems)
				if (system.getColonizationDate() < minDate) {
					minDate = system.getColonizationDate();
					firstSystem = system;
				}
		}
		
		return firstSystem;
	}
	
	public List<Achievement> getAchievements() {
		return DataAccess.getAchievementsByPlayer(getId());
	}
	
	public List<Structure> getStructures() {
		return DataAccess.getStructuresByOwner(getId());
	}
	
	public List<StarSystem> getSystems() {
		return DataAccess.getSystemsByOwner(getId());
	}
	
	public List<Treaty> getTreaties() {
		return DataAccess.getPlayerTreaties(getId());
	}

	public List<Research> getResearches() {
		return DataAccess.getResearchesByPlayer(getId());
	}

	public List<VisitedArea> getVisitedAreas() {
		return DataAccess.getVisitedAreasByPlayer(getId());
	}
	
	public String getTreatyWithPlayer(int idPlayer) {
		Player otherPlayer = DataAccess.getPlayerById(idPlayer);
		return otherPlayer != null ?
				getTreatyWithPlayer(otherPlayer) : Treaty.NEUTRAL;
	}
	
	public String getTreatyWithPlayer(Player otherPlayer) {
		if (otherPlayer.getId() == getId())
			return Treaty.PLAYER;
		
		if (getIdAlly() != 0) {
			Ally otherAlly = otherPlayer.getAlly();
			
			if (otherAlly != null) {
				// Joueurs dans la meme alliance
				if (getIdAlly() == otherAlly.getId())
					return Treaty.ALLY;
				
				// Traité entre alliances
				List<AllyTreaty> allyTreaties = otherAlly.getTreaties();
				
				for (AllyTreaty allyTreaty : allyTreaties) {
					if (allyTreaty.implyAlly(getIdAlly())) {
						if (allyTreaty.getType().equals(AllyTreaty.TYPE_WAR))
							return Treaty.ENEMY;
						else if ( allyTreaty.getSource()==0 )
							return allyTreaty.getPact();
					}
				}
			}
		}
		

		//Traité PNJ
		int playerId = 0;
		if(getLogin().contains(AiTools.PNJ_DEFENDER_AI_LOGIN)){
			if(DataAccess.getPnjDefenderByPnj(getId())!=null){
					playerId = DataAccess.getPnjDefenderByPnj(getId()).getIdPlayer();
				if(playerId!=0){
					Player player2 = DataAccess.getPlayerById(playerId);
					//Si le joueur protégé à une ally
					if(player2.getIdAlly()!=0){
						if(player2.getAlly()==otherPlayer.getAlly()){
							return Treaty.ALLIED;
						}
					}
					else // Le joueur n'a pas d'ally
					{
						if(player2.getId()==otherPlayer.getId())
							return Treaty.ALLIED;
					}
				}
			}
		}
		
		// Traité entre joueurs
		List<Treaty> playerTreaties = getTreaties();
		
		for (Treaty treaty : playerTreaties) {
			if (treaty.implyPlayer(otherPlayer.getId())) {
				if (treaty.getPact().equals(Treaty.TYPE_WAR))
					return Treaty.ENEMY;
				else if( treaty.getSource()==0 )
					return treaty.getPact();
			}
		}
		


			

		return Treaty.NEUTRAL;
	}
	
	public String getTreatyWithPlayer(HashMap<Integer, String> treatiesCache,
			int idPlayer) {
		int hash = getId() + 1000000000 + idPlayer;
		String treaty = treatiesCache.get(hash);
		
		if (treaty == null) {
			treaty = getTreatyWithPlayer(idPlayer);
			treatiesCache.put(hash, treaty);
		}
		
		return treaty;
	}

	public String getTreatyWithPlayer(HashMap<Long, String> treatiesCache,
			Player otherPlayer) {
		long hash = getId() * 1000000000l + otherPlayer.getId();
		String treaty = treatiesCache.get(hash);

		if (treaty == null) {
			treaty = getTreatyWithPlayer(otherPlayer);
			treatiesCache.put(hash, treaty);
		}
		
		return treaty;
	}
	
	/*
	 * Cette fonction renvoie true si le joueur est allié avec l'autre joueur
	 * ou a un pacte
	 */
	public boolean isAlliedWithPlayer(Player otherPlayer) {
		if (otherPlayer.getId() == getId())
			return true;
		if (getIdAlly() != 0) {
			Ally otherAlly = otherPlayer.getAlly();
			
			if (otherAlly != null) {
				if (getIdAlly() == otherAlly.getId())
					return true;
				List<AllyTreaty> allyTreaties = otherAlly.getTreaties();
				
				for (AllyTreaty allyTreaty : allyTreaties) {
					if (allyTreaty.implyAlly(getIdAlly())) {
						if (allyTreaty.getType().equals(AllyTreaty.TYPE_WAR))
							return false;
						else if ( allyTreaty.getSource()==0 )
							return true;
					}
				}
				
			}
		}
		
		//Traité PNJ
		//Player = pnjDefender
		//otherPlayer = joueur
		//Player2 = joueur lié au pnjDefender
		int playerId = 0;
		if(getLogin().contains(AiTools.PNJ_DEFENDER_AI_LOGIN)){
			if(DataAccess.getPnjDefenderByPnj(getId())!=null){
					playerId = DataAccess.getPnjDefenderByPnj(getId()).getIdPlayer();
				if(playerId!=0){
					Player player2 = DataAccess.getPlayerById(playerId);
					//Si le joueur protégé à une ally
					if(player2.getIdAlly()!=0){
						if(player2.getAlly()==otherPlayer.getAlly()){
							return true;
						}
					}
					else // Le joueur n'a pas d'ally
					{
						if(player2.getId()==otherPlayer.getId())
							return true;
					}
							
				}
			}
		}
		
		
		List<Treaty> playerTreaties = getTreaties();
			
			for (Treaty treaty : playerTreaties) {
				if (treaty.implyPlayer(otherPlayer.getId())) {
					if (treaty.getPact().equals(Treaty.TYPE_WAR))
						return false;
					else if ( treaty.getSource()==0 )
						return true;
				}
			}

			

			
			return false;
	}
	
public boolean isEnemyWithPlayer(Player otherPlayer) {
	if (otherPlayer.getId() == getId())
		return false;
		if (getIdAlly() != 0) {
			Ally otherAlly = otherPlayer.getAlly();
			
			if (otherAlly != null) {
				if (getIdAlly() == otherAlly.getId())
					return false;
				List<AllyTreaty> allyTreaties = otherAlly.getTreaties();
				
				for (AllyTreaty allyTreaty : allyTreaties) {
					if (allyTreaty.implyAlly(getIdAlly())) {
						if (allyTreaty.getType().equals(AllyTreaty.TYPE_WAR))
							return true;
						else if ( allyTreaty.getSource()==0 )
							return false;
					}
				}
				
			}
		}
		List<Treaty> playerTreaties = getTreaties();
			
			for (Treaty treaty : playerTreaties) {
				if (treaty.implyPlayer(otherPlayer.getId())) {
					if (treaty.getPact().equals(Treaty.TYPE_WAR))
						return true;
					else if ( treaty.getSource()==0 )
						return false;
				}
			}
			return false;
	}

public List<Treaty> getTotalPacts() {
	List<Treaty> allTreaties = DataAccess.getPlayerTreaties(getId());
	List<Treaty> totalPacts = new ArrayList<Treaty>();
	for(Treaty treaty : allTreaties) {
		if(treaty.getType().equals(Treaty.TYPE_TOTAL)  && treaty.getSource()==0) {
			totalPacts.add(treaty);
		}
	}
	return totalPacts;
}

public List<Treaty> getDefensivePacts() {
	List<Treaty> allTreaties = DataAccess.getPlayerTreaties(getId());
	List<Treaty> defensivePacts = new ArrayList<Treaty>();
	for(Treaty treaty : allTreaties) {
		if(treaty.getType().equals(Treaty.TYPE_DEFENSIVE)  && treaty.getSource()==0) {
			defensivePacts.add(treaty);
		}
	}
	return defensivePacts;
}

public List<Treaty> getNonAgressionPacts() {
	List<Treaty> allTreaties = DataAccess.getPlayerTreaties(getId());
	List<Treaty> nonAgressionPacts = new ArrayList<Treaty>();
	for(Treaty treaty : allTreaties) {
		if(treaty.getType().equals(Treaty.TYPE_ALLY) && treaty.getSource()==0) {
			nonAgressionPacts.add(treaty);
		}
	}
	return nonAgressionPacts;
}

public List<Treaty> getWars() {
	List<Treaty> allTreaties = DataAccess.getPlayerTreaties(getId());
	List<Treaty> wars = new ArrayList<Treaty>();
	for(Treaty treaty : allTreaties) {
		if(treaty.getType().equals(Treaty.TYPE_WAR)) {
			wars.add(treaty);
		}
	}
	return wars;
}

public Treaty getPlayerTreatyWithPlayer(Player otherPlayer) {
	List<Treaty> treaties = this.getTreaties();
	for(Treaty treaty : treaties) {
		if(treaty.implyPlayer(otherPlayer.getId())) {
			return treaty;
		}
	}
	return null;
}

public void checkNewRelationships(Treaty newTreaty, boolean hasDeclaredWar) {
	if(newTreaty.getType().equals(Treaty.TYPE_WAR)) {
		List<Treaty> totalPacts = this.getTotalPacts();
		for(Treaty pact : totalPacts){ //Pour chaque pacte total
			Player allied = pact.getOtherPlayer(this.getId());
			String alliedTreaty = allied.getTreatyWithPlayer(newTreaty.getOtherPlayerId(this.getId()));
			//Si on est neutre avec le nouvel ennemi potentiel
			if(!Treaty.isPact(alliedTreaty) && !alliedTreaty.equals(Treaty.ENEMY)) {
				new Treaty(allied.getId(),
						newTreaty.getOtherPlayerId(this.getId()),
						Treaty.TYPE_WAR,0).save();
				DataAccess.save(new Event(
						Event.EVENT_PLAYER_DECLARE_WAR_TOTAL,
						Event.TARGET_PLAYER, allied.getId(), 0, -1, -1,
						getLogin(),newTreaty.getOtherPlayer(this.getId()).getLogin()));
				DataAccess.save(new Event(
						Event.EVENT_PLAYER_WAR_DECLARED_TOTAL,
						Event.TARGET_PLAYER,newTreaty.getOtherPlayerId(this.getId()), 0, -1, -1,
						getLogin(),allied.getLogin()));
			}
		}
		/*
		 * Si la déclaration de guerre n'a pas été déclenchée par le joueur
		 * alors on regarde ses pactes défensif
		 */
		if(!hasDeclaredWar) {
			List<Treaty> defensivePacts = this.getDefensivePacts();
			for(Treaty pact : defensivePacts){ //Pour chaque pacte défensif
				Player allied = pact.getOtherPlayer(this.getId());
				String alliedTreaty = allied.getTreatyWithPlayer(newTreaty.getOtherPlayerId(this.getId()));
				//Si le pacte conclu avec le nouvel ennemi n'est pas Total, et si l'allié n'est pas déjà en guerre
				if(!Treaty.isPact(alliedTreaty) && !alliedTreaty.equals(Treaty.TYPE_WAR)) {
					new Treaty(allied.getId(),
							newTreaty.getOtherPlayerId(this.getId()),
							AllyTreaty.TYPE_WAR,0).save();
					DataAccess.save(new Event(
							Event.EVENT_PLAYER_DECLARE_WAR_DEFENSIVE,
							Event.TARGET_PLAYER, allied.getId(), 0, -1, -1,
							getLogin(),newTreaty.getOtherPlayer(this.getId()).getLogin()));
					DataAccess.save(new Event(
							Event.EVENT_PLAYER_WAR_DECLARED_DEFENSIVE,
							Event.TARGET_PLAYER,newTreaty.getOtherPlayerId(this.getId()), 0, -1, -1,
							getLogin(),allied.getLogin()));
				}
			}
		}
	}
}

	public Ally getAlly() {
		if (getIdAlly() == 0)
			return null;
		return DataAccess.getAllyById(getIdAlly());
	}

	public String getAllyName() {
		if (getIdAlly() == 0)
			return "";
		return getAlly().getName();
	}

	public String getAllyTag() {
		if (getIdAlly() == 0)
			return "";
		return getAlly().getTag();
	}
	
	public boolean isMale() {
		return !isSex();
	}
	
	public boolean isFemale() {
		return isSex();
	}
	
	public byte[] getAreasVisibility() {
		List<Area> areas = DataAccess.getAllAreas();
		byte[] areasVisibility = new byte[areas.size() * 2 + 1];
		
		long now = Utilities.now();
		List<VisitedArea> visitedAreas = getVisitedAreas();
		
		// Secteurs visités par le joueur
		synchronized (visitedAreas) {
			for (VisitedArea visitedArea : visitedAreas)
				if (visitedArea.getDate() < now) {
					areasVisibility[visitedArea.getIdArea()] =
						Area.VISIBILITY_VISITED;
				}
		}
		
		// Secteurs sous domination de l'alliance du joueur
		if (getIdAlly() != 0) {
			synchronized (areas) {
				for (Area area : areas) {
					if (getIdAlly() == area.getIdDominatingAlly() &&
							areasVisibility[area.getId()] ==
								Area.VISIBILITY_VISITED)
						areasVisibility[area.getId()] = Area.VISIBILITY_ALLY;
				}
			}
		}
		
		// Secteurs dans lesquels le joueur a une flotte, un système ou une
		// structure
		List<Fleet> fleets = getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets)
				areasVisibility[fleet.getIdCurrentArea()] = Area.VISIBILITY_PLAYER;
		}
		
		List<StarSystem> systems = getSystems();
		
		synchronized (systems) {
			for (StarSystem system : systems)
				areasVisibility[system.getIdArea()] = Area.VISIBILITY_PLAYER;
		}
		
		List<Structure> structures = getStructures();
		
		synchronized (structures) {
			for (Structure structure : structures)
				areasVisibility[structure.getIdArea()] = Area.VISIBILITY_PLAYER;
		}
		
		if (hasRight(ADMINISTRATOR)) {
			for (Area area : areas) {
				areasVisibility[(int) area.getId()] =
					Area.VISIBILITY_PLAYER;
			}
		}
		
		return areasVisibility;
	}
	
	public byte[] getSectorsVisibility() {
		return getSectorsVisibility(getAreasVisibility());
	}
	
	public byte[] getSectorsVisibility(byte[] areasVisibility) {
		List<Sector> sectors = DataAccess.getAllSectors();
		
		byte[] sectorsVisibility = new byte[sectors.size() + 1];
		
		synchronized (sectors) {
			for (Sector sector : sectors) {
				List<Area> areas = sector.getAreas();
				
				synchronized (areas) {
					byte visibility = Area.VISIBILITY_NONE;
					
					for (Area area : areas) {
						if (areasVisibility[area.getId()] > visibility)
							visibility = areasVisibility[area.getId()];
					}
					
					sectorsVisibility[sector.getId()] = visibility;
				}
			}
		}
		
		return sectorsVisibility;
	}
	
	// Si la zone a déjà été visitée, elle n'est pas ajoutée une 2e fois
	// Renvoie true si un nouveau secteur a été ajouté
	public boolean addVisitedArea(Area area, long date) {
		// Vérifie que la zone n'a pas déjà été visitée
		int id = getId();
		List<VisitedArea> visitedAreas = getVisitedAreas();
		
		synchronized (visitedAreas) {
			for (VisitedArea visitedArea : visitedAreas)
				if (visitedArea.getIdArea() == area.getId())
					return false;
		}
		
		VisitedArea newVisitedArea = new VisitedArea(
				id, area.getId(), date);
		newVisitedArea.save();
		
		return true;
	}
	
	/**
	 * Teste si le joueur a déjà voté pour une élection donnée.
	 * 
	 * @param idElection L'identifiant de l'élection testée.
	 * 
	 * @return Renvoie <code>true</code> si le joueur a déjà voté pour
	 * l'élection.
	 */
	public boolean hasVotedElection(int idElection){
		List<ElectionVoter> votes = DataAccess.getElectionVoterByPlayer(getId());
		
		synchronized (votes) {
			for (ElectionVoter vote : votes) {
				if (vote.getIdElection() == idElection)
					return true;
			}
		}
		
		return false;
	}
	
	/**
     * Check si le joueur a voté ou pas pour un vote
     * 
     * @param idPlayer
     * @param idVote
     * @return boolean
     */
    public boolean hasVoted(int idVote){
    	List<AllyVoter> votes = DataAccess.getVoteVoter(idVote);
    	synchronized (votes) {
			for(AllyVoter vote:votes){
				if( vote.getIdPlayer()==getId() )
		    		return true;
			}
		}    	
    	return false;
    }
	
    /**
     * Calcul le nombre de messages reçus depuis date
     * (Pourra peut etre renvoyer List<> si nécessaire
     * on utilisera alors un size())
     * @param	Data a partir de laquelle regardée
     * @return 	Nombre de messages
     */
    public int countMessagesSince(long date){
    	List<Message> messages = getMessages();
		int count = 0;
		
		synchronized (messages) {
			for (Message message : messages) {
				if (message.getDate() > date)
					count++;
			}
		}
		
    	return count;
    }
    
    public List<Event> getEvents() {
		List<Event> events = new ArrayList<Event>(
				DataAccess.getEventByTargetId(getId(), Event.TARGET_PLAYER));
		if (getIdAlly() != 0)
			events.addAll(DataAccess.getEventByTargetId(
					getIdAlly(), Event.TARGET_ALLY));
		return events;
	}
    
    public int countEventsSince(long date){
    	List<Event> events = getEvents();
		int count = 0;
		
		for (Event event : events) {
			if (event.getDate() > date)
				count++;
		}
		
    	return count;
    }
    
	/**
	 * Calcule le total des points de recherche générés par les laboratoires et
	 * centre de recherche du joueur. Cette fonction suppose qu'il n'y a aucun
	 * laboratoire ou centre de recherche en cours de construction qui se termine
	 * sur un système.
	 * 
	 * @return Le total des points de recherche générés par les laboratoires et
	 * centres de recherche du joueur.
	 */
	public int getResearchPoints() {
		double researchPoints = 0;
		List<StarSystem> systems = getSystems();
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				researchPoints += system.getProduction(Building.LABORATORY) *
					system.getProduction(Building.RESEARCH_CENTER) *
					system.getProductionModifier();
			}
		}
		
		return (int) Math.floor(researchPoints);
	}
	
	public static Player updateCredits(Player player) {
		long now = Utilities.now();
		double credits = 0;
		
		// On récupère les systèmes du joueur
		List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
		
		for (StarSystem system : systems) {
			boolean updated = false;
			
			// Si des infrastructures civiles ou corporations sont terminés
			// depuis la dernière fois que le système a été mis à jour, on
			// remet à jour le système pour recalculer les crédits générés
			// par le système
			Building currentBuilding = system.getCurrentBuilding();
			
			if (currentBuilding != null &&
					currentBuilding.getEnd() < now) {
				Building nextBuilding = system.getNextBuilding();
				
				if (currentBuilding.getType() == Building.CORPORATIONS ||
						currentBuilding.getType() ==
							Building.CIVILIAN_INFRASTRUCTURES) {
					// Infrastructures civiles ou corporations en cours de
					// construction terminé
					system = StarSystem.updateSystem(system);
					updated = true;
				} else if (nextBuilding != null &&
						nextBuilding.getEnd() < now) {
					if (nextBuilding.getType() == Building.CORPORATIONS ||
							nextBuilding.getType() ==
								Building.CIVILIAN_INFRASTRUCTURES) {
						// Infrastructures civiles ou corporations en attente
						// terminé
						system = StarSystem.updateSystem(system);
						updated = true;
					}
				}
			}
			
			// Calcule les points de recherche générés par les laboratoires sur les
			// systèmes qui n'ont pas été mis à jour
			if (!updated) {
				// Calcule la croissance de la population du système, jusqu'à
	    		// l'heure de la mise à jour
				double population = system.getPopulation();
				long frame = now - system.getLastPopulationUpdate();
				double maxPopulation = system.getProduction(
						Building.CIVILIAN_INFRASTRUCTURES);
				double growth = GameConstants.POPULATION_GROWTH * frame * maxPopulation;
				
				// Limite la population en fonction du nombre d'infrastructures civiles
				double growthCoef = .5;
				if (population + growth > maxPopulation) {
					growthCoef = .5 * (maxPopulation - population) / growth +
						(1 - (maxPopulation - population) / growth);
					growth = maxPopulation - population;
				}
				
				// Calcule les crédits gagnés avec les corporations
				credits += (population + growth * growthCoef) * frame *
						system.getProduction(Building.CORPORATIONS) *
						GameConstants.EXPLOITATION_RATE *
						system.getProductionModifier();
				
				population += growth;
				
				synchronized (system.getLock()) {
					system = DataAccess.getEditable(system);
					system.setPopulation(population);
					system.setLastPopulationUpdate(now);
					system.save();
				}
			}
		}
		
		// Met à jour le joueur
		player = DataAccess.getPlayerById(player.getId());
		
		// Ajoute les crédits au joueur
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits((long) credits);
			player.save();
		}
		
		return player;
	}
	
	public void updateResearch() {
		long now = Utilities.now();
		double researchPoints = 0;
		
		// On récupère les systèmes du joueur
		List<StarSystem> systems = new ArrayList<StarSystem>(getSystems());
		
		for (StarSystem system : systems) {
			boolean updated = false;
			
			// Si un laboratoire ou un centre de recherche est terminé
			// depuis la dernière fois que le système a été mis à jour, on
			// remet à jour le système pour recalculer les points de
			// recherche générés par le système
			Building currentBuilding = system.getCurrentBuilding();
			
			if (currentBuilding != null &&
					currentBuilding.getEnd() < now) {
				Building nextBuilding = system.getNextBuilding();
				
				if (currentBuilding.getType() == Building.LABORATORY ||
						currentBuilding.getType() ==
							Building.RESEARCH_CENTER) {
					// Laboratoire ou centre de recherche en cours de
					// construction terminé
					system = StarSystem.updateSystem(system);
					updated = true;
				} else if (nextBuilding != null &&
						nextBuilding.getEnd() < now) {
					if (nextBuilding.getType() == Building.LABORATORY ||
							nextBuilding.getType() ==
								Building.RESEARCH_CENTER) {
						// Laboratoire ou centre de recherche en attente terminé
						system = StarSystem.updateSystem(system);
						updated = true;
					}
				}
			}
			
			// Calcule les points de recherche générés par les laboratoires sur les
			// systèmes qui n'ont pas été mis à jour
			if (!updated) {
				researchPoints +=
					system.getProduction(Building.LABORATORY) *
					system.getProduction(Building.RESEARCH_CENTER) *
					(now - system.getLastResearchUpdate()) *
					system.getProductionModifier() *
					GameConstants.RESEARCH_RATE;
				
				synchronized (system.getLock()) {
					system = DataAccess.getEditable(system);
					system.setLastResearchUpdate(now);
					system.save();
				}
			}
		}
		
		// Ajoute les points de recherche à la recherche en cours
		if (researchPoints > 0)
			addResearchPoints(researchPoints);
	}
	
	/**
	* Ajoute les points à la recherche en cours du joueur. Si le joueur n'a pas
	* de recherche en cours, cette méthode est sans effet.
	*
	* @param points Le nombre de points à ajouter à la recherche en cours.
	*/
	public void addResearchPoints(double points) {
		// Récupère la recherche en cours du joueur, si elle est définie
		List<Research> researches = getResearches();
		Research currentResearch = null;
		
		synchronized (researches) {
			for (Research research : researches) {
				if (research.getQueuePosition() == 0) {
					currentResearch = research;
					break;
				}
			}
		}
		
		if (currentResearch != null) {
			// % de la recherche effectué
			long length = (long) Math.ceil(currentResearch.getLength() *
				Math.pow(.95, Advancement.getAdvancementLevel(getId(),
					Advancement.TYPE_RESEARCH)) / GameConstants.TIME_UNIT);

			
			double percentDone = points / length;
			
			// Teste si la recherche de la technologie est achevée
			// (l'avancement dépasse 100%)
			if (currentResearch.getProgress() + percentDone >= 1) {
				synchronized (currentResearch.getLock()) {
					Research research = DataAccess.getEditable(currentResearch);
					research.setQueuePosition(-1);
					research.setProgress(1);
					research.save();
				}
				
				double pointsUsed = (currentResearch.getLength()-
					currentResearch.getProgress()*currentResearch.getLength());
				
				points -= pointsUsed;
				
				researches = new ArrayList<Research>(researches);
				
				for (Research research : researches) {
					if (research.getQueuePosition() > 0) {
						synchronized (research.getLock()) {
							research = DataAccess.getEditable(research);
							research.setQueuePosition(research.getQueuePosition() - 1);
							research.save();
						}
					}
				}


//				addResearchPoints(points * (currentResearch.getProgress() +
//						percentDone - 1) / currentResearch.getProgress());

				
				addResearchPoints(points);
				
				Event event = new Event(
					Event.EVENT_NEW_TECHNOLOGY,
					Event.TARGET_PLAYER, getId(),
					0, -1, -1,
					String.valueOf(currentResearch.getIdTechnology()));
				event.save();
				
				UpdateTools.queueNewEventUpdate(getId(), false);
			} else {
				synchronized (currentResearch.getLock()) {
					Research research = DataAccess.getEditable(currentResearch);
					research.setProgress(Math.max(0, research.getProgress() + percentDone));
					research.save();
				}
			}
		}
	}
	
	public boolean hasResearchedTechnology(int idTechnology) {
		List<Research> researches = getResearches();
		
		synchronized (researches) {
			for (Research research : researches)
				if (research.getIdTechnology() == idTechnology) {
					return research.getProgress() == 1;
				}
		}
		
		return false;
	}
	
	public boolean hasResearchedShip(int idShip) {
		return hasResearchedShip(Ship.SHIPS[idShip]);
	}
	
	public boolean hasResearchedShip(Ship ship) {
		int[] requiredTechnologies = ship.getTechnologies();
		
		for (int idTechnology : requiredTechnologies)
			if (!hasResearchedTechnology(idTechnology))
				return false;
		
		return true;
	}
	
	public void addCredits(long credits) {
		setCredits(getCredits() + credits);
	}
	
	public int getLevel() {
		long xp = getXp();
		int level = 1;
		while (true) {
			if (xp < getLevelXp(level + 1))
				return level;
			level++;
		}
	}
	
	// Cette fonction n'ajoute pas d'XP aux flottes PNJ ; pour ajouter de l'XP
	// aux PNJ, utiliser setXp
	public void addXp(long xp) {
		// Les PNJ ne gagnent pas d'XP
		if (!isAi()) {
			long newXp = this.getXp() + xp;
			if (newXp > 829858628)
				newXp = 829858628;
			
			this.setXp(newXp);
		}
	}
	
	public long getFleetCost() {
		int fleetsCount = 0;
		List<Fleet> fleets = getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets)
				if (!fleet.isDelude())
					fleetsCount++;
		}
		
		double cost = 1000;
		cost *= Math.pow(1.5, Math.min(fleetsCount, 5));
		if (fleetsCount > 5)
		   cost *= Math.pow(1.4, Math.min(fleetsCount - 5, 5));
		if (fleetsCount > 10)
		   cost *= Math.pow(1.3, Math.min(fleetsCount - 10, 5));
		if (fleetsCount > 15)
		   cost *= Math.pow(1.2, Math.min(fleetsCount - 15, 5));
		if (fleetsCount > 20)
		   cost *= Math.pow(1.1, (fleetsCount - 20));
		
		cost *= Math.pow(.95, Advancement.getAdvancementLevel(
				getId(), Advancement.TYPE_FLEETS_COST));
		
		
		cost *= Product.getProductEffect(Product.PRODUCT_SULFARIDE, 
				getProductsCount(Product.PRODUCT_SULFARIDE));
		
		
		return (long) Math.ceil(cost / 100) * 100;
	}
	
	public long getMigrationCost() {

		double cost = 4000;
		cost*=Math.pow(getLevel(),2);
		
		return (long) (Math.ceil(cost / 100)) * 100;
	
	}
	
	public int getColonizationPoints() {
		 // 1 points par niveau d'XP - 1 (niveau 1) + le système de départ
		int points = getLevel() + GameConstants.SYSTEM_COST;
		
		points -= GameConstants.SYSTEM_COST * getSystems().size();
		
		// Compte les systèmes en cours de colonisation
		List<Fleet> fleets = getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_COLONIZE)) {
					StarSystem system = fleet.getSystemOver();
					
					if (system != null && system.getIdOwner() == 0) {
						points -= GameConstants.SYSTEM_COST;
					}
				}
			}
		}
		
		return points + Advancement.getAdvancementLevel(
			getId(), Advancement.TYPE_COLONIZATION_POINTS);
	}
	
	public boolean isRegistered() {
		return getRegistration().equals("");
	}
	
	// Systèmes appartenant au joueur ou ses alliés dans un secteur donné
	public List<Point> getAllySystems(Area area) {
		List<Point> allySystems = new ArrayList<Point>();
		for (StarSystem system : area.getColonizedSystems())
			try {
				if (system.getIdOwner() == getId() || (
						getIdAlly() != 0 &&
						system.getIdOwner() != 0 &&
						getIdAlly() == system.getOwner().getIdAlly()))
					allySystems.add(new Point(system.getX(), system.getY()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return allySystems;
	}
	
	// Flottes appartenant au joueur ou ses alliés dans un secteur donné
	public List<Point> getAllyFleets(Area area) {
		List<Point> allyFleets = new ArrayList<Point>();
		for (Fleet fleet : area.getFleets())
			try {
				if (fleet.getIdOwner() == getId() || (
						getIdAlly() != 0 &&
						getIdAlly() == fleet.getOwner().getIdAlly()))
					allyFleets.add(new Point(fleet.getCurrentX(), fleet.getCurrentY()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return allyFleets;
	}
	
	// Structures appartenant au joueur ou ses alliés dans un secteur donné
	public List<Structure> getAllyStructures(Area area) {
		List<Structure> allyStructures = new ArrayList<Structure>();
		List<Structure> structures = area.getStructures();
		synchronized (structures) {
			for (Structure structure : structures) {
				try {
					if (structure.getIdOwner() == getId() || (
							getIdAlly() != 0 &&
							getIdAlly() == structure.getOwner().getIdAlly()))
						allyStructures.add(structure);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return allyStructures;
	}
	
	// Balises d'observation appartenant au joueur ou ses alliés dans un secteur donné
	public List<Point> getAllyObserverWards(Area area) {
		List<Point> allyWards = new ArrayList<Point>();
		List<Ward> wards = area.getWards();
		synchronized (wards) {
			for (Ward ward : wards)
				try {
					if (ward.getType().startsWith(Ward.TYPE_OBSERVER) && (
							ward.getIdOwner() == getId() || (
							getIdAlly() != 0 &&
							getIdAlly() == ward.getOwner().getIdAlly())))
						allyWards.add(new Point(ward.getX(), ward.getY()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return allyWards;
	}

	// Balises de détection appartenant au joueur ou ses alliés dans un secteur donné
	public List<Point> getAllySentryWards(Area area) {
		List<Point> allyWards = new ArrayList<Point>();
		List<Ward> wards = area.getWards();
		synchronized (wards) {
			for (Ward ward : wards)
				try {
					if (ward.getType().startsWith(Ward.TYPE_SENTRY) && (
							ward.getIdOwner() == getId() || (
							getIdAlly() != 0 &&
							getIdAlly() == ward.getOwner().getIdAlly())))
						allyWards.add(new Point(ward.getX(), ward.getY()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return allyWards;
	}
	
	// Stations spatiales appartenant au joueur ou ses alliés dans un secteur donné
	public List<Point> getAllySpaceStations(Area area) {
		List<Point> allySpaceStations = new ArrayList<Point>();
		
		if (getIdAlly() != 0) {
		for (SpaceStation spaceStation : area.getSpaceStations())
			if (getIdAlly() == spaceStation.getIdAlly())
				allySpaceStations.add(new Point(spaceStation.getX(), spaceStation.getY()));
		}
		
		return allySpaceStations;
	}
	
	public boolean isLocationVisible(Area area, int x, int y) {
		if (hasRight(ADMINISTRATOR))
			return true;
		
		if (getIdAlly() != 0 && getIdAlly() == area.getIdDominatingAlly())
			return true;
		
		// Systèmes appartenant au joueur ou ses alliés
		List<Point> allySystems = getAllySystems(area);
		
		// Flottes appartenant au joueur ou ses alliés
		List<Point> allyFleets = getAllyFleets(area);
		
		// Balises d'observation appartenant au joueur ou ses alliés
		List<Point> allyObserverWards = getAllyObserverWards(area);
		
		// Balises d'observation appartenant au joueur ou ses alliés
		List<Point> allySpaceStations = getAllySpaceStations(area);
		
		// Structures appartenant au joueur ou ses alliés
		List<Structure> allyStructures = getAllyStructures(area);
		
		return isLocationVisible(area, x, y, allySystems, allyFleets,
			allyObserverWards, allySpaceStations, allyStructures);
	}
	
	public boolean isLocationVisible(Area area, int x, int y,
			List<Point> allySystems, List<Point> allyFleets,
			List<Point> allyObserverWards, List<Point> allySpaceStations,
			List<Structure> allyStructures) {
		if (hasRight(ADMINISTRATOR))
			return true;
		
		return isLocationVisible(area, x, y, 0, allySystems, allyFleets,
				allyObserverWards, allySpaceStations, allyStructures);
	}
	
	public boolean isLocationVisible(Area area, int x, int y, int radius,
			List<Point> allySystems, List<Point> allyFleets,
			List<Point> allyObserverWards, List<Point> allySpaceStations,
			List<Structure> allyStructures) {
		if (hasRight(ADMINISTRATOR))
			return true;
		
		if (getIdAlly() != 0 && getIdAlly() == area.getIdDominatingAlly())
			return true;
		
		int extraLos = 4 * Advancement.getAdvancementLevel(
				getId(), Advancement.TYPE_LINE_OF_SIGHT);
		
		int los = GameConstants.LOS_SYSTEM;
		for (Point allySystem : allySystems) {
			int dx = allySystem.x - x;
			int dy = allySystem.y - y;
			
			if (dx * dx + dy * dy <= los * los - radius * radius)
				return true;
		}
		
		los = extraLos + GameConstants.LOS_FLEET;
		for (Point allyFleet : allyFleets) {
			int dx = allyFleet.x - x;
			int dy = allyFleet.y - y;
			
			if (dx * dx + dy * dy <= los * los - radius * radius)
				return true;
		}
		
		los = extraLos + GameConstants.LOS_WARD;
		synchronized (allyObserverWards) {
			for (Point allyObserverWard : allyObserverWards) {
				int dx = allyObserverWard.x - x;
				int dy = allyObserverWard.y - y;
				
				if (dx * dx + dy * dy <= los * los - radius * radius)
					return true;
			}
		}
		
		los = GameConstants.LOS_SPACE_STATION;
		for (Point allySpaceStation : allySpaceStations) {
			int dx = allySpaceStation.x - x;
			int dy = allySpaceStation.y - y;
			
			if (dx * dx + dy * dy <= los * los - radius * radius)
				return true;
		}
		
		synchronized (allyStructures) {
			for (Structure structure : allyStructures) {
				if (structure.getBounds().contains(x, y))
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isLocationRevealed(int x, int y, Area area) {
		if (hasRight(ADMINISTRATOR))
			return true;
		
		// Balises d'observation appartenant au joueur ou ses alliés
		List<Point> allySentryWards = getAllySentryWards(area);
		
		return isLocationRevealed(x, y, allySentryWards);
	}
	
	public boolean isLocationRevealed(int x, int y, List<Point> allySentryWards) {
		if (hasRight(ADMINISTRATOR))
			return true;
		
		return isLocationRevealed(x, y, 0, allySentryWards);
	}
	
	public boolean isLocationRevealed(int x, int y, int radius,
			List<Point> allySentryWards) {
		if (hasRight(ADMINISTRATOR))
			return true;
		
		int los = Ward.SENTRY_DETECTION_RADIUS;
		for (Point allySentryWard : allySentryWards) {
			int dx = allySentryWard.x - x;
			int dy = allySentryWard.y - y;
			
			if (dx * dx + dy * dy <= los * los - radius * radius)
				return true;
		}
		
		return false;
	}
	
	// Renvoie le nombre de points d'XP nécessaires pour atteindre un niveau
	public static long getLevelXp(int level) {
		// Voir materials/simulation development.xlsx
		long xp = 0;
		
		for (int i = 1; i < level; i++)
			xp += GameConstants.XP_SHIP_DESTROYED *
				Fleet.getPowerAtLevel(i + 1) * (3 + 2 * i);
		
		return xp;
	}
	
	public boolean isAreaVisible(int idArea){
		byte[] areasVisibility = getAreasVisibility();
		
		if (idArea <= 0 || idArea >= areasVisibility.length)
			throw new IllegalArgumentException(
					"Invalid area id: '" + idArea + "'");
		
		return areasVisibility[idArea] > Area.VISIBILITY_VISITED;
	}
	
	public String getNextFleetName() {
		return getNextFleetName(this);
	}
	
	public static String getNextFleetName(Player player) {
		int suffixIndex = 0;
		
		List<Fleet> fleets = player.getFleets();
		
		search:while (true) {
			String fleetName = player.getSettingsFleetNamePrefix() + " " +
				getSuffix(player.getSettingsFleetNameSuffix(), suffixIndex);
			
			synchronized (fleets) {
				for (Fleet fleet : fleets) {
					if (fleet.getName().equalsIgnoreCase(fleetName)) {
						suffixIndex++;
						continue search;
					}
				}
			}
			
			return fleetName;
		}
	}
	
	public int getAchievementLevelsSum() {
		if (achievementsCache == -1)
			updateAchievementsCache();
		return achievementsCache;
	}
	
	public void updateAchievementsCache() {
		List<Achievement> achievements = getAchievements();
		
		int achievementsCache = 0;
		
		synchronized (achievements) {
			for (Achievement achievement : achievements)
				achievementsCache += achievement.getLevel();
		}
		
		this.achievementsCache = achievementsCache;
		
		if (getIdAlly() != 0)
			getAlly().updateAchievementsCache();
	}
	
	public long getPlayedTime(int scale) {
		List<Connection> connections = DataAccess.getConnectionsByPlayer(getId());
		
		long playedTime = 0;
		
		long now = Utilities.now() * 1000;
		Calendar reference = Calendar.getInstance();
		reference.setTimeInMillis(now);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now);
		
		// Deadlines pour le décompte au mois et au jour
		calendar.set(
			reference.get(Calendar.YEAR),
			reference.get(Calendar.MONTH),
			1, 0, 0, 0);
		long monthDeadline = calendar.getTimeInMillis() / 1000;
		
		calendar.set(
			reference.get(Calendar.YEAR),
			reference.get(Calendar.MONTH),
			reference.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		long dayDeadline = calendar.getTimeInMillis() / 1000;
		
		synchronized (connections) {
			for (Connection connection : connections) {
				if (connection.getEnd() == 0) {
					long current = now / 1000 - connection.getStart();
					
					switch (scale) {
					case SCALE_DAY:
						playedTime += Math.min(current, now / 1000 - dayDeadline);
						break;
					case SCALE_MONTH:
						playedTime += Math.min(current, now / 1000 - monthDeadline);
						break;
					case SCALE_OVERALL:
						playedTime += current;
						break;
					}
				} else {
					switch (scale) {
					case SCALE_DAY:
						if (connection.getEnd() > dayDeadline)
							playedTime += connection.getEnd() - Math.max(
									dayDeadline, connection.getStart());
						break;
					case SCALE_MONTH:
						if (connection.getEnd() > monthDeadline)
							playedTime += connection.getEnd() - Math.max(
									monthDeadline, connection.getStart());
						break;
					case SCALE_OVERALL:
						playedTime += connection.getEnd() - connection.getStart();
						break;
					}
				}
			}
		}
		
		return playedTime;
	}
	
	public List<String> getDoubleCompte(int from, int to) {
		List<String> found = new ArrayList<String>();
		List<Integer> dejavu = new ArrayList<Integer>();
		
		//Récupération des Ip du joueur sur une période donnée
		List<Connection> connexions = 
			DataAccess.getConnectionsCustom(from, to, getId());
		List<Integer> ips = new ArrayList<Integer>();
		int lastIp = 0;
		
		for(Connection connexion : connexions){
			if(lastIp!=connexion.getIp()){
				lastIp = connexion.getIp();
				ips.add(lastIp);
			}
		}
		
		List<Connection> doubles = new ArrayList<Connection>();
		boolean alreadyDone = false;
		
		for(int ip : ips){
			
			// Récupération des personnes connecté de from à To
			doubles = DataAccess.getConnectionsByDate(from, to);

			for(Connection connexion : doubles)
			{
				alreadyDone = false;
				if(connexion.getIdPlayer()!=getId() && connexion.getIp() == ip){

					for(int vu : dejavu)
					{
						if(connexion.getIdPlayer()==vu)
						{
							alreadyDone = true;
						}
					}
					
					if(alreadyDone == false){
						dejavu.add(connexion.getIdPlayer());
						found.add(DataAccess.getPlayerById(
									connexion.getIdPlayer()).getLogin());
						LoggingSystem.getServerLogger().error("Detected double of "+getLogin()
								+ " ==> "+DataAccess.getPlayerById(
										connexion.getIdPlayer()).getLogin());
					}
				}
			}
		}
		
		
		return found;
		
	}
	
	public static JSONStringer checkMultiAccounts(Player player1, Player player2) {
		long from = Utilities.now()-(60*24*3600); //Il y a 2 mois
		long to = Utilities.now();
		List<Connection> connections1 = 
			DataAccess.getConnectionsCustom((int)from, (int)to, player1.getId());
		List<Connection> connections2 = 
			DataAccess.getConnectionsCustom((int)from, (int)to, player2.getId());
		List<Integer> ips1 = new ArrayList<Integer>();
		List<Integer> ips2 = new ArrayList<Integer>();
		List<Integer> commonIps = new ArrayList<Integer>();
		int lastIp = 0;
		int probability=0;
		JSONStringer json = new JSONStringer();
		String reason = new String("");
		
		for(Connection connection1 : connections1){
			if(lastIp!=connection1.getIp()){
				lastIp = connection1.getIp();
				ips1.add(lastIp);
			}
		}
		
		lastIp=0;
		for(Connection connection2 : connections2){
			if(lastIp!=connection2.getIp()){
				lastIp = connection2.getIp();
				ips2.add(lastIp);
			}
		}
		
		for(int ip1: ips1) {
			for(int ip2 : ips2) {
				if(ip1 == ip2) {
					commonIps.add(ip1);
					probability+=20;
				}
			}
		}
		
		if(commonIps.size()>2) {
			probability=50;
			reason = "Plus de 2 IPs identiques";
		}
		else if (commonIps.size()>1){
			reason = "2 Ips identiques";
		}
		else if(commonIps.size()>0) {
			reason = "Une IP identique";
		}
		
		
		if(player1.getBirthday()== player2.getBirthday()) {
			if(commonIps.size()>0) {
				probability+=40;
			}
			else {
				probability+=20;
			}
			reason = reason.length()>0? reason +", " : "";
			reason+= "dates de naissance identiques";
		}
		
		if(player1.getPassword().equals(player2.getPassword())) {
			probability+=30;
			reason = reason.length()>0? reason +", " : "";
			reason+= "mots de passe identiques";
		}
		
		if(reason.length()==0) {
			reason = "Rien à signaler";
		}
		
		if(probability>100) {
			probability=100;
		}
		
		json.object().
		key(PlayerInfosData.FIELD_LOGIN)			.value(player1.getLogin()).
		key(PlayerInfosData.FIELD_OTHER_LOGIN)		.value(player2.getLogin()).
		key(PlayerInfosData.FIELD_REASON)			.value(reason).
		key(PlayerInfosData.FIELD_PROBABILITY)		.value(probability).
		key(PlayerInfosData.FIELD_REDUNDANT_IP)		.value(commonIps.size()>0?commonIps.get(commonIps.size()-1) : 0).
		endObject();
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static String getSuffix(int suffix, int index) {
		switch (suffix) {
		case 0:
			// 1, 2, 3...
			return String.valueOf(index + 1);
		case 1:
			// I, II, III...
			String[] figures = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
			
			String result = "";
			for (int i = 0; i < index / 10; i++)
				result += "X";
			
			result += figures[index % 10];
			
			return result;
		case 2:
			// A, B, C...
			String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
			
			if (index < 26)
				return characters[index];
			else
				return characters[index / 26 - 1] + characters[index % 26];
		default:
			throw new IllegalArgumentException("Unknown suffix: '" + suffix + "'.");
		}
	}
}
