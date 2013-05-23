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

public class CriticalHitEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Fleet source;
	
	private Fleet target;
	
	private int sourceSlot;
	
	private int targetSlot;

	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public CriticalHitEvent(Fleet source, Fleet target, int sourceSlot,
			int targetSlot) {
		super();
		this.source = source;
		this.target = target;
		this.sourceSlot = sourceSlot;
		this.targetSlot = targetSlot;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public Fleet getSource() {
		return source;
	}
	
	public Fleet getTarget() {
		return target;
	}
	
	public int getSourceSlot() {
		return sourceSlot;
	}
	
	public int getTargetSlot() {
		return targetSlot;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
