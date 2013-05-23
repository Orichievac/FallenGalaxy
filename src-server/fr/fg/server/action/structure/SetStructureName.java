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

package fr.fg.server.action.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Structure;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetStructureName extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Paramètres de l'action
		long idStructure = (Long) params.get("id");
		String name = (String) params.get("name");
		
		// Vérifie que le nom n'est pas blacklisté
		if (Badwords.containsBadwords(name))
			throw new IllegalOperationException("Ce nom n'est pas autorisé.");
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
				idStructure, player.getId());
		
		// Modifie le nom de la structure
		synchronized (structure.getLock()) {
			structure = DataAccess.getEditable(structure);
			structure.setName(name);
			structure.save();
		}
		
		// Met à jour le secteur
		UpdateTools.queueAreaUpdate(structure.getIdArea(), player.getId(), false);
		
		List<Update> updates = new ArrayList<Update>();
		
		updates.add(Update.getAreaUpdate());
		if (structure.getType() == Structure.TYPE_GENERATOR)
			updates.add(Update.getPlayerGeneratorsUpdate());
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
