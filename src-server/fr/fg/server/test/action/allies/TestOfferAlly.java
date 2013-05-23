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

package fr.fg.server.test.action.allies;

import org.json.JSONObject;

import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.test.action.TestAction;

public class TestOfferAlly extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "allies/offerally";
	
	Player player1;
	Ally ally1,ally2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init(){		
		ally1 =  new Ally("toto","TOT",Ally.ORGANIZATION_DEMOCRACY,"",0,3);
		DataAccess.save(ally1);
		
		player1 = new Player("Test1","TE1","","","");
		player1.setIdAlly(ally1.getId());
		player1.setAllyRank(2);
		DataAccess.save(player1);
		
		ally2 =  new Ally("totoPlop","TPL",Ally.ORGANIZATION_DEMOCRACY,"",0,3);
		DataAccess.save(ally2);
		
	}
	
	public void testAllGood() throws Exception{
		setPlayer("Test1");
	
		JSONObject answer = doRequest(URI, "ally=totoPlop");
		System.out.println(answer.toString(2));
		assertEquals("success",answer.get("type"));
	}
	
	public void cleanUp(){
		ally1.delete();
		ally2.delete();
		player1.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
