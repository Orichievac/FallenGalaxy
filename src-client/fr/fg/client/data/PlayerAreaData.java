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

public class PlayerAreaData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_NAME = "b", //$NON-NLS-1$
		FIELD_X = "c", //$NON-NLS-1$
		FIELD_Y = "d", //$NON-NLS-1$
		FIELD_TYPE = "e", //$NON-NLS-1$
		FIELD_DOMINATION = "g", //$NON-NLS-1$
		FIELD_SECTOR = "h", //$NON-NLS-1$
		FIELD_SPACE_STATIONS_COUNT = "i", //$NON-NLS-1$
		FIELD_MAX_SPACE_STATIONS = "j"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected PlayerAreaData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_ID];
	}-*/;
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_NAME];
	}-*/;
	
	public final native int getX() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_X];
	}-*/;
	
	public final native int getY() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_Y];
	}-*/;
	
	public final native String getType() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_TYPE];
	}-*/;
	
	public final boolean hasDomination() {
		return getDomination().length() > 0;
	}
	
	public final native String getDomination() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_DOMINATION];
	}-*/;

	public final native PlayerSectorData getSector() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_SECTOR];
	}-*/;

	public final native int getSpaceStationsCount() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_SPACE_STATIONS_COUNT];
	}-*/;

	public final native int getMaxSpaceStations() /*-{
		return this[@fr.fg.client.data.PlayerAreaData::FIELD_MAX_SPACE_STATIONS];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
