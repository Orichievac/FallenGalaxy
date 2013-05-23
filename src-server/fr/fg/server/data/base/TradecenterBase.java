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

public class TradecenterBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idTradecenter;
	private double rate0;
	private double rate1;
	private double rate2;
	private double rate3;
	private double variation;
	private double fees;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdTradecenter() {
		return idTradecenter;
	}
	
	public void setIdTradecenter(int idTradecenter) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idTradecenter = idTradecenter;
	}
	
	public double getRate0() {
		return rate0;
	}
	
	public void setRate0(double rate0) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.rate0 = rate0;
	}
	
	public double getRate1() {
		return rate1;
	}
	
	public void setRate1(double rate1) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.rate1 = rate1;
	}
	
	public double getRate2() {
		return rate2;
	}
	
	public void setRate2(double rate2) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.rate2 = rate2;
	}
	
	public double getRate3() {
		return rate3;
	}
	
	public void setRate3(double rate3) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.rate3 = rate3;
	}
	
	public double getVariation() {
		return variation;
	}
	
	public void setVariation(double variation) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.variation = variation;
	}
	
	public double getFees() {
		return fees;
	}
	
	public void setFees(double fees) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.fees = fees;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
