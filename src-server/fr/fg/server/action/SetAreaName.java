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

package fr.fg.server.action;

import java.util.Map;


import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class SetAreaName extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int id = (Integer) params.get("area");
		String name = (String) params.get("name");
		
		Area area = DataAccess.getAreaById(id);
		
		// Vérifie que le secteur existe
		if (area == null)
			throw new IllegalOperationException("Le secteur n'existe pas.");
		
		// Vérifie que le secteur est dominé par l'alliance du joueur
		int idAlly = area.getIdDominatingAlly();
		
		if (idAlly == 0 || idAlly != player.getIdAlly())
			throw new IllegalOperationException("Le secteur n'est pas sous la " +
					"domination de votre alliance.");
		
		// Vérifie que le joueur est le dirigeant de l'alliance
		Ally ally = player.getAlly();
		
		if (player.getAllyRank() != ally.getLeaderRank())
			throw new IllegalOperationException("Seul le dirigeant de l'alliance " +
					"peut renommer le secteur.");
		
		// Vérifie que le nom du système n'a pas été modifié durant 72h
		if (area.getLastNameUpdate() + 259200 > Utilities.now())
			throw new IllegalOperationException("Vous devez attendre 72h avant de " +
					"pouvoir renommer le secteur.");
		
		// Vérifie qu'un secteur portant ce nom n'existe pas déjà
		if (DataAccess.getAreaByName(name) != null)
			throw new IllegalOperationException("Un secteur portant ce nom existe déjà.");
		
		// Vérifie que le nom est autorisé
		if (Badwords.containsBadwords(name))
			throw new IllegalOperationException("Ce nom n'est pas autorisé.");
		
		// Modifie le nom du système
		synchronized (ally.getLock()) {
			Area newArea = DataAccess.getEditable(area);
			newArea.setLastNameUpdate(Utilities.now());
			newArea.setName(name);
			DataAccess.save(newArea);
		}
		
		// TODO jgottero teste si la domination ou si le nom du secteur à changé dans le js
		UpdateTools.queueAreaUpdate(area.getId());
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
