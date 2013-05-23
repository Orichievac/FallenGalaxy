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

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllFormats {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for fr.fg.test.format");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestNameStringFormat.class);
		suite.addTestSuite(TestEnumFormat.class);
		suite.addTestSuite(TestBooleanFormat.class);
		suite.addTestSuite(TestIntegerFormat.class);
		suite.addTestSuite(TestIntegerRangeFormat.class);
		suite.addTestSuite(TestStringFormat.class);
		suite.addTestSuite(TestSizedStringFormat.class);
		//$JUnit-END$
		return suite;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
