/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc, Thierry Chevalier

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.data.GameConstants;
import fr.fg.server.core.MessageTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Apply extends Action {
	
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String allyName = (String) params.get("ally");
		String cv = (String) params.get("cv");
		
		// Vérifie que le message ne contient pas l'ekey du joueur
		if (player.getEkey().length() > 0 && cv.contains(player.getEkey()))
			throw new IllegalOperationException("Ne transmettez pas votre clé d'export à d'autres joueurs.");
		
		if (player.getAlly() != null)
			throw new IllegalOperationException("Vous appartenez déjà à une alliance.");
		
		if (DataAccess.getApplicantByPlayer(player.getId()) != null)
			throw new IllegalOperationException("Vous postulez déjà dans une alliance.");
		
	
		long timestampRejoin = GameConstants.ALLY_REJOIN + player.getLastAllyIn();
		if (player.getLastAllyIn() > 0 && ((Utilities.now() - player.getLastAllyIn()) <  GameConstants.ALLY_REJOIN) )
			throw new IllegalOperationException("Vous ne pouvez pas rejoindre une alliance si vous avez" +
					" quittez la votre il y a moins d'une semaine.<br/><br/>" +
					"De nouveau possible le "+
					Utilities.formatDate(timestampRejoin*1000, "dd/MM/yy à HH:mm:ss")+ ".");
		
		
		
		Ally ally = DataAccess.getAllyByName(allyName);
		if (ally == null)
			ally = DataAccess.getAllyByTag(allyName);
		
		if (ally == null)
			throw new IllegalOperationException("Cette alliance n'existe pas.");
		
		if (ally.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas postuler dans une alliance PNJ.");
		
		if (ally.getMembers().size()+ DataAccess.getApplicantsByAlly(ally.getId()).size()>=GameConstants.ALLY_MAX_MEMBERS)
			throw new IllegalOperationException(ally.getName()+" ne peut pas accepter de nouvelles candidatures pour le moment.");
		
		List<Treaty> treaties = player.getTreaties();
		
		List<Player> blockingPlayer = new ArrayList<Player>();
		
		synchronized (treaties) {
			for (Treaty treaty : treaties) {
				Player otherPlayer = treaty.getOtherPlayer(player.getId());
				
				if (player.getTreatyWithPlayer(otherPlayer).equals(Treaty.ENEMY) &&
						otherPlayer.getAlly() != null) {
					if (ally.getTreatyWithAlly(otherPlayer.getAlly()) == AllyTreaty.TYPE_ALLY ||
							otherPlayer.getIdAlly() == ally.getId()) {
						blockingPlayer.add(otherPlayer);
					}
				}			
			}
		}
		
		if (blockingPlayer.size() != 0) {
			String msg = "";
			for (Player p : blockingPlayer) {
				msg += p.getLogin() + ", ";
			}
			throw new IllegalOperationException(
				"Vous êtes en guerre contre des personnes ayant des traités " +
				"avant l'alliance que vous souhaitez rejoindre : " +
				msg.substring(0, msg.length() - 3));
		}
		
		Applicant applicant = new Applicant(ally.getId(), player.getId());
		applicant.save();
		
		Event event = new Event(
			Event.EVENT_ALLY_APPLICANT,
			Event.TARGET_ALLY,
			ally.getId(), 0, -1, -1,
			player.getLogin());
		event.save();
		
		AllyNews applicantNews = new AllyNews(
			"Candidature " + player.getLogin(),
			"<b>" + player.getLogin() + "</b> souhaite intégrer notre alliance.<br/><br/>" +
			MessageTools.sanitizeHTML(cv),
			0, ally.getId(), 0);
		applicantNews.setIdApplicant(player.getId());
		applicantNews.save();
		
		// Signale l'évènement aux membres de l'alliance
		UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
		UpdateTools.queueNewAllyNewsUpdate(ally);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(0)
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
