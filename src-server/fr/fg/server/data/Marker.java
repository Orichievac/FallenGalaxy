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

import fr.fg.server.data.base.MarkerBase;

public class Marker extends MarkerBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Marker() {
		// Nécessaire pour la construction par réflection
	}

	public Marker(int x, int y, String message, String visibility,
			boolean galaxy, long date, int idArea, int idOwner, long idContract) {
		setX(x);
		setY(y);
		setMessage(message);
		setVisibility(visibility);
		setGalaxy(galaxy);
		setDate(date);
		setIdArea(idArea);
		setIdOwner(idOwner);
		setIdContract(idContract);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Player getOwner() {
		return DataAccess.getPlayerById(getIdOwner());
	}
	
	public Contract getContract() {
		return DataAccess.getContractById(getIdContract());
	}
	
	public boolean isVisibleFromPlayer(Player player) {
		String visibility = getVisibility();
		
		String treaty = player.getTreatyWithPlayer(getIdOwner());
		boolean visible = false;
		
		if (visibility.equals(Marker.VISIBILITY_PLAYER))
			visible = player.getId() == getIdOwner();
		else if (visibility.equals(Marker.VISIBILITY_ALLY))
			visible = player.getId() == getIdOwner() ||
				treaty.equals(Treaty.ALLY);
		else if (visibility.equals(Marker.VISIBILITY_ALLIED))
			visible = player.getId() == getIdOwner() ||
				treaty.equals(Treaty.ALLY) || treaty.equals(Treaty.ALLIED)
				|| Treaty.isPact(treaty);
		
		return visible;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
