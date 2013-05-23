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

package fr.fg.server.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import fr.fg.client.data.AllyInfluenceData;
import fr.fg.client.data.GalaxyAreaData;
import fr.fg.client.data.GalaxyJumpData;
import fr.fg.client.data.GalaxyMapData;
import fr.fg.client.data.GalaxyMarkerData;
import fr.fg.client.data.GalaxySectorData;
import fr.fg.client.data.StructureModuleData;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.TerritoryManager;
import fr.fg.server.data.AllyInfluence;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class GetGalaxy extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		byte[] areasVisibility = player.getAreasVisibility();
		byte[] sectorsVisibility = player.getSectorsVisibility(areasVisibility);
		
		List<Sector> sectors = new ArrayList<Sector>(DataAccess.getAllSectors());
		
		JSONStringer json = new JSONStringer();
		json.object().
			key(GalaxyMapData.FIELD_SECTORS).		array();
		
		List<Fleet> fleets = player.getFleets();
		
		for (Sector sector : sectors) {
			byte sectorVisibility = sectorsVisibility[sector.getId()];
			
			if (sectorVisibility == Area.VISIBILITY_NONE) {
				List<Integer> neighboursSectors = sector.getNeighbours();
				
				for (Integer idNeighbour : neighboursSectors) {
					if (sectorsVisibility[idNeighbour] >=
							Area.VISIBILITY_VISITED) {
						sectorVisibility = Area.VISIBILITY_UNKNOWN;
						break;
					}
				}
			}
			
			if (sectorVisibility != Area.VISIBILITY_NONE) {
				json.object().
					key(GalaxySectorData.FIELD_ID).				value(sector.getId()).
					key(GalaxySectorData.FIELD_X).				value(sector.getX()).
					key(GalaxySectorData.FIELD_Y).				value(sector.getY()).
					key(GalaxySectorData.FIELD_LVLMIN).				value(sector.getLvlMin()).
					key(GalaxySectorData.FIELD_LVLMAX).				value(sector.getLvlMax()).
					key(GalaxySectorData.FIELD_TERRITORY_HASH).	value(TerritoryManager.getInstance().getTerritoryHashBySector(sector.getId())).
					key(GalaxySectorData.FIELD_VISIBILITY).		value(Area.VISIBILITY_LABELS[sectorVisibility]);

				
				if (sectorVisibility != Area.VISIBILITY_UNKNOWN) {
					json.key(GalaxySectorData.FIELD_NAME).				value(sector.getName()).
						key(GalaxySectorData.FIELD_NEBULA).				value(sector.getNebula()).
						key(GalaxySectorData.FIELD_STRATEGIC_VALUE).	value(sector.getStrategicValue());
					
					// Influences des alliances dans le quadrant
					json.key(GalaxySectorData.FIELD_ALLY_INFLUENCES).	array();
					List<AllyInfluence> allyInfluences =
						DataAccess.getAllyInfluencesBySector(sector.getId());
					
					synchronized (allyInfluences) {
						for (AllyInfluence allyInfluence : allyInfluences) {
							json.object().
								key(AllyInfluenceData.FIELD_ALLY_NAME).	value(DataAccess.getAllyById(allyInfluence.getIdAlly()).getName()).
								key(AllyInfluenceData.FIELD_INFLUENCE).	value(allyInfluence.getInfluenceCoef()).
								endObject();
						}
					}
					
					json.endArray();
					
					// Liste des secteurs
					json.key(GalaxySectorData.FIELD_AREAS).		array();
					
					List<Area> areas = new ArrayList<Area>(sector.getAreas());
					
					for (Area area : areas) {
						byte areaVisibility = areasVisibility[area.getId()];
						
						if (areaVisibility == Area.VISIBILITY_NONE) {
							// Recherche si le secteur est voisin d'un
							// secteur dans lequel le joueur se trouve
							List<Integer> neighbours = area.getNeighbours();
							
							for (Integer idNeighbour : neighbours) {
								if (areasVisibility[idNeighbour] >=
										Area.VISIBILITY_VISITED) {
									areaVisibility = Area.VISIBILITY_UNKNOWN;
									break;
								}
							}
						}
						
						
						if (areaVisibility != Area.VISIBILITY_NONE) {
							json.object().
								key(GalaxyAreaData.FIELD_ID).			value(area.getId()).
								key(GalaxyAreaData.FIELD_X).			value(area.getX()).
								key(GalaxyAreaData.FIELD_Y).			value(area.getY()).
								key(GalaxyAreaData.FIELD_TYPE).			value(area.getGeneralType()).
								key(GalaxyAreaData.FIELD_VISIBILITY).	value(Area.VISIBILITY_LABELS[areaVisibility]);
							
							double hyperspaceBonus = 1;
							
							if (areaVisibility != Area.VISIBILITY_UNKNOWN) {
								// Bonus des relais
								List<Structure> structures = area.getStructures();
								
								synchronized (structures) {
									for (Structure structure : structures) {
										if (structure.getType() == Structure.TYPE_HYPERSPACE_RELAY &&
												structure.isActivated() &&
												(structure.getIdOwner() == player.getId() ||
												 player.getIdAlly() != 0 &&
												 player.getIdAlly() == structure.getOwner().getIdAlly())) {
											hyperspaceBonus = Math.min(hyperspaceBonus,
												Math.pow(.92, structure.getModuleLevel(
												StructureModuleData.TYPE_WARP_FIELD) + 1));
										}
									}
								}
								
								json.key(GalaxyAreaData.FIELD_NAME).		value(area.getName()).
									key(GalaxyAreaData.FIELD_PRODUCT).		value(area.getProduct());
							}
							
							json.key(GalaxyAreaData.FIELD_HYPERSPACE_COEF).value(hyperspaceBonus).
								endObject();
						}
					}
					
					json.endArray().
						key(GalaxySectorData.FIELD_MARKERS).	array();
					
					for (Area area : areas) {
						// Marqueurs
						List<Marker> markers = area.getMarkers();
						
						synchronized (markers) {
							for (Marker marker : markers) {
								if (marker.isGalaxy() && marker.isVisibleFromPlayer(player)) {
									Player owner = marker.getOwner();
									json.object().
										key(GalaxyMarkerData.FIELD_OWNER).		value(owner.getLogin()).
										key(GalaxyMarkerData.FIELD_ALLY).		value(owner.getAllyName()).
										key(GalaxyMarkerData.FIELD_MESSAGE).	value(marker.getMessage()).
										key(GalaxyMarkerData.FIELD_TREATY).		value(owner.getTreatyWithPlayer(player)).
										key(GalaxyMarkerData.FIELD_AREA_X).		value(area.getX()).
										key(GalaxyMarkerData.FIELD_AREA_Y).		value(area.getY()).
										key(GalaxyMarkerData.FIELD_ID_AREA).	value(area.getId()).
										key(GalaxyMarkerData.FIELD_CONTRACT).	value(marker.getIdContract() != 0 ? ContractManager.getTitle(marker.getContract()) : "").
										endObject();
								}
							}
						}
						
						List<Fleet> areaFleets = area.getFleets();
						
						synchronized(areaFleets){
							for (Fleet fleet : areaFleets) {
								if (fleet.getOwner().isAi() && fleet.getContract()!=null &&
										fleet.getContract().getAttendees().size()==1 &&
										fleet.getContract().getAttendees().get(0).getIdPlayer()!=0 &&
										fleet.getContract().getAttendees().get(0).getIdPlayer()==player.getId()) {
									
									json.object().
										key(GalaxyMarkerData.FIELD_OWNER).		value(player.getLogin()).
										key(GalaxyMarkerData.FIELD_ALLY).		value(player.getAllyName()).
										key(GalaxyMarkerData.FIELD_MESSAGE).	value("PNJ de mission"). //à changer?
										key(GalaxyMarkerData.FIELD_TREATY).		value(player.getTreatyWithPlayer(player)).
										key(GalaxyMarkerData.FIELD_AREA_X).		value(area.getX()).
										key(GalaxyMarkerData.FIELD_AREA_Y).		value(area.getY()).
										key(GalaxyMarkerData.FIELD_ID_AREA).	value(area.getId()).
										key(GalaxyMarkerData.FIELD_CONTRACT).	value(fleet.getIdContract() != 0 ? ContractManager.getTitle(fleet.getContract()) : "").
										endObject();
								}
								
								if (fleet.getOwner().isAi() && fleet.getContract()!=null &&
										fleet.getContract().getAttendees().size()==1 &&
										fleet.getContract().getAttendees().get(0).getIdAlly()!=0 &&
										fleet.getContract().getAttendees().get(0).getIdAlly()==player.getIdAlly()) {
									
									json.object().
										key(GalaxyMarkerData.FIELD_OWNER).		value(player.getLogin()).
										key(GalaxyMarkerData.FIELD_ALLY).		value(player.getAllyName()).
										key(GalaxyMarkerData.FIELD_MESSAGE).	value("PNJ de mission d'alliance"). //à changer?
										key(GalaxyMarkerData.FIELD_TREATY).		value(player.getTreatyWithPlayer(player)).
										key(GalaxyMarkerData.FIELD_AREA_X).		value(area.getX()).
										key(GalaxyMarkerData.FIELD_AREA_Y).		value(area.getY()).
										key(GalaxyMarkerData.FIELD_ID_AREA).	value(area.getId()).
										key(GalaxyMarkerData.FIELD_CONTRACT).	value(fleet.getIdContract() != 0 ? ContractManager.getTitle(fleet.getContract()) : "").
										endObject();
								}
							}
						}
					}
					
					json.endArray().
						key(GalaxySectorData.FIELD_JUMPS).		array();
					
					synchronized (fleets) {
						for (Fleet fleet : fleets) {
							// Note : ne pas utiliser getArea, qui renverrai la
							// destination pour la 2e partie du saut
							Area startArea = DataAccess.getAreaById(fleet.getIdArea());
							
							if (startArea.getIdSector() == sector.getId() &&
									fleet.isInHyperspace()) {
								Area endArea = fleet.getHyperspaceArea();
								
								if (endArea.getIdSector() == sector.getId()) {
									json.object().
										key(GalaxyJumpData.FIELD_START_X).	value(startArea.getX()).
										key(GalaxyJumpData.FIELD_START_Y).	value(startArea.getY()).
										key(GalaxyJumpData.FIELD_END_X).	value(endArea.getX()).
										key(GalaxyJumpData.FIELD_END_Y).	value(endArea.getY()).
										endObject();
								}
							}
						}
					}
					
					json.endArray();
				} else {
					json.key(GalaxySectorData.FIELD_AREAS).
						array().endArray().
						key(GalaxySectorData.FIELD_MARKERS).
						array().endArray();
				}
				
				json.endObject();
			}
		}
		
		json.endArray().
			key(GalaxySectorData.FIELD_JUMPS).		array();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				if (fleet.isInHyperspace()) {
					// Note : ne pas utiliser getArea, qui renverrai la
					// destination pour la 2e partie du saut
					Area startArea = DataAccess.getAreaById(fleet.getIdArea());
					Area endArea = fleet.getHyperspaceArea();
					
					if (startArea.getIdSector() != endArea.getIdSector()) {
						Sector startSector = startArea.getSector();
						Sector endSector = endArea.getSector();
						
						json.object().
							key(GalaxyJumpData.FIELD_START_X).	value(startSector.getX()).
							key(GalaxyJumpData.FIELD_START_Y).	value(startSector.getY()).
							key(GalaxyJumpData.FIELD_END_X).	value(endSector.getX()).
							key(GalaxyJumpData.FIELD_END_Y).	value(endSector.getY()).
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
