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
import fr.fg.server.contract.dialog.DialogEntry;
import fr.fg.server.contract.dialog.Parameter;
import fr.fg.server.contract.dialog.ParameterType;
import fr.fg.server.contract.player.PlayerContractModel;
import fr.fg.server.contract.player.PlayerLocationConstraints;
import fr.fg.server.contract.player.PlayerLocationConstraintsFactory;
import fr.fg.server.contract.player.PlayerRequirements;
import fr.fg.server.contract.player.PlayerRequirementsFactory;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractArea;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.ContractReward;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Ward;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterWardBuiltEvent;
import fr.fg.server.events.impl.DialogUpdateEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class BuildWards extends PlayerContractModel implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //

	private final static String
		KEY_NPC_ID = "npc",
		KEY_MARKERS = "markers",
		KEY_BUILT_WARDS_COUNT = "built";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static final PlayerRequirements REQUIREMENTS =
		PlayerRequirementsFactory.getLevelRequirements(2);

	private static final PlayerLocationConstraints LOCATION_CONSTRAINTS =
		PlayerLocationConstraintsFactory.getLevelLocationConstraints(2, 5, 1);
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BuildWards() throws Exception {
		super(1, REQUIREMENTS, LOCATION_CONSTRAINTS);
		
		GameEventsDispatcher.addGameEventListener(this,
			AfterWardBuiltEvent.class, DialogUpdateEvent.class);
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
					Marker marker = new Marker(location.x, location.y,
						getMarkerText(contract, player, contractArea), Marker.VISIBILITY_PLAYER,
						true, Utilities.now(), area.getId(),
						attendee.getIdPlayer(), contract.getId());
					marker.save();
					
					DataHelper.storePlayerParameter(contract.getId(),
						attendee.getIdPlayer(), KEY_MARKERS,
						contractArea.getId(), marker.getId());
				}
			}
		}
	}
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof AfterWardBuiltEvent) {
			AfterWardBuiltEvent gameEvent = (AfterWardBuiltEvent) event;
			
			Ward ward = gameEvent.getWard();
			Player player = ward.getOwner();
			List<Contract> contracts = player.getRunningContractsByType(getType());
			
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
									contract, player, contractArea);
							
							if (builtWardsCount >= getWardsCountToBuildByArea(contract))
								continue;
							
							Marker marker = getMarkerByArea(
								contract, player, contractArea);
							
							if (marker != null && Math.abs(marker.getX() - ward.getX()) <= 6 &&
									Math.abs(marker.getY() - ward.getY()) <= 6) {
								synchronized (ward.getLock()) {
									ward = DataAccess.getEditable(ward);
									ward.setIdOwner(getNpcId(contract));
									ward.save();
								}
								
								DataHelper.storePlayerParameter(contract.getId(),
									player.getId(), KEY_BUILT_WARDS_COUNT,
									contractArea.getId(), builtWardsCount + 1);
								
								if (getWardsCountToBuildByArea(contract) ==
										getBuiltWardsCount(contract, player, contractArea)) {
									marker.delete();
								} else {
									synchronized (marker.getLock()) {
										marker = DataAccess.getEditable(marker);
										marker.setMessage(getMarkerText(contract, player, contractArea));
										marker.save();
									}
								}
								
								if (getTotalBuiltWardsCount(contract, player) ==
										getTotalWardsCountToBuild(contract))
									setSuccess(contract, player);
								
								UpdateTools.queueContractsUpdate(player.getId());
								return;
							}
						}
				}
			}
		} else if (event instanceof DialogUpdateEvent) {
			DialogUpdateEvent gameEvent = (DialogUpdateEvent) event;
			
			Contract contract = gameEvent.getContract();
			Player player = gameEvent.getPlayer();
			Fleet source = gameEvent.getSourceFleet();
			
			if (contract != null && contract.getType().equals(getType())) {
				if (gameEvent.getTargetEntry().equals("update")) {
					ContractReward reward = contract.getRewards().get(0);
					
					if (reward.getKeyName() > 1) {
						synchronized (reward) {
							reward = DataAccess.getEditable(reward);
							reward.setKeyName(reward.getKeyName() - 1);
							reward.save();
						}
					} else {
						reward.delete();
					}
					
					synchronized (source.getLock()) {
						source = DataAccess.getEditable(source);
						source.addXp(getFleetXpReward(contract));
						source.save();
					}
					
					UpdateTools.queueContractsUpdate(player.getId(), false);
					UpdateTools.queuePlayerFleetUpdate(player.getId(), source.getId(), false);
					UpdateTools.queueAreaUpdate(player);
				} else if (gameEvent.getTargetEntry().equals(DialogEntry.END_OF_DIALOG)) {
					if (contract.getRewards().size() == 0)
						contract.delete();
					
					UpdateTools.queueContractsUpdate(player.getId(), false);
					UpdateTools.queueAreaUpdate(player);
				}
			}
		}
	}
	
	@Override
	public void finalize(Contract contract) {
		Player player = contract.getAttendees().get(0).getPlayer();
		List<StarSystem> systems = player.getSystems();
		LoggingSystem.getActionLogger().info("Mission BuildWards terminée pour : "+contract.getAttendees().get(0).getPlayer().getLogin());
		Player network = DataAccess.getPlayerById(getNpcId(contract));
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				Area area = system.getArea();
				Point tile;
				if (area.isFreeTile(system.getX() - 1, system.getY() - 3, Area.NO_FLEETS, null))
					tile = new Point(system.getX() - 1, system.getY() - 3);
				else
					tile = system.getFreeTile();
				
				NpcHelper.spawnFleet(network, area,
					contract.getId(), "reward", tile, Ship.HURRICANE,
					(int) contract.getDifficulty() + 1, true);
			}
		}
	}
	
	public int getNpcId(Contract contract) {
		return DataHelper.getContractIntParameter(contract.getId(), KEY_NPC_ID);
	}
	
	public Marker getMarkerByArea(Contract contract, Player player,
			ContractArea contractArea) {
		return DataAccess.getMarkerById(
			DataHelper.getPlayerIntMapParameter(contract.getId(),
				player.getId(), KEY_MARKERS, contractArea.getId()));
	}
	
	@Override
	public void createReward(Contract contract) {
		ContractHelper.addFleetXpReward(contract,
			getFleetXpReward(contract), contract.getAreas().size());
	}
	
	@Override
	public void createRelationships(Contract contract) {
		ContractHelper.addRelationship(contract, contract.getVariant(), 3);
	}
	
	public int getFleetXpReward(Contract contract) {
		return (int) (400 + 5 * contract.getDifficulty());
	}
	
	public boolean mayFleetGetReward(
			@Parameter(ParameterType.SOURCE_FLEET) Fleet fleet) {
		return fleet.getLevel() != 15;
	}
	
	public int getTotalBuiltWardsCount(Contract contract, Player player) {
		int total = 0;
		List<ContractArea> contractAreas = contract.getAreas();
		synchronized (contractAreas) {
			for (ContractArea contractArea : contractAreas)
				total += getBuiltWardsCount(contract, player, contractArea);
		}
		return total;
	}
	
	public int getTotalWardsCountToBuild(Contract contract) {
		return contract.getAreas().size() * getWardsCountToBuildByArea(contract);
	}
	
	public int getBuiltWardsCount(Contract contract, Player player,
			ContractArea contractArea) {
		return DataHelper.getPlayerNotNullIntMapParameter(contract.getId(),
			player.getId(), KEY_BUILT_WARDS_COUNT, contractArea.getId());
	}
	
	public int getWardsCountToBuildByArea(Contract contract) {
		return 1 + (int) contract.getDifficulty() / 5;
	}
	
	public int getRequiredWardPower(Contract contract) {
		return (int) contract.getDifficulty() + 5;
	}
	
	@Override
	public long getDifficulty(Player player) {
		return player.getLevel() - 1;
	}
	
	@Override
	public String getVariant(Player player) {
		return NpcHelper.FACTION_INDEPENDANT_NETWORK;
	}
	
	@Override
	public String getNpcAction(Contract contract, Player player, Fleet target) {
		return Fleet.NPC_ACTION_MISSION;
	}
	
	public String getMarkerText(Contract contract, Player player, ContractArea contractArea) {
		return "Construisez " + (getWardsCountToBuildByArea(contract) -
			getBuiltWardsCount(contract, player, contractArea)) +
			" balises d'observation d'une durée de " +
			getRequiredWardPower(contract) +
			" jours à 5 cases maximum de cet emplacement";
	}
	
	@Override
	public String getDetailedGoal(Contract contract, Player player) {
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
					int builtWards = getBuiltWardsCount(contract, player, contractArea);
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
			return "Récupérez la récompense sur l'un de vos systèmes";
		}
	}
	
	@Override
	public ContractState getState(Contract contract, Player player) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(getTotalBuiltWardsCount(contract, player));
			buffer.append("/");
			buffer.append(getTotalWardsCountToBuild(contract));
			buffer.append(" balises construites");
			
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
	public String getContractType() {
		return Contract.TYPE_SOLO;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
