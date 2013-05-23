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

import java.util.List;

import org.json.JSONObject;

import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Report;
import fr.fg.server.data.ReportAction;
import fr.fg.server.data.ReportDamage;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Slot;
import fr.fg.server.data.Treaty;
import fr.fg.server.test.action.TestAction;

public class TestBattle extends TestAction {
	
	public final static String URI = "battle";
	
	private Area arena;
	private Fleet myFleet,enemyFleet;
	private Player player,enemy;
	private Treaty treaty;
	
	private Slot[] mySave,enemySave;

	public void init() {
		if( DataAccess.getPlayerByLogin("TestBattleAttacker")==null ){
			player = new Player("TestBattleAttacker","onsenfu","onsenfou@aussi.com","","");
			DataAccess.save(player);
		}
		else
			player = DataAccess.getPlayerByLogin("TestBattleAttacker");
		
		if( DataAccess.getPlayerByLogin("TestBattleEnemy")==null ){
			enemy = new Player("TestBattleEnemy","onsenfou","onsenfou@enemy.com","","");
			DataAccess.save(enemy);
		}
		else
			enemy = DataAccess.getPlayerByLogin("TestBattleEnemy");
		
		treaty = new Treaty(player.getId(),enemy.getId(),Treaty.TYPE_WAR,0);
		DataAccess.save(treaty);
		
		arena = new Area("BattleField",50,50,50,50,Area.AREA_START,2,0);
		DataAccess.save(arena);
		
		myFleet = new Fleet("attacking",1,1,player.getId(),arena.getId());
		enemyFleet = new Fleet("defending",1,2,enemy.getId(),arena.getId());
		
		myFleet.setSlots(new Slot[]{new Slot(21,593,true),new Slot(121,200,true),new Slot(42,73,true),new Slot(0,0,true),new Slot(0,0,true)});
		enemyFleet.setSlots(new Slot[]{new Slot(21,1000,true),new Slot(1,2500,true),new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true)});
		enemyFleet.setBasicSkill(new Skill(Skill.SKILL_REPAIR,0), 0);
		
		DataAccess.save(myFleet);
		DataAccess.save(enemyFleet);
		
//		mySave = myFleet.cloneSlots(myFleet.getSlots());
//		enemySave = enemyFleet.cloneSlots(enemyFleet.getSlots());
	}	

	public void testAllGood() throws Exception{
		setPlayer("TestBattleAttacker");
		
		JSONObject answer = doRequest(URI, "fleet="+myFleet.getId()+"&enemy="+enemyFleet.getId());
		System.out.println(answer.toString(2));
		assertEquals(SUCCESS, answer.get("type"));
		
		myFleet = DataAccess.getFleetById(myFleet.getId());
		if(myFleet==null){
			myFleet = new Fleet("temp",1,1,player.getId(),arena.getId());
			myFleet.setSlots(new Slot[]{new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true)});
		}
		
		enemyFleet = DataAccess.getFleetById(enemyFleet.getId());
		if(enemyFleet==null){
			enemyFleet = new Fleet("temp",1,2,player.getId(),arena.getId());
			enemyFleet.setSlots(new Slot[]{new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true),new Slot(0,0,true)});
		}
		
		Report report = DataAccess.getReportById(answer.getJSONObject("data").getInt("id"));
		System.out.println(report.getHash());
		
		long[] myDamage = new long[]{0,0,0,0,0};
		long[] enemyDamage = new long[]{0,0,0,0,0};
		
		List<ReportAction> actions = DataAccess.getReportActionsByReportId(report.getId());
		synchronized (actions) {
			for(ReportAction action:actions){
				
				List<ReportDamage> damages = DataAccess.getReportDamagesByAction(action.getId());
				synchronized (damages) {
					for(ReportDamage damage:damages){
						if(damage.getTargetPosition() < GameConstants.FLEET_SLOT_COUNT){
							enemyDamage[damage.getTargetPosition()] += damage.getDamage();
						}
						else{
							myDamage[damage.getTargetPosition() - GameConstants.FLEET_SLOT_COUNT] += damage.getDamage();
						}
					}
				}
				
			}
		}
		
		if(mySave[0].getId()!=0){
			long qty = (long) (mySave[0].getCount()-(Math.ceil(myDamage[0]/mySave[0].getShip().getHull())));
			assertEquals((qty>=0?qty:0),myFleet.getSlot0Count(),5);
		}
		if(mySave[1].getId()!=0){
			long qty = (long) (mySave[1].getCount()-(Math.ceil(myDamage[1]/mySave[1].getShip().getHull())));
			assertEquals((qty>=0?qty:0),myFleet.getSlot1Count(),5);
		}
		if(mySave[2].getId()!=0){
			long qty = (long) (mySave[2].getCount()-(Math.ceil(myDamage[2]/mySave[2].getShip().getHull())));
			assertEquals((qty>=0?qty:0),myFleet.getSlot2Count(),5);
		}
		if(mySave[3].getId()!=0){
			long qty = (long) (mySave[3].getCount()-(Math.ceil(myDamage[3]/mySave[3].getShip().getHull())));
			assertEquals((qty>=0?qty:0),myFleet.getSlot3Count(),5);
		}
		if(mySave[4].getId()!=0){
			long qty = (long) (mySave[4].getCount()-(Math.ceil(myDamage[4]/mySave[4].getShip().getHull())));
			assertEquals((qty>=0?qty:0),myFleet.getSlot4Count(),5);
		}
		
		if(enemySave[0].getId()!=0){
			long qty = (long) (enemySave[0].getCount()-(Math.ceil(enemyDamage[0]/enemySave[0].getShip().getHull())));
			assertEquals((qty>=0?qty:0),enemyFleet.getSlot0Count(),5);
		}
		if(enemySave[1].getId()!=0){
			long qty = (long) (enemySave[1].getCount()-(Math.ceil(enemyDamage[1]/enemySave[1].getShip().getHull())));
			assertEquals((qty>=0?qty:0),enemyFleet.getSlot1Count(),5);
		}
		if(enemySave[2].getId()!=0){
			long qty = (long) (enemySave[2].getCount()-(Math.ceil(enemyDamage[2]/enemySave[2].getShip().getHull())));
			assertEquals((qty>=0?qty:0),enemyFleet.getSlot2Count(),5);
		}
		if(enemySave[3].getId()!=0){
			long qty = (long) (enemySave[3].getCount()-(Math.ceil(enemyDamage[3]/enemySave[3].getShip().getHull())));
			assertEquals((qty>=0?qty:0),enemyFleet.getSlot3Count(),5);
		}
		if(enemySave[4].getId()!=0){
			long qty = (long) (enemySave[4].getCount()-(Math.ceil(enemyDamage[4]/enemySave[4].getShip().getHull())));
			assertEquals((qty>=0?qty:0),enemyFleet.getSlot4Count(),5);
		}
	}
	
	public void cleanUp() {
		player.delete();
		enemy.delete();
		myFleet.delete();
		enemyFleet.delete();
		arena.delete();
		treaty.delete();
	}
}
