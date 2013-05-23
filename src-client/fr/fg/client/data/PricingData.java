/*
Copyright 2012 jgottero

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

public class PricingData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_COUNTRIES = "a", //$NON-NLS-1$
		FIELD_PLAYER_COUNTRY = "b"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	protected PricingData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public final native int getCountriesCount() /*-{
		return this[@fr.fg.client.data.PricingData::FIELD_COUNTRIES].length;
	}-*/;

	public final native CountryData getCountryAt(int index) /*-{
		return this[@fr.fg.client.data.PricingData::FIELD_COUNTRIES][index];
	}-*/;

	public final native String getPlayerCountry() /*-{
		return this[@fr.fg.client.data.PricingData::FIELD_PLAYER_COUNTRY];
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
}
