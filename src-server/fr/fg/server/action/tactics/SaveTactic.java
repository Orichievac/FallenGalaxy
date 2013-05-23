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

package fr.fg.server.action.tactics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.core.TacticTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Tactic;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class SaveTactic extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String name = (String) params.get("name");
		String hash = (String) params.get("hash");
	
		int idSlot0 = (Integer) params.get("slot_id_0");
		long slot0Count = (Long) params.get("tactic_slot0_count");
		int idSlot1 = (Integer) params.get("slot_id_1");
		long slot1Count = (Long) params.get("tactic_slot1_count");
		int idSlot2 = (Integer) params.get("slot_id_2");
		long slot2Count = (Long) params.get("tactic_slot2_count");
		int idSlot3 = (Integer) params.get("slot_id_3");
		long slot3Count = (Long) params.get("tactic_slot3_count");
		int idSlot4 = (Integer) params.get("slot_id_4");
		long slot4Count = (Long) params.get("tactic_slot4_count");
		
		// Teste si la tactique a déjà été enregistrée
		List<Tactic> tactics = new ArrayList<Tactic>(player.getTactics());
		
		for (Tactic tactic : tactics)
			if (tactic.getHash().equals(hash)) {
				synchronized (tactic) {
					tactic = DataAccess.getEditable(tactic);
					tactic.setName(name);
					tactic.setDate(Utilities.now());
					tactic.save();
				}
				
				return TacticTools.getPlayerTactics(null, player.getId()).toString();
			}
		
		// Vérifie que la limite de tactiques n'a pas été atteinte
		if (tactics.size() >= 100)
			throw new IllegalOperationException("Vous devez supprimer des " +
				"tactiques avant de pouvoir en enregistrer de nouvelles.");
		
		Tactic tactic = new Tactic(player.getId(), name, hash, idSlot0, slot0Count, idSlot1, slot1Count, idSlot2, slot2Count,
		idSlot3, slot3Count, idSlot4, slot4Count);
		tactic.save();
		
		return TacticTools.getPlayerTactics(null, player.getId()).toString();
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
