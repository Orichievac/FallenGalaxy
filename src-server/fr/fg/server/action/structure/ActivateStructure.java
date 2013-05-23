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

package fr.fg.server.action.structure;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.data.StructureSkill;
import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class ActivateStructure extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		long idStructure = (Long) params.get("structure");
		boolean activate = (Boolean) params.get("activate");
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
				idStructure, player.getId());
		
		// Vérifie que la structure est activée
		if (activate == structure.isActivated())
			throw new IllegalOperationException("La structure est déjà " +
				(activate ? "activée." : "désactivé."));
		
		long idEnergySupplierStructure = 0;
		
		if (activate) {
			// Vérifie que la structure n'est pas en cours de démontage
			Area area = structure.getArea();
			List<Fleet> fleets = area.getFleets();
			
			synchronized (fleets) {
				for (Fleet fleet : fleets) {
					if (fleet.getIdOwner() == player.getId() &&
							structure.getBounds().contains(
							fleet.getCurrentX(), fleet.getCurrentY()) &&
							fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE))
						throw new IllegalOperationException("Une flotte est " +
							"déjà en train de démonter la structure.");
				}
			}
			
			

			
			
			// Vérifie qu'il y a une source d'énergie disponible
			int requiredEnergy = structure.getEnergyConsumption();
			if (requiredEnergy > 0) {
				Structure energySupplierStructure = area.getEnergySupplierStructure(
					player, requiredEnergy, structure.getX(), structure.getY());
				
				if (energySupplierStructure == null)
					throw new IllegalOperationException("Aucune source " +
						"d'énergie ne peut alimenter la structure.");
				
				idEnergySupplierStructure = energySupplierStructure.getId();
				
				
				// Vérifie que le maximum de structure de production n'a pas déjà été atteint
				int type=structure.getType();
				if(type==Structure.TYPE_EXPLOITATION_TITANE ||
						type==Structure.TYPE_EXPLOITATION_CRISTAL ||
						type==Structure.TYPE_EXPLOITATION_ANDIUM ||
						type==Structure.TYPE_EXPLOITATION_ANTIMATIERE||
						type==Structure.TYPE_LABORATORY ||
						type==Structure.TYPE_INFRASTRUCTURE)
				{
					List<Structure> structures = DataAccess.getStructuresByEnergySupplier(idEnergySupplierStructure);
					int countProdStructure=0;
					for(Structure structureArea : structures)
					{
						if(structureArea.getType()==Structure.TYPE_EXPLOITATION_TITANE ||
								structureArea.getType()==Structure.TYPE_EXPLOITATION_CRISTAL ||
								structureArea.getType()==Structure.TYPE_EXPLOITATION_ANDIUM ||
								structureArea.getType()==Structure.TYPE_EXPLOITATION_ANTIMATIERE ||
								structureArea.getType()==Structure.TYPE_LABORATORY ||
								structureArea.getType()==Structure.TYPE_INFRASTRUCTURE ){
							countProdStructure++;
						}
					}
					
					if(countProdStructure>=GameConstants.MAX_PRODUCTION_STRUCTURE+
							structure.getArea().getSector().getType()*2)
							throw new IllegalOperationException("Il y a trop de structures" +
								" de production pour la source d'énergie. (Limite dans ce quadrant : "+
								(GameConstants.MAX_PRODUCTION_STRUCTURE+structure.getArea().getSector().getType()*2)
								+ ")");
					
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
			} else {
				idEnergySupplierStructure = structure.getId();
				
			}
		} else {
			// Vérifie que la capacité de stockage de la structure n'est pas
			// en cours d'utilisation
			if (structure.getPayload() > 0) {
				Area area = structure.getArea();
				List<Structure> structures = area.getStructures();
				
				long storehousesPayload = 0;
				synchronized (structures) {
					for (Structure areaStructure : structures)
						if (areaStructure.getIdOwner() == structure.getIdOwner())
							storehousesPayload += areaStructure.getPayload();
				}
				
				StorehouseResources storehouseResources = structure.getArea(
					).getStorehouseResourcesByPlayer(player.getId());
				
				double areaResources = 0;
				if (storehouseResources != null) {
					for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
						areaResources += storehouseResources.getResource(i);
				}
				
				if (storehousesPayload - structure.getPayload() < areaResources)
					throw new IllegalOperationException("Videz les ressources " +
						"stockées sur la structure avant de la désactiver.");
			}
			
			if(structure.getType()==Structure.TYPE_SPACESHIP_YARD)
			{
				StructureSpaceshipYard spaceshipYard = structure.getSpaceshipYard();
				if(spaceshipYard.isBuilding())
					throw new IllegalOperationException("Vous ne pouvez pas désactiver" +
							" un chantier spatial produisant des vaisseaux !");
				
				if(!spaceshipYard.isEmpty())
					throw new IllegalOperationException("Vous ne pouvez pas désactiver" +
					" un chantier spatial contenant des vaisseaux !");
			}
			
		}
		
		
		
		if (!activate) {
			List<Structure> structures = new ArrayList<Structure>(
				DataAccess.getStructuresByEnergySupplier(structure.getId()));
			
			for (Structure suppliedStructure : structures) {
				if (suppliedStructure.getId() != structure.getId()) {
					synchronized (suppliedStructure.getLock()) {
						suppliedStructure = DataAccess.getEditable(suppliedStructure);
						suppliedStructure.setIdEnergySupplierStructure(0);
						suppliedStructure.save();
					}
				}
			}
		}
		
		
		
		synchronized (structure.getLock()) {
			structure = DataAccess.getEditable(structure);
			structure.setIdEnergySupplierStructure(idEnergySupplierStructure);
			
			List<StructureSkill> structuresSkills = structure.getSkills();
			for(int i=0; i< structuresSkills.size();i++)
			{
				StructureSkill skillI = structuresSkills.get(i);
				
				synchronized(skillI.getLock()){
					skillI = DataAccess.getEditable(structuresSkills.get(i));
					skillI.setReload(Utilities.now()+skillI.getReloadLength());
					skillI.save();
				}
			}
			
			structure.save();
		}
		
		UpdateTools.queueAreaUpdate(structure.getIdArea(), player.getId(),
				new Point(structure.getX(), structure.getY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerGeneratorsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
