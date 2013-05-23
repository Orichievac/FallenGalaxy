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

package fr.fg.server.servlet;


public class ActionForward {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		SUCCESS = 0,
		ERROR = 1,
		CONFIRM_PASSWORD = 2,
		WRONG_PASSWORD = 3,
		DISCONNECTED = 4;
	
	public final static String[] TYPES = {
		"success", "error", "confirmPassword", "wrongPassword", "disconnected"
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private Object data;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private ActionForward(int type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}

	public static ActionForward success() {
		return new ActionForward(SUCCESS, "\"OK\"");
	}
	
	public static ActionForward success(Object data) {
		return new ActionForward(SUCCESS, data);
	}
	
	public static ActionForward error(String message) {
		return new ActionForward(ERROR, "\"" + message + "\"");
	}
	
	public static ActionForward confirmPassword() {
		return new ActionForward(CONFIRM_PASSWORD, "\"OK\"");
	}
	
	public static ActionForward wrongPassword() {
		return new ActionForward(WRONG_PASSWORD, "\"OK\"");
	}
	
	public static ActionForward disconnected() {
		return new ActionForward(DISCONNECTED, "\"OK\"");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
