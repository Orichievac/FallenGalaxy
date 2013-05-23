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

package fr.fg.client.core.ally;

import java.util.HashMap;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.JSTextPane;

public class ApplyDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTextField allyField;
	
	private JSTextPane commentPane;
	
	private JSButton sendBt, cancelBt;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ApplyDialog() {
		super("Postuler à une alliance", true, true, true);
		
		// Nom de l'alliance
		JSLabel allyLabel = new JSLabel("&nbsp;Alliance");
		allyLabel.setPixelWidth(100);
		
		allyField = new JSTextField();
		allyField.setPixelWidth(200);
		
		// CV
		commentPane = new JSTextPane();
		commentPane.setHTML("Votre commentaire ici...");
		commentPane.setPixelSize(300, 220);
		commentPane.sinkEvents(Event.ONCLICK);
		
		// Boutons Envoyer / Annuler
		sendBt = new JSButton("Postuler");
		sendBt.setPixelWidth(100);
		sendBt.addClickListener(this);
		
		cancelBt = new JSButton("Annuler");
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(allyLabel);
		layout.addComponent(allyField);
		layout.addRow();
		layout.addComponent(commentPane);
		layout.addRowSeparator(5);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(sendBt);
		layout.addComponent(cancelBt);
		
		setComponent(layout);
		centerOnScreen();
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == sendBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("ally", allyField.getText());
			params.put("cv", commentPane.getHTML());
			
			currentAction = new Action("allies/apply", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			currentAction = null;
			allyField.setFocus(false);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
		setVisible(false);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
