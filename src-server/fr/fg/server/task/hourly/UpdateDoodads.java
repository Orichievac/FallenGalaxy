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
import fr.fg.server.data.StellarObject;

public class UpdateDoodads extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void run() {
		this.setName("UpdateDoodads (hourly)");
		List<Area> areas = new ArrayList<Area>(DataAccess.getAllAreas());

		for (Area area : areas) {
			List<StellarObject> objects = new ArrayList<StellarObject>(
					DataAccess.getObjectsByArea(area.getId()));
			
			// Compte le nombre de doodads dans la zone
			int count = 0;
			int[] typesCount = new int[5];
			for (StellarObject object : objects)
				if (object.getType().equals(StellarObject.TYPE_DOODAD)) {
					int type = object.getVariant();
					
					if (type < typesCount.length) {
						count++;
						typesCount[type]++;
					}
				}
			
			double[] probabilities = new double[typesCount.length];
			double probabilitiesSum = 0;
			
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = 1. / (1 + typesCount[i]);
				probabilitiesSum += probabilities[i];
			}
			
			// Ratio doodads / nombres de cases secteur = 1 / 1500
			int maxDoodadsCount =
				area.getWidth() * area.getHeight() / 1500;
			
			doodads:for (int i = count; i < maxDoodadsCount; i++) {
				// Un doodad récupéré met en moyenne 6h pour être regénéré
				if (Math.random() < .1667) {
					// Génère une position aléatoire dans le système
					// pour la flotte
					int x, y;
					count = 0;
					do {
						x = (int) (Math.random() * area.getWidth());
						y = (int) (Math.random() * area.getHeight());

						if (++count == 100)
							continue doodads;
					} while (!area.isFreeTile(x, y, Area.NO_SYSTEMS |
						Area.NO_OBJECTS | Area.NO_FLEETS | Area.NO_STRUCTURES, null));
					
					double random = Math.random() * probabilitiesSum;
					double value = 0;
					
					for (int j = 0; j < probabilities.length; j++) {
						value += probabilities[j];
						
						if (random < value) {
							count++;
							typesCount[j]++;
							
							probabilitiesSum = 0;
							for (int k = 0; k < probabilities.length; k++) {
								probabilities[k] = 1. / (1 + typesCount[k]);
								probabilitiesSum += probabilities[k];
							}
							
							StellarObject doodad = new StellarObject(x, y,
								StellarObject.TYPE_DOODAD, j, area.getId());
							doodad.save();
							break;
						}
					}
					
					// TODO jgottero signaler update
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
