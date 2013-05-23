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

package fr.fg.server.action.markers;

import java.util.Map;


import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DeleteMarker extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idMarker = (Integer) params.get("marker");
		
		Marker marker = DataAccess.getMarkerById(idMarker);
		
		// Vérifie que la balise existe
		if (marker == null)
			throw new IllegalOperationException("Ce message n'existe pas.");
		
		// Vérifie que le joueur a le droit de supprimer la balise
		if (!marker.isVisibleFromPlayer(player))
			throw new IllegalOperationException("Ce message ne vous appartient pas.");
		
		// Vérifie que la balise n'est pas une balise automatique
		if (marker.getIdContract() != 0)
			throw new IllegalOperationException("Impossible de supprimer un message du jeu.");
		
		// Supprime la balise
		marker.delete();
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(marker.getIdArea());
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
