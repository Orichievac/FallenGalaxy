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

public class XpData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_XP = "a",
		FIELD_COLONIZATION_POINTS = "b",
		FIELD_LAST_UPDATE = "c";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected XpData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final double getXp() /*-{
		return this[@fr.fg.client.data.XpData::FIELD_XP];
	}-*/;

	public native final int getColonizationPoints() /*-{
		return this[@fr.fg.client.data.XpData::FIELD_COLONIZATION_POINTS];
	}-*/;

	public native final int getLastUpdate() /*-{
		return this[@fr.fg.client.data.XpData::FIELD_LAST_UPDATE];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
