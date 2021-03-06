/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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


public class Technology {
	// ------------------------------------------------------- CONSTANTES -- //

	
	public final static int
		TECHNOLOGY_0_PULSE_GENERATOR			= 1,
		TECHNOLOGY_0_SPACE_WARFARE				= 2,
		TECHNOLOGY_0_BALLISTIC					= 3,
		TECHNOLOGY_0_DOCTRINE_R55				= 4,
		TECHNOLOGY_1_PULSE_LASER				= 5,
		TECHNOLOGY_1_BOARDING					= 6,
		TECHNOLOGY_1_FIRE_CONTOL_SYSTEM			= 7,
		TECHNOLOGY_1_EVASIVE_MANEUVERS			= 8,
		TECHNOLOGY_1_SPACE_LOGISTICS			= 9,
		TECHNOLOGY_2_PULSE_REACTOR				= 10,
		TECHNOLOGY_2_HEPTANIUM_PROPULSION		= 11,
		TECHNOLOGY_2_BOMBARDMENT				= 12,
		TECHNOLOGY_2_SELF_DEFENSE_SYSTEM		= 13,
		TECHNOLOGY_2_DOCTRINE_C15				= 14,
		TECHNOLOGY_2_DRONES						= 15,
		TECHNOLOGY_3_SPACE_TIME_COMPRESSION		= 16,
		TECHNOLOGY_3_GAUSS_CANON				= 17,
		TECHNOLOGY_3_BATTLE_LINE				= 18,
		TECHNOLOGY_3_MICRO_CHARGES				= 19,
		TECHNOLOGY_3_DOCTRINE_VR_70				= 20,
		TECHNOLOGY_3_PROPAGANDA_THEORY			= 21,
		TECHNOLOGY_3_ADVANCED_SPACEFLIGHT		= 22,
		TECHNOLOGY_3_MAGNETIC_SHIELD			= 23,
		TECHNOLOGY_4_CHAOS_THEORY				= 24,
		TECHNOLOGY_4_SDI						= 25,
		TECHNOLOGY_4_FUSION_PROPULSION			= 26,
		TECHNOLOGY_4_ELECTRONIC_WARFARE			= 27,
		TECHNOLOGY_4_BATTLE_DRUGS				= 28,
		TECHNOLOGY_4_ENDOCTRINATION				= 29,
		TECHNOLOGY_4_OFFENSIVE_MANEUVERS		= 30,
		TECHNOLOGY_4_KESLEY_STABILISER			= 31,
		TECHNOLOGY_4_IRIDIUM_ALLOY				= 32,
		TECHNOLOGY_5_FUSION_BATTERY				= 33,
		TECHNOLOGY_5_UNIFIED_FIELD_THEORY		= 34,
		TECHNOLOGY_5_TRANSCENDENCE				= 35,
		TECHNOLOGY_5_MATTER_ALTERATION			= 36,
		TECHNOLOGY_5_CHAOS_GENERATOR			= 37,
		TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION		= 38,
		TECHNOLOGY_5_MAGNETODYNAMICS			= 39,
		TECHNOLOGY_6_DYNAMIC_REALITY			= 40,
		TECHNOLOGY_6_PHASE_TRANSFER				= 41,
		TECHNOLOGY_6_PARTICLE_PROJECTION		= 42,
		TECHNOLOGY_8_CAPITAL_SHIPS				= 43,
		TECHNOLOGY_6_DOGME_MARTIALE             = 44,
		TECHNOLOGY_7_DESTRUCTION_SHIPS          = 45,
	
		
		
		TECHNOLOGY_0_QUANTUM_PHYSICS			= 101,
		TECHNOLOGY_1_QUANTUM_COMPUTER			= 102,
		TECHNOLOGY_1_MECANIZED_WORKFORCE		= 103,
		TECHNOLOGY_1_SUPER_STRUCTURES			= 104,
		TECHNOLOGY_2_EXOPLANETARY_ECONOMICS		= 105,
		TECHNOLOGY_2_BIOMECHANICS				= 106,
		TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS	= 107,
		TECHNOLOGY_2_ORBITAL_ENGINEERING		= 108,
		TECHNOLOGY_3_APPLIED_RELATIVITY			= 109,
		TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE	= 110,
		TECHNOLOGY_3_NANITES					= 111,
		TECHNOLOGY_3_PLANETARY_CONSTRUCTION		= 112,
		TECHNOLOGY_4_EXONOMETRICS_THEORY		= 113,
		TECHNOLOGY_4_QUANTUM_ENGINEERING		= 114,
		TECHNOLOGY_4_INTELLIGENT_MATERIALS		= 115,
		TECHNOLOGY_4_SPACE_ENGINEERING			= 116,
		TECHNOLOGY_5_UNIFIED_PHYSICS			= 117,
		TECHNOLOGY_5_INTELLIGENT_MACHINES		= 118,
		TECHNOLOGY_5_ARCOLOGY					= 119,
		TECHNOLOGY_6_HARMONIZED_EXONOMETRICS	= 120,
		TECHNOLOGY_6_WEATHER_CONTROL			= 121,
		TECHNOLOGY_6_MAGELLAN_PROGRAM			= 122,
		TECHNOLOGY_7_ULTIMATE_BUILDINGS 		= 123
//		TECHNOLOGY_0_HULL_SILO = 201,
//		TECHNOLOGY_1_HULL_SILO = 202,
//		TECHNOLOGY_2_HULL_SILO = 203,
//		TECHNOLOGY_3_HULL_SILO = 204,
//		TECHNOLOGY_4_HULL_SILO = 205,
//		TECHNOLOGY_5_HULL_SILO = 206,
//		TECHNOLOGY_6_HULL_SILO = 207,
//		TECHNOLOGY_7_HULL_SILO = 208,
//		TECHNOLOGY_8_HULL_SILO = 209,
//		TECHNOLOGY_9_HULL_SILO = 210,
//		TECHNOLOGY_10_HULL_SILO = 211,
//		TECHNOLOGY_11_HULL_SILO = 212,
//		TECHNOLOGY_12_HULL_SILO = 213,
//		TECHNOLOGY_13_HULL_SILO = 214,
//		TECHNOLOGY_14_HULL_SILO = 215,
//		TECHNOLOGY_15_HULL_SILO = 216,
//		TECHNOLOGY_16_HULL_SILO = 217,
//		TECHNOLOGY_17_HULL_SILO = 218,
//		TECHNOLOGY_18_HULL_SILO = 219,
//		TECHNOLOGY_19_HULL_SILO = 220,
//		TECHNOLOGY_20_HULL_SILO = 221,
//		TECHNOLOGY_21_HULL_SILO = 222,
//		TECHNOLOGY_22_HULL_SILO = 223,
//		TECHNOLOGY_23_HULL_SILO = 224,
//		TECHNOLOGY_24_HULL_SILO = 225,
//		TECHNOLOGY_25_HULL_SILO = 226,
//		TECHNOLOGY_26_HULL_SILO = 227,
//		TECHNOLOGY_27_HULL_SILO = 228,
//		TECHNOLOGY_28_HULL_SILO = 229,
//		TECHNOLOGY_29_HULL_SILO = 230,
//		TECHNOLOGY_30_HULL_SILO = 231
//		
		;
	
	// Liste des technologies avec pour chacune les points de recherche
	// nécessaires pour les développer et les prérequis
	public final static Technology[] TECHNOLOGIES = {
		// Technologies militaires
		new Technology(TECHNOLOGY_0_PULSE_GENERATOR,			0,        100),
		new Technology(TECHNOLOGY_0_SPACE_WARFARE,				0,         50),
		new Technology(TECHNOLOGY_0_BALLISTIC,					0,        100),
		new Technology(TECHNOLOGY_0_DOCTRINE_R55,				0,         25),
		new Technology(TECHNOLOGY_1_PULSE_LASER,				1,        4000, TECHNOLOGY_0_PULSE_GENERATOR),
		new Technology(TECHNOLOGY_1_BOARDING,					1,        1000, TECHNOLOGY_0_SPACE_WARFARE),
		new Technology(TECHNOLOGY_1_FIRE_CONTOL_SYSTEM,			1,         750, TECHNOLOGY_0_BALLISTIC),
		new Technology(TECHNOLOGY_1_EVASIVE_MANEUVERS,			1,       1500),
		new Technology(TECHNOLOGY_1_SPACE_LOGISTICS,			1,       3000),
		new Technology(TECHNOLOGY_2_PULSE_REACTOR,				2,       13500, TECHNOLOGY_0_PULSE_GENERATOR),
		new Technology(TECHNOLOGY_2_HEPTANIUM_PROPULSION,		2,       19000, TECHNOLOGY_1_BOARDING, TECHNOLOGY_1_PULSE_LASER),
		new Technology(TECHNOLOGY_2_BOMBARDMENT,				2,        7000, TECHNOLOGY_1_FIRE_CONTOL_SYSTEM),
		new Technology(TECHNOLOGY_2_SELF_DEFENSE_SYSTEM,		2,       15000, TECHNOLOGY_1_FIRE_CONTOL_SYSTEM),
		new Technology(TECHNOLOGY_2_DOCTRINE_C15,				2,       11000, TECHNOLOGY_0_DOCTRINE_R55),
		new Technology(TECHNOLOGY_2_DRONES,						2,       10000, TECHNOLOGY_1_SPACE_LOGISTICS),
		new Technology(TECHNOLOGY_3_SPACE_TIME_COMPRESSION,		3,       45000, TECHNOLOGY_2_PULSE_REACTOR),
		new Technology(TECHNOLOGY_3_GAUSS_CANON,				3,      100000, TECHNOLOGY_2_HEPTANIUM_PROPULSION),
		new Technology(TECHNOLOGY_3_BATTLE_LINE,				3,      150000, TECHNOLOGY_2_HEPTANIUM_PROPULSION),
		new Technology(TECHNOLOGY_3_MICRO_CHARGES,				3,       60000, TECHNOLOGY_2_SELF_DEFENSE_SYSTEM),
		new Technology(TECHNOLOGY_3_DOCTRINE_VR_70,				3,       90000, TECHNOLOGY_2_DOCTRINE_C15),
		new Technology(TECHNOLOGY_3_PROPAGANDA_THEORY,			3,      130000, TECHNOLOGY_2_DOCTRINE_C15),
		new Technology(TECHNOLOGY_3_ADVANCED_SPACEFLIGHT,		3,       95000, TECHNOLOGY_1_EVASIVE_MANEUVERS),
		new Technology(TECHNOLOGY_3_MAGNETIC_SHIELD,			3,      105000, TECHNOLOGY_2_DRONES),
		new Technology(TECHNOLOGY_4_CHAOS_THEORY,				4,     1150000, TECHNOLOGY_3_SPACE_TIME_COMPRESSION),
		new Technology(TECHNOLOGY_4_SDI,						4,     1100000, TECHNOLOGY_3_GAUSS_CANON),
		new Technology(TECHNOLOGY_4_FUSION_PROPULSION,			4,     1250000, TECHNOLOGY_3_BATTLE_LINE, TECHNOLOGY_2_BOMBARDMENT),
		new Technology(TECHNOLOGY_4_ELECTRONIC_WARFARE,			4,      700000, TECHNOLOGY_3_MICRO_CHARGES),
		new Technology(TECHNOLOGY_4_BATTLE_DRUGS,				4,      650000, TECHNOLOGY_3_DOCTRINE_VR_70),
		new Technology(TECHNOLOGY_4_ENDOCTRINATION,				4,      950000, TECHNOLOGY_3_PROPAGANDA_THEORY),
		new Technology(TECHNOLOGY_4_OFFENSIVE_MANEUVERS,		4,      400000, TECHNOLOGY_3_ADVANCED_SPACEFLIGHT),
		new Technology(TECHNOLOGY_4_KESLEY_STABILISER,			4,     1500000, TECHNOLOGY_3_MAGNETIC_SHIELD),
		new Technology(TECHNOLOGY_4_IRIDIUM_ALLOY,				4,      750000, TECHNOLOGY_1_SPACE_LOGISTICS),
		new Technology(TECHNOLOGY_5_FUSION_BATTERY,				5,     2800000, TECHNOLOGY_4_FUSION_PROPULSION),
		new Technology(TECHNOLOGY_5_UNIFIED_FIELD_THEORY,		5,     4800000, TECHNOLOGY_4_ELECTRONIC_WARFARE),
		new Technology(TECHNOLOGY_5_TRANSCENDENCE,				5,     3200000, TECHNOLOGY_4_BATTLE_DRUGS, TECHNOLOGY_4_ENDOCTRINATION),
		new Technology(TECHNOLOGY_5_MATTER_ALTERATION,			5,     6000000, TECHNOLOGY_4_CHAOS_THEORY),
		new Technology(TECHNOLOGY_5_CHAOS_GENERATOR,			5,     3600000, TECHNOLOGY_4_CHAOS_THEORY),
		new Technology(TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION,		5,     7600000, TECHNOLOGY_4_KESLEY_STABILISER, TECHNOLOGY_4_IRIDIUM_ALLOY),
		new Technology(TECHNOLOGY_5_MAGNETODYNAMICS,		    5,     8500000, TECHNOLOGY_4_FUSION_PROPULSION),
		new Technology(TECHNOLOGY_6_DYNAMIC_REALITY,		    6,    32000000, TECHNOLOGY_5_MAGNETODYNAMICS, TECHNOLOGY_5_CHAOS_GENERATOR),
		new Technology(TECHNOLOGY_6_PHASE_TRANSFER,		     	6,    42000000, TECHNOLOGY_5_TRANSCENDENCE),
		new Technology(TECHNOLOGY_6_PARTICLE_PROJECTION,		6,    56000000, TECHNOLOGY_5_FUSION_BATTERY, TECHNOLOGY_5_UNIFIED_FIELD_THEORY),
		new Technology(TECHNOLOGY_6_DOGME_MARTIALE,             6,    34000000, TECHNOLOGY_5_MATTER_ALTERATION),
		new Technology(TECHNOLOGY_7_DESTRUCTION_SHIPS,          7,    40000000, TECHNOLOGY_6_DOGME_MARTIALE, TECHNOLOGY_6_DYNAMIC_REALITY),		
		new Technology(TECHNOLOGY_8_CAPITAL_SHIPS,			    8,   320000000, TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION),
		
		
		// Technologies civiles
		new Technology(TECHNOLOGY_0_QUANTUM_PHYSICS,     		0,	      200),
		new Technology(TECHNOLOGY_1_QUANTUM_COMPUTER,     		1,	      3500, TECHNOLOGY_0_QUANTUM_PHYSICS),
		new Technology(TECHNOLOGY_1_MECANIZED_WORKFORCE,     	1,	     6000),
		new Technology(TECHNOLOGY_1_SUPER_STRUCTURES,     		1,	     2000),
		new Technology(TECHNOLOGY_2_EXOPLANETARY_ECONOMICS,     2,	    28000),
		new Technology(TECHNOLOGY_2_BIOMECHANICS,     			2,       22000, TECHNOLOGY_1_MECANIZED_WORKFORCE),
		new Technology(TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS,    2, 	    18000),
		new Technology(TECHNOLOGY_2_ORBITAL_ENGINEERING,     	2,	     15000, TECHNOLOGY_1_SUPER_STRUCTURES),
		new Technology(TECHNOLOGY_3_APPLIED_RELATIVITY,     	3,	     80000, TECHNOLOGY_0_QUANTUM_PHYSICS),
		new Technology(TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE,   	3,      120000, TECHNOLOGY_1_QUANTUM_COMPUTER),
		new Technology(TECHNOLOGY_3_NANITES,     				3,      200000, TECHNOLOGY_2_BIOMECHANICS),
		new Technology(TECHNOLOGY_3_PLANETARY_CONSTRUCTION,     3,       60000, TECHNOLOGY_1_SUPER_STRUCTURES),
		new Technology(TECHNOLOGY_4_EXONOMETRICS_THEORY,     	4,     2200000, TECHNOLOGY_2_EXOPLANETARY_ECONOMICS),
		new Technology(TECHNOLOGY_4_QUANTUM_ENGINEERING,     	4,     1800000, TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE, TECHNOLOGY_3_NANITES),
		new Technology(TECHNOLOGY_4_INTELLIGENT_MATERIALS,  	4,     1200000, TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS),
		new Technology(TECHNOLOGY_4_SPACE_ENGINEERING,     		4,     1000000, TECHNOLOGY_2_ORBITAL_ENGINEERING),
		new Technology(TECHNOLOGY_5_UNIFIED_PHYSICS,     		5,     5000000, TECHNOLOGY_3_APPLIED_RELATIVITY),
		new Technology(TECHNOLOGY_5_INTELLIGENT_MACHINES,   	5,    10000000, TECHNOLOGY_4_QUANTUM_ENGINEERING, TECHNOLOGY_4_INTELLIGENT_MATERIALS),
		new Technology(TECHNOLOGY_5_ARCOLOGY,     				5,     4000000, TECHNOLOGY_3_PLANETARY_CONSTRUCTION),
		new Technology(TECHNOLOGY_6_HARMONIZED_EXONOMETRICS,    6,    50000000, TECHNOLOGY_4_EXONOMETRICS_THEORY, TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE),
		new Technology(TECHNOLOGY_6_WEATHER_CONTROL,     		6,    40000000, TECHNOLOGY_5_INTELLIGENT_MACHINES, TECHNOLOGY_5_ARCOLOGY),
		new Technology(TECHNOLOGY_6_MAGELLAN_PROGRAM,     		6,    30000000, TECHNOLOGY_4_SPACE_ENGINEERING),
	    new Technology(TECHNOLOGY_7_ULTIMATE_BUILDINGS,         7,    60000000, TECHNOLOGY_5_INTELLIGENT_MACHINES, TECHNOLOGY_6_WEATHER_CONTROL)
//		// Technologies pour les modules
//		new Technology(TECHNOLOGY_0_HULL_SILO,     		    0,  100),
//		new Technology(TECHNOLOGY_1_HULL_SILO,     		    1, 500 , TECHNOLOGY_0_HULL_SILO),
//	
	};
	
	
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Technology[] technologiesById;
	
	static {
		technologiesById = new Technology[500];
		
		for (Technology technology : TECHNOLOGIES)
			technologiesById[technology.getId()] = technology;
	}
	
	private int id;
	private int level;
	private long length;
	private int[] requirements;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	private Technology(int id, int level, long length, int... requirements) {
		this.id = id;
		this.level = level;
		this.length = length;
		this.requirements = requirements;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getLevel() {
		return level;
	}
	
	public long getLength() {
		return length / GameConstants.TIME_UNIT;
	}
	
	public int[] getRequirements() {
		return requirements;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static Technology getTechnologyById(int id) {
		if (id < 0 || id >= technologiesById.length)
			return null;
		return technologiesById[id];
	}
	
	public static int getMaxTechLevel(){
		int lvl=0;
		for (Technology technology : TECHNOLOGIES)
		{
			if(technology.getLevel()>lvl)
				lvl=technology.getLevel();
		}
		return lvl;
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
