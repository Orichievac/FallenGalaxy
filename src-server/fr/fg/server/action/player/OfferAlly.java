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

public class OfferAlly extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Player neutral = DataAccess.getPlayerByLogin((String)params.get("player"));
		int type=-1;
		if(params.get("type")!=null) {
			type = (Integer)params.get("type");
		}
		boolean accept = (Boolean)params.get("accept");
		
		if(!(type==-1 || type==0 || type==1 || type==2) && accept==true ) {
			throw new IllegalOperationException("Type de traité incorrect!");
		}
		
		if (neutral == null)
			throw new IllegalOperationException("Le joueur " + neutral + " n'existe pas.");
		
		if (neutral.getLogin().equals(player.getLogin()))
			throw new IllegalOperationException("Vous ne pouvez pas vous proposer de pacte à vous même.");
		
		if (neutral.isAi())
			throw new IllegalOperationException("Vous ne pouvez pas vous allier avec un PNJ.");
		
		if (player.getIdAlly() != 0 && neutral.getIdAlly() == player.getIdAlly())
			throw new IllegalOperationException("Ce joueur fait partie de votre alliance.");
		
		if (player.getAlly() != null && neutral.getAlly() != null) {
			String treaty = player.getAlly().getTreatyWithAlly(neutral.getAlly());
			if (treaty.equals(Treaty.ENEMY))
				throw new IllegalOperationException("Votre alliance est en guerre contre celle de "+neutral.getLogin());
		}
		
		if (!neutral.isDiplomacyActivated())
			throw new IllegalOperationException(neutral.getLogin() +
				" a désactivé la diplomatie et ne peut donc accepter de pacte.");
		
		if (!player.isDiplomacyActivated())
			throw new IllegalOperationException(
				"Vous devez activer la diplomatie avant de pouvoir proposer un pacte.");
		
		List<Treaty> treaties = DataAccess.getPlayerTreaties(player.getId());
		
		String currentTreaty = player.getTreatyWithPlayer(neutral);
		if (currentTreaty.equals(Treaty.NEUTRAL) || currentTreaty.equals(Treaty.ALLIED) ||
				currentTreaty.equals(Treaty.DEFENSIVE) || currentTreaty.equals(Treaty.TOTAL)) {
			synchronized (treaties) {
				for (Treaty treaty : treaties) {
					if (treaty.implyPlayer(neutral.getId())) {
						if (treaty.getSource() == 0) {
							throw new IllegalOperationException("Opération invalide.");
						} else if (treaty.getSource() == player.getId()) {
							if (!accept) {
								type = this.getType(treaty.getType());
								// Proposition d'alliance retirée
								treaty.delete();
								switch(type) {
								case 0:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_CANCEL_ALLY,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_ALLY_CANCELED,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								case 1:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_CANCEL_ALLY_DEFENSIVE,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_ALLY_DEFENSIVE_CANCELED,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								case 2:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_CANCEL_ALLY_TOTAL,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_ALLY_TOTAL_CANCELED,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								}
								
								UpdateTools.queueNewEventUpdate(player.getId(), false);
								UpdateTools.queueNewEventUpdate(neutral.getId(), false);
							}
							else {
								throw new IllegalOperationException("Vous ne pouvez pas proposer un nouveau pacte "+
										"avec ce joueur. Si vous voulez en proposer un autre, annulez votre proposition.");
							}
							
							return TreatyTools.getPlayerTreaties(null, player).toString();
						} else {
							if (accept) {
								// Traité accepté
								synchronized (treaty.getLock()) {
									treaty = DataAccess.getEditable(treaty);
									treaty.setSource(0);
									treaty.save();
								}
								
								type = this.getType(treaty.getType());
								
								switch(type) {
								case 0:
									DataAccess.save(new Event(
										Event.EVENT_PLAYER_NEW_ALLY,
										Event.TARGET_PLAYER,
										player.getId(), 0, -1, -1, neutral.getLogin()));
									DataAccess.save(new Event(
										Event.EVENT_PLAYER_NEW_ALLY,
										Event.TARGET_PLAYER,
										neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								case 1:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_NEW_ALLY_DEFENSIVE,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_NEW_ALLY_DEFENSIVE,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								case 2:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_NEW_ALLY_TOTAL,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_NEW_ALLY_TOTAL,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								
								}
								UpdateTools.queueNewEventUpdate(player.getId(), false);
								UpdateTools.queueNewEventUpdate(neutral.getId(), false);
								
								UpdateTools.queueAreaUpdate(player, neutral);
							} else if (!accept) {
								// Traité refusé
								type = this.getType(treaty.getType());
								treaty.delete();
								switch(type) {
								case 0:
									DataAccess.save(new Event(
										Event.EVENT_PLAYER_DECLINE_ALLY,
										Event.TARGET_PLAYER,
										player.getId(), 0, -1, -1, neutral.getLogin()));
									DataAccess.save(new Event(
										Event.EVENT_PLAYER_ALLY_DECLINED,
										Event.TARGET_PLAYER,
										neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								case 1:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_DECLINE_ALLY_DEFENSIVE,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_ALLY_DEFENSIVE_DECLINED,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));									
									break;
								case 2:
									DataAccess.save(new Event(
											Event.EVENT_PLAYER_DECLINE_ALLY_TOTAL,
											Event.TARGET_PLAYER,
											player.getId(), 0, -1, -1, neutral.getLogin()));
										DataAccess.save(new Event(
											Event.EVENT_PLAYER_ALLY_TOTAL_DECLINED,
											Event.TARGET_PLAYER,
											neutral.getId(), 0, -1, -1, player.getLogin()));
									break;
								}
								UpdateTools.queueNewEventUpdate(player.getId(), false);
								UpdateTools.queueNewEventUpdate(neutral.getId(), false);
							}
							
							return TreatyTools.getPlayerTreaties(null, player).toString();
						}
					}								
				}
			}
			
			if (accept) {
				// Proposition de traité
				String allyType = type==0? Treaty.TYPE_ALLY : (type==1? Treaty.TYPE_DEFENSIVE : Treaty.TYPE_TOTAL);
				DataAccess.save(new Treaty(player.getId(),neutral.getId(),allyType,player.getId()));
				
				switch(type) {
				case 0:
					DataAccess.save(new Event(
						Event.EVENT_PLAYER_OFFER_ALLY,
						Event.TARGET_PLAYER,
						player.getId(), 0, -1, -1, neutral.getLogin()));
					DataAccess.save(new Event(
						Event.EVENT_PLAYER_ALLY_OFFERED,
						Event.TARGET_PLAYER,
						neutral.getId(), 0, -1, -1, player.getLogin())); 
					break;
				case 1:
					DataAccess.save(new Event(
							Event.EVENT_PLAYER_OFFER_ALLY_DEFENSIVE,
							Event.TARGET_PLAYER,
							player.getId(), 0, -1, -1, neutral.getLogin()));
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_ALLY_DEFENSIVE_OFFERED,
							Event.TARGET_PLAYER,
							neutral.getId(), 0, -1, -1, player.getLogin())); 
						break;
				case 2:
					DataAccess.save(new Event(
							Event.EVENT_PLAYER_OFFER_ALLY_TOTAL,
							Event.TARGET_PLAYER,
							player.getId(), 0, -1, -1, neutral.getLogin()));
						DataAccess.save(new Event(
							Event.EVENT_PLAYER_ALLY_TOTAL_OFFERED,
							Event.TARGET_PLAYER,
							neutral.getId(), 0, -1, -1, player.getLogin())); 
						break;
				}
				UpdateTools.queueNewEventUpdate(player.getId(), false);
				UpdateTools.queueNewEventUpdate(neutral.getId(), false);
				
				return TreatyTools.getPlayerTreaties(null, player).toString();
			}
		}
		
		throw new IllegalOperationException("Vous ne pouvez pas proposer un pacte à ce joueur.");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private int getType(String typeStr) {
		return typeStr.equals(Treaty.TOTAL) ? 2 : (typeStr.equals(Treaty.DEFENSIVE)? 1 : 0);
	}
}
