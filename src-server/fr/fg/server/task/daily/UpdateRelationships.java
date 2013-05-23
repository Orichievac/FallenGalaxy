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

package fr.fg.server.task.daily;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Relationship;

public class UpdateRelationships extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static double
		DAILY_POSITIVE_MODIFIER = .5,
		DAILY_NEGATIVE_MODIFIER = .33;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateRelationships (daily)");
		List<Relationship> relationships =
			new ArrayList<Relationship>(DataAccess.getAllRelationships());
		
		// Les relations tendent à redevenir neutres
		for (Relationship relationship : relationships) {
			if (relationship.getValue() > 0) {
				if (relationship.getValue() <= DAILY_POSITIVE_MODIFIER) {
					relationship.delete();
				} else {
					synchronized (relationship.getLock()) {
						relationship = DataAccess.getEditable(relationship);
						relationship.addValue(-DAILY_POSITIVE_MODIFIER);
						relationship.save();
					}
				}
			} else {
				if (relationship.getValue() >= -DAILY_NEGATIVE_MODIFIER) {
					relationship.delete();
				} else {
					synchronized (relationship.getLock()) {
						relationship = DataAccess.getEditable(relationship);
						relationship.addValue(DAILY_NEGATIVE_MODIFIER);
						relationship.save();
					}
				}
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
