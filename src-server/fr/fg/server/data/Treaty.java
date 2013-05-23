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

import fr.fg.server.data.base.TreatyBase;
import fr.fg.server.util.Utilities;

public class Treaty extends TreatyBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		PLAYER = "player",
		ENEMY = "enemy",
		ALLY = "ally", // /!\ ALLY = Alliance, ALLIED = allié /!\
		DEFENSIVE = "defensive",
		TOTAL = "total",
		ALLIED = "allied",
		NEUTRAL = "neutral",
		UNKNOWN = "unknown",
		UNINHABITED = "uninhabited",
		PIRATE = "pirate";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Treaty() {
		// Nécessaire pour la construction par réflection
	}
	
	public Treaty(int idPlayer1, int idPlayer2, String type, int source) {
		setIdPlayer1(idPlayer1);
		setIdPlayer2(idPlayer2);
		setType(type);
		setDate(Utilities.now());
		setLastActivity(getDate());
		setSource(source);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean implyPlayer(int idPlayer) {
		return getIdPlayer1() == idPlayer || getIdPlayer2() == idPlayer;
	}
	
	public Player getOtherPlayer(int idPlayer){
		return DataAccess.getPlayerById(getOtherPlayerId(idPlayer));
	}
	
	public int getOtherPlayerId(int idPlayer){
		return (getIdPlayer1()==idPlayer?getIdPlayer2():getIdPlayer1());
	}
	
	public Player getPlayer1(){
		return DataAccess.getPlayerById(getIdPlayer1());
	}
	
	public Player getPlayer2(){
		return DataAccess.getPlayerById(getIdPlayer2());
	}
	
	public boolean isPact() {
		return (this.getType().equals(TYPE_ALLY) || this.getType().equals(TYPE_DEFENSIVE) || this.getType().equals(TYPE_TOTAL));
	}
	
	public String getPact() {
		return getType().equals(TYPE_ALLY)? Treaty.ALLIED : getType();
	}
	
	public static boolean isPact(String pact) {
		return pact.equals(ALLIED) ||  pact.equals(DEFENSIVE) || pact.equals(TOTAL);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
