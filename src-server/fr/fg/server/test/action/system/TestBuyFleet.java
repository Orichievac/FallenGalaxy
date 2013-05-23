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

import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.test.action.TestAction;

public class TestBuyFleet extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "buyfleet";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	public StarSystem system;
	public Player buyer;
	public Area area;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		buyer = new Player("testBuyer","testBuyer","testBuyer@testBuyer.com","","");
		DataAccess.save(buyer);
		
		area = new Area("testBuyArea",100,100,0,0,Area.AREA_START,2,0);
		DataAccess.save(area);
		
		system = new StarSystem("testBuyPlace",50,50,false,1,1,3,new int[]{1,1,1,1,1,1,1,1},
				area.getId(),buyer.getId());
		system.setResource(10000, StarSystem.TITANIUM);
		DataAccess.save(system);
	}
	
	public void testBuyFleetOk() throws Exception{
		setPlayer("testBuyer");
		JSONObject answer = doRequest(URI, "system="+system.getId());
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
	}
	
	public void cleanUp() {
		area.delete();
		buyer.delete();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
