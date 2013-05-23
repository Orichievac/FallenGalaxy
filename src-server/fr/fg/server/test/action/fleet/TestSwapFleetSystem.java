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
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.test.action.TestAction;

public class TestSwapFleetSystem extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String URI = "swapfleetsystem";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet fleet;
	private StarSystem system;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void init() {
		fleet = new Fleet("fleet", 10, 10, 1, 4);
		system = new StarSystem("system", 10, 10, false, 1, 1, 3, new int[8], 4, 1);
		DataAccess.save(fleet);
		DataAccess.save(system);
	}
	
	public void testTransfer() throws Exception {
		fleet = DataAccess.getEditable(fleet);
		fleet.setSlot(new Slot(1, 10, true), 0);
		fleet.setSlot(new Slot(2, 10, true), 1);
//		fleet.setResource(100, 0);
		DataAccess.save(fleet);
		system = DataAccess.getEditable(system);
		system.setSlot(new Slot(2, 1, true), 0);
		system.setSlot(new Slot(1, 1, true), 1);
		system.setSlot(new Slot(121, 100, true), 2);
		DataAccess.save(system);
		
		
		System.out.println("--fleet");
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = fleet.getSlot(i);
			System.out.println(slot.getId() + " - " + slot.getCount());
		}
//		for (int i = 0; i < 8; i++)
//			System.out.print(fleet.getResource(i) + ", ");
		System.out.println();
		
		System.out.println("--system");
		for (int i = 0; i < GameConstants.SYSTEM_SLOT_COUNT; i++) {
			Slot slot = system.getSlot(i);
			System.out.println(slot.getId() + " - " + slot.getCount());
		}
		for (int i = 0; i < 8; i++)
			System.out.print(system.getResource(i) + ", ");
		System.out.println();
		
//		String params = "slot14_id=0&slot10_count=0&resource16=0&slot12_count=0&slot04_id=0&slot01_id=0&slot02_count=0&slot00_count=35&slot11_count=0&slot10_id=0&slot12_id=0&slot11_id=0&system=17&slot03_id=0&slot04_count=0&slot00_id=1&resource17=0&slot02_id=0&slot13_id=0&slot01_count=0&slot13_count=0&resource13=0&fleet=13834&resource11=0&resource14=0&slot03_count=0&resource12=0&slot14_count=0&resource10=0&resource15=0";
		
		JSONObject answer = doRequest(URI, 
				"fleet=" + fleet.getId() + "&system=" + system.getId() + "&" +
				"slot10_id=" + fleet.getSlot0Id() + "&slot10_count=" + fleet.getSlot0Count() + "&" +
				"slot11_id=" + fleet.getSlot1Id() + "&slot11_count=" + fleet.getSlot1Count() + "&" +
				"slot12_id=" + 121 + "&slot12_count=" + 60 + "&" +
				"slot13_id=" + fleet.getSlot3Id() + "&slot13_count=" + fleet.getSlot3Count() + "&" +
				"slot14_id=" + fleet.getSlot4Id() + "&slot14_count=" + fleet.getSlot4Count() + "&" +
				"resource10=" + 3 + "&" +
				"resource11=" + fleet.getResource1() + "&" +
				"resource12=" + fleet.getResource2() + "&" +
				"resource13=" + fleet.getResource3());
		
		System.out.println(answer.toString(2));
		
		fleet = DataAccess.getFleetById(fleet.getId());
		system = DataAccess.getSystemById(system.getId());
		
		System.out.println("--fleet");
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = fleet.getSlot(i);
			System.out.println(slot.getId() + " - " + slot.getCount());
		}
//		for (int i = 0; i < 8; i++)
//			System.out.print(fleet.getResource(i) + ", ");
		System.out.println();

		System.out.println("--system");
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = system.getSlot(i);
			System.out.println(slot.getId() + " - " + slot.getCount());
		}
		for (int i = 0; i < 8; i++)
			System.out.print(system.getResource(i) + ", ");
		System.out.println();
	}
	
	@Override
	public void cleanUp() {
		fleet.delete();
		system.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
