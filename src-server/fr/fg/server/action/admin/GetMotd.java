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

package fr.fg.server.action.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.client.data.MotdData;
import fr.fg.client.data.MotdsData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.MessageOfTheDay;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class GetMotd extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		if(!player.isModerator())
			throw new IllegalOperationException("Vous devez être modérateur pour pouvoir exécuter cette action!");
		
		JSONStringer json = new JSONStringer();
		
		List<MessageOfTheDay> motds = new ArrayList<MessageOfTheDay>(DataAccess.getAllMessageOfTheDay());
		
		json.object().key(MotdsData.FIELD_ARRAY).array();
		for(MessageOfTheDay motd : motds) {
			json.object().
			key(MotdData.FIELD_ID)				.value(motd.getId()).
			key(MotdData.FIELD_MESSAGE)			.value(motd.getMessage()).
			key(MotdData.FIELD_DATE)			.value(Utilities.formatDate(motd.getDate()*1000, "dd/MM/yy à HH:mm:ss")).
			endObject();
			
		}
		json.endArray()
			.endObject();
		
		return json.toString();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
