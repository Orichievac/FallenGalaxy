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

public class SpaceStationData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String CLASS_NAME = "SpaceStationData"; //$NON-NLS-1$
	
	public final static String
	PLAYER = "player",
	ENEMY = "enemy",
	ALLY = "ally",
	DEFENSIVE = "defensive",
	TOTAL = "total",
	ALLIED = "allied",
	NEUTRAL = "neutral",
	PIRATE = "pirate";
	
	public final static double SPACE_STATION_RADIUS = 4.5;
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_NAME = "b", //$NON-NLS-1$
		FIELD_X = "c", //$NON-NLS-1$
		FIELD_Y = "d", //$NON-NLS-1$
		FIELD_ALLY_NAME = "i", //$NON-NLS-1$
		FIELD_ALLY_TAG = "j", //$NON-NLS-1$
		FIELD_TREATY = "k", //$NON-NLS-1$
		FIELD_LEVEL = "l", //$NON-NLS-1$
		FIELD_RESOURCES = "m", //$NON-NLS-1$
		FIELD_ALLY_ID = "n", //$NON-NLS-1$
		FIELD_HULL = "o", //$NON-NLS-1$
		FIELD_CREDITS = "p", //$NON-NLS-1$
		FIELD_PRODUCTION_MODIFIER = "r", //$NON-NLS-1$
		FIELD_LINE_OF_SIGHT = "s"; //$NON-NLS-1$
	
	public final static int[][] COST_LEVELS = {
		{    100000,     100000,     100000, 0,     150000},
		{    500000,     500000,     500000, 0,     750000},
		{   3300000,    3300000,    3300000, 0,    5000000},
		{  22000000,   22000000,   22000000, 0,   34000000},
		{ 150000000,  150000000,  150000000, 0,  225000000},
		{1000000000, 1000000000, 1000000000, 0, 1500000000},
	};
	
	public final static int[] INFLUENCE_LEVELS = {0, 1, 5, 25, 125, 625};
	
	public final static int[] HULL_LEVELS = {100, 600, 4000, 30000, 240000, 2000000};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected SpaceStationData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_ID];
	}-*/;

	public native final String getName() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_NAME];
	}-*/;

	public native final int getX() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_X];
	}-*/;

	public native final int getY() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_Y];
	}-*/;
	
	public native final int getIdAlly() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_ALLY_ID];
	}-*/;
	
	public native final String getAllyName() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_ALLY_NAME];
	}-*/;
	
	public final boolean hasAlly() {
		return getIdAlly() != 0;
	}
	
	public native final String getAllyTag() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_ALLY_TAG];
	}-*/;
	
	public final boolean hasAllyTag() {
		return getAllyTag().length() > 0;
	}
	
	public native final String getTreaty() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_TREATY];
	}-*/;

	public native final int getLevel() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_LEVEL];
	}-*/;

	public native final int getResourcesCount() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_RESOURCES].length;
	}-*/;
	
	public native final double getResourceAt(int index) /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_RESOURCES][index];
	}-*/;

	public native final double getCredits() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_CREDITS];
	}-*/;

	public native final int getHull() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_HULL];
	}-*/;

	public native final double getProductionModifier() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_PRODUCTION_MODIFIER];
	}-*/;

	public native final int getLineOfSight() /*-{
		return this[@fr.fg.client.data.SpaceStationData::FIELD_LINE_OF_SIGHT];
	}-*/;

	public final boolean isAlliedSpaceStation() {
		return (this.getTreaty().equals(ALLIED) || this.getTreaty().equals(ALLIED) || this.getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL));
	}
	
	public final boolean isAllySpaceStation() {
		return this.getTreaty().equals(ALLY);
	}
	
	public final boolean isEnemySpaceStation() {
		return this.getTreaty().equals(ENEMY);
	}
	
	public final boolean isPirateSpaceStation() {
		return this.getTreaty().equals(PIRATE);
	}
	
	public final boolean isPlayerSpaceStation() {
		return this.getTreaty().equals(PLAYER);
	}
	
	public final boolean isNeutralSpaceStation() {
		return this.getTreaty().equals(NEUTRAL);
	}
	
	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
