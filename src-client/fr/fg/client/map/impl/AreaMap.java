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
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.core.Utilities;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.Point;

public class AreaMap extends BaseMap {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static double
		TRADE_CENTER_RADIUS = 4.5,
		PIRATES_RADIUS = 4.5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private UIItemFactory factory;
	
	private HashMap<String, UIItem> items;
	
	private int tileSize;
	
	private Widget nearStarField, farStarField;
	
	private boolean gridVisible;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AreaMap(String id, int tileSize, Widget nearStarField,
			Widget farStarField) {
		super(id);
		this.tileSize = tileSize;
		this.factory = new UIItemFactory(getRenderingHints());
		this.items = new HashMap<String, UIItem>();
		this.nearStarField = nearStarField;
		this.farStarField = farStarField;
		this.gridVisible = false;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	// Renvoie la taille d'une case
	public int getTileSize() {
		return this.tileSize;
	}
	
	// Renvoie la taille de la carte, en cases
	public Dimension getMapTileSize() {
		Dimension mapSize = new Dimension();
		mapSize.setWidth((size.getWidth() - 1) / tileSize);
		mapSize.setHeight((size.getHeight() - 1) / tileSize);
		return mapSize;
	}

	public void setMapTileSize(Dimension size) {
		setMapSize(new Dimension(
			size.getWidth()  * tileSize + 1, // +1 pour la dernière ligne du quadrillage
			size.getHeight() * tileSize + 1));
	}
	
	public void setView(Point view) {
		Point oldView = getView();
		
		super.setView(view);
		
		if (getView().getX() / 250 != oldView.getX() / 250 ||
			getView().getY() / 250 != oldView.getY() / 250)
			updateCulledItems();
		
		updateStarfields();
	}

	/**
	 * Renvoie true si la grille de jeu est visible.
	 *
	 * @return true si la grille est visible.
	 */
	public boolean isGridVisible() {
		return this.gridVisible;
	}
	
	public void setGridVisible(boolean visible) {
		this.gridVisible = visible;
		
		if (visible)
			addStyleName("grid"); //$NON-NLS-1$
		else
			removeStyleName("grid"); //$NON-NLS-1$
	}
	
	public void setZoom(double zoom) {
		super.setZoom(zoom);
		fireRenderingHintsUpdate();
		updateCulledItems();
	}
	
	public void addItem(Object data, String dataClass) {
		try {
			UIItem item = factory.createItem(data, dataClass);
			
			if (item != null) {
				add(item);
				items.put(factory.getHashCode(data, dataClass), item);
			}
			
			for (MiniMap miniMap : miniMaps)
				miniMap.addItem(data, dataClass);
			
			if (item != null)
				item.updateCulledState();
		} catch (Exception e) {
			Utilities.log("Failed to add item: " +
					factory.getHashCode(data, dataClass), e);
		}
	}
	
	public UIItem getItem(Object data, String dataClass) {
		return items.get(factory.getHashCode(data, dataClass));
	}
	
	public void updateOrAddItem(Object data, String dataClass) {
		try {
			UIItem item = items.get(factory.getHashCode(data, dataClass));
			
			if (item != null) {
				try {
					item.onDataUpdate(data);
					item.updateCulledState();
				} catch (Exception e) {
					Utilities.log("Failed to update item: " +
							factory.getHashCode(data, dataClass), e);
				}
			} else {
				try {
					item = factory.createItem(data, dataClass);
					
					if (item != null) {
						add(item);
						items.put(factory.getHashCode(data, dataClass), item);
						item.updateCulledState();
					}
				} catch (Exception e) {
					Utilities.log("Failed to add item: " +
							factory.getHashCode(data, dataClass), e);
				}
			}
			
			for (MiniMap miniMap : miniMaps)
				miniMap.updateOrAddItem(data, dataClass);
		} catch (Exception e) {
			Utilities.log("Failed to update or add item: " +
					factory.getHashCode(data, dataClass), e);
		}
	}
	
	public void updateItem(Object data, String dataClass) {
		try {
			UIItem item = items.get(factory.getHashCode(data, dataClass));
			
			if (item != null)
				item.onDataUpdate(data);
			
			for (MiniMap miniMap : miniMaps)
				miniMap.updateItem(data, dataClass);
			
			if (item != null)
				item.updateCulledState();
		} catch (Exception e) {
			Utilities.log("Failed to update item: " +
					factory.getHashCode(data, dataClass), e);
		}
	}
	
	public void removeItem(Object data, String dataClass) {
		try {
			String hash = factory.getHashCode(data, dataClass);
			UIItem item = items.get(hash);
			
			if (item != null) {
				remove(item);
				items.remove(hash);
				item.destroy();
			}
			
			for (MiniMap miniMap : miniMaps)
				miniMap.removeItem(data, dataClass);
		} catch (Exception e) {
			Utilities.log("Failed to remove item: " +
					factory.getHashCode(data, dataClass), e);
		}
	}
	
	public void clear() {
		super.clear();
		
		for (UIItem item : items.values())
			item.destroy();
		items.clear();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	private void updateCulledItems() {
		for (UIItem item : items.values())
			item.updateCulledState();
	}
	
	private void updateStarfields() {
		if (farStarField != null)
			DOM.setStyleAttribute(farStarField.getElement(), "backgroundPosition", //$NON-NLS-1$
				(int) Math.floor(-getView().getX() / 24) + "px " + //$NON-NLS-1$
				(int) Math.floor(-getView().getY() / 24) + "px"); //$NON-NLS-1$
		if (nearStarField != null)
			DOM.setStyleAttribute(nearStarField.getElement(), "backgroundPosition", //$NON-NLS-1$
				(int) Math.floor(-getView().getX() / 12) + "px " + //$NON-NLS-1$
				(int) Math.floor(-getView().getY() / 12) + "px"); //$NON-NLS-1$
	}

	private UIItemRenderingHints getRenderingHints() {
		return new UIItemRenderingHints(this, zoom, tileSize);
	}
	
	private void fireRenderingHintsUpdate() {
		UIItemRenderingHints hints = getRenderingHints();
		
		factory.setRenderingHints(hints);
		
		for (UIItem item : items.values())
			item.setRenderingHints(hints);
	}
}
