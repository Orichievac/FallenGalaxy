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

public class PlayerFleetData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_TAG = "b", //$NON-NLS-1$
		FIELD_AREA = "c", //$NON-NLS-1$
		FIELD_LAST_MOVE = "d", //$NON-NLS-1$
		FIELD_MOVEMENT_RELOAD = "e", //$NON-NLS-1$
		FIELD_START_JUMP = "f", //$NON-NLS-1$
		FIELD_END_JUMP = "g", //$NON-NLS-1$
		FIELD_CAN_JUMP = "h", //$NON-NLS-1$
		FIELD_CAPTURE = "i", //$NON-NLS-1$
		FIELD_COLONIZING = "j", //$NON-NLS-1$
		FIELD_MOVEMENT = "k", //$NON-NLS-1$
		FIELD_MOVEMENT_MAX = "l", //$NON-NLS-1$
		FIELD_NAME = "n", //$NON-NLS-1$
		FIELD_SLOTS = "o", //$NON-NLS-1$
		FIELD_SKILLS = "p", //$NON-NLS-1$
		FIELD_SYSTEM = "q", //$NON-NLS-1$
		FIELD_SYSTEM_TREATY = "r", //$NON-NLS-1$
		FIELD_ITEMS = "s", //$NON-NLS-1$
		FIELD_XP = "t", //$NON-NLS-1$
		FIELD_X = "u", //$NON-NLS-1$
		FIELD_Y = "v", //$NON-NLS-1$
		FIELD_SKILL_POINTS = "w", //$NON-NLS-1$
		FIELD_SKIRMISH_ACTION_SLOTS = "x", //$NON-NLS-1$
		FIELD_SKIRMISH_ACTION_ABILITIES = "y", //$NON-NLS-1$
		FIELD_BATTLE_ACTION_SLOTS = "z", //$NON-NLS-1$
		FIELD_BATTLE_ACTION_ABILITIES = "A", //$NON-NLS-1$
		FIELD_POWER_LEVEL = "B", //$NON-NLS-1$
		FIELD_SCHEDULED_MOVE = "C", //$NON-NLS-1$
		FIELD_OFFENSIVE_LINK = "F", //$NON-NLS-1$
		FIELD_DEFENSIVE_LINK = "G", //$NON-NLS-1$
		FIELD_OFFENSIVE_LINKED_COUNT = "H", //$NON-NLS-1$
		FIELD_DEFENSIVE_LINKED_COUNT = "I", //$NON-NLS-1$
		FIELD_DELUDE = "J", //$NON-NLS-1$
		FIELD_STEALTH = "K", //$NON-NLS-1$
		FIELD_SHORTCUT = "L", //$NON-NLS-1$
		FIELD_JUMP_TARGET = "M", //$NON-NLS-1$
		FIELD_VERSION = "N", //$NON-NLS-1$
		FIELD_MIGRATION = "O"; //$NON-NLS-1$
		
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected PlayerFleetData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public native final int getId() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_ID];
	}-*/;

	public native final int getX() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_X];
	}-*/;

	public native final int getY() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_Y];
	}-*/;

	public native final int getTag() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_TAG];
	}-*/;
	
	public native final PlayerAreaData getArea() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_AREA];
	}-*/;
	
	public native final int getLastMove() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_LAST_MOVE];
	}-*/;
	
	public native final int getMovementReloadRemainingTime() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_MOVEMENT_RELOAD];
	}-*/;

	public native final int getStartJumpRemainingTime() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_START_JUMP];
	}-*/;
	
	public native final int getEndJumpRemainingTime() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_END_JUMP];
	}-*/;
	
	public native final int getJumpReloadRemainingTime() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_CAN_JUMP];
	}-*/;

	public native final boolean isCapturing() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_CAPTURE];
	}-*/;
	
	public native final int getColonizationRemainingTime() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_COLONIZING];
	}-*/;
	
	public native final int getMovement() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_MOVEMENT];
	}-*/;

	public native final int getMovementMax() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_MOVEMENT_MAX];
	}-*/;
	
	public native final String getName() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_NAME];
	}-*/;

	public native final int getSlotsCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SLOTS].length;
	}-*/;
	
	public native final SlotInfoData getSlotAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SLOTS][index];
	}-*/;

	public native final int getSkillPoints() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKILL_POINTS];
	}-*/;
	
	public native final int getSkillsCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKILLS].length;
	}-*/;
	
	public final int getSkillLevel(int type) {
		for (int i = 0; i < getSkillsCount(); i++)
			if (getSkillAt(i).getType() == type)
				return getSkillAt(i).getLevel();
		return -1;
	}
	
	public native final SkillData getSkillAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKILLS][index];
	}-*/;

	public native final boolean isOverSystem() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SYSTEM] != 0;
	}-*/;
	
	public native final int getSystem() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SYSTEM];
	}-*/;
	
	public native final String getSystemTreaty() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SYSTEM_TREATY];
	}-*/;
	
	public native final int getItemsCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_ITEMS].length;
	}-*/;

	public native final ItemInfoData getItemAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_ITEMS][index];
	}-*/;
	
	public native final int getXp() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_XP];
	}-*/;

	public native final int getSkirmishActionSlotsCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKIRMISH_ACTION_SLOTS].length;
	}-*/;

	public native final int getSkirmishActionSlotAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKIRMISH_ACTION_SLOTS][index];
	}-*/;

	public native final int getSkirmishActionAbilitiesCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKIRMISH_ACTION_ABILITIES].length;
	}-*/;

	public native final int getSkirmishActionAbilityAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SKIRMISH_ACTION_ABILITIES][index];
	}-*/;

	public native final int getBattleActionSlotsCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_BATTLE_ACTION_SLOTS].length;
	}-*/;

	public native final int getBattleActionSlotAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_BATTLE_ACTION_SLOTS][index];
	}-*/;

	public native final int getBattleActionAbilitiesCount() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_BATTLE_ACTION_ABILITIES].length;
	}-*/;

	public native final int getBattleActionAbilityAt(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_BATTLE_ACTION_ABILITIES][index];
	}-*/;

	public native final int getPowerLevel() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_POWER_LEVEL];
	}-*/;
	
	public native final int getOffensiveLinkedFleetId() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_OFFENSIVE_LINK];
	}-*/;

	public native final int getDefensiveLinkedFleetId() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_DEFENSIVE_LINK];
	}-*/;

	public native final double getOffensiveLinkedCount(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_OFFENSIVE_LINKED_COUNT][index];
	}-*/;

	public native final double getDefensiveLinkedCount(int index) /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_DEFENSIVE_LINKED_COUNT][index];
	}-*/;

	public final int getFleetLevel() {
		return FleetData.getFleetLevel(getXp());
	}
	
	public native final boolean isScheduledMove() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SCHEDULED_MOVE];
	}-*/;

	public native final boolean isMigrating() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_MIGRATION];
	}-*/;
	
	
	public native final boolean isDelude() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_DELUDE];
	}-*/;

	public native final boolean isStealth() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_STEALTH];
	}-*/;
	
	public native final int getShortcut() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_SHORTCUT];
	}-*/;

	public native final String getJumpTarget() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_JUMP_TARGET];
	}-*/;
	
	public native final double getVersion() /*-{
		return this[@fr.fg.client.data.PlayerFleetData::FIELD_VERSION];
	}-*/;
	
	public final long[] getResources() {
		long[] resources = new long[4];
		for (int i = 0; i < getItemsCount(); i++) {
			ItemInfoData item = getItemAt(i);
			if (item.getType() == ItemInfoData.TYPE_RESOURCE)
				resources[item.getId()] = (long) item.getCount();
		}
		return resources;
	}
	
	public final double getTotalWeight() {
		double sum = 0;
		for (int i = 0; i < getItemsCount(); i++)
			sum += getItemAt(i).getWeight() * getItemAt(i).getCount();
		return sum;
	}
	
	public final long getPayload() {
		long payload = 0;
		for (int i = 0; i < getSlotsCount(); i++)
			if (getSlotAt(i).getId() != 0)
				payload += getSlotAt(i).getCount() *
					ShipData.SHIPS[getSlotAt(i).getId()].getPayload();
		return payload;
	}
	
	public final int getPower() {
		int power = 0;
		for (int i = 0; i < getSlotsCount(); i++)
			if (getSlotAt(i).getId() != 0)
				power += ShipData.CLASSES_POWER[ShipData.SHIPS[getSlotAt(
					i).getId()].getShipClass()] * getSlotAt(i).getCount();
		return power;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
