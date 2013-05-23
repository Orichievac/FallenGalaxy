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

import java.util.Map;


import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class BookmarkMessage extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		if (!player.hasRight(Player.PREMIUM))
			throw new IllegalOperationException("Vous devez avoir un compte " +
					"premium pour accèder à cette fonctionnalité.");
		
		int idMessage = (Integer) params.get("id");
		Message message = DataAccess.getMessageById(idMessage);
		
		// Vérifie que le message existe et est adressé au joueur
		if (message == null || message.getIdPlayer() != player.getId())
			throw new IllegalOperationException(
					"Vous n'êtes pas autorisé à conserver ce message.");
		
		if (player.getBookmarkedMessagesCount() >= 25 && !player.isAdministrator())
			throw new IllegalOperationException(
					"Vous ne pouvez conserver plus de 25 messages." +
					"Supprimez des messages archivés pour pouvoir en " +
					"conserver de nouveaux.");
		
		synchronized (message.getLock()) {
			message = DataAccess.getEditable(message);
			message.setBookmarked(true);
			DataAccess.save(message);
		}
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
