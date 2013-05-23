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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import fr.fg.client.animation.ResourcesUpdater;
import fr.fg.client.animation.ToolTipTextUpdater;
import fr.fg.client.core.selection.Selection;
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StructureData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class SelectionInfo extends BaseWidget implements SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Element backgroundElement, infoElement;
	
	private ArrayList<TimerHandler> updaters;
	
	private final static String[] BUILDINGS_ORDER = {
    	"civilian_infrastructures",
    	"corporations",
    	"trade_port",
    	"extractor_center",
    	"refinery",
    	"exploitation0",
    	"exploitation1",
    	"exploitation2",
    	"exploitation3",
    	"storehouse",
    	"factory",
    	"spaceship_yard",
    	"defensive_deck",
    	"laboratory",
    	"research_center"
	};
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SelectionInfo() {
		this.updaters = new ArrayList<TimerHandler>();
		
		getElement().setId("selectionInfo");
		getElement().setAttribute("unselectable", "on");
		
		backgroundElement = DOM.createDiv();
		backgroundElement.setAttribute("unselectable", "on");
		getElement().appendChild(backgroundElement);
		
		infoElement = DOM.createDiv();
		infoElement.setAttribute("unselectable", "on");
		infoElement.setClassName("info");
		getElement().appendChild(infoElement);

		
		setVisible(false);
		
		SelectionManager.getInstance().addSelectionListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void selectionChanged(Selection newSelection,
			Selection oldSelection) {
		update();
	}
	
	public void update() {
		// Supprime les timers en cours d'exécution
		for (TimerHandler updater : updaters) {
			TimerManager.unregister(updater);
			updater.destroy();
		}
		updaters.clear();
		
		infoElement.setInnerHTML("");
		setVisible(false);
		
		switch (SelectionManager.getInstance().getSelection().getType()) {
		case Selection.TYPE_SYSTEM:
			// Sélection d'un système
			showSystemInfo();
			break;
		case Selection.TYPE_SPACE_STATION:
			// Selection d'une station spatiale
			showSpaceStationInfo();
			break;
		case Selection.TYPE_STRUCTURE:
			// Sélection d'une structure
			showStructureInfo();
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void showSystemInfo() {
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		PlayerStarSystemData systemData = SelectionTools.getSelectedSystem();
		
		// Crédits disponibles
		double corporationsProduction =
			Client.getInstance().getResourcesManager().getCurrentPopulation(systemData.getId()) *
			BuildingData.getProduction(BuildingData.CORPORATIONS, systemData);
		double production = corporationsProduction * systemData.getProductionModifier();
		double population = Client.getInstance().getResourcesManager(
		).getCurrentPopulation(systemData.getId());
		int maxPopulation = (int) BuildingData.getProduction(
				BuildingData.CIVILIAN_INFRASTRUCTURES, systemData);
		String id = ToolTipTextUpdater.generateId();
		ResourcesUpdater creditsUpdater = new ResourcesUpdater(
			(String) null, id, Client.getInstance().getResourcesManager(),
			systemData.getId(), ResourcesUpdater.RESOURCE_CREDITS, true);
		
		OutlineText text = TextManager.getText("<div class=\"infoRow systemResStock" +
				((population < maxPopulation )? " growing" : "" )+ // Jaune si la population augmente
				"\" unselectable=\"on\">" +
			"<img class=\"resource credits\" src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" unselectable=\"on\"/>&nbsp;" +
			"<b id=\"" + id + "\" unselectable=\"on\">" + creditsUpdater.getValue() + "</b>" +
			"&nbsp;<span class=\"production\" unselectable=\"on\">+" +
			(int) Math.floor(production) + "</span></div>");
		text.getElement().getStyle().setProperty("position", "static");
		
		TimerManager.register(creditsUpdater, TimerManager.SECOND_UNIT);
		updaters.add(creditsUpdater);
		
		int inUse = 0;
		for (int i = 0; i < 5; i++)
			inUse += systemData.getBuildingsCount(BuildingData.CIVILIAN_INFRASTRUCTURES, i);
		for (int i = 0; i < systemData.getBuildsCount(); i++)
			inUse += systemData.getBuildAt(i).getType().equals(BuildingData.CIVILIAN_INFRASTRUCTURES) &&
				systemData.getBuildAt(i).getLevel() == 0  ? 1 : 0;
		
        double dayCorpoProduction = ( (10 * corporationsProduction)) / 10. * 1440;
		id = ToolTipTextUpdater.generateId();
		ToolTipManager.getInstance().register(text.getElement(),
			"<div class=\"title\"><span id=\"" + id + "\"></span> Crédits</div>" +
			"<div>" + dynamicMessages.corporations() + " <span class=\"production\">+" +
			(((int) (10 * corporationsProduction)) / 10.) + "</span> / min</div>" +
			"<div class=\"dayProduction\"> Production par jour : " + Formatter.formatNumber(dayCorpoProduction) + "</div>" +
			(systemData.getProductionModifier() > 1 ? "<div>Station spatiale " + 
			" <span class=\"production\">+" + (((int) (10 * corporationsProduction *
			(systemData.getProductionModifier() - 1))) / 10.) + "</span> / min</div>": "") +
			"<div class=\"dayProduction\"Production par jour : " + (((int) (10 * corporationsProduction)) / 10.) * 1440 + "</div>" +
			"<div class=\"emphasize\">Espace disponible : " +
			(systemData.getAvailableSpace() - inUse) + " / " +
			systemData.getAvailableSpace() + "</div>" +
			((population < maxPopulation )? "<div class=\"growing\">Votre population augmente !</div>" : "" ));
		
		creditsUpdater = new ResourcesUpdater(
			text.getElement(), id, Client.getInstance().getResourcesManager(),
			systemData.getId(), ResourcesUpdater.RESOURCE_CREDITS, false);
		
		TimerManager.register(creditsUpdater, TimerManager.SECOND_UNIT);
		updaters.add(creditsUpdater);
		
		infoElement.appendChild(text.getElement());
		
		// Ressources disponibles sur le système
		int totalRessources = 0;
		for (int i = 0; i < 5; i++) {
			totalRessources += systemData.getResourceAt(i);
		}
		long stock = (long) BuildingData.getProduction(BuildingData.STOREHOUSE, systemData);
		for (int i = 0; i < 4; i++) {
			double exploitationsProduction =
				BuildingData.getProduction(BuildingData.EXPLOITATION + i, systemData);
			double refineriesProduction =
				BuildingData.getProduction(BuildingData.REFINERY, systemData);
			production = exploitationsProduction * refineriesProduction *
				systemData.getProductionModifier();
			
			id = ToolTipTextUpdater.generateId();
			ResourcesUpdater updater = new ResourcesUpdater((String) null, id,
				Client.getInstance().getResourcesManager(),
				systemData.getId(), i, true);
			
			text = TextManager.getText("<div class=\"infoRow systemResStock" +
					(( totalRessources >= stock )? " overflow" : "" )+ // Texte en rouge si trop de stock
					"\" unselectable=\"on\">" +
				"<img class=\"resource r" + i + "\" src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" unselectable=\"on\"/>&nbsp;" +
				"<b id=\"" + id + "\" unselectable=\"on\">" + updater.getValue() + "</b>" +
				"&nbsp;<span class=\"production\" unselectable=\"on\">+" +
				(int) Math.floor(production) + "</span></div>");
			
			TimerManager.register(updater, TimerManager.SECOND_UNIT);
			
			
			inUse = 0;
			for (int j = 0; j < 5; j++)
				inUse += systemData.getBuildingsCount(BuildingData.EXPLOITATION + i, j);
			for (int j = 0; j < systemData.getBuildsCount(); j++)
				inUse += systemData.getBuildAt(j).getType().equals(BuildingData.EXPLOITATION + i) &&
					systemData.getBuildAt(j).getLevel() == 0 ? 1 : 0;
			
			double dayExplProduction = (10 * exploitationsProduction) / 10. * 1440; // 1440 = 60 * 24 (Nombre de minute par heure multiplié par nombre d'heures par jour)
			
			double dayRaffProduction = ((int) (10 * exploitationsProduction * (refineriesProduction - 1))) / 10. * 1440;
			
			double dayTotalProduction = dayExplProduction + dayRaffProduction;
			
			id = ToolTipTextUpdater.generateId();
			ToolTipManager.getInstance().register(text.getElement(),
				"<div class=\"title\"><span id=\"" + id + "\"></span> " +
				dynamicMessages.getString("resource" + i) + "</div>" +
				"<div>" + dynamicMessages.getString("exploitation" + i) + " <span class=\"production\">+" +
				(((int) (10 * exploitationsProduction)) / 10.) + "</span> / min</div>" +
				 
				(refineriesProduction > 1 ? "<div>" + dynamicMessages.refinery() + " " +
				" <span class=\"production\">+" + (((int) (10 * exploitationsProduction *
				(refineriesProduction - 1))) / 10.) + "</span> / min</div>" +
				"<div class=\"dayProduction\">Production par jour : " + Formatter.formatNumber(dayTotalProduction) + "</div>" 
				: "<div class=\"dayProduction\">Production par jour : " + Formatter.formatNumber(dayExplProduction) + "</div>") +
				(systemData.getProductionModifier() > 1 ? "<div>Station spatiale " +
				" <span class=\"production\">+" + (((int) (10 * exploitationsProduction *
				refineriesProduction * (systemData.getProductionModifier() - 1))) / 10.) +
				"</span> / min</div>" : "") +
				"<div class=\"emphasize\">Gisements disponibles : " +
				(systemData.getAvailableResource(i) - inUse) + " / " +
				systemData.getAvailableResource(i) + "</div>");
			
			updater = new ResourcesUpdater(text.getElement(), id,
				Client.getInstance().getResourcesManager(),
				systemData.getId(), i, false);
			
			TimerManager.register(updater, TimerManager.SECOND_UNIT);
			updaters.add(updater);
			
			infoElement.appendChild(text.getElement());
		}
		
		// Nombre de bâtiments construits
		
		int buildingsCount = 0;
		for (String type : BUILDINGS_ORDER)
			for (int level = 0; level < 5; level++)
				buildingsCount += systemData.getBuildingsCount(type, level);

		
		text = TextManager.getText("<div class=\"infoRow systemResStock"+ 
				((buildingsCount >= systemData.getBuildingLand())? " overflow" : "" ) +
				"\" unselectable=\"on\">" +
				"<img class=\"resource b\" src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" unselectable=\"on\"/>&nbsp;" +
						buildingsCount+"/"+systemData.getBuildingLand()+"</div>");
		infoElement.appendChild(text.getElement());
		
		id = ToolTipTextUpdater.generateId();
		ToolTipManager.getInstance().register(text.getElement(),
			"<div class=\"title\"><span id=\"" + id + "\"></span> Bâtiments construits</div>");
		
		
		backgroundElement.setClassName("star");
		backgroundElement.getStyle().setProperty("backgroundPosition",
				(int) Math.floor(-120 * (systemData.getStarImage() - 1)) + "px 0");
		
		setVisible(true);
	}
	
	private void showSpaceStationInfo() {
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		SpaceStationData spaceStationData = SelectionTools.getSelectedSpaceStation();
		
		// Crédits déposés sur la station
		String credits;
		if (spaceStationData.getLevel() < 5)
			credits = Formatter.formatNumber(spaceStationData.getCredits(), true) + "&nbsp;/&nbsp;" +
				Formatter.formatNumber(SpaceStationData.COST_LEVELS[spaceStationData.getLevel() + 1][4], true);
		else
			credits = Formatter.formatNumber(SpaceStationData.COST_LEVELS[5][4], true) + "&nbsp;/&nbsp;" +
				Formatter.formatNumber(SpaceStationData.COST_LEVELS[5][4], true);
		
		OutlineText text = TextManager.getText("<div class=\"infoRow\" " +
				"unselectable=\"on\"><img class=\"resource credits\" " +
				"src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\"/>&nbsp;" + credits + "</div>");
		
		infoElement.appendChild(text.getElement());
		
		ToolTipManager.getInstance().register(text.getElement(),
			"<div class=\"title\">" + Formatter.formatNumber(
			spaceStationData.getCredits()) + " Crédits</div>");
		
		// Ressources déposées sur la station
		for (int i = 0; i < 3; i++) {
			String resources;
			if (spaceStationData.getLevel() < 5)
				resources = Formatter.formatNumber(spaceStationData.getResourceAt(i), true) + "&nbsp;/&nbsp;" +
					Formatter.formatNumber(SpaceStationData.COST_LEVELS[spaceStationData.getLevel() + 1][i], true);
			else
				resources = Formatter.formatNumber(SpaceStationData.COST_LEVELS[5][i], true) + "&nbsp;/&nbsp;" +
					Formatter.formatNumber(SpaceStationData.COST_LEVELS[5][i], true);
			
			text = TextManager.getText("<div class=\"infoRow\" " +
				"unselectable=\"on\"><img class=\"resource r" + i + "\" " +
				"src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\"/>&nbsp;" + resources + "</div>");
			
			infoElement.appendChild(text.getElement());
			
			ToolTipManager.getInstance().register(text.getElement(),
				"<div class=\"title\">" + Formatter.formatNumber(
				spaceStationData.getResourceAt(i)) + " " +
				dynamicMessages.getString("resource" + i) + "</div>");
		}
		
		backgroundElement.setClassName("spaceStation");
		backgroundElement.getStyle().setProperty("backgroundPosition", "");
		
		setVisible(true);
	}
	
	private void showStructureInfo() {
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		StructureData structureData = SelectionTools.getSelectedStructure();
		
		// Crédits déposés sur la structure
		String id = ToolTipTextUpdater.generateId();
		ResourcesUpdater creditsUpdater = new ResourcesUpdater(
			(String) null, id, Client.getInstance().getResourcesManager(),
			0, ResourcesUpdater.RESOURCE_CREDITS, true);
		
		OutlineText text = TextManager.getText("<div class=\"infoRow\" unselectable=\"on\">" +
			"<img class=\"resource credits\" src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" unselectable=\"on\"/>&nbsp;" +
			"<b id=\"" + id + "\" unselectable=\"on\">" + creditsUpdater.getValue() + "</b></div>");
		text.getElement().getStyle().setProperty("position", "static");
		
		TimerManager.register(creditsUpdater, TimerManager.SECOND_UNIT);
		updaters.add(creditsUpdater);
		
		id = ToolTipTextUpdater.generateId();
		ToolTipManager.getInstance().register(text.getElement(),
			"<div class=\"title\"><span id=\"" + id + "\"></span> Crédits</div>");
		
		creditsUpdater = new ResourcesUpdater(
			text.getElement(), id, Client.getInstance().getResourcesManager(),
			0, ResourcesUpdater.RESOURCE_CREDITS, false);
		
		TimerManager.register(creditsUpdater, TimerManager.SECOND_UNIT);
		updaters.add(creditsUpdater);
		
		infoElement.appendChild(text.getElement());
		
		// Ressources déposées sur la structure
		for (int i = 0; i < 4; i++) {
			String resources = Formatter.formatNumber(
				structureData.getResourceAt(i), true);
			
			text = TextManager.getText("<div class=\"infoRow\" " +
				"unselectable=\"on\"><img class=\"resource r" + i + "\" " +
				"src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"unselectable=\"on\"/>&nbsp;" + resources + "</div>");
			
			infoElement.appendChild(text.getElement());
			
			ToolTipManager.getInstance().register(text.getElement(),
				"<div class=\"title\">" + Formatter.formatNumber(
				structureData.getResourceAt(i)) + " " +
				dynamicMessages.getString("resource" + i) + "</div>");
		}
		
		backgroundElement.setClassName("structure");
		backgroundElement.getStyle().setProperty("backgroundPosition", "");
		
		setVisible(true);
	}
}
