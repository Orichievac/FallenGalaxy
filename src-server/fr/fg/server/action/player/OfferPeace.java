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
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class OfferPeace extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {

		Player enemy = DataAccess.getPlayerByLogin((String)params.get("player"));
		
		if( enemy==null )
			throw new IllegalOperationException("Le joueur "+(String)params.get("player")+" n'existe pas.");
		
		if( enemy.getLogin().equals(player.getLogin()))
			throw new IllegalOperationException("Vous ne pouvez pas annuler une guerre avec vous même.");
		
		if (enemy.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas proposer de trêve à un PNJ.");
		
		List<Treaty> treaties = DataAccess.getPlayerTreaties(player.getId());
		
		boolean accept = (Boolean) params.get("accept");
		
		for (Treaty treaty : treaties) {
			int otherPlayer = treaty.getOtherPlayerId(player.getId());
			
			if (enemy.getId() == otherPlayer &&
					treaty.getType().equals(AllyTreaty.TYPE_WAR)) {
				
				if (treaty.getSource() == 0) {
					if (accept) {
						if (Utilities.now() > treaty.getLastActivity() +
								GameConstants.WAR_INACTIVITY) {
							// Guerre inactive
							treaty.delete();
							
							DataAccess.save(new Event(
								Event.EVENT_PLAYER_END_OF_WAR,
								Event.TARGET_PLAYER,
								player.getId(), 0, -1, -1, enemy.getLogin()));
							DataAccess.save(new Event(
								Event.EVENT_PLAYER_END_OF_WAR,
								Event.TARGET_PLAYER,
								enemy.getId(), 0, -1, -1, player.getLogin()));
							
							UpdateTools.queueNewEventUpdate(player.getId(), false);
							UpdateTools.queueNewEventUpdate(enemy.getId(), false);
							
							UpdateTools.queueAreaUpdate(player, enemy);
							
							return TreatyTools.getPlayerTreaties(null, player).toString();
						} else {
							// Proposition de trêve
							synchronized (treaty.getLock()) {
								treaty = DataAccess.getEditable(treaty);
								treaty.setSource(player.getId());
								treaty.save();
							}
							
							DataAccess.save(new Event(
								Event.EVENT_PLAYER_OFFER_PEACE,
								Event.TARGET_PLAYER,
								player.getId(), 0, -1, -1, enemy.getLogin()));
							DataAccess.save(new Event(
								Event.EVENT_PLAYER_PEACE_OFFERED,
								Event.TARGET_PLAYER,
								enemy.getId(), 0, -1, -1, player.getLogin()));
							
							UpdateTools.queueNewEventUpdate(player.getId(), false);
							UpdateTools.queueNewEventUpdate(enemy.getId(), false);
							
							return TreatyTools.getPlayerTreaties(null, player).toString();
						}
					} else {
						throw new IllegalOperationException("Opération invalide.");
					}
				} else if (treaty.getSource() == player.getId()) {
					if (!accept) {
						// Proposition de trêve retirée
						synchronized (treaty.getLock()) {
							treaty = DataAccess.getEditable(treaty);
							treaty.setSource(0);
							treaty.save();
						}
						
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_CANCEL_PEACE,
							Event.TARGET_PLAYER,
							player.getId(), 0, -1, -1, enemy.getLogin()));
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_PEACE_CANCELED,
							Event.TARGET_PLAYER,
							enemy.getId(), 0, -1, -1, player.getLogin()));
						
						UpdateTools.queueNewEventUpdate(player.getId(), false);
						UpdateTools.queueNewEventUpdate(enemy.getId(), false);
						
						return TreatyTools.getPlayerTreaties(null, player).toString();
					} else {
						throw new IllegalOperationException("Opération invalide.");
					}
				} else {
					if (accept) {
						// Proposition de trêve acceptée
						treaty.delete();
						
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_NEW_PEACE,
							Event.TARGET_PLAYER,
							player.getId(), 0, -1, -1, enemy.getLogin()));
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_NEW_PEACE,
							Event.TARGET_PLAYER,
							enemy.getId(), 0, -1, -1, player.getLogin()));
						
						UpdateTools.queueNewEventUpdate(player.getId(), false);
						UpdateTools.queueNewEventUpdate(enemy.getId(), false);
						
						UpdateTools.queueAreaUpdate(player, enemy);
						
						return TreatyTools.getPlayerTreaties(null, player).toString();
					} else {
						// Proposition de trêve refusée
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_DECLINE_PEACE,
							Event.TARGET_PLAYER,
							player.getId(), 0, -1, -1, enemy.getLogin()));
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_PEACE_DECLINED,
							Event.TARGET_PLAYER,
							enemy.getId(), 0, -1, -1, player.getLogin()));
						
						UpdateTools.queueNewEventUpdate(player.getId(), false);
						UpdateTools.queueNewEventUpdate(enemy.getId(), false);
						
						return TreatyTools.getPlayerTreaties(null, player).toString();
					}
				}
			}
		}

		throw new IllegalOperationException("Vous n'êtes pas en guerre contre ce joueur.");
		
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
