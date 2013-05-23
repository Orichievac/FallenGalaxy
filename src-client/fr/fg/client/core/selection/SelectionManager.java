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

package fr.fg.client.core.selection;

import fr.fg.client.core.Client;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerFleetsData;
import fr.fg.client.data.PlayerSystemsData;

public class SelectionManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static SelectionManager instance = new SelectionManager();
	
	private Selection selection;
	
	private SelectionListenerCollection selectionListeners;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private SelectionManager() {
		this.selection = new Selection();
		this.selectionListeners = null;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Selection getSelection() {
		return selection;
	}
	
	public int getIdSelectedSystem() {
		return isSystemSelected() ? (int) selection.getFirstIdSelection() : -1;
	}
	
	public int getIdSelectedSpaceStation() {
		return isSpaceStationSelected() ? (int) selection.getFirstIdSelection() : -1;
	}
	
	public long getIdSelectedStructure() {
		return isStructureSelected() ? selection.getFirstIdSelection() : -1;
	}
	
	public long[] getIdSelectedFleets() {
		return isFleetSelected() ? selection.getIdSelections() : new long[0];
	}
	
	public boolean isFleetSelected(int idFleet) {
		long[] idSelectedFleets = getIdSelectedFleets();
		
		for (long idSelectedFleet : idSelectedFleets)
			if (idSelectedFleet == idFleet)
				return true;
		return false;
	}
	
	public void selectSystem(int idSelectedSystem) {
		if (isSystemSelected() && getIdSelectedSystem() == idSelectedSystem)
			return;
		
		Selection oldSelection = selection;
		selection = new Selection(Selection.TYPE_SYSTEM, idSelectedSystem);
		
		fireSelectionChanged(selection, oldSelection);
	}
	
	public void selectFleet(int idSelectedFleet) {
		if (isFleetSelected() && getIdSelectedFleets().length == 1 &&
				getIdSelectedFleets()[0] == idSelectedFleet)
			return;
		
		Selection oldSelection = selection;
		selection = new Selection(Selection.TYPE_FLEET, idSelectedFleet);
		
		fireSelectionChanged(selection, oldSelection);

	}
	
	public void selectSpaceStation(int idSelectedSpaceStation) {
		if (isSpaceStationSelected() &&
				getIdSelectedSpaceStation() == idSelectedSpaceStation)
			return;

		Selection oldSelection = selection;
		selection = new Selection(Selection.TYPE_SPACE_STATION, idSelectedSpaceStation);
		
		fireSelectionChanged(selection, oldSelection);
	}
	
	public void selectStructure(long idSelectedStructure) {
		if (isStructureSelected() &&
				getIdSelectedStructure() == idSelectedStructure)
			return;
		
		Selection oldSelection = selection;
		selection = new Selection(Selection.TYPE_STRUCTURE, idSelectedStructure);
		
		fireSelectionChanged(selection, oldSelection);
	}
	
	public void addSelectedFleet(int idSelectedFleet) {
		if (isFleetSelected()) {
			long[] idSelectedFleets = getIdSelectedFleets();
			for (long idFleet : idSelectedFleets)
				if (idFleet == idSelectedFleet)
					return;
			
			long[] newIdSelectedFleets = new long[idSelectedFleets.length + 1];
			for (int i = 0; i < idSelectedFleets.length; i++)
				newIdSelectedFleets[i] = idSelectedFleets[i];
			newIdSelectedFleets[idSelectedFleets.length] = idSelectedFleet;
			
			Selection oldSelection = selection;
			selection = new Selection(Selection.TYPE_FLEET, newIdSelectedFleets);
			
			fireSelectionChanged(selection, oldSelection);
		} else {
			selectFleet(idSelectedFleet);
		}
	}
	
	public void removeSelectedFleet(int idSelectedFleet) {
		if (isFleetSelected()) {
			long[] idSelectedFleets = getIdSelectedFleets();
			
			for (long idFleet : idSelectedFleets)
				if (idFleet == idSelectedFleet) {
					if (idSelectedFleets.length == 1) {
						setNoSelection();
						return;
					}
					
					long[] newIdSelectedFleets = new long[idSelectedFleets.length - 1];
					int index = 0;
					for (int i = 0; i < idSelectedFleets.length; i++) {
						if (idSelectedFleets[i] == idFleet)
							continue;
						newIdSelectedFleets[index++] = idSelectedFleets[i];
					}
					
					Selection oldSelection = selection;
					selection = new Selection(Selection.TYPE_FLEET, newIdSelectedFleets);
					
					fireSelectionChanged(selection, oldSelection);
					break;
				}
		}
	}
	
	public void setNoSelection() {
		if (!isItemSelected())
			return;
		
		Selection oldSelection = selection;
		selection = new Selection();
		
		fireSelectionChanged(selection, oldSelection);
	}
	
	public int getSelectedFleetsCount() {
		return isFleetSelected() ? getIdSelectedFleets().length : 0;
	}
	
	public boolean isItemSelected() {
		return isSystemSelected() || isFleetSelected() ||
			isSpaceStationSelected() || isStructureSelected();
	}
	
	public boolean isSystemSelected() {
		return selection.getType() == Selection.TYPE_SYSTEM;
	}
	
	public boolean isFleetSelected() {
		return selection.getType() == Selection.TYPE_FLEET;
	}
	
	public boolean isSpaceStationSelected() {
		return selection.getType() == Selection.TYPE_SPACE_STATION;
	}
	
	public boolean isStructureSelected() {
		return selection.getType() == Selection.TYPE_STRUCTURE;
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
	
	public void updateSelection(IndexedAreaData areaData) {
		switch (selection.getType()) {
		case Selection.TYPE_FLEET:
			long[] idSelectedFleets = getIdSelectedFleets();
			for (long idSelectedFleet : idSelectedFleets)
				if (areaData.getFleetById((int) idSelectedFleet) == null)
					removeSelectedFleet((int) idSelectedFleet);
			break;
		case Selection.TYPE_SPACE_STATION:
			if (areaData.getSpaceStationById(getIdSelectedSpaceStation()) == null)
				setNoSelection();
			break;
		case Selection.TYPE_SYSTEM:
			if (areaData.getSystemById(getIdSelectedSystem()) == null)
				setNoSelection();
			break;
		case Selection.TYPE_STRUCTURE:
			if (areaData.getStructureById(getIdSelectedStructure()) == null)
				setNoSelection();
			break;
		}
	}
	
	public void updateSelection(PlayerFleetsData fleetsData) {
		switch (selection.getType()) {
		case Selection.TYPE_FLEET:
			long[] idSelectedFleets = getIdSelectedFleets();
			for (long idSelectedFleet : idSelectedFleets) {
				boolean found = false;
				for (int i = 0; i < fleetsData.getFleetsCount(); i++)
					if (fleetsData.getFleetAt(i).getId() == idSelectedFleet) {
						found = true;
						break;
					}
				if (!found)
					removeSelectedFleet((int) idSelectedFleet);
			}
			break;
		}
	}
	
	public void updateSelection(PlayerSystemsData systemsData) {
		switch (selection.getType()) {
		case Selection.TYPE_SYSTEM:
			int idSelectedSystem = getIdSelectedSystem();
			boolean found = false;
			for (int i = 0; i < systemsData.getSystemsCount(); i++)
				if (systemsData.getSystemAt(i).getId() == idSelectedSystem) {
					found = true;
					break;
				}
			if (!found)
				setNoSelection();
			break;
		}
	}
	
	public static SelectionManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void fireSelectionChanged(Selection newSelection, Selection oldSelection) {
		if (selectionListeners != null)
			selectionListeners.fireSelectionChanged(newSelection, oldSelection);
		
		// Update EmpireView : Cadre blanc de sélection
		Client.getInstance().getEmpireView().updateFleetSelection();
	}
}
