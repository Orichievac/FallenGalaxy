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

import java.util.List;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.test.action.TestAction;

public class TestSendMessage extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "messages/add";
	
	// -------------------------------------------------------- ATTRIBUTS -- //

	private int targetMessagesCount, playerMessagesCount;
	private Player target;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		target = DataAccess.getPlayerByLogin("Fedaykin");
		targetMessagesCount = DataAccess.getMessagesByPlayer(target.getId()).size();
		playerMessagesCount = DataAccess.getMessagesByPlayer(getPlayer().getId()).size();
	}
	
	public void testSelfMessage() throws Exception {
		Player player = getPlayer();
		JSONObject answer = doRequest(URI,
				"player=" + player.getLogin() + "&title=test&content=test");
		assertEquals(ERROR, answer.get("type"));
		assertEquals(playerMessagesCount,
				DataAccess.getMessagesByPlayer(player.getId()).size());
		System.out.println(answer.toString(2));
	}
	
	public void testInvalidTarget() throws Exception {
		JSONObject answer = doRequest(URI,
				"player=xxxxxxxx&title=test&content=test");
		assertEquals(ERROR, answer.get("type"));
	}
	
	public void testSendMessage() throws Exception {
		JSONObject answer = doRequest(URI,
				"player=" + target.getLogin() + "&title=test&content=test");
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(targetMessagesCount + 1,
				DataAccess.getMessagesByPlayer(target.getId()).size());
		
		getLastPlayerMessage(target).delete();
	}
	
	public void testFlood() throws Exception {
		JSONObject answer = doRequest(URI,
				"player=" + target.getLogin() + "&title=test&content=test");
		assertEquals(SUCCESS, answer.get("type"));
		answer = doRequest(URI,
				"player=" + target.getLogin() + "&title=test&content=test");
		assertEquals(ERROR, answer.get("type"));
		
		getLastPlayerMessage(target).delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private Message getLastPlayerMessage(Player player) {
		List<Message> messages = player.getMessages();
		if (messages.size() > 0)
			return messages.get(messages.size() - 1);
		return null;
	}
}
