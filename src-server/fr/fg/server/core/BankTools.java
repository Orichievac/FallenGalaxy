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

package fr.fg.server.core;

import fr.fg.client.data.BankAccountData;
import fr.fg.server.data.Bank;
import fr.fg.server.util.JSONStringer;

public class BankTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getBankAccount(JSONStringer json, Bank bank, int idPlayer) {
		if (json == null)
			json = new JSONStringer();
		
		double[] resources = bank.getAccountResources(idPlayer);
		json.object().
			key(BankAccountData.FIELD_RESSOURCES).	array();
		
		for (int i = 0; i < resources.length; i++)
			json.value(resources[i]);
		
		json.endArray();
		
		double[] rates = bank.getRates();
		json.key(BankAccountData.FIELD_RATES).		array();
		
		for (int i = 0; i < resources.length; i++)
			json.value(rates[i]);
		
		json.endArray().
			key(BankAccountData.FIELD_FEES).		value(bank.getPlayerFees(idPlayer)).
			endObject();
		
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
