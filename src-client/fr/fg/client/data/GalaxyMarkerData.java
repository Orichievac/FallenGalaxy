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

public class GalaxyMarkerData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_MESSAGE = "a", //$NON-NLS-1$
		FIELD_OWNER = "b", //$NON-NLS-1$
		FIELD_ALLY = "c", //$NON-NLS-1$
		FIELD_TREATY = "d", //$NON-NLS-1$
		FIELD_AREA_X = "e", //$NON-NLS-1$
		FIELD_AREA_Y = "f", //$NON-NLS-1$
		FIELD_ID_AREA = "g", //$NON-NLS-1$
		FIELD_CONTRACT = "h"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected GalaxyMarkerData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final String getMessage() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_MESSAGE];
	}-*/;
	
	public native final String getOwner() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_OWNER];
	}-*/;
	
	public native final String getAlly() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_ALLY];
	}-*/;

	public final boolean hasAlly() {
		return getAlly().length() > 0;
	}
	
	public native final int getAreaX() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_AREA_X];
	}-*/;

	public native final int getAreaY() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_AREA_Y];
	}-*/;

	public native final int getIdArea() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_ID_AREA];
	}-*/;
	
	public native final String getTreaty() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_TREATY];
	}-*/;
	
	public final boolean isContractMarker() {
		return getContract().length() > 0;
	}
	
	public native final String getContract() /*-{
		return this[@fr.fg.client.data.GalaxyMarkerData::FIELD_CONTRACT];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
