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

public class ReportBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int idPlayerAttacking;
	private int idPlayerDefending;
	private long date;
	private String hash;
	private int hits;
	private long lastView;
	private String attackerEnvironment;
	private String defenderEnvironment;
	private double attackerDamageFactor;
	private double defenderDamageFactor;
	private boolean retreat;
	private int idArea;
	private int attackerXpGain;
	private int defenderXpGain;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public int getIdPlayerAttacking() {
		return idPlayerAttacking;
	}
	
	public void setIdPlayerAttacking(int idPlayerAttacking) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayerAttacking = idPlayerAttacking;
	}
	
	public int getIdPlayerDefending() {
		return idPlayerDefending;
	}
	
	public void setIdPlayerDefending(int idPlayerDefending) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayerDefending = idPlayerDefending;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (hash == null)
			throw new IllegalArgumentException("Invalid value: '" +
				hash + "' (must not be null).");
		else
			this.hash = hash;
	}
	
	public int getHits() {
		return hits;
	}
	
	public void setHits(int hits) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.hits = hits;
	}
	
	public long getLastView() {
		return lastView;
	}
	
	public void setLastView(long lastView) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.lastView = lastView;
	}
	
	public String getAttackerEnvironment() {
		return attackerEnvironment;
	}
	
	public void setAttackerEnvironment(String attackerEnvironment) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (attackerEnvironment == null)
			throw new IllegalArgumentException("Invalid value: '" +
				attackerEnvironment + "' (must not be null).");
		else
			this.attackerEnvironment = attackerEnvironment;
	}
	
	public String getDefenderEnvironment() {
		return defenderEnvironment;
	}
	
	public void setDefenderEnvironment(String defenderEnvironment) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (defenderEnvironment == null)
			throw new IllegalArgumentException("Invalid value: '" +
				defenderEnvironment + "' (must not be null).");
		else
			this.defenderEnvironment = defenderEnvironment;
	}
	
	public double getAttackerDamageFactor() {
		return attackerDamageFactor;
	}
	
	public void setAttackerDamageFactor(double attackerDamageFactor) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.attackerDamageFactor = attackerDamageFactor;
	}
	
	public double getDefenderDamageFactor() {
		return defenderDamageFactor;
	}
	
	public void setDefenderDamageFactor(double defenderDamageFactor) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.defenderDamageFactor = defenderDamageFactor;
	}
	
	public boolean isRetreat() {
		return retreat;
	}
	
	public void setRetreat(boolean retreat) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.retreat = retreat;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idArea = idArea;
	}
	
	public int getAttackerXpGain() {
		return attackerXpGain;
	}
	
	public void setAttackerXpGain(int attackerXpGain) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.attackerXpGain = attackerXpGain;
	}
	
	public int getDefenderXpGain() {
		return defenderXpGain;
	}
	
	public void setDefenderXpGain(int defenderXpGain) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.defenderXpGain = defenderXpGain;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
