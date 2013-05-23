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

import java.util.List;
import java.util.Map;


import fr.fg.server.core.MessageTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class CreateMarker extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idArea = (Integer) params.get("area");
		int x = (Integer) params.get("x");
		int y = (Integer) params.get("y");
		String message = (String) params.get("message");
		String visibility = (String) params.get("visibility");
		boolean galaxy = (Boolean) params.get("galaxy");
		
		Area area = DataAccess.getAreaById(idArea);
		
		// Vérifie que le secteur existe
		if (area == null)
			throw new IllegalOperationException("Le secteur n'existe pas.");
		
		// Vérifie que les coordonnées sont valides
		if (x >= area.getWidth() || y >= area.getHeight())
			throw new IllegalOperationException("Coordonnées du marqueur invalide.");
		
		// Vérifie que le message ne contient pas l'ekey du joueur
		if (player.getEkey().length() > 0 && message.contains(player.getEkey()))
			throw new IllegalOperationException("Ne transmettez pas votre clé d'export à d'autres joueurs.");
		
		// Supprime le marqueur le plus vieux posé par le joueur s'il en a
		// déjà posé 25
		List<Marker> markers = DataAccess.getMarkersByPlayer(player.getId());
		Marker firstMarker = null;
		int markersCount = 0;
		
		synchronized (markers) {
			for (Marker marker : markers) {
				if (marker.getIdContract() == 0) {
					markersCount++;
					
					if (firstMarker == null)
						firstMarker = marker;
					else if (firstMarker.getDate() > marker.getDate())
						firstMarker = marker;
				}
			}
		}
		
		if (markersCount >= 25)
			firstMarker.delete();
		
		Marker marker = new Marker(x, y,
			Badwords.parse(MessageTools.tidyHTML(message)), visibility,
			galaxy, Utilities.now() + GameConstants.MARKER_LENGTH, idArea,
			player.getId(), 0);
		
		DataAccess.save(marker);
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(marker.getIdArea());
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
