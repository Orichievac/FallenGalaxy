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

package fr.fg.server.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.fg.client.data.ContractAttendeeData;
import fr.fg.client.data.ContractData;
import fr.fg.client.data.ContractRelationshipData;
import fr.fg.client.data.ContractRewardData;
import fr.fg.client.data.ContractStateData;
import fr.fg.client.data.ContractsData;
import fr.fg.client.data.RelationshipData;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.contract.ContractState;
import fr.fg.server.contract.NpcHelper;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyRelationship;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractArea;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.ContractRelationship;
import fr.fg.server.data.ContractReward;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Relationship;
import fr.fg.server.data.Sector;
import fr.fg.server.util.JSONStringer;

public class ContractTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getPlayerContracts(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		// Offres de missions
		json.object().
			key(ContractsData.FIELD_CONTRACT_OFFERS).	array();
		
		List<ContractAttendee> playerInvolvements =
			DataAccess.getAttendeesByPlayer(player);
		
		for (ContractAttendee playerInvolvement : playerInvolvements) {
			Contract contract = playerInvolvement.getContract();
			
			if (!contract.getState().equals(Contract.STATE_WAITING))
				continue;
			
			String missionType="";
			if(contract.getTarget()==Contract.TARGET_PLAYER)
			{
				if(contract.getMaxAttendees() > 1)
					missionType="Mission PvP";
				else
					missionType="Mission solo";
			}
			else if(contract.getTarget()==Contract.TARGET_ALLY){
				if(contract.getMaxAttendees() > 1)
					missionType="Mission AvA";
				else
					missionType="Mission d'alliance";
			}
			
			json.object().
				key(ContractData.FIELD_ID).			value(contract.getId()).
				key(ContractData.FIELD_REGISTERED).	value(playerInvolvement.isRegistered()).
				key(ContractData.FIELD_GOAL).		value(ContractManager.getGoal(contract)).
				key(ContractData.FIELD_TITLE).		value(ContractManager.getTitle(contract)).
				key(ContractData.FIELD_FINISHED).	value(false).
				key(ContractData.FIELD_MISSION_TYPE). value(missionType).
				key(ContractData.FIELD_ACCEPTED).	value(playerInvolvement.isRegistered()).
				key(ContractData.FIELD_DESCRIPTION).value(ContractManager.getDescription(contract));
			
			// Récompense
			json.key(ContractData.FIELD_REWARD).		array();
			List<ContractReward> rewards = contract.getRewards();
			
			synchronized (rewards) {
				for (ContractReward reward : rewards) {
					json.object().
						key(ContractRewardData.FIELD_TYPE).	value(reward.getType()).
						key(ContractRewardData.FIELD_KEY).	value(reward.getKeyName()).
						key(ContractRewardData.FIELD_VALUE).value(reward.getValue()).
						endObject();
				}
			}
			
			json.endArray();
			
			// Relations
			json.key(ContractData.FIELD_RELATIONSHIPS).	array();
			List<ContractRelationship> relationships = new ArrayList<ContractRelationship>(
				DataAccess.getRelationshipsByContract(contract.getId()));
			
			Collections.sort(relationships, new Comparator<ContractRelationship>() {
				public int compare(ContractRelationship r1,
						ContractRelationship r2) {
					return r1.getModifier() > r2.getModifier() ? -1 : 1;
				}
			});
			
			for (ContractRelationship relationship : relationships) {
				json.object().
					key(ContractRelationshipData.FIELD_FACTION_ID).		value(NpcHelper.getFactionByAlly(relationship.getIdAlly())).
					key(ContractRelationshipData.FIELD_FACTION_NAME).	value(relationship.getAlly().getName()).
					key(ContractRelationshipData.FIELD_MODIFIER).		value(relationship.getModifier()).
					endObject();
			}
			
			json.endArray().
				key(ContractData.FIELD_SECTORS).		array();
			
			List<String> sectorNames = new ArrayList<String>();
			List<ContractArea> contractAreas = contract.getAreas();
			
			synchronized (contractAreas) {
				if(contractAreas.size()==0 || contract.getType().equals("PirateHunt") ||
						contract.getType().equals("PirateOffensive") ||
						contract.getType().equals("AllyPirateHunt") ||
						contract.getType().equals("AllyPirateOffensive"))
					sectorNames.add("Tous");
				else
				{
					for (ContractArea contractArea : contractAreas) {
						Sector sector = contractArea.getArea().getSector();
						if (!sectorNames.contains(sector.getName()))
							sectorNames.add(sector.getName());
					}
				}
			}
			
			for (String sectorName : sectorNames)
				json.value(sectorName);
			
			json.endArray().
				endObject();
		}
		
		json.endArray();
		
		// Missions en cours
		json.key(ContractsData.FIELD_ACTIVE_CONTRACT).	array();
		
		for (ContractAttendee playerInvolvement : playerInvolvements) {
			Contract contract = playerInvolvement.getContract();
			
			String missionType="";
			if(contract.getTarget()==Contract.TARGET_PLAYER)
			{
				if(contract.getMaxAttendees() > 1)
					missionType="Mission PvP";
				else
					missionType="Mission solo";
			}
			else if(contract.getTarget()==Contract.TARGET_ALLY){
				if(contract.getMaxAttendees() > 1)
					missionType="Mission AvA";
				else
					missionType="Mission d'alliance";
			}
				
			
			if (contract.getState().equals(Contract.STATE_WAITING))
				continue;
			
			
			json.object().
				key(ContractData.FIELD_ID).			value(contract.getId()).
				key(ContractData.FIELD_GOAL).		value(ContractManager.getDetailedGoal(contract, player)).
				key(ContractData.FIELD_TITLE).		value(ContractManager.getTitle(contract)).
				key(ContractData.FIELD_DESCRIPTION).value(ContractManager.getDescription(contract)).
				key(ContractData.FIELD_MISSION_TYPE). value(missionType).
				key(ContractData.FIELD_FINISHED).	value(contract.getState().equals(Contract.STATE_FINALIZING)).
				key(ContractData.FIELD_ACCEPTED).	value(true).
				key(ContractData.FIELD_ATTENDEES).	array();
			
			// Joueurs inscrits pour la mission
			List<ContractAttendee> attendees = contract.getAttendees();
			
			synchronized (attendees) {
				for (ContractAttendee attendee : attendees) {
					if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
						Player attendeePlayer = attendee.getPlayer();
						
						json.object().
							key(ContractAttendeeData.FIELD_TYPE).		value(Contract.TARGET_PLAYER).
							key(ContractAttendeeData.FIELD_LOGIN).		value(attendeePlayer.getLogin()).
							key(ContractAttendeeData.FIELD_TREATY).		value(player.getTreatyWithPlayer(attendeePlayer)).
							key(ContractAttendeeData.FIELD_ALLY_NAME).	value(attendeePlayer.getAllyName()).
							key(ContractAttendeeData.FIELD_ALLY_TAG).	value(attendeePlayer.getAllyTag()).
							endObject();
					} else {
						Ally ally = attendee.getAlly();
						
						json.object().
							key(ContractAttendeeData.FIELD_TYPE).		value(Contract.TARGET_ALLY).
							key(ContractAttendeeData.FIELD_TREATY).		value(player.getAlly().getTreatyWithAlly(ally)).
							key(ContractAttendeeData.FIELD_ALLY_NAME).	value(ally.getName()).
							endObject();
					}
				}
			}
			
			json.endArray();
			
			// Récompense de la mission
			json.key(ContractData.FIELD_REWARD).		array();
			List<ContractReward> rewards = DataAccess.getRewardsByContract(contract.getId());
			
			for (ContractReward reward : rewards) {
				json.object().
					key(ContractRewardData.FIELD_TYPE).	value(reward.getType()).
					key(ContractRewardData.FIELD_KEY).	value(reward.getKeyName()).
					key(ContractRewardData.FIELD_VALUE).value(reward.getValue()).
					endObject();
			}
			
			json.endArray();
			
			//Relation
			json.key(ContractData.FIELD_RELATIONSHIPS).	array();
			List<ContractRelationship> relationships = new ArrayList<ContractRelationship>(
				DataAccess.getRelationshipsByContract(contract.getId()));
			
			Collections.sort(relationships, new Comparator<ContractRelationship>() {
				public int compare(ContractRelationship r1,
						ContractRelationship r2) {
					return r1.getModifier() > r2.getModifier() ? -1 : 1;
				}
			});
			
			
			for (ContractRelationship relationship : relationships) {
				json.object().
					key(ContractRelationshipData.FIELD_FACTION_ID).		value(NpcHelper.getFactionByAlly(relationship.getIdAlly())).
					key(ContractRelationshipData.FIELD_FACTION_NAME).	value(relationship.getAlly().getName()).
					key(ContractRelationshipData.FIELD_MODIFIER).		value(relationship.getModifier()).
					endObject();
			}
			
			json.endArray().
				endObject();
		}
		
		json.endArray();
		
		// Relations d'alliance avec les autres factions
		json.key(ContractsData.FIELD_ALLY_RELATIONSHIPS).array();
		if(player.getAlly()!=null){
			List<AllyRelationship> allyRelationships = player.getAlly().getRelationships();
		
		
		synchronized (allyRelationships) {
			for (String faction : NpcHelper.ALL_FACTIONS) {
				Ally ally = NpcHelper.getAllyFaction(faction);
				double value = 0;
				
				for (AllyRelationship relationship : allyRelationships)
					if (relationship.getIdAlly() == ally.getId()) {
						value = relationship.getValue();
						break;
					}
				
				json.object().
					key(RelationshipData.FIELD_FACTION_ID).			value(faction).
					key(RelationshipData.FIELD_FACTION_NAME).		value(ally.getName()).
					key(RelationshipData.FIELD_TYPE_RELATIONSHIP). 	value(1).
					key(RelationshipData.FIELD_LEVEL).				value(Relationship.getRelationshipLevel(value)).
					key(RelationshipData.FIELD_VALUE).				value(value > 0 ? (int) Math.floor(value) : (int) Math.ceil(value)).
					endObject();
			}
		}
		}
		json.endArray();
		
		// Relations avec les autres factions
		json.key(ContractsData.FIELD_RELATIONSHIPS).array();
		List<Relationship> relationships = player.getRelationships();
		
		synchronized (relationships) {
			for (String faction : NpcHelper.ALL_FACTIONS) {
				Ally ally = NpcHelper.getAllyFaction(faction);
				double value = 0;
				
				for (Relationship relationship : relationships)
					if (relationship.getIdAlly() == ally.getId()) {
						value = relationship.getValue();
						break;
					}
				
				json.object().
					key(RelationshipData.FIELD_FACTION_ID).		value(faction).
					key(RelationshipData.FIELD_FACTION_NAME).	value(ally.getName()).
					key(RelationshipData.FIELD_TYPE_RELATIONSHIP). value(0).
					key(RelationshipData.FIELD_LEVEL).			value(Relationship.getRelationshipLevel(value)).
					key(RelationshipData.FIELD_VALUE).			value(value > 0 ? (int) Math.floor(value) : (int) Math.ceil(value)).
					endObject();
			}
		}
		
		json.endArray().
			endObject();
		
		
		return json;
	}

	public static JSONStringer getContractsState(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		json.array();
		List<ContractAttendee> involvements = DataAccess.getAttendeesByPlayer(player);
		
		for (ContractAttendee involvement : involvements) {
			if (!involvement.isRegistered())
				continue;
			
			Contract contract = involvement.getContract();
			ContractState state = ContractManager.getContractState(contract, player);
			
			json.object().
				key(ContractStateData.FIELD_NAME).			value(state.getName()).
				key(ContractStateData.FIELD_STATUS).		value(state.getStatus()).
				key(ContractStateData.FIELD_DESCRIPTION).	value(state.getDescription()).
				key(ContractStateData.FIELD_ID_AREA).		value(state.getIdArea());
			
			if (state.getLocation() != null)
				json.key(ContractStateData.FIELD_X).		value(state.getLocation().x).
					key(ContractStateData.FIELD_Y).			value(state.getLocation().y);
			
			json.endObject();
		}
		
		json.endArray();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
