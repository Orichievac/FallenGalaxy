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

import fr.fg.server.contract.ContractManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;


public class UpdateContracts extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("UpdateContracts (hourly)");
		List<Player> players = new ArrayList<Player>(DataAccess.getAllPlayers());
		
		ContractManager.getInstance().updateContractsPvP();
		
		for (Player player : players)
			ContractManager.getInstance().updatePlayerContracts(player);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
