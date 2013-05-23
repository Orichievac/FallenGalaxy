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

package fr.fg.client.openjwt.core;

import com.google.gwt.core.client.GWT;

import fr.fg.client.openjwt.core.impl.ConfigImpl;

public class Config {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static int
		VALUE_QUALITY_LOW = ConfigImpl.VALUE_QUALITY_LOW,
		VALUE_QUALITY_AVERAGE = ConfigImpl.VALUE_QUALITY_AVERAGE,
		VALUE_QUALITY_HIGH = ConfigImpl.VALUE_QUALITY_HIGH,
		VALUE_QUALITY_MAXIMUM = ConfigImpl.VALUE_QUALITY_MAXIMUM;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Config instance = new Config();
	
	private ConfigImpl impl;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Config() {
		this.impl = GWT.create(ConfigImpl.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static String getServerUrl() {
		return instance.impl.getServerUrl();
	}
	
	public static String getMediaUrl() {
		return instance.impl.getMediaUrl();
	}
	
	public static String getTheme() {
		return instance.impl.getTheme();
	}
	
	public static boolean isDebug() {
		return instance.impl.isDebug();
	}
	
	public static int getGraphicsQuality() {
		return instance.impl.getGraphicsQuality();
	}

	public static void setGraphicsQuality(int graphicsQuality) {
		instance.impl.setGraphicsQuality(graphicsQuality);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
