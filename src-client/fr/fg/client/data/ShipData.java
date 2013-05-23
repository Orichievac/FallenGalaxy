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

package fr.fg.client.data;

import com.google.gwt.core.client.GWT;

import fr.fg.client.core.settings.Settings;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.ProductData;
import fr.fg.client.data.TechnologyData;
import fr.fg.client.data.WeaponData;
import fr.fg.client.data.WeaponGroupData;


public class ShipData {
	// ------------------------------------------------------- CONSTANTES -- //

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
		FORTRESS_ALT = 13,
		ZERO_ALT =14,
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
		HADES    = 47,
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
		TITAN = 81,
		PROFECY = 82,
		MULE = 121,
		MAMMOTH = 122,
		ORION = 123;
	
	public final static int[] CLASSES_POWER = {
		0, 1, 10, 100, 1000, 10000, 100000, 0
	};
	
	// Liste de tous les vaisseaux du jeu
	public final static ShipData[] SHIPS;
	
	public final static int
		HIDE_ABILITIES = 0,
		SHOW_PASSIVE_ABILITIES = 1,
		SHOW_ALL_ABILITIES = 2;
	
	static {
		SHIPS = new ShipData[140];
		SHIPS[RECON]		= new ShipData(0,			FIGHTER,		  16, 0, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.AUTOCANON, 2)},														new AbilityData[]{AbilityData.getFocusFireAbility(2, new int[]{TechnologyData.TECHNOLOGY_0_DOCTRINE_R55}, 1.55), AbilityData.getCriticalHitAbility(new int[]{TechnologyData.TECHNOLOGY_4_CHAOS_THEORY}, .25, 2)},																																																																		new int[]{   50,     0,     0,     0,    30},     0,    60, new int[]{}, 																						new ProductData[0]); //$NON-NLS-1$
		SHIPS[CORSAIR]		= new ShipData(0,			FIGHTER,		  22, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.LASER, 2)},															new AbilityData[]{AbilityData.getActiveLeechAbility(4, new int[]{TechnologyData.TECHNOLOGY_1_BOARDING}, .75), AbilityData.getBreachAbility(new int[]{TechnologyData.TECHNOLOGY_2_DOCTRINE_C15}, 1.15)},																																																																					new int[]{    0,    40,    15,     0,    20},     0,    80, new int[]{TechnologyData.TECHNOLOGY_0_SPACE_WARFARE}, 												new ProductData[0]); //$NON-NLS-1$ //$NON-NLS-2$
		SHIPS[FORTRESS]		= new ShipData(0,			FIGHTER,		  22, 0, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 1)},													new AbilityData[]{AbilityData.getBarrageAbility(3, new int[]{TechnologyData.TECHNOLOGY_2_BOMBARDMENT}, .65, .80, FIGHTER), AbilityData.getBarrageAbility(3, new int[]{TechnologyData.TECHNOLOGY_2_BOMBARDMENT}, .65, .80, CORVETTE), AbilityData.getBarrageAbility(3, new int[]{TechnologyData.TECHNOLOGY_2_BOMBARDMENT}, .65, .80, FRIGATE)},																																			new int[]{    0,     0,    50,     0,    25},     0,    70, new int[]{TechnologyData.TECHNOLOGY_1_FIRE_CONTOL_SYSTEM}, 											new ProductData[0]); //$NON-NLS-1$
		SHIPS[ZERO]			= new ShipData(0,			FIGHTER,		  20, 0, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 1)},														new AbilityData[]{AbilityData.getTimeShiftAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_SPACE_TIME_COMPRESSION}, 2), AbilityData.getResistanceAbility(2, new int[]{TechnologyData.TECHNOLOGY_2_PULSE_REACTOR}, ShipData.FIGHTER, .65), AbilityData.getResistanceAbility(2, new int[]{TechnologyData.TECHNOLOGY_2_PULSE_REACTOR}, ShipData.CORVETTE, .65), AbilityData.getResistanceAbility(2, new int[]{TechnologyData.TECHNOLOGY_2_PULSE_REACTOR}, ShipData.FRIGATE, .65)},			new int[]{   25,    25,     0,     0,    25},     0,    70, new int[]{TechnologyData.TECHNOLOGY_1_PULSE_LASER, TechnologyData.TECHNOLOGY_2_PULSE_REACTOR}, 		new ProductData[0]); //$NON-NLS-1$
		SHIPS[BLADE]		= new ShipData(0,			FIGHTER,		  18, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 1)},														new AbilityData[]{AbilityData.getSquadronAbility(new int[]{TechnologyData.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT}, FIGHTER, .5), AbilityData.getEcmAbility(new int[]{TechnologyData.TECHNOLOGY_4_ELECTRONIC_WARFARE, TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, .25)},																																																					new int[]{    0,    30,    20,     0,    25},     0,    65, new int[]{TechnologyData.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT}, 										new ProductData[0]); //$NON-NLS-1$
		SHIPS[GUARDIAN]		= new ShipData(0,			FIGHTER,		  20, 0, new int[]{CORVETTE, FRIGATE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 1)},													new AbilityData[]{AbilityData.getMimicryAbility(4, new int[]{TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER}), AbilityData.getDetonationAbility(new int[]{TechnologyData.TECHNOLOGY_5_TRANSCENDENCE}, .65)},																																																																				new int[]{   35,    10,     0,     0,    30},     0,    75, new int[]{TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER}, 											new ProductData[0]); //$NON-NLS-1$
		SHIPS[EAGLE]		= new ShipData(0,			FIGHTER,		  20, 0, new int[]{DESTROYER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PPC, 1), new WeaponGroupData(WeaponData.PULSE_LASER, 1)},				new AbilityData[]{AbilityData.getParticleProjectionAbility(5, new int[]{TechnologyData.TECHNOLOGY_6_PARTICLE_PROJECTION}, 4, 5)},																																																																																						new int[]{    0,     0,    25,    25,    30},     0,    90, new int[]{TechnologyData.TECHNOLOGY_6_PARTICLE_PROJECTION}, 										new ProductData[0]); //$NON-NLS-1$
																																																																																																																																																																																																										
		SHIPS[RECON_ALT]	= new ShipData(RECON,		FIGHTER,		  16, 0, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 1)},													new AbilityData[]{AbilityData.getFocusFireAbility(2, new int[]{TechnologyData.TECHNOLOGY_0_DOCTRINE_R55}, 1.60), AbilityData.getTerminatorAbility(new int[]{TechnologyData.TECHNOLOGY_0_DOCTRINE_R55}, 2)},																																																																				new int[]{   25,     0,     0,     30,    30},     0,    90, new int[]{}, 																						new ProductData[]{new ProductData(ProductData.PRODUCT_IRIDIUM, 2), 	new ProductData(ProductData.PRODUCT_HEPTANIUM, 2), new ProductData(ProductData.PRODUCT_SELENIUM, 2)}); //DONE
		SHIPS[CORSAIR_ALT]	= new ShipData(CORSAIR,		FIGHTER,		  22, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.BLASTER, 2)},															new AbilityData[]{AbilityData.getActiveLeechAbility(4, new int[]{TechnologyData.TECHNOLOGY_1_BOARDING}, .80), AbilityData.getPillageAbility(new int[]{TechnologyData.TECHNOLOGY_1_BOARDING}, .3)},																																																																						new int[]{    0,    20,    20,     10,    25},     0,    80, new int[]{TechnologyData.TECHNOLOGY_0_SPACE_WARFARE}, 												new ProductData[]{new ProductData(ProductData.PRODUCT_NECTAR, 2)}); //$NON-NLS-1$ //$NON-NLS-2$ 
		SHIPS[BLADE_ALT]	= new ShipData(BLADE,		FIGHTER,		  18, 0, new int[]{FIGHTER, CORVETTE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 1)},														new AbilityData[]{AbilityData.getSquadronAbility(new int[]{TechnologyData.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT}, FIGHTER, .5), AbilityData.getEvadeAbility(new int[]{TechnologyData.TECHNOLOGY_4_ELECTRONIC_WARFARE, TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, .1)},																																																					new int[]{    0,    15,    10,     25,    25},     0,    75, new int[]{TechnologyData.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT},										new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 3),new ProductData(ProductData.PRODUCT_IRIDIUM, 1),new ProductData(ProductData.PRODUCT_HEPTANIUM, 3)}); 
		SHIPS[ZERO_ALT]		= new ShipData(ZERO,		FIGHTER,		  22, 0, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 1)},														new AbilityData[]{AbilityData.getTimeShiftAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_SPACE_TIME_COMPRESSION}, 2), AbilityData.getAllResistanceAbility(5, new int[]{TechnologyData.TECHNOLOGY_2_PULSE_REACTOR}, 0.35, 3)},																																																															new int[]{   13,    13,     0,     24,    25},     0,    70, new int[]{TechnologyData.TECHNOLOGY_1_PULSE_LASER, TechnologyData.TECHNOLOGY_2_PULSE_REACTOR}, 	new ProductData[]{new ProductData(ProductData.PRODUCT_NECTAR, 2), new ProductData(ProductData.PRODUCT_HEPTANIUM, 1), new ProductData(ProductData.PRODUCT_SELENIUM, 1)}); //$NON-NLS-1$
		SHIPS[GUARDIAN_ALT]	= new ShipData(GUARDIAN,	FIGHTER,		  22, 0, new int[]{CORVETTE, FRIGATE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 1)},													new AbilityData[]{AbilityData.getMimicryAbility(4, new int[]{TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER}), AbilityData.getCanaliseAbility(new int[]{TechnologyData.TECHNOLOGY_5_TRANSCENDENCE}, 1, 0.2)},																																																																			new int[]{   15,    10,     0,     20,    30},     0,    80, new int[]{TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER}, 											new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 3), new ProductData(ProductData.PRODUCT_NECTAR, 3), new ProductData(ProductData.PRODUCT_IRIDIUM, 2),new ProductData(ProductData.PRODUCT_ANTILIUM, 2)});
		SHIPS[EAGLE_ALT]	= new ShipData(EAGLE,		FIGHTER,		  22, 0, new int[]{DESTROYER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PPC, 1), new WeaponGroupData(WeaponData.PULSE_LASER, 1)},			 	new AbilityData[]{AbilityData.getParticleProjectionAbility(5, new int[]{TechnologyData.TECHNOLOGY_6_PARTICLE_PROJECTION}, 4, 5),AbilityData.getAnticipationAbility(new int[]{}, DESTROYER, .15)},																																																																						new int[]{    0,     0,     0,     50,    30},     0,   110, new int[]{TechnologyData.TECHNOLOGY_6_PARTICLE_PROJECTION}, 										new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 7),new ProductData(ProductData.PRODUCT_SULFARIDE, 4),new ProductData(ProductData.PRODUCT_ANTILIUM, 4)});
		SHIPS[FORTRESS_ALT]	= new ShipData(FORTRESS,	FIGHTER,		  23, 0, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 1)},													new AbilityData[]{AbilityData.getBarrageV2Ability(3, new int[]{}, .75, .85), AbilityData.getBarrageV2Ability(5, new int[]{}, .65, .80)},																																																																																				new int[]{    0,     0,    35,     15,    25},     0,    70, new int[]{TechnologyData.TECHNOLOGY_1_FIRE_CONTOL_SYSTEM}, 										new ProductData[]{new ProductData(ProductData.PRODUCT_HEPTANIUM, 1), new ProductData(ProductData.PRODUCT_NECTAR, 1)});
		
		
		SHIPS[VALKYRIE]		= new ShipData(0,			CORVETTE,		 190, 1, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.LASER, 12), new WeaponGroupData(WeaponData.PULSE_LASER, 4)},			new AbilityData[]{AbilityData.getRepairAbility(3, new int[]{TechnologyData.TECHNOLOGY_2_DRONES}, .15), AbilityData.getRapidFireAbility(5, new int[]{TechnologyData.TECHNOLOGY_3_DOCTRINE_VR_70}, .65)},																																																																					new int[]{  300,     0,   200,     0,   260},     0,   800, new int[]{TechnologyData.TECHNOLOGY_1_PULSE_LASER}, 												new ProductData[0]); //$NON-NLS-1$
		SHIPS[HELLCAT]		= new ShipData(0,			CORVETTE,		 190, 1, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 8)},													new AbilityData[]{AbilityData.getFragChargeAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_MICRO_CHARGES, TechnologyData.TECHNOLOGY_3_BATTLE_LINE}, 1.2), AbilityData.getHeatAbility(new int[]{TechnologyData.TECHNOLOGY_3_MICRO_CHARGES}, -1)},																																																										new int[]{    0,   280,   240,     0,   250},     0,  1000, new int[]{TechnologyData.TECHNOLOGY_2_SELF_DEFENSE_SYSTEM}, 										new ProductData[0]); //$NON-NLS-1$
		SHIPS[SPECTRE]		= new ShipData(0,			CORVETTE,		 165, 1, new int[]{FRIGATE, DESTROYER},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.GAUSS, 2)},															new AbilityData[]{AbilityData.getDodgeAbility(new int[]{TechnologyData.TECHNOLOGY_5_UNIFIED_FIELD_THEORY}, .25), AbilityData.getHarassmentAbility(new int[]{TechnologyData.TECHNOLOGY_3_GAUSS_CANON}, .85)},																																																																			new int[]{  160,   160,   160,     0,   270},     0,  1000, new int[]{TechnologyData.TECHNOLOGY_3_GAUSS_CANON}, 												new ProductData[0]);
		SHIPS[RAPTOR]		= new ShipData(0,			CORVETTE,		 180, 1, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 10)},														new AbilityData[]{AbilityData.getDevastateAbility(6, new int[]{TechnologyData.TECHNOLOGY_3_DOCTRINE_VR_70}, .25), AbilityData.getCriticalHitAbility(new int[]{TechnologyData.TECHNOLOGY_4_CHAOS_THEORY}, .15, 3)},																																																																		new int[]{  160,   340,     0,     0,   270},     0,  1000, new int[]{TechnologyData.TECHNOLOGY_2_HEPTANIUM_PROPULSION},										new ProductData[0]);
		SHIPS[SENTINEL]		= new ShipData(0,			CORVETTE,		 210, 1, new int[]{},					new WeaponGroupData[0],																										new AbilityData[]{AbilityData.getDeflectorAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, FIGHTER, 1), AbilityData.getDeflectorAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, CORVETTE, 1), AbilityData.getInhibitorFieldAbility(3, new int[]{TechnologyData.TECHNOLOGY_5_UNIFIED_FIELD_THEORY, TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER})},																								new int[]{  500,     0,     0,     0,   250},     0,  1100, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, 											new ProductData[0]);
		SHIPS[BANSHEE]		= new ShipData(0,			CORVETTE,		 170, 1, new int[]{FIGHTER, CORVETTE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 9)},													new AbilityData[]{AbilityData.getImmaterialAbility(new int[]{TechnologyData.TECHNOLOGY_5_MATTER_ALTERATION, TechnologyData.TECHNOLOGY_5_CHAOS_GENERATOR}, .5, 1.25), AbilityData.getConfusionAbility(2, new int[]{}, true), AbilityData.getConfusionAbility(4, new int[]{}, false)},																																																	new int[]{    0,   300,   180,     0,   260},     0,  1200, new int[]{TechnologyData.TECHNOLOGY_5_MATTER_ALTERATION}, 											new ProductData[0]);
		
		SHIPS[RAPTOR_ALT]	= new ShipData(RAPTOR,		CORVETTE, 		 180, 1, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 10)},														new AbilityData[]{AbilityData.getOverheatingAbility(6, new int[]{TechnologyData.TECHNOLOGY_3_DOCTRINE_VR_70}, 1.4, .75), AbilityData.getCriticalHitV2Ability(new int[]{TechnologyData.TECHNOLOGY_4_CHAOS_THEORY}, .15, .10, 2, 3)},																																																														new int[]{   80,   120,     0,     300,   270},   0,  1300, new int[]{TechnologyData.TECHNOLOGY_2_HEPTANIUM_PROPULSION}, 										new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 1), new ProductData(ProductData.PRODUCT_IRIDIUM, 2),new ProductData(ProductData.PRODUCT_ANTILIUM, 1)});
		SHIPS[SENTINEL_ALT]	= new ShipData(SENTINEL,	CORVETTE, 		 220, 1, new int[]{},					new WeaponGroupData[0],																										new AbilityData[]{AbilityData.getDeflectorAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, FIGHTER, 1), AbilityData.getDeflectorAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, CORVETTE, 1),AbilityData.getDeflectorAbility(7, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, FRIGATE, 2),AbilityData.getDeflectorAbility(7, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, DESTROYER, 2)},										new int[]{  250,     0,     0,     250,   250},   0,  1300, new int[]{TechnologyData.TECHNOLOGY_3_MAGNETIC_SHIELD}, 											new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 2), new ProductData(ProductData.PRODUCT_SULFARIDE, 2)});
		SHIPS[HELLCAT_ALT]	= new ShipData(HELLCAT,		CORVETTE,		 205, 1, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 8)},													new AbilityData[]{AbilityData.getProximityChargeAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_MICRO_CHARGES, TechnologyData.TECHNOLOGY_3_BATTLE_LINE}, .2), AbilityData.getHeatAbility(new int[]{TechnologyData.TECHNOLOGY_3_MICRO_CHARGES}, -1)},																																																									new int[]{    0,   200,   160,     180,   250},   0,  1200, new int[]{TechnologyData.TECHNOLOGY_2_SELF_DEFENSE_SYSTEM}, 										new ProductData[]{new ProductData(ProductData.PRODUCT_ANTILIUM, 1), new ProductData(ProductData.PRODUCT_HEPTANIUM, 2), new ProductData(ProductData.PRODUCT_NECTAR, 1) });
		SHIPS[VALKYRIE_ALT]	= new ShipData(VALKYRIE,	CORVETTE,		 200, 1, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 12)},														new AbilityData[]{AbilityData.getRecupAbility(5, new int[]{TechnologyData.TECHNOLOGY_2_DRONES}, .15), AbilityData.getRapidFireV2Ability(6, new int[]{TechnologyData.TECHNOLOGY_3_DOCTRINE_VR_70}, .45, true)},																																																																			new int[]{  200,     0,   100,     200,   260},   0,  1200, new int[]{TechnologyData.TECHNOLOGY_1_PULSE_LASER}, 												new ProductData[]{new ProductData(ProductData.PRODUCT_NECTAR, 1), new ProductData(ProductData.PRODUCT_HEPTANIUM, 1), new ProductData(ProductData.PRODUCT_IRIDIUM, 1)});
		SHIPS[SPECTRE_ALT]	= new ShipData(SPECTRE,		CORVETTE,		 175, 1, new int[]{FRIGATE, DESTROYER},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.GAUSS, 2)},															new AbilityData[]{AbilityData.getDodgeV2Ability(new int[]{TechnologyData.TECHNOLOGY_5_UNIFIED_FIELD_THEORY}, .20, .35), AbilityData.getHarassmentAbility(new int[]{TechnologyData.TECHNOLOGY_3_GAUSS_CANON}, .80)},																																																																		new int[]{   80,    80,    80,     240,   270},   0,  1200, new int[]{TechnologyData.TECHNOLOGY_3_GAUSS_CANON}, 												new ProductData[]{new ProductData(ProductData.PRODUCT_NECTAR, 2),new ProductData(ProductData.PRODUCT_IRIDIUM, 2),new ProductData(ProductData.PRODUCT_ANTILIUM, 3)});
		SHIPS[BANSHEE_ALT]	= new ShipData(BANSHEE,		CORVETTE, 		 180, 1, new int[]{FIGHTER, CORVETTE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 9)},													new AbilityData[]{AbilityData.getImmaterialAbility(new int[]{TechnologyData.TECHNOLOGY_5_MATTER_ALTERATION, TechnologyData.TECHNOLOGY_5_CHAOS_GENERATOR}, .65, 1.1), AbilityData.getWeaknessAbility(new int[]{}, 0.80)},																																																																new int[]{    0,   150,    90,     240,   260},   0,  1350, new int[]{TechnologyData.TECHNOLOGY_5_MATTER_ALTERATION}, 											new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 4), new ProductData(ProductData.PRODUCT_NECTAR, 3),new ProductData(ProductData.PRODUCT_SULFARIDE, 1),new ProductData(ProductData.PRODUCT_HEPTANIUM, 5)});

		
		
		SHIPS[PALADIN]		= new ShipData(0,			FRIGATE,		1760, 3, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.AUTOCANON, 160)},														new AbilityData[]{AbilityData.getTauntAbility(3, new int[]{TechnologyData.TECHNOLOGY_5_TRANSCENDENCE}, 2, 2), AbilityData.getDamageReturnAuraAbility(4, new int[]{TechnologyData.TECHNOLOGY_3_PROPAGANDA_THEORY}, .1)},																																																																	new int[]{    0,  2200,  2800,     0,  2600},     0,  4000, new int[]{TechnologyData.TECHNOLOGY_3_PROPAGANDA_THEORY}, 											new ProductData[0]);
		SHIPS[FURY]			= new ShipData(0,			FRIGATE,		1300, 2, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 100)},													new AbilityData[]{AbilityData.getRageAbility(5, new int[]{TechnologyData.TECHNOLOGY_4_BATTLE_DRUGS}, -2),	AbilityData.getOutflankingAbility(new int[]{TechnologyData.TECHNOLOGY_4_OFFENSIVE_MANEUVERS}, .3)},																																																																			new int[]{  800,  2400,  1600,     0,  2500},     0,  5000, new int[]{TechnologyData.TECHNOLOGY_3_BATTLE_LINE}, 												new ProductData[0]); //$NON-NLS-1$ //$NON-NLS-2$
		SHIPS[LIBERTY]		= new ShipData(0,			FRIGATE,		1500, 2, new int[]{FRIGATE, DESTROYER},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 80)},													new AbilityData[]{AbilityData.getSublimationAbility(new int[]{TechnologyData.TECHNOLOGY_5_FUSION_BATTERY}, .35), AbilityData.getFusionAbility(5, new int[]{TechnologyData.TECHNOLOGY_5_FUSION_BATTERY, TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER}, 2)},																																																								new int[]{  600,  2000,  2400,     0,  2550},     0,  5400, new int[]{TechnologyData.TECHNOLOGY_4_FUSION_PROPULSION}, 											new ProductData[0]);
		SHIPS[STYX]			= new ShipData(0,			FRIGATE,		1600, 2, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.INFERNO, 24)},															new AbilityData[]{AbilityData.getRetributionAbility(4, new int[]{TechnologyData.TECHNOLOGY_4_ENDOCTRINATION}, 2, .4), AbilityData.getSacrificeAbility(5, new int[]{TechnologyData.TECHNOLOGY_5_TRANSCENDENCE}, -1, -3, -2)},																																																															new int[]{ 4800,     0,     0,     0,  2450},     0,  6400, new int[]{TechnologyData.TECHNOLOGY_4_ENDOCTRINATION}, 												new ProductData[0]);
		SHIPS[SERAPHIM]		= new ShipData(0,			FRIGATE,		1500, 2, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.GAUSS, 12), new WeaponGroupData(WeaponData.LASER, 64)},				new AbilityData[]{AbilityData.getPhaseAbility(new int[]{TechnologyData.TECHNOLOGY_4_ELECTRONIC_WARFARE}), AbilityData.getRiftAbility(5, new int[]{TechnologyData.TECHNOLOGY_4_SDI})},																																																																									new int[]{ 3000,  2200,     0,     0,  2250},     0,  5000, new int[]{TechnologyData.TECHNOLOGY_4_SDI}, 														new ProductData[0]);
		SHIPS[SCORPION]		= new ShipData(0,			FRIGATE,		1450, 3, new int[]{DESTROYER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 64)},													new AbilityData[]{AbilityData.getOverloadAbility(new int[]{TechnologyData.TECHNOLOGY_5_CHAOS_GENERATOR}, 4, -3), AbilityData.getQuantumErrorAbility(new int[]{TechnologyData.TECHNOLOGY_5_CHAOS_GENERATOR}, 3, 1.5)},																																																																	new int[]{ 1800,     0,  3200,     0,  2400},     0,  6500, new int[]{TechnologyData.TECHNOLOGY_4_CHAOS_THEORY, TechnologyData.TECHNOLOGY_4_FUSION_PROPULSION}, new ProductData[0]);
		SHIPS[HADES]        = new ShipData(0,           FRIGATE,        1600, 3, new int[]{DREADNOUGHT},        new WeaponGroupData[]{new WeaponGroupData(WeaponData.SONIC_TORPEDO, 80)},                                                  new AbilityData[]{AbilityData.getFiercenessAbility(5, new int[]{TechnologyData.TECHNOLOGY_6_DOGME_MARTIALE}, ShipData.DREADNOUGHT, 1.25)},                                                                                                                                                                                                                                                                                                                                                                                                                                 new int[]{ 1800,     0,  2500,     0,  2500},     0, 5500, new int[]{TechnologyData.TECHNOLOGY_6_DOGME_MARTIALE},                                                 new ProductData[0]);																																																																																																																																																																																																							
		SHIPS[JUDANIMATOR]    = new ShipData(0,           FRIGATE,        1800, 3, new int[]{FRIGATE, DESTROYER}, new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 120)},                                                  new AbilityData[0],                                                                                                                                                                                                                                                                                                                                                                                                                                                                     new int[]{ 2500,  1700,  3000,     0,  2700},     0,  6000, new int[]{TechnologyData.TECHNOLOGY_7_DESTRUCTION_SHIPS},                                              new ProductData[0]);
		//SHIPS[PALADIN_ALT]	= new ShipData(PALADIN,		FRIGATE,		2150, 3, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.AUTOCANON, 200)},														new AbilityData[]{AbilityData.getTauntAbility(3, new int[]{}, 2, 2), AbilityData.getDamageReturnAuraAbility(4, new int[]{}, .15)},																																																																																						new int[]{    0,  2200,  2800,     0,  2500},     0,  4000, new int[]{}, new ProductData[]{new ProductData(ProductData.PRODUCT_UNREACHABLE, 1)});
		SHIPS[FURY_ALT]		= new ShipData(FURY,		FRIGATE,		1350, 2, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 100)},													new AbilityData[]{AbilityData.getRageAbility(5, new int[]{TechnologyData.TECHNOLOGY_4_BATTLE_DRUGS}, -1),	AbilityData.getCollateralAbility(new int[]{}, 2)},																																																																															new int[]{  400,  1200,   800,  2400,  2500},     0,  6000, new int[]{TechnologyData.TECHNOLOGY_3_BATTLE_LINE},													new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 2), new ProductData(ProductData.PRODUCT_NECTAR, 2),new ProductData(ProductData.PRODUCT_HEPTANIUM, 1),new ProductData(ProductData.PRODUCT_ANTILIUM, 1)});
		SHIPS[SERAPHIM_ALT]	= new ShipData(SERAPHIM,	FRIGATE,		1650, 2, new int[]{CORVETTE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.GAUSS, 12), new WeaponGroupData(WeaponData.LASER, 64)},				new AbilityData[]{AbilityData.getRiftAbility(5, new int[]{TechnologyData.TECHNOLOGY_4_SDI})},																																																																																															new int[]{ 1000,   800,     0,  3400,  2250},  5000,  7000, new int[]{TechnologyData.TECHNOLOGY_4_SDI}, 														new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 2), new ProductData(ProductData.PRODUCT_NECTAR, 2),new ProductData(ProductData.PRODUCT_IRIDIUM, 2),new ProductData(ProductData.PRODUCT_HEPTANIUM, 2),new ProductData(ProductData.PRODUCT_ANTILIUM, 2)});
		SHIPS[LIBERTY_ALT]	= new ShipData(LIBERTY,		FRIGATE,		1650, 2, new int[]{FRIGATE, DESTROYER},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 80)},													new AbilityData[]{AbilityData.getSublimationV2Ability(new int[]{TechnologyData.TECHNOLOGY_5_FUSION_BATTERY},  .30, .30, .50, 1.0), AbilityData.getAbsorbtionAbility(new int[]{TechnologyData.TECHNOLOGY_5_FUSION_BATTERY, TechnologyData.TECHNOLOGY_4_KESLEY_STABILISER}, .1)},																																																			new int[]{  200,   800,   900,  3000,  2550},     0,  6800, new int[]{TechnologyData.TECHNOLOGY_4_FUSION_PROPULSION}, 											new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 3), new ProductData(ProductData.PRODUCT_NECTAR, 3),new ProductData(ProductData.PRODUCT_HEPTANIUM, 4)});
		SHIPS[STYX_ALT]		= new ShipData(STYX,		FRIGATE,		1800, 2, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.INFERNO, 24)},															new AbilityData[]{AbilityData.getDamageRetunPassifAbility(new int[]{TechnologyData.TECHNOLOGY_4_ENDOCTRINATION}, .15), AbilityData.getSacrificeAbility(4, new int[]{TechnologyData.TECHNOLOGY_5_TRANSCENDENCE}, -1, -3, -2)},																																																															new int[]{ 2400,     0,     0,  2400,  2450},     0,  7400, new int[]{TechnologyData.TECHNOLOGY_4_ENDOCTRINATION}, 												new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 2), new ProductData(ProductData.PRODUCT_IRIDIUM, 2),new ProductData(ProductData.PRODUCT_HEPTANIUM, 4),new ProductData(ProductData.PRODUCT_ANTILIUM, 2)}); 
		SHIPS[SCORPION_ALT]	= new ShipData(SCORPION,	FRIGATE,		1550, 3, new int[]{DESTROYER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.TWIN_AUTOCANON, 64)},													new AbilityData[]{AbilityData.getOverloadAbility(new int[]{TechnologyData.TECHNOLOGY_5_CHAOS_GENERATOR}, 4, -2), AbilityData.getQuantumErrorAbility(new int[]{TechnologyData.TECHNOLOGY_5_CHAOS_GENERATOR}, 3, 1.5), AbilityData.getIncoherenceAbility(new int[]{}, .35, 1)},																																																			new int[]{  900,     0,  1600,  2500,  2400},     0,  7500, new int[]{TechnologyData.TECHNOLOGY_4_CHAOS_THEORY, TechnologyData.TECHNOLOGY_4_FUSION_PROPULSION}, new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 3), new ProductData(ProductData.PRODUCT_IRIDIUM, 3),new ProductData(ProductData.PRODUCT_HEPTANIUM, 2),new ProductData(ProductData.PRODUCT_ANTILIUM, 3)});
		SHIPS[PALADIN_ALT]	= new ShipData(PALADIN,		FRIGATE,		1900, 0, new int[]{FIGHTER},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.AUTOCANON, 160)},														new AbilityData[]{AbilityData.getTauntAbility(3, new int[]{TechnologyData.TECHNOLOGY_5_TRANSCENDENCE}, 2, 1), AbilityData.getDamageRepartitionAbility(5, new int[]{}, .1)},																																																																												new int[]{    0,  1300,  1700,  2000,  2600},     0,  6000, new int[]{TechnologyData.TECHNOLOGY_3_PROPAGANDA_THEORY}, 											new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 2), new ProductData(ProductData.PRODUCT_HEPTANIUM, 2),new ProductData(ProductData.PRODUCT_ANTILIUM, 1)});
																																																																																																																																																																																									 													
		SHIPS[TEMPLAR]		= new ShipData(0,			DESTROYER,	   14000, 4, new int[]{},					new WeaponGroupData[0],																										new AbilityData[]{AbilityData.getForceFieldAbility(8, new int[]{TechnologyData.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 1, 2), AbilityData.getRepairAbility(4, new int[]{}, .2)},																																																																											new int[]{    0, 19200, 32000,     0, 25000},	  0, 40000, new int[]{TechnologyData.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION},										new ProductData[0]);
		SHIPS[OBLIVION]		= new ShipData(0,			DESTROYER,	   14500, 3, new int[]{FRIGATE},			new WeaponGroupData[]{new WeaponGroupData(WeaponData.SONIC_TORPEDO, 510)},													new AbilityData[]{AbilityData.getLotteryAbility(3, new int[]{TechnologyData.TECHNOLOGY_6_DYNAMIC_REALITY}), AbilityData.getIncohesionAbility(new int[]{TechnologyData.TECHNOLOGY_5_MAGNETODYNAMICS}, -1, 1.3, 2, .7)},																																																																	new int[]{40000,  8000,     0,     0, 26000},	  0, 50000, new int[]{TechnologyData.TECHNOLOGY_5_MAGNETODYNAMICS}, 											new ProductData[0]);
		SHIPS[HURRICANE]	= new ShipData(0,			DESTROYER,	   13000, 3, new int[]{CORVETTE, FRIGATE},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.PULSE_LASER, 500), new WeaponGroupData(WeaponData.INFERNO, 90)},		new AbilityData[]{AbilityData.getHullEnergyTransferAbility(1, new int[]{TechnologyData.TECHNOLOGY_6_PHASE_TRANSFER}, 1.15, 0.75), AbilityData.getDamageEnergyTransferAbility(1, new int[]{TechnologyData.TECHNOLOGY_6_PHASE_TRANSFER}, 0.9, 1.3)},																																																										new int[]{    0,     0, 54000,     0, 24000},	  0, 48000, new int[]{TechnologyData.TECHNOLOGY_6_PHASE_TRANSFER}, 												new ProductData[0]);
		
		SHIPS[TEMPLAR_ALT]	= new ShipData(TEMPLAR,		DESTROYER,	   15000, 4, new int[]{},					new WeaponGroupData[]{},																									new AbilityData[]{AbilityData.getForceFieldAbility(7, new int[]{TechnologyData.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 1, 2), AbilityData.getRepercuteAbility(6, new int[]{}, .25)},																																																																										new int[]{    0,  4200, 7000, 40000, 25000},     0, 55000, new int[]{TechnologyData.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION}, 										new ProductData[]{new ProductData(ProductData.PRODUCT_SELENIUM, 5), new ProductData(ProductData.PRODUCT_NECTAR, 5),new ProductData(ProductData.PRODUCT_SULFARIDE, 3)});
																																																																																																																																																																																																									
		// Croiseurs
		SHIPS[TITAN]		= new ShipData(0,			DREADNOUGHT,  120000, 4, new int[]{FIGHTER, DESTROYER},	new WeaponGroupData[]{new WeaponGroupData(WeaponData.ION_CANON, 1600), new WeaponGroupData(WeaponData.TACTICAL_ROCKET, 3600)}, new AbilityData[]{AbilityData.getArtilleryShotAbility(3, new int[]{TechnologyData.TECHNOLOGY_8_CAPITAL_SHIPS}, .25)},																																																																																								new int[]{100000,    0,     0,     0, 50000}, 50000, 500000, new int[]{TechnologyData.TECHNOLOGY_8_CAPITAL_SHIPS},												new ProductData[0]);
		
		
		SHIPS[MULE]			= new ShipData(0,			FREIGHTER,		  50, 0, new int[]{},					new WeaponGroupData[0],																										new AbilityData[0],																																																																																																																		new int[]{    0,     0,   150,     0,    75},   500,   500, new int[]{}, new ProductData[0]);
		SHIPS[MAMMOTH]		= new ShipData(0,			FREIGHTER,		1400, 1, new int[]{},					new WeaponGroupData[0],																										new AbilityData[0],																																																																																																																		new int[]{ 2000,  2000,     0,     0,  1900}, 10000, 12000, new int[]{TechnologyData.TECHNOLOGY_1_SPACE_LOGISTICS}, 											new ProductData[0]); //$NON-NLS-1$
		SHIPS[ORION]		= new ShipData(0,			FREIGHTER,		4500, 0, new int[]{},					new WeaponGroupData[0],																										new AbilityData[]{AbilityData.getUnbreakableAbility(new int[]{TechnologyData.TECHNOLOGY_4_IRIDIUM_ALLOY})},																																																																																												new int[]{ 6000,     0, 10000,     0,  8100}, 30000, 50000, new int[]{TechnologyData.TECHNOLOGY_4_IRIDIUM_ALLOY}, 												new ProductData[0]);
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
	private WeaponGroupData[] weapons;
	
	// Capacités spéciales du vaisseau (augmentation de bouclier...)
	private AbilityData[] abilities;
	
	// Coût en ressources du vaisseau
	private int[] cost;
	
	// Quantité de ressources que le vaisseau peut transporter
	private double payload;
	
	// Temps de construction du vaisseau
	private int buildTime;
	
	// Technologies nécessaires à la construction du vaisseau
	private int[] technologies;
	
	// Version originale du vaisseau
	private int original;
	
	// Produits nécessaires à la construction du vaisseau
	private ProductData[] requiredProducts;
	
	// Faction propriétaire du vaisseau
	private String faction = "";
	
	// Niveau de relation nécessaire avec la faction afin de le débloquer
	private int lvlRelation = 0;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ShipData(int original, int shipClass, int hull, int protection,
			int[] targets, WeaponGroupData[] weapons, AbilityData[] abilities,
			int[] cost, int payload, int buildTime, int[] technologies,
			ProductData[] requiredProducts) {
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
	
	public ShipData(int original, int shipClass, int hull, int protection,
			int[] targets, WeaponGroupData[] weapons, AbilityData[] abilities,
			int[] cost, int payload, int buildTime, int[] technologies,
			ProductData[] requiredProducts, String faction, int lvlRelation) {
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
	// --------------------------------------------------------- METHODES -- //

	public int getShipClass() {
		return shipClass;
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

	public WeaponGroupData[] getWeapons() {
		return weapons;
	}
	
	public AbilityData[] getAbilities() {
		return abilities;
	}

	public boolean hasAbility(int type) {
		for (AbilityData ability : abilities) {
			if (ability.getType() == type)
				return true;
		}
		return false;
	}

	public AbilityData getAbility(int type) {
		for (AbilityData ability : abilities) {
			if (ability.getType() == type)
				return ability;
		}
		return null;
	}
	
	public int[] getCost() {
		return cost;
	}
	
	public double getPayload() {
		return payload;
	}
	
	public double getBuildTime() {
		return (double) buildTime / Settings.getTimeUnit();
	}
	
	public int[] getTechnologies() {
		return technologies;
	}
	
	public int getOriginal() {
		return original;
	}
	
	public boolean isAltered() {
		return original != 0;
	}
	
	public ProductData[] getRequiredProducts() {
		return requiredProducts;
	}
	

	
	public static String getDesc(int id, long count, int availableAbilities) {
		return getDesc(id, count, availableAbilities, SHOW_ALL_ABILITIES);
	}
	
	public static String getDesc(int id, long count, int availableAbilities,
			int showAbilities) {
		return getDesc(id, count, availableAbilities,
				showAbilities, -1, 0, 1, 1);
	}
	
	public static String getDesc(int id, long count, int availableAbilities,
			int showAbilities, int hull, int protectionModifier,
			double damageModifier, double hullModifier) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		ShipData ship = SHIPS[id];
		
		// Cibles du vaisseau
		String targets = ""; //$NON-NLS-1$
		for (int j = 0; j < ship.getTargets().length; j++)
			targets += (j == 0 ? "" : ", ") + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
					"shipClasses" + ship.getTargets()[j]); //$NON-NLS-1$
		
		// Armement du vaisseau
		String weapons = ""; //$NON-NLS-1$
		for (WeaponGroupData weaponGroup : ship.getWeapons()) {
			String color =
				(damageModifier > 1 ? " style=\"color: #00c000;\"" :
				(damageModifier < 1 ? " style=\"color: #ff8000;\"" : ""));
			
			// Dégâts
			String damage = "<span" + color + "><b>" +
				Math.round(weaponGroup.getWeapon(
						).getDamageMin() * damageModifier) + "-" +
				Math.round(weaponGroup.getWeapon(
						).getDamageMax() * damageModifier) + "</b>";
			
			if (damageModifier > 1)
				damage += " (+" + (int) Math.round((damageModifier - 1) * 100) + "%)";
			else if (damageModifier < 1)
				damage += " (-" + (int) Math.round((1 - damageModifier) * 100) + "%)";
			
			damage += "</span>";
			
			weapons += "<div>" + weaponGroup.getCount() + "x " + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
					"weapon" + weaponGroup.getIdWeapon()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
					"<img class=\"stat s-damage\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
					"images/misc/blank.gif\"/> " + damage + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// Charge
		String payload = "";
		if (ship.getPayload() > 0)
			payload = "Capacité : " + Formatter.formatNumber(ship.getPayload(), true);
		
		String abilities = ""; //$NON-NLS-1$
		
		if (showAbilities > HIDE_ABILITIES) {
			// Capacités du vaisseau
			for (int i = 0; i < ship.getAbilities().length; i++) {
				AbilityData ability = ship.getAbilities()[i];
				
				if (ability.isPassive() || showAbilities == SHOW_ALL_ABILITIES)
					if ((availableAbilities & (1 << i)) != 0)
						abilities += "<div class=\"title\">" +
							"<div style=\"float: right; color: #1d97d5;\">" +
								(ability.isPassive() ? "Passif" :
								"<img class=\"stat s-small-cooldown\" src=\"" +
								Config.getMediaUrl() + //$NON-NLS-1$
								"images/misc/blank.gif\" unselectable=\"on\"/>" +
								ability.getCooldown()) +
							"</div>" + ability.getName() + "</div>" +
							"<div class=\"justify\">" + ability.getDesc(id) + "</div>";
			}
		}
		
		String color =
			(protectionModifier > 0 ? " style=\"color: #00c000;\"" :
			(protectionModifier < 0 ? " style=\"color: #ff8000;\"" : ""));
		
		// Protection
		String protection = "<span" + color + "><b>" + (ship.getProtection() + protectionModifier) + "</b>";
		
		if (protectionModifier > 0)
			protection += " (+" + protectionModifier + ")";
		else if (protectionModifier < 0)
			protection += " (" + protectionModifier + ")";
		
		protection += "</span>";
		
		String hullValue;
		if (hull > 0) {
			String[] colors = {"#ff0000", "#ff7201", "#ffd800", "#ceff01", "#00ff00"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			double[] thresholds = {.2, .4, .6, .8, 1};
			
			double coef = hull / (double) ship.getHull();
			color = "";
			for (int i = 0; i < thresholds.length; i++)
				if (coef <= thresholds[i]) {
					color = colors[i];
					break;
				}
			
			hullValue = "<b style=\"color: " + color + ";\">" +
				Formatter.formatNumber(Math.round(hull * hullModifier)) + "</b> / " +
				"<b>" + Formatter.formatNumber(Math.round(ship.getHull() * hullModifier)) + "</b>";
			
			if (hullModifier != 1) {
				color =
					(hullModifier > 1 ? " style=\"color: #00c000;\"" :
					(hullModifier < 1 ? " style=\"color: #ff8000;\"" : ""));
				
				hullValue += "<span" + color + ">";
				
				if (hullModifier > 1)
					hullValue += " (+" + (int) Math.round((hullModifier - 1) * 100) + "%)";
				else if (hullModifier < 1)
					hullValue += " (-" + (int) Math.round((1 - hullModifier) * 100) + "%)";
				
				hullValue += "</span>";
			}
		} else {
			hullValue = "<b>" + Formatter.formatNumber(ship.getHull()) + "</b>";
		}
		
		return "<div class=\"title\">" + (count > 1 ? Formatter.formatNumber(count) + " " : "") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			dynamicMessages.getString("ship" + (count > 1 ? "s" : "") + id) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			" <span style=\"font-weight: normal;\">(" + dynamicMessages.getString("shipClass" + ship.getShipClass()) + ")</span></div>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"<div><img class=\"stat s-struct\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
			"images/misc/blank.gif\"/> " + hullValue + //$NON-NLS-1$ //$NON-NLS-2$
			" <img class=\"stat s-shield\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
			"images/misc/blank.gif\"/> " + protection + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
			(targets.length() > 0 ? "<div>" + messages.shipTargets(targets) + "</div>" : "") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			weapons + payload + abilities;
	}
	
	
	public String getFaction(){
		return faction;
	}
	
	public int getLvlRelation(){
		return lvlRelation;
	}
	

	
	// ------------------------------------------------- METHODES PRIVEES -- //
}

