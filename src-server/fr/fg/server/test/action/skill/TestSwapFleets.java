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

package fr.fg.server.test.action.skill;

import org.json.JSONObject;

import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.FleetLink;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.test.action.TestAction;

public class TestSwapFleets extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "skill/swapfleetposition";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Area arena = null;
	private Player swappedPlayer = null, swapperPlayer = null;
	private Fleet swapped = null,swapper = null,linked = null;
	private FleetLink link = null;
	private StarSystem system = null;	
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testSwapFleet() throws Exception{
		arena = new Area("TestSwapFleet",100,100,1000,1000,Area.AREA_START,2,0);
		DataAccess.save(arena);
		
		swappedPlayer = new Player("TestSwappedPlayer","","","","");
		DataAccess.save(swappedPlayer);
		
		swapped = new Fleet("TestSwapped",50,50,swappedPlayer.getId(),arena.getId());
		DataAccess.save(swapped);
		
		
		swapperPlayer = new Player("TestSwapperPlayer","","","","");
		DataAccess.save(swapperPlayer);
		
		swapper = new Fleet("TestSwapper",52,52,swapperPlayer.getId(),arena.getId());
		swapper.setUltimateSkill(new Skill(Skill.SKILL_ULTIMATE_SWAP,0));
		DataAccess.save(swapper);
		
		system = new StarSystem("TestSystemSwap",50,50,false,0,0,3,new int[]{0,0,0,0},arena.getId(),0);
		DataAccess.save(system);
		
		setPlayer("TestSwapperPlayer");
		
		JSONObject answer = doRequest(URI, "fleet="+swapper.getId()+"&affected="+swapped.getId());
		assertEquals("success", answer.get("type"));
		
		swapped = DataAccess.getFleetById(swapped.getId());
		swapper = DataAccess.getFleetById(swapper.getId());
		
		assertEquals(52, swapped.getX());
		assertEquals(52, swapped.getY());
		
		assertEquals(50, swapper.getX());
		assertEquals(50, swapper.getY());
		
		assertEquals(0, swapper.getMovement());
	}
	
	public void testSwapBreakLink() throws Exception{
		arena = new Area("TestSwapFleet",100,100,1000,1000,Area.AREA_START,2,0);
		DataAccess.save(arena);
		
		
		swappedPlayer = new Player("TestSwappedPlayer","","","","");
		DataAccess.save(swappedPlayer);
		
		swapped = new Fleet("TestSwapped",50,50,swappedPlayer.getId(),arena.getId());
		DataAccess.save(swapped);
		
		linked = new Fleet("TestLinked",40,50,swappedPlayer.getId(),arena.getId());
		DataAccess.save(linked);
		
		link = new FleetLink(swapped.getId(),linked.getId(),FleetLink.LINK_DEFENSIVE);
		DataAccess.save(link);
		
		
		swapperPlayer = new Player("TestSwapperPlayer","","","","");
		DataAccess.save(swapperPlayer);
		
		swapper = new Fleet("TestSwapper",70,70,swapperPlayer.getId(),arena.getId());
		swapper.setUltimateSkill(new Skill(Skill.SKILL_ULTIMATE_SWAP,2));
		DataAccess.save(swapper);
		
		setPlayer("TestSwapperPlayer");
		
		JSONObject answer = doRequest(URI, "fleet="+swapper.getId()+"&affected="+swapped.getId());
		assertEquals("success", answer.get("type"));

		swapped = DataAccess.getFleetById(swapped.getId());
		swapper = DataAccess.getFleetById(swapper.getId());
		
		assertEquals(70, swapped.getX());
		assertEquals(70, swapped.getY());
		
		assertEquals(50, swapper.getX());
		assertEquals(50, swapper.getY());
		
		assertEquals(0, swapper.getMovement());
		
		assertEquals(DataAccess.getFleetLinks(swapped.getId(), linked.getId()), 0);
	}
	
	public void testSwapFleetInSystem() throws Exception{
		arena = new Area("TestSwapFleet",100,100,1000,1000,Area.AREA_START,2,0);
		DataAccess.save(arena);
		
		
		swappedPlayer = new Player("TestSwappedPlayer","","","","");
		DataAccess.save(swappedPlayer);
		
		swapped = new Fleet("TestSwapped",50,50,swappedPlayer.getId(),arena.getId());
		DataAccess.save(swapped);
		
		
		swapperPlayer = new Player("TestSwapperPlayer","","","","");
		DataAccess.save(swapperPlayer);
		
		swapper = new Fleet("TestSwapper",60,60,swapperPlayer.getId(),arena.getId());
		swapper.setUltimateSkill(new Skill(Skill.SKILL_ULTIMATE_SWAP,2));
		DataAccess.save(swapper);
		
		system = new StarSystem("TestSystemSwap",50,50,false,0,0,3,new int[]{0,0,0,0},arena.getId(),swappedPlayer.getId());
		DataAccess.save(system);
		
		setPlayer("TestSwapperPlayer");
		
		JSONObject answer = doRequest(URI, "fleet="+swapper.getId()+"&affected="+swapped.getId());
		assertEquals("error", answer.get("type"));		
	}	
	
	public void testSwapFleetColonizing() throws Exception{
		arena = new Area("TestSwapFleet",100,100,1000,1000,Area.AREA_START,2,0);
		DataAccess.save(arena);
		
		
		swappedPlayer = new Player("TestSwappedPlayer","","","","");
		DataAccess.save(swappedPlayer);
		
		system = new StarSystem("TestSystemSwap",50,50,false,0,0,3,new int[]{0,0,0,0},arena.getId(),0);
		DataAccess.save(system);
		
		swapped = new Fleet("TestSwapped",50,50,swappedPlayer.getId(),arena.getId());
		DataAccess.save(swapped);
		
		swapperPlayer = new Player("TestSwapperPlayer","","","","");
		DataAccess.save(swapperPlayer);
		
		swapper = new Fleet("TestSwapper",60,60,swapperPlayer.getId(),arena.getId());
		swapper.setUltimateSkill(new Skill(Skill.SKILL_ULTIMATE_SWAP,2));
		DataAccess.save(swapper);
		
		setPlayer("TestSwapperPlayer");
		
		JSONObject answer = doRequest(URI, "fleet="+swapper.getId()+"&affected="+swapped.getId());
		assertEquals("error", answer.get("type"));		
	}	
	
	@Override
	public void cleanUp() {
		if(link!=null)
			link.delete();
		if(system!=null)
			system.delete();
		if(swapped!=null)
			swapped.delete();
		if(swapper!=null)
			swapper.delete();
		if(linked!=null)
			linked.delete();
		if(swappedPlayer!=null)
			swappedPlayer.delete();
		if(swapperPlayer!=null)
			swapperPlayer.delete();
		if(arena!=null)
			arena.delete();		
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
