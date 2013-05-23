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

import java.util.Map;

import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetSpaceStationName extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		// Paramètres de l'action
		int idSpaceStation = (Integer) params.get("id");
		String name = (String) params.get("name");
		
		// Vérifie que le nom n'est pas blacklisté
		if (Badwords.containsBadwords(name))
			throw new IllegalOperationException("Ce nom n'est pas autorisé.");
		
		SpaceStation spaceStation = DataAccess.getSpaceStationById(idSpaceStation);
		
		if (spaceStation == null)
			throw new IllegalOperationException("Station invalide.");
		
		if (player.getIdAlly() != spaceStation.getIdAlly())
			throw new IllegalOperationException("Cette station n'appartient pas à votre alliance.");
		
		if (player.getAllyRank() < player.getAlly().getRequiredRank(Ally.RIGHT_MANAGE_STATIONS))
			throw new IllegalOperationException("Vous n'avez pas le rang nécessaire pour pouvoir modifier le nom de la station.");
		
		// Modifie le nom de la station
		synchronized (spaceStation.getLock()) {
			SpaceStation newSpaceStation = DataAccess.getEditable(spaceStation);
			newSpaceStation.setName(name);
			DataAccess.save(newSpaceStation);
		}
		
		// Met à jour le secteur
		UpdateTools.queueAreaUpdate(spaceStation.getIdArea(), player.getId(), false);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
