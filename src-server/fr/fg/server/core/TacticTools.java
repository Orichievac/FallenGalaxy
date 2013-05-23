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

import fr.fg.client.data.TacticData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Tactic;
import fr.fg.server.util.JSONStringer;

public class TacticTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getPlayerTactics(JSONStringer json, int idPlayer) {
		if (json == null)
			json = new JSONStringer();
		
		json.array();
		
		List<Tactic> tactics = DataAccess.getTacticsByPlayer(idPlayer);
		
		synchronized (tactics) {
			for (Tactic tactic : tactics)
				json.object().
					key(TacticData.FIELD_HASH).	value(tactic.getHash()).
					key(TacticData.FIELD_SLOT_0).	value(tactic.getSlotId0()).
					key(TacticData.FIELD_SLOT_1).	value(tactic.getSlotId1()).
					key(TacticData.FIELD_SLOT_2).	value(tactic.getSlotId2()).
					key(TacticData.FIELD_SLOT_3).	value(tactic.getSlotId3()).
					key(TacticData.FIELD_SLOT_4).	value(tactic.getSlotId4()).
					key(TacticData.FIELD_SLOT_0_COUNT).	value(tactic.getTacticSlot0Count()).
					key(TacticData.FIELD_SLOT_1_COUNT).	value(tactic.getTacticSlot1Count()).
					key(TacticData.FIELD_SLOT_2_COUNT).	value(tactic.getTacticSlot2Count()).
					key(TacticData.FIELD_SLOT_3_COUNT).	value(tactic.getTacticSlot3Count()).
					key(TacticData.FIELD_SLOT_4_COUNT).	value(tactic.getTacticSlot4Count()).
					key(TacticData.FIELD_DATE).	value(tactic.getDate()).
					key(TacticData.FIELD_NAME).	value(tactic.getName()).
					endObject();
		}
		
		json.endArray();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
