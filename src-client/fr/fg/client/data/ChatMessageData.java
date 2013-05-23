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

public class ChatMessageData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_TYPE = "a", //$NON-NLS-1$
		FIELD_RIGHTS = "b", //$NON-NLS-1$
		FIELD_AUTHOR = "c", //$NON-NLS-1$
		FIELD_CONTENT = "d", //$NON-NLS-1$
		FIELD_ALLY_TAG = "e", //$NON-NLS-1$
		FIELD_DATE = "f", //$NON-NLS-1$
		FIELD_ALLY_NAME = "g", //$NON-NLS-1$
		FIELD_CHANNEL = "h", //$NON-NLS-1$
		FIELD_LEVEL = "i", //$NON-NLS-1$
		FIELD_RANK = "j", //$NON-NLS-1$
		FIELD_CONNECTED = "k", //$NON-NLS-1$
		FIELD_AWAY = "l"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ChatMessageData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final native String getType() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_TYPE];
	}-*/;
	
	public final native String getRights() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_RIGHTS];
	}-*/;

	public final native String getAuthor() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_AUTHOR];
	}-*/;

	public final native String getContent() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_CONTENT];
	}-*/;

	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_ALLY_TAG];
	}-*/;

	public final native String getDate() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_DATE];
	}-*/;

	public final native String getAllyName() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_ALLY_NAME];
	}-*/;

	public final native String getChannel() /*-{
		return this[@fr.fg.client.data.ChatMessageData::FIELD_CHANNEL];
	}-*/;
	
	public final native int getLevel() /*-{
	return this[@fr.fg.client.data.ChatMessageData::FIELD_LEVEL];
    }-*/;
	
	public final native int getPlayerRank() /*-{
	return this[@fr.fg.client.data.ChatMessageData::FIELD_RANK];
    }-*/;


	// ------------------------------------------------- METHODES PRIVEES -- //
}
