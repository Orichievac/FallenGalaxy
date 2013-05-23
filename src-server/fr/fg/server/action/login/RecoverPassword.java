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

package fr.fg.server.action.login;

import java.util.Collections;
import java.util.HashMap;
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

public class RecoverPassword extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Temps en secondes, avant de pouvoir demander un nouvel email pour
	// récupérer son mot de passe
	public final static int EMAIL_FLOOD_LIMIT = 60;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private static Map<String, Long> passwordRecoverQueries;
	
	static {
		passwordRecoverQueries = Collections.synchronizedMap(
				new HashMap<String, Long>());
	}
	
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String email = (String) params.get("email");
		
		player = DataAccess.getPlayerByEmail(email);
		
		// Vérifie que le joueur existe
		if (player == null)
			throw new IllegalOperationException(
					Messages.getString("common.unknownPlayer"));
		
		// Vérifie qu'au moins 1 min s'est écoulée depuis la dernière demande
		Long query = passwordRecoverQueries.get(email);
		
		if (query != null && query + EMAIL_FLOOD_LIMIT > Utilities.now())
			throw new IllegalOperationException(
					Messages.getString("common.emailFloodLimit"));
		
		passwordRecoverQueries.put(email, Utilities.now());
		
		// Envoie un email pour récupérer le mot de passe
		String hash;
		do {
			hash = RandomStringUtils.randomAlphanumeric(32);
		} while (DataAccess.getPlayerByRecoverEmail(hash) != null);
		
		String newPassword = RandomStringUtils.randomAlphanumeric(8);
		
		try {
			Mailer.sendMail(email,
				Messages.getString("common.recoverEmailSubject"),
				Messages.getString("common.recoverEmailContent",
					player.getLogin(), newPassword, Config.getServerURL() +
					"recover/" + hash));
		} catch (MessagingException e) {
			LoggingSystem.getServerLogger().error("Could not send recover email.", e);
			throw new IllegalOperationException(
					Messages.getString("common.emailSentFailed"));
		}
		
		synchronized (player.getLock()) {
			Player newPlayer = DataAccess.getEditable(player);
			newPlayer.setRecoverEmail(hash);
			newPlayer.setRecoverPassword(Utilities.encryptPassword(newPassword));
			newPlayer.save();
		}
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
