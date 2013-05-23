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
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetRank extends Action {

	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Ally ally = player.getAlly();
		long update = (Long) params.get("update");
		
		if (ally == null)
			throw new IllegalOperationException("Vous n'avez pas alliance.");
		
		Player changed = DataAccess.getPlayerById((Integer) params.get("id"));
		
		if (changed == null || changed.getIdAlly() != player.getIdAlly())
			throw new IllegalOperationException("Ce joueur ne fait pas partie de votre alliance.");
		
		if (player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_PROMOTE, 
				Math.max((Integer)params.get("rank"),changed.getAllyRank())) )
			throw new IllegalOperationException("Vous n'avez pas les droits nécessaires pour changer le rang de ce membre.");
		
		if (changed.getAllyRank() == (Integer) params.get("rank"))
			throw new IllegalOperationException("Ce joueur possède déjà ce rang.");
		
		synchronized (changed.getLock()) {
			Player newKicked = DataAccess.getEditable(changed);
			newKicked.setAllyRank((Integer)params.get("rank"));
			DataAccess.save(newKicked);
		}
		
		DataAccess.save(new Event(Event.EVENT_ALLY_NEW_RANK,Event.TARGET_PLAYER,
				changed.getId(),0,-1,-1,ally.getOrganization(),((Integer) params.get("rank")).toString(),"",""));
		
		UpdateTools.queueNewEventUpdate(changed.getId(), false);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAllyUpdate(update)
		);
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
