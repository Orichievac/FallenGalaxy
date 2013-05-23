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

import com.google.gwt.core.client.JavaScriptObject;


public class FleetData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static String
	PLAYER = "player",
	ENEMY = "enemy",
	ALLY = "ally",
	DEFENSIVE = "defensive",
	TOTAL = "total",
	ALLIED = "allied",
	NEUTRAL = "neutral",
	PIRATE = "pirate";
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_X = "b", //$NON-NLS-1$
		FIELD_Y = "c", //$NON-NLS-1$
		FIELD_NAME = "d", //$NON-NLS-1$
		FIELD_MOVEMENT = "e", //$NON-NLS-1$
		FIELD_MOVEMENT_RELOAD = "f", //$NON-NLS-1$
		FIELD_MOVEMENT_MAX = "g", //$NON-NLS-1$
		FIELD_LAST_MOVE = "i", //$NON-NLS-1$
		FIELD_AI = "j", //$NON-NLS-1$
		FIELD_LEVEL = "k", //$NON-NLS-1$
		FIELD_OWNER = "l", //$NON-NLS-1$
		FIELD_ALLY = "m", //$NON-NLS-1$
		FIELD_TREATY = "n", //$NON-NLS-1$
		FIELD_START_JUMP = "o", //$NON-NLS-1$
		FIELD_END_JUMP = "p", //$NON-NLS-1$
		FIELD_SKIN = "q", //$NON-NLS-1$
		FIELD_PIRATE = "s", //$NON-NLS-1$
		FIELD_COLONIZING = "t", //$NON-NLS-1$
		FIELD_CAN_JUMP = "u", //$NON-NLS-1$
		FIELD_SLOTS = "v", //$NON-NLS-1$
		FIELD_SHIPS = "w", //$NON-NLS-1$
		FIELD_CLASSES = "x", //$NON-NLS-1$
		FIELD_OFFENSIVE_LINK = "y", //$NON-NLS-1$
		FIELD_DEFENSIVE_LINK = "z", //$NON-NLS-1$
		FIELD_SKILLS = "A", //$NON-NLS-1$
		FIELD_XP = "B", //$NON-NLS-1$
		FIELD_CAPTURE = "C", //$NON-NLS-1$
		FIELD_STEALTH = "D", //$NON-NLS-1$
		FIELD_RESERVED = "E", //$NON-NLS-1$
		FIELD_NPC_ACTION = "F", //$NON-NLS-1$
		FIELD_POWER_LEVEL = "G", //$NON-NLS-1$
		FIELD_SCHEDULED_MOVE = "H", //$NON-NLS-1$
		FIELD_ALLY_TAG = "I", //$NON-NLS-1$
		FIELD_OFFENSIVE_LINKED_COUNT = "J", //$NON-NLS-1$
		FIELD_DEFENSIVE_LINKED_COUNT = "K", //$NON-NLS-1$
		FIELD_LINE_OF_SIGHT = "L", //$NON-NLS-1$
		FIELD_DELUDE = "M", //$NON-NLS-1$
		FIELD_SKIRMISH_ABILITIES = "N", //$NON-NLS-1$
		FIELD_BATTLE_ABILITIES = "O", //$NON-NLS-1$
		FIELD_IMMOBILIZED = "P", //$NON-NLS-1$
		FIELD_JUMP_TARGET = "Q", //$NON-NLS-1$
		FIELD_VERSION = "R", //$NON-NLS-1$
		FIELD_MIGRATION = "S", //$NON-NLS-1$
	    FIELD_XP_PLAYER = "T", //$NON-NLS-1$
	    FIELD_AWAY = "U", //$NON-NLS-1$
	    FIELD_CONNECTED = "V"; //$NON-NLS-1$
	
	public final static String CLASS_NAME = "FleetData"; //$NON-NLS-1$
	
	public final static int[] TRAINING_COST = {
		0,
		4000, // 2
		16000, // 3
		65000, // 4
		250000, // 5
		1000000, // 6
		3000000, // 7
		7500000, // 8
		1500000, // 9
		22000000, // 10
		30000000, // 11
		39000000, // 12
		46000000, // 13
		50000000, // 14
		52000000, // 15
	};
	
	private final static int[] XP_LEVELS = {
		0, 30, 64, 100, 150, 200, 260, 330, 410, 500, 605, 730, 870, 1030, 1210
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	protected FleetData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_ID];
	}-*/;

	public native final int getX() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_X];
	}-*/;
	
	public native final int getY() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_Y];
	}-*/;
	
	public native final String getName() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_NAME];
	}-*/;

	public native final int getMovement() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_MOVEMENT];
	}-*/;

	public native final int getMovementReloadRemainingTime() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_MOVEMENT_RELOAD];
	}-*/;

	public native final int getMovementMax() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_MOVEMENT_MAX];
	}-*/;

	public native final int getLastMove() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_LAST_MOVE];
	}-*/;

	public native final boolean isAi() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_AI];
	}-*/;
	
	public native final boolean isImmobilized() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_IMMOBILIZED];
	}-*/;
	
	public native final int getLevel() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_LEVEL];
	}-*/;

	public native final String getOwner() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_OWNER];
	}-*/;
	
	public native final String getAlly() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_ALLY];
	}-*/;

	public final boolean hasAlly() {
		return getAlly().length() > 0;
	}

	public native final String getAllyTag() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_ALLY_TAG];
	}-*/;

	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public native final String getTreaty() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_TREATY];
	}-*/;

	public native final int getStartJumpRemainingTime() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_START_JUMP];
	}-*/;
	
	public native final int getEndJumpRemainingTime() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_END_JUMP];
	}-*/;
	
	public native final int getSkin() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SKIN];
	}-*/;

	public native final boolean isPirate() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_PIRATE];
	}-*/;
	
	public native final boolean isCapturing() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_CAPTURE];
	}-*/;
	
	public native final int getColonizationRemainingTime() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_COLONIZING];
	}-*/;
	
	public native final int getJumpReloadRemainingTime() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_CAN_JUMP];
	}-*/;
	
	public native final boolean hasSlots() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SLOTS] !== false;
	}-*/;
	
	public native final int getSlotsCount() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SLOTS].length;
	}-*/;
	
	public native final SlotInfoData getSlotAt(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SLOTS][index];
	}-*/;

	public native final boolean hasShips() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SHIPS] !== false;
	}-*/;
	
	public native final int getShipsCount() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SHIPS].length;
	}-*/;
	
	public native final ShipInfoData getShipAt(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SHIPS][index];
	}-*/;

	public native final boolean hasClasses() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_CLASSES] !== false;
	}-*/;
	
	public native final int getClassesCount() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_CLASSES].length;
	}-*/;
	
	public native final int getClassAt(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_CLASSES][index];
	}-*/;
	
	public native final int getOffensiveLinkedFleetId() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_OFFENSIVE_LINK];
	}-*/;

	public native final int getDefensiveLinkedFleetId() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_DEFENSIVE_LINK];
	}-*/;
	
	public native final double getOffensiveLinkedCount(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_OFFENSIVE_LINKED_COUNT][index];
	}-*/;
	
	public native final double getDefensiveLinkedCount(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_DEFENSIVE_LINKED_COUNT][index];
	}-*/;
	
	public native final int getSkillsCount() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SKILLS].length;
	}-*/;
	
	public native final SkillData getSkillAt(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SKILLS][index];
	}-*/;
	
	public native final int getXp() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_XP];
	}-*/;

	public native final boolean isReserved() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_RESERVED];
	}-*/;

	public native final String getNpcAction() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_NPC_ACTION];
	}-*/;

	public native final int getPowerLevel() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_POWER_LEVEL];
	}-*/;
	
	public final int getFleetLevel() {
		return getFleetLevel(getXp());
	}

	public native final boolean isScheduledMove() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SCHEDULED_MOVE];
	}-*/;

	public native final boolean isStealth() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_STEALTH];
	}-*/;

	public native final boolean isDelude() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_DELUDE];
	}-*/;

	public native final int getLineOfSight() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_LINE_OF_SIGHT];
	}-*/;
	
	public native final int getSkirmishAbilitiesCount() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SKIRMISH_ABILITIES].length;
	}-*/;
	
	public native final int getSkirmishAbilityAt(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_SKIRMISH_ABILITIES][index];
	}-*/;
	
	public native final int getBattleAbilitiesCount() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_BATTLE_ABILITIES].length;
	}-*/;
	
	public native final int getBattleAbilityAt(int index) /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_BATTLE_ABILITIES][index];
	}-*/;
	
	public native final String getJumpTarget() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_JUMP_TARGET];
	}-*/;
	
	public native final double getVersion() /*-{
		return this[@fr.fg.client.data.FleetData::FIELD_VERSION];
	}-*/;
	
	public native final boolean isMigrating() /*-{
	return this[@fr.fg.client.data.FleetData::FIELD_MIGRATION];
	}-*/;
	
	public final static double getXpFactor(int attackerPower, int defenderPower) {
		if (attackerPower == defenderPower) {
			return 1;
		} else if (attackerPower < defenderPower) {
			double coef = Math.round(100 *
					((getPowerLevel(defenderPower + 1) - 1) /
							(double) (getPowerLevel(attackerPower + 1) - 1))) / 100.;
			
			coef -= 1;
			coef /= 2;
			coef += 1;
			
			return Math.min(1.5, coef);
		} else {
			return Math.max(0,
				Math.round(100 * ((getPowerLevel(defenderPower + 1) - 1) /
				(double) (getPowerLevel(attackerPower + 1) - 1))) / 100. -
				0.1 * (attackerPower - defenderPower));
		}
	}
	
	public final static int getFleetLevelXp(int level) {
		return XP_LEVELS[level - 1];
	}
	
	public static int getPowerLevel(int level) {
		if (level <= 1) {
			return 0;
		} else {
			// Voir materials/simulation development.xlsx
			double value = 500;
			double prod = 25;
			
			for (int i = 0; i < level - 2; i++) {
				double prodBefore = prod;
				prod = prod * (1.02 + .53 * Math.pow(.95, i + 1));
				
				// Coefficient fonction de la production
				double coef1 = prod / prodBefore;
				
				// Coefficient fonction du nombre de flottes à détruire pour
				// atteindre le niveau suivant
				double coef2 = (3 + 2 * (i + 2)) / (double) (3 + 2 * (i + 1));
				value = value * coef1 / coef2;
			}
			return (int) Math.floor(value / 10) * 10 + 1;
		}
	}
	
	public final static int getFleetLevel(double xp) {
		int i;
		for (i = 0; i < XP_LEVELS.length; i++)
			if (xp < XP_LEVELS[i])
				return i;
		return 15;
	}
	
	 public native final boolean isConnected() /*-{
     return this[@fr.fg.client.data.FleetData::FIELD_CONNECTED] !== undefined ?
             this[@fr.fg.client.data.FleetData::FIELD_CONNECTED] :
             (this[@fr.fg.client.data.FleetData::FIELD_TREATY] ==
                     @fr.fg.client.data.TreatyData::PLAYER ? true : false);
}-*/;

public native final boolean isAway() /*-{
     return this[@fr.fg.client.data.FleetData::FIELD_AWAY] !== undefined ?
             this[@fr.fg.client.data.FleetData::FIELD_AWAY] : false;
}-*/;
	
	public final boolean isAllyFleet() {
		return this.getTreaty().equals(ALLY);
	}
	
	public final boolean isAlliedFleet() {
		return (this.getTreaty().equals(ALLIED) || this.getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL));
	}
	
	public final boolean isEnemyFleet() {
		return this.getTreaty().equals(ENEMY);
	}
	
	public final boolean isPirateFleet() {
		return this.getTreaty().equals(PIRATE);
	}
	
	public final boolean isPlayerFleet() {
		return this.getTreaty().equals(PLAYER);
	}
	
	public final boolean isNeutralFleet() {
		return this.getTreaty().equals(NEUTRAL);
	}
	
	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //



	
}
