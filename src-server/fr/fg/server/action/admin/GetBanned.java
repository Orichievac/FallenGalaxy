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

import fr.fg.server.data.Ban;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;
import fr.fg.client.data.PlayerInfosData;
import fr.fg.client.data.PlayersInfosData;

public class GetBanned extends Action {
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
		
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		json.object().key(PlayersInfosData.FIELD_ARRAY).array();
		for(Player bannedPlayer : players) {
			if(bannedPlayer.isAi() ||
					(!(bannedPlayer.getBanChat()>Utilities.now()) && !(bannedPlayer.getBanGame()>Utilities.now()))) {
				continue;
			}
			Ban lastBan= DataAccess.getLastBanByPlayerId(bannedPlayer.getId());
			long banTime = lastBan.getBanType()== Ban.CHAT? bannedPlayer.getBanChat() : bannedPlayer.getBanGame();
			json.object().
			key(PlayerInfosData.FIELD_LOGIN)			.value(bannedPlayer.getLogin()).
			key(PlayerInfosData.FIELD_ID)				.value(bannedPlayer.getId()).
			key(PlayerInfosData.FIELD_REASON)			.value(lastBan.getBanReason()).
			key(PlayerInfosData.FIELD_DATE)			.value(Utilities.formatDate(banTime*1000, "dd/MM/yy à HH:mm:ss")).
			key(PlayerInfosData.FIELD_BANNEDGAME)		.value(bannedPlayer.getBanGame()>Utilities.now())
			.endObject();
			
		}
		json.endArray()
			.endObject();
		
		return json.toString();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
