/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc, Thierry Chevalier

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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.fg.server.core.AiTools;
import fr.fg.server.core.BattleTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterBattleEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;


public class UpdatePirateAiFleets extends Thread implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Une flotte pirate PNJ détruite met en moyenne 6h pour être regénérée
	public final static double RESPAWN_PROBABILITY = .1667;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public UpdatePirateAiFleets() {
		GameEventsDispatcher.addGameEventListener(this, AfterBattleEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof AfterBattleEvent) {
			AfterBattleEvent gameEvent = (AfterBattleEvent) event;
			
			// Marque les flottes pirates PNJ endommagées pour suppression
			String attacker = gameEvent.getAttackingFleetBefore().getOwner().getLogin();
			
			if ((AiTools.isPiratePlayer(attacker)||
					(gameEvent.getAttackingFleetBefore().getOwner().isAi() &&
					gameEvent.getAttackingFleetBefore().getName().contains(AiTools.PNJ_DEFENDER_AI_LOGIN))) &&
					!gameEvent.isAttackingFleetDestroyed() &&
					gameEvent.getAttackingFleetBefore().getPower() !=
						gameEvent.getAttackingFleetAfter().getPower()) {
				Fleet attackingFleet = gameEvent.getAttackingFleetAfter();
				
				synchronized (attackingFleet.getLock()) {
					attackingFleet = DataAccess.getEditable(attackingFleet);
					attackingFleet.setTacticsDefined(false);
					attackingFleet.setLastMove(Utilities.now());
					attackingFleet.save();
				}
			}
			
			// Le joueur peut avoir été supprimé dans le cadre d'une mission
			String defender = "";
			if (gameEvent.getDefendingFleetBefore().getOwner() != null)
				defender = gameEvent.getDefendingFleetBefore().getOwner().getLogin();
			
			if (AiTools.isPiratePlayer(defender) &&
					!gameEvent.isDefendingFleetDestroyed() &&
					gameEvent.getDefendingFleetBefore().getPower() !=
						gameEvent.getDefendingFleetAfter().getPower()) {
				Fleet defendingFleet = gameEvent.getDefendingFleetAfter();
				
				synchronized (defendingFleet.getLock()) {
					defendingFleet = DataAccess.getEditable(defendingFleet);
					defendingFleet.setTacticsDefined(false);
					defendingFleet.setLastMove(Utilities.now());
					defendingFleet.save();
				}
			}
		}
	}
	
	public void run() {
		this.setName("UpdatePirateAiFleets (hourly)");
		List<Area> areas = new ArrayList<Area>(DataAccess.getAllAreas());
		int[] piratesCount = new int[100];
		List<Integer> areasToUpdate = new ArrayList<Integer>();
		
		
		// Supprime les flottes pirates PNJ qui ont été endommagées
		long now = Utilities.now();
		
		for (Area area : areas) {
			List<Fleet> fleets = new ArrayList<Fleet>(area.getFleets());
			
			for (Fleet fleet : fleets) {
				if (fleet.getOwner() != null &&
						AiTools.isPiratePlayer(fleet.getOwner().getLogin()) ||
						(fleet.getOwner().isAi() && fleet.getName().contains(AiTools.PNJ_DEFENDER_AI_LOGIN))) {
					if (!fleet.isTacticsDefined() &&
							fleet.getLastMove() + 3600 < now) {
						fleet.delete();
						
						if (!areasToUpdate.contains(area.getId()))
							areasToUpdate.add(area.getId());
					}
				}

			}
		}
		
		
		
		for (Area area : areas) {
			
			// Compte le nombre de flottes pirates PNJ dans le secteur
			Arrays.fill(piratesCount, 0);
			
			List<Fleet> fleets = new ArrayList<Fleet>(area.getFleets());
			List<SpaceStation> spaceStations = new ArrayList<SpaceStation>(area.getSpaceStations());
			List<StarSystem> systems = new ArrayList<StarSystem>(area.getColonizedSystems());
			List<Structure> structures = new ArrayList<Structure>(area.getStructures());
			List<Fleet> fleetBombarding = new ArrayList<Fleet>();
			
			int[][] playersBonus = new int[100][2];
			int j =0;
			for(j=0;j<100;j++)
			{
				playersBonus[j][0]=Integer.MAX_VALUE;
			}
			
			int nbFleetsNotAi=0;
			
			for (Fleet fleet : fleets) {
				if(!fleet.getOwner().isAi()){
					
					nbFleetsNotAi++;
					
					//Gestion du nombre de flottes du joueur présent dans les secteurs
					boolean found = false;

					j=0;
					while(j<playersBonus.length && playersBonus[j][0]!= Integer.MAX_VALUE){
						
						if(playersBonus[j][0]==fleet.getIdOwner()){
							found = true;
							break;
						}
						j++;
					}

					if(found)
						playersBonus[j][1]++;
					else{
						playersBonus[j][0]=fleet.getIdOwner();
						playersBonus[j][1]=1;
					}
					
					//Gestion du nombre de flottes qui minent.
					if(fleet.getCurrentAction()==Fleet.CURRENT_ACTION_MINE)
						playersBonus[j][1]++;
					
					//Gestion du nombre de flotte qui attaquent
					//Joueur cherchant à xp => PNJ se rebellent
					if(fleet.getCurrentAction()==Fleet.CURRENT_ACTION_BATTLE)
						playersBonus[j][1]++;
				}
				
				if(fleet.getCurrentAction()==Fleet.CURRENT_ACTION_ATTACK_STRUCTURE)
				{
					fleetBombarding.add(fleet);
				}
				
			}

			
			fleets:for (Fleet fleet : fleets) {
					if(fleet.getName().contains(AiTools.PNJ_DEFENDER_AI_LOGIN) && fleet.getOwner() != null && fleet.getOwner().isAi())
					{
						//Si le joueur saborde son compte, les PNJ sont link par une autre table
						//Pas possible de delete en cascade dans ce cas
						//Gestion une seule fois pour supprimer le PNJ defender et ses flottes
						if(DataAccess.getPnjDefenderByPnj(fleet.getIdOwner())==null)
						{
							fleet.getOwner().delete();
							continue fleets;
						}
						
						int playerId = 
							DataAccess.getPnjDefenderByPnj(fleet.getIdOwner()).getIdPlayer();
						
						if(DataAccess.getPlayerById(playerId)==null)
						{
							LoggingSystem.getServerLogger().warn("UpdatePirateAiFleet : Player inconnu");
							continue fleets;
						}
						
						int idAllyToHelp=DataAccess.getPlayerById(playerId).getIdAlly();
						
						Player player = DataAccess.getPlayerById(playerId);

						//Attaque des lvl elevé aussi? % ? TODO
						Fleet realTarget=null;
						for(Fleet target : fleetBombarding){
							if(target.getStructureUnderFleet()!=null && 
								((target.getStructureUnderFleet().getOwner().getIdAlly()==idAllyToHelp && idAllyToHelp !=0) ||
								(idAllyToHelp==0 && target.getStructureUnderFleet().getOwner().getId() == playerId)))
							{
								
								if(!target.isStealth() || (target.isStealth() &&
										 player.isLocationRevealed(target.getCurrentX(),
												 		target.getCurrentY(),
												 		area)))
									{
										int powerFleet = fleet.getPowerLevel();
									
										//Initialisation et attaque s'il n'y a qu'une flotte
										if(realTarget == null)
											realTarget = target;
										//Le PNJ attaque des flottes entre getPowerLevel()
										//et getPowerLevel()-2
										else if (powerFleet-target.getPowerLevel() >= 0 &&
												powerFleet-target.getPowerLevel() <= 2)
											realTarget = target;
										//Si que des flottes > au lvl de la flotte, on attaque la plus faible
										else if(realTarget.getPowerLevel()>powerFleet && 
												target.getPowerLevel()<realTarget.getPowerLevel())
											realTarget = target;
										//Flotte low power? Par élimination lors de l'init? et du 3ème
										//Suppression des flottes amochées
										
									}
								}
							}
						
						if (realTarget != null && Math.random()<0.35) {
							if(fleet.getMovementReload()<Utilities.now())
							{
								int fleetX = realTarget.getCurrentX();
								int fleetY = realTarget.getCurrentY();

								
								coords:for (int x = -1; x <= 1; x++)
									for (int y = -1; y <= 1; y++)
										if (area.isFreeTile(fleetX + x, fleetY + y,
												Area.CHECK_FLEET_MOVEMENT, fleet.getOwner())) {
											// FIXME bug dans la JVM 1.6 qui plante pour une raison inconnue sur le synchronized
//											synchronized (fleet.getLock()) {
												fleet = DataAccess.getEditable(fleet);
												fleet.setLocation(fleetX + x, fleetY + y);
												fleet.save();
//											}
											
											FleetTools.checkTriggeredCharges(fleet, null);
											fleet = DataAccess.getFleetById(fleet.getId());
											realTarget = DataAccess.getFleetById(realTarget.getId());
											
											if (fleet != null && fleet.getMovementReloadRemainingTime()<11 &&
													realTarget != null) {
												try {
													BattleTools.battle(BattleTools.MODE_BATTLE,
														fleet, realTarget, false);
												} catch (Exception e) {
													LoggingSystem.getServerLogger().warn(
														"NPC pirate attack failed.", e);
												}
											}
											
											if (!areasToUpdate.contains(area.getId()))
												areasToUpdate.add(area.getId());
											
											break coords;
										}
							}
							
						}
					}
					else if (fleet.getOwner() != null && AiTools.isPiratePlayer(fleet.getOwner().getLogin()))
					{
						
						piratesCount[fleet.getPowerLevel()]++;
						
						int powerLevel = fleet.getPowerLevel();
						
						// Cherche s'il y a une flotte a attaquer à proximité...
						Fleet nearestOpenToAttackFleet = null;
						
						// Cherche s'il y a une structure  attaquer à proximité... (algo naif)
						Structure nearestOpenToAttackStructure = null;
						
						int minDistance = 35 * 35;
						
						attack:for (Fleet fleet2 : fleets) {
							
							//On n'attaque pas si :
							//La flotte est IA
							//La flotte est en HE
							//La flotte n'a pas exactement un lvl de moins et elle a une puissance > LvlMin
							//La flotte a plus de 2 lvl de plus et celle ci a une puissace < Lvlmin
							boolean sector_x5 = (fleet.getArea().getGeneralType() == Area.AREA_GENERAL_MINING_X5);
							
							if (fleet2.getOwner() == null ||
									fleet2.getOwner().isAi() ||
									fleet2.isInHyperspace() ||
									(fleet2.getPowerLevel() + 1 != powerLevel &&
									 fleet2.getPowerLevel() >= fleet2.getArea().getSector().getLvlMin()) ||
									 (fleet2.getPowerLevel() < fleet2.getArea().getSector().getLvlMin() &&
										fleet.getPowerLevel() > fleet2.getArea().getSector().getLvlMin() +2 &&
										!sector_x5) ||
										(fleet2.getPowerLevel() < fleet2.getArea().getSector().getLvlMin()+5 &&
												fleet.getPowerLevel() > fleet2.getArea().getSector().getLvlMin() +7 &&
												sector_x5))
								continue;
							
							
							for (SpaceStation spaceStation : spaceStations)
								if (spaceStation.contains(fleet2.getCurrentX(), fleet2.getCurrentY()))
									continue attack;
							
							for (StarSystem system : systems)
								if (system.contains(fleet2.getCurrentX(), fleet2.getCurrentY()))
									continue attack;
	
							
							int dx = fleet2.getCurrentX() - fleet.getCurrentX();
							int dy = fleet2.getCurrentY() - fleet.getCurrentY();
							int distance = dx * dx + dy * dy;
							
							double ratio = 1;
							
							j=0;
							while(j< playersBonus.length && playersBonus[j][0]!= Integer.MAX_VALUE){
								if(playersBonus[j][0]==fleet2.getIdOwner() && playersBonus[j][1]>=5){
									//Le nombre de case max est atteignable uniquement si le joueur
									//A toutes les flottes non PNJ du secteur, et qu'elles sont toutes immobilisées
									//Par combat ou par minage
									
									//Exemple pour le premier
									//Si un joueur à 4 flottes dans un secteur désert et les flottes viennent de combattrent
									//playersBonus[j][1] = 4 flottes + 4 combats = 8
									//ratio = 1 + 8 / 4 ( 4 = nombre de flottes non Ai du secteur ) * 0.5 = 1.5
									//35*1.5 = 35+17.5 = 52.5 de distance en x et y maxi
									
									//Exemple numéro 2
									//Si un joueur à 5/10 flottes combattants et 
									//qu'il y a 50 flottes non pnj dans le secteur
									//playersBonus[j][1] = 10 flottes + 5 combats = 15
									//Ratio = 1 + 15 / 50 = 1.3
									//La présence est importante, ici 1.3 avec 15 flottes, 1.5 avec 4 flottes
									if(playersBonus[j][1]<10)
										ratio += ((double)playersBonus[j][1]/(double)nbFleetsNotAi*0.5); // 17.5+17.5 Cases en plus maxi
									else if(playersBonus[j][1]<=25)
										ratio += ((double)playersBonus[j][1]/(double)nbFleetsNotAi); // 35 + 35 maxi
									else if (playersBonus[j][1]<=50)
										ratio += ((double)playersBonus[j][1]/(double)nbFleetsNotAi*1.5); // 52.5+52.5 maxi
									else
										ratio += ((double)playersBonus[j][1]/(double)nbFleetsNotAi*2); // 70 + 70 maxi
								}
								j++;
							}
							
							
							if (distance < (int)((double)minDistance*ratio*ratio)) {
								{
									nearestOpenToAttackFleet = fleet2;
									minDistance = distance;
								}
							}
							
	
						}
						
						minDistance = 40 * 40;
						
						if(nearestOpenToAttackFleet == null)
						{
							attackStructure:for (Structure structure : structures) {		
								if (structure.getOwner().isAi() ||
										!structure.isActivated())
									continue;
								
								
								int dx = structure.getX() - fleet.getCurrentX();
								int dy = structure.getY() - fleet.getCurrentY();
								int distance = dx * dx + dy * dy;
								
								if (distance < minDistance) {
									nearestOpenToAttackStructure = structure;
									minDistance = distance;
								}
								
	
							}
						}
						
	
						
						// Détermine si le PNJ attaque
						boolean attack = false;
						boolean attackStructure = Math.random() < .04;
						
						double coef = 1.00;
						
						if (nearestOpenToAttackFleet != null) {
							
							if(nearestOpenToAttackFleet.getPowerLevel()<
									nearestOpenToAttackFleet.getArea().getSector().getLvlMin() &&
									fleet.getPowerLevel() <= area.getSector().getLvlMin()+1)
								coef = 1.3;
							
							switch (area.getType()) {
							case Area.AREA_START:
							if (nearestOpenToAttackFleet.getPowerLevel() + 1 == powerLevel)
								attack = nearestOpenToAttackFleet.isStealth() ? false : Math.random() < .05;
								break;
							case Area.AREA_MINING_0_5:
								if (nearestOpenToAttackFleet.getPowerLevel() + 1 == powerLevel)
									attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15 : Math.random() < .20;
								attackStructure = Math.random() < (6-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_5_10:
								if (nearestOpenToAttackFleet.getPowerLevel() + 1 == powerLevel)
									attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15 : Math.random() < .20;
								attackStructure = Math.random() < (11-fleet.getPowerLevel())*0.006;
								break;
								
							case Area.AREA_MINING_10_15:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (16-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_15_20:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (21-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_20_25:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (26-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_25_30:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (31-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_30_35:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (36-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_35_40:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (41-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_40_45:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (46-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_45_50:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (51-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_50_55:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (56-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_MINING_55_60:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .15*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (61-fleet.getPowerLevel())*0.006;
								break;
							case Area.AREA_COLONY_10_20:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .10*coef : Math.random() < .20*coef;
								break;
							case Area.AREA_COLONY_20_30:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .10*coef : Math.random() < .20*coef;
								break;
							case Area.AREA_COLONY_30_40:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .10*coef : Math.random() < .20*coef;
								break;
							case Area.AREA_COLONY_40_50:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .10*coef : Math.random() < .20*coef;
								break;
							case Area.AREA_COLONY_50_60:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .10*coef : Math.random() < .20*coef;
								break;
							case Area.AREA_BATTLE_0_10:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .05 : Math.random() < .10;
								break;
							case Area.AREA_BATTLE_10_20:
							case Area.AREA_BATTLE_20_30:
							case Area.AREA_BATTLE_30_40:
							case Area.AREA_BATTLE_40_50:
							case Area.AREA_BATTLE_50_60:
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .1*coef : Math.random() < .20*coef;
								attackStructure = Math.random() < (10-(fleet.getPowerLevel()-area.getSector().getLvlMin()))*0.007;
								break;
							default :
								attack = nearestOpenToAttackFleet.isStealth() ? Math.random() < .1 : Math.random() < .15;
								break;
								
							}
						}
	
						if (attack) {
							if(fleet.getMovementReload()<Utilities.now())
							{
								int fleetX = nearestOpenToAttackFleet.getCurrentX();
								int fleetY = nearestOpenToAttackFleet.getCurrentY();
								
								// TODO jgottero a améliorer pour prendre le chemin le plus court > utiliser NpcHelper.getNearestFreeTile
								coords:for (int x = -1; x <= 1; x++)
									for (int y = -1; y <= 1; y++)
										if (area.isFreeTile(fleetX + x, fleetY + y,
												Area.CHECK_FLEET_MOVEMENT, fleet.getOwner())) {
//											synchronized (fleet.getLock()) {
												fleet = DataAccess.getEditable(fleet);
												fleet.setLocation(fleetX + x, fleetY + y);
												fleet.save();
//											}
											
											FleetTools.checkTriggeredCharges(fleet, null);
											fleet = DataAccess.getFleetById(fleet.getId());
											nearestOpenToAttackFleet = DataAccess.getFleetById(nearestOpenToAttackFleet.getId());
											
											if (fleet != null && fleet.getMovementReloadRemainingTime()<11 &&
													nearestOpenToAttackFleet != null) {
												try {
													BattleTools.battle(BattleTools.MODE_BATTLE,
														fleet, nearestOpenToAttackFleet, false);
												} catch (Exception e) {
													LoggingSystem.getServerLogger().warn(
														"NPC pirate attack failed.", e);
												}
											}
											
											if (!areasToUpdate.contains(area.getId()))
												areasToUpdate.add(area.getId());
											
											break coords;
										}
							}
						}
						
		
						
						else if (nearestOpenToAttackStructure != null 
								&& attackStructure
								&& fleet.getMovementReload()<Utilities.now()
								)
							{
							
							int structureX = nearestOpenToAttackStructure.getX();
							int structureY = nearestOpenToAttackStructure.getY();
							
							// TODO jgottero a améliorer pour prendre le chemin le plus court > utiliser NpcHelper.getNearestFreeTile
							// TODO nbosc à vérifier les coordonées de placement de la structure si c'est le coin haut gauche = ok
							int width = (int)nearestOpenToAttackStructure.getSize().getWidth();
							int height = (int)nearestOpenToAttackStructure.getSize().getHeight();
							coords:for (int x = (int) -Math.ceil(width/2); x <= Math.ceil(width/2); x++)
								for (int y = (int) -Math.ceil(height/2); y <= Math.ceil(height/2); y++)
									if (area.isFreeTile(structureX + x, structureY + y,
											Area.CHECK_FLEET_MOVEMENT, fleet.getOwner()) &&
											//fleet.getStructureUnderFleet()!=null &&
											nearestOpenToAttackStructure.contains(structureX + x, structureY + y)) {
										
										
//										synchronized (fleet.getLock()) {
											fleet = DataAccess.getEditable(fleet);
											fleet.setLocation(structureX + x, structureY + y);
											fleet.save();
//										}
										
										
										FleetTools.checkTriggeredCharges(fleet, null);
										fleet = DataAccess.getFleetById(fleet.getId());
										
										
										if (fleet != null && fleet.getStructureUnderFleet()!=null &&
												 fleet.getMovementReloadRemainingTime()<11) {
											try {
//												synchronized (fleet.getLock()) {
													fleet = DataAccess.getEditable(fleet);
													fleet.doAction(Fleet.CURRENT_ACTION_ATTACK_STRUCTURE, Utilities.now() +
															GameConstants.ATTACK_STRUCTURE_MOVEMENT_RELOAD);
													fleet.save();
//												}
												
												Event event = new Event(
														Event.EVENT_STRUCTURE_ATTACKED,
														Event.TARGET_PLAYER,
														fleet.getStructureUnderFleet().getIdOwner(),
														fleet.getStructureUnderFleet().getIdArea(),
														fleet.getStructureUnderFleet().getX(),
														fleet.getStructureUnderFleet().getY(),
														fleet.getStructureUnderFleet().getName(),
														fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "???" : fleet.getName(),
														fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ? "??? (flotte pirate)" : fleet.getOwner().getLogin());
													event.save();
													
													UpdateTools.queueNewEventUpdate(fleet.getStructureUnderFleet().getIdOwner());
													
											} catch (Exception e) {
												LoggingSystem.getServerLogger().warn(
													"NPC pirate attack structure failed.", e);
											}
										}
										
										if (!areasToUpdate.contains(area.getId()))
											areasToUpdate.add(area.getId());
										
										break coords;
									}
							
							}
						else{
							if(fleet.getMovementReload()<Utilities.now())
							{
								// Déplace aléatoirement la flotte de +/- 30 cases en
								// absisses et en ordonnées
								if (Math.random() < .6) {
									int x, y, count = 0;
									do {
										x = fleet.getCurrentX() + (int) (Math.random() * 30 - 15);
										y = fleet.getCurrentY() + (int) (Math.random() * 30 - 15);
										
										if (++count == 100)
											continue fleets;
									} while (!area.isFreeTile(x, y,
										Area.CHECK_FLEET_MOVEMENT, fleet.getOwner()));
									
//									synchronized (fleet.getLock()) {
										fleet = DataAccess.getEditable(fleet);
										fleet.setLocation(x, y);
										fleet.save();
//									}
									
									FleetTools.checkTriggeredCharges(fleet, null);
									
									if (!areasToUpdate.contains(area.getId()))
										areasToUpdate.add(area.getId());
								}
							}
						}
					}
				}

			
			// Génère les flottes pirates manquantes
			switch (area.getType()) {
			case Area.AREA_START:
				boolean spawn = true;
				//List<StarSystem> systems = area.getColonizedSystems();
				
				if (systems.size() > area.getSystems().size() / 2) {
					// Trie les systèmes colonisés par XP des joueurs
					Collections.sort(systems, new Comparator<StarSystem>() {
						public int compare(StarSystem s1, StarSystem s2) {
							Player p1 = s1.getOwner();
							Player p2 = s2.getOwner();
							
							if (p1.isAi() && !p2.isAi())
								return 1;
							if (p2.isAi() && !p1.isAi())
								return -1;
							
							if (p1.getXp() > p2.getXp())
								return -1;
							else
								return 1;
						}
					});
					
					// Bloque le spawn des FP PNJ si 12 joueurs ont dépassé le
					// niveau 6
					int count = Math.min(12, systems.size());
					if (systems.get(count - 1).getOwner().getLevel() >= 6)
						spawn = false;
				}
				
				if (spawn) {
					for (int i = piratesCount[1]; i < 15; i++)
						mayCreatePirateFleet(area, 1, areasToUpdate);
					for (int i = piratesCount[2]; i < 6; i++)
						mayCreatePirateFleet(area, 2, areasToUpdate);

				}
				break;
			case Area.AREA_MINING_0_5:
				for (int power = 2; power <= 5; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_MINING_5_10:
				for (int power = 5; power <= 10; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_COLONY_10_20:
				for (int i = piratesCount[10]; i < 6; i++)
					mayCreatePirateFleet(area, 10, areasToUpdate);
				for (int i = piratesCount[11]; i < 3; i++)
					mayCreatePirateFleet(area, 11, areasToUpdate);
				break;
			case Area.AREA_MINING_10_15:
				for (int power = 11; power <= 15; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_MINING_15_20:
				for (int power = 16; power <= 20; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_COLONY_20_30:
				for (int i = piratesCount[20]; i < 6; i++)
					mayCreatePirateFleet(area, 20, areasToUpdate);
				for (int i = piratesCount[21]; i < 3; i++)
					mayCreatePirateFleet(area, 21, areasToUpdate);
				break;
			case Area.AREA_MINING_20_25:
				for (int power = 21; power <= 25; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_MINING_25_30:
				for (int power = 26; power <= 30; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_COLONY_30_40:
				for (int i = piratesCount[30]; i < 6; i++)
					mayCreatePirateFleet(area, 30, areasToUpdate);
				for (int i = piratesCount[31]; i < 3; i++)
					mayCreatePirateFleet(area, 31, areasToUpdate);
				break;
			case Area.AREA_MINING_30_35:
				for (int power = 31; power <= 35; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_MINING_35_40:
				for (int power = 36; power <= 40; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_COLONY_40_50:
				for (int i = piratesCount[40]; i < 6; i++)
					mayCreatePirateFleet(area, 40, areasToUpdate);
				for (int i = piratesCount[41]; i < 3; i++)
					mayCreatePirateFleet(area, 41, areasToUpdate);
				break;
			case Area.AREA_MINING_40_45:
				for (int power = 41; power <= 45; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_MINING_45_50:
				for (int power = 46; power <= 50; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_COLONY_50_60:
				for (int i = piratesCount[50]; i < 6; i++)
					mayCreatePirateFleet(area, 50, areasToUpdate);
				for (int i = piratesCount[51]; i < 3; i++)
					mayCreatePirateFleet(area, 51, areasToUpdate);
				break;
			case Area.AREA_MINING_50_55:
				for (int power = 51; power <= 55; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_MINING_55_60:
				for (int power = 56; power <= 60; power++)
					for (int i = piratesCount[power]; i < 25; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_BATTLE_0_10:
				for (int power = 2; power <= 10; power++)
					for (int i = piratesCount[power]; i < 5; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_BATTLE_10_20:
				for (int power = 11; power <= 20; power++)
					for (int i = piratesCount[power]; i < 5; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_BATTLE_20_30:
				for (int power = 21; power <= 30; power++)
					for (int i = piratesCount[power]; i < 5; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_BATTLE_30_40:
				for (int power = 31; power <= 40; power++)
					for (int i = piratesCount[power]; i < 5; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_BATTLE_40_50:
				for (int power = 41; power <= 50; power++)
					for (int i = piratesCount[power]; i < 5; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			case Area.AREA_BATTLE_50_60:
				for (int power = 51; power <= 60; power++)
					for (int i = piratesCount[power]; i < 5; i++)
						mayCreatePirateFleet(area, power, areasToUpdate);
				break;
			}
			
		}
		
		for (Integer idArea : areasToUpdate)
			UpdateTools.queueAreaUpdate(idArea);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void mayCreatePirateFleet(Area area, int power,
			List<Integer> areasToUpdate) {
		if (Math.random() < RESPAWN_PROBABILITY) {
			Point location = area.getRandomFreeTile(Area.CHECK_HYPERJUMP_OUTPUT, null);
			try {
				AiTools.createPirateFleet(power, location.x, location.y, area.getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!areasToUpdate.contains(area.getId()))
				areasToUpdate.add(area.getId());
		}
	}
}

