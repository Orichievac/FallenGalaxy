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

public class ContactData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_TYPE = "a", //$NON-NLS-1$
		FIELD_NAME = "b", //$NON-NLS-1$
		FIELD_MUTUAL = "c", //$NON-NLS-1$
		FIELD_CONNECTED = "d", //$NON-NLS-1$
		FIELD_ALLY_TAG = "e"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ContactData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getType() /*-{
		return this[@fr.fg.client.data.ContactData::FIELD_TYPE];
	}-*/;
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.ContactData::FIELD_NAME];
	}-*/;

	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.ContactData::FIELD_ALLY_TAG];
	}-*/;
	
	public final native boolean isMutual() /*-{
		return this[@fr.fg.client.data.ContactData::FIELD_MUTUAL];
	}-*/;
	
	public final native boolean isConnected() /*-{
		return this[@fr.fg.client.data.ContactData::FIELD_CONNECTED];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
