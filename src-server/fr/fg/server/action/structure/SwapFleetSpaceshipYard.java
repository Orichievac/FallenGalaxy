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
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ship;
import fr.fg.server.data.Slot;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class SwapFleetSpaceshipYard extends Action {
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
		
		// Vérifie que la structure est un chantier spatial
		if (structure.getType() != Structure.TYPE_SPACESHIP_YARD)
			throw new IllegalOperationException("Type de structure invalide.");
		
		// Vérifie que le chantier spatial est activé
		if (!structure.isActivated())
			throw new IllegalOperationException("Le chantier spatial est désactivé.");
		
		// Vérifie que la flotte est dans le même secteur que la structure
		if (fleet.getIdCurrentArea() != structure.getIdArea())
			throw new IllegalOperationException(
				"La flotte n'est pas dans le même secteur que la structure.");
		
		// Vérifie que la flotte est sur la structure
		if (!structure.getBounds().contains(fleet.getCurrentX(), fleet.getCurrentY()))
			throw new IllegalOperationException(
				"La flotte est trop éloignée de la structure.");
		
		StructureSpaceshipYard spaceshipYard =
			DataAccess.getSpaceshipYardByStructure(structure.getId());
		
		synchronized (spaceshipYard.getLock()) {
			spaceshipYard = DataAccess.getEditable(spaceshipYard);
			spaceshipYard.update();
			spaceshipYard.save();
		}
		
		// Slots avant transfert
		Slot[] fleetSlotsBefore = fleet.getSlots();
		Slot[] spaceshipYardSlotsBefore = spaceshipYard.getSlots();
		
		// Slots après transfert
		Slot[] fleetSlotsAfter = new Slot[GameConstants.FLEET_SLOT_COUNT];
		long[] fleetShipsAfter = new long[Ship.SHIPS.length];
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			int id = (Integer) params.get("slot" + i + "_id");
			long count = id == 0 ? 0 : (Long) params.get("slot" + i + "_count");
			
			fleetSlotsAfter[i] = new Slot(id, count,
				fleetSlotsBefore[i].isFront()
			);
			fleetShipsAfter[id] = count;
		}
		
		// Vérifie qu'il n'y a pas 2x le même type de vaisseau dans
		// 2 slots différents
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			if (fleetSlotsAfter[i].getId() != 0) {
				for (int j = i + 1; j < GameConstants.FLEET_SLOT_COUNT; j++) {
					if (fleetSlotsAfter[j].getId() == fleetSlotsAfter[i].getId())
						throw new IllegalOperationException(
							"Le même vaisseau est présent sur deux slots différents.");
				}
			}
		}
		
		// Compte le nombre de vaisseaux avant transfert
		long[] fleetShipsBefore = new long[Ship.SHIPS.length];
		double[] spaceshipYardShipsBefore = new double[Ship.SHIPS.length];
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			if (fleetSlotsBefore[i].getId() != 0)
				fleetShipsBefore[fleetSlotsBefore[i].getId()] +=
					(long) fleetSlotsBefore[i].getCount();
		}
		
		for (int i = 0; i < GameConstants.FLEET_SLOT_COUNT; i++) {
			if (spaceshipYardSlotsBefore[i].getId() != 0)
				spaceshipYardShipsBefore[spaceshipYardSlotsBefore[i].getId()] +=
					spaceshipYardSlotsBefore[i].getCount();
		}
		
		// Calcule le nombre de vaisseaux restants sur le chantier spatial
		int currentSlot = 0;
		Slot[] spaceshipYardSlotsAfter = new Slot[GameConstants.FLEET_SLOT_COUNT];
		double[] spaceshipYardShipsAfter = new double[Ship.SHIPS.length];
		
		for (int i = 0; i < spaceshipYardSlotsAfter.length; i++)
			spaceshipYardSlotsAfter[i] = new Slot();
		
		for (int i = 1; i < Ship.SHIPS.length; i++) {
			if (fleetShipsAfter[i] != 0 || spaceshipYardShipsBefore[i] != 0 ||
					fleetShipsBefore[i] != 0) {
				double spaceshipYardShips = spaceshipYardShipsBefore[i] -
					fleetShipsAfter[i] + fleetShipsBefore[i];
				
				if (spaceshipYardShips < 0)
					throw new IllegalOperationException("Nombre de vaisseaux " +
						"à transférer invalide.");
				
				if (spaceshipYardShips > 0) {
					if (currentSlot >= 5)
						throw new IllegalOperationException("Types de vaisseaux " +
							"à transférer invalide.");
					
					spaceshipYardSlotsAfter[currentSlot] =
						new Slot(i, spaceshipYardShips, true);
					currentSlot++;
				}
				
				spaceshipYardShipsAfter[i] = spaceshipYardShips;
			}
		}
		
		// Vérifie qu'il reste un vaisseau sur la flotte
		boolean containsShips = false;
		for (int i = 1; i < fleetShipsAfter.length; i++)
			if (fleetShipsAfter[i] > 0) {
				containsShips = true;
				break;
			}
		
		if (!containsShips)
			throw new IllegalOperationException(
				"Une flotte ne peut pas être vide.");
		
		// Vérifie que la flotte a la capacité pour transporter les ressources
		ItemContainer itemContainer = fleet.getItemContainer();
		double totalWeight = itemContainer.getTotalWeight();
		
		if (totalWeight > Fleet.getPayload(fleetSlotsAfter))
			throw new IllegalOperationException("La flotte n'a pas " +
				"la capacité pour transporter les ressources.");
		
		// Vérifie que le niveau de la flotte ne dépasse pas le niveau du joueur
		if (Fleet.getLevelAtPower(Fleet.getPower(fleetSlotsAfter)) > player.getLevel())
			throw new IllegalOperationException(
				"La puissance d'une flotte ne peut dépasser le niveau d'XP.");
		
		if (Fleet.getPower(spaceshipYardSlotsAfter) > structure.getMaxShips())
			throw new IllegalOperationException(
				"Le chantier spatial ne peut pas contenir autant de vaisseaux.");
		
		
				
				
//		// Vérifie que le niveaux des flottes ne dépasse pas le niveau du quadrant
//		if (Fleet.getLevelAtPower(Fleet.getPower(fleetSlotsAfter)) >
//			fleet.getArea().getSector().getLvlMax())
//					throw new IllegalOperationException(
//						"La puissance d'une flotte ne peut dépasser la limite" +
//						" du quadrant : "+fleet.getArea().getSector().getLvlMax()+
//						".");
		
		// Transfert les vaisseaux
		synchronized (spaceshipYard.getLock()) {
			spaceshipYard = DataAccess.getEditable(spaceshipYard);
			spaceshipYard.setSlots(spaceshipYardSlotsAfter);
			spaceshipYard.save();
		}
		
		synchronized (fleet.getLock()) {
			fleet = DataAccess.getEditable(fleet);
			fleet.setSlots(fleetSlotsAfter);
			fleet.save();
		}
		
		// Met à jour l'affichage des joueurs connectés sur le secteur
		UpdateTools.queueAreaUpdate(fleet.getIdCurrentArea(),
			player.getId(), new Point(fleet.getX(), fleet.getY()));
		
		JSONStringer json = new JSONStringer();
		
		FleetTools.getPlayerFleet(json, fleet.getId());
		UpdateTools.queuePlayerFleetsUpdate(fleet.getIdOwner());
		UpdateTools.queuePlayerSystemsUpdate(fleet.getIdOwner());
		UpdateTools.queueAreaUpdate(fleet.getOwner());

		return json.toString();
		
		/*return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getAreaUpdate()
		);*/
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
