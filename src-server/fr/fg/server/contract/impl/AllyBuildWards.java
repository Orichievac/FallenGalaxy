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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fr.fg.server.contract.ContractHelper;
import fr.fg.server.contract.ContractState;
import fr.fg.server.contract.DataHelper;
import fr.fg.server.contract.NpcHelper;
import fr.fg.server.contract.ally.AllyContractModel;
import fr.fg.server.contract.ally.AllyLocationConstraints;
import fr.fg.server.contract.ally.AllyLocationConstraintsFactory;
import fr.fg.server.contract.ally.AllyRequirements;
import fr.fg.server.contract.ally.AllyRequirementsFactory;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractArea;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ward;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterWardBuiltEvent;
import fr.fg.server.events.impl.DialogUpdateEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class AllyBuildWards extends AllyContractModel implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
	NPC_RECIPIENT_REWARD = "recipientReward";
	
	private final static String
		KEY_NPC_ID = "npc",
		KEY_MARKERS = "markers",
		KEY_BUILT_WARDS_COUNT = "built";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static final AllyRequirements REQUIREMENTS =
		AllyRequirementsFactory.getLevelRequirements(2);

	private static final AllyLocationConstraints LOCATION_CONSTRAINTS =
		AllyLocationConstraintsFactory.getLevelLocationConstraints(2, 5, 1);
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AllyBuildWards() throws Exception {
		super(1, REQUIREMENTS, LOCATION_CONSTRAINTS);
		
		GameEventsDispatcher.addGameEventListener(this,
			AfterWardBuiltEvent.class,DialogUpdateEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void launch(Contract contract) {
		// Génère le PNJ qui donnera la récompense et contrôlera les balises
		Player player = NpcHelper.createAICharacter(contract.getId(),
			NpcHelper.FACTION_INDEPENDANT_NETWORK, true,
			NpcHelper.AVATAR_NETWORK_CONNECTION,
			NpcHelper.getRandomIndependantNetworkName());
		DataHelper.storeContractParameter(contract.getId(), KEY_NPC_ID, player.getId());
		
		List<ContractArea> contractAreas = contract.getAreas();
		synchronized (contractAreas) {
			for (ContractArea contractArea : contractAreas) {
				Area area = contractArea.getArea();
				
				// Génère les marqueurs qui indiqueront la position des balises
				// à construire
				Point location = area.getRandomFreeTiles(11, 11,
						Area.NO_SYSTEMS | Area.NO_OBJECTS | Area.NO_STRUCTURES, null);
				location.translate(6, 6);
				
				List<ContractAttendee> attendees = new ArrayList<ContractAttendee>(contract.getAttendees());
				
				for (ContractAttendee attendee : attendees) {
					if(attendee.getAlly().getMembers()!=null){
					Marker marker = new Marker(location.x, location.y,
						getMarkerText(contract, player.getAlly(), contractArea), Marker.VISIBILITY_ALLY,
						true, Utilities.now(), area.getId(),
						attendee.getAlly().getMembers().get(0).getId(), contract.getId());
					marker.save();
					
					DataHelper.storeAllyParameter(contract.getId(),
						attendee.getIdAlly(), KEY_MARKERS,
						contractArea.getId(), marker.getId());
					}
				}
			}
		}
	}
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof AfterWardBuiltEvent) {
			AfterWardBuiltEvent gameEvent = (AfterWardBuiltEvent) event;
			
			Ward ward = gameEvent.getWard();
			Player player = ward.getOwner();
			Ally ally = player.getAlly();
			if(ally != null){
			List<Contract> contracts = ally.getRunningContractsByType(getType());
			
			// Recherche parmi les contrats en cours du joueur si la balise
			// correspond à un emplacement constructible
			for (Contract contract : contracts) {
				if (ward.getPower() < getRequiredWardPower(contract) ||
						!(ward.getType().equals(Ward.TYPE_OBSERVER) ||
						  ward.getType().equals(Ward.TYPE_OBSERVER_INVISIBLE)))
					continue;
				
				List<ContractArea> contractAreas = contract.getAreas();
				synchronized (contractAreas) {
					for (ContractArea contractArea : contractAreas)
						if (contractArea.getIdArea() == ward.getIdArea()) {
							int builtWardsCount = getBuiltWardsCount(
									contract, ally, contractArea);
							
//							if (builtWardsCount >= getWardsCountToBuildByArea(contract))
//								continue;
							
							Marker marker = getMarkerByArea(
								contract, ally, contractArea);
							
							if (marker != null && Math.abs(marker.getX() - ward.getX()) <= 31 &&
									Math.abs(marker.getY() - ward.getY()) <= 31) {
								synchronized (ward.getLock()) {
									ward = DataAccess.getEditable(ward);
									ward.setIdOwner(getNpcId(contract));
									ward.save();
								}
								
								DataHelper.storeAllyParameter(contract.getId(),
									ally.getId(), KEY_BUILT_WARDS_COUNT,
									contractArea.getId(), builtWardsCount + 1);
								
								if (getWardsCountToBuildByArea(contract) ==
										getBuiltWardsCount(contract, ally, contractArea)) {
									marker.delete();
								} else {
									synchronized (marker.getLock()) {
										marker = DataAccess.getEditable(marker);
										marker.setMessage(getMarkerText(contract, ally, contractArea));
										marker.save();
									}
								}
								
								if (getTotalBuiltWardsCount(contract, ally) >=
										getTotalWardsCountToBuild(contract)){
									setSuccess(contract, ally);
									//giveReward(player, contract);
									contract.delete();
								}
								UpdateTools.queueContractsUpdate(player.getId());
								return;
							}
						}
					}
				}
			}
		}
	}
	
	
	

	@Override
	public void finalize(Contract contract) {
		Ally ally = contract.getAttendees().get(0).getAlly();
		LoggingSystem.getActionLogger().info("Mission AllyBuildWards terminée pour : "+ally.getName());
		
		List<Integer> idPlayers = ally.getIdMembers();

		for(Integer idPlayer : idPlayers)
		{
			Player player = DataAccess.getPlayerById(idPlayer);
			long xpToAdd=0;
			synchronized (player.getLock()) {
				// FIXME à améliorer
				
				player = DataAccess.getEditable(player);

				long teamReward = contract.getRewards().get(0).getValue();
				int allyMembersCount = player.getAlly().getMembers().size();
				double mediumPointsByMember = player.getAlly().getPoints()/allyMembersCount;
				
				long memberReward =	teamReward/allyMembersCount;
				
				double ratio = player.getPoints()/mediumPointsByMember;
				
				xpToAdd = (long)(memberReward * ratio);
				player.addXp(xpToAdd);
				player.save();
			}
			
			
			Event evenement = new Event(
					Event.EVENT_REWARD_PERSO,
					Event.TARGET_PLAYER,
					player.getId(), 
					0, 
					-1, 
					-1,
					xpToAdd+" xp"
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
	
	public Marker getMarkerByArea(Contract contract, Ally ally,
			ContractArea contractArea) {
		return DataAccess.getMarkerById(
			DataHelper.getAllyIntMapParameter(contract.getId(),
				ally.getId(), KEY_MARKERS, contractArea.getId()));
	}
	
	@Override
	public void createReward(Contract contract) {
		
// Ancienne version
//		ContractHelper.addXpReward(contract,
//				(long) Math.ceil(getTotalWardsCountToBuild(contract)*
//					getRequiredWardPower(contract)*getRequiredWardPower(contract)*
//							0.03));
		
		// Pour la création de vaisseaux
		// pour un point de puissance 0.02*0.6 (0.8 now)
		// un point de puissance = 80
		//0.6->0.8 car 30 crédits + 50 ressources hormis AM peu cher,
		//Ici c'est uniquement AM
		ContractHelper.addXpReward(contract,
				(long) Math.ceil(getTotalWardsCountToBuild(contract)*
						Ward.getWardCost(Ward.TYPE_OBSERVER, getRequiredWardPower(contract))
						/80*0.02*1.3));

	}
	
	@Override
	public void createRelationships(Contract contract) {
		ContractHelper.addRelationship(contract, contract.getVariant(), 5);
	}
	
	public int getFleetXpReward(Contract contract) {
		return (int) (400 + 5 * contract.getDifficulty());
	}
	
	
	public int getTotalBuiltWardsCount(Contract contract, Ally ally) {
		int total = 0;
		List<ContractArea> contractAreas = contract.getAreas();
		synchronized (contractAreas) {
			for (ContractArea contractArea : contractAreas)
				total += getBuiltWardsCount(contract, ally, contractArea);
		}
		return total;
	}
	
	public int getTotalWardsCountToBuild(Contract contract) {
		return contract.getAreas().size() * getWardsCountToBuildByArea(contract);
	}
	
	public int getBuiltWardsCount(Contract contract, Ally ally,
			ContractArea contractArea) {
		return DataHelper.getAllyNotNullIntMapParameter(contract.getId(),
			ally.getId(), KEY_BUILT_WARDS_COUNT, contractArea.getId());
	}
	
	public int getWardsCountToBuildByArea(Contract contract) {
		return 4 + (int) (contract.getDifficulty()*3.3);
	}
	
	public int getRequiredWardPower(Contract contract) {
		return  (int) (contract.getDifficulty()*3.5);
	}
	
	
	
	@Override
	public long getDifficulty(Ally ally) {
		long difficulty=0;
		long i=10000;
		
		while(i<ally.getPoints())
		{
			i*=2;
			difficulty++;
		}
		
		return (long) difficulty;
	}
	
	@Override
	public String getVariant(Ally ally) {
		return NpcHelper.FACTION_INDEPENDANT_NETWORK;
	}
	
	@Override
	public String getNpcAction(Contract contract, Player player, Fleet target) {
		return Fleet.NPC_ACTION_MISSION;
	}
	
	public String getMarkerText(Contract contract, Ally ally, ContractArea contractArea) {
		return "Construisez " + (getWardsCountToBuildByArea(contract) -
			getBuiltWardsCount(contract, ally, contractArea)) +
			" balises d'observation d'une durée de " +
			getRequiredWardPower(contract) +
			" jours à 30 cases maximum de cet emplacement";
	}
	
	@Override
	public String getDetailedGoal(Contract contract, Ally ally) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();
			List<ContractArea> contractAreas =
				DataAccess.getAreasByContract(contract.getId());
			
			// Aggrège les données par secteur
			ArrayList<Area> areas = new ArrayList<Area>();
			HashMap<Integer, Integer> builtWardsByArea = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> wardsToBuildByArea = new HashMap<Integer, Integer>();
			
			synchronized (contractAreas) {
				for (ContractArea contractArea : contractAreas) {
					int builtWards = getBuiltWardsCount(contract, ally, contractArea);
					int wardsToBuild = getWardsCountToBuildByArea(contract);
					int idArea = contractArea.getIdArea();
					
					if (!builtWardsByArea.containsKey(idArea)) {
						builtWardsByArea.put(idArea, builtWards);
						wardsToBuildByArea.put(idArea, wardsToBuild);
						areas.add(contractArea.getArea());
					} else {
						builtWardsByArea.put(idArea, builtWardsByArea.get(idArea) + builtWards);
						wardsToBuildByArea.put(idArea, wardsToBuildByArea.get(idArea) + wardsToBuild);
					}
				}
			}
			
			Collections.sort(areas, new Comparator<Area>() {
				public int compare(Area a1, Area a2) {
					return a1.getName().compareToIgnoreCase(a2.getName());
				}
			});
			
			buffer.append("Construisez des balises d'observation d'une durée d'au moins ");
			buffer.append(getRequiredWardPower(contract));
			buffer.append(" jours dans les secteurs suivants, près des positions marquées dans les secteurs suivants :<br/>");
			
			boolean first = true;
			for (Area area : areas) {
				if (first)
					first = false;
				else
					buffer.append("<br/>");
				
				buffer.append("Secteur ");
				buffer.append(area.getName());
				buffer.append(" : ");
				buffer.append(builtWardsByArea.get(area.getId()));
				buffer.append("/");
				buffer.append(wardsToBuildByArea.get(area.getId()));
			}
			
			return buffer.toString();
		} else {
			int hours_remaining = (int)((contract.getStateDate()+GameConstants.END_ALLY_REWARD-Utilities.now())/3600);
			return "<b>"+ hours_remaining+"</b> heures restantes.<br/>" +
					"Récupérez la récompense sur un de vos systèmes.";
		}
	}
	
	@Override
	public ContractState getState(Contract contract, Player player) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(getTotalBuiltWardsCount(contract, player.getAlly()));
			buffer.append("/");
			buffer.append(getTotalWardsCountToBuild(contract));
			buffer.append(" balises construites");
			
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
	public String getContractType() {
		return Contract.TYPE_ALLY_SOLO;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
