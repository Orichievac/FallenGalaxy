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

import fr.fg.server.test.action.TestAction;
import fr.fg.server.util.Utilities;

public class TestGetMessages extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "messages/get";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testGetAllMessages() throws Exception {
		setPlayer("Veldryn");
		JSONObject answer = doRequest(URI, "update=0");
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(getPlayer().getMessages().size(), answer.getJSONObject(
				"data").getJSONArray("messages").length());
		System.out.println(answer.toString(2));
	}

	public void testGetNoMessages() throws Exception {
		setPlayer("Veldryn");
		JSONObject answer = doRequest(URI, "update=" + Utilities.now());
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(0, answer.getJSONObject(
				"data").getJSONArray("messages").length());
		System.out.println(answer.toString(2));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
