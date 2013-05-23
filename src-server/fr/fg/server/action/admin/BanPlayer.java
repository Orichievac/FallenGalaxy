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

import java.util.Map;

import fr.fg.server.data.Ban;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class BanPlayer extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		if(!player.isModerator()) {
			throw new IllegalOperationException("Vous devez être modérateur pour pouvoir exécuter cette action!");
		}
		String login = (String) params.get("login");
		long time = ((Long) params.get("time"));
		String reason = (String) params.get("reason");
		boolean bangame = ((Boolean) params.get("bangame"));
		Player bannedPlayer = DataAccess.getPlayerByLogin(login);
		if(bannedPlayer==null) {
			throw new IllegalOperationException(login+": Ce joueur n'existe pas!");
		}
		if(bangame==true && !player.isAdministrator()) {
			throw new IllegalOperationException("Vous devez être administrateur pour bannir "+login+" du jeu!");
		}
		
		if(bannedPlayer.isModerator() && !bangame) {
			throw new IllegalOperationException("Vous ne pouvez pas bannir du chat un modérateur");
		}
		
		if(bannedPlayer.isAdministrator()) {
			throw new IllegalOperationException("Impossible de bannir un administrateur!");
		}
		
		synchronized (bannedPlayer.getLock()) {
			long now = Utilities.now();
			bannedPlayer = DataAccess.getEditable(bannedPlayer);
			if(!bangame) {
				if(bannedPlayer.getBanChat()>now) {
					bannedPlayer.setBanChat(bannedPlayer.getBanChat()+time);
				}
				else {
					bannedPlayer.setBanChat(now+time);
				}
			}
			else {
				if(bannedPlayer.getBanGame()>now) {
					bannedPlayer.setBanGame(bannedPlayer.getBanGame()+time);
				}
				else {
					bannedPlayer.setBanGame(now+time);
				}
			}
			DataAccess.save(bannedPlayer);
			Ban ban = new Ban(bannedPlayer.getId(),reason,bangame?1:0);
			DataAccess.save(ban);
		}
		
		return FORWARD_SUCCESS;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
