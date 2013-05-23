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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;

public class NewTreatyDialog extends JSDialog implements ClickListener,
		DialogListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int TARGET_PLAYER = 0, TARGET_ALLY = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int target;
	
	private ActionCallback callback;
	
	private JSComboBox treatyComboBox; 
	
	private JSButton okBt, cancelBt;
	
	private JSTextField nameField;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public NewTreatyDialog(int target, ActionCallback callback) {
		super("Proposer Pacte / Déclarer Guerre", true, true, true);
		
		this.target = target;
		this.callback = callback;
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		
		ArrayList<String> treaties = new ArrayList<String>();
		treaties.add("Pacte de Non Agression");
		treaties.add("Pacte Défensif");
		treaties.add("Pacte Total");
		treaties.add("Déclarer la guerre");
		
		treatyComboBox = new JSComboBox();
		treatyComboBox.setPixelWidth(200);
		treatyComboBox.setItems(treaties);
		
		JSLabel nameLabel = new JSLabel(target == TARGET_PLAYER ?
				"Au joueur" : "A l'alliance");
		nameLabel.setPixelWidth(80);
		
		nameField = new JSTextField();
		nameField.setPixelWidth(120);
		
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(JSRowLayout.createHorizontalSeparator(30));
		layout.addComponent(treatyComboBox);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(30));
		layout.addRow();
		layout.addComponent(nameLabel);
		layout.addComponent(nameField);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addRowSeparator(10);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
		addDialogListener(this);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			HashMap<String, String> params = new HashMap<String, String>();
			String uri;
			
			if (target == TARGET_PLAYER) {
				if (treatyComboBox.getSelectedIndex() == 3) {
					params.put("player", nameField.getText());
					uri = "declarewar";
				} else {
					params.put("player", nameField.getText());
					params.put("type",  ((Integer)treatyComboBox.getSelectedIndex()).toString() );
					params.put("accept", "true");
					uri = "offerally";
				}
			} else {
				if (treatyComboBox.getSelectedIndex() == 3) {
					params.put("ally", nameField.getText());
					uri = "allies/declarewar";
				} else {
					params.put("ally", nameField.getText());
					params.put("type",  ((Integer)treatyComboBox.getSelectedIndex()).toString() );
					params.put("accept", "true");
					uri = "allies/offerally";
				}
			}
			
			new Action(uri, params, this);
		} else if (sender == cancelBt) {
			removeDialogListener(this);
			setVisible(false);
			callback = null;
			treatyComboBox = null;
			okBt.removeClickListener(this);
			okBt = null;
			cancelBt.removeClickListener(this);
			cancelBt = null;
		}
	}
	
	public void dialogClosed(Widget sender) {
		removeDialogListener(this);
		callback = null;
		treatyComboBox = null;
		okBt.removeClickListener(this);
		okBt = null;
		cancelBt.removeClickListener(this);
		cancelBt = null;
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		removeDialogListener(this);
		setVisible(false);
		treatyComboBox = null;
		okBt.removeClickListener(this);
		okBt = null;
		cancelBt.removeClickListener(this);
		cancelBt = null;
		
		callback.onSuccess(data);
		callback = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
