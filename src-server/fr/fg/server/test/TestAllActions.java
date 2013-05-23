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

package fr.fg.server.test;

import fr.fg.server.test.action.TestAction;
import fr.fg.server.test.action.allies.TestApply;
import fr.fg.server.test.action.fleet.TestJumpHyperspace;
import fr.fg.server.test.action.fleet.TestMoveFleet;
import fr.fg.server.test.action.fleet.TestSetFleetName;
import fr.fg.server.test.action.ladder.TestGetLadder;
import fr.fg.server.test.action.messages.TestDeleteMessage;
import fr.fg.server.test.action.messages.TestGetMessages;
import fr.fg.server.test.action.messages.TestSendMessage;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllActions {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite("Test for fr.fg.test.action");
		
		TestAction.setOutEnabled(false);
		
		// Allies
		suite.addTestSuite(TestApply.class);
		
		// Chat
		
		// Fleet
		suite.addTestSuite(TestJumpHyperspace.class);
		suite.addTestSuite(TestMoveFleet.class);
		suite.addTestSuite(TestSetFleetName.class);
		
		// Ladder
		suite.addTestSuite(TestGetLadder.class);
		
		// Messages
		suite.addTestSuite(TestDeleteMessage.class);
		suite.addTestSuite(TestGetMessages.class);
		suite.addTestSuite(TestSendMessage.class);
		
		return suite;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
