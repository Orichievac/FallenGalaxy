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

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	@SuppressWarnings("unused")
	private final static DBManager instance = new DBManager();
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private DBManager() {
		// Crée la source de données pour la base de données
		try {
			Class.forName(Config.getJdbcDriver());
			LoggingSystem.getServerLogger().info("Database driver loaded.");
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error(
					"Could not load database driver.", e);
		}
	}

	// --------------------------------------------------------- METHODES -- //
	
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(
					Config.getJdbcUrl(),
					Config.getJdbcUser(),
					Config.getJdbcPassword());
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error(
					"Could not create database connection.", e);
		}
		return null;
	}
	
	public static String parseQuery(String sql) {
		return sql.replace("%prefix%", Config.getTablePrefix());
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
