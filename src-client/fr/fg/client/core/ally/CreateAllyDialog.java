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

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.SelectionListener;

public class CreateAllyDialog extends JSDialog implements ClickListener,
		SelectionListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static String[] ORGANIZATIONS = {
		"tyranny", "warmonger", "democracy", "oligarchy", "anarchy"
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton createBt, cancelBt;
	
	private JSTextField nameField, tagField;
	
	private HTMLPanel descriptionPanel;
	
	private JSComboBox organizationComboBox;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public CreateAllyDialog() {
		super("Fonder une alliance", true, true, true);
		
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		// Nom de l'alliance
		JSLabel nameBt = new JSLabel("&nbsp;Nom");
		nameBt.setPixelWidth(120);

		nameField = new JSTextField();
		nameField.setPixelWidth(180);
		nameField.setMaxLength(20);
		
		// Tag de l'alliance
		JSLabel tagBt = new JSLabel("&nbsp;Tag");
		tagBt.setPixelWidth(120);
		
		tagField = new JSTextField();
		tagField.setPixelWidth(180);
		tagField.setMaxLength(3);
		
		// Gouvernements
		JSLabel organizationBt = new JSLabel("&nbsp;Gouvernement");
		organizationBt.setPixelWidth(120);
		
		ArrayList<String> organizations = new ArrayList<String>();
		for (int i = 0; i < ORGANIZATIONS.length; i++)
			organizations.add(dynamicMessages.getString(ORGANIZATIONS[i]));
		
		organizationComboBox = new JSComboBox();
		organizationComboBox.setItems(organizations);
		organizationComboBox.setPixelWidth(180);
		organizationComboBox.addSelectionListener(this);
		
		// Description
		JSLabel descriptionLabel = new JSLabel("&nbsp;Description");
		descriptionLabel.setPixelWidth(120);
		
		descriptionPanel = new HTMLPanel(
			"<div class=\"small justify\" unselectable=\"on\">" +
			dynamicMessages.getString(ORGANIZATIONS[0] + "Desc") + "</div>");
		descriptionPanel.setPixelSize(170, 130);
		descriptionPanel.getElement().getStyle().setProperty("padding", "5px");
		OpenJWT.setElementFloat(descriptionPanel.getElement(), "left");
		
		// Boutons pour créer / annuler
		createBt = new JSButton("Créer");
		createBt.setPixelWidth(100);
		createBt.addClickListener(this);
		
		cancelBt = new JSButton("Annuler");
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(nameBt);
		layout.addComponent(nameField);
		layout.addRow();
		layout.addComponent(tagBt);
		layout.addComponent(tagField);
		layout.addRow();
		layout.addComponent(organizationBt);
		layout.addComponent(organizationComboBox);
		layout.addRow();
		layout.addComponent(descriptionLabel);
		layout.addComponent(descriptionPanel);
		layout.addRow();
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(createBt);
		layout.addComponent(cancelBt);
		
		setDefaultCloseOperation(JSDialog.DESTROY_ON_CLOSE);
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == createBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", nameField.getText());
			params.put("tag", tagField.getText());
			params.put("organization",
					ORGANIZATIONS[organizationComboBox.getSelectedIndex()]);
			
			currentAction = new Action("allies/createally", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == organizationComboBox) {
			DynamicMessages dynamicMessages =
				(DynamicMessages) GWT.create(DynamicMessages.class);
			
			descriptionPanel.getElement().setInnerHTML(
				"<div class=\"small justify\" unselectable=\"on\">" +
					dynamicMessages.getString(
						ORGANIZATIONS[newValue] + "Desc") + "</div>");
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			currentAction = null;
			nameField.setFocus(false);
			tagField.setFocus(false);
		}
	}
	
	public void onSuccess(AnswerData data) {
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
		setVisible(false);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
