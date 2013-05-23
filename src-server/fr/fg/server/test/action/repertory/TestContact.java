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

package fr.fg.server.test.action.repertory;

import org.json.JSONObject;

import fr.fg.server.data.Contact;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.test.action.TestAction;

public class TestContact extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init(){
		
	}
	
	public void testAddPlayer() throws Exception{
		setPlayer("Fedaykin");
		JSONObject answer = doRequest("repertory/add", "player=JayJay&type=friend");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		
		assertEquals("JayJay",((JSONObject)answer.get("data")).get("name"));
		assertEquals("disconnected",((JSONObject)answer.get("data")).get("type"));
	}
	
	public void testAddIgnore() throws Exception{
		setPlayer("Fedaykin");
		JSONObject answer = doRequest("repertory/add", "player=AngamaraStudio&type=ignore");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		
		assertEquals("AngamaraStudio",((JSONObject)answer.get("data")).get("name"));
		assertEquals("ignore",((JSONObject)answer.get("data")).get("type"));
	}
	
	public void testGetContact() throws Exception{
		setPlayer("Fedaykin");
		JSONObject answer = doRequest("repertory/add", "player=Elcor&type=friend");
		assertEquals(SUCCESS, answer.get("type"));
		
		answer = doRequest("repertory/get", "");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		
		answer = doRequest("repertory/get", "type=friend");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		
		answer = doRequest("repertory/get", "type=ignore");
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
	}
	
	public void cleanUp(){
		Player[] players = new Player[3];
		
		players[0] = DataAccess.getPlayerByLogin("JayJay");
		players[1] = DataAccess.getPlayerByLogin("AngamaraStudio");
		players[2] = DataAccess.getPlayerByLogin("Elcor");
		
		Contact[] contacts = new Contact[3];
		
		contacts[0] = DataAccess.getContactByContact(3, players[0].getId());
		contacts[1] = DataAccess.getContactByContact(3, players[1].getId());
		contacts[2] = DataAccess.getContactByContact(3, players[2].getId());
		
		if( contacts[2]!=null ){
			contacts[0].delete();
			contacts[1].delete();
			contacts[2].delete();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
