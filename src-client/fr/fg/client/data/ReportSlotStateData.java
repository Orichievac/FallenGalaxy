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

public class ReportSlotStateData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_SLOT_INDEX = "a",
		FIELD_DAMAGE_MODIFIER = "b",
		FIELD_PROTECTION_MODIFIER = "c",
		FIELD_HULL_MODIFIER = "d"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ReportSlotStateData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getSlotIndex() /*-{
		return this[@fr.fg.client.data.ReportSlotStateData::FIELD_SLOT_INDEX];
	}-*/;
	
	public native final double getDamageModifier() /*-{
		return this[@fr.fg.client.data.ReportSlotStateData::FIELD_DAMAGE_MODIFIER];
	}-*/;

	public native final int getProtectionModifier() /*-{
		return this[@fr.fg.client.data.ReportSlotStateData::FIELD_PROTECTION_MODIFIER];
	}-*/;

	public native final double getHullModifier() /*-{
		return this[@fr.fg.client.data.ReportSlotStateData::FIELD_HULL_MODIFIER];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
