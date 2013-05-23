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

package fr.fg.server.action.systems;

import java.util.Map;


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.ArrayUtils;

public class Build extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idSystem			 	= (Integer) params.get("system");
		String currentBuildingId	= (String)  params.get("build_id");
		int currentBuildingLevel 	= (Integer) params.get("build_level");
		String nextBuildingId	 	= (String)  params.get("queue_id");
		int nextBuildingLevel	 	= (Integer) params.get("queue_level");
		String thirdBuildingId		= (String)	params.get("third_id");
		int thirdBuildingLevel		= (Integer)	params.get("third_level");
		// Recherche l'identifiant du batiment en cours de construction
		int currentBuildingType = -2;
		
		if (currentBuildingId.length() > 0) {
			for (int i = 0; i < Building.BUILDING_LABELS.length; i++)
				if (currentBuildingId.equals(Building.BUILDING_LABELS[i])) {
					currentBuildingType = i;
					break;
				}
		} else {
			currentBuildingType = -1;
		}
		
		if (currentBuildingType == -2)
			throw new IllegalOperationException("Construction invalide.");
		
		// Recherche l'identifiant du batiment en attente
		int nextBuildingType = -2;
		
		if (nextBuildingId.length() > 0) {
			for (int i = 0; i < Building.BUILDING_LABELS.length; i++)
				if (nextBuildingId.equals(Building.BUILDING_LABELS[i])) {
					nextBuildingType = i;
					break;
				}
		} else {
			nextBuildingType = -1;
		}
		
		if (nextBuildingType == -2)
			throw new IllegalOperationException("Construction en attente invalide.");
		
		// Recherche l'identifiant du batiment en attente supplémentaire (Premium)
		int thirdBuildingType = -2;
		
		if (thirdBuildingId.length() > 0) {
			for (int i = 0; i < Building.BUILDING_LABELS.length; i++)
				if (thirdBuildingId.equals(Building.BUILDING_LABELS[i])) {
					thirdBuildingType = i;
					break;
				}
		} else {
			thirdBuildingType = -1;
		}
		
		if (thirdBuildingType == -2)
			throw new IllegalOperationException("Construction en attente invalide.");
		
		StarSystem system = DataAccess.getSystemById(idSystem);
		
		// Vérifie que le joueur a développé les technologies nécessaires
		if (currentBuildingType != -1) {
			for (int requirement : Building.getRequiredTechnologies(
					currentBuildingType, currentBuildingLevel)) {
				if (!player.hasResearchedTechnology(requirement))
					throw new IllegalOperationException("Vous n'avez " +
						"pas recherché les technologies nécessaires.");
			}
		}
		
		if (nextBuildingType != -1) {
			for (int requirement : Building.getRequiredTechnologies(
					nextBuildingType, nextBuildingLevel)) {
				if (!player.hasResearchedTechnology(requirement))
					throw new IllegalOperationException("Vous n'avez " +
						"pas recherché les technologies nécessaires.");
			}
		}
		
		if (thirdBuildingType != -1) {
			for (int requirement : Building.getRequiredTechnologies(
					thirdBuildingType, thirdBuildingLevel)) {
				if (!player.hasResearchedTechnology(requirement))
					throw new IllegalOperationException("Vous n'avez " +
						"pas recherché les technologies nécessaires.");
			}
		}
		
		// Vérifie que le système existe
		if (system == null)
			throw new IllegalOperationException("Le système n'existe pas.");
		
		// Vérifie que le système appartient au joueur
		if (system.getIdOwner() != player.getId())
			throw new IllegalOperationException("Le système ne vous appartient pas.");
		
		// Met à jour les crédits du joueur
		player = Player.updateCredits(player);
		
		long credits = 0;
		
		system = StarSystem.updateSystem(system);
		
		synchronized (system.getLock()) {
			system = DataAccess.getEditable(system);
			
			// Batiments en cours de construction
			Building currentBuilding	= system.getCurrentBuilding();
			Building nextBuilding		= system.getNextBuilding();
			Building thirdBuilding		= system.getThirdBuilding();
			
			Building newCurrentBuilding	= currentBuildingType == -1 ?
				null : new Building(currentBuildingType, currentBuildingLevel, 0);
			Building newNextBuilding	= nextBuildingType == -1 ?
				null : new Building(nextBuildingType, nextBuildingLevel, 0);
			Building newThirdBuilding	= thirdBuildingType == -1 ?
				null : new Building(thirdBuildingType, thirdBuildingLevel, 0);
			
			// Annule la construction du batiment en cours et en attente, et
			// restitue les ressources utilisées
			if (currentBuilding != null) {
				// Restitue les ressources pour la construction en cours
				int[] cost = Building.getCost(currentBuilding);
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
					system.addResource(cost[i], i);
				
				int creditsCost = (int) Math.ceil(cost[GameConstants.CREDITS] *
					system.getProduction(Building.TRADE_PORT));
				credits += creditsCost;
				
				if (nextBuilding != null) {
					// Restitue les ressources pour la construction en attente
					cost = Building.getCost(nextBuilding);
					for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
						system.addResource(cost[i], i);
					
					creditsCost = (int) Math.ceil(cost[GameConstants.CREDITS] *
						system.getProduction(Building.TRADE_PORT));
					credits += creditsCost;
				}
				
				if (thirdBuilding != null) {
					// Restitue les ressources pour la construction en attente
					cost = Building.getCost(thirdBuilding);
					for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
						system.addResource(cost[i], i);
					
					creditsCost = (int) Math.ceil(cost[GameConstants.CREDITS] *
						system.getProduction(Building.TRADE_PORT));
					credits += creditsCost;
				}
				
				system.setCurrentBuilding(null);
				system.setNextBuilding(null);
				system.setThirdBuilding(null);
			}
			
			// Lance les nouvelles constructions
			boolean currentBuildingHasChanged;
			boolean nextBuildingHasChanged;
			boolean thirdBuildingHasChanged;
			
			if (newCurrentBuilding != null) {
				currentBuildingHasChanged =
					currentBuilding == null ||
					newCurrentBuilding.getType() != currentBuilding.getType() ||
					newCurrentBuilding.getLevel() != currentBuilding.getLevel();
				
				credits += build(player, credits, system, newCurrentBuilding,
					currentBuildingHasChanged ?
						0 : currentBuilding.getEnd(), null,null);
				
				// Construction en attente
				if (newNextBuilding != null) {
					nextBuildingHasChanged =
						currentBuildingHasChanged ||
						nextBuilding == null ||
						newNextBuilding.getType() != nextBuilding.getType() ||
						newNextBuilding.getLevel() != nextBuilding.getLevel();
					
					credits += build(player, credits, system, newNextBuilding,
						nextBuildingHasChanged ?
							0 : nextBuilding.getEnd(), newCurrentBuilding,null);
				
					// Construction en attente supplémentaire (Premium)
					if (newThirdBuilding != null && player.isPremium()) {
						thirdBuildingHasChanged =
							currentBuildingHasChanged ||
							nextBuildingHasChanged ||
							thirdBuilding == null ||
							newThirdBuilding.getType() != thirdBuilding.getType() ||
							newThirdBuilding.getLevel() != thirdBuilding.getLevel();
						
						credits += build(player, credits, system, newThirdBuilding,
							thirdBuildingHasChanged ?
								0 : thirdBuilding.getEnd(), newNextBuilding, newCurrentBuilding );
					}
					else if(newThirdBuilding != null && !player.isPremium()) {
						throw new IllegalOperationException(" Vous devez être premium pour avoir un "+
								"3ème élément dans la file d'attente!");
					}
				}
			}
			
			system.save();
		}
		
		player = DataAccess.getPlayerById(player.getId());
		
		synchronized (player) {
			player = DataAccess.getEditable(player);
			player.addCredits(credits);
			player.save();
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerSystemsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private long build(Player player, long currentCredits, StarSystem system,
			Building newBuilding, long oldBuildingEnd, Building previousBuilding, Building firstBuilding)
			throws IllegalOperationException {
		// Vérifie qu'il reste suffisament d'espace constructible sur la
		// planète
		if (newBuilding.getLevel() == 0 &&
				system.getBuildingLand() <= system.getBuiltLand() +
				(previousBuilding != null &&
						previousBuilding.getLevel() == 0 ? 1 : 0))
			throw new IllegalOperationException(
					"Il n'y a plus d'espace constructible sur le système.");
		
		// Vérifie que la batiment est de niveau 0 ou qu'il existe un batiment
		// de niveau n-1 (amélioration)
		int[] buildings = system.getBuildings(newBuilding.getType());
		boolean throwException =false;
	/*	if (newBuilding.getLevel() > 0 && ((buildings[newBuilding.getLevel() - 1] -
			 (previousBuilding != null &&
			  previousBuilding.getType() == newBuilding.getType() &&
			  previousBuilding.getLevel() == newBuilding.getLevel() ? 1 : 0) <= 0 &&
			!(previousBuilding != null &&
			  previousBuilding.getType() == newBuilding.getType() &&
			  previousBuilding.getLevel() == newBuilding.getLevel() - 1))
			  || (firstBuilding!=null &&
					  firstBuilding.getType() == newBuilding.getType() &&
					  firstBuilding.getLevel() == newBuilding.getLevel() ? 1 : 0) <=0 &&
					  !(firstBuilding != null &&
							  firstBuilding.getType() == newBuilding.getType() &&
							  firstBuilding.getLevel() == newBuilding.getLevel() - 1))
			)*/
		if (newBuilding.getLevel() > 0) { //Si le batiment à construire est d'un niveau superieur à 0 (amélioration)
			if(previousBuilding !=null) {
				/* 
				 	On teste si le batiment précédent est du même type et s'il est du même niveau.
					Etant donné que le batiment traité est d'un niveau superieur à 0, on vérifie
					qu'on peut bien  le construire, donc s'il existe ou s'il y a en construction de niveau n-1
					ou un batiment déjà construit de niveau n-1
					Dans le cas où newBuilding se réfère au 3e élément de la queue, on doit aussi traiter
					firstBuilding
				*/
				int quantity = (previousBuilding.getType() == newBuilding.getType() &&
				  previousBuilding.getLevel() == newBuilding.getLevel() ? 1 : 0);
				boolean isThirdElement = false;
				if(firstBuilding!=null){
					isThirdElement = true;
					quantity += (firstBuilding.getType() == newBuilding.getType() &&
					  firstBuilding.getLevel() == newBuilding.getLevel() ? 1 : 0);
				}
				/*
				  	Si le nombre de batiment construit ou en construction de niveau inférieur à newBuilding
					est inferieur ou égal à 0 ...
				*/
				if( (buildings[newBuilding.getLevel() - 1] - quantity) <= 0) {
					if(	!(previousBuilding != null &&
						  previousBuilding.getType() == newBuilding.getType() &&
						  previousBuilding.getLevel() == newBuilding.getLevel() - 1)) {
						if(isThirdElement==true) {
							if(!(firstBuilding != null &&
						  firstBuilding.getType() == newBuilding.getType() &&
						 firstBuilding.getLevel() == newBuilding.getLevel() - 1)) {
								throwException = true;
							}
						}
						else
						{
							throwException = true;
					
						}
					}
				}
			}
			else
			{
				if(buildings[newBuilding.getLevel() - 1] <= 0) {
					throwException = true;
				}
			}
			
			if(throwException) {
				throw new IllegalOperationException("Vous n'avez pas effectué " +
				"les constructions nécessaires au bâtiment.");
			}
		}
		
		// Vérifie qu'un seul centre de recherche / usine / raffinerie est
		// construit sur le système
		if (newBuilding.getLevel() == 0 &&
			(newBuilding.getType() == Building.RESEARCH_CENTER ||
			 newBuilding.getType() == Building.FACTORY ||
			 newBuilding.getType() == Building.REFINERY ||
			 newBuilding.getType() == Building.CORPORATIONS ||
			 newBuilding.getType() == Building.EXTRACTOR_CENTER ||
			 newBuilding.getType() == Building.TRADE_PORT) &&
			// Batiment bonus déjà construit ?
			(ArrayUtils.sum(buildings) > 0 ||
			// Batiment précédent = batiment bonus du meme type ?
			(previousBuilding != null &&
			 previousBuilding.getType() == newBuilding.getType())
			 || firstBuilding != null && 
			 	firstBuilding.getType() == newBuilding.getType()))
				throw new IllegalOperationException("Un bâtiment bonus maximum de " +
						"chaque type peut être construit.");
		
		// Vérifie qu'il reste suffisament d'espace pour les infrastructures
		// civiles
		int space = system.getAvailableSpace();
		if (newBuilding.getLevel() == 0 &&
			newBuilding.getType() == Building.CIVILIAN_INFRASTRUCTURES &&
			((previousBuilding != null &&
			  previousBuilding.getType() == newBuilding.getType() &&
			  // +1 si le batiment précédent est une infrastructure
			  previousBuilding.getLevel() == 0) ? 1 : 0) +
			  ( (firstBuilding!=null?
					  (firstBuilding.getType() == newBuilding.getType()?
					  (firstBuilding.getLevel() == 0? 1 : 0) : 0) : 0) ) +
			  // Nombre d'infrastructures >= espace disponible ?
			  ArrayUtils.sum(buildings) >= space)
			throw new IllegalOperationException("Vous ne disposez pas de " +
				"suffisament d'espace pour de nouvelles " +
				"infrastructures civiles.");
		
		// Vérifie qu'il reste suffisament de ressources disponibles pour les
		// exploitations
		int[] resources = system.getAvailableResources();
		if (newBuilding.getLevel() == 0 &&
			Building.isExploitation(newBuilding) &&
			((previousBuilding != null &&
			  previousBuilding.getType() == newBuilding.getType() &&
			  // +1 si le batiment précédent est une nouvelle exploitation du
			  // meme type que le nouveau batiment
			  previousBuilding.getLevel() == 0) ? 1 : 0) +
			  ( (firstBuilding!=null?
					  (firstBuilding.getType() == newBuilding.getType()?
					  (firstBuilding.getLevel() == 0? 1 : 0) : 0) : 0) ) +
			 // Nombre d'exploitation >= ressources disponibles ?
			 ArrayUtils.sum(buildings) >=
				 resources[Building.getFarmedResource(newBuilding)])
					throw new IllegalOperationException("Vous ne disposez pas de " +
							"suffisament de gisements pour une nouvelle " +
							"exploitation.");
		
		// Vérifie que le système contient les ressources nécessaires
		int[] cost = Building.getCost(newBuilding);
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
			if (system.getResource(i) < cost[i])
				throw new IllegalOperationException("Vous n'avez pas les " +
					"ressources nécessaires à la construction du bâtiment.");
			else
				system.addResource(-cost[i], i); // Décrémente les ressources du système
		}
		
		// Vérifie que le joueur possède les crédits nécessaires
		long credits;
		int creditsCost = (int) Math.ceil(cost[GameConstants.CREDITS] *
			system.getProduction(Building.TRADE_PORT));
		if (player.getCredits() + currentCredits < creditsCost) {
			throw new IllegalOperationException("Vous n'avez pas les " +
				"crédits nécessaires à la construction du bâtiment.");
		} else {
			credits = -creditsCost;
		}
		
		// Construit le batiment
		int[] factory = system.getBuildings(Building.FACTORY);
		if(firstBuilding == null){
			if (previousBuilding == null) {
				if (oldBuildingEnd > 0) {
					// Le bâtiment n'a pas changé ; conserve le même temps de
					// construction restant
					newBuilding.setEnd(oldBuildingEnd);
				}
				else {
					newBuilding.setEnd((long) (
							system.getLastUpdate() +
							Building.getBuildTime(newBuilding) *
							system.getProduction(Building.FACTORY)));
				}
				system.setCurrentBuilding(newBuilding);
			} else {
				if (oldBuildingEnd > 0) {
					// Le bâtiment n'a pas changé ; conserve le même temps de
					// construction restant
					newBuilding.setEnd(oldBuildingEnd);
				} else {
					if (previousBuilding.getType() == Building.FACTORY) {
						factory = new int[5];
						factory[previousBuilding.getLevel()] = 1;
					}
					newBuilding.setEnd((long) (
							system.getCurrentBuildingEnd() +
							Building.getBuildTime(newBuilding) *
							Building.getProduction(Building.FACTORY, factory)));
				}
				system.setNextBuilding(newBuilding);
			}
		}
		else {
			if (oldBuildingEnd > 0) {
				// Le bâtiment n'a pas changé ; conserve le même temps de
				// construction restant
				newBuilding.setEnd(oldBuildingEnd);
			} else {
				boolean newFact = false;
				if (previousBuilding.getType() == Building.FACTORY) {
					newFact =true;
					factory = new int[5];
					factory[previousBuilding.getLevel()] = 1;
				}
				
				if(firstBuilding.getType() == Building.FACTORY) {
					if(!newFact) {
						factory = new int[5];
					}
					factory[firstBuilding.getLevel()] = 1;
				}
				
				newBuilding.setEnd((long) (
						system.getNextBuildingEnd() +
						Building.getBuildTime(newBuilding) *
						Building.getProduction(Building.FACTORY, factory)));
			}
			system.setThirdBuilding(newBuilding);
		}
		
		return credits;
	}
}
