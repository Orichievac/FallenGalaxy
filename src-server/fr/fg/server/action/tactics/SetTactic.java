	
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
	
	package fr.fg.server.action.tactics;
	
	import java.awt.Point;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Map;
	
	
	
	import fr.fg.server.core.FleetTools;
	import fr.fg.server.core.SystemTools;
	import fr.fg.server.core.Update;
	import fr.fg.server.core.UpdateTools;
	import fr.fg.server.data.DataAccess;
	import fr.fg.server.data.Fleet;
	import fr.fg.server.data.GameConstants;
	import fr.fg.server.data.IllegalOperationException;
	import fr.fg.server.data.Player;
	import fr.fg.server.data.Ship;
	import fr.fg.server.data.Slot;
	import fr.fg.server.data.StarSystem;
	import fr.fg.server.servlet.Action;
	import fr.fg.server.servlet.Session;
	import fr.fg.server.util.JSONStringer;
	
	
	
	public class SetTactic extends Action {
		// ------------------------------------------------------- CONSTANTES -- //
		// -------------------------------------------------------- ATTRIBUTS -- //
		// ---------------------------------------------------- CONSTRUCTEURS -- //
		// --------------------------------------------------------- METHODES -- //
		
		@Override
		protected String execute(Player player, Map<String, Object> params,
		Session session) throws Exception {
			
			int idSystem = (Integer) params.get("system");
			
			
			
			// Compte le nombre de flottes à réapprovisionner
			int fleetsCount = 0;
			for (int i = 0; i < 9; i++) {
				if (params.get("fleet" + i) == null)
				break;
				fleetsCount++;
			}
			
			// Charge les flottes
			Fleet[] fleets = new Fleet[fleetsCount];
			
			for (int i = 0; i < fleetsCount; i++) {
				
				int idFleet = (Integer) params.get("fleet" + i);
				
				Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId());
				StarSystem system = SystemTools.getSystemByIdWithChecks(
				idSystem, player.getId());
				
				
				fleets[i] = fleet;
				
				
				// Vérifie que la/les flotte est/sont dans les même secteur que le système
				if (fleet.getIdCurrentArea() != system.getIdArea())
				if (fleets.length == 1){throw new IllegalOperationException("La flotte n'est pas dans le même " +
					"secteur que le système.");}
				else
				throw new IllegalOperationException("Les flottes ne sont pas dans le même " +
				"secteur que le système.");
				
				// Vérifie que la/les flotte(s) n'est/ne sont pas immobilisée(s)     
				if(fleet.getCurrentAction()!=Fleet.CURRENT_ACTION_NONE)
				if (fleets.length == 1 ){ throw new IllegalOperationException("La flotte est immobilisée."); }
				else
				throw new IllegalOperationException("Les flottes sont immobilisées.");
				
				// Vérifie que la/les flotte(s) n'est/ne sont pas loin du système
				if (!system.contains(fleets[i].getCurrentX(), fleets[i].getCurrentY()))
				if (fleets.length == 1 ){throw new IllegalOperationException("La flotte est trop éloignée du " +
					"système pour pouvoir échanger des vaisseaux.");}
				else
				throw new IllegalOperationException("Les flottes sont trop éloignées du " +
				"système pour pouvoir échanger des vaisseaux.");
				
				Fleet newFleet;
				StarSystem newSystem;
				
				synchronized (fleet.getLock()) {
					synchronized (system.getLock()) {
						newFleet = DataAccess.getEditable(fleets[i]);
						newSystem = DataAccess.getEditable(system);
						
						// Indique s'il faut maj la tactique de la flotte
						boolean updateTactics = false;
						
						//// Remplacement des slots de la flotte
						
						// Anciens nombres
						double[] oldTacticCount = new double[GameConstants.FLEET_SLOT_COUNT];
						
						// Parcourt des slots de la tactique
						for (int slotIndex = 0; slotIndex < GameConstants.FLEET_SLOT_COUNT; slotIndex++) {
						
							// ID du vaisseau à mettre dans la flotte
							int tacticShipId = (Integer) params.get("slot_id_" + slotIndex);
							
							// Nombre de vaisseaux
							long tacticShipCount = (Long) params.get("tactic_slot" + slotIndex + "_count");
							
							// Ancien nombre de vaisseaux
							int fleetSlotIndex = getFleetSlotById(fleet, tacticShipId);
							if(fleetSlotIndex != -1){
								oldTacticCount[slotIndex] = fleet.getSlot(fleetSlotIndex).getCount();
							}else{
								oldTacticCount[slotIndex] = 0;
							}
							
							// On doit maj la tactique de la flotte s'il y a une différence de compo
							if (newFleet.getSlot(slotIndex).getId() != tacticShipId)
								updateTactics = true;
							
							// Nouveau slot
							Slot fleetSlot = new Slot(
								tacticShipId,
								tacticShipCount,
								newFleet.getSlot(slotIndex).isFront()
							);
							
							// On écrase l'ancien slot
							newFleet.setSlot(fleetSlot, slotIndex);
						}
						
						////////
						
						// Vérifie qu'il n'y a pas 2x le même type de vaisseau dans 2 slots différents
						for (int k = 0; k < GameConstants.FLEET_SLOT_COUNT; k++) {
							if (newFleet.getSlot(k).getId() != 0) {
								for (int j = k + 1; j < GameConstants.FLEET_SLOT_COUNT; j++) {
									if (newFleet.getSlot(j).getId() == newFleet.getSlot(k).getId())
									throw new IllegalOperationException("Le même vaisseau est présent sur deux slots différents.");
								}
							}
						}
						
						// Vérifie que le niveau de la flotte ne dépasse pas le niveau du joueur
						if (newFleet.getPowerLevel() > player.getLevel())
							throw new IllegalOperationException("La puissance d'une flotte ne peut dépasser le niveau d'XP.");
						
						
						//// Calcule le nombre de vaisseaux restant sur le système
						
						for(int tacticIndex = 0; tacticIndex < GameConstants.FLEET_SLOT_COUNT; tacticIndex++) {
								
							// Côté flotte
							int tacticShipId = (Integer) params.get("slot_id_" + tacticIndex);
							long tacticShipCount = (Long) params.get("tactic_slot" + tacticIndex + "_count");
							double oldFleetShipCount = oldTacticCount[tacticIndex];
							
							if(oldFleetShipCount < tacticShipCount){ // Il n'y a pas assez de vaisseaux
								
								int systemSlotIndex = getSystemSlotById(newSystem, tacticShipId);
								
								if(systemSlotIndex == -1){
									throw new IllegalOperationException("Vous n'avez pas certains vaisseaux pour réapporivisionner les flottes sélectionnées.");
								}else{
								
									// Côté système
									Slot systemSlot = newSystem.getSlot(systemSlotIndex);
									double newSystemShipCount = systemSlot.getCount() - (tacticShipCount - oldFleetShipCount);
									
									if(systemSlot.getId() == tacticShipId){
										if(newSystemShipCount < 0){
											throw new IllegalOperationException("Vous n'avez pas assez de vaisseaux pour réapporivisionner les flottes sélectionnées.");
											
										}else if(newSystemShipCount == 0){
											// Nouveau slot vide
											newSystem.setSlot(
												new Slot(
													0,
													0,
													true
												),
												systemSlotIndex
											);
											
										}else{
											// Nouveau slot avec les vaisseaux restant
											newSystem.setSlot(
												new Slot(
													tacticShipId,
													newSystemShipCount,
													true
												),
												systemSlotIndex
											);
										}
									}
								}
								
							}else if(oldFleetShipCount > tacticShipCount){ // Il y a trop de vaisseaux dans le slot
								
								long toomuchCount = (long) oldFleetShipCount - tacticShipCount;
								
								int searchSystemSlot = getSystemSlotById(newSystem, tacticShipId);
								
								if(searchSystemSlot == -1){ // Des vaisseaux du mêmes types ne sont pas sur le système
									
									int nextFreeSystemSlot = getFreeSystemSlotIndex(newSystem);
									
									if(nextFreeSystemSlot == -1){
										throw new IllegalOperationException("Vous n'avez pas assez de place dans votre système pour déposer les vaisseaux en trop.");
									}else{
										// On ajoute un slot et on dépose les vaisseaux en trop
										newSystem.setSlot(
											new Slot(
												tacticShipId,
												toomuchCount,
												true
											),
											nextFreeSystemSlot
										);
									}
								
								}else{ // On ajoute les nouveaux vaisseaux au vaisseaux déjà présents sur le système
									newSystem.setSlot(
										new Slot(
											newSystem.getSlot(searchSystemSlot).getId(),
											newSystem.getSlot(searchSystemSlot).getCount() + toomuchCount,
											true
										),
										searchSystemSlot
									);
								}
							}
						}
						
						////////
						
						
						//// On dépose les slots de vaisseaux en trop dans le système
						
						for(int tacticIndex = 0; tacticIndex < GameConstants.FLEET_SLOT_COUNT; tacticIndex++) {
								
							// Côté flotte
							int oldFleetShipId = fleet.getSlot(tacticIndex).getId();
							double oldFleetShipCount = fleet.getSlot(tacticIndex).getCount();
							
							if(oldFleetShipId != 0 && getFleetSlotById(newFleet, oldFleetShipId) == -1){ // Vaisseaux en trop dans la flotte, il faut les mettre dans le système
								int searchSystemSlot = getSystemSlotById(newSystem, oldFleetShipId);
								
								if(searchSystemSlot == -1){ // Des vaisseaux du mêmes types ne sont pas sur le système
									
									int nextFreeSystemSlot = getFreeSystemSlotIndex(newSystem);
									
									if(nextFreeSystemSlot == -1){
										throw new IllegalOperationException("Vous n'avez pas assez de place dans votre système pour déposer les vaisseaux en trop.");
									}else{
										// On ajoute un slot et on dépose les vaisseaux en trop
										newSystem.setSlot(
											new Slot(
												oldFleetShipId,
												oldFleetShipCount,
												true
											),
											nextFreeSystemSlot
										);
									}
								
								}else{ // On ajoute les nouveaux vaisseaux au vaisseaux déjà présents sur le système
									newSystem.setSlot(
										new Slot(
											newSystem.getSlot(searchSystemSlot).getId(),
											newSystem.getSlot(searchSystemSlot).getCount() + oldFleetShipCount,
											true
										),
										searchSystemSlot
									);
								}
							}
						}
						
						////////
						
						// Met à jour la tactique au besoin
						if (updateTactics)
							newFleet.updateTactics();
						
						// Met à jour la flotte et le système
						newFleet.save();
						newSystem.save();
						
					}
				}
				
				
				
				/// Met à jour l'affichage des joueurs connectés sur le secteur
				UpdateTools.queueAreaUpdate(newFleet.getIdCurrentArea(),
				player.getId(), new Point(newFleet.getX(), newFleet.getY()));
				
				JSONStringer json = new JSONStringer();
				
				FleetTools.getPlayerFleet(json, newFleet.getId());
				UpdateTools.queuePlayerFleetsUpdate(newFleet.getIdOwner());
				UpdateTools.queuePlayerSystemsUpdate(newFleet.getIdOwner());
				UpdateTools.queueAreaUpdate(newFleet.getOwner());
				
				// return json.toString(); 
			}
			
			int currentArea = fleets[0].getIdCurrentArea();
			Point[] locations = new Point[fleetsCount];
			
			// Met à jour l'affichage des joueurs connectés sur le secteur
			UpdateTools.queueAreaUpdate(currentArea, player.getId(), locations);
			
			ArrayList<Update> updates = new ArrayList<Update>();
			updates.add(Update.getAreaUpdate());
			for (Fleet fleet : fleets){
				updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
			}
			
			return UpdateTools.formatUpdates(player, updates);
		}
		
		// ------------------------------------------------- METHODES PRIVEES -- //
		
		private int getSystemSlotById(StarSystem system, int id){
			for (int systemSlotIndex = 0; systemSlotIndex < GameConstants.SYSTEM_SLOT_COUNT; systemSlotIndex++) {
				if(system.getSlot(systemSlotIndex).getId() == id){
					return systemSlotIndex;
				}
			}
			return -1;
		}
		
		private int getFreeSystemSlotIndex(StarSystem system){
			for (int systemSlotIndex = 0; systemSlotIndex < GameConstants.SYSTEM_SLOT_COUNT; systemSlotIndex++) {
				if(system.getSlot(systemSlotIndex).getId() == 0){
					return systemSlotIndex;
				}
			}
			return -1;
		}
		
		private int getFleetSlotById(Fleet fleet, int id){
			for (int slotIndex = 0; slotIndex < GameConstants.FLEET_SLOT_COUNT; slotIndex++) {
				if(fleet.getSlot(slotIndex).getId() == id){
					return slotIndex;
				}
			}
			return -1;
		}
		
	}