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

import fr.fg.server.data.base.ResearchBase;

public class Research extends ResearchBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Research() {
		// Nécessaire pour la construction par réflection
	}
	
	public Research(int technology, float progress, int queuePosition,
			int idPlayer) {
		setIdTechnology(technology);
		setProgress(progress);
		setQueuePosition(queuePosition);
		setIdPlayer(idPlayer);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public long getLength() {
		return Technology.getTechnologyById(getIdTechnology()).getLength();
	}
	
	public int[] getRequirements() {
		return Technology.getTechnologyById(
				getIdTechnology()).getRequirements();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
