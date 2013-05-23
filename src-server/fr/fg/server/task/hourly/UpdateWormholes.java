/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.server.task.hourly;

//import java.util.ArrayList;
//import java.util.List;
//
//import fr.fg.data.Area;
//import fr.fg.data.DataAccess;
//import fr.fg.data.StellarObject;
//import fr.fg.data.Wormhole;
//import fr.fg.util.ArrayUtils;
//import fr.fg.util.Utilities;

public class UpdateWormholes extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateWormholes (hourly)");
//		List<StellarObject> objects = new ArrayList<StellarObject>();
//		objects.addAll(DataAccess.getAllObjects());
//		
//		List<Integer> alreadyChanged = new ArrayList<Integer>();
//		
//		//Pour chaque objet de type wormhole
//		for(int i=0;i<objects.size();i++){
//			if( !objects.get(i).getType().startsWith(StellarObject.TYPE_WORMHOLE) )
//				continue;
//			
//			//Si ce wormhole a déjà été utilisé on passe
//			if(alreadyChanged.size()!=0)
//				if(ArrayUtils.contains(alreadyChanged,objects.get(i).getId()))
//					continue;
//				
//			Wormhole wormhole = DataAccess.getWormholeById(objects.get(i).getId());
//			
//			//Si c'est un wormhole en formation qui doit se former
//			if( objects.get(i).getType().equals(StellarObject.TYPE_WORMHOLE_FORMING) &&
//					wormhole.getDate()+Wormhole.FORMING_TIME>Utilities.now() ){
//				
//				StellarObject connectedWormhole = wormhole.getConnectedWormhole(objects.get(i).getId());
//				
//				synchronized (objects.get(i).getLock()) {
//					synchronized (connectedWormhole.getLock()) {
//						StellarObject newObject = DataAccess.getEditable(objects.get(i));
//						StellarObject connected = DataAccess.getEditable(connectedWormhole);
//
//						if(Utilities.random(0,100)<Wormhole.ONE_WAY){
//							newObject.setType(StellarObject.TYPE_WORMHOLE_ONEWAY);
//							connected.setType(StellarObject.TYPE_WORMHOLE_NOWAY);
//						}
//						else{
//							newObject.setType(StellarObject.TYPE_WORMHOLE_TWOWAY);
//							connected.setType(StellarObject.TYPE_WORMHOLE_TWOWAY);
//						}
//						DataAccess.save(newObject);
//						DataAccess.save(connected);
//					}
//				}
//				
//				alreadyChanged.add(connectedWormhole.getId());
//				
//			}
//			else{
//				//On va peut etre bouger ou changer le wormhole
//				if(Utilities.random(0,100)>Wormhole.ALTER_WORMHOLE){
//					continue;
//				}
//				
//				if(Utilities.random(0,100)<Wormhole.CLOSE_WORMHOLE){
//					StellarObject connectedWormhole = wormhole.getConnectedWormhole(objects.get(i).getId());
//					
//					synchronized (objects.get(i).getLock()) {
//						synchronized (connectedWormhole.getLock()) {
//							StellarObject newObject = DataAccess.getEditable(objects.get(i));
//							StellarObject connected = DataAccess.getEditable(connectedWormhole);
//
//							newObject.setType(StellarObject.TYPE_WORMHOLE_FORMING);
//							connected.setType(StellarObject.TYPE_WORMHOLE_FORMING);
//							
//							Area areaRand[] = new Area[2];
//							int j = 0;
//							do{
//								List<Area> areas = DataAccess.getAllAreas();
//								int rand = Utilities.random(0, areas.size() - 1);
//								
//								List<StellarObject> inArea = new ArrayList<StellarObject>(DataAccess.getObjectsByArea(rand));
//								
//								boolean alright = true;
//								for(StellarObject obj:inArea)
//									if(obj.getType().startsWith(StellarObject.TYPE_WORMHOLE)) alright = false;
//								
//								if(alright)areaRand[j++] = areas.get(rand);
//							}while(j<2);
//							
//							int[] worm1 = areaRand[0].getFreeCoordinate(StellarObject.getSize(StellarObject.TYPE_WORMHOLE).width,
//									StellarObject.getSize(StellarObject.TYPE_WORMHOLE).height, 
//									Area.NO_OBJECTS | Area.NO_SYSTEMS);
//							
//							int[] worm2 = areaRand[1].getFreeCoordinate(StellarObject.getSize(StellarObject.TYPE_WORMHOLE).width,
//									StellarObject.getSize(StellarObject.TYPE_WORMHOLE).height, 
//									Area.NO_OBJECTS | Area.NO_SYSTEMS);
//							
//							newObject.setX(worm1[0]);
//							newObject.setY(worm1[1]);
//							newObject.setIdArea(areaRand[0].getId());
//							
//							connected.setX(worm2[0]);
//							connected.setY(worm2[1]);
//							connected.setIdArea(areaRand[1].getId());
//							
//							DataAccess.save(newObject);
//							DataAccess.save(connected);
//						}
//					}
//					
//					alreadyChanged.add(connectedWormhole.getId());
//				}
//				else{
//					StellarObject objectToConnect = null;
//					for(int j=i+1;j<objects.size();j++){
//						if(ArrayUtils.contains(alreadyChanged, j)
//								|| objects.get(j).getType().equals(StellarObject.TYPE_WORMHOLE_FORMING)
//								|| objects.get(j).getId()==wormhole.getConnectedWormhole(objects.get(i).getId()).getId()
//								|| !objects.get(i).getType().startsWith(StellarObject.TYPE_WORMHOLE)) 
//							continue;
//						objectToConnect = DataAccess.getObjectById(objects.get(j).getId());
//						break;
//					}
//					
//					if(objectToConnect==null){
//						for(int j=i-1;j>0;j--){
//							if(ArrayUtils.contains(alreadyChanged, j)
//									|| objects.get(j).getType().equals(StellarObject.TYPE_WORMHOLE_FORMING)
//									|| objects.get(j).getId()!=wormhole.getConnectedWormhole(objects.get(i).getId()).getId()
//									|| !objects.get(i).getType().startsWith(StellarObject.TYPE_WORMHOLE)) 
//								continue;
//							objectToConnect = DataAccess.getObjectById(objects.get(j).getId());
//							break;
//						}
//					}
//					
//					if(objectToConnect==null) continue;
//					
//					Wormhole wormhole2 = DataAccess.getWormholeById(objectToConnect.getId());
//					
//					if(wormhole2!=null){
//					
//						StellarObject objectToDisconnect1 = wormhole.getConnectedWormhole(objects.get(i).getId());
//						StellarObject objectToDisconnect2 = wormhole2.getConnectedWormhole(objectToConnect.getId());
//						
//						synchronized (objects.get(i).getLock()) {
//							synchronized (objectToConnect.getLock()) {
//								
//									StellarObject newObject = DataAccess.getEditable(objects.get(i));
//									StellarObject connected = DataAccess.getEditable(objectToConnect);
//							
//									if(Utilities.random(0,100)<Wormhole.ONE_WAY){
//										newObject.setType(StellarObject.TYPE_WORMHOLE_ONEWAY);
//										connected.setType(StellarObject.TYPE_WORMHOLE_NOWAY);
//									}
//									else{
//										newObject.setType(StellarObject.TYPE_WORMHOLE_TWOWAY);
//										connected.setType(StellarObject.TYPE_WORMHOLE_TWOWAY);
//									}
//							
//									DataAccess.delete(wormhole);
//									
//									wormhole = new Wormhole(newObject.getId(),connected.getId(),Utilities.now());
//									
//									DataAccess.save(newObject);
//									DataAccess.save(connected);
//									DataAccess.save(wormhole);
//							}
//						}
//						
//						synchronized (objectToDisconnect1.getLock()) {
//							synchronized (objectToDisconnect2.getLock()) {
//									StellarObject newObject = DataAccess.getEditable(objectToDisconnect1);
//									StellarObject connected = DataAccess.getEditable(objectToDisconnect2);
//							
//									if(Utilities.random(0,100)<Wormhole.ONE_WAY){
//										newObject.setType(StellarObject.TYPE_WORMHOLE_ONEWAY);
//										connected.setType(StellarObject.TYPE_WORMHOLE_NOWAY);
//									}
//									else{
//										newObject.setType(StellarObject.TYPE_WORMHOLE_TWOWAY);
//										connected.setType(StellarObject.TYPE_WORMHOLE_TWOWAY);
//									}
//									
//									DataAccess.delete(wormhole2);
//									
//									wormhole2 = new Wormhole(newObject.getId(),connected.getId(),Utilities.now());
//									
//									DataAccess.save(newObject);
//									DataAccess.save(connected);
//									DataAccess.save(wormhole2);
//							}
//						}
//						
//						alreadyChanged.add(objects.get(i).getId());
//						alreadyChanged.add(objectToConnect.getId());
//						alreadyChanged.add(objectToDisconnect1.getId());
//						alreadyChanged.add(objectToDisconnect2.getId());
//						
//					}
//				}
//			}
//		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
