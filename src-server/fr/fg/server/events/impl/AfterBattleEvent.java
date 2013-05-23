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

public class AfterBattleEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet attackingFleetBefore, defendingFleetBefore,
		attackingFleetAfter, defendingFleetAfter;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	// Déclenché quand un joueur attaque une flotte ennemie avec l'une de ses
	// flottes, après que le combat soit résolu
	public AfterBattleEvent(Fleet attackingFleetBefore,
			Fleet defendingFleetBefore,
			Fleet attackingFleetAfter,
			Fleet defendingFleetAfter) {
		this.attackingFleetBefore = attackingFleetBefore;
		this.defendingFleetBefore = defendingFleetBefore;
		this.attackingFleetAfter = attackingFleetAfter;
		this.defendingFleetAfter = defendingFleetAfter;
	}

	// --------------------------------------------------------- METHODES -- //

	public Fleet getAttackingFleetBefore() {
		return attackingFleetBefore;
	}

	public Fleet getDefendingFleetBefore() {
		return defendingFleetBefore;
	}

	public Fleet getAttackingFleetAfter() {
		return attackingFleetAfter;
	}

	public Fleet getDefendingFleetAfter() {
		return defendingFleetAfter;
	}
	
	public boolean isDefendingFleetDestroyed() {
		return defendingFleetAfter == null;
	}
	
	public boolean isAttackingFleetDestroyed() {
		return attackingFleetAfter == null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
