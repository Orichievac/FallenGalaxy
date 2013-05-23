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

import fr.fg.server.core.TerritoryManager;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Structure;

public class UpdateTerritories extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateTerritories (hourly)");
		List<Area> areas = new ArrayList<Area>(DataAccess.getAllAreas());
		
		for (Area area : areas) {
			// Calcule le nombre de puits gravitationnels
			List<StellarObject> objects = new ArrayList<StellarObject>(area.getObjects());
			int gravityWellsCount = 0;
			
			for (StellarObject object : objects) {
				if (object.getType() == StellarObject.TYPE_GRAVITY_WELL)
					gravityWellsCount++;
			}
			
			// Calcule le nombre de générateurs par alliance
			List<Structure> structures = new ArrayList<Structure>(area.getStructures());
			int[] generators = new int[gravityWellsCount];
			int generatorIndex = 0;
			
			for (Structure structure : structures) {
				if (structure.getType() == Structure.TYPE_GENERATOR)
					generators[generatorIndex++] = structure.getOwner().getIdAlly();
			}
			
			// Détermine l'alliance qui domine le secteur (50% des puits de
			// gravités contrôlés + 1)
			int idDominatingAlly = 0;
			
			for (int i = 0; i < gravityWellsCount; i++) {
				int idAlly = generators[i];
				if (idAlly == 0)
					continue;
				
				int count = 1;
				
				for (int j = 0; j < generators.length; j++) {
					if (i != j && generators[i] == generators[j])
						count++;
				}
				
				if (count >= gravityWellsCount / 2 + 1) {
					idDominatingAlly = idAlly;
					break;
				}
			}
			
			if (idDominatingAlly != area.getIdDominatingAlly()) {
				int idDominatingAllyBefore = area.getIdDominatingAlly();
				
				synchronized (area.getLock()) {
					area = DataAccess.getEditable(area);
					area.setIdDominatingAlly(idDominatingAlly);
					area.save();
				}
				
				if (idDominatingAllyBefore != 0)
					UpdateTools.queueProductsUpdate(DataAccess.getAllyById(
						idDominatingAllyBefore).getMembers());
				if (idDominatingAlly != 0)
					UpdateTools.queueProductsUpdate(DataAccess.getAllyById(
						idDominatingAlly).getMembers());
			}
		}
		
		TerritoryManager.getInstance().updateAllTerritoriesMap();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
