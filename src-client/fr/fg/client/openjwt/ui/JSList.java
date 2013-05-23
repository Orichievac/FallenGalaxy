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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.ClassNameUpdater;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class JSList extends JSComponent implements SourceSelectionEvents {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "List";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,				"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,				"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,			"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,			"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,			"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,		"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,		"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HORIZONTAL_MARGIN,"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,				"list");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_X,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_Y,	"0");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private SelectionListenerCollection selectionListeners;
	
	private JSScrollPane scrollPane;
	
	private FlowPanel listContainer;
	
	private int selectedIndex;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSList() {
		super(UI_CLASS_ID);
		
		this.selectedIndex = -1;
		this.scrollPane = new JSScrollPane();
		this.listContainer = new FlowPanel();
		this.scrollPane.setView(listContainer);
		
		// Force l'ajout du scrollpane au composant pour que les listeners
		// soient correctement attachés, et déplace le scrollpane au bon
		// endroit dans la structure de la liste
		add(scrollPane);
		getElement().removeChild(scrollPane.getElement());
		loadInnerHTML(new String[]{"content"});
		getSubElementById("content").appendChild(scrollPane.getElement());
		
		sinkEvents(Event.ONCLICK);
	}
	
	public JSList(ArrayList<Object> items) {
		this();
		setItems(items);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addItem(Object item) {
		addItem(item, -1);
	}
	
	public void addItem(Object item, int index) {
		ArrayList<Object> items = new ArrayList<Object>();
		items.add(item);
		addItems(items, index);
	}
	
	public void addItems(ArrayList<?> items) {
		addItems(items, -1);
	}
	
	public void addItems(ArrayList<?> items, int index) {
		// Insertion par défaut en fin de liste si l'index n'est pas spécifié
		if (index < 0)
			index = listContainer.getWidgetCount();
		
		if (listContainer.getWidgetCount() > 0) {
			if (index == 0)
				listContainer.getWidget(0).removeStyleDependentName("first");
			else if (index == listContainer.getWidgetCount())
				listContainer.getWidget(index - 1).removeStyleDependentName("last");
		}
		
		// Crée les nouveaux éléments
		for (int i = 0; i < items.size(); i++) {
			ListItem item = new ListItem(items.get(i));
			
			if (i == listContainer.getWidgetCount())
				// Insertion en fin de liste
				listContainer.add(item);
			else
				// Insertion en milieu de liste
				listContainer.insert(item, i + index);
		}
		
		if (listContainer.getWidgetCount() > 0) {
			listContainer.getWidget(0).addStyleDependentName("first");
			listContainer.getWidget(listContainer.getWidgetCount() - 1).addStyleDependentName("last");
		}
		
		this.scrollPane.update();
	}
	
	public void removeItem(Object item) {
		int count = listContainer.getWidgetCount();
		for (int i = 0; i < count; i++)
			if (((ListItem) listContainer.getWidget(i)).getItem() == item) {
				listContainer.remove(i);
				
				if (i == 0 && count > 1)
					listContainer.getWidget(0).addStyleDependentName("first");
				if (i == count -1 && count > 1)
					listContainer.getWidget(count - 2).addStyleDependentName("last");

				this.scrollPane.update();
				
				if (i == selectedIndex && selectionListeners != null)
					selectionListeners.fireSelectionChange(this, -1, i);
				break;
			}
	}
	
	public void removeItemAt(int index) {
		int count = listContainer.getWidgetCount();
		if (index >= 0 && index < count) {
			listContainer.remove(index);
			
			if (this.selectedIndex > index)
				this.selectedIndex--;
			
			if (index == 0 && count > 1)
				listContainer.getWidget(0).addStyleDependentName("first");
			if (index == count -1 && count > 1)
				listContainer.getWidget(count - 2).addStyleDependentName("last");
			
			this.scrollPane.update();
			
			if (index == selectedIndex && selectionListeners != null) {
				this.selectedIndex = -1;
				selectionListeners.fireSelectionChange(this, -1, index);
			}
		}
	}
	
	public void setItems(ArrayList<?> items) {
		// Supprime les anciens items
		listContainer.clear();
		
		int oldValue = this.selectedIndex;
		this.selectedIndex = -1;
		
		// Ajoute les nouveaux items
		addItems(items);
		
		if (oldValue != -1 && selectionListeners != null)
			selectionListeners.fireSelectionChange(this, -1, oldValue);
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public Object getSelectedItem() {
		if (selectedIndex == -1)
			return null;
		return ((ListItem) listContainer.getWidget(selectedIndex)).getItem();
	}
	
	public JSScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public int getItemsCount() {
		return listContainer.getWidgetCount();
	}
	
	public Object getItemAt(int index) {
		if (index >= 0 && index < listContainer.getWidgetCount())
			return ((ListItem) listContainer.getWidget(index)).getItem();
		return null;
	}
	
	public void setItemAt(Object item, int index) {
		if (index >= 0 && index < listContainer.getWidgetCount())
			((ListItem) listContainer.getWidget(index)).setItem(item);
	}
	
	public void setSelectedIndex(int index) {
		if (index == selectedIndex)
			return;
		
		// Déselectionne l'item s'il y en a un sélectionné
		int oldValue = this.selectedIndex;
		if (oldValue != -1)
			listContainer.getWidget(oldValue).removeStyleDependentName("selected");
		
		// Sélectionne le nouvel item
		if (index < -1)
			index = -1;
		if (index >= listContainer.getWidgetCount())
			index = listContainer.getWidgetCount() - 1;
		
		this.selectedIndex = index;
		
		if (index != -1)
			listContainer.getWidget(index).addStyleDependentName("selected");
		
		// Signale le changement de valeur
		if (selectionListeners != null)
			selectionListeners.fireSelectionChange(this, index, oldValue);
	}
	
	public void setSelectedItem(Object item) {
		int count = listContainer.getWidgetCount();
		for (int i = 0; i < count; i++)
			if (((ListItem) listContainer.getWidget(i)).getItem() == item) {
				setSelectedIndex(i);
				break;
			}
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

	public void setPixelWidth(int width) {
		super.setPixelWidth(width);
		if (scrollPane != null)
			scrollPane.setPixelWidth(width - getUIPropertyInt(OpenJWT.INNER_HORIZONTAL_MARGIN));
	}

	public void setPixelHeight(int height) {
		super.setPixelHeight(height);
		if (scrollPane != null)
			scrollPane.setPixelHeight(height - getUIPropertyInt(OpenJWT.INNER_VERTICAL_MARGIN));
	}
	
	public void setWidth(String width) {
		super.setWidth(width);
		if (scrollPane != null)
			scrollPane.setWidth(width);
	}

	public void setHeight(String height) {
		super.setHeight(height);
		if (scrollPane != null)
			scrollPane.setHeight(height);
	}
	
	public void update() {
		scrollPane.update();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class ListItem extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private ClassNameUpdater updater;
		
		private Object item;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ListItem(Object item) {
			this.item = item;
			setElement(DOM.createDiv());
			setStylePrimaryName("listItem");
			getElement().setAttribute("unselectable", "on");

			String decoration = getUIProperty(OpenJWT.FX_DECORATION);
			
			if (decoration.equals("")) {
				DOM.setInnerHTML(getElement(), item.toString());
			} else {
				OutlineText outlineText = TextManager.getText(item.toString(), decoration, new Point(
					getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_X),
					getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_Y)), true);
				
				DOM.setInnerHTML(getElement(), "");
				getElement().appendChild(outlineText.getElement());
			}
			
			sinkEvents(Event.ONCLICK | Event.ONMOUSEDOWN);
			if (Config.getGraphicsQuality() >=
					Config.VALUE_QUALITY_AVERAGE) {
				sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER);
			}
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public Object getItem() {
			return item;
		}
		
		public void setItem(Object item) {
			this.item = item;
			
			String decoration = getUIProperty(OpenJWT.FX_DECORATION);
			
			if (decoration.equals("")) {
				DOM.setInnerHTML(getElement(), item.toString());
			} else {
				OutlineText outlineText = TextManager.getText(item.toString(), decoration, new Point(
					getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_X),
					getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_Y)), true);
				
				DOM.setInnerHTML(getElement(), "");
				getElement().appendChild(outlineText.getElement());
			}
		}
		
		public void onUnload() {
			super.onUnload();
			if (this.updater != null) {
				TimerManager.unregister(updater);
				this.updater = null;
			}
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			switch (DOM.eventGetType(event)) {
			case Event.ONCLICK:
				Widget parent = getParent();
				int index = DOM.getChildIndex(parent.getElement(), getElement());
				
				while (!(parent instanceof JSList))
					parent = parent.getParent();
				JSList list = (JSList) parent;
				list.setSelectedIndex(index);
				break;
			case Event.ONMOUSEOUT:
				if (event.getToElement() != null &&
						!getElement().isOrHasChild(event.getToElement())) {
					if (updater != null) {
						if (updater.isFinished()) {
							updater = new ClassNameUpdater(this, "state", 4, 20);
							TimerManager.register(updater);
						}
						updater.setTargetClass(0, true);
					} else {
						updater = new ClassNameUpdater(this, "state", 4, 20);
						updater.setTargetClass(0, true);
						TimerManager.register(updater);
					}
				}
				break;
			case Event.ONMOUSEOVER:
				if (event.getFromElement() != null &&
						!getElement().isOrHasChild(event.getFromElement()) &&
						Config.getGraphicsQuality() >=
						Config.VALUE_QUALITY_AVERAGE) {
					if (updater != null) {
						if (updater.isFinished()) {
							updater = new ClassNameUpdater(this, "state", 0, 20);
							TimerManager.register(updater);
						}
						updater.setTargetClass(4, true);
					} else {
						updater = new ClassNameUpdater(this, "state", 0, 20);
						updater.setTargetClass(4, true);
						TimerManager.register(updater);
					}
					updater.setTargetClass(4, true);
				}
				break;
			case Event.ONMOUSEDOWN:
				SoundManager.getInstance().playSound(SOUND_CLICK);
				break;
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
