/*
Copyright 2010 Thierry

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

package fr.fg.server.action.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Mailer;
import fr.fg.server.util.Utilities;

public class SendOpeningMail extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		if(Config.getOpeningDate()<Utilities.now())
		{
			new OpeningMailThread().start(); //L'envoie de mail commence
			
			return new JSONStringer().value("Envoie des mails en cours").toString();
		}
		
		return new JSONStringer().value("Erreur: Le serveur n'est pas encore ouvert").toString();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private class OpeningMailThread extends Thread {
		
		public void run() {
			List<Player> players= new ArrayList<Player>(DataAccess.getAllPlayers());
			LoggingSystem.getServerLogger().info("Début de l'envoie des mails de notification de l'ouverture du nouveau serveur");
			int count = 0;
			for(Player player : players) {
				if(!player.isAi() && player.getEmail().length()>0) {
					count++;
					try {
						Mailer.sendMail(player.getEmail(),
								Messages.getString("common.emailOpeningSubject",player.getLogin()),
								Messages.getString("common.emailOpening", player.getLogin(),Config.getServerName(), Config.getServerURL()));
					} catch (MessagingException e) {
						count--;
						LoggingSystem.getServerLogger().error("Could not send opening server notification email to "+
								player.getLogin()+".", e);
					}
				}
			}
			LoggingSystem.getServerLogger().info("Fin de l'envoie des mails de notification de l'ouverture du nouveau serveur ("+count+" emails envoyés)");
		}
	}
}
