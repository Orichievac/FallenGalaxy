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

public class DialogOption {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String targetEntry;
	private DialogOptionCondition[] conditions;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DialogOption(String targetEntry, DialogOptionCondition... conditions) {
		this.targetEntry = targetEntry;
		this.conditions = conditions;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getTargetEntry() {
		return targetEntry;
	}
	
	public boolean isValidOption(Contract contract, Fleet source, Fleet target) {
		for (DialogOptionCondition condition : conditions)
			if (!condition.isValid(contract, source, target))
				return false;
		return true;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
