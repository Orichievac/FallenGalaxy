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

public class BeforeBattleEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet attackingFleet, defendingFleet;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	// Déclenché quand un joueur attaque une flotte ennemie avec l'une de ses
	// flottes, avant que le combat soit résolu
	public BeforeBattleEvent(Fleet attackingFleet, Fleet defendingFleet) {
		this.attackingFleet = attackingFleet;
		this.defendingFleet = defendingFleet;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public Fleet getAttackingFleet() {
		return attackingFleet;
	}
	
	public Fleet getDefendingFleet() {
		return defendingFleet;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
