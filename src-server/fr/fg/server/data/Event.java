/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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


import fr.fg.server.data.base.EventBase;
import fr.fg.server.util.Utilities;

public class Event extends EventBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		EVENT_PREMIUM_START				=  1, // P (durée avant expiration)
		EVENT_PREMIUM_EXTENDED			=  2, // P (durée prolongation, durée avant expiration)
		EVENT_PREMIUM_END				=  3, // P ()
		EVENT_PREMIUM_NEAR_END			=  4, // P (nombre de jours restants)
		
		EVENT_FLEET_ATTACK 			= 10, // P (nom flotte attaquante, nom flotte attaquée, nom joueur attaqué)
		EVENT_FLEET_UNDER_ATTACK 	= 11, // P (nom flotte attaquée, nom flotte attaquante, nom joueur attaquant)
		EVENT_FLEET_DESTROYED 		= 12, // P (nom flotte détruite)
		EVENT_FLEET_LOST			= 13, // P (nom flotte détruite)
		EVENT_BATTLE_REPORT 		= 14, // P (id rapport, lien externe)
		EVENT_STATION_UNDER_ATTACK	= 15, // A (nom station, pourcentage de PV restants)
		EVENT_STATION_LOST			= 16, // A (nom station)
		EVENT_STATION_SELF_DESTRUCT	= 17, // A (nom station, nom joueur)
		EVENT_SWAP					= 18, // P (nom flotte échangée, nom flotte source, nom joueur source)
		EVENT_EMP					= 19, // P (nom flotte immobilisée, nom flotte source, nom joueur source)
		
		EVENT_COLONIZATION 			= 20, // P (nom flotte qui a colonisé, nom système)
		EVENT_SYSTEM_CAPTURED 		= 21, // P (nom flotte qui a capturé, nom propriétaire système, nom système)
		EVENT_SYSTEM_LOST 			= 22, // P (nom flotte qui a capturé, nom propriétaire flotte, nom système)
		EVENT_START_CAPTURE			= 23, // P (nom flotte qui capture, nom propriétaire flotte, nom système)
		EVENT_DEVASTATE_SYSTEM		= 24, // P (nom flotte qui a dévasté, nom propriétaire système, nom système)
		EVENT_SYSTEM_DEVASTATED		= 25, // P (nom flotte qui a dévasté, nom propriétaire flotte, nom système)
		
		EVENT_DELUDE_LOST			= 26, // P (nom leurre attaqué, nom flotte attaquante, nom joueur attaquant)
		
		EVENT_NEW_TECHNOLOGY 		= 30, // P (id technologie)
		
		EVENT_ALLY_CREATED 			= 40, // A (nom joueur qui a créé l'alliance, nom alliance)
		EVENT_ALLY_DESTROYED 		= 41, // P (nom alliance)
		EVENT_ALLY_MEMBER_JOINED 	= 42, // A (nom joueur)
		EVENT_ALLY_MEMBER_LEFT 		= 43, // A (nom joueur)
		EVENT_ALLY_NEW_RANK 		= 44, // P (organisation alliance, nouveau rang : 1 ou +)
		EVENT_ALLY_APPLICANT 		= 45, // A (nom joueur qui postule)
		EVENT_ALLY_CANCEL_APPLY 	= 46, // A (nom joueur qui annule sa candidature)
		EVENT_ALLY_NEW_VOTEKICK		= 47, // A (nom du joueur qui lance le vote, nom du joueur visé)
		EVENT_ALLY_NEW_VOTEACCEPT	= 48, // A (nom du joueur qui lance le vote, nom du joueur visé)
	
		EVENT_ALLY_BREAK_ALLY 		= 50, // A (nom alliance avec laquelle le traité a été rompu)
		EVENT_ALLY_ALLY_BROKEN 		= 51, // A (nom alliance qui a rompu le traité)
		EVENT_ALLY_DECLARE_WAR 		= 52, // A (nom alliance attaquée)
		EVENT_ALLY_WAR_DECLARED 	= 53, // A (nom alliance qui a déclaré la guerre)
		EVENT_ALLY_OFFER_ALLY 		= 54, // A (nom alliance a qui l'alliance est proposée)
		EVENT_ALLY_ALLY_OFFERED 	= 55, // A (nom alliance qui reçoit la proposition)
		EVENT_ALLY_DECLINE_ALLY 	= 56, // A (nom alliance qui a émis la proposition d'alliance)
		EVENT_ALLY_ALLY_DECLINED 	= 57, // A (nom alliance qui a décliné la proposition)
		EVENT_ALLY_CANCEL_ALLY 		= 58, // A (nom alliance qui annule la proposition d'alliance)
		EVENT_ALLY_ALLY_CANCELED 	= 59, // A (nom alliance qui annule la proposition)
		EVENT_ALLY_NEW_ALLY 		= 60, // A (nom alliance alliée) 
		EVENT_ALLY_OFFER_PEACE 		= 61, // A (nom alliance a qui la trêve est proposée)
		EVENT_ALLY_PEACE_OFFERED 	= 62, // A (nom alliance qui reçoit la proposition)
		EVENT_ALLY_DECLINE_PEACE 	= 63, // A (nom alliance qui a émis la proposition de paix)
		EVENT_ALLY_PEACE_DECLINED 	= 64, // A (nom alliance qui a décliné la proposition)
		EVENT_ALLY_CANCEL_PEACE 	= 65, // A (nom alliance qui annule la proposition de paix)
		EVENT_ALLY_PEACE_CANCELED 	= 66, // A (nom alliance qui annule la proposition)
		EVENT_ALLY_NEW_PEACE 		= 67, // A (nom alliance alliée)
		
		EVENT_PLAYER_BREAK_ALLY 	= 70, // P (nom joueur avec laquelle le traité a été rompu)
		EVENT_PLAYER_ALLY_BROKEN 	= 71, // P (nom joueur qui a rompu le traité)
		EVENT_PLAYER_DECLARE_WAR 	= 72, // P (nom joueur attaquée)
		EVENT_PLAYER_WAR_DECLARED 	= 73, // P (nom joueur qui a déclaré la guerre)
		EVENT_PLAYER_OFFER_ALLY 	= 74, // P (nom joueur a qui l'alliance est proposée)
		EVENT_PLAYER_ALLY_OFFERED 	= 75, // P (nom joueur qui reçoit la proposition)
		EVENT_PLAYER_DECLINE_ALLY 	= 76, // P (nom joueur qui a émis la proposition d'alliance)
		EVENT_PLAYER_ALLY_DECLINED 	= 77, // P (nom joueur qui a décliné la proposition)
		EVENT_PLAYER_CANCEL_ALLY 	= 78, // P (nom joueur qui annule la proposition d'alliance)
		EVENT_PLAYER_ALLY_CANCELED 	= 79, // P (nom joueur qui annule la proposition)
		EVENT_PLAYER_NEW_ALLY 		= 80, // P (nom joueur alliée) 
		EVENT_PLAYER_OFFER_PEACE 	= 81, // P (nom joueur a qui la trêve est proposée)
		EVENT_PLAYER_PEACE_OFFERED 	= 82, // P (nom joueur qui reçoit la proposition)
		EVENT_PLAYER_DECLINE_PEACE 	= 83, // P (nom joueur qui a émis la proposition de paix)
		EVENT_PLAYER_PEACE_DECLINED = 84, // P (nom joueur qui a décliné la proposition)
		EVENT_PLAYER_CANCEL_PEACE 	= 85, // P (nom joueur qui annule la proposition de paix)
		EVENT_PLAYER_PEACE_CANCELED = 86, // P (nom joueur qui annule la proposition)
		EVENT_PLAYER_NEW_PEACE 		= 87, // P (nom joueur avec qui la paix a été signée)
		EVENT_PLAYER_END_OF_WAR		= 88, // P (nom joueur avec qui la paix a été signée)
		
		EVENT_NEW_STATION			= 90, // A (nom secteur)
		EVENT_STATION_UPGRADED		= 91, // A (nom station, niveau)
		
		EVENT_CHARGE_DEFUSED		= 100, // P (nom flotte qui a désamorcé, nom propriétaire flotte, type charge)
		EVENT_CHARGE_TRIGGERED		= 102, // P (nom flotte qui a subit la charge, nom propriétaire flotte, type charge, nombre charges déclenchées)
		EVENT_CHARGE_BLOWED			= 101, // P (nom flotte qui a subit la charge, type charge, nombre charges déclenchées)
		EVENT_CHARGE_FLEET_DESTROYED= 103, // P (nom flotte détruite par la charge, nom propriétaire flotte, type charge, nombre charges déclenchées)
		EVENT_CHARGE_FLEET_LOST 	= 104, // P (nom flotte détruite par la charge, type charge, nombre charges déclenchées)
		
		EVENT_STRUCTURE_ATTACKED	= 110, // P (nom structure, nom flotte qui bombarde, nom propriétaire flotte)
		EVENT_STRUCTURE_LOST		= 111, // P (nom structure, nom flotte qui a détruit la structure, nom propriétaire flotte) 
		EVENT_STRUCTURE_DESTROYED	= 112, // P (nom structure, nom propriétaire structure, nom flotte qui a détruit la structure)
		EVENT_STRUCTURE_DISMOUNTED	= 113, // P (nom structure, nom flotte qui démonte)
		EVENT_STRUCTURE_MOUNTED		= 114, // P (nom structure, nom flotte monte)
		EVENT_STRUCTURE_DAMAGED		= 115, // P (nom structure, pourcentage PV restants)
	
		EVENT_FLEET_CAPTURED_BLACKHOLE	= 121,
		EVENT_WARD_OBSERVER_LOST	= 131,
		EVENT_WARD_SENTRY_LOST		= 132,
	
		EVENT_PLAYER_ADD_FRIEND	= 133, // P (nom joueur ajouté en ami)
		EVENT_PLAYER_REMOVE_FRIEND	= 134, // P (nom joueur retiré des amis)
		EVENT_PLAYER_ADDED_FRIEND	= 135, // P (nom joueur qui vous a ajouté en ami)
		EVENT_PLAYER_REMOVED_FRIEND	= 136, // P (nom joueur qui vous a retiré des amis)
	
		EVENT_ALLY_ADDED_NEWS = 137, // A (nom du joueur qui a ajouté une news)
		EVENT_ALLY_REMOVED_NEWS = 138, // A (nom du joueur qui a supprimé une news)
	
		EVENT_MIGRATION_START = 140, // P (nom flotte qui a migré, nom système à migrer, nom système d'arrivée)
		EVENT_MIGRATION_END = 141, // P (nom flotte qui a migré, nom système à migrer, nom système d'arrivée)
		
		EVENT_ALLY_BREAK_ALLY_DEFENSIVE = 142,
		EVENT_ALLY_ALLY_DEFENSIVE_BROKEN = 143,
		EVENT_ALLY_OFFER_ALLY_DEFENSIVE = 144,
		EVENT_ALLY_ALLY_DEFENSIVE_OFFERED = 145,
		EVENT_ALLY_DECLINE_ALLY_DEFENSIVE = 146,
		EVENT_ALLY_ALLY_DEFENSIVE_DECLINED = 147,
		EVENT_ALLY_CANCEL_ALLY_DEFENSIVE = 148,
		EVENT_ALLY_ALLY_DEFENSIVE_CANCELED = 149,
		EVENT_ALLY_NEW_ALLY_DEFENSIVE = 150,
		EVENT_ALLY_BREAK_ALLY_TOTAL = 151,
		EVENT_ALLY_ALLY_TOTAL_BROKEN = 152,
		EVENT_ALLY_OFFER_ALLY_TOTAL = 153,
		EVENT_ALLY_ALLY_TOTAL_OFFERED = 154,
		EVENT_ALLY_DECLINE_ALLY_TOTAL = 155,
		EVENT_ALLY_ALLY_TOTAL_DECLINED = 156,
		EVENT_ALLY_CANCEL_ALLY_TOTAL = 157,
		EVENT_ALLY_ALLY_TOTAL_CANCELED = 158,
		EVENT_ALLY_NEW_ALLY_TOTAL = 159,
		
		EVENT_PLAYER_BREAK_ALLY_DEFENSIVE = 160,
		EVENT_PLAYER_ALLY_DEFENSIVE_BROKEN = 161,
		EVENT_PLAYER_OFFER_ALLY_DEFENSIVE = 162,
		EVENT_PLAYER_ALLY_DEFENSIVE_OFFERED = 163,
		EVENT_PLAYER_DECLINE_ALLY_DEFENSIVE = 164,
		EVENT_PLAYER_ALLY_DEFENSIVE_DECLINED = 165,
		EVENT_PLAYER_CANCEL_ALLY_DEFENSIVE = 166,
		EVENT_PLAYER_ALLY_DEFENSIVE_CANCELED = 167,
		EVENT_PLAYER_NEW_ALLY_DEFENSIVE = 168,
		EVENT_PLAYER_BREAK_ALLY_TOTAL = 169,
		EVENT_PLAYER_ALLY_TOTAL_BROKEN = 170,
		EVENT_PLAYER_OFFER_ALLY_TOTAL = 171,
		EVENT_PLAYER_ALLY_TOTAL_OFFERED = 172,
		EVENT_PLAYER_DECLINE_ALLY_TOTAL = 173,
		EVENT_PLAYER_ALLY_TOTAL_DECLINED = 174,
		EVENT_PLAYER_CANCEL_ALLY_TOTAL = 175,
		EVENT_PLAYER_ALLY_TOTAL_CANCELED = 176,
		EVENT_PLAYER_NEW_ALLY_TOTAL = 177,
		
		EVENT_ALLY_DECLARE_WAR_TOTAL = 178,
		EVENT_ALLY_WAR_DECLARED_TOTAL = 179,
		EVENT_ALLY_DECLARE_WAR_DEFENSIVE = 180,
		EVENT_ALLY_WAR_DECLARED_DEFENSIVE = 181,
	
		EVENT_PLAYER_DECLARE_WAR_TOTAL = 182,
		EVENT_PLAYER_WAR_DECLARED_TOTAL = 183,
		EVENT_PLAYER_DECLARE_WAR_DEFENSIVE = 184,
		EVENT_PLAYER_WAR_DECLARED_DEFENSIVE = 185,
		EVENT_REWARD_PERSO = 190, // P (%reward% obtenu lors de la fin du contrat d'alliance)
	
		
		EVENT_ELECTION_START = 200,
		
		EVENT_LEADER_DELEGATE = 201,
		
		EVENT_PREMIUM = 202,
		EVENT_PREMIUM_EXPIRED = 203,
		EVENT_PREMIUM_ADDED = 204,
		EVENT_LOTTERY_WON	 = 205, // P (symbole, type ressource, nombre ressources) 
		EVENT_LOTTERY_LOST	 = 206; // P (symbole)
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Event() {
		// Nécessaire pour la construction par réflection
	}
	
	public Event(int type, String target, int idTarget, int idArea, int x, int y, String... args) {
		setType(type);
		setArg1(args.length > 0 ? args[0] : "");
		setArg2(args.length > 1 ? args[1] : "");
		setArg3(args.length > 2 ? args[2] : "");
		setArg4(args.length > 3 ? args[3] : "");
		setDate(Utilities.now());
		setIdArea(idArea);
		setX(x);
		setY(y);
		setTarget(target);
		setIdTarget(idTarget);
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
