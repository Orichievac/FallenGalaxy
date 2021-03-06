/*
Copyright 2012 jgottero

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

public class PremiumTransactionBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int idPlayer;
	private String code;
	private long date;
	private int remainingHours;
	private int boughtHours;
	
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

	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		this.idPlayer = idPlayer;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		if (!isEditable())
			throwDataUneditableException();
		this.code = code;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		this.date = date;
	}
	
	public int getRemainingHours() {
		return remainingHours;
	}
	
	public void setRemainingHours(int remainingHours) {
		if (!isEditable())
			throwDataUneditableException();
		this.remainingHours = remainingHours;
	}
	
	public int getBoughtHours() {
		return boughtHours;
	}
	
	public void setBoughtHours(int boughtHours) {
		if (!isEditable())
			throwDataUneditableException();
		this.boughtHours = boughtHours;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
