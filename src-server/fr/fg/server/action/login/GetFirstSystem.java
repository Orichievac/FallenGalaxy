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

package fr.fg.server.action.login;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fr.fg.client.data.StateData;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.InitializationTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.VisitedArea;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.FirstSystemReceivedEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class GetFirstSystem extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static double
		LOW_COLONIZATION_COEF = .6,
		HIGH_COLONIZATION_COEF = .85;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	private final int PREMIUM_TIME = 604800; //On met 7 jours de Premium à chaque nouveau joueur
	
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String near = (String) params.get("near");
		
		if (player.getSystems().size() != 0)
			throw new IllegalOperationException("Vous avez déjà un système.");
		
		StarSystem playerSystem;
		
		if (near.length() == 0) {
			playerSystem = getRandomSystem();
		} else {
			Player nearPlayer = DataAccess.getPlayerByLogin(near);
			
			if (nearPlayer == null)
				throw new IllegalOperationException("Joueur inconnu.");
			
			if (nearPlayer.getSystems().size() == 0)
				throw new IllegalOperationException(
						"Le joueur n'a pas encore de système.");
			
			playerSystem = getSystemNearPlayer(nearPlayer);
		}
		
		// Affecte le système au joueur
		synchronized (playerSystem.getLock()) {
			StarSystem newSystem = DataAccess.getEditable(playerSystem);
			newSystem.setOwner(player.getId());
			newSystem.setStartSettings();
			DataAccess.save(newSystem);
			
			Fleet fleet = new Fleet(
				"Flotte I",
				newSystem.getX(),
				newSystem.getY() + 2,
				player.getId(),
				newSystem.getIdArea());
			fleet.setSlot(new Slot(Ship.RECON, 50, true), 0);
			fleet.setSlot(new Slot(Ship.MULE,  10, true), 1);
			fleet.save();
			
			VisitedArea visitedArea = new VisitedArea(
				player.getId(),
				newSystem.getIdArea(),
				Utilities.now());
			visitedArea.save();
		}
		
		Message welcomeMessage = new Message("Bienvenue sur Fallen Galaxy",
			"Bonjour " + player.getLogin() + ",<br/><br/>" +
			"Vous jouez sur la version 1.0 de Fallen Galaxy. Nous vous " +
			"encourageons à donner votre avis et à reporter les bugs " +
			"trouvés sur le forum : <a href=\"http://fallengalaxy.eu/forum\" " +
			"target=\"_blank\">http://fallengalaxy.eu/forum</a>.<br/><br/>" +
			"Une barre de signature pour les forums est accessible à l'" +
			"adresse suivante : <a href=\"" + Config.getServerURL() + "player/" +
			URLEncoder.encode(player.getLogin(), "UTF-8") + "\" target=\"_blank\">" +
			Config.getServerURL() + "player/" +
			URLEncoder.encode(player.getLogin(), "UTF-8") + "</a>.<br/>" +
			"<img src=\"" + Config.getServerURL() + "player/" +
			URLEncoder.encode(player.getLogin(), "UTF-8") + "\"/><br/><br/>" +
			
			"Vous disposez d'un compte premium durant 7 jours gratuitement.<br/><br/>"+
			"Nous vous souhaitons un bon jeu,<br/><br/>" +
			"L'equipe de Fallen Galaxy", 0, player.getId());
		welcomeMessage.save();
		
    	long now = Utilities.now();
		long timeElapsed = now - Config.getOpeningDate();
    	long minutesElapsed = timeElapsed / 60;
    	
    	long creditToAdd = 25000;
    	if(minutesElapsed < 60*24*7)
    		creditToAdd+=minutesElapsed*2;
    	else if(minutesElapsed < 60*24*7*2)
    		creditToAdd+=minutesElapsed*5;
    	else if(minutesElapsed < 60*24*7*3)
    		creditToAdd+=minutesElapsed*8;
    	else
    		creditToAdd+=minutesElapsed*13;

    	// Gestion des serveurs non ouvert
    	if(creditToAdd<0)
    		creditToAdd = 25000;
    	
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			if(player.getRights()<3)
			player.setRights(1);
			player.setEndPremium(Utilities.now()+PREMIUM_TIME);
			player.setCredits(creditToAdd);
			player.setRights(player.getRights() | Player.PREMIUM);
			player.setPremiumHours(24 * 7);
			player.save();
		}
		
		ContractManager.getInstance().updatePlayerContracts(player);
		
		// Déclenche un évènement pour l'affection du système initial du joueur
		GameEventsDispatcher.fireGameEvent(
				new FirstSystemReceivedEvent(player, playerSystem));
		
		// Met à jour le secteur chez les autres joueurs
		UpdateTools.queueAreaUpdate(playerSystem.getIdArea());
		
		// Renvoie les informations sur le joueur
		JSONStringer json = new JSONStringer();
		json.object().
			key(StateData.FIELD_STATE).value(StateData.STATE_ONLINE);
		InitializationTools.getInitializationData(json, player);
		json.endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private StarSystem getRandomSystem() throws IllegalOperationException {
		List<Area> areas = new ArrayList<Area>(DataAccess.getAllAreas());
		StarSystem selectedSystem = null;
		
		Collections.sort(areas, new Comparator<Area>() {
			public int compare(Area a1, Area a2) {
				return a1.getId() < a2.getId() ? -1 : 1;
			}
		});
		
		for (Area area : areas) {
			if (area.getType() == Area.AREA_START && !area.isFull()) {
				StarSystem system = getFreeSystemInArea(
						area, LOW_COLONIZATION_COEF);
				
				if (system != null) {
					selectedSystem = system;
					break;
				}
			}
		}
		
		if (selectedSystem == null)
			throw new IllegalOperationException("La galaxie est pleine " +
				"pour le moment. Réessayez plus tard.");
		
		return selectedSystem;
	}
	
	private StarSystem getSystemNearPlayer(Player player)
			throws IllegalOperationException {
		StarSystem startSystem = player.getFirstSystem();
		
		if (startSystem == null)
			throw new IllegalOperationException("Aucun système libre proche " +
					"du joueur n'a été trouvé.");
		
		if(startSystem.getArea().getSector().getLvlMax()>11)
			throw new IllegalOperationException("Aucun système libre proche " +
			"du joueur n'a été trouvé.");
		
		StarSystem system = getFreeSystemInArea(
				startSystem.getArea(), HIGH_COLONIZATION_COEF);
		
		if (system == null)
			throw new IllegalOperationException("Aucun système libre proche " +
				"du joueur n'a été trouvé.");
		
		return system;
	}
	
	private StarSystem getFreeSystemInArea(Area area, double colonizationCoef) {
		List<StarSystem> systems = area.getSystems();
		
		synchronized (systems) {
			// Compte le nombre de système affectés à l'IA et à des
			// joueurs
			int count = 0;
			int ai = 0;
			for (StarSystem system : systems) {
				if (system.isAi())
					ai++;
				else if (system.getIdOwner() != 0)
					count++;
			}
			
			// Nombre max de systèmes qui peuvent être affectés à des
			// joueurs dans le secteur
			int limit = (int) Math.floor(
					(systems.size() - ai) * colonizationCoef);
			
			if (count < limit) {
				systems:for (StarSystem system : systems) {
					if (!system.isAi() && system.getIdOwner() == 0) {
						// Vérifie que le système n'est pas en cours de
						// colonisation
						List<Fleet> fleets = area.getFleets();
						
						synchronized (fleets) {
							for (Fleet fleet : fleets) {
								if (fleet.getCurrentAction().equals(Fleet.CURRENT_ACTION_COLONIZE) &&
										fleet.getSystemOver().getId() == system.getId())
									continue systems;
							}
						}
						
						if (count + 1 >= limit) {
							synchronized (area.getLock()) {
								Area newArea = DataAccess.getEditable(area);
								newArea.setFull(true);
								newArea.save();
							}
						}
						
						return system;
					}
				}
			}
			else
			{
				synchronized (area.getLock()) {
					Area newArea = DataAccess.getEditable(area);
					newArea.setFull(true);
					newArea.save();
				}
			}
		}
		
		return null;
	}
}
