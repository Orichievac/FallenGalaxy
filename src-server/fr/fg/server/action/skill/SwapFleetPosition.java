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

package fr.fg.server.action.skill;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Treaty;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterSwapEvent;
import fr.fg.server.events.impl.BeforeSwapEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class SwapFleetPosition extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui utilise la compétence
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		if (fleet.getSkillLevel(Skill.SKILL_ULTIMATE_SWAP) < 0)
			throw new IllegalOperationException(
					"La flotte n'a pas la compétence distorsion spatiale.");
		
		// Vérifie que la compétence n'est pas en train de se recharger
		if (fleet.getSkillUltimateReload() > Utilities.now())
			throw new IllegalOperationException("La compétence est en cours de rechargement.");
		
		// Flotte ciblée pour échanger de position
		Fleet target = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("target"), 0);
		
		if (target.isStealth() && !player.isLocationRevealed(
				target.getCurrentX(), target.getCurrentY(), target.getArea()) && !target.getOwner().isAlliedWithPlayer(player))
			throw new IllegalOperationException("Cible invalide.");
		
		if ((target.getSkillLevel(Skill.SKILL_PIRATE) == -1 &&
				target.getOwner().isAi() &&
				player.getTreatyWithPlayer(target.getOwner()).equals(Treaty.NEUTRAL)) ||
				!ContractManager.isNpcAvailable(player, target))
			throw new IllegalOperationException("Cible invalide.");
		
		if (target.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_MOUNT_STRUCTURE))
			throw new IllegalOperationException("La flotte est en train " +
				"d'opérer sur une structure et ne peut être déplacée.");
		
		if (target.getCurrentAction().equals(Fleet.CURRENT_ACTION_MIGRATE))
			throw new IllegalOperationException("La flotte est en train " +
			"de migrer un système. Impossible d'échanger les positions.");
	
		// Vérifie que la cible est à portée
		int distance = fleet.getDistance(target);
		
		if (distance == -1 || distance > Skill.SKILL_ULTIMATE_SWAP_RANGE
				[fleet.getSkillLevel(Skill.SKILL_ULTIMATE_SWAP)])
			throw new IllegalOperationException("La cible est hors de portée.");
		
		// Vérifie que les flottes ne sont pas sur des systèmes
		List<StarSystem> systems = fleet.getArea().getSystems();
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		int targetX = target.getCurrentX();
		int targetY = target.getCurrentY();
		
		int radius = GameConstants.SYSTEM_RADIUS + 2;
		synchronized (systems) {
			for (StarSystem system : systems) {
				int dx = fleetX - system.getX();
				int dy = fleetY - system.getY();
				
				if (dx * dx + dy * dy <= radius * radius)
					throw new IllegalOperationException("La compétence ne " +
						"peut pas être utilisée à proximité d'un système.");
				
				dx = targetX - system.getX();
				dy = targetY - system.getY();
				
				if (dx * dx + dy * dy <= radius * radius)
					throw new IllegalOperationException("La compétence ne " +
						"peut pas être utilisée à proximité d'un système.");
			}
		}
		
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir utiliser la compétence.");
		
		GameEventsDispatcher.fireGameEvent(new BeforeSwapEvent(fleet, target));
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setLocation(targetX, targetY);
			fleet.setSkillUltimateReload(Utilities.now() +
				Skill.SKILL_ULTIMATE_SWAP_MOVEMENT_RELOAD);
			fleet.setSkillUltimateLastUse(Utilities.now());
			fleet.save();
		}
		
		synchronized (target.getLock()) {
			target = DataAccess.getEditable(target);
			target.setLocation(fleetX, fleetY);
			target.save();
		}
		
		GameEventsDispatcher.fireGameEvent(new AfterSwapEvent(fleet, target));
		
		// Met à jour la liste des flottes du joueur
		if (target.getIdOwner() != player.getId()) {
			String treaty = player.getTreatyWithPlayer(target.getOwner());
			boolean ally = treaty.equals(Treaty.ALLY) ||
				treaty.equals(Treaty.ALLIED);
			
			Event event = new Event(
				Event.EVENT_SWAP,
				Event.TARGET_PLAYER,
				target.getIdOwner(),
				target.getIdCurrentArea(),
				fleetX,
				fleetY,
				target.getName(),
				fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 || ally ? fleet.getName() : "???",
				fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 || ally ? player.getLogin() : "??? (flotte pirate)");
			event.save();
			
			UpdateTools.queuePlayerFleetUpdate(target.getIdOwner(), target.getId(), false);
			UpdateTools.queueNewEventUpdate(target.getIdOwner());
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		ArrayList<Update> updates = new ArrayList<Update>();
		updates.add(Update.getAreaUpdate());
		updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		if (target.getIdOwner() == player.getId())
			updates.add(Update.getPlayerFleetUpdate(target.getId()));
		else
			UpdateTools.queuePlayerFleetUpdate(target.getIdOwner(), target.getId(), false);
		
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
				player.getId(),
				new Point(fleet.getX(), fleet.getY()),
				new Point(target.getX(), target.getY()));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
