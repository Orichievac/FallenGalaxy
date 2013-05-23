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

import com.google.gwt.core.client.GWT;

import fr.fg.client.i18n.StaticMessages;

public class ProductData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		NO_PRODUCT = 0,
		PRODUCT_IRIDIUM = 1,
		PRODUCT_NECTAR = 2,
		PRODUCT_HEPTANIUM = 3,
		PRODUCT_SELENIUM = 4, //brown one
		PRODUCT_SULFARIDE = 5, //rubis
		PRODUCT_ANTILIUM = 6, // Ancienne AM
		
		PRODUCT_IRIDIUM_MEDIUM = 11,
		PRODUCT_NECTAR_MEDIUM = 12,
		PRODUCT_HEPTANIUM_MEDIUM = 13,
		PRODUCT_SELENIUM_MEDIUM = 14,
		PRODUCT_SULFARIDE_MEDIUM = 15,
		PRODUCT_ANTILIUM_MEDIUM = 16,
		
		PRODUCT_IRIDIUM_IMPORTANT = 21,
		PRODUCT_NECTAR_IMPORTANT = 22,
		PRODUCT_HEPTANIUM_IMPORTANT = 23,
		PRODUCT_SELENIUM_IMPORTANT = 24,
		PRODUCT_SULFARIDE_IMPORTANT = 25,
		PRODUCT_ANTILIUM_IMPORTANT = 26,
		
		PRODUCT_IRIDIUM_RICH = 31,
		PRODUCT_NECTAR_RICH = 32,
		PRODUCT_HEPTANIUM_RICH = 33,
		PRODUCT_SELENIUM_RICH = 34,
		PRODUCT_SULFARIDE_RICH = 35,
		PRODUCT_ANTILIUM_RICH = 36,
		
		PRODUCT_IRIDIUM_PUR = 41,
		PRODUCT_NECTAR_PUR = 42,
		PRODUCT_HEPTANIUM_PUR = 43,
		PRODUCT_SELENIUM_PUR = 44,
		PRODUCT_SULFARIDE_PUR = 45,
		PRODUCT_ANTILIUM_PUR = 46,
		
		PRODUCT_UNREACHABLE = 999;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private int count;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ProductData(int type, int count) {
		this.type = type;
		this.count = type == NO_PRODUCT ? 0 : count;
	}

	// --------------------------------------------------------- METHODES -- //
	
	public int getType() {
		return type;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getDesc() {
		return getDesc(type, count);
	}
	
	public static String getDesc(int type, int count) {
		String desc;
		StaticMessages messages = GWT.create(StaticMessages.class);
		
		switch (type) {
		case PRODUCT_IRIDIUM:
			desc = messages.product1Desc("<span class=\"emphasize\">" + (3 * count) + "%</span>");
			break;
		case PRODUCT_NECTAR:
			desc = messages.product2Desc("<span class=\"emphasize\">" + (5 * count) + "%</span>");
			break;
		case PRODUCT_HEPTANIUM:
			desc = messages.product3Desc("<span class=\"emphasize\">" + (2 * count) + "%</span>");
			break;
			//Générateur
		case PRODUCT_SELENIUM:
			desc = messages.product4Desc("<span class=\"emphasize\">" + (2 * count) + "</span>");
			break;
			//Flotte
		case PRODUCT_SULFARIDE:
			desc = messages.product5Desc("<span class=\"emphasize\">" + (2 * count) + "%</span>");
			break;
			//Balise
		case PRODUCT_ANTILIUM:
			desc = messages.product6Desc("<span class=\"emphasize\">" + (2 * count) + "</span>");
			break;
		default:
			throw new IllegalArgumentException("Unknown product type: '" + type + "'.");
		}
		
		return desc;
	}

	public static double getProductEffect(int type, int count) {
		switch (type) {
		case PRODUCT_IRIDIUM:
			return 1 + .03 * count;
		case PRODUCT_NECTAR:
			return 1 + .05 * count;
		case PRODUCT_HEPTANIUM:
			return 1 - .02 * count;
		case PRODUCT_SELENIUM:
			return count * 2;
		case PRODUCT_SULFARIDE:
			return 1 - .02 * count;
		case PRODUCT_ANTILIUM:
			return 2 * count;
			
		default:
			throw new IllegalArgumentException(
				"Invalid product type: '" + type + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
