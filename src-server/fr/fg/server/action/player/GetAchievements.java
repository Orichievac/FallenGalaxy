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


import fr.fg.client.data.AchievementData;
import fr.fg.server.data.Achievement;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class GetAchievements extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		List<Achievement> achievements = player.getAchievements();
		
		JSONStringer json = new JSONStringer();
		json.array();
		
		synchronized (achievements) {
			for (Achievement achievement : achievements) {
				json.object().
					key(AchievementData.FIELD_TYPE).	value(achievement.getType()).
					key(AchievementData.FIELD_SCORE).	value(achievement.getScore()).
					endObject();
			}
		}
		
		json.endArray();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
