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

public class HyperspaceSignatureData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "HyperspaceSignatureData"; //$NON-NLS-1$
	
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
		FIELD_OWNER = "d", //$NON-NLS-1$
		FIELD_ALLY_TAG = "e", //$NON-NLS-1$
		FIELD_TREATY = "f", //$NON-NLS-1$
		FIELD_CLASSES = "g", //$NON-NLS-1$
		FIELD_SHIPS = "h"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected HyperspaceSignatureData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getId() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_ID];
	}-*/;
	
	public final native int getX() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_X];
	}-*/;
	
	public final native int getY() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_Y];
	}-*/;

	public final native String getOwner() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_OWNER];
	}-*/;
	
	public final boolean hasOwner() {
		return getOwner().length() > 0;
	}
	
	public final native String getAllyTag() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_ALLY_TAG];
	}-*/;
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_TREATY];
	}-*/;

	public native final boolean hasShips() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_SHIPS] !== false;
	}-*/;
	
	public native final int getShipsCount() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_SHIPS].length;
	}-*/;
	
	public native final ShipInfoData getShipAt(int index) /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_SHIPS][index];
	}-*/;

	public native final boolean hasClasses() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_CLASSES] !== false;
	}-*/;
	
	public native final int getClassesCount() /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_CLASSES].length;
	}-*/;
	
	public native final int getClassAt(int index) /*-{
		return this[@fr.fg.client.data.HyperspaceSignatureData::FIELD_CLASSES][index];
	}-*/;
	
	public final boolean isAllyHyperspaceSignature() {
		return this.getTreaty().equals(ALLY);
	}
	
	public final boolean isAlliedHyperspaceSignature() {
		return (this.getTreaty().equals(ALLIED) || this.getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL));
	}
	
	public final boolean isEnemyHyperspaceSignature() {
		return this.getTreaty().equals(ENEMY);
	}
	
	public final boolean isPirateHyperspaceSignature() {
		return this.getTreaty().equals(PIRATE);
	}
	
	public final boolean isPlayerHyperspaceSignature() {
		return this.getTreaty().equals(PLAYER);
	}
	
	public final boolean isNeutralHyperspaceSignature() {
		return this.getTreaty().equals(NEUTRAL);
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
