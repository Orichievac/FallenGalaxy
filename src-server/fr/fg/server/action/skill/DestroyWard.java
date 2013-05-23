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
import java.util.List;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Treaty;
import fr.fg.server.data.Ward;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class DestroyWard extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui détruit la balise
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement.");
		
		// Cherche la structure construite sur la case de la flotte
		List<Ward> wards = DataAccess.getWardsByArea(fleet.getIdCurrentArea());
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		synchronized (wards) {
			for (Ward ward : wards) {
				if (ward.getX() == fleetX && ward.getY() == fleetY) {
					String treaty = ward.getOwner().getTreatyWithPlayer(player);
					
					if (treaty.equals(Treaty.PLAYER) ||
							((ward.getType().equals(Ward.TYPE_OBSERVER) ||
							ward.getType().equals(Ward.TYPE_OBSERVER_INVISIBLE) ||
							ward.getType().equals(Ward.TYPE_SENTRY) ||
							ward.getType().equals(Ward.TYPE_SENTRY_INVISIBLE)) &&
							(treaty.equals(Treaty.ENEMY) ||
							(treaty.equals(Treaty.NEUTRAL) &&
							fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)))) {
						if (!treaty.equals(Treaty.PLAYER) &&
								ward.getType().contains("invisible") &&
								!player.isLocationRevealed(ward.getX(),
									ward.getY(), ward.getArea()))
							throw new IllegalOperationException("Pas de balise à cet endroit.");
						
						ward.delete();
						
						synchronized (fleet.getLock()) {
							fleet = DataAccess.getEditable(fleet);
							fleet.doAction(Fleet.CURRENT_ACTION_ATTACK_WARD,
								Math.max(fleet.getMovementReload(),
								Utilities.now() + GameConstants.ACTION_MOVEMENT_RELOAD));
							fleet.save();
						}
						
						Effect effect = new Effect(Effect.TYPE_WARD_DESTRUCTION,
							fleet.getCurrentX(), fleet.getCurrentY(),
							fleet.getIdCurrentArea());
						
						UpdateTools.queueEffectUpdate(effect, player.getId(), false);
						
						// Met à jour le secteur
						UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
								player.getId(), new Point(fleetX, fleetY));
						
						return UpdateTools.formatUpdates(
							player,
							Update.getPlayerFleetUpdate(fleet.getId()),
							Update.getAreaUpdate(),
							Update.getEffectUpdate(effect)
						);
					} else {
						throw new IllegalOperationException(
							"Vous ne pouvez pas détruire cette balise.");
					}
				}
			}
		}
		
		throw new IllegalOperationException("Pas de balise à cet endroit.");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
