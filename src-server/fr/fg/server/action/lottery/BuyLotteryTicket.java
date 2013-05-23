/*
Copyright 2011 Jeremie Gottero
 
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
 
package fr.fg.server.action.lottery;
 
import java.util.List;
import java.util.Map;
 
import fr.fg.client.data.LotteryData;
import fr.fg.server.core.FleetTools;
import fr.fg.server.core.LotteryTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.LotteryTicket;
import fr.fg.server.data.Player;
import fr.fg.server.data.StellarObject;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
 
public class BuyLotteryTicket extends Action {
        // ------------------------------------------------------- CONSTANTES -- //
        // -------------------------------------------------------- ATTRIBUTS -- //
        // ---------------------------------------------------- CONSTRUCTEURS -- //
        // --------------------------------------------------------- METHODES -- //
       
        @Override
        protected String execute(Player player, Map<String, Object> params,
                        Session session) throws Exception {
                int idFleet = (Integer) params.get("fleet");
                int ticketLevel = (Integer) params.get("level");
                int number = (Integer) params.get("number");
               
                Fleet fleet = FleetTools.getFleetByIdWithChecks(
                                idFleet, player.getId());
               
                // Vérifie que la flotte est à proximité d'une loterie
                boolean nearLottery = false;
                List<StellarObject> objects = fleet.getArea().getObjects();
               
                synchronized (objects) {
                        for (StellarObject object : objects) {
                                if (object.getType() == StellarObject.TYPE_LOTTERY) {
                                	 int dx = object.getX() - fleet.getX();
                                     int dy = object.getY() - fleet.getY();
                                    
                                     if (dx * dx + dy * dy < LotteryData.LOTTERY_RADIUS *
                                                     LotteryData.LOTTERY_RADIUS) {
                                                nearLottery = true;
                                                break;
                                        }
                                }
                        }
                }
               
                if (!nearLottery)
                        throw new IllegalOperationException("La flotte n'est pas à proximité d'une loterie");
               
                // Vérifie que le joueur n'a pas déjà acheté un ticket
                LotteryTicket ticket = player.getLotteryTicket();
               
                if (ticket != null)
                        throw new IllegalOperationException("Vous avez déjà acheté un ticket de loterie !");
               
                // Vérifie que le joueur a suffisamment de crédits pour acheter un ticket
                int cost = LotteryTicket.TICKET_CREDITS_COST[ticketLevel];
               
                player = Player.updateCredits(player);
               
                if (cost > player.getCredits())
                        throw new IllegalOperationException("Vous n'avez pas suffisamment de crédits pour acheter un ticket.");
               
                synchronized (player.getLock()) {
                        player = DataAccess.getEditable(player);
                        player.addCredits(-cost);
                        player.save();
                }
               
                ticket = new LotteryTicket(player.getId(), ticketLevel, number);
                ticket.save();
               
                return LotteryTools.getLotteryInfo(null, player).toString();
        }
       
        // ------------------------------------------------- METHODES PRIVEES -- //
}