/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier, Nicolas Bosc

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

package fr.fg.server.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fr.fg.client.data.AreaData;
import fr.fg.client.data.AsteroidsData;
import fr.fg.client.data.BankData;
import fr.fg.client.data.BlackHoleData;
import fr.fg.client.data.ContractMarkerData;
import fr.fg.client.data.DoodadData;
import fr.fg.client.data.GateData;
import fr.fg.client.data.GravityWellData;
import fr.fg.client.data.HyperspaceSignatureData;
import fr.fg.client.data.LotteryData;
import fr.fg.client.data.MarkerData;
import fr.fg.client.data.PlanetData;
import fr.fg.client.data.PlayerSectorData;
import fr.fg.client.data.ShipInfoData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.data.TradeCenterData;
import fr.fg.client.data.WardData;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.StructureTools;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.data.Advancement;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Planet;
import fr.fg.server.data.Player;
import fr.fg.server.data.Product;
import fr.fg.server.data.Sector;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Slot;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Structure;
import fr.fg.server.data.Treaty;
import fr.fg.server.data.Ward;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class AreaTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getArea(JSONStringer json, Area area,
			Player player) throws Exception {
		if (json == null)
			json = new JSONStringer();
		
		// Détermine le niveau de visibilité de la zone
		byte[] areasVisibility = player.getAreasVisibility();
		if (areasVisibility[area.getId()] == Area.VISIBILITY_NONE)
			throw new IllegalOperationException(
					"Vous ne pouvez avoir d'informations sur ce secteur.");
		
		boolean areaContentVisible =
			areasVisibility[area.getId()] >= Area.VISIBILITY_ALLY;
		
		HashMap<Long, String> treatiesCache = new HashMap<Long, String>();
		
		// Caractéristiques générales de la zone
		Sector sector = area.getSector();
		
		json.object().
			key(AreaData.FIELD_ID).		value(area.getId()).
			key(AreaData.FIELD_NAME).	value(area.getName()).
			key(AreaData.FIELD_SECTOR).
			object().
				key(PlayerSectorData.FIELD_ID).			value(sector.getId()).
				key(PlayerSectorData.FIELD_NAME).		value(sector.getName()).
				key(PlayerSectorData.FIELD_X).			value(sector.getX()).
				key(PlayerSectorData.FIELD_Y).			value(sector.getY()).
				key(PlayerSectorData.FIELD_TYPE).		value(sector.getType()).
			endObject().
			key(AreaData.FIELD_WIDTH).			value(area.getWidth()).
			key(AreaData.FIELD_HEIGHT).			value(area.getHeight()).
			key(AreaData.FIELD_X).				value(area.getX()).
			key(AreaData.FIELD_Y).				value(area.getY()).
			key(AreaData.FIELD_NEBULA).			value(area.getSector().getNebula()).
			key(AreaData.FIELD_ENVIRONMENT).	value(area.getEnvironment());
		
		// Liste des objets dans le secteur
		List<StellarObject> objects =
			new ArrayList<StellarObject>(area.getObjects());
		
		// Portes hyperspatiales
		json.key(AreaData.FIELD_GATES).array();
		for (StellarObject object : objects) {
			if (object.getType().equals(StellarObject.TYPE_GATE)) {
				json.object().
					key(GateData.FIELD_ID).		value(object.getId()).
					key(GateData.FIELD_X).		value(object.getX()).
					key(GateData.FIELD_Y).		value(object.getY()).
					endObject();
			}
		}
		json.endArray();
		
		// Puits gravitationnels
		json.key(AreaData.FIELD_GRAVITY_WELLS).array();
		for (StellarObject object : objects) {
			if (object.getType().equals(StellarObject.TYPE_GRAVITY_WELL)) {
				json.object().
					key(GravityWellData.FIELD_ID).		value(object.getId()).
					key(GravityWellData.FIELD_X).		value(object.getX()).
					key(GravityWellData.FIELD_Y).		value(object.getY()).
					key(GravityWellData.FIELD_ENERGY).	value(object.getVariant()).
					endObject();
			}
		}
		json.endArray();
		
		// Centres de commerce
		json.key(AreaData.FIELD_TRADECENTERS).array();
		for (StellarObject object : objects) {
			if (object.getType().equals(StellarObject.TYPE_TRADECENTER)) {
				json.object().
					key(TradeCenterData.FIELD_ID).	value(object.getId()).
					key(TradeCenterData.FIELD_X).	value(object.getX()).
					key(TradeCenterData.FIELD_Y).	value(object.getY()).
					endObject();
			}
		}
		json.endArray();
		
		// Banques
		json.key(AreaData.FIELD_BANKS).array();
		for (StellarObject object : objects) {
			if (object.getType().equals(StellarObject.TYPE_BANK)) {
				json.object().
					key(BankData.FIELD_ID).		value(object.getId()).
					key(BankData.FIELD_X).		value(object.getX()).
					key(BankData.FIELD_Y).		value(object.getY()).
					endObject();
			}
		}
		json.endArray();
		
		// Loterie
		json.key(AreaData.FIELD_LOTTERYS).array();
		for (StellarObject object : objects) {
			if (object.getType().equals(StellarObject.TYPE_LOTTERY)) {
				json.object().
					key(LotteryData.FIELD_ID).		value(object.getId()).
					key(LotteryData.FIELD_X).		value(object.getX()).
					key(LotteryData.FIELD_Y).		value(object.getY()).
					endObject();
					}
				}
				json.endArray();
		
		// Balises
		List<Marker> markers = DataAccess.getMarkersByArea(area.getId());
		json.key(AreaData.FIELD_MARKERS).array();
		synchronized (markers) {
			for (Marker marker : markers) {
				if (marker.isVisibleFromPlayer(player)) {
					Player owner = marker.getOwner();
					String treaty = owner.getTreatyWithPlayer(treatiesCache, player);
					json.object().
						key(MarkerData.FIELD_ID).		value(marker.getId()).
						key(MarkerData.FIELD_X).		value(marker.getX()).
						key(MarkerData.FIELD_Y).		value(marker.getY()).
						key(MarkerData.FIELD_MESSAGE).	value(marker.getMessage()).
						key(MarkerData.FIELD_OWNER).	value(owner.getLogin()).
						key(MarkerData.FIELD_ALLY).		value(owner.getAllyName()).
						key(MarkerData.FIELD_VISIBILITY).	value(marker.getVisibility()).
						key(MarkerData.FIELD_TREATY).	value(treaty).
						key(MarkerData.FIELD_CONTRACT).	value(marker.getIdContract() != 0 ?
							ContractManager.getTitle(marker.getContract()) : "").
						endObject();
				}
			}
		}
		json.endArray();
		
		// Systèmes appartenant au joueur ou ses alliés
		List<Point> allySystems = player.getAllySystems(area);
		
		// Flottes appartenant au joueur ou ses alliés
		List<Point> allyFleets = player.getAllyFleets(area);
		
		// Balises d'observation appartenant au joueur ou ses alliés
		List<Point> allyObserverWards = player.getAllyObserverWards(area);
		
		// Balises de détection appartenant au joueur ou ses alliés
		List<Point> allySentryWards = player.getAllySentryWards(area);
		
		// Stations spatiales au joueur ou ses alliés
		List<Point> allySpaceStations = player.getAllySpaceStations(area);
		
		// Structure appartenant au joueur ou ses alliés
		List<Structure> allyStructures = player.getAllyStructures(area);
		
		if (areaContentVisible) {
			// Liste des flottes contenues dans la zone
			json.key(AreaData.FIELD_FLEETS);
			FleetTools.getAreaFleets(json, area, player, treatiesCache,
					allySystems, allyFleets, allyObserverWards, allySpaceStations,
					allyStructures, allySentryWards);
			
			// Liste des structures construites dans la zone
			json.key(AreaData.FIELD_STRUCTURES);
			StructureTools.getAreaStructures(json, area, player, treatiesCache,
					allySystems, allyFleets, allyObserverWards, allySpaceStations,
					allyStructures, allySentryWards);
			
			// Trous noirs
			json.key(AreaData.FIELD_BLACKHOLES).array();
			for (StellarObject object : objects) {
				if (object.getType().equals(StellarObject.TYPE_BLACKHOLE)) {
					json.object().
						key(BlackHoleData.FIELD_ID).	value(object.getId()).
						key(BlackHoleData.FIELD_X).		value(object.getX()).
						key(BlackHoleData.FIELD_Y).		value(object.getY()).
						endObject();
				}
			}
			json.endArray();
			
			// Astéroides
			json.key(AreaData.FIELD_ASTEROIDS).array();
			for (StellarObject object : objects) {
				if (object.getType().startsWith(StellarObject.TYPE_ASTEROID)) {
					if (player.isLocationVisible(area, object.getX(),
							object.getY(), allySystems, allyFleets,
							allyObserverWards, allySpaceStations, allyStructures)) {
						json.object().
							key(AsteroidsData.FIELD_ID).	value(object.getId()).
							key(AsteroidsData.FIELD_X).		value(object.getX()).
							key(AsteroidsData.FIELD_Y).		value(object.getY()).
							key(AsteroidsData.FIELD_TYPE).	value(object.getType()).
							endObject();
					}
				}
			}
			json.endArray();
			
			// Liste des doodads
			json.key(AreaData.FIELD_DOODADS).array();
			for (StellarObject object : objects) {
				if (object.getType().equals(StellarObject.TYPE_DOODAD)) {
					if (player.isLocationVisible(area, object.getX(),
							object.getY(), allySystems, allyFleets,
							allyObserverWards, allySpaceStations, allyStructures)) {
						json.object().
							key(DoodadData.FIELD_ID).		value(object.getId()).
							key(DoodadData.FIELD_X).		value(object.getX()).
							key(DoodadData.FIELD_Y).		value(object.getY()).
							key(DoodadData.FIELD_TYPE).		value(object.getVariant()).
							endObject();
					}
				}
			}
			json.endArray();
			
			// Domination de secteur
			json.key(AreaData.FIELD_DOMINATION).value(area.getDominatingAllyName());
			
			// Récupère les flottes amies qui ont la compétence tracker
			List<Fleet> trackerFleets = new ArrayList<Fleet>(area.getFleets());
			Iterator<Fleet> i = trackerFleets.iterator();
			
			while (i.hasNext()) {
				Fleet fleet = i.next();
				
				if (fleet.getSkillLevel(Skill.SKILL_TRACKER) < 1 ||
						(fleet.getIdOwner() != player.getId() &&
						(player.getIdAlly() == 0 ||
						fleet.getOwner().getIdAlly() != player.getIdAlly())) ||
						fleet.isDelude())
					i.remove();
			}
			
			// Signatures hyperspatiales
			List<Fleet> fleets = DataAccess.getHyperspaceFleets(area.getId());
			json.key(AreaData.FIELD_HYPERSPACE_SIGNATURES).array();
			
			synchronized (fleets) {
				for (Fleet fleet : fleets) {
					if (!fleet.isStartingJump())
						continue;
					
					if (player.isLocationVisible(area, fleet.getHyperspaceX(),
							fleet.getHyperspaceY(), allySystems, allyFleets,
							allyObserverWards, allySpaceStations, allyStructures)) {
						// Détermine si la flotte est tracée
						int trackerLevel = -1;
						for (Fleet trackerFleet : trackerFleets) {
							int dx = trackerFleet.getCurrentX() - fleet.getHyperspaceX();
							int dy = trackerFleet.getCurrentY() - fleet.getHyperspaceY();
							
							if (dx * dx + dy * dy < Skill.TRACKER_DETECTION_RANGE *
									Skill.TRACKER_DETECTION_RANGE) {
								trackerLevel = Math.max(trackerLevel,
									trackerFleet.getSkillLevel(Skill.SKILL_TRACKER));
							}
						}
						
						json.object().
							key(HyperspaceSignatureData.FIELD_ID).
								value(fleet.getId() * 541 + fleet.getIdOwner()).
							key(HyperspaceSignatureData.FIELD_X).
								value(fleet.getHyperspaceX()).
							key(HyperspaceSignatureData.FIELD_Y).
								value(fleet.getHyperspaceY());
						
						if (trackerLevel > -1) {
							Player owner = fleet.getOwner();
							String treaty = owner.getTreatyWithPlayer(player);
							
							if (fleet.getSkillLevel(Skill.SKILL_PIRATE) >= 0 &&
									!treaty.equals(Treaty.PLAYER) &&
									!treaty.equals(Treaty.ALLY) &&
									!treaty.equals(Treaty.ALLIED)) {
								json.key(HyperspaceSignatureData.FIELD_OWNER).		value("???").
									key(HyperspaceSignatureData.FIELD_ALLY_TAG).	value("").
									key(HyperspaceSignatureData.FIELD_TREATY).		value(Treaty.ENEMY);
							} else {
								json.key(HyperspaceSignatureData.FIELD_OWNER).		value(owner.getLogin()).
									key(HyperspaceSignatureData.FIELD_ALLY_TAG).	value(owner.getAllyTag()).
									key(HyperspaceSignatureData.FIELD_TREATY).		value(treaty);
							}
							
							// Estimation du contenu de la flotte par classes
							if (trackerLevel == 2) {
								json.key(HyperspaceSignatureData.FIELD_CLASSES).	array();
								int[] classes = fleet.getClassesQuantityQualifiers();
								for (int j = 0; j < classes.length; j++)
									json.value(classes[j]);
								json.endArray();
							} else {
								json.key(HyperspaceSignatureData.FIELD_CLASSES).	value(false);
							}
							
							// Estimation du contenu de la flotte par vaisseaux
							if (trackerLevel == 3) {
								json.key(HyperspaceSignatureData.FIELD_SHIPS).	array();
								for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
									Slot slot = fleet.getSlot(j);
									
									json.object().
										key(ShipInfoData.FIELD_ID).			value(slot.getId()).
										key(ShipInfoData.FIELD_CLASSES).	value(Fleet.getShipQuantityQualifier((long) slot.getCount())).
										endObject();
									
								}
								json.endArray();
							} else {
								json.key(HyperspaceSignatureData.FIELD_SHIPS).	value(false);
							}
						} else {
							json.key(HyperspaceSignatureData.FIELD_OWNER).	value("").
								key(HyperspaceSignatureData.FIELD_CLASSES).	value(false).
								key(HyperspaceSignatureData.FIELD_SHIPS).	value(false).
								key(HyperspaceSignatureData.FIELD_TREATY).	value("").
								key(HyperspaceSignatureData.FIELD_ALLY_TAG).value("");
						}
						
						json.endObject();
					}
				}
			}
			
			json.endArray();
			
			// Balises
			List<Ward> wards = area.getWards();
			json.key(AreaData.FIELD_WARDS).array();
			
			synchronized (wards) {
				for (Ward ward : wards) {
					if (ward.getIdOwner() == player.getId() ||
							player.isLocationVisible(area, ward.getX(),
								ward.getY(), allySystems, allyFleets,
								allyObserverWards, allySpaceStations,
								allyStructures)) {
						boolean visible = true;
						
						// Teste si la balise est détectée si elle invisible
						if (ward.getType().contains("invisible")) {
							String treaty = ward.getOwner().getTreatyWithPlayer(player);
							
							if (treaty.equals(Treaty.ENEMY) ||
									treaty.equals(Treaty.NEUTRAL)) {
								visible = player.isLocationRevealed(
									ward.getX(), ward.getY(), allySentryWards);
							}
						}
						
						if (visible) {
							Player owner = ward.getOwner();
							String treaty = owner.getTreatyWithPlayer(player);
							
							json.object().
								key(WardData.FIELD_ID).			value(ward.getId()).
								key(WardData.FIELD_X).			value(ward.getX()).
								key(WardData.FIELD_Y).			value(ward.getY()).
								key(WardData.FIELD_TYPE).		value(ward.getType()).
								key(WardData.FIELD_OWNER).		value(owner.getLogin()).
								key(WardData.FIELD_ALLY_TAG).	value(owner.getAllyTag()).
								key(WardData.FIELD_TREATY).		value(treaty);
							
							if (treaty.equals(Treaty.PLAYER) ||
									treaty.equals(Treaty.ALLY) ||
									treaty.equals(Treaty.ALLIED) ||
									Treaty.isPact(treaty)) {
								json.key(WardData.FIELD_LIFESPAN).	value(
										ward.getDate() - Utilities.now() +
										(ward.getType().startsWith(Ward.TYPE_OBSERVER) ||
										ward.getType().startsWith(Ward.TYPE_SENTRY) ?
										ward.getPower() * 86400 : GameConstants.CHARGES_LENGTH)).
									key(WardData.FIELD_POWER).		value(ward.getPower());
							}
							
							if (ward.getType().startsWith(Ward.TYPE_OBSERVER) &&
									(treaty.equals(Treaty.PLAYER) || treaty.equals(Treaty.ALLY)))
								{
								
								
								


								
								
									json.key(WardData.FIELD_LINE_OF_SIGHT).	value(GameConstants.LOS_WARD +
											2 * Advancement.getAdvancementLevel(ward.getIdOwner(),
													Advancement.TYPE_LINE_OF_SIGHT)+
													Product.getProductEffect(Product.PRODUCT_ANTILIUM, player.getProductsCount(Product.PRODUCT_ANTILIUM)));
								}
							else
								json.key(WardData.FIELD_LINE_OF_SIGHT).	value(0);
							
							json.endObject();
						}
					}
				}
			}
			
			json.endArray();
			
			// Stations spatiales
			json.key(AreaData.FIELD_SPACE_STATION).
				array();
			
			List<SpaceStation> spaceStations = area.getSpaceStations();
			
			synchronized (spaceStations) {
				for (SpaceStation spaceStation : spaceStations) {
					Ally ally = spaceStation.getAlly();
					
					String treaty = player.getIdAlly() == 0 ? Treaty.NEUTRAL :
						ally.getTreatyWithAlly(player.getIdAlly());
					
					json.object().
						key(SpaceStationData.FIELD_ID).			value(spaceStation.getId()).
						key(SpaceStationData.FIELD_NAME).		value(spaceStation.getName()).
						key(SpaceStationData.FIELD_X).			value(spaceStation.getX()).
						key(SpaceStationData.FIELD_Y).			value(spaceStation.getY()).
						key(SpaceStationData.FIELD_ALLY_ID).	value(ally.getId()).
						key(SpaceStationData.FIELD_ALLY_NAME).	value(ally.getName()).
						key(SpaceStationData.FIELD_ALLY_TAG).	value(ally.getTag()).
						key(SpaceStationData.FIELD_LEVEL).		value(spaceStation.getLevel()).
						key(SpaceStationData.FIELD_TREATY).		value(treaty).
						key(SpaceStationData.FIELD_HULL).		value(spaceStation.getHull());
					
					if (treaty.equals(Treaty.ALLY)) {
						json.key(SpaceStationData.FIELD_RESOURCES).
							array();
						for (int j = 0; j < GameConstants.RESOURCES_COUNT; j++)
							json.value(spaceStation.getResource(j));
						json.endArray().
							key(SpaceStationData.FIELD_CREDITS).			value(spaceStation.getCredits()).
							key(SpaceStationData.FIELD_PRODUCTION_MODIFIER).value(spaceStation.getProductionModifier()).
							key(SpaceStationData.FIELD_LINE_OF_SIGHT).		value(GameConstants.LOS_SPACE_STATION);
					} else {
						json.key(SpaceStationData.FIELD_LINE_OF_SIGHT).		value(0);
					}
					
					json.endObject();
				}
			}
			
			json.endArray();
		} else {
			json.key(AreaData.FIELD_FLEETS).		array().endArray().
				key(AreaData.FIELD_BLACKHOLES).		array().endArray().
				key(AreaData.FIELD_DOODADS).		array().endArray().
				key(AreaData.FIELD_DOMINATION).		value("").
				key(AreaData.FIELD_HYPERSPACE_SIGNATURES).array().endArray().
				key(AreaData.FIELD_WARDS).			array().endArray().
				key(AreaData.FIELD_ASTEROIDS).		array().endArray().
				key(AreaData.FIELD_SPACE_STATION).	array().endArray().
				key(AreaData.FIELD_STRUCTURES).		array().endArray();
		}
		
		// Liste des systèmes contenues dans la zone
		List<StarSystem> systems = area.getSystems();
		
		synchronized (systems) {
			json.key(AreaData.FIELD_SYSTEMS).array();
			
			for (StarSystem system : systems) {
				json.object().
					key(StarSystemData.FIELD_ID).				value(system.getId()).
					key(StarSystemData.FIELD_NAME).				value(system.getName()).
					key(StarSystemData.FIELD_X).				value(system.getX()).
					key(StarSystemData.FIELD_Y).				value(system.getY()).
					key(StarSystemData.FIELD_COLONIZABLE).		value(!system.isAi()).
					key(StarSystemData.FIELD_STAR_IMAGE).		value(system.getStarImage()).
					key(StarSystemData.FIELD_ASTEROID_BELT).	value(system.getAsteroidBelt()).
					key(StarSystemData.FIELD_PLANETS).			array();
				
				// Planètes dans le système
				long now = Utilities.now();
				List<Planet> planets = system.getPlanets();
				
				synchronized (planets) {
					for (Planet planet : planets) {
						json.object().
							key(PlanetData.FIELD_ANGLE).			value(Math.round(((planet.getAngle() +
									10 * now * planet.getRotationSpeed()) % (2 * Math.PI)) *
									180 / Math.PI)).
							key(PlanetData.FIELD_DISTANCE).			value(planet.getDistance()).
							key(PlanetData.FIELD_ROTATION_SPEED).	value(Math.round(planet.getRotationSpeed() * 18000 / Math.PI)).
							key(PlanetData.FIELD_IMAGE).			value(planet.getImage()).
							endObject();
					}
				}
				json.endArray();
				
				boolean visible = areaContentVisible;
				
				// Teste si le système est visible dans le brouillard de guerre
				if (visible) {
					Player owner = system.getOwner();
					boolean isAllySystem = false;
					
					if (owner != null) {
						String treaty = owner.getTreatyWithPlayer(treatiesCache, player);
						
						if (treaty.equals(Treaty.PLAYER) ||
								treaty.equals(Treaty.ALLY))
							isAllySystem = true;
					}
					
					if (!isAllySystem) {
						visible = player.isLocationVisible(area, system.getX(),
							system.getY(), GameConstants.SYSTEM_RADIUS,
							allySystems, allyFleets, allyObserverWards,
							allySpaceStations, allyStructures);
					}
				}
				
				// Affiche ou masque les propriétaires des systèmes, en
				// fonction de la visibilité de la zone
				if (visible) {
					Player owner = system.getOwner();
					Ally ally = null;
					if (owner != null)
						ally = owner.getAlly();
					
					String treaty = owner != null ?
							owner.getTreatyWithPlayer(treatiesCache, player) : Treaty.UNINHABITED;
					
					json.
						key(StarSystemData.FIELD_OWNER).		value(owner != null ? owner.getLogin() : "").
						key(StarSystemData.FIELD_ALLY).			value(ally != null ? ally.getName() : "").
						key(StarSystemData.FIELD_AI).			value(owner != null && owner.isAi()).
						key(StarSystemData.FIELD_TREATY).		value(treaty);
					
					if (treaty.equals(Treaty.PLAYER) ||
								treaty.equals(Treaty.ALLY))
						json.key(StarSystemData.FIELD_LINE_OF_SIGHT).	value(GameConstants.LOS_SYSTEM);
					else
						json.key(StarSystemData.FIELD_LINE_OF_SIGHT).	value(0);
				} else {
					json.
						key(StarSystemData.FIELD_TREATY).	value(Treaty.UNKNOWN);
				}
				
				json.endObject();
			}
			json.endArray();
		}
		
		// Missions dans le secteur
		json.key(AreaData.FIELD_CONTRACT_MARKERS).array();
		List<Fleet> fleets = area.getFleets();
		
		synchronized (fleets) {
			for (Fleet fleet : fleets) {
				if (fleet.getIdContract() != 0 &&
						ContractManager.getNpcAction(player, fleet
							).equals(Fleet.NPC_ACTION_MISSION)) {
					json.object().
						key(ContractMarkerData.FIELD_ID).	value(fleet.getId()).
						key(ContractMarkerData.FIELD_X).		value(fleet.getX()).
						key(ContractMarkerData.FIELD_Y).		value(fleet.getY()).
						endObject();
				}
			}
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	public static Area getRandomArea(){
		List<Sector> sectors = DataAccess.getAllSectors();
		List<Area> areas = sectors.get(Utilities.random(0, sectors.size() - 1)).getAreas();
		return areas.get(Utilities.random(0, areas.size() - 1));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
