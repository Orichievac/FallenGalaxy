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

package fr.fg.server.servlet.export;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import fr.fg.server.core.Ladder;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;

public class IndividualLadderServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = -829012543129614671L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String name = request.getRequestURI();
		
		if (name.endsWith(".xml"))
			name = name.substring(0, name.length() - 4);
		
		try {
			// Décode les entités (ex : %20)
			String encoding = request.getCharacterEncoding();
			name = URLDecoder.decode(
					name.substring(name.lastIndexOf("/") + 1),
					encoding == null ? "ISO-8859-1" : encoding);
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}
		
		if (name.length() == 0) {
			write(request, response, "Syntaxe invalide. Utiliser l'URL : " +
				Config.getServerURL() +
				(request.getServletPath().contains("player") ?
					"ladder/player/nom joueur" :
					"ladder/ally/nom alliance"));
		} else {
			// Récupère le classement
			Element root = new Element("ladder"); 
			int lowerBound, upperBound;
			
			if (request.getServletPath().contains("player")) {
				// Classement joueurs
				Player referencePlayer = DataAccess.getPlayerByLogin(name);
				
				root.setAttribute("type", "players");
				root.setAttribute("reference", referencePlayer.getLogin());
				
				if (referencePlayer == null || referencePlayer.isAi() ||
						referencePlayer.getPoints() == 0) {
					write(request, response, "Nom de joueur invalide ou " +
							"compte non validé.");
					return;
				}
				
				int rank = Ladder.getInstance().getPlayerRank(referencePlayer.getId());
				lowerBound = rank;
				upperBound = rank;
				
				List<Integer> playersLadder =
					Ladder.getInstance().getPlayersLadder();
				
				int index = rank;
				for (int i = 0; i < playersLadder.size(); i++)
					if (playersLadder.get(i) == referencePlayer.getId()) {
						index = i;
						break;
					}
				
				int count = 0;
				
				// Récupère les 5 joueurs précédants le joueur demandé
				for (int i = index - 1; i >= 0; i--) {
					if (++count == 5)
						break;
					
					Player ladderPlayer =
						DataAccess.getPlayerById(playersLadder.get(i));
					
					if (ladderPlayer == null)
						break;
					
					if (ladderPlayer.isAi() || ladderPlayer.getPoints() == 0)
						continue;
					
					int playerRank = Ladder.getInstance().getPlayerRank(ladderPlayer.getId());
					root.addContent(0, createPlayerElement(root, ladderPlayer, playerRank));
					lowerBound = playerRank;
				}
				
				// Récupère le joueur demandé et les 5 suivants
				count = 0;
				for (int i = index; i < playersLadder.size(); i++) {
					if (++count == 6)
						break;
					
					Player ladderPlayer =
						DataAccess.getPlayerById(playersLadder.get(i));
					
					if (ladderPlayer == null)
						break;
					
					if (ladderPlayer.isAi() || ladderPlayer.getPoints() == 0)
						continue;

					int playerRank = Ladder.getInstance().getPlayerRank(ladderPlayer.getId());
					root.addContent(createPlayerElement(root, ladderPlayer, playerRank));
					upperBound = playerRank;
				}
			} else {
				// Classement alliances
				Ally referenceAlly = DataAccess.getAllyByNameOrTag(name);
				
				root.setAttribute("type", "allies");
				root.setAttribute("reference", referenceAlly.getName());
				
				if (referenceAlly == null || referenceAlly.isAi() ||
						referenceAlly.getPoints() == 0) {
					write(request, response, "Nom d'alliance invalide.");
					return;
				}
				
				int rank = Ladder.getInstance().getAllyRank(referenceAlly.getId());
				lowerBound = rank;
				upperBound = rank;
				
				List<Integer> alliesLadder =
					Ladder.getInstance().getAlliesLadder();
				
				int index = rank;
				for (int i = 0; i < alliesLadder.size(); i++)
					if (alliesLadder.get(i) == referenceAlly.getId()) {
						index = i;
						break;
					}
				
				int count = 0;
				
				// Récupère les 5 alliances précédants l'alliance demandée
				for (int i = index - 1; i >= 0; i--) {
					if (++count == 5)
						break;
					
					Ally ladderAlly =
						DataAccess.getAllyById(alliesLadder.get(i));
					
					// Teste si l'alliance a été supprimée
					if (ladderAlly == null) {
						alliesLadder.remove(i);
						i--;
						continue;
					}
					
					if (ladderAlly.isAi() || ladderAlly.getPoints() == 0)
						continue;
					
					int allyRank = Ladder.getInstance().getAllyRank(ladderAlly.getId());
					root.addContent(0, createAllyElement(root, ladderAlly, allyRank));
					lowerBound = allyRank;
				}
				
				// Récupère l'alliance demandée et les 5 suivantes
				count = 0;
				for (int i = index; i < alliesLadder.size(); i++) {
					if (++count == 6)
						break;
					
					Ally ladderAlly =
						DataAccess.getAllyById(alliesLadder.get(i));
					
					// Teste si l'alliance a été supprimée
					if (ladderAlly == null) {
						alliesLadder.remove(i);
						i--;
						continue;
					}
					
					if (ladderAlly.isAi() || ladderAlly.getPoints() == 0)
						continue;
					
					int allyRank = Ladder.getInstance().getAllyRank(ladderAlly.getId());
					root.addContent(createAllyElement(root, ladderAlly, allyRank));
					upperBound = allyRank;
				}
			}
			
			root.setAttribute("lowerBound", String.valueOf(lowerBound));
			root.setAttribute("upperBound", String.valueOf(upperBound));
			
			// Sortie en XML
			writeXML(response, root);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private Element createPlayerElement(Element root, Player player, int rank) {
		Element element = new Element("player");
		element.setAttribute("login", player.getLogin());
		element.setAttribute("ally", player.getAllyName());
		element.setAttribute("rank", String.valueOf(rank));
		element.setAttribute("level", String.valueOf(player.getLevel()));
		element.setAttribute("points", String.valueOf(player.getPoints()));
		element.setAttribute("achievements", String.valueOf(player.getAchievementLevelsSum()));
		return element;
	}
	
	private Element createAllyElement(Element root, Ally ally, int rank) {
		Element element = new Element("ally");
		element.setAttribute("name", ally.getName());
		element.setAttribute("members", String.valueOf(ally.getMembers().size()));
		element.setAttribute("organization", ally.getOrganization());
		element.setAttribute("rank", String.valueOf(rank));
		element.setAttribute("points", String.valueOf(ally.getPoints()));
		element.setAttribute("achievements", String.valueOf(ally.getAchievementLevelsSum()));
		return element;
	}
}
