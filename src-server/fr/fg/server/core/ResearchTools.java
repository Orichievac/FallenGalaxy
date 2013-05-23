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

package fr.fg.server.core;

import java.util.List;


import fr.fg.client.data.ResearchData;
import fr.fg.client.data.ResearchedTechnologyData;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;
import fr.fg.server.util.JSONStringer;


public class ResearchTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getResearchData(JSONStringer json,
			Player player) throws Exception {
		if (json == null)
			json = new JSONStringer();
		
		json.object();
		
		json.key(ResearchData.FIELD_RESEARCH_POINTS).value(player.getResearchPoints());
		
		// Récupére toutes les recherches du joueur
		json.key(ResearchData.FIELD_RESEARCHED_TECHNOLOGIES).array();
		List<Research> researches = player.getResearches();
		
		synchronized (researches) {
			for (Research research : researches) {
				json.object().
					key(ResearchedTechnologyData.FIELD_ID).			value(research.getIdTechnology()).
					key(ResearchedTechnologyData.FIELD_PROGRESS).	value(research.getProgress()).
					endObject();
			}
		}
		json.endArray();
		json.key(ResearchData.FIELD_PENDING_TECHNOLOGIES).
			array();
		
		synchronized (researches) {
			for (int i = 0; i < 3; i++) {
				for (Research research : researches) {
					if (research.getQueuePosition() == i)
						json.value(research.getIdTechnology());
				}
			}
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
