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

public class AllyInfluenceBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idAlly;
	private int idSector;
	private double influenceCoef;
	private int influenceValue;
	private int systemsCount;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdAlly() {
		return idAlly;
	}
	
	public void setIdAlly(int idAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idAlly = idAlly;
	}
	
	public int getIdSector() {
		return idSector;
	}
	
	public void setIdSector(int idSector) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idSector = idSector;
	}
	
	public double getInfluenceCoef() {
		return influenceCoef;
	}
	
	public void setInfluenceCoef(double influenceCoef) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.influenceCoef = influenceCoef;
	}
	
	public int getInfluenceValue() {
		return influenceValue;
	}
	
	public void setInfluenceValue(int influenceValue) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.influenceValue = influenceValue;
	}
	
	public int getSystemsCount() {
		return systemsCount;
	}
	
	public void setSystemsCount(int systemsCount) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.systemsCount = systemsCount;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
