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

public class MessageBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String title;
	private String content;
	private long date;
	private int idAuthor;
	private int idPlayer;
	private int deleted;
	private boolean opened;
	private boolean bookmarked;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (title == null)
			throw new IllegalArgumentException("Invalid value: '" +
				title + "' (must not be null).");
		else
			this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (content == null)
			throw new IllegalArgumentException("Invalid value: '" +
				content + "' (must not be null).");
		else
			this.content = content;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.date = date;
	}
	
	public int getIdAuthor() {
		return idAuthor;
	}
	
	public void setIdAuthor(int idAuthor) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idAuthor = idAuthor;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public void setIdPlayer(int idPlayer) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idPlayer = idPlayer;
	}
	
	public int getDeleted() {
		return deleted;
	}
	
	public void setDeleted(int deleted) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.deleted = deleted;
	}
	
	public boolean isOpened() {
		return opened;
	}
	
	public void setOpened(boolean opened) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.opened = opened;
	}
	
	public boolean isBookmarked() {
		return bookmarked;
	}
	
	public void setBookmarked(boolean bookmarked) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.bookmarked = bookmarked;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
