/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

public class ItemInfoData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_COUNT = "b", //$NON-NLS-1$
		FIELD_TYPE = "c", //$NON-NLS-1$
		FIELD_STRUCTURE_TYPE = "d", //$NON-NLS-1$
		FIELD_STRUCTURE_LEVEL = "e"; //$NON-NLS-1$
	
	public final static int
		TYPE_NONE = 0,
		TYPE_RESOURCE = 1,
		TYPE_STUFF = 2,
		TYPE_STRUCTURE = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ItemInfoData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.ItemInfoData::FIELD_ID];
	}-*/;

	public native final double getCount() /*-{
		return this[@fr.fg.client.data.ItemInfoData::FIELD_COUNT];
	}-*/;
	
	public native final int getType() /*-{
		return this[@fr.fg.client.data.ItemInfoData::FIELD_TYPE];
	}-*/;

	public native final int getStructureType() /*-{
		return this[@fr.fg.client.data.ItemInfoData::FIELD_STRUCTURE_TYPE] === undefined ?
			0 : this[@fr.fg.client.data.ItemInfoData::FIELD_STRUCTURE_TYPE];
	}-*/;

	public native final int getStructureLevel() /*-{
		return this[@fr.fg.client.data.ItemInfoData::FIELD_STRUCTURE_LEVEL] === undefined ?
			0 : this[@fr.fg.client.data.ItemInfoData::FIELD_STRUCTURE_LEVEL];
	}-*/;
	
	public final long getWeight() {
		return getWeight(getType(), getId(), getStructureType(), getStructureLevel());
	}
	
	public static long getWeight(int type, long id, int structureType, int structureLevel) {
		switch (type) {
		case ItemInfoData.TYPE_NONE:
			return 0;
		case ItemInfoData.TYPE_RESOURCE:
			return 1;
		case ItemInfoData.TYPE_STUFF:
			return 1;
		case ItemInfoData.TYPE_STRUCTURE:
			return StructureData.getWeight(structureType, structureLevel);
		default:
			throw new IllegalStateException("Invalid type: '" + type + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
