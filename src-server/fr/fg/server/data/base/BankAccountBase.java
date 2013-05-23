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

public class BankAccountBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idBank;
	private int idPlayer;
	private double resource0;
	private double resource1;
	private double resource2;
	private double resource3;
	private long lastUpdate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdBank() {
		return idBank;
	}
	
	public void setIdBank(int idBank) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idBank < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idBank + "' (must be >= 0).");
		else
			this.idBank = idBank;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idPlayer < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idPlayer + "' (must be >= 0).");
		else
			this.idPlayer = idPlayer;
	}
	
	public double getResource0() {
		return resource0;
	}
	
	public void setResource0(double resource0) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource0 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource0 + "' (must be >= 0).");
		else
			this.resource0 = resource0;
	}
	
	public double getResource1() {
		return resource1;
	}
	
	public void setResource1(double resource1) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource1 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource1 + "' (must be >= 0).");
		else
			this.resource1 = resource1;
	}
	
	public double getResource2() {
		return resource2;
	}
	
	public void setResource2(double resource2) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource2 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource2 + "' (must be >= 0).");
		else
			this.resource2 = resource2;
	}
	
	public double getResource3() {
		return resource3;
	}
	
	public void setResource3(double resource3) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource3 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource3 + "' (must be >= 0).");
		else
			this.resource3 = resource3;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastUpdate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastUpdate + "' (must be >= 0).");
		else
			this.lastUpdate = lastUpdate;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
