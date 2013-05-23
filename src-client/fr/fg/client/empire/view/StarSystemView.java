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

package fr.fg.client.empire.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import fr.fg.client.animation.ProgressBarUpdater;
import fr.fg.client.animation.ToolTipTextUpdater;
import fr.fg.client.animation.ToolTipTimeUpdater;
import fr.fg.client.core.Client;
import fr.fg.client.core.EmpireUpdater;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.BuildData;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.empire.View;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.UIMiniItem;
import fr.fg.client.map.impl.AreaMap;
import fr.fg.client.map.impl.MiniMap;
import fr.fg.client.map.item.StarSystemItem;
import fr.fg.client.map.miniitem.StarSystemMiniItem;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.CallbackHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipListener;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class StarSystemView extends View implements ToolTipListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static Callback UPDATE_SYSTEMS_CALLBACK = new Callback() {
		public void run() {
			EmpireUpdater.getInstance().queueSystemsUpdate();
		}
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //

	private int id;
	
	private int idArea;
	
	private int x, y;
	
	private ProgressBarUpdater buildUpdater, buildShipUpdater;
	
	private ToolTipTimeUpdater buildTimeUpdater, buildShipTimeUpdater;
	
	private CallbackHandler buildCallback, buildShipCallback;
	
	// ------------------------------------------------ CONSTRUCTEURS -- //
	
	public StarSystemView(PlayerStarSystemData systemData, long lastUpdate) {
		this.id = systemData.getId();
		this.idArea = systemData.getArea().getId();
		this.x = systemData.getX();
		this.y = systemData.getY();
		
		setStyleName("view system");
		getElement().setAttribute("unselectable", "on");
		
		// Construit l'étoile du système
		Element star = DOM.createDiv();
		star.setAttribute("unselectable", "on");
		star.setClassName("star");
		star.getStyle().setProperty("backgroundPosition", -(systemData.getStarImage() - 1) * 30 + "px -225px");
		getElement().appendChild(star);
		
		Element row1 = DOM.createDiv();
		row1.setClassName("row1");
		row1.setAttribute("unselectable", "on");

		int remainingBuildingTime = 0;
		
		if (systemData.getBuildsCount() > 0) {
			BuildData buildData = systemData.getBuildAt(0);
			
			long buildTime = (long) Math.ceil(BuildingData.getBuildTime(
				buildData.getType(), buildData.getLevel()) *
				BuildingData.getProduction(BuildingData.FACTORY, systemData));
			long buildEnd = buildData.getEnd() - lastUpdate;
			
			if (buildEnd > 0) {
				remainingBuildingTime = (int) buildEnd;
				
				int buildWidth = (int) Math.round(100 * (1 - (
						buildEnd / (double) buildTime)));
				
				Element buildBar = DOM.createDiv();
				buildBar.setClassName("progressBar progressBar-build");
				buildBar.setAttribute("unselectable", "on");
				Element buildProgress = DOM.createDiv();
				buildProgress.setClassName("currentProgress");
				buildProgress.setAttribute("unselectable", "on");
				buildProgress.getStyle().setProperty("width", buildWidth + "%");
				buildBar.appendChild(buildProgress);
				row1.appendChild(buildBar);
				
				buildUpdater = new ProgressBarUpdater(buildProgress,
					(int) (buildTime - buildEnd), (int) buildTime);
				TimerManager.register(buildUpdater, buildTime < 1800 ?
					TimerManager.SECOND_UNIT : TimerManager.MINUTE_UNIT);
			} else {
				UPDATE_SYSTEMS_CALLBACK.run();
			}
		}
		
		Element row2 = DOM.createDiv();
		row2.setClassName("row2");
		row2.setAttribute("unselectable", "on");
		
		int buildSlot = -1;
		int remainingBuildShipTime = 0;
		
		for (int i = 0; i < systemData.getBuildSlotsCount(); i++) {
			SlotInfoData slotData = systemData.getBuildSlotAt(i);
			
			if (slotData.getId() != 0) {
				buildSlot = i;
				
				double shipProduction = BuildingData.getProduction(
						BuildingData.SPACESHIP_YARD, systemData);
				double shipBuildTime = shipProduction > 0 ?
					ShipData.SHIPS[slotData.getId()].getBuildTime() /
					shipProduction : Integer.MAX_VALUE;
				
				int totalBuildTime = (int) (shipBuildTime *
					systemData.getBuildSlotOrderedAt(i));
				int remainingBuildTime = (int) (shipBuildTime *
					slotData.getCount() - lastUpdate -
					systemData.getLastUpdate());
				
				if (remainingBuildTime > 0) {
					remainingBuildShipTime = remainingBuildTime;
					
					int buildShipWidth = (int) Math.round(100 * (1 - (
						remainingBuildTime / (double) totalBuildTime)));
					
					Element buildShipBar = DOM.createDiv();
					buildShipBar.setClassName("progressBar progressBar-buildShip");
					buildShipBar.setAttribute("unselectable", "on");
					Element buildShipProgress = DOM.createDiv();
					buildShipProgress.setClassName("currentProgress");
					buildShipProgress.setAttribute("unselectable", "on");
					buildShipProgress.getStyle().setProperty("width", buildShipWidth + "%");
					buildShipBar.appendChild(buildShipProgress);
					row2.appendChild(buildShipBar);
					
					Element infoShipsRow = DOM.createDiv();
					infoShipsRow.setClassName("infoShipsRow");
					infoShipsRow.setAttribute("unselectable", "on");
					Element buildCurrentShip = DOM.createDiv();
					buildCurrentShip.setClassName("currentShip");
					buildCurrentShip.setAttribute("unselectable", "on");
					buildCurrentShip.getStyle().setProperty("backgroundPosition", - ((slotData.getId() * 15) - 2) + "px");
					infoShipsRow.appendChild(buildCurrentShip);
                    star.appendChild(infoShipsRow);
					
					buildShipUpdater = new ProgressBarUpdater(buildShipProgress,
						totalBuildTime - remainingBuildTime, totalBuildTime);
					TimerManager.register(buildShipUpdater, totalBuildTime < 1800 ?
						TimerManager.SECOND_UNIT : TimerManager.MINUTE_UNIT);
			
					
				}
				break;
			}
		}

		getElement().appendChild(row1);
		getElement().appendChild(row2);
		
		if (systemData.getShortcut() != -1) {
			OutlineText text = TextManager.getText(String.valueOf(systemData.getShortcut()));
			
			Element shortcut = DOM.createDiv();
			shortcut.setClassName("shortcut");
			shortcut.setAttribute("unselectable", "on");
			shortcut.appendChild(text.getElement());
			
			getElement().appendChild(shortcut);
		}
		
		// Tooltip de description du système
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		String tooltip = "<div class=\"owner-player\"><b>" +
			systemData.getName() + "</b></div>";
		
		if (systemData.getBuildsCount() > 0) {
			// Construction de bâtiment
			String id = ToolTipTextUpdater.generateId();
			buildTimeUpdater = new ToolTipTimeUpdater(
				getElement(), id, remainingBuildingTime);
			
			buildCallback = new CallbackHandler(
				UPDATE_SYSTEMS_CALLBACK, 1000 * (remainingBuildingTime + 1));
			TimerManager.registerCallback(buildCallback);
			
			BuildData buildData = systemData.getBuildAt(0);
			
			tooltip += "<div>" +
				BuildingData.getName(buildData.getType(), buildData.getLevel()) +
				" : <b id=\"" + id + "\"></b></div>";
		}
		
		if (buildSlot != -1) {
			// Construction de vaisseaux
			String id = ToolTipTextUpdater.generateId();
			buildShipTimeUpdater = new ToolTipTimeUpdater(
				getElement(), id, remainingBuildShipTime);
			
			buildShipCallback = new CallbackHandler(
				UPDATE_SYSTEMS_CALLBACK, 1000 * (remainingBuildShipTime + 1));
			TimerManager.registerCallback(buildShipCallback);
			
			SlotInfoData slotData = systemData.getBuildSlotAt(buildSlot);
			
			tooltip += "<div><span class=\"owner-player\">" +
				Formatter.formatNumber(
				systemData.getBuildSlotOrderedAt(buildSlot), true) + "</span>&nbsp;" +
				dynamicMessages.getString("ships" + slotData.getId()) +
				" : <b id=\"" + id + "\"></b><div unselectable=\"on\" class=\"spaceship\" style=\"" +
				"background-position: -" + (slotData.getId() * 50) + "px 0\" /></div>";
			
			
		}
		
		if (buildTimeUpdater != null || buildShipTimeUpdater != null)
			ToolTipManager.getInstance().addToolTipListener(this);
		
		ToolTipManager.getInstance().register(getElement(), tooltip);
		
		sinkEvents(Event.ONCLICK | Event.ONDBLCLICK |
				Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// ----------------------------------------------------- METHODES -- //
	
	public void onToolTipOpening(Element element) {
		if (element != getElement())
			return;
		
		if (buildTimeUpdater != null) {
			buildTimeUpdater.synchronize();
			TimerManager.register(buildTimeUpdater, TimerManager.SECOND_UNIT);
		}
		
		if (buildShipTimeUpdater != null) {
			buildShipTimeUpdater.synchronize();
			TimerManager.register(buildShipTimeUpdater, TimerManager.SECOND_UNIT);
		}
	}
	
	public void onToolTipClosed(Element element) {
		if (element != getElement())
			return;
		
		if (buildTimeUpdater != null)
			TimerManager.unregister(buildTimeUpdater);
		if (buildShipTimeUpdater != null)
			TimerManager.unregister(buildShipTimeUpdater);
	}
	
	public void destroy() {
		if (buildShipTimeUpdater != null) {
			TimerManager.unregister(buildShipTimeUpdater);
			buildShipTimeUpdater.destroy();
			buildShipTimeUpdater = null;
		}
		
		if (buildShipUpdater != null) {
			TimerManager.unregister(buildShipUpdater);
			buildShipUpdater.destroy();
			buildShipUpdater = null;
		}
		
		if (buildTimeUpdater != null) {
			TimerManager.unregister(buildTimeUpdater);
			buildTimeUpdater.destroy();
			buildTimeUpdater = null;
		}
		
		if (buildUpdater != null) {
			TimerManager.unregister(buildUpdater);
			buildUpdater.destroy();
			buildUpdater = null;
		}
		
		if (buildCallback != null) {
			TimerManager.unregisterCallback(buildCallback);
			buildCallback = null;
		}
		
		if (buildShipCallback != null) {
			TimerManager.unregisterCallback(buildShipCallback);
			buildShipCallback = null;
		}
		
		ToolTipManager.getInstance().removeToolTipListener(this);
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			Client.getInstance().getAreaContainer().setIdArea(idArea, new Point(x, y));
			break;
		case Event.ONDBLCLICK:
			IndexedAreaData area = Client.getInstance().getAreaContainer().getArea();
			if (area.getId() == idArea)
				SelectionManager.getInstance().selectSystem(id);
			break;
		case Event.ONMOUSEOVER:
			if (event.getFromElement() == null ||
					getElement().isOrHasChild(event.getFromElement()))
				return;
			
			setHighlighted(true);
			break;
		case Event.ONMOUSEOUT:
			if (event.getToElement() == null ||
					getElement().isOrHasChild(event.getToElement()))
				return;
			
			setHighlighted(false);
			break;
		}
	}
	
	// --------------------------------------------- METHODES PRIVEES -- //
	
	private void setHighlighted(boolean highlight) {
		IndexedAreaData area = Client.getInstance().getAreaContainer().getArea();
		if (area.getId() == idArea) {
			StarSystemData system = area.getSystemById(id);
			
			AreaMap map = Client.getInstance(
				).getAreaContainer().getMap();
			
			UIItem item = map.getItem(system, StarSystemData.CLASS_NAME);
			
			if (item != null)
				((StarSystemItem) item).showSystemOutline(highlight);
			
			for (MiniMap miniMap : map.getMiniMaps()) {
				UIMiniItem miniItem = miniMap.getItem(system, StarSystemData.CLASS_NAME);
				
				if (miniItem != null) {
					StarSystemMiniItem miniSystem = (StarSystemMiniItem) miniItem;
					miniSystem.setBrightLineOfSight(highlight);
				}
			}
		}
	}
}
