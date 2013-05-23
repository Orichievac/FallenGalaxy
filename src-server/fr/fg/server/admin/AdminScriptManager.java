/*
Copyright 2010 Thierry Chevalier

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

package fr.fg.server.admin;

import java.util.ArrayList;
import java.util.HashMap;

import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class AdminScriptManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	private final static AdminScriptManager instance = new AdminScriptManager();
	
	HashMap<String, ScriptModel> scriptModels;
	ArrayList<String> scriptsName;
	String packageName;
	String prefix;
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	private AdminScriptManager() {
		scriptModels = new HashMap<String, ScriptModel>();
		scriptsName = new ArrayList<String>();
		// Charge les scripts disponibles
		packageName = getClass().getPackage().getName();
		prefix = new String(packageName + ".impl");
		Class<?>[] classes = Utilities.getClasses(prefix);
		for (Class<?> c : classes) {
			try {
				scriptModels.put(c.getName(), (ScriptModel) c.newInstance());
				scriptsName.add(new String(c.getName()).substring(prefix.length()+1));
				LoggingSystem.getServerLogger().trace(
						"Script registered: '" + c.getName() + "'.");
			} catch (InstantiationException e) {
				// Ignoré
			} catch (Exception e) {
				LoggingSystem.getServerLogger().error("Could not " +
						"load script: '" + c.getName() + "'.", e);
			}
		}
	}
	// --------------------------------------------------------- METHODES -- //
	public ScriptModel getScriptByName(String name) {
		return scriptModels.get(prefix+"."+name);
	}
	
	public ArrayList<String> getAllScriptsName() {
		return scriptsName;
	}
	
	public final static AdminScriptManager getInstance() {
		return instance;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
