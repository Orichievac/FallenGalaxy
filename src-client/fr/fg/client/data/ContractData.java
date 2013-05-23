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

public class ContractData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ID = "a", //$NON-NLS-1$
		FIELD_TYPE = "b", //$NON-NLS-1$
		FIELD_START_TIME = "c", //$NON-NLS-1$
		FIELD_GOAL = "d", //$NON-NLS-1$
		FIELD_REGISTERED = "e", //$NON-NLS-1$
		FIELD_REWARD = "f", //$NON-NLS-1$
		FIELD_ACCEPTED = "g", //$NON-NLS-1$
		FIELD_DESCRIPTION = "h", //$NON-NLS-1$
		FIELD_TITLE = "i", //$NON-NLS-1$
		FIELD_SECTORS = "j", //$NON-NLS-1$
		FIELD_ATTENDEES = "k", //$NON-NLS-1$
		FIELD_RELATIONSHIPS = "l", //$NON-NLS-1$
		FIELD_FINISHED = "m", //$NON-NLS-1$
		FIELD_MISSION_TYPE = "n"; //$NON-NLS-1$
	
		public final static String
			TYPE_SOLO = "player",
			TYPE_PVP = "pvp",
			TYPE_MULTIPLAYER = "multiplayer",
			TYPE_MULTIALLY = "multially",
			TYPE_AVA = "ava",
			TYPE_ALLY_SOLO = "ally",
			TYPE_ALL = "all";
		
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ContractData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native double getId() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_ID];
	}-*/;
	
	public final native String getType() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_TYPE];
	}-*/;
	
	public final native double getStartTime() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_START_TIME];
	}-*/;
	
	public final native String getGoal() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_GOAL];
	}-*/;
	
	public final native String getTitle() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_TITLE];
	}-*/;

	public final native String getDescription() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_DESCRIPTION];
	}-*/;
	
	public final native boolean isRegistered() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_ACCEPTED];
	}-*/;
	
	public final native int getRewardsCount() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_REWARD].length;
	}-*/;
	
	public final native ContractRewardData getRewardAt(int index) /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_REWARD][index];
	}-*/;
	
	public final native int getSectorsCount() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_SECTORS].length;
	}-*/;
	
	public final native String getSectorAt(int index) /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_SECTORS][index];
	}-*/;
	
	public final native int getAttendeesCount() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_ATTENDEES].length;
	}-*/;
	
	public final native ContractAttendeeData getAttendeeAt(int index) /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_ATTENDEES][index];
	}-*/;
	
	public final native int getRelationshipsCount() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_RELATIONSHIPS].length;
	}-*/;
	
	public final native ContractRelationshipData getRelationshipAt(int index) /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_RELATIONSHIPS][index];
	}-*/;
	
	public final native boolean isFinished() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_FINISHED];
	}-*/;
	
	public final native String getMissionType() /*-{
		return this[@fr.fg.client.data.ContractData::FIELD_MISSION_TYPE];
	}-*/;
	// ------------------------------------------------- METHODES PRIVEES -- //
}
