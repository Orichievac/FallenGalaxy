/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.client.core.settings;

public class Settings {
	// ------------------------------------------------------- CONSTANTES -- //
	public final static int USER = 0;
	public final static int PREMIUM = 1;
	public final static int MODERATOR = 2;
	public final static int ADMINISTRATOR = 4;
	public final static int SUPER_ADMINISTRATOR = 8;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Settings instance = new Settings();
	
	private int brightness;
	private int fleetsSkin;
	private String fleetPrefix;
	private int fleetSuffix;
	private boolean censorship;
	private boolean gridVisible;
	private boolean premium;
	private String securityKey;
	private int playerId;
	private String playerLogin;
	private int timeUnit;
	private boolean connectionOptimized;
	private String ekey;
	private int rights;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Settings() {
		brightness = 0;
		fleetsSkin = 1;
		censorship = true;
		gridVisible = false;
		premium = false;
		securityKey = null;
		playerId = 0;
		playerLogin = "";
		timeUnit = 1;
		connectionOptimized = false;
		ekey = "";
		rights=0;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static void setBrightness(int brightness) {
		instance.brightness = brightness;
	}
	
	public static int getBrightness() {
		return instance.brightness;
	}
	
	public static void setFleetsSkin(int fleetsSkin) {
		instance.fleetsSkin = fleetsSkin;
	}
	
	public static int getFleetsSkin() {
		return instance.fleetsSkin;
	}
	
	public static boolean isCensorshipActive() {
		return instance.censorship;
	}
	
	public static void setCensorshipActive(boolean censorship) {
		instance.censorship = censorship;
	}
	
	public static void setGridVisible(boolean gridVisible) {
		instance.gridVisible = gridVisible;
	}

	public static boolean isGridVisible() {
		return instance.gridVisible;
	}

	public static boolean isPremium() {
		return instance.premium;
	}
	
	public static void setPremium(boolean premium) {
		instance.premium = premium;
	}
	
	public static String getSecurityKey() {
		return instance.securityKey;
	}
	
	public static void setSecurityKey(String key) {
		instance.securityKey = key;
	}
	
	public static int getPlayerId() {
		return instance.playerId;
	}
	
	public static void setPlayerId(int playerId) {
		instance.playerId = playerId;
	}
	
	public static String getPlayerLogin() {
		return instance.playerLogin;
	}
	
	public static void setPlayerLogin(String playerLogin) {
		instance.playerLogin = playerLogin;
	}
	
	public static int getTimeUnit() {
		return instance.timeUnit;
	}

	public static void setTimeUnit(int timeUnit) {
		instance.timeUnit = timeUnit;
	}

	public static boolean isConnectionOptimized() {
		return instance.connectionOptimized;
	}

	public static void setConnectionOptimized(boolean connectionOptimized) {
		instance.connectionOptimized = connectionOptimized;
	}

	public static String getFleetPrefix() {
		return instance.fleetPrefix;
	}

	public static void setFleetPrefix(String fleetPrefix) {
		instance.fleetPrefix = fleetPrefix;
	}

	public static int getFleetSuffix() {
		return instance.fleetSuffix;
	}

	public static void setFleetSuffix(int fleetSuffix) {
		instance.fleetSuffix = fleetSuffix;
	}
	
	public static String getEkey() {
		return instance.ekey;
	}
	
	public static void setEkey(String ekey) {
		instance.ekey = ekey;
	}
	
	public static void setRights(int rights) {
		instance.rights = rights;
	}
	
	public static int getRights() {
		return instance.rights;
	}
	
	public static boolean hasRight(int right) {
		if (right == USER)
			return true;
		return (getRights() & right) != 0;
	}
	
	public static boolean isModerator() {
		return Settings.hasRight(Settings.MODERATOR);
	}
	
	public static boolean isAdministrator() {
		return Settings.hasRight(Settings.ADMINISTRATOR);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
