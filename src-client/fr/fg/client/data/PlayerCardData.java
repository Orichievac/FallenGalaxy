/*
Copyright 2010 Ghost

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

public class PlayerCardData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static String
		FIELD_DESCRIPTION = "a",
		FIELD_AVATAR = "b"; //$NON-NLS-1$
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	protected PlayerCardData() {
		// TODO Auto-generated constructor stub
	}
	// --------------------------------------------------------- METHODES -- //
	
	public native final String getDescription() /*-{
		return this[@fr.fg.client.data.PlayerCardData::FIELD_DESCRIPTION];
	}-*/;
	
	public native final String getAvatarUrl() /*-{
	return this[@fr.fg.client.data.PlayerCardData::FIELD_AVATAR];
}-*/;
	// ------------------------------------------------- METHODES PRIVEES -- //
}
