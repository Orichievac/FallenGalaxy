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

package fr.fg.server.servlet;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class Servers {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String SERVERS_URL =
		"http://fallengalaxy.com/servers.xml";
	
	public final static String
		PARAMETER_NAME		= "name",
		PARAMETER_URL		= "url",
		PARAMETER_LANGUAGE	= "language",
		PARAMETER_OPENING	= "opening";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Servers instance = new Servers();
	
	private List<Map<String, String>> servers;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Servers() {
		this.servers = new LinkedList<Map<String,String>>();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static List<Map<String, String>> getAllServers() {
		return instance.servers;
	}

	@SuppressWarnings("unchecked")
	public static void updateServersCache() throws Exception {
		SAXBuilder builder = new SAXBuilder(false);
		Document document = builder.build(new URL(SERVERS_URL));
		
		// Racine du document
		Element rootNode = document.getRootElement();
		
		// Construit la liste des serveurs
		List<Element> serverElements = rootNode.getChildren("server");
		List<Map<String, String>> servers = Collections.synchronizedList(
				new LinkedList<Map<String,String>>());
		
		for (int i = 0; i < serverElements.size(); i++) {
			Element serverElement = serverElements.get(i);
			
			// Lit les paramètres du serveur
			Map<String, String> server = Collections.synchronizedMap(
					new HashMap<String, String>());
			List<Element> parameterElements =
					serverElement.getChildren("parameter");
			
			for (int j = 0; j < parameterElements.size(); j++) {
				Element parameterElement = parameterElements.get(j);
				server.put(parameterElement.getChild("key").getValue(),
						parameterElement.getChild("value").getValue());
			}
			servers.add(server);
		}
		
		instance.servers = servers;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
