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

public class TestFleetSkill extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "setfleetskill";
	
	// -------------------------------------------------------- ATTRIBUTS -- //

	private Fleet fleet;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	public void init() {
		fleet = new Fleet("fleet", 10, 10, 1, 4);
//		fleet.setBasicSkill(new Skill(Skill.SKILL_BOOSTERS, 0), 0);
		DataAccess.save(fleet);
	}
	
	public void testSetSkill() throws Exception {
		JSONObject answer = doRequest(URI, "fleet=" + fleet.getId() + "&skill=" + 50);
		System.out.println(answer.toString(2));
		System.out.println(DataAccess.getFleetById(fleet.getId()).getBasicSkill(0).getType());
	}
	
	@Override
	public void cleanUp() {
		fleet.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
