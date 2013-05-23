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

import fr.fg.server.data.base.WardBase;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.Utilities;

public class Ward extends WardBase {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static int
		OBSERVER_DETECTION_RADIUS = 5,
		SENTRY_DETECTION_RADIUS = 15;
	
	public final static int
		MINE_TRIGGER_RADIUS = 5,
		STUN_TRIGGER_RADIUS = 5;
	
	public final static int
		CHARGE_DEFUSE_RADIUS = 10;
	
	public final static int
		STUN_LENGTH = 4 * 3600;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Ward() {
		// Nécessaire pour la construction par réflection
	}
	
	public Ward(int x, int y, String type, int power, int idArea, int idOwner) {
		setX(x);
		setY(y);
		setType(type);
		setPower(power);
		setDate(Utilities.now());
		setIdArea(idArea);
		setIdOwner(idOwner);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Player getOwner() {
		return DataAccess.getPlayerById(getIdOwner());
	}
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public int getMinePower() {
		if (getType().equals(TYPE_MINE) ||
				getType().equals(TYPE_MINE_INVISIBLE))
			return (Fleet.getPowerAtLevel(getPower() + 1) - 1) / 2;
		else
			throw new IllegalStateException(
				"Invalid operation for ward type: '" + getType() + "'.");
	}
	
	public static int getRequiredSkill(String wardType) {
		if (wardType.equals(TYPE_OBSERVER) ||
				wardType.equals(TYPE_OBSERVER_INVISIBLE) ||
				wardType.equals(TYPE_SENTRY) ||
				wardType.equals(TYPE_SENTRY_INVISIBLE))
			return Skill.SKILL_ENGINEER;
		else if (wardType.equals(TYPE_STUN) ||
				wardType.equals(TYPE_STUN_INVISIBLE) ||
				wardType.equals(TYPE_MINE) ||
				wardType.equals(TYPE_MINE_INVISIBLE))
			return Skill.SKILL_PYROTECHNIST;
		else
			throw new IllegalArgumentException(
					"Invalid value: '" + wardType + "'.");
	}
	
	public static int getRequiredSkillLevel(String wardType) {
		if (wardType.equals(TYPE_OBSERVER) || wardType.equals(TYPE_STUN))
			return 0;
		else if (wardType.equals(TYPE_SENTRY) || wardType.equals(TYPE_MINE))
			return 1;
		else if (wardType.equals(TYPE_OBSERVER_INVISIBLE) || wardType.equals(TYPE_STUN_INVISIBLE))
			return 2;
		else if (wardType.equals(TYPE_SENTRY_INVISIBLE) || wardType.equals(TYPE_MINE_INVISIBLE))
			return 3;
		else
			throw new IllegalArgumentException(
					"Invalid value: '" + wardType + "'.");
	}
	
	public static int getWardXp(String wardType) {
		if (wardType.equals(TYPE_OBSERVER) ||
				wardType.equals(TYPE_SENTRY))
			return 3;
		else if (wardType.equals(TYPE_OBSERVER_INVISIBLE) ||
				wardType.equals(TYPE_SENTRY_INVISIBLE))
			return 6;
		if (wardType.equals(TYPE_STUN) ||
				wardType.equals(TYPE_MINE))
			return 3;
		else if (wardType.equals(TYPE_STUN_INVISIBLE) ||
				wardType.equals(TYPE_MINE_INVISIBLE))
			return 6;
		else
			throw new IllegalArgumentException(
					"Invalid type: '" + wardType + "'.");
	}
	
	public static long getWardCost(String wardType, int power) {
		if (wardType.startsWith(TYPE_OBSERVER) ||
				wardType.startsWith(TYPE_SENTRY)) {
			// Observation + détection 
			int increment = 500;
			int cost = 1500;
			
			for (int i = 1; i < power; i++) {
				cost += increment;
				increment += 50;
			}
			
			if (wardType.contains("invisible"))
				cost *= 4;
			
			return cost;
		} else if (wardType.startsWith(TYPE_MINE)) {
			// Charge explosive
			int ships = (Fleet.getPowerAtLevel(power + 1) - 1) / 2;
			int cost = ships * 10;
			
			if (wardType.contains("invisible"))
				cost *= 5;
			
			return cost;
		} else if (wardType.startsWith(TYPE_STUN)) {
			// Charge IEM
			int cost;
			
			if (wardType.contains("invisible"))
				cost = 47500 + 2500 * power;
			else
				cost = 9000 + 1000 * power;
			
			return cost;
		} else {
			throw new IllegalArgumentException(
					"Invalid type: '" + wardType + "'.");
		}
	}
	
	public static String getWardName(String type, int count) {
		String name = type.toLowerCase();
		
		while (name.contains("_")) {
			int index = name.indexOf("_");
			
			name = name.substring(0, index) +
				name.substring(index + 1, index + 2).toUpperCase() +
				name.substring(index + 2);
		}
		
		String key = "ward." + name + "s";
		return Messages.getString(key + (count > 1 ? "" : "[one]")).toLowerCase();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
