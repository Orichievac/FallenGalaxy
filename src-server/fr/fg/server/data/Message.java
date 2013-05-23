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

package fr.fg.server.data;

import fr.fg.server.data.base.MessageBase;
import fr.fg.server.util.Utilities;

public class Message extends MessageBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Message() {
		// Nécessaire pour la construction par réflection
	}
	
	public Message(String title, String content, int idAuthor, int idPlayer) {
		this(title, content, idAuthor, idPlayer, Utilities.now());
	}
	
	public Message(String title, String content, int idAuthor, int idPlayer,
			long date) {
		setTitle(title);
		setContent(content);
		setDate(date);
		setIdAuthor(idAuthor);
		setIdPlayer(idPlayer);
		setDeleted(0);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Player getAuthor() {
		if (getIdAuthor() == 0)
			return null;
		return DataAccess.getPlayerById(getIdAuthor());
	}
	
	public Player getReceiver() {
		if (getIdPlayer() == 0)
			return null;
		return DataAccess.getPlayerById(getIdPlayer());
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
