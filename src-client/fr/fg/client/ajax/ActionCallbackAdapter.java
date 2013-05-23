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

package fr.fg.client.ajax;

import com.google.gwt.core.client.GWT;

import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.JSOptionPane;

public class ActionCallbackAdapter implements ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void onSuccess(AnswerData data) {
		
	}
	
	public void onFailure(String error) {
		onFailureDefaultBehavior(error);
	}
	
	public static void onFailureDefaultBehavior(String error) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		if (error.length() > 1000)
			error = error.substring(0, 1000) + "[...]";
		
		JSOptionPane.showMessageDialog(error, messages.error(),
				JSOptionPane.OK_OPTION, JSOptionPane.ERROR_MESSAGE, null);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
