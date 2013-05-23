/*
Copyright 2010 Nicolas Bosc

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


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.AiTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.PnjDefender;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureModule;
import fr.fg.server.util.Config;
import fr.fg.server.util.Utilities;

public class UpdateDefenseHangar extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateDefenseHangar (hourly)");
		
		List<Area> areas = new ArrayList<Area>(DataAccess.getAllAreas());
		List<Integer> areasToUpdate = new ArrayList<Integer>();
		
		for (Area area : areas) {
			
			List<Structure> areaStructures = 
				new ArrayList<Structure>(DataAccess.getStructuresByArea(area.getId()));
			List<Fleet> areaFleets = 
				new ArrayList<Fleet>(DataAccess.getFleetsByArea(area.getId()));
			
			for (Structure structure : areaStructures){
				int numberFleet = 0;
				
				
				if(structure.getType()==Structure.TYPE_DEFENSE_HANGAR && structure.isActivated()){

					int lvl = Math.min(GameConstants.FLEET_MAX_POWERLEVEL/2, 1+structure.getModuleLevel(StructureModule.TYPE_LVL_PNJ));
					int number = 1+structure.getModuleLevel(StructureModule.TYPE_NUMBER_PNJ);
					for(Structure structure2 : areaStructures)
					{
						if(structure2!=structure &&
								structure2.getType()==Structure.TYPE_DEFENSE_HANGAR &&
								structure2.isActivated() &&
								structure2.getIdOwner()==structure.getIdOwner())
								
							number+= structure2.getModuleLevel(StructureModule.TYPE_NUMBER_PNJ);
					}
					int prod = structure.getModuleLevel(StructureModule.TYPE_PROD_PNJ);
					
					PnjDefender defender = DataAccess.getPnjDefenderByPlayer(structure.getIdOwner());
					
					if (defender == null) {
						String login = AiTools.PNJ_DEFENDER_AI_LOGIN + " - "+ structure.getOwner().getLogin();
						
						Player pnj = DataAccess.getPlayerByLogin(login);
						
						if (pnj == null) {
							pnj = new Player(login, Utilities.encryptPassword(
									Config.getBypassPassword()), "", "", "");
							pnj.setAi(true);
							pnj.save();
						}
						
						defender = new PnjDefender(structure.getIdOwner(), pnj.getId());
						defender.save();
					}
					
					int pnjId = defender.getIdPnj();
					Player pnj = DataAccess.getPlayerById(pnjId);
					
					if (pnj == null) {
						String login = AiTools.PNJ_DEFENDER_AI_LOGIN + " - "+ structure.getOwner().getLogin();
						
						pnj = DataAccess.getPlayerByLogin(login);
						
						if (pnj == null) {
							pnj = new Player(login, Utilities.encryptPassword(
									Config.getBypassPassword()), "", "", "");
							pnj.setAi(true);
							pnj.save();
						}
						
						synchronized (defender.getLock()) {
							defender = DataAccess.getEditable(defender);
							defender.setIdPnj(pnj.getId());
							defender.save();
						}
					}
					
					//Gestion un par source now
					for(Fleet fleet : areaFleets)
					{
						if(fleet.getName().contains("Defender") && fleet.getIdOwner()==pnjId)
							numberFleet++;
					}

					


						Point tile = structure.getFreeTile();
						if(numberFleet<number){
							if(tile!=null)
								try {
									if(Math.random()<prod*0.12)
										AiTools.createPirateFleet(lvl*2, tile.x, tile.y, area.getId(), pnj.getId());
								} catch (Exception e) {
									e.printStackTrace();
								}
								else
									mayCreatePirateFleet(area, lvl*2, prod*0.12, areasToUpdate, pnj.getId());
						}
				}
			}
		}

			for (Integer idArea : areasToUpdate)
				UpdateTools.queueAreaUpdate(idArea);
		

	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
		private void mayCreatePirateFleet(Area area, int power, double respawnProba, 
				List<Integer> areasToUpdate, int pnjId) {
			if (Math.random() < respawnProba) {
				Point location = area.getRandomFreeTile(Area.CHECK_HYPERJUMP_OUTPUT, null);
				try {
					AiTools.createPirateFleet(power, location.x, location.y, area.getId(), pnjId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!areasToUpdate.contains(area.getId()))
					areasToUpdate.add(area.getId());
			}
		}
}
