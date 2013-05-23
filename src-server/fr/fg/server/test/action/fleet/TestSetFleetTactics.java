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

import java.util.Arrays;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.test.action.TestAction;

public class TestSetFleetTactics extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "settactics";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet[] fleets;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		setPlayer("JayJay");
		fleets = new Fleet[20];
		Fleet fleet;
		
		fleet = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		fleet.setSlot(new Slot(Ship.RECON, 100, false), 0);
		DataAccess.save(fleet);
		fleets[0] = fleet;
		
		fleet = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		fleet.setSlot(new Slot(Ship.RECON, 100, false), 0);
		fleet.setSlot(new Slot(Ship.CORSAIR, 100, true), 1);
		fleet.setSlot(new Slot(Ship.FURY, 100, false), 2);
		DataAccess.save(fleet);
		fleets[1] = fleet;
		
		fleet = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		fleet.setSlot(new Slot(Ship.RECON, 100, true), 0);
		fleet.setSlot(new Slot(Ship.MAMMOTH, 100, true), 1);
		fleet.setSlot(new Slot(Ship.FURY, 100, false), 2);
		DataAccess.save(fleet);
		fleets[2] = fleet;
		
		fleet = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		fleet.setSlot(new Slot(Ship.RECON, 100, true), 0);
		fleet.setSlot(new Slot(Ship.MAMMOTH, 100, true), 1);
		fleet.setSlot(new Slot(Ship.FURY, 100, false), 2);
		fleet.setSlot(new Slot(Ship.MULE, 100, false), 3);
		DataAccess.save(fleet);
		fleets[3] = fleet;
		
		fleet = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		fleet.setSlot(new Slot(Ship.MAMMOTH, 100, false), 1);
		fleet.setSlot(new Slot(Ship.MULE, 100, true), 3);
		DataAccess.save(fleet);
		fleets[4] = fleet;
		
		fleet = new Fleet("Fleet", 2, 2, getPlayer().getId(), 1);
		fleet.setSlot(new Slot(Ship.RECON, 100, false), 0);
		fleet.setSlot(new Slot(Ship.CORSAIR, 100, true), 1);
		DataAccess.save(fleet);
		fleets[5] = fleet;
	}

	public void testSetTactics1() throws Exception {
		int[] skirmishActionsSlots     = { 0,  1, -1, -1, -1};
		int[] skirmishActionsAbilities = {-1, -1, -1, -1, -1};
		int[] battleActionsSlots     = { 0,  1, -1, -1, -1,  0,  1, -1, -1, -1,  0,  1, -1, -1, -1};
		int[] battleActionsAbilities = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		
		JSONObject answer = doRequest(URI, encodeRequest(fleets[5],
				skirmishActionsSlots, skirmishActionsAbilities,
				battleActionsSlots, battleActionsAbilities));

		System.out.println(answer.toString());
		assertEquals("success", answer.get("type"));
	}

	public void testSetTactics2() throws Exception {
		int[] skirmishActionsSlots     = {-1,  0,  1, -1, -1};
		int[] skirmishActionsAbilities = {-1, -1, -1, -1, -1};
		int[] battleActionsSlots     = { 0,  1, -1, -1, -1,  0,  1, -1, -1, -1,  0,  1, -1, -1, -1};
		int[] battleActionsAbilities = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		
		JSONObject answer = doRequest(URI, encodeRequest(fleets[5],
				skirmishActionsSlots, skirmishActionsAbilities,
				battleActionsSlots, battleActionsAbilities));

		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testSetTactics3() throws Exception {
		int[] skirmishActionsSlots     = { 0,  1, -1, -1, -1};
		int[] skirmishActionsAbilities = {-1, -1, -1, -1, -1};
		int[] battleActionsSlots     = { 0,  1, -1, -1, -1, -1,  1,  0, -1, -1,  0,  1, -1, -1, -1};
		int[] battleActionsAbilities = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		
		JSONObject answer = doRequest(URI, encodeRequest(fleets[5],
				skirmishActionsSlots, skirmishActionsAbilities,
				battleActionsSlots, battleActionsAbilities));

		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testSetTactics4() throws Exception {
		int[] skirmishActionsSlots     = { 0,  1, -1, -1, -1};
		int[] skirmishActionsAbilities = { 1, -1, -1, -1, -1};
		int[] battleActionsSlots     = { 0,  1, -1, -1, -1,  0,  1, -1, -1, -1,  0,  1, -1, -1, -1};
		int[] battleActionsAbilities = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		
		JSONObject answer = doRequest(URI, encodeRequest(fleets[5],
				skirmishActionsSlots, skirmishActionsAbilities,
				battleActionsSlots, battleActionsAbilities));

		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testSetTactics5() throws Exception {
		int[] skirmishActionsSlots     = { 0,  1, -1,  0, -1};
		int[] skirmishActionsAbilities = { 0,  0, -1, -1, -1};
		int[] battleActionsSlots     = { 0,  1, -1,  0, -1, -1,  0,  1, -1,  0, -1, -1,  0,  1, -1};
		int[] battleActionsAbilities = { 0,  0, -1,  0, -1, -1,  0,  0, -1,  0, -1, -1, -1,  0, -1};
		
		JSONObject answer = doRequest(URI, encodeRequest(fleets[5],
				skirmishActionsSlots, skirmishActionsAbilities,
				battleActionsSlots, battleActionsAbilities));

		System.out.println(answer.toString());
		assertEquals("success", answer.get("type"));
	}

	public void testBackSlots1() throws Exception {
		JSONObject answer = doRequest(URI, encodeRequest(fleets[0],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT]));

		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testBackSlots2() throws Exception {
		JSONObject answer = doRequest(URI, encodeRequest(fleets[1],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT]));
		
		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testFreighterSlots1() throws Exception {
		JSONObject answer = doRequest(URI, encodeRequest(fleets[2],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT]));
		
		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testFreighterSlots2() throws Exception {
		JSONObject answer = doRequest(URI, encodeRequest(fleets[3],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.SKIRMISH_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT],
				new int[GameConstants.BATTLE_ACTIONS_COUNT]));
		
		System.out.println(answer.toString());
		assertEquals("error", answer.get("type"));
	}

	public void testFreighterSlots3() throws Exception {
		int[] skirmishActions = new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		Arrays.fill(skirmishActions, -1);
		int[] battleActions = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		Arrays.fill(battleActions, -1);
		
		JSONObject answer = doRequest(URI, encodeRequest(fleets[4],
				skirmishActions, skirmishActions,
				battleActions, battleActions));

		System.out.println(answer.toString());
		assertEquals("success", answer.get("type"));
	}

	public void cleanUp() {
		for (Fleet fleet : fleets)
			if (fleet != null)
				fleet.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private String encodeRequest(Fleet fleet, int[] skirmishActionsSlots,
			int[] skirmishActionsAbilities, int[] battleActionsSlots,
			int[] battleActionsAbilities) {
		return "id=" + fleet.getId() + "&" + encodeFleetSlots(fleet) + "&" +
			encodeFleetTactics(skirmishActionsSlots, skirmishActionsAbilities) + "&" +
			encodeFleetTactics(battleActionsSlots, battleActionsAbilities);
	}
	
	private String encodeFleetSlots(Fleet fleet) {
		String value = "";
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			value += (i > 0 ? "&" : "") + "slot" + i + "_front=" + fleet.getSlot(i).isFront();
		return value;
	}
	
	private String encodeFleetTactics(int[] actionsSlots, int[] actionsAbilities) {
		String value = "";
		String type = actionsSlots.length == GameConstants.SKIRMISH_ACTIONS_COUNT ? "skirmish" : "battle";
		for (int i = 0; i < actionsSlots.length; i++) {
			value += (i > 0 ? "&" : "") + type + "_action" + i + "_slot=" + actionsSlots[i];
			value += "&" + type + "_action" + i + "_ability=" + actionsAbilities[i];
		}
		return value;	
	}
}
