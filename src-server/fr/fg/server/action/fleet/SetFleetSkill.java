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

package fr.fg.server.action.fleet;

import java.awt.Point;
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
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetFleetSkill extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idFleet = (Integer) params.get("fleet");
		int skill = (Integer) params.get("skill");
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
				idFleet, player.getId(), FleetTools.ALLOW_HYPERSPACE);
		
		if (!player.isDiplomacyActivated() && skill == Skill.SKILL_PIRATE)
			throw new IllegalOperationException("La compétence pirate ne " +
				"peut pas être sélectionnée lorsque la diplomatie désactivée.");
		
		synchronized (fleet.getLock()) {
			// Vérifie que la flotte a un point de compétence disponible
			if (fleet.getAvailableSkillPoints() <= 0)
				throw new IllegalOperationException("Vous n'avez pas de points de " +
						"compétences disponible.");
			
			if (Skill.isUltimateSkill(skill)) {
				Skill ultimateSkill = fleet.getUltimateSkill();
				
				if (ultimateSkill.getType() == 0) {
					// Vérifie que la flotte est au moins niveau 6
					if (fleet.getLevel() < 6)
						throw new IllegalOperationException("La flotte doit être au " +
								"moins niveau 6 pour pouvoir choisir la " +
								"compétence ultime.");
				} else {
					// Vérifie qu'une compétence ultime différente n'a pas déjà
					// été choisie
					if (ultimateSkill.getType() != skill)
						throw new IllegalOperationException("Vous ne pouvez avoir " +
							"plus d'une compétence ultime par flotte.");
					
					// Vérifie que la compétence n'est pas au niveau 3
					// (niveau maximal)
					if (ultimateSkill.getLevel() == 2)
						throw new IllegalOperationException("Le niveau maximal a " +
								"été atteint dans la compétence ultime.");
					
					// Vérifie que la flotte est au moins niveau 10 si la
					// compétence est au niveau 1
					if (ultimateSkill.getLevel() == 0 && fleet.getLevel() < 10)
						throw new IllegalOperationException("La flotte doit être " +
								"de niveau 10 avant de pouvoir choisir " +
								"la compétence ultime au niveau 2.");
					
					// Vérifie que la flotte est au moins niveau 14 si la
					// compétence est au niveau 2
					if (ultimateSkill.getLevel() == 1 && fleet.getLevel() < 14)
						throw new IllegalOperationException("La flotte doit être " +
								"de niveau 14 avant de pouvoir choisir " +
								"la compétence ultime au niveau 3.");
				}
				
				// Enregistre la compétence ultime
				fleet = DataAccess.getEditable(fleet);
				fleet.setUltimateSkill(new Skill(skill,
						ultimateSkill.getType() == 0 ?
								0 : ultimateSkill.getLevel() + 1));
				fleet.save();
			} else if (Skill.isBasicSkill(skill)) {
				boolean found = false;
				
				// Vérifie que le joueur ne choisit pas des compétences
				// incompatibles entre elles
				switch (skill) {
				case Skill.SKILL_PIRATE:
					if (fleet.getSkillLevel(Skill.SKILL_OFFENSIVE_LINK) >= 0 ||
							fleet.getSkillLevel(Skill.SKILL_DEFENSIVE_LINK) >= 0) {
						throw new IllegalOperationException("La compétence " +
							"pirate n'est pas compatible avec les liens " +
							"offensifs / défensifs.");
					}
					break;
				case Skill.SKILL_OFFENSIVE_LINK:
				case Skill.SKILL_DEFENSIVE_LINK:
					if (fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0) {
						throw new IllegalOperationException("La compétence " +
								"pirate n'est pas compatible avec les liens " +
								"offensifs / défensifs.");
					}
					break;
				}
				
				// Cherche s'il s'agit d'une amélioration de compétence
				for (int i = 0; i < 3; i++) {
					Skill basicSkill = fleet.getBasicSkill(i);
					
					if (basicSkill.getType() == skill) {
						// Vérifie que la compétence n'est pas au niveau 4
						// (niveau maximal)
						if (basicSkill.getLevel() == 3)
							throw new IllegalOperationException("Le niveau maximal a " +
									"été atteint dans la compétence.");
						
						// Vérifie que la flotte est au moins niveau 3 si la
						// compétence est au niveau 1
						if (basicSkill.getLevel() == 0 && fleet.getLevel() < 3)
							throw new IllegalOperationException("La flotte doit être " +
									"de niveau 3 avant de pouvoir choisir " +
									"une compétence au niveau 2.");
						
						// Vérifie que la flotte est au moins niveau 5 si la
						// compétence est au niveau 2
						if (basicSkill.getLevel() == 1 && fleet.getLevel() < 5)
							throw new IllegalOperationException("La flotte doit être " +
									"de niveau 5 avant de pouvoir choisir " +
									"une compétence au niveau 3.");

						// Vérifie que la flotte est au moins niveau 7 si la
						// compétence est au niveau 3
						if (basicSkill.getLevel() == 2 && fleet.getLevel() < 7)
							throw new IllegalOperationException("La flotte doit être " +
									"de niveau 7 avant de pouvoir choisir " +
									"une compétence au niveau 4.");
						
						basicSkill.setLevel(basicSkill.getLevel() + 1);
						fleet = DataAccess.getEditable(fleet);
						fleet.setBasicSkill(basicSkill, i);
						fleet.save();
						
						found = true;
						break;
					}
				}
				
				if (!found) {
					// Cherche un emplacement libre pour la compétence
					for (int i = 0; i < 3; i++) {
						Skill basicSkill = fleet.getBasicSkill(i);
						
						if (basicSkill.getType() == 0) {
							fleet = DataAccess.getEditable(fleet);
							fleet.setBasicSkill(new Skill(skill, 0), i);
							
							// Met à jour le tag
							if (fleet.getTag() < 4) {
								switch (skill) {
								case Skill.SKILL_PIRATE:
									fleet.setTag(4);
									break;
								case Skill.SKILL_MINING:
									fleet.setTag(5);
									break;
								case Skill.SKILL_ENGINEER:
									fleet.setTag(6);
									break;
								case Skill.SKILL_PYROTECHNIST:
									fleet.setTag(7);
									break;
								}
							}
							
							fleet.save();
							
							found = true;
							break;
						}
					}
					
					if (!found)
						throw new IllegalOperationException("Une flotte ne peut " +
								"avoir que 3 compétences basiques.");
				}
			} else {
				throw new IllegalOperationException("Compétence invalide.");
			}
		}
		
		// Casse les liens quand on sélectionne la compétence pirate
		if (skill == Skill.SKILL_PIRATE) {
			List<FleetLink> links = fleet.getLinks();
			
			for (FleetLink link : links) {
				if (link.isOffensive() || link.isDefensive())
					link.delete();
			}
		}
		
		if (skill == Skill.SKILL_PIRATE || skill == Skill.SKILL_SPY ||
				skill == Skill.SKILL_TRACKER) {
			// Met à jour le secteur
			UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
					new Point(fleet.getCurrentX(), fleet.getCurrentY()));
		}
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
