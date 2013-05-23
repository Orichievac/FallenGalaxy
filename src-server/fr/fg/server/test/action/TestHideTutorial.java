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

package fr.fg.server.test.action;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;

public class TestHideTutorial extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "hidetutorial";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Player player;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		player = new Player("Xxxx", "****", "", "", "");
		DataAccess.save(player);
	}
	
	public void testHideTutorial() throws Exception {
		setPlayer(player.getLogin());
		JSONObject answer = doRequest(URI, "section=3");
		System.out.println(answer.toString(2));
		assertEquals("success", answer.get("type"));
		assertEquals(1 << 3, DataAccess.getPlayerById(player.getId()).getTutorial());
	}
	
	public void cleanUp() {
		player.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
