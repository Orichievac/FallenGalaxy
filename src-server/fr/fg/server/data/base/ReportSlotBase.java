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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class ReportSlotBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int position;
	private int slotId;
	private long slotCount;
	private boolean slotFront;
	private int availableAbilities;
	private int idReport;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.position = position;
	}
	
	public int getSlotId() {
		return slotId;
	}
	
	public void setSlotId(int slotId) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slotId = slotId;
	}
	
	public long getSlotCount() {
		return slotCount;
	}
	
	public void setSlotCount(long slotCount) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slotCount = slotCount;
	}
	
	public boolean isSlotFront() {
		return slotFront;
	}
	
	public void setSlotFront(boolean slotFront) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slotFront = slotFront;
	}
	
	public int getAvailableAbilities() {
		return availableAbilities;
	}
	
	public void setAvailableAbilities(int availableAbilities) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.availableAbilities = availableAbilities;
	}
	
	public int getIdReport() {
		return idReport;
	}
	
	public void setIdReport(int idReport) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idReport = idReport;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
