/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier, Nicolas Bosc

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

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureModule;
import fr.fg.server.util.LoggingSystem;

public class RepairStructures extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("RepairStructures (hourly)");
		
		List<Structure> structures = new ArrayList<Structure>(
				DataAccess.getAllStructures());
		List<Long> cdf = new ArrayList<Long>();
		
		for(Structure structure : structures){
			if(structure.getType()==Structure.TYPE_FORCE_FIELD && structure.isActivated()){
				cdf.add(structure.getId());
			}
		}
		
		structures.clear();
		
		for(long idForceField : cdf)
		{
			if(idForceField == 4979)
				LoggingSystem.getServerLogger().error("ForceField detected : "+idForceField);
			Structure structure = DataAccess.getStructureById(idForceField);
				
				int repairLevel = structure.getModuleLevel(StructureModule.TYPE_REPAIR);
				int i=repairLevel;
				
				if(idForceField == 4979)
					LoggingSystem.getServerLogger().error("repairLevel : "+repairLevel);
				
				List<Structure> structuresInForceField=
					structure.getAllyStructuresWithinRange(Structure.FORCE_FIELD_RADIUS);
				
				if(idForceField == 4979)
					LoggingSystem.getServerLogger().error("structuresInForceField size : "+structuresInForceField.size());
				
				synchronized(structuresInForceField){
				
				while (repairLevel > 0 && i>0) {
					
					List<Long> ids = new ArrayList<Long>();
					
					if(structure.getHull()<structure.getMaxHull()){
						if(idForceField == 4979)
							LoggingSystem.getServerLogger().error("auto repair");
						ids.add(structure.getId());
						repairLevel--;
					}
					
					for(Structure allyStructure : structuresInForceField)
					{
						if(idForceField == 4979)
							LoggingSystem.getServerLogger().error("allyStructureId : "+allyStructure.getId());
						synchronized(allyStructure.getLock())
						{
							if (allyStructure.getHull() < allyStructure.getMaxHull() &&
									  repairLevel>0) {
								ids.add(allyStructure.getId());
								if(idForceField == 4979)
									LoggingSystem.getServerLogger().error("+1 to : "+allyStructure.getId());
								repairLevel--;
							}
						}
					}

					
					for(long id : ids)
					{
						Structure healed = DataAccess.getStructureById(id);
						synchronized (healed.getLock()) {
							healed = DataAccess.getEditable(healed);
							healed.setHull(Math.min(healed.getMaxHull(),
									healed.getHull()+1));
							if(idForceField == 4979)
								LoggingSystem.getServerLogger().error("+1 done to : "+healed.getId());
							
							healed.save();
						}
					}
					i--;
				}
			}
		}
	}
	

	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
