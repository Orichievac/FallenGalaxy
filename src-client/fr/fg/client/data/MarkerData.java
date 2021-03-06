/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier, Nicolas Bosc

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


public class MarkerData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "MarkerData"; //$NON-NLS-1$
	
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
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_X = "b", //$NON-NLS-1$
		FIELD_Y = "c", //$NON-NLS-1$
		FIELD_MESSAGE = "d", //$NON-NLS-1$
		FIELD_OWNER = "e", //$NON-NLS-1$
		FIELD_ALLY = "f", //$NON-NLS-1$
		FIELD_TREATY = "g", //$NON-NLS-1$
		FIELD_CONTRACT = "h", //$NON-NLS-1$
		FIELD_VISIBILITY = "i";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected MarkerData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_ID];
	}-*/;
	
	public native final int getX() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_X];
	}-*/;
	
	public native final int getY() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_Y];
	}-*/;

	public native final String getMessage() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_MESSAGE];
	}-*/;

	public native final String getOwner() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_OWNER];
	}-*/;

	public native final String getAlly() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_ALLY];
	}-*/;
	
	public final boolean hasAlly() {
		return getAlly().length() > 0;
	}
	
	public native final String getTreaty() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_TREATY];
	}-*/;

	public final boolean isContractMarker() {
		return getContract().length() > 0;
	}

	public native final String getContract() /*-{
		return this[@fr.fg.client.data.MarkerData::FIELD_CONTRACT];
	}-*/;
	
	public native final String getVisibility() /*-{
			return this[@fr.fg.client.data.MarkerData::FIELD_VISIBILITY];
		}-*/;

	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}