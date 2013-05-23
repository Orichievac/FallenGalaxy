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

public class TreatyData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
	PLAYER = "player",
	ENEMY = "enemy",
	ALLY = "ally",
	DEFENSIVE = "defensive",
	TOTAL = "total",
	ALLIED = "allied",
	NEUTRAL = "neutral",
	PIRATE = "pirate";
	
	public final static String
		FIELD_TARGET = "a", //$NON-NLS-1$
		FIELD_DATE = "b", //$NON-NLS-1$
		FIELD_SOURCE = "c", //$NON-NLS-1$
		FIELD_TYPE = "d"; //$NON-NLS-1$
		
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected TreatyData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final String getTarget() /*-{
		return this[@fr.fg.client.data.TreatyData::FIELD_TARGET];
	}-*/;

	public native final double getDate() /*-{
		return this[@fr.fg.client.data.TreatyData::FIELD_DATE];
	}-*/;

	public native final int getSource() /*-{
		return this[@fr.fg.client.data.TreatyData::FIELD_SOURCE];
	}-*/;
	
	public native final String getType() /*-{
		return this[@fr.fg.client.data.TreatyData::FIELD_TYPE];
	}-*/;
	
	public static String getPact(String pact) {
		return (pact.equals(DEFENSIVE) || pact.equals(TOTAL) )? ALLIED : pact;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
