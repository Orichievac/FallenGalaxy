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

package fr.fg.server.action;

import java.util.Map;

import fr.fg.client.data.ServerData;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;

public class GetServerInfos extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		JSONStringer json = new JSONStringer();
		json.object().
		key(ServerData.FIELD_NAME).			value(Config.getServerName()).
		key(ServerData.FIELD_LANGUAGE).		value(Config.getServerLang()).
		key(ServerData.FIELD_OPENING).			value(Config.getOpeningDate()).
		key(ServerData.FIELD_URL).			value(Config.getServerURL())
		.endObject();
		return json.toString();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
