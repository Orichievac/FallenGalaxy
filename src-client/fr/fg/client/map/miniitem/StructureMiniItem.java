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

package fr.fg.client.map.miniitem;

import fr.fg.client.data.StructureData;
import fr.fg.client.map.UIMiniItemRenderingHints;

public class StructureMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private StructureData structureData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StructureMiniItem(StructureData structureData,
			UIMiniItemRenderingHints hints) {
		super(structureData.getX(), structureData.getY(), hints);
		
		this.structureData = structureData;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		updateData(structureData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onDataUpdate(Object newData) {
		StructureData newStructureData = (StructureData) newData;
		
		if (structureData.getX() != newStructureData.getX() ||
			structureData.getY() != newStructureData.getY())
			setLocation(newStructureData.getX(), newStructureData.getY());
		
		if (!structureData.getTreaty().equals(newStructureData.getTreaty()))
			updateData(newStructureData);
		
		structureData = newStructureData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		structureData = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	public void updateData(StructureData structureData) {
		getElement().setClassName("structure " +
				"structure-" + structureData.getPact()); //$NON-NLS-1$
	}
}
