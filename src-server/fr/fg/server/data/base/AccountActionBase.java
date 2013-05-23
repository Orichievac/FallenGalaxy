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

public class AccountActionBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		ACTION_REGISTERED = "registered",
		ACTION_CLOSED = "closed",
		ACTION_IDLE = "idle";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String login;
	private String action;
	private String email;
	private long birthday;
	private long date;
	private int ip;
	private long playedTime;
	private String reason;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (login == null)
			throw new IllegalArgumentException("Invalid value: '" +
				login + "' (must not be null).");
		else
			this.login = login;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (action == null)
			throw new IllegalArgumentException("Invalid value: '" +
				action + "' (must not be null).");
		else if (action.equals(ACTION_REGISTERED))
			this.action = ACTION_REGISTERED;
		else if (action.equals(ACTION_CLOSED))
			this.action = ACTION_CLOSED;
		else if (action.equals(ACTION_IDLE))
			this.action = ACTION_IDLE;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + action + "'.");
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (email == null)
			throw new IllegalArgumentException("Invalid value: '" +
				email + "' (must not be null).");
		else
			this.email = email;
	}
	
	public long getBirthday() {
		return birthday;
	}
	
	public void setBirthday(long birthday) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.birthday = birthday;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public int getIp() {
		return ip;
	}
	
	public void setIp(int ip) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.ip = ip;
	}
	
	public long getPlayedTime() {
		return playedTime;
	}
	
	public void setPlayedTime(long playedTime) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.playedTime = playedTime;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (reason == null)
			throw new IllegalArgumentException("Invalid value: '" +
				reason + "' (must not be null).");
		else
			this.reason = reason;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
