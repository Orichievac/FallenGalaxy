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

public class StateData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		STATE_ONLINE = 1,
		STATE_OFFLINE = 2,
		STATE_ALREADY_ONLINE = 3,
		STATE_NO_SYSTEM = 4;
	
	public final static String
		FIELD_STATE = "a",
		FIELD_SERVERS = "b",
		FIELD_CURRENT_SERVER = "c",
		FIELD_RESEARCH = "d",
		FIELD_UNREAD_MESSAGES = "e",
		FIELD_ONLINE_PLAYERS = "f",
		FIELD_AREA = "g",
		FIELD_VIEW_X = "h",
		FIELD_VIEW_Y = "i",
		FIELD_XP = "j",
		FIELD_COLONIZATION_POINTS = "k",
		FIELD_PLAYER_SYSTEMS = "l",
		FIELD_PLAYER_FLEETS = "m",
		FIELD_OPTIONS = "n",
		FIELD_SECURITY_KEY = "o",
		FIELD_PLAYER_ID = "p",
		FIELD_PLAYER_LOGIN = "q",
		FIELD_PREMIUM = "r",
		FIELD_TIME_UNIT = "s",
		FIELD_LAST_CONNECTION = "t",
		FIELD_CONTRACT_STATES = "u",
		FIELD_NEW_EVENTS = "v",
		FIELD_ALLY = "w",
		FIELD_CHAT_CHANNELS = "x",
		FIELD_SERVER_SHUTDOWN = "y",
		FIELD_TUTORIAL = "z",
		FIELD_ADVANCEMENTS = "A",
		FIELD_TACTICS = "B",
		FIELD_EKEY = "C",
		FIELD_PLAYER_CONTRACTS = "D",
		FIELD_PLAYER_GENERATORS = "E",
		FIELD_PRODUCTS = "F",
		FIELD_RIGHTS = "G",
		FIELD_CHANGELOGS = "Q";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected StateData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native int getState() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_STATE];
	}-*/;
	
	public final native int getServersCount() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_SERVERS].length;
	}-*/;
	
	public final native ServerData getServerAt(int index) /*-{
		return this[@fr.fg.client.data.StateData::FIELD_SERVERS][index];
	}-*/;
	
	public final native int getCurrentServerIndex() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CURRENT_SERVER];
	}-*/;

	public final native int getUnreadMessages() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_UNREAD_MESSAGES];
	}-*/;

	public final native int getOnlinePlayers() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_ONLINE_PLAYERS];
	}-*/;

	public final native AreaData getArea() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_AREA];
	}-*/;

	public final native int getViewX() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_VIEW_X];
	}-*/;

	public final native int getViewY() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_VIEW_Y];
	}-*/;

	public final native int getXp() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_XP];
	}-*/;

	public final native int getColonizationPoints() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_COLONIZATION_POINTS];
	}-*/;

	public final native ResearchData getResearch() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_RESEARCH];
	}-*/;

	public final native PlayerSystemsData getPlayerSystems() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PLAYER_SYSTEMS];
	}-*/;

	public final native PlayerFleetsData getPlayerFleets() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PLAYER_FLEETS];
	}-*/;

	public final native PlayerGeneratorsData getPlayerGenerators() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PLAYER_GENERATORS];
	}-*/;

	public final native OptionsData getOptions() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_OPTIONS];
	}-*/;
	
	public final native String getSecurityKey() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_SECURITY_KEY];
	}-*/;

	public final native int getPlayerId() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PLAYER_ID];
	}-*/;

	public final native String getPlayerLogin() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PLAYER_LOGIN];
	}-*/;

	public final native String getEkey() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_EKEY];
	}-*/;

	public final native boolean isPremium() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PREMIUM];
	}-*/;

	public final native int getTimeUnit() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_TIME_UNIT];
	}-*/;

	public final native String getLastConnection() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_LAST_CONNECTION];
	}-*/;

	public final native ContractsStateData getContractsStates() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CONTRACT_STATES];
	}-*/;

	public final native int getContractStatesCount() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CONTRACT_STATES].length;
	}-*/;

	public final native int getContractStateAt(int index) /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CONTRACT_STATES][index];
	}-*/;

	public final native boolean hasNewEvents() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_NEW_EVENTS];
	}-*/;

	public final native AllyData getAlly() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_ALLY];
	}-*/;
	
	public final native ChatChannelsData getChatChannels() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CHAT_CHANNELS];
	}-*/;

	public final native int getServerShutdown() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_SERVER_SHUTDOWN];
	}-*/;

	public final native double getTutorial() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_TUTORIAL];
	}-*/;

	public final native AdvancementsData getAdvancements() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_ADVANCEMENTS];
	}-*/;

	public final native TacticsData getTactics() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_TACTICS];
	}-*/;

	public final native ContractsData getPlayerContracts() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PLAYER_CONTRACTS];
	}-*/;
	
	public final native ProductsData getProducts() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_PRODUCTS];
	}-*/;
	
	public final native int getRights() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_RIGHTS];
	}-*/;

	public final native int getChangelogsCount() /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CHANGELOGS].length;
	}-*/;
	
	public final native ChangelogData getChangelogAt(int index) /*-{
		return this[@fr.fg.client.data.StateData::FIELD_CHANGELOGS][index];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
