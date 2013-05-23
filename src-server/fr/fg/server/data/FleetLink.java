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

import fr.fg.server.data.base.FleetLinkBase;
import fr.fg.server.util.Utilities;

public class FleetLink extends FleetLinkBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetLink() {
		// Nécessaire pour la construction par réflection
	}
	
	public FleetLink(int idSrcFleet, int idDstFleet, String link) {
		setIdSrcFleet(idSrcFleet);
		setIdDstFleet(idDstFleet);
		setLink(link);
		setDate(Utilities.now());
	}
	
	// --------------------------------------------------------- METHODES -- //	
	
	public Fleet getSrcFleet() {
		return DataAccess.getFleetById(getIdSrcFleet());
	}

	public Fleet getDstFleet() {
		return DataAccess.getFleetById(getIdDstFleet());
	}
	
	public Fleet getOtherFleet(int idFleet){
		return DataAccess.getFleetById(getOtherFleetId(idFleet));
	}
	
	public int getOtherFleetId(int idFleet){
		if (getIdSrcFleet() == idFleet)
			return getIdDstFleet();
		else if (getIdDstFleet() == idFleet)
			return getIdSrcFleet();
		return 0;
	}
	
	public boolean isOffensive() {
		return getLink().equals(LINK_OFFENSIVE);
	}
	
	public boolean isDefensive() {
		return getLink().equals(LINK_DEFENSIVE);
	}
	
	public boolean isDelude() {
		return getLink().equals(LINK_DELUDE);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
