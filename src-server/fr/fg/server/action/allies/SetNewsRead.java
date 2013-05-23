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

package fr.fg.server.action.allies;

import java.util.Map;


import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyNewsRead;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetNewsRead extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idNews = (Integer) params.get("id");
		
		AllyNews news = DataAccess.getAllyNewsById(idNews);
		
		// Vérifie que le message existe et est adressé au joueur
		if (news == null || news.getIdAlly() != player.getIdAlly())
			throw new IllegalOperationException("Vous n'êtes pas autorisé à lire ce message.");
		
		if (news.getIdParent() != 0)
			news = DataAccess.getAllyNewsById(news.getIdParent());
		
		AllyNewsRead read = DataAccess.getAllyNewsReadByNewsAndPlayer(
				news.getId(), player.getId());
		
		if (read == null) {
			AllyNewsRead allyNewsRead = new AllyNewsRead(news.getId(), player.getId());
			allyNewsRead.save();
		}
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
