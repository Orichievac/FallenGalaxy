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

package fr.fg.server.action.player;

import java.util.List;
import java.util.Map;


import fr.fg.server.core.TreatyTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class SetDiplomacyState extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		boolean active = (Boolean) params.get("active");
		
		if (player.isDiplomacyActivated()) {
			if (active) {
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setSwitchDiplomacyDate(0);
					player.save();
				}
			} else {
				List<Fleet> fleets = player.getFleets();
				
				synchronized (fleets) {
					for (Fleet fleet : fleets)
						if (fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0)
							throw new IllegalOperationException("Vous ne " +
								"pouvez pas désactiver la diplomatie tant " +
								"que vous disposez de flottes pirates.");
				}
				
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setSwitchDiplomacyDate(Utilities.now() +
						GameConstants.DIPLOMACY_SWITCH_LENGTH - 10);
					player.save();
				}
			}
		} else {
			if (active) {
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setSwitchDiplomacyDate(Utilities.now() +
						GameConstants.DIPLOMACY_SWITCH_LENGTH - 10);
					player.save();
				}
			} else {
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setSwitchDiplomacyDate(0);
					player.save();
				}
			}
		}
		
		return TreatyTools.getDiplomacyState(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
