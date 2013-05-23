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

package fr.fg.server.action.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.AiTools;
import fr.fg.server.core.GeneratorTools;
import fr.fg.server.core.GeneratorTools.Location;
import fr.fg.server.data.Area;
import fr.fg.server.data.Connection;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Maintenance extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Opérations de maintenance
		
		// Reset des tactiques
//		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
//		
//		for (Player player2 : players) {
//			if (player2.isAi())
//				continue;
//			
//			List<Fleet> fleets = new ArrayList<Fleet>(player2.getFleets());
//			
//			for (Fleet fleet : fleets) {
//				synchronized (fleet.getLock()) {
//					fleet = DataAccess.getEditable(fleet);
//					fleet.updateTactics();
//					fleet.save();
//				}
//			}
//		}
		
//		// Création d'un nouveau secteur
//		int x = Integer.parseInt((String) params.get("param1"));
//		int y = Integer.parseInt((String) params.get("param2"));
//		int idSector = Integer.parseInt((String) params.get("param3"));
//		
//		GeneratorTools.createArea(Area.AREA_BANK_10_20,
//			new GeneratorTools.Location(x, y), idSector);
		
		
		List<Structure> structures = new ArrayList<Structure>(DataAccess.getAllStructures());
		
//		for (Structure structure : structures) {
//			if (structure.getType() == Structure.TYPE_SPACESHIP_YARD && structure.getShipContainer() == null) {
//				ShipContainer shipContainer = new ShipContainer(ShipContainer.CONTAINER_STRUCTURE, structure.getId());
//				shipContainer.save();
//			}
//		}
		
		/*for (Structure structure : structures) {
			if (structure.getType() == Structure.TYPE_SPACESHIP_YARD) {
				for (int i = 0; i < 6; i++)
					if (structure.getModuleLevel(StructureModule.TYPE_DECK_FIGHTER + i) > 1) {
						List<StructureModule> modules = new ArrayList<StructureModule>(structure.getModules());
						for (StructureModule module : modules) {
							StorehouseResources resources = structure.getArea().getStorehouseResourcesByPlayer(player.getId());
							
							long[] cost = structure.getModuleCost(StructureModule.TYPE_DECK_FIGHTER, module.getLevel());
							synchronized (resources) {
								resources = DataAccess.getEditable(resources);
								for (int j = 0; j < 4; j++) {
									resources.addResource(cost[j], j);
								}
								resources.save();
							}
							
							synchronized (module) {
								module = DataAccess.getEditable(module);
								module.setLevel(1);
								module.save();
							}
						}
					}
			}
		}*/
		
		String param4 = (String) params.get("param4");

		if (param4.equals("1")) {
//			for(int i=1;i<=50;i++)
				
//				if(DataAccess.getPlayerByLogin("pirate"+i)!=null)
//						DataAccess.getPlayerByLogin("pirate"+i).delete();
			AiTools.createPiratePlayers();
		} else if (param4.equals("2")) {
			//http://gaia.fallengalaxy.eu/admin/maintenance.do?param4=2&param1=-10&param2=-8&seckey=av5qu6l7YnufJkKfB2td
			// Création d'un nouveau quadrants
			int x = Integer.parseInt((String) params.get("param1"));
			int y = Integer.parseInt((String) params.get("param2"));
			
			GeneratorTools.createSector(Sector.SECTOR_START,
				new GeneratorTools.Location(x, y));
		} else if (param4.equals("3")) {
			// Création d'un nouveau secteur
			int x = Integer.parseInt((String) params.get("param1"));
			int y = Integer.parseInt((String) params.get("param2"));

			GeneratorTools.createSector(Sector.SECTOR_COLONIES_10_20,
				new GeneratorTools.Location(x, y));
		}
		else if (param4.equals("4")) {
			// Création d'un nouveau secteur
			int x = Integer.parseInt((String) params.get("param1"));
			int y = Integer.parseInt((String) params.get("param2"));

			GeneratorTools.createSector(Sector.SECTOR_COLONIES_20_30,
				new GeneratorTools.Location(x, y));
		}
		else if (param4.equals("5")) {
			// Création d'un nouveau secteur
			int x = Integer.parseInt((String) params.get("param1"));
			int y = Integer.parseInt((String) params.get("param2"));

			GeneratorTools.createSector(Sector.SECTOR_COLONIES_30_40,
				new GeneratorTools.Location(x, y));
		}
		else if (param4.equals("6")) {
			// Création d'un nouveau secteur
			int x = Integer.parseInt((String) params.get("param1"));
			int y = Integer.parseInt((String) params.get("param2"));

			GeneratorTools.createSector(Sector.SECTOR_COLONIES_40_50,
				new GeneratorTools.Location(x, y));
		}
		else if (param4.equals("10")) {
			// Création d'un nouveau secteur
			int x = Integer.parseInt((String) params.get("param1"));
			int y = Integer.parseInt((String) params.get("param2"));

			GeneratorTools.createSector(Sector.SECTOR_COLONIES_50_60,
				new GeneratorTools.Location(x, y));
		}
		else if(param4.equals("7"))
		{
			int count = Integer.parseInt((String) params.get("param1"));
			List<Location> locations = GeneratorTools.generateLocations(count, 70, 5, 10);
			for(int i=0;i<locations.size();i++)
			LoggingSystem.getServerLogger().info("x : "+locations.get(i).getX()+
					" y : "+locations.get(i).getY()
					);
		}
		else if(param4.equals("8"))
		{
			int count = Integer.parseInt((String) params.get("param1"));
			List<Location> locations = GeneratorTools.generateSectorLocations(count, 70, 5, 10);
			for(int i=0;i<locations.size();i++)
			LoggingSystem.getServerLogger().info("x : "+locations.get(i).getX()+
					" y : "+locations.get(i).getY()
					);
		}
		else if(param4.equals("9"))
		{
			// Création des secteurs pirates X5
			
			List<Sector> sectors = DataAccess.getAllSectors();
			
			for(Sector sector : sectors)
			{
				boolean done = false;
				int x=0, y=0;
				
				for(Area area : sector.getAreas())
				{
					if(area.getType()>=22 && area.getType()<=26)
						done = true;
					
					if(area.getY()>y)
					{
						x=area.getX();
						y=area.getY();
					}
					
				}
				
				LoggingSystem.getServerLogger().error("Done? "+done);
				LoggingSystem.getServerLogger().error("Quadrant "+sector.getName());
				if(done == false)
				{
					switch(sector.getType()){
					case 1 :
						GeneratorTools.createArea(22, x+2, y+4, sector.getId());
						break;
					case 2 :
						GeneratorTools.createArea(23, x+2, y+4, sector.getId());
						break;
					case 3 :
						GeneratorTools.createArea(24, x+2, y+4, sector.getId());
						break;
					case 4 :
						GeneratorTools.createArea(25, x+2, y+4, sector.getId());
						break;
					case 5 :
						GeneratorTools.createArea(26, x+2, y+4, sector.getId());
						break;
					case 6 :
						GeneratorTools.createArea(27, x+2, y+4, sector.getId());
						break;
					}	
				}
			}

		}
		else if(param4.equals("modify"))
		{
			//Modification des astéroides
			List<Area> areas = DataAccess.getAllAreas();
			
			for(Area area : areas){
				
				String type1="";
				String type2="";
				boolean todo = false, titane = false, crystal = false, andium = false;
				
				if(area.getType()==Area.AREA_MINING_10_15)
				{
					//3 lowc 6 vein
					
					type1 = "lowc";
					type2= "vein";
					todo = true;
				}
				else if(area.getType()==Area.AREA_MINING_20_25)
				{
					//3 important, 6 mediumc
					type1 = "important";
					type2= "mediumc";
					todo = true;
				}
				else if(area.getType()==Area.AREA_MINING_30_35)
				{
					//3 high, 6 riches
					type1 = "high";
					type2= "avg";
					todo = true;
				}
				else if(area.getType()==Area.AREA_MINING_40_45)
				{
					//3 pur, 6 abondant
					type1 = "pure";
					type2= "abondant";
					todo = true;
				}
				else if(area.getType()==Area.AREA_MINING_50_55)
				{
					//3 pur, 6 abondant
					type1 = "concentrate";
					type2= "pure";
					todo = true;
				}
				
				if(todo==true)
				{
					List<StellarObject> objects = area.getObjects();
					// asteroid_ + type + _ressource
					for(int i=0; i<objects.size();i++)
					//for(StellarObject object : objects)
					{
						StellarObject object = objects.get(i);
						

//						
//						LoggingSystem.getServerLogger().error("Current : "+object.getId());
//						LoggingSystem.getServerLogger().error("Next : "+objects.get(i+1).getId());
//						
						String type = object.getType();
						objects.remove(objects);
						
						LoggingSystem.getServerLogger().error("AE");
						
//						LoggingSystem.getServerLogger().error("Current : "+object.getId());
//						LoggingSystem.getServerLogger().error("Next : " +objects.get(i+1).getId());
						
						LoggingSystem.getServerLogger().error("1111111");
						synchronized (object.getLock()) {
							if(object.getType().contains("titanium")){
								if(!titane){
							StellarObject object2 = DataAccess.getEditable(object);
							object2.setType("asteroid_"+type1+"_titanium");
							object2.save();
							titane=true;
								}
								else
								{
									StellarObject object2 = DataAccess.getEditable(object);
									object2.setType("asteroid_"+type2+"_titanium");
									object2.save();
								}
							}
							else if(object.getType().contains("crystal"))
							{
								if(!crystal){
									StellarObject object2 = DataAccess.getEditable(object);
									object2.setType("asteroid_"+type1+"_crystal");
									object2.save();
									crystal=true;
									}
									else
									{
										StellarObject object2 = DataAccess.getEditable(object);
										object2.setType("asteroid_"+type2+"_crystal");
										object2.save();
									}
							}
							else if(object.getType().contains("andium"))
							{
								if(!andium){
									StellarObject object2 = DataAccess.getEditable(object);
									object2.setType("asteroid_"+type1+"_andium");
									object2.save();
									andium=true;
									}
									else
									{
										StellarObject object2 = DataAccess.getEditable(object);
										object2.setType("asteroid_"+type2+"_andium");
										object2.save();
									}
							}
						}
						
						LoggingSystem.getServerLogger().error("Test");
						
					}
					
				}
				
				
			}
			
			
		}
		else if(param4.equals("baha"))
		{
			List<Connection> connexions = DataAccess.getAllConnections();
			List<Connection> connexionsToCheck = new ArrayList<Connection>();
			List<Connection> connexionsToCheck2 = new ArrayList<Connection>();
			
			for(Connection connex : connexions)
			if(connex.getStart()>Utilities.now()-7*24*3600)
			{
				connexionsToCheck.add(connex);
			}
			
			connexionsToCheck2 = connexionsToCheck;
			
			for(Connection connex : connexionsToCheck)
			{
				for(Connection connex2 : connexionsToCheck2);
			}
		}
		// Génération de puits dans un quadrant
		else if(param4.equals("42")){ 
			String actiontodo = (String) params.get("param5"); //doit être "puits"
			int sector = Integer.parseInt((String) params.get("param2")); // doit contenir l'id du quadrant
			if(actiontodo.equals("puits")){
				
				Sector sectorToUpdate=DataAccess.getSectorById(sector);
				List<Area> areas= DataAccess.getAreasBySector(sectorToUpdate.getId());

				for(int i=0;i< areas.size();i++){
					
					Area area=areas.get(i);
					int areaWidth=area.getWidth();
					int areaHeight=area.getHeight();
					
					//Genere les puits dans un secteur
					int gravityWellCount=3;
					int puitsCount=0;
					int[][] puitsPosition = new int[gravityWellCount][2];
					if((area.getType()==Area.AREA_BANK_0_10) || (area.getType()==Area.AREA_BANK_10_20))
					{
						gravityWellCount=2;
					}
					for (int k = 0; k < gravityWellCount; k++) {
						
						int x, y;
						boolean invalid;
						
							do {
								
							x = (int) (Math.random() * areaWidth);
								y = (int) (Math.random() * areaHeight);
								
								// Vérifie que le puits est a au moins 40 cases
								// d'écart des portes hyperspatiales
								invalid = false;
								int gatesCount=area.getGates().size();
								int[][] gatesPosition = new int[gatesCount][2];
								
								for(int n=0;n<gatesCount;n++){
								gatesPosition[n][0] = area.getGates().get(n).getX();
								gatesPosition[n][1] = area.getGates().get(n).getY();
								}
								
								for (int l = 0; l < gatesCount; l++) {
									int dx = x - gatesPosition[l][0];
									int dy = y - gatesPosition[l][1];
									
									if (dx * dx + dy * dy < 40 * 40) {
										invalid = true;
										break;
									}
								}
								
			
								
								// Vérifie que le puits n'entre pas en collision avec
								// un système
								
								int systemsCount=area.getSystems().size();
								int[][] systemsPosition = new int[systemsCount][2];
								
								for(int n=0;n<systemsCount;n++){
									systemsPosition[n][0] = area.getSystems().get(n).getX();
									systemsPosition[n][1] = area.getSystems().get(n).getY();
								}
								
								for (int l = 0; l < systemsCount; l++) {
									int dx = x - systemsPosition[l][0];
									int dy = y - systemsPosition[l][1];
									int radiusSq = 2 * GameConstants.SYSTEM_RADIUS + 5;
									
									if (dx * dx + dy * dy < radiusSq * radiusSq) {
										invalid = true;
										break;
									}
								}
								
								// Vérifie que le puits n'entre pas en collision avec
								// un autre.
								
	
								
								for (int l = 0; l < puitsCount; l++) {
									int dx = x - puitsPosition[l][0];
									int dy = y - puitsPosition[l][1];
									int radiusSq = 2 * GameConstants.SYSTEM_RADIUS + 3;
									
									if (dx * dx + dy * dy < radiusSq * radiusSq) {
										invalid = true;
										break;
									}
								}
								
								
								
							} while (invalid);
							
							StellarObject puits = new StellarObject(
									x, y, StellarObject.TYPE_GRAVITY_WELL, 1000, area.getId());
							
							puits.save();
							
							puitsCount++;
							puitsPosition[puitsCount-1][0]=x;
							puitsPosition[puitsCount-1][1]=y;
							
						}
				
				}
			}
		}
		

		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
