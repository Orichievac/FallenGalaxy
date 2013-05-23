/*
Copyright 2010 Thierry Chevalier

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

package fr.fg.client.core;

import java.util.HashMap;

import com.google.gwt.user.client.ui.HTMLPanel;

import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.data.ServerData;
import fr.fg.client.data.AnswerData;

public class HelpDialog extends JSDialog implements ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	JSRowLayout layout;
	private HTMLPanel frame;
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	public HelpDialog() {
		super("Aide de Fallen Galaxy", false, true, true);
		centerOnScreen();
		HashMap<String, String> params = new HashMap<String, String>();
		new Action("serverinfos", params, this);
	}
	// --------------------------------------------------------- METHODES -- //
	public void onSuccess(AnswerData data) {
		ServerData serverData = data.getServerInfos(); 
		frame = new HTMLPanel(
				"<iframe src=\"http://help.fallengalaxy.com/?lang="+serverData.getLanguage()+"\" width=750 height=350 scrolling=no frameborder=0 ></iframe>"
				);
		setComponent(frame);
		centerOnScreen();
		setVisible(true);
	}
	
	public void onFailure(String error) {
		// TODO Auto-generated method stub
		
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
