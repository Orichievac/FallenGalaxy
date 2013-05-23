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


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.TreatyTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
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
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String allyName = (String) params.get("ally");
		
		Ally ally = player.getAlly();
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		Ally allied = DataAccess.getAllyByName(allyName);
		if (allied == null)
			allied = DataAccess.getAllyByTag(allyName);
		
		if( allied==null )
			throw new IllegalOperationException("L'alliance "+(String)params.get("ally")+" n'existe pas.");
		
		if( allied.getName().equals(ally.getName()))
			throw new IllegalOperationException("Vous ne pouvez annuler une alliance avec votre propre alliance.");
		
		if (player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_MANAGE_DIPLOMACY))
			throw new IllegalOperationException("Vous n'avez pas les " +
				"droits nécessaires pour effectuer cette action.");
		
		if (allied.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas annuler une alliance avec une alliance PNJ.");
		
		List<AllyTreaty> treaties = DataAccess.getAllyTreaties(ally.getId());
		
		// TODO cancel si l'alliance retire sa proposition d'alliance ?
		int type=-1;
		boolean deleted = false;
		synchronized (treaties) {
			for( AllyTreaty treaty : treaties ){
				int otherAlly = (treaty.getIdAlly1()==ally.getId())?treaty.getIdAlly2():treaty.getIdAlly1();
				if( allied.getId()==otherAlly && (treaty.getType().equals(Treaty.ALLY) || treaty.getType().equals(Treaty.DEFENSIVE)
						|| treaty.getType().equals(Treaty.TOTAL)) ){
					type = this.getType(treaty.getType());
					treaty.delete();
					deleted = true;
					break;
				}
			}
		}
		if( !deleted )
			throw new IllegalOperationException("Votre alliance n'a pas de traité d'alliance avec cette alliance.");
		
		// Vérifie les liens entre flottes des membres des deux alliances
		List<Player> members = ally.getMembers();
		synchronized (members) {
			for (Player member : members)
				FleetTools.checkLinks(member);
		}
		
		members = allied.getMembers();
		synchronized (members) {
			for (Player member : members)
				FleetTools.checkLinks(member);
		}
		
		switch(type) {
		case 0:
			DataAccess.save(new Event(
					Event.EVENT_ALLY_ALLY_BROKEN,
					Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
				
			DataAccess.save(new Event(
				Event.EVENT_ALLY_BREAK_ALLY,
				Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
			break;
		case 1:
			DataAccess.save(new Event(
					Event.EVENT_ALLY_ALLY_DEFENSIVE_BROKEN,
					Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
				
			DataAccess.save(new Event(
				Event.EVENT_ALLY_BREAK_ALLY_DEFENSIVE,
				Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
			break;
		case 2:
			DataAccess.save(new Event(
					Event.EVENT_ALLY_ALLY_TOTAL_BROKEN,
					Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
				
			DataAccess.save(new Event(
				Event.EVENT_ALLY_BREAK_ALLY_TOTAL,
				Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
			break;
		}
		
		// Mise à jour des secteurs consultés pour les joueurs de l'alliance,
		// afin que l'annulation de l'alliance soit prise en compte, au besoin
		UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
		UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
		
		UpdateTools.queueAreaUpdate(ally.getMembers());
		UpdateTools.queueAreaUpdate(allied.getMembers());
		
		return TreatyTools.getPlayerTreaties(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	private int getType(String typeStr) {
		return typeStr.equals(Treaty.TOTAL) ? 2 : (typeStr.equals(Treaty.DEFENSIVE)? 1 : 0);
	}
}
