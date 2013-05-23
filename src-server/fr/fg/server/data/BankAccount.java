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

package fr.fg.server.data;

import fr.fg.server.data.base.BankAccountBase;
import fr.fg.server.util.Utilities;

public class BankAccount extends BankAccountBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BankAccount() {
		// Nécessaire pour la construction par réflection
	}

	public BankAccount(int idBank, int idPlayer) {
		setIdBank(idBank);
		setIdPlayer(idPlayer);
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
		setLastUpdate(Utilities.now());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public double getResource(int type) {
		switch (type) {
		case 0:
			return getResource0();
		case 1:
			return getResource1();
		case 2:
			return getResource2();
		case 3:
			return getResource3();
		}
		throw new IllegalArgumentException(
				"Invalid resource index: '" + type + "'.");
	}
	
	public void addResource(double resources, int type) {
		setResource(getResource(type) + resources, type);
	}
	
	public void setResource(double resources, int type) {
		switch (type) {
		case 0:
			setResource0(resources);
			break;
		case 1:
			setResource1(resources);
			break;
		case 2:
			setResource2(resources);
			break;
		case 3:
			setResource3(resources);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid resource index: '" + type + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
