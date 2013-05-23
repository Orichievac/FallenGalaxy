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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.impl.AreaContainerImpl;
import fr.fg.client.core.selection.Selection;
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.core.settings.OptionsDialog;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.AsteroidsData;
import fr.fg.client.data.BankData;
import fr.fg.client.data.LotteryData;
import fr.fg.client.data.BlackHoleData;
import fr.fg.client.data.ContractMarkerData;
import fr.fg.client.data.DoodadData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GateData;
import fr.fg.client.data.GravityWellData;
import fr.fg.client.data.HyperspaceSignatureData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.MarkerData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.TradeCenterData;
import fr.fg.client.data.WardData;
import fr.fg.client.map.ScrollController;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.impl.AreaMap;
import fr.fg.client.map.impl.MiniMap;
import fr.fg.client.map.item.FleetItem;
import fr.fg.client.map.item.SpaceStationItem;
import fr.fg.client.map.item.StarSystemItem;
import fr.fg.client.map.item.StructureItem;
import fr.fg.client.map.item.WardItem;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;
import gwt.canvas.client.Canvas;

public class AreaContainer extends AbsolutePanel implements EventListener,
		EventPreview, ActionCallback, SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int TILE_SIZE = 40;
	
	public final static int NEBULAS_COUNT = 8;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private AreaContainerImpl impl;
	
	private BaseWidget nebula, mainNebula;
	
	private IndexedAreaData currentArea;
	
	private AreaMap map;
	
	private Canvas canvas;
	
	private ScrollController controller;
	
	// Dernière position de la souris, pour centrer la position du zoom /
	// dézoom dessus
	private int mouseX, mouseY;
	
	private Action downloadAction;
	
	private Point view;
	
	private long lastAreaUpdate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AreaContainer() {
		impl = GWT.create(AreaContainerImpl.class);
		impl.createStarFields();
		
		getElement().setId("map");
		getElement().setAttribute("unselectable", "on");
		
		mainNebula = new BaseWidget();
		mainNebula.getElement().setId("mainNebula");
		mainNebula.setVisible(false);
		nebula = new BaseWidget();
		nebula.getElement().setId("nebula");
		
		nebula.getElement().setAttribute("unselectable", "on");
		mainNebula.getElement().setAttribute("unselectable", "on");
		
		add(nebula, 0, 0);
		add(mainNebula, 0, 0);
		if (impl.getFarStarField() != null)
			add(impl.getFarStarField(), 0, 0);
		if (impl.getNearStarField() != null)
			add(impl.getNearStarField(), 0, 0);
		
		map = new AreaMap("area", TILE_SIZE, impl.getNearStarField(),
				impl.getFarStarField());
		add(map, 0, 0);
		
		controller = new ScrollController(map, this);
		controller.setEnabled(false);
		
		EventManager.addEventHook(this);
		SelectionManager.getInstance().addSelectionListener(this);
		
		sinkEvents(Event.ONMOUSEWHEEL | Event.ONCLICK |
				Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void drawCircle(int x, int y, int radius, String color) {
		drawCircles(x, y, new int[]{radius}, new String[]{color});
	}
	
	public void drawCircles(int x, int y, int[] radius, String[] colors) {
		hideCanvas();
		
		int coef = (int) (map.getTileSize() * map.getZoom());
		
		int maxRadius = radius[0];
		for (int i = 1; i < radius.length; i++)
			if (maxRadius < radius[i])
				maxRadius = radius[i];
		
		canvas = new Canvas(
			(int) (2 * (maxRadius + .5) * coef + 4),
			(int) (2 * (maxRadius + .5) * coef + 4));
		canvas.getElement().setId("canvas");
		canvas.getElement().setAttribute("unselectable", "on");
		canvas.setBackgroundColor(Canvas.TRANSPARENT);
		
		for (int i = 0; i < radius.length; i++) {
			canvas.beginPath();
			canvas.arc(
				2 + (int) Math.floor((maxRadius + .5) * coef),
				2 + (int) Math.floor((maxRadius + .5) * coef),
				(int) Math.floor((radius[i] + .5) * coef),
				0, 2 * Math.PI, true);
			canvas.closePath();
			canvas.setStrokeStyle(colors[i]);
			canvas.setLineWidth(3);
			canvas.stroke();
		}
		
		map.add(canvas, (x - maxRadius) * coef - 2, (y - maxRadius) * coef - 2);
	}
	
	public void hideCanvas() {
		if (canvas != null) {
			canvas.clear();
			canvas.removeFromParent();
			canvas = null;
		}
	}
	
	public AreaMap getMap() {
		return map;
	}
	
	public ScrollController getScrollController() {
		return controller;
	}
	
	public IndexedAreaData getArea() {
		return currentArea;
	}
	
	public void setIdArea(int idArea) {
		setIdArea(idArea, null);
	}
	
	public void setIdArea(int idArea, Point view) {
		if ((downloadAction != null && downloadAction.isPending()) ||
				idArea == currentArea.getId()) {
			if (view != null) {
				map.centerView(new Point(
						view.getX() * map.getTileSize(),
						view.getY() * map.getTileSize()));
			}
			return;
		}
		
		this.view = view;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("area", String.valueOf(idArea));
		
		downloadAction = new Action("getarea", params, this);
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		setArea(new IndexedAreaData(data.getArea()), view);
		view = null;
	}
	
	public void setArea(IndexedAreaData areaData) {
		setArea(areaData, null);
	}
	
	public void setArea(IndexedAreaData areaData, Point view) {
		if (currentArea != null && areaData.getId() == currentArea.getId())
			return;
		
		this.lastAreaUpdate = Utilities.getCurrentTime();
		boolean updateEmpireView = false;
		if (currentArea != null && !currentArea.getSector(
				).equals(areaData.getSector()))
			updateEmpireView = true;
		
		controller.setEnabled(true);
		
		SelectionManager.getInstance().setNoSelection();
		
		hideCanvas();
		map.clear();
		
		this.currentArea = areaData;
		
		map.setMapTileSize(new Dimension(areaData.getWidth(), areaData.getHeight()));
		
		// Centre la vue
		if (view != null) {
			map.centerView(new Point(view.getX() * map.getTileSize(),
					view.getY() * map.getTileSize()));
		} else {
			map.centerView(new Point(
					areaData.getWidth() * map.getTileSize() / 2,
					areaData.getHeight() * map.getTileSize() / 2));
		}
		
		// Nébuleuse
		setNebula(areaData.getNebula(), false);
		
		Client.getInstance().getNamePanel().setCurrentArea(areaData);
		
		// Champs d'astéroïdes
		for (int i = 0; i < areaData.getAsteroidsCount(); i++)
			map.addItem(areaData.getAsteroidsAt(i), AsteroidsData.CLASS_NAME);
		
		// Trous noirs
		for (int i = 0; i < areaData.getBlackHolesCount(); i++)
			map.addItem(areaData.getBlackHoleAt(i), BlackHoleData.CLASS_NAME);
		
		// Systèmes
		for (int i = 0; i < areaData.getSystemsCount(); i++)
			map.addItem(areaData.getSystemAt(i), StarSystemData.CLASS_NAME);
		
		// Portes hyperspatiales
		for (int i = 0; i < areaData.getGatesCount(); i++)
			map.addItem(areaData.getGateAt(i), GateData.CLASS_NAME);
		
		// Puits gravitationnels
		for (int i = 0; i < areaData.getGravityWellsCount(); i++)
			map.addItem(areaData.getGravityWellAt(i), GravityWellData.CLASS_NAME);
		
		// Doodads
		for (int i = 0; i < areaData.getDoodadsCount(); i++)
			map.addItem(areaData.getDoodadAt(i), DoodadData.CLASS_NAME);
		
		// Banques
		for (int i = 0; i < areaData.getBanksCount(); i++)
			map.addItem(areaData.getBankAt(i), BankData.CLASS_NAME);
		
		// Loteries
		for (int i = 0; i < areaData.getLotterysCount(); i++)
			map.addItem(areaData.getLotteryAt(i), LotteryData.CLASS_NAME);
		
		// Centres de commerce
		for (int i = 0; i < areaData.getTradeCentersCount(); i++)
			map.addItem(areaData.getTradeCenterAt(i), TradeCenterData.CLASS_NAME);
		
		// Stations spatiales
		for (int i = 0; i < areaData.getSpaceStationsCount(); i++)
			map.addItem(areaData.getSpaceStationAt(i), SpaceStationData.CLASS_NAME);
		
		// Signatures HE
		for (int i = 0; i < areaData.getHyperspaceSignaturesCount(); i++)
			map.addItem(areaData.getHyperspaceSignatureAt(i), HyperspaceSignatureData.CLASS_NAME);
		
		// Flottes
		for (int i = 0; i < areaData.getFleetsCount(); i++)
			map.addItem(areaData.getFleetAt(i), FleetData.CLASS_NAME);
		
		// Flottes
		for (int i = 0; i < areaData.getStructuresCount(); i++)
			map.addItem(areaData.getStructureAt(i), StructureData.CLASS_NAME);
		
		// Marqueurs
		for (int i = 0; i < areaData.getMarkersCount(); i++)
			map.addItem(areaData.getMarkerAt(i), MarkerData.CLASS_NAME);
		
		// Marqueurs de missions
		for (int i = 0; i < areaData.getContractMarkersCount(); i++)
			map.addItem(areaData.getContractMarkerAt(i), ContractMarkerData.CLASS_NAME);
		
		// Balises
		for (int i = 0; i < areaData.getWardsCount(); i++)
			map.addItem(areaData.getWardAt(i), WardData.CLASS_NAME);
		
		if (updateEmpireView)
			Client.getInstance().getEmpireView().updateView();
	}
	
	public void updateArea(IndexedAreaData area) {
		if (area.getId() != currentArea.getId())
			return;
		
		this.lastAreaUpdate = Utilities.getCurrentTime();
		
		try {
			// Trous noirs
			for (int i = 0; i < currentArea.getBlackHolesCount(); i++)
				if (area.getBlackHoleById(currentArea.getBlackHoleAt(i).getId()) == null)
					map.removeItem(currentArea.getBlackHoleAt(i), BlackHoleData.CLASS_NAME);
			
			for (int i = 0; i < area.getBlackHolesCount(); i++)
				map.updateOrAddItem(area.getBlackHoleAt(i), BlackHoleData.CLASS_NAME);
			
			// Systèmes
			for (int i = 0; i < area.getSystemsCount(); i++)
				map.updateItem(area.getSystemAt(i), StarSystemData.CLASS_NAME);
			
			// Doodads
			for (int i = 0; i < currentArea.getDoodadsCount(); i++)
				if (area.getDoodadById(currentArea.getDoodadAt(i).getId()) == null)
					map.removeItem(currentArea.getDoodadAt(i), DoodadData.CLASS_NAME);
			
			for (int i = 0; i < area.getDoodadsCount(); i++)
				map.updateOrAddItem(area.getDoodadAt(i), DoodadData.CLASS_NAME);

			// Stations spatiales
			for (int i = 0; i < currentArea.getSpaceStationsCount(); i++)
				if (area.getSpaceStationById(currentArea.getSpaceStationAt(i).getId()) == null)
					map.removeItem(currentArea.getSpaceStationAt(i), SpaceStationData.CLASS_NAME);
			
			for (int i = 0; i < area.getSpaceStationsCount(); i++)
				map.updateOrAddItem(area.getSpaceStationAt(i), SpaceStationData.CLASS_NAME);
			
			// Flottes
			for (int i = 0; i < currentArea.getFleetsCount(); i++)
				if (area.getFleetById(currentArea.getFleetAt(i).getId()) == null)
					map.removeItem(currentArea.getFleetAt(i), FleetData.CLASS_NAME);
			
			for (int i = 0; i < area.getFleetsCount(); i++)
				map.updateOrAddItem(area.getFleetAt(i), FleetData.CLASS_NAME);

			// Structures
			for (int i = 0; i < currentArea.getStructuresCount(); i++)
				if (area.getStructureById(currentArea.getStructureAt(i).getId()) == null)
					map.removeItem(currentArea.getStructureAt(i), StructureData.CLASS_NAME);
			
			for (int i = 0; i < area.getStructuresCount(); i++)
				map.updateOrAddItem(area.getStructureAt(i), StructureData.CLASS_NAME);
			
			// Astéroides
			for (int i = 0; i < currentArea.getAsteroidsCount(); i++)
				if (area.getAsteroidsById(currentArea.getAsteroidsAt(i).getId()) == null)
					map.removeItem(currentArea.getAsteroidsAt(i), AsteroidsData.CLASS_NAME);
			
			for (int i = 0; i < area.getAsteroidsCount(); i++)
				map.updateOrAddItem(area.getAsteroidsAt(i), AsteroidsData.CLASS_NAME);
			
			// Marqueurs
			for (int i = 0; i < currentArea.getMarkersCount(); i++)
				if (area.getMarkerById(currentArea.getMarkerAt(i).getId()) == null)
					map.removeItem(currentArea.getMarkerAt(i), MarkerData.CLASS_NAME);
			
			for (int i = 0; i < area.getMarkersCount(); i++)
				map.updateOrAddItem(area.getMarkerAt(i), MarkerData.CLASS_NAME);

			// Signatures HE
			for (int i = 0; i < currentArea.getHyperspaceSignaturesCount(); i++)
				if (area.getHyperspaceSignatureById(currentArea.getHyperspaceSignatureAt(i).getId()) == null)
					map.removeItem(currentArea.getHyperspaceSignatureAt(i), HyperspaceSignatureData.CLASS_NAME);
			
			for (int i = 0; i < area.getHyperspaceSignaturesCount(); i++)
				map.updateOrAddItem(area.getHyperspaceSignatureAt(i), HyperspaceSignatureData.CLASS_NAME);
			
			// Marqueurs de missions
			for (int i = 0; i < currentArea.getContractMarkersCount(); i++)
				if (area.getContractMarkerById(currentArea.getContractMarkerAt(i).getId()) == null)
					map.removeItem(currentArea.getContractMarkerAt(i), ContractMarkerData.CLASS_NAME);
			
			for (int i = 0; i < area.getContractMarkersCount(); i++)
				map.updateOrAddItem(area.getContractMarkerAt(i), ContractMarkerData.CLASS_NAME);
			
			// Balises
			for (int i = 0; i < currentArea.getWardsCount(); i++)
				if (area.getWardById(currentArea.getWardAt(i).getId()) == null)
					map.removeItem(currentArea.getWardAt(i), WardData.CLASS_NAME);
			
			for (int i = 0; i < area.getWardsCount(); i++)
				map.updateOrAddItem(area.getWardAt(i), WardData.CLASS_NAME);
			
			this.currentArea = area;
		} catch (Exception e) {
			Utilities.log("Could not update area.", e);
		}
	}
	
	public void setNebula(int nebula, boolean main) {
		// Nébuleuse
		this.nebula.getElement().getStyle().setProperty("background", "url('" +
			Config.getMediaUrl() + "images/nebulas/" +
			(Settings.getBrightness() == OptionsDialog.VALUE_BRIGHTNESS_DARK ?
			"dark/" : "bright/") + nebula + "1.jpg') 50% 50% repeat");
		if (main) {
			this.mainNebula.getElement().getStyle().setProperty("background", "url('" +
				Config.getMediaUrl() + "images/nebulas/" +
				(Settings.getBrightness() ==
					OptionsDialog.VALUE_BRIGHTNESS_DARK ? "dark/" : "bright/") +
				nebula + "2.jpg') 50% 50% no-repeat");
			this.mainNebula.setVisible(true);
		} else {
			this.mainNebula.setVisible(false);
		}
	}
	
	public long getLastAreaUpdate() {
		return lastAreaUpdate;
	}
	
	public void addMiniMap(MiniMap miniMap) {
		map.addMiniMap(miniMap);
	}
	
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEWHEEL:
			if (currentArea == null)
				return;
			
			int wheelDelta = DOM.eventGetMouseWheelVelocityY(event);
			
			Point view = new Point(map.getView());
			
			if (wheelDelta < 0) {
				if (map.getZoom() < 1) {
					view.addX((int) Math.floor(mouseX / ( map.getZoom())));
					view.addY((int) Math.floor(mouseY / ( map.getZoom())));
					map.setZoom(map.getZoom() * 2);
					map.centerView(view);
				}
			} else if (wheelDelta > 0) {
				if (map.getZoom() > .125) {
					view.addX((int) (Window.getClientWidth() / (2 * map.getZoom())));
					view.addY((int) (Window.getClientHeight() / (2 * map.getZoom())));
					map.setZoom(map.getZoom() / 2);
					map.centerView(view);
				} else {
					Client.getInstance().getGalaxyMap().show(GalaxyMap.MODE_DEFAULT);
				}
			}
			
			DOM.eventPreventDefault(event);
			DOM.eventCancelBubble(event, true);
			break;
		}
	}
	
	public void selectionChanged(Selection newSelection, Selection oldSelection) {
		applySelection(oldSelection, false);
		applySelection(newSelection, true);
	}
	
	public boolean onEventPreview(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEMOVE:
			mouseX = OpenJWT.eventGetPointerX(event);
			mouseY = OpenJWT.eventGetPointerY(event);
			break;
		case Event.ONKEYDOWN:
			Element target = DOM.eventGetTarget(event);
			
			if ((target != null && DOM.getElementProperty(
					target, "nodeName").toLowerCase().equals("input"))) //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			
			if (event.getKeyCode() == 18) {
				event.cancelBubble(true);
				event.preventDefault();
				
				for (int i = 0; i < currentArea.getFleetsCount(); i++) {
					UIItem item = map.getItem(currentArea.getFleetAt(i),
						FleetData.CLASS_NAME);
					((FleetItem) item).setPowerLevelVisible(true);
				}
				
				for (int i = 0; i < currentArea.getStructuresCount(); i++) {
					UIItem item = map.getItem(currentArea.getStructureAt(i),
						StructureData.CLASS_NAME);
					((StructureItem) item).setHullBarVisible(true);
				}
			} else if (event.getKeyCode() == 87) {
				// Affichage de la portées des charges alliées
				event.cancelBubble(true);
				event.preventDefault();
				
				for (int i = 0; i < currentArea.getWardsCount(); i++) {
					String treaty = currentArea.getWardAt(i).getTreaty();
					if (treaty.equals("player") || treaty.equals("ally") ||
							treaty.equals("allied")) {
						UIItem item = map.getItem(
								currentArea.getWardAt(i), WardData.CLASS_NAME);
						WardItem ward = (WardItem) item;
						ward.setTriggerRadiusVisible(true);
					}
				}
			} else if (event.getKeyCode() == 88) {
				// Affichage de la portées des charges ennemies
				event.cancelBubble(true);
				event.preventDefault();
				
				for (int i = 0; i < currentArea.getWardsCount(); i++) {
					String treaty = currentArea.getWardAt(i).getTreaty();
					if (treaty.equals("enemy") || treaty.equals("pirate")) {
						UIItem item = map.getItem(
								currentArea.getWardAt(i), WardData.CLASS_NAME);
						WardItem ward = (WardItem) item;
						ward.setTriggerRadiusVisible(true);
					}
				}
			} else if (event.getKeyCode() == 83) {
				// Sélection de toutes les flottes du secteur
				event.cancelBubble(true);
				event.preventDefault();
				
				for (int i = 0; i < currentArea.getFleetsCount(); i++) {
					FleetData fleet = currentArea.getFleetAt(i);
					
					if (fleet.getTreaty().equals("player") && fleet.getMovement() > 0 &&
							!fleet.isScheduledMove()) {
						SelectionManager.getInstance().addSelectedFleet(fleet.getId());
						if (SelectionManager.getInstance().getSelectedFleetsCount() == 9)
							return true;
					}
				}
				
				for (int i = 0; i < currentArea.getFleetsCount(); i++) {
					FleetData fleet = currentArea.getFleetAt(i);
					
					if (fleet.getTreaty().equals("player") && fleet.getMovement() > 0 &&
							fleet.isScheduledMove() && !SelectionManager.getInstance(
								).isFleetSelected(fleet.getId())) {
						SelectionManager.getInstance().addSelectedFleet(fleet.getId());
						if (SelectionManager.getInstance().getSelectedFleetsCount() == 9)
							return true;
					}
				}
			}
			break;
		case Event.ONKEYUP:
			target = DOM.eventGetTarget(event);
			
			if ((target != null && DOM.getElementProperty(
					target, "nodeName").toLowerCase().equals("input"))) //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			
			if (event.getKeyCode() == 18) {
				for (int i = 0; i < currentArea.getFleetsCount(); i++) {
					UIItem item = map.getItem(
							currentArea.getFleetAt(i), FleetData.CLASS_NAME);
					((FleetItem) item).setPowerLevelVisible(false);
				}
				
				for (int i = 0; i < currentArea.getStructuresCount(); i++) {
					UIItem item = map.getItem(currentArea.getStructureAt(i),
						StructureData.CLASS_NAME);
					((StructureItem) item).setHullBarVisible(false);
				}
			} else if (event.getKeyCode() == 87) {
				for (int i = 0; i < currentArea.getWardsCount(); i++) {
					String treaty = currentArea.getWardAt(i).getTreaty();
					if (treaty.equals("player") || treaty.equals("ally") ||
							treaty.equals("allied")) {
						UIItem item = map.getItem(
								currentArea.getWardAt(i), WardData.CLASS_NAME);
						WardItem ward = (WardItem) item;
						ward.setTriggerRadiusVisible(false);
					}
				}
			} else if (event.getKeyCode() == 88) {
				for (int i = 0; i < currentArea.getWardsCount(); i++) {
					String treaty = currentArea.getWardAt(i).getTreaty();
					if (treaty.equals("enemy") || treaty.equals("pirate")) {
						UIItem item = map.getItem(
								currentArea.getWardAt(i), WardData.CLASS_NAME);
						WardItem ward = (WardItem) item;
						ward.setTriggerRadiusVisible(false);
					}
				}
			}
			break;
		}
		return true;
	}
	
	public void setStarfieldsOffset(int offsetX, int offsetY) {
		if (impl.getFarStarField() != null)
			DOM.setStyleAttribute(impl.getFarStarField().getElement(),
					"backgroundPosition",
				(int) Math.floor(offsetX / 24) + "px " +
				(int) Math.floor(offsetY / 24) + "px");
		if (impl.getNearStarField() != null)
			DOM.setStyleAttribute(impl.getNearStarField().getElement(),
					"backgroundPosition",
				(int) Math.floor(offsetX / 12) + "px " +
				(int) Math.floor(offsetY / 12) + "px");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void applySelection(Selection selection, boolean select) {
		switch (selection.getType()) {
		case Selection.TYPE_FLEET:
			for (long idSelection : selection.getIdSelections()) {
				FleetData fleetData = currentArea.getFleetById((int) idSelection);
				
				if (fleetData != null)
					((FleetItem) map.getItem(fleetData,
						FleetData.CLASS_NAME)).setSelected(select);
			}
			break;
		case Selection.TYPE_SYSTEM:
			StarSystemData systemData = currentArea.getSystemById(
				(int) selection.getFirstIdSelection());
			
			if (systemData != null)
				((StarSystemItem) map.getItem(systemData,
					StarSystemData.CLASS_NAME)).setSelected(select);
			break;
		case Selection.TYPE_SPACE_STATION:
			SpaceStationData spaceStationData = currentArea.getSpaceStationById(
				(int) selection.getFirstIdSelection());
			
			if (spaceStationData != null)
				((SpaceStationItem) map.getItem(spaceStationData,
						SpaceStationData.CLASS_NAME)).setSelected(select);
			break;
		case Selection.TYPE_STRUCTURE:
			StructureData structureData = currentArea.getStructureById(
				selection.getFirstIdSelection());
			
			if (structureData != null)
				((StructureItem) map.getItem(structureData,
						StructureData.CLASS_NAME)).setSelected(select);
			break;
		}
	}
}
