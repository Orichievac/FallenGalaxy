/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc, Thierry Chevalier

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

import fr.fg.server.util.Config;

public class GameConstants {
	// ------------------------------------------------------- CONSTANTES -- //

	// Nombre d'heures de premium lors de la saisie d'un code allopass
	// Valeur : 30jours
	public final static int ALLOPASS_CODE_PREMIUM_HOURS = 24 * 30;
	
	// Limite du niveau d'XP joueur
	public final static int PLAYER_LEVEL_LIMIT = 200;
	
	// Limite du nombre de membres dans une alliance
	public final static int ALLY_MAX_MEMBERS = 20;
	
	// Unité de temps du serveur, en secondes (1 seconde par défaut)
	// Ex : une valeur de 10 multiplie par 10 la vitesse du serveur
	public final static int TIME_UNIT = Config.getTimeUnit();
	
	// Durée à partir de laquelle le compte est supprimé pour inactivité
	// Valeur : 60 jours
	public final static int ACCOUNT_IDLE_LENGTH = 5184000;
	
	// Nombre de ressources (hormis les crédits)
	public final static int RESOURCES_COUNT = 4;
	
	// Index des crédits
	public final static int CREDITS = 4;
	
	// Rayon des systèmes, en cases
	public final static int SYSTEM_RADIUS = 5;
	
	// Rayon d'attraction des trous noir
	public final static int BLACKHOLE_GRAVITY_RADIUS = 10;
	
	// Rayon des stations spatiales, en cases
	public final static double SPACE_STATION_RADIUS = 4.5;
	
	// Population maximale des systèmes
	public final static int SYSTEM_POPULATION_CAPACITY = 5;
	
	// Population initiale des systèmes
	public final static int SYSTEM_INITIAL_POPULATION = 1;
	
	// Capacité de stockage initiale des systèmes
	public final static int SYSTEM_STORAGE_CAPACITY = 5000;
	
	// Nombre de cases constructibles dans les systèmes
	public final static int SYSTEM_BUILDING_LAND = 22;
	
	// Croissance de la population, en % de la population maximale du système
	// par seconde
	// Valeur : 10% de la capacité maximale des systèmes / jour
	public final static double POPULATION_GROWTH = 0.0000011574074 * TIME_UNIT;
	
	// Facteur de production de ressources par seconde des exploitations
	// Valeur : 1 ressource / min
	public final static double EXPLOITATION_RATE = 0.01666666666 * TIME_UNIT;
	
	// Facteur de production des points de recherche par seconde des laboratoires
	// Valeur : 1 pt / min
	public final static double RESEARCH_RATE = 0.01666666666 * TIME_UNIT;
	
	// Nombre de slots de vaisseaux pour les flottes
	public final static int FLEET_SLOT_COUNT = 5;

	// Nombre de slots de vaisseaux pour les systèmes
	public final static int SYSTEM_SLOT_COUNT = 10;
	
	// Taille de la file d'attente pour construire des vaisseaux
	public final static int SHIPS_QUEUE_LENGTH = 3;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal
	// Valeur : 1 min
	public final static int MOVEMENT_RELOAD = 10 / TIME_UNIT;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal après avoir exécuté une action
	// Valeur : 1h
	public final static int ACTION_MOVEMENT_RELOAD = 3600 / TIME_UNIT;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal après avoir saboté une station
	// Valeur : 2h
	public final static int DESTROY_SPACE_STATION_MOVEMENT_RELOAD = 7200 / TIME_UNIT;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal après avoir attaqué une structure
	// Valeur : 4h
	public final static int ATTACK_STRUCTURE_MOVEMENT_RELOAD = 14400 / TIME_UNIT;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal après avoir réparé une structure
	// Valeur : 4h
	public final static int REPAIR_STRUCTURE_MOVEMENT_RELOAD = 14400 / TIME_UNIT;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal après une escarmouche
	// Valeur : 2h
	public final static int SKIRMISH_MOVEMENT_RELOAD = 2 * 3600;
	
	// Temps en secondes avant que le mouvement restant d'une flotte soit
	// réinitialisé à son mouvement maximal après une bataille
	// Valeur : 4h
	public final static int BATTLE_MOVEMENT_RELOAD = 4 * 3600;
	
	// Durée d'un voyage hyperspatial entre deux secteurs, en secondes
	// Valeur : 3h
	public final static int HYPERSPACE_AREAS_TRAVEL_LENGTH = 10800 / TIME_UNIT;

	// Durée d'un voyage hyperspatial entre deux secteurs, en secondes
	// Valeur : 6h
	public final static int HYPERSPACE_SECTORS_TRAVEL_LENGTH = 21600 / TIME_UNIT;
	
	// Distance maximale entre deux secteurs pour faire un saut vers un secteur
	// inconnu
	public final static int HYPERSPACE_DISTANCE_MAX = 10;
	
	// Distance maximale entre une flotte et une porte hyperspatiale pour
	// pouvoir faire un saut hyperspatial
	public final static int HYPERGATE_JUMP_RADIUS = 10;
	
	// Ligne de vue des flottes
	public final static int LOS_FLEET = 25;
	
	// Ligne de vue des systèmes
	public final static int LOS_SYSTEM = 10;

	// Ligne de vue des balises d'observation
	public final static int LOS_WARD = 50;
	
	// Ligne de vue des stations spatiales
	public final static int LOS_SPACE_STATION = 10;
	
	// Durée de vie des charges IEM / explosives
	public final static int CHARGES_LENGTH = 86400 * 14;
	
	// Temps de colonisations des systèmes, en secondes
	// Valeur : 24h
	public final static int COLONIZATION_LENGTH = 86400 / TIME_UNIT;
	
	// Temps de capture des systèmes, en secondes
	// Valeur : 72h
	public final static int CAPTURE_LENGTH = 259200 / TIME_UNIT;
	
	// Temps de migration des systèmes, en secondes
	// Valeur : 4j
	public final static int MIGRATION_LENGTH = 4 * 24 * 60 * 60 / TIME_UNIT;
	
	// Temps de capture des systèmes sous domination, en secondes
	// Valeur : 96h
	public final static int DOMINATED_CAPTURE_LENGTH = 345600 / TIME_UNIT;
	
	// Cout en points de colonisation d'un système dans le noyau
	public final static int SYSTEM_COST = 5;
	
	// Intervalle de temps minimum entre deux messages, en secondes
	public final static int MESSAGES_FLOOD_LIMIT = 10;
	
	// Intervalle de temps minimum entre l'achat de deux flottes
	// Valeur : 1h
	public final static int BUY_FLEET_COOLDOWN = 3600;
	
	// Pertes minimales infligées aux flottes qui stationnent au dessus d'un
	// trou noir, en % des vaisseaux de la flotte
	public final static int BLACKHOLE_MIN_DAMAGE = 5;
	
	// Pertes maximales infligées aux flottes qui stationnent au dessus d'un
	// trou noir, en % des vaisseaux de la flotte
	public final static int BLACKHOLE_MAX_DAMAGE = 25;
	
	// Durée de vie des messages
	// Valeur : 5 jours
	public final static int MESSAGE_LIFESPAN = 5 * 24 * 3600;
	
	// Durée de vie des messages en premium
	// Valeur : 15 jours
	public final static int MESSAGE_LIFESPAN_PREMIUM = 15 * 24 * 3600;
	
	// Durée de vie des news
	public final static int NEWS_LIFESPAN = 10 * 24 * 3600;
	
	// Gain en XP par vaisseau détruit, en pourcentage de la puissance du
	// vaisseau
	public final static double XP_SHIP_DESTROYED = 0.02;

	// Gain en XP par vaisseau détruit par une mine, en pourcentage de la
	// puissance du vaisseau
	public final static double XP_MINE_SHIP_DESTROYED = 0.005;
	
	// Durée au bout de laquelle les marqueurs sont automatiquement supprimés,
	// en secondes
	// Valeur : 1 semaine
	public final static int MARKER_LENGTH = 604800;
	
	// Durée minimale pour l'extraction de ressources, en secondes
	// Valeur : 5h
	public final static int EXTRACTION_MIN_LENGTH = 18000 / TIME_UNIT;
	
	// Durée maximale pour l'extraction de ressources, en secondes
	// Valeur : 10h
	public final static int EXTRACTION_MAX_LENGTH = 36000 / TIME_UNIT;
	
	// Vitesse de déplacement des extracteurs, en cases
	public final static int EXTRACTOR_MOVEMENT = 5;
	
	// Nombre d'actions dans une escarmouche
	public final static int SKIRMISH_ACTIONS_COUNT = 5;
	
	// Nombre d'actions dans un combat classique
	public final static int BATTLE_ACTIONS_COUNT = 15;
	
	// Nombre de rounds lors d'un tir de barrage, sans la compétence artilleur
	public final static int BARRAGE_ROUNDS_COUNT = 1;
	
	// Bonus de dégats contre les cibles prioritaires des vaisseaux
	// Valeur : +30%
	public final static double PRIORITY_TARGET_DAMAGE = .3;
	
	// Temps au bout duquel une guerre est considérée comme inactive et peut
	// être supprimée automatiquement
	// Valeur : 1 semaine
	public final static int WAR_INACTIVITY = 7 * 3600 * 24;
	
	// Temps au bout duquel le mode de diplomatie peut être basculé
	// Valeur : 4 jours
	public final static int DIPLOMACY_SWITCH_LENGTH = 4 * 3600 * 24;
	
	// Nombre maximum d'évènements par joueur
	public final static int MAX_PLAYER_EVENTS = 80;
	
	// Nombre maximum d'évènements par alliance
	public final static int MAX_ALLY_EVENTS = 15;
	
	// Liste des thèmes disponibles
	public final static String[] THEMES = {
		Config.getMediaURL() + "style/FallenCraft2Red",
		Config.getMediaURL() + "style/FallenCraft2Blue",
		Config.getMediaURL() + "style/Abyss"
	};
	
	// Nombre de structure de production maximum par générateur dans un secteur de départ
	public final static int MAX_PRODUCTION_STRUCTURE = 4;
	
	// Nombre de joueur minimum pour que les contrats d'alliance soient ajoutés.
	public final static int MIN_MEMBER_FOR_ALLY_CONTRACT = 5;
	
	// Timeout pour les missions d'alliance
	// Une fois la mission finie, les joueurs ont 48h pour récupérer la récompense
	public final static int END_ALLY_REWARD = 172800;
	
	// Temps avant de pouvoir rejoindre une ally quand on vient d'en quitter une
	// Une semaine
	public final static int ALLY_REJOIN = 7 * 3600 * 24;
	
	//Temps minimum de minage
	public final static int MINIMUM_MINING_TIME = 30 * 60; // = 30 min
	
	//Limitation des flottes à la puissance X 
	public final static int FLEET_MAX_POWERLEVEL = 50; 
	
	//Niveau d'écart max entre les joueurs dans un contrat pvp
	public final static int MAX_LEVEL_DISTANCE_PVP = 2; 
	
	// --------------------------------------------------------- METHODES -- //
}
