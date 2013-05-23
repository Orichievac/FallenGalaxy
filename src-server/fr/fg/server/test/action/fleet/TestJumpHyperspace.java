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

import java.util.List;

import org.json.JSONObject;

import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StellarObject;
import fr.fg.server.test.action.TestAction;

public class TestJumpHyperspace extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "hyperspace";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Area areaIn, areaOutNear, areaOutFar, areaOutOfRange;
	private StellarObject gate;
	private Fleet fleet;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		areaIn = DataAccess.getAreaById(1);
		
		List<Integer> neighbours = areaIn.getNeighbours();
		for (Integer idArea : neighbours) {
			Area area = DataAccess.getAreaById(idArea);
			if (areaOutNear == null && !area.isNeighbour(areaIn.getId()))
				areaOutNear = area;
			else if (areaOutFar == null && area.isNeighbour(areaIn.getId()))
				areaOutFar = area;
		}
		for (Area area : DataAccess.getAllAreas())
			if (!neighbours.contains(area) && areaIn != area) {
				areaOutOfRange = area;
				break;
			}
		
		gate = areaIn.getGates().get(0);
		fleet = new Fleet("test", gate.getX() - 2, gate.getY() - 2, getPlayer().getId(), areaIn.getId());
		DataAccess.save(fleet);
	}
	
	public void testInvalidFleet() throws Exception {
		JSONObject answer = doRequest(URI, "id=100000000&gate=" + gate.getId() + "&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testInvalidGate() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=100000000&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testWrongGate() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + areaOutNear.getGates().get(0).getId() + "&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testInvalidArea() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=100000000");
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testAreaOutOfRange() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaOutOfRange.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testInvalidOwner() throws Exception {
		setPlayer("Veldryn");
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testSameArea() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaIn.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testNotEnoughMovement() throws Exception {
		fleet = DataAccess.getEditable(fleet);
		fleet.setMovement(0, true);
		DataAccess.save(fleet);
		
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(ERROR, answer.get("type"));
	}

	public void testNearJump() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(areaOutNear.getName(), answer.getJSONObject("data").getString("areaName"));
		
		fleet = DataAccess.getFleetById(fleet.getId());
		assertEquals(true, fleet.isInHyperspace());
		assertEquals(areaOutNear.getId(), fleet.getHyperspaceIdArea());
	}

	public void testFarJump() throws Exception {
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaOutFar.getId());
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(areaOutFar.getName(), answer.getJSONObject("data").getString("areaName"));
		
		fleet = DataAccess.getFleetById(fleet.getId());
		assertEquals(true, fleet.isInHyperspace());
		assertEquals(areaOutFar.getId(), fleet.getHyperspaceIdArea());
	}

	public void testSkilledNearJump() throws Exception {
		fleet = DataAccess.getEditable(fleet);
		fleet.setBasicSkill(new Skill(Skill.SKILL_TRACKER, 2), 0);
		DataAccess.save(fleet);
		
		JSONObject answer = doRequest(URI, "id=" + fleet.getId() + "&gate=" + gate.getId() + "&area=" + areaOutNear.getId());
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		assertEquals(areaOutNear.getName(), answer.getJSONObject("data").getString("areaName"));
		
		fleet = DataAccess.getFleetById(fleet.getId());
		assertEquals(true, fleet.isInHyperspace());
		assertEquals(areaOutNear.getId(), fleet.getHyperspaceIdArea());
	}

	public void cleanUp() {
		fleet.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
