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


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class CancelApply extends Action {
	
	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {

		Applicant application = DataAccess.getApplicantByPlayer(player.getId());
		
		if( application==null )
			throw new IllegalOperationException("Vous ne postulez à aucune alliance.");
		
		Ally ally = application.getAlly();
		
		List<AllyVote> votes = DataAccess.getAllyVotesByAlly(ally.getId());
		
		synchronized (votes) {
			for( AllyVote vote : votes ){
				if( vote.getType().equals("accept") && vote.getIdPlayer()==application.getIdPlayer() )
				{
					vote.delete();
					break;
				}
			}
		}
		
		Event event = new Event(Event.EVENT_ALLY_CANCEL_APPLY,
			Event.TARGET_ALLY, application.getIdAlly(),
			0, -1, -1, application.getPlayer().getLogin());
		event.save();
		
		application.delete();
		
		AllyNews news = DataAccess.getAllyNewsByApplicant(player.getId());
		
		if (news != null) {
			synchronized (news.getLock()) {
				AllyNews newNews = DataAccess.getEditable(news);
				newNews.setIdApplicant(0);
				newNews.save();
			}
			
			AllyNews cancelApplyNews = new AllyNews("Candidature annulée",
					"<b>" + player.getLogin() + "</b> a retiré sa " +
					"candidature pour entrer dans notre alliance.",
					0, news.getIdAlly(), news.getId());
			cancelApplyNews.save();
		}
		
		UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
		UpdateTools.queueNewAllyNewsUpdate(ally, player.getId());
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(0)
		);
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
