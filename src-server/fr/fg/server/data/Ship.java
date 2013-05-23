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


import fr.fg.server.data.Ability;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Product;
import fr.fg.server.data.Technology;
import fr.fg.server.data.Weapon;
import fr.fg.server.data.WeaponGroup;

public class Ship {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static int[] CLASSES_POWER = {
		0, 1, 10, 100, 1000, 10000, 100000, 0
	};
	
	// Classes de vaisseaux
	public final static int
		FIGHTER = 1,
		CORVETTE = 2,
		FRIGATE = 3,
		DESTROYER = 4,
		DREADNOUGHT = 5,
		BATTLECRUISER = 6,
		FREIGHTER = 7;
	
	// Vaisseaux par type
	public final static int
		RECON = 1,
		CORSAIR = 2,
		FORTRESS = 3,
		ZERO = 4,
		BLADE = 5,
		GUARDIAN = 6,
		EAGLE = 7,
		RECON_ALT = 11,
		CORSAIR_ALT = 12,
		FORTRESS_ALT =13,
		ZERO_ALT = 14,
		BLADE_ALT = 15,
		GUARDIAN_ALT = 16,
		EAGLE_ALT = 17,
		VALKYRIE = 21,
		HELLCAT = 22,
		SPECTRE = 23,
		RAPTOR = 24,
		SENTINEL = 25,
		BANSHEE = 26,
		VALKYRIE_ALT = 31,
		HELLCAT_ALT = 32,
		SPECTRE_ALT = 33,
		RAPTOR_ALT = 34,
		SENTINEL_ALT = 35,
		BANSHEE_ALT = 36,
		PALADIN = 41,
		FURY = 42,
		LIBERTY = 43,
		STYX = 44,
		SERAPHIM = 45,
		SCORPION = 46,
		HADES = 47,
		JUDANIMATOR = 48,
		PALADIN_ALT = 51,
		FURY_ALT = 52,
		LIBERTY_ALT = 53,
		STYX_ALT = 54,
		SERAPHIM_ALT = 55,
		SCORPION_ALT = 56,
		TEMPLAR = 61,
		OBLIVION = 62,
		HURRICANE = 63,
		TEMPLAR_ALT = 71,
		TITAN			= 81,
		PROFECY = 82,
		MULE = 121,
		MAMMOTH = 122,
		ORION = 123;
	
	
	// Liste de tous les vaisseaux du jeu
	public final static Ship[] SHIPS;
	
	static {
		SHIPS = new Ship[140];
		SHIPS[RECON]		= new Ship(0,			FIGHTER,		  16, 0, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.AUTOCANON, 2)},											new Ability[]{Ability.getFocusFireAbility(2, new int[]{Technology.TECHNOLOGY_0_DOCTRINE_R55}, 1.55), Ability.getCriticalHitAbility(new int[]{Technology.TECHNOLOGY_4_CHAOS_THEORY}, .25, 2)},																																																										new int[]{   50,     0,     0,     0,    30},     0,    60, new int[]{}, 																													new Product[0]);
		SHIPS[CORSAIR]		= new Ship(0,			FIGHTER,		  22, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroup[]{new WeaponGroup(Weapon.LASER, 2)},												new Ability[]{Ability.getActiveLeechAbility(4, new int[]{Technology.TECHNOLOGY_1_BOARDING}, .75), Ability.getBreachAbility(new int[]{Technology.TECHNOLOGY_2_DOCTRINE_C15}, 1.15)},																																																													new int[]{    0,    40,    15,     0,    20},     0,    80, new int[]{Technology.TECHNOLOGY_0_SPACE_WARFARE}, 																				new Product[0]);
		SHIPS[FORTRESS]		= new Ship(0,			FIGHTER,		  22, 0, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.TACTICAL_ROCKET, 1)},										new Ability[]{Ability.getBarrageAbility(3, new int[]{Technology.TECHNOLOGY_2_BOMBARDMENT}, .65, .80, FIGHTER), Ability.getBarrageAbility(3, new int[]{Technology.TECHNOLOGY_2_BOMBARDMENT}, .65, .80, CORVETTE), Ability.getBarrageAbility(3, new int[]{Technology.TECHNOLOGY_2_BOMBARDMENT}, .65, .80, FRIGATE)},																													new int[]{    0,     0,    50,     0,    25},     0,    70, new int[]{Technology.TECHNOLOGY_1_FIRE_CONTOL_SYSTEM}, 																			new Product[0]);
		SHIPS[ZERO]			= new Ship(0,			FIGHTER,		  20, 0, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 1)},											new Ability[]{Ability.getTimeShiftAbility(4, new int[]{Technology.TECHNOLOGY_3_SPACE_TIME_COMPRESSION}, 2), Ability.getResistanceAbility(2, new int[]{Technology.TECHNOLOGY_2_PULSE_REACTOR}, Ship.FIGHTER, .65), Ability.getResistanceAbility(2, new int[]{Technology.TECHNOLOGY_2_PULSE_REACTOR}, Ship.CORVETTE, .65), Ability.getResistanceAbility(2, new int[]{Technology.TECHNOLOGY_2_PULSE_REACTOR}, Ship.FRIGATE, .65)},		new int[]{   25,    25,     0,     0,    25},     0,    70, new int[]{Technology.TECHNOLOGY_1_PULSE_LASER, Technology.TECHNOLOGY_2_PULSE_REACTOR}, 											new Product[0]);
		SHIPS[BLADE]		= new Ship(0,			FIGHTER,		  18, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 1)},											new Ability[]{Ability.getSquadronAbility(new int[]{Technology.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT}, FIGHTER, .5), Ability.getEcmAbility(new int[]{Technology.TECHNOLOGY_4_ELECTRONIC_WARFARE, Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, .25)},																																														new int[]{    0,    30,    20,     0,    25},     0,    65, new int[]{Technology.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT},																		new Product[0]);
		SHIPS[GUARDIAN]		= new Ship(0,			FIGHTER,		  20, 0, new int[]{CORVETTE, FRIGATE},	new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 1)},										new Ability[]{Ability.getMimicryAbility(4, new int[]{Technology.TECHNOLOGY_4_KESLEY_STABILISER}), Ability.getDetonationAbility(new int[]{Technology.TECHNOLOGY_5_TRANSCENDENCE}, .65)},																																																												new int[]{   35,    10,     0,     0,    30},     0,    75, new int[]{Technology.TECHNOLOGY_4_KESLEY_STABILISER}, 																			new Product[0]); //$NON-NLS-1$
		SHIPS[EAGLE]		= new Ship(0,			FIGHTER,		  20, 0, new int[]{DESTROYER},			new WeaponGroup[]{new WeaponGroup(Weapon.PPC, 1), new WeaponGroup(Weapon.PULSE_LASER, 1)},			new Ability[]{Ability.getParticleProjectionAbility(5, new int[]{Technology.TECHNOLOGY_6_PARTICLE_PROJECTION}, 4, 5)},																																																																												new int[]{    0,     0,    25,    25,    30},     0,    90, new int[]{Technology.TECHNOLOGY_6_PARTICLE_PROJECTION}, 																		new Product[0]); //$NON-NLS-1$
																																																																																																																																																																																						
		SHIPS[RECON_ALT]	= new Ship(RECON,		FIGHTER,		  16, 0, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 1)},										new Ability[]{Ability.getFocusFireAbility(2, new int[]{Technology.TECHNOLOGY_0_DOCTRINE_R55}, 1.60), Ability.getTerminatorAbility(new int[]{Technology.TECHNOLOGY_0_DOCTRINE_R55}, 2)},																																																												new int[]{   25,     0,     0,     30,    30},     0,    90, new int[]{}, 																													new Product[]{new Product(Product.PRODUCT_HEPTANIUM, 2), new Product(Product.PRODUCT_IRIDIUM, 2), new Product(Product.PRODUCT_SELENIUM, 2)});
		SHIPS[CORSAIR_ALT]	= new Ship(CORSAIR,		FIGHTER,		  22, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroup[]{new WeaponGroup(Weapon.BLASTER, 2)},												new Ability[]{Ability.getActiveLeechAbility(4, new int[]{Technology.TECHNOLOGY_1_BOARDING}, .80), Ability.getPillageAbility(new int[]{Technology.TECHNOLOGY_1_BOARDING}, .3)},																																																														new int[]{    0,    20,    20,     10,    25},     0,    80, new int[]{Technology.TECHNOLOGY_0_SPACE_WARFARE}, 																				new Product[]{new Product(Product.PRODUCT_NECTAR, 2)}); //$NON-NLS-1$ //$NON-NLS-2$
		SHIPS[BLADE_ALT]	= new Ship(BLADE,		FIGHTER,		  18, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 1)},											new Ability[]{Ability.getSquadronAbility(new int[]{Technology.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT}, FIGHTER, .5), Ability.getEvadeAbility(new int[]{Technology.TECHNOLOGY_4_ELECTRONIC_WARFARE, Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, .1)},																																														new int[]{    0,    15,    10,     25,    25},     0,    75, new int[]{Technology.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT}, 																		new Product[]{new Product(Product.PRODUCT_SELENIUM, 3),new Product(Product.PRODUCT_IRIDIUM, 1),new Product(Product.PRODUCT_HEPTANIUM, 3)}); 
		SHIPS[ZERO_ALT]		= new Ship(ZERO,		FIGHTER,		  22, 0, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 1)},											new Ability[]{Ability.getTimeShiftAbility(4, new int[]{Technology.TECHNOLOGY_3_SPACE_TIME_COMPRESSION}, 2), Ability.getAllResistanceAbility(5, new int[]{Technology.TECHNOLOGY_2_PULSE_REACTOR}, 0.35, 3)},																																																							new int[]{   13,    13,     0,     24,    25},     0,    70, new int[]{Technology.TECHNOLOGY_1_PULSE_LASER, Technology.TECHNOLOGY_2_PULSE_REACTOR}, 										new Product[]{new Product(Product.PRODUCT_NECTAR, 2), new Product(Product.PRODUCT_HEPTANIUM, 1), new Product(Product.PRODUCT_SELENIUM, 1)}); //$NON-NLS-1$
		SHIPS[GUARDIAN_ALT]	= new Ship(GUARDIAN,	FIGHTER,		  22, 0, new int[]{CORVETTE, FRIGATE},	new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 1)},										new Ability[]{Ability.getMimicryAbility(4, new int[]{Technology.TECHNOLOGY_4_KESLEY_STABILISER}), Ability.getCanaliseAbility(new int[]{Technology.TECHNOLOGY_5_TRANSCENDENCE}, 1, 0.2)},																																																											new int[]{   15,    10,     0,     20,    30},     0,    80, new int[]{Technology.TECHNOLOGY_4_KESLEY_STABILISER},																			new Product[]{new Product(Product.PRODUCT_SELENIUM, 3), new Product(Product.PRODUCT_NECTAR, 3), new Product(Product.PRODUCT_IRIDIUM, 2),new Product(Product.PRODUCT_ANTILIUM, 2)});
		SHIPS[EAGLE_ALT]	= new Ship(EAGLE,		FIGHTER,		  22, 0, new int[]{DESTROYER},			new WeaponGroup[]{new WeaponGroup(Weapon.PPC, 1), new WeaponGroup(Weapon.PULSE_LASER, 1)},			new Ability[]{Ability.getParticleProjectionAbility(5, new int[]{Technology.TECHNOLOGY_6_PARTICLE_PROJECTION}, 4, 5),Ability.getAnticipationAbility(new int[]{}, DESTROYER, .15)},																																																													new int[]{    0,     0,     0,     50,    30},     0,   110, new int[]{Technology.TECHNOLOGY_6_PARTICLE_PROJECTION}, 																		new Product[]{new Product(Product.PRODUCT_SELENIUM, 7),new Product(Product.PRODUCT_SULFARIDE, 4),new Product(Product.PRODUCT_ANTILIUM, 4)});
		SHIPS[FORTRESS_ALT]	= new Ship(FORTRESS,	FIGHTER,		  23, 0, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.TACTICAL_ROCKET, 1)},										new Ability[]{Ability.getBarrageV2Ability(3, new int[]{}, .75, .85),Ability.getBarrageV2Ability(5, new int[]{}, .65, .80)},																																																																											new int[]{    0,     0,    35,     15,    25},     0,    70, new int[]{Technology.TECHNOLOGY_1_FIRE_CONTOL_SYSTEM}, 																		new Product[]{new Product(Product.PRODUCT_HEPTANIUM, 1), new Product(Product.PRODUCT_NECTAR, 1)});
																																																																																																																																																																																									
		SHIPS[VALKYRIE]		= new Ship(0,			CORVETTE,		 190, 1, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.LASER, 12), new WeaponGroup(Weapon.PULSE_LASER, 4)},		new Ability[]{Ability.getRepairAbility(3, new int[]{Technology.TECHNOLOGY_2_DRONES}, .15), Ability.getRapidFireAbility(5, new int[]{Technology.TECHNOLOGY_3_DOCTRINE_VR_70}, .65)},																																																													new int[]{  300,     0,   200,     0,   260},     0,   800, new int[]{Technology.TECHNOLOGY_1_PULSE_LASER}, 																				new Product[0]);
		SHIPS[HELLCAT]		= new Ship(0,			CORVETTE,		 190, 1, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.TACTICAL_ROCKET, 8)},										new Ability[]{Ability.getFragChargeAbility(4, new int[]{Technology.TECHNOLOGY_3_MICRO_CHARGES, Technology.TECHNOLOGY_3_BATTLE_LINE}, 1.2), Ability.getHeatAbility(new int[]{Technology.TECHNOLOGY_3_MICRO_CHARGES}, -1)},																																																			new int[]{    0,   280,   240,     0,   250},     0,  1000, new int[]{Technology.TECHNOLOGY_2_SELF_DEFENSE_SYSTEM}, 																		new Product[0]);
		SHIPS[SPECTRE]		= new Ship(0,			CORVETTE,		 165, 1, new int[]{FRIGATE, DESTROYER},	new WeaponGroup[]{new WeaponGroup(Weapon.GAUSS, 2)},												new Ability[]{Ability.getDodgeAbility(new int[]{Technology.TECHNOLOGY_5_UNIFIED_FIELD_THEORY}, .25), Ability.getHarassmentAbility(new int[]{Technology.TECHNOLOGY_3_GAUSS_CANON}, .85)},																																																											new int[]{  160,   160,   160,     0,   270},     0,  1000, new int[]{Technology.TECHNOLOGY_3_GAUSS_CANON}, 																				new Product[0]);
		SHIPS[RAPTOR]		= new Ship(0,			CORVETTE, 		 180, 1, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 10)},											new Ability[]{Ability.getDevastateAbility(6, new int[]{Technology.TECHNOLOGY_3_DOCTRINE_VR_70}, .25), Ability.getCriticalHitAbility(new int[]{Technology.TECHNOLOGY_4_CHAOS_THEORY}, .15, 3)},																																																										new int[]{  160,   340,     0,     0,   270},     0,  1000, new int[]{Technology.TECHNOLOGY_2_HEPTANIUM_PROPULSION}, 																		new Product[0]);
		SHIPS[SENTINEL]		= new Ship(0,			CORVETTE, 		 210, 1, new int[]{},					new WeaponGroup[0],																					new Ability[]{Ability.getDeflectorAbility(4, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, FIGHTER, 1), Ability.getDeflectorAbility(4, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, CORVETTE, 1), Ability.getInhibitorFieldAbility(3, new int[]{Technology.TECHNOLOGY_5_UNIFIED_FIELD_THEORY, Technology.TECHNOLOGY_4_KESLEY_STABILISER})},																			new int[]{  500,     0,     0,     0,   250},     0,  1100, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, 																			new Product[0]);
		SHIPS[BANSHEE]		= new Ship(0,			CORVETTE, 		 170, 1, new int[]{FIGHTER, CORVETTE},	new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 9)},										new Ability[]{Ability.getImmaterialAbility(new int[]{Technology.TECHNOLOGY_5_MATTER_ALTERATION, Technology.TECHNOLOGY_5_CHAOS_GENERATOR}, .5, 1.25), Ability.getConfusionAbility(2, new int[]{}, true), Ability.getConfusionAbility(4, new int[]{}, false)},																																										new int[]{    0,   300,   180,     0,   260},     0,  1200, new int[]{Technology.TECHNOLOGY_5_MATTER_ALTERATION}, 																			new Product[0]);
		
		SHIPS[RAPTOR_ALT]	= new Ship(RAPTOR,		CORVETTE, 		 180, 1, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 10)},											new Ability[]{Ability.getOverheatingAbility(6, new int[]{Technology.TECHNOLOGY_3_DOCTRINE_VR_70}, 1.4, .75), Ability.getCriticalHitV2Ability(new int[]{Technology.TECHNOLOGY_4_CHAOS_THEORY}, .15, .10, 2, 3)},																																																						new int[]{   80,    120,     0,     300,   270},     0,  1300, new int[]{Technology.TECHNOLOGY_2_HEPTANIUM_PROPULSION}, 																		new Product[]{new Product(Product.PRODUCT_SELENIUM, 1), new Product(Product.PRODUCT_IRIDIUM, 2),new Product(Product.PRODUCT_ANTILIUM, 1)});
		SHIPS[SENTINEL_ALT]	= new Ship(SENTINEL,	CORVETTE, 		 220, 1, new int[]{},					new WeaponGroup[0],																					new Ability[]{Ability.getDeflectorAbility(4, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, FIGHTER, 1), Ability.getDeflectorAbility(4, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, CORVETTE, 1),Ability.getDeflectorAbility(7, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, FRIGATE, 2),Ability.getDeflectorAbility(7, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, DESTROYER, 2)},						new int[]{  250,     0,     0,     250,   250},     0,  1300, new int[]{Technology.TECHNOLOGY_3_MAGNETIC_SHIELD}, 																			new Product[]{new Product(Product.PRODUCT_SELENIUM, 2), new Product(Product.PRODUCT_SULFARIDE, 2)});
		SHIPS[HELLCAT_ALT]	= new Ship(HELLCAT,		CORVETTE,		 205, 1, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.TACTICAL_ROCKET, 8)},										new Ability[]{Ability.getProximityChargeAbility(4, new int[]{Technology.TECHNOLOGY_3_MICRO_CHARGES, Technology.TECHNOLOGY_3_BATTLE_LINE}, .2), Ability.getHeatAbility(new int[]{Technology.TECHNOLOGY_3_MICRO_CHARGES}, -1)},																																																		new int[]{    0,   200,   160,     180,   250},     0,  1200, new int[]{Technology.TECHNOLOGY_2_SELF_DEFENSE_SYSTEM}, 																		new Product[]{new Product(Product.PRODUCT_ANTILIUM, 1), new Product(Product.PRODUCT_HEPTANIUM, 2), new Product(Product.PRODUCT_NECTAR, 1) });
		SHIPS[VALKYRIE_ALT]	= new Ship(VALKYRIE,	CORVETTE,		 200, 1, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 12)},											new Ability[]{Ability.getRecupAbility(5, new int[]{Technology.TECHNOLOGY_2_DRONES}, .15), Ability.getRapidFireV2Ability(6, new int[]{Technology.TECHNOLOGY_3_DOCTRINE_VR_70}, .45, true)},																																																											new int[]{  200,     0,   100,     200,   260},     0,  1200, new int[]{Technology.TECHNOLOGY_1_PULSE_LASER}, 																				new Product[]{new Product(Product.PRODUCT_NECTAR, 1), new Product(Product.PRODUCT_HEPTANIUM, 1), new Product(Product.PRODUCT_IRIDIUM, 1)});
		SHIPS[SPECTRE_ALT]	= new Ship(SPECTRE,		CORVETTE,		 175, 1, new int[]{FRIGATE, DESTROYER},	new WeaponGroup[]{new WeaponGroup(Weapon.GAUSS, 2)},												new Ability[]{Ability.getDodgeV2Ability(new int[]{Technology.TECHNOLOGY_5_UNIFIED_FIELD_THEORY}, .20, .35), Ability.getHarassmentAbility(new int[]{Technology.TECHNOLOGY_3_GAUSS_CANON}, .80)},																																																										new int[]{   80,    80,    80,     240,   270},     0,  1200, new int[]{Technology.TECHNOLOGY_3_GAUSS_CANON}, 																				new Product[]{new Product(Product.PRODUCT_NECTAR, 2),new Product(Product.PRODUCT_IRIDIUM, 2),new Product(Product.PRODUCT_ANTILIUM, 3)});
		SHIPS[BANSHEE_ALT]	= new Ship(BANSHEE,		CORVETTE, 		 180, 1, new int[]{FIGHTER, CORVETTE},	new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 9)},										new Ability[]{Ability.getImmaterialAbility(new int[]{Technology.TECHNOLOGY_5_MATTER_ALTERATION, Technology.TECHNOLOGY_5_CHAOS_GENERATOR}, .65, 1.1), Ability.getWeaknessAbility(new int[]{}, 0.8)},																																																									new int[]{    0,   150,    90,     240,   260},     0,  1350, new int[]{Technology.TECHNOLOGY_5_MATTER_ALTERATION}, 																				new Product[]{new Product(Product.PRODUCT_SELENIUM, 4), new Product(Product.PRODUCT_NECTAR, 3),new Product(Product.PRODUCT_SULFARIDE, 1),new Product(Product.PRODUCT_HEPTANIUM, 5)});
		
		
		SHIPS[PALADIN]		= new Ship(0,			FRIGATE,		1760, 3, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.AUTOCANON, 160)},											new Ability[]{Ability.getTauntAbility(3, new int[]{Technology.TECHNOLOGY_5_TRANSCENDENCE}, 2, 2), Ability.getDamageReturnAuraAbility(4, new int[]{Technology.TECHNOLOGY_3_PROPAGANDA_THEORY}, .1)},																																																									new int[]{    0,  2200,  2800,     0,  2600},     0,  4000, new int[]{Technology.TECHNOLOGY_3_PROPAGANDA_THEORY}, 																			new Product[0]);
		SHIPS[FURY]			= new Ship(0,			FRIGATE,		1300, 2, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 100)},										new Ability[]{Ability.getRageAbility(5, new int[]{Technology.TECHNOLOGY_4_BATTLE_DRUGS}, -2),	Ability.getOutflankingAbility(new int[]{Technology.TECHNOLOGY_4_OFFENSIVE_MANEUVERS}, .3)},																																																											new int[]{  800,  2400,  1600,     0,  2500},     0,  5000, new int[]{Technology.TECHNOLOGY_3_BATTLE_LINE}, 																				new Product[0]);
		SHIPS[LIBERTY]		= new Ship(0,			FRIGATE,		1500, 2, new int[]{FRIGATE, DESTROYER},	new WeaponGroup[]{new WeaponGroup(Weapon.TACTICAL_ROCKET, 80)},										new Ability[]{Ability.getSublimationAbility(new int[]{Technology.TECHNOLOGY_5_FUSION_BATTERY}, .35), Ability.getFusionAbility(5, new int[]{Technology.TECHNOLOGY_5_FUSION_BATTERY, Technology.TECHNOLOGY_4_KESLEY_STABILISER}, 2)},																																																	new int[]{  600,  2000,  2400,     0,  2550},     0,  5400, new int[]{Technology.TECHNOLOGY_4_FUSION_PROPULSION}, 																			new Product[0]);
		SHIPS[STYX]			= new Ship(0,			FRIGATE,		1600, 2, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.INFERNO, 24)},												new Ability[]{Ability.getRetributionAbility(4, new int[]{Technology.TECHNOLOGY_4_ENDOCTRINATION}, 2, .4), Ability.getSacrificeAbility(5, new int[]{Technology.TECHNOLOGY_5_TRANSCENDENCE}, -1, -3, -2)},																																																							new int[]{ 4800,     0,     0,     0,  2450},     0,  6400, new int[]{Technology.TECHNOLOGY_4_ENDOCTRINATION}, 																				new Product[0]);
		SHIPS[SERAPHIM]		= new Ship(0,			FRIGATE,		1500, 2, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.GAUSS, 12), new WeaponGroup(Weapon.LASER, 64)},			new Ability[]{Ability.getPhaseAbility(new int[]{Technology.TECHNOLOGY_4_ELECTRONIC_WARFARE}), Ability.getRiftAbility(5, new int[]{Technology.TECHNOLOGY_4_SDI})},																																																																	new int[]{ 3000,  2200,     0,     0,  2250},     0,  5000, new int[]{Technology.TECHNOLOGY_4_SDI}, 																						new Product[0]);
		SHIPS[SCORPION]		= new Ship(0,			FRIGATE,		1450, 3, new int[]{DESTROYER},			new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 64)},										new Ability[]{Ability.getOverloadAbility(new int[]{Technology.TECHNOLOGY_5_CHAOS_GENERATOR}, 4, -3), Ability.getQuantumErrorAbility(new int[]{Technology.TECHNOLOGY_5_CHAOS_GENERATOR}, 3, 1.5)},																																																									new int[]{ 1800,     0,  3200,     0,  2400},     0,  6500, new int[]{Technology.TECHNOLOGY_4_CHAOS_THEORY, Technology.TECHNOLOGY_4_FUSION_PROPULSION}, 									new Product[0]);
		SHIPS[HADES]        = new Ship(0,           FRIGATE,        1600, 3, new int[]{DREADNOUGHT},        new WeaponGroup[]{new WeaponGroup(Weapon.SONIC_TORPEDO, 80)},                                       new Ability[]{Ability.getFiercenessAbility(5, new int[]{Technology.TECHNOLOGY_6_DOGME_MARTIALE}, Ship.DREADNOUGHT, 1.25)},                                                                                                                                                                                                                                                                                                                               new int[]{ 1800,     0,  2500,     0,  2500},     0,  5500,    new int[]{Technology.TECHNOLOGY_6_DOGME_MARTIALE},                                                                              new Product[0]);			
		SHIPS[JUDANIMATOR]    = new Ship(0,           FRIGATE,        1800, 3, new int[]{FRIGATE, DESTROYER}, new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 120)},                                      new Ability[0],                                                                                                                                                                                                                                                                                                                                                                                                                   new int[]{ 2500,  1700,  3000,     0,  2700},     0,  6000,    new int[]{Technology.TECHNOLOGY_7_DESTRUCTION_SHIPS},                                                                           new Product[0]);
		//SHIPS[PALADIN_ALT]	= new Ship(PALADIN,		FRIGATE,		2150, 3, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.AUTOCANON, 200)},											new Ability[]{Ability.getTauntAbility(3, new int[]{}, 2, 2), Ability.getDamageReturnAuraAbility(4, new int[]{}, .15)},																																																																												new int[]{    0,  2200,  2800,     0,  2500},     0,  4000, new int[]{}, new Product[]{new Product(Product.PRODUCT_UNREACHABLE, 1)});
		SHIPS[FURY_ALT]		= new Ship(FURY,		FRIGATE,		1350, 2, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 100)},										new Ability[]{Ability.getRageAbility(5, new int[]{Technology.TECHNOLOGY_4_BATTLE_DRUGS}, -1),	Ability.getCollateralAbility(new int[]{}, 2)},																																																																						new int[]{  400,  1200,   800,  2400,  2500},     0,  6000, new int[]{Technology.TECHNOLOGY_3_BATTLE_LINE}, 																				new Product[]{new Product(Product.PRODUCT_SELENIUM, 2), new Product(Product.PRODUCT_NECTAR, 2),new Product(Product.PRODUCT_HEPTANIUM, 1),new Product(Product.PRODUCT_ANTILIUM, 1)});
		SHIPS[SERAPHIM_ALT]	= new Ship(SERAPHIM,	FRIGATE,		1650, 2, new int[]{CORVETTE},			new WeaponGroup[]{new WeaponGroup(Weapon.GAUSS, 12), new WeaponGroup(Weapon.LASER, 64)},			new Ability[]{Ability.getRiftAbility(5, new int[]{Technology.TECHNOLOGY_4_SDI})},																																																																																					new int[]{ 1000,   800,     0,  3400,  2250},  5000,  7000, new int[]{Technology.TECHNOLOGY_4_SDI}, 																						new Product[]{new Product(Product.PRODUCT_SELENIUM, 2), new Product(Product.PRODUCT_NECTAR, 2),new Product(Product.PRODUCT_IRIDIUM, 2),new Product(Product.PRODUCT_HEPTANIUM, 2),new Product(Product.PRODUCT_ANTILIUM, 2)});
		SHIPS[LIBERTY_ALT]	= new Ship(LIBERTY,		FRIGATE,		1650, 2, new int[]{FRIGATE, DESTROYER},	new WeaponGroup[]{new WeaponGroup(Weapon.TACTICAL_ROCKET, 80)},										new Ability[]{Ability.getSublimationV2Ability(new int[]{Technology.TECHNOLOGY_5_FUSION_BATTERY}, .30, .30, .50, 1.0), Ability.getAbsorbtionAbility(new int[]{Technology.TECHNOLOGY_5_FUSION_BATTERY, Technology.TECHNOLOGY_4_KESLEY_STABILISER}, 0.1)},																																												new int[]{  200,   800,   900,  3000,  2550},     0,  6800, new int[]{Technology.TECHNOLOGY_4_FUSION_PROPULSION}, 																			new Product[]{new Product(Product.PRODUCT_SELENIUM, 3), new Product(Product.PRODUCT_NECTAR, 3),new Product(Product.PRODUCT_HEPTANIUM, 4)});
		SHIPS[STYX_ALT]		= new Ship(STYX,		FRIGATE,		1800, 2, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.INFERNO, 24)},												new Ability[]{Ability.getDamageRetunPassifAbility(new int[]{Technology.TECHNOLOGY_4_ENDOCTRINATION}, .15), Ability.getSacrificeAbility(4, new int[]{Technology.TECHNOLOGY_5_TRANSCENDENCE}, -1, -3, -2)},																																																							new int[]{ 2400,     0,     0,  2400,  2450},     0,  7400, new int[]{Technology.TECHNOLOGY_4_ENDOCTRINATION}, 																				new Product[]{new Product(Product.PRODUCT_SELENIUM, 2), new Product(Product.PRODUCT_IRIDIUM, 2),new Product(Product.PRODUCT_HEPTANIUM, 4),new Product(Product.PRODUCT_ANTILIUM, 2)}); 
		SHIPS[SCORPION_ALT]	= new Ship(SCORPION,	FRIGATE,		1550, 3, new int[]{DESTROYER},			new WeaponGroup[]{new WeaponGroup(Weapon.TWIN_AUTOCANON, 64)},										new Ability[]{Ability.getOverloadAbility(new int[]{Technology.TECHNOLOGY_5_CHAOS_GENERATOR}, 4, -2), Ability.getQuantumErrorAbility(new int[]{Technology.TECHNOLOGY_5_CHAOS_GENERATOR}, 3, 1.5), Ability.getIncoherenceAbility(new int[]{}, .35, 1)},																																												new int[]{  900,     0,  1600,  2500,  2400},     0,  7500, new int[]{Technology.TECHNOLOGY_4_CHAOS_THEORY, Technology.TECHNOLOGY_4_FUSION_PROPULSION}, 									new Product[]{new Product(Product.PRODUCT_SELENIUM, 3), new Product(Product.PRODUCT_IRIDIUM, 3),new Product(Product.PRODUCT_HEPTANIUM, 2),new Product(Product.PRODUCT_ANTILIUM, 3)});
		SHIPS[PALADIN_ALT]	= new Ship(PALADIN,		FRIGATE,		1900, 0, new int[]{FIGHTER},			new WeaponGroup[]{new WeaponGroup(Weapon.AUTOCANON, 160)},											new Ability[]{Ability.getTauntAbility(3, new int[]{Technology.TECHNOLOGY_5_TRANSCENDENCE}, 2, 1), Ability.getDamageRepartitionAbility(5, new int[]{}, .1)},																																																																			new int[]{    0,  1300,  1700,  2000,  2600},     0,  6000, new int[]{Technology.TECHNOLOGY_3_PROPAGANDA_THEORY}, 																			new Product[]{new Product(Product.PRODUCT_SELENIUM, 2), new Product(Product.PRODUCT_HEPTANIUM, 2),new Product(Product.PRODUCT_ANTILIUM, 1)});
																																																																																																																																																																																					
		SHIPS[TEMPLAR]		= new Ship(0,			DESTROYER,	   14000, 4, new int[]{},					new WeaponGroup[]{},																				new Ability[]{Ability.getForceFieldAbility(8, new int[]{Technology.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 1, 2), Ability.getRepairAbility(4, new int[]{}, .2)},																																																																		new int[]{    0, 19200, 32000,     0, 25000},     0, 40000, new int[]{Technology.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 																		new Product[0]);
		SHIPS[OBLIVION]		= new Ship(0,			DESTROYER,	   14500, 3, new int[]{FRIGATE},			new WeaponGroup[]{new WeaponGroup(Weapon.SONIC_TORPEDO, 510)},										new Ability[]{Ability.getLotteryAbility(3, new int[]{Technology.TECHNOLOGY_6_DYNAMIC_REALITY}), Ability.getIncohesionAbility(new int[]{Technology.TECHNOLOGY_5_MAGNETODYNAMICS}, -1, 1.3, 2, .7)},																																																									new int[]{40000,  8000,     0,     0, 26000},	  0, 50000, new int[]{Technology.TECHNOLOGY_5_MAGNETODYNAMICS}, 																			new Product[0]);
		SHIPS[HURRICANE]	= new Ship(0,			DESTROYER,	   13000, 3, new int[]{CORVETTE, FRIGATE},	new WeaponGroup[]{new WeaponGroup(Weapon.PULSE_LASER, 500), new WeaponGroup(Weapon.INFERNO, 90)},	new Ability[]{Ability.getHullEnergyTransferAbility(1, new int[]{Technology.TECHNOLOGY_6_PHASE_TRANSFER}, 1.15, 0.75), Ability.getDamageEnergyTransferAbility(1, new int[]{Technology.TECHNOLOGY_6_PHASE_TRANSFER}, 0.9, 1.3)},																																																		new int[]{    0,     0, 54000,     0, 24000},	  0, 48000, new int[]{Technology.TECHNOLOGY_6_PHASE_TRANSFER}, 																				new Product[0]);
		
		SHIPS[TEMPLAR_ALT]	= new Ship(TEMPLAR,		DESTROYER,	   15000, 4, new int[]{},					new WeaponGroup[]{},																				new Ability[]{Ability.getForceFieldAbility(7, new int[]{Technology.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 1, 2), Ability.getRepercuteAbility(6, new int[]{}, .25)},																																																																	new int[]{    0,  4200,  7000, 40000, 25000},     0, 55000, new int[]{Technology.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 																		new Product[]{new Product(Product.PRODUCT_SELENIUM, 5), new Product(Product.PRODUCT_NECTAR, 5),new Product(Product.PRODUCT_SULFARIDE, 3)});
		
		SHIPS[TITAN]		= new Ship(0,			DREADNOUGHT,  120000, 4, new int[]{FIGHTER, DESTROYER},	new WeaponGroup[]{new WeaponGroup(Weapon.ION_CANON, 1600), new WeaponGroup(Weapon.TACTICAL_ROCKET, 3600)}, new Ability[]{Ability.getArtilleryShotAbility(3, new int[]{Technology.TECHNOLOGY_8_CAPITAL_SHIPS}, .25)},																																																																													new int[]{100000,    0,     0,     0, 50000}, 250000, 500000, new int[]{Technology.TECHNOLOGY_8_CAPITAL_SHIPS},																				new Product[0]);
		
		
		SHIPS[MULE]			= new Ship(0,			FREIGHTER,		  50, 0, new int[]{},					new WeaponGroup[0],																					new Ability[0],																																																																																																						new int[]{    0,     0,   150,     0,    75},   500,   500, new int[]{}, 																													new Product[0]);
		SHIPS[MAMMOTH]		= new Ship(0,			FREIGHTER,		1400, 1, new int[]{},					new WeaponGroup[0],																					new Ability[0],																																																																																																						new int[]{ 2000,  2000,     0,     0,  1900}, 10000, 12000, new int[]{Technology.TECHNOLOGY_1_SPACE_LOGISTICS}, 																			new Product[0]);
		SHIPS[ORION]		= new Ship(0,			FREIGHTER,		4500, 0, new int[]{},					new WeaponGroup[0],																					new Ability[]{Ability.getUnbreakableAbility(new int[]{Technology.TECHNOLOGY_4_IRIDIUM_ALLOY})},																																																																																		new int[]{ 6000,     0, 10000,     0,  8100}, 30000, 50000, new int[]{Technology.TECHNOLOGY_4_IRIDIUM_ALLOY}, 																				new Product[0]);
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Classe du vaisseau, entre 1 et 7 (chasseur, corvette, frégate,
	// destroyer, croiseur, cuirassé, cargo)
	private int shipClass;
	
	// Points de structure du vaisseau
	private int hull;
	
	// Points de bouclier minimum du vaisseau (réduction minimale des dégâts)
	private int protection;
	
	// Classes de vaisseaux ciblées en priorité par le vaisseau
	private int[] targets;
	
	// Armes embarquées par le vaisseau
	private WeaponGroup[] weapons;
	
	// Capacités spéciales du vaisseau (augmentation de bouclier...)
	private Ability[] abilities;
	
	// Coût en ressources du vaisseau
	private int[] cost;
	
	// Quantité de ressources que le vaisseau peut transporter
	private double payload;
	
	// Temps de construction du vaisseau
	private int buildTime;
	
	// Technologies nécessaires à la construction du vaisseau
	private int[] technologies;
	
	// Version modifiée d'un vaisseau standard ?
	private int original;
	
	// Produits nécessaires à la construction du vaisseau
	private Product[] requiredProducts;
	
	// Faction propriétaire du vaisseau
	private String faction="";
	
	// Niveau de relation nécessaire avec la faction pour obtenier le vaisseau
	private int lvlRelation=-10;
	
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Ship(int original, int shipClass, int hull, int protection,
			int[] targets, WeaponGroup[] weapons, Ability[] abilities,
			int[] cost, double payload, int buildTime, int[] technologies,
			Product[] requiredProducts) {
		this.original = original;
		this.shipClass = shipClass;
		this.hull = hull;
		this.protection = protection;
		this.targets = targets;
		this.weapons = weapons;
		this.abilities = abilities;
		this.cost = cost;
		this.payload = payload;
		this.buildTime = buildTime;
		this.technologies = technologies;
		this.requiredProducts = requiredProducts;
	}
	
	public Ship(int original, int shipClass, int hull, int protection,
			int[] targets, WeaponGroup[] weapons, Ability[] abilities,
			int[] cost, double payload, int buildTime, int[] technologies,
			Product[] requiredProducts, String faction, int lvlRelation) {
		this.original = original;
		this.shipClass = shipClass;
		this.hull = hull;
		this.protection = protection;
		this.targets = targets;
		this.weapons = weapons;
		this.abilities = abilities;
		this.cost = cost;
		this.payload = payload;
		this.buildTime = buildTime;
		this.technologies = technologies;
		this.requiredProducts = requiredProducts;
		this.faction = faction;
		this.lvlRelation = lvlRelation;
	}
	
	public Ship(int i, int frigate2, int j, int k, int[] ls,
			WeaponGroup[] weaponGroups, Ability[] abilities2, Ship ship) {
		// TODO Auto-generated constructor stub
	}

	// --------------------------------------------------------- METHODES -- //
	
	public int getShipClass() {
		return shipClass;
	}

	public int getPower() {
		return CLASSES_POWER[shipClass];
	}
	
	public int getHull() {
		return hull;
	}

	public int getProtection() {
		return protection;
	}

	public int[] getTargets() {
		return targets;
	}

	public WeaponGroup[] getWeapons() {
		return weapons;
	}
	
	public Ability[] getAbilities() {
		return abilities;
	}

	public int[] getCost() {
		return cost;
	}
	
	public int getTotalCost(){
		int[] costs = getCost();
		int cost=0;
		for(int i=0;i<costs.length;i++)
			cost += costs[i];
		
		return cost;
	}

	public double getPayload() {
		return payload;
	}

	public double getBuildTime() {
		return (double) buildTime / GameConstants.TIME_UNIT;
	}

	public int[] getTechnologies() {
		return technologies;
	}
	
	public boolean hasAbility(int type) {
		for (Ability ability : abilities) {
			if (ability.getType() == type)
				return true;
		}
		return false;
	}
	
	// Vérifie que le vaisseau a la capacité et qu'elle a été recherchée
	public boolean hasAbility(int type, Player player) {
		for (Ability ability : abilities) {
			if (ability.getType() == type) {
				int[] requirements = ability.getRequirements();
				
				for (int requirement : requirements)
					if (!player.hasResearchedTechnology(requirement))
						return false;
				
				return true;
			}
		}
		return false;
	}
	
	public Ability getAbility(int type) {
		for (Ability ability : abilities) {
			if (ability.getType() == type)
				return ability;
		}
		return null;
	}

	public boolean isAltered() {
		return original != 0;
	}
	
	public Product[] getRequiredProducts() {
		return requiredProducts;
	}
	
	public String getFaction(){
		return faction;
	}
	
	public int getLvlRelation(){
		return lvlRelation;
	}
	

	
	// ------------------------------------------------- METHODES PRIVEES -- //
}

