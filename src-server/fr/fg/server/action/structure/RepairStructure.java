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

public class RepairStructure extends Action {
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
		Structure repairedStructure = fleet.getStructureUnderFleet();
		
		if (repairedStructure == null)
			throw new IllegalOperationException("La flotte n'est pas " +
					"stationnée sur une structure.");
		
		// Vérifie que la flotte dipose de la compétence ingénieur
		if (fleet.getSkillLevel(Skill.SKILL_ENGINEER) == -1)
			throw new IllegalOperationException("La flotte ne dipose pas " +
					"de la compétence ingénieur.");
		
		// Vérifie que la structure n'est pas au max de ses points de vie
		if (repairedStructure.getHull() >= repairedStructure.getMaxHull())
			throw new IllegalOperationException("La structure n'est pas endommagée.");
		
		// Vérifie que la flotte peut attaquer la structure
		String treaty = repairedStructure.getOwner().getTreatyWithPlayer(player);
		
		if (treaty.equals(Treaty.ENEMY) || treaty.equals(Treaty.NEUTRAL))
			throw new IllegalOperationException("Vous ne pouvez " +
					"réparer que les structures amies.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_REPAIR_STRUCTURE, Utilities.now() +
				GameConstants.REPAIR_STRUCTURE_MOVEMENT_RELOAD);
			fleet.save();
		}
		
		UpdateTools.queueAreaUpdate(fleet.getIdArea(), new Point(fleetX, fleetY));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
