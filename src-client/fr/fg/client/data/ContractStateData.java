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

public class ContractStateData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_NAME = "a",
		FIELD_STATUS = "b",
		FIELD_DESCRIPTION = "c",
		FIELD_ID_AREA = "d",
		FIELD_X = "e",
		FIELD_Y = "f";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ContractStateData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_NAME];
	}-*/;
	
	public final native String getStatus() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_STATUS];
	}-*/;
	
	public final native String getDescription() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_DESCRIPTION];
	}-*/;

	public final native int getIdArea() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_ID_AREA];
	}-*/;

	public final native boolean isLocationDefined() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_X] !== undefined &&
			this[@fr.fg.client.data.ContractStateData::FIELD_Y] !== undefined;
	}-*/;

	public final native int getX() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_X];
	}-*/;

	public final native int getY() /*-{
		return this[@fr.fg.client.data.ContractStateData::FIELD_Y];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
