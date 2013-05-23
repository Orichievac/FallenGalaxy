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

import fr.fg.client.animation.BlinkUpdater;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;

public class SpaceStationItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private SpaceStationData spaceStationData;
	
	private ArrayList<Element> borders;
	
	private ArrayList<BlinkUpdater> borderUpdaters;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SpaceStationItem(SpaceStationData spaceStationData,
			UIItemRenderingHints hints) {
		super(spaceStationData.getX(), spaceStationData.getY(), hints);
		
		this.spaceStationData  = spaceStationData;
		this.borders = new ArrayList<Element>();
		
		if (Config.getGraphicsQuality() >= Config.VALUE_QUALITY_HIGH)
			this.borderUpdaters = new ArrayList<BlinkUpdater>();
		
		setStyleName("spaceStation"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		Element shield = DOM.createDiv();
		shield.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		shield.setClassName("shield"); //$NON-NLS-1$
		getElement().appendChild(shield);
		
		for (int i = 0; i < 4; i ++) {
			Element border = buildBorder(spaceStationData.getTreaty());
			borders.add(border);
			getElement().appendChild(border);
			
			if (Config.getGraphicsQuality() >= Config.VALUE_QUALITY_HIGH) {
				BlinkUpdater updater = new BlinkUpdater(border, 150, 2500);
				borderUpdaters.add(updater);
				
				if (!isCulled())
					TimerManager.register(updater);
			}
		}
		
		updateData(spaceStationData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setSelected(boolean selected) {
		// Non utilisé
	}
	
	@Override
	public void onDataUpdate(Object newData) {
		SpaceStationData newSpaceStationData = (SpaceStationData) newData;
		
		if (spaceStationData.getX() != newSpaceStationData.getX() ||
			spaceStationData.getY() != newSpaceStationData.getY())
			setLocation(newSpaceStationData.getX(), newSpaceStationData.getY());
		
		if (!spaceStationData.getTreaty().equals(newSpaceStationData.getTreaty()) ||
			!spaceStationData.getAllyTag().equals(newSpaceStationData.getAllyTag()) ||
			!spaceStationData.getName().equals(newSpaceStationData.getName()) ||
			spaceStationData.getLevel() != newSpaceStationData.getLevel() ||
			spaceStationData.getHull() != newSpaceStationData.getHull() ||
			(spaceStationData.isAllySpaceStation() &&
			spaceStationData.getProductionModifier() != newSpaceStationData.getProductionModifier()))
			updateData(newSpaceStationData);
		
		spaceStationData = newSpaceStationData;
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		updateRendering();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		borders.clear();
		borders = null;
		
		if (borderUpdaters != null) {
			for (BlinkUpdater updater : borderUpdaters)
				TimerManager.unregister(updater);
			borderUpdaters.clear();
			borderUpdaters = null;
		}
		
		spaceStationData = null;
	}
	
	@Override
	public void onCullStateUpdate(boolean culled) {
		super.onCullStateUpdate(culled);
		
		if (borderUpdaters != null) {
			if (culled) {
				for (int i = 0; i < borderUpdaters.size(); i++)
					TimerManager.unregister(borderUpdaters.get(i));
			} else {
				for (int i = 0; i < borderUpdaters.size(); i++)
					TimerManager.register(borderUpdaters.get(i));
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateRendering() {
		int center = 180;
		int[] x = { -12, 120,  12, -120};
		int[] y = {-120, -12, 120,   12};
		
		for (int i = 0; i < borders.size(); i++) {
			Element border = borders.get(i);
			
			border.getStyle().setProperty("left", (int) Math.round(
				(center + x[i]) * hints.getZoom()) + "px");
			border.getStyle().setProperty("top", (int) Math.round(
				(center + y[i]) * hints.getZoom()) + "px");
		}
	}
	
	private void updateData(SpaceStationData spaceStationData) {
		// Propriétaire de la station
		for (int i = 0; i < borders.size(); i++) {
			Element border = borders.get(i);
			
			border.setClassName("border border-" +
				spaceStationData.getTreaty()); //$NON-NLS-1$
		}
		
		updateRendering();
		
		String[] colors = {"#ff0000", "#ff7201", "#ffd800", "#ceff01", "#00ff00"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		double[] thresholds = {.2, .4, .6, .8, 1};
		int maxHull = SpaceStationData.HULL_LEVELS[spaceStationData.getLevel()];
		
		double coef = spaceStationData.getHull() / (double) maxHull;
		String color = "";
		for (int i = 0; i < thresholds.length; i++)
			if (coef <= thresholds[i]) {
				color = colors[i];
				break;
			}
		
		String hullValue = "<div>Points de structure : <b style=\"color: " + color + ";\">" +
			Formatter.formatNumber(Math.round(spaceStationData.getHull())) + "</b> / " +
			"<b>" + Formatter.formatNumber(Math.round(maxHull)) + "</b></div>";
		
		String production = "";
		if (spaceStationData.getTreaty().equals("ally"))
			production = "<div>Bonus de production : <b>+" + (int) Math.floor(
				(spaceStationData.getProductionModifier() - 1) * 1000) / 10. +
				"%</b></div>";
		
		setStyleName("spaceStation spaceStation-" + spaceStationData.getTreaty());
		
		setToolTipText(
			"<div class=\"owner-" + spaceStationData.getTreaty() + "\"><b>" +
			spaceStationData.getAllyName() + "</b></div>" +
			"<div style=\"font-weight: bold;\">" + spaceStationData.getName() + "</div>" +
			"<div>Niveau : <b>" + spaceStationData.getLevel() + "</b></div>" +
			"<div>Influence : <b>+" +
			SpaceStationData.INFLUENCE_LEVELS[spaceStationData.getLevel()] +
			"</b></div>" + hullValue + production, 200);
	}

	private Element buildBorder(String treaty) {
		Element border = DOM.createDiv();
		border.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return border;
	}
}
