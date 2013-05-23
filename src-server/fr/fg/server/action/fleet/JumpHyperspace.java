/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

package fr.fg.server.action.fleet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class JumpHyperspace extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Paramètres de l'action
		String type	= (String) params.get("type");
		int target	= (Integer) params.get("target");
		
		// Vérifie que la destination est valide
		Area area = null;
		Sector sector = null;
		
		int lvlMin=0;
		
		int lvlMax=10;
		
		if (type.equals("area")) {
			area = DataAccess.getAreaById(target);
			
			// Vérifie que le secteur existe
			if (area == null)
				throw new IllegalOperationException(
						"Le secteur de destination n'existe pas.");
			lvlMin=1;
			lvlMax=100;
			

		} else if (type.equals("sector")) {
			sector = DataAccess.getSectorById(target);
			
			// Vérifie que le secteur existe
			if (sector == null)
				throw new IllegalOperationException(
						"Le quadrant de destination n'existe pas.");
			lvlMin=sector.getLvlMin();
			lvlMax=sector.getLvlMax();
				
		}
		
		// Compte le nombre de flottes à faire sauter
		int fleetsCount = 0;
		for (int i = 0; i < 9; i++) {
			if (params.get("fleet" + i) == null)
				break;
			fleetsCount++;
		}
		
		// Charge les flottes
		Fleet[] fleets = new Fleet[fleetsCount];

		for (int i = 0; i < 9; i++) {
			if (params.get("fleet" + i) == null)
				break;
			
			int idFleet = (Integer) params.get("fleet" + i);
			Fleet fleet = FleetTools.getFleetByIdWithChecks(
					idFleet, player.getId());
			
			if (i > 0 && fleet.getIdCurrentArea() != fleets[0].getIdCurrentArea())
				throw new IllegalOperationException(
					"Sélection de flottes invalide.");
			
			if(type.equals("area") && 
					area.getGeneralType()==Area.AREA_GENERAL_MINING_X5 &&
					player.getLevel()<area.getSector().getLvlMin()+5)
						throw new IllegalOperationException("Vous n'avez pas" +
						" un niveau suffisant pour accéder à ce secteur.");
			
		
			//if((fleet.getPowerLevel() < lvlMin) && (fleet.getArea().getSector().getLvlMax()<= lvlMin))
			if(player.getLevel()<lvlMin){
					
					throw new IllegalOperationException("Vous n'avez pas" +
							" un niveau suffisant pour accéder à ce quadrant.");
				}
			
			
			if(player.getLevel()<lvlMin+5 && area!=null && area.getGeneralType()==Area.AREA_GENERAL_MINING_X5)
			{
				throw new IllegalOperationException("Vous n'avez pas" +
					" un niveau suffisant pour accéder à ce secteur.");
			}
			
//			if(fleet.getPowerLevel() > lvlMax)
//			{
//				
//				throw new IllegalOperationException("Vous avez un" +
//						" niveau trop élevé pour accéder à ce quadrant.");
//			}


			
			fleets[i] = fleet;
		}
		
		// Secteur de la flotte avant le saut
		int currentArea = fleets[0].getIdCurrentArea();
		Point[] locations = new Point[fleetsCount];
		
		for (int i = 0; i < fleetsCount; i++) {
			Fleet fleet = fleets[i];
			locations[i] = new Point(fleet.getX(), fleet.getY());
			
			// Effectue le saut hyperspatial
			if (type.equals("area")) {
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.jumpHyperspace(area);
					fleet.save();
				}
			} else if (type.equals("sector")) {
				synchronized (fleet.getLock()) {
					fleet = DataAccess.getEditable(fleet);
					fleet.jumpHyperspace(sector);
					fleet.save();
				}
			}
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(currentArea, player.getId(), locations);
		
		ArrayList<Update> updates = new ArrayList<Update>();
		updates.add(Update.getAreaUpdate());
		for (Fleet fleet : fleets)
			updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
