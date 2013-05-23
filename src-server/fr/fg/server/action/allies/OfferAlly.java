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
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class OfferAlly extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String allyName = (String) params.get("ally");
		int type=-1;
		if(params.get("type")!=null) {
			type = (Integer)params.get("type");
		}
		boolean accept = (Boolean) params.get("accept");
		
		Ally ally = player.getAlly();
		
		if (ally == null)
			throw new IllegalOperationException("Vous n'appartenez à aucune alliance.");
		
		Ally allied = DataAccess.getAllyByName(allyName);
		if (allied == null)
			allied = DataAccess.getAllyByTag(allyName);
		
		if (allied == null)
			throw new IllegalOperationException("L'alliance " + allyName + " n'existe pas.");
		
		if (allied.getName().equals(ally.getName()))
			throw new IllegalOperationException("Vous ne pouvez proposer une alliance à votre propre alliance.");
		
		if (player.getAllyRank() < ally.getRequiredRank(Ally.RIGHT_MANAGE_DIPLOMACY))
			throw new IllegalOperationException("Vous n'avez pas les " +
				"droits nécessaires pour effectuer cette action.");
		
		if (allied.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas vous allier avec une alliance PNJ.");
		
		List<AllyTreaty> treaties = DataAccess.getAllyTreaties(ally.getId());
		
		boolean found = false;
		String currentTreaty = ally.getTreatyWithAlly(allied);
		if (currentTreaty.equals(AllyTreaty.NEUTRAL) || currentTreaty.equals(AllyTreaty.ALLIED) ||
				currentTreaty.equals(AllyTreaty.DEFENSIVE) || currentTreaty.equals(AllyTreaty.TOTAL)) {
			synchronized (treaties) {
				for (AllyTreaty treaty : treaties) {
					if (treaty.implyAlly(allied.getId()) && treaty.isPact()) {
						if (treaty.getSource() == 0) {
							throw new IllegalOperationException("Opération invalide.");
						} else if (treaty.getSource() == ally.getId()) {
							if (!accept) {
								// Proposition d'alliance retirée
								treaty.delete();
								//Traité refusé
								switch(type) {
								case 0:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_CANCEL_ALLY,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_ALLY_CANCELED,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								case 1:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_CANCEL_ALLY_DEFENSIVE,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_ALLY_DEFENSIVE_CANCELED,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								case 2:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_CANCEL_ALLY_TOTAL,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_ALLY_TOTAL_CANCELED,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								}
								
								UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
								UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
							}
						} else {
							if (accept) {
								// Traité accepté
								synchronized (treaty.getLock()) {
									treaty = DataAccess.getEditable(treaty);
									treaty.setSource(0);
									treaty.save();
								}
								
								//Annulation des guerres entres les joueurs des 2 alliances s'il y en a
								List<Player> allyPlayers = ally.getMembers();
								for(Player allyPlayer : allyPlayers) {
									List<Treaty> playerTreaties = allyPlayer.getTreaties();
									for(Treaty playerTreaty : playerTreaties) {
										if(playerTreaty.getType().equals(Treaty.TYPE_WAR)) {
											Player impliedPlayer = playerTreaty.getOtherPlayer(allyPlayer.getId());
											if(impliedPlayer.getAlly()!=null &&
													impliedPlayer.getAlly().getName().equals(allied.getName())) {
												playerTreaty.delete();
											}
										}
									}
								}
								
								type = this.getType(treaty.getType());
								
								switch(type) {
								case 0:
									DataAccess.save(new Event(
										Event.EVENT_ALLY_NEW_ALLY,
										Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
									DataAccess.save(new Event(
										Event.EVENT_ALLY_NEW_ALLY,
										Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								case 1:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_NEW_ALLY_DEFENSIVE,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_NEW_ALLY_DEFENSIVE,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								case 2:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_NEW_ALLY_TOTAL,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_NEW_ALLY_TOTAL,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								
								}
								
								// Mise à jour des secteurs consultés pour les joueurs
								// de l'alliance, afin que la ratification de
								// l'alliance soit prise en compte, au besoin
								UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
								UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
								
								UpdateTools.queueAreaUpdate(ally.getMembers());
								UpdateTools.queueAreaUpdate(allied.getMembers());
							} else {
								treaty.delete();
								// Traité refusé
								type = this.getType(treaty.getType());
								treaty.delete();
								switch(type) {
								case 0:
									DataAccess.save(new Event(
										Event.EVENT_ALLY_DECLINE_ALLY,
										Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
									DataAccess.save(new Event(
										Event.EVENT_ALLY_ALLY_DECLINED,
										Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								case 1:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_DECLINE_ALLY_DEFENSIVE,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_ALLY_DEFENSIVE_DECLINED,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));							
									break;
								case 2:
									DataAccess.save(new Event(
											Event.EVENT_ALLY_DECLINE_ALLY_TOTAL,
											Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
										DataAccess.save(new Event(
											Event.EVENT_ALLY_ALLY_TOTAL_DECLINED,
											Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
									break;
								}
								
								UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
								UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
							}
						}
						
						found = true;
						break;
					}
				}
			}
			
			if (!found && type!=-1) {
				if (accept) {
					String allyType = type==0? Treaty.TYPE_ALLY : (type==1? Treaty.TYPE_DEFENSIVE : Treaty.TYPE_TOTAL);
					AllyTreaty allyTreaty = new AllyTreaty(
						ally.getId(),
						allied.getId(),
						allyType,
						ally.getId());
					allyTreaty.save();
					
					switch(type) {
					case 0:
						DataAccess.save(new Event(
							Event.EVENT_ALLY_OFFER_ALLY,
							Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
						DataAccess.save(new Event(
							Event.EVENT_ALLY_ALLY_OFFERED,
							Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
						break;
					case 1:
						DataAccess.save(new Event(
								Event.EVENT_ALLY_OFFER_ALLY_DEFENSIVE,
								Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
							DataAccess.save(new Event(
								Event.EVENT_ALLY_ALLY_DEFENSIVE_OFFERED,
								Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
							break;
					case 2:
						DataAccess.save(new Event(
								Event.EVENT_ALLY_OFFER_ALLY_TOTAL,
								Event.TARGET_ALLY, ally.getId(), 0, -1, -1, allied.getName()));
							DataAccess.save(new Event(
								Event.EVENT_ALLY_ALLY_TOTAL_OFFERED,
								Event.TARGET_ALLY, allied.getId(), 0, -1, -1, ally.getName()));
							break;
					}
					
					UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
					UpdateTools.queueNewEventUpdate(allied.getMembers(), false);
					
					return TreatyTools.getPlayerTreaties(null, player).toString();
				}
				
				throw new IllegalOperationException("Cette alliance ne vous propose pas de traité.");
			}
		}
		
		return TreatyTools.getPlayerTreaties(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	private int getType(String typeStr) {
		return typeStr.equals(Treaty.TOTAL) ? 2 : (typeStr.equals(Treaty.DEFENSIVE)? 1 : 0);
	}
}
