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

public class LadderData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_PLAYERS = "a", //$NON-NLS-1$
		FIELD_SELF_DATA = "b", //$NON-NLS-1$
		FIELD_RANGE = "c", //$NON-NLS-1$
		FIELD_ALLIES = "d", //$NON-NLS-1$
		FIELD_LIFESPAN = "e"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected LadderData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native boolean isPlayerLadder() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_PLAYERS] !== undefined;
	}-*/;
	
	public final native LadderPlayerData getPlayerAt(int index) /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_PLAYERS][index];
	}-*/;
	
	public final native int getPlayersCount() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_PLAYERS].length;
	}-*/;
	
	public final native LadderAllyData getAllyAt(int index) /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_ALLIES][index];
	}-*/;
	
	public final native int getAlliesCount() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_ALLIES].length;
	}-*/;
	
	public final native boolean hasSelfData() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_SELF_DATA] !== undefined;
	}-*/;
	
	public final native LadderPlayerData getSelfPlayerData() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_SELF_DATA];
	}-*/;
	
	public final native boolean hasSelfAlly() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_SELF_DATA] !== false;
	}-*/;
	
	public final native LadderAllyData getSelfAllyData() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_SELF_DATA];
	}-*/;
	
	public final native int getRange() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_RANGE];
	}-*/;

	public final native int getLifespan() /*-{
		return this[@fr.fg.client.data.LadderData::FIELD_LIFESPAN];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
