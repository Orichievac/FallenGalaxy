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

public class ContractBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TARGET_PLAYER = "player",
		TARGET_ALLY = "ally",
		TARGET_ALL = "all";
	
	public final static String
		STATE_WAITING = "waiting",
		STATE_RUNNING = "running",
		STATE_FINALIZING = "finalizing";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private String type;
	private String variant;
	private String target;
	private int maxAttendees;
	private String state;
	private int step;
	private long difficulty;
	private long stateDate;
	
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (type == null)
			throw new IllegalArgumentException("Invalid value: '" +
				type + "' (must not be null).");
		else
			this.type = type;
	}
	
	public String getVariant() {
		return variant;
	}
	
	public void setVariant(String variant) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (variant == null)
			throw new IllegalArgumentException("Invalid value: '" +
				variant + "' (must not be null).");
		else
			this.variant = variant;
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
		else if (target.equals(TARGET_PLAYER))
			this.target = TARGET_PLAYER;
		else if (target.equals(TARGET_ALLY))
			this.target = TARGET_ALLY;
		else if (target.equals(TARGET_ALL))
			this.target = TARGET_ALL;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + target + "'.");
	}
	
	public int getMaxAttendees() {
		return maxAttendees;
	}
	
	public void setMaxAttendees(int maxAttendees) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.maxAttendees = maxAttendees;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (state == null)
			throw new IllegalArgumentException("Invalid value: '" +
				state + "' (must not be null).");
		else if (state.equals(STATE_WAITING))
			this.state = STATE_WAITING;
		else if (state.equals(STATE_RUNNING))
			this.state = STATE_RUNNING;
		else if (state.equals(STATE_FINALIZING))
			this.state = STATE_FINALIZING;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + state + "'.");
	}
	
	public int getStep() {
		return step;
	}
	
	public void setStep(int step) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.step = step;
	}
	
	public long getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(long difficulty) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.difficulty = difficulty;
	}
	
	public long getStateDate() {
		return stateDate;
	}
	
	public void setStateDate(long stateDate) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.stateDate = stateDate;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
