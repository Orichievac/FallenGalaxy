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
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.ItemInfoData;
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
import fr.fg.client.core.Client;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.Utilities;
//import fr.fg.client.core.TradeDialog.Control;

public class TradeDialog extends JSDialog implements ActionCallback,
KeyboardListener, ClickListener {
// ------------------------------------------------------- CONSTANTES -- //

private final static int MODE_BUY = 0, MODE_SELL = 1;

// -------------------------------------------------------- ATTRIBUTS -- //

private Control[][] controls;

private PlayerFleetData fleetData;

private Action currentAction;

private JSButton transferBt, exitBt;

private JSTextField quantityField;

private JSRowLayout layout;

private long playerCredits;

private double variation;

private double fees;

private double[] baseRates;

private int selectedResourceIndex;

private long selectedResourceCount;

private int mode;

private JSLabel playerCreditsLabel, payloadValueLabel;

// ---------------------------------------------------- CONSTRUCTEURS -- //

public TradeDialog() {
super("Centre de commerce", true, true, true);

StaticMessages messages = GWT.create(StaticMessages.class);

JSLabel buyLabel = new JSLabel("&nbsp;<b>Achat</b> (prix pour 100 unités)");
buyLabel.setPixelWidth(204);

JSLabel sellLabel = new JSLabel("&nbsp;<b>Vente</b> (prix pour 100 unités)");
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
JSLabel quantityLabel = new JSLabel("&nbsp;Quantité");
quantityLabel.setPixelWidth(120);

quantityField = new JSTextField();
quantityField.setPixelWidth(120);
quantityField.addKeyboardListener(this);
quantityField.setToolTipText(messages.transferLimitHelp());

transferBt = new JSButton();
transferBt.setPixelWidth(100);
transferBt.getElement().getStyle().setProperty("visibility", "hidden");
transferBt.addClickListener(this);

// Crédits du joueur
JSLabel creditsLabel = new JSLabel("&nbsp;Vos crédits");
creditsLabel.setPixelWidth(120);

playerCreditsLabel = new JSLabel();
playerCreditsLabel.setPixelWidth(317);

// Capacité de transport de la flotte
JSLabel payloadLabel = new JSLabel("&nbsp;Capacité");
payloadLabel.setPixelWidth(120);

payloadValueLabel = new JSLabel();
payloadValueLabel.setPixelWidth(317);

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
layout.addComponent(transferBt);
layout.addRow();
layout.addComponent(JSRowLayout.createHorizontalSeparator(5));
layout.addComponent(creditsLabel);
layout.addComponent(playerCreditsLabel);
layout.addRow();
layout.addComponent(JSRowLayout.createHorizontalSeparator(5));
layout.addComponent(payloadLabel);
layout.addComponent(payloadValueLabel);
layout.addRowSeparator(20);
layout.addComponent(exitBt);
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
}

public void show(PlayerFleetData fleetData) {
if (currentAction != null && currentAction.isPending())
	return;

this.fleetData = fleetData;

HashMap<String, String> params = new HashMap<String, String>();
params.put("fleet", String.valueOf(fleetData.getId()));

currentAction = new Action("trade/getrates", params, this);

Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_TRADECENTER);
}

public void onFailure(String error) {
ActionCallbackAdapter.onFailureDefaultBehavior(error);
}

public void onSuccess(AnswerData data) {
// Calcule les taux de change
this.variation = data.getTradeCenterRates().getVariation();
this.fees = data.getTradeCenterRates().getFees();
this.baseRates = new double[4];

for (int i = 0; i < 4; i++)
	baseRates[i] = data.getTradeCenterRates().getRate(i);

updateUI();

setVisible(true);
}

public void onKeyDown(Widget sender, char keyCode, int modifiers) {
// Sans effet
}

public void onKeyPress(Widget sender, char keyCode, int modifiers) {
// Sans effet
}

public void onKeyUp(Widget sender, char keyCode, int modifiers) {
updateCredits();
}

public void onClick(Widget sender) {
if (sender == transferBt) {
	if (currentAction != null && currentAction.isPending())
		return;
	
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("fleet", String.valueOf(fleetData.getId()));
	params.put("resource", String.valueOf(selectedResourceIndex));
	params.put("count", String.valueOf(
			mode == MODE_BUY ? getQuantity() : -getQuantity()));
	
	currentAction = new Action("trade/change", params,
		new ActionCallbackAdapter() {
			@Override
			public void onSuccess(AnswerData data) {
				UpdateManager.UPDATE_CALLBACK.onSuccess(data);
				
				fleetData = Client.getInstance().getEmpireView(
						).getFleetById(fleetData.getId());
				
				if (fleetData != null) {
					baseRates[selectedResourceIndex] +=
						(mode == MODE_SELL ? -1 : 1) *
						getQuantity() * variation;
					
					if (baseRates[selectedResourceIndex] < .005)
						baseRates[selectedResourceIndex] = .005;
					if (baseRates[selectedResourceIndex] > 9.52)
						baseRates[selectedResourceIndex] = 9.52;
					
					updateUI();
				}
			}
		});
} else if (sender == exitBt) {
	setVisible(false);
}
}

// ------------------------------------------------- METHODES PRIVEES -- //

private void updateUI() {
DynamicMessages dynamicMessages =
	(DynamicMessages) GWT.create(DynamicMessages.class);

for (int i = 0; i < 4; i++) {
	setControl(i, 0, "resource r" + i, "", (int) Math.ceil(100 *
		baseRates[i] * (1 + fees)),
		dynamicMessages.getString("resource" + i));
	setControl(i, 1, "", "", -1, null);
	controls[i][0].setSelected(false);
	controls[i][1].setSelected(false);
}

controls[0][0].setSelected(true);
mode = MODE_BUY;
selectedResourceIndex = 0;

// Ressources
int resourceIndex = 0;
for (int i = 0; i < fleetData.getItemsCount(); i++) {
	ItemInfoData item = fleetData.getItemAt(i);
	if (item.getType() == ItemInfoData.TYPE_RESOURCE) {
		String count = Formatter.formatNumber(item.getCount(), true);
		
		setControl(resourceIndex++, 1, "resource r" + item.getId(), count, //$NON-NLS-1$
			(int) Math.floor(100 * baseRates[item.getId()] * (1 - fees)),
			"<b>" + Formatter.formatNumber(item.getCount()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
			dynamicMessages.getString("resource" + item.getId()) + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}

// Capacité de la flotte
payloadValueLabel.setText(fleetData.getTotalWeight() +
		" / " + fleetData.getPayload());

quantityField.setText("");

// Crédits disponibles
playerCredits = Client.getInstance(
		).getResourcesManager().getCurrentCredits();

updateCredits();
}

private void setControl(int x, int y, String className, String content,
	int tradeValue, String tooltip) {
controls[x][y].setStyleName("control " + className); //$NON-NLS-1$
controls[x][y].getElement().setInnerHTML(content +
	"<div class=\"tradeValue\"></div>" +
	"<div class=\"selection\"></div>");

if (tradeValue != -1) {
	OutlineText value = TextManager.getText("&nbsp;" +
			Utilities.parseSmilies(tradeValue + ":cr:"));
	
	controls[x][y].getElement().getFirstChildElement(
			).appendChild(value.getElement());
}

if (tooltip != null)
	ToolTipManager.getInstance().register(controls[x][y].getElement(), tooltip, 200);
else
	ToolTipManager.getInstance().unregister(controls[x][y].getElement());
}

private void onClick(int x, int y) {
if (!controls[x][y].getStyleName().contains("resource"))
	return;

mode = y;

if (mode == MODE_SELL) {
	int index = 0;
	for (int i = 0; i < 4; i++)
		if (fleetData.getItemAt(i).getType() == ItemInfoData.TYPE_RESOURCE) {
			if (index == x) {
				selectedResourceIndex = fleetData.getItemAt(i).getId();
				selectedResourceCount = (long) fleetData.getItemAt(i).getCount();
				break;
			}
			index++;
		}
} else {
	selectedResourceIndex = x;
}

for (int i = 0; i < controls.length; i++)
	for (int j = 0; j < controls[i].length; j++)
		controls[i][j].setSelected(i == x && j == y);

if (mode == MODE_SELL)
	quantityField.setText(String.valueOf(selectedResourceCount));
else
	quantityField.setText("");
quantityField.setFocus(true);
quantityField.select();

updateCredits();
}

private void updateCredits() {
if (getQuantity() > 0) {
	transferBt.setLabel(mode == MODE_BUY ? "Acheter" : "Vendre");
	transferBt.getElement().getStyle().setProperty("visibility", "");
	
	double payload = Math.round((double)fleetData.getPayload()); //overflow long
	double totalWeight = Math.round((double) fleetData.getTotalWeight()); //overflow long
	
	double quantity = getQuantity(); //Overflow
	if (mode == MODE_SELL && quantity > selectedResourceCount) {
		quantity = selectedResourceCount;
		quantityField.setText(String.valueOf(quantity));
	}
	
	if (mode == MODE_BUY && totalWeight + quantity > payload) {
		quantity = Math.round((double) (payload - totalWeight));
		quantityField.setText(String.valueOf(quantity));
	}
	
	double rateBefore = baseRates[selectedResourceIndex];
	double rateAfter = baseRates[selectedResourceIndex] +
		(mode == MODE_SELL ? -1 : 1) * quantity * variation;
	
	// Taux de change
	double rate;
	double min = .005;
	double max = 9.52;
	
	if (rateAfter < min) {
		double coef = (rateBefore - min) / (rateBefore - rateAfter); 
		rate = coef * (rateBefore - min) / 2 + (1 - coef) * min;
		rateAfter = min;
	} else if (rateAfter > max) {
		//double coef = (max - rateBefore) / (rateAfter - rateBefore); 
		//rate = coef * (max - rateBefore) / 2 + (coef) * max;
		rate=(rateAfter+rateBefore)/2; // remplacement des 2 lignes:à modifier plus tard
		rateAfter = max;

	} else {
		rate = (rateAfter + rateBefore) / 2; 
	}
	
	rate *= (1 + (mode == MODE_SELL ? -fees : fees));
	
	long credits = (long) (mode == MODE_SELL ? 
		Math.floor(rate * quantity) :
		Math.ceil(rate * quantity));
	
	// Taux de change sans taxe
	double creditsTaxFree = (double) (mode == MODE_SELL ? //Overflow long 
		Math.floor(rateBefore * quantity) :
		Math.ceil(rateBefore * quantity));
	
	double tax = mode == MODE_SELL ?
			1 - (credits / (double) creditsTaxFree) :
			Math.abs(creditsTaxFree - credits) / (double) credits;
	

	playerCreditsLabel.setText(Formatter.formatNumber(playerCredits +
		(mode == MODE_SELL ? credits : -credits)) +
		"&nbsp;<img src=\"" + Config.getMediaUrl() +
		"images/misc/blank.gif\" class=\"resource credits\"/> (" +
		Formatter.formatNumber((mode == MODE_SELL ? credits : -credits)) +
		", dont " + (double) Math.ceil(100 * tax) + "% taxe)"); //Overflow int
	
	payloadValueLabel.setText(totalWeight +
		(mode == MODE_SELL ? -quantity : quantity) + " / " + payload);
} else {
	transferBt.getElement().getStyle().setProperty("visibility", "hidden");
	
	playerCreditsLabel.setText(Formatter.formatNumber(playerCredits) +
		"&nbsp;<img src=\"" + Config.getMediaUrl() +
		"images/misc/blank.gif\" class=\"resource credits\"/>");
	
	payloadValueLabel.setText(fleetData.getTotalWeight() +
			" / " + fleetData.getPayload());
}
}

private long getQuantity() {
String text = quantityField.getText();

long quantity = Utilities.parseNumber(text);

if (quantity < 0)
	return 0;
if (quantity > 1000000000)
	return 1000000000;
return quantity;
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

public void setSelected(boolean selected) {
	if (selected && getStyleName().contains("resource"))
		addStyleName("control-selected");
	else
		removeStyleName("control-selected");
}

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