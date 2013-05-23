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

import fr.fg.server.data.StarSystem;
import fr.fg.server.events.GameEvent;

public class BuiltShipsEvent extends GameEvent {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private StarSystem source;
	
	private int shipId;
	
	private long count;

	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public BuiltShipsEvent(StarSystem source, int shipId, long count) {
		super();
		this.source = source;
		this.shipId = shipId;
		this.count = count;
	}

	// --------------------------------------------------------- METHODES -- //

	public StarSystem getSource() {
		return source;
	}

	public int getShipId() {
		return shipId;
	}

	public long getCount() {
		return count;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
