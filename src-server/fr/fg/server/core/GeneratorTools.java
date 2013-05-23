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

package fr.fg.server.core;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.fg.server.data.Area;
import fr.fg.server.data.Bank;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Planet;
import fr.fg.server.data.Player;
import fr.fg.server.data.RandomName;
import fr.fg.server.data.Sector;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Tradecenter;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.ArrayUtils;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class GeneratorTools {
	// ------------------------------------------------------- CONSTANTES -- //

	private final static int
		NEBULA_IMAGES_COUNT = 8,
		STAR_IMAGES_COUNT = 15,
		MIN_PLANET_DISTANCE = 60,
		MAX_PLANET_DISTANCE = 190,
		PLANET_SIZE = 30,
		ORGANIZATION_NAMES_COUNT = 20;
	
	private final static int
		PARAMETER_DENSITY = 70,
		PARAMETER_LOCATION_GAP = 5, // Espace minimum entre 2 zones
		PARAMETER_HYPERWAY = 10;
	
	private final static double[] PLANET_ROTATION_SPEEDS = {
		Math.PI / 256,	Math.PI / 284,	Math.PI / 348,	Math.PI / 768,
		Math.PI / 1024,	Math.PI / 1536,	Math.PI / 2048
	};
	
	private final static int[][][] PLANET_IMAGES = {
		{{0, 19}},
		{{0, 14}, {5, 19}},
		{{0, 11}, {4, 15}, {8, 19}},
		{{0, 9}, {3, 12}, {7, 16}, {10, 19}},
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static boolean initialized = false;
	
	private static List<RandomName> locationNames;
	
	private static List<String> organizationNames;

	private static List<Integer> nebulas;
	
	private static int currentLocationName, currentOrganizationName, currentNebula;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static void createGalaxy(int sectorsCount) {
		// Mélange les noms aléatoires
		initRandomLists();
		
		// Position des quadrants
		ArrayList<Location> sectorsLocations = generateLocations(
			sectorsCount,
			PARAMETER_DENSITY,
			PARAMETER_LOCATION_GAP,
			PARAMETER_HYPERWAY);
		
		// Types de quadrants
		int[] sectorsType = new int[sectorsCount];
		for (int i = 0; i < sectorsCount; i++) {
			sectorsType[i] = Sector.SECTOR_START;
			
			if ((i % 15 == 5) || (i % 15 == 6) || (i % 15 == 4))
				sectorsType[i] = Sector.SECTOR_COLONIES_10_20;
			
			if ((i % 15 == 7) || (i % 15 == 8))
				sectorsType[i] = Sector.SECTOR_COLONIES_20_30;
			
			if ((i % 15 == 9) || (i % 15 == 10))
				sectorsType[i] = Sector.SECTOR_COLONIES_30_40;
			
			if ((i % 15 == 11) || (i % 15 == 12))
				sectorsType[i] = Sector.SECTOR_COLONIES_40_50;
			
			if ((i % 15 == 13))
				sectorsType[i] = Sector.SECTOR_COLONIES_50_60;
			
			
		}
		
		for (int i = 0; i < sectorsCount; i++) {
			createSector(sectorsType[i], sectorsLocations.get(i));
			
			LoggingSystem.getServerLogger().trace("Sector " + (i + 1) + "/" +
					sectorsCount + " created.");
		}

		clearRandomLists();
	}
	
	public static Sector createSector(int sectorType, Location sectorLocation) {
		boolean cleanUp = !initialized;
		if (!initialized)
			initRandomLists();
		
		int nebula = getRandomNebula();
		
		Sector sector = new Sector(
			getRandomLocationName(),
			sectorLocation.getX(),
			sectorLocation.getY(),
			sectorType,
			nebula);
		sector.save();
		
		// Nombre et types de secteurs dans le quadrant
		int areasCount, areasType[];
		
		switch (sectorType) {
		case Sector.SECTOR_START:
			areasCount = 30;
			areasType = new int[areasCount];
			
			for (int j = 0; j < areasCount; j++) {
				if(j==0) areasType[j] = Area.AREA_MINING_0_5;
				else if (j==1) areasType[j] = Area.AREA_BANK_0_10;
				else if(j==2) areasType[j] = Area.AREA_MINING_5_10;
				else if (j<22) areasType[j] = Area.AREA_START;
				else areasType[j] = Area.AREA_BATTLE_0_10;
			}
			break;
		case Sector.SECTOR_COLONIES_10_20:
			areasCount = 30;
			areasType = new int[areasCount];
			
			for (int j = 0; j < areasCount; j++) {
				if(j==0) areasType[j] = Area.AREA_MINING_10_15;
				else if (j==1) areasType[j] = Area.AREA_BANK_10_20;
				else if(j==2) areasType[j] = Area.AREA_MINING_15_20;
				else if (j<20) areasType[j] = Area.AREA_COLONY_10_20;
				else areasType[j] = Area.AREA_BATTLE_10_20;
			}
			break;
		case Sector.SECTOR_COLONIES_20_30:
			areasCount = 30;
			areasType = new int[areasCount];
			
			for (int j = 0; j < areasCount; j++) {
				if(j==0) areasType[j] = Area.AREA_MINING_20_25;
				else if (j==1) areasType[j] = Area.AREA_BANK_20_30;
				else if(j==2) areasType[j] = Area.AREA_MINING_25_30;
				else if (j<18) areasType[j] = Area.AREA_COLONY_20_30;
				else areasType[j] = Area.AREA_BATTLE_20_30;
			}
			break;
		case Sector.SECTOR_COLONIES_30_40:
			areasCount = 30;
			areasType = new int[areasCount];
			
			for (int j = 0; j < areasCount; j++) {
				if(j==0) areasType[j] = Area.AREA_MINING_30_35;
				else if (j==1) areasType[j] = Area.AREA_BANK_30_40;
				else if(j==2) areasType[j] = Area.AREA_MINING_35_40;
				else if (j<16) areasType[j] = Area.AREA_COLONY_30_40;
				else areasType[j] = Area.AREA_BATTLE_30_40;
			}
			break;
		case Sector.SECTOR_COLONIES_40_50:
			areasCount = 30;
			areasType = new int[areasCount];
			
			for (int j = 0; j < areasCount; j++) {
				if(j==0) areasType[j] = Area.AREA_MINING_40_45;
				else if (j==1) areasType[j] = Area.AREA_BANK_40_50;
				else if(j==2) areasType[j] = Area.AREA_MINING_45_50;
				else if (j<14) areasType[j] = Area.AREA_COLONY_40_50;
				else areasType[j] = Area.AREA_BATTLE_40_50;
			}
			break;
		case Sector.SECTOR_COLONIES_50_60:
			areasCount = 30;
			areasType = new int[areasCount];
			
			for (int j = 0; j < areasCount; j++) {
				if(j==0) areasType[j] = Area.AREA_MINING_50_55;
				else if (j==1) areasType[j] = Area.AREA_BANK_50_60;
				else if(j==2) areasType[j] = Area.AREA_MINING_55_60;
				else if (j<12) areasType[j] = Area.AREA_COLONY_50_60;
				else areasType[j] = Area.AREA_BATTLE_50_60;
			}
			break;
		default:
			throw new IllegalArgumentException(
				"Invalid sector type: '" + sectorType + "'.");
		}
		
		// Génère la position des secteurs
		ArrayList<Location> areasLocations = generateLocations(
				areasCount,
				PARAMETER_DENSITY,
				PARAMETER_LOCATION_GAP,
				PARAMETER_HYPERWAY);
		
		for (int j = 0; j < areasLocations.size(); j++) {
			createArea(areasType[j], areasLocations.get(j), sector.getId());
		}
		
		if (cleanUp)
			clearRandomLists();
		
		return sector;
	}
	
	public static Area createArea(int areaType, int x, int y, int idSector)
	{
		return createArea(areaType, new Location(x,y), idSector);
	}
	
	public static Area createArea(int areaType, Location areaLocation, int idSector) {
		boolean cleanUp = !initialized;
		if (!initialized)
			initRandomLists();
		
		// Génère l'IA pour la campagne
		Player campaignNPC = null;
		
		int areaWidth, areaHeight, systemsCount, gatesCount,
			tradeCentersCount, banksCount, spaceStationsLimit,
			gravityWellCount;
		double tradeCentersVariation, tradeCentersFees,
			banksBaseRate, banksVariableRate;
		long banksFees;
		
		switch (areaType) {
		case Area.AREA_START:
			// Secteur de départ
			areaWidth = 140 + 20 * Utilities.random(0, 2);
			areaHeight = 140 + 20 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0014);
			gatesCount = 1;
			tradeCentersCount = 1;
			tradeCentersVariation = .000001;
			tradeCentersFees = .05;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			
			
			// Organisation de départ dans laquelle le joueur commence
			String organizationName = getRandomOrganizationName();
			
			campaignNPC = DataAccess.getPlayerByLogin(Messages.getString(organizationName));
			if (campaignNPC == null) {
				campaignNPC = new Player(Messages.getString(organizationName), "", "", "", "");
				campaignNPC.setAi(true);
				campaignNPC.setXp(Player.getLevelXp(5));
				campaignNPC.save();
			}
			break;
		case Area.AREA_MINING_0_5:
			// Secteur pirate de départ
			areaWidth = 440 + 40 * Utilities.random(0, 3);
			areaHeight = 440 + 40 * Utilities.random(0, 3);
			systemsCount = 8 + Utilities.random(0, 4);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_5_10:
			// Secteur pirate de départ
			areaWidth = 440 + 40 * Utilities.random(0, 3);
			areaHeight = 440 + 40 * Utilities.random(0, 3);
			systemsCount = 8 + Utilities.random(0, 4);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_PVP_0_10:
			// Secteur avec des missions PvP
			areaWidth = 240 + 40 * Utilities.random(0, 3);
			areaHeight = 120 + 40 * Utilities.random(0, 1);
			systemsCount = Utilities.random(1, 3);
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 0;
			gravityWellCount=3;
			break;
		case Area.AREA_BANK_0_10:
			// Secteur bancaire
			areaWidth = 80 + 40 * Utilities.random(0, 1);
			areaHeight = 80 + 40 * Utilities.random(0, 1);
			systemsCount = 0;
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 1;
			banksBaseRate = 	0.00000006888; // 2,5% / semaine
			banksVariableRate = 0.00000013778; // 5% / semaine
			banksFees = 100000;
			spaceStationsLimit = 0;
			gravityWellCount=2;
			break;
		case Area.AREA_BATTLE_0_10:
			// Secteur de combat/pvp/bordure
			areaWidth = 200 + 40 * Utilities.random(0, 2);
			areaHeight = 200 + 40 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0004);
			gatesCount = 1 + (int) (Math.random() * 1.5);
			tradeCentersCount = 1;
			tradeCentersVariation = .000001;
			tradeCentersFees = .05;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=5;
			break;
			
			
			
			/************************************/
			/******** Sector : lvl 10-20 ********/
			/************************************/
		case Area.AREA_COLONY_10_20:
			// Secteur de colonies niveaux 10-20
			areaWidth = 140 + 25 * Utilities.random(0, 2);
			areaHeight = 140 + 25 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0012);
			gatesCount = 1;
			tradeCentersCount = 1;
			tradeCentersVariation = .0000001;
			tradeCentersFees = .04;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_10_15:
			// Secteur pirate niveaux 10-20
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_15_20:
			// Secteur pirate niveaux 10-20
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_BANK_10_20:
			// Secteur bancaire niveaux 10-20
			areaWidth = 80 + 40 * Utilities.random(0, 1);
			areaHeight = 80 + 40 * Utilities.random(0, 1);
			systemsCount = 0;
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 1;
			banksBaseRate = 	0.00000009645; // 3,5% / semaine
			banksVariableRate = 0.00000019290; // 7% / semaine
			banksFees = 1000000;
			spaceStationsLimit = 0;
			gravityWellCount=2;
			break;
			
		case Area.AREA_BATTLE_10_20:
			// Secteur de combat/pvp/bordure
			areaWidth = 200 + 40 * Utilities.random(0, 3);
			areaHeight = 200 + 40 * Utilities.random(0, 3);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0004);
			gatesCount = 1 + (int) (Math.random() * 1.5);
			tradeCentersCount = 1;
			tradeCentersVariation = .0000001;
			tradeCentersFees = .04;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=6;
			break;
			
			/************************************/
			/******** Sector : lvl 20-30 ********/
			/************************************/
		case Area.AREA_COLONY_20_30:
			// Secteur de colonies niveaux 20-30
			areaWidth = 140 + 25 * Utilities.random(0, 2);
			areaHeight = 140 + 25 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0012);
			gatesCount = 1;
			tradeCentersCount = 1;
			tradeCentersVariation = .00000001;
			tradeCentersFees = .03;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_20_25:
			// Secteur pirate niveaux 20-30
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_25_30:
			// Secteur pirate niveaux 20-30
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_BANK_20_30:
			// Secteur bancaire niveaux 20-30
			areaWidth = 80 + 40 * Utilities.random(0, 1);
			areaHeight = 80 + 40 * Utilities.random(0, 1);
			systemsCount = 0;
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 1;
			
			banksBaseRate = 	0.00000012402; // 4,5% / semaine
			banksVariableRate = 0.00000024800; // 9% / semaine
			banksFees = 10000000;
			spaceStationsLimit = 0;
			gravityWellCount=2;
			break;
		case Area.AREA_BATTLE_20_30:
			// Secteur de combat/pvp/bordure
			areaWidth = 220 + 40 * Utilities.random(0, 3);
			areaHeight = 220 + 40 * Utilities.random(0, 3);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0003);
			gatesCount = 1 + (int) (Math.random() * 1.5);
			tradeCentersCount = 1;
			tradeCentersVariation = .00000001;
			tradeCentersFees = .03;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=7;
			break;
			
			/************************************/
			/******** Sector : lvl 30-40 ********/
			/************************************/
		case Area.AREA_COLONY_30_40:
			// Secteur de colonies niveaux 30-40
			areaWidth = 140 + 25 * Utilities.random(0, 2);
			areaHeight = 140 + 25 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0012);
			gatesCount = 1;
			tradeCentersCount = 1;
			tradeCentersVariation = .000000005;
			tradeCentersFees = .025;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_30_35:
			// Secteur pirate niveaux 30-40
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_35_40:
			// Secteur pirate niveaux 30-40
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_BANK_30_40:
			// Secteur bancaire niveaux 30-40
			areaWidth = 80 + 40 * Utilities.random(0, 1);
			areaHeight = 80 + 40 * Utilities.random(0, 1);
			systemsCount = 0;
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 1;
			banksBaseRate = 	0.00000015159; // 5,5% / semaine
			banksVariableRate = 0.00000030310; // 11% / semaine
			banksFees = 100000000;
			spaceStationsLimit = 0;
			gravityWellCount=2;
			break;
		case Area.AREA_BATTLE_30_40:
			// Secteur de combat/pvp/bordure
			areaWidth = 250 + 40 * Utilities.random(0, 3);
			areaHeight = 250 + 40 * Utilities.random(0, 3);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0003);
			gatesCount = 1 + (int) (Math.random() * 1.5);
			tradeCentersCount = 1;
			tradeCentersVariation = .000000005;
			tradeCentersFees = .025;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=8;
			break;
			/************************************/
			/******** Sector : lvl 40-50 ********/
			/************************************/
		case Area.AREA_COLONY_40_50:
			// Secteur de colonies niveaux 40-50
			areaWidth = 140 + 25 * Utilities.random(0, 2);
			areaHeight = 140 + 25 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0012);
			gatesCount = 1;
			tradeCentersCount = 1;
			tradeCentersVariation = .000000001;
			tradeCentersFees = .02;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_40_45:
			// Secteur pirate niveaux 40-50
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_45_50:
			// Secteur pirate niveaux 40-50
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_BANK_40_50:
			// Secteur bancaire niveaux 40-50
			areaWidth = 80 + 40 * Utilities.random(0, 1);
			areaHeight = 80 + 40 * Utilities.random(0, 1);
			systemsCount = 0;
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 1;
			banksBaseRate = 	0.00000017916; // 6,5% / semaine
			banksVariableRate = 0.00000035820; // 13% / semaine
			banksFees = 1000000000;
			spaceStationsLimit = 0;
			gravityWellCount=2;
			break;
		case Area.AREA_BATTLE_40_50:
			// Secteur de combat/pvp/bordure
			areaWidth = 300 + 40 * Utilities.random(0, 3);
			areaHeight = 300 + 40 * Utilities.random(0, 3);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .00025);
			gatesCount = 1 + (int) (Math.random() * 1.5);
			tradeCentersCount = 1;
			tradeCentersVariation = .000000001;
			tradeCentersFees = .02;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=9;
			break;
			/************************************/
			/******** Sector : lvl 50-60 ********/
			/************************************/
		case Area.AREA_COLONY_50_60:
			// Secteur de colonies niveaux 40-50
			areaWidth = 140 + 25 * Utilities.random(0, 2);
			areaHeight = 140 + 25 * Utilities.random(0, 2);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .0012);
			gatesCount = 1;
			tradeCentersCount = 1;
			tradeCentersVariation = .000000001;
			tradeCentersFees = .02;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_50_55:
			// Secteur pirate niveaux 50-60
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_MINING_55_60:
			// Secteur pirate niveaux 50-60
			areaWidth = 360 + 40 * Utilities.random(0, 2);
			areaHeight = 360 + 40 * Utilities.random(0, 2);
			systemsCount = 4 + Utilities.random(0, 8);
			gatesCount = 1;
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=3;
			break;
		case Area.AREA_BANK_50_60:
			// Secteur bancaire niveaux 50-60
			areaWidth = 80 + 40 * Utilities.random(0, 1);
			areaHeight = 80 + 40 * Utilities.random(0, 1);
			systemsCount = 0;
			gatesCount = Utilities.random(1, 2);
			tradeCentersCount = 0;
			tradeCentersVariation = 0;
			tradeCentersFees = 0;
			banksCount = 1;
			banksBaseRate = 	0.00000042916; // Taux de base 
			banksVariableRate = 0.00000055820; // Taux maximal
			banksFees = 2000000000;
			spaceStationsLimit = 0;
			gravityWellCount=2;
			break;
		case Area.AREA_BATTLE_50_60:
			// Secteur de combat/pvp/bordure
			areaWidth = 300 + 40 * Utilities.random(0, 3);
			areaHeight = 300 + 40 * Utilities.random(0, 3);
			systemsCount = (int) Math.round((areaWidth * areaHeight) * .00025);
			gatesCount = 1 + (int) (Math.random() * 1.5);
			tradeCentersCount = 1;
			tradeCentersVariation = .000000001;
			tradeCentersFees = .02;
			banksCount = 0;
			banksBaseRate = 0;
			banksVariableRate = 0;
			banksFees = 0;
			spaceStationsLimit = 1;
			gravityWellCount=9;
			break;
			
		default:
			throw new IllegalArgumentException(
				"Invalid area type: '" + areaType + "'.");
		}
		
		int productType = 0;
		if(DataAccess.getSectorById(idSector).getType()==1)
			productType = Utilities.random(1, 3);
		else if(DataAccess.getSectorById(idSector).getType()==2)
			productType = Utilities.random(1, 5);
		else if(DataAccess.getSectorById(idSector).getType()==3)
			productType = Utilities.random(1, 6);
		
		String environment ="";
		
		switch(Utilities.random(1, 6))
		{
		case 1:
			environment = Area.ENVIRONMENT_NORMAL;
			break;
		case 2:
			environment = Area.ENVIRONMENT_DESERT;
			break;
		case 3:
			environment = Area.ENVIRONMENT_EPAVE;
			break;
		case 4:
			environment = Area.ENVIRONMENT_GLACIAL;
			break;
		case 5:
			environment = Area.ENVIRONMENT_MINE;
			break;
		case 6:
			environment = Area.ENVIRONMENT_ALIEN;
			break;
		}
		
		Area area = new Area(
			getRandomLocationName(),
			areaWidth, areaHeight,
			areaLocation.getX(),
			areaLocation.getY(),
			areaType,
			spaceStationsLimit,
			idSector,
			productType,
			environment);
		area.save();
		
		
		// Génère les portes hyperspatiales
		int[][] gatesPosition = new int[gatesCount][2];
		
		for (int k = 0; k < gatesCount; k++) {
			int x, y;
			double[] mean, stdDev;
			
			switch (areaType) {
			case Area.AREA_MINING_55_60:
			case Area.AREA_MINING_45_50:
			case Area.AREA_MINING_35_40:
			case Area.AREA_MINING_25_30:
			case Area.AREA_MINING_15_20:
			case Area.AREA_MINING_5_10:
				
			case Area.AREA_MINING_50_55:
			case Area.AREA_MINING_40_45:
			case Area.AREA_MINING_30_35:
			case Area.AREA_MINING_20_25:
			case Area.AREA_MINING_10_15:
			case Area.AREA_MINING_0_5:
			
				double[][] positions = {
					{	 areaWidth / 8., 	 areaHeight / 8.},
					{7 * areaWidth / 8., 	 areaHeight / 8.},
					{	 areaWidth / 8., 7 * areaHeight / 8.},
					{7 * areaWidth / 8., 7 * areaHeight / 8.},
				};
				
				mean   = positions[(int) (Math.random() * 4)];
				stdDev = new double[]{areaWidth / 6., areaHeight / 6.};
				break;
			default:
				mean   = new double[]{areaWidth / 2., areaHeight / 2.};
				stdDev = new double[]{areaWidth / 3., areaHeight / 3.};
				break;
			}
			
			boolean invalid;
			do {
				do {
					x = (int) Math.round(Utilities.randn(mean[0], stdDev[0]));
				} while (x < 10 || x >= areaWidth - 10);
				
				do {
					y = (int) Math.round(Utilities.randn(mean[1], stdDev[1]));
				} while (y < 10 || y >= areaHeight - 10);
				
				// Vérifie que la porte hyperspatiale est a au moins
				// 40 cases d'écart des autres
				invalid = false;
				for (int l = 0; l < k; l++) {
					int dx = x - gatesPosition[l][0];
					int dy = y - gatesPosition[l][1];
					
					if (dx * dx + dy * dy < 40 * 40) {
						invalid = true;
						break;
					}
				}
			} while (invalid);
			
			StellarObject gate = new StellarObject(x, y, StellarObject.TYPE_GATE, 0, area.getId());
			gate.save();
			
			gatesPosition[k][0] = x;
			gatesPosition[k][1] = y;
		}
		


		// Génère les banques
		int[][] banksPosition = new int[banksCount][2];
		
		for (int k = 0; k < banksCount; k++) {
			int x, y;
			boolean invalid;
			
			do {
				x = Utilities.random(GameConstants.SYSTEM_RADIUS + 2,
						areaWidth  - GameConstants.SYSTEM_RADIUS - 3);
				y = Utilities.random(GameConstants.SYSTEM_RADIUS + 2,
						areaHeight - GameConstants.SYSTEM_RADIUS - 3);
				
				// Vérifie que le centre de commerce est a au moins 35
				// cases d'écart des portes hyperspatiales
				invalid = false;
				
				for (int l = 0; l < gatesCount; l++) {
					int dx = x - gatesPosition[l][0];
					int dy = y - gatesPosition[l][1];
					
					if (dx * dx + dy * dy < 35 * 35) {
						invalid = true;
						break;
					}
				}
				
				// Vérifie que la banque est à au moins 45
				// cases d'écart d'une autre
				for (int l = 0; l < k; l++) {
					int dx = x - banksPosition[l][0];
					int dy = y - banksPosition[l][1];
					
					if (dx * dx + dy * dy < 45 * 45) {
						invalid = true;
						break;
					}
				}
			} while (invalid);
			
			StellarObject bankObject = new StellarObject(
					x, y, StellarObject.TYPE_BANK, 0, area.getId());
			bankObject.save();
			
			Bank bank = new Bank(
					bankObject.getId(),
					banksBaseRate,
					banksVariableRate,
					banksFees);
			bank.save();
			
			banksPosition[k][0] = x;
			banksPosition[k][1] = y;
		}
		

		// Génère les centres de commerce
		int[][] tradeCentersPosition = new int[tradeCentersCount][2];
		
		for (int k = 0; k < tradeCentersCount; k++) {
			int x, y;
			boolean invalid;
			
			do {
				x = Utilities.random(GameConstants.SYSTEM_RADIUS + 2,
						areaWidth  - GameConstants.SYSTEM_RADIUS - 3);
				y = Utilities.random(GameConstants.SYSTEM_RADIUS + 2,
						areaHeight - GameConstants.SYSTEM_RADIUS - 3);
				
				// Vérifie que le centre de commerce est a au moins 45
				// cases d'écart des portes hyperspatiales
				invalid = false;
				
				for (int l = 0; l < gatesCount; l++) {
					int dx = x - gatesPosition[l][0];
					int dy = y - gatesPosition[l][1];
					
					if (dx * dx + dy * dy < 45 * 45) {
						invalid = true;
						break;
					}
				}
				
				// Vérifie que le centre de commerce est à au moins 45
				// cases d'écart d'un autre
				for (int l = 0; l < k; l++) {
					int dx = x - tradeCentersPosition[l][0];
					int dy = y - tradeCentersPosition[l][1];
					
					if (dx * dx + dy * dy < 45 * 45) {
						invalid = true;
						break;
					}
				}
			} while (invalid);
			
			StellarObject tradeCenterObject = new StellarObject(
					x, y, StellarObject.TYPE_TRADECENTER, 0, area.getId());
			tradeCenterObject.save();
			
			Tradecenter tradeCenter = new Tradecenter(
					tradeCenterObject.getId(),
					tradeCentersVariation,
					tradeCentersFees);
			tradeCenter.save();
			
			tradeCentersPosition[k][0] = x;
			tradeCentersPosition[k][1] = y;
		}
		

		// Génère les systèmes 
		int[][] systemsPosition = new int[systemsCount][2];
		
		for (int k = 0; k < systemsCount; k++) {
		
			int x, y;
			boolean invalid;
			
			do {
			
				x = Utilities.random(GameConstants.SYSTEM_RADIUS + 2,
						areaWidth  - GameConstants.SYSTEM_RADIUS - 3);
				y = Utilities.random(GameConstants.SYSTEM_RADIUS + 2,
						areaHeight - GameConstants.SYSTEM_RADIUS - 3);
				
				// Vérifie que le système est a au moins 45 cases
				// d'écart des portes hyperspatiales
				invalid = false;
				
				for (int l = 0; l < gatesCount; l++) {
					int dx = x - gatesPosition[l][0];
					int dy = y - gatesPosition[l][1];
					
					if (dx * dx + dy * dy < 45 * 45) {
						invalid = true;
						break;
					}
				}
				
				// Vérifie que le système est à au moins 25
				// cases d'écart d'un centre de commerce
				for (int l = 0; l < tradeCentersCount; l++) {
					int dx = x - tradeCentersPosition[l][0];
					int dy = y - tradeCentersPosition[l][1];
					
					if (dx * dx + dy * dy < 25 * 25) {
						invalid = true;
						break;
					}
				}
				
				// Vérifie que le système n'entre pas en collision avec
				// un autre
				for (int l = 0; l < k; l++) {
					int dx = x - systemsPosition[l][0];
					int dy = y - systemsPosition[l][1];
					int radiusSq = 2 * GameConstants.SYSTEM_RADIUS + 5;
					
					if (dx * dx + dy * dy < radiusSq * radiusSq) {
						invalid = true;
						break;
					}
				}
			} while (invalid);
			
			
			
			// Caractéristiques du système
			int starImage = Utilities.random(1, STAR_IMAGES_COUNT);
			boolean aiSystem;
			int availableSpace;
			int[] availableResources;
			int idOwner;
			
			switch (areaType) {
			case Area.AREA_START:
				// ------------------------------------------ Secteur de départ
				aiSystem = k < 0;
				
				int[] availableSpaceValues = {4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 3};
				int[][] availableResourcesValues = {
					{3, 3, 3, 0},
					{4, 2, 3, 0},
					{4, 3, 2, 0},
					{2, 4, 3, 0},
					{3, 4, 2, 0},
					{2, 3, 4, 0},
					{3, 2, 4, 0},
					{3, 2, 2, 0},
					{2, 3, 2, 0},
					{2, 2, 3, 0},
					{3, 3, 3, 1}
				};
				
				int random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = aiSystem ? campaignNPC.getId() : 0;
				break;
			case Area.AREA_MINING_0_5:
			case Area.AREA_MINING_5_10:
				// ------------------------------------------- Secteur minier 1
				aiSystem = true;
				availableSpace = 4;
				availableResources = new int[]{4, 4, 2, 0};
				idOwner = 0;
				break;
			case Area.AREA_PVP_0_10:
				// ---------------------------------------------- Secteur PvP 1
				aiSystem = true;
				availableSpace = 4;
				availableResources = new int[]{4, 4, 2, 0};
				idOwner = 0;
				break;
			case Area.AREA_BATTLE_0_10:
				// ------------------------------------------ Secteur de combat
				aiSystem = k < 0;
				availableSpaceValues = new int[]{4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 3};
				availableResourcesValues = new int[][]{
					{3, 3, 3, 0},
					{4, 2, 3, 0},
					{4, 3, 2, 0},
					{2, 4, 3, 0},
					{3, 4, 2, 0},
					{2, 3, 4, 0},
					{3, 2, 4, 0},
					{3, 2, 2, 0},
					{2, 3, 2, 0},
					{2, 2, 3, 0},
					{3, 3, 3, 1}
				};
				
				random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = 0;
				break;
			case Area.AREA_COLONY_10_20:
			case Area.AREA_BATTLE_10_20:
				// ---------------------------------------------- Colonie lvl 2
				aiSystem = k < 0;
				
				availableSpaceValues = new int[]{4, 5, 5, 4, 4, 5, 4, 5, 4, 5, 4, 4, 4, 4};
				availableResourcesValues = new int[][]{
					{4, 3, 4, 0},
					{2, 4, 3, 0},
					{3, 3, 3, 0},
					{4, 2, 5, 0},
					{5, 3, 3, 0},
					{4, 3, 2, 0},
					{3, 5, 3, 0},
					{2, 4, 3, 0},
					{5, 4, 2, 0},
					{2, 2, 5, 0},
					{3, 4, 4, 0},
					{4, 3, 2, 1},
					{2, 4, 3, 1},
					{3, 2, 4, 1},
				};
				
				random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = 0;
				break;
				
			case Area.AREA_COLONY_20_30:
			case Area.AREA_BATTLE_20_30:
				// ---------------------------------------------- Colonie lvl 3
				aiSystem = k < 0;
				
				availableSpaceValues = new int[]{5, 6, 6, 5, 5, 6, 5, 6, 5, 6, 5, 5, 6, 5};
				availableResourcesValues = new int[][]{
					{4, 4, 4, 0},
					{3, 5, 5, 0},
					{4, 3, 4, 0},
					{4, 4, 3, 1},
					{5, 3, 3, 0},
					{4, 3, 4, 0},
					{3, 4, 5, 1},
					{3, 3, 6, 1},
					{4, 4, 2, 1},
					{3, 5, 4, 1},
					{4, 3, 3, 1},
					{5, 2, 2, 2},
					{3, 4, 5, 1},
					{3, 4, 4, 2},
				};
				
				random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = 0;
				break;
				
			case Area.AREA_COLONY_30_40:
			case Area.AREA_BATTLE_30_40:
				// ---------------------------------------------- Colonie lvl 4
				aiSystem = k < 0;
				
				availableSpaceValues = new int[]{6, 6, 7, 6, 6, 7, 6, 6, 6, 7, 6, 6, 6, 6};
				availableResourcesValues = new int[][]{
					{5, 4, 4, 2},
					{5, 4, 3, 2},
					{5, 2, 4, 2},
					{6, 3, 3, 2},
					{4, 5, 4, 2},
					{3, 5, 4, 2},
					{4, 5, 3, 2},
					{4, 4, 5, 2},
					{3, 3, 5, 4},
					{3, 4, 5, 2},
					{3, 6, 3, 3},
					{3, 3, 6, 3},
					{5, 5, 4, 1},
					{5, 4, 5, 1},
				};
				
				random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = 0;
				break;
				

			case Area.AREA_COLONY_40_50:
			case Area.AREA_BATTLE_40_50:
				// ---------------------------------------------- Colonie lvl 5
				aiSystem = k < 0;
				
				availableSpaceValues = new int[]{7, 7, 8, 7, 7, 8, 7, 7, 7, 8, 7, 7, 7, 7};
				availableResourcesValues = new int[][]{
					{5, 4, 4, 3},
					{5, 4, 3, 3},
					{5, 2, 4, 3},
					{6, 3, 3, 3},
					{4, 5, 4, 3},
					{3, 5, 4, 3},
					{4, 5, 3, 3},
					{4, 4, 5, 3},
					{3, 3, 5, 5},
					{3, 4, 5, 3},
					{3, 6, 3, 4},
					{3, 3, 6, 4},
					{5, 5, 4, 2},
					{5, 4, 5, 2},
				};
				
				random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = 0;
				break;
				
			case Area.AREA_COLONY_50_60:
			case Area.AREA_BATTLE_50_60:
				// ---------------------------------------------- Colonie lvl 6
				aiSystem = k < 0;
				
				availableSpaceValues = new int[]{8, 8, 9, 9, 9, 9, 8, 8, 8, 8, 8, 8, 8, 8};
				availableResourcesValues = new int[][]{
					{6, 5, 5, 4},
					{6, 5, 4, 4},
					{6, 3, 5, 4},
					{7, 4, 4, 4},
					{5, 6, 5, 4},
					{4, 6, 5, 4},
					{5, 6, 5, 5},
					{5, 5, 6, 4},
					{4, 4, 6, 6},
					{4, 5, 6, 4},
					{4, 7, 4, 5},
					{4, 4, 7, 5},
					{6, 6, 5, 3},
					{6, 5, 6, 3},
				};
				
				random = (int) (Math.random() * availableSpaceValues.length);
				availableSpace = availableSpaceValues[random];
				availableResources = availableResourcesValues[random];
				
				idOwner = 0;
				break;
				
			case Area.AREA_MINING_55_60:	
			case Area.AREA_MINING_45_50:
			case Area.AREA_MINING_35_40:
			case Area.AREA_MINING_25_30:
			case Area.AREA_MINING_15_20:
			case Area.AREA_MINING_50_55:
			case Area.AREA_MINING_40_45:
			case Area.AREA_MINING_30_35:
			case Area.AREA_MINING_20_25:
			case Area.AREA_MINING_10_15:
				// ------------------------------------------- Secteur pirate 2
				aiSystem = true;
				availableSpace = 4;
				availableResources = new int[]{4, 4, 2, 0};
				idOwner = 0;
				break;
			default:
				throw new IllegalStateException();
			}
			
			int asteroidBelt = 0;
			double random = Math.random();
			if (random < .15)
				asteroidBelt = 1;
			else if (random < .3)
				asteroidBelt = 2;
			
			StarSystem system = new StarSystem(getRandomLocationName(),
					x, y, aiSystem, starImage, asteroidBelt, availableSpace,
					availableResources, area.getId(), idOwner);
			system.save();
			
			systemsPosition[k][0] = x;
			systemsPosition[k][1] = y;
			
		
			// Génère les planètes du système
			int planetsCount = (int) Math.round(Utilities.randn(2.5, 1));
			if (planetsCount < 1)
				planetsCount = 1;
			else if (planetsCount > 4)
				planetsCount = 4;
			
			
			
			int step = (MAX_PLANET_DISTANCE - MIN_PLANET_DISTANCE) / planetsCount;
			

			for (int l = 0; l < planetsCount; l++) {
				int distance = Utilities.random(
						MIN_PLANET_DISTANCE + step * l + PLANET_SIZE / 2,
						MIN_PLANET_DISTANCE + step * (l + 1) - PLANET_SIZE / 2);
				double angle = Math.random() * 2 * Math.PI;
				double rotationSpeed = PLANET_ROTATION_SPEEDS[(int) Math.floor(
						Math.random() * PLANET_ROTATION_SPEEDS.length)];
				int image = Utilities.random(
						PLANET_IMAGES[planetsCount - 1][l][0],
						PLANET_IMAGES[planetsCount - 1][l][1]);
				
			
				Planet planet = new Planet(angle, distance,
						rotationSpeed, image, system.getId());
				planet.save();
			}
		}
		
	// Génère les puits
		
		int puitsCount=0;
		int[][] puitsPosition = new int[gravityWellCount][2];
		
		for (int k = 0; k < gravityWellCount; k++) {
			int x, y;
			boolean invalid;
			
				do {
					
					x = (int) (Math.random() * areaWidth);
					y = (int) (Math.random() * areaHeight);
					
					// Vérifie que le puits est a au moins 40 cases
					// d'écart des portes hyperspatiales
					invalid = false;

					for(int n=0;n<gatesCount;n++){
					gatesPosition[n][0] = area.getGates().get(n).getX();
					gatesPosition[n][1] = area.getGates().get(n).getY();
					}
					
					for (int l = 0; l < gatesCount; l++) {
						int dx = x - gatesPosition[l][0];
						int dy = y - gatesPosition[l][1];
						
						if (dx * dx + dy * dy < 40 * 40) {
							invalid = true;
							break;
						}
					}
					
					//Vérifie que le puits est a 30 cases au moins d'un centre de commerce
					if(tradeCentersCount!=0){

						for (int l = 0; l < tradeCentersCount; l++) {
							int dx = x - tradeCentersPosition[l][0];
							int dy = y - tradeCentersPosition[l][1];
							
							if (dx * dx + dy * dy < 30 * 30) {
								invalid = true;
								break;
							}
						}
					}
					
					//Vérifie que le puits est a 20 cases au moins d'une banque
					if(banksCount!=0){

						for (int l = 0; l < banksCount; l++) {
							int dx = x - banksPosition[l][0];
							int dy = y - banksPosition[l][1];
							
							if (dx * dx + dy * dy < 20 * 20) {
								invalid = true;
								break;
							}
						}
					}
					
					// Vérifie que le puits n'entre pas en collision avec
					// un système
					if(systemsCount!=0){
//					int[][] systemsPosition = new int[systemsCount][2];
//					
//					for(int n=0;n<systemsCount;n++){
//					
//						
//							systemsPosition[n][0] = area.getSystems().get(n).getX();
//							systemsPosition[n][1] = area.getSystems().get(n).getY();
//					}
					
					for (int l = 0; l < systemsCount; l++) {
						int dx = x - systemsPosition[l][0];
						int dy = y - systemsPosition[l][1];
						int radiusSq = 2 * GameConstants.SYSTEM_RADIUS + 5;
						
						if (dx * dx + dy * dy < radiusSq * radiusSq) {
							invalid = true;
							break;
						}
					}
					}
					
					// Vérifie que le puits est à au moins 40 cases d'un autre puits
					for (int l = 0; l < puitsCount; l++) {
						int dx = x - puitsPosition[l][0];
						int dy = y - puitsPosition[l][1];
						int radiusSq = 40;
						
						if (dx * dx + dy * dy < radiusSq * radiusSq) {
							invalid = true;
							break;
						}
					}
					
					// Vérifie que le puits est à au moins 50 cases d'un trou noir
					for (int l = 0; l < puitsCount; l++) {
						int dx = x - puitsPosition[l][0];
						int dy = y - puitsPosition[l][1];
						int radiusSq = 50;
						
						if (dx * dx + dy * dy < radiusSq * radiusSq) {
							invalid = true;
							break;
						}
					}
				} while (invalid);
				
				int maxEnergy = 80*area.getSector().getType();
				
				StellarObject puits = new StellarObject(
						x, y, StellarObject.TYPE_GRAVITY_WELL, maxEnergy, area.getId());
				
				puits.save();
				
				puitsCount++;
				puitsPosition[puitsCount-1][0]=x;
				puitsPosition[puitsCount-1][1]=y;
				
			}
		
		// Génération d'astéroïdes
		int asteroidsCount = areaWidth * areaHeight / 2700;
		
		for (int k = 0; k < asteroidsCount; k++) {
			StellarObject asteroid = new StellarObject(
					0, 0, StellarObject.TYPE_ASTEROID, 0, area.getId());
			Rectangle bounds = asteroid.getBounds();
			int x, y;
			
			do {
				x = (int) (Math.random() * areaWidth);
				y = (int) (Math.random() * areaHeight);
			} while (!area.areFreeTiles(
				x - bounds.width  / 2 - 1,
				y - bounds.height / 2 - 1,
				bounds.width + 2,
				bounds.height + 2,
				Area.CHECK_OBJECT_SPAWN, null));
			
			asteroid.setX(x);
			asteroid.setY(y);
			
			// Un astéroide dense est entouré d'autres astéroides
			int neighbours = (int) (Math.random() * 6);
			
			String mainType;
			
			switch (areaType) {
			case Area.AREA_MINING_0_5:
				if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_LOW_TITANIUM;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_LOW_CRYSTAL;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_LOW_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
			case Area.AREA_MINING_5_10:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_VEIN_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_LOW_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_VEIN_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_LOW_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_VEIN_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_LOW_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_10_15:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_LOWC_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_VEIN_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_LOWC_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_VEIN_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_LOWC_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_VEIN_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_15_20:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_MEDIUMC_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_LOWC_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_MEDIUMC_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_LOWC_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_MEDIUMC_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_LOWC_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_20_25:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_IMPORTANT_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_MEDIUMC_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_IMPORTANT_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_MEDIUMC_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_IMPORTANT_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_MEDIUMC_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_25_30:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_AVG_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_IMPORTANT_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_AVG_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_IMPORTANT_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_AVG_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_IMPORTANT_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_30_35:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_HIGH_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_AVG_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_HIGH_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_AVG_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_HIGH_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_AVG_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_35_40:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_ABONDANT_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_HIGH_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_ABONDANT_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_HIGH_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_ABONDANT_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_HIGH_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_40_45:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_PURE_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_ABONDANT_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_PURE_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_ABONDANT_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_PURE_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_ABONDANT_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_45_50:
				if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_PURE_TITANIUM;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_PURE_CRYSTAL;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_PURE_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
			case Area.AREA_MINING_50_55:
				if (k < 1)
					mainType = StellarObject.TYPE_ASTEROID_CONCENTRATE_TITANIUM;
				else if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_PURE_TITANIUM;
				else if (k < 4)
					mainType = StellarObject.TYPE_ASTEROID_CONCENTRATE_CRYSTAL;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_PURE_CRYSTAL;
				else if (k < 7)
					mainType = StellarObject.TYPE_ASTEROID_CONCENTRATE_ANDIUM;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_PURE_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_MINING_55_60:
				if (k < 3)
					mainType = StellarObject.TYPE_ASTEROID_CONCENTRATE_TITANIUM;
				else if (k < 6)
					mainType = StellarObject.TYPE_ASTEROID_CONCENTRATE_CRYSTAL;
				else if (k < 9)
					mainType = StellarObject.TYPE_ASTEROID_CONCENTRATE_ANDIUM;
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_COLONY_10_20:
				String[] types = {
					StellarObject.TYPE_ASTEROID_VEIN_TITANIUM,
					StellarObject.TYPE_ASTEROID_VEIN_CRYSTAL,
					StellarObject.TYPE_ASTEROID_VEIN_ANDIUM
				};
				
				if (k < 1)
					mainType = types[(int) (Math.random() * types.length)];
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_COLONY_20_30:
				String[] types2 = {
					StellarObject.TYPE_ASTEROID_MEDIUMC_TITANIUM,
					StellarObject.TYPE_ASTEROID_MEDIUMC_CRYSTAL,
					StellarObject.TYPE_ASTEROID_MEDIUMC_ANDIUM
				};
				
				if (k < 3)
					mainType = types2[(int) (Math.random() * types2.length)];
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_COLONY_30_40:
				String[] types3 = {
					StellarObject.TYPE_ASTEROID_AVG_TITANIUM,
					StellarObject.TYPE_ASTEROID_AVG_CRYSTAL,
					StellarObject.TYPE_ASTEROID_AVG_ANDIUM
				};
				
				if (k < 1)
					mainType = types3[(int) (Math.random() * types3.length)];
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
				
			case Area.AREA_COLONY_40_50:
				String[] types4 = {
						StellarObject.TYPE_ASTEROID_ABONDANT_TITANIUM,
						StellarObject.TYPE_ASTEROID_ABONDANT_CRYSTAL,
						StellarObject.TYPE_ASTEROID_ABONDANT_ANDIUM
				};
				
				if (k < 3)
					mainType = types4[(int) (Math.random() * types4.length)];
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
			case Area.AREA_COLONY_50_60:
				String[] types5 = {
						StellarObject.TYPE_ASTEROID_CONCENTRATE_TITANIUM,
						StellarObject.TYPE_ASTEROID_CONCENTRATE_CRYSTAL,
						StellarObject.TYPE_ASTEROID_CONCENTRATE_ANDIUM
				};
				
				if (k < 3)
					mainType = types5[(int) (Math.random() * types5.length)];
				else if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
			default:
				if (neighbours > 0)
					mainType = StellarObject.TYPE_ASTEROID_DENSE;
				else
					mainType = StellarObject.TYPE_ASTEROID;
				break;
			}
			
			asteroid.setType(mainType);
			
			int[] asteroidsOffset = new int[neighbours];
			Arrays.fill(asteroidsOffset, -1);
			
			int[][] positions = {
					{-1, -1},
					{-1,  0},
					{-1,  1},
					{ 0, -1},
					{ 0,  1},
					{ 1, -1},
					{ 1,  0},
					{ 1,  1}
			};
			
			for (int l = 0; l < neighbours; l++) {
				int offset;
				do {
					offset = (int) (Math.random() * positions.length);
				} while (ArrayUtils.contains(asteroidsOffset, offset));
				asteroidsOffset[l] = offset;
				
				x = asteroid.getX() + positions[offset][0] * bounds.width;
				y = asteroid.getY() + positions[offset][1] * bounds.height;
				
				if (area.areFreeTiles(
						x - bounds.width  / 2 - 1,
						y - bounds.height / 2 - 1,
						bounds.width,
						bounds.height,
						Area.CHECK_OBJECT_SPAWN, null)) {
					String type = Math.random() < .1 ?
							StellarObject.TYPE_ASTEROID_DENSE :
							StellarObject.TYPE_ASTEROID;
					
					StellarObject neighbourAsteroid = new StellarObject(
							x, y, type, 0, area.getId());
					neighbourAsteroid.save();
				}
			}
			
			asteroid.save();
		}
		

		// Génération de trous noirs
		int blackholesCount;
		
		switch (areaType) {
		case Area.AREA_START:
		case Area.AREA_BANK_0_10:
		case Area.AREA_BANK_10_20:
		case Area.AREA_BANK_20_30:
		case Area.AREA_BANK_30_40:
		case Area.AREA_BANK_40_50:
		case Area.AREA_BANK_50_60:
			blackholesCount = 0;
			break;
		case Area.AREA_MINING_0_5:
		case Area.AREA_MINING_5_10:
			blackholesCount = Utilities.random(2, 3);
			break;
		case Area.AREA_PVP_0_10:
			blackholesCount = 1;
			break;
		case Area.AREA_COLONY_10_20:
			blackholesCount = Utilities.random(0, 1);
			break;
		case Area.AREA_MINING_15_20:
		case Area.AREA_MINING_10_15:
			blackholesCount = Utilities.random(3, 5);
			break;
		case Area.AREA_COLONY_20_30:
		case Area.AREA_COLONY_30_40:
		case Area.AREA_COLONY_40_50:
		case Area.AREA_COLONY_50_60:
		case Area.AREA_BATTLE_0_10:
			blackholesCount = 1;
			break;
		case Area.AREA_MINING_20_25:
		case Area.AREA_MINING_30_35:
		case Area.AREA_MINING_40_45:
		case Area.AREA_MINING_50_55:
		case Area.AREA_MINING_25_30:
		case Area.AREA_MINING_35_40:
		case Area.AREA_MINING_45_50:
		case Area.AREA_MINING_55_60:
			blackholesCount = Utilities.random(4, 5);
			break;
		case Area.AREA_BATTLE_10_20:
		case Area.AREA_BATTLE_20_30:
		case Area.AREA_BATTLE_30_40:
		case Area.AREA_BATTLE_40_50:
		case Area.AREA_BATTLE_50_60:
			blackholesCount = 2;
			break;
		default:
			throw new IllegalStateException();
		}
		
		for (int k = 0; k < blackholesCount; k++) {
			StellarObject blackhole = new StellarObject(0, 0,
					StellarObject.TYPE_BLACKHOLE, 0, area.getId());
			int x, y;
			boolean invalid;
			
			do {
				x = (int) (Math.random() * areaWidth);
				y = (int) (Math.random() * areaHeight);
				invalid = false;
				
				// Vérifie que le trou noir est à au moins 40 cases des puits
				for (int l = 0; l < puitsCount; l++) {
					int dx = x - puitsPosition[l][0];
					int dy = y - puitsPosition[l][1];
					int radiusSq = 40;
					
					if (dx * dx + dy * dy < radiusSq * radiusSq) {
						invalid = true;
						break;
					}
				}
				
				// Vérifie que le trou noir est à au moins 25 cases des systemes
				for (int l = 0; l < systemsCount; l++) {
					int dx = x - systemsPosition[l][0];
					int dy = y - systemsPosition[l][1];
					int radiusSq = 25;
					
					if (dx * dx + dy * dy < radiusSq * radiusSq) {
						invalid = true;
						break;
					}
				}
				
				// Vérifie que le trou noir est à au moins 30 cases des portes HE
				for (int l = 0; l < gatesCount; l++) {
					int dx = x - gatesPosition[l][0];
					int dy = y - gatesPosition[l][1];
					int radiusSq = 30;
					
					if (dx * dx + dy * dy < radiusSq * radiusSq) {
						invalid = true;
						break;
					}
				}
				
			} while (!area.areFreeTiles(
					x - 10, y - 10, 21, 21,
					Area.CHECK_OBJECT_SPAWN, null) && (invalid));
			
			blackhole.setX(x);
			blackhole.setY(y);
			
			blackhole.save();
		}
		
		if (cleanUp)
			clearRandomLists();
		
		return area;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	public static ArrayList<Location> generateLocations(int count, int density,
			int gap, int hway) {
		// Ecart type de la loi normale : calculé de manière à ce que 95% des
		// zones se trouvent dans le rayon de la galaxie / du quadrant, le
		// rayon étant calculé en prenant la densité de zones d (le rayon est
		// donc de sqrt(d * zones / pi))
		double radius = Math.sqrt(density * count / Math.PI);
		double radiusSq = radius * radius;
		double stdDev = radius / 2;
		boolean invalid;
		
		// Zones générées
		ArrayList<Location> locations = new ArrayList<Location>();
		
		
//		for(Sector sector : DataAccess.getAllSectors())
//		{
//			Location locationRdy = new Location(sector.getX(),sector.getY());
//			locations.add(locationRdy);
//		}
		// Génère les zones
		while (locations.size() < count) {
			Location location;
			
			int tries = 0;
			
			do {
				tries++;
				location = new Location(
						(int) Math.round(Utilities.randn(0, stdDev)),
						(int) Math.round(Utilities.randn(0, stdDev)));
				invalid = false;
				
				// Teste si la zone est à l'intérieur du rayon
				if (location.getNormSq() > radiusSq) {
					invalid = true;
				} else {
					// Teste si la zone entre en collision avec une autre zone
					for (Location otherLocation : locations) {
						if (Math.abs(location.getX() - otherLocation.getX()) <= gap &&
							Math.abs(location.getY() - otherLocation.getY()) <= gap) {
							invalid = true;
							break;
						}
						
						// Vérifie qu'une zone n'est pas générée à la droite
						// d'un autre, afin de ne pas gêner l'affichage
						if (location.getY() >= otherLocation.getY() -  2 &&
							location.getY() <= otherLocation.getY() +  3 &&
							location.getX() >= otherLocation.getX() - 15 &&
							location.getX() <= otherLocation.getX() + 15) {
							invalid = true;
							break;
						}
					}
				}
			} while (invalid && tries < 1000);
			
			if (tries == 1000)
				locations.clear(); // On a pris un mauvais départ, on recommence tout !
			else
				locations.add(location);
		}
		
		// Compte le nombre de zones auquelles est reliée chaque zone
		for (Location location : locations) {
			for (Location otherLocation : locations) {
				int dx = location.getX() - otherLocation.getX();
				int dy = location.getY() - otherLocation.getY();
				
				if (location != otherLocation &&
						dx * dx + dy * dy <= hway * hway) {
					location.addWay();
					otherLocation.addWay();
				}
			}
		}
		
		// Regénère les zones qui ne sont reliées à aucune autre zone
		for (int i = 0; i < count; i++) {
			Location location = locations.get(i);
			
			if (location.getWays() == 0) {
				do {
					location = new Location(
							(int) Math.round(Utilities.randn(0, stdDev)),
							(int) Math.round(Utilities.randn(0, stdDev)));
					invalid = false;
					
					// Teste si la zone est à l'intérieur du rayon de la galaxie
					if (location.getNormSq() > radiusSq) {
						invalid = true;
					} else {
						// Teste si la zone entre en collision avec une autre zone
						for (Location otherLocation : locations)
							if (location != otherLocation &&
									Math.abs(location.getX() - otherLocation.getX()) <= gap &&
									Math.abs(location.getY() - otherLocation.getY()) <= gap) {
								invalid = true;
								break;
							}
					}
					
					// Teste si la zone est reliée a au moins une autre zone
					if (!invalid) {
						for (Location otherLocation : locations) {
							int dx = location.getX() - otherLocation.getX();
							int dy = location.getY() - otherLocation.getY();
							
							if (location != otherLocation &&
									dx * dx + dy * dy <= hway * hway) {
								location.addWay();
								otherLocation.addWay();
							}
						}
					}
				} while (invalid || location.getWays() == 0);
				
				locations.set(i, location);
			}
		}
		
		// Vérifie que toutes les zones sont accessibles
		ArrayList<Location> openedList = new ArrayList<Location>();
		ArrayList<Location> closedList = new ArrayList<Location>();
		openedList.add(locations.get(0));
		
		while (openedList.size() > 0) {
			Location baseLocation = openedList.remove(0);
			closedList.add(baseLocation);
			
			for (Location location : locations) {
				if (openedList.contains(location) || closedList.contains(location))
					continue;
				
				int dx = location.getX() - baseLocation.getX();
				int dy = location.getY() - baseLocation.getY();
				
				if (dx * dx + dy * dy <= hway * hway)
					openedList.add(location);
			}
		}
		
		// Regénére les positions si ce n'est pas le cas
		if (closedList.size() != count)
			return generateLocations(count, density, gap, hway);
		
		return locations;
	}
	
	public static ArrayList<Location> generateSectorLocations(int count, int density,
			int gap, int hway) {
		// Ecart type de la loi normale : calculé de manière à ce que 95% des
		// zones se trouvent dans le rayon de la galaxie / du quadrant, le
		// rayon étant calculé en prenant la densité de zones d (le rayon est
		// donc de sqrt(d * zones / pi))
		double radius = Math.sqrt(density * count / Math.PI);
		double radiusSq = radius * radius;
		double stdDev = radius / 2;
		boolean invalid;
		
		// Zones générées
		ArrayList<Location> locations = new ArrayList<Location>();
		
		
		for(Sector sector : DataAccess.getAllSectors())
		{
			Location locationRdy = new Location(sector.getX(),sector.getY());
			locations.add(locationRdy);
		}
		// Génère les zones
		while (locations.size() < count) {
			Location location;
			
			int tries = 0;
			
			do {
				tries++;
				location = new Location(
						(int) Math.round(Utilities.randn(0, stdDev)),
						(int) Math.round(Utilities.randn(0, stdDev)));
				invalid = false;
				
				// Teste si la zone est à l'intérieur du rayon
				if (location.getNormSq() > radiusSq) {
					invalid = true;
				} else {
					// Teste si la zone entre en collision avec une autre zone
					for (Location otherLocation : locations) {
						if (Math.abs(location.getX() - otherLocation.getX()) <= gap &&
							Math.abs(location.getY() - otherLocation.getY()) <= gap) {
							invalid = true;
							break;
						}
						
						// Vérifie qu'une zone n'est pas générée à la droite
						// d'un autre, afin de ne pas gêner l'affichage
						if (location.getY() >= otherLocation.getY() -  2 &&
							location.getY() <= otherLocation.getY() +  3 &&
							location.getX() >= otherLocation.getX() - 15 &&
							location.getX() <= otherLocation.getX() + 15) {
							invalid = true;
							break;
						}
					}
				}
			} while (invalid && tries < 1000);
			
			if (tries == 1000)
				locations.clear(); // On a pris un mauvais départ, on recommence tout !
			else
				locations.add(location);
		}
		
		// Compte le nombre de zones auquelles est reliée chaque zone
		for (Location location : locations) {
			for (Location otherLocation : locations) {
				int dx = location.getX() - otherLocation.getX();
				int dy = location.getY() - otherLocation.getY();
				
				if (location != otherLocation &&
						dx * dx + dy * dy <= hway * hway) {
					location.addWay();
					otherLocation.addWay();
				}
			}
		}
		
		// Regénère les zones qui ne sont reliées à aucune autre zone
		for (int i = 0; i < count; i++) {
			Location location = locations.get(i);
			
			if (location.getWays() == 0) {
				do {
					location = new Location(
							(int) Math.round(Utilities.randn(0, stdDev)),
							(int) Math.round(Utilities.randn(0, stdDev)));
					invalid = false;
					
					// Teste si la zone est à l'intérieur du rayon de la galaxie
					if (location.getNormSq() > radiusSq) {
						invalid = true;
					} else {
						// Teste si la zone entre en collision avec une autre zone
						for (Location otherLocation : locations)
							if (location != otherLocation &&
									Math.abs(location.getX() - otherLocation.getX()) <= gap &&
									Math.abs(location.getY() - otherLocation.getY()) <= gap) {
								invalid = true;
								break;
							}
					}
					
					// Teste si la zone est reliée a au moins une autre zone
					if (!invalid) {
						for (Location otherLocation : locations) {
							int dx = location.getX() - otherLocation.getX();
							int dy = location.getY() - otherLocation.getY();
							
							if (location != otherLocation &&
									dx * dx + dy * dy <= hway * hway) {
								location.addWay();
								otherLocation.addWay();
							}
						}
					}
				} while (invalid || location.getWays() == 0);
				
				locations.set(i, location);
			}
		}
		
		// Vérifie que toutes les zones sont accessibles
		ArrayList<Location> openedList = new ArrayList<Location>();
		ArrayList<Location> closedList = new ArrayList<Location>();
		openedList.add(locations.get(0));
		
		while (openedList.size() > 0) {
			Location baseLocation = openedList.remove(0);
			closedList.add(baseLocation);
			
			for (Location location : locations) {
				if (openedList.contains(location) || closedList.contains(location))
					continue;
				
				int dx = location.getX() - baseLocation.getX();
				int dy = location.getY() - baseLocation.getY();
				
				if (dx * dx + dy * dy <= hway * hway)
					openedList.add(location);
			}
		}
		
		// Regénére les positions si ce n'est pas le cas
		if (closedList.size() != count)
			return generateLocations(count, density, gap, hway);
		
		return locations;
	}
	
	private static void initRandomLists() {
		locationNames = new ArrayList<RandomName>(DataAccess.getRandomNamesByType(RandomName.TYPE_LOCATION));
		organizationNames = new ArrayList<String>();
		nebulas = new ArrayList<Integer>();
		
		for (int i = 0; i < ORGANIZATION_NAMES_COUNT; i++)
			organizationNames.add("organization." + i);
		for (int i = 1; i <= NEBULA_IMAGES_COUNT; i++)
			nebulas.add(i);
		
		Collections.shuffle(locationNames);
		Collections.shuffle(organizationNames);
		Collections.shuffle(nebulas);
		
		currentLocationName = 0;
		currentOrganizationName = 0;
		currentNebula = 0;
		initialized = true;
	}
	
	private static int getRandomNebula() {
		return nebulas.get(currentNebula++ % nebulas.size());
	}
	
	private static String getRandomLocationName() {
		return locationNames.get(currentLocationName++ % locationNames.size()).getName();
	}
	
	private static String getRandomOrganizationName() {
		return organizationNames.get(currentOrganizationName++ % organizationNames.size());
	}
	
	private static void clearRandomLists() {
		locationNames.clear();
		organizationNames.clear();
		nebulas.clear();
		locationNames = null;
		organizationNames = null;
		nebulas = null;
		initialized = false;
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	public static class Location {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int x, y;
		
		private double normSq;
		
		private int ways;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Location(int x, int y) {
			this.x = x;
			this.y = y;
			this.normSq = x * x + y * y;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public double getNormSq() {
			return normSq;
		}
		
		public int getWays() {
			return ways;
		}
		
		public void addWay() {
			ways++;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
