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
import fr.fg.server.servlet.format.LongRangeFormat;
import junit.framework.TestCase;

public class TestLongRangeFormat extends TestCase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void testFormat() {
		LongRangeFormat lrf = new LongRangeFormat();
		lrf.setArgs(Format.BETWEEN, 10, 20);
		
		assertEquals(15l, lrf.format("15"));
		assertEquals(10l, lrf.format("10"));
		assertEquals(null, lrf.format("5"));
		assertEquals(null, lrf.format("-1"));
		assertEquals(null, lrf.format("a"));
		assertEquals(null, lrf.format("1."));
		assertEquals(null, lrf.format(null));
		
		lrf.setArgs(Format.SUPERIOR_EQUAL, 10);
		
		assertEquals(15l, lrf.format("15"));
		assertEquals(10l, lrf.format("10"));
		assertEquals(null, lrf.format("5"));
		assertEquals(null, lrf.format("-1"));
		assertEquals(null, lrf.format("a"));
		assertEquals(null, lrf.format("1."));
		assertEquals(null, lrf.format(null));
		
		lrf.setArgs(Format.INFERIOR_EQUAL, 10);
		
		assertEquals(null, lrf.format("15"));
		assertEquals(10l, lrf.format("10"));
		assertEquals(5l, lrf.format("5"));
		assertEquals(-1l, lrf.format("-1"));
		assertEquals(null, lrf.format("a"));
		assertEquals(null, lrf.format("1."));
		assertEquals(null, lrf.format(null));
		
		lrf.setArgs(Format.NOT_EQUAL, 10);
		
		assertEquals(15l, lrf.format("15"));
		assertEquals(null, lrf.format("10"));
		assertEquals(5l, lrf.format("5"));
		assertEquals(-1l, lrf.format("-1"));
		assertEquals(null, lrf.format("a"));
		assertEquals(null, lrf.format("1."));
		assertEquals(null, lrf.format(null));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
