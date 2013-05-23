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

package fr.fg.server.action.bank;

import java.util.List;
import java.util.Map;


import fr.fg.server.core.BankTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.data.Bank;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.StellarObject;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class GetAccount extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		Fleet fleet = FleetTools.getFleetByIdWithChecks(idFleet, player.getId());
		
		// Recherche la banque dans le secteur de la flotte
		List<StellarObject> objects = fleet.getArea().getObjects();
		int idBank = -1;
		
		synchronized (objects) {
			for (StellarObject object : objects)
				if (object.getType().equals(StellarObject.TYPE_BANK) &&
						object.getBounds().contains(
							fleet.getCurrentX(), fleet.getCurrentY())) {
					idBank = object.getId();
					break;
				}
		}
		
		if (idBank == -1)
			throw new IllegalOperationException("La flotte n'est pas " +
				"placée sur une banque.");
		
		Bank bank = DataAccess.getBankById(idBank);
		bank.updateAllAccounts();
		
		return BankTools.getBankAccount(null, bank, player.getId()).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
