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
import fr.fg.server.contract.dialog.DialogEntry;
import fr.fg.server.contract.player.PlayerContractModel;
import fr.fg.server.contract.player.PlayerLocationConstraints;
import fr.fg.server.contract.player.PlayerLocationConstraintsFactory;
import fr.fg.server.contract.player.PlayerRequirements;
import fr.fg.server.contract.player.PlayerRequirementsFactory;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Contract;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterBattleEvent;
import fr.fg.server.events.impl.BeforeBattleEvent;
import fr.fg.server.events.impl.DialogUpdateEvent;
import fr.fg.server.util.LoggingSystem;

public class PirateHunt extends PlayerContractModel implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	

	private final static String
		KEY_NPC_ID = "npc",
		KEY_REQUIRED_KILLS = "requiredKills",
		KEY_KILLS = "kills",
		KEY_LAST_BATTLE = "lastBattle",
		KEY_PLAYER_LEVEL = "playerLevel";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static final PlayerRequirements REQUIREMENTS =
		PlayerRequirementsFactory.getPointsRequirements(5000);
	
	private static final PlayerLocationConstraints LOCATION_CONSTRAINTS =
		PlayerLocationConstraintsFactory.getPointsLocationConstraints(0);
	
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PirateHunt() throws Exception {
		super(8, REQUIREMENTS, LOCATION_CONSTRAINTS);
		
		GameEventsDispatcher.addGameEventListener(
			this, AfterBattleEvent.class, DialogUpdateEvent.class, BeforeBattleEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void initialize(Contract contract) {

		// Génère et sauvegarde le type et le nombre de FP à détruire (IA?)
		// 20 par joueur de level joueur ou +1 à détruire
		 int kills = (int) (contract.getDifficulty()*4);
		
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
	
	@Override
	public void finalize(Contract contract) {
		Player player = contract.getAttendees().get(0).getPlayer();
		LoggingSystem.getActionLogger().info("Mission PirateHunt terminée pour : "+player.getLogin());
	
		synchronized(player.getLock())
		{
			player = DataAccess.getEditable(player);
			
			player.addCredits(contract.getRewards().get(0).getValue());
			player.save();

				LoggingSystem.getActionLogger().info("reward to "+player.getLogin()
						+" = "+contract.getRewards().get(0).getValue()+ " credits.");
				
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
				contracts = player.getRunningContractsByType(getType());
				
			// Vérifie que la flotte à détruire est une flotte pirate
			if (gameEvent.getDefendingFleetBefore().getSkillLevel(
					Skill.SKILL_PIRATE) == -1)
				return;

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
						if (getKillsDone(contract) >=
							getTotalKills(contract)){
							setSuccess(contract, player);
							contract.delete();
						}
						UpdateTools.queueContractsUpdate(player.getId());
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
							setSuccess(contract, player);
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
		
		if (contract != null && contract.getType().equals(getType())) {
			if (gameEvent.getTargetEntry().equals(DialogEntry.END_OF_DIALOG)) {
				addKill(contract, player, true);
				return;
			}
			}
		 }
	}
	
	public void addKill(Contract contract, Player player, boolean end) {
		DataHelper.storeContractParameter(contract.getId(),KEY_KILLS,
				DataHelper.getContractNotNullIntParameter(
						contract.getId(), KEY_KILLS)+1);
		// Fin de contrat
		if (DataHelper.getContractNotNullIntParameter(
				contract.getId(), KEY_KILLS) >=
					DataHelper.getContractIntParameter(
							contract.getId(), KEY_REQUIRED_KILLS) &&
							end){
		
			synchronized(player.getLock())
			{
				player = DataAccess.getEditable(player);
				
				player.addCredits(contract.getRewards().get(0).getValue());
				player.save();
	
					LoggingSystem.getActionLogger().info("reward to "+player.getLogin()
							+" = "+contract.getRewards().get(0).getValue()+ " credits");
					
					UpdateTools.queueContractsUpdate(player.getId(), false);
					UpdateTools.queueAreaUpdate(player);
			}
			
			contract.delete();
		}
		else
		{
		UpdateTools.queueContractsUpdate(player.getId());
		UpdateTools.queueAreaUpdate(player);
		}

	}
	
	public long getDifficulty(Player player) {
		
		return getPointsDifficulty(player.getPoints());
	}
	
	
	private int getPointsDifficulty(long points) {
		
		int fleetToAdd=2;
		long i=10000;
		
		while(i<points)
		{
			i*=3;
			fleetToAdd++;
		}
		return fleetToAdd;
	}

	public void createReward(Contract contract) {
		if(contract.getAttendees().get(0).getPlayer()!=null)
		ContractHelper.addCreditReward(contract,
			(long) (contract.getAttendees().get(0).getPlayer().getPoints()*0.75));
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
	
	
	public String getVariant(Player player) {
		return NpcHelper.getRandomFaction(
			NpcHelper.FACTION_GDO,
			NpcHelper.FACTION_INDEPENDANT_NETWORK
		);
	}
	

	@Override
	public String getDetailedGoal(Contract contract, Player player) {
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
			return "Récupérez la récompense.";
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
			return new ContractState(
				getTitle(contract),
				contract.getState(),
				"Récupérez la récompense",
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
		return Contract.TYPE_SOLO;
	}

	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
