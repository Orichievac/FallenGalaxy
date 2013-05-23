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

package fr.fg.server.action.contact;

import java.util.Map;


import fr.fg.server.core.ContactTools;
import fr.fg.server.data.Contact;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.core.UpdateTools;

public class AddContact extends Action{

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Player toAdd = DataAccess.getPlayerByLogin((String) params.get("player"));
		String type = (String) params.get("type");
		
		if (toAdd == null)
			throw new IllegalOperationException("Ce joueur n'existe pas.");
		
		if (toAdd.getLogin().equals(player.getLogin()))
			throw new IllegalOperationException("Vous ne pouvez pas " +
					"vous ajouter vous même à votre liste.");
		
		Contact contact = DataAccess.getContactByContact(player.getId(), toAdd.getId());
		
		if (contact != null)
			throw new IllegalOperationException("Ce joueur fait déjà partie de vos " +
				(contact.getType().equals("friend") ? "amis." : "joueurs ignorés."));
		
		if (type.equals(Contact.TYPE_FRIEND) &&
				!player.hasRight(Player.PREMIUM) &&
				DataAccess.getContactByContact(toAdd.getId(), player.getId()) == null)
			throw new IllegalOperationException("Il faut un compte premium pour pouvoir ajouter un nouvel ami.");
		
		DataAccess.save(new Contact(player.getId(), toAdd.getId(), type));
		
		if(type.equals(Contact.TYPE_FRIEND))
		{
			//On ajoute un événement pour le joueur qui ajoute un autre joueur dans sa liste d'amis
			Event event1 = new Event(Event.EVENT_PLAYER_ADD_FRIEND, Event.TARGET_PLAYER,
					player.getId(), 0, -1, -1, toAdd.getLogin());
			event1.save();
			//Et on ajoute un autre événement pour le joueur ajouté
			Event event2 = new Event(Event.EVENT_PLAYER_ADDED_FRIEND, Event.TARGET_PLAYER,
					toAdd.getId(), 0, -1, -1, player.getLogin());
			event2.save();
			UpdateTools.queueNewEventUpdate(player.getId());
			UpdateTools.queueNewEventUpdate(toAdd.getId());
		}
		
		return ContactTools.getPlayerContacts(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
