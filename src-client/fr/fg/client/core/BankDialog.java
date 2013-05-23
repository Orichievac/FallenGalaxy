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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.BankAccountData;
import fr.fg.client.data.PlayerFleetData;
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
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;

public class BankDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		RESOURCES_BANK = 0,
		RESOURCES_FLEET = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Control[][] controls;
	
	private PlayerFleetData fleetData;
	
	private BankAccountData bankAccountData;
	
	private Action currentAction;
	
	private JSButton exitBt, transferBt, cancelBt;
	
	private JSTextField quantityField;
	
	private JSLabel playerCreditsLabel, payloadValueLabel;
	
	private JSRowLayout layout;
	
	private JSLabel transferLabel;
	
	private ResourcesManager resourcesManager;
	
	private long[] fleetResources;
	
	private double[] rates;
	
	private long[][] resources;
	
	private boolean insufficientCredits;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BankDialog(ResourcesManager resourcesManager) {
		super("Banque", true, true, true);
		
		this.resourcesManager = resourcesManager;
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		
		JSLabel buyLabel = new JSLabel("&nbsp;<b>Ressources déposées</b>");
		buyLabel.setPixelWidth(204);
		
		JSLabel sellLabel = new JSLabel("&nbsp;<b>Ressources sur la flotte</b>");
		sellLabel.setPixelWidth(204);
		
		// ressources
		FlowPanel row = new FlowPanel();
		OpenJWT.setElementFloat(row.getElement(), "left");
		controls = new Control[4][2];
		
		for (int y = 0; y < 2; y++) {
			if (y == 0)
				row.add(JSRowLayout.createHorizontalSeparator(4));
			
			for (int x = 0; x < 4; x++) {
				Control control = new Control(x, y);
				row.add(control);
				controls[x][y] = control;
			}
			
			if (y == 0)
				row.add(JSRowLayout.createHorizontalSeparator(20));
			else if (y == 1)
				row.add(JSRowLayout.createHorizontalSeparator(5));
		}
		
		// Quantités de ressources à vendre / à acheter
		JSLabel quantityLabel = new JSLabel("&nbsp;Transférer au maximum");
		quantityLabel.setPixelWidth(180);
		
		quantityField = new JSTextField();
		quantityField.setPixelWidth(120);
		quantityField.setToolTipText(messages.transferLimitHelp());
		
		// Capacité de transport de la flotte
		JSLabel payloadLabel = new JSLabel("&nbsp;Capacité");
		payloadLabel.setPixelWidth(180);
		
		payloadValueLabel = new JSLabel();
		payloadValueLabel.setPixelWidth(257);
		
		// Coût d'un transfert de ressources
		JSLabel creditsLabel = new JSLabel("&nbsp;Coût transfert");
		creditsLabel.setPixelWidth(180);
		
		playerCreditsLabel = new JSLabel();
		playerCreditsLabel.setPixelWidth(100);
		
		transferLabel = new JSLabel();
		transferLabel.setPixelWidth(430);
		transferLabel.getElement().getStyle().setProperty("visibility", "hidden");
		
		transferBt = new JSButton("Transférer");
		transferBt.setPixelWidth(100);
		transferBt.addClickListener(this);
		transferBt.setVisible(false);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		cancelBt.setVisible(false);
		
		exitBt = new JSButton("Terminé");
		exitBt.setPixelWidth(100);
		exitBt.addClickListener(this);
		
		// Mise en forme des composants
		layout = new JSRowLayout();
		layout.addComponent(buyLabel);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(20));
		layout.addComponent(sellLabel);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addRow();
		layout.addComponent(row);
		layout.addRowSeparator(20);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(5));
		layout.addComponent(quantityLabel);
		layout.addComponent(quantityField);
		layout.addRow();
		layout.addComponent(JSRowLayout.createHorizontalSeparator(5));
		layout.addComponent(payloadLabel);
		layout.addComponent(payloadValueLabel);
		layout.addRow();
		layout.addComponent(JSRowLayout.createHorizontalSeparator(5));
		layout.addComponent(creditsLabel);
		layout.addComponent(playerCreditsLabel);
		layout.addRowSeparator(10);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(5));
		layout.addComponent(transferLabel);
		layout.addRowSeparator(10);
		layout.addComponent(exitBt);
		layout.addComponent(transferBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addRowSeparator(5);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible)
			quantityField.setFocus(false);
		else
			Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_BANK);
		
		layout.update();
	}
	
	public void show(PlayerFleetData fleetData) {
		if (currentAction != null && currentAction.isPending())
			return;
		
		this.fleetData = fleetData;
		
		fleetResources = new long[4];
		resources = new long[4][2];
		// TODO à refaire
		long[] resources = fleetData.getResources();
		for (int i = 0; i < resources.length; i++) {
			fleetResources[i] = resources[i];
			this.resources[i][RESOURCES_FLEET] = fleetResources[i];
		}
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fleet", String.valueOf(fleetData.getId()));
		
		currentAction = new Action("bank/getaccount", params, this);
	}
	
	public void onClick(Widget sender) {
		if (sender == cancelBt) {
			resources = new long[4][2];
			for (int i = 0; i < bankAccountData.getResourcesCount(); i++)
				resources[i][RESOURCES_BANK] = (long) bankAccountData.getResourceAt(i);
			for (int i = 0; i < fleetResources.length; i++)
				resources[i][RESOURCES_FLEET] = fleetResources[i];
			
			updateUI();
		} else if (sender == exitBt) {
			setVisible(false);
		} else if (sender == transferBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("fleet", String.valueOf(fleetData.getId()));
			for (int i = 0; i < resources.length; i++)
				params.put("resource" + i, String.valueOf(resources[i][RESOURCES_FLEET]));
			
			currentAction = new Action("bank/transfer", params, this);
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		for (int i = 0; i < fleetData.getItemsCount(); i++)
			fleetResources[i] = resources[i][RESOURCES_FLEET];
		
		bankAccountData = data.getBankAccount();
		
		String transferCost = Formatter.formatNumber(
			bankAccountData.getFees(), true) + "&nbsp;<img src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" " +
			"class=\"resource credits\"/>";
		playerCreditsLabel.setText(transferCost);
		
		resources = new long[4][2];
		for (int i = 0; i < bankAccountData.getResourcesCount(); i++)
			resources[i][RESOURCES_BANK] = (long) bankAccountData.getResourceAt(i);
		for (int i = 0; i < fleetResources.length; i++)
			resources[i][RESOURCES_FLEET] = fleetResources[i];
		
		rates = new double[4];
		for (int i = 0; i < bankAccountData.getRatesCount(); i++)
			rates[i] = bankAccountData.getRateAt(i);
		
		transferLabel.setText("&nbsp;<span style=\"color: red;\">" +
			"Voulez-vous procéder au transfert des ressources pour " +
			transferCost + " ?</span>");
		transferLabel.getElement().getStyle().setProperty("visibility", "hidden");
		transferBt.setVisible(false);
		cancelBt.setVisible(false);
		exitBt.setVisible(true);
		
		if (resourcesManager.getCurrentCredits() < bankAccountData.getFees()) {
			transferLabel.setText("&nbsp;<span style=\"color: red;\">" +
				"Crédits insuffisants pour un transfert.</span>");
			transferLabel.getElement().getStyle().setProperty("visibility", "");
			insufficientCredits = true;
		} else {
			insufficientCredits = false;
		}
		
		quantityField.setText("");
		
		updateUI();
		
		if (!isVisible())
			setVisible(true);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateUI() {
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		for (int i = 0; i < 4; i++) {
			setControl(i, 0, "resource r" + i, resources[i][RESOURCES_BANK] > 0 ?
				Formatter.formatNumber(resources[i][RESOURCES_BANK], true) : "", rates[i],
				"<b>" + Formatter.formatNumber(resources[i][RESOURCES_BANK]) + " " + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.getString("resource" + i) + "</b>");
			setControl(i, 1, "", "", -1, null);
		}
		
		// Ressources
		int resourceIndex = 0;
		for (int i = 0; i < resources.length; i++) {
			double resource = resources[i][RESOURCES_FLEET];
			if (resource != 0) {
				String count = Formatter.formatNumber(resource, true);
				
				setControl(resourceIndex++, 1, "resource r" + i, count, //$NON-NLS-1$
					-1, "<b>" + Formatter.formatNumber(resource) + " " + //$NON-NLS-1$ //$NON-NLS-2$
					dynamicMessages.getString("resource" + i) + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		// Capacité de la flotte
		long payload = fleetData.getPayload();
		long resourcesSum = 0;
		
		for (int i = 0; i < resources.length; i++)
			resourcesSum += resources[i][1];
		
		payloadValueLabel.setText(resourcesSum + " / " + payload);
		
		if (!insufficientCredits) {
			boolean transfer = false;
			for (int i = 0; i < fleetResources.length; i++) {
				if (fleetResources[i] != resources[i][RESOURCES_FLEET]) {
					transfer = true;
					break;
				}
			}
			
			transferBt.setVisible(transfer);
			cancelBt.setVisible(transfer);
			exitBt.setVisible(!transfer);
			transferLabel.getElement().getStyle().setProperty(
					"visibility", transfer ? "" : "hidden");
		}
		layout.update();
	}
	
	private long getTransferLimit() {
		String text = quantityField.getText();
		
		long quantity = Utilities.parseNumber(text);
		
		
		if (quantity < 0)
			return 0;
		if (quantity > 2000000000)
			return 2000000000;
		return quantity;
	}
	
	private void setControl(int x, int y, String className, String content,
			double interest, String tooltip) {
		controls[x][y].setStyleName("control " + className); //$NON-NLS-1$
		controls[x][y].getElement().setInnerHTML(content +
			"<div class=\"tradeValue\"></div>" +
			"<div class=\"selection\"></div>");
		
		if (interest != -1) {
			OutlineText value = TextManager.getText(
				"+" + ((int) Math.round(1000 * interest * 604800)) / 10. + "%");
			
			controls[x][y].getElement().getFirstChildElement(
					).appendChild(value.getElement());
		}
		
		if (tooltip != null)
			ToolTipManager.getInstance().register(controls[x][y].getElement(), tooltip, 180);
		else
			ToolTipManager.getInstance().unregister(controls[x][y].getElement());
	}
	
	private void onClick(int x, int y) {
		if (insufficientCredits)
			return;
		
		long payload = fleetData.getPayload();
		long resourcesSum = 0;
		
		for (int i = 0; i < resources.length; i++)
			resourcesSum += resources[i][1];
		
		int resourceIndex;
		if (y == RESOURCES_BANK) {
			resourceIndex = x;
		} else {
			resourceIndex = 0;
			int count = 0;
			for (int i = 0; i < resources.length; i++) {
				if (resources[i][RESOURCES_FLEET] > 0) {
					if (count == x)
						resourceIndex = i;
					
					count++;
				}
			}		
		}
		
		double transferedResources;
		if (y == RESOURCES_FLEET)
			transferedResources = resources[resourceIndex][y];
		else
			transferedResources = Math.min(
				payload - resourcesSum, resources[x][y]);
		
		long limit = getTransferLimit();
		if (limit > 0)
			transferedResources = Math.min(limit, transferedResources);
		
		resources[resourceIndex][y == 0 ? 1 : 0] += transferedResources;
		resources[resourceIndex][y] -= transferedResources;
		
		updateUI();
	}
	
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
