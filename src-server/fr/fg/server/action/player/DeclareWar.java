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
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DeclareWar extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		
		Player enemy = DataAccess.getPlayerByLogin((String)params.get("player"));
		
		if (enemy == null)
			throw new IllegalOperationException("Le joueur "+(String)params.get("player")+" n'existe pas.");
		
		if (enemy.getLogin().equals(player.getLogin()))
			throw new IllegalOperationException("Vous ne pouvez pas vous déclarer la guerre à vous même.");
		
		if (enemy.getIdAlly() != 0 && enemy.getIdAlly() == player.getIdAlly())
			throw new IllegalOperationException("Vous ne pouvez pas déclarer la guerre à un joueur de " +
					"votre alliance.");
		
		if (enemy.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas déclarer la guerre à un PNJ.");
		
		if (!enemy.isDiplomacyActivated())
			throw new IllegalOperationException(enemy.getLogin() +
				" a désactivé la diplomatie et ne peut donc être attaqué.");
		
		if (!player.isDiplomacyActivated())
			throw new IllegalOperationException(
				"Vous devez activer la diplomatie avant de pouvoir déclarer la guerre.");
		
		if (player.getAlly() != null && enemy.getAlly() != null) {
			String treaty = player.getAlly().getTreatyWithAlly(enemy.getAlly());
			if (!treaty.equals(Treaty.NEUTRAL))
				throw new IllegalOperationException("Il existe déjà un traité " +
						"entre votre alliance et celle de ce joueur.");
		}
		
		List<Treaty> treaties = DataAccess.getPlayerTreaties(player.getId());
		
		synchronized (treaties) {
			for (Treaty treaty : treaties){
				Player otherPlayer = treaty.getOtherPlayer(player.getId());
				
				if (otherPlayer.getId() == enemy.getId()) {
					throw new IllegalOperationException("Vous avez déjà un traité avec ce joueur.");
				}
			}
		}
		
		Treaty newTreaty = new Treaty(player.getId(),enemy.getId(),Treaty.TYPE_WAR,0);
		newTreaty.save();
		
		DataAccess.save(new Event(Event.EVENT_PLAYER_WAR_DECLARED,
									Event.TARGET_PLAYER,enemy.getId(),0,-1,-1,player.getLogin()));
		DataAccess.save(new Event(Event.EVENT_PLAYER_DECLARE_WAR,
									Event.TARGET_PLAYER,player.getId(),0,-1,-1,enemy.getLogin()));
		
		player.checkNewRelationships(newTreaty, true);
		enemy.checkNewRelationships(newTreaty, false);
		
		UpdateTools.queueNewEventUpdate(player.getId(), false);
		UpdateTools.queueNewEventUpdate(enemy.getId(), false);
		
		UpdateTools.queueAreaUpdate(player, enemy);
		
		return TreatyTools.getPlayerTreaties(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
