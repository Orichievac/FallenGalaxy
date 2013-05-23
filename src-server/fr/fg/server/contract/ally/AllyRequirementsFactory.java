/*
Copyright 2010 Nicolas Bosc

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

package fr.fg.server.contract.ally;

import fr.fg.server.data.Ally;

public class AllyRequirementsFactory {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public static AllyRequirements getNoRequirements() {
		return new DefaultRequirements(-1, -1, -1, -1);
	}
	
	public static AllyRequirements getPointsRequirements(long minPoints) {
		return new DefaultRequirements(minPoints, -1, -1, -1);
	}
	
	public static AllyRequirements getPointsRequirements(long minPoints, long maxPoints) {
		return new DefaultRequirements(minPoints, maxPoints, -1, -1);
	}
	
	public static AllyRequirements getLevelRequirements(int minLevel) {
		return new DefaultRequirements(-1, -1, minLevel, -1);
	}
	
	public static AllyRequirements getLevelRequirements(int minLevel, int maxLevel) {
		return new DefaultRequirements(-1, -1, minLevel, maxLevel);
	}
	
	public static AllyRequirements getPointsAndLevelRequirements(long minPoints,
			long maxPoints, int minLevel, int maxLevel) {
		return new DefaultRequirements(minPoints, maxPoints, minLevel, maxLevel);
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static class DefaultRequirements implements AllyRequirements {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private long minPoints, maxPoints;
		
		private int minLevel, maxLevel;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public DefaultRequirements(long minPoints, long maxPoints,
				int minLevel, int maxLevel) {
			super();
			this.minPoints = minPoints;
			this.maxPoints = maxPoints;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public boolean isAdmissible(Ally ally) {
			long points = 0;
			if(ally!=null) points =ally.getPoints();
			
			if (minPoints != -1 && points < minPoints)
				return false;
			if (maxPoints != -1 && points > minPoints)
				return false;
			
			int level = ally.getMediumLevel();
			
			if (minLevel != -1 && level < minLevel)
				return false;
			if (maxLevel != -1 && level > minLevel)
				return false;
			
			
			return true;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
