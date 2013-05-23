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

public class PaymentOptionData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_PREMIUM_SMS = "premium-sms",
		TYPE_PREMIUM_CALLING = "premium-calling",
		TYPE_CREDIT_CARD = "credit-card";

	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_TYPE = "b", //$NON-NLS-1$
		FIELD_LEGAL = "c", //$NON-NLS-1$
		FIELD_PRICE = "d", //$NON-NLS-1$
		FIELD_CURRENCY = "e", //$NON-NLS-1$
		FIELD_DESCRIPTION = "f"; //$NON-NLS-1$
		
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	protected PaymentOptionData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //

	public final native String getId() /*-{
		return this[@fr.fg.client.data.PaymentOptionData::FIELD_ID];
	}-*/;

	public final native String getDescription() /*-{
		return this[@fr.fg.client.data.PaymentOptionData::FIELD_DESCRIPTION];
	}-*/;

	public final native String getType() /*-{
		return this[@fr.fg.client.data.PaymentOptionData::FIELD_TYPE];
	}-*/;

	public final native String getLegal() /*-{
		return this[@fr.fg.client.data.PaymentOptionData::FIELD_LEGAL];
	}-*/;

	public final native double getPrice() /*-{
		return this[@fr.fg.client.data.PaymentOptionData::FIELD_PRICE];
	}-*/;

	public final native String getCurrency() /*-{
		return this[@fr.fg.client.data.PaymentOptionData::FIELD_CURRENCY];
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
}
