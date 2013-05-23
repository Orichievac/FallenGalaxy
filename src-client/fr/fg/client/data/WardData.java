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

public class WardData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "WardData"; //$NON-NLS-1$
	
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
		FIELD_TYPE = "d", //$NON-NLS-1$
		FIELD_OWNER = "e", //$NON-NLS-1$
		FIELD_ALLY_TAG = "f", //$NON-NLS-1$
		FIELD_TREATY = "g", //$NON-NLS-1$
		FIELD_LIFESPAN = "h", //$NON-NLS-1$
		FIELD_LINE_OF_SIGHT = "i", //$NON-NLS-1$
		FIELD_POWER = "j"; //$NON-NLS-1$
	
	public final static String
		TYPE_OBSERVER = "observer",
		TYPE_OBSERVER_INVISIBLE = "observer_invisible",
		TYPE_SENTRY = "sentry",
		TYPE_SENTRY_INVISIBLE = "sentry_invisible",
		TYPE_STUN = "stun",
		TYPE_STUN_INVISIBLE = "stun_invisible",
		TYPE_MINE = "mine",
		TYPE_MINE_INVISIBLE = "mine_invisible";
	
	public final static int
		OBSERVER_DETECTION_RADIUS = 5,
		SENTRY_DETECTION_RADIUS = 15;
	
	public final static int
		MINE_TRIGGER_RADIUS = 5,
		STUN_TRIGGER_RADIUS = 5;
	
	public final static int
		CHARGE_DEFUSE_RADIUS = 10;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected WardData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_ID];
	}-*/;
	
	public native final int getX() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_X];
	}-*/;
	
	public native final int getY() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_Y];
	}-*/;
	
	public native final String getType() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_TYPE];
	}-*/;

	public native final int getPower() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_POWER];
	}-*/;
	
	public native final String getOwner() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_OWNER];
	}-*/;
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public native final String getAllyTag() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_ALLY_TAG];
	}-*/;
	
	public native final String getTreaty() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_TREATY];
	}-*/;
	
	public native final int getLifespan() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_LIFESPAN];
	}-*/;

	public native final int getLineOfSight() /*-{
		return this[@fr.fg.client.data.WardData::FIELD_LINE_OF_SIGHT];
	}-*/;
	
	
	public final boolean isAlliedWard() {
		return ( this.getTreaty().equals(ALLIED) || this.getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL));
	}
	
	public final boolean isAllyWard() {
		return this.getTreaty().equals(ALLY);
	}
	
	public final boolean isEnemyWard() {
		return this.getTreaty().equals(ENEMY);
	}
	
	public final boolean isPirateWard() {
		return this.getTreaty().equals(PIRATE);
	}
	
	public final boolean isPlayerWard() {
		return this.getTreaty().equals(PLAYER);
	}
	
	public final boolean isNeutralWard() {
		return this.getTreaty().equals(NEUTRAL);
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
