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

import java.util.HashMap;
import java.util.Map;

public class PrimitiveLoader extends ClassLoader {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Map<String, Class<?>> nameToPrimitiveClass =
		new HashMap<String, Class<?>>();
	
	static {
		nameToPrimitiveClass.put("boolean", Boolean.TYPE);
		nameToPrimitiveClass.put("byte", Byte.TYPE);
		nameToPrimitiveClass.put("char", Character.TYPE);
		nameToPrimitiveClass.put("short", Short.TYPE);
		nameToPrimitiveClass.put("int", Integer.TYPE);
		nameToPrimitiveClass.put("long", Long.TYPE);
		nameToPrimitiveClass.put("float", Float.TYPE);
		nameToPrimitiveClass.put("double", Double.TYPE);
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PrimitiveLoader() {
		super();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = nameToPrimitiveClass.get(name);
		if (c == null)
			throw new ClassNotFoundException(name);
		return c;
	}
	
	public String toString() {
		return "PrimitiveLoader";
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
