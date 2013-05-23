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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureModule;
import fr.fg.server.data.StructureSkill;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class UpgradeModule extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		long idStructure = (Long) params.get("structure");
		int type = (Integer) params.get("type");
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
				idStructure, player.getId());
		
		// Vérifie que la structure peut recevoir le module
		if (!structure.isValidModule(type))
			throw new IllegalOperationException(
				"Module incompatible avec la structure.");
		
		// Vérifie que la structure est activée
		if (!structure.isActivated())
			throw new IllegalOperationException("La structure est désactivée.");
		
		// Vérifie que le module n'est pas au niveau maximum
		if (structure.getModuleLevel(type) >= StructureModule.getMaxLevel(type))
			throw new IllegalOperationException("Le module a été amélioré au niveau maximum.");
		
		// Vérifie que la structure n'est pas au niveau maximum du quadrant
//		if (structure.getLevel() >= structure.getArea().getSector().getLvlMax())
//			throw new IllegalOperationException("La structure ne doit passer dépasser la " +
//					"limite du quadrant : "+ structure.getArea().getSector().getLvlMax());
		
//		int idTechnology=1;
//		switch(type){
//		case StructureModule.TYPE_HULL:
//			idTechnology=200+structure.getModuleLevel(type)+1;
//			break;
//		case StructureModule.TYPE_RESOURCES_PAYLOAD:
//			idTechnology=300+structure.getModuleLevel(type)+1;
//			break;
//		}
		
		// TODO : Vérifie que la recherche pour le module est faite :
//		if(!player.hasResearchedTechnology(idTechnology))
//			throw new IllegalOperationException("Vous n'avez pas complété " +
//					"la recherche pour ce module");
//		;
		
		// Calcule le prix de base de la structure
		int[] structureCost = Structure.getBaseCost(structure.getType());
		int baseCost = 0;
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			baseCost += structureCost[i];
		
		// Calcule le prix de l'amélioration du module
		long[] cost = structure.getModuleCost(type, structure.getModuleLevel(type) + 1);
		
		// Vérifie que le joueur a suffisament de ressources
		StorehouseResources storehouseResources = structure.getArea(
			).getStorehouseResourcesByPlayer(player.getId());
		
		long[] playerResources = new long[GameConstants.RESOURCES_COUNT];
		if (storehouseResources != null) {
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				playerResources[i] = storehouseResources.getResource(i);
		}
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			if (playerResources[i] < cost[i])
				throw new IllegalOperationException("Vous n'avez pas " +
						"suffisament de ressources pour pouvoir améliorer le " +
						"module.");
		
		player = Player.updateCredits(player);
		
		if (player.getCredits() < cost[4])
			throw new IllegalOperationException("Vous n'avez pas " +
				"suffisament de crédits pour pouvoir améliorer le " +
				"module.");
		
		
		
		
		if(type==StructureModule.TYPE_LVL_PNJ)
		{
			if(4+structure.getModuleLevel(type)*2>structure.getOwner().getLevel())
				throw new IllegalOperationException("Vous n'avez pas " +
						"le niveau nécessaire pour améliorer ce module. " +
						"Votre niveau doit dépasser celui des PNJ créés.");
		}
		
		
		int maxHullBefore = structure.getMaxHull();
		
		// Supprime les ressources
		if (storehouseResources != null) {
			synchronized (storehouseResources.getLock()) {
				storehouseResources = DataAccess.getEditable(storehouseResources);
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
					storehouseResources.addResource(-cost[i], i);
				storehouseResources.save();
			}
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-cost[4]);
			player.save();
		}
		
		
		// Augmente le niveau du module
		List<StructureModule> modules = new ArrayList<StructureModule>(structure.getModules());
		boolean found = false;
		
		for (StructureModule module : modules) {
			if (module.getType() == type) {
				synchronized (module.getLock()) {
					module = DataAccess.getEditable(module);
					module.setLevel(module.getLevel() + 1);
					module.save();
				}
				found = true;
				break;
			}
		}
		
		if (!found) {
			StructureModule module = new StructureModule(structure.getId(), type, 1);
			module.save();
		}
		
		synchronized (structure) {
			structure = DataAccess.getEditable(structure);
			structure.updateModulesCache();
			structure.setHull(structure.getHull() + structure.getMaxHull() - maxHullBefore);
			structure.save();
		}
		
		//Si la structure à des capacités, il faut mettre à jour
		//le cooldown.
		if(structure.getSkills()!=null)
		{
			if(structure.getSkills().size()==1)
			{
				StructureSkill skill=structure.getSkills().get(0);
				synchronized(skill.getLock())
				{
					skill = DataAccess.getEditable(skill);
					if(skill.getReloadLength()+Utilities.now()<skill.getReload())
					{
						skill.setLastUse(Utilities.now());
						skill.setReload(skill.getReloadLength()+Utilities.now());
					}
					skill.save();
				}
			}
		}
		
		// Mise à jour du secteur
		UpdateTools.queueAreaUpdate(structure.getIdArea(), player.getId(),
			false, new Point(structure.getX(), structure.getY()));

		List<Update> updates = new ArrayList<Update>();
		
		updates.add(Update.getAreaUpdate());
		updates.add(Update.getPlayerSystemsUpdate());
		if (structure.getType() == Structure.TYPE_GENERATOR)
			updates.add(Update.getPlayerGeneratorsUpdate());
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
