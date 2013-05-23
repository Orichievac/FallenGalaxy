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
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class ColonizeOrCapture extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idFleet = (Integer) params.get("fleet");
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		// Vérifie que la flotte n'est pas une flotte pirate
		if (fleet.getSkillLevel(Skill.SKILL_PIRATE) != -1)
			throw new IllegalOperationException("Les flottes pirates ne peuvent pas " +
					"coloniser ou capturer de systèmes.");
		
		// Récupère le système sur lequel la flotte est stationnée, s'il existe
		Area area = fleet.getArea();
		
		List<StarSystem> systems = area.getSystems();
		StarSystem targetSystem = null;
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				if (system.contains(fleet.getCurrentX(), fleet.getCurrentY())) {
					targetSystem = system;
					break;
				}
			}
		}
		
		// Vérifie que la flotte est sur un système
		if (targetSystem == null)
			throw new IllegalOperationException("Approchez votre flotte d'un système " +
					" pour le coloniser.");
		
		// Vérifie que le système n'est pas un système AI
		if (targetSystem.isAi())
			throw new IllegalOperationException("Ce système n'est pas colonisable.");
		
		if (targetSystem.getIdOwner() == 0) {
			// Vérifie que le joueur a suffisament de points de colonisation
			// disponibles
			int points = player.getColonizationPoints();
			
			if (points < GameConstants.SYSTEM_COST)
				throw new IllegalOperationException("Il faut au moins " +
						GameConstants.SYSTEM_COST + " points de colonisation" +
						" pour pouvoir coloniser.");
			
			// Vérifie que le secteur n'est pas sous domination
			// TODO a remettre
//			int idDominatingAlly = area.getIdDominatingAlly();
//			if (idDominatingAlly != 0 && idDominatingAlly != player.getIdAlly())
//				throw new IllegalOperationException("Ce secteur est sous la domination " +
//						"d'une alliance autre que la votre. Il est impossible " +
//						"d'y établir une colonie.");
		} else {
			// Vérifie que le système n'appartient pas au joueur
			if (targetSystem.getIdOwner() == player.getId())
				throw new IllegalOperationException("Ce système vous appartient.");
			
			// Vérifie que le secteur est un secteur de la bordure
			if (area.getSector().getType() != 0) // TODO jgottero gestion bombardement
				throw new IllegalOperationException("La capture de systèmes n'est " +
					"pas possible dans les quadrants de départ.");
			
			Player owner = targetSystem.getOwner();
			
			// Vérifie que le joueur est en guerre contre le
			// propriétaire du système
			if (!player.getTreatyWithPlayer(owner).equals(Treaty.ENEMY))
				throw new IllegalOperationException("Vous devez être en guerre " +
						"contre le propriétaire du système pour pouvoir lui " +
						"capturer son système.");
		}
		
		// Vérifie qu'une flotte n'est pas déjà en train de coloniser ou de
		// capturer le système
		List<Fleet> fleets = area.getFleets();
		
		synchronized (fleets) {
			for (Fleet areaFleet : fleets)
				if (areaFleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_COLONIZE) &&
						areaFleet.getSystemOver().getId() == targetSystem.getId()) {
					String action = targetSystem.getIdOwner() == 0 ?
							"coloniser" : "capturer";
					
					if (areaFleet.getIdOwner() == player.getId())
						throw new IllegalOperationException("Une seule flotte à la " +
							"fois peut " + action + " un système.");
					else
						throw new IllegalOperationException("La flotte " +
							areaFleet.getName() + " appartenant à " +
							areaFleet.getOwner().getLogin() +
							" est déjà en train de " + action +
							" ce système.");
				}
		}
		
		long length;
		
		if (targetSystem.getIdOwner() == 0) {
			// Durée de la colonisation
			length = GameConstants.COLONIZATION_LENGTH;
		} else {
			// Durée de la capture
			int idAlly = area.getIdDominatingAlly();
			
			length = idAlly != 0 &&
				idAlly == targetSystem.getOwner().getIdAlly() ?
					GameConstants.DOMINATED_CAPTURE_LENGTH :
					GameConstants.CAPTURE_LENGTH;
		}
		
		long now = Utilities.now();
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_COLONIZE, now + length);
			fleet.save();
		}
		
		if (targetSystem.getIdOwner() != 0) {
			// Evenement pour signaler le début de la capture du système
			DataAccess.save(new Event(
					Event.EVENT_START_CAPTURE,
					Event.TARGET_PLAYER,
					targetSystem.getIdOwner(),
					fleet.getIdCurrentArea(),
					fleet.getX(),
					fleet.getY(),
					fleet.getName(),
					player.getLogin(),
					targetSystem.getName()));
			
			UpdateTools.queueNewEventUpdate(targetSystem.getIdOwner(), false);
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getXpUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
