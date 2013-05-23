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

package fr.fg.client.openjwt.ui;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;

public class JSRowLayout extends JSComponent {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "RowLayout";
	
	public final static int
		ALIGN_LEFT = 0,
		ALIGN_CENTER = 1,
		ALIGN_RIGHT = 2;
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"rowLayout");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int rowsCount;
	
	private ArrayList<ArrayList<Widget>> layoutComponents;

	private ArrayList<Integer> rowsHeight;
	
	private ArrayList<Integer> rowsAlignment;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSRowLayout() {
		super(UI_CLASS_ID);
		
		this.rowsCount = 0;
		this.rowsAlignment = new ArrayList<Integer>();
		this.rowsHeight = new ArrayList<Integer>();
		this.layoutComponents = new ArrayList<ArrayList<Widget>>();
		addRow();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addComponent(Widget component) {
		addComponent(component, rowsCount - 1);
	}
	
	public void addComponent(Widget component, int row) {
		if (row < 0)
			row = 0;
		
		// Ajoute le nombre suffisant de lignes
		if (rowsCount <= row)
			setRowsCount(row + 1);
		
		// Ajoute le composant sur la ligne
		ArrayList<Widget> components = layoutComponents.get(row);
		components.add(components.size() - 1, component);
		insert(component, getElement(), DOM.getChildIndex(getElement(),
				layoutComponents.get(row).get(
						layoutComponents.get(row).size() - 1
						).getElement()), true);
		
		if (isAttached())
			update();
	}
	
	public void addRow() {
		setRowsCount(rowsCount + 1);
	}
	
	public void addRowSeparator(int height) {
		addRow();
		setRowAlignment(ALIGN_RIGHT);
		layoutComponents.get(rowsCount - 1).get(0).setHeight(height + "px");
		rowsHeight.set(rowsCount - 1, height);
		addRow();
	}
	
	public int getRowsCount() {
		return rowsCount;
	}
	
	public void setRowAlignment(int alignment) {
		setRowAlignment(alignment, rowsCount - 1);
	}
	
	public void setRowAlignment(int alignment, int row) {
		if (row >= rowsCount)
			row = rowsCount - 1;
		else if (row < 0)
			row = 0;
		
		rowsAlignment.set(row, alignment);
		
		if (isAttached())
			update();
	}
	
	public void removeRow(int row) {
		if (row >= 0 && row < rowsCount) {
			// Supprime les composants
			ArrayList<Widget> components = layoutComponents.get(row);
			for (int i = 0; i < components.size(); i++)
				remove(components.get(i));
			
			layoutComponents.remove(row);
			rowsAlignment.remove(row);
			rowsHeight.remove(row);
			
			rowsCount--;
			
			if (isAttached())
				update();
		}
	}
	
	public void clear() {
		super.clear();
		
		this.rowsCount = 0;
		this.rowsAlignment = new ArrayList<Integer>();
		this.rowsHeight = new ArrayList<Integer>();
		this.layoutComponents = new ArrayList<ArrayList<Widget>>();
		
		if (isAttached())
			update();
	}
	
	public void update() {
		int maxWidth = 0;
		int totalHeight = 0;
		int[] dimensions = new int[rowsCount];

		// Calcule les dimensions des lignes
		for (int i = 0; i < rowsCount; i++) {
			ArrayList<Widget> components = layoutComponents.get(i);
			int width = 0;
			int rowHeight = rowsHeight.get(i);
			
			for (int j = 1; j < components.size() - 1; j++) {
				Widget widget = components.get(j);
				
				if (!widget.isVisible())
					continue;
				
				if (widget instanceof JSComponent) {
					width += ((JSComponent) widget).getPixelWidth();
					rowHeight = Math.max(rowHeight,
							((JSComponent) widget).getPixelHeight());
				} else {
					width += widget.getOffsetWidth();
					rowHeight = Math.max(rowHeight, widget.getOffsetHeight());
				}
			}
			
			dimensions[i] = width;
			
			components.get(0).setHeight(rowHeight + "px");
			components.get(components.size() - 1).setHeight(rowHeight + "px");
			
			if (width > maxWidth)
				maxWidth = width;
			
			totalHeight += rowHeight;
		}
		
		// Calcule la marge de la ligne pour l'alignement
		for (int i = 0; i < rowsCount; i++) {
			ArrayList<Widget> components = layoutComponents.get(i);
			
			switch (rowsAlignment.get(i)) {
			case ALIGN_LEFT:
				components.get(0).setWidth("0px");
				components.get(0).setHeight("0px");
				
				int width = maxWidth - dimensions[i];
				components.get(components.size() - 1).setWidth(width + "px");
				if (width == 0)
					components.get(components.size() - 1).setHeight("0px");
				break;
			case ALIGN_CENTER:
				width = (int) Math.floor((maxWidth - dimensions[i]) / 2.);
				components.get(0).setWidth(width + "px");
				if (width == 0)
					components.get(0).setHeight("0px");
				
				width = (int) Math.ceil((maxWidth - dimensions[i]) / 2.);
				components.get(components.size() - 1).setWidth(width + "px");
				if (width == 0)
					components.get(components.size() - 1).setHeight("0px");
				break;
			case ALIGN_RIGHT:
				width = maxWidth - dimensions[i];
				components.get(0).setWidth(width + "px");
				if (width == 0)
					components.get(0).setHeight("0px");
				
				components.get(components.size() - 1).setWidth("0px");
				components.get(components.size() - 1).setHeight("0px");
				break;
			}
		}
		
		setPixelSize(maxWidth, totalHeight);
	}
	
	public void onLoad() {
		super.onLoad();
		update();
	}
	
	public static JSComponent createHorizontalSeparator(int width) {
		JSLabel separator = new JSLabel();
		separator.setPixelWidth(width);
		return separator;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void setRowsCount(int rows) {
		while (layoutComponents.size() < rows) {
			layoutComponents.add(new ArrayList<Widget>());
			
			Separator leftSeparator = new Separator(true);
			layoutComponents.get(layoutComponents.size() - 1).add(leftSeparator);
			add(leftSeparator);
			
			Separator rightSeparator = new Separator(false);
			layoutComponents.get(layoutComponents.size() - 1).add(rightSeparator);
			add(rightSeparator);
			
			rowsAlignment.add(ALIGN_LEFT);
			rowsHeight.add(1);
			rowsCount++;
		}
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class Separator extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Separator(boolean first) {
			setElement(DOM.createDiv());
			addStyleName("separator" + (first ? " separator-first" : ""));
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
