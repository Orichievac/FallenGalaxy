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

package fr.fg.server.test.action.fleet;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.test.action.TestAction;

public class TestMoveFleet extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "movefleets";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet fleet1, fleet2;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		setPlayer("JayJay");
		fleet1 = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		DataAccess.save(fleet1);
		fleet2 = new Fleet("Fleet", 3, 2, getPlayer().getId(), 1);
		DataAccess.save(fleet2);
	}
	
	// REMIND jgottero ajouter tests manquants
	
	public void testMoveToSameTile() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet1.getId() +
				"&x=" + fleet2.getX() + "&y=" + fleet2.getY());
		
		assertEquals("error", answer.get("type"));
	}
	
	public void testValidMove() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet1.getId() + "&x=1&y=1");
		
		assertEquals("success", answer.get("type"));
	}
	
	public void cleanUp() {
		fleet1.delete();
		fleet2.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
