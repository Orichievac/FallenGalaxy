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

import fr.fg.server.data.base.TradecenterBase;

public class Tradecenter extends TradecenterBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
   
	public Tradecenter() {
		// Nécessaire pour la construction par réflection
	}

	public Tradecenter(int idTradecenter, double variation, double fees) {
		setIdTradecenter(idTradecenter);
		setRate0(1);
		setRate1(1);
		setRate2(1);
		setRate3(1);
		setVariation(variation);
		setFees(fees);
	}
    
    // ---------------------------------------------------------- METHODES -- //

	public double getPlayerFees(int idPlayer) {
		return getFees() * Math.pow(.94, Advancement.getAdvancementLevel(
				idPlayer, Advancement.TYPE_TRADE_TAX));
	}
	
	public double getRate(int index) {
		switch (index) {
		case 0:
			return getRate0();
		case 1:
			return getRate1();
		case 2:
			return getRate2();
		case 3:
			return getRate3();
		default:
			throw new IllegalArgumentException(
					"Invalid rate index : '" + index + "'.");
		}
	}
	
	public void setRate(double rate, int index) {
		switch (index) {
		case 0:
			setRate0(rate);
			break;
		case 1:
			setRate1(rate);
			break;
		case 2:
			setRate2(rate);
			break;
		case 3:
			setRate3(rate);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid rate index : '" + index + "'.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
