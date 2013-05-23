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

package fr.fg.server.test.action.trade;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Tradecenter;
import fr.fg.server.test.action.TestAction;

public class TestChange extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "trade/change";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Player owner;
	private Fleet fleet;
	private StellarObject object;
	private Tradecenter tradecenter;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		owner = new Player("testplayer", "", "", "", "");
		owner.save();
		
		fleet = new Fleet("fleet", 10, 10, owner.getId(), 4);
//		fleet.setResource(1000, 0);
		fleet.setSlot(new Slot(Ship.MAMMOTH, 10, true), 0);
		fleet.save();
		
		object = new StellarObject(10, 10, StellarObject.TYPE_TRADECENTER, 0, 4);
		object.save();
		
		tradecenter = new Tradecenter(object.getId(), 0.00001, .05);
		tradecenter.setRate(-0.01, 0);
		tradecenter.save();
	}
	
	public void testChange() throws Exception {
		setPlayer(owner.getLogin());
		
		owner = DataAccess.getEditable(owner);
		owner.setCredits(1000);
		owner.save();
		
		doRequest(URI, "fleet=" + fleet.getId() + "&resource=" + 0 + "&count=-1000");
		
		fleet = DataAccess.getFleetById(fleet.getId());
		tradecenter = DataAccess.getTradecenterById(tradecenter.getIdTradecenter());
		owner = DataAccess.getPlayerById(owner.getId());
		
		System.out.println("Player credits: " + owner.getCredits());
		
		System.out.print("Fleet resources: ");
//		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
//			System.out.print(fleet.getResource(i) + " ");
		System.out.println();
		
		System.out.print("Tradecenter rates: ");
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			System.out.print(tradecenter.getRate(i) + " ");
		System.out.println();
	}
	
	@Override
	public void cleanUp() {
		owner.delete();
		object.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
