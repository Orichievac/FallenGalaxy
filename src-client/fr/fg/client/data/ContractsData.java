/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

public class ContractsData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static String
		FIELD_CONTRACT_OFFERS = "a",
		FIELD_ACTIVE_CONTRACT = "b",
		FIELD_RELATIONSHIPS = "c",
		FIELD_ALLY_RELATIONSHIPS = "d"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ContractsData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getContractOffersCount() /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_CONTRACT_OFFERS].length;
	}-*/;
	
	public final native ContractData getContractOfferAt(int index) /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_CONTRACT_OFFERS][index];
	}-*/;
	
	public final native int getActiveContractsCount() /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_ACTIVE_CONTRACT].length;
	}-*/;
	
	public final native ContractData getActiveContractAt(int index) /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_ACTIVE_CONTRACT][index];
	}-*/;
	
	public final native int getRelationshipsCount() /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_RELATIONSHIPS].length;
	}-*/;
	
	public final native RelationshipData getRelationshipAt(int index) /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_RELATIONSHIPS][index];
	}-*/;
	
	public final native int getAllyRelationshipsCount() /*-{
	return this[@fr.fg.client.data.ContractsData::FIELD_ALLY_RELATIONSHIPS].length;
}-*/;
	
	public final native RelationshipData getAllyRelationshipAt(int index) /*-{
		return this[@fr.fg.client.data.ContractsData::FIELD_ALLY_RELATIONSHIPS][index];
	}-*/;
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
