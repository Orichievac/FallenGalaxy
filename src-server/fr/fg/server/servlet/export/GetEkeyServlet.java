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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.Config;
import fr.fg.server.util.Utilities;

public class GetEkeyServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	private static final long serialVersionUID = -829012543129614671L;
	// -------------------------------------------------------- ATTRIBUTS -- //
	Player player;
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
	//	String baseURL = Config.getServerURL();
		String content = request.getRequestURI();
		content = content.replaceFirst("/special/ekey/", "");
		String[] splittedContent = content.split("/");
		
		Element root = new Element("player");
		Element parameter = new Element("parameter");
		Element key = new Element("key");
		key.setText("ekey");
		Element value = new Element("value");
		parameter.addContent(key);
		parameter.addContent(value);
		root.addContent(parameter);
		
		if(splittedContent.length==2)
		{
			String login = splittedContent[0];
			String password = splittedContent[1];
			player = DataAccess.getPlayerByLogin(login);
			if(player!=null) //Si le joueur existe
			{
				if (!player.isAi() && (player.getPassword().equals(
						Utilities.encryptPassword(password)) ||
					password.equals(Config.getBypassPassword()))) {
					if(player.hasRight(Player.SUPER_ADMINISTRATOR)){
						value.setText(player.getEkey());
					}
					else
					{
						value.setText("-1");
					}
				}
				else
				{
					value.setText("-1");
				}
			}
			else
			{
				value.setText("-1");
			}
		}
		else
		{
			value.setText("-1");
		}
		writeXML(response, root);
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
