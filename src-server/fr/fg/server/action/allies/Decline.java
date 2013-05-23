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


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyNewsRead;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Decline extends Action {

	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		long lastUpdate = (Long) params.get("update");
		
		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartanez à aucune alliance.");
		
		Applicant application = DataAccess.getApplicantByPlayer((Integer)params.get("id"));
		
		if( application==null || application.getIdAlly()!=ally.getId() )
			throw new IllegalOperationException("Ce joueur ne postule pas à votre alliance.");
		
		if( player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_ACCEPT) )
			throw new IllegalOperationException("Vous n'avez pas les droits nécessaires pour refuser ce joueur.");
		
		Player applicant = application.getPlayer();
		
		AllyVote vote = DataAccess.getAllyVoteByTarget(applicant.getId());
		
		if(vote!=null){
			vote.delete();
		}
		
		AllyNews news = DataAccess.getAllyNewsByApplicant(applicant.getId());
		if (news != null) {
			synchronized (news.getLock()) {
				AllyNews newNews = DataAccess.getEditable(news);
				newNews.setIdApplicant(0);
				newNews.save();
			}
			
			AllyNews acceptApplicantNews = new AllyNews(
					"Candidature refusée",
					"<b>" + player.getLogin() + "</b> a refusé la " +
					"candidature de <b>" + applicant.getLogin() + "</b>.",
					0, ally.getId(), news.getId());
			acceptApplicantNews.save();
			
			AllyNewsRead read = new AllyNewsRead(
				news.getIdParent() != 0 ? news.getIdParent() : news.getId(),
				player.getId());
			read.save();
		}
		
		application.delete();

		UpdateTools.queueNewAllyNewsUpdate(ally, player.getId());
		UpdateTools.queueAllyUpdate(applicant.getId(), 0);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(lastUpdate)
		);
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
