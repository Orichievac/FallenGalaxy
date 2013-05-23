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

package fr.fg.server.test.action.settings;

import org.json.JSONObject;

import fr.fg.server.data.Player;
import fr.fg.server.test.action.TestAction;

public class TestSetSettings extends TestAction {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String URI = "setsettings";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void testSetSettings() throws Exception {
		boolean grid = false;
		int brightness = 0;
		int skin = 1;
		
		JSONObject answer = doRequest(URI,
				"grid=" + grid + "&" +
				"brightness=" + brightness + "&" +
				"skin=" + skin);
		assertEquals("success", answer.get("type"));
		
		Player player = getPlayer();
		assertEquals(grid, player.isSettingsGridVisible());
		assertEquals(brightness, player.getSettingsBrightness());
		assertEquals(skin, player.getSettingsFleetSkin());
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
