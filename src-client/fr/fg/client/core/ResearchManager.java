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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.ProgressBarUpdater;
import fr.fg.client.animation.ToolTipTextUpdater;
import fr.fg.client.animation.ToolTipTimeUpdater;
import fr.fg.client.data.AdvancementData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.ResearchData;
import fr.fg.client.data.ResearchedTechnologyData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.TechnologyData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.Map;
import fr.fg.client.map.ScrollController;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.CallbackHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class ResearchManager implements DialogListener, Map, EventPreview, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int TECH_TREE_WIDTH = 2000, TECH_TREE_HEIGHT = 1200;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int viewX, viewY;
	
	private JSDialog researchDialog;
	
	private JSLabel researchPointValue;
	
	private ScrollController controller;
	
	private AbsolutePanel rootContainer, mapContainer;
	
	private int halfWindowWidth, halfWindowHeight;
	
	private ResearchData researchData;
	
	private long researchLastUpdate;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ResearchManager() {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		this.viewX = 0;
		this.viewY = 0;
		
		mapContainer = new AbsolutePanel();
		mapContainer.setSize(TECH_TREE_WIDTH + "px", TECH_TREE_HEIGHT + "px");
		mapContainer.getElement().setAttribute("unselectable", "on");
		mapContainer.addStyleName("scrollarea");
		
		rootContainer = new AbsolutePanel();
		rootContainer.setStyleName("technologiesTree");
		rootContainer.getElement().setAttribute("unselectable", "on");
		rootContainer.add(mapContainer);
		
		controller = new ScrollController(this, rootContainer);
		controller.setEnabled(false);
		
		// Recherche totale générée par le joueur
		JSLabel researchPointLabel = new JSLabel("Recherche");
		researchPointLabel.setPixelWidth(100);
		researchPointValue = new JSLabel();
		researchPointValue.setPixelWidth(120);
		researchPointValue.setAlignment(JSLabel.ALIGN_RIGHT);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(researchPointLabel);
		layout.addComponent(researchPointValue);
		
		researchDialog = new JSDialog("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"iconResearch\"/> " +
				messages.research(), false, true, true);
		researchDialog.setComponent(layout);
		researchDialog.setLocation(20, 20, false);
		researchDialog.addDialogListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getShipAvailableAbilities(int shipId) {
		return getShipAvailableAbilities(ShipData.SHIPS[shipId]);
	}
	
	public int getShipAvailableAbilities(ShipData ship) {
		int availableAbilities = 0;
		
		for (int i = 0; i < ship.getAbilities().length; i++) {
			int[] requirements = ship.getAbilities()[i].getRequirements();
			boolean available = true;
			
			for (int requirement : requirements)
				if (!hasResearchedTechnology(requirement)) {
					available = false;
					break;
				}
			
			if (available)
				availableAbilities |= 1 << i;
		}
		
		return availableAbilities;
	}
	
	public void show() {
		updateUI();
		
		// Passe en plein écran
		Client.getInstance().setFullScreenMode(true);
		Client.getInstance().getFullScreenPanel().add(rootContainer);
		
		halfWindowWidth = Window.getClientWidth() / 2;
		halfWindowHeight = Window.getClientHeight() / 2;
		
		// Centre la vue sur la technologie en cours
		int currentTechnologyId = researchData.getPendingTechnologiesCount() > 0 ?
			researchData.getPendingTechnologyAt(0) : 0;
		
		if (currentTechnologyId != 0) {
			TechnologyData currentTechnology =
				TechnologyData.getTechnologyById(currentTechnologyId);
			setView(new Point(currentTechnology.getX() - 80,
					currentTechnology.getY() - 15));
		} else {
			setView(new Point(halfWindowWidth - 200, halfWindowHeight - 200));
		}
		
		controller.setEnabled(true);
		Client.getInstance().getAreaContainer().getScrollController().setEnabled(false);
		
		researchDialog.setVisible(true);
		
		EventManager.addEventHook(this);
		
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_RESEARCH);
		
		updateResearches();
	}
	
	public void hide() {
		EventManager.removeEventHook(this);
		
		researchDialog.setVisible(false);
		
		mapContainer.clear();
		
		controller.setEnabled(false);
		Client.getInstance().getAreaContainer().getScrollController().setEnabled(true);
		
		Client.getInstance().setFullScreenMode(false);
	}
	
	public void setResearches(ResearchData researchData) {
		this.researchData = researchData;
		this.researchLastUpdate = Utilities.getCurrentTime();
		
		Client.getInstance().getToolBar().blinkResearch(
			researchData.getPendingTechnologiesCount() == 0 &&
			researchData.getResearchedTechnologiesCount() <
			TechnologyData.TECHNOLOGIES.length);
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
		if (x < halfWindowWidth - 200)
			x = halfWindowWidth - 200;
		if (y < halfWindowHeight - 200)
			y = halfWindowHeight - 200;
		if (x > TECH_TREE_WIDTH - halfWindowWidth + 200)
			x = TECH_TREE_WIDTH - halfWindowWidth + 200;
		if (y > TECH_TREE_HEIGHT - halfWindowHeight + 200)
			y = TECH_TREE_HEIGHT - halfWindowHeight + 200;
		
		this.viewX = x;
		this.viewY = y;
		
		// Déplace la carte de la galaxie
		mapContainer.getElement().getStyle().setProperty(
				"left", -viewX + halfWindowWidth + "px");
		mapContainer.getElement().getStyle().setProperty(
				"top", -viewY + halfWindowHeight + "px");
		
		rootContainer.getElement().getStyle().setProperty(
				"backgroundPosition", (-viewX / 2) + "px " + (-viewY / 2) + "px");
	}

	public void dialogClosed(Widget sender) {
		hide();
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
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		setResearches(data.getResearch());
		updateUI();
	}
	
	public boolean hasResearchedTechnology(int idTechnology) {
		for (int j = 0; j < researchData.getResearchedTechnologiesCount(); j++) {
			ResearchedTechnologyData researchedTechData =
				researchData.getResearchedTechnologyAt(j);
			
			if (researchedTechData.getId() == idTechnology) {
				TechnologyData techData = TechnologyData.getTechnologyById(idTechnology);
				
				double progress;
				if (researchData.getPendingTechnologiesCount() > 0 &&
						researchData.getPendingTechnologyAt(0) == idTechnology) {
					progress = Math.min(1, (researchedTechData.getProgress() *
						getTechLength(techData) + (Utilities.getCurrentTime() -
						researchLastUpdate) * researchData.getResearchPoints() *
						BuildingData.RESEARCH_RATE) /
						getTechLength(techData));
				} else {
					progress = researchedTechData.getProgress();
				}
				
				return progress == 1;
			}
		}
		
		return false;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static long getTechLength(TechnologyData techData) {
		return (long) Math.ceil(techData.getLength() * Math.pow(.95,
			Client.getInstance().getAdvancementDialog().getAdvancementLevel(
				AdvancementData.TYPE_RESEARCH)));
	}
	
	private void updateUI() {
		researchPointValue.setText("<b>" + Formatter.formatNumber(
			researchData.getResearchPoints()) +
			"<img class=\"resource research\" src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" unselectable=\"on\"/></b> / min");
		
		mapContainer.clear();
		
		// Ajoute les technologies accessibles au joueurs à la liste des
		// technologies recherchés ou en cours de recherche
		loop:for (TechnologyData technology : TechnologyData.TECHNOLOGIES) {
			// Teste si le joueur a déjà recherché ou est en train de
			// rechercher la technologie
			for (int j = 0; j < researchData.getResearchedTechnologiesCount(); j++)
				if (researchData.getResearchedTechnologyAt(j).getId() == technology.getId())
					continue loop;
			
			// Teste si le joueur a développé toutes les technologies requises
			boolean available = true;
			for (int requirement : technology.getRequirements()) {
				boolean found = false;
				
				for (int k = 0; k < researchData.getResearchedTechnologiesCount(); k++)
					if (researchData.getResearchedTechnologyAt(k).getId() == requirement) {
						ResearchedTechnologyData researchedTechData =
							researchData.getResearchedTechnologyAt(k);
						TechnologyData techData = TechnologyData.getTechnologyById(
								researchedTechData.getId());
						
						found = true;
						
						double progress;
						if (researchData.getPendingTechnologiesCount() > 0 &&
								researchData.getPendingTechnologyAt(0) == requirement) {
							progress = Math.min(1, (researchedTechData.getProgress() *
								getTechLength(techData) + (Utilities.getCurrentTime() -
								researchLastUpdate) * researchData.getResearchPoints() *
								BuildingData.RESEARCH_RATE) /
								getTechLength(techData));
						} else {
							progress = researchedTechData.getProgress();
						}
						available = progress >= 1;
						break;
					}
				
				if (!found)
					available = false;
				if (!available)
					break;
			}
			
			// Ajoute la technologie a la liste des technologies du joueur
			if (available)
				addResearch(TechnologyData.getTechnologyById(technology.getId()),
						0, -1, researchData.getResearchPoints() *
						BuildingData.RESEARCH_RATE);
		}
		
		for (int i = 0; i < researchData.getResearchedTechnologiesCount(); i++) {
			ResearchedTechnologyData researchedTechData = researchData.getResearchedTechnologyAt(i);
			
			TechnologyData techData = TechnologyData.getTechnologyById(
					researchedTechData.getId());
			
			int queuePosition = -1;
			
			for (int j = 0; j < researchData.getPendingTechnologiesCount(); j++) {
				if (researchData.getPendingTechnologyAt(j) == techData.getId()) {
					queuePosition = j;
					break;
				}
			}
			
			double progress;
			if (queuePosition == 0) {
				progress = Math.min(1, (researchedTechData.getProgress() *
					getTechLength(techData) + (Utilities.getCurrentTime() -
					researchLastUpdate) * researchData.getResearchPoints() *
					BuildingData.RESEARCH_RATE) /
					getTechLength(techData));
			} else {
				progress = researchedTechData.getProgress();
			}
			
			addResearch(techData, progress, queuePosition,
					researchData.getResearchPoints() *
					BuildingData.RESEARCH_RATE);
		}
	}
	
	private void addResearch(TechnologyData technology, double progress,
			int queuePosition, double researchPoints) {
		Research r = new Research(technology, progress, queuePosition, researchPoints);
		mapContainer.add(r, technology.getX(), technology.getY());
		
		// Affiche les technologies nécessaires au développement de la
		// technologie sous forme de lien
		for (int j = 0; j < technology.getRequirements().length; j++) {
			TechnologyData requiredTechnology =
				TechnologyData.getTechnologyById(
					technology.getRequirements()[j]);
			
			// Début du lien
			int sx = requiredTechnology.getX() + 160;
			int sy = requiredTechnology.getY() + 13;
			
			// Fin du lien
			int ex = technology.getX();
			int ey = technology.getY() + 13;
			
			// Milieu du lien
			int mx = sx + 20;
			
			// Trace le lien
			mapContainer.add(new Line(sx, sy, mx, sy), Math.min(sx, mx), sy);
			mapContainer.add(new Line(mx, sy, mx, ey), mx, Math.min(sy, ey));
			mapContainer.add(new Line(mx, ey, ex, ey), Math.min(mx, ex), ey);
		}
	}
	
	private void startResearch(int researchId) {
		if (currentAction != null && currentAction.isPending())
			return;
		
		if (researchData.getPendingTechnologiesCount() > 0 &&
				researchData.getPendingTechnologyAt(0) == researchId)
			return;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("queue0", String.valueOf(researchId));
		params.put("queue1", "0");
		params.put("queue2", "0");
		
		currentAction = new Action("research/set", params, this);
	}
	
	private void queueResearch(int researchId) {
		if (currentAction != null && currentAction.isPending())
			return;
		
		for (int i = 0; i < researchData.getPendingTechnologiesCount(); i++)
			if (researchData.getPendingTechnologyAt(i) == researchId)
				return;
		
		int[] queue = new int[3];
		
		int i;
		for (i = 0; i < Math.min(2, researchData.getPendingTechnologiesCount()); i++)
			queue[i] = researchData.getPendingTechnologyAt(i);
		queue[i] = researchId;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("queue0", String.valueOf(queue[0]));
		params.put("queue1", String.valueOf(queue[1]));
		params.put("queue2", String.valueOf(queue[2]));
		
		currentAction = new Action("research/set", params, this);
	}
	
	private void updateResearches() {
		if (currentAction != null && currentAction.isPending())
			return;
		
		currentAction = new Action("research/get", Action.NO_PARAMETERS, this);
	}
	
	private class Research extends Widget implements ActionCallback, Callback {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int id;
		
		private double progress;
		
		private ToolTipTextUpdater timeUpdater;
		
		private ProgressBarUpdater progressBarUpdater;
		
		private CallbackHandler researchCallback;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Research(TechnologyData technology, double progress,
				int queuePosition, double researchPoints) {
			this.id = technology.getId();
			this.progress = progress;
			
			setElement(DOM.createDiv());
			setStyleName("technology" + (progress >= 1 ? " researched" : ""));
			getElement().setId("tech-" + technology.getId());
			
			String time;
			if (researchPoints > 0) {
				int remainingTime = (int) Math.ceil((1 - progress) *
					getTechLength(technology) / researchPoints);
				
				if (queuePosition == 0) {
					String id = ToolTipTextUpdater.generateId();
					
					time = "<span id=\"" + id + "\">" +
						Formatter.formatDate(remainingTime) + "</span>";
					
					timeUpdater = new ToolTipTimeUpdater(getElement(), id, remainingTime);
					TimerManager.register(timeUpdater);
					
					researchCallback = new CallbackHandler(this, 1000l * remainingTime);
					TimerManager.registerCallback(researchCallback);
				} else {
					time = Formatter.formatDate(remainingTime);
				}
			} else {
				time = "∞";
			}
			
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			// Sélectionne la technologie si c'est la technologie en cours de
			// recherche
			if (queuePosition == 0)
				addStyleName("current");
			
			getElement().setInnerHTML(
				"<div class=\"name\">" + (queuePosition != -1 ? queuePosition +
				1 + ". " : "") + technology.getName() + "</div>" +
				"<div class=\"progress\"><div class=\"currentProgress\" " +
				"style=\"width: " + (int) Math.floor(100 * progress) + "%;\"></div>" +
				"</div><div class=\"length\">" + (progress >= 1 ?
				messages.researchFinished() : messages.researchLength(time)) +
				"</div>");
			ToolTipManager.getInstance().register(getElement(), technology.getToolTip());
			
			if (queuePosition == 0 && researchPoints > 0) {
				progressBarUpdater = new ProgressBarUpdater(
					getElement().getFirstChildElement(
					).getNextSiblingElement().getFirstChildElement(),
					(int) Math.floor(getTechLength(technology) * progress / researchPoints),
					(int) (getTechLength(technology) / researchPoints));
				TimerManager.register(progressBarUpdater);
			}
			
			sinkEvents(Event.ONCLICK);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		protected void onUnload() {
			if (timeUpdater != null) {
				TimerManager.unregister(timeUpdater);
				timeUpdater.destroy();
				timeUpdater = null;
			}
			
			if (progressBarUpdater != null) {
				TimerManager.unregister(progressBarUpdater);
				progressBarUpdater.destroy();
				progressBarUpdater = null;
			}
			
			if (researchCallback != null) {
				TimerManager.unregisterCallback(researchCallback);
				researchCallback = null;
			}
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			switch (event.getTypeInt()) {
			case Event.ONCLICK:
				if (progress < 1) {
					if (event.getShiftKey() || event.getCtrlKey())
						queueResearch(id);
					else
						startResearch(id);
				}
				break;
			}
		}
		
		public void run() {
			updateResearches();
		}
		
		public void onFailure(String error) {
			ActionCallbackAdapter.onFailureDefaultBehavior(error);
		}
		
		public void onSuccess(AnswerData data) {
			setResearches(data.getResearch());
			updateUI();
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class Line extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Line(int x1, int y1, int x2, int y2) {
			setElement(DOM.createDiv());
			
			getElement().getStyle().setProperty("margin", "-1px 0 0 -1px");
			
			if (x1 == x2) {
				setWidth("3px");
				setHeight(Math.abs(y2 - y1) + 2 + "px");
			} else if (y1 == y2) {
				setWidth(Math.abs(x2 - x1) + 2 + "px");
				setHeight("3px");
			}
			
			getElement().getStyle().setProperty("backgroundColor", "#808080");
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
