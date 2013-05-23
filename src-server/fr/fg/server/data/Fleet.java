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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.fg.client.data.StructureModuleData;
import fr.fg.server.data.base.FleetBase;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.FleetLocationUpdateEvent;
import fr.fg.server.events.impl.FleetMovementReloadUpdateEvent;
import fr.fg.server.events.impl.JumpFinishedEvent;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Fleet extends FleetBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		NPC_ACTION_NONE = "none",
		NPC_ACTION_TALK = "talk",
		NPC_ACTION_MISSION = "mission";
	
	public final static int
		FIELD_X = 1 << 0,
		FIELD_Y = 1 << 1,
		FIELD_MOVEMENT_RELOAD = 1 << 2;
	
	// Mouvement de base des flottes, en cases
	public final static int BASE_MOVEMENT = 40;
	
	// Mouvement maximal des flottes, en fonction des classes de vaisseaux
	// contenues dans la flotte
	public final static int[] MOVEMENT_MAX_BY_CLASS = {
		0,
		BASE_MOVEMENT + 25,
		BASE_MOVEMENT + 20,
		BASE_MOVEMENT + 15,
		BASE_MOVEMENT + 10,
		BASE_MOVEMENT + 5,
		BASE_MOVEMENT + 0,
		BASE_MOVEMENT + 15,
	};
	
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
	
	// XP nécessaire pour les niveaux des flottes
	private final static int[] XP_LEVELS = {
		0, 30, 64, 100, 150, 200, 260, 330, 410, 500, 605, 730, 870, 1030, 1210
	};
	
	// Indication sur le nombre de vaisseaux par tranches
	// (de 0 à 10, de 100 à 100...)
	private final static int[] QUANTITY_QUALIFIERS = {
		0, 10, 100, 500, 2500, 10000, 50000, 250000, 1500000
	};
	
	// Seuils de puissance précalculés
	private final static int[] POWER_AT_LEVEL =
		new int[GameConstants.PLAYER_LEVEL_LIMIT + 2];
	
	private final static int[] LEVEL_AT_POWER;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	static {
		for (int level = 0; level < POWER_AT_LEVEL.length; level++) {
			if (level <= 1) {
				POWER_AT_LEVEL[level] = 0;
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
				POWER_AT_LEVEL[level] = (int) Math.floor(value / 10) * 10 + 1;
			}
		}
		
		LEVEL_AT_POWER = new int[POWER_AT_LEVEL[POWER_AT_LEVEL.length - 1]];
		
		for (int power = 0; power < LEVEL_AT_POWER.length; power++) {
			int level = 1;
			while (true) {
				int powerLevel = POWER_AT_LEVEL[Math.min(POWER_AT_LEVEL.length - 1, level + 1)];
				
				if (power < powerLevel) {
					LEVEL_AT_POWER[power] = level;
					break;
				}
				level++;
				
				if (level >= GameConstants.PLAYER_LEVEL_LIMIT) {
					LEVEL_AT_POWER[power] = GameConstants.PLAYER_LEVEL_LIMIT;
					break;
				}
			}
		}
	}
	
	private int[] skillLevels;
	
	private int availableSkillPoints;
	
	private int dirtyFlag;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Fleet() {
		// Nécessaire pour la construction par réflection
		
		skillLevels = null;
		availableSkillPoints = -1;
	}

	public Fleet(String name, int x, int y, int idOwner, int idArea) {
		setName(name);
		setTag(0);
		setShortcut(-1);
		setNpcType("");
		setCurrentAction(CURRENT_ACTION_NONE);
		setX(x);
		setY(y);
		setScheduledMove(false);
		setScheduledX(0);
		setScheduledY(0);
		setUnstuckable(false);
		setStuckCount(0);
		setLastMove(0);
		setHyperspaceIdArea(0);
		setHyperspaceX(0);
		setHyperspaceY(0);
		setMovementReload(0);
		setXp(0);
		setEncodedSkill0(0);
		setEncodedSkill1(0);
		setEncodedSkill2(0);
		setEncodedSkillUltimate(0);
		setSkill0Reload(0);
		setSkill1Reload(0);
		setSkill2Reload(0);
		setSkillUltimateReload(0);
		setEncodedSkirmishSlots("99999");
		setEncodedSkirmishAbilities("99999");
		setEncodedBattleSlots("999999999999999");
		setEncodedBattleAbilities("999999999999999");
		setTacticsDefined(false);
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
		setSlot0Id(0);
		setSlot0Count(0);
		setSlot0Front(true);
		setSlot1Id(0);
		setSlot1Count(0);
		setSlot1Front(true);
		setSlot2Id(0);
		setSlot2Count(0);
		setSlot2Front(true);
		setSlot3Id(0);
		setSlot3Count(0);
		setSlot3Front(true);
		setSlot4Id(0);
		setSlot4Count(0);
		setSlot4Front(true);
		setIdOwner(idOwner);
		setIdArea(idArea);
		setIdContract(0);
		setMovementLeft(getMovementMax());
		updateSkillsCache();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public long getVersion() {
		long version = super.getVersion() * 17 + getItemContainer().getVersion();
		List<FleetLink> links = getLinks();
		synchronized (links) {
			for (FleetLink link : links)
				version = version * 17 + link.getVersion();
		}
		return version;
	}
	
	public void doAction(String action, long end) {
		setMovement(0, true);
		setCurrentAction(action);
		setMovementReload(end);
	}
	
	@Override
	public void setMovementReload(long movementReload) {
		super.setMovementReload(movementReload);
		if (getOriginalCopy() != null)
			this.dirtyFlag |= FIELD_MOVEMENT_RELOAD;
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		if (getOriginalCopy() != null)
			this.dirtyFlag |= FIELD_X;
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		if (getOriginalCopy() != null)
			this.dirtyFlag |= FIELD_Y;
	}
	
	@Override
	public void save() {
		boolean triggerLocationEvent = false;
		Point locationBefore = null;
		boolean triggerMovementReloadEvent = false;
		
		if ((this.dirtyFlag & FIELD_X) != 0 || (this.dirtyFlag & FIELD_Y) != 0) {
			triggerLocationEvent = true;
			Fleet original = (Fleet) getOriginalCopy();
			locationBefore = new Point(original.getCurrentX(), original.getCurrentY());
		}
		
		if ((this.dirtyFlag & FIELD_MOVEMENT_RELOAD) != 0) {
			triggerMovementReloadEvent = true;
		}
		
		super.save();
		
		if (getItemContainer() == null) {
		    ItemContainer itemContainer = new ItemContainer(
		    		ItemContainer.CONTAINER_FLEET, getId());
		    itemContainer.save();
		}
		
		this.dirtyFlag = 0;
		
		if (triggerMovementReloadEvent)
			GameEventsDispatcher.fireGameNotification(
				new FleetMovementReloadUpdateEvent(this));
		
		if (triggerLocationEvent) {
			GameEventsDispatcher.fireGameNotification(new FleetLocationUpdateEvent(
				this, locationBefore, new Point(getCurrentX(), getCurrentY())));
			checkLinks();
		}
	}
	
	public void setSkirmishActions(int[] slots, int[] abilities) {
		setEncodedSkirmishSlots(encodeActions(slots));
		setEncodedSkirmishAbilities(encodeActions(abilities));
	}
	
	public void setBattleActions(int[] slots, int[] abilities) {
		setEncodedBattleSlots(encodeActions(slots));
		setEncodedBattleAbilities(encodeActions(abilities));
	}
	
	public int[] getSkirmishActionSlots() {
		return decodeActions(getEncodedSkirmishSlots());
	}

	public int[] getSkirmishActionAbilities() {
		return decodeActions(getEncodedSkirmishAbilities());
	}

	public int[] getBattleActionSlots() {
		return decodeActions(getEncodedBattleSlots());
	}

	public int[] getBattleActionAbilities() {
		return decodeActions(getEncodedBattleAbilities());
	}
	
	// Indique si la flotte est en hyperespace
	public boolean isInHyperspace() {
		return getHyperspaceIdArea() != 0 && Utilities.now() < getMovementReload();
	}
	
	public boolean isStartingJump() {
		long now = Utilities.now();
		
		return isInHyperspace() && (now - getLastMove()) /
				(double) (getMovementReload() - getLastMove()) < .5;
	}

	public boolean isEndingJump() {
		long now = Utilities.now();
		
		return isInHyperspace() && (now - getLastMove()) /
				(double) (getMovementReload() - getLastMove()) >= .5;
	}
	
	// Renvoie le secteur dans lequel se trouve la flotte, en fonction du temps
	// passé à sauter en hyperspace
	public int getIdCurrentArea() {
		if (getHyperspaceIdArea() == 0 || isStartingJump())
			return getIdArea();
		else
			return getHyperspaceIdArea();
	}
	
	public int getCurrentX() {
		if (getHyperspaceIdArea() == 0 || isStartingJump())
			return getX();
		else
			return getHyperspaceX();
	}
	
	public int getCurrentY() {
		if (getHyperspaceIdArea() == 0 || isStartingJump())
			return getY();
		else
			return getHyperspaceY();
	}
	
	public long getStartJumpRemainingTime() {
		if (!isInHyperspace())
			return 0;
		
		if (isEndingJump())
			return 0;
		else
			return getMovementReload() - (getMovementReload() -
					getLastMove()) / 2 - Utilities.now();
	}
	
	public long getEndJumpRemainingTime() {
		if (!isInHyperspace())
			return 0;
		
		return getMovementReload() - Utilities.now();
	}
	
	// Indique le temps restant en hyperespace
	public long getMovementReloadRemainingTime() {
		if (getMovementReload() <= Utilities.now())
			return 0;
		return getMovementReload() - Utilities.now();
	}
	
	// Indique si la flotte peut effectuer un saut hyperspatial
	public boolean isJumpReloaded() {
		return true;
//		return getHyperspaceEnd() + GameConstants.HYPERSPACE_OUT +
//			GameConstants.HYPERSPACE_RELOAD < Utilities.now();
	}
	
	// Indique dans combien de temps la flotte pourra effectuer un saut
	// hyperspatial
	public long getJumpReloadRemainingTime() {
		return 0;
//		if (isJumpReloaded())
//			return 0;
//		return getHyperspaceEnd() + GameConstants.HYPERSPACE_OUT +
//			GameConstants.HYPERSPACE_RELOAD - Utilities.now();
	}
	
	public int getMovement() {
		if (isInHyperspace())
			return 0;
		
		long now = Utilities.now();
		int movementMax = getMovementMax();
		
		// Teste si le mouvement doit être rechargé
		if (now > getMovementReload())
			return movementMax;
		
		return getMovementLeft();
	}
	
	public void move(int x, int y) throws IllegalOperationException {
		if (!isEditable())
			throwDataUneditableException();
		
		if (x == getCurrentX() && y == getCurrentY())
			return;
		
		if (isInHyperspace())
			throw new IllegalOperationException("La flotte est en hyperespace.");
		
		setScheduledX(x);
		setScheduledY(y);
		setScheduledMove(true);
		updateScheduledMove();
	}
	
	public void updateScheduledMove() {
		int scheduledX = getScheduledX(), scheduledY = getScheduledY();
		int currentX = getCurrentX();
		int currentY = getCurrentY();
		
		int dx = scheduledX - currentX;
		int dy = scheduledY - currentY;
		int distance = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
		int movement = getMovement();
		
		if (movement <= 3)
			return;
		
		int tmpX, tmpY;
		if (movement >= distance) {
			tmpX = scheduledX;
			tmpY = scheduledY;
			
			setScheduledX(0);
			setScheduledY(0);
			setScheduledMove(false);
		} else {
			double angle = Math.atan2(dy, dx);
			tmpX = (int) Math.round(currentX + Math.cos(angle) * (movement - 3));
			tmpY = (int) Math.round(currentY + Math.sin(angle) * (movement - 3));
		}
		
		Area area = getArea();
		
		// Vérifie que la case est libre
		if (area.isFreeTile(tmpX, tmpY, Area.CHECK_FLEET_MOVEMENT, getOwner())) {
			// Déplace la flotte
			dx = tmpX - currentX;
			dy = tmpY - currentY;
			distance = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
			
			setLocation(tmpX, tmpY);
			setMovement(movement - distance, false);
			setCurrentAction(CURRENT_ACTION_NONE);
			setMovementReload(Utilities.now() + GameConstants.MOVEMENT_RELOAD);
		} else {
			// Recherche une case libre sur le trajet
			int xi = currentX,
				yi = currentY,
				xf = tmpX,
				yf = tmpY;
			
			// Calcule les points sur le trajet de la flotte
			List<Point> points = new ArrayList<Point>();
			
			int i, xinc, yinc, cumul, x, y;
			x = xi;
			y = yi;
			dx = xf - xi;
			dy = yf - yi;
			xinc = (dx > 0) ? 1 : -1;
			yinc = (dy > 0) ? 1 : -1;
			dx = Math.abs(dx);
			dy = Math.abs(dy);
			
			if (dx > dy) {
				cumul = dx / 2;
				for (i = 1 ; i <= dx; i++) {
					x += xinc;
					cumul += dy;
					if (cumul >= dx) {
						cumul -= dx;
						y += yinc;
					}
					points.add(0, new Point(x, y));
				}
			} else {
				cumul = dy / 2 ;
				for (i = 1 ; i <= dy; i++) {
					y += yinc;
					cumul += dx;
					if (cumul >= dy) {
						cumul -= dy;
						x += xinc ;
					}
					points.add(0, new Point(x, y));
				}
			}
			
			// Cherche un point libre sur le trajet pour y déplacer la
			// flotte
			for (Point point : points) {
				if (area.isFreeTile(point.x, point.y,
						Area.CHECK_FLEET_MOVEMENT, getOwner())) {
					dx = point.x - currentX;
					dy = point.y - currentY;
					distance = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
					
					setLocation(point.x, point.y);
					setMovement(movement - distance, false);
					setMovementReload(Utilities.now() + GameConstants.MOVEMENT_RELOAD);
					return;
				}
			}
			
			// Annule la planification si la flotte est bloquée sur
			// l'intégralité du trajet
			if (movement >= getMovementMax()) {
				setScheduledX(0);
				setScheduledY(0);
				setScheduledMove(false);
			}
		}
	}
	
	public void cancelJump() throws Exception {
		// Vérifie que la flotte est en train d'effectuer un saut hyperspatial
		if (!isStartingJump())
			throw new IllegalOperationException("La flotte n'est pas en " +
					"train de passer en hyperespace.");
		
		long now = Utilities.now();
		
		int hyperspaceIdArea = getHyperspaceIdArea();
		long movementReload = getMovementReload();
		
		setHyperspaceIdArea(0);
		setHyperspaceX(0);
		setHyperspaceY(0);
		setLastMove(now);
		doAction(CURRENT_ACTION_NONE, 0);
		
		// Détermine s'il faut supprimer le secteur des secteurs connus du joueur
		Player owner = getOwner();
		List<VisitedArea> areas = owner.getVisitedAreas();
		
		synchronized (areas) {
			for (VisitedArea area : areas) {
				if (area.getIdArea() == hyperspaceIdArea) {
					if (area.getDate() == movementReload) {
						// La flotte était à l'origine de l'exploration du
						// secteur ; détermine si une autre flotte du joueur
						// prend la meme direction, sans quoi le secteur est
						// supprimé des secteurs connus du joueur
						List<Fleet> hyperspaceFleets = DataAccess.getHyperspaceFleets(hyperspaceIdArea);
						long newDate = Long.MAX_VALUE;
						
						synchronized (hyperspaceFleets) {
							for (Fleet hyperspaceFleet : hyperspaceFleets) {
								if (hyperspaceFleet.getIdOwner() == getIdOwner() && hyperspaceFleet.getId()!=getId())
									newDate = Math.min(newDate,
											hyperspaceFleet.getMovementReload());
							}
						}
						
						if (newDate != Long.MAX_VALUE) {
							VisitedArea newArea = DataAccess.getEditable(area);
							newArea.setDate(newDate);
							newArea.save();
						} else {
							area.delete();
						}
						break;
					}
					break;
				}
			}
		}
	}
	
	public void jumpHyperspace(Sector sectorOut)
			throws IllegalOperationException {
		// Vérifie que le quadrant de destination n'est pas le quadrant dans
		// lequel la flotte se trouve
		if (sectorOut.getId() == getArea().getIdSector())
			throw new IllegalOperationException("Vous ne pouvez pas " +
					"effectuer de saut vers le même quadrant.");
		
		Sector sectorIn = getArea().getSector();
		
		// Vérifie que le quadrant de destination est accessible
		if (!sectorIn.isNeighbour(sectorOut.getId())) {
			byte[] sectorsVisibility = getOwner().getSectorsVisibility();
			
			if (sectorsVisibility[sectorOut.getId()] == Area.VISIBILITY_NONE) {
				List<Integer> neighbours = sectorOut.getNeighbours();
				
				boolean valid = false;
				for (int idNeighbour : neighbours) {
					if (sectorsVisibility[idNeighbour] >=
							Area.VISIBILITY_VISITED) {
						valid = true;
						break;
					}
				}
				
				if (!valid)
					throw new IllegalOperationException(
						"Vous n'avez pas accès à ce quadrant.");
			}
		}
		
		// Détermine un secteur au hasard dans le quadrant de destination
		List<Area> areas = sectorOut.getAreas();
		Area area = areas.get((int) (Math.random() * areas.size()));
		while(area.getGeneralType()==Area.AREA_GENERAL_MINING_X5)
		{
			area = areas.get((int) (Math.random() * areas.size()));
		}
		jumpHyperspace(area, true);
	}
	
	public void jumpHyperspace(Area areaOut)
			throws IllegalOperationException {
		jumpHyperspace(areaOut, false);
	}
	
	public boolean isMigrating(){
		if(getCurrentAction()==CURRENT_ACTION_MIGRATE)
			return true;
		else 
			return false;
	}
	public void setMovement(int movement, boolean stopScheduledMove) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (movement < 0)
			throw new IllegalArgumentException(
					"Le mouvement doit être >= 0 ! [x=" + getX() +
					",y=" + getY() + ",schx=" + getScheduledX() +
					",schy=" + getScheduledY() + ",movement=" +
					movement + ",getmvt" + getMovement());
		
		long now = Utilities.now();
		
		if (getHyperspaceIdArea() != 0 &&
				now >= getMovementReload()) {
			int fromArea = getIdArea();
			
			setIdArea(getHyperspaceIdArea());
			setX(getHyperspaceX());
			setY(getHyperspaceY());
			setHyperspaceIdArea(0);
			setHyperspaceX(0);
			setHyperspaceY(0);
			
			GameEventsDispatcher.fireGameNotification(
					new JumpFinishedEvent(this, fromArea));
		}
		
		// Découvre la flotte si elle était furtive
		if (isStealth() && getSkillLevel(Skill.SKILL_ULTIMATE_STEALTH) < 1)
			setStealth(false);
		
		if (stopScheduledMove) {
			setScheduledX(0);
			setScheduledY(0);
			setScheduledMove(false);
		}
		
		setMovementLeft(movement);
		setLastMove(now);
	}
	
	public List<FleetLink> getLinks() {
		return DataAccess.getFleetLinks(getId());
	}
	
	public int getAvailableSkillPoints() {
		if (availableSkillPoints == -1)
			updateSkillsCache();
		return availableSkillPoints;
	}
	
	public int getSkillLevel(int type) {
		if (skillLevels == null)
			updateSkillsCache();
		return skillLevels[type];
	}
	
	public Skill getBasicSkill(int index) {
		switch (index) {
		case 0:
			return decodeSkill(getEncodedSkill0());
		case 1:
			return decodeSkill(getEncodedSkill1());
		case 2:
			return decodeSkill(getEncodedSkill2());
		default:
			throw new IllegalArgumentException(
					"Invalid skill index: '" + index + "'.");
		}
	}
	
	public void setBasicSkill(Skill skill, int index) {
		switch (index) {
		case 0:
			setEncodedSkill0(encodeSkill(skill));
			break;
		case 1:
			setEncodedSkill1(encodeSkill(skill));
			break;
		case 2:
			setEncodedSkill2(encodeSkill(skill));
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid skill index: '" + index + "'.");
		}
		updateSkillsCache();
	}
	
	public Skill getUltimateSkill() {
		return decodeSkill(getEncodedSkillUltimate());
	}
	
	public void setUltimateSkill(Skill skill) {
		setEncodedSkillUltimate(encodeSkill(skill));
		updateSkillsCache();
	}
	
	public long getBasicSkillReload(int index) {
		switch (index) {
		case 0:
			return getSkill0Reload();
		case 1:
			return getSkill1Reload();
		case 2:
			return getSkill2Reload();
		default:
			throw new IllegalArgumentException(
					"Invalid skill index: '" + index + "'.");
		}
	}
	
	public void setBasicSkillReload(long reload, int index) {
		switch (index) {
		case 0:
			setSkill0Reload(reload);
			break;
		case 1:
			setSkill1Reload(reload);
			break;
		case 2:
			setSkill2Reload(reload);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid skill index: '" + index + "'.");
		}
	}

	public long getBasicSkillLastUse(int index) {
		switch (index) {
		case 0:
			return getSkill0LastUse();
		case 1:
			return getSkill1LastUse();
		case 2:
			return getSkill2LastUse();
		default:
			throw new IllegalArgumentException(
					"Invalid skill index: '" + index + "'.");
		}
	}
	
	public void setBasicSkillLastUse(long reload, int index) {
		switch (index) {
		case 0:
			setSkill0LastUse(reload);
			break;
		case 1:
			setSkill1LastUse(reload);
			break;
		case 2:
			setSkill2LastUse(reload);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid skill index: '" + index + "'.");
		}
	}
	
	public int[] getClassesQuantityQualifiers() {
		int[] classes = {0, 0, 0, 0, 0, 0, 0};
		
		// Compte le nombre de vaisseaux par classes
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Slot slot = getSlot(i);
			
			if (slot.getId() != 0) {
				classes[Ship.SHIPS[slot.getId()].getShipClass() - 1] += slot.getCount();
			}
		}
		
		// Recherche la tranche qui comporte le nombre de vaisseaux, pour chaque classe
		for (int i = 0; i < classes.length; i++) {
			if (classes[i] == 0)
				continue;
			
			boolean found = false;
			for (int j = 0; j < QUANTITY_QUALIFIERS.length; j++) {
				if (classes[i] < QUANTITY_QUALIFIERS[j]) {
					classes[i] = j;
					found = true;
					break;
				}
			}
			
			if (!found)
				classes[i] = QUANTITY_QUALIFIERS.length;
		}
		
		return classes;
	}
	
	public Slot getSlot(int index) {
		switch (index) {
		case 0:
			return new Slot(getSlot0Id(), getSlot0Count(), isSlot0Front());
		case 1:
			return new Slot(getSlot1Id(), getSlot1Count(), isSlot1Front());
		case 2:
			return new Slot(getSlot2Id(), getSlot2Count(), isSlot2Front());
		case 3:
			return new Slot(getSlot3Id(), getSlot3Count(), isSlot3Front());
		case 4:
			return new Slot(getSlot4Id(), getSlot4Count(), isSlot4Front());
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public Slot[] getSlots() {
		return new Slot[]{
			getSlot(0), getSlot(1), getSlot(2), getSlot(3), getSlot(4)
		};
	}
	
	public void setSlot(Slot slot, int index) {
		int oldSlotId;
		
		switch (index) {
		case 0:
			oldSlotId = getSlot0Id();
			setSlot0Id(slot.getId());
			setSlot0Count((long) slot.getCount());
			setSlot0Front(slot.isFront());
			break;
		case 1:
			oldSlotId = getSlot1Id();
			setSlot1Id(slot.getId());
			setSlot1Count((long) slot.getCount());
			setSlot1Front(slot.isFront());
			break;
		case 2:
			oldSlotId = getSlot2Id();
			setSlot2Id(slot.getId());
			setSlot2Count((long) slot.getCount());
			setSlot2Front(slot.isFront());
			break;
		case 3:
			oldSlotId = getSlot3Id();
			setSlot3Id(slot.getId());
			setSlot3Count((long) slot.getCount());
			setSlot3Front(slot.isFront());
			break;
		case 4:
			oldSlotId = getSlot4Id();
			setSlot4Id(slot.getId());
			setSlot4Count((long) slot.getCount());
			setSlot4Front(slot.isFront());
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
		
		if (oldSlotId != slot.getId()) {
			if (slot.getId() == 0)
				resetSlotActions(index);
			else
				updateTactics();
		}
	}
	
	public void setSlotFront(boolean front, int index) {
		switch (index) {
		case 0:
			setSlot0Front(front);
			break;
		case 1:
			setSlot1Front(front);
			break;
		case 2:
			setSlot2Front(front);
			break;
		case 3:
			setSlot3Front(front);
			break;
		case 4:
			setSlot4Front(front);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid slot index: '" + index + "'.");
		}
	}
	
	public void setSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++)
			setSlot(slots[i], i);
	}
	
	public long getShipsCount() {
		return getSlot0Count() + getSlot1Count() +
			getSlot2Count() + getSlot3Count() + getSlot4Count();
	}
	
	public int getMovementMax() {
		int movementMax = BASE_MOVEMENT;
		
//		int movementMax = MOVEMENT_MAX_BY_CLASS[Ship.FIGHTER];
//		
//		// Mouvement en fonction des classes de vaisseaux
//		for (int i = 0; i < GameConstants.SHIPS_SLOT_COUNT; i++) {
//			Slot slot = getSlot(i);
//			if (slot.getId() != 0) {
//				movementMax = Math.min(movementMax,
//						MOVEMENT_MAX_BY_CLASS[Ship.SHIPS[slot.getId()].getShipClass()]);
//			}
//		}
		
		return movementMax;
	}
	
	public ItemContainer getItemContainer() {
		return DataAccess.getItemContainerByFleet(getId());
	}

	public ShipContainer getShipContainer() {
		return DataAccess.getShipContainerByFleet(getId());
	}
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdCurrentArea());
	}
	
	public Player getOwner() {
		return DataAccess.getPlayerById(getIdOwner());
	}
	
	public long getPayload() {
		return getPayload(getSlots());
	}
	
	// Cette fonction n'ajoute pas d'XP aux flottes PNJ ; pour ajouter de l'XP
	// aux PNJ, utiliser setXp
	public void addXp(long newXp) {
		// Les PNJ ne gagnent pas d'XP
		if (getOwner().isAi())
			return;
		
		long xp = getXp() + newXp;
		long maxXp = XP_LEVELS[XP_LEVELS.length - 1];
		
		if (xp > maxXp)
			xp = maxXp;
		
		this.setXp(xp);
		updateSkillsCache();
	}
	
	public int getLevel() {
		long xp = getXp();
		
		int i;
		for (i = 0; i < XP_LEVELS.length; i++)
			if (xp < XP_LEVELS[i])
				return i;
		
		return 15;
	}

	public static int getLevelXp(int level) {
		return XP_LEVELS[level - 1];
	}
	
	public StarSystem getSystemOver() {
		List<StarSystem> systems = getArea().getSystems();
		
		for (StarSystem system : systems) {
			if (system.contains(getCurrentX(), getCurrentY()))
				return system;
		}
		
		return null;
	}
	
	public Structure getStructureUnderFleet() {
		List<Structure> structures = getArea().getStructures();
		
		for (Structure structure : structures) {
			if (structure.getBounds().contains(getCurrentX(), getCurrentY()))
				return structure;
		}
		
		return null;
	}
	
	public boolean isColonizing() {
		return getCurrentAction().equals(CURRENT_ACTION_COLONIZE);
	}
	
	public boolean isCapturing() {
		if (!getCurrentAction().equals(CURRENT_ACTION_COLONIZE))
			return false;
		
		if (getSystemOver().getIdOwner() != 0)
			return true;
		return false;
	}
	
	public long getColonizationRemainingTime() {
		if (!isColonizing())
			return 0;
		
		return getMovementReload() - Utilities.now();
	}
	
	public static int getShipQuantityQualifier(long quantity) {
		if (quantity == 0)
			return 0;
		
		for (int j = 0; j < QUANTITY_QUALIFIERS.length; j++) {
			if (quantity < QUANTITY_QUALIFIERS[j]) {
				return j;
			}
		}
		
		return QUANTITY_QUALIFIERS.length;
	}
	
	public boolean isDelude() {
		List<FleetLink> links = DataAccess.getFleetLinks(getId());
		
		synchronized (links) {
			for (FleetLink link : links) {
				if (link.isDelude() && link.getIdDstFleet() == getId())
					return true;
			}
		}
		
		return false;
	}
	
	public List<Integer> getDeludes() {
		ArrayList<Integer> deludes = new ArrayList<Integer>();
		List<FleetLink> links = DataAccess.getFleetLinks(getId());
		
		synchronized (links) {
			for (FleetLink link : links) {
				if (link.isDelude()) {
					if (link.getIdSrcFleet() == getId())
						deludes.add(link.getIdDstFleet());
					else
						return new ArrayList<Integer>();
				}
			}
		}
		
		return deludes;
	}
	
	public boolean hasOffensiveLink() {
		return getOffensiveLinkedFleet() != null;
	}
	
	public Fleet getOffensiveLinkedFleet() {
		List<FleetLink> links = DataAccess.getFleetLinks(getId());
		
		synchronized (links) {
			for (FleetLink link : links)
				if (link.isOffensive())
					return link.getOtherFleet(getId());
		}
		return null;
	}

	public int getOffensiveLinkedFleetId() {
		Fleet fleet = getOffensiveLinkedFleet();
		
		return fleet == null ? 0 : fleet.getId();
	}
	
	public boolean hasDefensiveLink() {
		return getDefensiveLinkedFleet() != null;
	}
	
	public Fleet getDefensiveLinkedFleet() {
		List<FleetLink> links = DataAccess.getFleetLinks(getId());
		
		synchronized (links) {
			for (FleetLink link : links)
				if (link.isDefensive())
					return link.getOtherFleet(getId());
		}
		return null;
	}
	
	public int getDefensiveLinkedFleetId() {
		Fleet fleet = getDefensiveLinkedFleet();
		
		return fleet == null ? 0 : fleet.getId();
	}
	
	// Renvoie le coût total de la flotte
	public long getTotalCost() {
		int value = 0;
		
		Slot[] slots = getSlots();
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			if (slots[i].getShip() != null &&
				slots[i].getShip().getShipClass() != Ship.FREIGHTER)
				value += slots[i].getCount() * slots[i].getShip().getTotalCost();
		}
		
		return value;
	}
	
	public int getDistance(Fleet fleet) {
		if (getIdCurrentArea() != fleet.getIdCurrentArea())
			return -1;
		
		return (int) Math.ceil(Math.sqrt(
				(getCurrentX() - fleet.getCurrentX()) * (getCurrentX() - fleet.getCurrentX()) +
				(getCurrentY() - fleet.getCurrentY()) * (getCurrentY() - fleet.getCurrentY())));
	}
	
	public int getPower() {
		return getPower(getSlots());
	}
	
	public static long getPayload(Slot[] slots) {
		long payload = 0;
		
		for (Slot slot : slots)
			if (slot.getId() != 0)
				payload += Ship.SHIPS[slot.getId()].getPayload() * slot.getCount();
		
		return payload;
	}
	
	public static int getPower(Slot[] slots) {
		int power = 0;
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++)
			power += slots[i].getId() != 0 ?
				Ship.CLASSES_POWER[slots[i].getShip().getShipClass()] *
				slots[i].getCount() : 0;
		return power;
	}
	
	public int getPowerLevel() {
		return getLevelAtPower(getPower());
	}
	
	public static int getLevelAtPower(int power) {
		if (power >= LEVEL_AT_POWER.length)
			power = LEVEL_AT_POWER.length - 1;
		
		return LEVEL_AT_POWER[power];
	}
	
	public static int getPowerAtLevel(int level) {
		if (level >= POWER_AT_LEVEL.length)
			level = POWER_AT_LEVEL.length - 1;
		
		return POWER_AT_LEVEL[level];
	}
	
	public void updateTactics() {
		int[] skirmishSlots		= new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		int[] skirmishAbilities = new int[GameConstants.SKIRMISH_ACTIONS_COUNT];
		
		scheduleActions(skirmishSlots, skirmishAbilities);
		setSkirmishActions(skirmishSlots, skirmishAbilities);
		
		int[] battleSlots	  = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		int[] battleAbilities = new int[GameConstants.BATTLE_ACTIONS_COUNT];
		
		scheduleActions(battleSlots, battleAbilities);
		setBattleActions(battleSlots, battleAbilities);
		
		// Place les cargos derrière
		int slotsCount = 0, freightersCount = 0;
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Ship ship = getSlot(i).getShip();
			
			if (ship != null) {
				slotsCount++;
				
				if (ship.getShipClass() == Ship.FREIGHTER)
					freightersCount++;
			}
		}
		
		int backFreightersCount = Math.min(slotsCount / 2, freightersCount);
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			Ship ship = getSlot(i).getShip();
			
			if (ship != null)
				setSlotFront(true, i);
		}
		
		if (backFreightersCount > 0) {
			int count = 0;
			
			for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
				Ship ship = getSlot(i).getShip();
				
				if (ship != null && ship.getShipClass() == Ship.FREIGHTER) {
					setSlotFront(false, i);
					
					if (++count == backFreightersCount)
						break;
				}
			}
		}
		
		setTacticsDefined(false);
	}
	
	public void setLocation(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Area getHyperspaceArea() {
		return DataAccess.getAreaById(getHyperspaceIdArea());
	}

	public void checkLinks() {
		// Supprime les liens si le déplacement conduit à trop écarter deux
		// flottes liés
		List<FleetLink> links = DataAccess.getFleetLinks(getId());
		
		for (FleetLink link : links){
			if (link.isOffensive() || link.isDefensive()) {
				Fleet otherFleet = link.getOtherFleet(getId());
				
				int range = link.isOffensive() ?
						Skill.SKILL_OFFENSIVE_LINK_RANGE :
						Skill.SKILL_DEFENSIVE_LINK_RANGE;
				
				int distance = getDistance(otherFleet);
				
				if (isInHyperspace() || distance == -1 || distance > range)
					link.delete();
			}
		}
	}
	
	public final static double getXpFactor(int attackerPower, int defenderPower) {
		if (attackerPower == defenderPower) {
			return 1;
		} else if (attackerPower < defenderPower) {
			double coef = Math.round(100 *
					((getPowerAtLevel(defenderPower + 1) - 1) /
							(double) (getPowerAtLevel(attackerPower + 1) - 1))) / 100.;
			
			coef -= 1;
			coef /= 2;
			coef += 1;
			
			return Math.min(1.5, coef);
		} else {
			return Math.max(0,
				Math.round(100 * ((getPowerAtLevel(defenderPower + 1) - 1) /
				(double) (getPowerAtLevel(attackerPower + 1) - 1))) / 100. -
				0.1 * (attackerPower - defenderPower));
		}
	}

	public void updateSkillsCache() {
		int[] skillLevels = new int[Skill.SKILLS_COUNT + 1];
		Arrays.fill(skillLevels, -1);
		
		int spentPoints = 0;
		
		// Compétences basiques
		for (int i = 0; i < 3; i++) {
			Skill skill = getBasicSkill(i);
			
			if (skill.getType() != 0) {
				skillLevels[skill.getType()] = skill.getLevel();
				spentPoints += skill.getLevel() + 1;
			}
		}
		
		// Compétence ultime
		Skill skill = getUltimateSkill();
		
		if (skill.getType() != 0) {
			skillLevels[skill.getType()] = skill.getLevel();
			spentPoints += skill.getLevel() + 1;
		}
		
		this.skillLevels = skillLevels;
		this.availableSkillPoints = getLevel() - spentPoints;
	}
	
	public void setEncodedTactics(String encodedSkirmishSlots,
			String encodedSkirmishAbilities, String encodedBattleSlots,
			String encodedBattleAbilities) {
		setEncodedSkirmishSlots(encodedSkirmishSlots);
		setEncodedSkirmishAbilities(encodedSkirmishAbilities);
		setEncodedBattleSlots(encodedBattleSlots);
		setEncodedBattleAbilities(encodedBattleAbilities);
	}
	
	@Override
	public void delete() {
		// Supprime les structures transportées par la flotte
		if (!isDelude()) {
			ItemContainer container = getItemContainer();
			for (int i = 0; i < container.getMaxItems(); i++) {
				Item item = container.getItem(i);
				
				if (item.getType() == Item.TYPE_STRUCTURE) {
					Structure structure =
						DataAccess.getStructureById(item.getIdStructure());
					if (structure != null) {
						structure.delete();
					}
				}
			}
		}
		
		// Supprime les leurres créés par la flotte
		for (Integer idDelude : getDeludes())
			DataAccess.getFleetById(idDelude).delete();
		
		super.delete();
	}
	
	public Contract getContract() {
		return DataAccess.getContractById(getIdContract());
	}
	
	// Jette les items en trop
	public void updateContainerMax(){
	ItemContainer container = getItemContainer();
		synchronized (container.getLock()) {
				container = DataAccess.getEditable(getItemContainer());
				
			if (getPayload() < getItemContainer().getTotalWeight()) {
				double coef = getPayload() / getItemContainer().getTotalWeight();
				
				for (int i = 0; i < getItemContainer().getMaxItems(); i++) {
					Item item = getItemContainer().getItem(i);
					item.setCount((long) Math.floor(item.getCount() * coef));
					container.setItem(item, i);
				}
			}
			container.save();
		}
	}
	
	// Vérifie si la flotte n'a pas plus d'item que possible
	public ItemContainer isOverLoaded(ItemContainer fleetContainer, double payloadBefore){
	Fleet fleet = this;
	if(fleet != null)
	{
		if(fleetContainer.getTotalWeight()>fleet.getPayload())
		{
			if(fleet.getPayload()==0)
			{
				fleetContainer.setItem0Count(0);
				fleetContainer.setItem0Id(0);
				fleetContainer.setItem0Type(0);
				
				fleetContainer.setItem1Count(0);
				fleetContainer.setItem1Id(0);
				fleetContainer.setItem1Type(0);
				
				fleetContainer.setItem2Count(0);
				fleetContainer.setItem2Id(0);
				fleetContainer.setItem2Type(0);
				
				fleetContainer.setItem3Count(0);
				fleetContainer.setItem3Id(0);
				fleetContainer.setItem3Type(0);
			}
			else
			{
				
				int random= (int) (Math.random()*4);
					for(int i=0;i<4;i++)
					{
						double dif=fleet.getPayload()-payloadBefore;
						int type = (i+random)%4;
						if(fleetContainer.getTotalWeight()<=fleet.getPayload())
							break;
						
						int itemType =fleetContainer.getItem(type).getType();
						if(itemType== Item.TYPE_RESOURCE)
						{
							if(fleetContainer.getItem(type).getCount()>dif)
							{
								double newContainer =
									fleetContainer.getItem(type).getCount()-dif;
								fleetContainer.getItem(type).setCount(newContainer);
									
							}
							else
							{
								fleetContainer.getItem(type).setCount(0);
								fleetContainer.getItem(type).setId(0);
								fleetContainer.getItem(type).setType(0);
							}
						}

					}
					for(int i=0;i<4;i++)
					{
						double dif=fleet.getPayload()-payloadBefore;
						int type = (i+random)%4;
						if(fleetContainer.getTotalWeight()<=fleet.getPayload())
							break;
						
						int itemType =fleetContainer.getItem(type).getType();
							if (itemType== Item.TYPE_STRUCTURE)
							{
								fleetContainer.getItem(type).setCount(0);
								fleetContainer.getItem(type).setId(0);
								fleetContainer.getItem(type).setType(0);
							}
						}

					}
				}
			}	
		return fleetContainer;
		}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	// Efface les actions d'un vaisseau sans affecter les actions des autres
	// vaisseaux
	private void resetSlotActions(int slot) {
		int[] skirmishActions = getSkirmishActionSlots();
		int[] skirmishAbilities = getSkirmishActionAbilities();
		int[] battleActions = getBattleActionSlots();
		int[] battleAbilities = getBattleActionAbilities();
		
		for (int i = 0; i < skirmishActions.length; i++)
			if (skirmishActions[i] == slot) {
				skirmishActions[i] = -1;
				skirmishAbilities[i] = -1;
			}
		
		for (int i = 0; i < battleActions.length; i++)
			if (battleActions[i] == slot) {
				battleActions[i] = -1;
				battleAbilities[i] = -1;
			}
		
		setSkirmishActions(skirmishActions, skirmishAbilities);
		setBattleActions(battleActions, battleAbilities);
		
		// Déplace un slot sur la ligne de front s'il y a en trop à l'arrière
		int frontCount = 0, backCount = 0;
		for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
			if (getSlot(j).getId() != 0)
				if (getSlot(j).isFront())
					frontCount++;
				else
					backCount++;
		
		loop:while (backCount > frontCount) {
			for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
				if (getSlot(j).getId() != 0 &&
						!getSlot(j).isFront() &&
						getSlot(j).getShip().getShipClass() != Ship.FREIGHTER) {
					setSlotFront(true, j);
					backCount--;
					frontCount++;
					continue loop;
				}
			
			for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++)
				if (getSlot(j).getId() != 0 && !getSlot(j).isFront()) {
					setSlotFront(true, j);
					backCount--;
					frontCount++;
					continue loop;
				}
		}
	}
	
	private void scheduleActions(int[] slots, int[] abilities) {
		List<Research> researches =
			DataAccess.getResearchesByPlayer(getIdOwner());
		
		Arrays.fill(slots, -1);
		Arrays.fill(abilities, -1);
		
		// Regénère les actions
		for (int i = 0; i < slots.length; i++) {
			for (int j = 0; j < GameConstants.FLEET_SLOT_COUNT; j++) {
				int shipId = getSlot(j).getId();
				
				if (shipId == 0)
					continue;
				
				if (getShipUnavailabilityTime(slots, abilities, j, i) > 0)
					continue;
				
				int ability = -2;
				
				if (Ship.SHIPS[shipId].getWeapons().length > 0) {
					ability = -1;
				} else {
					// Recherche une compétence active pour laquelle le joueur
					// a développé tous les prérequis
					Ability[] shipAbilities = Ship.SHIPS[shipId].getAbilities();
					abilities:for (int k = 0; k < shipAbilities.length; k++) {
						Ability shipAbility = shipAbilities[k];
						
						if (!shipAbility.isPassive()) {
							requirements:for (int requirement : shipAbility.getRequirements()) {
								synchronized (researches) {
									for (Research research : researches) {
										if (research.getIdTechnology() == requirement) {
											if (research.getProgress() < 1) {
												continue abilities;
											} else {
												continue requirements;
											}
										}
									}
								}
								
								continue abilities;
							}
							
							if (shipAbility.getCooldown() <= slots.length + 4 - i - 1) {
								ability = k;
								break abilities;
							}
						}
					}
				}
				
				if (ability != -2) {
					slots[i] = j;
					abilities[i] = ability;
					break;
				}
			}
		}
	}
	
	private int getShipUnavailabilityTime(int[] slots, int[] abilities, int slotIndex, int step) {
		// Vérifie que le vaisseau est disponible
		for (int k = 0; k < step; k++) {
			int actionSlot = slots[k];
			
			if (actionSlot == slotIndex) {
				Ship ship = Ship.SHIPS[getSlot(slotIndex).getId()];
				
				int ability = abilities[k];
				int cooldown = ability == -1 ? 4 :
					ship.getAbilities()[ability].getCooldown();
				
				if (k + cooldown >= step)
					return 1 + k + cooldown - step;
			}
		}
		
		return 0;
	}
	
	private int encodeSkill(Skill skill) {
		return (skill.getLevel() << 6) | skill.getType();
	}
	
	private Skill decodeSkill(int data) {
		if (data != 0)
			return new Skill(data & 0x3f, data >> 6);
		return new Skill();
	}
	
	private String encodeActions(int[] actions) {
		StringBuffer result = new StringBuffer(actions.length);
		for (int i = 0; i < actions.length; i++)
			result.append(actions[i] == -1 ? "9" : String.valueOf(actions[i]));
		return result.toString();
	}
	
	private int[] decodeActions(String actions) {
		int[] result = new int[actions.length()];
		for (int i = 0; i < actions.length(); i++) {
			result[i] = Integer.parseInt(String.valueOf(actions.charAt(i)));
			result[i] = result[i] == 9 ? -1 : result[i];
		}
		return result;
	}
	
	private void jumpHyperspace(Area areaOut, boolean sectorJump)
			throws IllegalOperationException {
		Area areaIn = getArea();
		
		// Vérifie que la flotte est suffisament proche d'une porte
		// hyperspatiale
		List<StellarObject> gates = areaIn.getGates();
		StellarObject gateIn = null;
		
		for (StellarObject gate : gates) {
			int dx = getCurrentX() - gate.getX();
			int dy = getCurrentY() - gate.getY();
			
			if (dx * dx + dy * dy <= GameConstants.HYPERGATE_JUMP_RADIUS *
					GameConstants.HYPERGATE_JUMP_RADIUS) {
				gateIn = gate;
				break;
			}
		}
		
		if (gateIn == null)
			throw new IllegalOperationException(
					"Aucune porte hyperspatiale à portée.");
		
		// Vérifie que la flotte n'est pas dans un système
		List<StarSystem> systems = areaIn.getSystems();
		
		synchronized (systems) {
			for (StarSystem system : systems) {
				int dx = getCurrentX() - system.getX();
				int dy = getCurrentY() - system.getY();
				
				if (dx * dx + dy * dy < GameConstants.SYSTEM_RADIUS *
						GameConstants.SYSTEM_RADIUS) {
					throw new IllegalOperationException("Impossible " +
							"d'effectuer un saut dans un système.");
				}
			}
		}
		
		// Vérifie que le secteur de destination n'est pas le secteur dans
		// lequel la flotte se trouve
		if (areaOut.getId() == gateIn.getIdArea())
			throw new IllegalOperationException("Vous ne pouvez pas effectuer de " +
					"saut vers le même secteur.");
		
		// Vérifie que la flotte n'est pas en hyperespace ou en train de sortir
		// d'hyperespace
		if (isInHyperspace())
			throw new IllegalOperationException("La flotte est en hyperespace.");
		
		// Vérifie que l'hyperpropulsion a été rechargée
		if (!isJumpReloaded())
			throw new IllegalOperationException(
					"La flotte recharge son hyperpropulsion.");
		
		// Vérifie que la flotte a au moins un point de mouvement
		if (getMovement() == 0)
			throw new IllegalOperationException("La flotte n'a pas " +
					"suffisament de mouvement pour sauter.");
		
		// Vérifie que la zone de destination est accessible
		if (!sectorJump && !areaIn.isNeighbour(areaOut.getId())) {
			byte[] areasVisibility = getOwner().getAreasVisibility();
			
			if (areasVisibility[areaOut.getId()] == Area.VISIBILITY_NONE) {
				List<Integer> neighbours = areaOut.getNeighbours();
				
				boolean valid = false;
				for (int idNeighbour : neighbours) {
					if (areasVisibility[idNeighbour] >=
							Area.VISIBILITY_VISITED) {
						valid = true;
						break;
					}
				}
				
				if (!valid)
					throw new IllegalOperationException(
						"Vous n'avez pas accès à ce secteur.");
			}
		}
		
		// Détermine une porte hyperspatiale d'arrivée aléatoirement
		gates = areaOut.getGates();
		StellarObject gateOut = gates.get((int) (Math.random() * gates.size()));
		
		int[] result = areaOut.generateExitPosition(gateOut.getX(), gateOut.getY(), 
				10, 5, Area.CHECK_HYPERJUMP_OUTPUT, null);

		// Réduction de la durée du temps de saut avec les relais HE
		double coef = 1;
		
		if (!sectorJump) {
			List<Structure> structures = areaOut.getStructures();
			Player owner = getOwner();
			
			synchronized (structures) {
				for (Structure structure : structures) {
					if (structure.getType() == Structure.TYPE_HYPERSPACE_RELAY &&
							structure.isActivated() &&
							(structure.getIdOwner() == getIdOwner() ||
							(owner.getIdAlly() != 0 &&
							 structure.getOwner().getIdAlly() == owner.getIdAlly()))) {
						coef = Math.min(coef, Math.pow(.92, structure.getModuleLevel(
							StructureModuleData.TYPE_WARP_FIELD) + 1));
					}
				}
			}
		}
		
		// Réduction de la durée de saut avec l'heptanium
		coef *= Product.getProductEffect(Product.PRODUCT_HEPTANIUM,
			getOwner().getProductsCount(Product.PRODUCT_HEPTANIUM));
		
		// Compétence tracker
		int trackerLevel = getSkillLevel(Skill.SKILL_TRACKER);
		
		long end = Utilities.now() + (long) (Math.ceil(coef * (
			(sectorJump ?
				GameConstants.HYPERSPACE_SECTORS_TRAVEL_LENGTH :
				GameConstants.HYPERSPACE_AREAS_TRAVEL_LENGTH) -
			(trackerLevel != -1 ? (sectorJump ? 2 : 1) *
				Skill.SKILL_TRACKER_EFFECT[trackerLevel] : 0))));
		
		doAction(CURRENT_ACTION_JUMP, end);
		setHyperspaceIdArea(areaOut.getId());
		setHyperspaceX(result[0]);
		setHyperspaceY(result[1]);
		
		if (getSkillLevel(Skill.SKILL_ULTIMATE_STEALTH) != 2)
			setStealth(false);
		
		checkLinks();
		
		getOwner().addVisitedArea(areaOut, end);
	}
}
