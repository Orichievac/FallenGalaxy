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

import fr.fg.server.core.ContainerTools;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SwapFleetStorehouse extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		int idFleet = (Integer) params.get("fleet");
		long idStructure = (Long) params.get("structure");
		
		// Récupère la flotte et la structure
		Fleet fleet = FleetTools.getFleetByIdWithChecks(
			idFleet, player.getId());
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
				idStructure, player.getId());
		
		// Vérifie que la structure est un silo
		if (structure.getType() != Structure.TYPE_STOREHOUSE)
			throw new IllegalOperationException("Type de structure invalide.");
		
		// Vérifie que le silo est activé
		if (!structure.isActivated())
			throw new IllegalOperationException("Le silo est désactivé.");
		
		// Vérifie que la flotte est dans le même secteur que la structure
		if (fleet.getIdCurrentArea() != structure.getIdArea())
			throw new IllegalOperationException(
				"La flotte n'est pas dans le même secteur que la structure.");
		
		// Vérifie que la flotte est sur la structure
		if (!structure.getBounds().contains(fleet.getCurrentX(), fleet.getCurrentY()))
			throw new IllegalOperationException(
				"La flotte est trop éloignée de la structure.");
		
		// Ressources stockées par le joueur sur les silos du secteurs
		StorehouseResources storehouseResources = fleet.getArea(
			).getStorehouseResourcesByPlayer(player.getId());
		
		double[] playerResources = new double[4];
		if (storehouseResources != null) {
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				playerResources[i] = storehouseResources.getResource(i);
		}
		
		ItemContainer storehouseContainerBefore = new ItemContainer(
			ItemContainer.CONTAINER_STRUCTURE, structure.getId());
		storehouseContainerBefore.setResources(playerResources);
		
		ItemContainer fleetContainerBefore = fleet.getItemContainer();
		
		// Nouvelles ressources
		ItemContainer fleetContainerAfter = new ItemContainer(
			ItemContainer.CONTAINER_FLEET, fleet.getId());
		
		for (int i = 0; i < fleetContainerBefore.getMaxItems(); i++) {
			int type = (Integer) params.get("item" + i + "_type");
			long id = (Long) params.get("item" + i + "_id");
			long count = (Long) params.get("item" + i + "_count");
			
			Item item = new Item(type, id, count);
			fleetContainerAfter.setItem(item, i);
		}
		

		
		ItemContainer storehouseContainerAfter = ContainerTools.swap(
			fleetContainerBefore, fleetContainerAfter, fleet.getPayload(),
			storehouseContainerBefore, storehouseContainerBefore.getPayload());
		
		// Transfert les ressources sur la flotte
		synchronized (fleetContainerBefore) {
			fleetContainerBefore = DataAccess.getEditable(fleetContainerBefore);
			fleetContainerBefore.copy(fleetContainerAfter);
			fleetContainerBefore.save();
		}
		
		// Stocke les ressources
		if (storehouseResources == null) {
			storehouseResources = new StorehouseResources(fleet.getIdCurrentArea(), player.getId());
			storehouseResources.setResources(storehouseContainerAfter.getResourcesAsLong());
			storehouseResources.save();
		} else {
			synchronized (storehouseResources) {
				storehouseResources = DataAccess.getEditable(storehouseResources);
				storehouseResources.setResources(storehouseContainerAfter.getResourcesAsLong());
				storehouseResources.save();
			}
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(), player.getId(),
			new Point(fleet.getCurrentX(), fleet.getCurrentY()));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getPlayerSystemsUpdate(),
			Update.getAreaUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
