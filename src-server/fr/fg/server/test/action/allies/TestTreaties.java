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

import java.util.List;

import org.json.JSONObject;

import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.test.action.TestAction;

public class TestTreaties extends TestAction {
// ------------------------------------------------------- CONSTANTES -- //
	
	private Ally ally1,ally2;
	private Player player1,player2;
	
	private AllyTreaty treaty;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init(){		
		ally1 = new Ally("TestAllyTreaty1","TA1",Ally.ORGANIZATION_DEMOCRACY,"",0,0);
		DataAccess.save(ally1);
		ally2 = new Ally("TestAllyTreaty2","TA2",Ally.ORGANIZATION_DEMOCRACY,"",0,0);
		DataAccess.save(ally2);
		
		player1 = new Player("TestPlayerTreaty1","","","","");
		player1.setIdAlly(ally1.getId());
		player1.setAllyRank(2);
		DataAccess.save(player1);
		player2 = new Player("TestPlayerTreaty2","","","","");
		player2.setIdAlly(ally2.getId());
		player2.setAllyRank(2);
		DataAccess.save(player2);
	}
	
	public void testSetTreaties() throws Exception{
		//Déclaration de guerre
		setPlayer(player1.getLogin());
		
		JSONObject answer = doRequest("allies/declarewar", "ally="+ally2.getName());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		List<AllyTreaty> treaties = ally1.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally2.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(0, treaty.getSource());
		
		//Proposition de paix
		answer = doRequest("allies/offerpeace", "ally="+ally2.getName());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally1.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally2.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(ally1.getId(), treaty.getSource());
		
		//Refus de paix
		setPlayer(player2.getLogin());
		
		answer = doRequest("allies/offerpeace", "ally="+ally1.getName()+"&accept=false");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally2.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally1.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(0, treaty.getSource());
		
		//Proposition de paix
		answer = doRequest("allies/offerpeace", "ally="+ally1.getName());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally2.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally1.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("war", treaty.getType());
		assertEquals(ally2.getId(), treaty.getSource());
		
		//Acceptation de paix
		setPlayer(player1.getLogin());
		
		answer = doRequest("allies/offerpeace", "ally="+ally2.getName()+"&accept=true");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally1.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally2.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNull(treaty);
		
		//Proposition d'alliance
		answer = doRequest("allies/offerally", "ally="+ally2.getName());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally1.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally2.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("ally", treaty.getType());
		assertEquals(ally1.getId(), treaty.getSource());
		
		//Refus d'alliance
		setPlayer(player2.getLogin());
		
		answer = doRequest("allies/offerally", "ally="+ally1.getName()+"&accept=false");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally2.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally1.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNull(treaty);
		
		//Proposition d'alliance
		answer = doRequest("allies/offerally", "ally="+ally1.getName());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally2.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally1.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("ally", treaty.getType());
		assertEquals(ally2.getId(), treaty.getSource());
		
		//Acceptation d'alliance
		setPlayer(player1.getLogin());
		
		answer = doRequest("allies/offerally", "ally="+ally2.getName()+"&accept=true");
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally1.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally2.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNotNull(treaty);
		assertEquals("ally", treaty.getType());
		assertEquals(0, treaty.getSource());
		
		//Brisage d'alliance
		answer = doRequest("allies/breakally", "ally="+ally2.getName());
		
		assertEquals("success",answer.getString("type"));
		
		treaty = null;
		treaties = ally1.getTreaties();
		synchronized (treaties) {
			for(AllyTreaty eachAllyTreaty:treaties){
				if(eachAllyTreaty.implyAlly(ally2.getId())){
					treaty = eachAllyTreaty;
					break;
				}
			}
		}
		
		assertNull(treaty);
	}
	
	public void cleanUp(){
		ally1.delete();
		ally2.delete();
		player1.delete();
		player2.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

}
