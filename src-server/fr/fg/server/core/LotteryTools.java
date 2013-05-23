/*
Copyright 2011 jgottero
 
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
 
package fr.fg.server.core;
 
import fr.fg.client.data.LotteryInfoData;
import fr.fg.server.data.LotteryTicket;
import fr.fg.server.data.Player;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;
 
public class LotteryTools {
        // ------------------------------------------------------- CONSTANTES -- //
        // -------------------------------------------------------- ATTRIBUTS -- //
        // ---------------------------------------------------- CONSTRUCTEURS -- //
        // --------------------------------------------------------- METHODES -- //
       
        public static JSONStringer getLotteryInfo(JSONStringer json, Player player) {
                if (json == null)
                        json = new JSONStringer();
               
                LotteryTicket ticket = player.getLotteryTicket();
               
                int time;
                int period = 48*3600;
                int ref = (int) (18.5*3600);
                int d = (int) (Utilities.now() % period);
                if (d < ref)
                        time = ref - d;
                else
                        time = (ref + period) - d;
                
                int drawRemainingTime = time;
               
                json.object().
                        key(LotteryInfoData.FIELD_BOUGHT_TICKET_LEVEL). value(ticket == null ? -1 : ticket.getLevel()).
                        key(LotteryInfoData.FIELD_BOUGHT_TICKET_NUMBER).value(ticket == null ? -1 : ticket.getNumber()).
                        key(LotteryInfoData.FIELD_DRAW_REMAINING_TIME). value(drawRemainingTime).
                        endObject();
               
                return json;
        }
       
        // ------------------------------------------------- METHODES PRIVEES -- //
}