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

import fr.fg.server.data.base.StructureModuleBase;

public class StructureModule extends StructureModuleBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_HULL = 0,
		TYPE_RESOURCES_PAYLOAD = 1,
		TYPE_HANGAR = 2,
		TYPE_REPAIR = 3,
		TYPE_SKILL_RELOAD = 4,
		TYPE_WARP_FIELD = 5,
		TYPE_REACTOR = 6,
		TYPE_DECK_FIGHTER = 7,
		TYPE_DECK_CORVETTE = 8,
		TYPE_DECK_FRIGATE = 9,
		TYPE_DECK_DESTROYER = 10,
		TYPE_DECK_DREADNOUGHT = 11,
		TYPE_DECK_BATTLECRUISER = 12,
		TYPE_PROD_TITANE = 13,
		TYPE_PROD_CRISTAL = 14,
		TYPE_PROD_ANDIUM = 15,
		TYPE_PROD_ANTIMATIERE = 16,
		TYPE_PROD_CREDIT = 17,
		TYPE_PROD_IDEA = 18,
		TYPE_LVL_PNJ = 20,
		TYPE_NUMBER_PNJ = 21,
		TYPE_PROD_PNJ = 22;
	
	private final static double[]
	    HULL_COST_COEF					= {0.75, 0.00, 0.25, 0.00, 0.40},
	    RESOURCES_PAYLOAD_COST_COEF		= {0.00, 0.75, 0.25, 0.00, 0.40},
	    HANGAR_COST_COEF				= {0.50, 0.00, 0.50, 0.00, 0.40},
		REPAIR_COST_COEF				= {0.00, 0.00, 3.00, 0.00, 1.20},
		SKILL_RELOAD_COST_COEF			= {0.00, 0.00, 0.00, 1.00, 0.40},
		WARP_FIELD_COST_COEF			= {0.00, 0.50, 0.00, 0.00, 0.20},
		REACTOR_COST_COEF				= {0.50, 0.50, 0.00, 0.00, 0.40},
		DECK_FIGHTER_COST_COEF			= {0.00, 1.00, 0.00, 0.00, 0.40},
		DECK_CORVETTE_COST_COEF			= {0.00, 10.00, 0.00, 0.00, 4.00},
		DECK_FRIGATE_COST_COEF			= {50.00, 50.00, 0.00, 0.00, 40.00},
		DECK_DESTROYER_COST_COEF		= {500.00, 500.00, 0.00, 0.00, 400.00},
		DECK_DREADNOUGHT_COST_COEF		= {3000.00, 3000.00, 3000.00, 0.00, 4000.00},
		DECK_BATTLECRUISER_COST_COEF	= {30000.00, 30000.00, 30000.00, 0.00, 40000.00},
		PROD_TITANE_COEF				= {0.00, 0.75, 0.25, 0.00, 0.60},
		PROD_CRISTAL_COEF				= {0.25, 0.00, 0.75, 0.00, 0.60},
		PROD_ANDIUM_COEF				= {0.75, 0.25, 0.00, 0.00, 0.60},
		PROD_ANTIMATIERE_COEF			= {0.90, 0.00, 0.40, 0.00, 0.80},
		PROD_CREDIT_COEF				= {0.50, 0.50, 0.00, 0.00, 0.60},
		PROD_IDEA_COEF					= {0.00, 0.00, 1.00, 0.00, 0.60},
		LVL_PNJ_COEF					= {1.00, 0.00, 0.00, 0.00, 0.70},
		NUMBER_PNJ_COEF					= {0.50, 0.50, 0.00, 0.00, 0.70},
		PROD_PNJ_COEF					= {0.50, 0.00, 0.50, 0.00, 0.40};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StructureModule() {
		// Nécessaire pour la construction par réflection
	}
	
	public StructureModule(long idStructure, int type, int level) {
		setIdStructure(idStructure);
		setType(type);
		setLevel(level);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static int getMaxLevel(int type) {
		switch (type) {
		case TYPE_DECK_FIGHTER:
		case TYPE_DECK_CORVETTE:
		case TYPE_DECK_FRIGATE:
		case TYPE_DECK_DESTROYER:
		case TYPE_DECK_DREADNOUGHT:
		case TYPE_DECK_BATTLECRUISER:
			return 1;
		default:
			return 99;
		}
	}
	
	public static double[] getCostCoef(int type) {
		switch (type) {
		case TYPE_HULL:
			return HULL_COST_COEF;
		case TYPE_RESOURCES_PAYLOAD:
			return RESOURCES_PAYLOAD_COST_COEF;
		case TYPE_HANGAR:
			return HANGAR_COST_COEF;
		case TYPE_REPAIR:
			return REPAIR_COST_COEF;
		case TYPE_SKILL_RELOAD:
			return SKILL_RELOAD_COST_COEF;
		case TYPE_WARP_FIELD:
			return WARP_FIELD_COST_COEF;
		case TYPE_REACTOR:
			return REACTOR_COST_COEF;
		case TYPE_DECK_FIGHTER:
			return DECK_FIGHTER_COST_COEF;
		case TYPE_DECK_CORVETTE:
			return DECK_CORVETTE_COST_COEF;
		case TYPE_DECK_FRIGATE:
			return DECK_FRIGATE_COST_COEF;
		case TYPE_DECK_DESTROYER:
			return DECK_DESTROYER_COST_COEF;
		case TYPE_DECK_DREADNOUGHT:
			return DECK_DREADNOUGHT_COST_COEF;
		case TYPE_DECK_BATTLECRUISER:
			return DECK_BATTLECRUISER_COST_COEF;
		case TYPE_PROD_TITANE:
			return PROD_TITANE_COEF;
		case TYPE_PROD_CRISTAL:
			return PROD_CRISTAL_COEF;
		case TYPE_PROD_ANDIUM:
			return PROD_ANDIUM_COEF;
		case TYPE_PROD_ANTIMATIERE:
			return PROD_ANTIMATIERE_COEF;
		case TYPE_PROD_CREDIT:
			return PROD_CREDIT_COEF;
		case TYPE_PROD_IDEA:
			return PROD_IDEA_COEF;
		case TYPE_LVL_PNJ:
			return LVL_PNJ_COEF;
		case TYPE_NUMBER_PNJ:
			return NUMBER_PNJ_COEF;
		case TYPE_PROD_PNJ:
			return PROD_PNJ_COEF;
		default:
			throw new IllegalArgumentException("Invalid type: '" + type + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
