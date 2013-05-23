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
import java.util.ArrayList;
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

public class LadderServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = -829012543129614671L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String range = request.getRequestURI();
		
		if (range.endsWith(".xml"))
			range = range.substring(0, range.length() - 4);
		
		try {
			// Décode les entités (ex : %20)
			String encoding = request.getCharacterEncoding();
			range = URLDecoder.decode(
				range.substring(range.lastIndexOf("/") + 1),
				encoding == null ? "ISO-8859-1" : encoding);
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}
		
		if (range.length() == 0 || !range.contains("-")) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		// Parse l'intervalle du classement demandé
		String[] numbers = range.split("-");
		
		if (numbers.length != 2) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		int lowerBound, upperBound;
		
		try {
			lowerBound = Integer.parseInt(numbers[0]);
		} catch (Exception e) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		try {
			upperBound = Integer.parseInt(numbers[1]);
		} catch (Exception e) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		if (lowerBound > upperBound ||
				lowerBound - upperBound > 500 ||
				lowerBound < 1) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		// Récupère le classement
		Element root = new Element("ladder");
		root.setAttribute("lowerBound", String.valueOf(lowerBound));
		root.setAttribute("upperBound", String.valueOf(upperBound));
		
		if (request.getRequestURI().contains("players")) {
			root.setAttribute("type", "players");
			
			List<Integer> playersLadder =
				Ladder.getInstance().getPlayersLadder();
			
			long previousPoints = -1;
			int previousRank = -1;
			
			for (int i = lowerBound - 1; i <= upperBound - 1 &&
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
				
				Element element = new Element("player");
				element.setAttribute("login", ladderPlayer.getLogin());
				element.setAttribute("ally", ladderPlayer.getAllyName());
				element.setAttribute("rank", String.valueOf(previousRank));
				element.setAttribute("level", String.valueOf(ladderPlayer.getLevel()));
				element.setAttribute("points", String.valueOf(ladderPlayer.getPoints()));
				element.setAttribute("achievements", String.valueOf(ladderPlayer.getAchievementLevelsSum()));
				root.addContent(element);
			}
		} else {
			root.setAttribute("type", "allies");
			
			List<Integer> alliesLadder =
				new ArrayList<Integer>(Ladder.getInstance().getAlliesLadder());
			
			long previousPoints = -1;
			int previousRank = -1;
			
			for (int i = lowerBound - 1; i <= upperBound - 1 &&
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
				
				Element element = new Element("ally");
				element.setAttribute("name", ladderAlly.getName());
				element.setAttribute("members", String.valueOf(ladderAlly.getMembers().size()));
				element.setAttribute("organization", ladderAlly.getOrganization());
				element.setAttribute("rank", String.valueOf(previousRank));
				element.setAttribute("points", String.valueOf(ladderAlly.getPoints()));
				element.setAttribute("achievements", String.valueOf(ladderAlly.getAchievementLevelsSum()));
				root.addContent(element);
			}
		}
		
		// Sortie en XML
		writeXML(response, root);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void writeInvalidSyntax(HttpServletRequest request,
			HttpServletResponse response) {
		write(request, response, "Syntaxe invalide. Utiliser l'URL : " +
			Config.getServerURL() +
			(request.getServletPath().contains("player") ?
				"ladder/players/début-fin" :
				"ladder/allies/début-fin"));
	}
}
