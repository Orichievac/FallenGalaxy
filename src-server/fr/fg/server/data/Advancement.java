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

package fr.fg.server.data;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.base.AdvancementBase;

public class Advancement extends AdvancementBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_FLEETS_COST = 0,
		TYPE_TRAINING_MAX_LEVEL = 1,
		TYPE_COLONIZATION_POINTS = 2,
		TYPE_BUILDING_LAND = 3,
		TYPE_POPULATION_GROWTH = 4,
		TYPE_MINING_UPGRADE = 5,
		TYPE_BANK_TAX = 6,
		TYPE_TRADE_TAX = 7,
		TYPE_RESEARCH = 8,
		TYPE_LINE_OF_SIGHT = 9;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Advancement() {
		// Nécessaire pour la construction par réflection
	}
	
	public Advancement(int idPlayer, int type, int level) {
		setIdPlayer(idPlayer);
		setType(type);
		setLevel(level);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getCost() {
		return getCost(getType());
	}
	
	public static int getCost(int type) {
		switch (type) {
		case TYPE_FLEETS_COST:
			return 2;
		case TYPE_TRAINING_MAX_LEVEL:
			return 3;
		case TYPE_COLONIZATION_POINTS:
			return 8;
		case TYPE_BUILDING_LAND:
			return 5;
		case TYPE_POPULATION_GROWTH:
			return 1;
		case TYPE_MINING_UPGRADE:
			return 2;
		case TYPE_BANK_TAX:
			return 1;
		case TYPE_TRADE_TAX:
			return 3;
		case TYPE_RESEARCH:
			return 4;
		case TYPE_LINE_OF_SIGHT:
			return 1;
		default:
			throw new IllegalArgumentException("Invalid type: " + type + ".");
		}
	}
	
	public void increaseLevel() {
		setLevel(getLevel() + 1);
	}
	
	public static int getAdvancementLevel(int idPlayer, int type) {
		List<Advancement> advancements = DataAccess.getAdvancementsByPlayer(idPlayer);
		
		synchronized (advancements) {
			for (Advancement advancement : advancements)
				if (advancement.getType() == type)
					return advancement.getLevel();
		}
		
		return 0;
	}
	
	public static void setAdvancementLevel(int idPlayer, int type, int level) {
		List<Advancement> advancements = new ArrayList<Advancement>(
				DataAccess.getAdvancementsByPlayer(idPlayer));
		
		synchronized (advancements) {
			for (Advancement advancement : advancements)
				if (advancement.getType() == type) {
					synchronized (advancement.getLock()) {
						advancement = DataAccess.getEditable(advancement);
						advancement.setLevel(level);
						advancement.save();
					}
					return;
				}
		}
		
		Advancement advancement = new Advancement(idPlayer, type, level);
		advancement.save();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
