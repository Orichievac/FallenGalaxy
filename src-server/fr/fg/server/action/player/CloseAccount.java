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

package fr.fg.server.action.player;

import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Mailer;
import fr.fg.server.util.Utilities;

public class CloseAccount extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String password = (String) params.get("password");
		String reason = (String) params.get("reason");
		
		if (!player.getPassword().equals(Utilities.encryptPassword(password)))
			throw new IllegalOperationException("Mot de passe invalide.");
		
		// Génère un hash pour confirmer la fermeture du compte
		String hash;
		do {
			hash = RandomStringUtils.randomAlphanumeric(32);
		} while (DataAccess.getPlayerByCloseAccountHash(hash) != null);
		
		try {
			Mailer.sendMail(player.getEmail(),
				Messages.getString("common.closeAccountSubject"),
				Messages.getString("common.closeAccountContent",
					player.getLogin(),
					Config.getServerURL() + "delete/" + hash));
		} catch (MessagingException e) {
			LoggingSystem.getServerLogger().error("Could not send recover email.", e);
			throw new IllegalOperationException(
					Messages.getString("common.emailSentFailed"));
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.setCloseAccountHash(hash);
			player.setCloseAccountReason(reason);
			player.save();
		}
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
