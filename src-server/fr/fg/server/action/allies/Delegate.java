/*
Copyright 2010 Nicolas Bosc

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

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Delegate extends Action {

	//--------------------------------------------------------- CONSTANTES --//
	//---------------------------------------------------------- ATTRIBUTS --//
	//------------------------------------------------------ CONSTRUCTEURS --//
	//----------------------------------------------------------- METHODES --//
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		
		Player target = DataAccess.getPlayerById((Integer)params.get("target"));
		
		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartanez à aucune alliance.");
		
		boolean ok = false;
		
		for(Player leader : ally.getLeaders()){
			if(player == leader && player.getAllyRank()>1)
			{
				ok = true;
			}
		}
		
		if(ok==false)
			throw new IllegalOperationException("Vous n'êtes pas leader de votre alliance.");
		
		if(player.getAllyRank()==target.getAllyRank())
			throw new IllegalOperationException("Vous ne pouvez pas échanger votre rang d'alliance avec un membre qui a le même que vous.");
		
		int targetAllyRank = target.getAllyRank();
		
		synchronized(target.getLock()){
			target = DataAccess.getEditable(target);
			target.setAllyRank(player.getAllyRank());
			target.save();
		}
		
		synchronized(player.getLock()){
			player = DataAccess.getEditable(player);
			player.setAllyRank(targetAllyRank);
			player.save();
		}
		
		Event event = new Event(
				Event.EVENT_LEADER_DELEGATE,
				Event.TARGET_ALLY,
				ally.getId(), 
				0, 
				-1, 
				-1,
				player.getLogin(),
				target.getLogin()
				);
			event.save();
			
			UpdateTools.queueNewEventUpdate(ally.getMembers(), false);

		
	
		
		return FORWARD_SUCCESS;
	}
	
	//--------------------------------------------------- METHODES PRIVEES --//
}
