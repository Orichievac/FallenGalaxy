/*
Copyright 2010 Thierry Chevalier

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

public class PlayerInfosData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
		public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_LOGIN = "b", //$NON-NLS-1$
		FIELD_BANNEDGAME = "c", //$NON-NLS-1$
		FIELD_DATE = "d", //$NON-NLS-1$
		FIELD_REASON = "e", //$NON-NLS-1$
		FIELD_PROBABILITY = "f", //$NON-NLS-1$
		FIELD_OTHER_LOGIN = "g", //$NON-NLS-1$
		FIELD_REDUNDANT_IP = "h"; //$NON-NLS-1$
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	protected PlayerInfosData() {
		// Impossible de construire directement un objet JS
	}
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_ID];
	}-*/;
	
	public native final String getLogin() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_LOGIN];
	}-*/;
	
	public native final boolean isBanned() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_BANNEDGAME];
	}-*/;
	
	public native final String getDate() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_DATE];
	}-*/;
	
	public native final String getReason() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_REASON];
	}-*/;
	
	public native final int getProbability() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_PROBABILITY];
	}-*/;
	
	public native final String getOtherLogin() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_OTHER_LOGIN];
	}-*/;
	
	public native final int getRedundantIp() /*-{
	return this[@fr.fg.client.data.PlayerInfosData::FIELD_REDUNDANT_IP];
	}-*/;
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
