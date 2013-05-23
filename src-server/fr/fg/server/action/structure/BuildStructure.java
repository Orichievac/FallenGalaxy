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

import fr.fg.server.contract.NpcHelper;
import fr.fg.server.core.AiTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.PnjDefender;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Structure;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class BuildStructure extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static String PNJ_AI_LOGIN = "Defender";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int type = (Integer) params.get("type");
		
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
		
		// Vérifie que la flotte a suffisament de ressources
		int[] cost = Structure.getBaseCost(type);
		ItemContainer itemContainer = fleet.getItemContainer();
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			if (itemContainer.getResource(i) < cost[i])
				throw new IllegalOperationException("La flotte n'a pas " +
					"suffisament de ressources pour pouvoir construire la " +
					"structure.");
		
		player = Player.updateCredits(player);
		
		if (player.getCredits() < cost[4])
			throw new IllegalOperationException("Vous n'avez pas " +
				"suffisament de crédits pour pouvoir construire la " +
				"structure.");
		
		// Vérifie que le maximum de générateurs n'a pas déjà été atteint
		Area area = fleet.getArea();
		
		if (type == Structure.TYPE_GENERATOR) {
			List<Structure> playerStructures = player.getStructures();
			int generatorsCount = 0;
			
			synchronized (playerStructures) {
				for (Structure playerStructure : playerStructures) {
					if (playerStructure.getType() == Structure.TYPE_GENERATOR)
						generatorsCount++;
				}
			}
			
			if (generatorsCount >= 1 + player.getLevel() / 4)
				throw new IllegalOperationException("La limite de " +
					"générateurs a été atteinte. Détruisez d'autres " +
					"générateurs pour pouvoir en construire un nouveau.");
		}
		


		
		
		
		
		
		
		
		
		// Vérifie que l'emplacement est libre
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		area.checkValidStructureLocation(type, fleetX, fleetY);
		
		long idEnergySupplierStructure;
		int requiredEnergy = Structure.getEnergyConsumption(type);
		
		if (requiredEnergy > 0) {
			Structure energySupplierStructure = area.getEnergySupplierStructure(
				player, requiredEnergy, fleetX, fleetY);
			if (energySupplierStructure == null)
				throw new IllegalOperationException("Aucune source " +
					"d'énergie ne peut alimenter la structure.");
			
			idEnergySupplierStructure = energySupplierStructure.getId();
		} else {
			idEnergySupplierStructure = 0;
		}
		
		
		// Vérifie les hangars de défense
		// 1 Max par secteur
		if(type == Structure.TYPE_DEFENSE_HANGAR){
			
//			List<Structure> areaStructures = area.getStructures();
//			int hangarsCount = 0;
//			
//			
//			synchronized (areaStructures) {
//				for (Structure areaStructure : areaStructures) {
//					int structureAllyOwner = player.getIdAlly();
//					int checkOtherAllyOwner = areaStructure.getOwner().getIdAlly();
//					if (areaStructure.getType() == Structure.TYPE_DEFENSE_HANGAR &&
//							((structureAllyOwner!=0 && checkOtherAllyOwner==player.getIdAlly()) ||
//							(structureAllyOwner==0 && player.getId()==areaStructure.getIdOwner())))
//						hangarsCount++;
//				}
//			}
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
			
			// Genère le PNJ
			if(DataAccess.getPnjDefenderByPlayer(player.getId()) == null)
				{
				
				String login = AiTools.PNJ_DEFENDER_AI_LOGIN + " - "+ player.getLogin();
				
				if(DataAccess.getPlayerByLogin(login)==null){
					
					
					Player pnj = new Player(login, Utilities.encryptPassword(
							Config.getBypassPassword()), "", "", "");
					pnj.setAi(true);
					pnj.save();
					
					PnjDefender defender = new PnjDefender(player.getId(), pnj.getId());
					defender.save();
									
					//Problème
					//-Si le joueur quitte l'alliance
					
					//Problème
					//Gérer l'attaque des PNJ
					//Si login = pirate X alos attaque normal ou alors fleet.name pour pas accedder au player
					//Sinon attaque les ennemis
					//Génaération de flotte avec nom spécial=> done
					//50%/25% d'attaquer par heure? lvl des flottes?
					
					//Seul la plus haut structure alliance compte?
					//n²
					
					// 1 maxi par source d'energie?
					}
				else
					{
					LoggingSystem.getServerLogger().error("Création d'un NPC pour hangar buggé" +
							" pour le joueur "+player.getLogin()+"("+player.getId()+")");
					}
				}
			
			
			
		}
		
		
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
			for(Structure structure : structures)
			{
				if(structure.getType()==Structure.TYPE_EXPLOITATION_TITANE ||
						structure.getType()==Structure.TYPE_EXPLOITATION_CRISTAL ||
						structure.getType()==Structure.TYPE_EXPLOITATION_ANDIUM ||
						structure.getType()==Structure.TYPE_EXPLOITATION_ANTIMATIERE||
						structure.getType()==Structure.TYPE_LABORATORY ||
						structure.getType()==Structure.TYPE_INFRASTRUCTURE ){
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
		
		
		// Supprime les ressources de la flotte
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.doAction(Fleet.CURRENT_ACTION_BUILD_STRUCTURE, Utilities.now() +
					Skill.SKILL_ENGINEER_MOVEMENT_RELOAD);
			fleet.addXp(Structure.getXp(type));
			fleet.save();
		}
		
		synchronized (itemContainer.getLock()) {
			itemContainer = DataAccess.getEditable(itemContainer);
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			{
				if(cost[i]!=0)
					itemContainer.addResource(-cost[i], i);
			}
			itemContainer.save();
		}
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-cost[4]);
			player.save();
		}
		
		// Construit la structure
		String name = Messages.getString("structure" + type);
		if (name.length() > 20)
			name = name.substring(0, 20);
		
		Structure structure = new Structure(type,
			name, Structure.getDefaultHull(type),
			fleetX, fleetY, idEnergySupplierStructure,
			player.getId(), fleet.getIdCurrentArea());
		structure.save();
		
		if (idEnergySupplierStructure == 0) {
			synchronized (structure.getLock()) {
				structure = DataAccess.getEditable(structure);
				structure.setIdEnergySupplierStructure(structure.getId());
				structure.save();
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
