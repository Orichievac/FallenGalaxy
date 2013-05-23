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

public class ContractDialogBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idFleet;
	private int idPlayer;
	private String currentEntry;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdFleet() {
		return idFleet;
	}
	
	public void setIdFleet(int idFleet) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idFleet = idFleet;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	public String getCurrentEntry() {
		return currentEntry;
	}
	
	public void setCurrentEntry(String currentEntry) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (currentEntry == null)
			throw new IllegalArgumentException("Invalid value: '" +
				currentEntry + "' (must not be null).");
		else
			this.currentEntry = currentEntry;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
