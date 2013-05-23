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

public class PlanetData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		FIELD_ANGLE = "a", //$NON-NLS-1$
		FIELD_DISTANCE = "b", //$NON-NLS-1$
		FIELD_ROTATION_SPEED = "c", //$NON-NLS-1$
		FIELD_IMAGE = "d"; //$NON-NLS-1$
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected PlanetData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public native final double getAngle() /*-{
		return this[@fr.fg.client.data.PlanetData::FIELD_ANGLE] * Math.PI / 180;
	}-*/;
	
	public native final int getDistance() /*-{
		return this[@fr.fg.client.data.PlanetData::FIELD_DISTANCE];
	}-*/;
	
	public native final double getRotationSpeed() /*-{
		return this[@fr.fg.client.data.PlanetData::FIELD_ROTATION_SPEED] * Math.PI / 18000;
	}-*/;
	
	public native final int getImage() /*-{
		return this[@fr.fg.client.data.PlanetData::FIELD_IMAGE];
	}-*/;
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
