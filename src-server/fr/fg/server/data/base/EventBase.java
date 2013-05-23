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

public class EventBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TARGET_ALLY = "ally",
		TARGET_PLAYER = "player";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private int type;
	private String arg1;
	private String arg2;
	private String arg3;
	private String arg4;
	private long date;
	private int idArea;
	private int x;
	private int y;
	private String target;
	private int idTarget;
	
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
	
	public String getArg1() {
		return arg1;
	}
	
	public void setArg1(String arg1) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (arg1 == null)
			throw new IllegalArgumentException("Invalid value: '" +
				arg1 + "' (must not be null).");
		else
			this.arg1 = arg1;
	}
	
	public String getArg2() {
		return arg2;
	}
	
	public void setArg2(String arg2) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (arg2 == null)
			throw new IllegalArgumentException("Invalid value: '" +
				arg2 + "' (must not be null).");
		else
			this.arg2 = arg2;
	}
	
	public String getArg3() {
		return arg3;
	}
	
	public void setArg3(String arg3) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (arg3 == null)
			throw new IllegalArgumentException("Invalid value: '" +
				arg3 + "' (must not be null).");
		else
			this.arg3 = arg3;
	}
	
	public String getArg4() {
		return arg4;
	}
	
	public void setArg4(String arg4) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (arg4 == null)
			throw new IllegalArgumentException("Invalid value: '" +
				arg4 + "' (must not be null).");
		else
			this.arg4 = arg4;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
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
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (target == null)
			throw new IllegalArgumentException("Invalid value: '" +
				target + "' (must not be null).");
		else if (target.equals(TARGET_ALLY))
			this.target = TARGET_ALLY;
		else if (target.equals(TARGET_PLAYER))
			this.target = TARGET_PLAYER;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + target + "'.");
	}
	
	public int getIdTarget() {
		return idTarget;
	}
	
	public void setIdTarget(int idTarget) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idTarget < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idTarget + "' (must be >= 0).");
		else
			this.idTarget = idTarget;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
