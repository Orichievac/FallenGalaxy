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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import fr.fg.client.animation.LoopClassNameUpdater;
import fr.fg.client.animation.RotationUpdater;
import fr.fg.client.core.Client;
import fr.fg.client.data.PlanetData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.FastMath;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;


public class StarSystemItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int SYSTEM_RADIUS = 5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private StarSystemData systemData;
	
		
	private ArrayList<RotationUpdater> rotationHandlers;
	
	private ArrayList<Element> planets;
	
	private ArrayList<Element> borders;
	
	private Element star, asteroidBelt;
	
	private String systemInfo;
	
	private OutlineText systemInfoText;
	
	private LoopClassNameUpdater systemBorderUpdater;
	
	private boolean selected;
	
	private boolean mouseOverSystem;
		
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
		public StarSystemItem(StarSystemData systemData, UIItemRenderingHints hints) {
		super(systemData.getX(), systemData.getY(), hints);
		
	
			
		

		setStylePrimaryName("starSystem"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		Element system1 = DOM.createDiv();
		system1.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		system1.setClassName("system"); //$NON-NLS-1$
		getElement().appendChild(system1);
		
		if (!systemData.getTreaty().equals("unknown")) { //$NON-NLS-1$
			String owner;
			if (systemData.hasOwner()) {
				owner = systemData.getOwner();
				if (systemData.isAi())
					owner += "<img src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
						"images/misc/blank.gif\" class=\"ai\"/>"; //$NON-NLS-1$
			} else {
				StaticMessages messages =
					(StaticMessages) GWT.create(StaticMessages.class);
				
				if (systemData.isColonizable())
					owner = messages.vacantSystem();
				else
					owner = messages.uncolonizableSystem();
			};
			
			
			if (systemData.hasOwner()){
		
				setToolTipText((systemData.hasAlly() ?"<div class=\"justify\"><b><font color='#33CC00'>[" + systemData.getAlly() + "] </font></b>" :"") + owner + "</br><b><font color='red'>" + systemData.getName() + "</font></b></div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
			
		}
			else
			{
				setToolTipText("<div class=\"justify\">" + owner + "</br><b><font color='red'>" + systemData.getName() + "</font></b></div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		this.systemData = systemData;
		this.planets = new ArrayList<Element>();
		this.borders = new ArrayList<Element>();
		this.rotationHandlers = new ArrayList<RotationUpdater>();
		
		// Crée le système
		Element system = getElement();
		system.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Ceinture d'astéroides
		asteroidBelt = buildAsteroidBelt(systemData.getAsteroidBelt());
		DOM.appendChild(system, asteroidBelt);
		
		// Construit les planètes du système
		for (int i = 0; i < systemData.getPlanetsCount(); i++) {
			Element planet = buildPlanet(systemData.getPlanetAt(i));
			planets.add(planet);
			system.appendChild(planet);
		}
		
		// Construit l'astre du système
		star = buildStar();
		DOM.appendChild(system, star);
		
		// Frontières du système
		for (double i = 0; i < 2 * Math.PI; i += Math.PI / 6) {
			Element border = buildBorder(systemData.getTreaty());
			borders.add(border);
			DOM.appendChild(system, border);
		}
		
		updateData(systemData);
		updateRendering();
	}
	
	// --------------------------------------------------------- METHODES -- //


	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if (selected) {
			systemBorderUpdater.setTargetClass(0);
			systemBorderUpdater.setCurrentClass(0);
			TimerManager.unregister(systemBorderUpdater);
		} else if (mouseOverSystem) {
			systemBorderUpdater.loopTargetClass(0, 3);
			TimerManager.register(systemBorderUpdater);
		}
		
		setStyleName("system " + (selected ?
				"system-selected" : "system-" + systemData.getPact()));
		
		// Frontières du système
		for (int i = 0; i < borders.size(); i++) {
			if (selected && systemData.isPlayerStarSystem())
				borders.get(i).setClassName("border border-selected"); //$NON-NLS-1$
			else
				borders.get(i).setClassName("border border-" + systemData.getPact()); //$NON-NLS-1$
		}
	}
	
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		updateRendering();
	}
	
	@Override
	public void onDataUpdate(Object newData) {
		StarSystemData newSystemData = (StarSystemData) newData;
		
		if ((systemData.hasAlly() && !systemData.getAlly().equals(newSystemData.getAlly())) ||
			(systemData.hasOwner() && !systemData.getOwner().equals(newSystemData.getOwner())) ||
			!systemData.getName().equals(newSystemData.getName()) ||
			!systemData.getTreaty().equals(newSystemData.getTreaty())) {
			updateData(newSystemData);
		}
		
		systemData = newSystemData;
	}
	
	public void onLoad() {
		super.onLoad();
		
		for (TimerHandler handler : rotationHandlers)
			TimerManager.register(handler);
	}
	
	public void onUnload() {
		super.onUnload();
		
		for (TimerHandler handler : rotationHandlers)
			TimerManager.unregister(handler);
		
		if (systemBorderUpdater != null)
			TimerManager.unregister(systemBorderUpdater);
	}
	
	@Override
	public void onCullStateUpdate(boolean culled) {
		super.onCullStateUpdate(culled);
		
		if (culled) {
			for (TimerHandler handler : rotationHandlers)
				TimerManager.unregister(handler);
		} else {
			for (TimerHandler handler : rotationHandlers)
				TimerManager.register(handler);
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEOVER:
			if (event.getFromElement() == null ||
					getElement().isOrHasChild(event.getFromElement()))
				return;
			
			sinkEvents(Event.ONMOUSEMOVE);
			break;
		case Event.ONMOUSEOUT:
			if (event.getToElement() == null ||
					getElement().isOrHasChild(event.getToElement()))
				return;
			
			mouseOverSystem = false;
			
			unsinkEvents(Event.ONMOUSEMOVE);
			
			if (!selected) {
				systemBorderUpdater.setTargetClass(0);
				systemBorderUpdater.setCurrentClass(0);
				
				TimerManager.unregister(systemBorderUpdater);
			}
			break;
		case Event.ONMOUSEMOVE:
			int radius = getSystemRadius();
			int dx = getElement().getAbsoluteLeft() + radius - event.getClientX();
			int dy = getElement().getAbsoluteTop()  + radius - event.getClientY();
			
			if (dx * dx + dy * dy < radius * radius) {
				mouseOverSystem = true;
				
				if (!selected) {
					systemBorderUpdater.loopTargetClass(0, 3);
					TimerManager.register(systemBorderUpdater);
				}
			} else {
				mouseOverSystem = false;
				
				if (!selected) {
					systemBorderUpdater.setTargetClass(0);
					systemBorderUpdater.setCurrentClass(0);
					TimerManager.unregister(systemBorderUpdater);
				}
			}
			break;
		}
	}
	
	public void showSystemOutline(boolean show) {
		if (systemData.getTreaty().equals("player") &&
				systemBorderUpdater != null) {
			if (show) {
				systemBorderUpdater.loopTargetClass(0, 3);
				TimerManager.register(systemBorderUpdater);
			} else {
				systemBorderUpdater.setTargetClass(0);
				systemBorderUpdater.setCurrentClass(0);
				TimerManager.unregister(systemBorderUpdater);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (systemBorderUpdater != null) {
			TimerManager.unregister(systemBorderUpdater);
			systemBorderUpdater.destroy();
			systemBorderUpdater = null;
		}
		
		for (TimerHandler handler : rotationHandlers) {
			TimerManager.unregister(handler);
			handler.destroy();
		}
		rotationHandlers.clear();
		rotationHandlers = null;
		
		planets.clear();
		planets = null;
		
		borders.clear();
		borders = null;
		
		star = null;
		asteroidBelt = null;
		systemInfo = null;
		systemInfoText = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private int getSystemRadius() {
		return (int) Math.floor(
				SYSTEM_RADIUS * hints.getTileSize() * hints.getZoom() + 50);
	}
	
	// Construit l'astre du système
	private Element buildStar() {
		Element star = DOM.createDiv();
		DOM.setElementAttribute(star, "unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setElementProperty(star, "className", "star"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return star;
	}
	
	// Construit la ceinture d'astéroides
	private Element buildAsteroidBelt(int type) {
		Element asteroidBelt = DOM.createDiv();
		DOM.setElementAttribute(asteroidBelt, "unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setElementProperty(asteroidBelt, "className", "asteroidBelt asteroidBelt" + type + " env_" + Client.getInstance().getAreaContainer().getArea().getEnvironment() ); //$NON-NLS-1$ //$NON-NLS-2$
		
		return asteroidBelt;
	}
	
	// Construit une planète du système
	private Element buildPlanet(PlanetData planetData) {
		Element planet = DOM.createDiv();
		DOM.setElementAttribute(planet, "unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setElementProperty(planet, "className", "planet"); //$NON-NLS-1$ //$NON-NLS-2$
		
		int systemRadius = getSystemRadius();
		
		if (Config.getGraphicsQuality() >=
				Config.VALUE_QUALITY_HIGH) {
			// Affecte un handler pour gérer le mouvement de rotation des planètes
			rotationHandlers.add(new RotationUpdater(planet,
					planetData.getAngle(), planetData.getDistance() * hints.getZoom(),
					planetData.getRotationSpeed() * 10, systemRadius, systemRadius));
		} else {
			planet.getStyle().setProperty("left", (int) Math.floor(systemRadius + //$NON-NLS-1$
				FastMath.cos(planetData.getAngle()) * planetData.getDistance() *
					hints.getZoom()) + "px"); //$NON-NLS-1$
			planet.getStyle().setProperty("top",  (int) Math.floor(systemRadius + //$NON-NLS-1$
				FastMath.sin(planetData.getAngle()) * planetData.getDistance() *
					hints.getZoom()) + "px"); //$NON-NLS-1$
		}
		
		return planet;
	}
	
	private Element buildBorder(String treaty) {
		Element border = DOM.createDiv();
		DOM.setElementAttribute(border, "unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return border;
	}
	
	private void updateData(StarSystemData systemData) {
		// Timer pour éclairer les frontières du système au survol de la souris
		setStyleName("system " + (selected ?
				"system-selected" : "system-" + systemData.getPact()));
		
		if (systemData.isPlayerStarSystem()) {
			if (systemBorderUpdater == null) {
				systemBorderUpdater = new LoopClassNameUpdater(this, "border-over", 0, 10);
				sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
			}
		} else {
			if (systemBorderUpdater != null) {
				TimerManager.unregister(systemBorderUpdater);
				systemBorderUpdater.destroy();
				systemBorderUpdater = null;
				unsinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
			}
		}
		
		// Frontières du système
		for (int i = 0; i < borders.size(); i++) {
			if (selected && systemData.isPlayerStarSystem())
				borders.get(i).setClassName("border border-selected"); //$NON-NLS-1$
			else
				borders.get(i).setClassName("border border-" + systemData.getPact());
			
			if (systemData.getTreaty().equals("uninhabited") || //$NON-NLS-1$
				systemData.getTreaty().equals("unknown")) { //$NON-NLS-1$
				borders.get(i).getStyle().setProperty("display", "none");
			} else {
				borders.get(i).getStyle().setProperty("display", "");
			}
		}
		
		// Noms du système, du propriétaire et de l'alliance du propriétaire
		if (!systemData.getTreaty().equals("unknown")) { //$NON-NLS-1$
			String owner;
			if (systemData.hasOwner()) {
				owner = systemData.getOwner();
				if (systemData.isAi())
					owner += "<img src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
						"images/misc/blank.gif\" class=\"ai\"/>"; //$NON-NLS-1$
			} else {
				StaticMessages messages =
					(StaticMessages) GWT.create(StaticMessages.class);
				
				if (systemData.isColonizable())
					owner = messages.vacantSystem();
				else
					owner = messages.uncolonizableSystem();
			}
			
			systemInfo =
				"<div class=\"name systemName\" unselectable=\"on\">" + systemData.getName() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"name ownerName owner-" + systemData.getPact() + "\" unselectable=\"on\">" + owner + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				(systemData.hasAlly() ? "<div class=\"name allyName owner-" + systemData.getPact() + "\" unselectable=\"on\">" + //$NON-NLS-1$ //$NON-NLS-2$
						systemData.getAlly() + "</div>" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			systemInfo = "<div class=\"name systemName\" unselectable=\"on\">" + systemData.getName() + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (systemInfoText != null) {
			systemInfoText.setText(systemInfo);
		} else {
			systemInfoText = TextManager.getText(systemInfo);
			getElement().appendChild(systemInfoText.getElement());
		}
	}
	
	private void updateRendering() {
		int systemRadius = getSystemRadius();
		
		// Met à jour les dimensions du secteur
		Element system = getElement();
		DOM.setStyleAttribute(system, "marginLeft", Math.floor( //$NON-NLS-1$
				hints.getTileSize() * hints.getZoom() / 2 - systemRadius) + "px"); //$NON-NLS-1$
		DOM.setStyleAttribute(system, "marginTop", Math.floor( //$NON-NLS-1$
				hints.getTileSize() * hints.getZoom() / 2 - systemRadius) + "px"); //$NON-NLS-1$
		setWidth(2 * systemRadius + "px"); //$NON-NLS-1$
		setHeight(2 * systemRadius + "px"); //$NON-NLS-1$
		
		// Met à jour la position et les dimensions de l'astre
		DOM.setStyleAttribute(star, "left", systemRadius + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setStyleAttribute(star, "top",  systemRadius + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setStyleAttribute(star, "backgroundPosition", (int) Math.floor( //$NON-NLS-1$
				-120 * (systemData.getStarImage() - 1) * hints.getZoom()) + "px " + //$NON-NLS-1$
				(hints.getZoom() == 1 ? "0" : hints.getZoom() == .5 ? "-120px" : "-225px")); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Met à jour la position de la ceinture d'asteroides
		DOM.setStyleAttribute(asteroidBelt, "left", systemRadius + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setStyleAttribute(asteroidBelt, "top",  systemRadius + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Met à jour les dimensions des planètes et les handlers pour gérer
		// la rotation des planètes
		for (int i = 0; i < systemData.getPlanetsCount(); i++) {
			PlanetData planetData = systemData.getPlanetAt(i);
			Element planet = planets.get(i);
			DOM.setStyleAttribute(planet, "backgroundPosition", //$NON-NLS-1$
					(int) Math.floor((planetData.getImage() - 1) *
						(hints.getZoom() == .25 ? -8 : -30 * hints.getZoom())) + "px " + //$NON-NLS-1$
					(hints.getZoom() == 1 ? "-180px" : (hints.getZoom() == .5 ? "-210px" : "-255px"))); //$NON-NLS-1$ $NON-NLS-2$
			
			if (rotationHandlers.size() > 0) {
				RotationUpdater handler = rotationHandlers.get(i);
				handler.setRadius(planetData.getDistance() * hints.getZoom());
				handler.setOffset(systemRadius, systemRadius);
			} else {
				planet.getStyle().setProperty("left", (int) Math.floor(systemRadius + //$NON-NLS-1$
						FastMath.cos(planetData.getAngle()) * planetData.getDistance() *
							hints.getZoom()) + "px"); //$NON-NLS-1$
				planet.getStyle().setProperty("top",  (int) Math.floor(systemRadius + //$NON-NLS-1$
					FastMath.sin(planetData.getAngle()) * planetData.getDistance() *
						hints.getZoom()) + "px"); //$NON-NLS-1$
			}
		}
		
		// Met à jour la position des frontières
		double angle = 0;
		for (int i = 0; i < borders.size(); i++) {
			Element border = borders.get(i);
			
			DOM.setStyleAttribute(border, "left", (int) Math.floor(systemRadius + FastMath.cos(angle) * //$NON-NLS-1$
					SYSTEM_RADIUS * hints.getTileSize() * hints.getZoom()) + "px"); //$NON-NLS-1$
			DOM.setStyleAttribute(border, "top", (int) Math.floor(systemRadius + FastMath.sin(angle) * //$NON-NLS-1$
					SYSTEM_RADIUS * hints.getTileSize() * hints.getZoom()) + "px"); //$NON-NLS-1$
			angle += Math.PI / 6;
		}
	}
	

	
}
