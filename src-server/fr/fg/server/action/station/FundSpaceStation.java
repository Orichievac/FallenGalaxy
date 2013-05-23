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

package fr.fg.server.action.station;

import java.util.ArrayList;
import java.util.Map;

import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterSpaceStationFund;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class FundSpaceStation extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idStation = (Integer) params.get("station");
		long credits = (Long) params.get("credits");
		
		SpaceStation spaceStation = DataAccess.getSpaceStationById(idStation);
		
		if (spaceStation == null)
			throw new IllegalOperationException("La station spatiale n'existe pas.");
		
		if (player.getIdAlly() == 0 || spaceStation.getIdAlly() != player.getIdAlly())
			throw new IllegalOperationException("La station spatiale " +
				"n'appartient pas à votre alliance.");
		
		if (spaceStation.getLevel() == 5)
			throw new IllegalOperationException("La station spatiale a " +
				"atteint le niveau maximal et ne peut plus être améliorée.");
		
		player = Player.updateCredits(player);
		
		if (player.getCredits() < credits)
			throw new IllegalOperationException(
				"Vous n'avez pas suffisament de crédits.");
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-credits);
			player.save();
		}
		
		// Transfert les crédits sur la station
		boolean levelUp;
		
		synchronized (spaceStation.getLock()) {
			spaceStation = DataAccess.getEditable(spaceStation);
			spaceStation.setCredits(spaceStation.getCredits() + credits);
			levelUp = spaceStation.tryLevelUp();
			spaceStation.save();
		}
		
		GameEventsDispatcher.fireGameEvent(new AfterSpaceStationFund(
				player, spaceStation, credits, new long[4]));
		
		// Met à jour l'affichage des membres de l'alliance connectés sur le
		// secteur
		if (levelUp) {
			spaceStation.getArea().getSector().updateInfluences();
			
			ArrayList<Player> members = new ArrayList<Player>(player.getAlly().getMembers());
			
			for (int i = 0; i < members.size(); i++)
				if (members.get(i).getId() == player.getId()) {
					members.remove(i);
					break;
				}
			
			Event event = new Event(Event.EVENT_STATION_UPGRADED, Event.TARGET_ALLY,
				player.getIdAlly(), spaceStation.getIdArea(), spaceStation.getX(), spaceStation.getY(),
				spaceStation.getName(), String.valueOf(spaceStation.getLevel()));
			event.save();
			
			UpdateTools.queueNewEventUpdate(members, false);
			UpdateTools.queueAreaUpdate(members);
			
			return UpdateTools.formatUpdates(
				player,
				Update.getNewEventUpdate(),
				Update.getPlayerSystemsUpdate(),
				Update.getAreaUpdate()
			);
		} else {
			UpdateTools.queueAreaUpdate(spaceStation.getIdArea(), player.getId());
			
			return UpdateTools.formatUpdates(
				player,
				Update.getPlayerSystemsUpdate(),
				Update.getAreaUpdate()
			);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
