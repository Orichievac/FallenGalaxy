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

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.StarSystem;
import fr.fg.server.util.Utilities;

public class UpdateExtractorCenters extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateExtractorCenters (hourly)");
		List<Integer> playersToUpdate = new ArrayList<Integer>();
		List<StarSystem> systems = new ArrayList<StarSystem>(DataAccess.getAllSystems());
		long now = Utilities.now();
		
		for (StarSystem system : systems) {
			if (system.getIdOwner() == 0 || system.isAi())
				continue;
			
			Building currentBuilding = system.getCurrentBuilding();
			Building nextBuilding = system.getNextBuilding();
			
			if ((currentBuilding != null &&
					currentBuilding.getType() == Building.EXTRACTOR_CENTER &&
					system.getCurrentBuildingEnd() < now) || (
					nextBuilding != null &&
					nextBuilding.getType() == Building.EXTRACTOR_CENTER &&
					system.getNextBuildingEnd() < now)) {
				system = StarSystem.updateSystem(system);
			}
			
			int production = (int) system.getProduction(Building.EXTRACTOR_CENTER);
			
			if (production > 0) {
				int resourceIndex = (int) (Math.random() * GameConstants.RESOURCES_COUNT);
				
				synchronized (system) {
					system = DataAccess.getEditable(system);
					system.addResource(
						production * (Math.random() * .5 + .75) *
						(resourceIndex == 3 ? .5 : 1) *
						system.getProductionModifier(),
						resourceIndex);
					system.save();
				}
				
				if (!playersToUpdate.contains(system.getIdOwner()))
					playersToUpdate.add(system.getIdOwner());
			}
		}
		
		for (int idPlayer : playersToUpdate)
			UpdateTools.queuePlayerSystemsUpdate(idPlayer);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
