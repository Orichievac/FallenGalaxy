/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.server.task.hourly;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Ward;
import fr.fg.server.util.Utilities;

public class CleanUpWards extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("CleanUpWards (hourly)");
		List<Ward> wards = new ArrayList<Ward>(DataAccess.getAllWards());
		long now = Utilities.now();
		
		for (Ward ward : wards)
			if (ward.getType().startsWith(Ward.TYPE_OBSERVER) ||
					ward.getType().startsWith(Ward.TYPE_SENTRY)) {
				if (now - ward.getDate() > 86400 * ward.getPower())
					ward.delete();
			} else {
				if (now - ward.getDate() > GameConstants.CHARGES_LENGTH)
					ward.delete();
			}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
