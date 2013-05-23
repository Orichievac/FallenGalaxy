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

public class StructureBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int type;
	private String name;
	private int hull;
	private int x;
	private int y;
	private boolean shared;
	private int shortcut;
	private long idEnergySupplierStructure;
	private int idOwner;
	private int idArea;
	private long idItemContainer;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.type = type;
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
	
	public int getHull() {
		return hull;
	}
	
	public void setHull(int hull) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.hull = hull;
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
	
	public boolean isShared() {
		return shared;
	}
	
	public void setShared(boolean shared) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.shared = shared;
	}
	
	public int getShortcut() {
		return shortcut;
	}
	
	public void setShortcut(int shortcut) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.shortcut = shortcut;
	}
	
	public long getIdEnergySupplierStructure() {
		return idEnergySupplierStructure;
	}
	
	public void setIdEnergySupplierStructure(long idEnergySupplierStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idEnergySupplierStructure = idEnergySupplierStructure;
	}
	
	public int getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(int idOwner) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idOwner = idOwner;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idArea = idArea;
	}
	
	public long getIdItemContainer() {
		return idItemContainer;
	}
	
	public void setIdItemContainer(long idItemContainer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idItemContainer = idItemContainer;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
