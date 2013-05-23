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

import fr.fg.server.core.TreatyTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class OfferPeace extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String allyName = (String) params.get("ally");
		
		Ally ally = player.getAlly();
		
		boolean accept = (Boolean) params.get("accept");
		
		if( ally==null )
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		Ally enemy = DataAccess.getAllyByName(allyName);
		if (enemy == null)
			enemy = DataAccess.getAllyByTag(allyName);
		
		if( enemy==null )
			throw new IllegalOperationException("L'alliance "+(String)params.get("ally")+" n'existe pas.");
		
		if( enemy.getName().equals(ally.getName()))
			throw new IllegalOperationException("Vous ne pouvez proposer une trêve à votre propre alliance.");
		
		if (player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_MANAGE_DIPLOMACY))
			throw new IllegalOperationException("Vous n'avez pas les " +
				"droits nécessaires pour effectuer cette action.");
		
		if (enemy.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas proposer de trêve à une alliance PNJ.");
		
		List<AllyTreaty> treaties = DataAccess.getAllyTreaties(ally.getId());
		
		boolean found = false;
		
		for (AllyTreaty treaty : treaties) {
			int otherAlly = treaty.getOtherAllyId(ally.getId());
			
			if (enemy.getId() == otherAlly &&
					treaty.getType().equals(AllyTreaty.TYPE_WAR)) {
				
				if (treaty.getSource() == 0) {
					if (accept) {
						if (Utilities.now() > treaty.getLastActivity() +
								GameConstants.WAR_INACTIVITY) {
							// Guerre inactive
							treaty.delete();
							
							// TODO jgottero event spécial pour la paix par inactivé
							Event event = new Event(Event.EVENT_ALLY_NEW_PEACE,
								Event.TARGET_ALLY, ally.getId(),0,-1,-1,enemy.getName());
							event.save();
							event = new Event(Event.EVENT_ALLY_NEW_PEACE,
								Event.TARGET_ALLY, enemy.getId(),0,-1,-1,ally.getName());
							event.save();
							
							// Mise à jour des secteurs consultés pour les joueurs
							// de l'alliance, afin que la ratification du
							// du traité de paix soit prise en compte, au besoin
							UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
							UpdateTools.queueNewEventUpdate(enemy.getMembers(), false);
							
							UpdateTools.queueAreaUpdate(ally.getMembers());
							UpdateTools.queueAreaUpdate(enemy.getMembers());
						} else {
							// Proposition de trêve
							synchronized (treaty.getLock()) {
								treaty = DataAccess.getEditable(treaty);
								treaty.setSource(ally.getId());
								treaty.save();
							}
							
							Event event = new Event(Event.EVENT_ALLY_OFFER_PEACE,
								Event.TARGET_ALLY, ally.getId(),0,-1,-1,enemy.getName());
							event.save();
							event = new Event(Event.EVENT_ALLY_PEACE_OFFERED,
								Event.TARGET_ALLY, enemy.getId(),0,-1,-1,ally.getName());
							event.save();
							
							UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
							UpdateTools.queueNewEventUpdate(enemy.getMembers(), false);
						}
					} else {
						throw new IllegalOperationException("Opération invalide.");
					}
				} else if (treaty.getSource() == ally.getId()) {
					if (!accept) {
						// Proposition de trêve retirée
						DataAccess.save(new Event(
							Event.EVENT_ALLY_CANCEL_PEACE,
							Event.TARGET_ALLY,
							ally.getId(),0,-1,-1,enemy.getName()));
						DataAccess.save(new Event(
							Event.EVENT_ALLY_PEACE_CANCELED,
							Event.TARGET_ALLY,
							enemy.getId(),0,-1,-1,ally.getName()));
						
						UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
						UpdateTools.queueNewEventUpdate(enemy.getMembers(), false);
					} else {
						throw new IllegalOperationException("Opération invalide.");
					}
				} else {
					if (accept) {
						// Proposition de trêve acceptée
						treaty.delete();
						
						DataAccess.save(new Event(
							Event.EVENT_ALLY_NEW_PEACE,
							Event.TARGET_ALLY,
							ally.getId(),0,-1,-1,enemy.getName()));
						DataAccess.save(new Event(
							Event.EVENT_ALLY_NEW_PEACE,
							Event.TARGET_ALLY,
							enemy.getId(),0,-1,-1,ally.getName()));
						
						// Mise à jour des secteurs consultés pour les joueurs
						// de l'alliance, afin que la ratification du
						// du traité de paix soit prise en compte, au besoin
						UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
						UpdateTools.queueNewEventUpdate(enemy.getMembers(), false);
						
						UpdateTools.queueAreaUpdate(ally.getMembers());
						UpdateTools.queueAreaUpdate(enemy.getMembers());
					} else {
						// Proposition de trêve refusée
						DataAccess.save(new Event(
							Event.EVENT_ALLY_DECLINE_PEACE,
							Event.TARGET_ALLY,
							ally.getId(),0,-1,-1,enemy.getName()));
						DataAccess.save(new Event(
							Event.EVENT_ALLY_PEACE_DECLINED,
							Event.TARGET_ALLY,
							enemy.getId(),0,-1,-1,ally.getName()));
						
						synchronized (treaty.getLock()) {
							AllyTreaty newTreaty = DataAccess.getEditable(treaty);
							newTreaty.setSource(0);
							DataAccess.save(newTreaty);
						}
						
						UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
						UpdateTools.queueNewEventUpdate(enemy.getMembers(), false);
					}
				}
				
				found = true;
				break;
			}
		}
		
		if( !found )
			throw new IllegalOperationException("Votre alliance n'est pas en guerre contre cette alliance.");
		
		return TreatyTools.getPlayerTreaties(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
