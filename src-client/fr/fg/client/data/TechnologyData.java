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

import com.google.gwt.core.client.GWT;

import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;

public class TechnologyData {
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
		TECHNOLOGY_7_ULTIMATE_BUILDINGS			= 123;
//		
//		
//		TECHNOLOGY_0_HULL_SILO = 201,
//		TECHNOLOGY_1_HULL_SILO = 202;
	
	// Liste des technologies avec pour chacune les points de recherche
	// nécessaires pour les développer et les prérequis
	public final static TechnologyData[] TECHNOLOGIES = {
		// Technologies militaires
		new TechnologyData(TECHNOLOGY_0_PULSE_GENERATOR,			         100,  0,  0),
		new TechnologyData(TECHNOLOGY_0_SPACE_WARFARE,				          50,  0,  2),
		new TechnologyData(TECHNOLOGY_0_BALLISTIC,					         100,  0,  3),
		new TechnologyData(TECHNOLOGY_0_DOCTRINE_R55,				          25,  0,  5),
		new TechnologyData(TECHNOLOGY_1_PULSE_LASER,				        4000,  1,  1, TECHNOLOGY_0_PULSE_GENERATOR),
		new TechnologyData(TECHNOLOGY_1_BOARDING,					        1000,  1,  2, TECHNOLOGY_0_SPACE_WARFARE),
		new TechnologyData(TECHNOLOGY_1_FIRE_CONTOL_SYSTEM,			         750,  1,  3, TECHNOLOGY_0_BALLISTIC),
		new TechnologyData(TECHNOLOGY_1_EVASIVE_MANEUVERS,			        1500,  1,  7),
		new TechnologyData(TECHNOLOGY_1_SPACE_LOGISTICS,			        3000,  1,  9),
		new TechnologyData(TECHNOLOGY_2_PULSE_REACTOR,				       13500,  2,  0, TECHNOLOGY_0_PULSE_GENERATOR),
		new TechnologyData(TECHNOLOGY_2_HEPTANIUM_PROPULSION,		       19000,  2,  2, TECHNOLOGY_1_BOARDING, TECHNOLOGY_1_PULSE_LASER),
		new TechnologyData(TECHNOLOGY_2_BOMBARDMENT,				        7000,  2,  3, TECHNOLOGY_1_FIRE_CONTOL_SYSTEM),
		new TechnologyData(TECHNOLOGY_2_SELF_DEFENSE_SYSTEM,		       15000,  2,  4, TECHNOLOGY_1_FIRE_CONTOL_SYSTEM),
		new TechnologyData(TECHNOLOGY_2_DOCTRINE_C15,				       11000,  2,  5, TECHNOLOGY_0_DOCTRINE_R55),
		new TechnologyData(TECHNOLOGY_2_DRONES,						       10000,  2,  8, TECHNOLOGY_1_SPACE_LOGISTICS),
		new TechnologyData(TECHNOLOGY_3_SPACE_TIME_COMPRESSION,		       45000,  3,  0, TECHNOLOGY_2_PULSE_REACTOR),
		new TechnologyData(TECHNOLOGY_3_GAUSS_CANON,				      100000,  3,  1, TECHNOLOGY_2_HEPTANIUM_PROPULSION),
		new TechnologyData(TECHNOLOGY_3_BATTLE_LINE,				      150000,  3,  2, TECHNOLOGY_2_HEPTANIUM_PROPULSION),
		new TechnologyData(TECHNOLOGY_3_MICRO_CHARGES,				       60000,  3,  4, TECHNOLOGY_2_SELF_DEFENSE_SYSTEM),
		new TechnologyData(TECHNOLOGY_3_DOCTRINE_VR_70,				       90000,  3,  5, TECHNOLOGY_2_DOCTRINE_C15),
		new TechnologyData(TECHNOLOGY_3_PROPAGANDA_THEORY,			      130000,  3,  6, TECHNOLOGY_2_DOCTRINE_C15),
		new TechnologyData(TECHNOLOGY_3_ADVANCED_SPACEFLIGHT,		       95000,  3,  7, TECHNOLOGY_1_EVASIVE_MANEUVERS),
		new TechnologyData(TECHNOLOGY_3_MAGNETIC_SHIELD,			      105000,  3,  8, TECHNOLOGY_2_DRONES),
		new TechnologyData(TECHNOLOGY_4_CHAOS_THEORY,				     1150000,  4,  0, TECHNOLOGY_3_SPACE_TIME_COMPRESSION),
		new TechnologyData(TECHNOLOGY_4_SDI,						     1100000,  4,  1, TECHNOLOGY_3_GAUSS_CANON),
		new TechnologyData(TECHNOLOGY_4_FUSION_PROPULSION,			     1250000,  4,  3, TECHNOLOGY_3_BATTLE_LINE, TECHNOLOGY_2_BOMBARDMENT),
		new TechnologyData(TECHNOLOGY_4_ELECTRONIC_WARFARE,			      700000,  4,  4, TECHNOLOGY_3_MICRO_CHARGES),
		new TechnologyData(TECHNOLOGY_4_BATTLE_DRUGS,				      650000,  4,  5, TECHNOLOGY_3_DOCTRINE_VR_70),
		new TechnologyData(TECHNOLOGY_4_ENDOCTRINATION,				      950000,  4,  6, TECHNOLOGY_3_PROPAGANDA_THEORY),
		new TechnologyData(TECHNOLOGY_4_OFFENSIVE_MANEUVERS,		      400000,  4,  7, TECHNOLOGY_3_ADVANCED_SPACEFLIGHT),
		new TechnologyData(TECHNOLOGY_4_KESLEY_STABILISER,			     1500000,  4,  8, TECHNOLOGY_3_MAGNETIC_SHIELD),
		new TechnologyData(TECHNOLOGY_4_IRIDIUM_ALLOY,				      750000,  4,  9, TECHNOLOGY_1_SPACE_LOGISTICS),
		new TechnologyData(TECHNOLOGY_5_FUSION_BATTERY,				     2800000,  5,  3, TECHNOLOGY_4_FUSION_PROPULSION),
		new TechnologyData(TECHNOLOGY_5_UNIFIED_FIELD_THEORY,		     4800000,  5,  4, TECHNOLOGY_4_ELECTRONIC_WARFARE),
		new TechnologyData(TECHNOLOGY_5_TRANSCENDENCE,				     3200000,  5,  6, TECHNOLOGY_4_BATTLE_DRUGS, TECHNOLOGY_4_ENDOCTRINATION),
		new TechnologyData(TECHNOLOGY_5_MATTER_ALTERATION,			     6000000,  5,  0, TECHNOLOGY_4_CHAOS_THEORY),
		new TechnologyData(TECHNOLOGY_5_CHAOS_GENERATOR,			     3600000,  5,  1, TECHNOLOGY_4_CHAOS_THEORY),
		new TechnologyData(TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION,		     7600000,  5,  8, TECHNOLOGY_4_KESLEY_STABILISER, TECHNOLOGY_4_IRIDIUM_ALLOY),
		new TechnologyData(TECHNOLOGY_5_MAGNETODYNAMICS,		     	 8500000,  5,  2, TECHNOLOGY_4_FUSION_PROPULSION),
		new TechnologyData(TECHNOLOGY_6_DYNAMIC_REALITY,		     	24000000,  6,  2, TECHNOLOGY_5_MAGNETODYNAMICS, TECHNOLOGY_5_CHAOS_GENERATOR),
		new TechnologyData(TECHNOLOGY_6_PHASE_TRANSFER,		     		42000000,  6,  6, TECHNOLOGY_5_TRANSCENDENCE),
		new TechnologyData(TECHNOLOGY_6_PARTICLE_PROJECTION,		    56000000,  6,  4, TECHNOLOGY_5_FUSION_BATTERY, TECHNOLOGY_5_UNIFIED_FIELD_THEORY),
		new TechnologyData(TECHNOLOGY_6_DOGME_MARTIALE,                 34000000,  6,  0, TECHNOLOGY_5_MATTER_ALTERATION), 
		new TechnologyData(TECHNOLOGY_7_DESTRUCTION_SHIPS,              40000000,  7,  1, TECHNOLOGY_6_DOGME_MARTIALE, TECHNOLOGY_6_DYNAMIC_REALITY),	
		new TechnologyData(TECHNOLOGY_8_CAPITAL_SHIPS,			       320000000,  8,  8, TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION),
		
		
		// Technologies civiles
		new TechnologyData(TECHNOLOGY_0_QUANTUM_PHYSICS,     			     200,  0, 13),
		new TechnologyData(TECHNOLOGY_1_QUANTUM_COMPUTER,     			    3500,  1, 14, TECHNOLOGY_0_QUANTUM_PHYSICS),
		new TechnologyData(TECHNOLOGY_1_MECANIZED_WORKFORCE,     		    6000,  1, 15),
		new TechnologyData(TECHNOLOGY_1_SUPER_STRUCTURES,     			    2000,  1, 17),
		new TechnologyData(TECHNOLOGY_2_EXOPLANETARY_ECONOMICS,     	   28000,  2, 13),
		new TechnologyData(TECHNOLOGY_2_BIOMECHANICS,     			       22000,  2, 15, TECHNOLOGY_1_MECANIZED_WORKFORCE),
		new TechnologyData(TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS,     	   18000,  2, 16),
		new TechnologyData(TECHNOLOGY_2_ORBITAL_ENGINEERING,     		   15000,  2, 18, TECHNOLOGY_1_SUPER_STRUCTURES),
		new TechnologyData(TECHNOLOGY_3_APPLIED_RELATIVITY,     		   80000,  3, 12, TECHNOLOGY_0_QUANTUM_PHYSICS),
		new TechnologyData(TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE,   	      120000,  3, 14, TECHNOLOGY_1_QUANTUM_COMPUTER),
		new TechnologyData(TECHNOLOGY_3_NANITES,     				      200000,  3, 15, TECHNOLOGY_2_BIOMECHANICS),
		new TechnologyData(TECHNOLOGY_3_PLANETARY_CONSTRUCTION,            60000,  3, 17, TECHNOLOGY_1_SUPER_STRUCTURES),
		new TechnologyData(TECHNOLOGY_4_EXONOMETRICS_THEORY,     	     2200000,  4, 13, TECHNOLOGY_2_EXOPLANETARY_ECONOMICS),
		new TechnologyData(TECHNOLOGY_4_QUANTUM_ENGINEERING,     	     1800000,  4, 15, TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE, TECHNOLOGY_3_NANITES),
		new TechnologyData(TECHNOLOGY_4_INTELLIGENT_MATERIALS,  	     1200000,  4, 16, TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS),
		new TechnologyData(TECHNOLOGY_4_SPACE_ENGINEERING,     		     1000000,  4, 18, TECHNOLOGY_2_ORBITAL_ENGINEERING),
		new TechnologyData(TECHNOLOGY_5_UNIFIED_PHYSICS,     		     5000000,  5, 12, TECHNOLOGY_3_APPLIED_RELATIVITY),
		new TechnologyData(TECHNOLOGY_5_INTELLIGENT_MACHINES,   	    10000000,  5, 16, TECHNOLOGY_4_QUANTUM_ENGINEERING, TECHNOLOGY_4_INTELLIGENT_MATERIALS),
		new TechnologyData(TECHNOLOGY_5_ARCOLOGY,     				     4000000,  5, 17, TECHNOLOGY_3_PLANETARY_CONSTRUCTION),
		new TechnologyData(TECHNOLOGY_6_HARMONIZED_EXONOMETRICS,        50000000,  6, 14, TECHNOLOGY_4_EXONOMETRICS_THEORY, TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE),
		new TechnologyData(TECHNOLOGY_6_WEATHER_CONTROL,     		    40000000,  6, 17, TECHNOLOGY_5_INTELLIGENT_MACHINES, TECHNOLOGY_5_ARCOLOGY),
		new TechnologyData(TECHNOLOGY_6_MAGELLAN_PROGRAM,     		    30000000,  6, 18, TECHNOLOGY_4_SPACE_ENGINEERING),
		new TechnologyData(TECHNOLOGY_7_ULTIMATE_BUILDINGS,             60000000,  7, 16, TECHNOLOGY_5_INTELLIGENT_MACHINES, TECHNOLOGY_6_WEATHER_CONTROL),
		// Techonlogies pour les modules
//		new TechnologyData(TECHNOLOGY_0_HULL_SILO,     		    100,  0, 17),
//		new TechnologyData(TECHNOLOGY_1_HULL_SILO,     		    1000,  1, 17, TECHNOLOGY_0_HULL_SILO),
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static TechnologyData[] technologiesById;
	
	static {
		technologiesById = new TechnologyData[500];
		
		for (TechnologyData technology : TECHNOLOGIES)
			technologiesById[technology.getId()] = technology;
	}
	
	private int id;
	private long length;
	private int[] requirements;
	private String name;
	private int x, y;
	private String toolTip;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public TechnologyData(int id, long length, int x, int y, int... requirements) {
		this.id = id;
		this.length = length;
		this.requirements = requirements;
		this.x = x;
		this.y = y;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}

	public long getLength() {
		return length;
	}
	
	public String getName() {
		if (name == null) {
			DynamicMessages dynamicMessages =
				(DynamicMessages) GWT.create(DynamicMessages.class);
			
			name = dynamicMessages.getString("research" + getId());
		}
		
		return name;
	}
	
	public int getX() {
		return x * 200 + 20;
	}

	public int getY() {
		return y * 50 + 25;
	}

	public int[] getRequirements() {
		return requirements;
	}

	public String getToolTip() {
		if (toolTip == null) {
			StaticMessages staticMessages =
				(StaticMessages) GWT.create(StaticMessages.class);
			DynamicMessages dynamicMessages =
				(DynamicMessages) GWT.create(DynamicMessages.class);
			
			String unlock = ""; //$NON-NLS-1$
			
			// Vaisseaux débloqués
			for (int i = 0; i < ShipData.SHIPS.length; i++) {
				ShipData ship = ShipData.SHIPS[i];
				
				if (ship == null || ship.isAltered())
					continue;
				
				int[] requirements = ship.getTechnologies();
				
				for (int requirement : requirements) {
					if (requirement == id) {
						unlock += "<div><span style=\"color: red;\">[" + //$NON-NLS-1$
							dynamicMessages.getString("shipClass" + //$NON-NLS-1$
							ship.getShipClass()) + "]</span> " + //$NON-NLS-1$
							dynamicMessages.getString("ship" + i) + //$NON-NLS-1$
							(requirements.length > 1 ? " (1/" + //$NON-NLS-1$
							requirements.length + ")" : "") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}
			
			// Capacités débloquées
			for (int i = 0; i < ShipData.SHIPS.length; i++) {
				ShipData ship = ShipData.SHIPS[i];
				
				if (ship == null || ship.isAltered())
					continue;
				
				abilities:for (int j = 0; j < ship.getAbilities().length; j++) {
					AbilityData ability = ship.getAbilities()[j];
					for (int k = 0; k < j; k++)
						if (ability.getType() == ship.getAbilities()[k].getType())
							continue abilities;
						
					int[] requirements = ability.getRequirements();
					
					for (int requirement : requirements) {
						if (requirement == id) {
							unlock += "<div><span style=\"color: #3fbcff;\">[" + //$NON-NLS-1$
								staticMessages.ability() + "]</span> " + //$NON-NLS-1$
								dynamicMessages.getString("ability" + //$NON-NLS-1$
								ability.getType()) + " (" + //$NON-NLS-1$
								dynamicMessages.getString("ship" + i) + ")" + //$NON-NLS-1$ //$NON-NLS-2$
								(requirements.length > 1 ? " (1/" + //$NON-NLS-1$
								requirements.length + ")" : "") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
			}
			
			// Constructions débloquées
			for (int i = 0; i < BuildingData.BUILDINGS.length; i++) {
				for (int j = 0; j < 5; j++) {
					int[] requirements = BuildingData.getRequiredTechnologies(
							BuildingData.BUILDINGS[i], j);
					
					for (int requirement : requirements) {
						if (requirement == id) {
							unlock += "<div><span style=\"color: #00c000;\">[" + //$NON-NLS-1$
								staticMessages.building() + "]</span> " + //$NON-NLS-1$
								BuildingData.getName(BuildingData.BUILDINGS[i], j) +
								(requirements.length > 1 ? " (1/" + //$NON-NLS-1$
								requirements.length + ")" : "") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
			}
			
			// Technologies débloquées
			for (TechnologyData technology : TechnologyData.TECHNOLOGIES) {
				int[] requirements = technology.getRequirements();
				
				for (int requirement : requirements) {
					if (requirement == id) {
						unlock += "<div><span style=\"color: #ffc000;\">[" + //$NON-NLS-1$
							staticMessages.technology() + "]</span> " + //$NON-NLS-1$
							dynamicMessages.getString("research" + technology.getId()) +
							(requirements.length > 1 ?
							" (1/" + requirements.length + ")" : "") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}
			}
			
			toolTip = "<div class=\"title\">" + getName() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div>" + Formatter.formatNumber(getLength(), true) + //$NON-NLS-1$
				"<img class=\"resource research\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
				"images/misc/blank.gif\" unselectable=\"on\"/></div>" + //$NON-NLS-1$
				"<div style=\"font-weight: bold;\">" + //$NON-NLS-1$
				staticMessages.researchAllow() + "</div>" + unlock; //$NON-NLS-1$
		}
		
		return toolTip;
	}
	
	public static TechnologyData getTechnologyById(int id) {
		return technologiesById[id];
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
