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

public class AllyRelationshipBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idPlayerally;
	private int idAlly;
	private double value;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdPlayerally() {
		return idPlayerally;
	}
	
	public void setIdPlayerally(int idPlayerally) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayerally = idPlayerally;
	}
	
	public int getIdAlly() {
		return idAlly;
	}
	
	public void setIdAlly(int idAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idAlly = idAlly;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.value = value;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
