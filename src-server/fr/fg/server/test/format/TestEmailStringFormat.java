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

import fr.fg.server.servlet.format.EmailStringFormat;
import junit.framework.TestCase;

public class TestEmailStringFormat extends TestCase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testFormat() {
		EmailStringFormat nsf = new EmailStringFormat();
		
		assertEquals("test@test.fr", nsf.format("  test@test.fr "));
		assertEquals(null, nsf.format("testé@test.fr"));
		assertEquals(null, nsf.format("test@te"));
		assertEquals(null, nsf.format("test@te."));
		assertEquals(null, nsf.format("@te.fr"));
		assertEquals(null, nsf.format(null));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
