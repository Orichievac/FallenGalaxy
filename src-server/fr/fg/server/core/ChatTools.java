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

import java.util.HashSet;
import java.util.Set;

import fr.fg.client.data.ChatChannelData;
import fr.fg.client.data.ChatChannelsData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.util.JSONStringer;

public class ChatTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getChannels(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		player = DataAccess.getPlayerById(player.getId());
		
		json.object().
			key(ChatChannelsData.FIELD_CHANNELS).	array();
		
		Set<String> channels = new HashSet<String>(
			ChatManager.getInstance().getPlayerChannels(player.getId()));
		
		for (String channel : channels) {
			json.object().
				key(ChatChannelData.FIELD_NAME).	value(ChatManager.getInstance().getChannelName(channel)).
				key(ChatChannelData.FIELD_ACTIVE).	value(ChatManager.getInstance().isChannelEnabled(player, channel)).
				endObject();
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
