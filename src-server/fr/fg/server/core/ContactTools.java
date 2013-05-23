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

package fr.fg.server.core;

import java.util.List;

import fr.fg.client.data.ContactData;
import fr.fg.server.data.Contact;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.util.JSONStringer;

public class ContactTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getPlayerContacts(JSONStringer json, Player player) {
		if (json == null)
			json = new JSONStringer();
		
		List<Contact> contacts = DataAccess.getContactsByPlayer(player.getId());
		json.array();
		
		synchronized (contacts) {
			for (Contact contact : contacts) {
				boolean mutual = false;
				boolean connected = false;
				
				if (contact.getType().equals(Contact.TYPE_FRIEND)) {
					Contact friendContact = DataAccess.getContactByContact(
							contact.getIdContact(), player.getId());
					
					if (friendContact != null && friendContact.getType(
							).equals(Contact.TYPE_FRIEND)) {
						mutual = true;
					}
					
					connected = ConnectionManager.getInstance(
							).isConnected(contact.getIdContact());
				}
				
				Player contactPlayer = contact.getContact();
				
				json.object().
					key(ContactData.FIELD_NAME).		value(contactPlayer.getLogin()).
					key(ContactData.FIELD_TYPE).		value(contact.getType()).
					key(ContactData.FIELD_MUTUAL).		value(mutual).
					key(ContactData.FIELD_CONNECTED).	value(connected).
					key(ContactData.FIELD_ALLY_TAG).	value(contactPlayer.getAllyTag()).
					endObject();
			}
		}

		List<Contact> requests = DataAccess.getContactsByContact(player.getId());
		
		synchronized (requests) {
			for (Contact request : requests) {
				if (request.getType().equals(Contact.TYPE_FRIEND)) {
					Contact friendContact = DataAccess.getContactByContact(
							player.getId(), request.getIdPlayer());
					
					if (friendContact != null && friendContact.getType(
							).equals(Contact.TYPE_FRIEND)) {
						continue;
					}
					
					boolean connected = ConnectionManager.getInstance(
						).isConnected(request.getIdPlayer());
					
					Player contactPlayer = request.getPlayer();
					
					json.object().
						key(ContactData.FIELD_NAME).		value(contactPlayer.getLogin()).
						key(ContactData.FIELD_TYPE).		value("request").
						key(ContactData.FIELD_MUTUAL).		value(false).
						key(ContactData.FIELD_CONNECTED).	value(connected).
						key(ContactData.FIELD_ALLY_TAG).	value(contactPlayer.getAllyTag()).
						endObject();
				}
			}
		}
		
		json.endArray();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
