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
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.test.action.TestAction;

public class TestApply extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "allies/apply";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Player applicant,enemy;
	private AllyTreaty allyTreaty;
	private Ally ally,alliedAlly;
	private Treaty treaty;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		ally = new Ally("TestUnitaire","TU","Democracy","Alliance de test",5000,0);
		alliedAlly = new Ally("TestUnitaire2","TU2","Democracy","Alliance Allié",5000,0);
		DataAccess.save(ally);
		DataAccess.save(alliedAlly);
		
		applicant = new Player("ApplicantTest","","","","");
		enemy = new Player("EnemyTest","","","","");
		enemy.setIdAlly(alliedAlly.getId());
		enemy.setAllyRank(2);
		DataAccess.save(applicant);
		DataAccess.save(enemy);
		
		alliedAlly = DataAccess.getEditable(alliedAlly);
		alliedAlly.setIdCreator(enemy.getId());
		DataAccess.save(alliedAlly);
	}
	
	public void testApplyAlreadyInAlly() throws Exception{
		System.setProperty("fg.login", "EnemyTest");
		
		System.out.println(getName());
		JSONObject answer = doRequest(URI, "ally=TestUnitaire&cv=Je veux rentrer");
		System.out.println(answer.toString(2));
		assertEquals("error",answer.get("type"));
	}
	
	public void testApplyAlreadyApplying() throws Exception{
		System.setProperty("fg.login", "ApplicantTest");
		
		Applicant oldApplication = new Applicant(alliedAlly.getId(),applicant.getId());
		DataAccess.save(oldApplication);
		
		System.out.println(getName());
		JSONObject answer = doRequest(URI, "ally=TestUnitaire&cv=Je veux rentrer");
		System.out.println(answer.toString(2));
		assertEquals("error",answer.get("type"));
		
		oldApplication.delete();
	}
	
	public void testApplyUnexistingAlly() throws Exception{
		System.setProperty("fg.login", "ApplicantTest");
		
		System.out.println(getName());
		JSONObject answer = doRequest(URI, "ally=TestUnexistingAlly&cv=Je veux rentrer");
		System.out.println(answer.toString(2));
		assertEquals("error",answer.get("type"));
	}
	
	public void testApplyConflictedTreaty() throws Exception{
		System.setProperty("fg.login", "ApplicantTest");
		
		allyTreaty = new AllyTreaty(ally.getId(),alliedAlly.getId(),"ally",0);		
		DataAccess.save(allyTreaty);
		allyTreaty = DataAccess.getEditable(allyTreaty);
		DataAccess.save(allyTreaty);
		
		treaty = new Treaty(applicant.getId(),enemy.getId(),"war",0);
		DataAccess.save(treaty);
		
		System.out.println(getName());
		JSONObject answer = doRequest(URI, "ally=TestUnitaire&cv=Je veux rentrer");
		System.out.println(answer.toString(2));
		assertEquals("error",answer.get("type"));
		
		allyTreaty.delete();
		treaty.delete();
	}
	
	public void testApplyValid() throws Exception{
		System.setProperty("fg.login", "ApplicantTest");
		
		System.out.println(getName());
		JSONObject answer = doRequest(URI, "ally=TestUnitaire&cv=Je veux rentrer");
		System.out.println(answer.toString(2));
		assertEquals("success",answer.get("type"));
	}
	
	public void cleanUp() {
		enemy.delete();
		applicant.delete();
		ally.delete();
		alliedAlly.delete();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
