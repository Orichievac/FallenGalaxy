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
 
package fr.fg.client.data;
 
import com.google.gwt.core.client.JavaScriptObject;
 
public class LotteryInfoData extends JavaScriptObject {
        // ------------------------------------------------------- CONSTANTES -- //
 
        public final static String
                FIELD_BOUGHT_TICKET_LEVEL = "a", //$NON-NLS-1$
                FIELD_DRAW_REMAINING_TIME = "b", //$NON-NLS-1$
                FIELD_BOUGHT_TICKET_NUMBER = "c"; //$NON-NLS-1$
       
        // -------------------------------------------------------- ATTRIBUTS -- //
        // ---------------------------------------------------- CONSTRUCTEURS -- //
       
        protected LotteryInfoData() {
                // Impossible de construire directement un objet JS
        }
       
        // --------------------------------------------------------- METHODES -- //
 
        public final boolean hasBoughtTicket() {
                return getBoughtTicketLevel() != -1;
        }
 
        public native final int getBoughtTicketLevel() /*-{
                return this[@fr.fg.client.data.LotteryInfoData::FIELD_BOUGHT_TICKET_LEVEL];
        }-*/;
 
        public native final int getBoughtTicketNumber() /*-{
                return this[@fr.fg.client.data.LotteryInfoData::FIELD_BOUGHT_TICKET_NUMBER];
        }-*/;
 
        public native final int getDrawRemainingTime() /*-{
                return this[@fr.fg.client.data.LotteryInfoData::FIELD_DRAW_REMAINING_TIME];
        }-*/;
       
        // ------------------------------------------------- METHODES PRIVEES -- //
}