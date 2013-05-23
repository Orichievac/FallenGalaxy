/*
Copyright 2012 jgottero

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

public class ChangelogBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private String text;
	private long date;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if (!isEditable())
			throwDataUneditableException();
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		if (!isEditable())
			throwDataUneditableException();
		this.text = text;
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
