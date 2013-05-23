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

package fr.fg.server.contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.contract.dialog.DialogDetails;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Treaty;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.ContractStateUpdateEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class ContractManager implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		PLAYER_CONTRACT_DEADLINE = 24 * 3600,
		ALLY_CONTRACT_DEADLINE = 48 * 3600;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static ContractManager instance = new ContractManager();
	
	private Map<String, ContractModel> contractModelsByType;
	
	private List<ContractModel> contractModels;
	
	private List<Contract> contractsPvP;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private ContractManager() {
		List<ContractModel> contractModels = new ArrayList<ContractModel>();
		
		// Charge les contrats disponibles
		String packageName = getClass().getPackage().getName();
		Class<?>[] classes = Utilities.getClasses(packageName + ".impl");
		for (Class<?> c : classes) {
			try {
				contractModels.add((ContractModel) c.newInstance());
				LoggingSystem.getServerLogger().trace(
						"Contract registered: '" + c.getName() + "'.");
			} catch (InstantiationException e) {
				// Ignoré
			} catch (Exception e) {
				LoggingSystem.getServerLogger().error("Could not " +
						"load contract: '" + c.getName() + "'.", e);
			}
		}
		
		Map<String, ContractModel> contractModelsByType =
			new HashMap<String, ContractModel>();
		
		for (ContractModel model : contractModels)
			contractModelsByType.put(model.getType(), model);
		
		this.contractModels = Collections.synchronizedList(contractModels);
		this.contractModelsByType = Collections.synchronizedMap(contractModelsByType);
		
		NpcHelper.initializeFactions();
		
		GameEventsDispatcher.addGameEventListener(this, ContractStateUpdateEvent.class);
		
		
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	// Indique si le PNJ est reservé pour un autre joueur
	public static boolean isNpcAvailable(Player player, Fleet fleet) {
		if (fleet.getIdContract() == 0)
			return true;
		
		return fleet.getContract().isAttendee(player);
	}
	
	// Indique si le PNJ est reservé pour un autre joueur V2
	public static boolean isNpcAvailableV2(Player player, Fleet fleet) {
		if (fleet.getIdContract() == 0)
			return true;
		
		if(fleet.getContract().isAttendee(player)){
			if(fleet.getContract().getState().equals(Contract.STATE_FINALIZING) &&
					fleet.getContract().getTarget().equals(Contract.TARGET_ALLY))
			{
				if(fleet.getSystemOver()!=null){
					if(fleet.getSystemOver().getIdOwner()==player.getId())
						return true;
					else
						return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else
			return false;
	}
	
	// Indique l'action que le joueur peut faire avec le PNJ
	public static String getNpcAction(Player player, Fleet fleet) {
		if (fleet.getIdContract() == 0 || !isNpcAvailable(player, fleet))
			return Fleet.NPC_ACTION_NONE;
		
		Contract contract = fleet.getContract();
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		
		return model.getNpcAction(contract, player, fleet);
	}
	
	public static String getTitle(Contract contract) {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		return model.getTitle(contract);
	}
	
	public static String getDescription(Contract contract) {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		return model.getDescription(contract);
	}
	
	public static String getGoal(Contract contract) {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		return model.getGoal(contract);
	}
	
	public static long getDifficulty(Contract contract, Player player) {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		return model.getDifficulty(player);
	}
	
	public static String getDetailedGoal(Contract contract, Player player) {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
			return model.getDetailedGoal(contract, player);
		} else if (contract.getTarget().equals(Contract.TARGET_ALLY)) {
			if (player.getIdAlly() == 0)
				throw new IllegalArgumentException("Vous n'avez pas d'alliance.");
			return model.getDetailedGoal(contract, player.getAlly());
		} else {
			throw new IllegalArgumentException("Mission invalide.");
		}
	}
	
	public void updatePlayerContracts(Player player) {
		// Vérifie que le joueur a au moins un système et n'est pas un joueur IA
		if (player.isAi() || player.getSystems().size() == 0)
			return;
		
//		Algo affectation contrats vs (every hour)
//		Pour tt player
//		Tant que nb contrats < 4
//		Rechercher un contrat en cours compatible (pts,xp, emplacement) non affecté à plus de 10 players
//		Affecter contrat
//		Si nb contrats vs < 2
//		Rechercher dans la liste des contrats vs ceux qui sont valides
//		Si au moins 1 valide :
//		Créer un nouveau contrat en fct (pts,xp, emplacement) player
//
//		 
//
//		Algo affectation contrats solo (every hour)
//		Pour tt players
//		Si nb contrats solo < 2
//		Rechercher dans la liste des contrats solos ceux qui sont valides
//		Si au moins 1 valide :
//		Créer un nouveau contrat en fct (pts,xp, emplacement) player
		
//		Mission solo / Mission joueur contre joueur / Mission alliance contre alliance
		
		
//		check_valid_range => permet de déterminer si un joueur / alliance peut participer à un contrat déjà créé
//		
//		méga contrats galactiques > qui concernent toute la galaxie (destruction d'un pirate...)
//		récompense solo = x1 / multi = x1.5 + .5 / joueur ?
		
		// Contrats ne pouvant être fait qu'une fois en même temps.
		int matchPirateHunt=0;
		int matchAllyPirateHunt=0;
		int matchPirateOffensive=0;
		int matchAllyPirateOffensive=0;
		
		
		
		
		// Dénombre les contrats solo / vs
		List<ContractAttendee> contractAttendees =
			new ArrayList<ContractAttendee>(
				DataAccess.getAttendeesByPlayer(player));
		
		int countPlayerSingle = 0;
		int countPlayerVersus = 0;
		
		for (ContractAttendee contractAttendee : contractAttendees) {
			Contract contract = contractAttendee.getContract();
			
			if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
				if (contract.getMaxAttendees() == 1)
					countPlayerSingle++;
				else {
					countPlayerVersus++;
					
					//Suppression des attendees non présent durant 36h
					//En fait le joueur lui même
					if(Utilities.now() - contractAttendee.getDate() > 36 * 3600 && 
							!contractAttendee.isRegistered())
					{
						//On vérifie s'il y a toujours un attendees dans le contrat
						if(DataAccess.getAttendeesByContract(contract.getId()).size()>1){
							contractAttendees.remove(contractAttendee);
							contractAttendee.delete();
						}
						else
						{
							//Suppression du contrat
							contractsPvP.remove(contract);
							contract.delete();
						}
					}
					
					
					//Suppression des contrats non démarré depuis 96h
					if(contract.getState().equals(Contract.STATE_WAITING) &&
							Utilities.now()-contract.getStateDate() > 4 * 24 * 60 * 60)
					{
						//Suppression du contrat
						contractsPvP.remove(contract);
						contract.delete();
					}
					
				}
				if(contract.getType().equals("PirateHunt"))
					matchPirateHunt++;
				else if(contract.getType().equals("PirateOffensive"))
					matchPirateOffensive++;
				}
		}
		
		// Affecte les contrats solo
		boolean updateContracts = false;
		if (countPlayerSingle < 2) {
			ArrayList<ContractModel> admissibleContractModels =
				new ArrayList<ContractModel>();
			
			synchronized (contractModels) {
				for (ContractModel model : contractModels)
					if (model.getMaxAttendees() > 0 &&
							model.isAdmissible(player) &&
							model.getContractType().equals(Contract.TYPE_SOLO))
					{
						if(model.getType().equals("PirateHunt") && matchPirateHunt>0)
							continue;
						else if(model.getType().equals("PirateOffensive") && matchPirateOffensive>0)
							continue;
						admissibleContractModels.add(model);
					}
			}
			
			if (admissibleContractModels.size() > 0) {
				ContractModel model = admissibleContractModels.get(
					(int) (Math.random() * admissibleContractModels.size()));
				
				
				model.createContract(player, 1);
				updateContracts = true;
			}
		}
		
		
		if (updateContracts)
			UpdateTools.queueContractsUpdate(player.getId());
		
		
		/******************************************/
		/*******************PvP*******************/
		/******************************************/
		
		//TODO suppression contrat en cours depuis trop longtemps?
		//Nécessaire de gerer les contrats PvP car actuellement pas d'abandon
		//Sinon abus possible
		
		// Affecte les contrats multi
		if (countPlayerVersus < 2) {
			ArrayList<ContractModel> admissibleContractModels =
				new ArrayList<ContractModel>();
			
			//contractsPvP.size()<100 pour limiter le nombre de contrats max non full
			//contractsPvP.size()>10 pour avoir un minimum de 10 contrats pvp qui circule
			//Math.random()<0.80 pour faire de nouveau contrat de temps en temps
			

			//TODO récompense, relation etc qui reçoit (contrat impl), bonus x combien?(model)
			if(contractsPvP != null && contractsPvP.size()>10 && Math.random()<0.80){ 
				
				int tries = 0;
				boolean found = false;
				
				List<Contract> contractsList = contractsPvP;
				
				//TODO
				//Créer un contrat si aucun de trouvé?
				
				//TODO
				//Faire priorité pour new contract
				//Cache de différent niveaux de priorité par rapport a l'heure/date de création
				
				//GvG : Attendre le système de groupe,
				//non pas utilsable uniquement pour le GvG

				//TODO
				//Verification si changement d'alliance au début du contrat dans
				//cette classe. 
				//Un joueur peut avoir changé d'alliance entre la création du contrat
				//et du lancement de celui ci
	
				
				//Gestion du pvp et groupe dans le contract manager d'abord
				loop:while(tries < 100 && found == false && contractsList.size()>0){
					//Affecte des contrats aléatoirement, qui existe déjà
					int random = Utilities.random(0,contractsList.size()-1);
					
					Contract contractRandom = contractsList.get(random);
					contractsList.remove(random);
					
					for(ContractAttendee attendee : contractRandom.getAttendees())
					{
						if(attendee.getIdPlayer()==player.getId()){
							
							tries++;
							continue loop;
						}
					}
					
					
					
					//Les contrats sont affectés selon la difficulté
					//Celle ci doit bouger assez rapidement selon les points/lvl/...
					//Les joueurs qui ont une difficulté pour un contrat existant entre
					//0.8 et 1.2 fois la difficuluté du créateur du contrat
					//auront la possibilité d'y participer, selon le placement etc.				
					long searchedDifficulty = getDifficulty(contractRandom, player);
					
					long lowerDifficulty = (long) (searchedDifficulty*0.8);
					long upperDifficulty = (long) (searchedDifficulty*1.2);
		
					if(lowerDifficulty<=contractRandom.getDifficulty() &&
							upperDifficulty>=contractRandom.getDifficulty())
					{

						//Test sur le contractArea
						List<StarSystem> systems = player.getSystems();
						StarSystem system = systems.get((int) (Math.random() * systems.size()));
						Sector sector = system.getArea().getSector();
						
						if(((DataAccess.getAreasByContract(contractRandom.getId())!=null) &&
								DataAccess.getAreasByContract(contractRandom.getId()).get(0).getArea().getSector()==sector) ||
								DataAccess.getAreasByContract(contractRandom.getId())==null){
							
							//For sur tout les attendees pour verifier les treatys
							List<ContractAttendee> attendees = contractRandom.getAttendees();
							for(ContractAttendee contractAttendee : attendees)
							{
								//Vérification des attendees entre eux, pour voir si leurs relations ont changé
								for(ContractAttendee contractAttendee2 : attendees)
								{
									if(contractAttendee.getPlayer().getTreatyWithPlayer(contractAttendee2.getPlayer())==Treaty.ALLIED ||
											contractAttendee.getPlayer().getTreatyWithPlayer(contractAttendee2.getPlayer())==Treaty.ALLY)
									{
										//delete joueur
										attendees.remove(contractAttendee2);
										contractAttendee2.delete();
										
										//TODO
										//Event pour lui dire qu'il à était supprimé car il a changé ses relations avec un participant
									}
										
								}
								
								//verification du nouveau joueur avec les joueurs déjà inscrits
								if(contractAttendee.getPlayer().getTreatyWithPlayer(player)==Treaty.ALLIED ||
										contractAttendee.getPlayer().getTreatyWithPlayer(player)==Treaty.ALLY)
								{
									continue loop;
								}
							}
						
							//Test avec le level max d'écart
							if(player.getLevel()>contractRandom.getAttendees().get(0).getPlayer().getLevel()+GameConstants.MAX_LEVEL_DISTANCE_PVP ||
									player.getLevel()<contractRandom.getAttendees().get(0).getPlayer().getLevel()-GameConstants.MAX_LEVEL_DISTANCE_PVP )
								continue loop;
										
							//On permet plus d'attendees ot registered que le nombre de max attendees
							//Le serveur s'occupe de supprimer le trop plein d'attendee lors du démarage du contrat
							if(contractRandom.getMaxAttendees()*2>DataAccess.getAttendeesByContract(contractRandom.getId()).size())
							{
								contractsPvP.remove(contractRandom);
							}
							
							//Nouveau participant
							ContractAttendee attendee = new ContractAttendee(
									contractRandom.getId(), player.getId(), 0);
								attendee.save();

								found = true;
								
								updateContracts = true;
						}
					}
					tries++;
				}
			}
			else
			{
				//Affecte les nouveaux contrats
				synchronized (contractModels) {
					for (ContractModel model : contractModels)
						if (model.getMaxAttendees() > 1 &&
								model.isAdmissible(player) && 
								(model.getContractType().equals(Contract.TYPE_PVP)||
										model.getContractType().equals(Contract.TYPE_MULTIPLAYER) ||
										model.getContractType().equals(Contract.TYPE_GVG)))
						{
							if(model.getType().equals("PirateHunt") && matchPirateHunt>0)
								continue;
							else if(model.getType().equals("PirateOffensive") && matchPirateOffensive>0)
								continue;
							admissibleContractModels.add(model);
						}
				}
				
				if (admissibleContractModels.size() > 0) {
					ContractModel model = admissibleContractModels.get(
						(int) (Math.random() * admissibleContractModels.size()));
					
					int numberOfAttendees = model.getContractType().equals(Contract.TYPE_GVG) ?
							(2*Utilities.random(2, 4)) : Utilities.random(2, model.getMaxAttendees());
							
					Contract toAdd = model.createContractPvP(player, numberOfAttendees);
					if(toAdd != null)
						contractsPvP.add(toAdd);
					//TODO
					//Système de priorité pour les new contracts afin de les remplir directement?
					
					updateContracts = true;
				}
			}
		}
		
		

		if (updateContracts)
			UpdateTools.queueContractsUpdate(player.getId());
		
		
		/******************************************/
		/*******************Ally*******************/
		/******************************************/
		
		updateContracts=false;
		
		if (player.isAi() || player.getAlly() == null || player.getAlly().getMembersSystems().size()==0)
			return;
		//Compte les contrats d'alliance
		List<ContractAttendee> contractAllyAttendees =
			new ArrayList<ContractAttendee>(
				DataAccess.getAttendeesByPlayer(player));
		
		int countAllySingle = 0;
		int countAllyVersus = 0;
		
		for (ContractAttendee contractAttendee : contractAllyAttendees) {
			Contract contract = contractAttendee.getContract();
			
			if (contract.getTarget().equals(Contract.TARGET_ALLY)) {
				
				// Les récompenses des missions ally doivent rester 48h Maxi
				if(contract.getState().equals(Contract.STATE_FINALIZING)&&
						contract.getStateDate()+172800<Utilities.now())
				{
					//TODO EVENT
					contract.delete();
				}
				
				
				if (contract.getMaxAttendees() == 1)
					countAllySingle++;
				else
					countAllyVersus++;
				
				if(contract.getType().equals("AllyPirateHunt"))
					matchAllyPirateHunt++;
				else if(contract.getType().equals("AllyPirateOffensive"))
					matchAllyPirateOffensive++;
			}
		}
		
		int memberCount=player.getAlly().getMembers().size();
		
		
		// Affecte les contrats ally
		if (countAllySingle < 2 && 
				memberCount >= GameConstants.MIN_MEMBER_FOR_ALLY_CONTRACT) {
			ArrayList<ContractModel> admissibleContractModels =
				new ArrayList<ContractModel>();
			
			synchronized (contractModels) {
				for (ContractModel model : contractModels)
					if (model.getMaxAttendees() > 0 &&
							model.isAdmissible(player.getAlly()) &&
							model.getContractType().equals(Contract.TYPE_ALLY_SOLO))
					{
						if(model.getType().equals("AllyPirateHunt") && matchAllyPirateHunt>0)
							continue;
						else if(model.getType().equals("AllyPirateOffensive") && matchAllyPirateOffensive>0)
							continue;
						
						admissibleContractModels.add(model);
					}
			}
			
			if (admissibleContractModels.size() > 0) {
				ContractModel model = admissibleContractModels.get(
					(int) (Math.random() * admissibleContractModels.size()));
				
				model.createContract(player.getAlly(), 1);
				updateContracts = true;
			}
		}
		
		
		if (updateContracts)
			UpdateTools.queueContractsUpdate(player.getId());
		
		
		
		
		
		
		/******************************************/
		/*******************AvA*******************/
		/******************************************/
		
		updateContracts=false;
		
		// Affecte les contrats multi
		if (countAllyVersus < 1 && memberCount >= GameConstants.MIN_MEMBER_FOR_ALLY_CONTRACT) {
			ArrayList<ContractModel> admissibleContractModels =
				new ArrayList<ContractModel>();
			
			//contractsPvP.size()<100 pour limiter le nombre de contrats max non full
			//contractsPvP.size()>10 pour avoir un minimum de 10 contrats pvp qui circule
			//Math.random()<0.80 pour faire de nouveau contrat de temps en temps
			

			//TODO récompense, relation etc qui reçoit (contrat impl), bonus x combien?(model)
			if(contractsPvP != null && contractsPvP.size()>10 && Math.random()<0.80){ 
				
				int tries = 0;
				boolean found = false;
				
				List<Contract> contractsList = contractsPvP;
				
				//TODO
				//Créer un contrat si aucun de trouvé?
				
				//TODO
				//Faire priorité pour new contract
				//Cache de différent niveaux de priorité par rapport a l'heure/date de création
				
				//GvG : Attendre le système de groupe,
				//non pas utilsable uniquement pour le GvG

				//TODO
				//Verification si changement d'alliance au débutr du contrat dans
				//cette classe. 
				//Un joueur peut avoir changé d'alliance entre la création du contrat
				//et du lancement de celui ci
	
				
				//Gestion du pvp et groupe dans le contract manager d'abord
				loop:while(contractsList.size()>0 && tries < 100 && found == false){
					//Affecte des contrats aléatoirement, qui existe déjà
					int random = Utilities.random(0,contractsList.size()-1);
					
					Contract contractRandom = contractsList.get(random);
					contractsList.remove(random);
					
					for(ContractAttendee attendee : contractRandom.getAttendees())
					{
						if(attendee.getIdAlly()==player.getIdAlly()){
							
							tries++;
							continue loop;
						}
					}
					
					
					
					//Les contrats sont affectés selon la difficultée
					//Celle ci doit bouger assez rapidement selon les points/lvl/...
					//Les joueurs qui ont une difficulté pour un contrat existant entre
					//0.8 et 1.2 fois la difficuluté du créateur du contrat
					//auront la possibilité d'y participer, selon le placement etc.				
					long searchedDifficulty = getDifficulty(contractRandom, player);
					
					long lowerDifficulty = (long) (searchedDifficulty*0.8);
					long upperDifficulty = (long) (searchedDifficulty*1.2);
		
					if(lowerDifficulty<=contractRandom.getDifficulty() &&
							upperDifficulty>=contractRandom.getDifficulty())
					{

						//Test sur le contractArea
						List<StarSystem> systems = player.getAlly().getSystems();
						StarSystem system = systems.get((int) (Math.random() * systems.size()));
						Sector sector = system.getArea().getSector();
						
						if(((DataAccess.getAreasByContract(contractRandom.getId())!=null) &&
								DataAccess.getAreasByContract(contractRandom.getId()).get(0).getArea().getSector()==sector) ||
								DataAccess.getAreasByContract(contractRandom.getId())==null){
							
							//For sur tout les attendees pour verifier les treatys
							List<ContractAttendee> attendees = contractRandom.getAttendees();
							for(ContractAttendee contractAttendee : attendees)
							{
								//Vérification des attendees entre eux, pour voir si leurs relations ont changé
								//Le faire directement dans le changement de treaty? Compliqué pour les alliances
								for(ContractAttendee contractAttendee2 : attendees)
								{
									if(contractAttendee.getAlly().getTreatyWithAlly(contractAttendee2.getAlly())==Treaty.ALLIED ||
											contractAttendee.getAlly().getTreatyWithAlly(contractAttendee2.getAlly())==Treaty.DEFENSIVE ||
											contractAttendee.getAlly().getTreatyWithAlly(contractAttendee2.getAlly())==Treaty.TOTAL)
									{
										//delete ally
										attendees.remove(contractAttendee2);
										contractAttendee2.delete();
										
										//TODO
										//Event pour dire qu'elle a été supprimé car elle a changé ses relations
									}
										
								}
								
								//verification de l'alliance avec celles déjà inscrites
								if(contractAttendee.getAlly().getTreatyWithAlly(player.getAlly())==Treaty.ALLIED ||
										contractAttendee.getAlly().getTreatyWithAlly(player.getAlly())==Treaty.DEFENSIVE ||
										contractAttendee.getAlly().getTreatyWithAlly(player.getAlly())==Treaty.TOTAL)
									
								{
									continue loop;
								}
							}
						
							//Test avec le level max d'écart
							if(player.getAlly().getMediumLevel()>contractRandom.getAttendees().get(0).getAlly().getMediumLevel()+GameConstants.MAX_LEVEL_DISTANCE_PVP ||
									player.getAlly().getMediumLevel()<contractRandom.getAttendees().get(0).getAlly().getMediumLevel()-GameConstants.MAX_LEVEL_DISTANCE_PVP )
								continue loop;
										
							//On permet plus d'attendees ot registered que le nombre de max attendees
							//Le serveur s'occupe de supprimer le trop plein d'attendee lors du démarage du contrat
							if(contractRandom.getMaxAttendees()*2>DataAccess.getAttendeesByContract(contractRandom.getId()).size())
							{
								contractsPvP.remove(contractRandom);
							}
							
							//Nouveau participant
							ContractAttendee attendee = new ContractAttendee(
									contractRandom.getId(), player.getIdAlly(), 0);
								attendee.save();

								found = true;
								
								updateContracts = true;
						}
					}
					tries++;
				}
			}
			else
			{
				//Affecte les nouveaux contrats
				synchronized (contractModels) {
					for (ContractModel model : contractModels)
						if (model.getMaxAttendees() > 1 &&
								model.isAdmissible(player.getAlly()) && 
								(model.getContractType().equals(Contract.TYPE_AVA)))
						{
							if(model.getType().equals("AllyPirateHunt") && matchAllyPirateHunt>0)
								continue;
							else if(model.getType().equals("AllyPirateOffensive") && matchAllyPirateOffensive>0)
								continue;
							admissibleContractModels.add(model);
						}
				}
				
				if (admissibleContractModels.size() > 0) {
					ContractModel model = admissibleContractModels.get(
						(int) (Math.random() * admissibleContractModels.size()));
					
					int numberOfAttendees = Utilities.random(2, model.getMaxAttendees());
							
					Contract toAdd = model.createContractAvA(player.getAlly(), numberOfAttendees);
					if(toAdd != null)
						contractsPvP.add(toAdd);
					//TODO
					//Système de priorité pour les new contracts afin de les remplir directement?
					
					updateContracts = true;
				}
			}
		}
		
		

		if (updateContracts)
			UpdateTools.queueContractsUpdate(player.getId());
		
		
		
		
		
	}
	
	public void updateContracts() {
		long now = Utilities.now();
		List<Contract> contracts =
			new ArrayList<Contract>(DataAccess.getAllContracts());
		
		for (Contract contract : contracts) {
			if (!contract.getState().equals(Contract.STATE_WAITING))
				continue;
			
			if (contract.getTarget().equals(Contract.TARGET_ALLY)) {
				if (contract.getStateDate() + ALLY_CONTRACT_DEADLINE < now) {
					List<ContractAttendee> attendees = contract.getAttendees();
					boolean startInstance = false;
					
					for (ContractAttendee attendee : attendees) {
						if (attendee.isRegistered())
							startInstance = true;
					}
					
					if (startInstance) {
						
					} else {
						contract.delete();
					}
				}
			} else if (contract.getTarget().equals(Contract.TARGET_PLAYER)) {
				
			}
			
			// Supprime les contrats périmés
			
		}
	}
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof ContractStateUpdateEvent) {
			ContractStateUpdateEvent gameEvent = (ContractStateUpdateEvent) event;
			
			if (gameEvent.getNewState().equals(Contract.STATE_RUNNING)) {
				Contract contract = DataAccess.getContractById(gameEvent.getIdContract());
				ContractModel model = instance.contractModelsByType.get(contract.getType());
				model.launch(contract);
			}
		}
	}
	
	public static DialogDetails talk(Contract contract, Fleet source, Fleet target,
			int option) throws IllegalOperationException {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		return model.talk(contract, source, target, option);
	}
	
	public static ContractState getContractState(Contract contract, Player player) {
		ContractModel model = instance.contractModelsByType.get(contract.getType());
		return model.getState(contract, player);
	}
	
	public final static ContractManager getInstance() {
		return instance;
	}

	public void updateContractsPvP() {
		contractsPvP = DataAccess.getMultiContractsWaiting();
		
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
