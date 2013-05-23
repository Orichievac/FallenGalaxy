/*
Copyright 2010 Nicolas Bosc

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

public class PnjDefenderBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int idPlayer;
	private int idPnj;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
			this.id = id;
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
	
	public int getIdPnj() {
		return idPnj;
	}
	
	public void setIdPnj(int idPnj) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idPnj < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idPnj + "' (must be >= 0).");
		else
			this.idPnj = idPnj;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
