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

public class AllyMemberData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_ID = "a",
		FIELD_LOGIN = "b",
		FIELD_RANK = "c",
		FIELD_POINTS = "d",
		FIELD_FLEETS = "e",
		FIELD_SYSTEMS = "f",
		FIELD_IDLE_TIME = "g",
		FIELD_FLEETS_POWER = "h";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AllyMemberData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_ID];
	}-*/;

	public native final String getLogin() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_LOGIN];
	}-*/;
	
	public native final int getRank() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_RANK];
	}-*/;

	public native final double getPoints() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_POINTS];
	}-*/;
	
	public native final int getFleetsCount() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_FLEETS].length;
	}-*/;
	
	public native final AllyMemberFleetData getFleetAt(int index) /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_FLEETS][index];
	}-*/;
	
	public native final int getSystemsCount() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_SYSTEMS].length;
	}-*/;
	
	public native final String getSystemAt(int index) /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_SYSTEMS][index];
	}-*/;
	
	public native final int getIdleTime() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_IDLE_TIME];
	}-*/;

	public native final double getTotalFleetsPower() /*-{
		return this[@fr.fg.client.data.AllyMemberData::FIELD_FLEETS_POWER];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
