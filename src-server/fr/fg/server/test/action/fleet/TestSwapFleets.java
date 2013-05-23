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

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Slot;
import fr.fg.server.test.action.TestAction;

public class TestSwapFleets extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String URI = "swapfleets";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet fleet0, fleet1;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void init() {
		setPlayer("JayJay");
		fleet0 = new Fleet("fleet", 10, 10, getPlayer().getId(), 2);
		fleet1 = new Fleet("fleet", 11, 10, getPlayer().getId(), 2);
		DataAccess.save(fleet0);
		DataAccess.save(fleet1);
	}
	
	public void testTransfer() throws Exception {
		fleet0 = DataAccess.getEditable(fleet0);
		fleet0.setSlot(new Slot(44, 4, true), 0);
		fleet0.setSlot(new Slot(1, 25, true), 1);
//		fleet0.setResource(100, 0);
		DataAccess.save(fleet0);
		fleet1 = DataAccess.getEditable(fleet1);
		fleet1.setSlot(new Slot(121, 10, true), 0);
		fleet1.setSlot(new Slot(24, 100, true), 1);
		DataAccess.save(fleet1);
		
		System.out.println("--fleet0");
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = fleet0.getSlot(i);
			System.out.println(slot.getId() + "\t" + slot.getCount() + "\t" + slot.isFront());
		}
//		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
//			System.out.print(fleet0.getResource(i) + ", ");
		System.out.println();
		
		System.out.println("--fleet1");
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = fleet1.getSlot(i);
			System.out.println(slot.getId() + "\t" + slot.getCount() + "\t" + slot.isFront());
		}
//		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
//			System.out.print(fleet1.getResource(i) + ", ");
		System.out.println();
		
//		String params = "fleet14_id=0&slot10_count=0&resource16=0&slot12_count=0&slot04_id=0&slot01_id=0&slot02_count=0&slot00_count=35&slot11_count=0&slot10_id=0&slot12_id=0&slot11_id=0&system=17&slot03_id=0&slot04_count=0&slot00_id=1&resource17=0&slot02_id=0&slot13_id=0&slot01_count=0&slot13_count=0&resource13=0&fleet=13834&resource11=0&resource14=0&slot03_count=0&resource12=0&slot14_count=0&resource10=0&resource15=0";
		
		doRequest(URI, 
				"fleet0=" + fleet1.getId() + "&fleet1=" + fleet0.getId() + "&" +
				"slot10_id=" + fleet0.getSlot0Id() + "&slot10_count=" + fleet0.getSlot0Count()+ "&" +
				"slot11_id=" + fleet0.getSlot1Id() + "&slot11_count=" + fleet0.getSlot1Count() + "&" +
				"slot12_id=" + fleet1.getSlot1Id() + "&slot12_count=" + fleet1.getSlot1Count() + "&" +
				"slot13_id=" + fleet0.getSlot3Id() + "&slot13_count=" + fleet0.getSlot3Count() + "&" +
				"slot14_id=" + fleet0.getSlot4Id() + "&slot14_count=" + fleet0.getSlot4Count() + "&" +
				"slot00_id=" + fleet1.getSlot0Id() + "&slot00_count=" + fleet1.getSlot0Count()+ "&" +
				"slot01_id=" + 0 + "&slot01_count=" + 0 + "&" +
				"slot02_id=" + fleet1.getSlot2Id() + "&slot02_count=" + fleet1.getSlot2Count() + "&" +
				"slot03_id=" + fleet1.getSlot3Id() + "&slot03_count=" + fleet1.getSlot3Count() + "&" +
				"slot04_id=" + fleet1.getSlot4Id() + "&slot04_count=" + fleet1.getSlot4Count() + "&" +
				"resource10=" + fleet0.getResource1() + "&" +
				"resource11=" + fleet0.getResource1() + "&" +
				"resource12=" + fleet0.getResource2() + "&" +
				"resource13=" + fleet0.getResource3());
		
//		System.out.println(answer.toString(2));
		
		fleet0 = DataAccess.getFleetById(fleet0.getId());
		fleet1 = DataAccess.getFleetById(fleet1.getId());

//		System.out.println("--fleet0");
//		for (int i = 0; i < GameConstants.SHIPS_SLOT_COUNT; i++) {
//			Slot slot = fleet0.getSlot(i);
//			System.out.println(slot.getId() + "\t" + slot.getCount() + "\t" + slot.isFront());
//		}
//		for (int i = 0; i < 8; i++)
//			System.out.print(fleet0.getResource(i) + ", ");
//		System.out.println();
		
		System.out.println("--fleet1");
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = fleet1.getSlot(i);
			System.out.println(slot.getId() + "\t" + slot.getCount() + "\t" + slot.isFront());
		}
//		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
//			System.out.print(fleet1.getResource(i) + ", ");
		System.out.println();
		
	}
	
	@Override
	public void cleanUp() {
		fleet0.delete();
		fleet1.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
