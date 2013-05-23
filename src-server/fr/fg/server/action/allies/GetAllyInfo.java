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

package fr.fg.server.action.allies;

import java.util.List;
import java.util.Map;



import fr.fg.server.core.Ladder;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class GetAllyInfo extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		Ally searched = DataAccess.getAllyByName((String) params.get("name"));

		if (searched == null)
			throw new IllegalOperationException("Cette alliance n'existe pas.");

		List<Player> members = searched.getMembers();

		JSONStringer json = new JSONStringer();

		json.object().
			key("allyName").	value(searched.getName()).
			key("points").		value(searched.getPoints()).
			key("rank").		value(Ladder.getInstance().getAllyRank(searched.getId())).
			key("organization").value(searched.getOrganization()).
			key("description").	value(searched.getDescription()).
			key("creation").	value(Utilities.getDate(searched.getBirthdate())).
			key("founder").		value(searched.getCreatorName()).
			key("treaty").		value(player.getIdAlly() == 0 ? Treaty.NEUTRAL :
									searched.getTreatyWithAlly(player.getIdAlly())).
			key("nbMembers").	value(members.size()).
			key("belongings").	object().
				key("fleets").	array();
		
		byte[] areasVisibility = player.getAreasVisibility();
		
		synchronized (members) {
			// Liste les flottes de l'alliance visibles du joueur
			for (Player member : members) {
				List<Fleet> fleets = member.getFleets();
				
				synchronized (fleets) {
					for (Fleet fleet : fleets) {
						if (fleet.getSkillLevel(Skill.SKILL_PIRATE) != -1)
							continue;
						
						Area area = fleet.getArea();
						boolean getInfo = true;
						
						if (areasVisibility[fleet.getIdCurrentArea()] > Area.VISIBILITY_VISITED) {
							List<StarSystem> systems = area.getColonizedSystems();
							
							synchronized (systems) {
								for (StarSystem system : systems) {
									if (system.isHidingFleet(fleet, player)) {
										getInfo = false;
										break;
									}
								}
							}
						} else {
							getInfo = false;
						}
						
						if (getInfo) {
							json.object().
								key("name").	value(fleet.getName()).
								key("owner").	value(member.getLogin()).
								key("x").		value(fleet.getCurrentX()).
								key("y").		value(fleet.getCurrentY()).
								key("areaName").value(area.getName()).
								key("areaId").	value(area.getId()).
								endObject();
						}
					}
				}
			}
			
			json.endArray().key("systems").array();
			
			// Liste les systèmes de l'alliance visibles du joueur
			for (Player member : members) {
				List<StarSystem> systems = member.getSystems();
				
				synchronized (systems) {
					for (StarSystem system : systems) {
						if (areasVisibility[system.getIdArea()] > Area.VISIBILITY_VISITED) {
							Area area = system.getArea();
							
							json.object().
								key("name").	value(system.getName()).
								key("owner").	value(member.getLogin()).
								key("x").		value(system.getX()).
								key("y").		value(system.getY()).
								key("areaName").value(area.getName()).
								key("areaId").	value(area.getId())
								.endObject();
						}
					}
				}
			}
		}
		
		json.endArray().endObject().endObject();

		return json.toString();

	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
