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

import java.util.List;
import java.util.Map;

import fr.fg.client.data.ProbeReportData;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class ProbeSystem extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(idFleet, player.getId());
		
		int skillIndex = -1;
		for (int i = 0; i < 3; i++)
			if (fleet.getBasicSkill(i).getType() == Skill.SKILL_SPY) {
				skillIndex = i;
				break;
			}
		
		if (skillIndex == -1)
			throw new IllegalOperationException("La flotte n'a " +
				"pas la compétence renseignement.");
		
		// Vérifie que la compétence n'est pas en train de se recharger
		if (fleet.getBasicSkillReload(skillIndex) > Utilities.now())
			throw new IllegalOperationException("La compétence est en cours de rechargement.");
		
		List<StarSystem> systems = fleet.getArea().getSystems();
		StarSystem probedSystem = null;
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				if (system.contains(fleet.getCurrentX(), fleet.getCurrentY())) {
					if (system.getIdOwner() != 0 || system.isAi())
						throw new IllegalOperationException("Vous ne pouvez pas " +
								"récupérer d'informations sur ce système.");
					
					probedSystem = system;
					break;
				}
			}
		}
		
		if (probedSystem == null)
			throw new IllegalOperationException("Vous devez vous approcher d'un " +
					"système inoccupé pour obtenir des informations dessus.");
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setBasicSkillReload(Utilities.now() +
				GameConstants.ACTION_MOVEMENT_RELOAD, skillIndex);
			fleet.setBasicSkillLastUse(Utilities.now(), skillIndex);
			fleet.addXp(Skill.SKILL_PROBE_XP_BONUS);
			fleet.save();
		}
		
		UpdateTools.queuePlayerFleetUpdate(player.getId(), fleet.getId(), false);
		UpdateTools.queueAreaUpdate(player);
		
		JSONStringer json = new JSONStringer();
		
		json.object().
			key(ProbeReportData.FIELD_SYSTEM_NAME).		value(probedSystem.getName()).
			key(ProbeReportData.FIELD_AVAILABLE_SPACE).	value(probedSystem.getAvailableSpace()).
			key(ProbeReportData.FIELD_RESOURCES).			array();
		int[] availableResources = probedSystem.getAvailableResources();
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			json.value(availableResources[i]);
		json.endArray().
			endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
