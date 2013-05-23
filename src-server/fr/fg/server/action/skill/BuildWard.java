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

package fr.fg.server.action.skill;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Ward;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterWardBuiltEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class BuildWard extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui construit la balise
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		String wardType = (String) params.get("ward");
		int power = (Integer) params.get("power");
		
		// Vérifie que la flotte a la compétence requise
		int requiredSkill = Ward.getRequiredSkill(wardType);
		
		if (fleet.getSkillLevel(requiredSkill) <
				Ward.getRequiredSkillLevel(wardType))
			throw new IllegalOperationException("La flotte n'a pas " +
					"la compétence nécessaire au niveau suffisant.");
		
		// Vérifie que la flotte a du mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir construire.");
		
		// Vérifie que la flotte a suffisament d'anti-matière
		long cost = Ward.getWardCost(wardType, power);
		
		if (fleet.getItemContainer().getResource(3) < cost)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de ressources pour pouvoir construire. " +
				cost + " " + Utilities.getResourceImg(3) + " nécessaires.");
		
		// Vérifie qu'il n'y pas déjà une structure construite à cet endroit
		Area area = fleet.getArea();
		List<Ward> wards = area.getWards();
		
		int fleetX = fleet.getCurrentX();
		int fleetY = fleet.getCurrentY();
		
		synchronized (wards) {
			for (Ward ward : wards) {
				if (ward.getX() == fleetX && ward.getY() == fleetY) {
					throw new IllegalOperationException("Une structure a " +
							"déjà été construite à cet endroit.");
				}
			}
		}
		
		// Vérifie que la charge n'est pas a proximité d'un système / d'un relai HE
		if (requiredSkill == Skill.SKILL_PYROTECHNIST) {
			List<StellarObject> gates = area.getGates();
			int radius = GameConstants.HYPERGATE_JUMP_RADIUS + 1;
			
			for (StellarObject gate : gates) {
				int dx = gate.getX() - fleetX;
				int dy = gate.getY() - fleetY;
				
				if (dx * dx + dy * dy <= radius * radius)
					throw new IllegalOperationException("Les charges ne " +
						"peuvent pas être construites à proximité des " +
						"relais hyperspatiaux.");
			}
			
			List<StarSystem> systems = area.getSystems();
			radius = GameConstants.SYSTEM_RADIUS +
				Ward.MINE_TRIGGER_RADIUS + 1;
			
			for (StarSystem system : systems) {
				int dx = system.getX() - fleetX;
				int dy = system.getY() - fleetY;
				
				if (dx * dx + dy * dy <= radius * radius)
					throw new IllegalOperationException("Les charges ne " +
						"peuvent pas être construites à proximité des " +
						"systèmes.");
			}
		}
		
		// Vérifie que la flotte a la puissance nécessaire
		if (requiredSkill == Skill.SKILL_PYROTECHNIST) {
			if (power > fleet.getPowerLevel())
				throw new IllegalOperationException("La puissance de la " +
					"charge ne peut pas dépasser la puissance de votre flotte.");
		}
		
		// Supprime les ressources de la flotte
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.addXp(Ward.getWardXp(wardType));
			fleet.doAction(Fleet.CURRENT_ACTION_BUILD_WARD,
				Math.max(fleet.getMovementReload(),
				Utilities.now() + (requiredSkill == Skill.SKILL_ENGINEER ?
				Skill.SKILL_ENGINEER_MOVEMENT_RELOAD :
				Skill.SKILL_PYROTECHNIST_MOVEMENT_RELOAD)));
			fleet.save();
		}
		
		ItemContainer itemContainer = fleet.getItemContainer();
		
		synchronized (itemContainer.getLock()) {
			itemContainer = DataAccess.getEditable(itemContainer);
			itemContainer.addResource(-cost, 3);
			itemContainer.save();
		}
		
		// Construit la structure
		Ward ward = new Ward(fleetX, fleetY, wardType, power,
				fleet.getIdCurrentArea(), player.getId());
		ward.save();
		
		GameEventsDispatcher.fireGameEvent(new AfterWardBuiltEvent(fleet, ward));
		
		// Met à jour le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
				player.getId(), new Point(fleetX, fleetY));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
