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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateManager;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Player;

public class PollingServlet extends AjaxServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		CONTINUATION_TIMEOUT_DEFAULT = 30000,
		CONTINUATION_TIMEOUT_PROXY = 10000;
	
	private static final long serialVersionUID = 5414459286539449765L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

    public void process(HttpServletRequest request,
			HttpServletResponse response, int method, Player player) {
    	if (method != Action.METHOD_POST) {
    		write(request, response, ActionForward.error("Use POST only."));
    		return;
    	}
    	
    	if (player == null) {
    		write(request, response, ActionForward.disconnected());
    		return;
    	}
    	
		// Vérifie que sa clé de sécurité est valide
		if (request.getParameter(SECURITY_KEY) == null ||
				!player.getSecurityKey().equals(request.getParameter(SECURITY_KEY))) {
			write(request, response, ActionForward.error("Invalid security key."));
			return;
		}
		
		List<Update> updates =
			UpdateManager.getInstance().getUpdatesByPlayer(player.getId());
		
		if (updates.size() == 0) {
			Continuation continuation = ContinuationSupport.getContinuation(request);
			
			if (continuation.isInitial()) {
				continuation.setTimeout(player.isSettingsOptimizeConnection() ?
					CONTINUATION_TIMEOUT_PROXY : CONTINUATION_TIMEOUT_DEFAULT);
				continuation.suspend();
				player.setContinuation(continuation);
				return;
			}
		}
		
		player.setContinuation(null);
		
		try {
			String json = UpdateTools.formatUpdates(player, updates);
			updates.clear();
			
			write(request, response, ActionForward.success(json));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	// ------------------------------------------------- METHODES PRIVEES -- //
}
