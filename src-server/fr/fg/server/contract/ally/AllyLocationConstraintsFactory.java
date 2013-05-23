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
import fr.fg.server.data.Area;
import fr.fg.server.data.Sector;

public class AllyLocationConstraintsFactory {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static AllyLocationConstraints getLevelLocationConstraints(
			int startValue, int incrementEvery, int incrementBy) {
		return new LevelLocationConstraints(startValue, incrementEvery,
				incrementBy, true, new int[0], new int[0]);
	}

	public static AllyLocationConstraints getLevelLocationConstraints(
			int startValue, int incrementEvery, int incrementBy,
			boolean allowMultipleAreaSelection, int[] invalidSectorTypes,
			int[] invalidAreaTypes) {
		return new LevelLocationConstraints(startValue, incrementEvery,
				incrementBy, allowMultipleAreaSelection, invalidSectorTypes,
				invalidAreaTypes);
	}
	
	public static AllyLocationConstraints getPointsLocationConstraints(
			long pointsThreshold, int pointsMultiplier) {
		return new PointLocationConstraints(pointsThreshold,
				pointsMultiplier, true, new int[0], new int[0]);
	}
	
	public static AllyLocationConstraints getPointsLocationConstraints(
			long pointsThreshold, int pointsMultiplier,
			boolean allowMultipleAreaSelection) {
		return new PointLocationConstraints(pointsThreshold,
				pointsMultiplier, allowMultipleAreaSelection,
				new int[0], new int[0]);
	}
	
	public static AllyLocationConstraints getPointsLocationConstraints(
			long pointsThreshold, int pointsMultiplier,
			boolean allowMultipleAreaSelection, int[] invalidSectorTypes,
			int[] invalidAreaTypes) {
		return new PointLocationConstraints(pointsThreshold,
				pointsMultiplier, allowMultipleAreaSelection,
				invalidSectorTypes, invalidAreaTypes);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static int getAreasCountByPoints(Ally ally,
			long pointsThreshold, int pointsMultiplier) {

			double threshold = pointsThreshold;
			int multiplier = pointsMultiplier;
			int areasCount = 1;
			
			while (threshold < ally.getPoints()) {
				threshold *= multiplier;
				areasCount++;
			}
			
			return areasCount;

	}
	
	// ------------------------------------------------- CLASSES INTERNES -- //
	
	private static abstract class BaseLocationConstraints implements
			AllyLocationConstraints {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private boolean allowMultipleAreaSelection;
		
		private int[] invalidSectorTypes;
		
		private int[] invalidAreaTypes;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public BaseLocationConstraints(
				boolean allowMultipleAreaSelection,
				int[] invalidSectorTypes, int[] invalidAreaTypes) {
			this.allowMultipleAreaSelection = allowMultipleAreaSelection;
			this.invalidSectorTypes = invalidSectorTypes;
			this.invalidAreaTypes = invalidAreaTypes;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public boolean allowMultipleAreaSelection() {
			return allowMultipleAreaSelection;
		}
		
		public boolean isValidSector(Sector sector) {
			for (int i = 0; i < invalidSectorTypes.length; i++)
				if (invalidSectorTypes[i] == sector.getType())
					return false;
			return true;
		}
		
		public boolean isValidArea(Area area) {
			for (int i = 0; i < invalidAreaTypes.length; i++)
				if (invalidAreaTypes[i] == area.getType())
					return false;
			return true;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private static class LevelLocationConstraints extends
			BaseLocationConstraints {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int startValue;
		
		private int incrementEvery;
		
		private int incrementBy;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public LevelLocationConstraints(int startValue, int incrementEvery,
				int incrementBy, boolean allowMultipleAreaSelection,
				int[] invalidSectorTypes, int[] invalidAreaTypes) {
			super(allowMultipleAreaSelection, invalidSectorTypes,
					invalidAreaTypes);
			
			this.startValue = startValue;
			this.incrementEvery = incrementEvery;
			this.incrementBy = incrementBy;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public int getAreasCount(Ally ally) {
			int level = ally.getMediumLevel() - 1;
			return startValue + (level / incrementEvery) * incrementBy;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private static class PointLocationConstraints extends
			BaseLocationConstraints {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private long pointsThreshold;
		
		private int pointsMultiplier;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public PointLocationConstraints(
				long pointsThreshold, int pointsMultiplier,
				boolean allowMultipleAreaSelection,
				int[] invalidSectorTypes, int[] invalidAreaTypes) {
			super(allowMultipleAreaSelection, invalidSectorTypes,
					invalidAreaTypes);
			
			this.pointsThreshold = pointsThreshold;
			this.pointsMultiplier = pointsMultiplier;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public int getAreasCount(Ally ally) {
			return getAreasCountByPoints(ally,
					pointsThreshold, pointsMultiplier);
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
