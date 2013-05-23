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

public class TestDiscardResources extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String URI = "systems/discardresources";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testDiscardResources() throws Exception {
		setPlayer("veldryn");
		JSONObject answer = doRequest(URI, "system=" +
				DataAccess.getSystemsByOwner(getPlayer().getId()).get(0).getId() + "&" +
				"resource0=100000&resource1=0&resource2=0&resource3=0&" +
				"resource4=0&resource5=0&resource6=0&resource7=0");
		System.out.println(answer.toString(2));
		assertEquals("success", answer.get("type"));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
