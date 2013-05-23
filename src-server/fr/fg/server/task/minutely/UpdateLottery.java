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
 
package fr.fg.server.task.minutely;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
 
import org.json.JSONException;
import org.json.JSONObject;
 
import fr.fg.client.data.ChatMessageData;
import fr.fg.server.action.chat.SendMessage;
import fr.fg.server.core.ChatManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.LotteryTicket;
import fr.fg.server.data.Player;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;
 
public class UpdateLottery implements Runnable {
        // ------------------------------------------------------- CONSTANTES -- //
       
        public final static String SIGNS = "ΦΔΣΨ";
       
        // Fréquence des tirages de loterie, en jours
        public final static int LOTTERY_DRAW_DAY_FREQUENCY = 2;
       
        // Les tirages ont lieu à 18:30
        public final static int LOTTERY_DRAW_HOUR = 18;
        public final static int LOTTERY_DRAW_MINUTE = 30;
       
        // -------------------------------------------------------- ATTRIBUTS -- //
        // ---------------------------------------------------- CONSTRUCTEURS -- //
        // --------------------------------------------------------- METHODES -- //
       
        public void run() {
                update();
        }
       
        public static void update() {
               
               
                
                       
                        // Procède au tirage
        	    if ((System.currentTimeMillis() % (48 * 3600000l)) / 60000 == 18.5 * 60){
                        	final List<LotteryTicket> tickets = new ArrayList<LotteryTicket>(DataAccess.getAllLotteryTickets());  // Efface les tickets
                            for (LotteryTicket ticket : tickets) {
                                ticket.delete();
                        }
                        proceed(tickets); 
                        };
                        
                }
        
       
        // ------------------------------------------------- METHODES PRIVEES -- //
       
        private static void sendPublicChannelMessage(String message) {
                String login = "*Loterie Eden*";
               
                for (int j = 1; j < 50; j++) {
                        String channel = ChatManager.PUBLIC_CHANNEL_PREFIX + j;
                        Set<Integer> players = ChatManager.getInstance().getChannelPlayers(channel);
                       
                        if (players.size() > 0) {
                                LoggingSystem.getChatLogger().info("[" + channel + "] " + login + ": " + message);
                               
                                try {
                                        JSONObject json = new JSONObject();
                                        json.put(ChatMessageData.FIELD_CONTENT,                 message);
                                        json.put(ChatMessageData.FIELD_DATE,                    Utilities.formatDate(Utilities.now()*1000, "HH:mm"));
                                        json.put(ChatMessageData.FIELD_TYPE,                    SendMessage.TYPE_DEFAULT);
                                        json.put(ChatMessageData.FIELD_CHANNEL,                 channel);
                                        json.put(ChatMessageData.FIELD_AUTHOR,                  login);
                                        json.put(ChatMessageData.FIELD_RIGHTS,                  "player");
                                        json.put(ChatMessageData.FIELD_ALLY_TAG,                "");
                                        json.put(ChatMessageData.FIELD_ALLY_NAME,               "");
                                        String jsonString = json.toString();
                                       
                                        synchronized (players) {
                                                for (int idPlayer : players) {
                                                        UpdateTools.queueChatUpdate(idPlayer, jsonString);
                                                }
                                        }
                                } catch (JSONException e) {
                                        LoggingSystem.getServerLogger().warn("JSON exception", e);
                                }
                        }
                }
        }
       
        private static void proceed(List<LotteryTicket> tickets) {
                // Tirage !
                int winningNumber = (int) (Math.random() * 4);
                String winners = "";
               
                // Tirage gagnant
                for (int i = 0; i < tickets.size(); i++) {
                        LotteryTicket ticket = tickets.get(i);
                       
                        if (ticket.getNumber() == winningNumber) {
                                Player player = ticket.getPlayer();
                                if (ticket.getLevel() < 2) {
                                	int boughtHours = LotteryTicket.TICKET_REWARD[ticket.getLevel()] * 24;
                                	                                boolean newPremium = !player.isPremium();
                                	                               
                                	                                synchronized (player.getLock()) {
                                	                                        player = DataAccess.getEditable(player);
                                	                                        player.setRights(player.getRights() | Player.PREMIUM);
                                	                                        player.setPremiumHours(player.getPremiumHours() + boughtHours);
                                	                                        player.save();
                                	                                }
                                	                               
                                	                                if (newPremium) {
                                	                                        Event event = new Event(Event.EVENT_PREMIUM_START, Event.TARGET_PLAYER,
                                	                                                player.getId(), 0, -1, -1, String.valueOf(boughtHours / 24));
                                	                                        event.save();
                                	                                        UpdateTools.queueNewEventUpdate(ticket.getIdPlayer());

                                	                                } else {
                                	                                        Event event = new Event(Event.EVENT_PREMIUM_EXTENDED, Event.TARGET_PLAYER,
                                	                                                player.getId(), 0, -1, -1, String.valueOf(boughtHours / 24),
                                	                                                String.valueOf(player.getPremiumHours() / 24));
                                	                                        event.save();
                                	                                        UpdateTools.queueNewEventUpdate(ticket.getIdPlayer());

                                	                                }
                                	 
                                	                                        Event event = new Event(Event.EVENT_LOTTERY_WON, Event.TARGET_PLAYER,
                                	                                                player.getId(), 0, -1, -1, String.valueOf(winningNumber),
                                	ticket.getLevel() + " jours de Premium");
                                	                                        event.save();
                                	                                        UpdateTools.queueNewEventUpdate(ticket.getIdPlayer());

                                	} else {
                                	long xp = (long) Math.floor(LotteryTicket.TICKET_REWARD[ticket.getLevel()] / 100. *
                                	        (Player.getLevelXp(player.getLevel() + 1) - Player.getLevelXp(player.getLevel())));
                                	                                               
                                	                                                synchronized (player) {
                                	                                                        player = DataAccess.getEditable(player);
                                	                                                        player.addXp(xp);
                                	                                                        player.save();
                                	                                                }
                                	                                        Event event = new Event(Event.EVENT_LOTTERY_WON, Event.TARGET_PLAYER,
                                	                                                player.getId(), 0, -1, -1, String.valueOf(winningNumber),
                                	xp + " XP");
                                	                                        event.save();
                                	                                        UpdateTools.queueNewEventUpdate(ticket.getIdPlayer());

                                	 
                                	}
                             
                                winners += (winners.length() == 0 ? "" : ", ") + player.getLogin();
                               
                                
                        }
                }
               
                // Evènement pour signaler perdu
                for (int i = 0; i < tickets.size(); i++) {
                        LotteryTicket ticket = tickets.get(i);
                       
                        if (ticket.getNumber() != winningNumber) {
                                Event event = new Event(Event.EVENT_LOTTERY_LOST,
                                                Event.TARGET_PLAYER, ticket.getIdPlayer(), 0, -1, -1,
                                                String.valueOf(winningNumber));
                                event.save();
                                UpdateTools.queueNewEventUpdate(ticket.getIdPlayer());

                        }
                }
               
                // Envoi un message sur le chat pour annoncer les gagnants
                sendPublicChannelMessage("Le symbole gagnant est le " + SIGNS.charAt(winningNumber) + " !");
                sendPublicChannelMessage("Les gagnants sont : " + (winners.length() == 0 ? "Aucun gagnant :(" : winners + "."));
        }
}