/*
Copyright 2010 Thierry Chevalier

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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import fr.fg.server.core.ConnectionManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.Utilities;

public class GetOnlinePlayersServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	private static final long serialVersionUID = -829012543129614671L;
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		ConnectionManager cm = ConnectionManager.getInstance();
		Set<Integer> playerIds = cm.getConnectedPlayers();
		Element root = new Element("players");
		for(Integer playerId : playerIds)
		{
			Player player = DataAccess.getPlayerById(playerId);
			Element playerElement = new Element("player");
			Element login = new Element("login");
			login.setText(player.getLogin());
			Element ping = new Element("ping");
			ping.setText(String.valueOf(Utilities.now()-player.getLastPing()));
			playerElement.addContent(login);
			playerElement.addContent(ping);
			root.addContent(playerElement);
		}
		writeXML(response,root);
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
