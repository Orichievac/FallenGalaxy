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

package fr.fg.client.map.item;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import fr.fg.client.animation.LoopClassNameUpdater;
import fr.fg.client.core.Utilities;
import fr.fg.client.data.StructureData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Dimension;

public class StructureItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static String ID_HIGHLIGHT = "structureHighlight";
	
	private final static int[][] SPACESHIP_DECKS_OFFSET = {
		{0, 50}, {125, 0}, {125, 100}, {15, 0}, {15, 100}, {140, 50}
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private StructureData structureData;
	
	private ArrayList<Element> borders;
	
	private Element deactivationElement;
	
	private Element hullBarElement;
	
	private Element currentHullElement;
	
	private Element[] spaceshipDecks;
	
	private LoopClassNameUpdater structureBorderUpdater;
	
	private boolean selected;
	
	private boolean mouseOverStructure;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StructureItem(StructureData structureData, UIItemRenderingHints hints) {
		super(structureData.getX(), structureData.getY(), hints);
		
		this.structureData = structureData;
		this.borders = new ArrayList<Element>();
		
		if (structureData.getType() == StructureData.TYPE_FORCE_FIELD) {
			Element shield = DOM.createDiv();
			shield.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
			shield.setClassName("shield");
			getElement().appendChild(shield);
		}
		
		deactivationElement = DOM.createDiv();
		deactivationElement.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		deactivationElement.setClassName("structure-deactivated");
		deactivationElement.getStyle().setProperty("display", "none");
		getElement().appendChild(deactivationElement);
		
		hullBarElement = DOM.createDiv();
		hullBarElement.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		hullBarElement.setClassName("structure-hullBar");
		hullBarElement.getStyle().setProperty("display", "none");
		getElement().appendChild(hullBarElement);
		
		currentHullElement = DOM.createDiv();
		currentHullElement.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		currentHullElement.setClassName("currentHull");
		hullBarElement.appendChild(currentHullElement);
		
		if (structureData.getType() == StructureData.TYPE_SPACESHIP_YARD) {
			spaceshipDecks = new Element[6];
			for (int i = 0; i < 6; i++) {
				spaceshipDecks[i] = DOM.createDiv();
				spaceshipDecks[i].setInnerHTML("<div class=\"" +
					"spaceshipDeckClass\" unselectable=\"on\" " +
					"style=\"background-position: -" + (350 + 20 * i) + "px -470px\"></div>");
				spaceshipDecks[i].setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
				spaceshipDecks[i].setClassName("spaceshipDeck");
				spaceshipDecks[i].getStyle().setProperty("display", "none");
				getElement().appendChild(spaceshipDecks[i]);
			}
		}
		
		Element highlight = DOM.createDiv();
		highlight.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		highlight.setClassName("structure-highlight");
		highlight.getStyle().setProperty("display", "none");
		highlight.setId(ID_HIGHLIGHT + (long) structureData.getId());
		getElement().appendChild(highlight);
		
		for (int i = 0; i < getBordersCount(structureData.getType()); i ++) {
			Element border = buildBorder(structureData.getTreaty());
			borders.add(border);
			getElement().appendChild(border);
		}
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(structureData);
		updateRendering(structureData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEOVER:
			if (event.getFromElement() == null ||
					getElement().isOrHasChild(event.getFromElement()))
				return;
			
			mouseOverStructure = true;
			
			for (int i = 0; i < structureData.getIdAffectedStructuresCount(); i++) {
				Element affectedStructure = DOM.getElementById(ID_HIGHLIGHT +
					(long) structureData.getIdAffectedStructureAt(i));
				
				if (affectedStructure != null)
					affectedStructure.getStyle().setProperty("display", "");
			}
			
			if (!selected) {
				structureBorderUpdater.loopTargetClass(0, 3);
				TimerManager.register(structureBorderUpdater);
			}
			break;
		case Event.ONMOUSEOUT:
			if (event.getToElement() == null ||
					getElement().isOrHasChild(event.getToElement()))
				return;
			
			mouseOverStructure = false;
			
			for (int i = 0; i < structureData.getIdAffectedStructuresCount(); i++) {
				Element affectedStructure = DOM.getElementById(ID_HIGHLIGHT +
					(long) structureData.getIdAffectedStructureAt(i));
				
				if (affectedStructure != null)
					affectedStructure.getStyle().setProperty("display", "none");
			}
			
			if (!selected) {
				structureBorderUpdater.setTargetClass(0);
				structureBorderUpdater.setCurrentClass(0);
				TimerManager.unregister(structureBorderUpdater);
			}
			break;
		}
	}
	
	public void onUnload() {
		super.onUnload();
		
		if (structureBorderUpdater != null)
			TimerManager.unregister(structureBorderUpdater);
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if (selected) {
			structureBorderUpdater.setTargetClass(0);
			structureBorderUpdater.setCurrentClass(0);
			TimerManager.unregister(structureBorderUpdater);
		} else if (mouseOverStructure) {
			structureBorderUpdater.loopTargetClass(0, 3);
			TimerManager.register(structureBorderUpdater);
		}
		
		updateStyle();
	}
	
	@Override
	public void onDataUpdate(Object newData) {
		StructureData newStructureData = (StructureData) newData;
		
		if (structureData.getX() != newStructureData.getX() ||
			structureData.getY() != newStructureData.getY())
			setLocation(newStructureData.getX(), newStructureData.getY());
		
		boolean update = false;
		
		if (structureData.isPlayerStructure() &&
				newStructureData.isPlayerStructure()) {
			for (int i = 0; i < structureData.getResourcesCount(); i++)
				if (structureData.getResourceAt(i) != newStructureData.getResourceAt(i)) {
					update = true;
					break;
				}
		}
		
		if (update ||
			!structureData.getTreaty().equals(newStructureData.getTreaty()) ||
			!structureData.getAllyTag().equals(newStructureData.getAllyTag()) ||
			!structureData.getOwner().equals(newStructureData.getOwner()) ||
			structureData.getHull() != newStructureData.getHull() ||
			structureData.getMaxHull() != newStructureData.getMaxHull() ||
			structureData.getName() != newStructureData.getName() ||
			structureData.getLevel() != newStructureData.getLevel() ||
			structureData.isActivated() != newStructureData.isActivated() ||
			(((structureData.isPlayerStructure() || structureData.isAllyStructure()) &&
			 (newStructureData.isPlayerStructure() || newStructureData.isAllyStructure())) &&
			 (structureData.isShared() != newStructureData.isShared() ||
			  structureData.getMaxEnergy() != newStructureData.getMaxEnergy() ||
			  structureData.getUsedEnergy() != newStructureData.getUsedEnergy())))
			updateData(newStructureData);
		
		if (structureData.getSkillsCount() == newStructureData.getSkillsCount()) {
			for (int i = 0; i < structureData.getSkillsCount(); i++) {
				if ((structureData.getSkillAt(i).getReloadRemainingTime() > 0 &&
						newStructureData.getSkillAt(i).getReloadRemainingTime() == 0) ||
						(structureData.getSkillAt(i).getReloadRemainingTime() == 0 &&
						newStructureData.getSkillAt(i).getReloadRemainingTime() > 0)) {
					updateRendering(newStructureData);
					break;
				}
			}
		}
		
		structureData = newStructureData;
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		updateRendering(structureData);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		borders.clear();
		borders = null;
		deactivationElement = null;
		hullBarElement = null;
		
		structureData = null;
	}
	
	public void setHullBarVisible(boolean visible) {
		hullBarElement.getStyle().setProperty("display", visible ? "" : "none");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private int getBordersCount(int type) {
		switch (type) {
		case StructureData.TYPE_SPACESHIP_YARD:
		case StructureData.TYPE_HYPERSPACE_RELAY:
			return 4;
		case StructureData.TYPE_STOREHOUSE:
		case StructureData.TYPE_GENERATOR:
			return 3;
		case StructureData.TYPE_FORCE_FIELD:
		case StructureData.TYPE_STASIS_CHAMBER:
			return 2;
		default:
			return 0;
		}
	}
	
	private void updateRendering(StructureData structureData) {
		int offset = 0;
		if (hints.getZoom() == 1)
			offset = 8;
		else if (hints.getZoom() == .5)
			offset = 6;
		else if (hints.getZoom() == .25)
			offset = 4;
		
		for (int i = 0; i < borders.size(); i++) {
			Element border = borders.get(i);
			int x = 0, y = 0;
			
			switch (structureData.getType()) {
			case StructureData.TYPE_STOREHOUSE:
				switch (i) {
				case 0:
					x = 70;
					y = 35;
					break;
				case 1:
					x = 110;
					y = 69;
					break;
				case 2:
					x = 59;
					y = 91;
					break;
				}
				break;
			case StructureData.TYPE_SPACESHIP_YARD:
				switch (i) {
				case 0:
					x = 83;
					y = 6;
					break;
				case 1:
					x = 83;
					y = 137;
					break;
				case 2:
					x = 105;
					y = 49;
					break;
				case 3:
					x = 105;
					y = 94;
					break;
				}
				break;
			case StructureData.TYPE_FORCE_FIELD:
				switch (i) {
				case 0:
					x = 11;
					y = -3;
					break;
				case 1:
					x = 11;
					y = 26;
					break;
				}
				break;
			case StructureData.TYPE_STASIS_CHAMBER:
				switch (i) {
				case 0:
					x = 31;
					y = 4;
					break;
				case 1:
					x = 31;
					y = 58;
					break;
				}
				break;
			case StructureData.TYPE_HYPERSPACE_RELAY:
				switch (i) {
				case 0:
					x = 10;
					y = 10;
					break;
				case 1:
					x = 52;
					y = 10;
					break;
				case 2:
					x = 31;
					y = 31;
					break;
				case 3:
					x = 31;
					y = 62;
					break;
				}
				break;
			case StructureData.TYPE_GENERATOR:
				switch (i) {
				case 0:
					x = 18;
					y = 45;
					break;
				case 1:
					x = 171;
					y = 45;
					break;
				case 2:
					x = 94;
					y = 176;
					break;
				}
				break;
			}
			
			x += offset;
			y += offset;
			
			border.getStyle().setProperty("left", (int) Math.round(
				x * hints.getZoom()) + "px");
			border.getStyle().setProperty("top", (int) Math.round(
				y * hints.getZoom()) + "px");
		}
		
		Dimension size = structureData.getSize();
		
		setPixelSize(
			(int) (size.getWidth() * hints.getTileSize() * hints.getZoom()),
			(int) (size.getHeight() * hints.getTileSize() * hints.getZoom())
		);
		
		int margin = (int) ((size.getWidth() / 2) * hints.getTileSize() * hints.getZoom());
		getElement().getStyle().setProperty("margin", "-" + margin + "px 0 0 -" + margin + "px");
		
		double coef = 0;
		if (hints.getZoom() == .5)
			coef = 1;
		else if (hints.getZoom() == .25)
			coef = 1.5;
		else if (hints.getZoom() == .125)
			coef = 1.75;
		
		if (structureData.getType() == StructureData.TYPE_STASIS_CHAMBER &&
				structureData.getSkillAt(0).getReloadRemainingTime() > 0) {
			// Affichage en rouge de la structure pour signaler que sa
			// compétence est en cours de rechargement
			coef += 1.75;
		} else if (structureData.getType() == StructureData.TYPE_GENERATOR) {
			coef += 1.75 * ((structureData.getX() + structureData.getY()) % 3);
		}
		
		getElement().getStyle().setProperty("backgroundPosition", "-" +
			(int) (size.getWidth() * hints.getTileSize() * coef) + "px -" +
			(250 + StructureData.getBackgroundOffset(structureData.getType()) * hints.getTileSize()) + "px");
		
		switch (structureData.getType()) {
		case StructureData.TYPE_FORCE_FIELD:
			getElement().getFirstChildElement().getStyle().setProperty("margin",
				"-" + ((4 - structureData.getSize().getWidth() / 2) *
					hints.getTileSize() * hints.getZoom()) + "px 0 0 " +
				"-" + ((4 - structureData.getSize().getHeight() / 2) *
					hints.getTileSize() * hints.getZoom()) + "px");
			break;
		case StructureData.TYPE_SPACESHIP_YARD:
			offset = 350;
			if (hints.getZoom() == .5)
				offset = 470;
			else if (hints.getZoom() == .25)
				offset = 530;
			
			for (int i = 0; i < spaceshipDecks.length; i++) {
				spaceshipDecks[i].getStyle().setProperty("left",
					(int) Math.floor(SPACESHIP_DECKS_OFFSET[i][0] * hints.getZoom()) + "px");
				spaceshipDecks[i].getStyle().setProperty("top",
					(int) Math.floor(SPACESHIP_DECKS_OFFSET[i][1] * hints.getZoom()) + "px");
				spaceshipDecks[i].getFirstChildElement().getStyle().setProperty("backgroundPosition",
					"-" + (int) (offset + 20 * hints.getZoom() * i) + "px -470px");
			}
			break;
		}
	}
	
	private void updateData(StructureData structureData) {
		updateStyle();
		
		if (structureData.isPlayerStructure()) {
			if (structureBorderUpdater == null) {
				structureBorderUpdater = new LoopClassNameUpdater(this, "border-over", 0, 10);
				sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
			}
		} else {
			if (structureBorderUpdater != null) {
				TimerManager.unregister(structureBorderUpdater);
				structureBorderUpdater.destroy();
				structureBorderUpdater = null;
				unsinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
			}
		}
		
		deactivationElement.getStyle().setProperty(
			"display", structureData.isActivated() ? "none" : "");
		
		switch (structureData.getType()) {
		case StructureData.TYPE_FORCE_FIELD:
			getElement().getFirstChildElement().getStyle().setProperty(
				"display", structureData.isActivated() ? "" : "none");
			break;
		case StructureData.TYPE_SPACESHIP_YARD:
			for (int i = 0; i < 6; i++)
				spaceshipDecks[i].getStyle().setProperty("display",
					((structureData.getSpaceshipYardDecks() & (1 << i)) != 0) ? "" : "none");
			break;
		}
		
		String[] colors = {"#ff0000", "#ff7201", "#ffd800", "#ceff01", "#00ff00"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		double[] thresholds = {.2, .4, .6, .8, 1};
		
		double coef = structureData.getHull() / (double) structureData.getMaxHull();
		String color = "";
		for (int i = 0; i < thresholds.length; i++)
			if (coef <= thresholds[i]) {
				color = colors[i];
				break;
			}
		
		currentHullElement.getStyle().setProperty("width",
			40 * structureData.getHull() / structureData.getMaxHull() + "px");
		
		String hullValue = "<div>Points de structure : <b style=\"color: " + color + ";\">" +
			Formatter.formatNumber(Math.round(structureData.getHull())) + "</b> / " +
			"<b>" + Formatter.formatNumber(Math.round(structureData.getMaxHull())) + "</b></div>";
		
		String forceField = structureData.isWithinForceFieldRange() ?
			"<div class=\"emphasize\">Protégé par un champ de force</div>" : "";
		
		String owner = "<div class=\"owner-" + ((structureData.isAlliedStructure() && !structureData.isAllyStructure())?
				"allied" : structureData.getTreaty()) + "\"><b>" + //$NON-NLS-1$ //$NON-NLS-2$
			(structureData.hasAllyTag() ? "[" + structureData.getAllyTag() + "] " : "") +
			structureData.getOwner() + "</b>" + (structureData.isConnected() ?
					(structureData.isAway() ? Utilities.getAwayImage() : Utilities.getOnlineImage()) :
					Utilities.getOfflineImage()) + //$NON-NLS-1$
			(structureData.isAi() ? " <img src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
				"images/misc/blank.gif\" class=\"ai\"/>" : "") + //$NON-NLS-1$ //$NON-NLS-2$
			"</div>"; //$NON-NLS-1$
		
		String maxEnergy = "";
		if (structureData.getType() == StructureData.TYPE_GENERATOR && (
				structureData.isPlayerStructure() ||
				structureData.isAllyStructure()))
			maxEnergy = "<div>Energie : <b>" +
				structureData.getUsedEnergy() + " / " +
				structureData.getMaxEnergy() + "</b> " +
				Utilities.getEnergyImage() + "</div>";
		
		setToolTipText(owner +
			"<div style=\"font-weight: bold;\">" + structureData.getName() + "</div>" +
			"<div>Niveau : <b>" + structureData.getLevel() + "</b></div>" +
			hullValue + forceField + maxEnergy, 200);
	}
	
	private void updateStyle() {
		getElement().setClassName("structure " + (selected ?
			"structure-selected" : "structure-" + ((structureData.isAlliedStructure() && !structureData.isAllyStructure())?
					"allied" : structureData.getTreaty())) +
			" structure" + structureData.getType()); //$NON-NLS-1$
		
		for (int i = 0; i < borders.size(); i++) {
			if (selected && structureData.isPlayerStructure())
				borders.get(i).setClassName("border border-selected"); //$NON-NLS-1$
			else
				borders.get(i).setClassName("border border-" + ((structureData.isAlliedStructure() && !structureData.isAllyStructure())?
						"allied" : structureData.getTreaty())); //$NON-NLS-1$
		}
	}
	
	private Element buildBorder(String treaty) {
		Element border = DOM.createDiv();
		border.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return border;
	}
}
