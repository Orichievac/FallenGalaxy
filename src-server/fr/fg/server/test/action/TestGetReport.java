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

public class TestGetReport extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "getreport";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testAllGood() throws Exception{
		setPlayer("Fedaykin");
		
		JSONObject answer = doRequest(URI, "report=10");
		System.out.println(answer.toString(2));
		assertEquals("success", answer.get("type"));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
