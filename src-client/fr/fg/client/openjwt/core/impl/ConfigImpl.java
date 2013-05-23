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

package fr.fg.client.openjwt.core.impl;

import com.google.gwt.i18n.client.Dictionary;

public class ConfigImpl {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		VALUE_QUALITY_LOW = 0,
		VALUE_QUALITY_AVERAGE = 1,
		VALUE_QUALITY_HIGH = 2,
		VALUE_QUALITY_MAXIMUM = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String serverUrl, mediaUrl;
	
	private String theme;
	
	private boolean debug;
	
	private int graphicsQuality;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ConfigImpl() {
		Dictionary config = Dictionary.getDictionary("config");
		serverUrl = config.get("serverUrl");
		mediaUrl = config.get("mediaUrl");
		theme = config.get("theme");
		debug = config.get("debug").equals("true");
		graphicsQuality = VALUE_QUALITY_LOW;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getServerUrl() {
		return serverUrl;
	}
	
	public String getMediaUrl() {
		return mediaUrl;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public int getGraphicsQuality() {
		return graphicsQuality;
	}

	public void setGraphicsQuality(int graphicsQuality) {
		this.graphicsQuality = graphicsQuality;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
