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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import fr.fg.client.animation.BlinkUpdater;
import fr.fg.client.data.WardData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;

public class WardItem extends DrawableItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private WardData wardData;
	
	private boolean detectionRadiusVisible;
	
	private BlinkUpdater blinkUpdater;
	
	private Element border;
	
	private Element triggerRadius;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public WardItem(WardData wardData, UIItemRenderingHints hints) {
		super(wardData.getX(), wardData.getY(), hints);
		
		this.wardData = wardData;
		this.detectionRadiusVisible = false;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		border = buildBorder();
		getElement().appendChild(border);
		
		updateData(wardData);

		if (Config.getGraphicsQuality() >= Config.VALUE_QUALITY_HIGH) {
			blinkUpdater = new BlinkUpdater(border, 850, 950);
			blinkUpdater.setValue(
				((wardData.getX() - wardData.getY() + 18) % 18) * 100 +
				(int) (new Date().getTime() % 1800));
			
			if (!isCulled())
				TimerManager.register(blinkUpdater);
		}
		
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// --------------------------------------------------------- METHODES -- //

	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		if (triggerRadius != null) {
			triggerRadius.getStyle().setProperty("left", (int) Math.floor(
				wardData.getX() * hints.getZoom() * hints.getTileSize()) + "px");
			triggerRadius.getStyle().setProperty("top", (int) Math.floor(
				wardData.getX() * hints.getZoom() * hints.getTileSize()) + "px");
		}
		
		if (detectionRadiusVisible)
			updateCanvas();
	}
	
	@Override
	public void onDataUpdate(Object newData) {
		WardData newWardData = (WardData) newData;
		
		if (wardData.getX() != newWardData.getX() ||
			wardData.getY() != newWardData.getY())
			setLocation(newWardData.getX(), newWardData.getY());
		
		if (!wardData.getTreaty().equals(newWardData.getTreaty()) ||
			!wardData.getAllyTag().equals(newWardData.getAllyTag()) ||
			!wardData.getOwner().equals(newWardData.getOwner()))
			updateData(newWardData);
		
		wardData = newWardData;
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEOVER:
			setDetectionRadiusVisible(true);
			break;
		case Event.ONMOUSEOUT:
			setDetectionRadiusVisible(false);
			break;
		}
	}
	
	public void setTriggerRadiusVisible(boolean visible) {
		if (wardData.getType().startsWith(WardData.TYPE_MINE) ||
				wardData.getType().startsWith(WardData.TYPE_STUN)) {
			if (visible) {
				if (triggerRadius == null && isAttached()) {
					triggerRadius = DOM.createDiv();
					triggerRadius.setClassName("triggerRadius");
					triggerRadius.setAttribute("unselectable", "on");
					triggerRadius.getStyle().setProperty("left", (int) Math.floor(
						wardData.getX() * hints.getZoom() * hints.getTileSize()) + "px");
					triggerRadius.getStyle().setProperty("top", (int) Math.floor(
						wardData.getY() * hints.getZoom() * hints.getTileSize()) + "px");
					getElement().getParentElement().appendChild(triggerRadius);
				}
			} else {
				if (triggerRadius != null) {
					triggerRadius.getParentElement().removeChild(triggerRadius);
					triggerRadius = null;
				}
			}
		}
	}
	
	public void setDetectionRadiusVisible(boolean visible) {
		if (visible) {
			detectionRadiusVisible = true;
			updateCanvas();
		} else {
			detectionRadiusVisible = false;
			hideCanvas();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (blinkUpdater != null) {
			TimerManager.unregister(blinkUpdater);
			blinkUpdater = null;
		}
		
		setTriggerRadiusVisible(false);
		
		wardData = null;
		border = null;
	}
	
	@Override
	public void onCullStateUpdate(boolean culled) {
		super.onCullStateUpdate(culled);

		if (blinkUpdater != null) {
			if (culled) {
				TimerManager.unregister(blinkUpdater);
			} else {
				TimerManager.register(blinkUpdater);
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(WardData wardData) {
		setStylePrimaryName("ward"); //$NON-NLS-1$
		addStyleDependentName(wardData.getType().replace("_", "-"));
		addStyleDependentName(wardData.getTreaty()); 
		
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		border.setClassName("border border-" + ((wardData.isAlliedWard() && !wardData.isAllyWard())?
				"allied" : wardData.getTreaty()));
		
		String type = wardData.getType();
		while (type.contains("_")) {
			int index = type.indexOf("_");
			
			type = type.substring(0, index) +
				type.substring(index + 1, index + 2).toUpperCase() +
				type.substring(index + 2);
		}
		type = type.substring(0, 1).toUpperCase() + type.substring(1);
		
		String remainingTime = "";
		String power = "";
		if (wardData.isPlayerWard() ||
				wardData.isAllyWard() ||
				wardData.isAlliedWard()) {
			int days = Math.max(1, wardData.getLifespan() / (3600 * 24));
			
			remainingTime = "<div class=\"emphasize\"><div style=\"float: right;\">" +
				days + " jour" + (days > 1 ? "s" : "") + "</div>Durée de vie</div>";
			
			if (wardData.getType().startsWith(WardData.TYPE_MINE) ||
					wardData.getType().startsWith(WardData.TYPE_STUN)) {
				power = "<div class=\"emphasize\"><div style=\"float: right;\">" +
					wardData.getPower() + "&nbsp;<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"stat s-power\"/></div>Puissance</div>";
			}
		}
		
		setToolTipText(
			"<div class=\"owner-" + ((wardData.isAlliedWard() && !wardData.isAllyWard())?
					"allied" : wardData.getTreaty()) + "\"><b>" + //$NON-NLS-1$ //$NON-NLS-2$
			(wardData.hasAllyTag() ? "[" + wardData.getAllyTag() + "] " : "") +
			wardData.getOwner() + "</b></div>" +
			"<div style=\"font-weight: bold;\">" +
			dynamicMessages.getString("ward" + type) + "</div>" +
			"<div class=\"justify\">" +
			dynamicMessages.getString("ward" + type + "Desc") + "</div>" +
			remainingTime + power,
			200);
	}
	
	private void updateCanvas() {
		if (wardData.getType().startsWith(WardData.TYPE_MINE) ||
				wardData.getType().startsWith(WardData.TYPE_STUN)) {
			drawCircles(
				wardData.getX(), wardData.getY(),
				new int[]{wardData.getType().contains(WardData.TYPE_MINE) ?
				WardData.MINE_TRIGGER_RADIUS :
				WardData.STUN_TRIGGER_RADIUS,
				WardData.CHARGE_DEFUSE_RADIUS},
				new String[]{"red", "#00a9d6"});
		} else {
			drawCircle(
				wardData.getX(), wardData.getY(),
				wardData.getType().startsWith(WardData.TYPE_OBSERVER) ?
				WardData.OBSERVER_DETECTION_RADIUS :
				WardData.SENTRY_DETECTION_RADIUS, "#00a9d6");
		}
	}
	
	private Element buildBorder() {
		Element border = DOM.createDiv();
		border.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return border;
	}
}
