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
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class SwitchStealth extends Action {
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
		
		// Vérifie que la flotte a la compétence furtivité
		if (fleet.getSkillLevel(Skill.SKILL_ULTIMATE_STEALTH) < 0)
			throw new IllegalOperationException("La flotte n'a " +
					"pas la compétence furtivité.");
		
		// Vérifie que la compétence n'est pas en train de se recharger
		if (fleet.getSkillUltimateReload() > Utilities.now())
			throw new IllegalOperationException("La compétence est en cours de rechargement.");
		
		// Vérifie que la flotte a suffisament de mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir " + (fleet.isStealth() ?
					"sortir du" : "passer en") + " mode furtif.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			if (!fleet.isStealth()) {
				fleet.setSkillUltimateReload(Utilities.now() +
					GameConstants.ACTION_MOVEMENT_RELOAD);
				fleet.setSkillUltimateLastUse(Utilities.now());
			}
			fleet.setStealth(!fleet.isStealth());
			fleet.save();
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
				new Point(fleet.getCurrentX(), fleet.getCurrentY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
