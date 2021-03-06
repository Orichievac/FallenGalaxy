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
 
package fr.fg.client.data;
 
import com.google.gwt.core.client.JavaScriptObject;
 
import fr.fg.client.openjwt.core.Point;
 
public class LotteryData extends JavaScriptObject {
        // ------------------------------------------------------- CONSTANTES -- //
       
        public final static String CLASS_NAME = "LotteryData"; //$NON-NLS-1$
       
        public final static double LOTTERY_RADIUS = 4.5;
        
        public final static String
                FIELD_ID = "a", //$NON-NLS-1$
                FIELD_X = "b", //$NON-NLS-1$
                FIELD_Y = "c"; //$NON-NLS-1$
       
        // -------------------------------------------------------- ATTRIBUTS -- //
        // ---------------------------------------------------- CONSTRUCTEURS -- //
 
        protected LotteryData() {
                // Impossible de construire directement un objet JS
        }
       
        // --------------------------------------------------------- METHODES -- //
       
        public native final int getId() /*-{
                return this[@fr.fg.client.data.LotteryData::FIELD_ID];
        }-*/;
       
        public native final int getX() /*-{
                return this[@fr.fg.client.data.LotteryData::FIELD_X];
        }-*/;
       
        public native final int getY() /*-{
                return this[@fr.fg.client.data.LotteryData::FIELD_Y];
        }-*/;
 
        public final Point getLocation() {
                return new Point(getX(), getY());
        }
       
        // ------------------------------------------------- METHODES PRIVEES -- //
}