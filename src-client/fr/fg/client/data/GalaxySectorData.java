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

public class GalaxySectorData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_X = "b", //$NON-NLS-1$
		FIELD_Y = "c", //$NON-NLS-1$
		FIELD_VISIBILITY = "d", //$NON-NLS-1$
		FIELD_NAME = "e", //$NON-NLS-1$
		FIELD_AREAS = "f", //$NON-NLS-1$
		FIELD_NEBULA = "g", //$NON-NLS-1$
		FIELD_JUMPS = "h", //$NON-NLS-1$
		FIELD_MARKERS = "i", //$NON-NLS-1$
		FIELD_STRATEGIC_VALUE = "j", //$NON-NLS-1$
		FIELD_ALLY_INFLUENCES = "k", //$NON-NLS-1$
		FIELD_TERRITORY_HASH = "l", //$NON-NLS-1$
		FIELD_LVLMIN = "m", //$NON-NLS-1$
		FIELD_LVLMAX="n"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	protected GalaxySectorData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public final native int getId() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_ID];
	}-*/;

	public final native int getX() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_X];
	}-*/;

	public final native int getY() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_Y];
	}-*/;

	public final native String getVisibility() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_VISIBILITY];
	}-*/;

	public final native String getName() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_NAME];
	}-*/;
	
	public final native int getAreasCount() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_AREAS].length;
	}-*/;
	
	public final native GalaxyAreaData getAreaAt(int index) /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_AREAS][index];
	}-*/;

	public final native int getJumpsCount() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_JUMPS].length;
	}-*/;
	
	public final native GalaxyJumpData getJumpAt(int index) /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_JUMPS][index];
	}-*/;

	public final native int getMarkersCount() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_MARKERS].length;
	}-*/;
	
	public final native GalaxyMarkerData getMarkerAt(int index) /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_MARKERS][index];
	}-*/;

	public final native int getNebula() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_NEBULA];
	}-*/;
	
	public final native int getStrategicValue() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_STRATEGIC_VALUE];
	}-*/;

	public final native int getAllyInfluencesCount() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_ALLY_INFLUENCES].length;
	}-*/;

	public final native AllyInfluenceData getAllyInfluenceAt(int index) /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_ALLY_INFLUENCES][index];
	}-*/;
	
	public final native String getTerritoryHash() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_TERRITORY_HASH];
	}-*/;
	
	public final native int getLvlMin() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_LVLMIN];
	}-*/;
	
	public final native int getLvlMax() /*-{
		return this[@fr.fg.client.data.GalaxySectorData::FIELD_LVLMAX];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
