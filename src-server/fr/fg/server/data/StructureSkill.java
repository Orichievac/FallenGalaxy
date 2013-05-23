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

package fr.fg.server.data;

import fr.fg.server.data.base.StructureSkillBase;
import fr.fg.server.util.Utilities;

public class StructureSkill extends StructureSkillBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int TYPE_STASIS = 1;
	
	public final static int STASIS_RELOAD = 24 * 3600;
	
	public final static int STASIS_LENGTH = 4 * 3600;
	
	public final static int STASIS_RANGE = 25;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StructureSkill() {
		// Nécessaire pour la construction par réflection
	}
	
	public StructureSkill(long idStructure, int type, long reload) {
		setIdStructure(idStructure);
		setType(type);
		setLastUse(Utilities.now());
		setReload(reload);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Structure getStructure() {
		return DataAccess.getStructureById(getIdStructure());
	}
	
	public int getReloadLength() {
		switch (getType()) {
		case TYPE_STASIS:
			return (int) Math.round(Math.pow(.88, getStructure(
				).getModuleLevel(StructureModule.TYPE_SKILL_RELOAD)) *
				StructureSkill.STASIS_RELOAD);
		default:
			throw new IllegalStateException("Unknown skill: '" + getType() + "'.");
		}
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
