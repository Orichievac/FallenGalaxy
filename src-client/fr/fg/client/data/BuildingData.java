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

import fr.fg.client.core.Utilities;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;

public class BuildingData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		LABORATORY = "laboratory", //$NON-NLS-1$
		STOREHOUSE = "storehouse", //$NON-NLS-1$
		SPACESHIP_YARD = "spaceship_yard", //$NON-NLS-1$
		DEFENSIVE_DECK = "defensive_deck", //$NON-NLS-1$
		EXPLOITATION = "exploitation", //$NON-NLS-1$
		EXPLOITATION0 = "exploitation0", //$NON-NLS-1$
		EXPLOITATION1 = "exploitation1", //$NON-NLS-1$
		EXPLOITATION2 = "exploitation2", //$NON-NLS-1$
		EXPLOITATION3 = "exploitation3", //$NON-NLS-1$
		EXTRACTOR_CENTER = "extractor_center", //$NON-NLS-1$
		CIVILIAN_INFRASTRUCTURES = "civilian_infrastructures", //$NON-NLS-1$
		CORPORATIONS = "corporations", //$NON-NLS-1$
		TRADE_PORT = "trade_port", //$NON-NLS-1$
		RESEARCH_CENTER = "research_center", //$NON-NLS-1$
		FACTORY = "factory", //$NON-NLS-1$
		REFINERY = "refinery"; //$NON-NLS-1$
	
	public final static String[] BUILDINGS = {
		LABORATORY,
		STOREHOUSE,
		SPACESHIP_YARD,
		DEFENSIVE_DECK,
		EXPLOITATION0,
		EXPLOITATION1,
		EXPLOITATION2,
		EXPLOITATION3,
		EXTRACTOR_CENTER,
		CIVILIAN_INFRASTRUCTURES,
		CORPORATIONS,
		TRADE_PORT,
		RESEARCH_CENTER,
		FACTORY,
		REFINERY
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
	
	public final static double EXPLOITATION_RATE = 0.01666666666;
	
	public final static double RESEARCH_RATE = 0.01666666666;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	// Renvoie la classe CSS d'un type de bâtiment
	public static String getClassName(String building) {
		if (building.equals(SPACESHIP_YARD))
			return "spaceshipYard"; //$NON-NLS-1$
		else if (building.equals(DEFENSIVE_DECK))
			return "defensiveDeck"; //$NON-NLS-1$
		else if (building.equals(RESEARCH_CENTER))
			return "researchCenter"; //$NON-NLS-1$
		else if (building.equals(EXTRACTOR_CENTER))
			return "extractorCenter"; //$NON-NLS-1$
		else if (building.equals(CIVILIAN_INFRASTRUCTURES))
			return "civilianInfrastructures"; //$NON-NLS-1$
		else if (building.equals(TRADE_PORT))
			return "tradePort"; //$NON-NLS-1$
		else
			return building;
	}
	
	// Renvoie la description d'un bâtiment
	public static String getDesc(String building, int level, boolean upgrade) {
		if (upgrade)
			level--;
		
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		String className = getClassName(building);
		String desc = "<div class=\"justify\">" + //$NON-NLS-1$
			dynamicMessages.getString(className + "Desc") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		
		String effect = ""; //$NON-NLS-1$
		
		if (building.equals(LABORATORY) ||
				building.equals(EXPLOITATION0) ||
				building.equals(EXPLOITATION1) ||
				building.equals(EXPLOITATION2) ||
				building.equals(EXPLOITATION3)) {
			String resource = building.equals(LABORATORY) ? "research" : //$NON-NLS-1$
				"r" + (Integer.parseInt("" + building.charAt(EXPLOITATION.length())) + 1); //$NON-NLS-1$ //$NON-NLS-2$
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">+&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(Math.pow(5, level), true) + (upgrade ? " ► +&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(Math.pow(5, level + 1), true) : "") + //$NON-NLS-1$
				"&nbsp;" + Utilities.parseSmilies(":" + resource + ":") + "</span>" + //$NON-NLS-1$
				dynamicMessages.getString(className + "Prod") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (building.equals(CIVILIAN_INFRASTRUCTURES)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">+&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(Math.pow(5, level), true) + "&nbsp;M" + (upgrade ? " ► +&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(Math.pow(5, level + 1), true) + "&nbsp;M" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.civilianInfrastructuresProd1() + "</div>" + //$NON-NLS-1$
				"<div class=\"emphasize\" style=\"clear: both;\"><span style=\"float: right;\">+&nbsp;" + //$NON-NLS-1$
				(Math.pow(5, level) / 10.) + "&nbsp;M" + (upgrade ? " ► +&nbsp;" + //$NON-NLS-1$
				(Math.pow(5, level + 1) / 10.) + "&nbsp;M" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.civilianInfrastructuresProd2() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(STOREHOUSE)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" + //$NON-NLS-1$
				Formatter.formatNumber(10000 * Math.pow(10, level), true) + (upgrade ? " ► " + //$NON-NLS-1$
				Formatter.formatNumber(10000 * Math.pow(10, level + 1), true) : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.storehouseProd() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(SPACESHIP_YARD)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" + //$NON-NLS-1$
				dynamicMessages.getString("shipClass" + (level + 2)) + (upgrade ? " ► " + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.getString("shipClass" + (level + 3)) : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dynamicMessages.spaceshipYardProd1() + "</div>" + //$NON-NLS-1$
				"<div class=\"emphasize\" style=\"clear: both;\"><span style=\"float: right;\">x&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(Math.pow(5, level), true) + (upgrade ? " ► x&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(Math.pow(5, level + 1), true) : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$
				dynamicMessages.spaceshipYardProd2() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(EXTRACTOR_CENTER)) {
			int[] production = {90, 450, 2250, 11250, 56250};
			
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" + //$NON-NLS-1$
				Formatter.formatNumber(production[level], true) + (upgrade ?
				" ► " + Formatter.formatNumber(production[level + 1], true) : "") + //$NON-NLS-1$ //$NON-NLS-2$
				"&nbsp;/&nbsp;h</span>" + dynamicMessages.extractorCenterProd() + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (building.equals(TRADE_PORT)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">-" + //$NON-NLS-1$
				(5 * (level + 1)) + (upgrade ? "&nbsp;% ► -" + (5 * (level + 2)) : "") + "&nbsp;%" + //$NON-NLS-1$ //$NON-NLS-2$
				"&nbsp;" + Utilities.parseSmilies(":cr:") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dynamicMessages.tradePortProd() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(CORPORATIONS)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">" + //$NON-NLS-1$
				(100 + level * 25) + "&nbsp;%" + (upgrade ? " ► " + //$NON-NLS-1$ //$NON-NLS-2$
				(100 + (level + 1) * 25) + "&nbsp;%" : "") + //$NON-NLS-1$ //$NON-NLS-2$
				"&nbsp;" + Utilities.parseSmilies(":cr:") + "</span>" + //$NON-NLS-1$
				dynamicMessages.corporationsProd() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(REFINERY)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">+&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(10 * (level + 1), true) + "&nbsp;%" + (upgrade ? " ► +&nbsp;" + //$NON-NLS-1$ //$NON-NLS-2$
				Formatter.formatNumber(10 * (level + 2), true) + "&nbsp;%" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dynamicMessages.refineryProd() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(RESEARCH_CENTER)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">+&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(10 * (level + 1), true) + "&nbsp;%" + (upgrade ? " ► +&nbsp;" + //$NON-NLS-1$ //$NON-NLS-2$
				Formatter.formatNumber(10 * (level + 2), true) + "&nbsp;%" : "") + //$NON-NLS-1$ //$NON-NLS-2$
				"&nbsp;" + Utilities.parseSmilies(":research:") + "</span>" + //$NON-NLS-1$
				dynamicMessages.researchCenterProd() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(FACTORY)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">-&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(15 * (level + 1), true) + "&nbsp;%" + (upgrade ? " ► -&nbsp;" + //$NON-NLS-1$ //$NON-NLS-2$
				Formatter.formatNumber(15 * (level + 2), true) + "&nbsp;%" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dynamicMessages.factoryProd() + "</div>"; //$NON-NLS-1$
		} else if (building.equals(DEFENSIVE_DECK)) {
			effect = "<div class=\"emphasize\"><span style=\"float: right;\">+&nbsp;" + //$NON-NLS-1$
				Formatter.formatNumber(5 * (level + 1), true) + "&nbsp;%" + (upgrade ? " ► +&nbsp;" + //$NON-NLS-1$ //$NON-NLS-2$
				Formatter.formatNumber(5 * (level + 2), true) + "&nbsp;%" : "") + "</span>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dynamicMessages.defensiveDeckProd() + "</div>"; //$NON-NLS-1$
		}
		
		return desc + effect;
	}
	
	// Renvoie le nom d'un batiment
	public static String getName(String building, int level) {
		StaticMessages staticMessages =
			(StaticMessages) GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		return dynamicMessages.getString(getClassName(building)) + " " + //$NON-NLS-1$
			staticMessages.buildLevel(level + 1);
	}
	
	public static int getBuildTime(String type, int level) {
		if (type.equals(STOREHOUSE) ||
				type.equals(DEFENSIVE_DECK) ||
				type.equals(FACTORY)) {
			switch (level) {
			case 0:
				return 300 / Settings.getTimeUnit();
			case 1:
				return 5400 / Settings.getTimeUnit();
			case 2:
				return 21600 / Settings.getTimeUnit();
			case 3:
				return 86400 / Settings.getTimeUnit();
			case 4:
				return 216000 / Settings.getTimeUnit();
			default:
				throw new IllegalArgumentException(
						"Level value out of range: '" + level + "'");
			}
		} else if (type.equals(LABORATORY) ||
				type.equals(SPACESHIP_YARD) ||
				type.equals(EXPLOITATION0) ||
				type.equals(EXPLOITATION1) ||
				type.equals(EXPLOITATION2) ||
				type.equals(EXPLOITATION3) ||
				type.equals(CIVILIAN_INFRASTRUCTURES)) {
			switch (level) {
			case 0:
				return 600 / Settings.getTimeUnit(); // 10min
			case 1:
				return 10800 / Settings.getTimeUnit(); // 3h
			case 2:
				return 43200 / Settings.getTimeUnit(); // 12h
			case 3:
				return 172800 / Settings.getTimeUnit(); // 2j
			case 4:
				return 432000 / Settings.getTimeUnit(); // 5j
			default:
				throw new IllegalArgumentException(
						"Level value out of range: '" + level + "'");
			}
		} else if (type.equals(EXTRACTOR_CENTER) ||
				type.equals(CORPORATIONS) ||
				type.equals(TRADE_PORT) ||
				type.equals(RESEARCH_CENTER) ||
				type.equals(REFINERY)) {
			switch (level) {
			case 0:
				return 900 / Settings.getTimeUnit();
			case 1:
				return 16200 / Settings.getTimeUnit();
			case 2:
				return 64800 / Settings.getTimeUnit();
			case 3:
				return 259200 / Settings.getTimeUnit();
			case 4:
				return 648000 / Settings.getTimeUnit();
			default:
				throw new IllegalArgumentException(
						"Level value out of range: '" + level + "'");
			}
		} else {
			throw new IllegalArgumentException(
					"Invalid building type: '" + type + "'");
		}
	}
	
    public static double getProduction(String type, PlayerStarSystemData systemData) {
    	if (type.equals(STOREHOUSE)) {
    		return PlayerStarSystemData.SYSTEM_STORAGE_CAPACITY +
    			    10000 * systemData.getBuildingsCount(type, 0) +
    			   100000 * systemData.getBuildingsCount(type, 1) +
    			  1000000 * systemData.getBuildingsCount(type, 2) +
    			 10000000 * systemData.getBuildingsCount(type, 3) +
    			100000000 * systemData.getBuildingsCount(type, 4);
    	} else if (type.equals(CORPORATIONS)) {
    		return
    			1.00 * systemData.getBuildingsCount(type, 0) +
				1.25 * systemData.getBuildingsCount(type, 1) +
				1.50 * systemData.getBuildingsCount(type, 2) +
				1.75 * systemData.getBuildingsCount(type, 3) +
				2.00 * systemData.getBuildingsCount(type, 4);
    	} else if (type.equals(FACTORY)) {
    		return 1 -
    			.15 * systemData.getBuildingsCount(type, 0) -
    			.30 * systemData.getBuildingsCount(type, 1) -
    			.45 * systemData.getBuildingsCount(type, 2) -
    			.60 * systemData.getBuildingsCount(type, 3) -
    			.75 * systemData.getBuildingsCount(type, 4);
    	} else if (type.equals(RESEARCH_CENTER) || type.equals(REFINERY)) {
    		return 1 +
    			.1 * systemData.getBuildingsCount(type, 0) +
    			.2 * systemData.getBuildingsCount(type, 1) +
    			.3 * systemData.getBuildingsCount(type, 2) +
    			.4 * systemData.getBuildingsCount(type, 3) +
    			.5 * systemData.getBuildingsCount(type, 4);
    	} else if (type.equals(CIVILIAN_INFRASTRUCTURES)) {
    		return PlayerStarSystemData.SYSTEM_POPULATION_CAPACITY +
    			 1 * systemData.getBuildingsCount(type, 0) +
				 5 * systemData.getBuildingsCount(type, 1) +
				25 * systemData.getBuildingsCount(type, 2) +
			   125 * systemData.getBuildingsCount(type, 3) +
			   625 * systemData.getBuildingsCount(type, 4);
    	} else if (type.equals(EXTRACTOR_CENTER)) {
			return
				90 * systemData.getBuildingsCount(type, 0) +
			   450 * systemData.getBuildingsCount(type, 1) +
			  2250 * systemData.getBuildingsCount(type, 2) +
			 11250 * systemData.getBuildingsCount(type, 3) +
			 56250 * systemData.getBuildingsCount(type, 4);
    	} else if (type.equals(DEFENSIVE_DECK)) {
			return 1 + (
				 5 * systemData.getBuildingsCount(type, 0) +
				10 * systemData.getBuildingsCount(type, 1) +
				15 * systemData.getBuildingsCount(type, 2) +
				20 * systemData.getBuildingsCount(type, 3) +
			    25 * systemData.getBuildingsCount(type, 4)) / 100.;
    	} else if (type.equals(TRADE_PORT)) {
			return 1 - (
			   .05 * systemData.getBuildingsCount(type, 0) +
			   .10 * systemData.getBuildingsCount(type, 1) +
			   .15 * systemData.getBuildingsCount(type, 2) +
			   .20 * systemData.getBuildingsCount(type, 3) +
			   .25 * systemData.getBuildingsCount(type, 4));
    	} else {
    		return   systemData.getBuildingsCount(type, 0) +
    			 5 * systemData.getBuildingsCount(type, 1) +
    			25 * systemData.getBuildingsCount(type, 2) +
    		   125 * systemData.getBuildingsCount(type, 3) +
    		   625 * systemData.getBuildingsCount(type, 4);
    	}
    }
    
	public static int[] getCost(String type, int level) {
		if (type.equals(LABORATORY))
			return COST_LABORATORY[level];
		else if (type.equals(STOREHOUSE))
			return COST_STOREHOUSE[level];
		else if (type.equals(SPACESHIP_YARD))
			return COST_SPACESHIP_YARD[level];
		else if (type.equals(DEFENSIVE_DECK))
			return COST_DEFENSIVE_DECK[level];
		else if (type.equals(EXPLOITATION0))
			return COST_EXPLOITATION0[level];
		else if (type.equals(EXPLOITATION1))
			return COST_EXPLOITATION1[level];
		else if (type.equals(EXPLOITATION2))
			return COST_EXPLOITATION2[level];
		else if (type.equals(EXPLOITATION3))
			return COST_EXPLOITATION3[level];
		else if (type.equals(EXTRACTOR_CENTER))
			return COST_EXTRACTOR_CENTER[level];
		else if (type.equals(CIVILIAN_INFRASTRUCTURES))
			return COST_CIVILIAN_INFRASTRUCTURES[level];
		else if (type.equals(CORPORATIONS))
			return COST_CORPORATIONS[level];
		else if (type.equals(TRADE_PORT))
			return COST_TRADE_PORT[level];
		else if (type.equals(RESEARCH_CENTER))
			return COST_RESEARCH_CENTER[level];
		else if (type.equals(FACTORY))
			return COST_FACTORY[level];
		else if (type.equals(REFINERY))
			return COST_REFINERY[level];
		else
			return null;
	}
	
	public static int[] getRequiredTechnologies(String type, int level) {
		// Les batiments niveau 1 et 2 ne nécessitent pas de technologies
		if (level <= 1)
			return new int[0];
		
		int[] technologies = new int[0];
		
		if (type.equals(STOREHOUSE) ||
				type.equals(CIVILIAN_INFRASTRUCTURES))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_1_SUPER_STRUCTURES,
				TechnologyData.TECHNOLOGY_3_PLANETARY_CONSTRUCTION,
				TechnologyData.TECHNOLOGY_5_ARCOLOGY};
		else if (type.equals(SPACESHIP_YARD))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_2_ORBITAL_ENGINEERING,
				TechnologyData.TECHNOLOGY_4_SPACE_ENGINEERING,
				TechnologyData.TECHNOLOGY_6_MAGELLAN_PROGRAM};
		else if (type.equals(DEFENSIVE_DECK))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_1_QUANTUM_COMPUTER,
				TechnologyData.TECHNOLOGY_3_ARTIFICIAL_INTELLIGENCE,
				TechnologyData.TECHNOLOGY_5_INTELLIGENT_MACHINES};
		else if (type.equals(REFINERY))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_2_ADV_COMPOSITE_MATERIALS,
				TechnologyData.TECHNOLOGY_4_INTELLIGENT_MATERIALS,
				TechnologyData.TECHNOLOGY_6_WEATHER_CONTROL};
		else if (type.equals(RESEARCH_CENTER))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_0_QUANTUM_PHYSICS,
				TechnologyData.TECHNOLOGY_3_APPLIED_RELATIVITY,
				TechnologyData.TECHNOLOGY_5_UNIFIED_PHYSICS};
		else if (type.equals(FACTORY))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_1_MECANIZED_WORKFORCE,
				TechnologyData.TECHNOLOGY_3_NANITES,
				TechnologyData.TECHNOLOGY_5_INTELLIGENT_MACHINES};
		else if (type.equals(LABORATORY))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_0_QUANTUM_PHYSICS,
				TechnologyData.TECHNOLOGY_3_APPLIED_RELATIVITY,
				TechnologyData.TECHNOLOGY_5_UNIFIED_PHYSICS};
		else if (type.equals(EXPLOITATION0) ||
				type.equals(EXPLOITATION1) ||
				type.equals(EXPLOITATION2) ||
				type.equals(EXPLOITATION3))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_1_MECANIZED_WORKFORCE,
				TechnologyData.TECHNOLOGY_3_NANITES,
				TechnologyData.TECHNOLOGY_5_INTELLIGENT_MACHINES};
		else if (type.equals(TRADE_PORT) ||
				type.equals(CORPORATIONS))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_2_EXOPLANETARY_ECONOMICS,
				TechnologyData.TECHNOLOGY_4_EXONOMETRICS_THEORY,
				TechnologyData.TECHNOLOGY_6_HARMONIZED_EXONOMETRICS};
		else if (type.equals(EXTRACTOR_CENTER))
			technologies = new int[]{
				TechnologyData.TECHNOLOGY_2_BIOMECHANICS,
				TechnologyData.TECHNOLOGY_4_QUANTUM_ENGINEERING,
				TechnologyData.TECHNOLOGY_6_WEATHER_CONTROL};
		
		return technologies.length > 0 ?
				new int[]{technologies[level - 2]} : new int[0];
	}
	
    public static int getRequiredSpaceshipYardLevel(int shipClass) {
    	switch (shipClass) {
    	case ShipData.FIGHTER:
    	case ShipData.CORVETTE:
    	case ShipData.FREIGHTER:
    		return 0;
    	case ShipData.FRIGATE:
    		return 1;
    	case ShipData.DESTROYER:
    		return 2;
    	case ShipData.DREADNOUGHT:
    		return 3;
    	case ShipData.BATTLECRUISER:
    		return 4;
    	default:
    		throw new IllegalArgumentException(
    				"Invalid ship class: '" + shipClass + "'.");
    	}

    }
    
	// ------------------------------------------------- METHODES PRIVEES -- //
}
