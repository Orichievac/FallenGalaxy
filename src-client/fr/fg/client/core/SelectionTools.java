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

import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StructureData;

public class SelectionTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static PlayerStarSystemData getSelectedSystem() {
		if (SelectionManager.getInstance().isSystemSelected())
			return Client.getInstance().getEmpireView().getSystemById(
				SelectionManager.getInstance().getIdSelectedSystem());
		else
			return null;
	}
	
	public static SpaceStationData getSelectedSpaceStation() {
		if (SelectionManager.getInstance().isSpaceStationSelected()) {
			IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
			return areaData.getSpaceStationById(
				SelectionManager.getInstance().getIdSelectedSpaceStation());
		} else {
			return null;
		}
	}
	
	public static StructureData getSelectedStructure() {
		if (SelectionManager.getInstance().isStructureSelected()) {
			IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
			return areaData.getStructureById(
				SelectionManager.getInstance().getIdSelectedStructure());
		} else {
			return null;
		}
	}
	
	public static PlayerFleetData[] getSelectedFleets() {
		if (SelectionManager.getInstance().isFleetSelected()) {
			long[] idSelectedFleets = SelectionManager.getInstance().getIdSelectedFleets();
			PlayerFleetData[] fleets = new PlayerFleetData[idSelectedFleets.length];
			for (int i = 0; i < fleets.length; i++)
				fleets[i] = Client.getInstance().getEmpireView().getFleetById((int) idSelectedFleets[i]);
			return fleets;
		} else {
			return null;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
