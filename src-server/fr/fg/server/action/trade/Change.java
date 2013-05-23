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

package fr.fg.server.action.trade;

import java.util.List;
import java.util.Map;


import fr.fg.server.core.FleetTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.Tradecenter;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.AfterTradeEvent;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class Change extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int idFleet = (Integer) params.get("fleet");
		int resource = (Integer) params.get("resource");
		long count = (Long) params.get("count");
		
		// Détermine s'il s'agit d'une vente ou d'un achat
		boolean sell = count < 0;
		if (sell)
			count = -count;
		
		Fleet fleet = FleetTools.getFleetByIdWithChecks(idFleet, player.getId());
		
		// Vérifie que le secteur n'est pas sous domination d'une alliance
		// autre que celle du joueur
		// TODO à remettre
//		int idDominatingAlly = fleet.getArea().getIdDominatingAlly();
//		if (idDominatingAlly != 0 && idDominatingAlly != player.getIdAlly())
//			throw new IllegalOperationException("Ce secteur est sous domination " +
//					"d'une alliance autre que la votre. Il est impossible " +
//					"d'y échanger des ressources.");
		
		// Recherche le centre de commerce sur lequel la flotte est placée
		List<StellarObject> objects = fleet.getArea().getObjects();
		int idTradecenter = -1;
		
		synchronized (objects) {
			for (StellarObject object : objects)
				if (object.getType().equals(StellarObject.TYPE_TRADECENTER) &&
						object.getBounds().contains(
							fleet.getCurrentX(), fleet.getCurrentY())) {
					idTradecenter = object.getId();
					break;
				}
		}
		
		if (idTradecenter == -1)
			throw new IllegalOperationException("La flotte n'est pas " +
					"placée sur un centre de commerce.");
		
		Tradecenter tradecenter = DataAccess.getTradecenterById(idTradecenter);
		
		// Dans le cas d'une vente, vérifie qu'il y a suffisament de ressources
		// sur la flotte
		ItemContainer itemContainer = fleet.getItemContainer();
		if (sell && itemContainer.getResource(resource) < count)
			throw new IllegalOperationException(
				"Vous n'avez pas suffisament de ressources à transférer.");
		
		// Calcule le taux de change en fonction de la quantité échangée
		double rateBefore = tradecenter.getRate(resource);
		double rateAfter = tradecenter.getRate(resource) +
			(sell ? -1 : 1) * count * tradecenter.getVariation();
		
		double rate = (rateAfter + rateBefore) / 2;
		double min = .005;
		double max = 9.52;
		
		if (rateAfter < min) {
			double coef = (rateBefore - min) / (rateBefore - rateAfter);
			rate = coef * (rateBefore - min) / 2 + (1 - coef) * min;
			rateAfter = min;
		} else if (rateAfter > max) {
//			double coef = (max - rateBefore) / (rateAfter - rateBefore);
//			rate = coef * (max - rateBefore) / 2 + (1 - coef) * max;
			rate=(rateAfter+rateBefore)/2; // remplacement des 2 lignes:à modifier plus tard
			rateAfter = max;
		} else {
			rate = (rateAfter + rateBefore) / 2;
		}
		
		rate *= (1 + (sell ?
				-tradecenter.getPlayerFees(player.getId()) :
				tradecenter.getPlayerFees(player.getId())));
		
		// Met à jour les crédits du joueur
		player = Player.updateCredits(player);
		
		long credits = (long) (sell ?
			Math.floor(rate * count) :
			Math.ceil(rate * count));
		
		if (!sell && player.getCredits() < credits)
			throw new IllegalOperationException(
					"Vous n'avez pas suffisament de crédits.");
		
		if (!sell && itemContainer.getCompatibleOrFreePosition(Item.TYPE_RESOURCE, resource) == -1)
			throw new IllegalOperationException("La flotte n'a pas " +
				"d'emplacement de libre pour recevoir les ressources.");
		
		// Ajoute ou retire les crédits gagnés
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			player.addCredits(sell ? credits : -credits);
			player.save();
		}
		
		if (sell) {
			// Retire les ressources vendues de la flotte
			synchronized (itemContainer.getLock()) {
				itemContainer = DataAccess.getEditable(itemContainer);
				itemContainer.addResource(-count, resource);
				itemContainer.save();
			}
		} else {
			// Ajoute les ressources achetées à la flotte, en supprimant le
			// trop plein
			double totalWeight = itemContainer.getTotalWeight();
			
			long newResources = count;
			if (fleet.getPayload() < totalWeight + count)
				newResources = (long) Math.floor(fleet.getPayload() - totalWeight);
			
			synchronized (itemContainer.getLock()) {
				itemContainer = DataAccess.getEditable(itemContainer);
				itemContainer.addResource(newResources, resource);
				itemContainer.save();
			}
		}
		
		// Met à jour les taux du centre de commerce
		synchronized (tradecenter.getLock()) {
			tradecenter = DataAccess.getEditable(tradecenter);
			tradecenter.setRate(rateAfter, resource);
			tradecenter.save();
		}
		
		GameEventsDispatcher.fireGameEvent(new AfterTradeEvent(fleet, sell, resource, count));
		
		return UpdateTools.formatUpdates(
			player,
			Update.getPlayerFleetUpdate(fleet.getId()),
			Update.getPlayerSystemsUpdate()
		);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
