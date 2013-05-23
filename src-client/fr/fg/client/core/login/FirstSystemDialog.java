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

package fr.fg.client.core.login;

import java.util.HashMap;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;

public class FirstSystemDialog extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTextField playerField;
	
	private ActionCallback callback;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FirstSystemDialog(ActionCallback callback) {
		super("Départ", true, true, false);
		
		this.callback = callback;
		
		HTMLPanel info = new HTMLPanel("<div class=\"justify\" style=\"padding: 3px;\">" +
				"Si vous souhaitez être placé près d'un joueur, indiquez " +
				"son login. Laissez le champ vide sinon.</div>");
		info.setSize("300px", "50px");
		OpenJWT.setElementFloat(info.getElement(), "left");
		
		JSLabel playerLabel = new JSLabel("&nbsp;" + "Joueur");
		playerLabel.setPixelWidth(100);
		
		playerField = new JSTextField();
		playerField.setPixelWidth(200);
		
		JSButton goBt = new JSButton("C'est parti !");
		goBt.setPixelWidth(120);
		goBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(info);
		layout.addRow();
		layout.addComponent(playerLabel);
		layout.addComponent(playerField);
		layout.addRowSeparator(10);
		layout.addComponent(goBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			playerField.setFocus(false);
		}
	}
	
	public void onClick(Widget sender) {
		if (currentAction != null && currentAction.isPending())
			return;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("near", playerField.getText());
		
		currentAction = new Action("getfirstsystem", params, callback);
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
