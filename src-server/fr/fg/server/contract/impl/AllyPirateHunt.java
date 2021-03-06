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

package fr.fg.server.contract.impl;

import java.util.List;

import fr.fg.server.data.Skill;
import fr.fg.server.contract.ContractHelper;
import fr.fg.server.contract.ContractState;
import fr.fg.server.contract.DataHelper;
import fr.fg.server.contract.NpcHelper;
import fr.fg.server.contract.ally.AllyContractModel;
import fr.fg.server.contract.ally.AllyLocationConstraints;
import fr.fg.server.contract.ally.AllyLocationConstraintsFactory;
import fr.fg.server.contract.ally.AllyRequirements;
import fr.fg.server.contract.ally.AllyRequirementsFactory;
import fr.fg.server.contract.dialog.DialogEntry;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Contract;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterBattleEvent;
import fr.fg.server.events.impl.BeforeBattleEvent;
import fr.fg.server.events.impl.DialogUpdateEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class AllyPirateHunt extends AllyContractModel implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
	NPC_RECIPIENT_REWARD = "recipientReward";
	
	private final static String
		KEY_NPC_ID = "npc",
		KEY_REQUIRED_KILLS = "requiredKills",
		KEY_KILLS = "kills",
		KEY_LAST_BATTLE = "lastBattle",
		KEY_PLAYER_LEVEL = "playerLevel";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static final AllyRequirements REQUIREMENTS =
		AllyRequirementsFactory.getPointsRequirements(5000);
	
	private static final AllyLocationConstraints LOCATION_CONSTRAINTS =
		AllyLocationConstraintsFactory.getPointsLocationConstraints(15000, 3);
	
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AllyPirateHunt() throws Exception {
		super(8, REQUIREMENTS, LOCATION_CONSTRAINTS);
		
		GameEventsDispatcher.addGameEventListener(
			this, AfterBattleEvent.class, DialogUpdateEvent.class, BeforeBattleEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void initialize(Contract contract) {
		// Génère et sauvegarde le type et le nombre de FP à détruire (IA?)
		// 20 par joueur de level joueur ou +1 à détruire
		 int kills = (int) (contract.getDifficulty()*5);
		
		DataHelper.storeContractParameter(contract.getId(),
				KEY_REQUIRED_KILLS, kills);
		
	}
	
	public void launch(Contract contract) {
		// Génère le PNJ qui donnera la récompense et contrôlera les balises
		if (contract.getVariant().equals(NpcHelper.FACTION_GDO)){
			Player player = NpcHelper.createAICharacter(contract.getId(),
					NpcHelper.FACTION_GDO, true,
					NpcHelper.AVATAR_GDO_OFFICER,
					NpcHelper.getRandomCharacterName(true));
			DataHelper.storeContractParameter(contract.getId(), KEY_NPC_ID, player.getId());
		}else if(contract.getVariant().equals(NpcHelper.FACTION_BROTHERHOOD)){
			Player player = NpcHelper.createAICharacter(contract.getId(),
					NpcHelper.FACTION_BROTHERHOOD, true,
					NpcHelper.AVATAR_BROTHERHOOD_PARIA,
					NpcHelper.getRandomCharacterName(true));
			DataHelper.storeContractParameter(contract.getId(), KEY_NPC_ID, player.getId());
		}else if(contract.getVariant().equals(NpcHelper.FACTION_INDEPENDANT_NETWORK)){
			Player player = NpcHelper.createAICharacter(contract.getId(),
					NpcHelper.FACTION_INDEPENDANT_NETWORK, true,
					NpcHelper.AVATAR_NETWORK_CONNECTION,
					NpcHelper.getRandomIndependantNetworkName());
			DataHelper.storeContractParameter(contract.getId(), KEY_NPC_ID, player.getId());
		
		}
	}
	
//	@Override
//	public void finalize(Contract contract) {
//		Ally ally = contract.getAttendees().get(0).getAlly();
//		
//		
//		List<StarSystem> systems = ally.getSystems();
//		
//		Player npc;
//		
//		if (contract.getVariant().equals(NpcHelper.FACTION_GDO))
//			npc = NpcHelper.createAICharacter(contract.getId(),
//				NpcHelper.FACTION_GDO, true, NpcHelper.AVATAR_GDO_OFFICER);
//		else if(contract.getVariant().equals(NpcHelper.FACTION_BROTHERHOOD))
//			npc = NpcHelper.createAICharacter(contract.getId(),
//				NpcHelper.FACTION_BROTHERHOOD, true, NpcHelper.AVATAR_BROTHERHOOD_PARIA);
//		else
//			npc = NpcHelper.createAICharacter(contract.getId(),
//				NpcHelper.FACTION_INDEPENDANT_NETWORK, true, NpcHelper.AVATAR_NETWORK_CONNECTION);
//
//		
//		
//		synchronized (systems) {
//			for (StarSystem system : systems) {
//				Area area = system.getArea();
//				Point tile;
//				if (area.isFreeTile(system.getX() - 1, system.getY() - 3, Area.NO_FLEETS, null))
//					tile = new Point(system.getX() - 1, system.getY() - 3);
//				else
//					tile = system.getFreeTile();
//				
//				NpcHelper.spawnFleet(npc, area,
//					contract.getId(), NPC_RECIPIENT_REWARD, tile, Ship.HURRICANE,
//					(int) contract.getDifficulty() + 1, true);
//			}
//		}
//	}
	
	@Override
	public void finalize(Contract contract) {
		Ally ally = contract.getAttendees().get(0).getAlly();
		LoggingSystem.getActionLogger().info("Mission AllyPirateHunt terminée pour : "+ally.getName());
		
		List<Integer> idPlayers = ally.getIdMembers();

		for(Integer idPlayer : idPlayers)
		{
			Player player = DataAccess.getPlayerById(idPlayer);
			long creditToAdd=0;
			synchronized(player.getLock())
			{
				player = DataAccess.getEditable(player);
				
				long teamReward = contract.getRewards().get(0).getValue();
				int allyMembersCount = player.getAlly().getMembers().size();
				double mediumPointsByMember = player.getAlly().getPoints()/allyMembersCount;
				
				long memberReward =	teamReward/allyMembersCount;
				
				double ratio = player.getPoints()/mediumPointsByMember;
				
				creditToAdd = (long)(memberReward * ratio);
				
				player.addCredits(creditToAdd);
				player.save();
				
			}
			
			Event evenement = new Event(
					Event.EVENT_REWARD_PERSO,
					Event.TARGET_PLAYER,
					player.getId(), 
					0, 
					-1, 
					-1,
					creditToAdd+" crédits"
					);
			evenement.save();
				
			
			UpdateTools.queueNewEventUpdate(player.getId());
			UpdateTools.queueXpUpdate(player.getId(), false);
			UpdateTools.queueContractsUpdate(player.getId(), false);
			UpdateTools.queueAreaUpdate(player);
			
		}
		
	}
	
	public int getNpcId(Contract contract) {
		return DataHelper.getContractIntParameter(contract.getId(), KEY_NPC_ID);
	}
	
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof BeforeBattleEvent) {
			
			List<Contract> contracts=null;
			
			BeforeBattleEvent gameEvent = (BeforeBattleEvent) event;
			
			Player player = gameEvent.getAttackingFleet().getOwner();
			contracts = player.getRunningContractsByType(getType());
			
			for (Contract contract : contracts) {
				DataHelper.storeContractParameter(contract.getId(),
						KEY_PLAYER_LEVEL, player.getId(), player.getLevel());
			}
		}
		
		if (event instanceof AfterBattleEvent) {
			
			AfterBattleEvent gameEvent = (AfterBattleEvent) event;
			
			int defendingFleetId = gameEvent.getDefendingFleetBefore().getId();
			Fleet attackingFleet = gameEvent.getAttackingFleetBefore();
			Player player = attackingFleet.getOwner();
			
			List<Contract> contracts=null;
			if(player.getAlly()!=null && player.getRunningContractsByType("PirateHunt").size() ==0)
			{
				contracts = player.getAlly().getRunningContractsByType(getType());
			}
//			else
//				contracts = player.getRunningContractsByType(getType());
			// Vérifie que la flotte à détruire est une flotte pirate
			if (gameEvent.getDefendingFleetBefore().getSkillLevel(
					Skill.SKILL_PIRATE) == -1)
				return;
			
			if(contracts!=null)
			for (Contract contract : contracts) {
				
				int playerLevel = DataHelper.getContractIntMapParameter(
						contract.getId(), KEY_PLAYER_LEVEL, player.getId());
				
				// Si la flotte a un lvl > au joueur alors elle est marqué
				// Ou si la flotte à le niveau max des PNJ, alors il faut une flotte de ce niveau la
				if (gameEvent.getDefendingFleetBefore().getPowerLevel() >=
					playerLevel)
					{
					// Teste si la flotte attaquée a été détruite
						//Ou juste diminué
					if (gameEvent.isDefendingFleetDestroyed()) {
						addKill(contract,player,false);
						if (getKillsDone(contract) >= getTotalKills(contract)){
							setSuccess(contract, player.getAlly());
							contract.delete();
						}
						
					
						List<Player> members = player.getAlly().getMembers();
						
						synchronized (members) {
							for (Player member : members)
								UpdateTools.queueContractsUpdate(member.getId());
						}
						
						return;
					} else {
						DataHelper.storeContractParameter(contract.getId(),
								KEY_LAST_BATTLE, player.getId(), defendingFleetId);
						
						
						UpdateTools.queueNewEventUpdate(player.getId());
						UpdateTools.queueContractsUpdate(player.getId(), false);
						UpdateTools.queueAreaUpdate(player);
						if(gameEvent.getDefendingFleetBefore().getOwner().isAi()==false)
						{
						UpdateTools.queueNewEventUpdate(gameEvent.getDefendingFleetBefore().getOwner().getId());
						UpdateTools.queueAreaUpdate(gameEvent.getDefendingFleetBefore().getOwner());
						}
						
						return;
						
					}
				} else { 
					if (DataHelper.getContractIntMapParameter(
						contract.getId(), KEY_LAST_BATTLE, player.getId()) != null &&
						defendingFleetId == 
							DataHelper.getContractNotNullIntMapParameter(contract.getId(),KEY_LAST_BATTLE, player.getId())) {
					// Teste si la flotte attaquée a été détruite
					if (gameEvent.isDefendingFleetDestroyed())
						addKill(contract,player,false);
					
					if (getKillsDone(contract) >=
						getTotalKills(contract)){
					setSuccess(contract, player.getAlly());
					contract.delete();
					}
					
					return;
					}
				}	
				
			}
		}
		else if (event instanceof DialogUpdateEvent) {
			DialogUpdateEvent gameEvent = (DialogUpdateEvent) event;
			Contract contract = gameEvent.getContract();
			Player player = gameEvent.getPlayer();
			Fleet source = gameEvent.getSourceFleet();
			Fleet target = gameEvent.getTargetFleet();
			
			if (contract != null && contract.getType().equals(getType())) {
				if (target.getNpcType().equals(NPC_RECIPIENT_REWARD)) {
					// Fin de la mission
						if (gameEvent.getTargetEntry().equals(DialogEntry.END_OF_DIALOG)) {


								
							
//					UpdateTools.queueNewEventUpdate(player.getId());
//					UpdateTools.queueXpUpdate(player.getId(), false);
//					UpdateTools.queueContractsUpdate(player.getId(), false);
//					UpdateTools.queueAreaUpdate(player);
					}
				}
			}
		}
	}
	
	public void addKill(Contract contract, Player player, boolean end) {
		DataHelper.storeContractParameter(contract.getId(),KEY_KILLS,
				DataHelper.getContractNotNullIntParameter(
						contract.getId(), KEY_KILLS)+1);
		
		UpdateTools.queueContractsUpdate(player.getId());
		UpdateTools.queueAreaUpdate(player);
	}
	
	public long getDifficulty(Ally ally) {
		
		return ally.getMembers().size() + getPointsDifficulty(ally.getPoints());
	}
	
	
	private int getPointsDifficulty(long points) {
		
		int fleetToAdd=0;
		long i=10000;
		
		while(i<points)
		{
			i*=3;
			fleetToAdd++;
		}
			
		return fleetToAdd;
	}

	public void createReward(Contract contract) {
		if(contract.getAttendees().get(0).getAlly()!=null)
		ContractHelper.addCreditReward(contract,
			(long) (contract.getAttendees().get(0).getAlly().getPoints()*0.8));
	}
	
	@Override
	public void createRelationships(Contract contract) {
		ContractHelper.addRelationship(contract, contract.getVariant(), 5);
		if (contract.getVariant().equals(NpcHelper.FACTION_GDO))
			ContractHelper.addRelationship(contract, NpcHelper.FACTION_BROTHERHOOD, -4);
		else if(contract.getVariant().equals(NpcHelper.FACTION_BROTHERHOOD))
			ContractHelper.addRelationship(contract, NpcHelper.FACTION_INDEPENDANT_NETWORK, -4);
		else if(contract.getVariant().equals(NpcHelper.FACTION_INDEPENDANT_NETWORK))
			ContractHelper.addRelationship(contract, NpcHelper.FACTION_GDO, -4);
	}
	
	
	public String getVariant(Ally ally) {
		return NpcHelper.getRandomFaction(
			NpcHelper.FACTION_GDO,
			NpcHelper.FACTION_INDEPENDANT_NETWORK
		);
	}
	

	@Override
	public String getDetailedGoal(Contract contract, Ally ally) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();

				buffer.append("Détruisez ");
				buffer.append(getTotalKills(contract));
				buffer.append(" flottes pirates de puissance supérieure ou égale à votre niveau joueur.");
				buffer.append("<br/>");
				buffer.append("Si votre niveau est supérieur à 40, vous devez détruire les flottes pirates de puissance 40 avec des puissances 40 maximum.");
				buffer.append("<br/>Une fois qu'une flotte a été attaqué de cette manière, vous pouvez l'attaquer avec n'importe quelle puissance.");
				buffer.append("<br/><br/><b>");
				buffer.append(getKillsDone(contract));
				buffer.append("/");
				buffer.append(getTotalKills(contract));
				buffer.append("</b> ");
				buffer.append("flottes pirates détruites.");
				buffer.append("<br/>");
			
			
			return buffer.toString();
		} else {
			int hours_remaining = (int)((contract.getStateDate()+GameConstants.END_ALLY_REWARD-Utilities.now())/3600);
			return "<b>"+ hours_remaining+"</b> heures restantes.<br/>" +
					"Récupérez la récompense sur un de vos systèmes.";
		}
	}
	
	public int getTotalKills(Contract contract){
		return DataHelper.getContractNotNullIntParameter(
				contract.getId(), KEY_REQUIRED_KILLS);
	}
	
	public int getKillsDone(Contract contract){
		return DataHelper.getContractNotNullIntParameter(
				contract.getId(), KEY_KILLS);
	}
	
	public ContractState getState(Contract contract, Player player) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(getKillsDone(contract));
			buffer.append("/");
			buffer.append(getTotalKills(contract));
			buffer.append(" ");
			buffer.append("flottes pirates détruites");
			
			return new ContractState(
				getTitle(contract),
				contract.getState(),
				buffer.toString(),
				0, null);
		} else {
			int hours_remaining = (int)((contract.getStateDate()+GameConstants.END_ALLY_REWARD-Utilities.now())/3600);
			return new ContractState(
				getTitle(contract),
				contract.getState(),
				"Récupérez la récompense (<b>"+hours_remaining+"H</b> restantes)",
				0, null
			);
		}
	}

	@Override
	public String getNpcAction(Contract contract, Player player, Fleet target) {
		return Fleet.NPC_ACTION_MISSION;
	}
	
	@Override
	public String getContractType() {
		return Contract.TYPE_ALLY_SOLO;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	
	
//	Partie anciennement utilisée
//	List<Fleet> contractFleets = DataAccess.getFleetsByContract(contract.getId());
//	int number = 0;
//	for(Fleet contractFleet : contractFleets)
//	{
//		
//		//Suppression des flottes de récompenses du joueur
//		if(contractFleet.getSystemOver()!=null &&
//				contractFleet.getSystemOver().getIdOwner()==player.getId())
//			contractFleet.delete();
//		else
//			number++;
//	}
//	
//	//suppression du contrat si tout le monde à récupérer la récompense
//	if(number==0)
//		contract.delete();

	
}
