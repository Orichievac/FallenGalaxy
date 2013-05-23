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

package fr.fg.client.core;

import fr.fg.client.ajax.Action;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;

public class Lagometer extends BaseWidget implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Lagometer() {
		getElement().setId("lagometer");
		
		TimerManager.register(this, TimerManager.SECOND_UNIT);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void update(int interpolation) {
		if (Action.isDisconnected()) {
			setStyleName("lag-disconnected");
			
			setToolTipText("<div class=\"title\">Lagomètre</div>" +
				"<div style=\"color: red;\">Déconnecté</div>");
		} else {
			int lagLevel = Math.min(3, Action.getLastLag() / 200);
			setStyleName("lag" + lagLevel);
			
			setToolTipText("<div class=\"title\">Lagomètre</div>" +
				"<div>Latence&nbsp;actuelle&nbsp;:&nbsp;" + Action.getLastLag() + "ms</div>" +
				"<div>Latence&nbsp;moyenne&nbsp;:&nbsp;" + Action.getAverageLag() + "ms</div>" +
				"<div>Erreurs&nbsp;réseau&nbsp;:&nbsp;" + Action.getErrorsCount() + "</div>");
		}
	}
	
	public void destroy() {
		// Non utilisé
	}
	
	public boolean isFinished() {
		return false;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
