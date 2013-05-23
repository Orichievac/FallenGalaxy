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

package fr.fg.server.action.station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class BuildSpaceStation extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui construit la station
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		// Vérifie que la flotte a la compétence ingénieur
		if (fleet.getSkillLevel(Skill.SKILL_ENGINEER) == -1)
			throw new IllegalOperationException("La flotte n'a pas " +
					"la compétence ingénieur.");
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir construire.");
		
		// Vérifie que le joueur a une alliance
		if (player.getIdAlly() == 0)
			throw new IllegalOperationException("Vous devez avoir une " +
				"alliance pour pouvoir construire une station spatiale.");
		
		// Vérifie que la flotte a suffisament de ressources
		ItemContainer itemContainer = fleet.getItemContainer();
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			if (itemContainer.getResource(i) < SpaceStation.COST_LEVELS[0][i])
				throw new IllegalOperationException("La flotte n'a pas " +
					"suffisament de ressources pour pouvoir construire la " +
					"station spatiale.");
		
		player = Player.updateCredits(player);
		
		if (player.getCredits() < SpaceStation.COST_LEVELS[0][4])
			throw new IllegalOperationException("Vous n'avez pas " +
				"suffisament de crédits pour pouvoir construire la " +
				"station spatiale.");
		
		// Vérifie que le maximum de stations spatiales n'a pas déjà été
		// atteint
		Area area = fleet.getArea();
		
		if (area.getSpaceStations().size() >= area.getSpaceStationsLimit())
			throw new IllegalOperationException("La limite de stations " +
				"spatiales dans le secteur a été atteinte. Détruisez " +
				"d'autres stations pour pouvoir en construire une nouvelle.");
		
		// Vérifie que l'emplacement est libre
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		int radius = (int) Math.ceil(GameConstants.SPACE_STATION_RADIUS);
		
		if (!area.areFreeTiles(
				fleetX - radius - 1,
				fleetY - radius - 1,
				2 * radius + 3,
				2 * radius + 3,
				Area.CHECK_OBJECT_SPAWN, null))
			throw new IllegalOperationException("Il ne doit pas y avoir " +
				"d'objets à proximité de la flotte (astéroïdes, système...).");
		
		List<SpaceStation> spaceStations = area.getSpaceStations();
		
		synchronized (spaceStations) {
			for (SpaceStation spaceStation : spaceStations) {
				int dx = spaceStation.getX() - fleetX;
				int dy = spaceStation.getY() - fleetY;
				
				if (dx * dx + dy * dy <= 4 *
						GameConstants.SPACE_STATION_RADIUS *
						GameConstants.SPACE_STATION_RADIUS + 25) {
					throw new IllegalOperationException("La station " +
						"spatiale ne peut être construite trop près " +
						"d'une autre station.");
				}
			}
		}
		
		// Supprime les ressources de la flotte
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_BUILD_STRUCTURE, Utilities.now() +
					Skill.SKILL_ENGINEER_MOVEMENT_RELOAD);
			fleet.addXp(SpaceStation.XP_REWARD);
			fleet.save();
		}
		
		synchronized (itemContainer.getLock()) {
			itemContainer = DataAccess.getEditable(itemContainer);
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				itemContainer.addResource(-SpaceStation.COST_LEVELS[0][i], i);
			itemContainer.save();
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-SpaceStation.COST_LEVELS[0][4]);
			player.save();
		}
		
		// Construit la station spatiale
		SpaceStation spaceStation = new SpaceStation("Station spatiale",
				fleetX, fleetY, player.getIdAlly(), fleet.getIdCurrentArea());
		spaceStation.save();
		
		ArrayList<Player> members = new ArrayList<Player>(player.getAlly().getMembers());
		
		for (int i = 0; i < members.size(); i++)
			if (members.get(i).getId() == player.getId()) {
				members.remove(i);
				break;
			}
		
		Event event = new Event(Event.EVENT_NEW_STATION, Event.TARGET_ALLY,
			player.getIdAlly(), spaceStation.getIdArea(), spaceStation.getX(), spaceStation.getY(),
			area.getName());
		event.save();
		
		// Met à jour le secteur
		UpdateTools.queueNewEventUpdate(members, false);
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId());
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
