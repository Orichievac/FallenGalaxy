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

public class EventsData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_LAST_UPDATE = "a",
		FIELD_EVENTS = "b",
		FIELD_ALERTS = "c";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected EventsData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final double getLastUpdate() /*-{
		return this[@fr.fg.client.data.EventsData::FIELD_LAST_UPDATE];
	}-*/;
	
	public native final int getEventsCount() /*-{
		return this[@fr.fg.client.data.EventsData::FIELD_EVENTS].length;
	}-*/;
	
	public native final EventData getEventAt(int index) /*-{
		return this[@fr.fg.client.data.EventsData::FIELD_EVENTS][index];
	}-*/;
	
	public native final int getAlertsCount() /*-{
		return this[@fr.fg.client.data.EventsData::FIELD_ALERTS].length;
	}-*/;
	
	public native final AlertData getAlertAt(int index) /*-{
		return this[@fr.fg.client.data.EventsData::FIELD_ALERTS][index];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
