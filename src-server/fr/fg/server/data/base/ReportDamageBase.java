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

public class ReportDamageBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int targetPosition;
	private long damage;
	private int kills;
	private int hullDamage;
	private int modifiers;
	private double stealedResource0;
	private double stealedResource1;
	private double stealedResource2;
	private double stealedResource3;
	private long idReportAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getTargetPosition() {
		return targetPosition;
	}
	
	public void setTargetPosition(int targetPosition) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.targetPosition = targetPosition;
	}
	
	public long getDamage() {
		return damage;
	}
	
	public void setDamage(long damage) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.damage = damage;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void setKills(int kills) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.kills = kills;
	}
	
	public int getHullDamage() {
		return hullDamage;
	}
	
	public void setHullDamage(int hullDamage) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.hullDamage = hullDamage;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	public void setModifiers(int modifiers) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.modifiers = modifiers;
	}
	
	public double getStealedResource0() {
		return stealedResource0;
	}
	
	public void setStealedResource0(double stealedResource0) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.stealedResource0 = stealedResource0;
	}
	
	public double getStealedResource1() {
		return stealedResource1;
	}
	
	public void setStealedResource1(double stealedResource1) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.stealedResource1 = stealedResource1;
	}
	
	public double getStealedResource2() {
		return stealedResource2;
	}
	
	public void setStealedResource2(double stealedResource2) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.stealedResource2 = stealedResource2;
	}
	
	public double getStealedResource3() {
		return stealedResource3;
	}
	
	public void setStealedResource3(double stealedResource3) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.stealedResource3 = stealedResource3;
	}
	
	public long getIdReportAction() {
		return idReportAction;
	}
	
	public void setIdReportAction(long idReportAction) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idReportAction = idReportAction;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
