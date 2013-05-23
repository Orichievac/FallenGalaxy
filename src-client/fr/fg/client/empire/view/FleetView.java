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
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.SkillData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.client.empire.View;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.UIMiniItem;
import fr.fg.client.map.impl.AreaMap;
import fr.fg.client.map.impl.MiniMap;
import fr.fg.client.map.item.FleetItem;
import fr.fg.client.map.miniitem.FleetMiniItem;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.CallbackHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipListener;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class FleetView extends View implements ToolTipListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static Callback UPDATE_FLEETS_CALLBACK = new Callback() {
		public void run() {
			EmpireUpdater.getInstance().queueFleetsUpdate();
		}
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private PlayerFleetData fleetData;
	
	private ProgressBarUpdater hyperspaceUpdater, hyperspaceOutUpdater,
		movementReloadUpdater;
	
	private ToolTipTimeUpdater hyperspaceTimeUpdater, hyperspaceOutTimeUpdater,
		movementReloadTimeUpdater, jumpReloadTimeUpdater, colonizationTimeUpdater,
		migrationTimeUpdater;
	
	private CallbackHandler hyperspaceCallback;
	
	private Element row1, row2, tag, shortcut, skillPoints;
	
	// ------------------------------------------------ CONSTRUCTEURS -- //
	
	public FleetView(PlayerFleetData fleetData) {
		this.fleetData = fleetData;
		
		setStyleName("view fleet");
		getElement().setAttribute("unselectable", "on");
		
		row1 = DOM.createDiv();
		row1.setClassName("row1");
		row1.setAttribute("unselectable", "on");
		
		row2 = DOM.createDiv();
		row2.setClassName("row2");
		row2.setAttribute("unselectable", "on");
		
		tag = DOM.createDiv();
		tag.setClassName("tag");
		tag.setAttribute("unselectable", "on");
		
		shortcut = DOM.createDiv();
		shortcut.setClassName("shortcut");
		shortcut.setAttribute("unselectable", "on");
		shortcut.getStyle().setProperty("display", "none");
		
		skillPoints = DOM.createDiv();
		skillPoints.setClassName("skillPoints");
		skillPoints.setAttribute("unselectable", "on");
		skillPoints.setAttribute("style", "visibility:hidden;");
		skillPoints.getStyle().setProperty("display", "none");
		
		getElement().appendChild(tag);
		getElement().appendChild(row1);
		getElement().appendChild(row2);
		getElement().appendChild(shortcut);
		getElement().appendChild(skillPoints);
		
		updateData(fleetData);
		
		sinkEvents(Event.ONCLICK | Event.ONDBLCLICK |
				Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// ----------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		PlayerFleetData newFleetData = (PlayerFleetData) newData;
		boolean updateData = false;
		
		// Modifications sur les liens offensifs
		if (fleetData.getOffensiveLinkedFleetId() != 0 &&
				newFleetData.getOffensiveLinkedFleetId() != 0) {
			for (int i = 0; i < fleetData.getSlotsCount(); i++)
				if (fleetData.getOffensiveLinkedCount(i) !=
						newFleetData.getOffensiveLinkedCount(i)) {
					updateData = true;
					break;
				}
		}
		
		// Modifications sur les liens défensifs
		if (fleetData.getDefensiveLinkedFleetId() != 0 &&
				newFleetData.getDefensiveLinkedFleetId() != 0) {
			for (int i = 0; i < fleetData.getSlotsCount(); i++)
				if (fleetData.getDefensiveLinkedCount(i) !=
						newFleetData.getDefensiveLinkedCount(i)) {
					updateData = true;
					break;
				}
		}
		
		if (updateData || fleetData.getVersion() != newFleetData.getVersion())
			updateData(newFleetData);
		
		this.fleetData = newFleetData;
	}
	
	public void onToolTipOpening(Element element) {
		if (element != getElement())
			return;
		
		if (hyperspaceTimeUpdater != null) {
			hyperspaceTimeUpdater.synchronize();
			TimerManager.register(hyperspaceTimeUpdater, TimerManager.SECOND_UNIT);
		}

		if (hyperspaceOutTimeUpdater != null) {
			hyperspaceOutTimeUpdater.synchronize();
			TimerManager.register(hyperspaceOutTimeUpdater, TimerManager.SECOND_UNIT);
		}

		if (movementReloadTimeUpdater != null) {
			movementReloadTimeUpdater.synchronize();
			TimerManager.register(movementReloadTimeUpdater, TimerManager.SECOND_UNIT);
		}

		if (jumpReloadTimeUpdater != null) {
			jumpReloadTimeUpdater.synchronize();
			TimerManager.register(jumpReloadTimeUpdater, TimerManager.SECOND_UNIT);
		}

		if (colonizationTimeUpdater != null) {
			colonizationTimeUpdater.synchronize();
			TimerManager.register(colonizationTimeUpdater, TimerManager.SECOND_UNIT);
		}
		
		if (migrationTimeUpdater != null) {
			migrationTimeUpdater.synchronize();
			TimerManager.register(migrationTimeUpdater, TimerManager.SECOND_UNIT);
		}
	}
	
	public void onToolTipClosed(Element element) {
		if (element != getElement())
			return;
		
		if (hyperspaceTimeUpdater != null)
			TimerManager.unregister(hyperspaceTimeUpdater);
		if (hyperspaceOutTimeUpdater != null)
			TimerManager.unregister(hyperspaceOutTimeUpdater);
		if (movementReloadTimeUpdater != null)
			TimerManager.unregister(movementReloadTimeUpdater);
		if (jumpReloadTimeUpdater != null)
			TimerManager.unregister(jumpReloadTimeUpdater);
		if (colonizationTimeUpdater != null)
			TimerManager.unregister(colonizationTimeUpdater);
		if (migrationTimeUpdater != null)
			TimerManager.unregister(migrationTimeUpdater);
	}
	
	public void destroy() {
		stopTimers();
		
		ToolTipManager.getInstance().removeToolTipListener(this);
		fleetData = null;
		row1 = null;
		row2 = null;
		tag = null;
		shortcut = null;
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			Client.getInstance().getAreaContainer().setIdArea(
				fleetData.getArea().getId(),
				new Point(fleetData.getX(), fleetData.getY()));
			break;
		case Event.ONDBLCLICK:
			IndexedAreaData area = Client.getInstance().getAreaContainer().getArea();
			if (area.getId() == fleetData.getArea().getId())
				SelectionManager.getInstance().selectFleet(fleetData.getId());
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
	
	public void update(){
		updateData(this.fleetData);
	}
	
	public void updateSelection(){
		// Sélection : Cadre blanc de sélection
		if(SelectionManager.getInstance().isFleetSelected(fleetData.getId())){
			setStyleName("view fleet selected");
		}else{
			setStyleName("view fleet");
		}
	}
	
	// --------------------------------------------- METHODES PRIVEES -- //
	
	private void stopTimers() {
		if (hyperspaceUpdater != null) {
			TimerManager.unregister(hyperspaceUpdater);
			hyperspaceUpdater.destroy();
			hyperspaceUpdater = null;
		}
		
		if (hyperspaceOutUpdater != null) {
			TimerManager.unregister(hyperspaceOutUpdater);
			hyperspaceOutUpdater.destroy();
			hyperspaceOutUpdater = null;
		}
		
		if (movementReloadUpdater != null) {
			TimerManager.unregister(movementReloadUpdater);
			movementReloadUpdater.destroy();
			movementReloadUpdater = null;
		}
		
		if (hyperspaceTimeUpdater != null) {
			TimerManager.unregister(hyperspaceTimeUpdater);
			hyperspaceTimeUpdater.destroy();
			hyperspaceTimeUpdater = null;
		}
		
		if (hyperspaceOutTimeUpdater != null) {
			TimerManager.unregister(hyperspaceOutTimeUpdater);
			hyperspaceOutTimeUpdater.destroy();
			hyperspaceOutTimeUpdater = null;
		}
		
		if (movementReloadTimeUpdater != null) {
			TimerManager.unregister(movementReloadTimeUpdater);
			movementReloadTimeUpdater.destroy();
			movementReloadTimeUpdater = null;
		}
		
		if (jumpReloadTimeUpdater != null) {
			TimerManager.unregister(jumpReloadTimeUpdater);
			jumpReloadTimeUpdater.destroy();
			jumpReloadTimeUpdater = null;
		}
		
		if (colonizationTimeUpdater != null) {
			TimerManager.unregister(colonizationTimeUpdater);
			colonizationTimeUpdater.destroy();
			colonizationTimeUpdater = null;
		}
		
		if (migrationTimeUpdater != null) {
			TimerManager.unregister(migrationTimeUpdater);
			migrationTimeUpdater.destroy();
			migrationTimeUpdater = null;
		}
		
		if (hyperspaceCallback != null) {
			TimerManager.unregisterCallback(hyperspaceCallback);
			hyperspaceCallback = null;
		}
	}
	
	private void updateData(PlayerFleetData fleetData) {
		stopTimers();
		ToolTipManager.getInstance().removeToolTipListener(this);
		
		setToolTipText(buildToolTipText(fleetData));
		
		row1.setInnerHTML("");
		row2.setInnerHTML("");
		
		int deludeLevel = fleetData.getSkillAt(3).getType() ==
			SkillData.SKILL_ULTIMATE_DELUDE ?
				fleetData.getSkillAt(3).getLevel() : -1;
		
		boolean inHyperspace =
			fleetData.getStartJumpRemainingTime() > 0 ||
			fleetData.getEndJumpRemainingTime() > 0;
		
		// Flotte en hyperespace ?
		int tagOffset = 0;
		if (inHyperspace) {
			int hyperspaceWidth =
				fleetData.getStartJumpRemainingTime() == 0 ? 100 :
				(int) Math.round(100 * (1 - (fleetData.getStartJumpRemainingTime() / (double) (
					fleetData.getLastMove() + fleetData.getStartJumpRemainingTime()))));
			int hyperspaceOutWidth = fleetData.getStartJumpRemainingTime() == 0 ?
				(int) Math.round(100 * (1 - (fleetData.getEndJumpRemainingTime() / (double) (
					(fleetData.getLastMove() + fleetData.getEndJumpRemainingTime()) / 2)))) : 0;
			tagOffset = 1;
			
			Element hyperspaceOutBar = DOM.createDiv();
			hyperspaceOutBar.setClassName("progressBar progressBar-hyperspaceOut");
			hyperspaceOutBar.setAttribute("unselectable", "on");
			Element hyperspaceOutProgress = DOM.createDiv();
			hyperspaceOutProgress.setClassName("currentProgress");
			hyperspaceOutProgress.setAttribute("unselectable", "on");
			hyperspaceOutProgress.getStyle().setProperty("width", hyperspaceOutWidth + "%");
			hyperspaceOutBar.appendChild(hyperspaceOutProgress);
			row1.appendChild(hyperspaceOutBar);
			
			Element hyperspaceBar = DOM.createDiv();
			hyperspaceBar.setClassName("progressBar progressBar-hyperspace");
			hyperspaceBar.setAttribute("unselectable", "on");
			Element hyperspaceProgress = DOM.createDiv();
			hyperspaceProgress.setClassName("currentProgress");
			hyperspaceProgress.setAttribute("unselectable", "on");
			hyperspaceProgress.getStyle().setProperty("width", hyperspaceWidth + "%");
			hyperspaceBar.appendChild(hyperspaceProgress);
			row1.appendChild(hyperspaceBar);
			
			if (hyperspaceWidth < 100) {
				// Timer pour le saut hyperspatial
				hyperspaceUpdater = new ProgressBarUpdater(hyperspaceProgress,
					fleetData.getLastMove(), fleetData.getLastMove() +
					fleetData.getStartJumpRemainingTime());
				TimerManager.register(hyperspaceUpdater, TimerManager.MINUTE_UNIT);
				
				hyperspaceCallback = new CallbackHandler(
					UPDATE_FLEETS_CALLBACK, 1000 *
					(fleetData.getStartJumpRemainingTime() + 1));
				TimerManager.registerCallback(hyperspaceCallback);
			} else {
				// Durée du passage en hyperespace
				int startJumpLength = (fleetData.getLastMove() +
					fleetData.getEndJumpRemainingTime()) / 2;
				
				// Timer pour la sortie d'hyperespace
				hyperspaceOutUpdater = new ProgressBarUpdater(hyperspaceOutProgress,
					fleetData.getLastMove() - startJumpLength, startJumpLength);
				TimerManager.register(hyperspaceOutUpdater, TimerManager.MINUTE_UNIT);
				
				hyperspaceCallback = new CallbackHandler(
					UPDATE_FLEETS_CALLBACK, 1000 * (fleetData.getEndJumpRemainingTime() + 1));
				TimerManager.registerCallback(hyperspaceCallback);
			}
		} else if (!fleetData.isDelude() || deludeLevel > 0) {
			int movementWidth = (fleetData.getMovement() < fleetData.getMovementMax() ?
				(int) Math.round(100 * (fleetData.getMovement() / (double) fleetData.getMovementMax())) : 100);
			
			if (fleetData.getMovement() == 0)
				tagOffset = 2;
			
			Element movementBar = DOM.createDiv();
			movementBar.setClassName("progressBar progressBar-movement");
			movementBar.setAttribute("unselectable", "on");
			Element movementProgress = DOM.createDiv();
			movementProgress.setClassName("currentProgress");
			movementProgress.setAttribute("unselectable", "on");
			movementProgress.getStyle().setProperty("width", movementWidth + "%");
			movementBar.appendChild(movementProgress);
			row1.appendChild(movementBar);
		}
		
		// Rechargement du mouvement
		if (!(inHyperspace || fleetData.getMovement() >=
				fleetData.getMovementMax())) {
			int movementReloadWidth = fleetData.getMovement() <
				fleetData.getMovementMax() ? (int) Math.round(
					100 * (fleetData.getLastMove() /
					(fleetData.getMovementReloadRemainingTime() +
					fleetData.getLastMove()))) : 100;
			
			Element movementReloadBar = DOM.createDiv();
			movementReloadBar.setClassName("progressBar progressBar-movementReload");
			movementReloadBar.setAttribute("unselectable", "on");
			Element movementReloadProgress = DOM.createDiv();
			movementReloadProgress.setClassName("currentProgress");
			movementReloadProgress.setAttribute("unselectable", "on");
			movementReloadProgress.getStyle().setProperty("width", movementReloadWidth + "%");
			movementReloadBar.appendChild(movementReloadProgress);
			row2.appendChild(movementReloadBar);
			
			int maxValue = fleetData.getLastMove() +
				fleetData.getMovementReloadRemainingTime();
			movementReloadUpdater = new ProgressBarUpdater(
				movementReloadProgress, fleetData.getLastMove(), maxValue);
			TimerManager.register(movementReloadUpdater,
				maxValue < 1800 ? TimerManager.SECOND_UNIT : TimerManager.MINUTE_UNIT);
		}
		
		if (fleetData.getShortcut() != -1) {
			OutlineText text = TextManager.getText(String.valueOf(fleetData.getShortcut()));
			
			shortcut.setInnerHTML("");
			shortcut.appendChild(text.getElement());
		}
		
		shortcut.getStyle().setProperty("display", fleetData.getShortcut() != -1 ? "" : "none");
		
		tag.getStyle().setProperty("backgroundPosition", -(30 *
			fleetData.getTag()) + "px -" + (tagOffset * 30 + 1063) + "px");
		
		// Plus indiquant que des points de compétence sont inutilisés
		if( fleetData.getSkillPoints() > 0){
			skillPoints.setAttribute("style", "visibility: visible;");
		}else{
			skillPoints.setAttribute("style", "visibility: hidden;");
		}
		
		// Sélection : Cadre blanc de sélection
		if(SelectionManager.getInstance().isFleetSelected(fleetData.getId())){
			setStyleName("view fleet selected");
		}else{
			setStyleName("view fleet");
		}
		
		if (hyperspaceTimeUpdater != null ||
				hyperspaceOutTimeUpdater != null ||
				movementReloadTimeUpdater != null ||
				jumpReloadTimeUpdater != null ||
				colonizationTimeUpdater != null ||
				migrationTimeUpdater != null)
			ToolTipManager.getInstance().addToolTipListener(this);
	}
	
	private String buildToolTipText(PlayerFleetData fleetData) {
		// Tooltip
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		StaticMessages staticMessages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		int deludeLevel = fleetData.getSkillAt(3).getType() ==
			SkillData.SKILL_ULTIMATE_DELUDE ?
				fleetData.getSkillAt(3).getLevel() : -1;
		
		String tooltip = "<div><span class=\"owner-player\"><b>" +
			fleetData.getName() + "</b></span></div>";
		
		if (fleetData.isDelude())
			tooltip += "<div style=\"font-weight: bold; color: #ffa205;\">Leurre</div>";
		
		// Affiche la puissance de la flotte
		tooltip += "<div>" + staticMessages.fleetPower(
			"<b class=\"owner-player\">" +
			fleetData.getPowerLevel() + "</b> <img src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" " +
			"class=\"stat s-power\" unselectable=\"on\"/>") + "</div>";
		
		// Furtivité
		if (fleetData.isStealth())
			tooltip += "<div style=\"color: red;\">Mode furtif</div>";
		
		// Mouvement automatique en cours
		if (fleetData.isScheduledMove())
			tooltip += "<div style=\"color: red;\">Déplacement auto<br/>en cours...</div>";
		
		if (fleetData.getStartJumpRemainingTime() != 0) {
			// Passage en hyperespace en cours
			String id = ToolTipTextUpdater.generateId();
			hyperspaceTimeUpdater = new ToolTipTimeUpdater(getElement(), id,
					fleetData.getStartJumpRemainingTime());
			tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
					staticMessages.enteringHyperspace("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			// Secteur d'arrivée du saut
			if (fleetData.getJumpTarget().length() > 0)
				tooltip += "<div style=\"color: red;\">Destination <b>" +
					fleetData.getJumpTarget() + "</b></div>";
		} else if (fleetData.getEndJumpRemainingTime() != 0) {
			String id = ToolTipTextUpdater.generateId();
			hyperspaceOutTimeUpdater = new ToolTipTimeUpdater(getElement(), id,
					fleetData.getEndJumpRemainingTime());
			
			tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
					staticMessages.leavingHyperspace("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (!fleetData.isDelude() || deludeLevel > 0) {
			tooltip += "<div>" + staticMessages.movement("<b>" +
					fleetData.getMovement() + "/" +
					fleetData.getMovementMax() + "</b>") + "</div>";
			
			if (fleetData.getMovement() < fleetData.getMovementMax() &&
					fleetData.getColonizationRemainingTime() == 0 && !fleetData.isMigrating()) {
				String id = ToolTipTextUpdater.generateId();
				movementReloadTimeUpdater = new ToolTipTimeUpdater(getElement(), id,
						fleetData.getMovementReloadRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.maxMovement("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			if (fleetData.getJumpReloadRemainingTime() != 0) {
				String id = ToolTipTextUpdater.generateId();
				jumpReloadTimeUpdater = new ToolTipTimeUpdater(
						getElement(), id, fleetData.getJumpReloadRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
					staticMessages.jumpReload("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		
		// Liens offensifs / défensifs
		if (fleetData.getOffensiveLinkedFleetId() != 0)
			tooltip += "<div style=\"color: #fa941d;\">" +
				staticMessages.offensiveLink() + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		if (fleetData.getDefensiveLinkedFleetId() != 0)
			tooltip += "<div style=\"color: #2ba7dc;\">" +
				staticMessages.defensiveLink() + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		
		// Colonisation / capture en cours
		if (fleetData.getColonizationRemainingTime() != 0) {
			String id = ToolTipTextUpdater.generateId();
			colonizationTimeUpdater = new ToolTipTimeUpdater(getElement(), id,
					fleetData.getColonizationRemainingTime());
			
			tooltip += "<div style=\"color: red;\">" + (fleetData.isCapturing() ? //$NON-NLS-1$
					staticMessages.systemCapture("<b id=\"" + id + "\"></b>") : //$NON-NLS-1$ //$NON-NLS-2$
					staticMessages.colonization("<b id=\"" + id + "\"></b>")) + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		// Migration en cours
		if (fleetData.isMigrating() == true) {
			String id = ToolTipTextUpdater.generateId();
			migrationTimeUpdater = new ToolTipTimeUpdater(getElement(), id,
					fleetData.getMovementReloadRemainingTime());
			
			tooltip += "<div style=\"color: red;\">" +
			staticMessages.systemMigration("<b id=\"" + id + "\"></b>")+"</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
					
			}
		
		for (int i = 0; i < fleetData.getSlotsCount(); i++) {
			SlotInfoData slotData = fleetData.getSlotAt(i);
			
			if (slotData.getId() != 0) {
				String position = " <span style=\"color: " +
					(slotData.isFront() ? "#7aff01" : "#ff7901") + "\">" +
					(slotData.isFront() ? "▲" : "▼")+ "</span>";
				
				String offensiveLinkedCount = "";
				if (fleetData.getOffensiveLinkedFleetId() != 0 &&
						fleetData.getOffensiveLinkedCount(i) > 0)
					offensiveLinkedCount = "&nbsp;<span style=\"color: #fa941d;\">+" +
						Formatter.formatNumber(fleetData.getOffensiveLinkedCount(i), true) + "</span>";
				
				String defensiveLinkedCount = "";
				if (fleetData.getDefensiveLinkedFleetId() != 0 &&
						fleetData.getDefensiveLinkedCount(i) > 0)
					defensiveLinkedCount = "&nbsp;<span style=\"color: #2ba7dc;\">+" +
						Formatter.formatNumber(fleetData.getDefensiveLinkedCount(i), true) + "</span>";
				
				tooltip += "<div><span class=\"owner-player\">" + //$NON-NLS-1$
					Formatter.formatNumber(slotData.getCount(), true) + "</span>&nbsp;" + //$NON-NLS-1$
					dynamicMessages.getString("ship" + (slotData.getCount() > 1 ? //$NON-NLS-1$
					"s" : "") + slotData.getId()) + position + offensiveLinkedCount +
					defensiveLinkedCount + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		
		// Niveau et expérience
		if (!fleetData.isDelude()) {
			int level = fleetData.getFleetLevel();
			double levelXp = FleetData.getFleetLevelXp(level);
			int width;
			
			if (level < 15) {
				double nextLevelXp = FleetData.getFleetLevelXp(level + 1);
				width = (int) Math.floor((fleetData.getXp() - levelXp) * 80 /
					(nextLevelXp - levelXp));
			} else {
				width = 80;
			}
			
			tooltip += "<div class=\"xp-mini\" style=\"margin: 5px 0 2px 10px\">" + //$NON-NLS-1$
				"<div class=\"current-xp-mini\" style=\"width: " + //$NON-NLS-1$
					width + "px\"></div>" + //$NON-NLS-1$
				"<div class=\"name\">" + staticMessages.level(level) + "</div></div>"; //$NON-NLS-1$ //$NON-NLS-2$
			
			// Compétences
			String skills = ""; //$NON-NLS-1$
			int skillsCount = 0;
			int skillPoints = 0;
			for (int i = 0; i < fleetData.getSkillsCount(); i++) {
				SkillData skillData = fleetData.getSkillAt(i);
				
				if (skillData.getType() != 0) {
					skills += "<div class=\"miniSkill" + (skillData.getType() > 10 ? " ultimate" : "") + "\" style=\"background-position: -" + //$NON-NLS-1$
						(20 * (skillData.getType() > 10 ? skillData.getType() - 11 : skillData.getType())) +
						"px -" + (skillData.getType() > 10 ? 1268 : 771) + "px\">" + //$NON-NLS-1$
						(skillData.getLevel() + 1) + "</div>"; //$NON-NLS-1$
					skillsCount++;
					skillPoints += skillData.getLevel() + 1;
				}
			}
			if (skillPoints < level) {
				skills += "<div class=\"miniSkillPoint\">" + (level - skillPoints) + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
				skillsCount++;
			}
			tooltip += "<div style=\"width: " + (20 * skillsCount) + "px; height:18px; margin: 0 0 0 " + //$NON-NLS-1$ //$NON-NLS-2$
				((100 - 20 * skillsCount) / 2) + "px;\">" + skills + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		
		return tooltip;
	}
	
	private void setHighlighted(boolean highlight) {
		IndexedAreaData area = Client.getInstance().getAreaContainer().getArea();
		if (area.getId() == fleetData.getArea().getId()) {
			FleetData fleet = area.getFleetById(fleetData.getId());
			AreaMap map = Client.getInstance(
				).getAreaContainer().getMap();
			
			UIItem item = map.getItem(fleet, FleetData.CLASS_NAME);
			
			if (item != null)
				((FleetItem) item).showFleetOutline(highlight);
			
			for (MiniMap miniMap : map.getMiniMaps()) {
				UIMiniItem miniItem = miniMap.getItem(
					fleet, FleetData.CLASS_NAME);
				
				if (miniItem != null) {
					FleetMiniItem miniFleet = (FleetMiniItem) miniItem;
					miniFleet.setBrightLineOfSight(highlight);
				}
			}
		}
	}
}
