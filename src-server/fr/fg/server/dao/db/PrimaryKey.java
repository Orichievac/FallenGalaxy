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

package fr.fg.server.dao.db;

public class PrimaryKey {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		GENERATOR_NONE = 0,
		GENERATOR_INCREMENT = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String name;
	private int generator;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public PrimaryKey(String name, int generator) {
		super();
		this.name = name;
		this.generator = generator;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getGenerator() {
		return generator;
	}
	
	public void setGenerator(int generator) {
		this.generator = generator;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
