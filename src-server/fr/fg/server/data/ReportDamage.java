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

import fr.fg.server.data.base.ReportDamageBase;

public class ReportDamage extends ReportDamageBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		DODGED			= 1 << 0,
		CRITICAL_HIT	= 1 << 2,
		PHASED			= 1 << 3,
		SUBLIMATION		= 1 << 4,
		PARTICLES		= 1 << 5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ReportDamage() {
		// Nécessaire pour la construction par réflection
	}

	public ReportDamage(int targetPosition, long damage, int kills,
			int hullDamage) {
		setTargetPosition(targetPosition);
		setDamage(damage);
		setKills(kills);
		setHullDamage(hullDamage);
		setStealedResource0(0);
		setStealedResource1(0);
		setStealedResource2(0);
		setStealedResource3(0);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public double getStealedResource(int type) {
		switch (type) {
		case 0:
			return getStealedResource0();
		case 1:
			return getStealedResource1();
		case 2:
			return getStealedResource2();
		case 3:
			return getStealedResource3();
		}
		throw new IllegalArgumentException(
				"Invalid resource index: '" + type + "'.");
	}
	
	public void setStealedResource(double resources, int type) {
		switch (type) {
		case 0:
			setStealedResource0(resources);
			break;
		case 1:
			setStealedResource1(resources);
			break;
		case 2:
			setStealedResource2(resources);
			break;
		case 3:
			setStealedResource3(resources);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid resource index: '" + type + "'.");
		}
	}
	
	public void addDamage(long amount, int kills, int hullDamage) {
		setDamage(getDamage() + amount);
		setKills(getKills() + kills);
		setHullDamage(hullDamage);
	}
	
	public void addModifier(int modifier) {
		setModifiers(getModifiers() | modifier);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
