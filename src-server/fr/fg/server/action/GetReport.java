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

package fr.fg.server.action;

import java.util.Map;


import fr.fg.server.core.ReportTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Report;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class GetReport extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Report report = DataAccess.getReportById((Integer) params.get("report"));
		
		if (report == null)
			throw new IllegalOperationException("Rapport de combat inexistant.");
		
		if (report.getIdPlayerAttacking() != player.getId() &&
				report.getIdPlayerDefending() != player.getId())
			throw new IllegalOperationException("Ce rapport de combat ne vous appartient pas.");
		
		return ReportTools.getReport(null, player, report).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
