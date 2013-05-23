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
 
package fr.fg.server.data.base;
 
import fr.fg.server.dao.PersistentData;
 
public class LotteryTicketBase extends PersistentData {
        // ------------------------------------------------------- CONSTANTES -- //
        // -------------------------------------------------------- ATTRIBUTS -- //
	    private int idLottery;
        private int idPlayer;
        private int level;
        private long date;
        private int number;
       
        // ---------------------------------------------------- CONSTRUCTEURS -- //
        // --------------------------------------------------------- METHODES -- //
       
        public int getId() {
    		return idLottery;
    	}
    	
        public void setId(int idLottery) {
    		if (!isEditable())
    			throwDataUneditableException();
    		
    		this.idLottery = idLottery;
    	}
        
        public int getIdPlayer() {
                return idPlayer;
        }
       
        public void setIdPlayer(int idPlayer) {
                if (!isEditable())
                        throwDataUneditableException();
                this.idPlayer = idPlayer;
        }
       
        public int getLevel() {
                return level;
        }
       
        public void setLevel(int level) {
                if (!isEditable())
                        throwDataUneditableException();
                this.level = level;
        }
       
        public long getDate() {
                return date;
        }
       
        public void setDate(long date) {
                if (!isEditable())
                        throwDataUneditableException();
                this.date = date;
        }
       
        public int getNumber() {
                return number;
        }
       
        public void setNumber(int number) {
                if (!isEditable())
                        throwDataUneditableException();
                this.number = number;
        }
       
        // ------------------------------------------------- METHODES PRIVEES -- //
}