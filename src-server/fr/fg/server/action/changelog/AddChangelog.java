/*
Copyright 2012 jgottero

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

package fr.fg.server.action.changelog;

import java.util.Map;

import fr.fg.server.core.ChangelogTools;
import fr.fg.server.core.MessageTools;
import fr.fg.server.data.Changelog;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class AddChangelog extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String text = MessageTools.sanitizeHTML((String) params.get("text"));
		
		Changelog changelog = new Changelog(text);
		changelog.save();
		
		return ChangelogTools.getLastChangelogs(null).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
