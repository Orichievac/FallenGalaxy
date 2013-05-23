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

package fr.fg.server.contract.dialog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Dialog {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String npcType;
	
	private Map<String, DialogEntry> entries;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Dialog(String npcType) {
		this.npcType = npcType;
		this.entries = Collections.synchronizedMap(
			new HashMap<String, DialogEntry>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getNpcType() {
		return npcType;
	}
	
	public void addEntry(DialogEntry entry) {
		entries.put(entry.getName(), entry);
	}
	
	public DialogEntry getEntry(String name) {
		return entries.get(name);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
