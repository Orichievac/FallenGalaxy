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

public class AllyNewsBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String title;
	private String content;
	private long date;
	private int idAuthor;
	private int idVote;
	private int idElection;
	private int idApplicant;
	private int idAlly;
	private int idParent;
	private boolean sticky;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
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
		
		if (date < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				date + "' (must be >= 0).");
		else
			this.date = date;
	}
	
	public int getIdAuthor() {
		return idAuthor;
	}
	
	public void setIdAuthor(int idAuthor) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idAuthor < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idAuthor + "' (must be >= 0).");
		else
			this.idAuthor = idAuthor;
	}
	
	public int getIdVote() {
		return idVote;
	}
	
	public void setIdVote(int idVote) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idVote < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idVote + "' (must be >= 0).");
		else
			this.idVote = idVote;
	}
	
	public int getIdElection() {
		return idElection;
	}
	
	public void setIdElection(int idElection) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idElection < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idElection + "' (must be >= 0).");
		else
			this.idElection = idElection;
	}
	
	public int getIdApplicant() {
		return idApplicant;
	}
	
	public void setIdApplicant(int idApplicant) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idApplicant < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idApplicant + "' (must be >= 0).");
		else
			this.idApplicant = idApplicant;
	}
	
	public int getIdAlly() {
		return idAlly;
	}
	
	public void setIdAlly(int idAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idAlly < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idAlly + "' (must be >= 0).");
		else
			this.idAlly = idAlly;
	}
	
	public int getIdParent() {
		return idParent;
	}
	
	public void setIdParent(int idParent) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idParent < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idParent + "' (must be >= 0).");
		else
			this.idParent = idParent;
	}
	
	public boolean isSticky() {
		return sticky;
	}
	
	public void setSticky(boolean sticky) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.sticky = sticky;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
