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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.FleetLink;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetOffensiveLink extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Flotte qui crée le lien
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("fleet"), player.getId());
		
		if (fleet.getSkillLevel(Skill.SKILL_OFFENSIVE_LINK) < 0)
			throw new IllegalOperationException(
					"La flotte n'a pas la compétence lien offensif.");
		
		// Flotte ciblée pour créer le lien
		Fleet target = FleetTools.getFleetByIdWithChecks(
				(Integer) params.get("target"), 0, FleetTools.ALLOW_DELUDE);
		
		if (target.getOwner().isAi())
			throw new IllegalOperationException(
					"Impossible de se lier avec une flotte PNJ");
		
		if (fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 ||
				target.getSkillLevel(Skill.SKILL_PIRATE) >= 0)
			throw new IllegalOperationException("Une flotte pirate ne peut " +
					"pas être liée avec une autre flotte.");
		
		if (player.getTreatyWithPlayer(target.getOwner()).equals(Treaty.NEUTRAL) ||
				player.getTreatyWithPlayer(target.getOwner()).equals(Treaty.ENEMY))
			throw new IllegalOperationException("La flotte ne peut " +
					"pas se lier avec une flotte neutre ou ennemie.");
		
		// Vérifie que la cible est à portée
		int distance = fleet.getDistance(target);
		
		if (distance == -1 || distance > Skill.SKILL_OFFENSIVE_LINK_RANGE)
			throw new IllegalOperationException("La flotte est hors de portée.");
		
		// Vérifie que la flotte ciblée n'est pas déjà liée
		if (target.hasOffensiveLink()) {
			List<FleetLink> links = DataAccess.getFleetLinks(target.getId());
			
			for (FleetLink link : links)
				if (link.isOffensive()) {
					// Casse le lien
					link.delete();
					break;
				}
		}
		
		// Vérifie que la flotte source n'est pas déjà liée
		Fleet linkedFleet = fleet.getOffensiveLinkedFleet();
		
		if (linkedFleet != null) {
			List<FleetLink> links = DataAccess.getFleetLinks(
					fleet.getId(), linkedFleet.getId());
			
			for (FleetLink link : links)
				if (link.isOffensive()) {
					// Casse le lien
					link.delete();
					break;
				}
		}
		
		// Crée le lien offensif
		FleetLink link = new FleetLink(fleet.getId(),
				target.getId(), FleetLink.LINK_OFFENSIVE);
		link.save();
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		ArrayList<Update> updates = new ArrayList<Update>();
		updates.add(Update.getAreaUpdate());
		updates.add(Update.getPlayerFleetUpdate(fleet.getId()));
		if (target.getIdOwner() == player.getId())
			updates.add(Update.getPlayerFleetUpdate(target.getId()));
		else
			UpdateTools.queuePlayerFleetUpdate(target.getIdOwner(), target.getId(), false);
		
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
				player.getId(),
				new Point(fleet.getX(), fleet.getY()),
				new Point(target.getX(), target.getY()));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
