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

package fr.fg.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class AreaData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		HYPERSPACE_SHORT_JUMP = 10,
		HYPERSPACE_LONG_JUMP = 25;
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_NAME = "b", //$NON-NLS-1$
		FIELD_WIDTH = "c", //$NON-NLS-1$
		FIELD_HEIGHT = "d", //$NON-NLS-1$
		FIELD_X = "e", //$NON-NLS-1$
		FIELD_Y = "f", //$NON-NLS-1$
		FIELD_NEBULA = "g", //$NON-NLS-1$
		FIELD_DOMINATION = "i", //$NON-NLS-1$
		FIELD_SYSTEMS = "j", //$NON-NLS-1$
		FIELD_ASTEROIDS = "k", //$NON-NLS-1$
		FIELD_DOODADS = "l", //$NON-NLS-1$
		FIELD_GATES = "m", //$NON-NLS-1$
		FIELD_BLACKHOLES = "n", //$NON-NLS-1$
		FIELD_MARKERS = "p", //$NON-NLS-1$
		FIELD_FLEETS = "q", //$NON-NLS-1$
		FIELD_BANKS = "r", //$NON-NLS-1$
		FIELD_TRADECENTERS = "s", //$NON-NLS-1$
		FIELD_SPACE_STATION = "t", //$NON-NLS-1$
		FIELD_HYPERSPACE_SIGNATURES = "v", //$NON-NLS-1$
		FIELD_CONTRACT_MARKERS = "w", //$NON-NLS-1$
		FIELD_SECTOR = "x", //$NON-NLS-1$
		FIELD_WARDS = "y", //$NON-NLS-1$
		FIELD_STRUCTURES = "z", //$NON-NLS-1$
		FIELD_GRAVITY_WELLS = "A", //$NON-NLS-1$
		FIELD_ENVIRONMENT = "B",
		FIELD_LOTTERYS = "C";//$NON-NLS-1$
		
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AreaData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getId() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_ID];
	}-*/;
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_NAME];
	}-*/;

	public final native PlayerSectorData getSector() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_SECTOR];
	}-*/;
	
	public final native GalaxySectorData getQuadrant() /*-{
	return this[@fr.fg.client.data.GalaxySectorData::FIELD_NAME];
}-*/;
	
	public final native int getWidth() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_WIDTH];
	}-*/;
	
	public final native int getHeight() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_HEIGHT];
	}-*/;
	
	public final native int getX() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_X];
	}-*/;
	
	public final native int getY() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_Y];
	}-*/;

	public final native int getNebula() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_NEBULA];
	}-*/;

	public final native String getEnvironment() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_ENVIRONMENT];
	}-*/;
	
	public final boolean hasDomination() {
		return getDomination().length() > 0;
	}

	public final native String getDomination() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_DOMINATION];
	}-*/;

	public final native int getSystemsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_SYSTEMS].length;
	}-*/;

	public final native StarSystemData getSystemAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_SYSTEMS][index];
	}-*/;
	
	public final native int getAsteroidsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_ASTEROIDS].length;
	}-*/;
	
	public final native AsteroidsData getAsteroidsAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_ASTEROIDS][index];
	}-*/;
	
	public final native int getDoodadsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_DOODADS].length;
	}-*/;
	
	public final native DoodadData getDoodadAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_DOODADS][index];
	}-*/;

	public final native int getGatesCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_GATES].length;
	}-*/;

	public final native GateData getGateAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_GATES][index];
	}-*/;

	public final native int getBlackHolesCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_BLACKHOLES].length;
	}-*/;
	
	public final native BlackHoleData getBlackHoleAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_BLACKHOLES][index];
	}-*/;
	
	public final native int getMarkersCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_MARKERS].length;
	}-*/;
	
	public final native MarkerData getMarkerAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_MARKERS][index];
	}-*/;

	public final native int getFleetsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_FLEETS].length;
	}-*/;
	
	public final native FleetData getFleetAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_FLEETS][index];
	}-*/;

	public final native int getBanksCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_BANKS].length;
	}-*/;
	
	public final native BankData getBankAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_BANKS][index];
	}-*/;

	public final native int getTradeCentersCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_TRADECENTERS].length;
	}-*/;
	
	public final native TradeCenterData getTradeCenterAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_TRADECENTERS][index];
	}-*/;

	public final native int getSpaceStationsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_SPACE_STATION].length;
	}-*/;
	
	public final native SpaceStationData getSpaceStationAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_SPACE_STATION][index];
	}-*/;

	public final native int getHyperspaceSignaturesCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_HYPERSPACE_SIGNATURES].length;
	}-*/;
	
	public final native HyperspaceSignatureData getHyperspaceSignatureAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_HYPERSPACE_SIGNATURES][index];
	}-*/;

	public final native int getContractMarkersCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_CONTRACT_MARKERS].length;
	}-*/;
	
	public final native ContractMarkerData getContractMarkerAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_CONTRACT_MARKERS][index];
	}-*/;

	public final native int getWardsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_WARDS].length;
	}-*/;
	
	public final native WardData getWardAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_WARDS][index];
	}-*/;
	
	public final native int getStructuresCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_STRUCTURES].length;
	}-*/;
	
	public final native StructureData getStructureAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_STRUCTURES][index];
	}-*/;
	
	public final native int getGravityWellsCount() /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_GRAVITY_WELLS].length;
	}-*/;
	
	public final native GravityWellData getGravityWellAt(int index) /*-{
		return this[@fr.fg.client.data.AreaData::FIELD_GRAVITY_WELLS][index];
	}-*/;
	
	public final native LotteryData getLotteryAt(int index) /*-{
	return this[@fr.fg.client.data.AreaData::FIELD_LOTTERYS][index];
}-*/;
	
	public final native int getLotterysCount() /*-{
	return this[@fr.fg.client.data.AreaData::FIELD_LOTTERYS].length;
}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
