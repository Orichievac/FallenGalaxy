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

public class DialogData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_TALKER = "a", //$NON-NLS-1$
		FIELD_CONTENT = "b", //$NON-NLS-1$
		FIELD_OPTIONS = "c", //$NON-NLS-1$
		FIELD_AVATAR = "d", //$NON-NLS-1$
		FIELD_VALID_OPTIONS = "e"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected DialogData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public final native boolean isEndOfDialog() /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_TALKER] == '';
	}-*/;
	
	public final native String getAvatar() /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_AVATAR];
	}-*/;

	public final native String getTalker() /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_TALKER];
	}-*/;

	public final native String getContent() /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_CONTENT];
	}-*/;
	
	public final native int getOptionsCount() /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_OPTIONS].length;
	}-*/;
	
	public final native String getOptionAt(int index) /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_OPTIONS][index];
	}-*/;

	public final native int getValidOptionsCount() /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_VALID_OPTIONS].length;
	}-*/;
	
	public final native boolean isValidOptionAt(int index) /*-{
		return this[@fr.fg.client.data.DialogData::FIELD_VALID_OPTIONS][index];
	}-*/;
	
	public final String[] getOptions() {
		String[] options = new String[getOptionsCount()];
		for (int i = 0; i < options.length; i++)
			options[i] = getOptionAt(i);
		return options;
	}
	
	public final boolean[] getValidOptions() {
		boolean[] options = new boolean[getValidOptionsCount()];
		for (int i = 0; i < options.length; i++)
			options[i] = isValidOptionAt(i);
		return options;
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
