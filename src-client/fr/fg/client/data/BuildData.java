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

public class BuildData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_TYPE = "a", //$NON-NLS-1$
		FIELD_LEVEL = "b", //$NON-NLS-1$
		FIELD_END = "c", //$NON-NLS-1$
		FIELD_PROGRESS = "d"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected BuildData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public native final String getType() /*-{
		return this[@fr.fg.client.data.BuildData::FIELD_TYPE];
	}-*/;

	public native final int getLevel() /*-{
		return this[@fr.fg.client.data.BuildData::FIELD_LEVEL];
	}-*/;

	public native final int getEnd() /*-{
		return this[@fr.fg.client.data.BuildData::FIELD_END];
	}-*/;
	
	public native final double getProgress() /*-{
		return this[@fr.fg.client.data.BuildData::FIELD_PROGRESS];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
