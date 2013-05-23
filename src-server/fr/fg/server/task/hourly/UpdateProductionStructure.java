/*
Copyright 2010 Nicolas Bosc, Thierry Chevalier

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


import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureModule;

public class UpdateProductionStructure extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static int
	COEF_PRODUCTION = 135;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateProductionStructure (hourly)");
		List<Area> areas = new ArrayList<Area>(DataAccess.getAllAreas());
		
		List<Player> playersToUpdate = new ArrayList<Player>();
		
		for (Area area : areas) {
			
			List<Structure> areaStructures = 
				new ArrayList<Structure>(DataAccess.getStructuresByArea(area.getId()));
			
			for (Structure structure : areaStructures){
				if(structure.isActivated()){
					if((structure.getType()==Structure.TYPE_EXPLOITATION_TITANE ||
						structure.getType()==Structure.TYPE_EXPLOITATION_CRISTAL ||
						structure.getType()==Structure.TYPE_EXPLOITATION_ANDIUM ||
						structure.getType()==Structure.TYPE_EXPLOITATION_ANTIMATIERE||
						structure.getType()==Structure.TYPE_LABORATORY ||
						structure.getType()==Structure.TYPE_INFRASTRUCTURE )){
						
						int type=0;
						long prod=0;
						switch(structure.getType()){
						case Structure.TYPE_EXPLOITATION_TITANE:
							prod= (long) (COEF_PRODUCTION*Math.pow(1.8, structure.getModuleLevel(StructureModule.TYPE_PROD_TITANE)));
							type=0;
							break;
						case Structure.TYPE_EXPLOITATION_CRISTAL:
							prod= (long) (COEF_PRODUCTION*Math.pow(1.8, structure.getModuleLevel(StructureModule.TYPE_PROD_CRISTAL)));
							type=1;
							break;
						case Structure.TYPE_EXPLOITATION_ANDIUM:
							prod= (long) (COEF_PRODUCTION*Math.pow(1.8, structure.getModuleLevel(StructureModule.TYPE_PROD_ANDIUM)));
							type=2;
							break;
						case Structure.TYPE_EXPLOITATION_ANTIMATIERE:
							prod= (long) (COEF_PRODUCTION*Math.pow(1.8, structure.getModuleLevel(StructureModule.TYPE_PROD_ANTIMATIERE)));
							type=3;
							break;
						case Structure.TYPE_INFRASTRUCTURE:
							prod= (long) (COEF_PRODUCTION*Math.pow(1.8, structure.getModuleLevel(StructureModule.TYPE_PROD_CREDIT)));
							type=10;
							break;
						case Structure.TYPE_LABORATORY:
							prod= (long) (COEF_PRODUCTION*Math.pow(1.8, structure.getModuleLevel(StructureModule.TYPE_PROD_IDEA)));
							type=20;
							break;
						}
					
					
					
					
					// On recupère les ressources du joueurs dans le secteur
					
					List<StorehouseResources> ressources = 
						DataAccess.getStorehouseResourcesByArea(area.getId());
					long areaRessource = 0;
					
					StorehouseResources playerAreaRessource = null;
					for(StorehouseResources ressource : ressources)
					{
						if(ressource.getIdPlayer()==structure.getIdOwner())
						{
							playerAreaRessource=ressource;
							
							areaRessource=ressource.getResource0()+
							ressource.getResource1()+
							ressource.getResource2()+
							ressource.getResource3();
						}
					}
					
					// On recupère les ressources max que peut contenir le secteur pour le joueur
					long capacity=0;
					for(Structure areaStructure : areaStructures)
					{
						if(areaStructure.getType()==Structure.TYPE_STOREHOUSE &&
								areaStructure.getOwner()==structure.getOwner())
						{
							capacity+=areaStructure.getPayload();
						}
					}
					
					
					// On ajoute les ressources
	
					if(type<10){
					if(capacity-areaRessource>0)
					{
						if(capacity-areaRessource-prod<0)
						{
							prod=prod-(capacity-areaRessource);
							if(playerAreaRessource!=null){
								synchronized(playerAreaRessource)
								{
									playerAreaRessource = DataAccess.getEditable(playerAreaRessource);
									playerAreaRessource.addResource(prod, type);
									playerAreaRessource.save();
								}
							}
							else
							{
								playerAreaRessource = new StorehouseResources(area.getId(), structure.getIdOwner());
								playerAreaRessource.setResource(prod, type);
								playerAreaRessource.save();
							}
						}
						else
						{
							if(playerAreaRessource!=null){
								synchronized(playerAreaRessource)
								{
									playerAreaRessource = DataAccess.getEditable(playerAreaRessource);
									playerAreaRessource.addResource(prod, type);
									playerAreaRessource.save();
								}
							}
							else
							{
								playerAreaRessource = new StorehouseResources(area.getId(), structure.getIdOwner());
								playerAreaRessource.setResource(prod, type);
								playerAreaRessource.save();
							}
						}
						
						if (!playersToUpdate.contains(structure.getOwner()))
							playersToUpdate.add(structure.getOwner());
						
					}
					}
					else
					{
						Player player=structure.getOwner();
						
						synchronized (player.getLock()) {
							player = DataAccess.getEditable(player);
							if(type==10)
								player.addCredits(prod);
							else if(type==20)
								player.addResearchPoints(prod);
							player.save();
						}
					}
					}

				}
			}
			
			/*for (Player player : playersToUpdate)
				UpdateTools.formatUpdates(
						player,
						Update.getAreaUpdate()
					);*/
	}
		UpdateTools.queueAreaUpdate(playersToUpdate);

	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
