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

package fr.fg.server.data;

public class Skill {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static Skill NO_SKILL = new Skill();
	
	// Liste des compétences basiques
	// ! Mettre à jour isBasicSkill / isUltimateSkill
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
	
	public final static int
		SKILL_REPAIR			= 16; // Réparation
	
	// Liste des compétences ultimes
	public final static int
		SKILL_ULTIMATE_AMBUSH 	= 11, // Embuscade
		SKILL_ULTIMATE_SWAP 	= 12, // Distortion spatiale
		SKILL_ULTIMATE_STEALTH	= 13, // Furtivité
		SKILL_ULTIMATE_EMP		= 14, // EMP
		SKILL_ULTIMATE_DELUDE	= 15; // Leurre
	
	// Nombre de compétences
	public final static int SKILLS_COUNT = 16;
	
	// Portée de la compétence renseignements
	public final static int SPY_DETECTION_RANGE = 10;
	
	// Vol de ressources avec la compétence pirate
	public final static double[]
	    SKILL_PIRATE_EFFECT = {0, .1, .2, .3}; // 0%, 10%, 20%, 30%
	
	// Coeficient d'XP pour les combats en pirate
	public final static double SKILL_PIRATE_BATTLE_XP_COEF = 1.5;
	
	// Diminution du temps des sauts hyperspatiaux
	public final static int[]
	    SKILL_TRACKER_EFFECT = {1800, 2400, 3000, 3600}; // 30m, 40m, 50m, 60m
	
	// Portée de la compétence traqueur
	public final static int TRACKER_DETECTION_RANGE = 10;
	
	// Bornes pour le % de vaisseaux réparés avec la compétence réparation,
	// en fonction du niveau de la compétence
	public final static double[]
		SKILL_REPAIR_MIN = {.05, .05, .10, .15},
		SKILL_REPAIR_MAX = {.10, .20, .25, .30};
	
	// % des vaisseaux masqués avec la compétence furtivité, en fonction du
	// niveau de la compétence
	public final static float[] SKILL_STEATH_EFFECT = {.9f, .8f, .6f};
	
	// % de chances de sortir d'un combat, à chaque round, en fonction du
	// niveau de la compétence repli
	public final static double[][] SKILL_RETREAT_EFFECT = {
		{.03, .05, .07, .09}, // Avant le début du combat, selon le niveau de la compétence
		{.07, .11, .15, .19}, // Après 5 actions, selon le niveau de la compétence
		{.15, .23, .31, .40}  // Après 10 actions, selon le niveau de la compétence
	};
	
	// Distance max du lien EMP et durée
	public final static int
	    SKILL_ULTIMATE_EMP_RANGE[] = {5, 20, 100},
	    SKILL_ULTIMATE_EMP_SOURCE_MOVEMENT_RELOAD = 3600 * 6, // 6h
        SKILL_ULTIMATE_EMP_TARGET_MOVEMENT_RELOAD[] = {7200, 12600, 18000}; // 2h, 3h30, 5h
	
	// Valeur maximum de la flotte attaquée, en % de la puissance de la flotte qui attaque
	public final static double[] SKILL_ULTIMATE_AMBUSH_VALUE_LIMIT = {1.1, .95, .8};
	
	// % de vaisseaux se joignant en renforts
	public final static double[] SKILL_OFFENSIVE_LINK_COEF = {.05, .11, .17, .25};
	public final static double[] SKILL_DEFENSIVE_LINK_COEF = {.12, .25, .38, .50};
	
	public final static int
		SKILL_OFFENSIVE_LINK_RANGE = 15,
		SKILL_DEFENSIVE_LINK_RANGE = 15,
		SKILL_BOMBING_RANGE[] = {3, 5, 7, 9},
		SKILL_ULTIMATE_SWAP_RANGE[] = {5, 20, 100};
	
	// Modificateur de dégâts en fonction du niveau de la compétence
	public final static double[] SKILL_BOMBING_COEF = {.7, .8, .9, 1.};
	
	// Temps de rechargement du mouvement après un minage
	public final static int SKILL_MINING_MOVEMENT_RELOAD = 3600 * 6; // 6h
	
	// Coefficient de ressources gagnées en fonction du niveau de la compétence
	public final static int[] SKILL_MINING_RESOURCES_COEF = {1, 2, 3, 4};
	
	// XP gagnée après un minage
	public final static int SKILL_MINING_XP_BONUS = 7;
	
	// XP gagnée après une sonde
	public final static int SKILL_PROBE_XP_BONUS = 2;
	
	// XP gagnée après la création d'un leurre
	public final static int SKILL_DELUDE_XP_BONUS = 4;

	// XP gagnée après avoir désamorcé une mine
	public final static int SKILL_DEFUSE_XP_BONUS = 2;
	
	// Temps pour terminer une construction avec la compétence ingénieur
	public final static int SKILL_ENGINEER_MOVEMENT_RELOAD = 3600; // 1h
	
	// Temps pour terminer une construction avec la compétence artificier
	public final static int SKILL_PYROTECHNIST_MOVEMENT_RELOAD = 3 * 3600; // 1h
	
	// Temps de rechargement du mouvement après une distortion spatiale
	public final static int SKILL_ULTIMATE_SWAP_MOVEMENT_RELOAD = 6 * 3600; // 6h
	
	// Temps de rechargement après le désamorcage d'une charge
	public final static int SKILL_DEFUSE_MOVEMENT_RELOAD = 3600; // 1h
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	private int level;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Skill() {
		this.type = 0;
		this.level = 0;
	}
	
	public Skill(int type, int level) {
		this.type = type;
		this.level = level;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Skill))
			return false;
		Skill skill = (Skill) object;
		return skill.getType() == type && skill.getLevel() == level;
	}
	
	public static boolean isBasicSkill(int skill) {
		return skill >= SKILL_SPY && skill <= SKILL_PIRATE;
	}

	public static boolean isUltimateSkill(int skill) {
		return skill >= SKILL_ULTIMATE_AMBUSH &&
			skill <= SKILL_ULTIMATE_DELUDE;
	}
	
	public static int getDeludeCost(int power) {
		return (power + 1) * 5000;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
