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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.base.StructureBase;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Structure extends StructureBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_STOREHOUSE 				= 0,
		TYPE_SPACESHIP_YARD 			= 1,
		TYPE_FORCE_FIELD 				= 2,
		TYPE_STASIS_CHAMBER 			= 3,
		TYPE_HYPERSPACE_RELAY 			= 4,
		TYPE_GENERATOR 					= 5,
		TYPE_EXPLOITATION_TITANE		= 6,
		TYPE_EXPLOITATION_CRISTAL		= 7,
		TYPE_EXPLOITATION_ANDIUM		= 8,
		TYPE_EXPLOITATION_ANTIMATIERE	= 9,
		TYPE_INFRASTRUCTURE				= 10,
		TYPE_LABORATORY					= 11,
		TYPE_DEFENSE_HANGAR				= 12;
	
	public final static int GENERATOR_RADIUS = 40;
	
	public final static int FORCE_FIELD_RADIUS = 5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Bords de l'objet (mis en cache pour un accès rapide)
	private Rectangle bounds;
	
	private boolean modulesCached;
	
	private int level;
	
	private double payload;
	
	private int maxHull;
	
	private long maxShips;
	
	private int maxEnergy;
	
	private int spaceshipYardDecks;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Structure() {
		// Nécessaire pour la construction par réflection
		this.bounds = new Rectangle();
		this.modulesCached = false;
	}
	
	public Structure(int type, String name, int hull, int x, int y,
			long idEnergySupplierStructure, int idOwner, int idArea) {
		this.bounds = new Rectangle();
		this.modulesCached = false;
		
		super.setType(type);
		setName(name);
		setHull(hull);
		super.setX(x);
		super.setY(y);
		setShared(true);
		setShortcut(-1);
		setIdEnergySupplierStructure(idEnergySupplierStructure);
		setIdOwner(idOwner);
		setIdArea(idArea);
		setIdItemContainer(0);
		
		updateBounds();
	}
	
	// --------------------------------------------------------- METHODES -- //

	public boolean isActivated() {
		return getIdEnergySupplierStructure() != 0;
	}
	
	public ShipContainer getShipContainer() {
		return DataAccess.getShipContainerByStructure(getId());
	}
	
	public List<Structure> getActivatedForceFieldsWithinRange() {
		List<Structure> structures = getArea().getStructures();
		List<Structure> forceFields = new ArrayList<Structure>();
		
		Dimension size = getSize();
		Player owner = getOwner();
		
		synchronized (structures) {
			for (Structure structure : structures) {
				if (structure.getType() == Structure.TYPE_FORCE_FIELD &&
						structure.isActivated() &&
						(structure.getIdOwner() == getIdOwner() ||
						(owner.getIdAlly() != 0 &&
						structure.getOwner().getIdAlly() == owner.getIdAlly()))) {
					int dx = structure.getX() - getX();
					int dy = structure.getY() - getY();
					int radius = FORCE_FIELD_RADIUS + size.width + size.height;
					
					if (dx * dx + dy * dy <= radius * radius) {
						int cornerX = getX() - size.width / 2;
						int cornerY = getY() - size.height / 2;
						
						search:for (int j = cornerX ; j < cornerX + size.getWidth(); j++)
				        	for (int k = cornerY; k < cornerY + size.getHeight(); k++) {
								dx = structure.getX() - j;
								dy = structure.getY() - k;
								
								if (dx * dx + dy * dy <= FORCE_FIELD_RADIUS *
										FORCE_FIELD_RADIUS) {
									forceFields.add(structure);
									break search;
								}
				        	}
					}
				}
			}
		}
		
		return forceFields;
	}
	
	public List<Structure> getAllyStructuresWithinRange(int range) {
		List<Structure> structures = getArea().getStructures();
		List<Structure> structuresWithinRange = new ArrayList<Structure>();
		
		Dimension size = getSize();
		Player owner = getOwner();
		
		synchronized (structures) {
			for (Structure structure : structures) {
				if (structure.getId() != getId() &&
						(structure.getIdOwner() == getIdOwner() ||
						(owner.getIdAlly() != 0 &&
						structure.getOwner().getIdAlly() == owner.getIdAlly()))) {
					int dx = structure.getX() - getX();
					int dy = structure.getY() - getY();
					int radius = range + size.width + size.height;
					
					if (dx * dx + dy * dy <= radius * radius) {
						int cornerX = getX() - size.width / 2;
						int cornerY = getY() - size.height / 2;
						
						search:for (int j = cornerX ; j < cornerX + size.getWidth(); j++)
				        	for (int k = cornerY; k < cornerY + size.getHeight(); k++) {
								dx = structure.getX() - j;
								dy = structure.getY() - k;
								
								if (dx * dx + dy * dy <= range * range) {
									structuresWithinRange.add(structure);
									break search;
								}
				        	}
					}
				}
			}
		}
		
		return structuresWithinRange;
	}
	
	public List<StructureModule> getModules() {
		return DataAccess.getModulesByStructure(getId());
	}
	
	public StructureSpaceshipYard getSpaceshipYard() {
		return DataAccess.getSpaceshipYardByStructure(getId());
	}
	
	public double getPayload() {
		if (!modulesCached)
			updateModulesCache();
		return isActivated() ? payload : 0;
	}
	
	public double getAreaPayload(){
		double capacity=0;
		List<Structure> areaStructures = 
			new ArrayList<Structure>(DataAccess.getStructuresByArea(getIdArea()));
		for(Structure areaStructure : areaStructures)
		{
			if(areaStructure.getType()==Structure.TYPE_STOREHOUSE &&
					areaStructure.getOwner()==getOwner())
			{
				capacity+=areaStructure.getPayload();
			}
		}
		return capacity;
	}
	
	public long getMaxShips() {
		if (!modulesCached)
			updateModulesCache();
		return maxShips;
	}
	
	public int getMaxEnergy() {
		if (!modulesCached)
			updateModulesCache();
		return isActivated() ? maxEnergy : 0;
	}
	
	public int getUsedEnergy() {
		List<Structure> structures =
			DataAccess.getStructuresByEnergySupplier(getId());
		int usedEnergy = 0;
		
		synchronized (structures) {
			for (Structure structure : structures) {
				usedEnergy += structure.getEnergyConsumption();
			}
		}
		
		return usedEnergy;
	}
	
	public List<StructureSkill> getSkills() {
		return DataAccess.getSkillsByStructure(getId());
	}
	
	public Player getOwner() {
		return DataAccess.getPlayerById(getIdOwner());
	}
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public Point getFreeTile() {
    	Area area = getArea();
    	int halfWidth = bounds.width / 2;
    	int halfHeight = bounds.height / 2;
    	
    	for (int i = 0; i < bounds.width; i++)
        	for (int j = 0; j < bounds.height; j++) {
        		int x = getX() - halfWidth + i;
        		int y = getY() - halfHeight + j;
    			if (area.isFreeTile(x, y, Area.NO_FLEETS, null))
    				return new Point(x, y);
    		}
    	
    	return null;
	}
	
	public boolean contains(int x, int y) {
		return getBounds().contains(x, y);
	}
	
	public void setX(int x) {
		super.setX(x);
		updateBounds();
	}
	
	public void setY(int y) {
		super.setY(y);
		updateBounds();
	}
	
	public int getMaxHull() {
		if (!modulesCached)
			updateModulesCache();
		return maxHull;
	}
	
	public void setType(int type) {
		super.setType(type);
		updateBounds();
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public int getWeight() {
		int dimension = getSize().width * getSize().height;
		
		int coef;
		if (dimension <= 1)
			coef = 1500;
		else if (dimension <= 4)
			coef = 3000;
		else
			coef = 5000;
		
		return (int) (coef * Math.pow(1.25, getLevel()));
	}
	
	@Override
	public void setIdEnergySupplierStructure(long idEnergySupplierStructure) {
		super.setIdEnergySupplierStructure(idEnergySupplierStructure);
		modulesCached = false;
	}
	
	public synchronized void updateModulesCache() {
		List<StructureModule> modules = getModules();
		
		// Niveau
		int level = 0;
		synchronized (modules) {
			for (StructureModule module : modules)
				level += module.getLevel();
		}
		
		// Capacité
		double payload;
		switch (getType()) {
		case TYPE_STOREHOUSE:
			payload = 100000;
			break;
		default:
			payload = 0;
			break;
		}
		
		// Points de structure
		int maxHull = getDefaultHull(getType());
		
		synchronized (modules) {
			for (StructureModule module : modules) {
				switch (module.getType()) {
				case StructureModule.TYPE_RESOURCES_PAYLOAD:
					payload = (( Math.floor(Math.pow(2.15,
							module.getLevel()) * 100000)) / 1000) * 1000;
					break;
				case StructureModule.TYPE_HULL:
					for (int i = 0; i < module.getLevel(); i++)
						maxHull = (int) Math.floor(1.1 * maxHull +
							0.6 * maxHull * Math.pow(.9, i));
					break;
				}
			}
		}
		
		// Nombre max de vaisseaux
		long maxShips;
		switch (getType()) {
		case TYPE_SPACESHIP_YARD:
			maxShips = (((int) (1000 * Math.pow(1.9, getModuleLevel(
				StructureModule.TYPE_HANGAR)))) / 100) * 100;
			break;
		default:
			maxShips = 0;
			break;
		}
		
		// Energie max
		int maxEnergy;
		switch (getType()) {
		case TYPE_GENERATOR:
			maxEnergy = (int) Math.floor(25 * Math.pow(1.2, getModuleLevel(
				StructureModule.TYPE_REACTOR)));
			break;
		default:
			maxEnergy = 0;
			break;
		}
		
		// Plateformes pour les chantiers spatiaux
		int spaceshipYardDecks = 0;
		
		switch (getType()) {
		case TYPE_SPACESHIP_YARD:
			for (int i = 0; i < 6; i++)
				if (getModuleLevel(StructureModule.TYPE_DECK_FIGHTER + i) > 0)
					spaceshipYardDecks |= (1 << i);
			break;
		}
		
		this.maxHull = maxHull;
		this.payload = payload;
		this.level = level;
		this.maxShips = maxShips;
		this.maxEnergy = maxEnergy;
		this.spaceshipYardDecks = spaceshipYardDecks;
		modulesCached = true;
	}
	
	public int getLevel() {
		if (!modulesCached)
			updateModulesCache();
		return level;
	}
	
	public int getSpaceshipYardDecks() {
		if (!modulesCached)
			updateModulesCache();
		return spaceshipYardDecks;
	}
	
	public boolean isValidModule(int moduleType) {
		int[] validModules = getValidModules(getType());
		
		for (int validModule : validModules) {
			if (validModule == moduleType)
				return true;
		}
		
		return false;
	}
	
	public int[] getValidModules() {
		return getValidModules(getType());
	}
	
	public int getModuleLevel(int moduleType) {
		List<StructureModule> modules = new ArrayList<StructureModule>(getModules());
		
		for (StructureModule module : modules)
			if (module.getType() == moduleType)
				return module.getLevel();
		
		return 0;
	}
	
	public double getCostCoef(int moduleType) {
		List<StructureModule> modules = getModules();
		
		synchronized (modules) {
			for (StructureModule module : modules) {
				if (module.getType() == moduleType) {
					return Math.pow(2, module.getLevel()) *
						(getLevel() + 1) / (module.getLevel() + 1.);
				}
			}
		}
		
		return getLevel() + 1;
	}
	
	
	public long getDismountCost() {
		int[] baseCost = Structure.getBaseCost(getType());
		int coef = 0;
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			coef += baseCost[i];
		coef /= 7;
		
		return (long) (coef * Math.pow(1.25, getLevel()));
	}
	
	public long[] getModuleCost(int moduleType, int level) {
		double[] moduleCostCoef = StructureModule.getCostCoef(moduleType);
		double structureCostCoef;
		if (moduleType >= StructureModule.TYPE_DECK_FIGHTER &&
				moduleType <= StructureModule.TYPE_DECK_BATTLECRUISER)
			structureCostCoef = 1;
		else
			structureCostCoef = getCostCoef(moduleType);
		
		int[] structureCost = Structure.getBaseCost(getType());
		int baseCost = 0;
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			baseCost += structureCost[i];
		
		// Calcule le prix de l'amélioration du module
		long[] cost = new long[moduleCostCoef.length];
		for (int i = 0; i < cost.length; i++)
			cost[i] = (long) ((( Math.floor(baseCost * structureCostCoef * moduleCostCoef[i])) / 100) * 100);
		
		return cost;
	}
	
	public Dimension getSize() {
		return getSize(getType());
	}
	
	public int getEnergyConsumption() {
		return getEnergyConsumption(getType());
	}
	
	public void save() {
		super.save();
		
		switch (getType()) {
		case Structure.TYPE_SPACESHIP_YARD:
			if (getSpaceshipYard() == null) {
				StructureSpaceshipYard spaceshipYard =
					new StructureSpaceshipYard(getId());
				spaceshipYard.save();
			}
			
			if (getShipContainer() == null) {
				ShipContainer shipContainer = new ShipContainer(
					ShipContainer.CONTAINER_STRUCTURE, getId());
				shipContainer.save();
			}
			break;
		case Structure.TYPE_STASIS_CHAMBER:
			if (getSkills().size() == 0) {
				StructureSkill skill = new StructureSkill(getId(),
					StructureSkill.TYPE_STASIS, Utilities.now() +
					StructureSkill.STASIS_RELOAD);
				skill.save();
			}
			break;
		}
	}
	
	public static int[] getValidModules(int type) {
		switch (type) {
		case TYPE_STOREHOUSE:
			return new int[]{
				StructureModule.TYPE_HULL,
				StructureModule.TYPE_RESOURCES_PAYLOAD,
			};
		case TYPE_SPACESHIP_YARD:
			return new int[]{
				StructureModule.TYPE_HULL,
				StructureModule.TYPE_HANGAR,
				StructureModule.TYPE_DECK_FIGHTER,
				StructureModule.TYPE_DECK_CORVETTE,
				StructureModule.TYPE_DECK_FRIGATE,
				StructureModule.TYPE_DECK_DESTROYER,
				StructureModule.TYPE_DECK_DREADNOUGHT,
				StructureModule.TYPE_DECK_BATTLECRUISER,
			};
		case TYPE_FORCE_FIELD:
			return new int[]{
				StructureModule.TYPE_HULL,
				StructureModule.TYPE_REPAIR,
			};
		case TYPE_STASIS_CHAMBER:
			return new int[]{
				StructureModule.TYPE_HULL,
				StructureModule.TYPE_SKILL_RELOAD,
			};
		case TYPE_HYPERSPACE_RELAY:
			return new int[]{
				StructureModule.TYPE_HULL,
				StructureModule.TYPE_WARP_FIELD,
			};
		case TYPE_GENERATOR:
			return new int[]{
				StructureModule.TYPE_HULL,
				StructureModule.TYPE_REACTOR,
			};
					case TYPE_EXPLOITATION_TITANE:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_PROD_TITANE,
			};
		case TYPE_EXPLOITATION_CRISTAL:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_PROD_CRISTAL
			};
		case TYPE_EXPLOITATION_ANDIUM:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_PROD_ANDIUM,
			};
		case TYPE_EXPLOITATION_ANTIMATIERE:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_PROD_ANTIMATIERE,
			};
		case TYPE_LABORATORY:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_PROD_IDEA,
				};
		case TYPE_INFRASTRUCTURE:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_PROD_CREDIT,
				};
		case TYPE_DEFENSE_HANGAR:
			return new int[]{
					StructureModule.TYPE_HULL,
					StructureModule.TYPE_LVL_PNJ,
					StructureModule.TYPE_NUMBER_PNJ,
					StructureModule.TYPE_PROD_PNJ,
				};
		}
		
		return new int[0];
	}
	
	public static int getXp(int type) {
		return 8;
	}
	
	public static int[] getBaseCost(int type) {
		switch (type) {
		case TYPE_STOREHOUSE:
			return new int[]{10000, 0, 0, 0, 0};
		case TYPE_SPACESHIP_YARD:
			return new int[]{0, 15000, 0, 0, 0};
		case TYPE_FORCE_FIELD:
			return new int[]{0, 0, 5000, 0, 0};
		case TYPE_STASIS_CHAMBER:
			return new int[]{0, 0, 0, 5000, 0};
		case TYPE_HYPERSPACE_RELAY:
			return new int[]{0, 50000, 0, 0, 0};
		case TYPE_GENERATOR:
			return new int[]{25000, 0, 0, 0, 0};
		case TYPE_EXPLOITATION_TITANE:
			return new int[]{0, 25000, 0, 0, 0};
		case TYPE_EXPLOITATION_CRISTAL:
			return new int[]{0, 0, 25000, 0, 0};
		case TYPE_EXPLOITATION_ANDIUM:
			return new int[]{25000, 0, 0, 0, 0};
		case TYPE_EXPLOITATION_ANTIMATIERE:
			return new int[]{25000, 0, 10000, 0, 0};
		case TYPE_INFRASTRUCTURE:
			return new int[]{15000, 0, 15000, 0, 0};
		case TYPE_LABORATORY:
			return new int[]{0, 0, 25000, 0, 0};
		case TYPE_DEFENSE_HANGAR:
			return new int[]{20000, 10000, 0, 0, 0};
			
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static Dimension getSize(int type) {
		switch (type) {
		case TYPE_EXPLOITATION_TITANE:
		case TYPE_EXPLOITATION_CRISTAL:
		case TYPE_EXPLOITATION_ANDIUM:
		case TYPE_GENERATOR:
		case TYPE_INFRASTRUCTURE:
			return new Dimension(5, 5);
		case TYPE_SPACESHIP_YARD:
			return new Dimension(5, 4);
		case TYPE_STOREHOUSE:
		case TYPE_DEFENSE_HANGAR:
			return new Dimension(4, 4);
		case TYPE_STASIS_CHAMBER:
		case TYPE_HYPERSPACE_RELAY:
			return new Dimension(2, 2);
		case TYPE_FORCE_FIELD:
			return new Dimension(1, 1);
		case TYPE_EXPLOITATION_ANTIMATIERE:
		case TYPE_LABORATORY:
			return new Dimension(6,4);
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static int getEnergyDiffusionRange(int type) {
		switch (type) {
		case TYPE_GENERATOR:
			return 40;
		default:
			return 0;
		}
	}
	
	public static int getEnergyConsumption(int type) {
		switch (type) {
		case TYPE_STOREHOUSE:
			return 2;
		case TYPE_SPACESHIP_YARD:
			return 4;
		case TYPE_FORCE_FIELD:
			return 4;
		case TYPE_STASIS_CHAMBER:
			return 2;
		case TYPE_DEFENSE_HANGAR:
			return 10;
		case TYPE_HYPERSPACE_RELAY:
			return 20;
		case TYPE_EXPLOITATION_TITANE:
		case TYPE_EXPLOITATION_CRISTAL:
		case TYPE_EXPLOITATION_ANDIUM:
		case TYPE_EXPLOITATION_ANTIMATIERE:
		case TYPE_LABORATORY:
		case TYPE_INFRASTRUCTURE:
			return 8;
		case TYPE_GENERATOR:
			return 0;
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	public static int getDefaultHull(int type) {
		switch (type) {
		case TYPE_GENERATOR:
			return 20;
		case TYPE_STOREHOUSE:
		case TYPE_SPACESHIP_YARD:
		case TYPE_EXPLOITATION_TITANE:
		case TYPE_EXPLOITATION_CRISTAL:
		case TYPE_EXPLOITATION_ANDIUM:
		case TYPE_EXPLOITATION_ANTIMATIERE:
		case TYPE_LABORATORY:
		case TYPE_INFRASTRUCTURE:
		case TYPE_DEFENSE_HANGAR:
			return 10;
		case TYPE_STASIS_CHAMBER:
		case TYPE_HYPERSPACE_RELAY:
		case TYPE_FORCE_FIELD:
			return 5;
		default:
			throw new IllegalArgumentException("Invalid value: '" + type + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateBounds() {
		Dimension size = getSize(getType());
		
		this.bounds.setBounds(
			getX() - size.width / 2,
			getY() - size.height / 2,
			size.width,
			size.height
		);
	}
}
