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

public class StarSystemData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "StarSystemData"; //$NON-NLS-1$
	
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
		FIELD_NAME = "b", //$NON-NLS-1$
		FIELD_X = "c", //$NON-NLS-1$
		FIELD_Y = "d", //$NON-NLS-1$
		FIELD_COLONIZABLE = "e", //$NON-NLS-1$
		FIELD_STAR_IMAGE = "f", //$NON-NLS-1$
		FIELD_PLANETS = "g", //$NON-NLS-1$
		FIELD_OWNER = "h", //$NON-NLS-1$
		FIELD_ALLY = "i", //$NON-NLS-1$
		FIELD_AI = "j", //$NON-NLS-1$
		FIELD_TREATY = "k", //$NON-NLS-1$
		FIELD_LINE_OF_SIGHT = "l", //$NON-NLS-1$
		FIELD_ASTEROID_BELT = "m"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected StarSystemData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_ID];
	}-*/;
	
	public native final String getName() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_NAME];
	}-*/;
	
	public native final int getX() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_X];
	}-*/;
	
	public native final int getY() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_Y];
	}-*/;
	
	public native final boolean isColonizable() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_COLONIZABLE];
	}-*/;
	
	public native final int getStarImage() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_STAR_IMAGE];
	}-*/;
	
	public native final int getPlanetsCount() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_PLANETS].length;
	}-*/;
	
	public native final PlanetData getPlanetAt(int index) /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_PLANETS][index];
	}-*/;
	
	public native final String getOwner() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_OWNER];
	}-*/;

	public final boolean hasOwner() {
		return !getTreaty().equals("unknown") && getOwner().length() > 0; //$NON-NLS-1$
	}
	
	public native final String getAlly() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_ALLY];
	}-*/;
	
	public final boolean hasAlly() {
		return !getTreaty().equals("unknown") && getAlly().length() > 0; //$NON-NLS-1$
	}
	
	public native final boolean isAi() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_AI];
	}-*/;

	public native final String getTreaty() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_TREATY];
	}-*/;

	public native final int getLineOfSight() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_LINE_OF_SIGHT];
	}-*/;

	public native final int getAsteroidBelt() /*-{
		return this[@fr.fg.client.data.StarSystemData::FIELD_ASTEROID_BELT];
	}-*/;
	
	
	public final boolean isAlliedStarSystem() {
		return (this.getTreaty().equals(ALLIED) || this.getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL));
	}
	
	public final boolean isAllyStarSystem() {
		return this.getTreaty().equals(ALLY);
	}
	
	public final boolean isEnemyStarSystem() {
		return this.getTreaty().equals(ENEMY);
	}
	
	public final boolean isPirateStarSystem() {
		return this.getTreaty().equals(PIRATE);
	}
	
	public final boolean isPlayerStarSystem() {
		return this.getTreaty().equals(PLAYER);
	}
	
	public final boolean isNeutralStarSystem() {
		return this.getTreaty().equals(NEUTRAL);
	}
	
	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
