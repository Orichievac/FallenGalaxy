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

import fr.fg.client.data.DiplomacyData;
import fr.fg.client.data.DiplomacyStateData;
import fr.fg.client.data.TreatyData;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class TreatyTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getDiplomacyState(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		json.object().
			key(DiplomacyStateData.FIELD_ACTIVATED).	value(player.isDiplomacyActivated()).
			key(DiplomacyStateData.FIELD_EFFECTIVE).	value(
				player.getSwitchDiplomacyDate() == 0 ? 0 :
					player.getSwitchDiplomacyDate() - Utilities.now()).
			endObject();
		
		return json;
	}
	
	public static JSONStringer getPlayerTreaties(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		List<Treaty> treaties = DataAccess.getPlayerTreaties(player.getId());
		
		// Récupère les traités du joueur
		json.object().
			key(DiplomacyData.FIELD_DIPLOMACY_STATE);
		getDiplomacyState(json, player);
		
		json.key(DiplomacyData.FIELD_PLAYER_TREATIES). 	array();
		
		for (int i = 0; i < treaties.size(); i++) {
			Treaty treaty = treaties.get(i);
			
			Player otherPlayer = DataAccess.getPlayerById(
				treaty.getOtherPlayerId(player.getId()));
			
			json.object().
				key(TreatyData.FIELD_TARGET).		value(otherPlayer != null ? otherPlayer.getLogin() : "").
				key(TreatyData.FIELD_DATE).			value(treaty.getDate()).
				key(TreatyData.FIELD_SOURCE).		value(treaty.getSource()).
				key(TreatyData.FIELD_TYPE).			value(treaty.getType()).
				endObject();
		}
		
		json.endArray().
			key(DiplomacyData.FIELD_ALLY_TREATIES). 	array();
		
		// Récupère les traités de l'alliance du joueur
		if (player.getIdAlly() != 0) {
			List<AllyTreaty> allyTreaties = DataAccess.getAllyTreaties(player.getIdAlly());
			
			for (AllyTreaty treaty : allyTreaties) {
				Ally otherAlly = DataAccess.getAllyById(
					treaty.getOtherAllyId(player.getAlly().getId()));
				
				json.object().
					key(TreatyData.FIELD_TARGET).	value(otherAlly != null ? otherAlly.getName() : "").
					key(TreatyData.FIELD_DATE).		value(treaty.getDate()).
					key(TreatyData.FIELD_SOURCE).	value(treaty.getSource()).
					key(TreatyData.FIELD_TYPE).		value(treaty.getType()).
					endObject();
			}
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
