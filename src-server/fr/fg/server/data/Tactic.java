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

import fr.fg.server.data.base.TacticBase;
import fr.fg.server.util.Utilities;

public class Tactic extends TacticBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Tactic() {
		// Nécessaire pour la construction par réflection
	}
	

	public Tactic(int idPlayer, String name, String hash, int slotId0, long slot0Count, int slotId1, long slot1Count, int slotId2, long slot2Count
			, int slotId3, long slot3Count, int slotId4, long slot4Count) {
		setIdPlayer(idPlayer);
		setName(name);
		setHash(hash);
		setSlotId0(slotId0);
		setTacticSlot0Count(slot0Count);
		setSlotId1(slotId1);
		setTacticSlot1Count(slot1Count);
		setSlotId2(slotId2);
		setTacticSlot2Count(slot2Count);
		setSlotId3(slotId3);
		setTacticSlot3Count(slot3Count);
		setSlotId4(slotId4);
		setTacticSlot4Count(slot4Count);
		setDate(Utilities.now());
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
