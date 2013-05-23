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

public class ProbeReportData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_SYSTEM_NAME = "a", //$NON-NLS-1$
		FIELD_AVAILABLE_SPACE = "b", //$NON-NLS-1$
		FIELD_RESOURCES = "c"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ProbeReportData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final String getSystemName() /*-{
		return this[@fr.fg.client.data.ProbeReportData::FIELD_SYSTEM_NAME];
	}-*/;
	
	public native final int getAvailableSpace() /*-{
		return this[@fr.fg.client.data.ProbeReportData::FIELD_AVAILABLE_SPACE];
	}-*/;
	
	public native final int getResourcesCount() /*-{
		return this[@fr.fg.client.data.ProbeReportData::FIELD_RESOURCES].length;
	}-*/;
	
	public native final int getResourceAt(int index) /*-{
		return this[@fr.fg.client.data.ProbeReportData::FIELD_RESOURCES][index];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
