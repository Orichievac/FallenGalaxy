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

public class ResearchData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_RESEARCH_POINTS = "a", //$NON-NLS-1$
		FIELD_RESEARCHED_TECHNOLOGIES = "b", //$NON-NLS-1$
		FIELD_PENDING_TECHNOLOGIES = "c"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ResearchData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final int getResearchPoints() /*-{
		return this[@fr.fg.client.data.ResearchData::FIELD_RESEARCH_POINTS];
	}-*/;

	public native final int getResearchedTechnologiesCount() /*-{
		return this[@fr.fg.client.data.ResearchData::FIELD_RESEARCHED_TECHNOLOGIES].length;
	}-*/;

	public native final ResearchedTechnologyData getResearchedTechnologyAt(int index) /*-{
		return this[@fr.fg.client.data.ResearchData::FIELD_RESEARCHED_TECHNOLOGIES][index];
	}-*/;

	public native final int getPendingTechnologiesCount() /*-{
		return this[@fr.fg.client.data.ResearchData::FIELD_PENDING_TECHNOLOGIES].length;
	}-*/;

	public native final int getPendingTechnologyAt(int index) /*-{
		return this[@fr.fg.client.data.ResearchData::FIELD_PENDING_TECHNOLOGIES][index];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
