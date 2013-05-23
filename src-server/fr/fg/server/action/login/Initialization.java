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

package fr.fg.server.action.login;

import java.util.List;
import java.util.Map;



import fr.fg.client.data.ServerData;
import fr.fg.client.data.StateData;
import fr.fg.server.core.ChangelogTools;
import fr.fg.server.core.InitializationTools;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.ServerController;
import fr.fg.server.servlet.Servers;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class Initialization extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		JSONStringer json = new JSONStringer();
		json.object().
			key(StateData.FIELD_STATE);
		
		if (player == null) {
			json.value(StateData.STATE_OFFLINE).
				key(StateData.FIELD_SERVERS).			array();
			
			// Liste des serveurs accessibles
			List<Map<String, String>> servers = Servers.getAllServers();
			int index = 0;
			int serverIndex = -1;
			
			for (Map<String, String> server : servers) {
				String name = server.get(Servers.PARAMETER_NAME);
				
				if (name.equals(Config.getServerName()))
					serverIndex = index;
				
				json.object().
					key(ServerData.FIELD_NAME).				value(name).
					key(ServerData.FIELD_LANGUAGE).			value(server.get(Servers.PARAMETER_LANGUAGE)).
					key(ServerData.FIELD_URL).				value(server.get(Servers.PARAMETER_URL)).
					key(ServerData.FIELD_OPENING).			value(server.get(Servers.PARAMETER_OPENING)).
					endObject();
				
				index++;
			}
			
			long shutdownDate = ServerController.getShutdownDate();
			
			json.endArray();
			
			json.key(StateData.FIELD_CHANGELOGS);
			ChangelogTools.getLastChangelogs(json);
			
			json.key(StateData.FIELD_CURRENT_SERVER).	value(serverIndex).
				key(StateData.FIELD_SERVER_SHUTDOWN).	value(shutdownDate == -1 ? -1 : Math.max(0, shutdownDate - Utilities.now()));
		} else if (player.getContinuation() == null ||
				!player.getContinuation().isSuspended()) {
			if (player.getSystems().size() == 0) {
				// Le joueur n'a pas de système
				long shutdownDate = ServerController.getShutdownDate();
				
				json.value(StateData.STATE_NO_SYSTEM).
					key(StateData.FIELD_SECURITY_KEY).		value(player.getSecurityKey()).
					key(StateData.FIELD_SERVER_SHUTDOWN).	value(shutdownDate == -1 ? -1 : Math.max(0, shutdownDate - Utilities.now()));
			} else {
				// Renvoie les informations sur le joueur
				json.value(StateData.STATE_ONLINE);
				InitializationTools.getInitializationData(json, player);
			}
		} else {
			long shutdownDate = ServerController.getShutdownDate();
			
			json.value(StateData.STATE_ALREADY_ONLINE).
				key(StateData.FIELD_SECURITY_KEY).		value(player.getSecurityKey()).
				key(StateData.FIELD_SERVER_SHUTDOWN).	value(shutdownDate == -1 ? -1 : Math.max(0, shutdownDate - Utilities.now()));
		}
		
		json.endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
