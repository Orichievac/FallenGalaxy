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

package fr.fg.server.action.structure;

import java.awt.Point;
import java.util.Map;

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
import fr.fg.server.data.Structure;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class AttackStructure extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(idFleet, player.getId());
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		// Récupère la structure sur laquelle la flotte est stationnée
		Structure attackedStructure = fleet.getStructureUnderFleet();
		
		if (attackedStructure == null)
			throw new IllegalOperationException("La flotte n'est pas " +
					"stationnée sur une structure.");
		
		// Vérifie que le niveaux de la flotte ne dépasse pas le niveau du quadrant
//		if (fleet.getPowerLevel() >
//			fleet.getArea().getSector().getLvlMax())
//				throw new IllegalOperationException("Votre flotte doit avoir une puissance" +
//						" inférieure à " + fleet.getArea().getSector().getLvlMax()+
//						" pour attaquer une structure dans ce quadrant");
		
//		List<Structure> forceFields = attackedStructure.getActivatedForceFieldsWithinRange();
//		if (forceFields.size() > 0)
//			attackedStructure = forceFields.get(0);
		
		// Vérifie que la flotte peut attaquer la structure
		String treaty = attackedStructure.getOwner().getTreatyWithPlayer(player);
		
		if (!(treaty.equals(Treaty.ENEMY) || (treaty.equals(Treaty.NEUTRAL) &&
				fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)))
			throw new IllegalOperationException("Vous n'êtes pas en guerre " +
				"contre le propriétaire de la structure.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_ATTACK_STRUCTURE, Utilities.now() +
				GameConstants.ATTACK_STRUCTURE_MOVEMENT_RELOAD);
			fleet.save();
		}
		
		Event event = new Event(
			Event.EVENT_STRUCTURE_ATTACKED,
			Event.TARGET_PLAYER,
			attackedStructure.getIdOwner(),
			attackedStructure.getIdArea(),
			attackedStructure.getX(),
			attackedStructure.getY(),
			attackedStructure.getName(),
			fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : fleet.getName(),
			fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "??? (flotte pirate)" : fleet.getOwner().getLogin());
		event.save();
		
		UpdateTools.queueNewEventUpdate(attackedStructure.getIdOwner());
		UpdateTools.queueAreaUpdate(fleet.getIdArea(), new Point(fleetX, fleetY));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
