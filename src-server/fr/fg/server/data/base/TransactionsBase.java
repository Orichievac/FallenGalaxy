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

public class TransactionsBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int idplayer;
	private String code;
	private String idtransaction;
	
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
	
	public int getIdplayer() {
		return idplayer;
	}
	
	public void setIdplayer(int idplayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idplayer < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idplayer + "' (must be >= 0).");
		else
			this.idplayer = idplayer;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (code == null)
			throw new IllegalArgumentException("Invalid value: '" +
				code + "' (must not be null).");
		else
			this.code = code;
	}
	
	public String getIdtransaction() {
		return idtransaction;
	}
	
	public void setIdtransaction(String idtransaction) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idtransaction == null)
			throw new IllegalArgumentException("Invalid value: '" +
				idtransaction + "' (must not be null).");
		else
			this.idtransaction = idtransaction;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
