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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.fg.server.dao.DataLayer;
import fr.fg.server.dao.PersistentData;

public class DataAccess {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static <T extends PersistentData> T getEditable(T t) {
		return DataLayer.getInstance().getEditable(t);
	}
	
	public static void save(PersistentData data) {
		data.save();
	}

	public static void delete(PersistentData data) {
		data.delete();
	}
	
	public static void flush() {
		DataLayer.getInstance().flush();
	}

	public static void release() {
		DataLayer.getInstance().release();
	}
	
	// Player
	
	public static List<Player> getAllPlayers() {
		return DataLayer.getInstance().getAll(Player.class);
	}
	
	public static Player getPlayerById(int id) {
		return DataLayer.getInstance().getObjectById(Player.class, "id", id);
	}
	
	public static Player getPlayerByLogin(String login) {
		return DataLayer.getInstance().getObjectById(
				Player.class, "login", login);
	}

	public static Player getPlayerByEmail(String email) {
		return DataLayer.getInstance().getObjectById(
				Player.class, "email", email);
	}
	
	public static Player getPlayerByRegistrationHash(String hash) {
		Player player = DataLayer.getInstance().getObjectById(Player.class, "registration", hash);
		return player != null ? player : null;
	}
	
	public static Player getPlayerByEkey(String ekey) {
		Player player = DataLayer.getInstance().getObjectById(Player.class, "ekey", ekey);
		return player != null ? player : null;
	}
	
	public static Player getPlayerByCloseAccountHash(String hash) {
		Player player = DataLayer.getInstance().getObjectById(Player.class, "close_account_hash", hash);
		return player != null ? player : null;
	}
	
	public static Player getPlayerByRecoverEmail(String hash) {
		Player player = DataLayer.getInstance().getObjectById(Player.class, "recover_email", hash);
		return player != null ? player : null;
	}
	
	public static List<Player> getPlayersByAlly(int idAlly) {
		return DataLayer.getInstance().getObjectsById(Player.class, "id_ally", idAlly);
	}
	
	public static List<Player> getPlayersByRights(int rights) {
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		List<Player> administrators = new ArrayList<Player>();
		synchronized(players) {
			for(Player player : players) {
				if(player.hasRight(rights)) {
					administrators.add(player);
				}
			}
		}
		
		return administrators;
	}

	public static Changelog getChangelogById(long id) {
		return DataLayer.getInstance().getObjectById(Changelog.class, "id", id);
	}
	
	public static List<Changelog> getAllChangelogs() {
		return DataLayer.getInstance().getAll(Changelog.class);
	}

	// Treaty
	
	public static List<Treaty> getAllTreaties() {
		return DataLayer.getInstance().getAll(Treaty.class);
	}

	public static List<Treaty> getPlayer1Treaties(int idPlayer1) {
		return DataLayer.getInstance().getObjectsById(
				Treaty.class, "id_player1", idPlayer1);
	}

	public static List<Treaty> getPlayer2Treaties(int idPlayer2) {
		return DataLayer.getInstance().getObjectsById(
				Treaty.class, "id_player2", idPlayer2);
	}
	
	public static List<Treaty> getPlayerTreaties(int idPlayer) {
		List<Treaty> treaties = new ArrayList<Treaty>();
		treaties.addAll(DataLayer.getInstance().getObjectsById(
				Treaty.class, "id_player1", idPlayer));
		treaties.addAll(DataLayer.getInstance().getObjectsById(
				Treaty.class, "id_player2", idPlayer));
		return treaties;
	}
	
	// Message
	
	public static List<Message> getAllMessages() {
		return DataLayer.getInstance().getAll(Message.class);
	}
	
	public static Message getMessageById(int idMessage) {
		return DataLayer.getInstance().getObjectById(
				Message.class, "id", idMessage);
	}
	
	public static List<Message> getMessagesByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				Message.class, "id_player", idPlayer);
	}

	public static List<Message> getMessagesByAuthor(int idAuthor) {
		return DataLayer.getInstance().getObjectsById(
				Message.class, "id_author", idAuthor);
	}
	
	
	// AccountAction
	
	public static AccountAction getAccountActionByPlayer(String playerLogin) {
		return DataLayer.getInstance().getObjectById(
				AccountAction.class, "login", playerLogin);
	}
	
	// Achievement
	
	public static List<Achievement> getAchievementsByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				Achievement.class, "id_player", idPlayer);
	}
	
	// Advancement
	
	public static List<Advancement> getAdvancementsByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				Advancement.class, "id_player", idPlayer);
	}
	
	// Ally
	
	public static List<Ally> getAllAllies() {
		return DataLayer.getInstance().getAll(Ally.class);
	}
	
	public static Ally getAllyById(int idAlly){
		return DataLayer.getInstance().getObjectById(
				Ally.class, "id", idAlly);
	}
	
	public static Ally getAllyByName(String allyName){
		return DataLayer.getInstance().getObjectById(
				Ally.class, "name", allyName);
	}

	public static Ally getAllyByTag(String tag){
		return DataLayer.getInstance().getObjectById(
				Ally.class, "tag", tag);
	}
	
	public static Ally getAllyByNameOrTag(String allyName){
		Ally ally = getAllyByName(allyName);
		if (ally != null)
			return ally;
		else
			return getAllyByTag(allyName);
	}
	
	// AllyInfluence
	
	public static List<AllyInfluence> getAllyInfluencesByAlly(int idAlly) {
		return DataLayer.getInstance().getObjectsById(
				AllyInfluence.class, "id_ally", idAlly);
	}

	public static List<AllyInfluence> getAllyInfluencesBySector(int idSector) {
		return DataLayer.getInstance().getObjectsById(
				AllyInfluence.class, "id_sector", idSector);
	}
	
	// AllyNews
	
	public static List<AllyNews> getAllAllyNews() {
		return DataLayer.getInstance().getAll(AllyNews.class);
	}
	
	public static AllyNews getAllyNewsById(int idAllyNews){
		return DataLayer.getInstance().getObjectById(
				AllyNews.class, "id", idAllyNews);
	}
	
	public static List<AllyNews> getAllyNewsByAlly(int idAlly){
		return DataLayer.getInstance().getObjectsById(
			AllyNews.class, "id_ally", idAlly);
	}

	public static List<AllyNews> getAllyNewsByParent(int idParent){
		return DataLayer.getInstance().getObjectsById(
			AllyNews.class, "id_parent", idParent);
	}
	
	public static AllyNews getAllyNewsByApplicant(int idApplicant){
		List<AllyNews> news = DataLayer.getInstance().getObjectsById(
				AllyNews.class, "id_applicant", idApplicant);
		return news.size() > 0 ? news.get(0) : null;
	}
	
	// AllyNewsRead
	
	public static List<AllyNewsRead> getAllyNewsReadByNews(int idAllyNews){
		return DataLayer.getInstance().getObjectsById(
				AllyNewsRead.class, "id_ally_news", idAllyNews);
	}
	
	public static List<AllyNewsRead> getAllyNewsReadByPlayer(int idPlayer){
		return DataLayer.getInstance().getObjectsById(
				AllyNewsRead.class, "id_player", idPlayer);
	}
	
	public static AllyNewsRead getAllyNewsReadByNewsAndPlayer(int idAllyNews, int idPlayer) {
		List<AllyNewsRead> readList =
			DataAccess.getAllyNewsReadByPlayer(idPlayer);
		
		synchronized (readList) {
			for (AllyNewsRead read : readList) {
				if (read.getIdAllyNews() == idAllyNews)
					return read;
			}
		}
		
		return null;
	}
	
	// AllyTreaty
	
	public static List<AllyTreaty> getAllAllyTreaties(){
		return DataLayer.getInstance().getAll(AllyTreaty.class);
	}
	
	public static List<AllyTreaty> getAllyTreaties(int idAlly){
		List<AllyTreaty> treaties = new ArrayList<AllyTreaty>();
		treaties.addAll(DataLayer.getInstance().getObjectsById(
				AllyTreaty.class, "id_ally1", idAlly));
		treaties.addAll(DataLayer.getInstance().getObjectsById(
				AllyTreaty.class, "id_ally2", idAlly));
		return treaties;
	}
	
	// AllyVote
	
	public static List<AllyVote> getAllAllyVotes(){
		return DataLayer.getInstance().getAll(AllyVote.class);
	}
	
	public static AllyVote getAllyVoteById(int idVote){
		return DataLayer.getInstance().getObjectById(
				AllyVote.class, "id", idVote);
	}
	
	public static List<AllyVote> getAllyVotesByAlly(int idAlly){
		return DataLayer.getInstance().getObjectsById(
				AllyVote.class, "id_ally", idAlly);
	}
	
	public static AllyVote getAllyVoteByTarget(int idPlayer){
		return DataLayer.getInstance().getObjectById(
				AllyVote.class, "id_player", idPlayer);
	}
	
	// AllyVoter
	
	public static List<AllyVoter> getAllAllyVoter(){
		return DataLayer.getInstance().getAll(AllyVoter.class);
	}
	
	public static List<AllyVoter> getVoteVoter(int idVote){
		return DataLayer.getInstance().getObjectsById(
				AllyVoter.class, "id_vote", idVote);
	}
	
	public static List<AllyVoter> getPlayerVoter(int idPlayer){
		return DataLayer.getInstance().getObjectsById(
				AllyVoter.class, "id_player", idPlayer);
	}
	
	// Applicant
	
	public static List<Applicant> getAllApplicants(){
		return DataLayer.getInstance().getAll(Applicant.class);
	}
	
	public static List<Applicant> getApplicantsByAlly(int idAlly){
		return DataLayer.getInstance().getObjectsById(
				Applicant.class, "id_ally", idAlly);
	}

	public static Applicant getApplicantByPlayer(int idPlayer){
		return DataLayer.getInstance().getObjectById(
				Applicant.class, "id_player", idPlayer);
	}
	
	// Area

	public static List<Area> getAllAreas() {
		return DataLayer.getInstance().getAll(Area.class);
	}
	
	public static Area getAreaById(int idArea) {
		return DataLayer.getInstance().getObjectById(Area.class, "id", idArea);
	}

	public static Area getAreaByName(String name) {
		return DataLayer.getInstance().getObjectById(Area.class, "name", name);
	}

	public static List<Area> getAreasBySector(int idSector) {
		return DataLayer.getInstance().getObjectsById(Area.class, "id_sector", idSector);
	}
	
	// Bank
	
	public static List<Bank> getAllBanks() {
		return DataLayer.getInstance().getAll(Bank.class);
	}
	
	public static Bank getBankById(int idCenter){
		return DataLayer.getInstance().getObjectById(
				Bank.class, "id_bank", idCenter);
	}
	
	// BankAccount
	
	public static List<BankAccount> getBankAccountsByBank(int idBank) {
		return DataLayer.getInstance().getObjectsById(
				BankAccount.class, "id_bank", idBank);
	}
	
	public static List<BankAccount> getBankAccountsByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				BankAccount.class, "id_player", idPlayer);
	}
	
	 // LotteryTicket
    
    public static List<LotteryTicket> getAllLotteryTickets() {
            return DataLayer.getInstance().getAll(LotteryTicket.class);
    }
   
    public static LotteryTicket getLotteryTicketByPlayer(int idPlayer) {
            return DataLayer.getInstance().getObjectById(LotteryTicket.class, "id_player", idPlayer);
    }
	
	// Connection

	public static List<Connection> getAllConnections() {
		return DataLayer.getInstance().getAll(Connection.class);
	}
	
	public static List<Connection> getConnectionsByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				Connection.class, "id_player", idPlayer);
	}
	

	public static List<Connection> getConnectionsCustom(int start, int end, int idPlayer){
		List<Connection> connex = new ArrayList<Connection>(
				DataLayer.getInstance().getObjectsById(
						Connection.class, "id_player", idPlayer));
			
		

		Iterator<Connection> i = connex.iterator();
		while (i.hasNext()) {
			Connection connect = i.next();
			
			if (connect.getStart() < start || 
					connect.getStart() > end)
				i.remove();
		}

		
		return connex;
	}
	
	public static List<Connection> getConnectionsByDate(int start, int end){
		List<Connection> connex = new ArrayList<Connection>(
				DataLayer.getInstance().getAll(Connection.class));
			
		

		Iterator<Connection> i = connex.iterator();
		while (i.hasNext()) {
			Connection connect = i.next();
			
			if (connect.getStart() < start || 
					connect.getStart() > end)
				i.remove();
		}

		
		return connex;
	}
	
	
	public static Connection getActiveConnectionByPlayer(int idPlayer) {
		List<Connection> connections = getConnectionsByPlayer(idPlayer);
		
		for (int i = connections.size() - 1; i >= 0; i--) {
			if (connections.get(i).getEnd() == 0)
				return connections.get(i);
		}
		
		return null;
	}
	
	// Contact
	
	public static List<Contact> getContactsByPlayer(int idPlayer){
		return DataLayer.getInstance().getObjectsById(
				Contact.class, "id_player", idPlayer);
	}
	
	public static List<Contact> getContactsByContact(int idContact){
		return DataLayer.getInstance().getObjectsById(
				Contact.class, "id_contact", idContact);
	}
	
	public static Contact getContactByContact(int idPlayer, int idContact){
		List<Contact> contacts = DataLayer.getInstance().getObjectsById(
				Contact.class, "id_player", idPlayer);
		
		for (Contact contact : contacts)
			if (contact.getIdContact() == idContact)
				return contact;
		
		return null;
	}
	
	// Contract
	
	public static List<Contract> getAllContracts() {
		return DataLayer.getInstance().getAll(Contract.class);
	}
	
	public static Contract getContractById(long idContract){
		return DataLayer.getInstance().getObjectById(
				Contract.class, "id", idContract);
	}
	
	//TODO
	public static List<Contract> getMultiContractsWaiting() {
		List<Contract> contracts = new ArrayList<Contract>(
				DataLayer.getInstance().getAll(Contract.class));
		
		Iterator<Contract> i = contracts.iterator();
		
		while (i.hasNext()) {
			Contract contract = i.next();
			
			if (contract.getMaxAttendees() == 1 || 
					contract.getState()!=Contract.STATE_WAITING)
				i.remove();
		}
		
		return contracts;
	}
	


	// ContractAttendee
	
	public static List<ContractAttendee> getAttendeesByContract(
			long idContract) {
		return DataLayer.getInstance().getObjectsById(
				ContractAttendee.class, "id_contract", idContract);
	}
	
	public static List<ContractAttendee> getAttendeesByPlayer(
			Player player) {
		List<ContractAttendee> attendees = new ArrayList<ContractAttendee>(
			DataLayer.getInstance().getObjectsById(
				ContractAttendee.class, "id_player", player.getId()));
		
		if (player.getIdAlly() != 0) {
			attendees.addAll(DataLayer.getInstance().getObjectsById(
				ContractAttendee.class, "id_ally", player.getIdAlly()));
		}
		
		return attendees;
	}
	
	// ContractArea
	
	public static List<ContractArea> getAreasByContract(long idContract) {
		return DataLayer.getInstance().getObjectsById(
				ContractArea.class, "id_contract", idContract);
	}
	
	// ContractDialog
	
	public static List<ContractDialog> getDialogsByFleet(int idFleet) {
		return DataLayer.getInstance().getObjectsById(
				ContractDialog.class, "id_fleet", idFleet);
	}
	
	// ContractParameter
	
	public static ContractParameter getContractParameterById(long idParameter) {
		return DataLayer.getInstance().getObjectById(
				ContractParameter.class, "id", idParameter);
	}
	
	public static List<ContractParameter> getContractParametersByContract(long idContract) {
		return DataLayer.getInstance().getObjectsById(
				ContractParameter.class, "id_contract", idContract);
	}
	
	public static List<ContractParameter> getContractParametersByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				ContractParameter.class, "id_player", idPlayer);
	}
	
	// ContractParameterValue
	
	public static List<ContractParameterValue> getContractParameterValuesByParameter(long idParameter) {
		return DataLayer.getInstance().getObjectsById(
				ContractParameterValue.class, "id_contract_parameter", idParameter);
	}
	
	// ContractRelationship
	
	public static List<ContractRelationship> getRelationshipsByContract(long idContract) {
		return DataLayer.getInstance().getObjectsById(
				ContractRelationship.class, "id_contract", idContract);
	}
	
	// ContractReward
	
	public static List<ContractReward> getRewardsByContract(long idContract) {
		return DataLayer.getInstance().getObjectsById(
				ContractReward.class, "id_contract", idContract);
	}
	
	// Election
	
	public static List<Election> getAllElections() {
		return DataLayer.getInstance().getAll(Election.class);
	}
	
	public static Election getElectionById(int idElection){
		return DataLayer.getInstance().getObjectById(
				Election.class, "id", idElection);
	}
	
	public static Election getElectionByAlly(int idAlly){
		return DataLayer.getInstance().getObjectById(
				Election.class, "id_ally", idAlly);
	}
	
	// ElectionVoter
	
	public static List<ElectionVoter> getElectionVoterById(int idElection){
		return DataLayer.getInstance().getObjectsById(
				ElectionVoter.class, "id_election", idElection);
	}
	
	public static List<ElectionVoter> getElectionVoterByPlayer(int idPlayer){
		return DataLayer.getInstance().getObjectsById(
				ElectionVoter.class, "id_player", idPlayer);
	}
	
	// Flottes
	
	public static List<Fleet> getAllFleets() {
		return DataLayer.getInstance().getAll(Fleet.class);
	}
	
	public static Fleet getFleetById(int idFleet){
		return DataLayer.getInstance().getObjectById(
				Fleet.class, "id", idFleet);
	}
	
	public static List<Fleet> getFleetsByContract(long idContract){
		return DataLayer.getInstance().getObjectsById(
				Fleet.class, "id_contract", idContract);
	}
	
	public static List<Fleet> getUnstuckableFleets() {
		return DataLayer.getInstance().getObjectsById(
				Fleet.class, "unstuckable", true);
	}
	
	public static List<Fleet> getFleetsByArea(int idArea){
		List<Fleet> fleets = new ArrayList<Fleet>(
				DataLayer.getInstance().getObjectsById(
						Fleet.class, "id_area", idArea));
		
		// Supprime les flottes en hyperespace qui ont quitté le secteur
		// de départ
		Iterator<Fleet> i = fleets.iterator();
		while (i.hasNext()) {
			Fleet fleet = i.next();
			
			if (fleet.getIdCurrentArea() != idArea)
				i.remove();
		}
		
		// Ajoute les flottes en hyperespace en train d'atteindre le secteur
		// d'arrivée
		List<Fleet> hyperspaceFleets = DataLayer.getInstance().getObjectsById(
				Fleet.class, "hyperspace_id_area", idArea);
		
		for (Fleet fleet : hyperspaceFleets) {
			if (fleet.getIdCurrentArea() == idArea)
				fleets.add(fleet);
		}
		
		return fleets;
	}
	
	public static List<Fleet> getHyperspaceFleets(int idArea) {
		return DataLayer.getInstance().getObjectsById(
				Fleet.class, "hyperspace_id_area", idArea);
	}
	
	public static List<Fleet> getFleetsByOwner(int idPlayer){
		return DataLayer.getInstance().getObjectsById(
				Fleet.class, "id_owner", idPlayer);
	}
	
	// FleetLink
	
	public static List<FleetLink> getFleetLinks(int idFleet) {
		List<FleetLink> list = new ArrayList<FleetLink>();
		
		list.addAll(DataLayer.getInstance().getObjectsById(
				FleetLink.class, "id_src_fleet", idFleet));
		list.addAll(DataLayer.getInstance().getObjectsById(
				FleetLink.class, "id_dst_fleet", idFleet));
		
		return list;
	}
	
	public static List<FleetLink> getFleetLinks(int idFleet, int idOtherFleet) {
		List<FleetLink> list = new ArrayList<FleetLink>();
		list.addAll(DataLayer.getInstance().getObjectsById(
				FleetLink.class, "id_src_fleet", idFleet));
		list.addAll(DataLayer.getInstance().getObjectsById(
				FleetLink.class, "id_dst_fleet", idFleet));
		
		List<FleetLink> links = new ArrayList<FleetLink>();
		
		for (FleetLink link : list){
			if ((link.getIdSrcFleet() == idFleet &&
				 link.getIdDstFleet() == idOtherFleet) ||
				(link.getIdSrcFleet() == idOtherFleet &&
				 link.getIdDstFleet() == idFleet))
				links.add(link);
		}
		
		return links;
	}
	
	// ItemContainer
	
	public static ItemContainer getItemContainerById(long idItemContainer) {
		return DataLayer.getInstance().getObjectById(
			ItemContainer.class, "id", idItemContainer);
	}
	
	public static ItemContainer getItemContainerByFleet(int idFleet) {
		List<ItemContainer> containers = DataLayer.getInstance().getObjectsById(
			ItemContainer.class, "id_fleet", idFleet);
		return containers.size() > 0 ? containers.get(0) : null;
	}
	
	// Marker
	
	public static List<Marker> getAllMarkers() {
		return DataLayer.getInstance().getAll(Marker.class);
	}
	
	public static Marker getMarkerById(int idMarker) {
		return DataLayer.getInstance().getObjectById(
				Marker.class, "id", idMarker);
	}

	public static List<Marker> getMarkersByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				Marker.class, "id_owner", idPlayer);
	}

	public static List<Marker> getMarkersByArea(int idArea) {
		return DataLayer.getInstance().getObjectsById(
				Marker.class, "id_area", idArea);
	}

	// Object
	
	public static List<StellarObject> getAllObjects() {
		return DataLayer.getInstance().getAll(StellarObject.class);
	}
	
	public static StellarObject getObjectById(int idObject){
		return DataLayer.getInstance().getObjectById(
				StellarObject.class, "id", idObject);
	}
	
	public static List<StellarObject> getObjectsByArea(int idArea){
		return DataLayer.getInstance().getObjectsById(
				StellarObject.class, "id_area", idArea);
	}

	public static List<StellarObject> getObjectsByType(String type){
		return DataLayer.getInstance().getObjectsById(
				StellarObject.class, "type", type);
	}
	
	// Planet
	
	public static Planet getPlanetById(int idPlanet) {
		return DataLayer.getInstance().getObjectById(Planet.class, "id", idPlanet);
	}
	
	public static List<Planet> getPlanetsBySystem(int idSystem) {
		return DataLayer.getInstance().getObjectsById(Planet.class, "id_system", idSystem);
	}

	// RandomName
	
	public static List<RandomName> getAllRandomNames() {
		return DataLayer.getInstance().getAll(RandomName.class);
	}

	public static List<RandomName> getRandomNamesByType(String type) {
		return DataLayer.getInstance().getObjectsById(RandomName.class, "type", type);
	}
	
	// Relationship
	
	public static List<Relationship> getAllRelationships() {
		return DataLayer.getInstance().getAll(Relationship.class);
	}
	
	public static List<Relationship> getRelationshipsByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(Relationship.class, "id_player", idPlayer);
	}
	
	// Report
	
	public static Report getReportById(int idReport){
		return DataLayer.getInstance().getObjectById(
				Report.class, "id", idReport);
	}
	
	public static Report getReportByHash(String hash){
		return DataLayer.getInstance().getObjectById(
				Report.class, "hash", hash);
	}
	
	public static List<Report> getReportsByPlayer(int idPlayer){
		List<Report> reportList = new ArrayList<Report>();
		reportList.addAll(DataLayer.getInstance().getObjectsById(
				Report.class, "id_player_attacking", idPlayer));
		reportList.addAll(DataLayer.getInstance().getObjectsById(
				Report.class, "id_player_defending", idPlayer));
		return reportList;
	}
	
	public static List<Report> getAllReports() {
		return DataLayer.getInstance().getAll(Report.class);
	}
	
	// ReportAction
	
	public static ReportAction getReportActionById(int id){
		return DataLayer.getInstance().getObjectById(
				ReportAction.class, "id", id);
	}
	
	public static List<ReportAction> getReportActionsByReportId(int idReport){
		return DataLayer.getInstance().getObjectsById(
				ReportAction.class, "id_report", idReport);
	}
	
	// ReportActionAbility
	
	public static List<ReportActionAbility> getReportActionAbilitiesByAction(long idReportAction) {
		return DataLayer.getInstance().getObjectsById(
				ReportActionAbility.class, "id_report_action", idReportAction);
	}
	
	// ReportDamage
	
	public static List<ReportDamage> getReportDamagesByAction(long idReportAction) {
		return DataLayer.getInstance().getObjectsById(
				ReportDamage.class, "id_report_action", idReportAction);
	}
	
	// ReportSlot
	
	public static ReportSlot getReportSlotById(long idSlot){
		return DataLayer.getInstance().getObjectById(
				ReportSlot.class, "id", idSlot);
	}
	
	public static List<ReportSlot> getReportSlotsByReportId(int idReport){
		return DataLayer.getInstance().getObjectsById(
				ReportSlot.class, "id_report", idReport);
	}
	
	// ReportSlotState
	
	public static List<ReportSlotState> getReportSlotStatesByAction(long idReportAction) {
		return DataLayer.getInstance().getObjectsById(
				ReportSlotState.class, "id_report_action", idReportAction);
	}
	
	// Research
	
	public static List<Research> getResearchesByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(
				Research.class, "id_player", idPlayer);
	}
	
	// Sector
	
	public static List<Sector> getAllSectors() {
		return DataLayer.getInstance().getAll(Sector.class);
	}
	
	public static Sector getSectorById(int idSector) {
		return DataLayer.getInstance().getObjectById(Sector.class, "id", idSector);
	}

	public static Sector getSectorByName(String name) {
		return DataLayer.getInstance().getObjectById(Sector.class, "name", name);
	}
	
	// ShipContainer
	
	public static ShipContainer getShipContainerById(long id) {
		return DataLayer.getInstance().getObjectById(ShipContainer.class, "id", id);
	}
	
	public static ShipContainer getShipContainerByFleet(int idFleet) {
		List<ShipContainer> containers = DataLayer.getInstance().getObjectsById(
				ShipContainer.class, "id_fleet", idFleet);
		return containers.size() > 0 ? containers.get(0) : null;
	}
	
	public static ShipContainer getShipContainerByStructure(long idStructure) {
		List<ShipContainer> containers = DataLayer.getInstance().getObjectsById(
				ShipContainer.class, "id_structure", idStructure);
		return containers.size() > 0 ? containers.get(0) : null;
	}
	
	// SpaceStation
	
	public static List<SpaceStation> getAllSpaceStations() {
		return DataLayer.getInstance().getAll(SpaceStation.class);
	}
	
	public static SpaceStation getSpaceStationById(int idSpaceStation) {
		return DataLayer.getInstance().getObjectById(SpaceStation.class, "id", idSpaceStation);
	}
	
	public static List<SpaceStation> getSpaceStationsByAlly(int idAlly) {
		return DataLayer.getInstance().getObjectsById(SpaceStation.class, "id_ally", idAlly);
	}
	
	public static List<SpaceStation> getSpaceStationsByArea(int idArea) {
		return DataLayer.getInstance().getObjectsById(SpaceStation.class, "id_area", idArea);
	}

	// StorehouseResources
	
	public static List<StorehouseResources> getStorehouseResourcesByArea(int idArea) {
		return DataLayer.getInstance().getObjectsById(
			StorehouseResources.class, "id_area", idArea);
	}
	
	// Structure
	
	public static List<Structure> getAllStructures() {
		return DataLayer.getInstance().getAll(Structure.class);
	}
	
	public static Structure getStructureById(long idStructure) {
		return DataLayer.getInstance().getObjectById(
				Structure.class, "id", idStructure);
	}
	
	public static List<Structure> getStructuresByArea(int idArea) {
		return DataLayer.getInstance().getObjectsById(Structure.class, "id_area", idArea);
	}
	
	public static List<Structure> getStructuresByOwner(int idOwner) {
		return DataLayer.getInstance().getObjectsById(Structure.class, "id_owner", idOwner);
	}
	
	public static List<Structure> getStructuresByEnergySupplier(
			long idEnergySupplierStructure) {
		return DataLayer.getInstance().getObjectsById(Structure.class,
			"id_energy_supplier_structure", idEnergySupplierStructure);
	}
	
	// StructureModule
	
	public static List<StructureModule> getModulesByStructure(long idStructure) {
		return DataLayer.getInstance().getObjectsById(
			StructureModule.class, "id_structure", idStructure);
	}
	
	// StructureSkill
	
	public static List<StructureSkill> getSkillsByStructure(long idStructure) {
		return DataLayer.getInstance().getObjectsById(
			StructureSkill.class, "id_structure", idStructure);
	}
	
	// StructureSpaceshipYard
	
	public static List<StructureSpaceshipYard> getAllSpaceshipYards() {
		return DataLayer.getInstance().getAll(StructureSpaceshipYard.class);
	}
	
	public static StructureSpaceshipYard getSpaceshipYardByStructure(long idStructure) {
		return DataLayer.getInstance().getObjectById(
			StructureSpaceshipYard.class, "id_structure", idStructure);
	}
	
	// System
	
	public static List<StarSystem> getAllSystems() {
		return DataLayer.getInstance().getAll(StarSystem.class);
	}
	
	public static StarSystem getSystemById(int idSystem) {
		return DataLayer.getInstance().getObjectById(StarSystem.class, "id", idSystem);
	}

	public static List<StarSystem> getSystemsByArea(int idArea) {
		return DataLayer.getInstance().getObjectsById(StarSystem.class, "id_area", idArea);
	}
	
	public static List<StarSystem> getSystemsByOwner(int idOwner) {
		return DataLayer.getInstance().getObjectsById(StarSystem.class, "id_owner", idOwner);
	}
	
	// Tactic
	
	public static List<Tactic> getTacticsByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(Tactic.class, "id_player", idPlayer);
	}
	
	// Tradecenter
	
	public static List<Tradecenter> getAllTradecenters() {
		return DataLayer.getInstance().getAll(Tradecenter.class);
	}
	
	public static Tradecenter getTradecenterById(int idCenter){
		return DataLayer.getInstance().getObjectById(
				Tradecenter.class, "id_tradecenter", idCenter);
	}
	
	// VisitedArea
	
	public static List<VisitedArea> getVisitedAreasByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectsById(VisitedArea.class, "id_player", idPlayer);
	}
	
	// Ward
	
	public static List<Ward> getAllWards() {
		return DataLayer.getInstance().getAll(Ward.class);
	}
	
	public static Ward getWardById(int idWard) {
		return DataLayer.getInstance().getObjectById(Ward.class, "id", idWard);
	}
	
	public static List<Ward> getWardsByArea(int idArea) {
		return DataLayer.getInstance().getObjectsById(Ward.class, "id_area", idArea);
	}
	
	public static List<Ward> getWardsByOwner(int idOwner) {
		return DataLayer.getInstance().getObjectsById(Ward.class, "id_owner", idOwner);
	}

	public static List<Event> getAllEvents() {
		return DataLayer.getInstance().getAll(Event.class);
	}

	public static Event getEventById(int idEvent){
		return DataLayer.getInstance().getObjectById(Event.class, "id", idEvent);
	}

	public static List<Event> getEventByTargetId(int idTarget, String target) {
		List<Event> events = DataLayer.getInstance().getObjectsById(Event.class, "id_target", idTarget);
		
		List<Event> result = new ArrayList<Event>();
		for (Event event : events) {
			if (event.getTarget().equals(target) )
				result.add(event);
		}
		
		return result;
	}
	
	// Message Of The Day
	
	public static List<MessageOfTheDay> getAllMessageOfTheDay()
	{
		return DataLayer.getInstance().getAll(MessageOfTheDay.class);
	}
	
	public static MessageOfTheDay getMessageOfTheDayById(int id)
	{
		return DataLayer.getInstance().getObjectById(MessageOfTheDay.class, "id", id);
	}
	
	// Ban
	public static List<Ban> getBanByPlayerId(int id_player)
	{
		return DataLayer.getInstance().getObjectsById(Ban.class, "id_player", id_player);
	}
	
	public static Ban getLastBanChatByPlayerId(int id_player)
	{
		List<Ban> banList =
			DataLayer.getInstance().getObjectsById(Ban.class, "id_player", id_player);
		int lastBan=-1;
		for(int i=0;i<banList.size();i++)
		{
			if((lastBan<banList.get(i).getId()) && (banList.get(i).getBanType()==0))
			{
				lastBan=banList.get(i).getId();
			}
		}
		return lastBan>0? DataAccess.getBanById(lastBan) : null;
	}
	
	public static Ban getLastBanGameByPlayerId(int id_player)
	{
		List<Ban> banList =
			new ArrayList<Ban>(DataLayer.getInstance().getObjectsById(Ban.class, "id_player", id_player));
		int lastBan=-1;
		for(int i=0;i<banList.size();i++)
		{
			if((lastBan<banList.get(i).getId()) && (banList.get(i).getBanType()==1))
			{
				lastBan=banList.get(i).getId();
			}
		}
		return lastBan>0? DataAccess.getBanById(lastBan) : null;
	}
	
	public static Ban getLastBanByPlayerId(int id_player)
	{
		List<Ban> banList =
			new ArrayList<Ban>(DataLayer.getInstance().getObjectsById(Ban.class, "id_player", id_player));
		int lastBan=-1;
		for(int i=0;i<banList.size();i++)
		{
			if(lastBan<banList.get(i).getId())
			{
				lastBan=banList.get(i).getId();
			}
		}
		return lastBan>0? DataAccess.getBanById(lastBan) : null;
	}
	public static Ban getBanById(int id) {
		return DataLayer.getInstance().getObjectById(Ban.class, "id", id);
	}
	
	public static List<Ban> getBanByType(int ban_type) {
		return DataLayer.getInstance().getObjectsById(Ban.class, "ban_type", ban_type);
	}
	
	public static List<Ban> getAllBan() {
		return DataLayer.getInstance().getAll(Ban.class);
	}
	
	// Relationship
	
	public static List<AllyRelationship> getAllAllyRelationships() {
		return DataLayer.getInstance().getAll(AllyRelationship.class);
	}
	
	public static List<AllyRelationship> getAllyRelationshipsByAlly(int idAlly) {
		return DataLayer.getInstance().getObjectsById(AllyRelationship.class, "id_playerAlly", idAlly);
	}
	
	
	public static List<ContractAttendee> getAttendeesByAlly(
			Ally ally) {
		List<ContractAttendee> attendees = new ArrayList<ContractAttendee>(
			DataLayer.getInstance().getObjectsById(
				ContractAttendee.class, "id_ally", ally.getId()));
		
		
		return attendees;
	}
	
	// PnjDefender
	public static PnjDefender getPnjDefenderByPlayer(int idPlayer) {
		return DataLayer.getInstance().getObjectById(PnjDefender.class, "id_player", idPlayer);
	}
		
	public static PnjDefender getPnjDefenderByPnj(int idPnj) {
			return DataLayer.getInstance().getObjectById(PnjDefender.class, "id_pnj", idPnj);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //


}
