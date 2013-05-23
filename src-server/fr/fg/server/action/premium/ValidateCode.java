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

package fr.fg.server.action.premium;

import java.util.Map;

import fr.fg.server.core.PremiumTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;

public class ValidateCode extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String code = (String) params.get("code");
		
		if (!PremiumTools.validateCode(player, session.getForwardedHeader(), code))
			throw new IllegalOperationException("Le code est invalide. " +
				"En cas de problème, contactez-nous à l'adresse suivante " +
				"<a href=\"mailto:" + Config.getMailFrom() + "\">" +
				Config.getMailFrom() + "</a> en nous précisant votre code.");
		
		return PremiumTools.getPremiumState(null, DataAccess.getPlayerById(player.getId()), session.getForwardedHeader()).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
