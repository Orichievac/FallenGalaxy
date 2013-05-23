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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Session {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private HttpServletRequest request;
	
	private HttpSession session;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Session(HttpServletRequest request) {
		this.request = request;
		this.session = request.getSession();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void create() {
		session = request.getSession();
	}
	
	public void destroy() {
		session.invalidate();
	}
	
	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}
	
	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}
	
	public String getForwardedHeader(){
		
		return request.getHeader("X-Forwarded-For");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
