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

package fr.fg.server.action.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetSettings extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		boolean grid = (Boolean) params.get("grid");
		int brightness = (Integer) params.get("brightness");
		int fleetsSkin = (Integer) params.get("fleetsSkin");
		String theme = (String) params.get("theme");
		boolean censorship = (Boolean) params.get("censorship");
		int generalVolume = (Integer) params.get("generalvol");
		int soundVolume = (Integer) params.get("soundvol");
		int musicVolume = (Integer) params.get("musicvol");
		int graphics = (Integer) params.get("graphics");
		boolean optimizeConnection = (Boolean) params.get("optimConnect");
		
		boolean premium = player.hasRight(Player.PREMIUM);
		
		// Vérifie que les options ne sont pas réservées aux comptes premiums
		if ((fleetsSkin > 1 || !theme.equals(GameConstants.THEMES[0]) ||
				generalVolume > 0 || soundVolume > 0 || musicVolume > 0 ||
				graphics > 0) && !premium)
			throw new IllegalOperationException("Vous devez avoir un compte " +
				"premium pour accèder à cette fonctionnalité.");
		
		boolean updateArea = player.getSettingsFleetSkin() != fleetsSkin;
		
		// Sauvegarde les préférences
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.setSettingsBrightness(brightness);
			player.setSettingsFleetSkin(fleetsSkin);
			player.setSettingsGridVisible(grid);
			player.setSettingsTheme(theme);
			player.setSettingsCensorship(censorship);
			player.setSettingsGeneralVolume(generalVolume);
			player.setSettingsSoundVolume(soundVolume);
			player.setSettingsMusicVolume(musicVolume);
			player.setSettingsGraphics(graphics);
			player.setSettingsOptimizeConnection(optimizeConnection);
			player.save();
			
			if (updateArea)
				UpdateTools.queueAreaUpdate(player);
		}
		
		List<Update> updates = new ArrayList<Update>();
		
		if (updateArea)
			updates.add(Update.getAreaUpdate());
		
		return UpdateTools.formatUpdates(
			player,
			updates
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
