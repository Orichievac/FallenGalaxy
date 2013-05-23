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
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class DestroySpaceStation extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui construit la balise
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		String password = (String) params.get("password");
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		// Cherche la station spatiale construite sur la case de la flotte
		List<SpaceStation> spaceStations = DataAccess.getSpaceStationsByArea(
				fleet.getIdCurrentArea());
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		synchronized (spaceStations) {
			for (SpaceStation spaceStation : spaceStations) {
				int dx = spaceStation.getX() - fleetX;
				int dy = spaceStation.getY() - fleetY;
				double radius = GameConstants.SPACE_STATION_RADIUS;
				
				if (dx * dx + dy * dy <= radius * radius) {
					String treaty = Treaty.NEUTRAL;
					if (player.getIdAlly() != 0)
						treaty = spaceStation.getAlly().getTreatyWithAlly(
								player.getIdAlly());
					
					if (treaty.equals(Treaty.ENEMY) ||
						(treaty.equals(Treaty.ALLY) && player.getAllyRank() >=
						player.getAlly().getRequiredRank(Ally.RIGHT_MANAGE_STATIONS)) ||
						(treaty.equals(Treaty.NEUTRAL) &&
						fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)) {
						
						boolean newEvent = false;
						
						List<Update> updates = new ArrayList<Update>();
						
						// Endommage ou détruit la station
						if (treaty.equals(Treaty.ALLY) ||
								spaceStation.getHull() <= fleet.getPowerLevel()) {
							if (treaty.equals(Treaty.ALLY) &&
									(password == null ||
									!player.getPassword().equals(Utilities.encryptPassword(password))))
								throw new IllegalOperationException("Mot de passe invalide.");
								
							
							spaceStation.delete();
							
							spaceStation.getArea().getSector().updateInfluences();

							newEvent = true;
							
							if (treaty.equals(Treaty.ALLY)) {
								Event event = new Event(
									Event.EVENT_STATION_SELF_DESTRUCT,
									Event.TARGET_ALLY,
									spaceStation.getIdAlly(),
									spaceStation.getIdArea(),
									spaceStation.getX(),
									spaceStation.getY(),
									spaceStation.getName(),
									String.valueOf(player.getLogin())
								);
								event.save();
							} else {
								Event event = new Event(
									Event.EVENT_STATION_LOST,
									Event.TARGET_ALLY,
									spaceStation.getIdAlly(),
									spaceStation.getIdArea(),
									spaceStation.getX(),
									spaceStation.getY(),
									spaceStation.getName()
								);
								event.save();
							}
							
							Effect effect = new Effect(Effect.TYPE_STATION_DESTRUCTION,
								fleet.getCurrentX(), fleet.getCurrentY(),
								fleet.getIdCurrentArea());
							
							UpdateTools.queueEffectUpdate(effect, player.getId(), false);
							
							updates.add(Update.getEffectUpdate(effect));
						} else {
							double coefBefore = spaceStation.getHull() /
								(double) SpaceStation.HULL_LEVELS[spaceStation.getLevel()];
							
							synchronized (spaceStation.getLock()) {
								spaceStation = DataAccess.getEditable(spaceStation);
								spaceStation.setHull(spaceStation.getHull() - fleet.getPowerLevel());
								spaceStation.save();
							}
							
							double coefAfter = spaceStation.getHull() /
								(double) SpaceStation.HULL_LEVELS[spaceStation.getLevel()];
							
							if (coefBefore != 1 && (int) (coefBefore * 5) != (int) (coefAfter * 5)) {
								newEvent = true;
								
								Event event = new Event(
									Event.EVENT_STATION_UNDER_ATTACK,
									Event.TARGET_ALLY,
									spaceStation.getIdAlly(),
									spaceStation.getIdArea(),
									spaceStation.getX(),
									spaceStation.getY(),
									spaceStation.getName(),
									String.valueOf(coefAfter));
								event.save();
							}
						}
						
						synchronized (fleet.getLock()) {
							fleet = DataAccess.getEditable(fleet);
							fleet.doAction(Fleet.CURRENT_ACTION_ATTACK_STRUCTURE, Utilities.now() +
								GameConstants.DESTROY_SPACE_STATION_MOVEMENT_RELOAD);
							fleet.save();
						}
						
						// Mises à jour
						if (newEvent)
							UpdateTools.queueNewEventUpdate(
								spaceStation.getAlly().getMembers(), false);
						UpdateTools.queueAreaUpdate(
							fleet.getIdCurrentArea(), player.getId());
						
						updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
						updates.add(Update.getAreaUpdate());
						
						return UpdateTools.formatUpdates(player, updates);
					} else {
						throw new IllegalOperationException(
							"Vous ne pouvez pas saboter cette station spatiale.");
					}
				}
			}
		}
		
		throw new IllegalOperationException("Pas de station spatiale à cet endroit.");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
