/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import fr.fg.client.data.AlertData;
import fr.fg.client.data.EventData;
import fr.fg.client.data.EventsData;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.Structure;
import fr.fg.server.data.Building;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Technology;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class GetEvents extends Action {
	
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		long date = (Long) params.get("date");
		
		// Met à jour la date de dernière vision des messages
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.setEventsReadDate(Utilities.now());
			player.save();
		}
		
		// Tri les events par date
		List<Event> events = player.getEvents();
		Collections.sort(events, new Comparator<Event>() {
			public int compare(Event p1, Event p2) {
				if (p1.getDate() > p2.getDate())
					return -1;
				if (p1.getDate() == p2.getDate())
					return p1.getId() < p2.getId() ? -1 : 1;
				return 1;
			}
		});
		
		JSONStringer json = new JSONStringer();
		
		json.object().
			key(EventsData.FIELD_LAST_UPDATE).	value(Utilities.now()).
			key(EventsData.FIELD_EVENTS).		array();
		
		synchronized (events) {
			int i = 0;
			for (Event event : events) {
				if (event.getDate() > date) {
					json.object().
						key(EventData.FIELD_TYPE).		value(event.getType()).
						key(EventData.FIELD_ARG1).		value(event.getArg1()).
						key(EventData.FIELD_ARG2).		value(event.getArg2()).
						key(EventData.FIELD_ARG3).		value(event.getArg3()).
						key(EventData.FIELD_ARG4).		value(event.getArg4()).
						key(EventData.FIELD_DATE).		value(event.getDate()).
						key(EventData.FIELD_ID_AREA).	value(event.getIdArea()).
						key(EventData.FIELD_X).			value(event.getX()).
						key(EventData.FIELD_Y).			value(event.getY()).
						key(EventData.FIELD_AREA_NAME).	value(event.getIdArea() == 0 ?
							"" : DataAccess.getAreaById(event.getIdArea()).getName()).
						endObject();
				}
				i++;
			}			
		}
		
		json.endArray();
		
		// Alertes
		json.key(EventsData.FIELD_ALERTS).	array();
		
		// Pas de recherche en cours
		boolean noResearch = true;
		List<Research> researches = player.getResearches();
		
		synchronized (researches) {
			for (Research research : researches) {
				if (research.getQueuePosition() == 0) {
					noResearch = false;
					break;
				}
			}
		}
		
		if (noResearch && researches.size() < Technology.TECHNOLOGIES.length) {
			json.object().
				key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_NO_RESEARCH).
				key(AlertData.FIELD_PRIORITY).	value(AlertData.PRIORITY_HIGHEST).
				key(AlertData.FIELD_ID_AREA).	value(0).
				key(AlertData.FIELD_X).			value(-1).
				key(AlertData.FIELD_Y).			value(-1).
				key(AlertData.FIELD_AREA_NAME).	value("").
				endObject();
		}
		
		// Stocks élevés
		List<StarSystem> systems = player.getSystems();
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				double status = system.getResourcesCount() /
					system.getProduction(Building.STOREHOUSE);
				
				if (status > .75) {
					json.object().
						key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_SYSTEM_STOCK).
						key(AlertData.FIELD_PRIORITY).	value(status < .95 ? AlertData.PRIORITY_AVERAGE : AlertData.PRIORITY_HIGH).
						key(AlertData.FIELD_ARG1).		value(system.getName()).
						key(AlertData.FIELD_ARG2).		value(String.valueOf(status)).
						key(AlertData.FIELD_ID_AREA).	value(system.getIdArea()).
						key(AlertData.FIELD_X).			value(system.getX()).
						key(AlertData.FIELD_Y).			value(system.getY()).
						key(AlertData.FIELD_AREA_NAME).	value(system.getArea().getName()).
						endObject();
				}
			}
		}
		
		// Traités en attente
		List<Treaty> treaties = DataAccess.getPlayer2Treaties(player.getId());
		
		synchronized (treaties) {
			for (Treaty treaty : treaties) {
				if (treaty.getSource() == treaty.getIdPlayer1()) {
					json.object().
						key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_PENDING_TREATY).
						key(AlertData.FIELD_PRIORITY).	value(AlertData.PRIORITY_LOW).
						key(AlertData.FIELD_ARG1).		value(treaty.getPlayer1().getLogin()).
						key(AlertData.FIELD_ID_AREA).	value(0).
						key(AlertData.FIELD_X).			value(-1).
						key(AlertData.FIELD_Y).			value(-1).
						key(AlertData.FIELD_AREA_NAME).	value("").
						endObject();
				}
			}
		}
		
		// Points de civilisation non utilisés
		synchronized (player){
		if(player.getAdvancementPoints()>0){
			json.object().
			key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_AVAILABLE_CIVIL_POINT).
			key(AlertData.FIELD_PRIORITY).	value(AlertData.PRIORITY_LOW).
			key(AlertData.FIELD_ARG1).		value(player.getAdvancementPoints()).
			key(AlertData.FIELD_ID_AREA).	value(0).
			key(AlertData.FIELD_X).			value(-1).
			key(AlertData.FIELD_Y).			value(-1).
			key(AlertData.FIELD_AREA_NAME).	value("").
			endObject();
		}
		}
		
		// Structure désactivée
		List<Structure> structures = player.getStructures();
		
		synchronized (structures) {
			for (Structure struct : structures){
				
				if(struct.getIdArea()!=0){ // Structure dans flotte
				if(!struct.isActivated())
					json.object().
					key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_DESACTIVATE_STRUCTURE).
					key(AlertData.FIELD_PRIORITY).	value(AlertData.PRIORITY_AVERAGE).
					key(AlertData.FIELD_ARG1).		value(struct.getName()).
					key(AlertData.FIELD_ID_AREA).	value(struct.getIdArea()).
					key(AlertData.FIELD_X).			value(struct.getX()).
					key(AlertData.FIELD_Y).			value(struct.getY()).
					key(AlertData.FIELD_AREA_NAME).	value("").
					endObject();
				}
			}
			
		}
	
		
		// Points de compétences non affectés aux flottes & tactiques non définies
		List<Fleet> fleets = player.getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				
				int isFullCargo=1;
				
				if (fleet.getAvailableSkillPoints() > 0) {
					json.object().
						key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_AVAILABLE_SKILL_POINT).
						key(AlertData.FIELD_PRIORITY).	value(AlertData.PRIORITY_LOWEST).
						key(AlertData.FIELD_ARG1).		value(fleet.getName()).
						key(AlertData.FIELD_ARG2).		value(String.valueOf(fleet.getAvailableSkillPoints())).
						key(AlertData.FIELD_ID_AREA).	value(fleet.getIdCurrentArea()).
						key(AlertData.FIELD_X).			value(fleet.getX()).
						key(AlertData.FIELD_Y).			value(fleet.getY()).
						key(AlertData.FIELD_AREA_NAME).	value(fleet.getArea().getName()).
						endObject();
				}
				
				
				if (!fleet.isTacticsDefined()) {
					
					for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
						Slot slot = fleet.getSlot(i);
						if (slot.getShip() != null && slot.getShip(
								).getShipClass() != Ship.FREIGHTER){
							isFullCargo=0;
						}
					}
					
					if(isFullCargo==0){
						
						json.object().
							key(AlertData.FIELD_TYPE).		value(AlertData.ALERT_NO_TACTICS).
							key(AlertData.FIELD_PRIORITY).	value(AlertData.PRIORITY_AVERAGE).
							key(AlertData.FIELD_ARG1).		value(fleet.getName()).
							key(AlertData.FIELD_ID_AREA).	value(fleet.getIdCurrentArea()).
							key(AlertData.FIELD_X).			value(fleet.getX()).
							key(AlertData.FIELD_Y).			value(fleet.getY()).
							key(AlertData.FIELD_AREA_NAME).	value(fleet.getArea().getName()).
							endObject();
					}
				}
			}
		}
		
		json.endArray().
			endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
