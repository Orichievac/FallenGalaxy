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

import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Sector;
import fr.fg.server.data.StarSystem;

public class UpdateStrategicValue extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public void run() {
		this.setName("UpdateStrategicValue (hourly)");
		List<Sector> sectors = new ArrayList<Sector>(DataAccess.getAllSectors());
		
		for (Sector sector : sectors) {
			List<Area> areas = new ArrayList<Area>(sector.getAreas());
			long systemsCount = 0, colonizedSystems = 0;
			
			for (Area area : areas) {
				List<StarSystem> systems = area.getSystems();
				
				synchronized (systems) {
					for (StarSystem system : systems) {
						if (!system.isAi()) {
							systemsCount++;
							if (system.getIdOwner() != 0)
								colonizedSystems++;
						}
					}
				}
			}
			// TODO update systems en cas de changement de la valeur
			synchronized (sector.getLock()) {
				Sector newSector = DataAccess.getEditable(sector);
				if (systemsCount > 0)
					newSector.setStrategicValue((int) (
						Sector.getBaseStrategicValue(sector.getType()) +
						Sector.getSystemsStrategicValueRange(sector.getType()) *
						colonizedSystems / systemsCount));
				else
					newSector.setStrategicValue(
						Sector.getBaseStrategicValue(sector.getType()));
				newSector.save();
			}
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
