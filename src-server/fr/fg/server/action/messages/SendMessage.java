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

package fr.fg.server.action.messages;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.MessageTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.Utilities;

public class SendMessage extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		String targetName = (String) params.get("player");
		String title = Badwords.parse(MessageTools.tidyHTML((String) params.get("title")));
		String content = (String) params.get("content");
		
		// Récupère le dernier message envoyé par le joueur
		List<Message> messages = new LinkedList<Message>(player.getMessages());
		
		// Trie les messages par date de réception
		Collections.sort(messages, new Comparator<Message>() {
			public int compare(Message m1, Message m2) {
				if (m1.getDate() < m2.getDate())
					return -1;
				return 1;
			}
		});
		
		Message lastMessage = null;
		
		if (messages.size() > 0)
			lastMessage = messages.get(0);
		
		// Vérifie que 10 secondes se sont écoulées depuis le dernier message
		// envoyé par le joueur
		if (lastMessage != null && lastMessage.getDate() +
				GameConstants.MESSAGES_FLOOD_LIMIT > Utilities.now())
			throw new IllegalOperationException(
					Messages.getString("messages.excessMessages",
							GameConstants.MESSAGES_FLOOD_LIMIT));
		
		// Vérifie que le destinataire existe
		Player target = DataAccess.getPlayerByLogin(targetName);
		if (target == null)
			throw new IllegalOperationException(Messages.getString("messages.unknownUser"));
		
		// Vérifie que le destinaire n'est pas un PNJ
		if (target.isAi())
			throw new IllegalOperationException(
					"Vous ne pouvez pas envoyer de message à un PNJ.");
		
		// Teste si le joueur s'envoie un message à lui-même
		if (!Config.isDebug() && player.getId() == target.getId())
			throw new IllegalOperationException(
					Messages.getString("messages.selfMessage"));
		
		// Teste si le destinataire a ignoré l'expéditeur
		if (target.isIgnoring(player.getId()) && !player.hasRight(Player.MODERATOR))
			throw new IllegalOperationException("Ce joueur ne souhaite pas recevoir de messages de votre part.");
		
		// Vérifie que le message ne contient pas l'ekey du joueur
		if (player.getEkey().length() > 0 && content.contains(player.getEkey()))
			throw new IllegalOperationException("Ne transmettez pas votre clé d'export à d'autres joueurs.");
		
		// Parse le contenu du message
		content = Badwords.parse(content);
		content = MessageTools.sanitizeHTML(content);
		
		// Envoie le messsage
		Message message = new Message(
				title, content, player.getId(), target.getId());
		MessageTools.sendMessage(message);
		
		return UpdateTools.formatUpdates(
			player
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
