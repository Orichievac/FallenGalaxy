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

package fr.fg.server.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Message;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;

public class MessageTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		KEEP_QUOTES		= 1 << 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	/**
	 * Ajoute un message à la messagerie d'un joueur.
	 *
	 * @param message Le message a envoyer.
	 */
	public static void sendMessage(Message message) {
		// Crée un nouveau message dans la base de données
		DataAccess.save(message);
		
		// Signale au destinataire qu'il a reçu un message
		UpdateTools.queueNewMessageUpdate(message.getIdPlayer());
	}
	
	public static String sanitizeHTML(String html) throws IllegalOperationException {
		try {
	        // Construit les données à envoyer
	        String data = "content=" + URLEncoder.encode(html, "UTF-8");
	        
	        // Envoie les données
	        URL url = new URL(Config.getSanitizer());
	        URLConnection connection = url.openConnection();
	        connection.setDoOutput(true);
	        OutputStreamWriter writer = new OutputStreamWriter(
	        		connection.getOutputStream());
	        writer.write(data);
	        writer.flush();
	    
	        // Récupère la réponse
	        BufferedReader reader = new BufferedReader(
	        		new InputStreamReader(connection.getInputStream(), "UTF-8"));
	        String line;
	        StringBuffer buffer = new StringBuffer();
	        while ((line = reader.readLine()) != null)
	        	buffer.append(line);
	        writer.close();
	        reader.close();
	        
	        return buffer.toString();
	    } catch (Exception e) {
	    	LoggingSystem.getServerLogger().warn("Could not sanitize message.", e);
	    	throw new IllegalOperationException("Impossible de parser le message.");
	    }
	}
	
	public static String tidyHTML(String html) {
		return tidyHTML(html, 0);
	}
	
	public static String tidyHTML(String html, int options) {
		// Entités
		html = html.replaceAll("&(?!(#[0-9]+|amp|nbsp|lt|gt|quot);)", "&amp;");
		html = html.replace("<", "&lt;");
		html = html.replace(">", "&gt;");
		
		if ((options & KEEP_QUOTES) == 0)
			html = html.replace("\"", "&quot;");
		
		return html;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
