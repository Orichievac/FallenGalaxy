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

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.base.AllyNewsBase;
import fr.fg.server.util.Utilities;

public class AllyNews extends AllyNewsBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AllyNews() {
		// Nécessaire pour la construction par réflection
	}
	
	public AllyNews(String title, String content, int idAuthor, int idAlly, int idParent) {
		this(title, content, Utilities.now(), idAuthor, idAlly, idParent);
	}
	
	public AllyNews(String title, String content, long date, int idAuthor,
			int idAlly, int idParent) {
		setTitle(title);
		setContent(content);
		setDate(date);
		setIdAuthor(idAuthor);
		setIdVote(0);
		setIdElection(0);
		setIdApplicant(0);
		setIdAlly(idAlly);
		setIdParent(idParent);
		
		resetReadState();
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void resetReadState() {
		if (getIdParent() != 0) {
			List<AllyNewsRead> readList = new ArrayList<AllyNewsRead>(
					DataAccess.getAllyNewsReadByNews(getIdParent()));
			
			for (AllyNewsRead read : readList)
				if (read.getIdPlayer() != getIdAuthor())
					read.delete();
		}
	}
}
