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

package fr.fg.server.action.fleet;

import java.awt.Point;
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
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class Migration extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idFleet = (Integer) params.get("fleet");
		int idSystem = (Integer) params.get("system");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());
		
		StarSystem systemParam =DataAccess.getSystemById(idSystem);
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		// Vérifie que la flotte n'est pas une flotte pirate
		if (fleet.getSkillLevel(Skill.SKILL_PIRATE) != -1)
			throw new IllegalOperationException("Les flottes pirates ne peuvent pas " +
					"migrer de systèmes.");
		
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
					" pour le migrer.");
		
		// Vérifie que le système n'est pas un système AI
		if (targetSystem.isAi())
			throw new IllegalOperationException("Ce système n'est pas colonisable.");
		
		if (targetSystem.getIdOwner() != 0) {
			// Le système n'est pas libre
				throw new IllegalOperationException("Ce système n'est pas libre.");
		}
		
		
		// Vérifie qu'une flotte n'est pas déjà en train de migrer vers le même système
		List<Fleet> fleets = area.getFleets();
		
		synchronized (fleets) {
			for (Fleet areaFleet : fleets)
			{
				if (areaFleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_MIGRATE) &&
						areaFleet.getSystemOver().getId() == targetSystem.getId()) {
					
					if (areaFleet.getIdOwner() == player.getId())
						throw new IllegalOperationException("Une seule flotte à la " +
							"fois peut migrer sur un même système.");
					else
						throw new IllegalOperationException("La flotte " +
							areaFleet.getName() + " appartenant à " +
							areaFleet.getOwner().getLogin() +
							" est déjà en train de migrer" +
							" ce système.");
					
					
					
				}
			}
		}
		
		// Vérifie qu'une flotte n'est pas déjà en train de migrer vers le même système
		List<Fleet> fleetsPlayer = DataAccess.getFleetsByOwner(fleet.getIdOwner());
		
		synchronized (fleetsPlayer) {
			for (Fleet playerFleet : fleetsPlayer)
			{
				if (playerFleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_MIGRATE) &&
						playerFleet.getIdSystemMigrate()==idSystem) {
					
						throw new IllegalOperationException("Votre flotte " +
								playerFleet.getName() +
							" est déjà en train de migrer le système " +
							systemParam.getName());
				}
			}
		}
		
		long price = player.getMigrationCost();
		
		if (player.getCredits() < price)
			throw new IllegalOperationException(
					"Vous n'avez pas assez de crédits pour migrer. " +
					price + " crédits nécessaires.");
		
		
			// Durée de la migration
		long length = GameConstants.MIGRATION_LENGTH;

		
		long now = Utilities.now();
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_MIGRATE, now + length);
			fleet.setIdSystemMigrate(idSystem);
			fleet.save();
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-price);
			player.save();
		}
		
			// Evenement pour signaler le début de la migration
			DataAccess.save(new Event(
					Event.EVENT_MIGRATION_START,
					Event.TARGET_PLAYER,
					fleet.getIdOwner(),
					fleet.getIdCurrentArea(),
					fleet.getX(),
					fleet.getY(),
					fleet.getName(),
					systemParam.getName(),
					targetSystem.getName()));
			
			UpdateTools.queueNewEventUpdate(fleet.getIdOwner(), false);
		
		
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
				player.getId(), new Point(fleet.getX(),fleet.getY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getXpUpdate(),
			Update.getPlayerSystemsUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
