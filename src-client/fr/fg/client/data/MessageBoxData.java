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

public class MessageBoxData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_RECEIVED = "a", //$NON-NLS-1$
		FIELD_SENT = "b", //$NON-NLS-1$
		FIELD_LAST_UPDATE = "c"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected MessageBoxData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native MessageData getReceivedMessageAt(int index) /*-{
		return this[@fr.fg.client.data.MessageBoxData::FIELD_RECEIVED][index];
	}-*/;
	
	public final native int getReceivedMessageCount() /*-{
		return this[@fr.fg.client.data.MessageBoxData::FIELD_RECEIVED].length;
	}-*/;
	
	public final native MessageData getSentMessageAt(int index) /*-{
		return this[@fr.fg.client.data.MessageBoxData::FIELD_SENT][index];
	}-*/;

	public final native int getSentMessageCount() /*-{
		return this[@fr.fg.client.data.MessageBoxData::FIELD_SENT].length;
	}-*/;
	
	public final native double getLastUpdate() /*-{
		return this[@fr.fg.client.data.MessageBoxData::FIELD_LAST_UPDATE];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
