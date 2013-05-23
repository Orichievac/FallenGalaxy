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

public class AnswerData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_SUCCESS = "success", //$NON-NLS-1$
		TYPE_ERROR = "error", //$NON-NLS-1$
		TYPE_CONFIRM_PASSWORD = "confirmPassword", //$NON-NLS-1$
		TYPE_WRONG_PASSWORD = "wrongPassword", //$NON-NLS-1$
		TYPE_DISCONNECTED = "disconnected"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AnswerData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final String getType() /*-{
		return this.type;
	}-*/;
	
	public native final int getUpdatesCount() /*-{
		return this.data.length;
	}-*/;
	
	public native final UpdateData getUpdateAt(int index) /*-{
		return this.data[index];
	}-*/;
	
	public native final StateData getState() /*-{
		return this.data;
	}-*/;
	
	public native final LadderData getLadder() /*-{
		return this.data;
	}-*/;
	
	public native final AreaData getArea() /*-{
		return this.data;
	}-*/;

	public native final EmpireData getEmpire() /*-{
		return this.data;
	}-*/;

	public native final GalaxyMapData getGalaxyMap() /*-{
		return this.data;
	}-*/;

	public native final ReportData getReport() /*-{
		return this.data;
	}-*/;

	public native final ResearchData getResearch() /*-{
		return this.data;
	}-*/;
	
	public native final PlayerSystemsData getPlayerSystems() /*-{
		return this.data;
	}-*/;

	public native final MessageBoxData getMessageBox() /*-{
		return this.data;
	}-*/;

	public native final DialogData getDialog() /*-{
		return this.data;
	}-*/;

	public native final AllyData getAlly() /*-{
		return this.data;
	}-*/;

	public native final EventsData getEvents() /*-{
		return this.data;
	}-*/;

	public native final ContactsData getContacts() /*-{
		return this.data;
	}-*/;

	public native final DiplomacyData getDiplomacy() /*-{
		return this.data;
	}-*/;
	
	public native final TradeCenterRatesData getTradeCenterRates() /*-{
		return this.data;
	}-*/;

	public native final BankAccountData getBankAccount() /*-{
		return this.data;
	}-*/;
	
	public native final LotteryInfoData getLotteryInfoData() /*-{
	return this.data;
}-*/;
	
	public native final AllyDescriptionData getAllyDescription() /*-{
		return this.data;
	}-*/;
	
	public native final ProbeReportData getProbeReport() /*-{
		return this.data;
	}-*/;
	
	public native final DiplomacyStateData getDiplomacyState() /*-{
		return this.data;
	}-*/;
	
	public native final AchievementsData getAchievements() /*-{
		return this.data;
	}-*/;

	public native final TacticsData getTactics() /*-{
		return this.data;
	}-*/;

	public native final ContractsData getContracts() /*-{
		return this.data;
	}-*/;
	
	public native final ServerData getServerInfos() /*-{
	return this.data;
	}-*/;
	
	public native final String getStringData() /*-{
		return this.data;
	}-*/;
	
	public native final PlayerFleetData getFleetData() /*-{
		return this.data;
	}-*/;
	
	public native final ConnectedData getConnectedData() /*-{
		return this.data;
	}-*/;
	
	public native final PlayersInfosData getPlayersInfosData() /*-{
		return this.data;
	}-*/;
	
	public native final PlayerInfosData getPlayerInfosData() /*-{
		return this.data;
	}-*/;
	
	public native final MotdsData getMotdsData() /*-{
		return this.data;
	}-*/;
	
	public native final ScriptsData getScriptsData() /*-{
		return this.data;
	}-*/;
	
	public native final ScriptData getScriptData() /*-{
		return this.data;
	}-*/;

	public native final PlayerCardData getPlayerCardData() /*-{
		return this.data;
	}-*/;

	public native final PremiumStateData getPremiumState() /*-{
		return this.data;
	}-*/;

	public native final ChangelogsData getChangelogsData() /*-{
		return this.data;
	}-*/;

	public native final static AnswerData wrap(String json) /*-{
		return eval('(' + json + ')');
	}-*/;


	// ------------------------------------------------- METHODES PRIVEES -- //
}
