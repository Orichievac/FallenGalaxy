/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.ItemData;
import fr.fg.client.data.ItemInfoData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.ShipData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.core.Client;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.Utilities;
//import fr.fg.client.core.SwapDialog.Control;

public class SwapDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		MODE_FLEET_TO_FLEET = 0,
		MODE_SYSTEM_TO_FLEET = 1,
		MODE_FLEET_TO_STATION = 2,
		MODE_FLEET_TO_STOREHOUSE = 3,
		MODE_FLEET_TO_SPACESHIP_YARD = 4,
		MODE_BUY_FLEET_ON_SYSTEM = 5,
		MODE_BUY_FLEET_ON_SPACESHIP_YARD = 6;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSLabel[] nameLabels, fleetPowerLabels, payloadLabels;
	
	private Control[][] controls;
	
	private JSButton okBt, tacticsBt, cancelBt;
	
	private JSTextField transferLimitTextField, powerLimitTextField;
	
	private GroupData[] groups;
	
	private int mode;
	
	private Action currentAction;
	
	private JSRowLayout layout;
	
	private FlowPanel extraRow;
	
	private PlayerFleetData fleetData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SwapDialog() {
		super(((StaticMessages) GWT.create(StaticMessages.class)).transfer(),
				true, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		nameLabels = new JSLabel[2];
		nameLabels[0] = new JSLabel();
		nameLabels[0].setPixelWidth(255);
		nameLabels[1] = new JSLabel();
		nameLabels[1].setPixelWidth(255);
		
		fleetPowerLabels = new JSLabel[2];
		fleetPowerLabels[0] = new JSLabel();
		fleetPowerLabels[0].setPixelWidth(254);
		fleetPowerLabels[1] = new JSLabel();
		fleetPowerLabels[1].setPixelWidth(254);
		
		payloadLabels = new JSLabel[2];
		payloadLabels[0] = new JSLabel();
		payloadLabels[0].setPixelWidth(203);
		payloadLabels[1] = new JSLabel();
		payloadLabels[1].setPixelWidth(203);
		
		// Vaisseaux / ressources
		FlowPanel[] rows = new FlowPanel[2];
		controls = new Control[14][2];
		
		extraRow = new FlowPanel();
		extraRow.setVisible(false);
		extraRow.setPixelSize(487, 51);
		
		for (int y = 0; y < 2; y++) {
			FlowPanel row = new FlowPanel();
			OpenJWT.setElementFloat(row.getElement(), "left");
			row.setPixelSize(487, 51);
			rows[y] = row;
			
			for (int x = 0; x < 14; x++) {
				if (x == 0) {
					row.add(JSRowLayout.createHorizontalSeparator(4));
					extraRow.add(JSRowLayout.createHorizontalSeparator(4));
				}
				
				Control control = new Control(x, y);
				if (x < 5 || x >= 10)
					row.add(control);
				else if (y == 0)
					extraRow.add(control);
				controls[x][y] = control;
				
				if (x == 9)
					row.add(JSRowLayout.createHorizontalSeparator(19));
				if (x == 13) {
					row.add(JSRowLayout.createHorizontalSeparator(5));
					extraRow.add(JSRowLayout.createHorizontalSeparator(67));
				}
			}
		}
		
		// Limite d'unités transférées
		JSLabel transferLimitLabel = new JSLabel("&nbsp;" + messages.transferLimit()); //$NON-NLS-1$
		transferLimitLabel.setPixelWidth(179);
		transferLimitTextField = new JSTextField();
		transferLimitTextField.setPixelWidth(80);
		transferLimitTextField.setToolTipText(messages.transferLimitHelp());
		
		// Vérouiller la puissance
		powerLimitTextField = new JSTextField();
		powerLimitTextField.setPixelWidth(50);
		powerLimitTextField.setToolTipText("<div class=\"justify\">" +
				messages.swapLockPowerHelp() + "</div>", 200);
		JSLabel powerLimitLabel = new JSLabel("Limiter la puissance");
		powerLimitLabel.setPixelWidth(130);
		powerLimitLabel.setToolTipText("<div class=\"justify\">" +
				messages.swapLockPowerHelp() + "</div>", 200);
		
		JSLabel separator2 = new JSLabel();
		separator2.setPixelWidth(20);
		
		JSLabel optionsLabel = new JSLabel("&nbsp;<b>Options</b>");
		optionsLabel.setPixelWidth(200);
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setWidth(100 + "px"); //$NON-NLS-1$
		okBt.addClickListener(this);
		
		tacticsBt = new JSButton("► Tactique");
		tacticsBt.setWidth(100 + "px"); //$NON-NLS-1$
		tacticsBt.setVisible(false);
		tacticsBt.setToolTipText("Effectue le transfert et modifie la tactique");
		tacticsBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setWidth(100 + "px"); //$NON-NLS-1$
		cancelBt.addClickListener(this);
		
		JSLabel[] separators = new JSLabel[8];
		for (int i = 0; i < separators.length; i++) {
			separators[i] = new JSLabel();
			separators[i].setPixelWidth(i % 3 == 1 ? 20 : 5);
		}
		
		// Mise en forme des composants
		layout = new JSRowLayout();
		layout.addComponent(nameLabels[0]);
		layout.addRowSeparator(4);
		layout.addComponent(separators[0]);
		layout.addComponent(fleetPowerLabels[0]);
		layout.addComponent(separators[1]);
		layout.addComponent(payloadLabels[0]);
		layout.addComponent(separators[2]);
		layout.addRow();
		layout.addComponent(rows[0]);
		layout.addRowSeparator(1);
		layout.addComponent(extraRow);
		layout.addRowSeparator(20);
		layout.addComponent(nameLabels[1]);
		layout.addRowSeparator(4);
		layout.addComponent(separators[3]);
		layout.addComponent(fleetPowerLabels[1]);
		layout.addComponent(separators[4]);
		layout.addComponent(payloadLabels[1]);
		layout.addComponent(separators[5]);
		layout.addRow();
		layout.addComponent(rows[1]);
		layout.addRowSeparator(15);
		layout.addComponent(optionsLabel);
		layout.addRow();
		layout.addComponent(transferLimitLabel);
		layout.addComponent(transferLimitTextField);
		layout.addComponent(separator2);
		layout.addComponent(powerLimitLabel);
		layout.addComponent(powerLimitTextField);
		layout.addComponent(new JSLabel("&nbsp;<img unselectable=\"on\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" class=\"stat s-power\"/>"));
		layout.addRowSeparator(10);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(okBt);
		layout.addComponent(tacticsBt);
		layout.addComponent(cancelBt);
		
		sinkEvents(Event.ONCLICK);
		
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible)
			transferLimitTextField.setFocus(false);
	}
	
	public void show(GroupData[] groups, int mode) {
		this.groups = groups;
		this.mode = mode;
		this.transferLimitTextField.setText(""); //$NON-NLS-1$
		
		this.fleetData = groups[1].getFleetData();
		
		if(fleetData != null && ( mode == MODE_FLEET_TO_SPACESHIP_YARD || mode == MODE_BUY_FLEET_ON_SYSTEM || mode == MODE_BUY_FLEET_ON_SPACESHIP_YARD || mode == MODE_SYSTEM_TO_FLEET)){
			tacticsBt.setVisible(true);
		}else{
			tacticsBt.setVisible(false);
		}
		
		if (groups[0].getPowerLevelLimit() != -1)
			powerLimitTextField.setText(String.valueOf(groups[0].getPowerLevelLimit()));
		else if (groups[1].getPowerLevelLimit() != -1)
			powerLimitTextField.setText(String.valueOf(groups[1].getPowerLevelLimit()));
		
		for (int i = 0; i < 2; i++) {
			if (mode == MODE_SYSTEM_TO_FLEET && i == 0)
				nameLabels[i].setText(
					"<div class=\"swapIcon star\" style=\"background-image: -" + //$NON-NLS-1$
					(groups[i].getStarImage() * 30) + "px -225px;\"></div> &nbsp;" + //$NON-NLS-1$
					"<b>" + groups[i].getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
			else if (mode == MODE_FLEET_TO_STATION && i == 0)
				nameLabels[i].setText("<div class=\"swapIcon spaceStationIcon\"></div> &nbsp;" +
					"<b>" + groups[i].getName() + "</b>");
			else if (mode == MODE_FLEET_TO_STOREHOUSE && i == 0)
				nameLabels[i].setText("<div class=\"swapIcon storehouseIcon\"></div> &nbsp;" +
					"<b>" + groups[i].getName() + "</b>");
			else if (mode == MODE_FLEET_TO_SPACESHIP_YARD && i == 0)
				nameLabels[i].setText("<div class=\"swapIcon spaceshipYardIcon\"></div> &nbsp;" +
					"<b>" + groups[i].getName() + "</b>");
			else
				nameLabels[i].setText(
					"<img src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
					"images/misc/blank.gif\" class=\"swapIcon tag\" style=\"" + //$NON-NLS-1$
					"background-position: -" + (groups[i].getFleetTag() * 30) + //$NON-NLS-1$
					"px -1153px;\"/> &nbsp;<b>" + groups[i].getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		update();
		
		extraRow.setVisible(mode == MODE_SYSTEM_TO_FLEET || mode == MODE_BUY_FLEET_ON_SYSTEM);
		extraRow.getElement().getStyle().setProperty("visibility",
				mode == MODE_SYSTEM_TO_FLEET || mode == MODE_BUY_FLEET_ON_SYSTEM ? "" : "hidden");
		
		setVisible(true);
		
		layout.update();
		centerOnScreen();
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			for (int i = 0; i < 2; i++)
				if (!groups[i].hasRight(GroupData.ALLOW_EXCEED_POWER_LEVEL) &&
						groups[i].getPowerLevel() > groups[i].getPowerLevelLimit()) {
					JSOptionPane.showMessageDialog(
						"Vous ne pouvez valider le transfert tant que la " +
						"puissance dépasse la limite.",
						"Transfert impossible",
						JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
					return;
				}
			
			for (int i = 0; i < 2; i++)
				if (!groups[i].hasRight(GroupData.ALLOW_EXCEED_POWER) &&
						groups[i].getPower() > groups[i].getPowerLimit()) {
					JSOptionPane.showMessageDialog(
						"Vous ne pouvez valider le transfert tant que la " +
						"puissance dépasse la limite.",
						"Transfert impossible",
						JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
					return;
				}
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			
			for (int i = 0; i < groups[1].getItemsCount(); i++) {
				ItemData item = groups[1].getItemAt(i);
				params.put("item" + i + "_type", String.valueOf(item.getType())); //$NON-NLS-1$
				params.put("item" + i + "_id", String.valueOf(item.getId())); //$NON-NLS-1$
				params.put("item" + i + "_count", String.valueOf((long) item.getCount())); //$NON-NLS-1$
			}
			
			switch (mode) {
			case MODE_SYSTEM_TO_FLEET:
				// Transfert entre flotte / système
				params.put("system", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				for (int j = 0; j < groups[1].getSlotsCount(); j++) {
					params.put("slot1" + j + "_id", String.valueOf(groups[1].getSlotIdAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
					params.put("slot1" + j + "_count", String.valueOf(groups[1].getSlotCountAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				currentAction = new Action("swapfleetsystem", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						
						setVisible(false);
						groups = null;
						return;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_FLEET_TO_FLEET:
				// Transfert entre flottes
				params.put("fleet0", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet1", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				for (int i = 0; i < 2; i++)
					for (int j = 0; j < groups[i].getSlotsCount(); j++) {
						params.put("slot" + i + j + "_id", String.valueOf(groups[i].getSlotIdAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
						params.put("slot" + i + j + "_count", String.valueOf(groups[i].getSlotCountAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
					}
				
				currentAction = new Action("swapfleets", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						setVisible(false);
						groups = null;
						return;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_FLEET_TO_STATION:
				params.put("station", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				currentAction = new Action("swapfleetstation", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						setVisible(false);
						groups = null;
						return;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_FLEET_TO_STOREHOUSE:
				params.put("structure", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				currentAction = new Action("structure/swapfleetstorehouse", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						setVisible(false);
						groups = null;
						return;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_FLEET_TO_SPACESHIP_YARD:
				params.put("structure", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				for (int j = 0; j < groups[1].getSlotsCount(); j++) {
					params.put("slot" + j + "_id", String.valueOf(groups[1].getSlotIdAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
					params.put("slot" + j + "_count", String.valueOf(groups[1].getSlotCountAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				currentAction = new Action("structure/swapfleetspaceshipyard", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						setVisible(false);
						groups = null;
						return;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_BUY_FLEET_ON_SYSTEM:
				
				break;
			case MODE_BUY_FLEET_ON_SPACESHIP_YARD:
				
				break;
			}
		} else if (sender == cancelBt) {
			setVisible(false);
		} else if (sender == tacticsBt && fleetData != null && ( mode == MODE_FLEET_TO_SPACESHIP_YARD || mode == MODE_BUY_FLEET_ON_SYSTEM || mode == MODE_BUY_FLEET_ON_SPACESHIP_YARD || mode == MODE_SYSTEM_TO_FLEET)) {
			for (int i = 0; i < 2; i++)
				if (!groups[i].hasRight(GroupData.ALLOW_EXCEED_POWER_LEVEL) &&
						groups[i].getPowerLevel() > groups[i].getPowerLevelLimit()) {
					JSOptionPane.showMessageDialog(
						"Vous ne pouvez valider le transfert tant que la " +
						"puissance dépasse la limite.",
						"Transfert impossible",
						JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
					return;
				}
			
			for (int i = 0; i < 2; i++)
				if (!groups[i].hasRight(GroupData.ALLOW_EXCEED_POWER) &&
						groups[i].getPower() > groups[i].getPowerLimit()) {
					JSOptionPane.showMessageDialog(
						"Vous ne pouvez valider le transfert tant que la " +
						"puissance dépasse la limite.",
						"Transfert impossible",
						JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
					return;
				}
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			
			for (int i = 0; i < groups[1].getItemsCount(); i++) {
				ItemData item = groups[1].getItemAt(i);
				params.put("item" + i + "_type", String.valueOf(item.getType())); //$NON-NLS-1$
				params.put("item" + i + "_id", String.valueOf(item.getId())); //$NON-NLS-1$
				params.put("item" + i + "_count", String.valueOf((long) item.getCount())); //$NON-NLS-1$
			}
			
			switch (mode) {
			case MODE_SYSTEM_TO_FLEET:
				// Transfert entre flotte / système
				params.put("system", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				for (int j = 0; j < groups[1].getSlotsCount(); j++) {
					params.put("slot1" + j + "_id", String.valueOf(groups[1].getSlotIdAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
					params.put("slot1" + j + "_count", String.valueOf(groups[1].getSlotCountAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				currentAction = new Action("swapfleetsystem", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						// Fenêtre de tactique
						PlayerFleetData fleet = data.getFleetData();
						if (fleet == null)
							return;
						
						Client.getInstance().getTacticsDialog().show(fleet);
						setVisible(false);
						groups = null;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_FLEET_TO_SPACESHIP_YARD:
				params.put("structure", String.valueOf(groups[0].getId())); //$NON-NLS-1$
				params.put("fleet", String.valueOf(groups[1].getId())); //$NON-NLS-1$
				
				for (int j = 0; j < groups[1].getSlotsCount(); j++) {
					params.put("slot" + j + "_id", String.valueOf(groups[1].getSlotIdAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
					params.put("slot" + j + "_count", String.valueOf(groups[1].getSlotCountAt(j))); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				currentAction = new Action("structure/swapfleetspaceshipyard", params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
						
						// Fenêtre de tactique
						PlayerFleetData fleet = data.getFleetData();
						if (fleet == null)
							return;
						
						Client.getInstance().getTacticsDialog().show(fleet);
						setVisible(false);
						groups = null;
					}
				}); //$NON-NLS-1$
				break;
			case MODE_BUY_FLEET_ON_SYSTEM:
				
				break;
			case MODE_BUY_FLEET_ON_SPACESHIP_YARD:
				
				break;
			}
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
		
		setVisible(false);
		groups = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void update() {
		StaticMessages staticMessages =
			(StaticMessages) GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		// Efface les anciennes valeurs
		for (int y = 0; y < 2; y++)
			for (int x = 0; x < 14; x++)
				setControl(x, y, "", "", null); //$NON-NLS-1$ //$NON-NLS-2$
		
		for (int r = 0; r < 2; r++) {
			// Puissance de la flotte
			int fleetPower = groups[r].getPower();
			int fleetPowerLevel = groups[r].getPowerLevel();
			int levelFleetPower = GroupData.getPowerAtLevel(fleetPowerLevel);
			int nextLevelFleetPower = GroupData.getPowerAtLevel(fleetPowerLevel + 1) - 1;
			
			// Niveau de puissance
			OutlineText text;
			int width;
			if (!groups[r].hasRight(GroupData.ALLOW_EXCEED_POWER_LEVEL)) {
				text = TextManager.getText(
					"<div style=\"width: 254px\">" +
					"<b style=\"float: right;\">" + //$NON-NLS-1$
					fleetPowerLevel + "&nbsp;<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"stat s-power\" " +
					"unselectable=\"on\"/></b>&nbsp;" +
					staticMessages.swapPower() + "</div>"); //$NON-NLS-1$
				
				// Tooltip
				ToolTipManager.getInstance().register(fleetPowerLabels[r].getElement(),
					"<div class=\"title\">" + staticMessages.swapPower( //$NON-NLS-1$
					Formatter.formatNumber(fleetPower, true) + " / " + //$NON-NLS-1$
					Formatter.formatNumber(nextLevelFleetPower, true)) +
					"</div><div class=\"justify\">" + //$NON-NLS-1$
					staticMessages.swapPowerHelp() + "</div>", 200); //$NON-NLS-1$
				
				width = nextLevelFleetPower == levelFleetPower ? 100 :
					(int) Math.round(100 * (fleetPower - levelFleetPower) /
						(double) (nextLevelFleetPower - levelFleetPower));
			} else {
				text = TextManager.getText(
					"<div style=\"width: 254px\">" +
					"<b style=\"float: right;\">" + //$NON-NLS-1$
					Formatter.formatNumber(fleetPower, true) +
					"&nbsp;</b>&nbsp;" + "Vaisseaux" + "</div>"); //$NON-NLS-1$
				
				// Tooltip
				ToolTipManager.getInstance().register(fleetPowerLabels[r].getElement(),
					"<div class=\"title\">" + staticMessages.swapPower( //$NON-NLS-1$
					Formatter.formatNumber(fleetPower, true) + " / " + //$NON-NLS-1$
					Formatter.formatNumber(groups[r].getPowerLimit(), true)) +
					"</div><div class=\"justify\">" + //$NON-NLS-1$
					staticMessages.swapPowerHelp() + "</div>", 200); //$NON-NLS-1$
				
				if (!groups[r].hasRight(GroupData.ALLOW_EXCEED_POWER))
					width = Math.round(100 * fleetPower / groups[r].getPowerLimit());
				else
					width = 0;
			}
			
			// Barre de puissance
			fleetPowerLabels[r].getElement().setInnerHTML(
				"<div class=\"fleetPowerBar\" unselectable=\"on\">" + //$NON-NLS-1$
				"<div class=\"currentFleetPower" + (
				(!groups[r].hasRight(GroupData.ALLOW_EXCEED_POWER_LEVEL) && fleetPowerLevel > groups[r].getPowerLevelLimit()) ||
				(!groups[r].hasRight(GroupData.ALLOW_EXCEED_POWER) && fleetPower > groups[r].getPowerLimit()) ?
				" invalid" : "") + "\" style=\"width: " + width + "%;\" " + //$NON-NLS-1$ //$NON-NLS-2$
				"unselectable=\"on\"></div><div class=\"fleetPowerValue\" " + //$NON-NLS-1$
				"unselectable=\"on\"></div></div>"); //$NON-NLS-1$
			
			fleetPowerLabels[r].getElement().getFirstChildElement().getFirstChildElement(
					).getNextSiblingElement().appendChild(text.getElement());
			
			// Capacité de transport
			double itemsWeight = groups[r].getTotalWeight();
			double payload = groups[r].getPayload();
			
			width = (itemsWeight > payload ? 100 : payload == 0 ?
				0 : (int) Math.round(100 * Math.min(itemsWeight, payload) / payload));
			
			// Valeur de la charge
			text = TextManager.getText("<div style=\"width: 203px\">" +
				"<div style=\"float: right;\">" + //$NON-NLS-1$
				Formatter.formatNumber(itemsWeight, true) + " / " + //$NON-NLS-1$
				Formatter.formatNumber(payload, true) + "&nbsp;</div>" + //$NON-NLS-1$
				"&nbsp;" + staticMessages.payload() + "</div>"); //$NON-NLS-1$
			
			// Tooltip
			ToolTipManager.getInstance().register(payloadLabels[r].getElement(),
				"<div class=\"title\">" + staticMessages.payload() + //$NON-NLS-1$
				"</div><div class=\"justify\">" + staticMessages.payloadHelp() + //$NON-NLS-1$
				"</div>", 200); //$NON-NLS-1$
			
			// Barre de progression
			payloadLabels[r].getElement().setInnerHTML(
				"<div class=\"payloadBar\" unselectable=\"on\">" + //$NON-NLS-1$
				"<div class=\"currentPayload" + (!groups[r].hasRight(GroupData.ALLOW_EXCEED_PAYLOAD) &&
				itemsWeight > payload ? //$NON-NLS-1$
				" overload" : "") + "\" style=\"width: " + width + "%;\" " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"unselectable=\"on\"></div><div class=\"payloadValue\" " + //$NON-NLS-1$
				"unselectable=\"on\"></div></div>"); //$NON-NLS-1$
			
			payloadLabels[r].getElement().getFirstChildElement().getFirstChildElement(
					).getNextSiblingElement().appendChild(text.getElement());
			
			int slotIndex = 0;
			
			// Vaisseaux
			for (int i = 0; i < groups[r].getSlotsCount(); i++) {
				int slotId = groups[r].getSlotIdAt(i);
				if (slotId != 0) {
					long slotCount = groups[r].getSlotCountAt(i);
					String count = Formatter.formatNumber(slotCount, true);
					
					String content = "<div class=\"spaceship\" style=\"" + //$NON-NLS-1$
						"background-position: -" + (slotId * 50) + "px 0\">" + //$NON-NLS-1$ //$NON-NLS-2$
						count + "</div>"; //$NON-NLS-1$
					
					String tooltip = ShipData.getDesc(slotId, slotCount,
						Client.getInstance().getResearchManager(
							).getShipAvailableAbilities(slotId));
					
					setControl(slotIndex++, r, "", content, tooltip); //$NON-NLS-1$
				}
			}
			
			// Items
			int itemIndex = 0;
			for (int i = 0; i < groups[r].getItemsCount(); i++) {
				ItemData item = groups[r].getItemAt(i);
				if (item.getType() != ItemInfoData.TYPE_NONE) {
					String count = Formatter.formatNumber(item.getCount(), true);
					
					switch (item.getType()) {
					case ItemInfoData.TYPE_RESOURCE:
						setControl(10 + itemIndex, r, "resource r" + item.getId(), count, //$NON-NLS-1$
							"<b>" + Formatter.formatNumber(item.getCount()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
							dynamicMessages.getString("resource" + item.getId()) + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
						break;
					case ItemInfoData.TYPE_STUFF:
						// TODO
						break;
					case ItemInfoData.TYPE_STRUCTURE:
						setControl(10 + itemIndex, r, "", "<div class=\"structure\" " +
							"style=\"background-position: -" + (50 * item.getStructureType()) +
							"px -100px\">1</div><div class=\"selection\"></div>",
							"<div class=\"title\">" + dynamicMessages.getString("structure" +
							item.getStructureType()) + " niv. " + item.getStructureLevel() + "</div>" +
							"<div class=\"justify\">" + dynamicMessages.getString("structure" +
							item.getStructureType() + "Desc") + "</div>");
						break;
					}
					
					itemIndex++;
				}
			}
		}
	}
	
	private void setControl(int x, int y, String className, String content,
			String tooltip) {
		controls[x][y].setStyleName("control " + className); //$NON-NLS-1$
		controls[x][y].getElement().setInnerHTML(content);
		
		if (tooltip != null)
			ToolTipManager.getInstance().register(controls[x][y].getElement(), tooltip, 200);
		else
			ToolTipManager.getInstance().unregister(controls[x][y].getElement());
	}
	
	private long getTransferLimit() {
		long limit = Utilities.parseNumber(transferLimitTextField.getText());
		if (limit < 1)
			limit = -1;
		
		return limit;
	}
	
	private void onClick(int x, int y) {
		// Transfert de vaisseaux / items
		int fromX = x;
		int fromY = y;
		
		// Transfert de vaisseaux ?
		if (fromX < 10) {
			// Récupère le slot cliqué
			int fromSlotIndex = getSlotIndex(fromX, fromY);
			if (fromSlotIndex == -1)
				return;
			
			int fromSlotId = groups[fromY].getSlotIdAt(fromSlotIndex);
			long fromSlotCount = groups[fromY].getSlotCountAt(fromSlotIndex);
			
			long limit = getTransferLimit();
			if (limit == -1)
				limit = 10000000000l;
			
			// Détermine le nombre de vaisseaux qui restent sur la flotte
			int slotsCount = 0;
			for (int i = 0; i < groups[fromY].getSlotsCount(); i++) {
				if (groups[fromY].getSlotIdAt(i) != 0)
					slotsCount++;
			}

			int toY = fromY == 0 ? 1 : 0;
			
			long maxPower;
			if (!groups[toY].hasRight(GroupData.ALLOW_EXCEED_POWER)) {
				maxPower = groups[toY].getPowerLimit();
			} else if (!groups[toY].hasRight(GroupData.ALLOW_EXCEED_POWER_LEVEL)) {
				long powerLimit = Utilities.parseNumber(powerLimitTextField.getText());
				if (powerLimit < 1)
					powerLimit = groups[toY].getPowerLevelLimit();
				
				maxPower = GroupData.getPowerAtLevel((int) powerLimit + 1);
			} else {
				maxPower = Integer.MAX_VALUE;
			}
			
			// Impossible de transférer des vaisseaux d'une flotte vers un
			// système : limite les transferts au nombre de
			// vaisseaux initial sur le système
			if (!groups[fromY].hasRight(GroupData.ALLOW_GIVE_SHIPS) ||
					!groups[toY].hasRight(GroupData.ALLOW_RECEIVE_SHIPS)) {
				long count = 0;
				for (int i = 0; i < groups[toY].getSlotsCount(); i++)
					if (groups[toY].getSlotIdAt(i) == fromSlotId) {
						count = groups[toY].getSlotCountAt(i);
						break;
					}
				
				boolean found = false;
				for (int i = 0; i < groups[toY].getSlotsCount(); i++)
					if (groups[toY].getReferenceSlotIdAt(i) == fromSlotId) {
						found = true;
						limit = Math.min(limit, groups[toY].getReferenceSlotCountAt(i) - count);
						break;
					}
				
				if (!found)
					limit = 0;
			}
			
			for (int i = 0; i < groups[toY].getSlotsCount(); i++) {
				int toSlotId = groups[toY].getSlotIdAt(i);
				long toSlotCount = groups[toY].getSlotCountAt(i);
				
				if (toSlotId == fromSlotId) {
					// Slot contenant des vaisseaux du même type
					long count = Math.min(limit, fromSlotCount);
					if (slotsCount == 1 && count == fromSlotCount &&
							!groups[fromY].hasRight(GroupData.ALLOW_TRANSFER_ALL_SHIPS))
						count--;
					
					// Limite la puissance des flottes en fonction du niveau
					// du joueur
					int power = ShipData.CLASSES_POWER[ShipData.SHIPS[fromSlotId].getShipClass()];
					if (power > 0 && groups[toY].getPower() + power * count >= maxPower)
						count = Math.max(0, (maxPower - 1 - groups[toY].getPower()) / power);
					
					if (count == fromSlotCount) {
						groups[toY].setSlotCountAt(i, toSlotCount + fromSlotCount);
						groups[fromY].setSlotIdAt(fromSlotIndex, 0);
						groups[fromY].setSlotCountAt(fromSlotIndex, 0);
						groups[fromY].tidySlots();
					} else {
						groups[toY].setSlotCountAt(i, toSlotCount + count);
						groups[fromY].setSlotCountAt(fromSlotIndex, fromSlotCount - count);
					}
					update();
					return;
				}
			}
			
			for (int i = 0; i < groups[toY].getSlotsCount(); i++) {
				if (groups[toY].getSlotIdAt(i) == 0) {
					// Slot libre
					long count = Math.min(limit, fromSlotCount);
					if (slotsCount == 1 && count == fromSlotCount &&
							!groups[fromY].hasRight(GroupData.ALLOW_TRANSFER_ALL_SHIPS))
						count--;
					
					// Limite la puissance des flottes en fonction du niveau
					// du joueur
					int power = ShipData.CLASSES_POWER[ShipData.SHIPS[fromSlotId].getShipClass()];
					if (power > 0 && groups[toY].getPower() + power * count >= maxPower)
						count = Math.max(0, (maxPower - 1 - groups[toY].getPower()) / power);
					
					if (count == fromSlotCount) {
						groups[toY].setSlotIdAt(i, fromSlotId);
						groups[toY].setSlotCountAt(i, fromSlotCount);
						groups[fromY].setSlotIdAt(fromSlotIndex, 0);
						groups[fromY].setSlotCountAt(fromSlotIndex, 0);
						groups[fromY].tidySlots();
					} else {
						groups[toY].setSlotIdAt(i, fromSlotId);
						groups[toY].setSlotCountAt(i, count);
						groups[fromY].setSlotCountAt(fromSlotIndex, fromSlotCount - count);
					}
					update();
					return;
				}
			}
		} else {
			// Récupère l'item cliqué
			int fromItemIndex = getItemIndex(fromX - 10, fromY);
			if (fromItemIndex == -1)
				return;
			
			ItemData fromItem = groups[fromY].getItemAt(fromItemIndex);
			
			if (fromItem.getType() == ItemInfoData.TYPE_NONE)
				return;
			
			int toY = fromY == 0 ? 1 : 0;
			
			// Vérifie que le destinataire peut recevoir des items et que
			// l'expéditeur peut en envoyer
			if (!groups[toY].hasRight(GroupData.ALLOW_RECEIVE_ITEMS) ||
					!groups[fromY].hasRight(GroupData.ALLOW_GIVE_ITEMS))
				return;
			
			// Calcule la charge et la capacité du destinataire
			double toFleetPayload = groups[toY].getPayload();
			double toItemsWeight = 0;
			for (int i = 0; i < groups[toY].getItemsCount(); i++)
				toItemsWeight += groups[toY].getItemAt(i).getWeight() * groups[toY].getItemAt(i).getCount();
			
			double transferedItems;
			if (groups[toY].hasRight(GroupData.ALLOW_EXCEED_PAYLOAD))
				transferedItems = fromItem.getCount();
			else
				transferedItems = Math.min((toFleetPayload - toItemsWeight) / fromItem.getWeight(),
					fromItem.getCount());
			
			long limit = getTransferLimit();
			if (limit > 0)
				transferedItems = Math.min(limit, transferedItems);
			
			if (transferedItems > 0) {
				boolean found = false;
				
				// Recherche un emplacement avec pour transférer les items
				for (int i = 0; i < groups[toY].getItemsCount(); i++) {
					ItemData toItem = groups[toY].getItemAt(i);
					
					if (toItem.getType() == fromItem.getType() && toItem.getId() == fromItem.getId()) {
						found = true;
						if (fromItem.isStackable()) {
							fromItem.addCount(-transferedItems);
							toItem.addCount(transferedItems);
						}
						break;
					}
				}

				// Recherche un emplacement libre pour transférer les items
				if (!found) {
					for (int i = 0; i < groups[toY].getItemsCount(); i++) {
						ItemData toItem = groups[toY].getItemAt(i);
						
						if (toItem.getType() == ItemInfoData.TYPE_NONE) {
							found = true;
							toItem.setStructureType(fromItem.getStructureType());
							toItem.setStructureLevel(fromItem.getStructureLevel());
							toItem.setType(fromItem.getType());
							toItem.setId(fromItem.getId());
							toItem.setCount(transferedItems);
							fromItem.addCount(-transferedItems);
							break;
						}
					}
				}
				
				if (found)
					update();
			}
		}
	}
	
	private int getSlotIndex(int x, int y) {
		int index = 0;
		
		for (int i = 0; i < groups[y].getSlotsCount(); i++) {
			if (groups[y].getSlotIdAt(i) != 0) {
				if (index == x) 
					return i;
				index++;
			}
		}
		return -1;
	}
	
	private int getItemIndex(int x, int y) {
		int index = 0;
		
		for (int i = 0; i < groups[y].getItemsCount(); i++) {
			ItemData item = groups[y].getItemAt(i);
			if (item.getType() != ItemInfoData.TYPE_NONE) {
				if (index == x) 
					return i;
				index++;
			}
		}
		return -1;
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class Control extends BaseWidget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int x, y;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Control(int x, int y) {
			this.x = x;
			this.y = y;
			
			setStyleName("control"); //$NON-NLS-1$
			OpenJWT.setElementFloat(getElement(), "left"); //$NON-NLS-1$
			
			sinkEvents(Event.ONCLICK);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public void onBrowserEvent(Event event) {
			switch (event.getTypeInt()) {
			case Event.ONCLICK:
				onClick(x, y);
				break;
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}