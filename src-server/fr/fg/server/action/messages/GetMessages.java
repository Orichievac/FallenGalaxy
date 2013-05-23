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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



import fr.fg.client.data.MessageBoxData;
import fr.fg.client.data.MessageData;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class GetMessages extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		long lastUpdate = (Long) params.get("update");
		
		JSONStringer json = new JSONStringer();
		json.object().
			key(MessageBoxData.FIELD_RECEIVED).	array();
		
		List<Message> messages = new LinkedList<Message>(player.getMessages());
		List<Message> sent = new ArrayList<Message>(player.getSentMessages());
		
		// Trie les messages par date de réception
		Collections.sort(messages, new Comparator<Message>() {
			public int compare(Message m1, Message m2) {
				if (m1.getDate() < m2.getDate())
					return -1;
				return 1;
			}
		});
		
		// Trie les messages par date d'envoi
		Collections.sort(sent, new Comparator<Message>() {
			public int compare(Message m1, Message m2) {
				if (m1.getDate() < m2.getDate())
					return -1;
				return 1;
			}
		});
		
		for (Message message : messages) {
			// Ne renvoie pas les messages qui ont déjà été envoyés
			if (message.getDate() <= lastUpdate)
				continue;
			if (message.getDeleted() == player.getId())
				continue;
			
			Player author = message.getAuthor();
			
			json.object().
				key(MessageData.FIELD_ID).				value(message.getId()).
				key(MessageData.FIELD_TITLE).			value(message.getTitle()).
				key(MessageData.FIELD_CONTENT).			value(message.getContent()).
				key(MessageData.FIELD_DATE).			value(message.getDate()).
				key(MessageData.FIELD_TARGET_PLAYER).	value(author != null ? author.getLogin() : "").
				key(MessageData.FIELD_TARGET_ALLY_TAG).	value(author != null ? author.getAllyTag() : "").
				key(MessageData.FIELD_TREATY).			value(author != null ? player.getTreatyWithPlayer(author) : Treaty.NEUTRAL).
				key(MessageData.FIELD_READ).			value(message.isOpened()).
				key(MessageData.FIELD_BOOKMARK).		value(message.isBookmarked()).
				endObject();
		}
		
		json.endArray();
		
		
		json.key(MessageBoxData.FIELD_SENT).array();
		
		for (Message message : sent) {
			// Ne renvoie pas les messages qui ont déjà été envoyés
			if (message.getDate() <= lastUpdate)
				continue;
			if (message.getDeleted() == player.getId())
				continue;			
			
			Player receiver = message.getReceiver();
			
			json.object().
				key(MessageData.FIELD_ID).				value(message.getId()).
				key(MessageData.FIELD_TITLE).			value(message.getTitle()).
				key(MessageData.FIELD_CONTENT).			value(message.getContent()).
				key(MessageData.FIELD_DATE).			value(message.getDate()).
				key(MessageData.FIELD_TARGET_PLAYER).	value(receiver.getLogin()).
				key(MessageData.FIELD_TARGET_ALLY_TAG).	value(receiver.getAllyTag()).
				key(MessageData.FIELD_TREATY).			value(player.getTreatyWithPlayer(receiver)).
				key(MessageData.FIELD_READ).			value(true).
				key(MessageData.FIELD_BOOKMARK).		value(false).
				endObject();
		}
		
		json.endArray();
		
		json.key(MessageBoxData.FIELD_LAST_UPDATE).	value(Utilities.now());
		
		json.endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
