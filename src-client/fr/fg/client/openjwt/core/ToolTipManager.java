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

package fr.fg.client.openjwt.core;

import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.RootPanel;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.ui.JSToolTip;

public class ToolTipManager implements EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int INITIAL_DELAY = 50;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static ToolTipManager instance = new ToolTipManager();
	
	private Element currentToolTipElement;
	
	private JSToolTip toolTipWidget;
	
	private int mouseX, mouseY;
	
	private ToolTipListenerCollection listeners;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private ToolTipManager() {
		this.currentToolTipElement = null;
		
		EventManager.addEventHook(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addToolTipListener(ToolTipListener listener) {
		if (listeners == null)
			listeners = new ToolTipListenerCollection();
		listeners.add(listener);
	}
	
	public void removeToolTipListener(ToolTipListener listener) {
		if (listeners != null)
			listeners.remove(listener);
	}
	
	public void register(Element element, String toolTipText) {
		register(element, toolTipText, -1);
	}
	
	public void register(Element element, String toolTipText, int width) {
		setToolTip(element, toolTipText, width);
		
		if (isToolTipVisible() && element == currentToolTipElement) {
			toolTipWidget.setText(toolTipText);
			toolTipWidget.setPixelWidth(width);
		}
	}
	
	public void unregister(Element element) {
		removeToolTip(element);
		
		if (isToolTipVisible() && element == currentToolTipElement)
			hideToolTip();
	}
	
	public boolean isToolTipVisible() {
		return toolTipWidget != null;
	}
	
	public boolean onEventPreview(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEMOVE:
			// Enregistre les coordonnées de la souris
			int mouseX = OpenJWT.eventGetPointerX(event);
			int mouseY = OpenJWT.eventGetPointerY(event);
			
			if (this.mouseX == mouseX && this.mouseY == mouseY)
				return true;
			
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			
			Element target = DOM.eventGetTarget(event);
			Element toolTipElement = getToolTipElement(target);
			
			if (isToolTipVisible()) {
				if (toolTipElement != currentToolTipElement) {
					hideToolTip();
					currentToolTipElement = toolTipElement;
					displayToolTip();
				}
			} else {
				currentToolTipElement = toolTipElement;
				displayToolTip();
			}
			break;
		}
		return true;
	}
	
	public static ToolTipManager getInstance() {
		return instance;
	}

	public static native String getToolTipText(Element element) /*-{
		return element.__tooltipText || null;
	}-*/;
	
	public static native void setToolTipText(Element element, String toolTipText) /*-{
		element.__tooltipText = toolTipText;
	}-*/;
	
	public static native int getToolTipWidth(Element element) /*-{
		return element.__tooltipWidth || -1;
	}-*/;

	public static native void setToolTipWidth(Element element, int toolTipWidth) /*-{
		element.__tooltipWidth = toolTipWidth;
	}-*/;

	public static native void setToolTip(Element element, String toolTipText, int toolTipWidth) /*-{
		element.__tooltipText = toolTipText;
		element.__tooltipWidth = toolTipWidth;
	}-*/;

	public static native void removeToolTip(Element element) /*-{
		element.__tooltipText = null;
		element.__tooltipWidth = null;
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void displayToolTip() {
		if (currentToolTipElement != null && !isToolTipVisible()) {
			int toolTipWidth = getToolTipWidth(currentToolTipElement);
			
			boolean newWidget = toolTipWidget == null;
			
			if (listeners != null)
				listeners.fireToolTipOpening(currentToolTipElement);
			
			if (newWidget) {
				toolTipWidget = new JSToolTip(getToolTipText(currentToolTipElement), toolTipWidth);
			} else {
				toolTipWidget.setPixelWidth(toolTipWidth);
				toolTipWidget.setText(getToolTipText(currentToolTipElement));
			}
			
			if (newWidget)
				RootPanel.get().add(toolTipWidget, -999, -999);
			
			int x = Math.min(OpenJWT.getClientWidth() - 2 - (toolTipWidth == - 1 ?
				toolTipWidget.getPixelWidth() : toolTipWidth), mouseX + 30);
			int y = Math.min(OpenJWT.getClientHeight() - 5 -
				toolTipWidget.getPixelHeight(), mouseY + 30);
			
			RootPanel.get().setWidgetPosition(toolTipWidget, x, y);
		}
	}
	
	private void hideToolTip() {
		if (isToolTipVisible()) {
			toolTipWidget.removeFromParent();
			toolTipWidget = null;
			
			if (listeners != null)
				listeners.fireToolTipClosed(currentToolTipElement);
		}
	}
	
	private Element getToolTipElement(Element element) {
		String attribute = getToolTipText(element);
		
		if (attribute != null)
			return element;
		
		Element parent = element.getParentElement();
		return parent == null ? null : getToolTipElement(parent);
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
}
