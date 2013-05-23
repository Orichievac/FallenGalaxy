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

package fr.fg.server.test.action.settings;

import org.json.JSONObject;

import fr.fg.server.test.action.TestAction;
import fr.fg.server.util.Utilities;

public class TestSetPassword extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "setpassword";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testInvalidPassword() throws Exception {
		JSONObject answer = doRequest(URI, "password=toto&newpassword=titi");
		assertEquals("error", answer.get("type"));
	}

	public void testValidPassword() throws Exception {
		String password = "****";
		
		JSONObject answer = doRequest(URI, "password=" + password + "&newpassword=titi");
		assertEquals("success", answer.get("type"));
		
		assertEquals(Utilities.encryptPassword("titi"), getPlayer().getPassword());
		
		// Reset le password a sa valeur initiale
		doRequest(URI, "password=titi&newpassword=" + password);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
