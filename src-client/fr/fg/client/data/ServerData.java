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

public class ServerData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_NAME = "a",
		FIELD_LANGUAGE = "b",
		FIELD_URL = "c",
		FIELD_OPENING = "d";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ServerData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.ServerData::FIELD_NAME];
	}-*/;

	public final native String getLanguage() /*-{
		return this[@fr.fg.client.data.ServerData::FIELD_LANGUAGE];
	}-*/;

	public final native String getUrl() /*-{
		return this[@fr.fg.client.data.ServerData::FIELD_URL];
	}-*/;

	public final native double getOpeningDate() /*-{
		return this[@fr.fg.client.data.ServerData::FIELD_OPENING];
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
}
