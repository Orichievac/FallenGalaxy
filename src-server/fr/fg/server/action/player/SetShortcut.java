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

package fr.fg.server.action.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.StructureTools;
import fr.fg.server.core.SystemTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetShortcut extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		ArrayList<Update> updates = new ArrayList<Update>();
		long id = (Long) params.get("id");
		String type = (String) params.get("type");
		int shortcut = (Integer) params.get("shortcut");
		
		Fleet targetFleet = null;
		StarSystem targetSystem = null;
		Structure targetStructure = null;
		
		if (type.equals("fleet")) {
			targetFleet = FleetTools.getFleetByIdWithChecks((int) id, player.getId(),
				FleetTools.ALLOW_DELUDE | FleetTools.ALLOW_HYPERSPACE);
		} else if (type.equals("system")) {
			targetSystem = SystemTools.getSystemByIdWithChecks((int) id, player.getId());
		} else if (type.equals("structure")) {
			targetStructure = StructureTools.getStructureByIdWithChecks(id, player.getId());
			
			if (targetStructure.getType() != Structure.TYPE_GENERATOR)
				throw new IllegalOperationException("Seul les " +
					"générateurs peuvent recevoir un raccourci.");
		}
		
		// Efface le raccourci qui peut déjà être affecté
		if (shortcut != -1) {
			List<Fleet> fleets = new ArrayList<Fleet>(player.getFleets());
			
			for (Fleet fleet : fleets) {
				if (fleet.getShortcut() == shortcut)
					synchronized (fleet.getLock()) {
						fleet = DataAccess.getEditable(fleet);
						fleet.setShortcut(-1);
						fleet.save();
					}
				updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
			}
			
			List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
			
			for (StarSystem system : systems) {
				if (system.getShortcut() == shortcut)
					synchronized (system.getLock()) {
						system = DataAccess.getEditable(system);
						system.setShortcut(-1);
						system.save();
					}
				updates.add(Update.getPlayerSystemsUpdate());
			}
			
			List<Structure> structures = new ArrayList<Structure>(player.getStructures());
			
			for (Structure structure : structures) {
				if (structure.getShortcut() == shortcut)
					synchronized (structure.getLock()) {
						structure = DataAccess.getEditable(structure);
						structure.setShortcut(-1);
						structure.save();
					}
				updates.add(Update.getPlayerGeneratorsUpdate());
			}
		}
		
		if (targetFleet != null) {
			synchronized (targetFleet.getLock()) {
				targetFleet = DataAccess.getEditable(targetFleet);
				targetFleet.setShortcut(shortcut);
				targetFleet.save();
			}
		} else if (targetSystem != null) {
			synchronized (targetSystem.getLock()) {
				targetSystem = DataAccess.getEditable(targetSystem);
				targetSystem.setShortcut(shortcut);
				targetSystem.save();
			}
		} else if (targetStructure != null) {
			synchronized (targetStructure.getLock()) {
				targetStructure = DataAccess.getEditable(targetStructure);
				targetStructure.setShortcut(shortcut);
				targetStructure.save();
			}
		}
		
		
		if (targetStructure != null)
			updates.add(Update.getPlayerGeneratorsUpdate());
		else if (targetFleet != null)
			updates.add(Update.getPlayerFleetUpdate(targetFleet.getId()));
		else if (targetSystem != null)
			updates.add(Update.getPlayerSystemsUpdate());
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
