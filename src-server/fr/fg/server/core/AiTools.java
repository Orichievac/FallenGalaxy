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

package fr.fg.server.core;

import fr.fg.server.data.Ability;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Slot;
import fr.fg.server.data.Technology;
import fr.fg.server.util.Utilities;

public class AiTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Login du joueur pirate
	private final static String PIRATE_AI_LOGIN = "Pirate";
	public static final String PNJ_DEFENDER_AI_LOGIN = "Defender";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static boolean isPiratePlayer(String login) {
		if (login.startsWith(PIRATE_AI_LOGIN)) {
			Player pirate = DataAccess.getPlayerByLogin(login);
			
			return pirate != null && pirate.isAi();
		}
		
		return false;
	}
	
	public static Player getPiratePlayer(int level) {
		Player pirate = DataAccess.getPlayerByLogin(PIRATE_AI_LOGIN + level);
		if (pirate == null) {
			createPiratePlayers();
			pirate = DataAccess.getPlayerByLogin(PIRATE_AI_LOGIN + level);
		}
		return pirate;
	}
	
	public static void createPiratePlayers() {
		for (int i = 1; i <= 60; i++) {
			String login = PIRATE_AI_LOGIN + i;
			
			Player pirate = DataAccess.getPlayerByLogin(login);
			
			if (pirate == null) {

				pirate = new Player(login, "", "", "", "");
				pirate.setAi(true);
				pirate.save();
				
				// Affecte les technologies au PNJ
				Research r;
				
				if(i>12){
					r = new Research(Technology.TECHNOLOGY_8_CAPITAL_SHIPS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_6_DOGME_MARTIALE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_6_PHASE_TRANSFER, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_6_PARTICLE_PROJECTION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_6_DYNAMIC_REALITY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_MAGNETODYNAMICS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_CHAOS_GENERATOR, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_FUSION_BATTERY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_MATTER_ALTERATION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_TRANSCENDENCE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_UNIFIED_FIELD_THEORY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_BATTLE_DRUGS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_CHAOS_THEORY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_ELECTRONIC_WARFARE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_ENDOCTRINATION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_FUSION_PROPULSION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_IRIDIUM_ALLOY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_KESLEY_STABILISER, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_OFFENSIVE_MANEUVERS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_SDI, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_BATTLE_LINE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_DOCTRINE_VR_70, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_GAUSS_CANON, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_MAGNETIC_SHIELD, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_MICRO_CHARGES, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_PROPAGANDA_THEORY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_SPACE_TIME_COMPRESSION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_BOMBARDMENT, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_DOCTRINE_C15, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_DRONES, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_HEPTANIUM_PROPULSION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_PULSE_REACTOR, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_SELF_DEFENSE_SYSTEM, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_BOARDING, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_EVASIVE_MANEUVERS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_FIRE_CONTOL_SYSTEM, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_PULSE_LASER, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_SPACE_LOGISTICS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_BALLISTIC, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_DOCTRINE_R55, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_PULSE_GENERATOR, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_SPACE_WARFARE, 1, -1, pirate.getId());
					r.save();
				}
				
				switch (i) {
				
				case 11:
				case 12:
					r = new Research(Technology.TECHNOLOGY_6_PHASE_TRANSFER, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_6_PARTICLE_PROJECTION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_6_DYNAMIC_REALITY, 1, -1, pirate.getId());
					r.save();
				case 10:
					r = new Research(Technology.TECHNOLOGY_5_ADV_SPACE_CONSTRUCTION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_CHAOS_GENERATOR, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_FUSION_BATTERY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_MATTER_ALTERATION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_TRANSCENDENCE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_5_UNIFIED_FIELD_THEORY, 1, -1, pirate.getId());
					r.save();
				case 7:
				case 8:
				case 9:
					r = new Research(Technology.TECHNOLOGY_4_BATTLE_DRUGS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_CHAOS_THEORY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_ELECTRONIC_WARFARE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_ENDOCTRINATION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_FUSION_PROPULSION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_IRIDIUM_ALLOY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_KESLEY_STABILISER, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_OFFENSIVE_MANEUVERS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_4_SDI, 1, -1, pirate.getId());
					r.save();
					// Pas de break
				case 4:
				case 5:
				case 6:
					r = new Research(Technology.TECHNOLOGY_3_ADVANCED_SPACEFLIGHT, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_BATTLE_LINE, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_DOCTRINE_VR_70, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_GAUSS_CANON, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_MAGNETIC_SHIELD, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_MICRO_CHARGES, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_PROPAGANDA_THEORY, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_3_SPACE_TIME_COMPRESSION, 1, -1, pirate.getId());
					r.save();
					// Pas de break
				case 2:
				case 3:
					r = new Research(Technology.TECHNOLOGY_2_BOMBARDMENT, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_DOCTRINE_C15, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_DRONES, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_HEPTANIUM_PROPULSION, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_PULSE_REACTOR, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_2_SELF_DEFENSE_SYSTEM, 1, -1, pirate.getId());
					r.save();
					// Pas de break
				case 1:
					r = new Research(Technology.TECHNOLOGY_1_BOARDING, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_EVASIVE_MANEUVERS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_FIRE_CONTOL_SYSTEM, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_PULSE_LASER, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_1_SPACE_LOGISTICS, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_BALLISTIC, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_DOCTRINE_R55, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_PULSE_GENERATOR, 1, -1, pirate.getId());
					r.save();
					r = new Research(Technology.TECHNOLOGY_0_SPACE_WARFARE, 1, -1, pirate.getId());
					r.save();
					break;
				}
			}
		}
	}
	
	
	
	public static Fleet createFleetByHash(Fleet fleet, String hashCode) throws Exception{
		hashCode = hashCode.substring(2, hashCode.length() - 2);
		
		long shipsCode = Utilities.parseBase(hashCode.substring(0, hashCode.indexOf(".")), 84);
		
		// Id des vaisseaux
		int[] shipsId = new int[5];
		int slotsCount = 0;
		for (int i = 0; i < 5; i++) {
			shipsId[i] = (int) ((shipsCode >> (i * 8)) & 0xff);
			
			if (shipsId[i] != 0 && Ship.SHIPS[shipsId[i]] == null)
				throw new Exception("Invalid hash code (Err 02).");
			
			if (shipsId[i] > 0)
				slotsCount++;
		}
		
		hashCode = hashCode.substring(hashCode.indexOf(".") + 1);
		
		// Nombre de vaisseaux
		long[] shipsCount = new long[5];
		for (int i = 0; i < 5; i++) {
			if (shipsId[i] > 0) {
				if (!hashCode.contains("."))
					throw new Exception("Invalid hash code (Err 03).");
				
				shipsCount[i] = Utilities.parseBase(hashCode.substring(0, hashCode.indexOf(".")), 84);
				
				hashCode = hashCode.substring(hashCode.indexOf(".") + 1);
			}
		}
		
		// Position des vaisseaux
		if (hashCode.length() == 0)
			throw new Exception("Invalid hash code (Err 04).");
		
		boolean[] shipsFront = new boolean[5];
		int frontCode = (int) Utilities.parseBase(hashCode.substring(0, 1), 84);
		for (int i = 0; i < 5; i++) {
			shipsFront[i] = (frontCode & (1 << i)) != 0;
		}
//		for (int i = 1; i < 5; i++) {
//			if(shipsFront[i] !=true) throw new Exception("False "+i+" ,frontcode:"+(frontCode & (1 << i)));
//		}
		
		
		hashCode = hashCode.substring(1);
		if (hashCode.length() != 20)
			throw new Exception("Invalid hash code (Err 05).");
		
		//Création de la flotte

		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++){
			fleet.setSlot(new Slot(shipsId[i],		shipsCount[i], 		true), i);	
		}
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++){
			try {
				if (shipsFront[i] == false)
					fleet.setSlotFront(false, i);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
			


		
		// Tactique - escarmouche
		int[] skirmishSlots = new int[5];
		int[] skirmishAbilities = new int[5];
		
		long slotsCode = Utilities.parseBase(hashCode.substring(0, 3), 84);
		for (int i = 0; i < skirmishSlots.length; i++) {
			int slot = (int) ((slotsCode >> (3 * i)) & 7);
			
			if (slot != 5 && (slot < 0 || slot >= shipsId.length || shipsId[slot] == 0))
				throw new Exception("Invalid hash code (Err 06).");
			
			skirmishSlots[i] = slot == 5 ? -1 : slot;
		}
		
		long abilitiesCode = Utilities.parseBase(hashCode.substring(3, 6), 84);
		for (int i = 0; i < skirmishAbilities.length; i++) {
			int ability = (int) ((abilitiesCode >> (3 * i)) & 7);
			
			if (ability != 5) {
				Ability[] abilities = Ship.SHIPS[shipsId[skirmishSlots[i]]].getAbilities();
				
				if (ability < 0 || ability >= abilities.length ||
						abilities[ability].isPassive())
					throw new Exception("Invalid hash code (Err 07).");
			}
			
			skirmishAbilities[i] = ability == 5 ? -1 : ability;
		}
		
		// Tactique - bataille
		int[] battleSlots = new int[15];
		int[] battleAbilities = new int[15];
		
		slotsCode = Utilities.parseBase(hashCode.substring(6, 13), 84);
		for (int i = 0; i < battleSlots.length; i++) {
			int slot = (int) ((slotsCode >> (3 * i)) & 7);
			
			if (slot != 5 && (slot < 0 || slot >= shipsId.length || shipsId[slot] == 0))
				throw new Exception("Invalid hash code (Err 08).");
			
			battleSlots[i] = slot == 5 ? -1 : slot;
		}
		
		abilitiesCode = Utilities.parseBase(hashCode.substring(13, 20), 84);
		for (int i = 0; i < battleAbilities.length; i++) {
			int ability = (int) ((abilitiesCode >> (3 * i)) & 7);
			
			if (ability != 5) {
				Ability[] abilities = Ship.SHIPS[shipsId[battleSlots[i]]].getAbilities();
				
				if (ability < 0 || ability >= abilities.length ||
						abilities[ability].isPassive())
					throw new Exception("Invalid hash code (Err 09).");
			}
			
			battleAbilities[i] = ability == 5 ? -1 : ability;
		}
		
		/*Création des tactiques*/
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++){
		fleet.setSkirmishActions(skirmishSlots, skirmishAbilities);
		fleet.setBattleActions(battleSlots, battleAbilities);
		}
		
		return fleet;
	}
	
	// A utiliser avec FleetData.getPowerLevel(lvl)
	public static Fleet createFleetByHashAndPower(Fleet fleet, String hashCode, int power) throws Exception{
		hashCode = hashCode.substring(2, hashCode.length() - 2);
		
		long shipsCode = Utilities.parseBase(hashCode.substring(0, hashCode.indexOf(".")), 84);
		
		// Id des vaisseaux
		int[] shipsId = new int[5];
		int slotsCount = 0;
		for (int i = 0; i < 5; i++) {
			shipsId[i] = (int) ((shipsCode >> (i * 8)) & 0xff);
			
			if (shipsId[i] != 0 && Ship.SHIPS[shipsId[i]] == null)
				throw new Exception("Invalid hash code (Err 02).");
			
			if (shipsId[i] > 0)
				slotsCount++;
		}
		
		hashCode = hashCode.substring(hashCode.indexOf(".") + 1);
		
		// Nombre de vaisseaux
		long[] shipsCount = new long[5];
		for (int i = 0; i < 5; i++) {
			if (shipsId[i] > 0) {
				if (!hashCode.contains("."))
					throw new Exception("Invalid hash code (Err 03).");
				
				shipsCount[i] = Utilities.parseBase(hashCode.substring(0, hashCode.indexOf(".")), 84);
				
				hashCode = hashCode.substring(hashCode.indexOf(".") + 1);
			}
		}
		
		int powerBase = 0;
		int lowestShip = 0;
		int lowestClass = 0;
		// puissance de la flotte
		for (int i = 0; i < 5; i++) {
			if (shipsId[i] > 0) {
				if(Ship.SHIPS[shipsId[i]].getShipClass()>lowestShip){
					lowestShip = i;
					lowestClass = Ship.SHIPS[shipsId[i]].getShipClass();
				}
				powerBase+=shipsCount[i]*Math.pow(10, Ship.SHIPS[shipsId[i]].getShipClass()-1);
				
			}
		}
		double ratio = power/powerBase;
		
		powerBase=0;
		for (int i = 0; i < 5; i++) {
			if (shipsId[i] > 0) {
				
				shipsCount[i]=(long) Math.floor(shipsCount[i]*ratio);
				powerBase+=shipsCount[i]*Math.pow(10, Ship.SHIPS[shipsId[i]].getShipClass()-1);
			}
		}
		
		if(powerBase < power*.99){
			
			shipsCount[lowestShip]+=(long)Math.floor((power-powerBase)/Math.pow(10, lowestClass-1));
		}
		
		
		// Position des vaisseaux
		if (hashCode.length() == 0)
			throw new Exception("Invalid hash code (Err 04).");
		
		boolean[] shipsFront = new boolean[5];
		int frontCode = (int) Utilities.parseBase(hashCode.substring(0, 1), 84);
		for (int i = 0; i < 5; i++) {
			shipsFront[i] = (frontCode & (1 << i)) != 0;
		}
//		for (int i = 1; i < 5; i++) {
//			if(shipsFront[i] !=true) throw new Exception("False "+i+" ,frontcode:"+(frontCode & (1 << i)));
//		}
		
		
		hashCode = hashCode.substring(1);
		if (hashCode.length() != 20)
			throw new Exception("Invalid hash code (Err 05).");
		
		//Création de la flotte

		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++){
			fleet.setSlot(new Slot(shipsId[i],		shipsCount[i], 		true), i);	
		}
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++){
			try {
				if (shipsFront[i] == false)
					fleet.setSlotFront(false, i);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
			


		
		// Tactique - escarmouche
		int[] skirmishSlots = new int[5];
		int[] skirmishAbilities = new int[5];
		
		long slotsCode = Utilities.parseBase(hashCode.substring(0, 3), 84);
		for (int i = 0; i < skirmishSlots.length; i++) {
			int slot = (int) ((slotsCode >> (3 * i)) & 7);
			
			if (slot != 5 && (slot < 0 || slot >= shipsId.length || shipsId[slot] == 0))
				throw new Exception("Invalid hash code (Err 06).");
			
			skirmishSlots[i] = slot == 5 ? -1 : slot;
		}
		
		long abilitiesCode = Utilities.parseBase(hashCode.substring(3, 6), 84);
		for (int i = 0; i < skirmishAbilities.length; i++) {
			int ability = (int) ((abilitiesCode >> (3 * i)) & 7);
			
			if (ability != 5) {
				Ability[] abilities = Ship.SHIPS[shipsId[skirmishSlots[i]]].getAbilities();
				
				if (ability < 0 || ability >= abilities.length ||
						abilities[ability].isPassive())
					throw new Exception("Invalid hash code (Err 07).");
			}
			
			skirmishAbilities[i] = ability == 5 ? -1 : ability;
		}
		
		// Tactique - bataille
		int[] battleSlots = new int[15];
		int[] battleAbilities = new int[15];
		
		slotsCode = Utilities.parseBase(hashCode.substring(6, 13), 84);
		for (int i = 0; i < battleSlots.length; i++) {
			int slot = (int) ((slotsCode >> (3 * i)) & 7);
			
			if (slot != 5 && (slot < 0 || slot >= shipsId.length || shipsId[slot] == 0))
				throw new Exception("Invalid hash code (Err 08).");
			
			battleSlots[i] = slot == 5 ? -1 : slot;
		}
		
		abilitiesCode = Utilities.parseBase(hashCode.substring(13, 20), 84);
		for (int i = 0; i < battleAbilities.length; i++) {
			int ability = (int) ((abilitiesCode >> (3 * i)) & 7);
			
			if (ability != 5) {
				Ability[] abilities = Ship.SHIPS[shipsId[battleSlots[i]]].getAbilities();
				
				if (ability < 0 || ability >= abilities.length ||
						abilities[ability].isPassive())
					throw new Exception("Invalid hash code (Err 09).");
			}
			
			battleAbilities[i] = ability == 5 ? -1 : ability;
		}
		
		/*Création des tactiques*/
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++){
		fleet.setSkirmishActions(skirmishSlots, skirmishAbilities);
		fleet.setBattleActions(battleSlots, battleAbilities);
		}
		
		return fleet;
	}
	
	public static Fleet createPirateFleet(int level, int x, int y, int idArea) throws Exception {
		return createPirateFleet(level, x, y, idArea, 0);
	}
	
	public static Fleet createPirateFleet(int level, int x, int y, int idArea, int pnjId) throws Exception {
		
		Fleet fleet = null;
		
		if(pnjId==0){
			Player pirate = getPiratePlayer(level);
			
			if (pirate == null)
				throw new IllegalArgumentException("Cannot create " +
						"pirate fleet at level " + level + ".");
			
			fleet = new Fleet("Pirate " + level, x, y, pirate.getId(), idArea);
		}
		else
		{
			fleet = new Fleet("Defender " + level, x, y, pnjId, idArea);
		}
		
		fleet.setBasicSkill(new Skill(Skill.SKILL_PIRATE, 0), 0);
		String hashCode="";
		
		// Vaisseaux de la flotte
		switch (level) {
			// ------------------------------------------------------- Niveau 1
		case 1:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 250 recons - 250 corsairs
				fleet.setSlot(new Slot(Ship.RECON,		   250, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	   250, true), 1);
				fleet.setSlotFront(false, 0);
				
				fleet.setEncodedTactics("01909", "00999",
						"019099190990190", "009999090990099");
				break;
				
			case 2:
				// 20 valkyries - 300 corsairs
				fleet.setSlot(new Slot(Ship.VALKYRIE,	    20, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	   300, true), 1);
				fleet.setSlotFront(false, 1);
				break;
				
			case 3:
				// 15 valkyries - 150 fortress - 200 recons
				fleet.setSlot(new Slot(Ship.VALKYRIE,	    15, true), 0);
				fleet.setSlot(new Slot(Ship.FORTRESS,	   150, true), 1);
				fleet.setSlot(new Slot(Ship.RECON,		   200, true), 2);
				fleet.setSlotFront(false, 2);
				
				fleet.setEncodedTactics("02192", "90999",
						"201299201299201", "099099099099999");
				break;
				
			case 4:
				// 15 valkyries - 350 recons
				fleet.setSlot(new Slot(Ship.VALKYRIE,	    15, true), 0);
				fleet.setSlot(new Slot(Ship.RECON,		   350, true), 1);
				fleet.setSlotFront(false, 1);
				
				fleet.setEncodedTactics("10919", "09999",
						"109199109991099", "099099999999999");
				break;
				
			case 5:
				// 280 corsairs - 220 fortress
				fleet.setSlot(new Slot(Ship.CORSAIR,	   280, true), 0);
				fleet.setSlot(new Slot(Ship.FORTRESS,	   220, true), 1);
				
				fleet.setEncodedTactics("01999", "99999",
						"019990199901999", "999990999909999");
				break;
			}
			break;
			
			// ------------------------------------------------------- Niveau 2
		case 2:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 19 valkyries - 350 corsairs
				fleet.setSlot(new Slot(Ship.VALKYRIE,	    19, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	   350, true), 1);
				fleet.setSlotFront(false, 0);
				
				fleet.setEncodedTactics("01999", "90999",
						"019990199091909", "999990099090909");
				break;
				
			case 2:
				// 25 valkyries - 20 corsairs - 250 zéros - 20 fortress
				fleet.setSlot(new Slot(Ship.VALKYRIE,	    25, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	    20, true), 1);
				fleet.setSlot(new Slot(Ship.ZERO,		   250, true), 2);
				fleet.setSlot(new Slot(Ship.FORTRESS,	    20, true), 3);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 3);
				
				fleet.setEncodedTactics("20319", "99099",
						"320139203192031", "099909991999919");
				break;
				
			case 3:
				// 300 recons - 20 corsairs - 20 fortress - 200 zéros
				fleet.setSlot(new Slot(Ship.RECON,		   300, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	    20, true), 1);
				fleet.setSlot(new Slot(Ship.FORTRESS,	    20, true), 2);
				fleet.setSlot(new Slot(Ship.ZERO,	 	   200, true), 3);
				fleet.setSlotFront(false, 0);
				
				fleet.setEncodedTactics("03102", "09999",
						"023012039021390", "009091099909999");
				break;
				
			case 4:
				// 32 raptors - 20 valkyries - 20 corsairs
				hashCode="[[2raw.w.k.k.r3n63qdÀÏIRÒÎiÉJqÌVtN]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 5:
				// 270 recons - 270 corsairs
				fleet.setSlot(new Slot(Ship.RECON,		   270, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	   270, true), 1);
				fleet.setSlotFront(false, 0);
				
				fleet.setEncodedTactics("01909", "00999",
						"019099190990190", "009999090990099");
				break;
			}
			break;
			
			// ------------------------------------------------------- Niveau 3
		case 3:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 23 valkyries - 400 corsairs
				fleet.setSlot(new Slot(Ship.VALKYRIE,	    23, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	   400, true), 1);
				fleet.setSlotFront(false, 0);
				
				fleet.setEncodedTactics("01999", "90999",
						"019990199091909", "999990099090909");
				break;
			case 2:
				// 340 recons - 20 corsairs - 20 fortress - 250 zéros
				fleet.setSlot(new Slot(Ship.RECON,		   340, true), 0);
				fleet.setSlot(new Slot(Ship.CORSAIR,	    20, true), 1);
				fleet.setSlot(new Slot(Ship.FORTRESS,	    20, true), 2);
				fleet.setSlot(new Slot(Ship.ZERO,	 	   250, true), 3);
				fleet.setSlotFront(false, 0);
				
				fleet.setEncodedTactics("03102", "09999",
						"023012039021390", "009091099909999");
				break;
			case 3:
				// 36 raptors - 25 valkyries - 20 corsairs
				hashCode="[[2raw.A.k.p.r3n63qdÀÏIRÒÎiÉJqÌVtN]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				// 100 fortress - 380 recons - 150 zéros
				fleet.setSlot(new Slot(Ship.FORTRESS,	   100, true), 0);
				fleet.setSlot(new Slot(Ship.RECON,		   380, true), 1);
				fleet.setSlot(new Slot(Ship.ZERO,		   150, true), 2);
				fleet.setSlotFront(false, 1);
				
				fleet.setEncodedTactics("12019", "09999",
						"102190129091209", "009091999099919");
				break;
			case 5:
				// 23 hellcats - 400 recons
				fleet.setSlot(new Slot(Ship.HELLCAT,	    23, true), 0);
				fleet.setSlot(new Slot(Ship.RECON,		   400, true), 1);
				fleet.setSlotFront(false, 1);
				
				fleet.setEncodedTactics("10919", "09999",
						"109199109991099", "099099999999999");
				break;
			}
			break;

			// ------------------------------------------------------- Niveau 4
		case 4:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 4 paladins - 340 recons - 20 corsairs
				fleet.setSlot(new Slot(Ship.PALADIN,		 4, true), 0);
				fleet.setSlot(new Slot(Ship.RECON,		   340, true), 1);
				fleet.setSlot(new Slot(Ship.CORSAIR,		20, true), 2);
				fleet.setSlotFront(false, 1);
				
				fleet.setEncodedTactics("01291", "10999",
						"012910912909129", "109901999919999");
				break;
				
			case 2:
				// 32 spectres - 5 sentinelles - 5 valkyries - 320 recons - 2 hellcats
				fleet.setSlot(new Slot(Ship.SPECTRE,		32, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	 5, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		 5, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   320, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 2, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029130", "100190109919199");
				break;
				
			case 3:
				// 3 hellcats - 15 valkyries - 2 paladins - 19 spectres - 19 raptors
				fleet.setSlot(new Slot(Ship.HELLCAT,		 3, true), 0);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		15, true), 1);
				fleet.setSlot(new Slot(Ship.PALADIN,		 2, true), 2);
				fleet.setSlot(new Slot(Ship.SPECTRE,		19, true), 3);
				fleet.setSlot(new Slot(Ship.RAPTOR,			19, true), 4);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10423", "19099",
						"102439102314021", "101099091900910");
				break;
				
			case 4:
				// 60 corsairs - 70 spectres
				fleet.setSlot(new Slot(Ship.CORSAIR,		60, true), 0);
				fleet.setSlot(new Slot(Ship.SPECTRE,		70, true), 1);
				fleet.setSlotFront(false, 0);
				break;
				
			case 5:
				// 47 raptors - 5 hellcats - 24 valkyries
				fleet.setSlot(new Slot(Ship.RAPTOR,			47, true), 0);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 5, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		24, true), 2);
				fleet.setSlotFront(false, 2);
				
				fleet.setEncodedTactics("01299", "09099",
						"012999102991290", "090999900999099");
				break;
			}
			break;

			// ------------------------------------------------------- Niveau 5
		case 5:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 33 spectres - 8 sentinelles - 8 valkyries - 420 recons - 2 hellcats
				fleet.setSlot(new Slot(Ship.SPECTRE,		33, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	 8, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		 8, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   420, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 2, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029130", "100190109919199");
				break;
				
			case 2:
				// 40 corsairs - 425 blades - 425 fortress - 4 sentinelles
				fleet.setSlot(new Slot(Ship.CORSAIR,		40, true), 0);
				fleet.setSlot(new Slot(Ship.BLADE,	 	   425, true), 1);
				fleet.setSlot(new Slot(Ship.FORTRESS,	   425, true), 2);
				fleet.setSlot(new Slot(Ship.SENTINEL,	     4, true), 3);
				fleet.setSlotFront(false, 0);
				fleet.setSlotFront(false, 3);
				
				fleet.setEncodedTactics("31209", "09999",
						"312093120931209", "099990999909999");
				break;
				
			case 3:
				// 6
				fleet.setSlot(new Slot(Ship.HELLCAT,		 5, true), 0);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		20, true), 1);
				fleet.setSlot(new Slot(Ship.PALADIN,		 2, true), 2);
				fleet.setSlot(new Slot(Ship.SPECTRE,		24, true), 3);
				fleet.setSlot(new Slot(Ship.RAPTOR,			24, true), 4);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10423", "19099",
						"102439102314021", "100099090900990");
				break;
				
			case 4:
				// 7
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 2, true), 0);
				fleet.setSlot(new Slot(Ship.STYX,		 	 1, true), 1);
				fleet.setSlot(new Slot(Ship.LIBERTY,		 2, true), 2);
				fleet.setSlot(new Slot(Ship.PALADIN,		 2, true), 3);
				fleet.setSlot(new Slot(Ship.FURY,			 2, true), 4);
				fleet.setSlotFront(false, 2);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("43012", "00909",
						"432013420139240", "009900099019909");
				break;
				
			case 5:
				// 8
				fleet.setSlot(new Slot(Ship.SPECTRE,		35, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	 8, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		 8, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   400, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 2, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029930", "100190209909999");
				break;
			}
			break;

			// ------------------------------------------------------- Niveau 6
		case 6:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 4
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 4, true), 0);
				fleet.setSlot(new Slot(Ship.STYX,			 2, true), 1);
				fleet.setSlot(new Slot(Ship.LIBERTY,		 5, true), 2);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 3, true), 3);
				fleet.setSlot(new Slot(Ship.CORSAIR,		20, true), 4);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("31402", "00999",
						"213041290134902", "900990199009999");
				break;
			case 2:
				// 8
				fleet.setSlot(new Slot(Ship.SPECTRE,		45, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	12, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		12, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   430, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 3, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029930", "100190209909999");
				break;
			case 3:
				// 2
				fleet.setSlot(new Slot(Ship.SERAPHIM,	     3, true), 0);
				fleet.setSlot(new Slot(Ship.PALADIN,		 2, true), 1);
				fleet.setSlot(new Slot(Ship.BLADE,		   260, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,			15, true), 3);
				fleet.setSlot(new Slot(Ship.FORTRESS,		15, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10234", "19999",
						"120431204132041", "199290992199999");
				break;
			case 4:
				// 9
				fleet.setSlot(new Slot(Ship.CORSAIR,	   100, true), 0);
				fleet.setSlot(new Slot(Ship.SPECTRE,	   105, true), 1);
				fleet.setSlotFront(false, 0);
				break;
			case 5:
				// 11
				fleet.setSlot(new Slot(Ship.RAPTOR,			65, true), 0);
				fleet.setSlot(new Slot(Ship.HELLCAT,		10, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		40, true), 2);
				fleet.setSlotFront(false, 2);
				
				fleet.setEncodedTactics("01299", "09099",
						"012999102991290", "090999900999099");
				break;
			}
			break;

			// ------------------------------------------------------- Niveau 7
		case 7:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 1
				fleet.setSlot(new Slot(Ship.SERAPHIM,	     3, true), 0);
				fleet.setSlot(new Slot(Ship.PALADIN,		 4, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		 3, true), 2);
				fleet.setSlot(new Slot(Ship.SPECTRE,		66, true), 3);
				fleet.setSlot(new Slot(Ship.SENTINEL,		 3, true), 4);
				fleet.setSlotFront(false, 2);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("41203", "20199",
						"412031940213902", "201991929019999");
				break;
			case 2:
				// 5
				fleet.setSlot(new Slot(Ship.LIBERTY,		 3, true), 0);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		30, true), 1);
				fleet.setSlot(new Slot(Ship.ZERO,		   370, true), 2);
				fleet.setSlot(new Slot(Ship.SENTINEL,		 5, true), 3);
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 4, true), 4);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 3);
				
				fleet.setEncodedTactics("13240", "12099",
						"012340192413091", "900291090902990");
				break;
			case 3:
				// 6
				fleet.setSlot(new Slot(Ship.HELLCAT,		 6, true), 0);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		32, true), 1);
				fleet.setSlot(new Slot(Ship.PALADIN,		 3, true), 2);
				fleet.setSlot(new Slot(Ship.SPECTRE,		37, true), 3);
				fleet.setSlot(new Slot(Ship.RAPTOR,			37, true), 4);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10423", "19099",
						"102439102314021", "100099090900990");
				break;
			case 4:
				// 7
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 3, true), 0);
				fleet.setSlot(new Slot(Ship.STYX,		 	 2, true), 1);
				fleet.setSlot(new Slot(Ship.LIBERTY,		 3, true), 2);
				fleet.setSlot(new Slot(Ship.PALADIN,		 3, true), 3);
				fleet.setSlot(new Slot(Ship.FURY,			 3, true), 4);
				fleet.setSlotFront(false, 2);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("43012", "00909",
						"432013420139240", "009900099019909");
				break;
			case 5:
				// 8
				fleet.setSlot(new Slot(Ship.SPECTRE,		55, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	15, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		15, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   550, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 2, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029930", "100190209909999");
				break;
			}
			break;

			// ------------------------------------------------------- Niveau 8
		case 8:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 4
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 6, true), 0);
				fleet.setSlot(new Slot(Ship.STYX,			 3, true), 1);
				fleet.setSlot(new Slot(Ship.LIBERTY,		 7, true), 2);
				fleet.setSlot(new Slot(Ship.HELLCAT,		10, true), 3);
				fleet.setSlot(new Slot(Ship.CORSAIR,		60, true), 4);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("31402", "00999",
						"213041290134902", "900990199009999");
				break;
			case 2:
				// 8
				fleet.setSlot(new Slot(Ship.SPECTRE,		77, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	18, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		18, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   600, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 3, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029930", "100190209909999");
				break;
			case 3:
				// 2
				fleet.setSlot(new Slot(Ship.SERAPHIM,	     6, true), 0);
				fleet.setSlot(new Slot(Ship.PALADIN,		 5, true), 1);
				fleet.setSlot(new Slot(Ship.BLADE,		   610, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,			25, true), 3);
				fleet.setSlot(new Slot(Ship.FORTRESS,		25, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10234", "19999",
						"120431204132041", "199290992199999");
				break;
			case 4:
				// 9
				fleet.setSlot(new Slot(Ship.CORSAIR,	   110, true), 0);
				fleet.setSlot(new Slot(Ship.SPECTRE,	   165, true), 1);
				fleet.setSlotFront(false, 0);
				break;
			case 5:
				// 11
				fleet.setSlot(new Slot(Ship.RAPTOR,		   100, true), 0);
				fleet.setSlot(new Slot(Ship.HELLCAT,		16, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		60, true), 2);
				fleet.setSlotFront(false, 2);
				
				fleet.setEncodedTactics("01299", "09099",
						"012999102991290", "090999900999099");
				break;
			}
			break;

			// ------------------------------------------------------- Niveau 9
		case 9:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 1
				fleet.setSlot(new Slot(Ship.SERAPHIM,	     4, true), 0);
				fleet.setSlot(new Slot(Ship.PALADIN,		 5, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		 4, true), 2);
				fleet.setSlot(new Slot(Ship.SPECTRE,		83, true), 3);
				fleet.setSlot(new Slot(Ship.SENTINEL,		 5, true), 4);
				fleet.setSlotFront(false, 2);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("41203", "20199",
						"412031940213902", "201991929019999");
				break;
			case 2:
				// 5
				fleet.setSlot(new Slot(Ship.LIBERTY,		 5, true), 0);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		50, true), 1);
				fleet.setSlot(new Slot(Ship.ZERO,		   530, true), 2);
				fleet.setSlot(new Slot(Ship.SENTINEL,		 5, true), 3);
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 6, true), 4);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 3);
				
				fleet.setEncodedTactics("13240", "12099",
						"012340192413091", "900291090902990");
				break;
			case 3:
				// 6
				fleet.setSlot(new Slot(Ship.HELLCAT,		10, true), 0);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		52, true), 1);
				fleet.setSlot(new Slot(Ship.PALADIN,		 4, true), 2);
				fleet.setSlot(new Slot(Ship.SPECTRE,		58, true), 3);
				fleet.setSlot(new Slot(Ship.RAPTOR,			58, true), 4);
				fleet.setSlotFront(false, 1);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10423", "19099",
						"102439102314021", "100099090900990");
				break;
			case 4:
				// 7
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 5, true), 0);
				fleet.setSlot(new Slot(Ship.STYX,		 	 4, true), 1);
				fleet.setSlot(new Slot(Ship.LIBERTY,		 4, true), 2);
				fleet.setSlot(new Slot(Ship.PALADIN,		 4, true), 3);
				fleet.setSlot(new Slot(Ship.FURY,			 4, true), 4);
				fleet.setSlotFront(false, 2);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("43012", "00909",
						"432013420139240", "009900099019909");
				break;
			case 5:
				// 8
				fleet.setSlot(new Slot(Ship.SPECTRE,		90, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	30, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		30, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   650, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 3, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029930", "100190209909999");
				break;
			}
			break;

			// ------------------------------------------------------ Niveau 10
		case 10:
			switch (Utilities.random(1, 5)) {
			case 1:
				// 9 séraphins - 3 styx - 9 liberties - 50 hellcats - 80 corsairs
				fleet.setSlot(new Slot(Ship.SERAPHIM,		 9, true), 0);
				fleet.setSlot(new Slot(Ship.STYX,			 3, true), 1);
				fleet.setSlot(new Slot(Ship.LIBERTY,		 9, true), 2);
				fleet.setSlot(new Slot(Ship.HELLCAT,		50, true), 3);
				fleet.setSlot(new Slot(Ship.CORSAIR,		80, true), 4);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("31402", "00999",
						"213041290134902", "900990199009999");
				break;
				
			case 2:
				// 140 spectres - 22 sentinelles - 22 valkyries - 800 recons - 4 hellcats
				fleet.setSlot(new Slot(Ship.SPECTRE,	   140, true), 0);
				fleet.setSlot(new Slot(Ship.SENTINEL,	 	22, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		22, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,		   800, true), 3);
				fleet.setSlot(new Slot(Ship.HELLCAT,		 4, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("13203", "10199",
						"143203143029930", "100190209909999");
				break;
				
			case 3:
				// 9 séraphins - 9 paladins - 810 blades - 35 recons - 35 fortress
				fleet.setSlot(new Slot(Ship.SERAPHIM,	     9, true), 0);
				fleet.setSlot(new Slot(Ship.PALADIN,		 9, true), 1);
				fleet.setSlot(new Slot(Ship.BLADE,		   810, true), 2);
				fleet.setSlot(new Slot(Ship.RECON,			35, true), 3);
				fleet.setSlot(new Slot(Ship.FORTRESS,		35, true), 4);
				fleet.setSlotFront(false, 3);
				fleet.setSlotFront(false, 4);
				
				fleet.setEncodedTactics("10234", "19999",
						"120431204132041", "199290992199999");
				break;
				
			case 4:
				// 180 corsairs - 250 spectres
				fleet.setSlot(new Slot(Ship.CORSAIR,	   180, true), 0);
				fleet.setSlot(new Slot(Ship.SPECTRE,	   250, true), 1);
				fleet.setSlotFront(false, 0);
				break;
				
			case 5:
				// 150 raptors - 28 hellcats - 90 valkyries
				fleet.setSlot(new Slot(Ship.RAPTOR,		   150, true), 0);
				fleet.setSlot(new Slot(Ship.HELLCAT,		28, true), 1);
				fleet.setSlot(new Slot(Ship.VALKYRIE,		90, true), 2);
				fleet.setSlotFront(false, 2);
				
				fleet.setEncodedTactics("01299", "09099",
						"012999102991290", "090999900999099");
				break;
			}
			break;
			
			
		case 11:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[eÌaÈl.1.1.g.g.s3aÌ3nÎC6w7PRSÂoMrUW8]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[7ÅÏÑt.1.a.a.1T.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[ehÉÐÓ.1.a.a.d.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[fKÓMË.1.a.b.c.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4hÌJ0Ë.1.d.1.Ò.ee.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[4gÉHE9.1.1.Õ.1Z.c2.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[LlSolR.1.1.Ò.bÄ.f.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[KqÄnOÃ.1.1.20.1Î.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
			
		case 12:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.c.c.27.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[7ÅÏÑt.1.c.c.27.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[ehÉÐÓ.1.c.c.h.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[fKÓMË.1.b.c.i.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4hÌJ0Ë.1.j.1.1c.ee.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[4gÉHE9.1.1.1f.2h.ey.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[LlSolR.1.1.1b.fE.i.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[KqÄnOÃ.1.1.2E.2s.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 13:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[eÌaÈl.1.1.s.l.s3aÌ3nÎC6w7PRSÂoMrUW8]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[7ÅÏÑt.1.e.e.2V.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[ehÉÐÓ.1.f.f.k.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[fKÓMË.1.e.f.l.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4hÌJ0Ë.1.m.1.1G.hÀ.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[4gÉHE9.1.1.1D.2X.hm.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[LlSolR.1.1.1v.gU.o.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[KqÄnOÃ.1.1.2Ò.2Ð.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 14:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[eÌaÈl.1.1.y.q.s3aÌ3nÎC6w7PRSÂoMrUW8]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[7ÅÏÑt.1.h.h.3i.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[ehÉÐÓ.1.h.h.r.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[fKÓMË.1.i.j.o.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4hÌJ0Ë.1.q.1.1Å.mG.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[4gÉHE9.1.1.1Ç.3a.m2.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[LlSolR.1.1.1Ä.lA.r.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[KqÄnOÃ.1.1.3Z.3y.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 15:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[eÌaÈl.1.1.E.w.s3aÌ3nÎC6w7PRSÂoMrUW8]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[7ÅÏÑt.1.k.k.3Ó.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[ehÉÐÓ.1.l.l.v.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[fKÓMË.1.l.m.u.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4hÌJ0Ë.1.x.1.28.pÈ.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[4gÉHE9.1.1.2c.3Ó.p0.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[LlSolR.1.1.2p.rw.u.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[KqÄnOÃ.1.1.4E.4a.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 16:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.o.o.4X.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.p.p.B.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.p.q.A.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.H.1.2u.si.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.2I.4Ï.sM.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.2Ã.ww.A.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KqÄnOÃ.1.1.5C.4È.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[Jul9kV.1.1.4t.p.p.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 17:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.t.t.5C.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.u.u.H.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.u.v.G.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.M.1.36.yI.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.3a.5U.yI.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.3v.CW.G.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KqÄnOÃ.1.1.6H.5Y.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[Jul9kV.1.1.58.u.u.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 18:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.y.y.6D.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.z.z.Q.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.z.A.P.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.O.1.4c.HC.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.3Y.6B.GÊ.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.47.KA.M.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KqÄnOÃ.1.1.7S.6Ä.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[Jul9kV.1.1.6j.z.y.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 19:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.F.F.7n.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.E.E.Á.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.F.J.V.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.X.1.4Ã.Pg.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.4A.7F.O0.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.4Ã.RM.V.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KqÄnOÃ.1.1.8Ð.7Ô.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[Jul9kV.1.1.7x.E.E.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 20:
			switch (Utilities.random(1, 12)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.O.O.7Ï.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.K.K.Ì.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.L.R.Ä.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.Ê.1.5z.Vc.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.5c.8É.Vc.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.5Ã.YY.Ä.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KqÄnOÃ.1.1.aI.9e.1.s2xC2ÏËPYÄ1Mu2ÀÐpsÏH1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[KDpÑXd.1.4.6.6.6.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 9:
				hashCode="[[Jul9kV.1.1.8H.L.L.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[nË94ÀÃ.1.1.5.5.7Å.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[JAXtGÃ.1.1.5.5.Ã.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 12:
				hashCode="[[HvÈcbZ.1.1.5.5.Ã.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 21:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.X.X.8Ì.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.R.R.12.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.R.W.Ó.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.10.1.6b.ÄÄ.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.5Ê.aI.Âo.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.6v.ÅÊ.Ó.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KDpÑXd.1.5.7.7.2.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[Jul9kV.1.1.9Ò.S.S.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 9:
				hashCode="[[nË94ÀÃ.1.1.6.6.8I.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[JAXtGÃ.1.1.5.6.Ó.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[HvÈcbZ.1.1.6.6.É.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 22:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.Ç.Ç.9Ð.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.Á.Á.1b.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.À.Ä.19.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.1e.1.75.Ía.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.6Ä.bÒ.Í0.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.7g.ÑÂ.19.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KDpÑXd.1.6.8.8.1.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[Jul9kV.1.1.bk.Á.Á.q0ÅC3nËKeBLÐUyÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 9:
				hashCode="[[nË94ÀÃ.1.1.6.7.aÂ.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[JAXtGÃ.1.1.6.6.1g.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[HvÈcbZ.1.1.7.6.16.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 23:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.Ï.Ï.bÄ.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.Ë.Ë.1n.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.Å.Í.1r.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.1p.1.8l.13À.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.7Q.cÊ.1bk.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.8w.13Q.1o.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KDpÑXd.1.7.9.9.3.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[nÅ1rÓl.1.1.É.É.d8.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 9:
				hashCode="[[nË94ÀÃ.1.1.7.7.ds.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[JAXtGÃ.1.1.7.7.1s.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[HvÈcbZ.1.1.8.7.1i.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 24:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.Õ.Õ.eE.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.Õ.Õ.1B.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.Ì.10.1J.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.1D.1.9B.1gu.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.8S.fk.1iw.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.9C.1jÆ.1A.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KDpÑXd.1.8.a.a.7.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[nÅ1rÓl.1.1.É.É.d8.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 9:
				hashCode="[[nË94ÀÃ.1.1.8.8.f6.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[JAXtGÃ.1.1.8.8.1G.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[HvÈcbZ.1.1.9.8.1w.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 25:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[7ÅÏÑt.1.17.17.h3.62Ôb3nË22SdxZÁwÕFÇKsR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[ehÉÐÓ.1.19.19.1T.634b3nÆrrQÌE4XÈLcqlÄÊ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[fKÓMË.1.Õ.16.1Ä.62ÎÅ37ËÂLyhÔJLÉDdbESÏ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[4hÌJ0Ë.1.1Ä.1.a3.1mq.s1yË3mEI1ÂuDpZÀTXFL5k]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[4gÉHE9.1.1.a6.hc.1tW.m0Â33mJpÍz8ojÉÃÊWÌgÆÇ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			
			case 6:
				hashCode="[[LlSolR.1.1.aS.1va.1S.e0Áo3mJhPÏÍTNgÁRÊÉv99]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[KDpÑXd.1.9.c.b.5.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[nÅ1rÓl.1.1.19.19.gB.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				//TODO debug
			case 9:
				hashCode="[[nË94ÀÃ.1.1.9.9.hd.e1fw35ptÈdQZ3ÒÈÒ16Åsd]] ";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[JAXtGÃ.1.1.9.9.1Y.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[HvÈcbZ.1.1.a.8.1Y.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 26:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.a.d.d.5.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.1j.1j.iÌ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.a.b.iy.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.a.a.1Ò.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.b.9.1Ò.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 27:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.c.e.e.9.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.1j.1j.iÌ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.c.c.k1.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.b.b.2k.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.c.a.2k.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 28:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.e.g.f.5.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.1P.1P.lÔ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.d.d.ne.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.c.d.2A.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.e.b.2A.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 29:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.f.i.h.5.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.1Ä.1Ä.op.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.f.f.op.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.e.e.2U.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.f.d.2U.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 30:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.g.k.j.7.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.1Ó.1Ó.qÒ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.h.h.pÂ.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.g.g.2Æ.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.h.f.2Æ.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 31:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.j.l.l.2.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.2c.2c.tÑ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.i.i.tÑ.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.i.i.2Õ.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.j.h.2Õ.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 32:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.k.n.n.a.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.2r.2r.xl.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.k.k.w5.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.k.k.3h.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.m.i.3h.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 33:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.m.p.p.a.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.2J.2J.Ae.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.m.m.yG.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.m.m.3B.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.n.j.3V.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 34:
			switch (Utilities.random(1, 5)) {
			case 1:
				hashCode="[[KDpÑXd.1.o.s.r.3.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.2Á.2Á.Dv.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.o.o.Bj.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.m.m.4g.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.o.k.4g.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 35:
			switch (Utilities.random(1, 7)) {
			case 1:
				hashCode="[[KDpÑXd.1.q.u.t.8.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.2Ó.2Ó.GÉ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.q.q.Ej.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.o.o.3Y.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.q.m.4F.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[mÃ525E.2Q.qg.rs.2l.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[eÊ2lD.3Ï.1g.uÒ.38.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
				

			}
			break;
			
		case 36:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[KDpÑXd.1.s.w.w.6.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.3f.3f.KN.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.s.s.HF.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.p.p.55.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.s.o.4Ç.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			case 6:
				hashCode="[[mÃ525E.2X.sM.tY.2F.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 7:
				hashCode="[[7ÅÏÑÃ.k.2X.2À.vÓ.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 8:
				hashCode="[[eÊ2lD.4k.1g.xs.3s.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 37:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[KDpÑXd.1.u.z.y.5.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.3x.3x.OM.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.u.u.L0.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.r.r.5y.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.v.r.4Ð.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			case 6:
				hashCode="[[mÃ525E.2Ñ.tÁ.w8.2Z.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 7:
				hashCode="[[7ÅÏÑÃ.l.2Ñ.2Ô.yx.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 8:
				hashCode="[[eÊ2lD.4N.1g.zY.3M.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 38:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[KDpÑXd.1.w.B.B.7.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.3P.3P.SÃ.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.u.u.L0.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.t.t.5Ä.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.x.t.5q.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[mÃ525E.3f.wb.xB.2Ó.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 7:
				hashCode="[[7ÅÏÑÃ.n.3f.3i.zÀ.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 8:
				hashCode="[[eÊ2lD.4É.1g.CW.3Ë.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 39:
			switch (Utilities.random(1, 8)) {
			case 1:
				hashCode="[[KDpÑXd.1.y.E.D.a.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.3Ç.3Ç.Xf.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.y.y.ST.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.v.v.6f.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.z.v.5X.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[mÃ525E.3z.yH.yÕ.3h.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 7:
				hashCode="[[7ÅÏÑÃ.p.3s.3C.Ca.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 8:
				hashCode="[[eÊ2lD.50.1g.Gm.4j.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 40:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[KDpÑXd.1.B.G.G.4.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.3Ç.3Ç.Æ4.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.A.A.WÂ.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.y.y.6t.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.C.x.5X.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[mÉeÉhk.I4.xs.s.s.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[mÃ525E.3T.AÍ.AY.3B.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]] ";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 8:
				hashCode="[[7ÅÏÑÃ.q.3Q.3W.EG.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 9:
				hashCode="[[JnJzZÍ.p.p.2À.tb.2Ê.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 10:
				hashCode="[[KCRAby.A.1g.u.q.3w.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 11:
				hashCode="[[eÊ2lD.5k.1g.JE.4K.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 41:
			switch (Utilities.random(1, 11)) {
			case 1:
				hashCode="[[KDpÑXd.1.D.J.I.a.u0Pn0ÃlhZexQ77lBÍwf2Ó]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[nÅ1rÓl.1.1.4a.4a.Éc.e0Zo3nËxCÍ7XoÎÈEÔEËvË]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[nË94ÀÃ.1.1.C.C.Á4.e1fw35ptÈdQZ3ÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JAXtGÃ.1.1.A.A.6Ã.e1fw35ptÂÂXÕrÒÈÒ16Åsd]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[HvÈcbZ.1.1.F.A.6f.e0Åo2ÕÎFOcÁÈc4kEFYmÌ0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 6:
				hashCode="[[mÉeÉhk.JÒ.zY.u.u.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 7:
				hashCode="[[mÃ525E.3Í.Dn.CQ.3V.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 8:
				hashCode="[[7ÅÏÑÃ.s.3Ê.3Ð.GD.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 9:
				hashCode="[[JnJzZÍ.r.r.2Ð.ur.2Ô.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 10:
				hashCode="[[KCRAby.C.1g.v.t.3M.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			case 11:
				hashCode="[[eÊ2lD.5A.1g.N4.4Î.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
			
		case 42:
			switch (Utilities.random(1, 6)) {
			case 1:
				hashCode="[[mÉeÉhk.JÒ.zY.u.u.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.48.FT.F0.3Ï.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[7ÅÏÑÃ.v.48.4e.Hq.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JnJzZÍ.s.s.3b.wX.3i.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[KCRAby.F.1g.x.t.3Í.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[eÊ2lD.5Á.1g.Q2.5h.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 43:
			switch (Utilities.random(1, 6)) {
			case 1:
				hashCode="[[mÉeÉhk.LÕ.C8.w.w.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.4s.I3.Hf.4d.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[7ÅÏÑÃ.y.4s.4y.Io.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JnJzZÍ.t.t.3t.z7.3C.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[KCRAby.G.1g.y.w.4k.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[eÊ2lD.5Ô.1g.TO.5L.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 44:
			switch (Utilities.random(1, 6)) {
			case 1:
				hashCode="[[mÉeÉhk.QA.GÊ.A.A.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.4V.JÇ.IÓ.4x.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[7ÅÏÑÃ.z.4M.4S.LÁ.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JnJzZÍ.u.u.3N.Bx.3W.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[KCRAby.I.1g.A.y.4D.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[eÊ2lD.6l.1g.WÐ.5Í.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 45:
			switch (Utilities.random(1, 6)) {
			case 1:
				hashCode="[[mÉeÉhk.SÅ.Jk.C.C.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.4Ï.Mh.Lt.4R.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[7ÅÏÑÃ.B.4Æ.4Ì.Oa.v2ÎÅ32lÂLyqwGXÉB8ÁHÏh]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[JnJzZÍ.v.v.3X.EÓ.3Ï.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 5:
				hashCode="[[KCRAby.K.1g.C.A.4X.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 6:
				hashCode="[[eÊ2lD.6V.1g.ZK.6d.s3b43qRÅJjnKqEÈLdtwÍÓ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 46:
			switch (Utilities.random(1, 4)) {
			case 1:
				hashCode="[[mÉeÉhk.Vj.LQ.E.E.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.5d.ON.NZ.4Ë.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[JnJzZÍ.w.w.3Ò.Ht.4d.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[KCRAby.M.1g.E.C.4Ñ.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 47:
			switch (Utilities.random(1, 4)) {
			case 1:
				hashCode="[[mÉeÉhk.XY.O0.G.G.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.5x.QÓ.Qm.59.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[JnJzZÍ.x.x.4g.JË.4x.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[KCRAby.O.1g.G.E.5g.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
			
		case 48:
			switch (Utilities.random(1, 4)) {
			case 1:
				hashCode="[[mÉeÉhk.XY.QJ.J.J.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.5Â.SÁ.S4.5t.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[JnJzZÍ.z.z.4h.LT.4W.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[KCRAby.Q.1g.K.F.5r.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			}
			break;
		case 49:
			switch (Utilities.random(1, 4)) {
			case 1:
				hashCode="[[mÉeÉhk.Àm.SÏ.L.L.L.e1ÍI3qg2hÁÇ6Ä8ÉIaÎ8i0]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 2:
				hashCode="[[mÃ525E.60.Vv.Ux.5N.4.e1ÍQ3qRTfÑtl7IÉJdyxHÃ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 3:
				hashCode="[[JnJzZÍ.A.B.4i.OR.4Õ.71jÒ3qRpTXbncIÉJqÊDOl]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
				
			case 4:
				hashCode="[[KCRAby.S.1g.M.H.5N.s24h0ÕIjvpWtoiÈÁAqÎl9]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;
			}
			break;
			
		case 50:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[eÌaÈl.1.1.g.g.s3aÌ3nÎC6w7PRSÂoMrUW8]]";
				fleet=createFleetByHashAndPower(fleet, hashCode, Fleet.getPowerAtLevel(51));
				break;

			}
			break;
		case 51:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[eÌaÈl.u.O.bÎ.bÎ.c3aÌ3nÎC6w7PRSÂoMrUW8]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 52:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[nÅ5IfÔ.O.Y.5.7c.XI.s2JC3mJI1ÂZYGÄÈÒ1Ó8Ã1]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 53:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[MB4PÉs.ÉA.ÉA.5Ò.5Ò.5Ò.s2KÒ32gDfMÎÄoÒÉJqÌV5s]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 54:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[HwGQÃG.O.5Ò.O.O.9I.s24o2ÎÇTh8X7LÆ7ÈY0Ä4l]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 55:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[GnEjÌO.72.5Ò.72.72.72.s2zQ32gÇlTjqÂÂÃc6UmÍÆ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 56:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[nÅpqQÀ.7m.1g.9hÆ.7w.ÉA.s1Eh0vYtÃ8iCwx8ÆËEzvÎ]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 57:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[ÀÀ5ÅoÊ.ÉA.Y.9I.ÉA.O.s2KÒ0uMHÑqfImoÃnsÎdGE]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 58:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[ÂÑCISQ.9I.1.8.O.O.s2xC37ËVÕpÈcLmÉB7ÀouR]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 59:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[ÂÑÂRvM.7h.7h.O.O.O.s2KÒ2ÎtTfÔaÃpÆÈÁMbÈrN]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;
		case 60:
			switch (Utilities.random(1, 1)) {
			case 1:
				hashCode="[[ÀÆÅÎÏÕ.ÉA.5Ò.aY.8s.O.s2KË06MwnOw9pZ2U3ÈlÍY]]";
				fleet=createFleetByHash(fleet, hashCode);
				break;

			}
			break;







			
			
		
		default:
			throw new IllegalArgumentException("Cannot create " +
					"pirate fleet at level " + level + ".");
		}
		
		fleet.setTacticsDefined(true);
		fleet.save();
		
		return fleet;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}

