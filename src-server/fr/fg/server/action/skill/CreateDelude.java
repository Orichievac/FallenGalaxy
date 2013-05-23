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
import fr.fg.server.data.FleetLink;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class CreateDelude extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui crée le leurre
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		// Vérifie que la flotte a la compétence leurre
		if (fleet.getSkillLevel(Skill.SKILL_ULTIMATE_DELUDE) < 0)
			throw new IllegalOperationException(
					"La flotte n'a pas la compétence leurre.");
		
		// Vérifie que la compétence n'est pas en train de se recharger
		if (fleet.getSkillUltimateReload() > Utilities.now())
			throw new IllegalOperationException("La compétence est en cours de rechargement.");
		
		// Vérifie que la flotte a suffisament de mouvement
		if (fleet.getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de mouvement pour pouvoir utiliser la compétence.");
		
		// Vérifie que la flotte a suffisament d'anti-matière
		int cost = Skill.getDeludeCost(fleet.getPowerLevel());
		
		if (fleet.getItemContainer().getResource(3) < cost)
			throw new IllegalOperationException("La flotte n'a pas " +
				"suffisament de ressources pour pouvoir construire. " +
				cost + " " + Utilities.getResourceImg(3) + " nécessaires.");
		
		// Vérifie que le maximum de leurres n'a pas été atteind
		List<FleetLink> links = DataAccess.getFleetLinks(fleet.getId());
		
		int deludesCount = 0;
		synchronized (links) {
			for (FleetLink link : links) {
				if (link.isDelude())
					deludesCount++;
			}
		}
		
		int maxDeludes = fleet.getSkillLevel(Skill.SKILL_ULTIMATE_DELUDE) == 2 ? 2 : 1;
		
		if (deludesCount >= maxDeludes)
			throw new IllegalOperationException("La flotte ne peut pas créer plus de leurres.");
		
		// Cherche la case libre la plus proche de la flotte
		int x = -1, y = -1;
		int fleetX = fleet.getCurrentX(), fleetY = fleet.getCurrentY();
		boolean found = false;
		
    	Area area = fleet.getArea();
    	
    	for (int step = 0; step < 1000 && !found; step++)
    		for (int i = -step; i <= step && !found; i++) {
    			if (area.isFreeTile(fleetX + i, fleetY - step,
    					Area.CHECK_FLEET_MOVEMENT, player)) {
    				x = fleetX + i;
    				y = fleetY - step;
    				found = true;
    			} else if (area.isFreeTile(fleetX - step, fleetY + i,
    					Area.CHECK_FLEET_MOVEMENT, player)) {
    				x = fleetX - step;
    				y = fleetY + i;
    				found = true;
    			} else if (area.isFreeTile(fleetX + i, fleetY + step,
    					Area.CHECK_FLEET_MOVEMENT, player)) {
    				x = fleetX + i;
    				y = fleetY + step;
    				found = true;
    			} else if (area.isFreeTile(fleetX + step, fleetY + i,
    					Area.CHECK_FLEET_MOVEMENT, player)) {
    				x = fleetX + step;
    				y = fleetY + i;
    				found = true;
    			}
    		}
		
    	if (x == -1)
    		throw new IllegalOperationException("Impossible de créer un leurre.");
    	
		// Crée le leurre
		Fleet delude = new Fleet(player.getNextFleetName(), x, y, fleet.getIdOwner(), fleet.getIdCurrentArea());
		delude.setTag(8);
		delude.setEncodedSkirmishSlots(fleet.getEncodedSkirmishSlots());
		delude.setEncodedSkirmishAbilities(fleet.getEncodedSkirmishAbilities());
		delude.setEncodedBattleSlots(fleet.getEncodedBattleSlots());
		delude.setEncodedBattleAbilities(fleet.getEncodedBattleAbilities());
		delude.setEncodedSkill0(fleet.getEncodedSkill0());
		delude.setEncodedSkill1(fleet.getEncodedSkill1());
		delude.setEncodedSkill2(fleet.getEncodedSkill2());
		delude.setEncodedSkillUltimate(fleet.getEncodedSkillUltimate());
		delude.setTacticsDefined(true);
		delude.setSlots(fleet.getSlots());
		delude.save();
		
		FleetLink link = new FleetLink(fleet.getId(), delude.getId(), FleetLink.LINK_DELUDE);
		link.save();
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setSkillUltimateReload(Utilities.now() +
				GameConstants.ACTION_MOVEMENT_RELOAD);
			fleet.setSkillUltimateLastUse(Utilities.now());
			fleet.addXp(Skill.SKILL_DELUDE_XP_BONUS);
			fleet.save();
		}
		
		ItemContainer itemContainer = fleet.getItemContainer();
		synchronized (itemContainer.getLock()) {
			itemContainer = DataAccess.getEditable(itemContainer);
			itemContainer.addResource(-cost, 3);
			itemContainer.save();
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(delude.getIdCurrentArea(), player.getId(),
				new Point(delude.getX(), delude.getY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getAreaUpdate(),
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getPlayerFleetUpdate(delude.getId())
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
