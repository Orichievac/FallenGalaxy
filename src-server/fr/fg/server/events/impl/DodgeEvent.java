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

package fr.fg.server.events.impl;

import fr.fg.server.data.Fleet;
import fr.fg.server.events.GameEvent;

public class DodgeEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //

	private Fleet attacker;
	
	private Fleet defender;
	
	private int attackerSlot;
	
	private int defenderSlot;

	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public DodgeEvent(Fleet attacker, Fleet defender, int attackerSlot,
			int defenderSlot) {
		super();
		this.attacker = attacker;
		this.defender = defender;
		this.attackerSlot = attackerSlot;
		this.defenderSlot = defenderSlot;
	}

	// --------------------------------------------------------- METHODES -- //

	public Fleet getAttacker() {
		return attacker;
	}

	public Fleet getDefender() {
		return defender;
	}

	public int getAttackerSlot() {
		return attackerSlot;
	}

	public int getDefenderSlot() {
		return defenderSlot;
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
