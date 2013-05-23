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
import com.google.gwt.core.client.JavaScriptObject;

import fr.fg.client.core.Utilities;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.data.StructureData;

public class StructureModuleData extends JavaScriptObject {
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
		SHIPS_PAYLOAD_COST_COEF			= {0.50, 0.00, 0.50, 0.00, 0.40},
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
		PROD_PNJ_COEF					= {0.50, 0.00, 0.50, 0.00, 0.60};
	
	public final static String
		FIELD_TYPE = "a",
		FIELD_LEVEL = "b";
	
	public final static int
	COEF_PRODUCTION = 135;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected StructureModuleData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getType() /*-{
		return this[@fr.fg.client.data.StructureModuleData::FIELD_TYPE];
	}-*/;
	
	public final native int getLevel() /*-{
		return this[@fr.fg.client.data.StructureModuleData::FIELD_LEVEL];
	}-*/;
	
	public static double[] getCostCoef(int moduleType) {
		switch (moduleType) {
		case TYPE_HULL:
			return HULL_COST_COEF;
		case TYPE_RESOURCES_PAYLOAD:
			return RESOURCES_PAYLOAD_COST_COEF;
		case TYPE_HANGAR:
			return SHIPS_PAYLOAD_COST_COEF;
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
			throw new IllegalArgumentException("Invalid type: '" + moduleType + "'.");
		}
	}
	
	public static String getDesc(int structureType, int moduleType,
			int level, boolean upgrade) {
		String effect = ""; //$NON-NLS-1$
		
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		int currentProd =0;
		int nextProd =0;
		
		switch (moduleType) {
		case TYPE_HULL:
			// Augmentation points de structure
			int currentHull = StructureData.getDefaultHull(structureType);
			for (int i = 0; i < level; i++)
				currentHull = (int) Math.floor(1.1 * currentHull +
					0.6 * currentHull * Math.pow(.9, i));
			int nextHull = (int) Math.floor(1.1 * currentHull +
					0.6 * currentHull * Math.pow(.9, level));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
				Formatter.formatNumber(currentHull, true) + "&nbsp;" + (upgrade ? " ► " +
				Formatter.formatNumber(nextHull, true) + "&nbsp;" : "") +
				Utilities.getStatImage(Utilities.IMG_HULL) + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module0Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_RESOURCES_PAYLOAD:
			// Augmentation capacité
			double currentPayload = (((double) Math.floor(Math.pow(2.15, level) * 100000.)) / 1000.) * 1000.; //INT overflow at 2147M
			double nextPayload = (((double) Math.floor(Math.pow(2.15, level + 1) * 100000.)) / 1000.) * 1000.;//Same
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
				Formatter.formatNumber(currentPayload, true) + "&nbsp;" + (upgrade ? " ► " +
				Formatter.formatNumber(nextPayload, true) + "&nbsp;" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module1Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_HANGAR:
			// Augmentation nb vaisseaux
			int currentShipsLimit = (((int) (1000 * Math.pow(1.9, level))) / 100) * 100;
			int nextShipsLimit = (((int) (1000 * Math.pow(1.9, level + 1))) / 100) * 100;
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
				Formatter.formatNumber(currentShipsLimit, true) + "&nbsp;" + (upgrade ? " ► " +
				Formatter.formatNumber(nextShipsLimit, true) + "&nbsp;" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module2Prod1() + "</div>"; //$NON-NLS-1$
			
			// Accélération vitesse de constructin
			double currentShipProduction = ((int) (Math.pow(1.7, level) * 10)) / 10.;
			double nextShipProduction = ((int) (Math.pow(1.7, level + 1) * 10)) / 10.;
			
			effect += "<div class=\"emphasize\" style=\"clear: both;\">" +
				"<span style=\"float: right;\">x" +
				currentShipProduction + "&nbsp;" + (upgrade ? " ► x" +
				nextShipProduction + "&nbsp;" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module2Prod2() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_REPAIR:
			// Réparation automatique
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
				level + "&nbsp;" + (upgrade ? " ► " +
				(level + 1) + "&nbsp;" : "") +
				Utilities.getStatImage(Utilities.IMG_HULL) + "/h</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module3Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_SKILL_RELOAD:
			// Rechargement des compétences
			int currentBonus = 100 - (int) Math.round((Math.pow(0.88, level) * 100));
			int nextBonus = 100 - (int) Math.round((Math.pow(0.88, level + 1) * 100));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">-" +
				currentBonus + "%&nbsp;" + (upgrade ? " ► -" +
				nextBonus + "%&nbsp;" : "") +
				Utilities.getStatImage(Utilities.IMG_COOLDOWN) + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module4Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_WARP_FIELD:
			// Champ de distortion
			currentBonus = 100 - ((int) Math.round(Math.pow(.92, level + 1) * 100));
			nextBonus = 100 - ((int) Math.round(Math.pow(.92, level + 2) * 100));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">-" +
				currentBonus + "%&nbsp;" + (upgrade ? " ► -" +
				nextBonus + "%&nbsp;" : "") +
				Utilities.getStatImage(Utilities.IMG_COOLDOWN) + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.module5Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_REACTOR:
			int currentEnergy = (int) Math.floor(25 * Math.pow(1.2, level));
			int nextEnergy = (int) Math.floor(25 * Math.pow(1.2, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
				currentEnergy + "&nbsp;" + (upgrade ? " ► " +
				nextEnergy + "&nbsp;" : "") + Utilities.getEnergyImage() +
				"</span>" + dynamicMessages.module6Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_TITANE:
			currentProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level));
			nextProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getResourceImage(0) +
				"</span>" + dynamicMessages.module13Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_CRISTAL:
			currentProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level));
			nextProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getResourceImage(1) +
				"</span>" + dynamicMessages.module14Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_ANDIUM:
			currentProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level));
			nextProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getResourceImage(2) +
				"</span>" + dynamicMessages.module15Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_ANTIMATIERE:
			currentProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level));
			nextProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getResourceImage(3) +
				"</span>" + dynamicMessages.module16Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_CREDIT:
			currentProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level));
			nextProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getCreditsImage() +
				"</span>" + dynamicMessages.module17Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_IDEA:
			currentProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level));
			nextProd = (int) Math.floor(COEF_PRODUCTION * Math.pow(1.8, level + 1));
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getIdeaImage() +
				"</span>" + dynamicMessages.module18Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_LVL_PNJ:
			currentProd = 2+ 2*level;
			nextProd = 4 + 2*level;
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + Utilities.getStatImage("power") +
				"</span>" + dynamicMessages.module20Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_NUMBER_PNJ:
			currentProd = 1+ level;
			nextProd = 2 + level;
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") +
				"</span>" + dynamicMessages.module21Prod() + "</div>"; //$NON-NLS-1$
			break;
		case TYPE_PROD_PNJ:
			currentProd = (int) (0.12 * level*100);
			nextProd =(int) (0.12 * (level+1)*100);
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" +
			Formatter.formatNumber(currentProd,true) + "&nbsp;" + (upgrade ? " ► " +
					Formatter.formatNumber(nextProd,true) + "&nbsp;" : "") + "%" +
				"</span>" + dynamicMessages.module22Prod() + "</div>"; //$NON-NLS-1$
			break;
		}
		
		return "<div class=\"title\">" + dynamicMessages.getString("module" +
			moduleType) + " niv. " + (upgrade ? level + 1 : level) + "</div>" +
			"<div class=\"justify\">" + dynamicMessages.getString("module" +
			moduleType + "Desc") + "</div>" + effect;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
