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

public class LadderAllyData extends JavaScriptObject {
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
		FIELD_NAME = "a", //$NON-NLS-1$
		FIELD_TREATY = "b", //$NON-NLS-1$
		FIELD_ORGANIZATION = "c", //$NON-NLS-1$
		FIELD_MEMBERS = "d", //$NON-NLS-1$
		FIELD_POINTS = "e", //$NON-NLS-1$
		FIELD_RANK = "f", //$NON-NLS-1$
		FIELD_ACHIEVEMENTS = "g"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected LadderAllyData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getName() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_NAME];
	}-*/;
	
	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_TREATY];
	}-*/;
	
	public final native String getOrganization() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_ORGANIZATION];
	}-*/;
	
	public final native int getMembers() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_MEMBERS];
	}-*/;
	
	public final native int getPoints() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_POINTS];
	}-*/;
	
	public final native int getRank() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_RANK];
	}-*/;

	public final native int getAchievements() /*-{
		return this[@fr.fg.client.data.LadderAllyData::FIELD_ACHIEVEMENTS];
	}-*/;
	
	public final String getMedal() {
		return  getRank() == 1 ? "gold" : ( //$NON-NLS-1$
				getRank() == 2 ? "silver" : ( //$NON-NLS-1$
				getRank() == 3 ? "bronze" : "none")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
