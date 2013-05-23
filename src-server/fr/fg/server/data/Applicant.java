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

import fr.fg.server.data.base.ApplicantBase;
import fr.fg.server.util.Utilities;

public class Applicant extends ApplicantBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Applicant() {
		// Nécessaire pour la construction par réflection
	}

	public Applicant(int idAlly, int idPlayer) {
		setIdAlly(idAlly);
		setIdPlayer(idPlayer);
		setDate(Utilities.now());
	}

	// --------------------------------------------------------- METHODES -- //

	public Ally getAlly() {
    	return DataAccess.getAllyById(getIdAlly());
	}
	
    public Player getPlayer() {
    	return DataAccess.getPlayerById(getIdPlayer());
    }

	// ------------------------------------------------- METHODES PRIVEES -- //
}
