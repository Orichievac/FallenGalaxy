/*
Copyright 2012 jgottero

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;

public class UpdatePremiumAccounts implements Runnable {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		Collection<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		for (Player player : players) {
			// Diminue d'1 heure les comptes premiums
			if (player.getPremiumHours() > 0) {
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setPremiumHours(player.getPremiumHours() - 1);
					player.save();
				}
			}
			
			// Compte premium expire dans X jours
			if (player.getPremiumHours() == 24 * 2) {
				Event event = new Event(Event.EVENT_PREMIUM_NEAR_END,
					Event.TARGET_PLAYER, player.getId(), 0, -1, -1, "2");
				event.save();
			}
			
			if (player.getPremiumHours() == 24 * 7) {
				Event event = new Event(Event.EVENT_PREMIUM_NEAR_END,
					Event.TARGET_PLAYER, player.getId(), 0, -1, -1, "7");
				event.save();
			}
			
			// Fin du compte premium
			if (player.getPremiumHours() == 0 &&
					player.isPremium() &&
					!player.hasRight(Player.SUPER_ADMINISTRATOR)) {
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setRights(player.getRights() - Player.PREMIUM);
					player.setSettingsGeneralVolume(0);
					player.setSettingsSoundVolume(0);
					player.setSettingsMusicVolume(0);
					player.setSettingsGraphics(0);
					player.setSettingsFleetSkin(1);
					player.setSettingsTheme(GameConstants.THEMES[0]);
					player.save();
				}
				
				// Annule les technologies en attente du joueur
				List<Research> reasearches = new ArrayList<Research>(player.getResearches());
				
				for (Research research : reasearches) {
					if (research.getQueuePosition() > 0) {
						synchronized (research.getLock()) {
							research = DataAccess.getEditable(research);
							research.setQueuePosition(-1);
							research.save();
						}
					}
				}
				
				Event event = new Event(Event.EVENT_PREMIUM_END,
					Event.TARGET_PLAYER, player.getId(), 0, -1, -1);
				event.save();
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
