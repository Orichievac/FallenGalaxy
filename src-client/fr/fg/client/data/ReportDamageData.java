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

public class ReportDamageData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_AMOUNT = "a", //$NON-NLS-1$
		FIELD_TARGET_SLOT = "b", //$NON-NLS-1$
		FIELD_MODIFIERS = "c", //$NON-NLS-1$
		FIELD_KILLS = "d", //$NON-NLS-1$
		FIELD_HULL_DAMAGE = "e", //$NON-NLS-1$
		FIELD_STEALED_RESOURCES = "f"; //$NON-NLS-1$
	
	public final static int
		DODGED			= 1 << 0,
		CRITICAL_HIT	= 1 << 2,
		PHASED			= 1 << 3,
		SUBLIMATION		= 1 << 4,
		PARTICLES		= 1 << 5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ReportDamageData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public native final int getAmount() /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_AMOUNT];
	}-*/;

	public native final int getKills() /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_KILLS];
	}-*/;

	public native final int getHullDamage() /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_HULL_DAMAGE];
	}-*/;
	
	public native final int getTargetSlot() /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_TARGET_SLOT];
	}-*/;
	
	public native final int getModifiers() /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_MODIFIERS];
	}-*/;
	
	public native final int getStealedResourcesCount() /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_STEALED_RESOURCES].length;
	}-*/;
	
	public native final double getStealedResourceAt(int index) /*-{
		return this[@fr.fg.client.data.ReportDamageData::FIELD_STEALED_RESOURCES][index];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
