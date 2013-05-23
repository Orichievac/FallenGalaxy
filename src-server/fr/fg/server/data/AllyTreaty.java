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

import fr.fg.server.data.base.AllyTreatyBase;
import fr.fg.server.util.Utilities;

public class AllyTreaty extends AllyTreatyBase {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static String
	ENEMY = "enemy",
	ALLY = "ally",
	DEFENSIVE = "defensive",
	TOTAL = "total",
	ALLIED = "allied",
	NEUTRAL = "neutral";
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

    public AllyTreaty() {
		// Nécessaire pour la construction par réflection
    }

    public AllyTreaty(int idAlly1, int idAlly2, String type, int source) {
       setIdAlly1(idAlly1);
       setIdAlly2(idAlly2);
       setType(type);
       setDate(Utilities.now());
       setLastActivity(getDate());
       setSource(source);
    }
   
    // --------------------------------------------------------- METHODES -- //
    
	public boolean implyAlly(int idAlly) {
		return getIdAlly1() == idAlly || getIdAlly2() == idAlly;
	}
	
	public int getOtherAllyId(int idAlly){
		return (getIdAlly1()==idAlly?getIdAlly2():getIdAlly1());
	}
	
	public Ally getOtherAlly(int idAlly){
		return DataAccess.getAllyById(getOtherAllyId(idAlly));
	}
	
	public Ally getAlly1() {
		return DataAccess.getAllyById(getIdAlly1());
	}
	
	public Ally getAlly2() {
		return DataAccess.getAllyById(getIdAlly2());
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


