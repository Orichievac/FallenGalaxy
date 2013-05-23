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

package fr.fg.server.test.action.player;

import java.util.List;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.test.action.TestAction;

public class TestTreaties extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private Player player1,player2;
	
	private Treaty treaty;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init(){
		player1 = new Player("TestPlayerTreaty1","","","","");
		DataAccess.save(player1);
		player2 = new Player("TestPlayerTreaty2","","","","");
		DataAccess.save(player2);
	}
	
	public void testSetTreaties() throws Exception{
		//Déclaration de guerre
		setPlayer(player1.getLogin());
		
		JSONObject answer = doRequest("declarewar", "player="+player2.getLogin());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		List<Treaty> treaties = player1.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player2.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(0, treaty.getSource());
		
		//Proposition de paix
		answer = doRequest("offerpeace", "player="+player2.getLogin());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player1.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player2.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(player1.getId(), treaty.getSource());
		
		//Refus de paix
		setPlayer(player2.getLogin());
		
		answer = doRequest("offerpeace", "player="+player1.getLogin()+"&accept=false");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player2.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player1.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(0, treaty.getSource());
		
		//Proposition de paix
		answer = doRequest("offerpeace", "player="+player1.getLogin());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player2.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player1.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(player2.getId(), treaty.getSource());
		
		//Acceptation de paix
		setPlayer(player1.getLogin());
		
		answer = doRequest("offerpeace", "player="+player2.getLogin()+"&accept=true");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player1.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player2.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNull(treaty);
		
		//Proposition d'alliance
		answer = doRequest("offerally", "player="+player2.getLogin());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player1.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player2.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("ally", treaty.getType());
		assertEquals(player1.getId(), treaty.getSource());
		
		//Refus d'alliance
		setPlayer(player2.getLogin());
		
		answer = doRequest("offerally", "player="+player1.getLogin()+"&accept=false");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player2.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player1.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNull(treaty);
		
		//Proposition d'alliance
		answer = doRequest("offerally", "player="+player1.getLogin());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player2.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player1.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("ally", treaty.getType());
		assertEquals(player2.getId(), treaty.getSource());
		
		//Acceptation d'alliance
		setPlayer(player1.getLogin());
		
		answer = doRequest("offerally", "player="+player2.getLogin()+"&accept=true");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player1.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player2.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("ally", treaty.getType());
		assertEquals(0, treaty.getSource());
		
		//Brisage d'alliance
		answer = doRequest("breakally", "player="+player2.getLogin());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = player1.getTreaties();
		synchronized (treaties) {
			for(Treaty eachTreaty:treaties){
				if(eachTreaty.implyPlayer(player2.getId())){
					treaty = eachTreaty;
					break;
				}
			}
		}
		
		assertNull(treaty);
	}
	
	public void cleanUp(){
		player1.delete();
		player2.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
