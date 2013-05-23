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

import java.util.HashMap;
import java.util.List;

import fr.fg.server.data.base.ElectionBase;
import fr.fg.server.util.Utilities;

public class Election extends ElectionBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public static final int FREQUENCY = 21;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public Election() {
		// Nécessaire pour la construction par réflection
	}

	public Election(long date, int idAlly) {
		setDate(date);
		setIdAlly(idAlly);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public Ally getAlly() {
		if (getIdAlly() == 0)
			return null;
		return DataAccess.getAllyById(getIdAlly());
	}
	
	public boolean isEnded(){
		return (((Utilities.now()-getDate())/Utilities.SEC_IN_DAY)>=4?true:false);
	}
	
	public Player getWinner(){
		
		List<ElectionVoter> votes = DataAccess.getElectionVoterById(getId());
		
		HashMap<Player, Integer> candidats = new HashMap<Player, Integer>();
		
		int max = 0;
		
		Player elected = null;
		
		for(int i=0;i<votes.size();i++){
			Player player = DataAccess.getPlayerById(votes.get(i).getIdCandidate());				
			
			candidats.put(player, (candidats.get(player)!=null?candidats.get(player)+1:1));
			
			if(candidats.get(player)>max){
				elected = player;
				max ++;
			}
		}
		
		return elected;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
