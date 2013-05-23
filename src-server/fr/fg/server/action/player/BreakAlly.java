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

package fr.fg.server.action.player;

import java.util.List;
import java.util.Map;

import fr.fg.server.core.TreatyTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class BreakAlly extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		
		Player allied = DataAccess.getPlayerByLogin((String)params.get("player"));
		
		if( allied==null )
			throw new IllegalOperationException("Le joueur "+(String)params.get("player")+" n'existe pas.");
		
		if( allied.getLogin().equals(player.getLogin()))
			throw new IllegalOperationException("Vous ne pouvez pas annuler une alliance avec vous même.");
		
		if (allied.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas annuler une alliance avec un PNJ.");
		
		List<Treaty> treaties = DataAccess.getPlayerTreaties(player.getId());
		
		boolean deleted = false;
		int type=-1;
		synchronized (treaties) {
			for( Treaty treaty : treaties ){
				int otherPlayer = (treaty.getIdPlayer1()==player.getId())?treaty.getIdPlayer2():treaty.getIdPlayer1();
				if( allied.getId()==otherPlayer && (treaty.getType().equals(Treaty.ALLY) || treaty.getType().equals(Treaty.DEFENSIVE)
						|| treaty.getType().equals(Treaty.TOTAL)) ){
					type = this.getType(treaty.getType());
					treaty.delete();
					deleted = true;
					break;
				}
			}
		}
		
		if( !deleted )
			throw new IllegalOperationException("Vous n'avez pas de traité d'alliance avec ce joueur.");
		switch(type) {
		case 0:
			DataAccess.save(new Event(
					Event.EVENT_PLAYER_ALLY_BROKEN,
					Event.TARGET_PLAYER,
					allied.getId(), 0, -1, -1, player.getLogin()));
				
			DataAccess.save(new Event(
				Event.EVENT_PLAYER_BREAK_ALLY,
				Event.TARGET_PLAYER,
				player.getId(), 0, -1, -1, allied.getLogin()));
			break;
		case 1:
			DataAccess.save(new Event(
					Event.EVENT_PLAYER_ALLY_DEFENSIVE_BROKEN,
					Event.TARGET_PLAYER,
					allied.getId(), 0, -1, -1, player.getLogin()));
				
			DataAccess.save(new Event(
				Event.EVENT_PLAYER_BREAK_ALLY_DEFENSIVE,
				Event.TARGET_PLAYER,
				player.getId(), 0, -1, -1, allied.getLogin()));
			break;
		case 2:
			DataAccess.save(new Event(
					Event.EVENT_PLAYER_ALLY_TOTAL_BROKEN,
					Event.TARGET_PLAYER,
					allied.getId(), 0, -1, -1, player.getLogin()));
				
			DataAccess.save(new Event(
				Event.EVENT_PLAYER_BREAK_ALLY_TOTAL,
				Event.TARGET_PLAYER,
				player.getId(), 0, -1, -1, allied.getLogin()));
			break;
		}
		UpdateTools.queueNewEventUpdate(player.getId(), false);
		UpdateTools.queueNewEventUpdate(allied.getId(), false);
		
		UpdateTools.queueAreaUpdate(player, allied);
		
		return TreatyTools.getPlayerTreaties(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	private int getType(String typeStr) {
		return typeStr.equals(Treaty.TOTAL) ? 2 : (typeStr.equals(Treaty.DEFENSIVE)? 1 : 0);
	}
}
