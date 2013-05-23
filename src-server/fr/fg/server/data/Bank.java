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

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.base.BankBase;
import fr.fg.server.util.Utilities;

public class Bank extends BankBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Bank() {
		// Nécessaire pour la construction par réflection
	}
	
	public Bank(int idBank, double baseRate, double variableRate, long fees) {
		setIdBank(idBank);
		setBaseRate(baseRate);
		setVariableRate(variableRate);
		setFees(fees);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public long getPlayerFees(int idPlayer) {
		return (((long) Math.ceil(getFees() * Math.pow(.93,
			Advancement.getAdvancementLevel(
				idPlayer, Advancement.TYPE_BANK_TAX)))) / 1000) * 1000;
	}
	
	public void updateAccount(int idPlayer) {
		List<BankAccount> accounts = new ArrayList<BankAccount>(
				DataAccess.getBankAccountsByBank(getIdBank()));
		
		for (BankAccount account : accounts) {
			if (account.getIdPlayer() == idPlayer) {
				updateAccount(account, getRates(), Utilities.now());
				break;
			}
		}
	}
	
	public void updateAllAccounts() {
		List<BankAccount> accounts = new ArrayList<BankAccount>(
				DataAccess.getBankAccountsByBank(getIdBank()));
		double[] rates = getRates();
		long now = Utilities.now();
		
		for (BankAccount account : accounts) {
			updateAccount(account, rates, now);
		}
	}
	
	public double[] getRates() {
		double[] resources = getDepositedResources();
		double[] rates = new double[GameConstants.RESOURCES_COUNT];
		long resourcesSum = 0;
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			resourcesSum += resources[i] + 100000; // Pour limiter les effets de bord avec peu de ressources
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
			double coef = resourcesSum == 0 ? 1 : (resources[i] + 100000) / resourcesSum;
			rates[i] = getBaseRate() + getVariableRate() * Math.max(0, 1 - coef * 2);
		}
		
		return rates;
	}
	
	public BankAccount getAccount(int idPlayer) {
		List<BankAccount> accounts = DataAccess.getBankAccountsByBank(getIdBank());
		
		synchronized (accounts) {
			for (BankAccount account : accounts)
				if (account.getIdPlayer() == idPlayer)
					return account;
		}
		
		return null;
	}
	
	public double[] getAccountResources(int idPlayer) {
		List<BankAccount> accounts = new ArrayList<BankAccount>(
				DataAccess.getBankAccountsByBank(getIdBank()));
		double[] resources = new double[GameConstants.RESOURCES_COUNT];
		
		for (BankAccount account : accounts) {
			if (account.getIdPlayer() == idPlayer) {
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
					resources[i] = account.getResource(i);
				break;
			}
		}
		
		return resources;
	}
	
	public double[] getDepositedResources() {
		double[] resources = new double[GameConstants.RESOURCES_COUNT];
		List<BankAccount> accounts = DataAccess.getBankAccountsByBank(getIdBank());
		
		synchronized (accounts) {
			for (BankAccount account : accounts) {
				for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
					resources[i] = account.getResource(i);
			}
		}
		
		return resources;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateAccount(BankAccount account, double[] rates, long now) {
		synchronized (account.getLock()) {
			account = DataAccess.getEditable(account);
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				account.addResource(account.getResource(i) * rates[i] *
						(now - account.getLastUpdate()), i);
			account.setLastUpdate(now);
			account.save();
		}
	}
}
