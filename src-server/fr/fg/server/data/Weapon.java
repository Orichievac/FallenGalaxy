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

public class Weapon {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		AUTOCANON			= 0,
		LASER				= 1,
		PULSE_LASER			= 2,
		TACTICAL_ROCKET		= 3,
		TWIN_AUTOCANON		= 4,
		INFERNO				= 5,
		GAUSS				= 6,
		SONIC_TORPEDO		= 7,
		PPC					= 8,
		BLASTER				= 9,
		ION_CANON			= 10;
	
	public final static Weapon[] WEAPONS;
	
	static {
		WEAPONS = new Weapon[20];
		WEAPONS[AUTOCANON]		 = new Weapon(2,	 3);
		WEAPONS[LASER]			 = new Weapon(1,	 5);
		WEAPONS[PULSE_LASER]	 = new Weapon(3,	 6);
		WEAPONS[TACTICAL_ROCKET] = new Weapon(3,	 8);
		WEAPONS[TWIN_AUTOCANON]  = new Weapon(4,	 6);
		WEAPONS[INFERNO]		 = new Weapon(2,	38);
		WEAPONS[GAUSS]			 = new Weapon(16,	24);
		WEAPONS[SONIC_TORPEDO]	 = new Weapon(5,	10);
		WEAPONS[PPC]	 		 = new Weapon(0,	 0);
		WEAPONS[BLASTER] 		 = new Weapon(0,	 6);
		WEAPONS[ION_CANON]	 	 = new Weapon(8,	16);
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Dégâts minimum infligés par le vaisseau
	private int damageMin;

	// Dégâts maximum infligés par le vaisseau
	private int damageMax;

	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Weapon(int damageMin, int damageMax) {
		this.damageMin = damageMin;
		this.damageMax = damageMax;
	}

	// --------------------------------------------------------- METHODES -- //

	public int getDamageMin() {
		return damageMin;
	}

	public int getDamageMax() {
		return damageMax;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
