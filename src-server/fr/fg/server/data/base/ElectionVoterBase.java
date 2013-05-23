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

public class ElectionVoterBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idElection;
	private int idPlayer;
	private int idCandidate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdElection() {
		return idElection;
	}
	
	public void setIdElection(int idElection) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idElection < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idElection + "' (must be >= 0).");
		else
			this.idElection = idElection;
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
	
	public int getIdCandidate() {
		return idCandidate;
	}
	
	public void setIdCandidate(int idCandidate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idCandidate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idCandidate + "' (must be >= 0).");
		else
			this.idCandidate = idCandidate;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
