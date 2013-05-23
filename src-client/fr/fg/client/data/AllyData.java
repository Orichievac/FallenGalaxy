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

public class AllyData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a",
		FIELD_NAME = "b",
		FIELD_APPLICANT = "c",
		FIELD_ORGANIZATION = "d",
		FIELD_FOUNDER = "e",
		FIELD_BIRTHDATE = "f",
		FIELD_LAST_UPDATE = "g",
		FIELD_NEWS = "h",
		FIELD_MEMBERS = "i",
		FIELD_APPLICANTS = "j",
		FIELD_VOTES = "k",
		FIELD_APPLICATION_VOTE = "l",
		FIELD_DESCRIPTION = "m",
		FIELD_RIGHTS = "n",
		FIELD_TERRITORY_COLOR = "o";
	
	public final static String 
		RIGHT_ACCEPT				= "accept",
		RIGHT_KICK					= "kick",
		RIGHT_PROMOTE				= "promote",
		RIGHT_VOTE_ACCEPT			= "vote_accept",
		RIGHT_VOTE_KICK				= "vote_kick",
		RIGHT_ELECT					= "elect",
		RIGHT_MANAGE_NEWS			= "manage_news",
		RIGHT_MANAGE_STATIONS		= "manage_stations",
		RIGHT_MANAGE_DIPLOMACY		= "manage_diplomacy",
		RIGHT_MANAGE_DESCRIPTION	= "manage_description",
		RIGHT_MANAGE_CONTRACTS		= "manage_contracts";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected AllyData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getId() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_ID];
	}-*/;

	public native final String getName() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_NAME];
	}-*/;

	public native final boolean isApplicant() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_APPLICANT];
	}-*/;

	public native final String getOrganization() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_ORGANIZATION];
	}-*/;

	public native final String getDescription() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_DESCRIPTION];
	}-*/;

	public native final String getFounder() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_FOUNDER];
	}-*/;

	public native final double getBirthdate() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_BIRTHDATE];
	}-*/;

	public native final double getLastUpdate() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_LAST_UPDATE];
	}-*/;

	public native final int getNewsCount() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_NEWS].length;
	}-*/;

	public native final AllyNewsData getNewsAt(int index) /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_NEWS][index];
	}-*/;

	public native final int getMembersCount() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_MEMBERS].length;
	}-*/;

	public native final AllyMemberData getMemberAt(int index) /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_MEMBERS][index];
	}-*/;

	public native final int getApplicantsCount() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_APPLICANTS].length;
	}-*/;

	public native final AllyApplicantData getApplicantAt(int index) /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_APPLICANTS].length;
	}-*/;

	public native final int getVotesCount() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_VOTES].length;
	}-*/;

	public native final AllyVoteData getVoteAt(int index) /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_VOTES][index];
	}-*/;

	public native final boolean isApplicationVoted() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_APPLICATION_VOTE];
	}-*/;
	
	public native final int getRequiredRank(String right) /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_RIGHTS][right];
	}-*/;
	
	public native final int getRequiredRank(String right, int targetRank) /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_RIGHTS][right + targetRank];
	}-*/;
	
	public native final int getTerritoryColor() /*-{
		return this[@fr.fg.client.data.AllyData::FIELD_TERRITORY_COLOR];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
