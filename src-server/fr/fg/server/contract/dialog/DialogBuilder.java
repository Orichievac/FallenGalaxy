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

import java.util.ArrayList;

public class DialogBuilder {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String npcType;
	
	private String currentEntryName;
	
	private ArrayList<DialogEntry> entries;
	
	private ArrayList<DialogOption> options;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DialogBuilder(String npcType) {
		this.npcType = npcType;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public DialogBuilder addEntry(String name) {
		if (currentEntryName != null)
			buildCurrentEntry();
		
		currentEntryName = name;
		return this;
	}
	
	public DialogBuilder addOption(String targetEntry) {
		return addOption(targetEntry, new DialogOptionCondition[0]);
	}
	
	public DialogBuilder addOption(String targetEntry, DialogOptionCondition... conditions) {
		if (currentEntryName == null)
			throw new IllegalStateException("Set entry first.");
		
		if (options == null)
			options = new ArrayList<DialogOption>();
		options.add(new DialogOption(targetEntry, conditions));
		return this;
	}
	
	public Dialog getDialog() {
		if (currentEntryName != null)
			buildCurrentEntry();
		
		Dialog dialog = new Dialog(npcType);
		for (DialogEntry entry : entries)
			dialog.addEntry(entry);
		
		return dialog;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void buildCurrentEntry() {
		if (options.size() == 0)
			throw new IllegalStateException("Entry must contain at least one option.");
		
		DialogOption[] optionsArray = options.toArray(
				new DialogOption[options.size()]);
		
		DialogEntry entry = new DialogEntry(currentEntryName, optionsArray);
		
		if (entries == null)
			entries = new ArrayList<DialogEntry>();
		entries.add(entry);
		
		options = null;
		currentEntryName = null;
	}
}
