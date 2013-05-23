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

public class ReportActionData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_ACTION_ABILITIES = "a",
		FIELD_SLOT_STATES = "b",
		FIELD_DAMAGE = "c",
		FIELD_SLOT_INDEX = "d", //$NON-NLS-1$
		FIELD_ACTION_INDEX = "e",
		FIELD_FRONT_SLOTS = "f",
		FIELD_MODIFIERS = "g"; //$NON-NLS-1$
	
	public final static int
		INHIBITED			= 1 << 0;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ReportActionData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getActionAbilitiesCount() /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_ACTION_ABILITIES].length;
	}-*/;
	
	public native final ReportActionAbilityData getActionAbilityAt(int index) /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_ACTION_ABILITIES][index];
	}-*/;
	
	public native final int getDamageCount() /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_DAMAGE].length;
	}-*/;
	
	public native final ReportDamageData getDamageAt(int index) /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_DAMAGE][index];
	}-*/;
	
	public native final int getSlotStatesCount() /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_SLOT_STATES].length;
	}-*/;
	
	public native final ReportSlotStateData getSlotStateAt(int index) /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_SLOT_STATES][index];
	}-*/;

	public native final int getSlotIndex() /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_SLOT_INDEX];
	}-*/;

	public native final int getActionIndex() /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_ACTION_INDEX];
	}-*/;

	public native final boolean isFrontSlot(int slotIndex) /*-{
		return (this[@fr.fg.client.data.ReportActionData::FIELD_FRONT_SLOTS] & (1 << slotIndex)) != 0;
	}-*/;

	public native final int getModifiers() /*-{
		return this[@fr.fg.client.data.ReportActionData::FIELD_MODIFIERS];
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
}
