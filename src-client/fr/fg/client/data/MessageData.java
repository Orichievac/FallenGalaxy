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

public class MessageData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_TITLE = "b", //$NON-NLS-1$
		FIELD_CONTENT = "c", //$NON-NLS-1$
		FIELD_DATE = "d", //$NON-NLS-1$
		FIELD_TARGET_ALLY_TAG = "e", //$NON-NLS-1$
		FIELD_TARGET_PLAYER = "f", //$NON-NLS-1$
		FIELD_TREATY = "h", //$NON-NLS-1$
		FIELD_READ = "i", //$NON-NLS-1$
		FIELD_BOOKMARK = "k"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected MessageData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getId() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_ID];
	}-*/;
	
	public final native String getTitle() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_TITLE];
	}-*/;
	
	public final native String getContent() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_CONTENT];
	}-*/;
	
	public final native double getDate() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_DATE];
	}-*/;

	public final boolean hasTargetPlayer() {
		return getTargetPlayer().length() > 0;
	}
	
	public final native String getTargetPlayer() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_TARGET_PLAYER];
	}-*/;
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_TARGET_ALLY_TAG];
	}-*/;
	
	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_TREATY];
	}-*/;
	
	public final native boolean isRead() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_READ];
	}-*/;
	
	public final native void setRead(boolean read) /*-{
		this[@fr.fg.client.data.MessageData::FIELD_READ] = read;
	}-*/;
	
	public final native boolean isBookmark() /*-{
		return this[@fr.fg.client.data.MessageData::FIELD_BOOKMARK];
	}-*/;

	public final native void setBookmark(boolean bookmarked) /*-{
		this[@fr.fg.client.data.MessageData::FIELD_BOOKMARK] = bookmarked;
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
