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

package fr.fg.server.action.chat;

import java.util.Map;


import fr.fg.server.core.ChatManager;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetChannelEnable extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String channel = (String) params.get("channel");
		boolean enable = (Boolean) params.get("enable");
		
		if (!ChatManager.getInstance().isInChannel(channel, player.getId()))
			throw new IllegalOperationException(
				"Vous ne pouvez pas envoyer de message sur ce canal.");
		
		ChatManager.getInstance().setChannelEnabled(player, channel, enable);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getChatChannelsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
