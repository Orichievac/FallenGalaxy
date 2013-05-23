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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.BlinkUpdater;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AllyInfluenceData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.GalaxyAreaData;
import fr.fg.client.data.GalaxyJumpData;
import fr.fg.client.data.GalaxyMapData;
import fr.fg.client.data.GalaxyMarkerData;
import fr.fg.client.data.GalaxySectorData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.Map;
import fr.fg.client.map.ScrollController;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.core.Client;
import fr.fg.client.core.Tutorial;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.Utilities;
//import fr.fg.client.core.GalaxyMap.HyperspaceJump;
//import fr.fg.client.core.GalaxyMap.Location;
//import fr.fg.client.core.GalaxyMap.RootContainer;

public class GalaxyMap extends ActionCallbackAdapter
		implements DialogListener, KeyboardListener, Map, EventPreview, ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int MODE_DEFAULT = 1, MODE_HYPERSPACE = 2;
	
	private final static int VIEW_GALAXY = 1, VIEW_SECTOR = 2;
	
	private final static int
		MAP_SIZE = 1000,
		HALF_MAP_SIZE = MAP_SIZE / 2,
		MAP_SCALE = 10;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTextField searchField;
	
	private JSButton showGalaxyBt;
	
	private JSDialog infoDialog;
	
	private AbsolutePanel rootContainer, mapContainer;
	
	private GalaxyMapData galaxyMap;
	
	private int viewX, viewY;
	
	private int view;
	
	private int currentSectorId;
	
	private Action downloadAction;
	
	private int mode;
	
	private int showSector;
	
	private boolean showGalaxy;
	
	private BlinkUpdater updater;
	
	private int displayedAreaId, displayedSectorId;
	
	private ScrollController controller;
	
	//private AbsolutePanel influencePanel;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public GalaxyMap() {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		this.viewX = 0;
		this.viewY = 0;
		this.showSector = -1;
		this.showGalaxy = false;
		
		mapContainer = new AbsolutePanel();
		mapContainer.setSize(MAP_SIZE + "px", MAP_SIZE + "px");
		mapContainer.getElement().getStyle().setProperty("margin",
				-HALF_MAP_SIZE + "px 0 0 " + -HALF_MAP_SIZE + "px");
		mapContainer.getElement().setAttribute("unselectable", "on");
		mapContainer.addStyleName("galaxyMap");
		
		rootContainer = new RootContainer();
		rootContainer.add(mapContainer);
		
		controller = new ScrollController(this, rootContainer);
		controller.setEnabled(false);
		
		/*influencePanel = new AbsolutePanel();
		influencePanel.getElement().setId("influencePanel");*/
		
		// Champ de recherche de secteurs / quadrants
		JSLabel searchLabel = new JSLabel();
		searchLabel.addStyleName("iconSearch");
		searchLabel.setPixelWidth(31);
		
		searchField = new JSTextField();
		searchField.setPixelWidth(199);
		searchField.addKeyboardListener(this);
		
		showGalaxyBt = new JSButton("Afficher la galaxie");
		showGalaxyBt.setPixelWidth(150);
		showGalaxyBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(searchLabel);
		layout.addComponent(searchField);
		layout.addRow();
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(showGalaxyBt);
		
		infoDialog = new JSDialog("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"iconMap\"/> " +
				messages.galaxyMap(), false, true, true);
		infoDialog.setComponent(layout);
		infoDialog.setLocation(20, 20, false);
		infoDialog.addDialogListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void show(int mode) {
		if (downloadAction != null && downloadAction.isPending())
			return;
		
		this.mode = mode;
		downloadAction = new Action("getgalaxy", Action.NO_PARAMETERS, this);
	}
	
	public void showGalaxy(int mode) {
		this.showGalaxy = true;
		
		show(mode);
	}
	
	public void showSector(int mode, int sector) {
		this.showSector = sector;
		
		show(mode);
	}
	
	public void hide() {
		if (updater != null) {
			TimerManager.unregister(updater);
			updater = null;
		}
		
		EventManager.removeEventHook(this);
		
		controller.setEnabled(false);
		Client.getInstance().getAreaContainer(
				).getScrollController().setEnabled(true);
		
		galaxyMap = null;
		mapContainer.clear();
		
		Client.getInstance().getAreaContainer().setVisible(false);
		Client.getInstance().getAreaContainer().getMap().setVisible(true);
		Client.getInstance().getAreaContainer().setNebula(
				Client.getInstance().getAreaContainer().getArea().getNebula(), false);
		Client.getInstance().getAreaContainer().getMap().setView(
				Client.getInstance().getAreaContainer().getMap().getView());
		Client.getInstance().getDialogManager().hide();
		
		infoDialog.setVisible(false);
		
		Client.getInstance().setFullScreenMode(false);
		
		searchField.setFocus(false);
	}
	
	public double getZoom() {
		return 1;
	}
	
	public Point getView() {
		return new Point(viewX, viewY);
	}
	
	public void setView(Point location) {
		int x = location.getX();
		int y = location.getY();
		
		// Vérifie que la vue reste dans les limites de la carte
		if (x < -HALF_MAP_SIZE)
			x = -HALF_MAP_SIZE;
		if (y < -HALF_MAP_SIZE)
			y = -HALF_MAP_SIZE;
		if (x > HALF_MAP_SIZE)
			x = HALF_MAP_SIZE;
		if (y > HALF_MAP_SIZE)
			y = HALF_MAP_SIZE;
		
		this.viewX = x;
		this.viewY = y;
		
		// Déplace la carte de la galaxie
		mapContainer.getElement().getStyle().setProperty(
				"left", -viewX + (OpenJWT.getClientWidth() / 2) + "px");
		mapContainer.getElement().getStyle().setProperty(
				"top", -viewY + (OpenJWT.getClientHeight() / 2) + "px");
		
		if (view == VIEW_SECTOR)
			Client.getInstance().getAreaContainer().setStarfieldsOffset(
					-2 * viewX, -2 * viewY);
	}
	
	public void dialogClosed(Widget sender) {
		hide();
	}
	
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		if (keyCode == 27)
			return;
		
		String text = searchField.getText().toLowerCase();
		
		if (text.length() == 0)
			return;
		
		if (view == VIEW_GALAXY) {
			for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
				GalaxySectorData sector = galaxyMap.getSectorAt(i);
				
				if (sector.getName().toLowerCase().startsWith(text)) {
					setView(new Point(sector.getX() * MAP_SCALE, -sector.getY() * MAP_SCALE));
					return;
				}
			}
		} else {
			for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
				GalaxySectorData sector = galaxyMap.getSectorAt(i);
				
				if (sector.getId() == currentSectorId) {
					for (int j = 0; j < sector.getAreasCount(); j++) {
						GalaxyAreaData area = sector.getAreaAt(j);
						
						if (!area.getVisibility().equals("none") &&
								!area.getVisibility().equals("unknown") &&
								area.getName().toLowerCase().startsWith(text)) {
							setView(new Point(area.getX() * MAP_SCALE, -area.getY() * MAP_SCALE));
							return;
						}
					}
				}
			}
		}
	}

	public void onSuccess(AnswerData data) {
		Client.getInstance().setFullScreenMode(true);
		Client.getInstance().getFullScreenPanel().add(rootContainer);
		//Client.getInstance().getFullScreenPanel().add(influencePanel);
		
		infoDialog.setVisible(true);
		
		galaxyMap = data.getGalaxyMap();
		
		// Recherche l'identifiant du secteur dans lequel le secteur visualisé
		// se trouve
		if (showGalaxy) {
			displayGalaxy(0);
			showGalaxy = false;
		} else if (showSector != -1) {
			displaySector(showSector, 0);
			showSector = -1;
		} else {
			int idArea = Client.getInstance().getAreaContainer().getArea().getId();
			int idSector = -1;
			
			search:for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
				GalaxySectorData sector = galaxyMap.getSectorAt(i);
				
				for (int j = 0; j < sector.getAreasCount(); j++) {
					if (sector.getAreaAt(j).getId() == idArea) {
						idSector = sector.getId();
						break search;
					}
				}
			}
			
			if (idSector == -1)
				displayGalaxy(-1);
			else
				displaySector(idSector, idArea);
		}
		
		controller.setEnabled(true);
		Client.getInstance().getAreaContainer().getScrollController().setEnabled(false);
		
		EventManager.addEventHook(this);
		
		downloadAction = null;
	}
	
	public boolean onEventPreview(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONKEYUP:
			if (event.getKeyCode() == 27)
				hide();
			break;
		}
		return false;
	}
	
	public void onClick(Widget sender) {
		if (sender == showGalaxyBt) {
			displayGalaxy(currentSectorId);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void displayGalaxy(int centerOnSectorId) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		if (updater != null) {
			TimerManager.unregister(updater);
			updater = null;
		}
		
		view = VIEW_GALAXY;
		
		mapContainer.clear();
		mapContainer.addStyleName("galaxy");
		//influencePanel.clear();
		
		Client.getInstance().getAreaContainer().setVisible(false);
		Client.getInstance().getAreaContainer().getMap().setVisible(true);
		
		infoDialog.setTitle("<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"iconMap\"/> " + messages.galaxyMap());
		
		// Recherche le secteur dans lequel se trouve le secteur courant
		GalaxySectorData displayedSector = null;
		
		IndexedAreaData currentArea = Client.getInstance().getAreaContainer().getArea();
		
		search:for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
			GalaxySectorData sector = galaxyMap.getSectorAt(i);
			
			for (int j = 0; j < sector.getAreasCount(); j++) {
				GalaxyAreaData area = sector.getAreaAt(j);
				
				if (area.getId() == currentArea.getId()) {
					displayedSector = sector;
					displayedSectorId = sector.getId();
					break search;
				}
			}
		}
		
		// Affiche les sauts hyperspatiaux
		for (int j = 0; j < galaxyMap.getJumpsCount(); j++) {
			GalaxyJumpData jump = galaxyMap.getJumpAt(j);
			
			int startX = jump.getStartX() * MAP_SCALE + HALF_MAP_SIZE;
			int startY = -jump.getStartY() * MAP_SCALE + HALF_MAP_SIZE;
			int endX = jump.getEndX() * MAP_SCALE + HALF_MAP_SIZE;
			int endY = -jump.getEndY() * MAP_SCALE + HALF_MAP_SIZE;
			
			int dx = endX - startX;
			int dy = endY - startY;
			
			int dist = (int) Math.sqrt(dx * dx + dy * dy);
			int dots = dist / 20;
			
			for (int k = 1; k < dots; k++) {
				HyperspaceJump jumpUI = new HyperspaceJump(
					(dots - k) * startX / dots + (k) * endX / dots,
					(dots - k) * startY / dots + (k) * endY / dots,
					3 * k / dots
				);
				
				mapContainer.add(jumpUI);
			}
		}
		
		// Affiche les quadrants
		for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
			GalaxySectorData sector = galaxyMap.getSectorAt(i);
			StringBuffer content = new StringBuffer();
			
			boolean showMessageMarker = false;
			boolean showContractMarker = false;
			
			for (int k = 0; k < sector.getMarkersCount(); k++) {
				GalaxyMarkerData marker = sector.getMarkerAt(k);
				content.append(formatMarker(marker));

				showMessageMarker |= !marker.isContractMarker();
				showContractMarker |= marker.isContractMarker();
			}
			
			String name = sector.getName();
			if (sector.getVisibility().equals("unknown"))
				name = messages.unknownSector();
			
			String toolTipText = "<div class=\"title\">" + name +
				" <span style=\"font-weight: normal;\">(" + sector.getX() + ", " +
				sector.getY() + ")</span></div><div class=\"justify\">" +
						"<span class=\"emphasize\"><img src=\"" +Config.getMediaUrl() +
						"images/misc/blank.gif\" " +
						"class=\"smiley s28\"/>"+sector.getLvlMin()+" - "+ sector.getLvlMax()+"</span></div>" + content;
			

			
			Location location = addLocation(sector.getId(), sector.getX(), sector.getY(),
				sector.getName(), Location.TYPE_SECTOR,
				sector.getVisibility(), showMessageMarker,
				showContractMarker, false, toolTipText);
			
			if (sector.getId() == displayedSector.getId()) {
				BlinkUpdater updater = new BlinkUpdater(location.getElement(), 500, 350);
				TimerManager.register(updater);
			}
		}
		
		if (centerOnSectorId == -1) {
			setView(new Point(0, 0));
		} else {
			for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
				GalaxySectorData sector = galaxyMap.getSectorAt(i);
				
				if (sector.getId() == currentSectorId) {
					setView(new Point(sector.getX() * MAP_SCALE, -sector.getY() * MAP_SCALE));
					break;
				}
			}
		}
		
		searchField.setText("");
		searchField.setFocus(true);
		
		if (mode == MODE_DEFAULT) {
			Client.getInstance().getDialogManager().hide();
			Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_GALAXY_MAP);
		}
	}
	
	private void displaySector(int idSector, int centerOnAreaId) {
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		if (updater != null) {
			TimerManager.unregister(updater);
			updater = null;
		}
		
		view = VIEW_SECTOR;
		currentSectorId = idSector;
		
		mapContainer.clear();
		mapContainer.removeStyleName("galaxy");
		//influencePanel.clear();
		
		// Affiche les secteurs
		for (int i = 0; i < galaxyMap.getSectorsCount(); i++) {
			GalaxySectorData sector = galaxyMap.getSectorAt(i);
			
			if (sector.getId() == idSector) {
				BaseWidget background = new BaseWidget();
				background.getElement().getStyle().setProperty("position", "absolute");
				background.getElement().getStyle().setProperty("top", "0");
				background.getElement().getStyle().setProperty("left", "0");
				background.getElement().getStyle().setProperty("bottom", "0");
				background.getElement().getStyle().setProperty("right", "0");
				background.getElement().getStyle().setProperty("background",
						"url('" + Config.getServerUrl() + "territory/" + idSector + "-" + sector.getTerritoryHash() + ".png') center no-repeat");
				
				mapContainer.add(background, 0, 0);
				
				/*OutlineText text = TextManager.getText(
					"<div class=\"influence\">" +
					"<div class=\"header\">Valeur stratégique</div>" +
					"<div style=\"margin-bottom: 20px;\">" +
					sector.getStrategicValue() + " points</div>" +
					"</div>");
				influencePanel.add(text);
				
				ToolTipManager.getInstance().register(text.getElement(),
					"<div class=\"title\">Valeur stratégique</div>" +
					"<div class=\"justify\">La valeur stratégique mesure l'intérêt " +
					"et l'activité des quadrants. Plus les richesses naturelles " +
					"d'un quadrant sont élevées et plus il y a de systèmes colonisés, " +
					"plus la valeur stratégique augmente.</div>", 200);
				
				String influences = "<div class=\"influence\">" +
					"<div class=\"header\">Influence</div>";
				
				if (sector.getAllyInfluencesCount() > 0) {
					ArrayList<AllyInfluenceData> allyInfluences =
						new ArrayList<AllyInfluenceData>();
					
					for (int j = 0; j < sector.getAllyInfluencesCount(); j++)
						allyInfluences.add(sector.getAllyInfluenceAt(j));
					
					Collections.sort(allyInfluences, new Comparator<AllyInfluenceData>() {
						public int compare(AllyInfluenceData a1,
								AllyInfluenceData a2) {
							if (a1.getInfluence() > a2.getInfluence())
								return -1;
							else if (a1.getInfluence() < a2.getInfluence())
								return 1;
							return a1.getAllyName().compareToIgnoreCase(a2.getAllyName());
						}
					});
					
					for (AllyInfluenceData allyInfluence : allyInfluences) {
						influences += "<div>" + allyInfluence.getAllyName() + " - " +
							(int) Math.floor(sector.getStrategicValue() *
							allyInfluence.getInfluence()) + "&nbsp;points&nbsp;(" +
							(int) Math.floor(allyInfluence.getInfluence() * 100) + "%)</div>";
					}
				} else {
					influences += "<div>Aucune alliance n'a d'influence dans le quadrant.</div>";
				}
				
				influences += "</div>";
				
				text = TextManager.getText(influences);
				influencePanel.add(text);
				
				ToolTipManager.getInstance().register(text.getElement(),
					"<div class=\"title\">Influence</div>" +
					"<div class=\"justify\">L'influence détermine le nombre de " +
					"points de valeur stratégique reçus par chaque alliance dans le " +
					"quadrant. Chaque point de valeur stratégique augmente la " +
					"production de ressources, de crédits et la recherche des " +
					"systèmes. Construisez ou améliorez des stations spatiales " +
					"pour augmenter l'influence de votre alliance dans le quadrant.</div>", 200);
				*/
				Client.getInstance().getAreaContainer().setNebula(sector.getNebula(), true);
				
				infoDialog.setTitle("<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"iconMap\"/> " +
					messages.areaMap(sector.getName()));
				
				IndexedAreaData displayedArea =
					Client.getInstance().getAreaContainer().getArea();
				
				displayedAreaId = displayedArea.getId();
				
				// Sauts hyperspatiaux
				for (int j = 0; j < sector.getJumpsCount(); j++) {
					GalaxyJumpData jump = sector.getJumpAt(j);
					
					int startX = jump.getStartX() * MAP_SCALE + HALF_MAP_SIZE;
					int startY = -jump.getStartY() * MAP_SCALE + HALF_MAP_SIZE;
					int endX = jump.getEndX() * MAP_SCALE + HALF_MAP_SIZE;
					int endY = -jump.getEndY() * MAP_SCALE + HALF_MAP_SIZE;
					
					int dx = endX - startX;
					int dy = endY - startY;
					
					int dist = (int) Math.sqrt(dx * dx + dy * dy);
					int dots = dist / 20;
					
					for (int k = 1; k < dots; k++) {
						HyperspaceJump jumpUI = new HyperspaceJump(
							(dots - k) * startX / dots + (k) * endX / dots,
							(dots - k) * startY / dots + (k) * endY / dots,
							3 * k / dots
						);
						
						mapContainer.add(jumpUI);
					}
				}
				
				for (int j = 0; j < sector.getAreasCount(); j++) {
					GalaxyAreaData area = sector.getAreaAt(j);
					
					StringBuffer content = new StringBuffer();
					boolean showMessageMarker = false;
					boolean showContractMarker = false;
					
					
					if (!area.getVisibility().equals("unknown"))
						content.append("<div class=\"justify emphasize\" style=\"color:#01F000;\">" +
							"Secteur "+area.getType()+"</div>");
					
					if (!area.getVisibility().equals("unknown") && area.getProduct() != 0)
						content.append("<div class=\"justify emphasize\"><img src=\"" +
							Config.getMediaUrl() + "images/misc/blank.gif\" " +
							"class=\"galaxyProduct\" style=\"background-position: -" +
							(11 * area.getProduct()) + "px -2734px\"/> " +
							dynamicMessages.getString("product" +
							area.getProduct()) + "</div>");
					
					
					if (area.getHyperspaceCoef() < 1)
						content.append("<div class=\"justify\"><img src=\"" +
							Config.getMediaUrl() + "images/misc/blank.gif\" " +
							"class=\"galaxyHyperspaceBonus\"/> Durée sauts " +
							"hyperspatiaux <span class=\"emphasize\">-" +
							(100 - (int) Math.floor(
							area.getHyperspaceCoef() * 100)) + "%</span></div>");
					
					if(area.getIntType()==4){

						int levelMin = sector.getLvlMin()+5;
						content.append("<div class=\"justify\">" +
						"<span class=\"emphasize\"><img src=\"" +Config.getMediaUrl() +
						"images/misc/blank.gif\" " +
						"class=\"smiley s28\"/>"+levelMin+" - "+ sector.getLvlMax()+"</span></div>");
						
					}
					//Secteur pirate X0
					else if(area.getIntType()==1)
					{
						int lvlMax = sector.getLvlMin()+5;
						content.append("<div class=\"justify\">" +
						"<span class=\"emphasize\"><img src=\"" +Config.getMediaUrl() +
						"images/misc/blank.gif\" " +
						"class=\"smiley s28\"/>"+sector.getLvlMin()+" - "+ lvlMax +"</span></div>");
						
					}
					
					for (int k = 0; k < sector.getMarkersCount(); k++) {
						GalaxyMarkerData marker = sector.getMarkerAt(k);
						
						if (area.getId() == marker.getIdArea()) {
							content.append(formatMarker(marker));
							showMessageMarker |= !marker.isContractMarker();
							showContractMarker |= marker.isContractMarker();
						}
					}
					
					String name = area.getName();
					if (area.getVisibility().equals("unknown"))
						name = messages.unknownArea();
					
					//TODO
					String toolTipText = "<div class=\"title\">" + name +
						" <span style=\"font-weight: normal;\">(" + area.getX() + ", " +
						area.getY() + ")</span></div>" + content;
					
					Location location = addLocation(area.getId(), area.getX(), area.getY(),
							area.getName(), Location.TYPE_AREA,
							area.getVisibility(), showMessageMarker,
							showContractMarker,
							area.getHyperspaceCoef() < 1, toolTipText);
					
					if (area.getId() == displayedArea.getId()) {
						updater = new BlinkUpdater(location.getElement(), 500, 350);
						TimerManager.register(updater);
					}
					
					if (centerOnAreaId == area.getId())
						setView(new Point(area.getX() * MAP_SCALE, -area.getY() * MAP_SCALE));
				}
				
				break;
			}
		}
		
		if (centerOnAreaId == -1)
			setView(new Point(0, 0));
		
		Client.getInstance().getAreaContainer().getMap().setVisible(false);
		Client.getInstance().getAreaContainer().setVisible(true);
		
		searchField.setText("");
		searchField.setFocus(true);
		
		if (mode == MODE_DEFAULT) {
			Client.getInstance().getDialogManager().hide();
			Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_SECTOR_MAP);
		}
	}
	
	private Location addLocation(int id, int x, int y, String name, int type,
			String visibility, boolean messageMarker, boolean contractMarker,
			boolean hyperspaceBonus, String toolTipText) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		// Image du secteur / quadrant
		Location location = new Location(id,
				x * MAP_SCALE + HALF_MAP_SIZE,
				-y * MAP_SCALE + HALF_MAP_SIZE,
				type,
				visibility);
		mapContainer.add(location);
		
		// Nom du secteur / quadrant
		if (visibility.equals("unknown"))
			name = type == Location.TYPE_AREA ?
					messages.unknownArea() : messages.unknownSector();
		name = name.toUpperCase().replace(" ", "&nbsp;");
		
		OutlineText text = TextManager.getText(
			"<div class=\"mapLocationName\" unselectable=\"on\">" + name + "<br/>" +
			(hyperspaceBonus ? "<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"galaxyHyperspaceBonus\"/>" : "") +
			(contractMarker ? "<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"galaxyMissionMarker\"/>" : "") + 
			(messageMarker ? "<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"galaxyMarker\"/>" : "") + "</div>");
		location.add(text);
		
		int width = location.getOffsetWidth();
		text.getElement().getStyle().setProperty("width", width + 10 + "px");
		
		ToolTipManager.getInstance().register(location.getElement(), toolTipText, 240);
		
		return location;
	}
	
	private String formatMarker(GalaxyMarkerData marker) {
		if (marker.isContractMarker()) {
			return "<div class=\"justify\"><img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"galaxyMissionMarker\"/>" +
				" <span class=\"missionMarkerName\"><b>" +
				marker.getContract() + "</b></span> - " + marker.getMessage();
		} else {
			return "<div class=\"justify\"><img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"galaxyMarker\"/> <span class=\"owner-" +
				marker.getTreaty() + "\"><b>" + marker.getOwner() + "</b>" +
				(marker.hasAlly() ? " (" + marker.getAlly() + ")" : "") +
				"</span> - " + Utilities.parseSmilies(
				marker.getMessage()) + "</div>";
		}
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class Location extends AbsolutePanel {
		// --------------------------------------------------- CONSTANTES -- //
		
		public final static int TYPE_AREA = 1, TYPE_SECTOR = 2;
		
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int id;
		
		private int type;
		
		private String visibility;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Location(int id, int x, int y, int type, String visibility) {
			this.id = id;
			this.type = type;
			this.visibility = visibility;
			
			setStyleName("mapLocation m" + (type == TYPE_AREA ? "a" : "s") +
					"-" + visibility);
			
			getElement().getStyle().setProperty("left", x + "px");
			getElement().getStyle().setProperty("top",  y + "px");
			getElement().setAttribute("unselectable", "on");
			
			sinkEvents(Event.ONCLICK | Event.ONMOUSEWHEEL | Event.ONMOUSEDOWN);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
			
			switch (event.getTypeInt()) {
			case Event.ONMOUSEWHEEL:
				int wheelDelta = DOM.eventGetMouseWheelVelocityY(event);
				
				if (wheelDelta < 0)
					actionPerformed();
			case Event.ONMOUSEDOWN:
				if (event.getButton() != Event.BUTTON_RIGHT &&
						event.getButton() != 0)
					actionPerformed();
				break;
			case Event.ONCLICK:
				if (event.getButton() != Event.BUTTON_RIGHT &&
						event.getButton() != 0)
					actionPerformed();
				break;
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private void actionPerformed() {
			if (!Client.getInstance().isFullScreenMode())
				return;
			
			switch (mode) {
			case MODE_DEFAULT:
				switch (type) {
				case TYPE_AREA:
					if (!visibility.equals("unknown")) {
						Client.getInstance().getAreaContainer().setIdArea(id);
						hide();
					}
					break;
				case TYPE_SECTOR:
					if (!visibility.equals("unknown"))
						displaySector(id, -1);
					break;
				}
				break;
			case MODE_HYPERSPACE:
				switch (type) {
				case TYPE_AREA:
					hide();
					
					if (id != displayedAreaId) {
						long[] idSelectedFleets = SelectionManager.getInstance(
								).getIdSelectedFleets();
						
						HashMap<String, String> params = new HashMap<String, String>();
						for (int i = 0; i < idSelectedFleets.length; i++)
							params.put("fleet" + i, String.valueOf(idSelectedFleets[i]));
						params.put("type", String.valueOf("area"));
						params.put("target", String.valueOf(id));
						
						new Action("hyperspace", params, new ActionCallbackAdapter() {
							public void onSuccess(AnswerData data) {
								SelectionManager.getInstance().setNoSelection();
								UpdateManager.UPDATE_CALLBACK.onSuccess(data);
							}
						});
					}
					break;
				case TYPE_SECTOR:
					if (id == displayedSectorId) {
						displaySector(id, -1);
					} else {
						hide();
						
						if (id != displayedSectorId) {
							long[] idSelectedFleets = SelectionManager.getInstance(
								).getIdSelectedFleets();
							
							HashMap<String, String> params = new HashMap<String, String>();
							for (int i = 0; i < idSelectedFleets.length; i++)
								params.put("fleet" + i, String.valueOf(idSelectedFleets[i]));
							params.put("type", String.valueOf("sector"));
							params.put("target", String.valueOf(id));
							
							new Action("hyperspace", params, new ActionCallbackAdapter() {
								public void onSuccess(AnswerData data) {
									SelectionManager.getInstance().setNoSelection();
									UpdateManager.UPDATE_CALLBACK.onSuccess(data);
								}
							});
						}
					}
					break;
				}
				break;
			}
		}
	}
	
	private class HyperspaceJump extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public HyperspaceJump(int x, int y, int type) {
			setElement(DOM.createDiv());
			setStyleName("hyperspaceJump hyperspaceJump-" + type);
			
			getElement().getStyle().setProperty("left", x + "px");
			getElement().getStyle().setProperty("top",  y + "px");
			getElement().setAttribute("unselectable", "on");
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class RootContainer extends AbsolutePanel {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public RootContainer() {
			setStyleName("galaxyMapContainer");
			getElement().getStyle().setProperty("backgroundImage",
					"url('" + Config.getMediaUrl() + "images/misc/blank.gif')");
			getElement().setAttribute("unselectable", "on");
			
			sinkEvents(Event.ONCLICK | Event.ONMOUSEDOWN | Event.ONMOUSEMOVE |
					Event.ONMOUSEOUT | Event.ONMOUSEWHEEL);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public void onBrowserEvent(Event event) {
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			super.onBrowserEvent(event);
			
			switch (event.getTypeInt()) {
			case Event.ONMOUSEDOWN:
			case Event.ONCLICK:
				// Retourne à la vue de la galaxie avec un clic droit
				if (event.getButton() == Event.BUTTON_RIGHT &&
						view == VIEW_SECTOR) {
					displayGalaxy(currentSectorId);
				}
				break;
			case Event.ONMOUSEWHEEL:
				int wheelDelta = DOM.eventGetMouseWheelVelocityY(event);
				
				if (wheelDelta > 0 && view == VIEW_SECTOR) {
					displayGalaxy(currentSectorId);
				}
				break;
			case Event.ONMOUSEMOVE:
				int tmpMouseX = OpenJWT.eventGetPointerX(event);
				int tmpMouseY = OpenJWT.eventGetPointerY(event);

				// Met à jour le tooltip avec les coordonnées de la souris
				ToolTipManager.getInstance().unregister(mapContainer.getElement());
				
				if (event.getTarget() == mapContainer.getElement()) {
					ToolTipManager.getInstance().register(mapContainer.getElement(),
						messages.coordinates((int) Math.round((tmpMouseX +
						viewX - (OpenJWT.getClientWidth() / 2)) / (double) MAP_SCALE) + " : " +
						(int) Math.round(-(tmpMouseY + viewY - (OpenJWT.getClientHeight() / 2)) /
						(double) MAP_SCALE)).replace(" ", "&nbsp;").replace("-", "˗"));
				}
				break;
			case Event.ONMOUSEOUT:
				ToolTipManager.getInstance().unregister(mapContainer.getElement());
				break;
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
