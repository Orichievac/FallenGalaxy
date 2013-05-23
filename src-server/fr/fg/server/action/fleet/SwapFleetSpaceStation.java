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

package fr.fg.server.action.fleet;

import java.util.ArrayList;
import java.util.Map;

import fr.fg.server.core.ContainerTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterSpaceStationFund;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SwapFleetSpaceStation extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		int idStation = (Integer) params.get("station");
		
		// Récupère la flotte et la station spatiale
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());
		
		SpaceStation spaceStation = DataAccess.getSpaceStationById(idStation);
		
		if (spaceStation == null)
			throw new IllegalOperationException("La station spatiale n'existe pas.");
		
		if (player.getIdAlly() == 0 || spaceStation.getIdAlly() != player.getIdAlly())
			throw new IllegalOperationException("La station spatiale n'appartient pas à votre alliance.");
		
		if (spaceStation.getLevel() == 5)
			throw new IllegalOperationException("La station spatiale a " +
				"atteint le niveau maximal et ne peut plus être améliorée.");
		
		int dx = spaceStation.getX() - fleet.getCurrentX();
		int dy = spaceStation.getY() - fleet.getCurrentY();
		
		if (fleet.getIdCurrentArea() != spaceStation.getIdArea() ||
				dx * dx + dy * dy > GameConstants.SPACE_STATION_RADIUS *
				GameConstants.SPACE_STATION_RADIUS)
			throw new IllegalOperationException("La flotte n'est pas stationnée sur la station.");
		
		ItemContainer fleetContainerBefore = fleet.getItemContainer();
		ItemContainer spaceStationContainerBefore = new ItemContainer(
			ItemContainer.CONTAINER_SPACE_STATION, spaceStation.getId());
		spaceStationContainerBefore.setResources(spaceStation.getResources());
		
		// Nouvelles ressources
		ItemContainer fleetContainerAfter = new ItemContainer(
			ItemContainer.CONTAINER_FLEET, fleet.getId());
		
		for (int i = 0; i < fleetContainerBefore.getMaxItems(); i++) {
			int type = (Integer) params.get("item" + i + "_type");
			long id = (Long) params.get("item" + i + "_id");
			long count = (Long) params.get("item" + i + "_count");
			
			Item item = new Item(type, id, count);
			fleetContainerAfter.setItem(item, i);
		}
		
		ItemContainer spaceStationContainerAfter = ContainerTools.swap(
			fleetContainerBefore, fleetContainerAfter, fleet.getPayload(),
			spaceStationContainerBefore, spaceStationContainerBefore.getPayload());
		
		// Transfert les ressources sur la station
		boolean levelUp;
		long[] transferedResources = spaceStationContainerAfter.getResourcesAsLong();
		
		synchronized (spaceStation.getLock()) {
			spaceStation = DataAccess.getEditable(spaceStation);
			spaceStation.setResources(transferedResources);
			levelUp = spaceStation.tryLevelUp();
			if (levelUp) // Teste s'il est encore possible de l'améliorer
				while (spaceStation.tryLevelUp());
			spaceStation.save();
		}
		
		synchronized (fleetContainerBefore.getLock()) {
			fleetContainerBefore = DataAccess.getEditable(fleetContainerBefore);
			fleetContainerBefore.copy(fleetContainerAfter);
			fleetContainerBefore.save();
		}
		
		if (levelUp)
			fleet.getArea().getSector().updateInfluences();
		
		GameEventsDispatcher.fireGameEvent(new AfterSpaceStationFund(
				player, spaceStation, 0, transferedResources));
		
		// Met à jour l'affichage des membres de l'alliance connectés sur le
		// secteur
		if (levelUp) {
			ArrayList<Player> members = new ArrayList<Player>(player.getAlly().getMembers());
			
			for (int i = 0; i < members.size(); i++)
				if (members.get(i).getId() == player.getId()) {
					members.remove(i);
					break;
				}
			
			Event event = new Event(Event.EVENT_STATION_UPGRADED, Event.TARGET_ALLY,
				player.getIdAlly(), spaceStation.getIdArea(), spaceStation.getX(), spaceStation.getY(),
				spaceStation.getName(), String.valueOf(spaceStation.getLevel()));
			event.save();
			
			UpdateTools.queueNewEventUpdate(members, false);
			UpdateTools.queueAreaUpdate(members);
			
			return UpdateTools.formatUpdates(
				player,
				Update.getNewEventUpdate(),
				Update.getPlayerFleetUpdate(fleet.getId()),
				Update.getAreaUpdate()
			);
		} else {
			UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId());
			
			return UpdateTools.formatUpdates(
				player,
				Update.getPlayerFleetUpdate(fleet.getId()),
				Update.getAreaUpdate()
			);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
