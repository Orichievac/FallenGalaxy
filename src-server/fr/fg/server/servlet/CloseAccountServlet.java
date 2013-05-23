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

package fr.fg.server.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.fg.server.core.ConnectionManager;
import fr.fg.server.core.PlayerTools;
import fr.fg.server.core.UpdateManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;

@SuppressWarnings("serial")
public class CloseAccountServlet extends HttpServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
    public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
    	doGet(request, response);
    }
    
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String hash = request.getRequestURI();
		hash = hash.substring(hash.lastIndexOf("/") + 1);
		
		if (hash.length() > 0) {
			Player player = DataAccess.getPlayerByCloseAccountHash(hash);
			
			if (player != null) {
				if (ConnectionManager.getInstance().isConnected(player.getId()))
					ConnectionManager.getInstance().disconnect(player);
				
				PlayerTools.closeAccount(player, false);
				
				request.setAttribute("message",
					"Votre compte " + player.getLogin() + " a été fermé." +
					"<br/><a href=\"" + Config.getServerURL() + "\">" +
					Messages.getString("common.back") + "</a>");
			} else {
				request.setAttribute("message",
					"La clé de clôture du compte est invalide." +
					"<br/><a href=\"" + Config.getServerURL() + "\">" +
					Messages.getString("common.back") + "</a>");
			}
		} else {
			request.setAttribute("message", 
				"La clé de clôture du compte est invalide." +
				"<br/><a href=\"" + Config.getServerURL() + "\">" +
				Messages.getString("common.back") + "</a>");
		}
		
		// Déclenche les mises à jour en attente sur le thread
		UpdateManager.getInstance().flushUpdates();
		
		forward(Config.getMessageServlet(), request, response);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	// REMIND jgottero faire une super servlet avec cette méthode
	// Redirige la requête sur une autre servlet
	private void forward(String uri, HttpServletRequest request,
			HttpServletResponse response) {
		RequestDispatcher dispatcher =
			getServletContext().getRequestDispatcher(uri);
		
		try {
			dispatcher.forward(request, response);
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error(
					"Could not forward to URI : '" + uri + "'.", e);
		}
	}
}
