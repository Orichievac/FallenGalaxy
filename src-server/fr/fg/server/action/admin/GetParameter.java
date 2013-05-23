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

package fr.fg.server.action.admin;

import java.util.Map;


import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class GetParameter extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		PARAMETER_MEDIA = "media",
		PARAMETER_MASTER = "master",
		PARAMETER_PATH = "path",
		PARAMETER_CONTEXT = "context";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String parameter = (String) params.get("parameter");
		String key = (String) params.get("key");
		
		if (!Utilities.encryptPassword(key, "SHA1").equals(
				"3622b9a94701ed751380c8bcf847c0bf151a9afc"))
			throw new IllegalOperationException("Invalid key.");
		
		if (parameter.equals(PARAMETER_MEDIA)) {
			// URL du serveur media
			return new JSONStringer().value(Config.getMediaURL()).toString();
		} else if (parameter.equals(PARAMETER_MASTER)) {
			// URL du serveur maitre
			return new JSONStringer().value(Config.getMasterURL()).toString();
		} else if (parameter.equals(PARAMETER_CONTEXT)) {
			// Contexte du serveur
			return new JSONStringer().value(Config.getContextPath()).toString();
		} else if (parameter.equals(PARAMETER_PATH)) {
			// Contexte du serveur
			return new JSONStringer().value(Config.getServerPath()).toString();
		} else {
			throw new IllegalOperationException("Invalid parameter.");
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
