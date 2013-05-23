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


import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class StickNews extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {

		AllyNews news = DataAccess.getAllyNewsById((Integer)params.get("news"));
		
		if( news==null )
			throw new IllegalOperationException("Cette news n'existe pas.");
		
		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		if( news.getIdAlly()!=player.getIdAlly() )
			throw new IllegalOperationException("Vous ne pouvez pas épingler cette news.");
		
		if( news.getIdParent()!=0 )
			throw new IllegalOperationException("Vous ne pouvez pas épingler une réponse.");
		
		if( player.getAllyRank()<ally.getRequiredRank(Ally.RIGHT_MANAGE_NEWS) )
			throw new IllegalOperationException("Vous n'avez pas les droits nécessaires pour effectuer cette action.");
		
		if( ally.getStickyNewsCount()>=5 )
			throw new IllegalOperationException("Vous ne pouvez pas épigler plus de 5 news.");
		
		if( news.getIdAuthor()==0 )
			throw new IllegalOperationException("Vous ne pouvez pas épingler une news système.");
		
		synchronized (news) {
			AllyNews newNews = DataAccess.getEditable(news);
			newNews.setSticky(!newNews.isSticky());
			DataAccess.save(newNews);
		}

		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
