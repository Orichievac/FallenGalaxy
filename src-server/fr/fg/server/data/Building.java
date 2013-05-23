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

public class Building {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static int
		LABORATORY = 0,
		STOREHOUSE = 1,
		SPACESHIP_YARD = 2,
		DEFENSIVE_DECK = 3,
		EXPLOITATION0 = 4,
		EXPLOITATION1 = 5,
		EXPLOITATION2 = 6,
		EXPLOITATION3 = 7,
		EXTRACTOR_CENTER = 8,
		CIVILIAN_INFRASTRUCTURES = 9,
		CORPORATIONS = 10,
		TRADE_PORT = 11,
		RESEARCH_CENTER = 12,
		FACTORY = 13,
		REFINERY = 14;
	
	public final static int BUILDING_COUNT = 15;
	
	public final static int BUILDING_LEVEL_COUNT = 5;
	
	public final static String[] BUILDING_LABELS = {
		"laboratory",
		"storehouse",
		"spaceship_yard",
		"defensive_deck",
		"exploitation0",
		"exploitation1",
		"exploitation2",
		"exploitation3",
		"extractor_center",
		"civilian_infrastructures",
		"corporations",
		"trade_port",
		"research_center",
		"factory",
		"refinery"
	};

	public final static int[][]
  		COST_LABORATORY = {
			{0, 0, 500, 0, 0},
			{0, 0, 5000, 0, 2000},
			{0, 0, 120000, 0, 60000},
			{0, 0, 2000000, 0, 1000000},
			{0, 0, 40000000, 0, 20000000}},
		COST_STOREHOUSE = {
			{200, 0, 0, 0, 0},
			{2000, 0, 0, 0, 800},
			{36000, 0, 12000, 0, 24000},
			{600000, 0, 200000, 0, 400000},
			{12000000, 0, 4000000, 0, 8000000}},
		COST_SPACESHIP_YARD = {
			{500, 0, 0, 0, 0},
			{5000, 0, 0, 0, 2000},
			{90000, 0, 30000, 0, 60000},
			{1500000, 0, 500000, 0, 1000000},
			{30000000, 0, 10000000, 0, 20000000}},
		COST_DEFENSIVE_DECK = {
			{200, 0, 0, 0, 0},
			{2000, 0, 0, 0, 800},
			{30000, 0, 20000, 0, 24000},
			{400000, 0, 400000, 0, 400000},
			{8000000, 0, 8000000, 0, 8000000}},
	    COST_EXPLOITATION0 = {
			{0, 500, 0, 0, 0},
			{0, 5000, 0, 0, 2000},
			{0, 90000, 30000, 0, 60000},
			{0, 1500000, 500000, 0, 1000000},
			{0, 30000000, 10000000, 0, 20000000}},
		COST_EXPLOITATION1 = {
			{0, 0, 500, 0, 0},
			{0, 0, 5000, 0, 2000},
			{30000, 0, 90000, 0, 60000},
			{500000, 0, 1500000, 0, 1000000},
			{10000000, 0, 30000000, 0, 20000000}},
		COST_EXPLOITATION2 = {
			{500, 0, 0, 0, 0},
			{5000, 0, 0, 0, 2000},
			{90000, 30000, 0, 0, 60000},
			{1500000, 500000, 0, 0, 1000000},
			{30000000, 10000000, 0, 0, 20000000}},
		COST_EXPLOITATION3 = {
			{750, 0, 0, 0, 0},
			{7500, 0, 0, 0, 3000},
			{135000, 0, 45000, 0, 90000},
			{2250000, 0, 750000, 0, 1500000},
			{45000000, 0, 15000000, 0, 30000000}},
		COST_EXTRACTOR_CENTER = {
			{1000, 0, 0, 0, 0},
			{10000, 0, 0, 0, 4000},
			{180000, 60000, 0, 0, 120000},
			{3000000, 1000000, 0, 0, 2000000},
			{60000000, 20000000, 0, 0, 40000000}},
		COST_CIVILIAN_INFRASTRUCTURES = {
			{250, 250, 0, 0, 0},
			{2500, 2500, 0, 0, 2000},
			{60000, 60000, 0, 0, 60000},
			{1000000, 1000000, 0, 0, 1000000},
			{20000000, 20000000, 0, 0, 20000000}},
		COST_CORPORATIONS = {
			{1000, 0, 0, 0, 0},
			{10000, 0, 0, 0, 4000},
			{180000, 0, 60000, 0, 120000},
			{3000000, 0, 1000000, 0, 2000000},
			{60000000, 0, 20000000, 0, 40000000}},
		COST_TRADE_PORT = {
			{0, 750, 0, 0, 0},
			{0, 7500, 0, 0, 3000},
			{0, 135000, 45000, 0, 90000},
			{0, 2250000, 750000, 0, 1500000},
			{0, 45000000, 15000000, 0, 30000000}},
		COST_RESEARCH_CENTER = {
			{0, 750, 0, 0, 0},
			{0, 7500, 0, 0, 3000},
			{0, 180000, 0, 0, 90000},
			{0, 3000000, 0, 0, 1500000},
			{0, 60000000, 0, 0, 30000000}},
		COST_FACTORY = {
			{250, 0, 0, 0, 0},
			{2500, 0, 0, 0, 1000},
			{40000, 0, 20000, 0, 30000},
			{750000, 0, 250000, 0, 500000},
			{15000000, 0, 5000000, 0, 10000000}},
		COST_REFINERY = {
			{0, 1250, 0, 0, 0},
			{0, 12500, 0, 0, 5000},
			{0, 225000, 75000, 0, 150000},
			{0, 3750000, 1250000, 0, 2500000},
			{0, 75000000, 25000000, 0, 50000000}};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private int level;
	
	private long end;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Building(int type, int level, long end) {
		this.type = type;
		this.level = level;
		this.end = end;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public static int[] getCost(int type, int level) {
		switch (type) {
		case LABORATORY:
			return COST_LABORATORY[level];
		case STOREHOUSE:
			return COST_STOREHOUSE[level];
		case SPACESHIP_YARD:
			return COST_SPACESHIP_YARD[level];
		case DEFENSIVE_DECK:
			return COST_DEFENSIVE_DECK[level];
		case EXPLOITATION0:
			return COST_EXPLOITATION0[level];
		case EXPLOITATION1:
			return COST_EXPLOITATION1[level];
		case EXPLOITATION2:
			return COST_EXPLOITATION2[level];
		case EXPLOITATION3:
			return COST_EXPLOITATION3[level];
		case EXTRACTOR_CENTER:
			return COST_EXTRACTOR_CENTER[level];
		case CIVILIAN_INFRASTRUCTURES:
			return COST_CIVILIAN_INFRASTRUCTURES[level];
		case CORPORATIONS:
			return COST_CORPORATIONS[level];
		case TRADE_PORT:
			return COST_TRADE_PORT[level];
		case RESEARCH_CENTER:
			return COST_RESEARCH_CENTER[level];
		case FACTORY:
			return COST_FACTORY[level];
		case REFINERY:
			return COST_REFINERY[level];
		default:
			return null;
		}
	}
	
	public static int[] getCost(Building building) {
		return getCost(building.getType(), building.getLevel());
	}
	
	public static boolean isExploitation(int type) {
		return type >= EXPLOITATION0 && type <= EXPLOITATION3;
	}

	public static boolean isExploitation(Building building) {
		return isExploitation(building.getType());
	}
	
	public static int getFarmedResource(Building building) {
		return getFarmedResource(building.getType());
	}
	
	public static int getFarmedResource(int type) {
		if (isExploitation(type))
			return Integer.parseInt(BUILDING_LABELS[type].substring(
					"exploitation".length()));
		return -1;
	}
	
	public static int getBuildTime(int type, int level) {
		switch (type) {
		case STOREHOUSE:
		case DEFENSIVE_DECK:
		case FACTORY:
			switch (level) {
			case 0:
				return 300 / GameConstants.TIME_UNIT;
			case 1:
				return 5400 / GameConstants.TIME_UNIT;
			case 2:
				return 21600 / GameConstants.TIME_UNIT;
			case 3:
				return 86400 / GameConstants.TIME_UNIT;
			case 4:
				return 216000 / GameConstants.TIME_UNIT;
			default:
				throw new IllegalArgumentException(
						"Level value out of range: '" + level + "'");
			}
		case LABORATORY:
		case SPACESHIP_YARD:
		case EXPLOITATION0:
		case EXPLOITATION1:
		case EXPLOITATION2:
		case EXPLOITATION3:
		case CIVILIAN_INFRASTRUCTURES:
			switch (level) {
			case 0:
				return 600 / GameConstants.TIME_UNIT; // 10min
			case 1:
				return 10800 / GameConstants.TIME_UNIT; // 3h
			case 2:
				return 43200 / GameConstants.TIME_UNIT; // 12h
			case 3:
				return 172800 / GameConstants.TIME_UNIT; // 2j
			case 4:
				return 432000 / GameConstants.TIME_UNIT; // 5j
			default:
				throw new IllegalArgumentException(
						"Level value out of range: '" + level + "'");
			}
		case EXTRACTOR_CENTER:
		case CORPORATIONS:
		case TRADE_PORT:
		case RESEARCH_CENTER:
		case REFINERY:
			switch (level) {
			case 0:
				return 900 / GameConstants.TIME_UNIT;
			case 1:
				return 16200 / GameConstants.TIME_UNIT;
			case 2:
				return 64800 / GameConstants.TIME_UNIT;
			case 3:
				return 259200 / GameConstants.TIME_UNIT;
			case 4:
				return 648000 / GameConstants.TIME_UNIT;
			default:
				throw new IllegalArgumentException(
						"Level value out of range: '" + level + "'");
			}
		default:
			throw new IllegalArgumentException(
					"Invalid building type: '" + type + "'");
		}
	}
	
	public static int getBuildTime(Building building) {
		return getBuildTime(building.getType(), building.getLevel());
	}
	
    public static double getProduction(int type, int[] buildings) {
    	switch (type) {
    	case Building.STOREHOUSE:
    		return GameConstants.SYSTEM_STORAGE_CAPACITY +
    			    10000 * buildings[0] +
    			   100000 * buildings[1] +
    			  1000000 * buildings[2] +
    			 10000000 * buildings[3] +
    			100000000 * buildings[4];
    	case Building.CORPORATIONS:
    		return
    			1.00 * buildings[0] +
				1.25 * buildings[1] +
				1.50 * buildings[2] +
				1.75 * buildings[3] +
				2.00 * buildings[4];
    	case Building.FACTORY:
    		return 1 -
    			.15 * buildings[0] -
    			.30 * buildings[1] -
    			.45 * buildings[2] -
    			.60 * buildings[3] -
    			.75 * buildings[4];
    	case Building.RESEARCH_CENTER:
    	case Building.REFINERY:
    		return 1 +
    			.1 * buildings[0] +
    			.2 * buildings[1] +
    			.3 * buildings[2] +
    			.4 * buildings[3] +
    			.5 * buildings[4];
    	case Building.CIVILIAN_INFRASTRUCTURES:
    		return GameConstants.SYSTEM_POPULATION_CAPACITY +
    			 1 * buildings[0] +
				 5 * buildings[1] +
				25 * buildings[2] +
			   125 * buildings[3] +
			   625 * buildings[4];
    	case Building.EXTRACTOR_CENTER:
			return
				90 * buildings[0] +
			   450 * buildings[1] +
			  2250 * buildings[2] +
			 11250 * buildings[3] +
			 56250 * buildings[4];
    	case Building.DEFENSIVE_DECK:
    		return 1 + (
    			 5 * buildings[0] +
			 	10 * buildings[1] +
				15 * buildings[2] +
				20 * buildings[3] +
			    25 * buildings[4]) / 100.;
    	case Building.TRADE_PORT:
			return 1 - (
			   .05 * buildings[0] +
			   .10 * buildings[1] +
			   .15 * buildings[2] +
			   .20 * buildings[3] +
			   .25 * buildings[4]);
    	default:
    		return   buildings[0] +
    			 5 * buildings[1] +
    			25 * buildings[2] +
    		   125 * buildings[3] +
    		   625 * buildings[4];
    	}
    }
    
	public static int[] getRequiredTechnologies(int type, int level) {
		// Les batiments niveau 1 et 2 ne nécessitent pas de technologies
		if (level <= 1)
			return new int[0];
		
		int[] technologies = new int[0];
		
		switch (type) {
		case STOREHOUSE:
		case CIVILIAN_INFRASTRUCTURES:
			technologies = new int[]{
				Technology.TECHNOLOGY_1_SUPER_STRUCTURES,
				Technology.TECHNOLOGY_3_PLANETARY_CONSTRUCTION,
				Technology.TECHNOLOGY_5_ARCOLOGY};
			break;
		case SPACESHIP_YARD:
			technologies = new int[]{
				Technology.TECHNOLOGY_2_ORBITAL_ENGINEERING,
				Technology.TECHNOLOGY_4_SPACE_ENGINEERING,
				Technology.TECHNOLOGY_6_MAGELLAN_PROGRAM};
			break;
		case DEFENSIVE_DECK:
			technologies = new int[]{
				Technology.TECHNOLOGY_1_QUANTUM_COMPUTER,
				Technology.TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE,
				Technology.TECHNOLOGY_5_INTELLIGENT_MACHINES};
			break;
		case REFINERY:
			technologies = new int[]{
				Technology.TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS,
				Technology.TECHNOLOGY_4_INTELLIGENT_MATERIALS,
				Technology.TECHNOLOGY_6_WEATHER_CONTROL};
			break;
		case RESEARCH_CENTER:
			technologies = new int[]{
				Technology.TECHNOLOGY_0_QUANTUM_PHYSICS,
				Technology.TECHNOLOGY_3_APPLIED_RELATIVITY,
				Technology.TECHNOLOGY_5_UNIFIED_PHYSICS};
			break;
		case FACTORY:
			technologies = new int[]{
				Technology.TECHNOLOGY_1_MECANIZED_WORKFORCE,
				Technology.TECHNOLOGY_3_NANITES,
				Technology.TECHNOLOGY_5_INTELLIGENT_MACHINES};
			break;
		case LABORATORY:
			technologies = new int[]{
				Technology.TECHNOLOGY_0_QUANTUM_PHYSICS,
				Technology.TECHNOLOGY_3_APPLIED_RELATIVITY,
				Technology.TECHNOLOGY_5_UNIFIED_PHYSICS};
			break;
		case EXPLOITATION0:
		case EXPLOITATION1:
		case EXPLOITATION2:
		case EXPLOITATION3:
			technologies = new int[]{
				Technology.TECHNOLOGY_1_MECANIZED_WORKFORCE,
				Technology.TECHNOLOGY_3_NANITES,
				Technology.TECHNOLOGY_5_INTELLIGENT_MACHINES};
			break;
		case TRADE_PORT:
		case CORPORATIONS:
			technologies = new int[]{
				Technology.TECHNOLOGY_2_EXOPLANETARY_ECONOMICS,
				Technology.TECHNOLOGY_4_EXONOMETRICS_THEORY,
				Technology.TECHNOLOGY_6_HARMONIZED_EXONOMETRICS};
			break;
		case EXTRACTOR_CENTER:
			technologies = new int[]{
				Technology.TECHNOLOGY_2_BIOMECHANICS,
				Technology.TECHNOLOGY_4_QUANTUM_ENGINEERING,
				Technology.TECHNOLOGY_6_WEATHER_CONTROL};
			break;
		}
		
		return technologies.length > 0 ?
				new int[]{technologies[level - 2]} : new int[0];
	}
	
    public static int getRequiredSpaceshipYardLevel(int shipClass) {
    	switch (shipClass) {
    	case Ship.FIGHTER:
    	case Ship.CORVETTE:
    	case Ship.FREIGHTER:
    		return 0;
    	case Ship.FRIGATE:
    		return 1;
    	case Ship.DESTROYER:
    		return 2;
    	case Ship.DREADNOUGHT:
    		return 3;
    	case Ship.BATTLECRUISER:
    		return 4;
    	default:
    		throw new IllegalArgumentException(
    				"Invalid ship class: '" + shipClass + "'.");
    	}

    }
    
	// ------------------------------------------------- METHODES PRIVEES -- //
}
