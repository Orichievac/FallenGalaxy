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

import fr.fg.server.data.base.AllyVoteBase;
import fr.fg.server.util.Utilities;

public class AllyVote extends AllyVoteBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public AllyVote() {
		// Nécessaire pour la construction par réflection
	}

	public AllyVote(String type, int yes, int no, long date, int idPlayer, int idAlly, int idInitiator) {
		setType(type);
		setYes(yes);
		setNo(no);
		setDate(date);
		setIdPlayer(idPlayer);
		setIdAlly(idAlly);
		setIdInitiator(idInitiator);
	}

    // --------------------------------------------------------- METHODES -- //
	
	public Ally getAlly() {
		if (getIdAlly() == 0)
			return null;
		return DataAccess.getAllyById(getIdAlly());
	}
	
	public Player getPlayer() {
		if (getIdPlayer() == 0)
			return null;
		return DataAccess.getPlayerById(getIdPlayer());
	}
	
	public Player getInitiator(){
		if(getIdInitiator()==0)
			return null;
		return DataAccess.getPlayerById(getIdInitiator());
	}
	
	public boolean isEnded(){
		return (((Utilities.now()-getDate())/Utilities.SEC_IN_DAY)>=4?true:false);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
