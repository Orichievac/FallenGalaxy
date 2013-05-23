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

import java.util.List;
import java.util.Map;



import fr.fg.server.core.Ladder;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class GetPlayerInfo extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		String name = (String)params.get("name");
		
		if(player.getLogin().equals(name))
			throw new IllegalOperationException("Vous ne pouvez obtenir d'informations sur vous-même.");
		
		Player searched = DataAccess.getPlayerByLogin(name);
		
		if(searched==null)
			throw new IllegalOperationException("Ce joueur n'existe pas.");
		
		JSONStringer json = new JSONStringer();
		
		json.object().
			key("name").		value(searched.getLogin()).
			key("xp").			value(searched.getXp()).
			key("points").		value(searched.getPoints()).
			key("rank").		value(Ladder.getInstance().getPlayerRank(searched.getId())).
			key("ally_name").	value(searched.getAlly()!=null?searched.getAlly().getName():"").
			key("treaty").		value(player.getTreatyWithPlayer(searched)).
			key("belongings").	object().
				key("fleets").	array();
		
		byte[] areasVisibility = player.getAreasVisibility();
		
		List<Fleet> fleets = searched.getFleets();
		
		synchronized (fleets) {
			for(Fleet fleet:fleets){
				if (fleet.getSkillLevel(Skill.SKILL_PIRATE) != -1)
					continue;
				
				boolean getInfo = true;
				Area area = fleet.getArea();
				
				if( areasVisibility[fleet.getIdCurrentArea()] > Area.VISIBILITY_VISITED) {
					List<StarSystem> systems = area.getColonizedSystems();
					
					synchronized (systems) {
						for(StarSystem system:systems){
							if(system.isHidingFleet(fleet, player)) {
								getInfo = false;
								break;
							}									
						}
					}
				} else  {
					getInfo = false;
				}
				
				if(getInfo){
					json.object().
						key("name").		value(fleet.getName()).
						key("x").			value(fleet.getCurrentX()).
						key("y").			value(fleet.getCurrentY()).
						key("areaName").	value(area.getName()).
						key("areaId").		value(area.getId()).
					endObject();
				}
			}			
		}
		
		json.endArray().
			key("systems").	array();
		
		List<StarSystem> systems = searched.getSystems();
		
		synchronized (systems) {
			for(StarSystem system:systems){
				if (areasVisibility[system.getIdArea()] > Area.VISIBILITY_VISITED) {
					Area area = system.getArea();
					json.object().
						key("name").		value(system.getName()).
						key("x").			value(system.getX()).
						key("y").			value(system.getY()).
						key("areaName").	value(area.getName()).
						key("areaId").		value(area.getId()).
					endObject();
				}
			}			
		}
		
		json.endArray().endObject().endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
