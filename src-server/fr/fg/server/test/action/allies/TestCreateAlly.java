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

public class TestCreateAlly extends TestAction {
// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "allies/createally";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Player player,allyName;
	private Ally existing;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		Player temp1 = DataAccess.getPlayerByLogin("PlayerUnitaire");
		Player temp2 = DataAccess.getPlayerByLogin("PlayerUnitaire");
		Ally temp3 = DataAccess.getAllyByName("TestUnitaire");
		Ally temp4 = DataAccess.getAllyByName("newAllyUnitaire");
		
		if(temp1!=null)
			temp1.delete();
		if(temp2!=null)
			temp2.delete();
		if(temp3!=null)
			temp3.delete();
		if(temp4!=null)
			temp4.delete();
		
		player = new Player("PlayerUnitaire","PlayerUnitaire","PlayerUnitaire@test.com","","");
		DataAccess.save(player);
		
		existing = new Ally("TestUnitaire","TU","Democracy","Alliance de test",5000,1);
		DataAccess.save(existing);
		
		allyName = new Player("NameExisting","NameExisting","NameExisting@test.com","","");
		allyName.setIdAlly(existing.getId());
		allyName.setAllyRank(1);
		DataAccess.save(allyName);
		
	}
	
	public void testCrateAllyOk() throws Exception{
		setPlayer("PlayerUnitaire");
		JSONObject answer = doRequest(URI, "ally=newAllyUnitaire&organization=democracy");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		
		Ally newAlly = player.getAlly();
		assertNotNull(newAlly);
		
		if(newAlly==null) return;
		
		assertEquals(newAlly.getName(),"newAllyUnitaire");
		
		assertEquals(player.getIdAlly(), newAlly.getId());
		
		assertEquals(player.getAllyRank(), newAlly.getLeaderRank());
	}
	
	public void cleanUp() {
		existing.delete();
		player.delete();
		allyName.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
