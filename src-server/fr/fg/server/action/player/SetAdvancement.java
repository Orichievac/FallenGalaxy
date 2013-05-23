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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetAdvancement extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int type = (Integer) params.get("type");
		int advancementPoints = player.getAdvancementPoints();
		int cost = Advancement.getCost(type);
		
		if (cost > advancementPoints)
			throw new IllegalOperationException("Vous n'avez pas " +
				"suffisament de points de civilisation disponibles.");
		
		switch (type) {
		case Advancement.TYPE_POPULATION_GROWTH:
			List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
			
			for (StarSystem system : systems)
				StarSystem.updateSystem(system);
			break;
		case Advancement.TYPE_RESEARCH:
			player.updateResearch();
			break;
		}
		
		Advancement.setAdvancementLevel(player.getId(), type,
			Advancement.getAdvancementLevel(player.getId(), type) + 1);
		
		List<Update> updates = new ArrayList<Update>();
		updates.add(Update.getAdvancementsUpdate());
		
		switch (type) {
		case Advancement.TYPE_POPULATION_GROWTH:
		case Advancement.TYPE_BUILDING_LAND:
			updates.add(Update.getPlayerSystemsUpdate());
			break;
		case Advancement.TYPE_COLONIZATION_POINTS:
			updates.add(Update.getXpUpdate());
			break;
		case Advancement.TYPE_LINE_OF_SIGHT:
			updates.add(Update.getAreaUpdate());
			break;
		}
		
		return UpdateTools.formatUpdates(
			player,
			updates
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
