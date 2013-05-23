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

public class ReportData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_NEBULA = "b", //$NON-NLS-1$
		FIELD_FLEET_1 = "d", //$NON-NLS-1$
		FIELD_FLEET_2 = "e", //$NON-NLS-1$
		FIELD_ACTIONS = "f", //$NON-NLS-1$
		FIELD_ATTACKER_ENVIRONMENT = "g", //$NON-NLS-1$
		FIELD_DEFENDER_ENVIRONMENT = "h", //$NON-NLS-1$
		FIELD_RETREAT = "i", //$NON-NLS-1$
		FIELD_ATTACKER_DAMAGE_FACTOR = "j", //$NON-NLS-1$
		FIELD_DEFENDER_DAMAGE_FACTOR = "k", //$NON-NLS-1$
		FIELD_SOUND_VOLUME = "l", //$NON-NLS-1$
		FIELD_GENERAL_VOLUME = "m", //$NON-NLS-1$
		FIELD_DELUDE = "n", //$NON-NLS-1$
		FIELD_GRAPHICS_QUALITY = "o", //$NON-NLS-1$
		FIELD_DEFENDER_XP_FACTOR = "p", //$NON-NLS-1$
		FIELD_ATTACKER_XP_FACTOR= "q"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ReportData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_ID];
	}-*/;
	
	public native final int getNebula() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_NEBULA];
	}-*/;
	
	public native final int getActionsCount() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_ACTIONS].length;
	}-*/;

	public native final String getAttackerEnvironment() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_ATTACKER_ENVIRONMENT];
	}-*/;

	public native final String getDefenderEnvironment() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_DEFENDER_ENVIRONMENT];
	}-*/;
	
	public native final ReportActionData getActionsAt(int index) /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_ACTIONS][index];
	}-*/;
	
	public native final ReportSlotData getSlotAt(int fleet, int slotIndex) /*-{
		return fleet == 0 ?
			this[@fr.fg.client.data.ReportData::FIELD_FLEET_1][slotIndex] :
			this[@fr.fg.client.data.ReportData::FIELD_FLEET_2][slotIndex];
	}-*/;

	public native final boolean isRetreat() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_RETREAT];
	}-*/;

	public native final double getAttackerDamageFactor() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_ATTACKER_DAMAGE_FACTOR];
	}-*/;

	public native final double getDefenderDamageFactor() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_DEFENDER_DAMAGE_FACTOR];
	}-*/;
	
	public native final boolean hasSoundVolume() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_SOUND_VOLUME] !== undefined;
	}-*/;
	
	public native final int getSoundVolume() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_SOUND_VOLUME];
	}-*/;
	
	public native final boolean hasGeneralVolume() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_GENERAL_VOLUME] !== undefined;
	}-*/;
	
	public native final int getGeneralVolume() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_GENERAL_VOLUME];
	}-*/;
	
	public native final int getGraphicsQuality() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_GRAPHICS_QUALITY];
	}-*/;
	
	public native final boolean isDelude() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_DELUDE];
	}-*/;
	
	public native final int getDefenderBeforeXpFactor() /*-{
			return this[@fr.fg.client.data.ReportData::FIELD_DEFENDER_XP_FACTOR];
	}-*/;
	
	public native final int getAttackerBeforeXpFactor() /*-{
		return this[@fr.fg.client.data.ReportData::FIELD_ATTACKER_XP_FACTOR];
	}-*/;


	// ------------------------------------------------- METHODES PRIVEES -- //
}
