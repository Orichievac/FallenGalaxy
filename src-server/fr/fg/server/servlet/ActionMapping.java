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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActionMapping {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Map<String, Action> actionsMapping;
	
	private String basePackage;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ActionMapping() {
		this.actionsMapping = Collections.synchronizedMap(
				new HashMap<String, Action>());
		this.basePackage = "";
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addMapping(String path, Action action) {
		actionsMapping.put(path, action);
	}
	
	public Map<String, Action> getAllMappings() {
		return actionsMapping;
	}
	
	public Action getMapping(String path) {
		return actionsMapping.get(path);
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
