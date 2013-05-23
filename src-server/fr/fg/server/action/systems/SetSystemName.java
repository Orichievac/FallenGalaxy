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
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetSystemName extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idSystem = (Integer) params.get("id");
		String name = (String) params.get("name");
		
		// Vérifie que le nom n'est pas blacklisté
		if (Badwords.containsBadwords(name))
			throw new IllegalOperationException("Le nom choisi n'est pas autorisé.");
		
		StarSystem system = DataAccess.getSystemById(idSystem);
		
		// Vérifie que le système existe
		if (system == null)
			throw new IllegalOperationException("Le système n'existe pas.");
		
		// Vérifie que le système appartient au joueur
		if (system.getIdOwner() != player.getId())
			throw new IllegalOperationException("Le système ne vous appartient pas.");
		
		synchronized (system.getLock()) {
			StarSystem newSystem = DataAccess.getEditable(system);
			newSystem.setName(name);
			DataAccess.save(newSystem);
		}
		
		// Met à jour le secteur
		UpdateTools.queueAreaUpdate(system.getIdArea(), player.getId(), false);
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerSystemsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
