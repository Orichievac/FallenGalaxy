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

package fr.fg.server.util;

public class Config {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Config instance = new Config();
	
	private SimpleBundle configuration;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Config() {
		String resourceBase = null;
		try {
			this.configuration = new SimpleBundle("conf/server-config-local");
		} catch (Exception e) {
			System.err.println("Could not load server configuration for resource base: '" + resourceBase + "'.");
			e.printStackTrace();
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	// Nom du serveur
	public static String getServerName() {
		return instance.configuration.getString("server.name");
	}
	
	// Langue du serveur
	public static String getServerLang() {
		return instance.configuration.getString("server.lang");
	}
	
	// URL du serveur
	public static String getServerURL() {
		try {
			String url = instance.configuration.getString("server.url");
			
			if (url != null)
				return url;
		} catch (Exception e) {
			// Ignoré (clé manquante)
		}
		
		return "http://" + getServerName().toLowerCase() + ".fallengalaxy.com/";
	}
	
	// URL des fichiers multimédia (css, images, javascripts...)
	public static String getMediaURL() {
		return instance.configuration.getString("server.media");
	}
	
	// URL du script qui controle les mises à jour du serveur
	public static String getMasterURL() {
		return instance.configuration.getString("server.master");
	}
	
	// Chemin d'accès au contexte qui décrit le serveur
	public static String getContextPath() {
		return instance.configuration.getString("server.context");
	}
	
	// Chemin d'accès au serveur
	public static String getServerPath() {
		return instance.configuration.getString("server.path");
	}
	
	// Algorithme de cryptage des mots de passe
	public static String getPasswordEncryption() {
		return instance.configuration.getString("server.encryption");
	}
	
	// Indique si les Access Object doivent etre chargés au démarrage du
	// serveur
	public static boolean isAOPreloaded() {
		return instance.configuration.getBoolean("server.preloadAO");
	}
	
	// Indique si le serveur est en mode debug
	public static boolean isDebug() {
		return instance.configuration.getBoolean("server.debug");
	}
	
	// Renvoie le mot de passe pour se connecter à n'importe quel compte
	public static String getBypassPassword() {
		return instance.configuration.getString("server.bypassPassword");
	}
	
	// Date d'ouverture du serveur
	public static long getOpeningDate() {
		return instance.configuration.getLong("server.openingDate");
	}
	
	// Fichier de paramètres du serveur
	public static String getSanitizer() {
		return instance.configuration.getString("server.sanitizer");
	}
	
	// Protocole utilisé pour les mails
	public static String getMailProtocol() {
		return instance.configuration.getString("mail.protocol");
	}

	// Hote utilisé pour envoyer les mails
	public static String getMailHost() {
		return instance.configuration.getString("mail.host");
	}

	// Port utilisé pour envoyer les mails
	public static String getMailPort() {
		return instance.configuration.getString("mail.port");
	}

	// Adresse email utilisée pour envoyer les mails
	public static String getMailFrom() {
		return instance.configuration.getString("mail.from");
	}

	// Mot de passe SMTP pour envoyer les mails
	public static String getMailPassword() {
		return instance.configuration.getString("mail.password");
	}
	
	// Fichier de mapping de la base de données
	public static String getDbMapping() {
		return instance.configuration.getString("mapping.db");
	}

	// Fichier de mapping des actions
	public static String getActionMapping() {
		return instance.configuration.getString("mapping.action");
	}

	// Driver JDBC utilisé pour se connecter à la base de données
	public static String getJdbcDriver() {
		return instance.configuration.getString("jdbc.driver");
	}

	// Préfixe des tables de la base de données
	public static String getTablePrefix() {
		return instance.configuration.getString("jdbc.tablePrefix");
	}
	
	// URL de connexion JDBC
	public static String getJdbcUrl() {
		return instance.configuration.getString("jdbc.url");
	}

	// Nom d'utilisateur pour se connecter à la base de données
	public static String getJdbcUser() {
		return instance.configuration.getString("jdbc.user");
	}

	// Mot de passe pour se connecter à la base de données
	public static String getJdbcPassword() {
		return instance.configuration.getString("jdbc.password");
	}
	
	// Servlet d'erreur
	public static String getErrorServlet() {
		return instance.configuration.getString("servlet.error");
	}

	// Servlet d'erreur
	public static String getMessageServlet() {
		return instance.configuration.getString("servlet.message");
	}
	
	// Fichier de log du serveur
	public static String getServerLogFile() {
		return instance.configuration.getString("log.server.file");
	}

	// Fichier de log des transactions premiums
	public static String getPremiumLogFile() {
		return instance.configuration.getString("log.premium.file");
	}

	// Fichier de log du chat
	public static String getChatLogFile() {
		return instance.configuration.getString("log.chat.file");
	}

	// Fichier de log des actions
	public static String getActionLogFile() {
		return instance.configuration.getString("log.action.file");
	}

	// Niveau de log du serveur
	public static String getServerLogLevel() {
		return instance.configuration.getString("log.server.level");
	}
	
	// Unité de temps
	public static int getTimeUnit() {
		return instance.configuration.getInt("game.timeUnit");
	}

	// Code site allopass
	public static String getAllopassIdSite() {
		return instance.configuration.getString("allopass.site");
	}

	// Code produit allopass
	public static String getAllopassIdProduct() {
		return instance.configuration.getString("allopass.product");
	}

	// Code marchand allopass public
	public static String getAllopassPublicApiKey() {
		return instance.configuration.getString("allopass.publicApiKey");
	}

	// Code marchand allopass privé
	public static String getAllopassPrivateApiKey() {
		return instance.configuration.getString("allopass.privateApiKey");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
