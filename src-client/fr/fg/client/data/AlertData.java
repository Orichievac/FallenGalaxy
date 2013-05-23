/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

public class AlertData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int	
		ALERT_SYSTEM_STOCK				= 1,
		ALERT_NO_RESEARCH				= 2,
		ALERT_AVAILABLE_SKILL_POINT		= 3,
		ALERT_PENDING_TREATY			= 4,
		ALERT_NO_TACTICS				= 5,
		ALERT_DESACTIVATE_STRUCTURE		= 6,
		ALERT_AVAILABLE_CIVIL_POINT		= 7;
	
	public final static int
		PRIORITY_HIGHEST	= 5,
		PRIORITY_HIGH		= 4,
		PRIORITY_AVERAGE	= 3,
		PRIORITY_LOW		= 2,
		PRIORITY_LOWEST		= 1;
	
	public final static String
		FIELD_TYPE = "a",
		FIELD_PRIORITY = "b",
		FIELD_ARG1 = "c",
		FIELD_ARG2 = "d",
		FIELD_ARG3 = "e",
		FIELD_ID_AREA = "f",
		FIELD_X = "g",
		FIELD_Y = "h",
		FIELD_AREA_NAME = "i";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AlertData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getType() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_TYPE];
	}-*/;
	
	public native final int getPriority() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_PRIORITY];
	}-*/;
	
	public native final String getArg1() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_ARG1];
	}-*/;
	
	public native final String getArg2() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_ARG2];
	}-*/;
	
	public native final String getArg3() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_ARG3];
	}-*/;

	public native final int getIdArea() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_ID_AREA];
	}-*/;

	public native final int getX() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_X];
	}-*/;

	public native final int getY() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_Y];
	}-*/;

	public native final String getAreaName() /*-{
		return this[@fr.fg.client.data.AlertData::FIELD_AREA_NAME];
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
}
