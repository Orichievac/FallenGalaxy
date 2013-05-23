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

package fr.fg.server.servlet.format;

public interface Format {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int SUPERIOR_EQUAL = 1;
	public final static int INFERIOR_EQUAL = 2;
	public final static int BETWEEN = 3;
	public final static int NOT_EQUAL = 4;
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setArgs(Object... args);
	
	/**
	 * Formate la variable dans un type et avec une syntaxe définie par les
	 * classes qui implémentent cette méthode. Si la variable ne peut être
	 * formatée, la méthode renvoie <code>null</code>.
	 *
	 * @param var La variable à formater.
	 *
	 * @return Le résultat du formatage, ou <code>null</code> si la
	 * variable ne peut être formatée.
	 */
	public Object format(String var);
}
