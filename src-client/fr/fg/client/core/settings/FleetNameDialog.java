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

package fr.fg.client.core.settings;

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
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;

public class FleetNameDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTextField fleetPrefixField;
	
	private JSComboBox fleetSuffixComboBox;
	
	private JSButton okBt, cancelBt;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetNameDialog() {
		super("Nom flottes", true, true, true);
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		
		JSLabel fleetsNameLabel = new JSLabel("&nbsp;Nom des flottes par défaut (préfixe + suffixe)");
		fleetsNameLabel.setPixelWidth(300);
		
		fleetPrefixField = new JSTextField(Settings.getFleetPrefix());
		fleetPrefixField.setPixelWidth(160);
		
		ArrayList<String> suffixes = new ArrayList<String>();
		suffixes.add("1, 2, 3…");
		suffixes.add("I, II, III…");
		suffixes.add("A, B, C…");
		
		fleetSuffixComboBox = new JSComboBox();
		fleetSuffixComboBox.setItems(suffixes);
		fleetSuffixComboBox.setPixelWidth(140);
		fleetSuffixComboBox.setSelectedIndex(Settings.getFleetSuffix());
		
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(fleetsNameLabel);
		layout.addRow();
		layout.addComponent(fleetPrefixField);
		layout.addComponent(fleetSuffixComboBox);
		layout.addRowSeparator(10);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			okBt.removeClickListener(this);
			cancelBt.removeClickListener(this);
			fleetPrefixField = null;
			fleetSuffixComboBox = null;
			okBt = null;
			cancelBt = null;
			currentAction = null;
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("prefix", fleetPrefixField.getText());
			params.put("suffix", String.valueOf(fleetSuffixComboBox.getSelectedIndex()));
			
			currentAction = new Action("setfleetnamesettings", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		Settings.setFleetPrefix(fleetPrefixField.getText());
		Settings.setFleetSuffix(fleetSuffixComboBox.getSelectedIndex());
		setVisible(false);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
