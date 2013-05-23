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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.OpacityUpdater;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;

public class JSComboBox extends JSComponent implements EventPreview,
		ClickListener, SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "ComboBox";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"comboBox");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private SelectionListenerCollection selectionListeners;
	
	private OpacityUpdater updater;
	
	private JSButton selectedItemButton;
	
	private JSList itemsList;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSComboBox() {
		super(UI_CLASS_ID);
		
		this.selectedItemButton = new JSButton();
		this.selectedItemButton.addStyleName("selectedItem");
		this.selectedItemButton.addClickListener(this);
		
		// Liste déroulante permettant de sélectionner un item
		this.itemsList = new JSList();
		this.itemsList.setPixelSize(selectedItemButton.getPixelWidth(), 100);
		this.itemsList.setVisible(false);
		this.itemsList.addSelectionListener(this);
		
		add(selectedItemButton);
		add(itemsList, 0, selectedItemButton.getPixelHeight());
		
		int width = super.getPixelWidth();
		setPixelWidth(width == -1 ? selectedItemButton.getPixelWidth() : width);
		
		int height = super.getPixelHeight();
		setPixelHeight(height == -1 ? selectedItemButton.getPixelHeight() : height);
		
		sinkEvents(Event.ONCLICK);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addItem(Object item) {
		this.itemsList.addItem(item);
		if (getSelectedIndex() == -1)
			setSelectedIndex(0);
	}

	public void addItem(Object item, int index) {
		this.itemsList.addItem(item, index);
		if (getSelectedIndex() == -1)
			setSelectedIndex(0);
	}
	
	public void addItems(ArrayList<?> items) {
		this.itemsList.addItems(items);
		if (getSelectedIndex() == -1 && items.size() > 0)
			setSelectedIndex(0);
	}

	public void addItems(ArrayList<?> items, int index) {
		this.itemsList.addItems(items, index);
		if (getSelectedIndex() == -1 && items.size() > 0)
			setSelectedIndex(0);
	}
	
	public void removeItem(Object item) {
		this.itemsList.removeItem(item);
	}
	
	public void removeItemAt(int index) {
		this.itemsList.removeItemAt(index);
	}
	
	public void setItems(ArrayList<?> items) {
		this.itemsList.setItems(items);
		if (getSelectedIndex() == -1 && items.size() > 0)
			setSelectedIndex(0);
	}
	
	public void setPixelWidth(int width) {
		if (selectedItemButton != null) {
			selectedItemButton.setPixelWidth(width);
			itemsList.setPixelWidth(width);
			super.setPixelWidth(width);
		}
	}
	
	public void setPixelHeight(int height) {
		if (selectedItemButton != null) {
			selectedItemButton.setPixelHeight(height);
			setWidgetPosition(itemsList, 0, height);
			super.setPixelHeight(height);
		}
	}
	
	public void setItemsListPixelHeight(int height) {
		itemsList.setPixelHeight(height);
	}
	
	public void setSelectedIndex(int index) {
		itemsList.setSelectedIndex(index);
	}
	
	public int getSelectedIndex() {
		return itemsList.getSelectedIndex();
	}
	
	public Object getSelectedItem() {
		return itemsList.getSelectedItem();
	}
	
	public int getItemsCount() {
		return itemsList.getItemsCount();
	}
	
	public Object getItemAt(int index) {
		return itemsList.getItemAt(index);
	}
	
	public void addSelectionListener(SelectionListener listener) {
		if (selectionListeners == null)
			selectionListeners = new SelectionListenerCollection();
		selectionListeners.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		if (selectionListeners != null)
			selectionListeners.remove(listener);
	}
	
	public boolean isItemsListVisible() {
		return updater != null && !updater.isFinished() ?
			updater.getTargetOpacity() != 0 : itemsList.isVisible();
	}
	
	public void setItemsListVisible(boolean visible) {
		if (isItemsListVisible() != visible) {
			selectedItemButton.setSelected(visible);
			
			DOM.setStyleAttribute(getElement(), "zIndex",
					String.valueOf(visible ? 999999 : ""));
			
			if (visible)
				DOM.addEventPreview(this);
			else
				DOM.removeEventPreview(this);
			
			if (Config.getGraphicsQuality() >=
					Config.VALUE_QUALITY_AVERAGE) {
				if (updater != null) {
					if (updater.isFinished()) {
						updater = new OpacityUpdater(itemsList.getElement(),
								visible ? 0 : 1, visible ? 2.5 : 5,
								OpacityUpdater.HIDE_WHEN_TRANSPARENT);
						TimerManager.register(updater);
					}
					
					updater.setIncrement(visible ? 5 : 2.5);
					updater.setTargetOpacity(visible ? 1 : 0, true);
				} else {
					updater = new OpacityUpdater(itemsList.getElement(),
							visible ? 0 : 1, visible ? 2.5 : 5,
							OpacityUpdater.HIDE_WHEN_TRANSPARENT);
					TimerManager.register(updater);
					updater.setIncrement(visible ? 5 : 2.5);
					updater.setTargetOpacity(visible ? 1 : 0, true);
				}
				
				if (visible)
					itemsList.setVisible(true);
			} else {
				itemsList.setVisible(visible);
			}
			
			if (visible)
				itemsList.update();
		}
	}
	
	public void onUnload() {
		super.onUnload();
		
		if (this.updater != null) {
			TimerManager.unregister(updater);
			this.updater = null;
		}
	}
	
	public boolean onEventPreview(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
		    Element target = DOM.eventGetTarget(event);

		    boolean eventTargetsComboBox = (target != null) &&
		    	DOM.isOrHasChild(getElement(), target);
		    
			if (!eventTargetsComboBox)
				setItemsListVisible(false);
			break;
		}
		
		return true;
	}

	public void onClick(Widget sender) {
		setItemsListVisible(!itemsList.isVisible());
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (newValue == -1)
			selectedItemButton.setLabel("&nbsp;");
		else
			selectedItemButton.setLabel(itemsList.getSelectedItem().toString());
		setItemsListVisible(false);
		
		if (selectionListeners != null)
			selectionListeners.fireSelectionChange(this, newValue, oldValue);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			// Masque la liste quand on clic sur un élément déjà sélectionné
			if (isItemsListVisible() &&
					itemsList.getScrollPane().getView().getElement(
					).isOrHasChild(event.getTarget())) {
				setItemsListVisible(false);
			}
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
