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

package fr.fg.server.i18n;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.util.text.LocalizedProperties;

import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;

public class Messages {
	// -------------------------------------------------------- CONSTANTES --//
	
	private final static int
		ENTRY_NOT_FOUND = 0,
		SERVER_ENTRY = 1,
		CLIENT_STATIC_ENTRY = 2,
		CLIENT_DYNAMIC_ENTRY = 3;
	
	// --------------------------------------------------------- ATTRIBUTS --//
	
	private static Messages instance = new Messages();
	
	private Map<String, Integer> entriesLookup;
	
	private LocalizedProperties serverProperties, clientStaticProperties,
		clientDynamicProperties;
	
	// ----------------------------------------------------- CONSTRUCTEURS --//
	
	private Messages() {
		entriesLookup = Collections.synchronizedMap(
			new HashMap<String, Integer>());
		
		try {
			this.serverProperties = new LocalizedProperties();
			this.serverProperties.load(getClass().getResourceAsStream("messages_" +
					Config.getServerLang() + ".properties"), "UTF-8");
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error(
					"Could not load localization file!", e);
		}
		
		try {
			this.clientStaticProperties = new LocalizedProperties();
			this.clientStaticProperties.load(getClass().getResourceAsStream(
				"/fr/fg/client/i18n/StaticMessages.properties"), "UTF-8");
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error(
					"Could not load localization file!", e);
		}
		
		try {
			this.clientDynamicProperties = new LocalizedProperties();
			this.clientDynamicProperties.load(getClass().getResourceAsStream(
				"/fr/fg/client/i18n/DynamicMessages.properties"), "UTF-8");
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error(
					"Could not load localization file!", e);
		}
	}
	
	// ---------------------------------------------------------- METHODES --//
	
	public static boolean containsString(String entry) {
		return searchEntry(entry) != ENTRY_NOT_FOUND;
	}
	
	public static String getString(String entry, Object... args) {
		String str;
		
		switch (searchEntry(entry)) {
		case SERVER_ENTRY:
			str = instance.serverProperties.getProperty(entry);
			break;
		case CLIENT_STATIC_ENTRY:
			str = instance.clientStaticProperties.getProperty(entry);
			str = str.replace("''", "'");
			break;
		case CLIENT_DYNAMIC_ENTRY:
			str = instance.clientDynamicProperties.getProperty(entry);
			break;
		default:
			str = null;
			break;
		}
		
		if (str == null)
			return '!' + entry + '!';
		
		for (int i = 0; i < args.length; i++)
			str = str.replace("{" + i + "}", args[i].toString()); //$NON-NLS-1$ //$NON-NLS-2$
		
		return str;
	}
	
	// -------------------------------------------------- METHODES PRIVEES --//
	
	private static int searchEntry(String entry) {
		Integer value = instance.entriesLookup.get(entry);
		if (value != null)
			return value;
		
		if (instance.serverProperties.getProperty(entry) != null) {
			instance.entriesLookup.put(entry, SERVER_ENTRY);
			return SERVER_ENTRY;
		}
		
		if (instance.clientStaticProperties.getProperty(entry) != null) {
			instance.entriesLookup.put(entry, CLIENT_STATIC_ENTRY);
			return CLIENT_STATIC_ENTRY;
		}
		
		if (instance.clientDynamicProperties.getProperty(entry) != null) {
			instance.entriesLookup.put(entry, CLIENT_DYNAMIC_ENTRY);
			return CLIENT_DYNAMIC_ENTRY;
		}
		
		instance.entriesLookup.put(entry, ENTRY_NOT_FOUND);
		return ENTRY_NOT_FOUND;
	}
	
	// ------------------------------------------------------- CONSTANTES -- //
}

