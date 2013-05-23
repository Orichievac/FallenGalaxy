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

package fr.fg.server.test.format;

import fr.fg.server.servlet.format.EnumFormat;
import junit.framework.TestCase;

public class TestEnumFormat extends TestCase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void testFormat() {
		EnumFormat ef = new EnumFormat();
		ef.setArgs("value1", "value2");
		
		assertEquals("value1", ef.format("value1"));
		assertEquals("value2", ef.format("value2"));
		assertEquals(null, ef.format("value3"));
		assertEquals(null, ef.format(null));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
