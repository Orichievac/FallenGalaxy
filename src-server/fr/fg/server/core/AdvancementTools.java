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

import fr.fg.client.data.AdvancementData;
import fr.fg.client.data.AdvancementsData;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.util.JSONStringer;

public class AdvancementTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getPlayerAdvancements(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		List<Advancement> advancements = DataAccess.getAdvancementsByPlayer(player.getId());
		
		json.object().
			key(AdvancementsData.FIELD_ADVANCEMENTS).		array();
		
		synchronized (advancements) {
			for (Advancement advancement : advancements) {
				json.object().
					key(AdvancementData.FIELD_TYPE).	value(advancement.getType()).
					key(AdvancementData.FIELD_LEVEL).	value(advancement.getLevel()).
					endObject();
			}
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
