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

package fr.fg.server.test.action.system;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.test.action.TestAction;

public class TestSetSystemName extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String URI = "systems/setname";
	
	// -------------------------------------------------------- ATTRIBUTS -- //

	private int idSystem;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void init() {
		idSystem = DataAccess.getSystemsByOwner(getPlayer().getId()).get(0).getId();
	}
	
	public void testBlacklistedName() throws Exception {
		JSONObject answer = doRequest(URI, "system=" + idSystem + "&name=connard");
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testValidName() throws Exception {
		JSONObject answer = doRequest(URI, "system=" + idSystem + "&name=New Earth");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
