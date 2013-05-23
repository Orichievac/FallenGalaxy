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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.data.AdvancementData;
import fr.fg.client.data.AdvancementsData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;
import fr.fg.client.core.Client;
import fr.fg.client.core.Tutorial;
import fr.fg.client.core.UpdateManager;

public class AdvancementDialog extends JSDialog implements ClickListener, SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int REINITIALISATION = 2, VIEW_UPGRADES = 1, VIEW_ACQUIRED = 0;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTabbedPane tabbedPane;
	
	private JSLabel civilizationPointsLabel;
	
	private JSButton[] buyButtons;
	
	private JSButton resetBt;
	
	private JSLabel[] advancementLabels, costLabels;
	
	private HTMLPanel[] descriptionPanels;
	
	private JSRowLayout[] layouts;
	
	private JSRowLayout advancementsLayout;
	
	private AdvancementsData advancements;
	
	private JSScrollPane scrollPane;
	
	private Action currentAction;
	
	private int availablePoints;
	
	private int currentAdvancement; //Pour la confirmation d'affectation de point
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AdvancementDialog() {
		super("Civilisation", true, true, true);
		
		availablePoints = 0;
		
		tabbedPane = new JSTabbedPane();
		tabbedPane.addTab("Avancées acquises");
		tabbedPane.addTab("Amélioration");
		tabbedPane.addTab("Réinitialisation");
		tabbedPane.setPixelWidth(420);
		tabbedPane.addSelectionListener(this);
		
		civilizationPointsLabel = new JSLabel("");
		civilizationPointsLabel.setPixelWidth(410);
	
	advancementsLayout = new JSRowLayout();
	
	DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
	
	buyButtons = new JSButton[10];
	descriptionPanels = new HTMLPanel[10];
	advancementLabels = new JSLabel[10];
	costLabels = new JSLabel[10];
	layouts = new JSRowLayout[10];
	
	for (int i = 0; i < 10; i++) {
		advancementLabels[i] = new JSLabel();
		advancementLabels[i].setPixelWidth(263); // TODO 280 - scroll bar width
		
		costLabels[i] = new JSLabel("<div class=\"right title\">" +
			AdvancementData.getCost(i) + " points&nbsp;</div>");
		costLabels[i].setPixelWidth(88);
		
		JSButton buyBt = new JSButton();
		buyBt.setPixelWidth(31);
		buyBt.addStyleName("iconAdd");
		buyBt.setToolTipText("Cliquez pour améliorer au niveau suivant.", 200);
		buyBt.addClickListener(this);
		
		buyButtons[i] = buyBt;
		
		HTMLPanel descriptionPanel = new HTMLPanel(
			"<div style=\"padding: 0 10px;\">" +
			"<div style=\"padding-bottom: 2px;\">" +
			dynamicMessages.getString("advancement" + i + "Desc") + "</div>" +
			"<div class=\"emphasize\">Niveau actuel : -</div>" +
			"<div class=\"emphasize\">Prochain niveau : -</div></div>");
		descriptionPanel.setWidth("100%");
		OpenJWT.setElementFloat(descriptionPanel.getElement(), "left");
		
		descriptionPanels[i] = descriptionPanel;
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(JSRowLayout.createHorizontalSeparator(10));
		layout.addComponent(advancementLabels[i]);
		layout.addComponent(costLabels[i]);
		layout.addComponent(buyBt);
		layout.addRow();
		layout.addComponent(descriptionPanel);
		layout.addRowSeparator(16);

		
		layouts[i] = layout;
		
		advancementsLayout.addComponent(layout);
		advancementsLayout.addRow();
	
		

	}
	
	
	
	
	scrollPane = new JSScrollPane();
	scrollPane.setView(advancementsLayout);
	scrollPane.setPixelSize(420, 270);
	
	JSRowLayout layout = new JSRowLayout();
	layout.addComponent(tabbedPane);
	layout.addRow();
	layout.addComponent(JSRowLayout.createHorizontalSeparator(10));
	layout.addComponent(civilizationPointsLabel);
	layout.addRowSeparator(3);
	layout.addComponent(scrollPane);
	
	resetBt = new JSButton("Réinitialisation");
	resetBt.setPixelWidth(120);
	resetBt.addStyleName("iconReset");
	resetBt.setToolTipText("Cliquez pour réinitialiser vos points de civilisation sauf Terraformation et Programme de colonisation", 200);
	resetBt.addClickListener(this);
	layout.addComponent(resetBt);
	
	setComponent(layout);
	centerOnScreen();
}

// --------------------------------------------------------- METHODES -- //

@Override
public void setVisible(boolean visible) {
	super.setVisible(visible);
	
	if (visible) {
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_CIVILIZATION);
		
		advancementsLayout.update();
		scrollPane.update();
	}
}

public void setAdvancements(AdvancementsData advancements) {
	this.advancements = advancements;
	
	int availablePoints = 5 * Client.getInstance().getProgressBar().getPlayerLevel();
	
	for (int i = 0; i < advancements.getAdvancementsCount(); i++) {
		AdvancementData advancement = advancements.getAdvancementAt(i);
		availablePoints -= AdvancementData.getCost(advancement.getType()) *
			advancement.getLevel();
	}
	
	this.availablePoints = availablePoints;
	
	updateUI();
}

public int getAdvancementLevel(int type) {
	for (int i = 0; i < advancements.getAdvancementsCount(); i++) {
		AdvancementData advancement = advancements.getAdvancementAt(i);
		if (advancement.getType() == type)
			return advancement.getLevel();
	}
	
	return 0;
}


public void onClick(Widget sender) {
	currentAdvancement=0;
	for (int i = 0; i < buyButtons.length; i++)
	{
		currentAdvancement = i;
		if (sender == buyButtons[i]) {
			if (currentAction != null && currentAction.isPending())
				return;
			JSOptionPane.showMessageDialog(
					"Voulez-vous vraiment affecter ces points de civilisation à cette amélioration?",
					"Confirmation",
					JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.OK_OPTION) {
								
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("type", String.valueOf(currentAdvancement));
								
								currentAction = new Action("setadvancement", params, UpdateManager.UPDATE_CALLBACK);	
							}
						}
					}
					);
			break;
		}
	}

	if (sender == resetBt){
		if (currentAction != null && currentAction.isPending())
			return;
		JSOptionPane.showMessageDialog(
				"Voulez-vous vraiment réinitialiser vos points de civilisations (sauf Terraformation et Programme de colonisation)?",
				"Confirmation",
				JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
				
						if ((Integer) option == JSOptionPane.OK_OPTION) {
							
							currentAction = new Action("deletepoints", new HashMap<String, String>(), UpdateManager.UPDATE_CALLBACK);
							
						}
					}
              }
		);
	}
				
}
	
				


public void selectionChanged(Widget sender, int newValue, int oldValue) { 
	if (sender == tabbedPane) 
		updateUI(); 
}

public void updateUI() {

	
	switch (tabbedPane.getSelectedIndex()) {
	case VIEW_UPGRADES:
		scrollPane.setVisible(true);
		resetBt.setVisible(false);
	case VIEW_ACQUIRED:
		scrollPane.setVisible(true);
		resetBt.setVisible(false);
		
		break;
	case REINITIALISATION:
		scrollPane.setVisible(false);
		resetBt.setVisible(true);
		break;
	}
	
	civilizationPointsLabel.setText("Points de civilisation disponibles : " +
			"<span class=\"emphasize\">" + availablePoints + "</span>");
	
	StaticMessages messages = GWT.create(StaticMessages.class);
	DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
	
	for (int i = 0; i < descriptionPanels.length; i++) {
		String currentValue, nextValue, unit = "";
		
		int level = getAdvancementLevel(i);
		boolean visible = tabbedPane.getSelectedIndex() != VIEW_ACQUIRED || level > 0;
		String visibility = tabbedPane.getSelectedIndex() == VIEW_ACQUIRED ? "hidden" : "";
		
		layouts[i].setVisible(visible);
		costLabels[i].getElement().getStyle().setProperty("visibility", visibility);
		buyButtons[i].getElement().getStyle().setProperty("visibility", visibility);
		
		switch (i) {
		case AdvancementData.TYPE_FLEETS_COST:
			int currentProd = 0;
			if (level > 0)
				currentProd = -(1000 - (int) Math.round(Math.pow(.95, level) * 1000));
			int nextProd = -(1000 - (int) Math.round(Math.pow(.95, level + 1) * 1000));
			unit = "&nbsp;<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\" class=\"resource credits\"/>";
			currentValue = formatDecimal(currentProd) + "%";
			nextValue = formatDecimal(nextProd) + "%";
			break;
		case AdvancementData.TYPE_TRAINING_MAX_LEVEL:
			currentProd = level + 3;
			nextProd = level + 4;
			currentValue = messages.level(currentProd).toLowerCase();
			nextValue = messages.level(nextProd).toLowerCase();
			break;
		case AdvancementData.TYPE_COLONIZATION_POINTS:
			unit = messages.advancementColonizationPoints();
			currentProd = 0;
			if (level > 0)
				currentProd = level;
			nextProd = level + 1;
			currentValue = String.valueOf(currentProd);
			nextValue = String.valueOf(nextProd);
			break;
		case AdvancementData.TYPE_BUILDING_LAND:
			unit = messages.advancementBuildings();
			currentProd = 0;
			if (level > 0)
				currentProd = level;
			nextProd = level + 1;
			currentValue = "+" + currentProd;
			nextValue = "+" + nextProd;
			break;
		case AdvancementData.TYPE_POPULATION_GROWTH:
			currentProd = 0;
			if (level > 0)
				currentProd = ((int) Math.round(Math.pow(1.06, level) * 1000)) - 1000;
			nextProd = ((int) Math.round(Math.pow(1.06, level + 1) * 1000)) - 1000;
			currentValue = "+" + formatDecimal(currentProd) + "%";
			nextValue = "+" + formatDecimal(nextProd) + "%";
			break;
		case AdvancementData.TYPE_MINING_UPGRADE:
			currentProd = 0;
			if (level > 0)
				currentProd = -(1000 - (int) Math.round(Math.pow(.95, level) * 1000));
			nextProd = -(1000 - (int) Math.round(Math.pow(.95, level + 1) * 1000));
			currentValue = formatDecimal(currentProd) + "%";
			nextValue = formatDecimal(nextProd) + "%";
			break;
		case AdvancementData.TYPE_BANK_TAX:
			currentProd = 0;
			if (level > 0)
				currentProd = -(1000 - (int) Math.round(Math.pow(.93, level) * 1000));
			nextProd = -(1000 - (int) Math.round(Math.pow(.93, level + 1) * 1000));
			unit = "&nbsp;<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\" class=\"resource credits\"/>";
			currentValue = formatDecimal(currentProd) + "%";
			nextValue = formatDecimal(nextProd) + "%";
			break;
		case AdvancementData.TYPE_TRADE_TAX:
			currentProd = 0;
			if (level > 0)
				currentProd = -(1000 - (int) Math.round(Math.pow(.94, level) * 1000));
			nextProd = -(1000 - (int) Math.round(Math.pow(.94, level + 1) * 1000));
			unit = "&nbsp;<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\" class=\"resource credits\"/>";
			currentValue = formatDecimal(currentProd) + "%";
			nextValue = formatDecimal(nextProd) + "%";
			break;
		case AdvancementData.TYPE_RESEARCH:
			currentProd = 0;
			if (level > 0)
				currentProd = -(1000 - (int) Math.round(Math.pow(.95, level) * 1000));
			nextProd = -(1000 - (int) Math.round(Math.pow(.95, level + 1) * 1000));
			unit = "&nbsp;<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\" class=\"resource research\"/>";
			currentValue = formatDecimal(currentProd) + "%";
			nextValue = formatDecimal(nextProd) + "%";
			break;
		case AdvancementData.TYPE_LINE_OF_SIGHT:
			unit = messages.advancementTiles();
			currentProd = 0;
			if (level > 0)
				currentProd = 2 * level;
			nextProd = 2 * level + 2;
			currentValue = "+" + currentProd;
			nextValue = "+" + (nextProd);
			break;
		default:
			currentValue = "";
			nextValue = "";
			break;
		}
		
		String value = "";
		
		switch (tabbedPane.getSelectedIndex()) {
		case VIEW_UPGRADES:
			value = currentValue + "&nbsp;►&nbsp;" + nextValue + "&nbsp;" + unit;
			
			advancementLabels[i].setText("<span class=\"title\">" +
				dynamicMessages.getString("advancement" + i) +
				" niv. " + (level + 1) + "</span>");
			break;
		case VIEW_ACQUIRED:
			value = currentValue + "&nbsp;" + unit;
			
			advancementLabels[i].setText("<span class=\"title\">" +
				dynamicMessages.getString("advancement" + i) +
				(level > 0 ? " niv. " + level : "") + "</span>");
			break;
		}
		
		descriptionPanels[i].getElement().setInnerHTML(
			"<div style=\"padding: 0 25px 0 13px; clear: both;\">" +
			"<div style=\"padding-bottom: 2px;\">" +
			dynamicMessages.getString("advancement" + i + "Desc") + "</div>" +
			"<div class=\"emphasize\">" + 
			"<div style=\"float: left; width: 200px;\">" +
			dynamicMessages.getString("advancement" + i + "Prod") +
			"</div><div class=\"right\">" + value + "</div></div>" +
			"</div>");
		
		layouts[i].update();
	}
	
	advancementsLayout.update();
	scrollPane.update();
	
}

// ------------------------------------------------- METHODES PRIVEES -- //

private String formatDecimal(int value) {
	if (value == 0)
		return "0";
	String tmp = String.valueOf(value);
	return tmp.substring(0, tmp.length() - 1) + "," + tmp.substring(tmp.length() - 1);
}
}