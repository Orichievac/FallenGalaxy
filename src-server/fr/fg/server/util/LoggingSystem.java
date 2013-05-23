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

package fr.fg.server.util;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggingSystem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Logger serverLogger, chatLogger, actionLogger, premiumLogger;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static Logger getServerLogger() {
		if (serverLogger == null) {
			// Initialise le gestionnaire de logs
			try {
				DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
						new PatternLayout("%-5p %d{HH:mm:ss} %m%n"),
						Config.getServerLogFile(), "'.'yyyy-MM-dd");
				
				serverLogger = Logger.getLogger("server");
				serverLogger.addAppender(fileAppender);
				serverLogger.setLevel(Level.toLevel(Config.getServerLogLevel()));
				
				if (Config.isDebug()) {
					ConsoleAppender consoleAppender = new ConsoleAppender(
							new PatternLayout("%-5p %d{HH:mm:ss} %m [%C#%M, line %L]%n"));
					serverLogger.addAppender(consoleAppender);
				}
			} catch (IOException e) {
				System.err.println("Could not initialize server logging!");
				e.printStackTrace();
			}
			
			serverLogger.info("Server logging initialized.");
		}
		return serverLogger;
	}

	public static Logger getPremiumLogger() {
		if (premiumLogger == null) {
			// Initialise le gestionnaire de logs
			try {
				DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
						new PatternLayout("%-5p %d{HH:mm:ss} %m%n"),
						Config.getPremiumLogFile(), "'.'yyyy-MM-dd");
				
				premiumLogger = Logger.getLogger("premium");
				premiumLogger.addAppender(fileAppender);
				premiumLogger.setLevel(Level.DEBUG);
				
				if (Config.isDebug()) {
					ConsoleAppender consoleAppender = new ConsoleAppender(
							new PatternLayout("%-5p %d{HH:mm:ss} %m [%C#%M, line %L]%n"));
					premiumLogger.addAppender(consoleAppender);
				}
			} catch (IOException e) {
				System.err.println("Could not initialize premium transactions logging!");
				e.printStackTrace();
			}
			
			premiumLogger.info("Premium transactions logging initialized.");
		}
		return premiumLogger;
	}

	public static Logger getChatLogger() {
		if (chatLogger == null) {
			try {
				DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
						new PatternLayout("%d{HH:mm:ss} %m%n"),
						Config.getChatLogFile(), "'.'yyyy-MM-dd");
				
				chatLogger = Logger.getLogger("chat");
				chatLogger.addAppender(fileAppender);
				chatLogger.setLevel(Level.DEBUG);
				
//				if (Config.isDebug()) {
//					ConsoleAppender consoleAppender = new ConsoleAppender(
//							new PatternLayout("%d{HH:mm:ss} %m%n"));
//					chatLogger.addAppender(consoleAppender);
//				}
			} catch (IOException e) {
				System.err.println("Could not initialize chat logging!");
				e.printStackTrace();
			}
		}
		
		return chatLogger;
	}

	public static Logger getActionLogger() {
		if (actionLogger == null) {
			try {
				DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
						new PatternLayout("%d{HH:mm:ss} %m%n"),
						Config.getActionLogFile(), "'.'yyyy-MM-dd");
				
				actionLogger = Logger.getLogger("action");
				actionLogger.addAppender(fileAppender);
				actionLogger.setLevel(Level.DEBUG);
				
//				if (Config.isDebug()) {
//					ConsoleAppender consoleAppender = new ConsoleAppender(
//							new PatternLayout("%d{HH:mm:ss} %m%n"));
//					actionLogger.addAppender(consoleAppender);
//				}
			} catch (IOException e) {
				System.err.println("Could not initialize action logging!");
				e.printStackTrace();
			}
		}
		
		return actionLogger;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
