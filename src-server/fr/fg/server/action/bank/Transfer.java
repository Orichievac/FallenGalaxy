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
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Bank;
import fr.fg.server.data.BankAccount;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.StellarObject;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Transfer extends Action {
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
		
		// Vérifie que le nombre de ressources transférées est valide
		long[] fleetResources = new long[GameConstants.RESOURCES_COUNT];
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			fleetResources[i] = (Long) params.get("resource" + i);
		
		Bank bank = DataAccess.getBankById(idBank);
		bank.updateAllAccounts();
		double[] accountResources = bank.getAccountResources(player.getId());
		
		ItemContainer itemContainer = fleet.getItemContainer();
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
			if (fleetResources[i] > itemContainer.getResource(i) + accountResources[i])
				throw new IllegalOperationException(
					"Nombre de ressources à transférer invalide.");
			
			accountResources[i] += itemContainer.getResource(i) - fleetResources[i];
		}
		
		// Honoraires
		player = Player.updateCredits(player);
		
		if (player.getCredits() < bank.getPlayerFees(player.getId()))
			throw new IllegalOperationException("Vous n'avez pas suffisament de crédits.");
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-bank.getPlayerFees(player.getId()));
			player.save();
		}
		
		synchronized (itemContainer.getLock()) {
			itemContainer = DataAccess.getEditable(itemContainer);
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				itemContainer.setResource(fleetResources[i], i);
			itemContainer.save();
		}
		
		// Met à jour le compte bancaire
		BankAccount account = bank.getAccount(player.getId());
		
		if (account == null) {
			account = new BankAccount(bank.getIdBank(), player.getId());
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				account.setResource(accountResources[i], i);
			account.save();
		} else {
			synchronized (account.getLock()) {
				account = DataAccess.getEditable(account);
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
					account.setResource(accountResources[i], i);
				account.save();
			}
		}
		
		UpdateTools.queuePlayerSystemsUpdate(player.getId(), false);
		UpdateTools.queuePlayerFleetUpdate(player.getId(), fleet.getId(), false);
		UpdateTools.queueAreaUpdate(player);
		
		return BankTools.getBankAccount(null, bank, player.getId()).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
