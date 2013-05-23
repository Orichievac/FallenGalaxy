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

public class AreaBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		ENVIRONMENT_NORMAL = "normal",
		ENVIRONMENT_GLACIAL = "glacial",
		ENVIRONMENT_DESERT = "desert",
		ENVIRONMENT_ALIEN = "alien",
		ENVIRONMENT_MINE = "mine",
		ENVIRONMENT_EPAVE = "epave";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String name;
	private int width;
	private int height;
	private int x;
	private int y;
	private int type;
	private int spaceStationsLimit;
	private int product;
	private boolean full;
	private long lastNameUpdate;
	private int idDominatingAlly;
	private int idSector;
	private String environment;
	
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
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (width < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				width + "' (must be >= 0).");
		else
			this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (height < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				height + "' (must be >= 0).");
		else
			this.height = height;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.y = y;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (type < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				type + "' (must be >= 0).");
		else
			this.type = type;
	}
	
	public int getSpaceStationsLimit() {
		return spaceStationsLimit;
	}
	
	public void setSpaceStationsLimit(int spaceStationsLimit) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (spaceStationsLimit < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				spaceStationsLimit + "' (must be >= 0).");
		else
			this.spaceStationsLimit = spaceStationsLimit;
	}
	
	public int getProduct() {
		return product;
	}
	
	public void setProduct(int product) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (product < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				product + "' (must be >= 0).");
		else
			this.product = product;
	}
	
	public boolean isFull() {
		return full;
	}
	
	public void setFull(boolean full) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.full = full;
	}
	
	public long getLastNameUpdate() {
		return lastNameUpdate;
	}
	
	public void setLastNameUpdate(long lastNameUpdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastNameUpdate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastNameUpdate + "' (must be >= 0).");
		else
			this.lastNameUpdate = lastNameUpdate;
	}
	
	public int getIdDominatingAlly() {
		return idDominatingAlly;
	}
	
	public void setIdDominatingAlly(int idDominatingAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idDominatingAlly < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idDominatingAlly + "' (must be >= 0).");
		else
			this.idDominatingAlly = idDominatingAlly;
	}
	
	public int getIdSector() {
		return idSector;
	}
	
	public void setIdSector(int idSector) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idSector < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idSector + "' (must be >= 0).");
		else
			this.idSector = idSector;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public void setEnvironment(String environment) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (environment == null)
			throw new IllegalArgumentException("Invalid value: '" +
				environment + "' (must not be null).");
		else if (environment.equals(ENVIRONMENT_NORMAL))
			this.environment = ENVIRONMENT_NORMAL;
		else if (environment.equals(ENVIRONMENT_GLACIAL))
			this.environment = ENVIRONMENT_GLACIAL;
		else if (environment.equals(ENVIRONMENT_DESERT))
			this.environment = ENVIRONMENT_DESERT;
		else if (environment.equals(ENVIRONMENT_ALIEN))
			this.environment = ENVIRONMENT_ALIEN;
		else if (environment.equals(ENVIRONMENT_MINE))
			this.environment = ENVIRONMENT_MINE;
		else if (environment.equals(ENVIRONMENT_EPAVE))
			this.environment = ENVIRONMENT_EPAVE;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + environment + "'.");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
