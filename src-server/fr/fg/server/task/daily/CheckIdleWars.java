/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.server.task.daily;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Treaty;
import fr.fg.server.util.Utilities;

public class CheckIdleWars extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("CheckIdleWars (daily)");
		// Annule les traités de guerre inactifs
		List<Treaty> treaties = new ArrayList<Treaty>(
				DataAccess.getAllTreaties());
		
		for (Treaty treaty : treaties) {
			if (treaty.equals(Treaty.TYPE_WAR)) {
				if (treaty.getSource() != 0 &&
						Utilities.now() > treaty.getLastActivity() +
						GameConstants.WAR_INACTIVITY) {
					treaty.delete();
					
					// TODO jgottero event spécial pour la paix par inactivé
					DataAccess.save(new Event(
						Event.EVENT_PLAYER_NEW_PEACE,
						Event.TARGET_PLAYER,
						treaty.getIdPlayer1(), 0, -1, -1,
						treaty.getPlayer2().getLogin()));
					DataAccess.save(new Event(
						Event.EVENT_PLAYER_NEW_PEACE,
						Event.TARGET_PLAYER,
						treaty.getIdPlayer2(), 0, -1, -1,
						treaty.getPlayer1().getLogin()));
					
					UpdateTools.queueNewEventUpdate(treaty.getIdPlayer1(), false);
					UpdateTools.queueNewEventUpdate(treaty.getIdPlayer2(), false);
					
					UpdateTools.queueAreaUpdate(
						treaty.getPlayer1(), treaty.getPlayer2());
				}
			}
		}
		
		List<AllyTreaty> allyTreaties = new ArrayList<AllyTreaty>(
				DataAccess.getAllAllyTreaties());
		
		for (AllyTreaty allyTreaty : allyTreaties) {
			if (allyTreaty.equals(Treaty.TYPE_WAR)) {
				if (allyTreaty.getSource() != 0 &&
						Utilities.now() > allyTreaty.getLastActivity() +
						GameConstants.WAR_INACTIVITY) {
					allyTreaty.delete();
					
					// TODO jgottero event spécial pour la paix par inactivé
					DataAccess.save(new Event(
						Event.EVENT_ALLY_NEW_PEACE,
						Event.TARGET_ALLY,
						allyTreaty.getIdAlly1(), 0, -1, -1,
						allyTreaty.getAlly2().getName()));
					DataAccess.save(new Event(
						Event.EVENT_ALLY_NEW_PEACE,
						Event.TARGET_ALLY,
						allyTreaty.getIdAlly2(), 0, -1, -1,
						allyTreaty.getAlly1().getName()));
					
					// Mise à jour des secteurs consultés pour les joueurs
					// de l'alliance, afin que la ratification du
					// du traité de paix soit prise en compte, au besoin
					UpdateTools.queueNewEventUpdate(allyTreaty.getAlly1().getMembers(), false);
					UpdateTools.queueNewEventUpdate(allyTreaty.getAlly2().getMembers(), false);
					
					UpdateTools.queueAreaUpdate(allyTreaty.getAlly1().getMembers());
					UpdateTools.queueAreaUpdate(allyTreaty.getAlly2().getMembers());
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
