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

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.ProductData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.StructureModuleData;
import fr.fg.client.data.WeaponGroupData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.SelectionListener;

public class BuildShipDialog extends JSDialog implements ClickListener,
		SelectionListener, KeyboardListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private PlayerStarSystemData systemData;
	
	private StructureData structureData;
	
	private int slot;
	
	private int spaceshipYardLevel;
	
	private ResourcesManager resourcesManager;
	
	private ResearchManager researchManager;
	
	private ProductsManager productsManager;
	
	private JSComboBox classesComboBox;
	
	private JSButton okBt, cancelBt, maxQuantityBt;
	
	private JSList shipsList;
	
	private JSTextField quantityField;
	
	private JSLabel quantityLabel, creditsCostLabel, resourcesCostLabel;
	
	private Action currentAction;
	
	private int[] shipClasses;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BuildShipDialog(ResourcesManager resourcesManager,
			ResearchManager researchManager, ProductsManager productsManager) {
		super("Construction de vaisseaux", true, true, true);
		
		this.resourcesManager = resourcesManager;
		this.researchManager = researchManager;
		this.productsManager = productsManager;
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		// Filtre des vaisseaux par classes
		ArrayList<String> classes = new ArrayList<String>();
		classes.add("Tous");
		for (int i = 1; i < 8; i++)
			classes.add(dynamicMessages.getString("shipClasses" + i));
		
		classesComboBox = new JSComboBox();
		classesComboBox.setPixelWidth(120);
		classesComboBox.setItems(classes);
		classesComboBox.addSelectionListener(this);
		
		JSLabel classesLabel = new JSLabel("&nbsp;Filtrer par classe&nbsp;");
		classesLabel.setAlignment(JSLabel.ALIGN_RIGHT);
		
		// Liste des vaisseaux
		shipsList = new JSList();
		shipsList.setPixelSize(400, 204);
		shipsList.addSelectionListener(this);
		
		quantityLabel = new JSLabel("&nbsp;Quantité");
		quantityLabel.setPixelWidth(240);
		
		quantityField = new JSTextField();
		quantityField.setPixelWidth(100);
		quantityField.addKeyboardListener(this);
		quantityField.setToolTipText(messages.transferLimitHelp());
		quantityField.setEditable(false);
		
		maxQuantityBt = new JSButton("Max");
		maxQuantityBt.setPixelWidth(60);
		maxQuantityBt.addClickListener(this);
		
		JSLabel totalCostLabel = new JSLabel("&nbsp;Prix total");
		totalCostLabel.setPixelWidth(100);
		
		creditsCostLabel = new JSLabel("");
		creditsCostLabel.setPixelWidth(300);
		creditsCostLabel.setAlignment(JSLabel.ALIGN_RIGHT);

		resourcesCostLabel = new JSLabel("");
		resourcesCostLabel.setPixelWidth(400);
		resourcesCostLabel.setAlignment(JSLabel.ALIGN_RIGHT);
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(classesLabel);
		layout.addComponent(classesComboBox);
		layout.addRow();
		layout.addComponent(shipsList);
		layout.addRowSeparator(5);
		layout.addComponent(quantityLabel);
		layout.addComponent(quantityField);
		layout.addComponent(maxQuantityBt);
		layout.addRow();
		layout.addComponent(totalCostLabel);
		layout.addComponent(creditsCostLabel);
		layout.addRow();
		layout.addComponent(resourcesCostLabel);
		layout.addRowSeparator(30);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void show(StructureData structureData, int slot) {
		this.structureData = structureData;
		this.slot = slot;
		
		// Filtre des vaisseaux par classes
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		shipClasses = new int[8];
		ArrayList<String> classes = new ArrayList<String>();
		classes.add("Tous");
		for (int i = 1; i < 8; i++) {
			if (i == 7 || structureData.getModuleLevel(
					StructureModuleData.TYPE_DECK_FIGHTER + i - 1) > 0) {
				shipClasses[classes.size()] = i;
				classes.add(dynamicMessages.getString("shipClasses" + i));
			}
		}
		
		classesComboBox.setItems(classes);
		
		setVisible(true);
		
		updateShipsList();
	}
	
	public void show(PlayerStarSystemData systemData, int slot) {
		this.systemData = systemData;
		this.slot = slot;
		
		spaceshipYardLevel = -1;
		for (int i = 4; i >= 0; i--)
			if (systemData.getBuildingsCount(BuildingData.SPACESHIP_YARD, i) > 0) {
				spaceshipYardLevel = i;
				break;
			}
		
		// Filtre des vaisseaux par classes
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		shipClasses = new int[8];
		ArrayList<String> classes = new ArrayList<String>();
		classes.add("Tous");
		for (int i = 1; i < 8; i++)
			if (spaceshipYardLevel >= BuildingData.getRequiredSpaceshipYardLevel(i)) {
				shipClasses[classes.size()] = i;
				classes.add(dynamicMessages.getString("shipClasses" + i));
			}
		
		classesComboBox.setItems(classes);
		
		setVisible(true);
		
		updateShipsList();
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			quantityField.setFocus(false);
			resourcesManager = null;
			classesComboBox.removeSelectionListener(this);
			classesComboBox = null;
			okBt.removeClickListener(this);
			okBt = null;
			cancelBt.removeClickListener(this);
			cancelBt = null;
			maxQuantityBt.removeClickListener(this);
			maxQuantityBt = null;
			shipsList.removeSelectionListener(this);
			shipsList = null;
			quantityField.removeKeyboardListener(this);
			quantityField = null;
			quantityLabel = null;
			creditsCostLabel = null;
			resourcesCostLabel = null;
			currentAction = null;
			systemData = null;
			structureData = null;
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			if (getQuantity() > getMaxQuantity()) {
				JSOptionPane.showMessageDialog(
					"Vous n'avez pas les ressources nécessaires pour lancer " +
					"la construction des vaisseaux.",
					"Erreur",
					JSOptionPane.OK_OPTION,
					JSOptionPane.WARNING_MESSAGE, null);
				return;
			}
			
			int shipId = 0;
			if (shipsList.getSelectedIndex() != -1)
				shipId = ((ShipUI) shipsList.getSelectedItem()).shipId;
			
			HashMap<String, String> params = new HashMap<String, String>();
			if (structureData != null)
				params.put("structure", String.valueOf(structureData.getId()));
			else
				params.put("system", String.valueOf(systemData.getId()));
			params.put("slot", String.valueOf(slot));
			params.put("id", String.valueOf(shipId));
			params.put("count", String.valueOf(getQuantity()));
			
			if (structureData != null)
				currentAction = new Action("structure/buildships", params, this);
			else
				currentAction = new Action("systems/buildships", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
		} else if (sender == maxQuantityBt) {
			quantityField.setText(String.valueOf(getMaxQuantity()));
			updateCost();
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == shipsList) {
			// Affiche le détail du vaisseau sélectionné (armement et capacités)
			quantityField.setText("");
			
			if (newValue != -1) {
				if (oldValue != -1) {
					((ShipUI) shipsList.getItemAt(oldValue)).showDetails(false);
					shipsList.setItemAt(shipsList.getItemAt(oldValue), oldValue);
				}
				((ShipUI) shipsList.getItemAt(newValue)).showDetails(true);
				shipsList.setItemAt(shipsList.getItemAt(newValue), newValue);
				shipsList.update();
				
				quantityField.setEditable(newValue != 0);
				quantityField.setFocus(newValue != 0);
			} else {
				quantityField.setEditable(false);
				quantityField.setFocus(false);
			}
			
			updateCost();
		} else if (sender == classesComboBox) {
			// Filtre par classes
			updateShipsList();
		}
	}

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		// Sans effet
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// Sans effet
	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		updateCost();
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		setVisible(false);
		
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private long getQuantity() {
		String text = quantityField.getText();
		
		if (text.length() == 0)
			return 0;
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		text = text.replace(messages.unitK().toLowerCase(), "000"); //$NON-NLS-1$
		text = text.replace(messages.unitK().toUpperCase(), "000"); //$NON-NLS-1$
		text = text.replace(messages.unitM().toLowerCase(), "000000"); //$NON-NLS-1$
		text = text.replace(messages.unitM().toUpperCase(), "000000"); //$NON-NLS-1$
		text = text.replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		long quantity = 0;
		try {
			quantity = Long.parseLong(text);
		} catch (Exception e) {
			return 0;
		}
		
		if (quantity < 0)
			return 0;
		if (quantity > 1000000000)
			return 1000000000;
		return quantity;
	}
	
	private long getMaxQuantity() {
		int shipId = 0;
		if (shipsList.getSelectedIndex() != -1)
			shipId = ((ShipUI) shipsList.getSelectedItem()).shipId;
		
		if (shipId == 0) {
			return 0;
		} else {
			ShipData ship = ShipData.SHIPS[shipId];
			
			long maxQuantity = Long.MAX_VALUE;
			
			// Coût en crédits
			long currentCredits = resourcesManager.getCurrentCredits();
			int creditsCost = (int) Math.ceil(
				ship.getCost()[4] * getCreditsCostCoef());
			
			maxQuantity = Math.min(maxQuantity, currentCredits / creditsCost);
			
			// Coût en ressources
			for (int i = 0; i < 4; i++) {
				if (ship.getCost()[i] > 0) {
					long currentResource = getCurrentResource(i);
					
					maxQuantity = Math.min(maxQuantity,
							currentResource / ship.getCost()[i]);
				}
			}
			
			return maxQuantity;
		}
	}
	
	private void updateCost() {
		int shipId = 0;
		if (shipsList.getSelectedIndex() != -1)
			shipId = ((ShipUI) shipsList.getSelectedItem()).shipId;
		
		if (shipId == 0) {
			creditsCostLabel.setText("");
			resourcesCostLabel.setText("");
			quantityLabel.setText("&nbsp;Quantité");
		} else {
			if (getQuantity() > 0) {
				ShipData ship = ShipData.SHIPS[shipId];
				
				// Coût en crédits
				long creditsCost = (int) Math.ceil(ship.getCost()[4] *
						getQuantity() * getCreditsCostCoef());
				long currentCredits = resourcesManager.getCurrentCredits();
				
				creditsCostLabel.setText("<span class=\"small\"><b " +
					"style=\"color: " + (creditsCost <= currentCredits ?
					"#00c000" : "red") + ";\">" + Formatter.formatNumber(
					creditsCost) + "</b>&nbsp;<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"resource credits\"/> / <b>" +
					Formatter.formatNumber(currentCredits) + "</b></span>&nbsp;");
				
				// Coût en ressources
				String resources = "";
				int nbResources=0;
				for (int i = 0; i < 4; i++) {
					if (ship.getCost()[i] > 0) {
						nbResources++;
						long resourceCost = ship.getCost()[i] * getQuantity();
						long currentResource = getCurrentResource(i);
						if(nbResources==3) {
							resources+="<br/>";
						}
						resources += "&nbsp; <span class=\"small\"><b " +
							"style=\"color: " + (creditsCost <= currentCredits ?
							"#00c000" : "red") + ";\">" +
							Formatter.formatNumber(resourceCost) +
							"</b>&nbsp;<img src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\" class=\"resource " +
							"r" + i + "\"/> / <b>" + Formatter.formatNumber(
							currentResource) + "</b></span>&nbsp;";
					}
				}
				resourcesCostLabel.setText(resources);
			} else {
				creditsCostLabel.setText("");
				resourcesCostLabel.setText("");
			}
			
			quantityLabel.setText("&nbsp;Quantité (max " +
					Formatter.formatNumber(getMaxQuantity()) + ")");
		}
	}
	
	private void updateShipsList() {
		int filter = classesComboBox.getSelectedIndex();
		
		if (filter > 0)
			filter = shipClasses[filter];
		
		ArrayList<ShipUI> ships = new ArrayList<ShipUI>();
		ships.add(new ShipUI(0));
		
		for (int i = 0; i < ShipData.SHIPS.length; i++) {
			ShipData ship = ShipData.SHIPS[i];
			
			if (ship != null) {
				boolean available = false;
				for (int j = 1; j < 8; j++)
					if (shipClasses[j] == ship.getShipClass()) {
						available = true;
						break;
					}
				
				if (available) {
					// Vérifie que le joueur a développé les technologies nécessaires
					available = true;
					int[] requiredTechnologies = ShipData.SHIPS[i].getTechnologies();
					
					for (int idTechnology : requiredTechnologies)
						if (!researchManager.hasResearchedTechnology(idTechnology)) {
							available = false;
							break;
						}
					
					ProductData[] requiredProducts = ShipData.SHIPS[i].getRequiredProducts();
					HashMap<Integer, Integer> products = productsManager.getProducts();
					
					for (ProductData requiredProduct : requiredProducts) {
						Integer productCount = products.get(requiredProduct.getType());
						
						if (productCount == null || productCount < requiredProduct.getCount()) {
							available = false;
							break;
						}
					}
					
					if (available) {
						if (filter == 0)
							ships.add(new ShipUI(i));
						else if (ShipData.SHIPS[i].getShipClass() == filter)
							ships.add(new ShipUI(i));
					}
				}
			}
		}
		shipsList.setItems(ships);
		
		quantityField.setText("");
		updateCost();
	}
	
	private long getCurrentResource(int i) {
		if (structureData != null)
			return (long) structureData.getResourceAt(i);
		else
			return resourcesManager.getCurrentResource(systemData.getId(), i);
	}
	
	private double getCreditsCostCoef() {
		if (structureData != null)
			return 1;
		else
			return BuildingData.getProduction(BuildingData.TRADE_PORT, systemData);
	}
	
	private double getShipProduction() {
		if (structureData != null)
			return Math.pow(1.7, structureData.getModuleLevel(StructureModuleData.TYPE_HANGAR));
		else
			return BuildingData.getProduction(
				BuildingData.SPACESHIP_YARD, systemData);
	}
	
	private class ShipUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int shipId;
		
		private boolean showDetails;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ShipUI(int shipId) {
			this.shipId = shipId;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void showDetails(boolean showDetails) {
			this.showDetails = showDetails;
		}
		
		@Override
		public String toString() {
			if (shipId == 0) {
				return "<table unselectable=\"on\" class=\"action\" " +
					"cellspacing=\"0\">" +
					"<tr unselectable=\"on\">" +
					"<td unselectable=\"on\" valign=\"top\">" +
					"<div unselectable=\"on\" class=\"spaceship\" style=\"height: 25px;\"></div></td>" +
					"<td unselectable=\"on\" valign=\"center\" " +
					"style=\"padding: 2px 5px;\"><div class=\"title\" " +
					"unselectable=\"on\">Aucun vaisseau</div>" +
					"</td></tr></table>";
			}
			
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			DynamicMessages dynamicMessages =
				(DynamicMessages) GWT.create(DynamicMessages.class);
			
			ShipData ship = ShipData.SHIPS[shipId];
			
			// Cibles du vaisseau
			String targets = ""; //$NON-NLS-1$
			for (int j = 0; j < ship.getTargets().length; j++)
				targets += (j == 0 ? "" : ", ") + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
						"shipClasses" + ship.getTargets()[j]); //$NON-NLS-1$
			
			// Armement du vaisseau
			String weapons = ""; //$NON-NLS-1$
			if (showDetails) {
				for (WeaponGroupData weaponGroup : ship.getWeapons()) {
					// Dégâts
					String damage = "<b unselectable=\"on\">" +
						weaponGroup.getWeapon().getDamageMin() + "-" +
						weaponGroup.getWeapon().getDamageMax() + "</b>";
					
					weapons += "<div unselectable=\"on\">" + weaponGroup.getCount() + "x " + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
							"weapon" + weaponGroup.getIdWeapon()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
							"<img class=\"stat s-damage\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
							"images/misc/blank.gif\" unselectable=\"on\"/> " + damage + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			// Capacité
			String payload = "";
			if (ship.getPayload() > 0)
				payload = "<div>Capacité : " +
					Formatter.formatNumber(ship.getPayload(), true) + "</div>";
			
			String abilities = ""; //$NON-NLS-1$
			
			// Compétences du vaisseau
			if (showDetails) {
				for (AbilityData ability : ship.getAbilities()) {
					int[] requirements = ability.getRequirements();
					boolean available = true;
					
					for (int requirement : requirements)
						if (!researchManager.hasResearchedTechnology(requirement)) {
							available = false;
							break;
						}
					
					if (available) {
						abilities += "<div class=\"title\" unselectable=\"on\">" +
							"<div style=\"float: right; color: #1d97d5;\">" +
								(ability.isPassive() ?
								"Passif" : "<img class=\"stat s-small-cooldown\" src=\"" +
								Config.getMediaUrl() + //$NON-NLS-1$
								"images/misc/blank.gif\" unselectable=\"on\"/>" +
								ability.getCooldown()) +
							"</div>" + ability.getName() + "</div>" +
							"<div class=\"justify\">" + ability.getDesc(shipId) + "</div>";
					}
				}
			}
			
			// Protection
			String protection = "<b unselectable=\"on\">" + ship.getProtection() + "</b>";
			
			boolean available = true;
			
			// Coût en ressources
			String cost = "<div class=\"small right\" style=\"float: right;\" " +
					"unselectable=\"on\">";
			int creditsCost = (int) Math.ceil(ship.getCost()[4] * getCreditsCostCoef());
			long currentCredits = resourcesManager.getCurrentCredits();
			
			if (creditsCost > 0) {
				cost += "<b style=\"color: " + (creditsCost <=
				currentCredits ? "#00c000" : "red") + " !important;\">" +
				Formatter.formatNumber(creditsCost, true) + "</b>" +
				"&nbsp;<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"resource credits\"/><br/>";
				
				if (creditsCost > currentCredits)
					available = false;
			}
			
			for (int i = 0; i < 4; i++) {
				int resourceCost = ship.getCost()[i];
				long currentResource = getCurrentResource(i);
				
				if (resourceCost > 0) {
					cost += "<b style=\"color: " + (resourceCost <=
						currentResource ? "#00c000" : "red") +
						" !important;\">" + Formatter.formatNumber(
						resourceCost, true) + "</b>&nbsp;<img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" " +
						"class=\"resource r" + i + "\"/> ";
					
					if (resourceCost > currentResource)
						available = false;
				}
			}
			
			cost += "<br/>" + Formatter.formatDate(ship.getBuildTime() /
				getShipProduction()) + "&nbsp;<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"clock\"/>";
			
			cost += "</div>";
			
			String desc = cost + "<div class=\"title\" unselectable=\"on\">" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dynamicMessages.getString("ship" + shipId) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				" <span style=\"font-weight: normal;\" unselectable=\"on\">(" + dynamicMessages.getString("shipClass" + ship.getShipClass()) + ")</span></div>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<div class=\"small\" unselectable=\"on\">" +
				"<div unselectable=\"on\"><img class=\"stat s-struct\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
				"images/misc/blank.gif\" unselectable=\"on\"/> <b unselectable=\"on\">" + Formatter.formatNumber(ship.getHull()) + "</b> " + //$NON-NLS-1$ //$NON-NLS-2$
				"<img class=\"stat s-shield\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
				"images/misc/blank.gif\" unselectable=\"on\"/> " + protection + 
				" <img class=\"stat s-power\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
				"images/misc/blank.gif\" unselectable=\"on\"/><b>" + (ship.getShipClass() == ShipData.FIGHTER ? "1" : ship.getShipClass() == ShipData.CORVETTE ? "10"
				: ship.getShipClass() == ShipData.FRIGATE ? "100" : ship.getShipClass() == ShipData.DESTROYER ? "1000" :
				ship.getShipClass() == ShipData.DREADNOUGHT ? "10000" : ship.getShipClass() == ShipData.BATTLECRUISER ? "100000" :
				ship.getShipClass() == ShipData.FREIGHTER ? "0" : "") +"</b></div>" + //$NON-NLS-1$ //$NON-NLS-2$
				(targets.length() > 0 ? "<div unselectable=\"on\">" + messages.shipTargets(targets) + "</div>" : "") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				weapons + payload + abilities + "</div>";
			
			return "<table unselectable=\"on\"" + (available ? "" :
				" class=\"unavailable\"") +
				"cellspacing=\"0\" style=\"width: 100%;\">" +
				"<tr unselectable=\"on\">" +
				"<td unselectable=\"on\" valign=\"top\" style=\"width: 50px;\">" +
				"<div unselectable=\"on\" class=\"spaceship\" " +
				"style=\"background-position: -" +
				(shipId * 50) + "px 0\"></div></td>" +
				"<td unselectable=\"on\" valign=\"center\" " +
				"style=\"padding: 2px 5px;\">" +
				desc + "</td>" +
				"</tr></table>";
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
