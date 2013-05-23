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

public class TacticData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_HASH = "a",
		FIELD_DATE = "b",
		FIELD_SLOT_0 ="c",
		FIELD_SLOT_1 ="d",
		FIELD_SLOT_2 ="e",
		FIELD_SLOT_3 ="f",
		FIELD_SLOT_4 ="g",
		FIELD_SLOT_0_COUNT = "h",
		FIELD_SLOT_1_COUNT = "i",
		FIELD_SLOT_2_COUNT = "j",
		FIELD_SLOT_3_COUNT = "k",
		FIELD_SLOT_4_COUNT = "l",
		FIELD_NAME = "m";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected TacticData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.TacticData::FIELD_NAME];
	}-*/;
	
	public final native String getHash() /*-{
		return this[@fr.fg.client.data.TacticData::FIELD_HASH];
	}-*/;
	
	public final native double getDate() /*-{
		return this[@fr.fg.client.data.TacticData::FIELD_DATE];
	}-*/;
	
	public final native int getSlotId0() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_0];
}-*/;
	
	public final native int getSlotId1() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_1];
}-*/;
	
	public final native int getSlotId2() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_2];
}-*/;
	
	public final native int getSlotId3() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_3];
}-*/;
	
	public final native int getSlotId4() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_4];
}-*/;
	
	public final native int getSlotId0Count() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_0_COUNT];
}-*/;
	
	public final native int getSlotId1Count() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_1_COUNT];
}-*/;
	
	public final native int getSlotId2Count() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_2_COUNT];
}-*/;
	
	public final native int getSlotId3Count() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_3_COUNT];
}-*/;
	
	public final native int getSlotId4Count() /*-{
	return this[@fr.fg.client.data.TacticData::FIELD_SLOT_4_COUNT];
}-*/;
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
