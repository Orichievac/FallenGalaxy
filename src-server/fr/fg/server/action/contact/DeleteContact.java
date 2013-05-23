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

public class DeleteContact extends Action {
	
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Player toRemove = DataAccess.getPlayerByLogin((String) params.get("player"));
		
		if (toRemove == null)
			throw new IllegalOperationException("Ce joueur n'existe pas.");
		
		Contact contact = DataAccess.getContactByContact(player.getId(), toRemove.getId());
		
		if (contact == null)
			throw new IllegalOperationException("Ce joueur ne fait pas " +
					"partie de votre liste d'amis / ignorés.");
		
		contact.delete();
		//On ajoute un événement pour le joueur qui retire un autre joueur de sa liste d'amis
		Event event1 = new Event(Event.EVENT_PLAYER_REMOVE_FRIEND, Event.TARGET_PLAYER,
				player.getId(), 0, -1, -1, toRemove.getLogin());
		event1.save();
		//Et on ajoute un autre événement pour le joueur retiré
		Event event2 = new Event(Event.EVENT_PLAYER_REMOVED_FRIEND, Event.TARGET_PLAYER,
				toRemove.getId(), 0, -1, -1, player.getLogin());
		event2.save();
		UpdateTools.queueNewEventUpdate(player.getId());
		UpdateTools.queueNewEventUpdate(toRemove.getId());

		return ContactTools.getPlayerContacts(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
