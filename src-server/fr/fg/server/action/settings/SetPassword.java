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

package fr.fg.server.action.settings;

import java.util.Map;


import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class SetPassword extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		String password = (String) params.get("password");
		String newPassword = (String) params.get("newpassword");
		
		// Vérifie que le compte n'est pas le compte invité
		if (player.getLogin().equalsIgnoreCase("guest"))
			throw new IllegalOperationException(
					"Le mot de passe du compte invité ne peut être changé !");
		
		// Vérifie que le mot de passe est valide
		if (!Utilities.encryptPassword(password).equals(player.getPassword()))
			throw new IllegalOperationException("Mot de passe invalide !");
		
		// Modifie le mot de passe du joueur
		synchronized (player.getLock()) {
			Player newPlayer = DataAccess.getEditable(player);
			newPlayer.setPassword(Utilities.encryptPassword(newPassword));
			DataAccess.save(newPlayer);
		}
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
