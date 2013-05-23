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

public class ReportSlotStateBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int position;
	private long idReportAction;
	private double damageModifier;
	private int protectionModifier;
	private double hullModifier;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.position = position;
	}
	
	public long getIdReportAction() {
		return idReportAction;
	}
	
	public void setIdReportAction(long idReportAction) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idReportAction = idReportAction;
	}
	
	public double getDamageModifier() {
		return damageModifier;
	}
	
	public void setDamageModifier(double damageModifier) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.damageModifier = damageModifier;
	}
	
	public int getProtectionModifier() {
		return protectionModifier;
	}
	
	public void setProtectionModifier(int protectionModifier) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.protectionModifier = protectionModifier;
	}
	
	public double getHullModifier() {
		return hullModifier;
	}
	
	public void setHullModifier(double hullModifier) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.hullModifier = hullModifier;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
