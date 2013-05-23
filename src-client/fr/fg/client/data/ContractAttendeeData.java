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

public class ContractAttendeeData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_PLAYER = "player",
		TYPE_ALLY = "ally";
	
	public final static String
		FIELD_LOGIN = "a", //$NON-NLS-1$
		FIELD_ALLY_TAG = "b", //$NON-NLS-1$
		FIELD_ALLY_NAME = "c", //$NON-NLS-1$
		FIELD_TREATY = "d", //$NON-NLS-1$
		FIELD_TYPE = "e"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ContractAttendeeData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getLogin() /*-{
		return this[@fr.fg.client.data.ContractAttendeeData::FIELD_LOGIN];
	}-*/;
	
	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.ContractAttendeeData::FIELD_ALLY_TAG];
	}-*/;
	
	public final native String getAllyName() /*-{
		return this[@fr.fg.client.data.ContractAttendeeData::FIELD_ALLY_NAME];
	}-*/;
	
	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.ContractAttendeeData::FIELD_TREATY];
	}-*/;
	
	public final native String getType() /*-{
		return this[@fr.fg.client.data.ContractAttendeeData::FIELD_TYPE];
	}-*/;
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final boolean hasAllyName() {
		return getAllyName().length() > 0;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
