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

public class BankBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idBank;
	private double baseRate;
	private double variableRate;
	private long fees;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdBank() {
		return idBank;
	}
	
	public void setIdBank(int idBank) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idBank = idBank;
	}
	
	public double getBaseRate() {
		return baseRate;
	}
	
	public void setBaseRate(double baseRate) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.baseRate = baseRate;
	}
	
	public double getVariableRate() {
		return variableRate;
	}
	
	public void setVariableRate(double variableRate) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.variableRate = variableRate;
	}
	
	public long getFees() {
		return fees;
	}
	
	public void setFees(long fees) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.fees = fees;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
