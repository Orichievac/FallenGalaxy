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

public class AllyNewsData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a",
		FIELD_TITLE = "b",
		FIELD_CONTENT = "c",
		FIELD_DATE = "d",
		FIELD_PARENT = "f",
		FIELD_AUTHOR = "g",
		FIELD_STICKY = "h",
		FIELD_ALLY_TAG = "i",
		FIELD_TREATY = "j",
		FIELD_ID_APPLICANT = "k",
		FIELD_READ = "l";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AllyNewsData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_ID];
	}-*/;

	public native final String getTitle() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_TITLE];
	}-*/;

	public native final String getContent() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_CONTENT];
	}-*/;

	public native final double getDate() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_DATE];
	}-*/;

	public native final int getParent() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_PARENT];
	}-*/;

	public native final String getAuthor() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_AUTHOR];
	}-*/;
	
	public final boolean hasAuthor() {
		return getAuthor().length() > 0;
	}
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_ALLY_TAG];
	}-*/;

	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_TREATY];
	}-*/;
	
	public native final boolean isSticky() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_STICKY];
	}-*/;

	public native final int getIdApplicant() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_ID_APPLICANT];
	}-*/;

	public native final boolean isRead() /*-{
		return this[@fr.fg.client.data.AllyNewsData::FIELD_READ];
	}-*/;
	
	public final native void setRead(boolean read) /*-{
		this[@fr.fg.client.data.AllyNewsData::FIELD_READ] = read;
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
