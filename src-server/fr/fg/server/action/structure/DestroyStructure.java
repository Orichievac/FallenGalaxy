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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.LoggingSystem;

public class DestroyStructure extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Paramètres de l'action
		long idStructure = (Long) params.get("structure");
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
				idStructure, player.getId());
		
		
		// Si la structure est un silo, alors on vérifie si la capacité
		// max dans le secteur à changer
		if(structure.getType()==Structure.TYPE_STOREHOUSE)
			deleteExtraResources(structure);
		
		// Détruit la structure
		structure.delete();
		
		// Si une flotte était en train de démonter la structure, l'action est
		// annulée
		List<Fleet> fleets = structure.getArea().getFleets();

		List<Update> updates = new ArrayList<Update>();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				if (structure.getBounds().contains(
						fleet.getCurrentX(), fleet.getCurrentY()) &&
						fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE)) {
					synchronized (fleet.getLock()) {
						fleet = DataAccess.getEditable(fleet);
						fleet.doAction(Fleet.CURRENT_ACTION_NONE, 0);
						fleet.save();
					}
					updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
				}
			}
		}
		
		Effect effect = new Effect(structure.getSize().getWidth() > 1 ||
				structure.getSize().getHeight() > 1 ?
					Effect.TYPE_LARGE_STRUCTURE_DESTRUCTION :
					Effect.TYPE_SMALL_STRUCTURE_DESTRUCTION,
			structure.getX(), structure.getY(),
			structure.getIdArea());
		
		UpdateTools.queueEffectUpdate(effect, player.getId(), false);
		
		// Met à jour le secteur
		UpdateTools.queueAreaUpdate(structure.getIdArea(), player.getId());
		
		updates.add(Update.getAreaUpdate());
		updates.add(Update.getEffectUpdate(effect));
		if (structure.getType() == Structure.TYPE_GENERATOR)
			updates.add(Update.getPlayerGeneratorsUpdate());
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	
	public void deleteExtraResources(Structure structure){
		// Recupère les ressources du joueur dans l'area
		long areaResource=0;
		StorehouseResources playerAreaRessource = null;
		List<StorehouseResources> storehouseResources=
		DataAccess.getStorehouseResourcesByArea(structure.getIdArea());
		
		for(StorehouseResources storehouseResource : storehouseResources)
		{
			if(storehouseResource.getPlayer()==structure.getOwner()){
				areaResource=storehouseResource.getRessourceCount();
				playerAreaRessource = storehouseResource;
			}
		}
		
		// Recupère la capacité max du joueur dans l'area
		long capacity=0;
		List<Structure> areaStructures = 
			new ArrayList<Structure>(DataAccess.getStructuresByArea(structure.getIdArea()));
		for(Structure areaStructure : areaStructures)
		{
			if(areaStructure.getType()==Structure.TYPE_STOREHOUSE &&
					areaStructure.getOwner()==structure.getOwner())
			{
				capacity+=areaStructure.getPayload();
			}
		}
		
		if(capacity-structure.getPayload()>0)
		{
			long newCapacity = (long) (capacity-structure.getPayload());
			if(newCapacity-areaResource<0)
			{
				
				int random= (int) (Math.random()*4);
				if(playerAreaRessource!=null){
					for(int i=0;i<4;i++)
					{
						areaResource = playerAreaRessource.getRessourceCount();
						long dif = areaResource-newCapacity;
						int type = (i+random)%4;
						if(dif<0)
							break;
						
						if(playerAreaRessource.getResource(type)>dif)
						{
							long toDelete = playerAreaRessource.getResource(type)-dif;
							synchronized(playerAreaRessource)
							{
								playerAreaRessource = DataAccess.getEditable(playerAreaRessource);
								playerAreaRessource.setResource(toDelete, type);
								playerAreaRessource.save();
							}	
						}
						else
						{
							synchronized(playerAreaRessource)
							{
								playerAreaRessource = DataAccess.getEditable(playerAreaRessource);
								playerAreaRessource.setResource(0, type);
								playerAreaRessource.save();
							}
						}
					}
				}
				
			}
		}
		else // On remet à zéro si le silo était le seul dans le secteur
		{
			if(playerAreaRessource!=null){
				synchronized(playerAreaRessource)
				{
					playerAreaRessource = DataAccess.getEditable(playerAreaRessource);
					playerAreaRessource.resetResources();
					playerAreaRessource.save();
				}
			}
			
		}
	}
}
