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

public class PlayerGeneratorData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_AREA = "b", //$NON-NLS-1$
		FIELD_X = "c", //$NON-NLS-1$
		FIELD_Y = "d", //$NON-NLS-1$
		FIELD_NAME = "e", //$NON-NLS-1$
		FIELD_LEVEL = "f", //$NON-NLS-1$
		FIELD_USED_ENERGY = "g", //$NON-NLS-1$
		FIELD_MAX_ENERGY = "h", //$NON-NLS-1$
		FIELD_HULL = "i", //$NON-NLS-1$
		FIELD_MAX_HULL = "j", //$NON-NLS-1$
		FIELD_SHORTCUT = "k"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	protected PlayerGeneratorData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public native final int getId() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_ID];
	}-*/;
	
	public native final PlayerAreaData getArea() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_AREA];
	}-*/;
	
	public native final int getX() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_X];
	}-*/;

	public native final int getY() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_Y];
	}-*/;

	public native final int getHull() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_HULL];
	}-*/;

	public native final int getMaxHull() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_MAX_HULL];
	}-*/;

	public native final int getUsedEnergy() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_USED_ENERGY];
	}-*/;

	public native final int getMaxEnergy() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_MAX_ENERGY];
	}-*/;

	public native final int getLevel() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_LEVEL];
	}-*/;
	
	public native final int getShortcut() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_SHORTCUT];
	}-*/;
	
	public native final String getName() /*-{
		return this[@fr.fg.client.data.PlayerGeneratorData::FIELD_NAME];
	}-*/;
	
	public final int getGraphics() {
		return (getX() + getY()) % 3;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
