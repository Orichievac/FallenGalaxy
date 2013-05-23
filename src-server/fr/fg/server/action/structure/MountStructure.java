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

package fr.fg.server.action.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class MountStructure extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int itemIndex = (Integer) params.get("item");
		
		// Flotte qui construit la structure
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		// Vérifie que la flotte a la compétence ingénieur
		if (fleet.getSkillLevel(Skill.SKILL_ENGINEER) == -1)
			throw new IllegalOperationException("La flotte n'a pas " +
					"la compétence ingénieur.");
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir construire.");
		
		ItemContainer itemContainer = fleet.getItemContainer();
		Item structureItem = itemContainer.getItem(itemIndex);
		
		if (structureItem.getType() != Item.TYPE_STRUCTURE)
			throw new IllegalOperationException("Seules les structures " +
				"peuvent être assemblées.");
		
		// Vérifie que l'emplacement est libre
		Structure structure = structureItem.getStructure();
		Area area = fleet.getArea();
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		area.checkValidStructureLocation(structure.getType(), fleetX, fleetY);
		
		long idEnergySupplierStructure;
		int requiredEnergy = structure.getEnergyConsumption();
		
		if (requiredEnergy > 0) {
			Structure energySupplierStructure = area.getEnergySupplierStructure(
				player, requiredEnergy, fleetX, fleetY);
			if (energySupplierStructure == null)
				throw new IllegalOperationException("Aucune source " +
					"d'énergie ne peut alimenter la structure.");
			
			idEnergySupplierStructure = energySupplierStructure.getId();
		} else {
			idEnergySupplierStructure = structure.getId();
		}
		
		if (idEnergySupplierStructure == 0)
			throw new IllegalOperationException("Aucune source " +
				"d'énergie ne peut alimenter la structure.");
		
		int type = structure.getType();
		// Vérifie que le maximum de structure de production n'a pas déjà été atteint
		if(type==Structure.TYPE_EXPLOITATION_TITANE ||
				type==Structure.TYPE_EXPLOITATION_CRISTAL ||
				type==Structure.TYPE_EXPLOITATION_ANDIUM ||
				type==Structure.TYPE_EXPLOITATION_ANTIMATIERE||
				type==Structure.TYPE_LABORATORY ||
				type==Structure.TYPE_INFRASTRUCTURE )
		{
			List<Structure> structures = DataAccess.getStructuresByEnergySupplier(idEnergySupplierStructure);
			int countProdStructure=0;
			for(Structure areaStructure : structures)
			{
				if(areaStructure.getType()==Structure.TYPE_EXPLOITATION_TITANE ||
						areaStructure.getType()==Structure.TYPE_EXPLOITATION_CRISTAL ||
						areaStructure.getType()==Structure.TYPE_EXPLOITATION_ANDIUM ||
						areaStructure.getType()==Structure.TYPE_EXPLOITATION_ANTIMATIERE||
						areaStructure.getType()==Structure.TYPE_LABORATORY ||
						areaStructure.getType()==Structure.TYPE_INFRASTRUCTURE ){
					countProdStructure++;
				}
			}
			
			if(countProdStructure>=GameConstants.MAX_PRODUCTION_STRUCTURE+
					fleet.getArea().getSector().getType()*2)
					throw new IllegalOperationException("Il y a trop de structures" +
						" de production pour la source d'énergie" + " (Limite dans ce quadrant : "+
							(GameConstants.MAX_PRODUCTION_STRUCTURE+fleet.getArea().getSector().getType()*2)
							+ " par source d'énergie)");
			
		}
		
		
		if(type == Structure.TYPE_DEFENSE_HANGAR){
			
			List<Structure> sourceStructures = DataAccess.getStructuresByEnergySupplier(idEnergySupplierStructure);
			int hangarsCount = 0;
			
			
			synchronized (sourceStructures) {
				for (Structure sourceStructure : sourceStructures) {
					if (sourceStructure.getType() == Structure.TYPE_DEFENSE_HANGAR)
						hangarsCount++;
				}
			}
			
			if(hangarsCount>0)
				throw new IllegalOperationException("Un seul hangar" +
						" de défense peut être créé par source d'énergie.");
		}	
		
		
		// Lance l'assemblage de la structure
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_MOUNT_STRUCTURE, Utilities.now() +
				(6 + structure.getLevel() / 2) * 3600);
			fleet.save();
		}
		
		if (itemIndex != 0) {
			synchronized (itemContainer.getLock()) {
				itemContainer = DataAccess.getEditable(itemContainer);
				Item item1 = itemContainer.getItem(itemIndex);
				Item item2 = itemContainer.getItem(0);
				itemContainer.setItem(item2, itemIndex);
				itemContainer.setItem(item1, 0);
				itemContainer.save();
			}
		}
		
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId());
		
		List<Update> updates = new ArrayList<Update>();
		
		updates.add(Update.getAreaUpdate());
		updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		if (structure.getType() == Structure.TYPE_GENERATOR)
			updates.add(Update.getPlayerGeneratorsUpdate());
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
