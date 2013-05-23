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

package fr.fg.client.map.impl;

import java.util.HashMap;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.core.Utilities;
import fr.fg.client.map.UIMiniItem;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.Point;

public class MiniMap extends SimplePanel implements EventListener,
		WindowResizeListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static double
		MARGIN_SIZE = 0.03,
		SIZE_COEF = 1 - 2 * MARGIN_SIZE;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private UIMiniItemFactory factory;
	
	private HashMap<String, UIMiniItem> items;
	
	private AbsolutePanel miniMapContent;
	
	private Point miniMapLocation;
	
	private Dimension size;
	
	private AreaMap map;
	
	private BaseWidget view;
	
	private Widget mask;
	
	private Dimension viewSize;
	
	private boolean mouseDown;
	
	private Point offset;
	
	private double zoom;
	
	private int mouseX, mouseY;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public MiniMap(Dimension size, AreaMap map) {
		this.zoom = 1;
		this.offset = new Point();
		this.items = new HashMap<String, UIMiniItem>();
		this.size = new Dimension(size);
		this.map = map;
		this.mouseDown = false;
		this.viewSize = new Dimension(size);
		this.factory = new UIMiniItemFactory(getRenderingHints());
		
		setStyleName("miniMap"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		addStyleName("miniMap-zoom" + (int) zoom);
		
		miniMapContent = new AbsolutePanel();
		miniMapContent.setStyleName("miniMap-content"); //$NON-NLS-1$
		miniMapContent.getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		add(miniMapContent);
		
		view = new BaseWidget();
		view.addStyleName("view"); //$NON-NLS-1$
		view.getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		miniMapContent.add(view, 0, 0);
		
		mask = new MiniMapMask();
		miniMapContent.add(mask, 0, 0);
		
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEMOVE |
				Event.ONMOUSEUP | Event.ONCLICK | Event.ONMOUSEWHEEL);
		
		Window.addWindowResizeListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addItem(Object data, String dataClass) {
		UIMiniItem item = factory.createMiniItem(data, dataClass);
		
		if (item != null) {
			miniMapContent.add(item);
			
			items.put(factory.getHashCode(data, dataClass), item);
		}
	}
	
	public UIMiniItem getItem(Object data, String dataClass) {
		return items.get(factory.getHashCode(data, dataClass));
	}
	
	public void updateItem(Object data, String dataClass) {
		UIMiniItem item = getItem(data, dataClass);
		
		if (item != null)
			item.onDataUpdate(data);
	}
	
	public void removeItem(Object data, String dataClass) {
		String hash = factory.getHashCode(data, dataClass);
		UIMiniItem item = items.get(hash);
		
		if (item != null) {
			miniMapContent.remove(item);
			items.remove(hash);
			item.destroy();
		}
	}
	
	public void updateOrAddItem(Object data, String dataClass) {
		try {
			UIMiniItem item = items.get(factory.getHashCode(data, dataClass));
			
			if (item != null)
				updateItem(data, dataClass);
			else
				addItem(data, dataClass);
		} catch (Exception e) {
			Utilities.log("Failed to update or add item: " +
					factory.getHashCode(data, dataClass), e);
		}
	}
	
	public void clear() {
		miniMapContent.clear();
		
		for (UIMiniItem item : items.values())
			item.destroy();
		items.clear();
		
		miniMapContent.add(view, 0, 0);
		miniMapContent.add(mask, 0, 0);
		
		setZoom(1);
	}
	
	/**
	 * Redimensionne la minimap.
	 * 
	 * @param size Dimension de la minimap.
	 */
	public void setSize(Dimension size) {
		this.size = new Dimension(size);
		
		fireRenderingHintsUpdate();
		
		updateMiniMapLocation();
		update();
	}
	
	public void setMapSize(Dimension size) {
		fireRenderingHintsUpdate();
	}
	
	public void update() {
		Point mapViewLocation = map.getView();
		Dimension mapSize = map.getMapSize();
		Dimension mapBounds = map.getBounds();
		
		// Met à jour les dimensions du rectangle
		viewSize = new Dimension(
			(int) Math.floor(zoom * Math.min(mapSize.getWidth() > 0 ?
					mapBounds.getWidth() * SIZE_COEF * size.getWidth() / (
					mapSize.getWidth() * map.getZoom()) :
					size.getWidth(), size.getWidth())),
			
			(int) Math.floor(zoom * Math.min(mapSize.getHeight() > 0 ?
					mapBounds.getHeight() * SIZE_COEF * size.getHeight() / (
					mapSize.getHeight() * map.getZoom()) :
					size.getHeight(), size.getHeight()))
		);
		
		// Met à jour la position du rectangle
		int x = mapSize.getWidth()  > 0 ?
				(int) (offset.getX() + zoom * (
				(int) (size.getWidth() * MARGIN_SIZE +
				mapViewLocation.getX() *
				SIZE_COEF * size.getWidth()  / mapSize.getWidth())))  : 0;
		int y = mapSize.getHeight() > 0 ?
				(int) (offset.getY() + zoom * (
				(int) (size.getHeight() * MARGIN_SIZE +
				mapViewLocation.getY() *
				SIZE_COEF * size.getHeight() / mapSize.getHeight()))) : 0;
		int width = viewSize.getWidth();
		int height = viewSize.getHeight();
		
		// Vérifie que la vue reste dans les limites de la minimap
		if (zoom == 1) {
			if (x < 0) {
				width += x;
				x = 0;
			} else if (x + width >= size.getWidth() - 2) {
				width = size.getWidth() - 2 - x;
			}
			
			if (y < 0) {
				height += y;
				y = 0;
			} else if (y + height >= size.getHeight() - 2) {
				height = size.getHeight() - 2 - y;
			}
		}
		
		Element viewElement = view.getElement();
		DOM.setStyleAttribute(viewElement, "marginLeft", x + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setStyleAttribute(viewElement, "marginTop",  y + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setStyleAttribute(viewElement, "width",  width  + "px"); //$NON-NLS-1$ //$NON-NLS-2$
		DOM.setStyleAttribute(viewElement, "height", height + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void onLoad() {
		super.onLoad();
		updateMiniMapLocation();
	}
	
	public void onBrowserEvent(Event event) {
		boolean click = false;
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
			mouseDown = true;
			
			event.preventDefault();
			event.cancelBubble(true);
			break;
		case Event.ONCLICK:
			click = true;
			// Pas de break
		case Event.ONMOUSEMOVE:
			mouseX = OpenJWT.eventGetPointerX(event) -
				miniMapLocation.getX();
			mouseY = OpenJWT.eventGetPointerY(event) -
				miniMapLocation.getY();
			
			if (mouseDown || click) {
				int viewX = (int) (-offset.getX() / zoom + mouseX / zoom - viewSize.getWidth()  / (2 * zoom));
				int viewY = (int) (-offset.getY() / zoom + mouseY / zoom - viewSize.getHeight() / (2 * zoom));
				
				Dimension mapSize = map.getMapSize();
				map.setView(new Point(
					(int) Math.floor(((viewX - size.getWidth()  * MARGIN_SIZE) *
						mapSize.getWidth()  / (SIZE_COEF * size.getWidth()))),
					(int) Math.floor(((viewY - size.getHeight() * MARGIN_SIZE) *
						mapSize.getHeight() / (SIZE_COEF * size.getHeight())))
				));
				
				event.preventDefault();
				event.cancelBubble(true);
			}
			break;
		case Event.ONMOUSEUP:
			mouseDown = false;
			
			event.preventDefault();
			event.cancelBubble(true);
			break;
		case Event.ONMOUSEWHEEL:
			int wheelDelta = DOM.eventGetMouseWheelVelocityY(event);
			
			if (wheelDelta < 0) {
				if (zoom <= 2) {
					removeStyleName("miniMap-zoom" + (int) zoom);
					zoom = zoom * 2;
					addStyleName("miniMap-zoom" + (int) zoom);
					
					offset = new Point(
						offset.getX() * 2 - 2 * mouseX + size.getWidth() / 2,
						offset.getY() * 2 - 2 * mouseY + size.getHeight() / 2);
					
					fireRenderingHintsUpdate();
					update();
				}
			} else {
				if (zoom > 1) {
					removeStyleName("miniMap-zoom" + (int) zoom);
					zoom = zoom / 2;
					addStyleName("miniMap-zoom" + (int) zoom);
					
					if (zoom == 1)
						offset = new Point();
					else
						offset = new Point(
							(int) (offset.getX() / 2 + size.getWidth()  / (2 * zoom)),
							(int) (offset.getY() / 2 + size.getHeight() / (2 * zoom)));
					
					fireRenderingHintsUpdate();
					update();
				}
			}
			
			DOM.eventPreventDefault(event);
			DOM.eventCancelBubble(event, true);
			break;
		}
	}
	
	public void onWindowResized(int width, int height) {
		updateMiniMapLocation();
		update();
		
		Timer timer = new Timer() {
			@Override
			public void run() {
				updateMiniMapLocation();
				update();
			}
		};
		timer.schedule(50);
	}
	
	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		removeStyleName("miniMap-zoom" + (int) this.zoom);
		this.zoom = zoom;
		addStyleName("miniMap-zoom" + (int) this.zoom);
		
		if (zoom == 1)
			offset = new Point();
		
		fireRenderingHintsUpdate();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void updateMiniMapLocation() {
		Element element = miniMapContent.getElement();
		
		miniMapLocation = new Point(
				element.getAbsoluteLeft(),
				element.getAbsoluteTop());
	}
	
	protected UIMiniItemRenderingHints getRenderingHints() {
		return new UIMiniItemRenderingHints(
			map.getMapTileSize().getWidth() == 0 ? 0 :
				SIZE_COEF * size.getWidth() /
				map.getMapTileSize().getWidth(),
			map.getMapTileSize().getHeight() == 0 ? 0 :
				SIZE_COEF * size.getHeight() /
				map.getMapTileSize().getHeight(),
			zoom, offset, new Point(
				(int) (size.getWidth()  * MARGIN_SIZE),
				(int) (size.getHeight() * MARGIN_SIZE)));
	}
	
	protected void fireRenderingHintsUpdate() {
		UIMiniItemRenderingHints hints = getRenderingHints();
		
		factory.setRenderingHints(hints);
		
		for (int i = 0; i < miniMapContent.getWidgetCount(); i++) {
			Widget widget = miniMapContent.getWidget(i);
			
			if (widget instanceof UIMiniItem) {
				UIMiniItem item = (UIMiniItem) widget;
				item.setRenderingHints(hints);
			}
		}
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class MiniMapMask extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public MiniMapMask() {
			setElement(DOM.createDiv());
			addStyleName("miniMapMask"); //$NON-NLS-1$
			getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
