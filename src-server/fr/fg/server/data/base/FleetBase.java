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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class FleetBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		CURRENT_ACTION_NONE = "none",
		CURRENT_ACTION_JUMP = "jump",
		CURRENT_ACTION_MINE = "mine",
		CURRENT_ACTION_BUILD_STRUCTURE = "build_structure",
		CURRENT_ACTION_BUILD_WARD = "build_ward",
		CURRENT_ACTION_ATTACK_STRUCTURE = "attack_structure",
		CURRENT_ACTION_ATTACK_WARD = "attack_ward",
		CURRENT_ACTION_COLONIZE = "colonize",
		CURRENT_ACTION_PICK_UP = "pick_up",
		CURRENT_ACTION_DEFUSE = "defuse",
		CURRENT_ACTION_BATTLE = "battle",
		CURRENT_ACTION_DISMOUNT_STRUCTURE = "dismount_structure",
		CURRENT_ACTION_MOUNT_STRUCTURE = "mount_structure",
		CURRENT_ACTION_REPAIR_STRUCTURE = "repair_structure",
		CURRENT_ACTION_MIGRATE = "migrate";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String name;
	private int tag;
	private int shortcut;
	private String npcType;
	private String currentAction;
	private boolean stealth;
	private int x;
	private int y;
	private boolean scheduledMove;
	private int scheduledX;
	private int scheduledY;
	private boolean unstuckable;
	private int stuckCount;
	private int movementLeft;
	private long lastMove;
	private long movementReload;
	private int hyperspaceIdArea;
	private int hyperspaceX;
	private int hyperspaceY;
	private long xp;
	private int encodedSkill0;
	private int encodedSkill1;
	private int encodedSkill2;
	private int encodedSkillUltimate;
	private long skill0Reload;
	private long skill1Reload;
	private long skill2Reload;
	private long skillUltimateReload;
	private long skill0LastUse;
	private long skill1LastUse;
	private long skill2LastUse;
	private long skillUltimateLastUse;
	private String encodedSkirmishSlots;
	private String encodedSkirmishAbilities;
	private String encodedBattleSlots;
	private String encodedBattleAbilities;
	private boolean tacticsDefined;
	private long resource0;
	private long resource1;
	private long resource2;
	private long resource3;
	private int slot0Id;
	private long slot0Count;
	private boolean slot0Front;
	private int slot1Id;
	private long slot1Count;
	private boolean slot1Front;
	private int slot2Id;
	private long slot2Count;
	private boolean slot2Front;
	private int slot3Id;
	private long slot3Count;
	private boolean slot3Front;
	private int slot4Id;
	private long slot4Count;
	private boolean slot4Front;
	private int idOwner;
	private int idArea;
	private long idContract;
	private int idSystemMigrate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
			this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (name == null)
			throw new IllegalArgumentException("Invalid value: '" +
				name + "' (must not be null).");
		else
			this.name = name;
	}
	
	public int getTag() {
		return tag;
	}
	
	public void setTag(int tag) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (tag < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				tag + "' (must be >= 0).");
		else
			this.tag = tag;
	}
	
	public int getShortcut() {
		return shortcut;
	}
	
	public void setShortcut(int shortcut) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.shortcut = shortcut;
	}
	
	public String getNpcType() {
		return npcType;
	}
	
	public void setNpcType(String npcType) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (npcType == null)
			throw new IllegalArgumentException("Invalid value: '" +
				npcType + "' (must not be null).");
		else
			this.npcType = npcType;
	}
	
	public String getCurrentAction() {
		return currentAction;
	}
	
	public void setCurrentAction(String currentAction) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (currentAction == null)
			throw new IllegalArgumentException("Invalid value: '" +
				currentAction + "' (must not be null).");
		else if (currentAction.equals(CURRENT_ACTION_NONE))
			this.currentAction = CURRENT_ACTION_NONE;
		else if (currentAction.equals(CURRENT_ACTION_JUMP))
			this.currentAction = CURRENT_ACTION_JUMP;
		else if (currentAction.equals(CURRENT_ACTION_MINE))
			this.currentAction = CURRENT_ACTION_MINE;
		else if (currentAction.equals(CURRENT_ACTION_BUILD_STRUCTURE))
			this.currentAction = CURRENT_ACTION_BUILD_STRUCTURE;
		else if (currentAction.equals(CURRENT_ACTION_BUILD_WARD))
			this.currentAction = CURRENT_ACTION_BUILD_WARD;
		else if (currentAction.equals(CURRENT_ACTION_ATTACK_STRUCTURE))
			this.currentAction = CURRENT_ACTION_ATTACK_STRUCTURE;
		else if (currentAction.equals(CURRENT_ACTION_ATTACK_WARD))
			this.currentAction = CURRENT_ACTION_ATTACK_WARD;
		else if (currentAction.equals(CURRENT_ACTION_COLONIZE))
			this.currentAction = CURRENT_ACTION_COLONIZE;
		else if (currentAction.equals(CURRENT_ACTION_PICK_UP))
			this.currentAction = CURRENT_ACTION_PICK_UP;
		else if (currentAction.equals(CURRENT_ACTION_DEFUSE))
			this.currentAction = CURRENT_ACTION_DEFUSE;
		else if (currentAction.equals(CURRENT_ACTION_BATTLE))
			this.currentAction = CURRENT_ACTION_BATTLE;
		else if (currentAction.equals(CURRENT_ACTION_DISMOUNT_STRUCTURE))
			this.currentAction = CURRENT_ACTION_DISMOUNT_STRUCTURE;
		else if (currentAction.equals(CURRENT_ACTION_MOUNT_STRUCTURE))
			this.currentAction = CURRENT_ACTION_MOUNT_STRUCTURE;
		else if (currentAction.equals(CURRENT_ACTION_REPAIR_STRUCTURE))
			this.currentAction = CURRENT_ACTION_REPAIR_STRUCTURE;
		else if (currentAction.equals(CURRENT_ACTION_MIGRATE))
			this.currentAction = CURRENT_ACTION_MIGRATE;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + currentAction + "'.");
	}
	
	public boolean isStealth() {
		return stealth;
	}
	
	public void setStealth(boolean stealth) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.stealth = stealth;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (x < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				x + "' (must be >= 0).");
		else
			this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (y < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				y + "' (must be >= 0).");
		else
			this.y = y;
	}
	
	public boolean isScheduledMove() {
		return scheduledMove;
	}
	
	public void setScheduledMove(boolean scheduledMove) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.scheduledMove = scheduledMove;
	}
	
	public int getScheduledX() {
		return scheduledX;
	}
	
	public void setScheduledX(int scheduledX) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.scheduledX = scheduledX;
	}
	
	public int getScheduledY() {
		return scheduledY;
	}
	
	public void setScheduledY(int scheduledY) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.scheduledY = scheduledY;
	}
	
	public boolean isUnstuckable() {
		return unstuckable;
	}
	
	public void setUnstuckable(boolean unstuckable) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.unstuckable = unstuckable;
	}
	
	public int getStuckCount() {
		return stuckCount;
	}
	
	public void setStuckCount(int stuckCount) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (stuckCount < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				stuckCount + "' (must be >= 0).");
		else
			this.stuckCount = stuckCount;
	}
	
	public int getMovementLeft() {
		return movementLeft;
	}
	
	public void setMovementLeft(int movementLeft) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (movementLeft < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				movementLeft + "' (must be >= 0).");
		else
			this.movementLeft = movementLeft;
	}
	
	public long getLastMove() {
		return lastMove;
	}
	
	public void setLastMove(long lastMove) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastMove < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastMove + "' (must be >= 0).");
		else
			this.lastMove = lastMove;
	}
	
	public long getMovementReload() {
		return movementReload;
	}
	
	public void setMovementReload(long movementReload) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (movementReload < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				movementReload + "' (must be >= 0).");
		else
			this.movementReload = movementReload;
	}
	
	public int getHyperspaceIdArea() {
		return hyperspaceIdArea;
	}
	
	public void setHyperspaceIdArea(int hyperspaceIdArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (hyperspaceIdArea < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				hyperspaceIdArea + "' (must be >= 0).");
		else
			this.hyperspaceIdArea = hyperspaceIdArea;
	}
	
	public int getHyperspaceX() {
		return hyperspaceX;
	}
	
	public void setHyperspaceX(int hyperspaceX) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (hyperspaceX < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				hyperspaceX + "' (must be >= 0).");
		else
			this.hyperspaceX = hyperspaceX;
	}
	
	public int getHyperspaceY() {
		return hyperspaceY;
	}
	
	public void setHyperspaceY(int hyperspaceY) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (hyperspaceY < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				hyperspaceY + "' (must be >= 0).");
		else
			this.hyperspaceY = hyperspaceY;
	}
	
	public long getXp() {
		return xp;
	}
	
	public void setXp(long xp) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (xp < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				xp + "' (must be >= 0).");
		else
			this.xp = xp;
	}
	
	public int getEncodedSkill0() {
		return encodedSkill0;
	}
	
	public void setEncodedSkill0(int encodedSkill0) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkill0 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkill0 + "' (must be >= 0).");
		else
			this.encodedSkill0 = encodedSkill0;
	}
	
	public int getEncodedSkill1() {
		return encodedSkill1;
	}
	
	public void setEncodedSkill1(int encodedSkill1) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkill1 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkill1 + "' (must be >= 0).");
		else
			this.encodedSkill1 = encodedSkill1;
	}
	
	public int getEncodedSkill2() {
		return encodedSkill2;
	}
	
	public void setEncodedSkill2(int encodedSkill2) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkill2 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkill2 + "' (must be >= 0).");
		else
			this.encodedSkill2 = encodedSkill2;
	}
	
	public int getEncodedSkillUltimate() {
		return encodedSkillUltimate;
	}
	
	public void setEncodedSkillUltimate(int encodedSkillUltimate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkillUltimate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkillUltimate + "' (must be >= 0).");
		else
			this.encodedSkillUltimate = encodedSkillUltimate;
	}
	
	public long getSkill0Reload() {
		return skill0Reload;
	}
	
	public void setSkill0Reload(long skill0Reload) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skill0Reload < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skill0Reload + "' (must be >= 0).");
		else
			this.skill0Reload = skill0Reload;
	}
	
	public long getSkill1Reload() {
		return skill1Reload;
	}
	
	public void setSkill1Reload(long skill1Reload) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skill1Reload < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skill1Reload + "' (must be >= 0).");
		else
			this.skill1Reload = skill1Reload;
	}
	
	public long getSkill2Reload() {
		return skill2Reload;
	}
	
	public void setSkill2Reload(long skill2Reload) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skill2Reload < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skill2Reload + "' (must be >= 0).");
		else
			this.skill2Reload = skill2Reload;
	}
	
	public long getSkillUltimateReload() {
		return skillUltimateReload;
	}
	
	public void setSkillUltimateReload(long skillUltimateReload) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skillUltimateReload < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skillUltimateReload + "' (must be >= 0).");
		else
			this.skillUltimateReload = skillUltimateReload;
	}
	
	public long getSkill0LastUse() {
		return skill0LastUse;
	}
	
	public void setSkill0LastUse(long skill0LastUse) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skill0LastUse < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skill0LastUse + "' (must be >= 0).");
		else
			this.skill0LastUse = skill0LastUse;
	}
	
	public long getSkill1LastUse() {
		return skill1LastUse;
	}
	
	public void setSkill1LastUse(long skill1LastUse) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skill1LastUse < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skill1LastUse + "' (must be >= 0).");
		else
			this.skill1LastUse = skill1LastUse;
	}
	
	public long getSkill2LastUse() {
		return skill2LastUse;
	}
	
	public void setSkill2LastUse(long skill2LastUse) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skill2LastUse < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skill2LastUse + "' (must be >= 0).");
		else
			this.skill2LastUse = skill2LastUse;
	}
	
	public long getSkillUltimateLastUse() {
		return skillUltimateLastUse;
	}
	
	public void setSkillUltimateLastUse(long skillUltimateLastUse) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (skillUltimateLastUse < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				skillUltimateLastUse + "' (must be >= 0).");
		else
			this.skillUltimateLastUse = skillUltimateLastUse;
	}
	
	public String getEncodedSkirmishSlots() {
		return encodedSkirmishSlots;
	}
	
	public void setEncodedSkirmishSlots(String encodedSkirmishSlots) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkirmishSlots == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkirmishSlots + "' (must not be null).");
		else
			this.encodedSkirmishSlots = encodedSkirmishSlots;
	}
	
	public String getEncodedSkirmishAbilities() {
		return encodedSkirmishAbilities;
	}
	
	public void setEncodedSkirmishAbilities(String encodedSkirmishAbilities) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedSkirmishAbilities == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedSkirmishAbilities + "' (must not be null).");
		else
			this.encodedSkirmishAbilities = encodedSkirmishAbilities;
	}
	
	public String getEncodedBattleSlots() {
		return encodedBattleSlots;
	}
	
	public void setEncodedBattleSlots(String encodedBattleSlots) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedBattleSlots == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedBattleSlots + "' (must not be null).");
		else
			this.encodedBattleSlots = encodedBattleSlots;
	}
	
	public String getEncodedBattleAbilities() {
		return encodedBattleAbilities;
	}
	
	public void setEncodedBattleAbilities(String encodedBattleAbilities) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (encodedBattleAbilities == null)
			throw new IllegalArgumentException("Invalid value: '" +
				encodedBattleAbilities + "' (must not be null).");
		else
			this.encodedBattleAbilities = encodedBattleAbilities;
	}
	
	public boolean isTacticsDefined() {
		return tacticsDefined;
	}
	
	public void setTacticsDefined(boolean tacticsDefined) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.tacticsDefined = tacticsDefined;
	}
	
	public long getResource0() {
		return resource0;
	}
	
	public void setResource0(long resource0) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource0 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource0 + "' (must be >= 0).");
		else
			this.resource0 = resource0;
	}
	
	public long getResource1() {
		return resource1;
	}
	
	public void setResource1(long resource1) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource1 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource1 + "' (must be >= 0).");
		else
			this.resource1 = resource1;
	}
	
	public long getResource2() {
		return resource2;
	}
	
	public void setResource2(long resource2) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource2 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource2 + "' (must be >= 0).");
		else
			this.resource2 = resource2;
	}
	
	public long getResource3() {
		return resource3;
	}
	
	public void setResource3(long resource3) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (resource3 < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				resource3 + "' (must be >= 0).");
		else
			this.resource3 = resource3;
	}
	
	public int getSlot0Id() {
		return slot0Id;
	}
	
	public void setSlot0Id(int slot0Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot0Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot0Id + "' (must be >= 0).");
		else
			this.slot0Id = slot0Id;
	}
	
	public long getSlot0Count() {
		return slot0Count;
	}
	
	public void setSlot0Count(long slot0Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot0Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot0Count + "' (must be >= 0).");
		else
			this.slot0Count = slot0Count;
	}
	
	public boolean isSlot0Front() {
		return slot0Front;
	}
	
	public void setSlot0Front(boolean slot0Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot0Front = slot0Front;
	}
	
	public int getSlot1Id() {
		return slot1Id;
	}
	
	public void setSlot1Id(int slot1Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot1Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot1Id + "' (must be >= 0).");
		else
			this.slot1Id = slot1Id;
	}
	
	public long getSlot1Count() {
		return slot1Count;
	}
	
	public void setSlot1Count(long slot1Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot1Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot1Count + "' (must be >= 0).");
		else
			this.slot1Count = slot1Count;
	}
	
	public boolean isSlot1Front() {
		return slot1Front;
	}
	
	public void setSlot1Front(boolean slot1Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot1Front = slot1Front;
	}
	
	public int getSlot2Id() {
		return slot2Id;
	}
	
	public void setSlot2Id(int slot2Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot2Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot2Id + "' (must be >= 0).");
		else
			this.slot2Id = slot2Id;
	}
	
	public long getSlot2Count() {
		return slot2Count;
	}
	
	public void setSlot2Count(long slot2Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot2Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot2Count + "' (must be >= 0).");
		else
			this.slot2Count = slot2Count;
	}
	
	public boolean isSlot2Front() {
		return slot2Front;
	}
	
	public void setSlot2Front(boolean slot2Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot2Front = slot2Front;
	}
	
	public int getSlot3Id() {
		return slot3Id;
	}
	
	public void setSlot3Id(int slot3Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot3Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot3Id + "' (must be >= 0).");
		else
			this.slot3Id = slot3Id;
	}
	
	public long getSlot3Count() {
		return slot3Count;
	}
	
	public void setSlot3Count(long slot3Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot3Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot3Count + "' (must be >= 0).");
		else
			this.slot3Count = slot3Count;
	}
	
	public boolean isSlot3Front() {
		return slot3Front;
	}
	
	public void setSlot3Front(boolean slot3Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot3Front = slot3Front;
	}
	
	public int getSlot4Id() {
		return slot4Id;
	}
	
	public void setSlot4Id(int slot4Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot4Id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot4Id + "' (must be >= 0).");
		else
			this.slot4Id = slot4Id;
	}
	
	public long getSlot4Count() {
		return slot4Count;
	}
	
	public void setSlot4Count(long slot4Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (slot4Count < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				slot4Count + "' (must be >= 0).");
		else
			this.slot4Count = slot4Count;
	}
	
	public boolean isSlot4Front() {
		return slot4Front;
	}
	
	public void setSlot4Front(boolean slot4Front) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.slot4Front = slot4Front;
	}
	
	public int getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(int idOwner) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idOwner < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idOwner + "' (must be >= 0).");
		else
			this.idOwner = idOwner;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idArea < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idArea + "' (must be >= 0).");
		else
			this.idArea = idArea;
	}
	
	public long getIdContract() {
		return idContract;
	}
	
	public void setIdContract(long idContract) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idContract < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idContract + "' (must be >= 0).");
		else
			this.idContract = idContract;
	}
	
	public int getIdSystemMigrate() {
		return idSystemMigrate;
	}
	
	public void setIdSystemMigrate(int idSystemMigrate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idSystemMigrate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idSystemMigrate + "' (must be >= 0).");
		else
			this.idSystemMigrate = idSystemMigrate;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
