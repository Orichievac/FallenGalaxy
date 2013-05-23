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

public class ReportActionBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int actionIndex;
	private int slotIndex;
	private int frontSlots;
	private int modifiers;
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
	
	public int getActionIndex() {
		return actionIndex;
	}
	
	public void setActionIndex(int actionIndex) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.actionIndex = actionIndex;
	}
	
	public int getSlotIndex() {
		return slotIndex;
	}
	
	public void setSlotIndex(int slotIndex) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slotIndex = slotIndex;
	}
	
	public int getFrontSlots() {
		return frontSlots;
	}
	
	public void setFrontSlots(int frontSlots) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.frontSlots = frontSlots;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	public void setModifiers(int modifiers) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.modifiers = modifiers;
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
