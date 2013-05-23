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

import java.util.List;
import java.util.Map;


import fr.fg.server.core.MessageTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyNewsRead;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class AddNews extends Action {

	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		long lastUpdate = (Long) params.get("update");
		String content = (String) params.get("content");
		
		Ally ally = player.getAlly();
		
		if (ally == null)
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		// Vérifie que le message ne contient pas l'ekey du joueur
		if (player.getEkey().length() > 0 && content.contains(player.getEkey()))
			throw new IllegalOperationException("Ne transmettez pas votre clé d'export à d'autres joueurs.");
		
		List<AllyNews> newsList = DataAccess.getAllyNewsByAlly(ally.getId());
		
		synchronized (newsList) {
			for (AllyNews an : newsList) {
				if (an.getDate() + GameConstants.MESSAGES_FLOOD_LIMIT > Utilities.now() &&
						an.getIdAuthor() == player.getId() )
					throw new IllegalOperationException("Vous ne pouvez pas poster deux news en moins de 10 secondes.");
			}
		}
		
		AllyNews parent = null;
		if ((Integer) params.get("parent") != 0)
			parent = DataAccess.getAllyNewsById((Integer) params.get("parent"));
		
		if (parent != null && parent.getIdAlly() != player.getIdAlly())
			throw new IllegalOperationException("Opération invalide.");
		
		int idParent = 0;
		if (parent != null && parent.getIdParent() != 0)
			idParent = parent.getIdParent();
		else if (parent != null)
			idParent = parent.getId();
		
		AllyNews news = new AllyNews(
			MessageTools.tidyHTML((String) params.get("title")),
			MessageTools.sanitizeHTML(content),
			Utilities.now(),
			player.getId(),
			ally.getId(),
			idParent);
		news.save();
		if(parent==null)
		{
			Event event = new Event(Event.EVENT_ALLY_ADDED_NEWS, Event.TARGET_ALLY,
					ally.getId(), 0, -1, -1, player.getLogin());
			event.save();
			UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
		}
		if (idParent == 0) {
			AllyNewsRead newRead = new AllyNewsRead(news.getId(), player.getId());
			newRead.save();
		}
		
		UpdateTools.queueNewAllyNewsUpdate(ally, player.getId());
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(lastUpdate)
		);
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
