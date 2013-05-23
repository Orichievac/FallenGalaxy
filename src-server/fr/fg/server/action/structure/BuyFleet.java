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

package fr.fg.server.action.structure;

import java.awt.Point;
import java.util.Map;

import fr.fg.server.core.FleetTools;
import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class BuyFleet extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		long idStructure = (Long) params.get("structure");
		Structure structure = StructureTools.getStructureByIdWithChecks(idStructure, player.getId());
		
		if (!structure.isActivated())
			throw new IllegalOperationException("La structure est désactivée.");
		
		// Vérifie que la structure est un chantier spatial
		if (structure.getType() != Structure.TYPE_SPACESHIP_YARD)
			throw new IllegalOperationException("Structure invalide.");
		
		StructureSpaceshipYard spaceshipYard = structure.getSpaceshipYard();
		
		if (spaceshipYard.getLastBoughtFleet() + GameConstants.BUY_FLEET_COOLDOWN > Utilities.now())
			throw new IllegalOperationException("Vous devez attendre une heure " +
				"après avoir acheté une flotte pour en acheter une nouvelle.");
		
		// Met à jour les crédits disponibles
		player = Player.updateCredits(player);
		
		long price = player.getFleetCost();
		
		if(price<0)
			throw new IllegalOperationException(
					"Un bug est apparu sur votre connexion, veuillez vous reconnectez.");
		
		if (player.getCredits() < price)
			throw new IllegalOperationException(
					"Vous n'avez pas assez de crédits pour recruter une " +
					"flotte. " + price + " crédits nécessaires.");
		
		Point tile = structure.getFreeTile();
		
		if (tile == null)
			throw new IllegalOperationException("Il n'y a plus de place " +
					"sur la structure pour créer la flotte.");
		
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(-price);
			player.save();
		}
		
		String fleetName = player.getNextFleetName();
		
		Fleet createdFleet = new Fleet(fleetName, tile.x, tile.y,
				player.getId(), structure.getIdArea());
	    createdFleet.setSlot(new Slot(Ship.RECON, 10, true), 0);
	    createdFleet.save();
	    
		synchronized (spaceshipYard.getLock()) {
			spaceshipYard = DataAccess.getEditable(spaceshipYard);
			spaceshipYard.setLastBoughtFleet(Utilities.now());
			spaceshipYard.save();
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(createdFleet.getIdCurrentArea(),
				player.getId(),
				new Point(createdFleet.getX(), createdFleet.getY()));
		

		JSONStringer json = new JSONStringer();
		
		FleetTools.getPlayerFleet(json, createdFleet.getId());
		UpdateTools.queuePlayerFleetsUpdate(createdFleet.getIdOwner());
		UpdateTools.queuePlayerSystemsUpdate(createdFleet.getIdOwner());
		UpdateTools.queueAreaUpdate(createdFleet.getOwner());

		return json.toString();
		
		/*return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(createdFleet.getId()),
			Update.getPlayerSystemsUpdate(),
			Update.getAreaUpdate()
		);*/
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
