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
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import fr.fg.client.animation.WidthUpdater;
import fr.fg.client.core.selection.Selection;
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AdvancementData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StructureData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;





public class ProgressBar extends BaseWidget implements SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		VIEW_PLAYER_XP = 1,
		VIEW_FLEET_XP = 2,
		VIEW_FLEET_POWER = 3,
		VIEW_FLEET_PAYLOAD = 4,
		VIEW_SYSTEM_INHABITANTS = 5,
		VIEW_SYSTEM_STOCK = 6,
		VIEW_SPACE_STATION_LEVEL = 7,
		VIEW_STRUCTURE_PAYLOAD = 8;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int view;
	
	private long playerXp;
	
	private int colonizationPoints;
	
	private long playerGeneratorsCount;
	
	private Element progressBarContainer, currentProgress;
	
	private OutlineText progressValue;
	
	private WidthUpdater updater;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ProgressBar() {
		this.view = VIEW_PLAYER_XP;
		this.playerXp = 0;
		this.colonizationPoints = 0;
		
		Element element = getElement();
		element.setId("progressBar");
		element.setAttribute("unselectable", "on");
		
		progressBarContainer = DOM.createDiv();
		progressBarContainer.setClassName("container");
		progressBarContainer.setAttribute("unselectable", "on");
		element.appendChild(progressBarContainer);
		
		currentProgress = DOM.createDiv();
		currentProgress.setClassName("currentProgress");
		currentProgress.setAttribute("unselectable", "on");
		progressBarContainer.appendChild(currentProgress);
		
		progressValue = TextManager.getText("");
		progressBarContainer.appendChild(progressValue.getElement());
		
		if (Config.getGraphicsQuality() > Config.VALUE_QUALITY_LOW) {
			updater = new WidthUpdater(currentProgress, 0);
			TimerManager.register(updater);
		}
		
		sinkEvents(Event.ONCLICK);
		
		SelectionManager.getInstance().addSelectionListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	
	
	public void selectionChanged(Selection newSelection, Selection oldSelection) {
		update();
	}
	
	public void update() {
		switch (SelectionManager.getInstance().getSelection().getType()) {
		case Selection.TYPE_SYSTEM:
			// Sélection d'un système
			setSystemInhabitantsView(SelectionTools.getSelectedSystem());
			break;
		case Selection.TYPE_SPACE_STATION:
			// Selection d'une station spatiale
			setSpaceStationLevelView(SelectionTools.getSelectedSpaceStation());
			break;
		case Selection.TYPE_FLEET:
			// Sélection d'une ou plusieurs flottes
			setFleetPowerView(SelectionTools.getSelectedFleets()[0]);
			break;
		case Selection.TYPE_STRUCTURE:
			// Sélection de structure
			setStructurePayloadView(SelectionTools.getSelectedStructure());
			break;
		case Selection.TYPE_NONE:
			// Pas de sélection
			setPlayerXpView();
			break;
		}
	}
	
	public  double getPlayerXp() {
		return playerXp;
	}

	public int getColonizationPoints() {
		return colonizationPoints;
	}
	
	public void setPlayerData(long playerXp, int colonizationPoints) {
		this.playerXp = playerXp;
		this.colonizationPoints = colonizationPoints;
		
		if (view == VIEW_PLAYER_XP)
			setPlayerXpView();
	}
	
	public void setPlayerGeneratorsCount(long count){
		
     this.playerGeneratorsCount = count;
     
	}
	
	
	public int getView() {
		return view;
	}
	
	public void onLoad() {
		super.onLoad();
		
		progressValue.setWidth(progressBarContainer.getOffsetWidth());
	}
	
	public int getPlayerLevel(){
		long xp = playerXp;
		int level = 1;
		while (true) {
			if (xp < getPlayerLevelXp(level + 1))
				return level;
			level++;
		}
	}

	
	public final static long getPlayerLevelXp(int level) {
		// Voir materials/simulation development.xlsx
		long xp = 0;
		
		for (int i = 1; i < level; i++)
			xp += 0.02 * FleetData.getPowerLevel(i + 1) * (3 + 2 * i);
		
		return xp;
	}
	

		
	
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			switch (view) {
			case VIEW_FLEET_XP:
				setFleetPowerView(SelectionTools.getSelectedFleets()[0]);
				break;
			case VIEW_FLEET_POWER:
				setFleetPayloadView(SelectionTools.getSelectedFleets()[0]);
				break;
			case VIEW_FLEET_PAYLOAD:
				setFleetXpView(SelectionTools.getSelectedFleets()[0]);
				break;
			case VIEW_SYSTEM_INHABITANTS:
				setSystemStockView(SelectionTools.getSelectedSystem());
				break;
			case VIEW_SYSTEM_STOCK:
				setSystemInhabitantsView(SelectionTools.getSelectedSystem());
				break;
			case VIEW_PLAYER_XP:
				Client.getInstance().getAdvancementDialog().setVisible(true);
				break;
			}
			event.preventDefault();
			event.cancelBubble(true);
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void setCurrentProgress(double width) {
		if (width > 1)
			width = 1;
		
		if (updater != null)
			updater.setTargetWidth(width);
		else
			currentProgress.getStyle().setProperty(
					"width", Math.floor(100 * width) + "%");
	}
	
	private void setFleetXpView(PlayerFleetData fleetData) {
		this.view = VIEW_FLEET_XP;
		
		int level = fleetData.getFleetLevel();
		long xp = fleetData.getXp();
		int currentLevelXp = FleetData.getFleetLevelXp(level);
		int nextLevelXp;
		
		double width;
		if (level < 15) {
			nextLevelXp = FleetData.getFleetLevelXp(level + 1);
			width = (xp - currentLevelXp) /
				(double) (nextLevelXp - currentLevelXp);
		} else {
			nextLevelXp = currentLevelXp;
			width = 1;
		}
		
		// Redimensionne la barre d'XP
		setCurrentProgress(width);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">" + messages.experience() + "</div>" +
			"<div><b>XP : <span class=\"emphasize\">" + Formatter.formatNumber(xp) +
			"</span> / " + Formatter.formatNumber(nextLevelXp) + "</b></div>" +
			"<div class=\"justify\">Affrontrez des flottes ou utilisez " +
			"certaines compétences pour gagner de l'XP. Les flottes gagnent un " +
			"point de compétence à chaque montée de niveau.</div>" +
			"<div class=\"emphasize justify\">Cliquez pour afficher " +
			"la puissance de la flotte.</div>", 200);
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">" +
				messages.level(level) + "</div>");
	}
	
	private void setFleetPowerView(PlayerFleetData fleetData) {
		this.view = VIEW_FLEET_POWER;
		
		int powerLevel = fleetData.getPowerLevel();
		int power = fleetData.getPower();
		int currentPowerLevel = GroupData.getPowerAtLevel(powerLevel);
		int nextPowerLevel = GroupData.getPowerAtLevel(powerLevel + 1) - 1;
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">Puissance</div>" +
			"<div><b>Vaisseaux : <span class=\"emphasize\">" +
			Formatter.formatNumber(power) + "</span> / " +
			Formatter.formatNumber(nextPowerLevel) + "</b></div>" +
			"<div class=\"justify\">" + messages.swapPowerHelp() + "</div>" +
			"<div class=\"emphasize justify\">Cliquez pour afficher la " +
			"capacité de la flotte.</div>", 200);
		
		setCurrentProgress((power - currentPowerLevel) /
				(double) (nextPowerLevel - currentPowerLevel));
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">" +
			"Puissance " + powerLevel + "<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"stat s-power\"/></div>");
	}
	
	private void setStructurePayloadView(StructureData structureData) {
		this.view = VIEW_STRUCTURE_PAYLOAD;
		
		long payload = (long)structureData.getPayload();
		long totalResources = (long)structureData.getTotalResources();
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">Capacité</div>" +
			"<div><b>Ressources : <span class=\"emphasize\">" +
			Formatter.formatNumber(totalResources) + "</span> / " +
			Formatter.formatNumber(payload) + "</b></div>" +
			"<div class=\"justify\">Capacité maximale que les " +
			"silos dans le secteur peuvent contenir.</div>", 200);
		
		setCurrentProgress(payload == 0 ? 0 : totalResources /  (double) payload);
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">Charge " +
			(payload == 0 ? "0%" : Math.min(100, (100 * totalResources / payload)) + "%") + "</div>");
	}
	
	private void setFleetPayloadView(PlayerFleetData fleetData) {
		this.view = VIEW_FLEET_PAYLOAD;
		
		long payload = fleetData.getPayload();
		long totalWeight = (long) fleetData.getTotalWeight();
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">Capacité</div>" +
			"<div><b>Ressources : <span class=\"emphasize\">" +
			Formatter.formatNumber(totalWeight) + "</span> / " +
			Formatter.formatNumber(payload) + "</b></div>" +
			"<div class=\"justify\">" + messages.payloadHelp() + "</div>" +
			"<div class=\"emphasize justify\">Cliquez pour afficher " +
			"l'expérience de la flotte.</div>", 200);
		
		setCurrentProgress(payload == 0 ? 0 : totalWeight / (double) payload);
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">Charge " +
			(payload == 0 ? "0%" : Math.min(100, (100 * totalWeight / payload)) + "%") + "</div>");
	}
	
	private void setPlayerXpView() {
		this.view = VIEW_PLAYER_XP;

		int level = getPlayerLevel();
		long xp = playerXp;
		long currentLevelXp = getPlayerLevelXp(level);
		long nextLevelXp = getPlayerLevelXp(level + 1);	

		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		long requiredxp = (nextLevelXp - xp);
		long maxGeneratorNumber = (level/4) + 1;
				
		setToolTipText(null);
		setToolTipText("<div class=\"title\">" + messages.experience() + "</div>" +
			"<div><b>XP : <span class=\"emphasize\">" + Formatter.formatNumber(xp) +
			"</span> / " + Formatter.formatNumber(nextLevelXp) + "</b></div>" +
			"<div><b>Puissance max : <span class=\"emphasize\">" + level +
			Utilities.parseSmilies(" :p:") + " </span></b></br><b><span class=\"emphasize\">" + Formatter.formatNumber(requiredxp) + "</span> XP requis pour le prochain niveau.</b></div><div><b>" +
			messages.colonizationPoints("<span class=\"emphasize\">" +
			colonizationPoints + "</span>") + "</b></div><b>" +
			"Nombre max. de Générateurs : <span class=\"emphasize\">" + playerGeneratorsCount + "/"+ maxGeneratorNumber + "</span></b></div>" +
			"<div class=\"justify\">Le niveau détermine la puissance maximale " +
			"de vos flottes. Affrontez des flottes pour gagner de l'XP.<br/>" +
			"Vous gagnez un point de colonisation à chaque montée de niveau ; " +
			"coloniser un nouveau système requiert 5 points de colonisation.</div>" +
			"<div class=\"emphasize justify\">Cliquez pour afficher " +
			"les avancées de votre civilisation.</div>",
			200);
		
		// Redimensionne la barre d'XP
		setCurrentProgress((xp - currentLevelXp) /
				(double) (nextLevelXp - currentLevelXp));
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">" +
				messages.level(level) + "</div>");
	}
	
	private void setSystemInhabitantsView(PlayerStarSystemData systemData) {
		this.view = VIEW_SYSTEM_INHABITANTS;
		
		double population = Client.getInstance().getResourcesManager(
				).getCurrentPopulation(systemData.getId());
		int maxPopulation = (int) BuildingData.getProduction(
			BuildingData.CIVILIAN_INFRASTRUCTURES, systemData);
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">Population</div>" +
			"<div><b>Population : <span class=\"emphasize\">" +
			(Math.round(population * 10) / 10.) +
			" M</span> / " + (Math.round(maxPopulation * 10) / 10.) +
			" M</b></div><div><b>Croissance : " +
			"<span class=\"emphasize\">+" + (Math.round(
			(maxPopulation * (1 + .05 * Client.getInstance().getAdvancementDialog(
			).getAdvancementLevel(AdvancementData.TYPE_POPULATION_GROWTH)) *
			systemData.getPopulatinGrowthModifier() *
			PlayerStarSystemData.POPULATION_GROWTH * 3600 * 24) * 10) / 10.) +
			" M</span> / jour</b></div>" +
			"<div class=\"justify\">Construisez des infrastructures civiles " +
			"pour augmenter la population. Plus la population du système " +
			"est élevée, plus les corporations génèrent de crédits.</div>" +
			"</div><div class=\"emphasize justify\">Cliquez pour afficher les " +
			"stocks du système.</div>", 200);
		
		setCurrentProgress(population / maxPopulation);
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">Pop. " +
			Formatter.formatNumber(Math.floor(population)) + " M</div>");
	}
	
	private void setSystemStockView(PlayerStarSystemData systemData) {
		this.view = VIEW_SYSTEM_STOCK;
		
		long stock = (long) BuildingData.getProduction(BuildingData.STOREHOUSE, systemData);
		long totalResources = 0;
		for (int i = 0; i < systemData.getResourcesCount(); i++)
			totalResources += Client.getInstance().getResourcesManager(
				).getCurrentResource(systemData.getId(), i);
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">Stocks</div>" +
			"<div><b>Ressources : <span class=\"emphasize\">" +
			Formatter.formatNumber(totalResources) + "</span> / " +
			Formatter.formatNumber(stock) + "</b></div>" +
			"<div class=\"justify\">Construisez des dépôts pour augmenter " +
			"la capacité du stockage du système. Quand les stocks du " +
			"système sont pleins, les exploitations cessent leur production. " +
			"Les crédits ne comptent pas dans les stocks</div>" +
			"<div class=\"emphasize justify\">Cliquez pour afficher " +
			"la population du système.</div>", 200);
		
		setCurrentProgress(stock == 0 ? 0 : Math.min(1,
				totalResources / (double) stock));
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">Stock " +
			(stock == 0 ? "0%" : (100 * totalResources / stock) + "%") + "</div>");
	}
	
	private void setSpaceStationLevelView(SpaceStationData spaceStationData) {
		this.view = VIEW_SPACE_STATION_LEVEL;
		
		int level = spaceStationData.getLevel();
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText(null);
		setToolTipText("<div class=\"title\">Station spatiale niveau " + level + "</div>" +
			"<div>Transférez des ressources vers la station spatiale afin " +
			"d'améliorer son niveau. Lorsque la station spatiale gagne un niveau," +
			"elle devient plus résistante et génère davantage d'influence, ce qui " +
			"augmente les bonus de la station.</div>", 200);
		
		// Redimensionne la barre d'XP
		double width;
		if (spaceStationData.getLevel() == 5) {
			width = 1;
		} else {
			width = 0;
			for (int i = 0; i < 3; i++)
				width += Math.min(.25, .25 * spaceStationData.getResourceAt(i) /
					SpaceStationData.COST_LEVELS[spaceStationData.getLevel() + 1][i]);
			width += Math.min(.25, .25 * spaceStationData.getCredits() /
				SpaceStationData.COST_LEVELS[spaceStationData.getLevel() + 1][4]);
		}
		
		setCurrentProgress(width);
		
		progressValue.setText("<div class=\"barLabel\" unselectable=\"on\">" +
			messages.level(level) + "</div>");
	}
}
