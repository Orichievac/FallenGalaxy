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

public class UpdateData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		UPDATE_NEW_MESSAGE			=  1,
		UPDATE_AREA					=  2,
		UPDATE_SYSTEM				=  3,
		UPDATE_PLAYER_FLEETS		=  4,
		UPDATE_PLAYER_SYSTEMS		=  5,
		UPDATE_XP					=  6,
		UPDATE_PLAYER_XP			=  7,
		UPDATE_RESEARCH				=  9,
		UPDATE_CHAT					= 10,
		UPDATE_NEW_NEWS				= 12,
		UPDATE_CONTRACTS_STATE		= 14,
		UPDATE_PLAYER_CONTRACTS		= 15,
		UPDATE_NEW_EVENTS			= 16,
		UPDATE_CHAT_CHANNELS		= 17,
		UPDATE_ALLY					= 18,
		UPDATE_SERVER_SHUTDOWN		= 19,
		UPDATE_INFORMATION			= 20,
		UPDATE_ADVANCEMENTS			= 21,
		UPDATE_EFFECT				= 22,
		UPDATE_PLAYER_GENERATORS	= 23,
		UPDATE_PRODUCTS				= 24,
		UPDATE_PLAYER_FLEET			= 25;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected UpdateData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getType() /*-{
		return this.type;
	}-*/;
	
	public native final double getDate() /*-{
		return this.date;
	}-*/;
	
	public native final ChatMessageData getChatMessage() /*-{
		return this.data;
	}-*/;

	public native final AreaData getArea() /*-{
		return this.data;
	}-*/;

	public native final PlayerFleetsData getPlayerFleets() /*-{
		return this.data;
	}-*/;

	public native final PlayerSystemsData getPlayerSystems() /*-{
		return this.data;
	}-*/;

	public native final ContractsData getPlayerContracts() /*-{
		return this.data;
	}-*/;

	public native final ContractsStateData getContractsState() /*-{
		return this.data;
	}-*/;

	public native final ChatChannelsData getChatChannels() /*-{
		return this.data;
	}-*/;

	public native final AllyData getAlly() /*-{
		return this.data;
	}-*/;

	public native final int getServerShutdownRemainingTime() /*-{
		return this.data;
	}-*/;

	public native final String getInformation() /*-{
		return this.data;
	}-*/;

	public native final XpData getXp() /*-{
		return this.data;
	}-*/;

	public native final AdvancementsData getAdvancements() /*-{
		return this.data;
	}-*/;

	public native final EffectData getEffect() /*-{
		return this.data;
	}-*/;
	
	public native final PlayerGeneratorsData getPlayerGenerators() /*-{
		return this.data;
	}-*/;
	
	public native final ProductsData getProducts() /*-{
		return this.data;
	}-*/;
	
	public native final PlayerFleetData getPlayerFleet() /*-{
		return this.data;
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
