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

public class StellarObjectBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_GATE = "gate",
		TYPE_TRADECENTER = "tradecenter",
		TYPE_PIRATES = "pirates",
		TYPE_BANK = "bank",
		TYPE_LOTTERY = "lottery",
		TYPE_ASTEROID = "asteroid",
		TYPE_ASTEROID_DENSE = "asteroid_dense",
		TYPE_ASTEROID_LOW_TITANIUM = "asteroid_low_titanium",
		TYPE_ASTEROID_LOW_CRYSTAL = "asteroid_low_crystal",
		TYPE_ASTEROID_LOW_ANDIUM = "asteroid_low_andium",
		TYPE_ASTEROID_AVG_TITANIUM = "asteroid_avg_titanium",
		TYPE_ASTEROID_AVG_CRYSTAL = "asteroid_avg_crystal",
		TYPE_ASTEROID_AVG_ANDIUM = "asteroid_avg_andium",
		TYPE_ASTEROID_HIGH_TITANIUM = "asteroid_high_titanium",
		TYPE_ASTEROID_HIGH_CRYSTAL = "asteroid_high_crystal",
		TYPE_ASTEROID_HIGH_ANDIUM = "asteroid_high_andium",
		TYPE_BLACKHOLE = "blackhole",
		TYPE_DOODAD = "doodad",
		TYPE_GRAVITY_WELL = "gravity_well",
		TYPE_ASTEROID_VEIN_TITANIUM = "asteroid_vein_titanium",
		TYPE_ASTEROID_VEIN_CRYSTAL = "asteroid_vein_crystal",
		TYPE_ASTEROID_VEIN_ANDIUM = "asteroid_vein_andium",
		TYPE_ASTEROID_LOWC_TITANIUM = "asteroid_lowc_titanium",
		TYPE_ASTEROID_LOWC_CRYSTAL = "asteroid_lowc_crystal",
		TYPE_ASTEROID_LOWC_ANDIUM = "asteroid_lowc_andium",
		TYPE_ASTEROID_MEDIUMC_TITANIUM = "asteroid_mediumc_titanium",
		TYPE_ASTEROID_MEDIUMC_CRYSTAL = "asteroid_mediumc_crystal",
		TYPE_ASTEROID_MEDIUMC_ANDIUM = "asteroid_mediumc_andium",
		TYPE_ASTEROID_IMPORTANT_TITANIUM = "asteroid_important_titanium",
		TYPE_ASTEROID_IMPORTANT_CRYSTAL = "asteroid_important_crystal",
		TYPE_ASTEROID_IMPORTANT_ANDIUM = "asteroid_important_andium",
		TYPE_ASTEROID_ABONDANT_TITANIUM = "asteroid_abondant_titanium",
		TYPE_ASTEROID_ABONDANT_CRYSTAL = "asteroid_abondant_crystal",
		TYPE_ASTEROID_ABONDANT_ANDIUM = "asteroid_abondant_andium",
		TYPE_ASTEROID_PURE_TITANIUM = "asteroid_pure_titanium",
		TYPE_ASTEROID_PURE_CRYSTAL = "asteroid_pure_crystal",
		TYPE_ASTEROID_PURE_ANDIUM = "asteroid_pure_andium",
		TYPE_ASTEROID_CONCENTRATE_TITANIUM = "asteroid_concentrate_titanium",
		TYPE_ASTEROID_CONCENTRATE_CRYSTAL = "asteroid_concentrate_crystal",
		TYPE_ASTEROID_CONCENTRATE_ANDIUM = "asteroid_concentrate_andium";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int x;
	private int y;
	private String type;
	private int variant;
	private int idArea;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (type == null)
			throw new IllegalArgumentException("Invalid value: '" +
				type + "' (must not be null).");
		else if (type.equals(TYPE_GATE))
			this.type = TYPE_GATE;
		else if (type.equals(TYPE_TRADECENTER))
			this.type = TYPE_TRADECENTER;
		else if (type.equals(TYPE_PIRATES))
			this.type = TYPE_PIRATES;
		else if (type.equals(TYPE_BANK))
			this.type = TYPE_BANK;
		else if (type.equals(TYPE_LOTTERY))
			this.type = TYPE_LOTTERY;
		else if (type.equals(TYPE_ASTEROID))
			this.type = TYPE_ASTEROID;
		else if (type.equals(TYPE_ASTEROID_DENSE))
			this.type = TYPE_ASTEROID_DENSE;
		else if (type.equals(TYPE_ASTEROID_LOW_TITANIUM))
			this.type = TYPE_ASTEROID_LOW_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_LOW_CRYSTAL))
			this.type = TYPE_ASTEROID_LOW_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_LOW_ANDIUM))
			this.type = TYPE_ASTEROID_LOW_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_AVG_TITANIUM))
			this.type = TYPE_ASTEROID_AVG_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_AVG_CRYSTAL))
			this.type = TYPE_ASTEROID_AVG_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_AVG_ANDIUM))
			this.type = TYPE_ASTEROID_AVG_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_HIGH_TITANIUM))
			this.type = TYPE_ASTEROID_HIGH_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_HIGH_CRYSTAL))
			this.type = TYPE_ASTEROID_HIGH_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_HIGH_ANDIUM))
			this.type = TYPE_ASTEROID_HIGH_ANDIUM;
		else if (type.equals(TYPE_BLACKHOLE))
			this.type = TYPE_BLACKHOLE;
		else if (type.equals(TYPE_DOODAD))
			this.type = TYPE_DOODAD;
		else if (type.equals(TYPE_GRAVITY_WELL))
			this.type = TYPE_GRAVITY_WELL;
		else if (type.equals(TYPE_ASTEROID_VEIN_TITANIUM))
			this.type = TYPE_ASTEROID_VEIN_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_VEIN_CRYSTAL))
			this.type = TYPE_ASTEROID_VEIN_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_VEIN_ANDIUM))
			this.type = TYPE_ASTEROID_VEIN_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_LOWC_TITANIUM))
			this.type = TYPE_ASTEROID_LOWC_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_LOWC_CRYSTAL))
			this.type = TYPE_ASTEROID_LOWC_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_LOWC_ANDIUM))
			this.type = TYPE_ASTEROID_LOWC_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_MEDIUMC_TITANIUM))
			this.type = TYPE_ASTEROID_MEDIUMC_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_MEDIUMC_CRYSTAL))
			this.type = TYPE_ASTEROID_MEDIUMC_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_MEDIUMC_ANDIUM))
			this.type = TYPE_ASTEROID_MEDIUMC_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_IMPORTANT_TITANIUM))
			this.type = TYPE_ASTEROID_IMPORTANT_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_IMPORTANT_CRYSTAL))
			this.type = TYPE_ASTEROID_IMPORTANT_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_IMPORTANT_ANDIUM))
			this.type = TYPE_ASTEROID_IMPORTANT_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_ABONDANT_TITANIUM))
			this.type = TYPE_ASTEROID_ABONDANT_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_ABONDANT_CRYSTAL))
			this.type = TYPE_ASTEROID_ABONDANT_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_ABONDANT_ANDIUM))
			this.type = TYPE_ASTEROID_ABONDANT_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_PURE_TITANIUM))
			this.type = TYPE_ASTEROID_PURE_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_PURE_CRYSTAL))
			this.type = TYPE_ASTEROID_PURE_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_PURE_ANDIUM))
			this.type = TYPE_ASTEROID_PURE_ANDIUM;
		else if (type.equals(TYPE_ASTEROID_CONCENTRATE_TITANIUM))
			this.type = TYPE_ASTEROID_CONCENTRATE_TITANIUM;
		else if (type.equals(TYPE_ASTEROID_CONCENTRATE_CRYSTAL))
			this.type = TYPE_ASTEROID_CONCENTRATE_CRYSTAL;
		else if (type.equals(TYPE_ASTEROID_CONCENTRATE_ANDIUM))
			this.type = TYPE_ASTEROID_CONCENTRATE_ANDIUM;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + type + "'.");
	}
	
	public int getVariant() {
		return variant;
	}
	
	public void setVariant(int variant) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.variant = variant;
	}
	
	public int getIdArea() {
		return idArea;
	}
	
	public void setIdArea(int idArea) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idArea = idArea;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
