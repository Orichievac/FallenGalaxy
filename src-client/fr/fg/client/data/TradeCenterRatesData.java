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

public class TradeCenterRatesData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_RATES = "a",
		FIELD_VARIATION = "b",
		FIELD_FEES = "c"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected TradeCenterRatesData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final double getRate(int resource) /*-{
		return this[@fr.fg.client.data.TradeCenterRatesData::FIELD_RATES][resource];
	}-*/;
	
	public native final double getVariation() /*-{
		return this[@fr.fg.client.data.TradeCenterRatesData::FIELD_VARIATION];
	}-*/;
	
	public native final double getFees() /*-{
		return this[@fr.fg.client.data.TradeCenterRatesData::FIELD_FEES];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
