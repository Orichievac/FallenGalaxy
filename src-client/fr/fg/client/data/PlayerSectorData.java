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

package fr.fg.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class PlayerSectorData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_NAME = "b", //$NON-NLS-1$
		FIELD_X = "c", //$NON-NLS-1$
		FIELD_Y = "d", //$NON-NLS-1$
		FIELD_TYPE = "e"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected PlayerSectorData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.PlayerSectorData::FIELD_ID];
	}-*/;
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.PlayerSectorData::FIELD_NAME];
	}-*/;
	
	public final native int getX() /*-{
		return this[@fr.fg.client.data.PlayerSectorData::FIELD_X];
	}-*/;
	
	public final native int getY() /*-{
		return this[@fr.fg.client.data.PlayerSectorData::FIELD_Y];
	}-*/;
	
	public final native int getType() /*-{
	return this[@fr.fg.client.data.PlayerSectorData::FIELD_TYPE];
}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
