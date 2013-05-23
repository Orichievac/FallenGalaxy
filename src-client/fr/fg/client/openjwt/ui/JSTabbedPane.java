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

import com.google.gwt.user.client.Event;

import fr.fg.client.openjwt.OpenJWT;

public class JSTabbedPane extends JSComponent {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "TabbedPane";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"tabbedPane");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private SelectionListenerCollection selectionListeners;
	
	private int selectedIndex;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public JSTabbedPane() {
		super(UI_CLASS_ID);
		
		this.selectedIndex = -1;
		sinkEvents(Event.ONCLICK);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addTab(String tab) {
		addTab(tab, -1);
	}
	
	public void addTab(String tab, int index) {
		ArrayList<String> tabs = new ArrayList<String>();
		tabs.add(tab);
		addTabs(tabs, index);
	}
	
	public void addTabs(ArrayList<String> tabs) {
		addTabs(tabs, -1);
	}
	
	public void addTabs(ArrayList<String> tabs, int index) {
		// Insertion par défaut en fin de liste si l'index n'est pas spécifié
		if (index < 0)
			index = getWidgetCount();
		
		if (getWidgetCount() > 0) {
			if (index == 0)
				getWidget(0).removeStyleDependentName("first");
			else if (index == getWidgetCount())
				getWidget(index - 1).removeStyleDependentName("last");
		}
		
		// Crée les nouveaux éléments
		for (int i = 0; i < tabs.size(); i++) {
			JSTab tab = new JSTab(tabs.get(i));
			
			if (i == getWidgetCount())
				// Insertion en fin de liste
				add(tab);
			else
				// Insertion en milieu de liste
				insert(tab, getElement(), i + index, true);
		}
		
		if (getWidgetCount() > 0) {
			getWidget(0).addStyleDependentName("first");
			getWidget(getWidgetCount() - 1).addStyleDependentName("last");
			
			if (getSelectedIndex() == -1)
				setSelectedIndex(0);
		}
	}
	
	public void removeTab(String tab) {
		int count = getWidgetCount();
		for (int i = 0; i < count; i++)
			if (((JSTab) getWidget(i)).getLabel().equals(tab)) {
				remove(i);
				
				if (i == 0 && count > 1)
					getWidget(0).addStyleDependentName("first");
				if (i == count -1 && count > 1)
					getWidget(count - 2).addStyleDependentName("last");
				
				if (i == selectedIndex && selectionListeners != null)
					selectionListeners.fireSelectionChange(this, -1, i);
				break;
			}
	}
	
	public void removeTabAt(int index) {
		int count = getWidgetCount();
		if (index >= 0 && index < count) {
			remove(index);
			
			if (this.selectedIndex > index)
				this.selectedIndex--;
			
			if (index == 0 && count > 1)
				getWidget(0).addStyleDependentName("first");
			if (index == count -1 && count > 1)
				getWidget(count - 2).addStyleDependentName("last");
			
			if (index == selectedIndex && selectionListeners != null) {
				this.selectedIndex = -1;
				selectionListeners.fireSelectionChange(this, -1, index);
			}
		}
	}
	
	public void setTabs(ArrayList<String> tabs) {
		// Supprime les anciens onglets
		clear();
		
		int oldValue = this.selectedIndex;
		this.selectedIndex = -1;
		
		// Ajoute les nouveaux onglets
		addTabs(tabs);
		
		if (oldValue == -1 && getSelectedIndex() != -1 && selectionListeners != null)
			selectionListeners.fireSelectionChange(this, getSelectedIndex(), oldValue);
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public String getSelectedTab() {
		if (selectedIndex == -1)
			return null;
		return ((JSTab) getWidget(selectedIndex)).getLabel();
	}
	
	public String getTabAt(int index) {
		if (index >= 0 && index < getWidgetCount())
			return ((JSTab) getWidget(index)).getLabel();
		return null;
	}
	
	public void setTabAt(String tab, int index) {
		if (index >= 0 && index < getWidgetCount())
			((JSTab) getWidget(index)).setLabel(tab);
	}
	
	public void setSelectedIndex(int index) {
		if (index == selectedIndex)
			return;
		
		// Déselectionne l'onglet s'il y en a un sélectionné
		int oldValue = this.selectedIndex;
		if (oldValue != -1)
			getWidget(oldValue).removeStyleDependentName("selected");
		
		// Sélectionne le nouvel onglet
		if (index < -1)
			index = -1;
		if (index >= getWidgetCount())
			index = getWidgetCount() - 1;
		
		this.selectedIndex = index;
		
		if (index != -1)
			getWidget(index).addStyleDependentName("selected");
		
		// Signale le changement de valeur
		if (selectionListeners != null)
			selectionListeners.fireSelectionChange(this, index, oldValue);
	}
	
	public void setSelectedTab(String tab) {
		int count = getWidgetCount();
		for (int i = 0; i < count; i++)
			if (((JSTab) getWidget(i)).getLabel().equals(tab)) {
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
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
