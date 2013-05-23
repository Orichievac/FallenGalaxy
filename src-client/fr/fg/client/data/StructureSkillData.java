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

package fr.fg.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class StructureSkillData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int TYPE_STASIS = 1;

	public final static int STASIS_RELOAD = 24 * 3600;
	
	public final static int STASIS_RANGE = 25;
	
	public final static String
		FIELD_TYPE = "a",
		FIELD_LAST_USE = "b",
		FIELD_RELOAD = "c";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected StructureSkillData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getType() /*-{
		return this[@fr.fg.client.data.StructureSkillData::FIELD_TYPE];
	}-*/;
	
	public final native double getLastUseTime() /*-{
		return this[@fr.fg.client.data.StructureSkillData::FIELD_LAST_USE];
	}-*/;
	
	public final native double getReloadRemainingTime() /*-{
		return this[@fr.fg.client.data.StructureSkillData::FIELD_RELOAD];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
