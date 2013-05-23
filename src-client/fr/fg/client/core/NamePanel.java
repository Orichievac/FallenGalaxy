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

package fr.fg.client.core;

import java.util.HashMap;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;

import fr.fg.client.ajax.Action;
import fr.fg.client.core.selection.Selection;
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AllyData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StructureData;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class NamePanel extends SimplePanel implements SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		MODE_NONE = 0,
		MODE_AREA = 1,
		MODE_SYSTEM = 2,
		MODE_FLEET = 3,
		MODE_SPACE_STATION = 4,
		MODE_MULTIPLE_FLEETS = 5,
		MODE_STRUCTURE = 6;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int mode;
	
	private int currentId;
	
	private String currentName;
	
	private IndexedAreaData currentAreaData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public NamePanel() {
		getElement().setId("namePanel");
		
		mode = MODE_NONE;
		
		sinkEvents(Event.ONCLICK);
		
		SelectionManager.getInstance().addSelectionListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setCurrentArea(IndexedAreaData currentAreaData) {
		this.currentAreaData = currentAreaData;
		
		if (mode == MODE_AREA || mode == MODE_NONE)
			setAreaName(currentAreaData);
	}
	
	public void selectionChanged(Selection newSelection, Selection oldSelection) {
		update();
	}
	
	public void update() {
		switch (SelectionManager.getInstance().getSelection().getType()) {
		case Selection.TYPE_SYSTEM:
			// Sélection d'un système
			setSystemName(SelectionTools.getSelectedSystem());
			break;
		case Selection.TYPE_SPACE_STATION:
			// Selection d'une station spatiale
			setSpaceStationName(SelectionTools.getSelectedSpaceStation());
			break;
		case Selection.TYPE_FLEET:
			// Sélection d'une ou plusieurs flottes
			setFleetsName(SelectionTools.getSelectedFleets());
			break;
		case Selection.TYPE_STRUCTURE:
			// Sélection d'une structure
			setStructureName(SelectionTools.getSelectedStructure());
			break;
		case Selection.TYPE_NONE:
			// Pas de sélection
			if (currentAreaData != null)
				setAreaName(currentAreaData);
			break;
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			if (mode == MODE_AREA || mode == MODE_MULTIPLE_FLEETS)
				return;
			
			JSOptionPane.showInputDialog("Entrez un nouveau nom :", "Renommer",
				JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if (option == null || currentName.equals(option))
							return;
						
						HashMap<String, String> params =
							new HashMap<String, String>();
						params.put("id", String.valueOf(currentId));
						params.put("name", (String) option);
						
						switch (mode) {
						case MODE_SYSTEM:
							new Action("systems/setname", params,
									UpdateManager.UPDATE_CALLBACK);
							break;
						case MODE_FLEET:
							new Action("setfleetname", params,
									UpdateManager.UPDATE_CALLBACK);
							break;
						case MODE_SPACE_STATION:
							new Action("setspacestationname", params,
									UpdateManager.UPDATE_CALLBACK);
							break;
						case MODE_STRUCTURE:
							new Action("structure/setname", params,
									UpdateManager.UPDATE_CALLBACK);
							break;
						}
					}
			}, currentName);
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void setAreaName(IndexedAreaData areaData) {
		AllyData ally = Client.getInstance().getAllyDialog().getAlly();
				
		long areaX = areaData.getX();
		long areaY = areaData.getY();

		
		getElement().setInnerHTML((areaData.hasDomination() && ally.getId() != 0 &&
			areaData.getDomination().equals(ally.getName()) ?
			"<img class=\"domination\" src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\"/>" : "") + areaData.getName() + " (" + areaX + " , " + areaY + ")");
		

				
		ToolTipManager.getInstance().register(getElement(),
			"<div class=\"title\">" + areaData.getName() + " (" + areaX + " , " + areaY + ")</div>" +
			"<div class=\"justify\">Pour gagner la domination du secteur, " +
			"votre alliance doit posséder un générateur sur au moins 51% " +
			"des puits gravitationnels dans le secteur. L'alliance qui " +
			"domine le secteur n'est plus affectée par le brouillard de guerre.</div>", 200);
		
		mode = MODE_AREA;
		currentId = areaData.getId();
		currentName = areaData.getName();
	}
	
	
	
	private void setFleetName(PlayerFleetData fleetData) {
		getElement().setInnerHTML(fleetData.getName());
		
		ToolTipManager.getInstance().register(getElement(),
			"<div class=\"title\">" + fleetData.getName() + "</div>" +
			"<div class=\"justify\">Cliquez pour renommer la flotte.</div>", 200);
		
		mode = MODE_FLEET;
		currentId = fleetData.getId();
		currentName = fleetData.getName();
	}
	
	private void setFleetsName(PlayerFleetData[] fleetsData) {
		if (fleetsData.length == 1) {
			setFleetName(fleetsData[0]);
			return;
		}
		
		String name = "Groupe flottes (" + fleetsData.length + ")";
		getElement().setInnerHTML(name);
		
		ToolTipManager.getInstance().unregister(getElement());
		
		mode = MODE_MULTIPLE_FLEETS;
		currentId = 0;
		currentName = name;
	}
	
	private void setSystemName(PlayerStarSystemData systemData) {
		getElement().setInnerHTML(systemData.getName());
		
		ToolTipManager.getInstance().register(getElement(),
			"<div class=\"title\">" + systemData.getName() + "</div>" +
			"<div class=\"justify\">Cliquez pour renommer le système.</div>", 200);
		
		mode = MODE_SYSTEM;
		currentId = systemData.getId();
		currentName = systemData.getName();
	}
	
	private void setStructureName(StructureData structureData) {
		getElement().setInnerHTML(structureData.getName());
		
		ToolTipManager.getInstance().register(getElement(),
			"<div class=\"title\">" + structureData.getName() + "</div>" +
			"<div class=\"justify\">Cliquez pour renommer la structure.</div>", 200);
		
		mode = MODE_STRUCTURE;
		currentId = structureData.getId();
		currentName = structureData.getName();
	}
	
	private void setSpaceStationName(SpaceStationData spaceStationData) {
		getElement().setInnerHTML(spaceStationData.getName());
		
		ToolTipManager.getInstance().register(getElement(),
			"<div class=\"title\">" + spaceStationData.getName() + "</div>" +
			"<div class=\"justify\">Cliquez pour renommer la station spatiale.</div>", 200);
		
		mode = MODE_SPACE_STATION;
		currentId = spaceStationData.getId();
		currentName = spaceStationData.getName();
	}
}
