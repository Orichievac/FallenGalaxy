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

public class SkillData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_TYPE = "a", //$NON-NLS-1$
		FIELD_LEVEL = "b", //$NON-NLS-1$
		FIELD_RELOAD = "c", //$NON-NLS-1$
		FIELD_LAST_USE = "d"; //$NON-NLS-1$
	
	// Liste des compétences basiques
	public final static int
		SKILL_SPY				= 1, // Renseignements
		SKILL_TRACKER			= 2, // Traceur hyperespace
		SKILL_BOMBING			= 3, // Bombardement
		SKILL_OFFENSIVE_LINK 	= 4, // Lien offensif
		SKILL_DEFENSIVE_LINK	= 5, // Lien défensif
		SKILL_RETREAT	 		= 6, // Repli
		SKILL_ENGINEER		 	= 7, // Ingénieur
		SKILL_PYROTECHNIST		= 8, // Artificier
		SKILL_MINING			= 9, // Mineur
		SKILL_PIRATE		 	= 10; // Piraterie
	
	// Liste des compétences ultimes
	public final static int
		SKILL_ULTIMATE_AMBUSH 	= 11, // Embuscade
		SKILL_ULTIMATE_SWAP 	= 12, // Distortion spatiale
		SKILL_ULTIMATE_STEALTH	= 13, // Furtivité
		SKILL_ULTIMATE_EMP		= 14, // EMP
		SKILL_ULTIMATE_DELUDE	= 15; // Leurre
	
	public final static int
		OFFENSIVE_LINK_RANGE = 15,
		DEFENSIVE_LINK_RANGE = 15,
		EMP_RANGE[] = {5, 20, 100},
		SWAP_RANGE[] = {5, 20, 100},
		BOMBING_RANGE[] = {3, 5, 7, 9},
		SPY_RANGE = 10,
		TRACKER_RANGE = 10;
	
	public final static int
		BASIC_SKILLS_COUNT = 10,
		ULTIMATE_SKILLS_COUNT = 5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	protected SkillData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public native final int getType() /*-{
		return this[@fr.fg.client.data.SkillData::FIELD_TYPE];
	}-*/;

	public native final int getLevel() /*-{
		return this[@fr.fg.client.data.SkillData::FIELD_LEVEL];
	}-*/;
	
	public native final int getReloadRemainingTime() /*-{
		return this[@fr.fg.client.data.SkillData::FIELD_RELOAD];
	}-*/;

	public native final double getLastUseTime() /*-{
		return this[@fr.fg.client.data.SkillData::FIELD_LAST_USE];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
