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

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.AbsolutePanel;

import fr.fg.client.map.Map;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.Rectangle;

public class BaseMap extends AbsolutePanel implements WindowResizeListener, Map {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	protected Dimension size;
	
	protected Point view;
	
	protected ArrayList<MiniMap> miniMaps;
	
	protected double zoom;

	// Zone de l'écran dans laquelle la carte est restreinte
	protected Dimension bounds;

	// Limite maximale entre le bord de la carte et de l'écran
	protected Rectangle margin;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BaseMap(String id) {
		DOM.setElementAttribute(getElement(), "id", id); //$NON-NLS-1$
		DOM.setElementAttribute(getElement(), "unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.size = new Dimension();
		this.view = new Point();
		this.miniMaps = new ArrayList<MiniMap>();
		this.zoom = 1;
		this.bounds = new Dimension(Window.getClientWidth(),
				Window.getClientHeight());
		this.margin = new Rectangle(500, 500, 500, 500);
		
		Window.addWindowResizeListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //

	/**
	 * Renvoie l'identifiant de l'élément HTML de la carte (élément contenant
	 * tous les objets).
	 *
	 * @return L'identifiant de l'élément représentant la carte.
	 */
	public String getId() {
		return DOM.getElementAttribute(getElement(), "id"); //$NON-NLS-1$
	}
	
	/**
	 * Renvoie les dimensions de la carte.
	 *
	 * @return Les dimensions de la carte, en pixels.
	 */
	public Dimension getMapSize() {
		return this.size;
	}

	/**
	 * Modifie les dimensions de la carte. L'élément HTML représentant la carte
	 * prendra les nouvelles dimensions. La vue est mise à jour pour s'assurer
	 * qu'elle reste dans les limites autorisées.
	 *
	 * @param size Les nouvelles dimensions de la map, en pixels.
	 */
	public void setMapSize(Dimension size) {
		this.size = size;
		setSize((int) Math.ceil(size.getWidth()  * this.zoom) + "px", //$NON-NLS-1$
				(int) Math.ceil(size.getHeight() * this.zoom) + "px"); //$NON-NLS-1$
		
		for (MiniMap miniMap : miniMaps)
			miniMap.setMapSize(size);
		
		updateView();
	}

	/**
	 * Renvoie la marge de la vue, c'est à dire la distance maximale de
	 * laquelle la vue peut déborder une fois les limites de la carte
	 * atteintes.
	 *
	 * @return La marge de la vue.
	 */
	public Rectangle getMargin() {
		return this.margin;
	}

	/**
	 * Renvoie les contours de l'écran.
	 * 
	 * @return Les contours de l'écran, en pixels.
	 */
	public Dimension getBounds() {
		return this.bounds;
	}

	/**
	 * Modifie les contours de l'écran. On considère que le bord de la carte
	 * est atteind lorsque la carte a atteind les contours de l'écran. Cela
	 * permet par exemple de prendre en compte une interface qui masque une
	 * partie de la carte. La vue est mise à jour pour s'assurer qu'elle reste
	 * dans les limites autorisées. Par défaut, les contours de l'écran
	 * représentent la totalité de l'écran.
	 * 
	 * @param {Dimension} Les nouveaux contours de l'écran, en pixels.
	 */
	public void setBounds(Dimension bounds) {
		this.bounds = bounds;
		updateView();
	}
	
	public double getZoom() {
		return this.zoom;
	}
	
	public void setZoom(double zoom) {
		if (zoom == this.zoom)
			return;
		
		// Supprime le niveau de zoom actuel
		if (this.zoom != 1) {
			String className = "zoom" + this.zoom; //$NON-NLS-1$
			if (this.zoom < 1)
				className = "zoom0" + String.valueOf(this.zoom).substring(2); //$NON-NLS-1$
			removeStyleName(className);
		}
		
		// Nouveau niveau de zoom
		this.zoom = zoom;
		
		if (this.zoom != 1) {
			String className = "zoom" + this.zoom; //$NON-NLS-1$
			if (this.zoom < 1)
				className = "zoom0" + String.valueOf(this.zoom).substring(2); //$NON-NLS-1$
			addStyleName(className);
		}
		
		setSize(Math.ceil(this.size.getWidth()  * this.zoom) + "px", //$NON-NLS-1$
				Math.ceil(this.size.getHeight() * this.zoom) + "px"); //$NON-NLS-1$
		
		updateView();
	}
	
	public Point getView() {
		return view;
	}

	public void setView(Point view) {
		// Vérifie que la vue ne sort pas en dehors de la carte
		int maxX = size.getWidth()  + (int) Math.floor((margin.getWidth()  - bounds.getWidth()) / zoom);
		int maxY = size.getHeight() + (int) Math.floor((margin.getHeight() - bounds.getHeight()) / zoom);
		int minX = (int) Math.floor(-margin.getX() / zoom);
		int minY = (int) Math.floor(-margin.getY() / zoom);
		
		if (view.getX() > maxX)
			view.setX(maxX);
		if (view.getY() > maxY)
			view.setY(maxY);
		if (view.getX() < minX)
			view.setX(minX);
		if (view.getY() < minY)
			view.setY(minY);
		
		this.view = view;
		
		// Déplace la carte
		DOM.setStyleAttribute(getElement(), "left", //$NON-NLS-1$
				(int) Math.floor(-this.view.getX() * this.zoom) + "px"); //$NON-NLS-1$
		DOM.setStyleAttribute(getElement(), "top", //$NON-NLS-1$
				(int) Math.floor(-this.view.getY() * this.zoom) + "px"); //$NON-NLS-1$
		
		for (MiniMap miniMap : miniMaps)
			miniMap.update();
	}
	
	public void updateView() {
		setView(view);
	}
	
	public void centerView(Point view) {
		setView(new Point(
			view.getX() - (int) Math.floor(Window.getClientWidth() / (2 * zoom)),
			view.getY() - (int) Math.floor(Window.getClientHeight() / (2 * zoom))));
	}
	
	public ArrayList<MiniMap> getMiniMaps() {
		return miniMaps;
	}
	
	public void addMiniMap(MiniMap miniMap) {
		this.miniMaps.add(miniMap);
	}
	
	/**
	 * Supprime tous les objets de la carte.
	 */
	public void clear() {
		super.clear();
		
		for (MiniMap miniMap : miniMaps)
			miniMap.clear();
	}
	
	public void onWindowResized(int width, int height) {
		this.bounds = new Dimension(Window.getClientWidth(),
				Window.getClientHeight());
		updateView();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
