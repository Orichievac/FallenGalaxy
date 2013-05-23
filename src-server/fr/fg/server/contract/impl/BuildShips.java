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

package fr.fg.server.contract.impl;

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
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.DialogUpdateEvent;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.LoggingSystem;

public class BuildShips extends PlayerContractModel implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		NPC_RECIPIENT_WAITING = "recipientWaiting",
		NPC_RECIPIENT_DELIVERED = "recipientDelivered",
		NPC_RECIPIENT_REWARD = "recipientReward";
	
	private final static String
		KEY_NPC_RECIPIENT_FLEET = "recipientFleet",
		KEY_DELIVERED_SHIPS_COUNT = "delivered",
		KEY_SHIP_ID_TO_DELIVER = "shipId",
		KEY_SHIP_COUNT_TO_DELIVER_BY_NPC = "shipCount";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static final PlayerRequirements REQUIREMENTS =
		PlayerRequirementsFactory.getPointsRequirements(5000);
	
	static int[] forbiddenAreas = {22,23,24,25,26};
	
	
	private static final PlayerLocationConstraints LOCATION_CONSTRAINTS =
		PlayerLocationConstraintsFactory.getPointsLocationConstraints(15000, 3, true, new int[0], forbiddenAreas);

	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BuildShips() throws Exception {
		super(8, REQUIREMENTS, LOCATION_CONSTRAINTS);
		
		GameEventsDispatcher.addGameEventListener(
			this, DialogUpdateEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void initialize(Contract contract) {
		// Génère et sauvegarde le type de vaisseau à construire
		int shipIdToDeliver;
		int[] availableShips = ContractHelper.getEstimatedAvailableShips(
			contract.getDifficulty());
		
		do {
			shipIdToDeliver = availableShips[(int) (
				Math.random() * availableShips.length)];
		} while (Ship.SHIPS[shipIdToDeliver].getShipClass() == Ship.FREIGHTER);
		
		DataHelper.storeContractParameter(contract.getId(),
			KEY_SHIP_ID_TO_DELIVER, shipIdToDeliver);
		
		// Génère et sauvegarde le nombre de vaisseaux à construire
		long shipCountToDeliver = (long) Math.ceil(3600 * 6 *
			ContractHelper.getEstimatedProduction(contract.getDifficulty()) /
			(Ship.SHIPS[shipIdToDeliver].getPower() * ContractHelper.POWER_COST));
		long shipCountToDeliverByNpc = (long) Math.ceil(shipCountToDeliver /
			(double) contract.getAreas().size());
		
		DataHelper.storeContractParameter(contract.getId(),
			KEY_SHIP_COUNT_TO_DELIVER_BY_NPC, shipCountToDeliverByNpc);
	}
	
	public void launch(Contract contract) {
		List<ContractArea> areas = contract.getAreas();
		
		if (areas.size() == 0)
			throw new IllegalArgumentException("No area defined.");
		
		// Génère les flottes à qui il faut donner les vaisseaux
		Player npc;
		if (contract.getVariant().equals(NpcHelper.FACTION_GDO))
			npc = NpcHelper.createAICharacter(contract.getId(),
				NpcHelper.FACTION_GDO, true, NpcHelper.AVATAR_GDO_OFFICER);
		else
			npc = NpcHelper.createAICharacter(contract.getId(),
				NpcHelper.FACTION_BROTHERHOOD, true, NpcHelper.AVATAR_BROTHERHOOD_PARIA);
		
		for (ContractArea area : areas) {
			Fleet recipient = NpcHelper.spawnFleet(npc,
				DataAccess.getAreaById(area.getIdArea()),
				contract.getId(), NPC_RECIPIENT_WAITING,
				Ship.MAMMOTH, 120, true);
			DataHelper.storeContractParameter(contract.getId(),
				KEY_NPC_RECIPIENT_FLEET, area.getId(), recipient.getId());
		}
	}
	
	@Override
	public void finalize(Contract contract) {
		List<ContractArea> contractAreas = contract.getAreas();
		LoggingSystem.getActionLogger().info("Mission BuildShips terminée pour : "+contract.getAttendees().get(0).getPlayer().getLogin());
		synchronized (contractAreas) {
			for (ContractArea contractArea : contractAreas) {
				Fleet recipient = getRecipientFleetByArea(contract, contractArea);
				
				synchronized (recipient.getLock()) {
					recipient = DataAccess.getEditable(recipient);
					recipient.setNpcType(NPC_RECIPIENT_REWARD);
					recipient.save();
				}
			}
		}
	}
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof DialogUpdateEvent) {
			DialogUpdateEvent gameEvent = (DialogUpdateEvent) event;
			
			Contract contract = gameEvent.getContract();
			Player player = gameEvent.getPlayer();
			Fleet source = gameEvent.getSourceFleet();
			Fleet target = gameEvent.getTargetFleet();
			
			if (contract != null && contract.getType().equals(getType())) {
				if (target.getNpcType().equals(NPC_RECIPIENT_WAITING)) {
					if (gameEvent.getTargetEntry().equals("transfer")) {
						// Transfère les vaisseaux sur la flotte PNJ
						deliverShips(contract, source, target);
						
						// Teste si la flotte est pleine
						if (isNpcDelivered(contract, player, target)) {
							synchronized (target.getLock()) {
								target = DataAccess.getEditable(target);
								target.setNpcType(NPC_RECIPIENT_DELIVERED);
								target.save();
							}
						}
						
						// Teste si la mission est réussie
						if (getTotalDeliveredShipsCount(contract, player) >=
								getTotalShipsCountToDeliver(contract)) {
							setSuccess(contract, player);
						}
						
						UpdateTools.queueContractsUpdate(player.getId(), false);
						UpdateTools.queuePlayerFleetUpdate(player.getId(), source.getId(), false);
						UpdateTools.queueAreaUpdate(player);
					}
				} else if (target.getNpcType().equals(NPC_RECIPIENT_REWARD)) {
					// Fin de la mission
					if (gameEvent.getTargetEntry().equals(DialogEntry.END_OF_DIALOG)) {
						synchronized (player.getLock()) {
							// FIXME à améliorer
							player = DataAccess.getEditable(player);
							player.addXp(contract.getRewards().get(0).getValue());
							player.save();
						}
						
						contract.delete();
						
						UpdateTools.queueXpUpdate(player.getId(), false);
						UpdateTools.queueContractsUpdate(player.getId(), false);
						UpdateTools.queueAreaUpdate(player);
					}
				}
			}
		}
	}
	
	public long getDifficulty(Player player) {
		return player.getPoints();
	}
	
	public void createReward(Contract contract) {
		// Mission de coefficient de difficulté 60% et de 6h de durée
		ContractHelper.addXpReward(contract,
			(int) Math.ceil(getTotalShipsCountToDeliver(contract) *
				Ship.SHIPS[getShipIdToDeliver(contract)].getPower() *
				GameConstants.XP_SHIP_DESTROYED * .6));
	}
	

	
	@Override
	public void createRelationships(Contract contract) {
		ContractHelper.addRelationship(contract, contract.getVariant(), 5);
		if (contract.getVariant().equals(NpcHelper.FACTION_GDO))
			ContractHelper.addRelationship(contract, NpcHelper.FACTION_BROTHERHOOD, -4);
		else
			ContractHelper.addRelationship(contract, NpcHelper.FACTION_GDO, -4);
	}
	
	@Override
	public String getNpcAction(Contract contract, Player player, Fleet target) {
		if (target.getNpcType().equals(NPC_RECIPIENT_DELIVERED))
			return Fleet.NPC_ACTION_TALK;
		else
			return Fleet.NPC_ACTION_MISSION;
	}
	
	public String getVariant(Player player) {
		return NpcHelper.getRandomFaction(
			NpcHelper.FACTION_GDO,
			NpcHelper.FACTION_BROTHERHOOD
		);
	}
	
	public int getShipIdToDeliver(Contract contract) {
		return DataHelper.getContractIntParameter(
			contract.getId(), KEY_SHIP_ID_TO_DELIVER);
	}
	
	public String getShipNameToDeliver(Contract contract) {
		return Messages.getString("ships" + getShipIdToDeliver(contract));
	}
	
	public int getTotalShipsCountToDeliver(Contract contract) {
		return getShipsCountToDeliverByNpc(contract) * contract.getAreas().size();
	}
	
	public int getTotalDeliveredShipsCount(Contract contract, Player player) {
		int total = 0;
		List<ContractArea> contractAreas = contract.getAreas();
		synchronized (contractAreas) {
			for (ContractArea contractArea : contractAreas)
				total += getDeliveredShipsCount(contract, player,
					getRecipientFleetByArea(contract, contractArea));
		}
		return total;
	}
	
	public int getShipsCountToDeliverByNpc(Contract contract) {
		return DataHelper.getContractIntParameter(
			contract.getId(), KEY_SHIP_COUNT_TO_DELIVER_BY_NPC);
	}
	
	public int getDeliveredShipsCount(Contract contract, Player player, Fleet recipient) {
		return DataHelper.getPlayerNotNullIntMapParameter(contract.getId(),
			player.getId(), KEY_DELIVERED_SHIPS_COUNT, recipient.getId());
	}
	
	public Fleet getRecipientFleetByArea(Contract contract, ContractArea area) {
		return DataAccess.getFleetById(DataHelper.getContractIntMapParameter(
			contract.getId(), KEY_NPC_RECIPIENT_FLEET, area.getId()));
	}
	
	public int getShipsCountFleetMayDeliver(Contract contract,
			@Parameter(ParameterType.SOURCE_FLEET) Fleet fleet,
			@Parameter(ParameterType.TARGET_FLEET) Fleet recipient) {
		int shipToDeliver = getShipIdToDeliver(contract);
		int slotsCount = 0;
		for (Slot slot : fleet.getSlots())
			if (slot.getId() != 0)
				slotsCount++;
		for (Slot slot : fleet.getSlots())
			if (slot.getId() == shipToDeliver) {
				return (int) Math.min(slot.getCount() + (slotsCount == 1 ? -1 : 0),
					getShipsCountToDeliverByNpc(contract) -
					getDeliveredShipsCount(contract, fleet.getOwner(), recipient));
			}
		return 0;
	}
	
	public boolean mayFleetDeliverShips(Contract contract,
			@Parameter(ParameterType.SOURCE_FLEET) Fleet fleet,
			@Parameter(ParameterType.SOURCE_FLEET) Fleet recipient) {
		return getShipsCountFleetMayDeliver(contract, fleet, recipient) > 0;
	}
	
	public void deliverShips(Contract contract, Fleet fleet, Fleet recipient) {
		int shipId = getShipIdToDeliver(contract);
		int shipsCount = getShipsCountFleetMayDeliver(contract, fleet, recipient);
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
				Slot slot = fleet.getSlot(i);
				if (slot.getId() == shipId) {
					slot.addCount(-shipsCount);
					fleet.setSlot(slot, i);
					break;
				}
			}
			fleet.save();
		}
		
		DataHelper.storePlayerParameter(contract.getId(),
			fleet.getIdOwner(), KEY_DELIVERED_SHIPS_COUNT, recipient.getId(),
			getDeliveredShipsCount(contract, fleet.getOwner(), recipient) + shipsCount);
	}
	
	public boolean isNpcDelivered(Contract contract, Player player, Fleet recipient) {
		return getDeliveredShipsCount(contract, player, recipient) ==
			getShipsCountToDeliverByNpc(contract);
	}
	
	@Override
	public String getDetailedGoal(Contract contract, Player player) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();
			List<ContractArea> contractAreas =
				DataAccess.getAreasByContract(contract.getId());
			
			// Aggrège les données par secteur
			ArrayList<Area> areas = new ArrayList<Area>();
			HashMap<Integer, Integer> deliveredShipsByArea = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> shipsToDeliverByArea = new HashMap<Integer, Integer>();
			
			synchronized (contractAreas) {
				for (ContractArea contractArea : contractAreas) {
					Fleet recipient = getRecipientFleetByArea(contract, contractArea);
					int deliveredShips = getDeliveredShipsCount(contract, player, recipient);
					int shipsToDeliver = getShipsCountToDeliverByNpc(contract);
					int idArea = contractArea.getIdArea();
					
					if (!deliveredShipsByArea.containsKey(idArea)) {
						deliveredShipsByArea.put(idArea, deliveredShips);
						shipsToDeliverByArea.put(idArea, shipsToDeliver);
						areas.add(contractArea.getArea());
					} else {
						deliveredShipsByArea.put(idArea, deliveredShipsByArea.get(idArea) + deliveredShips);
						shipsToDeliverByArea.put(idArea, shipsToDeliverByArea.get(idArea) + shipsToDeliver);
					}
				}
			}
			
			Collections.sort(areas, new Comparator<Area>() {
				public int compare(Area a1, Area a2) {
					return a1.getName().compareToIgnoreCase(a2.getName());
				}
			});
			
			buffer.append("Construisez et livrez des ");
			buffer.append(getShipNameToDeliver(contract));
			buffer.append(" dans les secteurs suivants :<br/>");
			
			boolean first = true;
			for (Area area : areas) {
				if (first)
					first = false;
				else
					buffer.append("<br/>");
				
				buffer.append("Secteur ");
				buffer.append(area.getName());
				buffer.append(" : ");
				buffer.append(deliveredShipsByArea.get(area.getId()));
				buffer.append("/");
				buffer.append(shipsToDeliverByArea.get(area.getId()));
			}
			
			return buffer.toString();
		} else {
			return "Récupérez la récompense dans l'un des secteurs suivants : " +
				ContractHelper.formatContractAreas(contract);
		}
	}
	
	@Override
	public ContractState getState(Contract contract, Player player) {
		if (contract.getState().equals(Contract.STATE_RUNNING)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(getTotalDeliveredShipsCount(contract, player));
			buffer.append("/");
			buffer.append(getTotalShipsCountToDeliver(contract));
			buffer.append(" ");
			buffer.append(getShipNameToDeliver(contract));
			buffer.append(" livrés");
			
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
