/*
Copyright 2010 Ghost

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

/**
 * 
 */
package fr.fg.server.action.player;

import java.util.Map;

import fr.fg.server.core.MessageTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

/**
 * @author Ghost
 *
 */
public class SetPlayerCard extends Action {

	/* (non-Javadoc)
	 * @see fr.fg.server.servlet.Action#execute(fr.fg.server.data.Player, java.util.Map, fr.fg.server.servlet.Session)
	 */
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// --------------- récupération des paramètres
		int playerId = (Integer) params.get("id");
		String playerDesc = (String) params.get("description");
		
		// --------------- préparation des objets
		Player target = DataAccess.getPlayerById(playerId);
		
		// --------------- vérifications
		//si le joueur n'est pas trouvé, on quitte
		if(target == null)
			throw new IllegalOperationException("Le joueur n'existe pas");
		
		synchronized (target.getLock()) {
			Player playerE = DataAccess.getEditable(target);
			playerE.setDescription(MessageTools.sanitizeHTML(playerDesc));
			playerE.save();			
		}		
		
		//TODO faire les mise à jour qui vont bien
		return Action.FORWARD_SUCCESS;
	}
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
