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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class ResearchBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idTechnology;
	private double progress;
	private int queuePosition;
	private int idPlayer;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdTechnology() {
		return idTechnology;
	}
	
	public void setIdTechnology(int idTechnology) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idTechnology = idTechnology;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void setProgress(double progress) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.progress = progress;
	}
	
	public int getQueuePosition() {
		return queuePosition;
	}
	
	public void setQueuePosition(int queuePosition) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.queuePosition = queuePosition;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
