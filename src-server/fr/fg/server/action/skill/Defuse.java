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

import java.awt.Point;
import java.util.Map;

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
import fr.fg.server.data.Ward;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class Defuse extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idWard = (Integer) params.get("target");
		
		// Flotte qui désamorce la charge
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		if (fleet.getSkillLevel(Skill.SKILL_PYROTECHNIST) < 1)
			throw new IllegalOperationException("La flotte n'a pas " +
					"la compétence artificier au niveau suffisant.");
		
		// Vérifie que la charge existe
		Ward ward = DataAccess.getWardById(idWard);
		if (ward == null)
			throw new IllegalOperationException("Charge invalide.");
		
		// Vérifie que la charge est une chage
		if (!(ward.getType().equals(Ward.TYPE_MINE) ||
				 ward.getType().equals(Ward.TYPE_MINE_INVISIBLE) ||
				 ward.getType().equals(Ward.TYPE_STUN) ||
				 ward.getType().equals(Ward.TYPE_STUN_INVISIBLE)))
			throw new IllegalOperationException("Charge invalide.");
		
		// Vérifie que la charge est à portée et dans le même secteur
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		int dx = ward.getX() - fleetX;
		int dy = ward.getY() - fleetY;
		
		if (ward.getIdArea() != fleet.getIdCurrentArea() ||
				dx * dx + dy * dy > Ward.CHARGE_DEFUSE_RADIUS * Ward.CHARGE_DEFUSE_RADIUS)
			throw new IllegalOperationException("Charge invalide.");
		
		// Vérifie la diplomatie
		String treaty = ward.getOwner().getTreatyWithPlayer(player);
		
		if (!(treaty.equals(Treaty.ENEMY) ||
				(treaty.equals(Treaty.NEUTRAL) &&
				fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)))
			throw new IllegalOperationException("Vous n'êtes pas en guerre " +
				"contre le propriétaire de cette charge.");
		
		// Vérifie que la flotte a la puissance nécessaire
		if (ward.getPower() > fleet.getPowerLevel())
			throw new IllegalOperationException("La charge est trop " +
				"complexe pour être désamorcée par votre flotte.");
		
		// Détruit la charge
		ward.delete();
		
		synchronized (fleet) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_DEFUSE,
				Math.max(fleet.getMovementReload(),
				Utilities.now() + Skill.SKILL_DEFUSE_MOVEMENT_RELOAD));
			fleet.addXp(Skill.SKILL_DEFUSE_XP_BONUS);
			fleet.save();
		}
		
		Event event = new Event(
			Event.EVENT_CHARGE_DEFUSED,
			Event.TARGET_PLAYER,
			ward.getIdOwner(),
			ward.getIdArea(),
			ward.getX(),
			ward.getY(),
			fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? fleet.getName() : "???",
			fleet.getSkillLevel(Skill.SKILL_PIRATE) == -1 ? player.getLogin() : "??? (flotte pirate)",
			ward.getType());
		event.save();
		
		Effect effect = new Effect(Effect.TYPE_WARD_DESTRUCTION,
				ward.getX(), ward.getY(), ward.getIdArea());
		
		UpdateTools.queueNewEventUpdate(ward.getIdOwner(), false);
		UpdateTools.queueEffectUpdate(effect, player.getId(), false);
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
			new Point(fleetX, fleetY),
			new Point(ward.getX(), ward.getY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getEffectUpdate(effect)
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
