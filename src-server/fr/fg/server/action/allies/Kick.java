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

import fr.fg.server.core.ChatManager;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class Kick extends Action {

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
		
		Player kicked = DataAccess.getPlayerById((Integer)params.get("id"));
		
		if( kicked==null )
			throw new IllegalOperationException("Ce joueur n'existe pas");
		
		if( kicked.getIdAlly()!=player.getIdAlly() )
			throw new IllegalOperationException("Ce joueur ne fait pas partie de votre alliance.");
		
		if( player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_KICK, kicked.getAllyRank()) )
			throw new IllegalOperationException("Vous n'avez pas les droits nécessaires pour éjecter ce membre.");
		
		List<Player> members = ally.getMembers();
		
		// Quitte les canaux de discussion de l'alliance
		ChatManager.getInstance().leaveAllyChannels(kicked);
		
		synchronized (kicked.getLock()) {
			kicked = DataAccess.getEditable(kicked);
			kicked.setAllyRank(0);
			kicked.setIdAlly(0);
			kicked.setLastAllyIn(Utilities.now());
			kicked.save();
		}
		
		// Vérifie les liens entre flottes des membres de l'alliance
		synchronized (members) {
			for (Player member : members)
				FleetTools.checkLinks(member);
		}
		
		//TODO bmoyet Event : Ajoutez un événement pour le kick
		//DataAccess.save(new Event());
		
		// Met à jour l'influence de l'alliance
		ally.updateInfluences();
		
		// Mise à jour des secteurs consultés pour les joueurs de l'alliance,
		// afin que le membre éjecté soit pris en compte, au besoin
		UpdateTools.queueAreaUpdate(ally.getMembers());
		
		UpdateTools.queueAllyUpdate(kicked.getId(), 0, false);
		UpdateTools.queueChatChannelsUpdate(kicked.getId(), false);
		UpdateTools.queueAreaUpdate(kicked);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(lastUpdate),
			Update.getAreaUpdate()
		);
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
