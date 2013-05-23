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

package fr.fg.server.dao;

import fr.fg.server.dao.db.ForeignKey;

public class RestrictedDataException extends RuntimeException {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = 8283231804368030072L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public RestrictedDataException(ForeignKey foreignKey, Object key) {
		super("'" + foreignKey.getReferencedTable() + "." +
				foreignKey.getReferencedColumn() + "' value '" + key +
				"' is referenced as a restricted foreign key by '" +
				foreignKey.getTable().getName() + "." + foreignKey.getName() +
				"'");
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
