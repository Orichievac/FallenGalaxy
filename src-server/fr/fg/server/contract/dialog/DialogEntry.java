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

import fr.fg.server.data.Contract;
import fr.fg.server.data.Fleet;

public class DialogEntry {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIRST_ENTRY		= "init",
		END_OF_DIALOG	= "EOD";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String name;
	private DialogOption[] options;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DialogEntry(String name, DialogOption... options) {
		this.name = name;
		this.options = options;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getName() {
		return name;
	}
	
	public DialogOption[] getOptions() {
		return options;
	}
	
	public DialogOption getOption(int index) {
		return options[index];
	}
	
	public boolean isValidOption(int index, Contract contract, Fleet source, Fleet target) {
		if (index < 0 || index >= options.length)
			return false;
		
		return options[index].isValidOption(contract, source, target);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
