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

public class ContractParameterValueBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long idContractParameter;
	private String keyName;
	private String value;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getIdContractParameter() {
		return idContractParameter;
	}
	
	public void setIdContractParameter(long idContractParameter) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idContractParameter = idContractParameter;
	}
	
	public String getKeyName() {
		return keyName;
	}
	
	public void setKeyName(String keyName) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (keyName == null)
			throw new IllegalArgumentException("Invalid value: '" +
				keyName + "' (must not be null).");
		else
			this.keyName = keyName;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (value == null)
			throw new IllegalArgumentException("Invalid value: '" +
				value + "' (must not be null).");
		else
			this.value = value;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
