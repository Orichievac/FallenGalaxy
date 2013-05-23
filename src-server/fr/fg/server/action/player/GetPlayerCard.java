/*
Copyright 2010 Ghost

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

import java.util.Map;

import fr.fg.client.data.PlayerCardData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class GetPlayerCard extends Action {
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// --------------- récupération des paramètres
		String playerLogin = (String) params.get("login");
		
		// --------------- préparation des objets
		Player target = DataAccess.getPlayerByLogin(playerLogin);
		JSONStringer jsonResponse = new JSONStringer();
		String description = "";
		String avatarUrl = "";
		
		// --------------- vérifications
		//si le joueur n'est pas trouvé, on quitte
		if(target == null)
			throw new IllegalOperationException("Le joueur n'existe pas");
		
		// --------------- préparations
		description = target.getDescription();
		if(target.isPremium())
			avatarUrl = target.getAvatar();
		
		jsonResponse.object().
			key(PlayerCardData.FIELD_DESCRIPTION) 	.value(description).
			key(PlayerCardData.FIELD_AVATAR) 		.value(avatarUrl)
		.endObject();

		// --------------- Envois
		return jsonResponse.toString();
	}
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
