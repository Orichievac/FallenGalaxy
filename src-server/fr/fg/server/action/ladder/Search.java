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

package fr.fg.server.action.ladder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


import fr.fg.client.data.LadderAllyData;
import fr.fg.client.data.LadderData;
import fr.fg.client.data.LadderPlayerData;
import fr.fg.server.core.Ladder;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class Search extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String ladder = (String) params.get("ladder");
		String keyword = (String) params.get("search");
		
		// Calcule la durée de validité du classement, après quoi il devra être
		// remis à jour
		long now = Utilities.now() * 1000;
		Calendar reference = Calendar.getInstance();
		reference.setTimeInMillis(now);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now);
		
		calendar.set(
				reference.get(Calendar.YEAR),
				reference.get(Calendar.MONTH),
				reference.get(Calendar.DAY_OF_MONTH),
				reference.get(Calendar.HOUR_OF_DAY) + 1, 0, 0);
		// Note : ajoute 1 min pour laisser le temps au serveur de remettre à
		// jour le classement
		int lifespan = 60 + (int) (calendar.getTimeInMillis() -
				reference.getTimeInMillis()) / 1000;
		
		JSONStringer json = new JSONStringer();
		json.object();
		json.key(LadderData.FIELD_LIFESPAN).	value(lifespan).
			key(LadderData.FIELD_RANGE).		value(-1);
		
		Pattern regexp = buildRegexp(keyword);
		
		if (ladder.equals(GetLadder.LADDER_PLAYERS)) {
			// Classement joueurs
			json.key(LadderData.FIELD_PLAYERS).array();
			
			if (keyword.contains("*")) {
				List<Player> players = DataAccess.getAllPlayers();
				ArrayList<Player> result = new ArrayList<Player>();
				
				synchronized (players) {
					for (Player ladderPlayer : players) {
						if (!ladderPlayer.isAi() &&
								regexp.matcher(ladderPlayer.getLogin()).matches()) {
							result.add(ladderPlayer);
							
							if (result.size() == 50)
								break;
						}
					}
				}
				
				Collections.sort(result, new Comparator<Player>() {
					public int compare(Player p1, Player p2) {
						return Ladder.getInstance().getPlayerRank(p1.getId()) <
						Ladder.getInstance().getPlayerRank(p2.getId()) ? -1 : 1;
					}
				});
				
				for (Player ladderPlayer : result) {
					json.object().
						key(LadderPlayerData.FIELD_LOGIN).			value(ladderPlayer.getLogin()).
						key(LadderPlayerData.FIELD_ALLY).			value(ladderPlayer.getAllyName()).
						key(LadderPlayerData.FIELD_TREATY).			value(ladderPlayer.getTreatyWithPlayer(player)).
						key(LadderPlayerData.FIELD_RANK).			value(Ladder.getInstance().getPlayerRank(ladderPlayer.getId())).
						key(LadderPlayerData.FIELD_LEVEL).			value(ladderPlayer.getLevel()).
						key(LadderPlayerData.FIELD_POINTS).			value(ladderPlayer.getPoints()).
						key(LadderPlayerData.FIELD_ACHIEVEMENTS).	value(ladderPlayer.getAchievementLevelsSum()).
						endObject();
				}
			} else {
				Player ladderPlayer = DataAccess.getPlayerByLogin(keyword);
				
				if (ladderPlayer != null) {
					// Classement d'un joueur
					
					json.object().
						key(LadderPlayerData.FIELD_LOGIN).			value(ladderPlayer.getLogin()).
						key(LadderPlayerData.FIELD_ALLY).			value(ladderPlayer.getAllyName()).
						key(LadderPlayerData.FIELD_TREATY).			value(ladderPlayer.getTreatyWithPlayer(player)).
						key(LadderPlayerData.FIELD_RANK).			value(Ladder.getInstance().getPlayerRank(ladderPlayer.getId())).
						key(LadderPlayerData.FIELD_LEVEL).			value(ladderPlayer.getLevel()).
						key(LadderPlayerData.FIELD_POINTS).			value(ladderPlayer.getPoints()).
						key(LadderPlayerData.FIELD_ACHIEVEMENTS).	value(ladderPlayer.getAchievementLevelsSum()).
						endObject();
				} else {
					Ally ally = DataAccess.getAllyByNameOrTag(keyword);
					
					if (ally != null) {
						// Classement des membres d'une alliance
						List<Player> members = new ArrayList<Player>(ally.getMembers());
						
						Collections.sort(members, new Comparator<Player>() {
							public int compare(Player p1, Player p2) {
								return Ladder.getInstance().getPlayerRank(p1.getId()) <
								Ladder.getInstance().getPlayerRank(p2.getId()) ? -1 : 1;
							}
						});
						
						synchronized (members) {
							for (Player member : members) {
								json.object().
									key(LadderPlayerData.FIELD_LOGIN).			value(member.getLogin()).
									key(LadderPlayerData.FIELD_ALLY).			value(member.getAllyName()).
									key(LadderPlayerData.FIELD_TREATY).			value(member.getTreatyWithPlayer(player)).
									key(LadderPlayerData.FIELD_RANK).			value(Ladder.getInstance().getPlayerRank(member.getId())).
									key(LadderPlayerData.FIELD_LEVEL).			value(member.getLevel()).
									key(LadderPlayerData.FIELD_POINTS).			value(member.getPoints()).
									key(LadderPlayerData.FIELD_ACHIEVEMENTS).	value(member.getAchievementLevelsSum()).
									endObject();
							}
						}
					}
				}
			}
			
			json.endArray();
		} else if (ladder.equals(GetLadder.LADDER_ALLIES)) {
			// Classement alliances
			Ally ally = player.getAlly();
			
			json.key(LadderData.FIELD_ALLIES).array();
			
			if (keyword.contains("*")) {
				List<Ally> allies = DataAccess.getAllAllies();
				ArrayList<Ally> result = new ArrayList<Ally>();
				
				synchronized (allies) {
					for (Ally ladderAlly : allies) {
						if (!ladderAlly.isAi() &&
								regexp.matcher(ladderAlly.getName()).matches() ||
								regexp.matcher(ladderAlly.getTag()).matches()) {
							result.add(ladderAlly);
							
							if (result.size() == 50)
								break;
						}
					}
				}
				
				Collections.sort(result, new Comparator<Ally>() {
					public int compare(Ally a1, Ally a2) {
						return Ladder.getInstance().getAllyRank(a1.getId()) <
						Ladder.getInstance().getAllyRank(a2.getId()) ? -1 : 1;
					}
				});
				
				for (Ally ladderAlly : result) {
					json.object().
						key(LadderAllyData.FIELD_NAME).			value(ladderAlly.getName()).
						key(LadderAllyData.FIELD_MEMBERS).		value(ladderAlly.getMembers().size()).
						key(LadderAllyData.FIELD_ORGANIZATION).	value(ladderAlly.getOrganization()).
						key(LadderAllyData.FIELD_TREATY).		value(ally != null ? ally.getTreatyWithAlly(ladderAlly) : Treaty.NEUTRAL).
						key(LadderAllyData.FIELD_RANK).			value(Ladder.getInstance().getAllyRank(ladderAlly.getId())).
						key(LadderAllyData.FIELD_POINTS).		value(ladderAlly.getPoints()).
						key(LadderAllyData.FIELD_ACHIEVEMENTS).	value(ladderAlly.getAchievementLevelsSum()).
						endObject();
				}
			} else {
				Ally ladderAlly = DataAccess.getAllyByNameOrTag(keyword);
				
				if (ladderAlly != null) {
					// Classement d'un joueur
					
					json.object().
						key(LadderAllyData.FIELD_NAME).			value(ladderAlly.getName()).
						key(LadderAllyData.FIELD_MEMBERS).		value(ladderAlly.getMembers().size()).
						key(LadderAllyData.FIELD_ORGANIZATION).	value(ladderAlly.getOrganization()).
						key(LadderAllyData.FIELD_TREATY).		value(ally != null ? ally.getTreatyWithAlly(ladderAlly) : Treaty.NEUTRAL).
						key(LadderAllyData.FIELD_RANK).			value(Ladder.getInstance().getAllyRank(ladderAlly.getId())).
						key(LadderAllyData.FIELD_POINTS).		value(ladderAlly.getPoints()).
						key(LadderAllyData.FIELD_ACHIEVEMENTS).	value(ladderAlly.getAchievementLevelsSum()).
						endObject();
				}
			}
			
			json.endArray();
		}
		
		json.endObject();
		
		return json.toString();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	private Pattern buildRegexp(String keyword) {
		return Pattern.compile("^" + keyword.replace("*", ".*") + "$",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
