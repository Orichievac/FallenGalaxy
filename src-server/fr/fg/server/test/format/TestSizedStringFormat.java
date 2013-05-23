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

import fr.fg.server.servlet.format.Format;
import fr.fg.server.servlet.format.SizedStringFormat;
import junit.framework.TestCase;

public class TestSizedStringFormat extends TestCase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void testFormat() {
		SizedStringFormat ssf = new SizedStringFormat();
		ssf.setArgs(Format.BETWEEN, 5, 10);
		
		assertEquals("chaine", ssf.format("chaine"));
		assertEquals(null, ssf.format("une chaine trop longue"));
		assertEquals(null, ssf.format("test"));
		assertEquals(null, ssf.format(null));
		
		ssf.setArgs(Format.SUPERIOR_EQUAL, 5);
		
		assertEquals("chaine", ssf.format("chaine"));
		assertEquals("une chaine trop longue",
				ssf.format("une chaine trop longue"));
		assertEquals(null, ssf.format("test"));
		assertEquals(null, ssf.format(null));
		
		ssf.setArgs(Format.INFERIOR_EQUAL, 5);
		
		assertEquals("chain", ssf.format("chain"));
		assertEquals(null, ssf.format("une chaine trop longue"));
		assertEquals("test", ssf.format("test"));
		assertEquals(null, ssf.format(null));
		
		ssf.setArgs(Format.NOT_EQUAL, 5);
		
		assertEquals(null, ssf.format("chain"));
		assertEquals("une chaine trop longue",
				ssf.format("une chaine trop longue"));
		assertEquals("test", ssf.format("test"));
		assertEquals(null, ssf.format(null));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
