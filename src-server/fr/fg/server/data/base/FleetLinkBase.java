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

public class FleetLinkBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		LINK_DEFENSIVE = "defensive",
		LINK_OFFENSIVE = "offensive",
		LINK_DELUDE = "delude";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int idSrcFleet;
	private int idDstFleet;
	private String link;
	private long date;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getIdSrcFleet() {
		return idSrcFleet;
	}
	
	public void setIdSrcFleet(int idSrcFleet) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idSrcFleet = idSrcFleet;
	}
	
	public int getIdDstFleet() {
		return idDstFleet;
	}
	
	public void setIdDstFleet(int idDstFleet) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idDstFleet = idDstFleet;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (link == null)
			throw new IllegalArgumentException("Invalid value: '" +
				link + "' (must not be null).");
		else if (link.equals(LINK_DEFENSIVE))
			this.link = LINK_DEFENSIVE;
		else if (link.equals(LINK_OFFENSIVE))
			this.link = LINK_OFFENSIVE;
		else if (link.equals(LINK_DELUDE))
			this.link = LINK_DELUDE;
		else
			throw new IllegalArgumentException(
				"Invalid value: '" + link + "'.");
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
