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

public class LadderPlayerData extends JavaScriptObject {
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
		FIELD_LOGIN = "a", //$NON-NLS-1$
		FIELD_ALLY = "b", //$NON-NLS-1$
		FIELD_TREATY = "c", //$NON-NLS-1$
		FIELD_RANK = "d", //$NON-NLS-1$
		FIELD_LEVEL = "e", //$NON-NLS-1$
		FIELD_POINTS = "f", //$NON-NLS-1$
		FIELD_ACHIEVEMENTS = "g", //$NON-NLS-1$
		FIELD_PREMIUM = "h";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected LadderPlayerData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native String getLogin() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_LOGIN];
	}-*/;
	
	public final native String getAlly() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_ALLY];
	}-*/;
	
	public final native String getTreaty() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_TREATY];
	}-*/;
	
	public final native int getRank() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_RANK];
	}-*/;
	
	public final native int getLevel() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_LEVEL];
	}-*/;
	
	public final native int getPoints() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_POINTS];
	}-*/;
	
	public final native int getAchievements() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_ACHIEVEMENTS];
	}-*/;
	public final native boolean isPremium() /*-{
		return this[@fr.fg.client.data.LadderPlayerData::FIELD_PREMIUM];
	}-*/;
	
	public final String getMedal() {
		return  getRank() < 4 ? "gold" : ( //$NON-NLS-1$
				getRank() < 11 ? "silver" : ( //$NON-NLS-1$
				getRank() < 26 ? "bronze" : "none")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public final String getPact() { //Renvoie ALLIED si pacte
		return (getTreaty().equals(DEFENSIVE) || this.getTreaty().equals(TOTAL) )? ALLIED : getTreaty();
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
