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


import fr.fg.server.core.ChatManager;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class CreateAlly extends Action {

	
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		
		String name = (String) params.get("name");
		String tag  = (String) params.get("tag");
		
		if( DataAccess.getPlayerByLogin(name)!=null )
			throw new IllegalOperationException("Un joueur portant ce nom existe déjà.");
		
		if( DataAccess.getAllyByName(name)!=null )
			throw new IllegalOperationException("Une alliance portant ce nom existe déjà.");
		
		if (DataAccess.getAllyByTag(tag) != null)
			throw new IllegalOperationException("Une alliance avec ce tag existe déjà.");
		
		if( player.getAlly()!=null )
			throw new IllegalOperationException("Vous appartenez déjà à une alliance.");
		
		if( DataAccess.getApplicantByPlayer(player.getId())!=null )
			throw new IllegalOperationException("Vous postulez déjà à une alliance.");
		
		if( Badwords.containsBadwords(name) )
			throw new IllegalOperationException("Le nom de l'alliance comporte un mot non autorisé ou vulgaire.");
		
		if( Badwords.containsBadwords(tag) )
			throw new IllegalOperationException("Le tag comporte un mot non autorisé ou vulgaire.");
		
//		if (player.getLastAllyIn() > 0 && ((Utilities.now() - player.getLastAllyIn()) <  GameConstants.ALLY_REJOIN) )
//			throw new IllegalOperationException("Vous ne pouvez pas fonder une alliance si vous avez" +
//					" quittez la votre il y a moins d'une semaine.");
		
		
		Ally ally = new Ally(name, tag, (String)params.get("organization"),"",player.getPoints(),player.getId());
		ally.save();
		
		synchronized(player.getLock()){
			player = DataAccess.getEditable(player);
			player.setIdAlly(ally.getId());
			player.setAllyRank(ally.getLeaderRank());
			player.save();
		}
		
		// Rejoint les canaux de discussion de l'alliance
		ChatManager.getInstance().joinAllyChannels(player);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(0),
			Update.getChatChannelsUpdate(),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
