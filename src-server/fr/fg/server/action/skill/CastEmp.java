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

package fr.fg.server.action.skill;

import java.util.Map;

import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Treaty;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterEmpEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class CastEmp extends Action {
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
		
		if (fleet.getSkillLevel(Skill.SKILL_ULTIMATE_EMP) < 0)
			throw new IllegalOperationException(
					"La flotte n'a pas la compétence IEM.");
		
		// Vérifie que la compétence n'est pas en train de se recharger
		if (fleet.getSkillUltimateReload() > Utilities.now())
			throw new IllegalOperationException("La compétence est en cours de rechargement.");
		
		// Flotte ciblée pour créer le lien
		Fleet target = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("target"), 0);
		
		if (target.isStealth() && !player.isLocationRevealed(
				target.getCurrentX(), target.getCurrentY(), target.getArea()))
			throw new IllegalOperationException("Cible invalide.");
		
		String treaty = player.getTreatyWithPlayer(target.getOwner());
		
		if (!(treaty.equals(Treaty.ENEMY) || (treaty.equals(Treaty.NEUTRAL) &&
				(fleet.getSkillLevel(Skill.SKILL_PIRATE) != -1 ||
				target.getSkillLevel(Skill.SKILL_PIRATE) != -1))) ||
				!ContractManager.isNpcAvailable(player, target))
			throw new IllegalOperationException("Cible invalide.");
		
		// Vérifie que la cible est à portée
		int distance = fleet.getDistance(target);
		
		if (distance == -1 || distance > Skill.SKILL_ULTIMATE_EMP_RANGE
				[fleet.getSkillLevel(Skill.SKILL_ULTIMATE_EMP)])
			throw new IllegalOperationException("La cible est hors de portée.");
		
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir utiliser la compétence.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setSkillUltimateReload(Utilities.now() +
				Skill.SKILL_ULTIMATE_EMP_SOURCE_MOVEMENT_RELOAD);
			fleet.setSkillUltimateLastUse(Utilities.now());
			fleet.save();
		}
		
		synchronized (target.getLock()) {
			target = DataAccess.getEditable(target);
			target.setMovement(0, true);
			target.setMovementReload(Math.max(target.getMovementReload(),
				Utilities.now() +
				Skill.SKILL_ULTIMATE_EMP_TARGET_MOVEMENT_RELOAD
					[fleet.getSkillLevel(Skill.SKILL_ULTIMATE_EMP)]));
			target.save();
		}
		
		Event event = new Event(
			Event.EVENT_EMP,
			Event.TARGET_PLAYER,
			target.getIdOwner(),
			target.getIdCurrentArea(),
			fleet.getCurrentX(),
			fleet.getCurrentY(),
			target.getName(),
			fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? fleet.getName() : "???",
			fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? player.getLogin() : "??? (flotte pirate)");
		event.save();
		
		GameEventsDispatcher.fireGameEvent(new AfterEmpEvent(fleet, target));
		
		Effect effect = new Effect(Effect.TYPE_EMP,
			target.getCurrentX(), target.getCurrentY(),
			target.getIdCurrentArea());
		
		// Met à jour la liste des flottes de la cible
		UpdateTools.queueNewEventUpdate(target.getIdOwner(), false);
		UpdateTools.queuePlayerFleetUpdate(target.getIdOwner(), target.getId(), false);
		UpdateTools.queueEffectUpdate(effect, player.getId(), false);
		UpdateTools.queueAreaUpdate(target.getOwner());
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getEffectUpdate(effect)
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
