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

public class AllyDescriptionData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_NAME = "a",
		FIELD_TAG = "b",
		FIELD_DESCRIPTION = "c",
		FIELD_FOUNDER = "d",
		FIELD_LEADERS = "e";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AllyDescriptionData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.AllyDescriptionData::FIELD_NAME];
	}-*/;
	
	public final native String getTag() /*-{
		return this[@fr.fg.client.data.AllyDescriptionData::FIELD_TAG];
	}-*/;
	
	public final native String getDescription() /*-{
		return this[@fr.fg.client.data.AllyDescriptionData::FIELD_DESCRIPTION];
	}-*/;
	
	public final native String getFounder() /*-{
		return this[@fr.fg.client.data.AllyDescriptionData::FIELD_FOUNDER];
	}-*/;
	
	public final native int getLeadersCount() /*-{
		return this[@fr.fg.client.data.AllyDescriptionData::FIELD_LEADERS].length;
	}-*/;
	
	public final native String getLeaderAt(int index) /*-{
		return this[@fr.fg.client.data.AllyDescriptionData::FIELD_LEADERS][index];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
