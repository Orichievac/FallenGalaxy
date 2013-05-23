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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;

public class MarkerDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTextField messageField;
	
	private JSComboBox visibilityComboBox;
	
	private JSButton okBt, cancelBt, galaxyBt;
	
	private int idArea;
	
	private Point location;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public MarkerDialog(int idArea, Point location) {
		super("Afficher un message", true, true, true);
		
		this.idArea = idArea;
		this.location = location;
		
		// Message du marqueur
		JSLabel messageLabel = new JSLabel("&nbsp;Message");
		messageLabel.setPixelWidth(100);
		messageField = new JSTextField();
		messageField.setPixelWidth(150);
		
		// Visibilité du marqueur
		ArrayList<String> items = new ArrayList<String>();
		items.add("Vous");
		items.add("Alliance");
		items.add("Coalitions");
		
		JSLabel visibilityLabel = new JSLabel("&nbsp;Visible par");
		visibilityLabel.setPixelWidth(150);
		visibilityComboBox = new JSComboBox();
		visibilityComboBox.setPixelWidth(100);
		visibilityComboBox.setItems(items);
		
		JSLabel galaxyLabel = new JSLabel("&nbsp;Sur la galaxie");
		galaxyLabel.setPixelWidth(217);
		galaxyBt = new JSButton();
		galaxyBt.setPixelWidth(33);
		galaxyBt.addClickListener(this);
		galaxyBt.addStyleName("checkBox");
		
		// Boutons OK / Annuler
		okBt = new JSButton("OK");
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton("Annuler");
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(messageLabel);
		layout.addComponent(messageField);
		layout.addRow();
		layout.addComponent(visibilityLabel);
		layout.addComponent(visibilityComboBox);
		layout.addRow();
		layout.addComponent(galaxyLabel);
		layout.addComponent(galaxyBt);
		layout.addRowSeparator(10);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
	}

	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible)
			messageField.setFocus(true);
		else
			messageField.setFocus(false);
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			setVisible(false);
			
			String[] visibility = {"player", "ally", "allied"};
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("area", String.valueOf(idArea));
			params.put("x", String.valueOf(location.getX()));
			params.put("y", String.valueOf(location.getY()));
			params.put("message", messageField.getText());
			params.put("galaxy", String.valueOf(galaxyBt.getStyleName().contains("checked")));
			params.put("visibility", visibility[visibilityComboBox.getSelectedIndex()]);
			
			currentAction = new Action("markers/add", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
		} else if (sender == galaxyBt) {
			if (!galaxyBt.getStyleName().contains("checked"))
				galaxyBt.addStyleName("checked");
			else
				galaxyBt.removeStyleName("checked");
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		setVisible(false);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
