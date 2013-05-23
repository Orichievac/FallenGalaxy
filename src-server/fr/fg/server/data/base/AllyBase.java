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

public class AllyBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		ORGANIZATION_TYRANNY = "tyranny",
		ORGANIZATION_WARMONGER = "warmonger",
		ORGANIZATION_DEMOCRACY = "democracy",
		ORGANIZATION_OLIGARCHY = "oligarchy",
		ORGANIZATION_ANARCHY = "anarchy";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String name;
	private String tag;
	private boolean ai;
	private int color;
	private String organization;
	private String description;
	private long birthdate;
	private long points;
	private int rightAccept;
	private int rightManageNews;
	private int rightManageStations;
	private int rightManageDiplomacy;
	private int rightManageDescription;
	private int rightManageContracts;
	private int idCreator;
	
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
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (tag == null)
			throw new IllegalArgumentException("Invalid value: '" +
				tag + "' (must not be null).");
		else
			this.tag = tag;
	}
	
	public boolean isAi() {
		return ai;
	}
	
	public void setAi(boolean ai) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.ai = ai;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (color < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				color + "' (must be >= 0).");
		else
			this.color = color;
	}
	
	public String getOrganization() {
		return organization;
	}
	
	public void setOrganization(String organization) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (organization == null)
			throw new IllegalArgumentException("Invalid value: '" +
				organization + "' (must not be null).");
		else if (organization.equals(ORGANIZATION_TYRANNY))
			this.organization = ORGANIZATION_TYRANNY;
		else if (organization.equals(ORGANIZATION_WARMONGER))
			this.organization = ORGANIZATION_WARMONGER;
		else if (organization.equals(ORGANIZATION_DEMOCRACY))
			this.organization = ORGANIZATION_DEMOCRACY;
		else if (organization.equals(ORGANIZATION_OLIGARCHY))
			this.organization = ORGANIZATION_OLIGARCHY;
		else if (organization.equals(ORGANIZATION_ANARCHY))
			this.organization = ORGANIZATION_ANARCHY;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + organization + "'.");
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (description == null)
			throw new IllegalArgumentException("Invalid value: '" +
				description + "' (must not be null).");
		else
			this.description = description;
	}
	
	public long getBirthdate() {
		return birthdate;
	}
	
	public void setBirthdate(long birthdate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (birthdate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				birthdate + "' (must be >= 0).");
		else
			this.birthdate = birthdate;
	}
	
	public long getPoints() {
		return points;
	}
	
	public void setPoints(long points) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (points < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				points + "' (must be >= 0).");
		else
			this.points = points;
	}
	
	public int getRightAccept() {
		return rightAccept;
	}
	
	public void setRightAccept(int rightAccept) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rightAccept < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rightAccept + "' (must be >= 0).");
		else
			this.rightAccept = rightAccept;
	}
	
	public int getRightManageNews() {
		return rightManageNews;
	}
	
	public void setRightManageNews(int rightManageNews) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rightManageNews < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rightManageNews + "' (must be >= 0).");
		else
			this.rightManageNews = rightManageNews;
	}
	
	public int getRightManageStations() {
		return rightManageStations;
	}
	
	public void setRightManageStations(int rightManageStations) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rightManageStations < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rightManageStations + "' (must be >= 0).");
		else
			this.rightManageStations = rightManageStations;
	}
	
	public int getRightManageDiplomacy() {
		return rightManageDiplomacy;
	}
	
	public void setRightManageDiplomacy(int rightManageDiplomacy) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rightManageDiplomacy < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rightManageDiplomacy + "' (must be >= 0).");
		else
			this.rightManageDiplomacy = rightManageDiplomacy;
	}
	
	public int getRightManageDescription() {
		return rightManageDescription;
	}
	
	public void setRightManageDescription(int rightManageDescription) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rightManageDescription < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rightManageDescription + "' (must be >= 0).");
		else
			this.rightManageDescription = rightManageDescription;
	}
	
	public int getRightManageContracts() {
		return rightManageContracts;
	}
	
	public void setRightManageContracts(int rightManageContracts) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rightManageContracts < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rightManageContracts + "' (must be >= 0).");
		else
			this.rightManageContracts = rightManageContracts;
	}
	
	public int getIdCreator() {
		return idCreator;
	}
	
	public void setIdCreator(int idCreator) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idCreator < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idCreator + "' (must be >= 0).");
		else
			this.idCreator = idCreator;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
