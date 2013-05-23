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

package fr.fg.server.test.action.messages;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Message;
import fr.fg.server.test.action.TestAction;

public class TestDeleteMessage extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "messages/delete";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int messagesCount;
	private int sender, receiver;
	private Message message;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		sender = 1;
		receiver = 2;
		message = new Message("Test", "Message de test", sender, receiver);
		DataAccess.save(message);
		messagesCount = DataAccess.getMessagesByPlayer(receiver).size();
	}
	
	public void testDeleteMissingMessage() throws Exception {
		setPlayer(DataAccess.getPlayerById(receiver).getLogin());
		JSONObject answer = doRequest(URI, "id=100000000");
		assertEquals(ERROR, answer.get("type"));
		assertEquals(messagesCount,
				DataAccess.getMessagesByPlayer(receiver).size());
	}
	
	public void testUnauthorizedDeleteMessage() throws Exception {
		setPlayer(DataAccess.getPlayerById(sender).getLogin());
		JSONObject answer = doRequest(URI, "id=" + message.getId());
		assertEquals(ERROR, answer.get("type"));
		assertEquals(messagesCount,
				DataAccess.getMessagesByPlayer(receiver).size());
	}
	
	public void testDeleteMessage() throws Exception {
		setPlayer(DataAccess.getPlayerById(receiver).getLogin());
		JSONObject answer = doRequest(URI, "id=" + message.getId());
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(messagesCount - 1,
				DataAccess.getMessagesByPlayer(receiver).size());
	}
	
	public void cleanUp() {
		message.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
