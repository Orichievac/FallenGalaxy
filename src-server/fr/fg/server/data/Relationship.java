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

import fr.fg.server.data.base.RelationshipBase;

public class Relationship extends RelationshipBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int[] RELATIONSHIP_LEVELS = {30, 70, 200, 400};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Relationship() {
		// Nécessaire pour la construction par réflection
	}
	
	public Relationship(int idPlayer, int idAlly, double value) {
		setIdPlayer(idPlayer);
		setIdAlly(idAlly);
		setValue(value);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addValue(double value) {
		setValue(getValue() + value);
	}
	
	public Ally getAlly() {
		return DataAccess.getAllyById(getIdAlly());
	}
	
	public Player getPlayer() {
		return DataAccess.getPlayerById(getIdPlayer());
	}
	
	public static int getRelationshipLevel(double value) {
		double absoluteValue = Math.abs(value);
		int signum = (int) Math.signum(value);
		
		for (int i = 0; i < RELATIONSHIP_LEVELS.length; i++)
			if (absoluteValue < RELATIONSHIP_LEVELS[i])
				return i * signum;
		
		return RELATIONSHIP_LEVELS.length * signum;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
