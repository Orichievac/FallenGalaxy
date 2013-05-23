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

package fr.fg.client.data;

public interface Sounds {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		MISCLICK = "misclick",
		SMALL_SHOT1 = "smalllas",
		SMALL_SHOT2 = "slighlas",
		AVERAGE_SHOT1 = "plascanh",
		AVERAGE_SHOT2 = "stormplas",
		IMPACT = "impact1",
		EXPLOSION = "explosion",
		HYPERSPACE = "hyperspace",
		ENGINE = "engine",
		MUSIC1 = "done",
		MUSIC2 = "silence.cellule",
		MUSIC3 = "silence.efface",
		MUSIC4 = "studiotjp";
	
	public final static String[] MUSICS = {
		MUSIC1, MUSIC2, MUSIC3, MUSIC4
	};
	
	// --------------------------------------------------------- METHODES -- //
}
