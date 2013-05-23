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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.WardData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;

public class BuildWardDialog extends JSDialog implements ClickListener, KeyboardListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	
	private PlayerFleetData fleetData;
	
	private JSButton okBt, cancelBt;
	
	private JSTextField powerField;
	
	private JSLabel costValueLabel, powerLabel, powerUnitLabel;
	
	private JSRowLayout layout;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BuildWardDialog(String type, PlayerFleetData fleetData) {
		super(type.startsWith(WardData.TYPE_STUN) ||
			type.startsWith(WardData.TYPE_MINE) ?
			((DynamicMessages) GWT.create(DynamicMessages.class)).skill8() :
			((DynamicMessages) GWT.create(DynamicMessages.class)).skill7(),
			true, true, true);
		
		this.type = type;
		this.fleetData = fleetData;
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		String description = "";
		
		boolean charge = type.startsWith(WardData.TYPE_STUN) ||
			type.startsWith(WardData.TYPE_MINE);
		
		if (type.startsWith(WardData.TYPE_OBSERVER))
			description = "Indiquez la durée de vie de la balise d'observation :";
		else if (type.startsWith(WardData.TYPE_SENTRY))
			description = "Indiquez la durée de vie de la balise de détection :";
		else if (type.startsWith(WardData.TYPE_STUN))
			description = "Indiquez la puissance de la charge électromagnétique (seuil de puissance à partir duquel la charge se déclenche quand une flotte approche de la charge) :";
		else if (type.startsWith(WardData.TYPE_MINE))
			description = "Indiquez la puissance de la charge explosive (seuil de puissance à partir duquel la charge se déclenche quand une flotte approche de la charge) :";
		
		HTMLPanel descriptionPanel = new HTMLPanel(
			"<div class=\"justify\" style=\"padding: 5px;\">" + description + "</div>");
		descriptionPanel.setWidth("250px");
		OpenJWT.setElementFloat(descriptionPanel.getElement(), "left");
		
		// Puissance de la balise / mine
		powerUnitLabel = new JSLabel((charge ? "<img src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" " +
			"class=\"stat s-power\"/>" : "&nbsp;jours") + "&nbsp;");
		
		powerField = new JSTextField("1");
		powerField.setPixelWidth(50);
		powerField.addKeyboardListener(this);
		
		powerLabel = new JSLabel("&nbsp;" + (charge ? "Puissance (max " + fleetData.getPowerLevel() + ")" : "Durée de vie"));
		powerLabel.setPixelWidth(250 - powerUnitLabel.getPixelWidth() - powerField.getPixelWidth());
		
		JSLabel costLabel = new JSLabel("&nbsp;Coût");
		costLabel.setPixelWidth(100);
		
		costValueLabel = new JSLabel();
		costValueLabel.setPixelWidth(150);
		costValueLabel.setAlignment(JSLabel.ALIGN_RIGHT);
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		updateCost();
		
		layout = new JSRowLayout();
		layout.addComponent(descriptionPanel);
		layout.addRowSeparator(5);
		layout.addComponent(powerLabel);
		layout.addComponent(powerField);
		layout.addComponent(powerUnitLabel);
		layout.addRow();
		layout.addComponent(costLabel);
		layout.addComponent(costValueLabel);
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
		
		if (!visible) {
			fleetData = null;
			okBt.removeClickListener(this);
			okBt = null;
			cancelBt.removeClickListener(this);
			cancelBt = null;
			powerField.removeKeyboardListener(this);
			powerField = null;
			costValueLabel = null;
			layout = null;
			powerLabel = null;
			powerUnitLabel = null;
			currentAction = null;
		} else {
			powerLabel.setPixelWidth(250 - powerUnitLabel.getPixelWidth() - powerField.getPixelWidth());
			layout.update();
			
			powerField.setFocus(true);
			powerField.select();
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			int power = (int) Utilities.parseNumber(powerField.getText());
			boolean charge = type.startsWith(WardData.TYPE_STUN) ||
				type.startsWith(WardData.TYPE_MINE);
			
			if (power <= 0) {
				JSOptionPane.showMessageDialog(charge ?
					"Puissance invalide." : "Durée de vie invalide.",
					"Erreur", JSOptionPane.OK_OPTION,
					JSOptionPane.ERROR_MESSAGE, null);
				return;
			}
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("fleet", String.valueOf(fleetData.getId()));
			params.put("ward", String.valueOf(type));
			params.put("power", String.valueOf(power));
			
			currentAction = new Action("skill/buildward", params,
				new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						setVisible(false);
						SelectionManager.getInstance().setNoSelection();
						
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
					}
				});
		} else if (sender == cancelBt) {
			setVisible(false);
		}
	}
	
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		updateCost();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateCost() {
		int power = (int) Utilities.parseNumber(powerField.getText());
		long cost = 0;
		
		if (power > 99) {
			power = 99;
			powerField.setText(String.valueOf(power));
		}
		
		if (power > 0)
			cost = getWardCost(type, power);
		
		long[] resources = fleetData.getResources();
		
		costValueLabel.setText((cost > resources[3] ?
			"<span style=\"color: red;\">" + Formatter.formatNumber(cost, true) +
			"</span>" : Formatter.formatNumber(cost, true)) +
			"&nbsp;/&nbsp;" + Formatter.formatNumber(resources[3], true) +
			"&nbsp;<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"resource r3\"/>&nbsp;");
	}
	
	private long getWardCost(String type, int power) {
		if (type.startsWith(WardData.TYPE_OBSERVER) ||
				type.startsWith(WardData.TYPE_SENTRY)) {
			// Observation + détection 
			int increment = 500;
			int cost = 1500;
			
			for (int i = 1; i < power; i++) {
				cost += increment;
				increment += 50;
			}
			
			if (type.contains("invisible"))
				cost *= 4;
			
			return cost;
		} else if (type.startsWith(WardData.TYPE_MINE)) {
			// Charge explosive
			int ships = (FleetData.getPowerLevel(power + 1) - 1) / 2;
			int cost = ships * 10;
			
			if (type.contains("invisible"))
				cost *= 5;
			
			return cost;
		} else {
			// Charge IEM
			int cost;
			
			if (type.contains("invisible"))
				cost = 47500 + 2500 * power;
			else
				cost = 9000 + 1000 * power;
			
			return cost;
		}
	}
}
