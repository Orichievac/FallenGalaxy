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
import java.util.List;
import java.util.Map;



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

public class GetLadder extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		LADDER_PLAYERS = "players",
		LADDER_ALLIES  = "allies";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String ladder = (String) params.get("ladder");
		int range = (Integer) params.get("range");
		boolean self = (Boolean) params.get("self");
		
		Ally ally = player.getAlly();
		
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
		json.key(LadderData.FIELD_LIFESPAN).	value(lifespan);
		
		if (ladder.equals(LADDER_PLAYERS)) {
			// Classement des joueurs
			List<Integer> playersLadder =
				Ladder.getInstance().getPlayersLadder();
			
			json.
				key(LadderData.FIELD_RANGE).	value(range).
				key(LadderData.FIELD_PLAYERS).	array();
			
			long previousPoints = -1;
			int previousRank = -1;
			for (int i = range * 50; i < (range + 1) * 50 &&
					i < playersLadder.size(); i++) {
				Player ladderPlayer =
					DataAccess.getPlayerById(playersLadder.get(i));
				
				if (ladderPlayer == null)
					break;
				
				if (ladderPlayer.isAi() || ladderPlayer.getPoints() == 0)
					continue;
				
				// Calcule le classement du joueur
				if (previousPoints == -1) {
					previousPoints = ladderPlayer.getPoints();
					previousRank = Ladder.getInstance().getPlayerRank(
							ladderPlayer.getId());
				}
				
				previousRank = previousPoints == ladderPlayer.getPoints() ?
					previousRank : i + 1;
				previousPoints = ladderPlayer.getPoints();
				
				json.object().
					key(LadderPlayerData.FIELD_LOGIN).			value(ladderPlayer.getLogin()).
					key(LadderPlayerData.FIELD_ALLY).			value(ladderPlayer.getAllyName()).
					key(LadderPlayerData.FIELD_TREATY).			value(ladderPlayer.getTreatyWithPlayer(player)).
					key(LadderPlayerData.FIELD_RANK).			value(previousRank).
					key(LadderPlayerData.FIELD_LEVEL).			value(ladderPlayer.getLevel()).
					key(LadderPlayerData.FIELD_POINTS).			value(ladderPlayer.getPoints()).
					key(LadderPlayerData.FIELD_ACHIEVEMENTS).	value(ladderPlayer.getAchievementLevelsSum()).
					key(LadderPlayerData.FIELD_PREMIUM).		value(ladderPlayer.isPremium()).
					endObject();
			}
			json.endArray();
			
			// Récupère les données sur le joueur au besoin
			if (self) {
				json.key(LadderData.FIELD_SELF_DATA).
					object().
					key(LadderPlayerData.FIELD_LOGIN).			value(player.getLogin()).
					key(LadderPlayerData.FIELD_ALLY).			value(ally != null ? ally.getName() : "").
					key(LadderPlayerData.FIELD_TREATY).			value(Treaty.PLAYER).
					key(LadderPlayerData.FIELD_RANK).			value(Ladder.getInstance().getPlayerRank(player.getId())).
					key(LadderPlayerData.FIELD_LEVEL).			value(player.getLevel()).
					key(LadderPlayerData.FIELD_POINTS).			value(player.getPoints()).
					key(LadderPlayerData.FIELD_ACHIEVEMENTS).	value(player.getAchievementLevelsSum()).
					key(LadderPlayerData.FIELD_PREMIUM).		value(player.isPremium()).
					endObject();
			}
		} else {
			// Classement des alliances
			List<Integer> alliesLadder =
				new ArrayList<Integer>(Ladder.getInstance().getAlliesLadder());
			
			json.
				key(LadderData.FIELD_RANGE).	value(range).
				key(LadderData.FIELD_ALLIES).	array();
			
			long previousPoints = -1;
			int previousRank = -1;
			for (int i = range * 50; i < (range + 1) * 50 &&
					i < alliesLadder.size(); i++) {
				Ally ladderAlly = DataAccess.getAllyById(alliesLadder.get(i));
				
				// Teste si l'alliance a été supprimée
				if (ladderAlly == null) {
					alliesLadder.remove(i);
					i--;
					continue;
				}
				
				if (ladderAlly.isAi() || ladderAlly.getPoints() == 0)
					continue;
				
				// Calcule le classement du joueur
				if (previousPoints == -1) {
					previousPoints = ladderAlly.getPoints();
					previousRank = Ladder.getInstance().getAllyRank(
							ladderAlly.getId());
				}
				
				previousRank = previousPoints == ladderAlly.getPoints() ?
					previousRank : i + 1;
				previousPoints = ladderAlly.getPoints();
				
				json.object().
					key(LadderAllyData.FIELD_NAME).			value(ladderAlly.getName()).
					key(LadderAllyData.FIELD_MEMBERS).		value(ladderAlly.getMembers().size()).
					key(LadderAllyData.FIELD_ORGANIZATION).	value(ladderAlly.getOrganization()).
					key(LadderAllyData.FIELD_TREATY).		value(ally != null ? ally.getTreatyWithAlly(ladderAlly) : Treaty.NEUTRAL).
					key(LadderAllyData.FIELD_RANK).			value(previousRank).
					key(LadderAllyData.FIELD_POINTS).		value(ladderAlly.getPoints()).
					key(LadderAllyData.FIELD_ACHIEVEMENTS).	value(ladderAlly.getAchievementLevelsSum()).
					endObject();
			}
			json.endArray();
			
			// Récupère les données sur le joueur au besoin
			if (self) {
				if (ally != null) {
					json.key(LadderData.FIELD_SELF_DATA).
						object().
						key(LadderAllyData.FIELD_NAME).				value(ally.getName()).
						key(LadderAllyData.FIELD_MEMBERS).			value(ally.getMembers().size()).
						key(LadderAllyData.FIELD_ORGANIZATION).		value(ally.getOrganization()).
						key(LadderAllyData.FIELD_TREATY).			value(Treaty.ALLY).
						key(LadderAllyData.FIELD_RANK).				value(Ladder.getInstance().getAllyRank(ally.getId())).
						key(LadderAllyData.FIELD_POINTS).			value(ally.getPoints()).
						key(LadderAllyData.FIELD_ACHIEVEMENTS).		value(ally.getAchievementLevelsSum()).
						endObject();
				} else {
					json.key(LadderData.FIELD_SELF_DATA).value(false);
				}
			}
		}
		
		json.endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
