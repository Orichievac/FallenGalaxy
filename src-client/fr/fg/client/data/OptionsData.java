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

import fr.fg.client.openjwt.core.Config;

public class OptionsData extends JavaScriptObject {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String[] THEMES = {
		Config.getMediaUrl() + "style/FallenCraft2Red",
		Config.getMediaUrl() + "style/FallenCraft2Blue"
	};
	
	public final static String
		FIELD_GRID = "a",
		FIELD_BRIGHTNESS = "b",
		FIELD_FLEETS_SKIN = "c",
		FIELD_THEME = "e",
		FIELD_CENSORSHIP = "f",
		FIELD_GENERAL_VOLUME = "g",
		FIELD_SOUND_VOLUME = "h",
		FIELD_MUSIC_VOLUME = "i",
		FIELD_GRAPHICS_QUALITY = "j",
		FIELD_CONNECTION_OPTIMIZED = "k",
		FIELD_FLEET_PREFIX = "l",
		FIELD_FLEET_SUFFIX = "m";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected OptionsData() {
		// Impossible de construire directement un objet JS
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final native boolean isGridVisible() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_GRID];
	}-*/;

	public final native int getBrightness() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_BRIGHTNESS];
	}-*/;

	public final native int getFleetsSkin() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_FLEETS_SKIN];
	}-*/;

	public final native String getTheme() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_THEME];
	}-*/;

	public final native boolean isCensorshipActive() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_CENSORSHIP];
	}-*/;

	public final native int getGeneralVolume() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_GENERAL_VOLUME];
	}-*/;

	public final native int getSoundVolume() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_SOUND_VOLUME];
	}-*/;

	public final native int getMusicVolume() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_MUSIC_VOLUME];
	}-*/;

	public final native int getGraphicsQuality() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_GRAPHICS_QUALITY];
	}-*/;

	public final native boolean isConnectionOptimized() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_CONNECTION_OPTIMIZED];
	}-*/;

	public final native String getFleetPrefix() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_FLEET_PREFIX];
	}-*/;

	public final native int getFleetSuffix() /*-{
		return this[@fr.fg.client.data.OptionsData::FIELD_FLEET_SUFFIX];
	}-*/;

	// ------------------------------------------------- METHODES PRIVEES -- //
}
