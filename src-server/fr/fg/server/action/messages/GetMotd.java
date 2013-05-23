/*
Copyright 2010 Thierry Chevalier

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

package fr.fg.server.action.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.fg.client.data.ChatMessageData;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;
import fr.fg.server.data.MessageOfTheDay;

public class GetMotd extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	public String execute(Player player, Map<String, Object> params, Session session) throws Exception
	{
		List<Update> updates = new ArrayList<Update>();
		String type = (String) params.get("type");
		int motdType = type.equals("chat")? MessageOfTheDay.CHAT : MessageOfTheDay.INGAME;
		MessageOfTheDay motd = DataAccess.getMessageOfTheDayById(motdType);
		if(motd!=null)
		{
			JSONObject json = new JSONObject();
			json.put(ChatMessageData.FIELD_CONTENT,		"Message du jour: "+motd.getMessage());
			json.put(ChatMessageData.FIELD_DATE,		Utilities.formatDate(motd.getDate()*1000, "HH:mm"));
			json.put(ChatMessageData.FIELD_TYPE,		"info");
			json.put(ChatMessageData.FIELD_CHANNEL,		"0");
			json.put(ChatMessageData.FIELD_AUTHOR,		"");
			json.put(ChatMessageData.FIELD_RIGHTS,		"");
			json.put(ChatMessageData.FIELD_ALLY_TAG,	"");
			json.put(ChatMessageData.FIELD_ALLY_NAME,	"");
			updates.add(Update.getChatUpdate(json.toString()));
			return UpdateTools.formatUpdates(player, updates);
		}
		else
		{
			return FORWARD_SUCCESS;
		}
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
