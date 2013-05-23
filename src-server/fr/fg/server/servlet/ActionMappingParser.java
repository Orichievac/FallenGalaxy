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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import fr.fg.server.servlet.format.Format;
import fr.fg.server.util.LoggingSystem;

public class ActionMappingParser {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@SuppressWarnings("unchecked")
	public ActionMapping parse(URL url) throws JDOMException, IOException {
		Map<String, String> constants = new HashMap<String, String>();
		
		SAXBuilder builder = new SAXBuilder(true);
		Document doc = builder.build(url);
		
		// Racine du document
		Element rootNode = doc.getRootElement();
		
		ActionMapping actionMapping = new ActionMapping();
		actionMapping.setBasePackage(rootNode.getAttributeValue("package"));
		
		// Construit la liste des constantes
		List<Element> constantElements = rootNode.getChildren("constant");
		
		for (int i = 0; i < constantElements.size(); i++) {
			Element constantElement = constantElements.get(i);
			
			constants.put(
					parseConstants(constantElement.getAttributeValue(
							"name"), constants),
					parseConstants(constantElement.getAttributeValue(
							"value"), constants));
		}
		
		// Liste des actions
		List<Element> actionElements = rootNode.getChildren("action");
		
		for (int i = 0; i < actionElements.size(); i++) {
			Element actionElement = actionElements.get(i);
			String actionName = parseConstants(
					actionElement.getAttributeValue("name"), constants);
			
			try {
				// Vérifie que l'action n'a pas déjà été définie
				if (actionMapping.getMapping(actionName) != null)
					throw new Exception("Action defined twice!");
				
				Action action = (Action) Class.forName(
						actionMapping.getBasePackage() + "." + parseConstants(
						actionElement.getAttributeValue(
								"class"), constants)).newInstance();
				
				action.setMethod(parseConstants(
						actionElement.getAttributeValue(
								"method"), constants).equals("get") ?
								Action.METHOD_GET : Action.METHOD_POST);
				
				// Droits nécessaires pour pouvoir éxécuter l'action
				String rights = actionElement.getAttributeValue("rights");
				if (rights != null) {
					try {
						int value = Integer.parseInt(
								parseConstants(rights, constants, true));
						action.setRights(value);
					} catch (Exception e) {
						// Ne charge pas l'action si les droits sont invalides
						throw new Exception(
								"Invalid rights value: '" + rights + "'. " +
								"Action was not loaded for security reasons.");
					}
				}
				
				// Etat de la connexion pour pouvoir éxecuter l'action
				String connectionStatus = actionElement.getAttributeValue("connection");
				if (connectionStatus != null) {
					int value;
					
					if (connectionStatus.equals("login")) {
						value = Action.CONNECTION_STATUS_LOGIN;
					} else if (connectionStatus.equals("logout")) {
						value = Action.CONNECTION_STATUS_LOGOUT;
					} else if (connectionStatus.equals("login_or_logout")) {
						value = Action.CONNECTION_STATUS_LOGIN_OR_LOGOUT;
					} else {
						// Ne charge pas l'action si l'état de la connexion est
						// invalide
						throw new Exception(
							"Invalid connection status: '" + connectionStatus +
							"'. Action was not loaded for security reasons.");
					}
					
					action.setConnectionStatus(value);
				}
				
				// Paramètres de l'action
				List<Element> parameterElements =
					actionElement.getChildren("action-param");
				
				for (int j = 0; j < parameterElements.size(); j++) {
					Element parameterElement = parameterElements.get(j);
					
					String paramName = parseConstants(
							parameterElement.getAttributeValue(
									"name"), constants);
					action.addParam(paramName, Boolean.parseBoolean(
							parseConstants(parameterElement.getAttributeValue(
									"required"), constants)));
					
					List<Element> formatsElements =
						parameterElement.getChildren("param-format");
					
					// Formats du paramètre
					for (int k = 0; k < formatsElements.size(); k++) {
						Element formatElement = formatsElements.get(k);
						
						Format format = (Format) Class.forName(parseConstants(
								formatElement.getAttributeValue(
										"class"), constants, true)).newInstance();
						
						// Arguments du format
						Object[] args = new String[formatElement.getChildren(
								"arg").size()];
						List<Element> argsElements = formatElement.getChildren(
								"arg");
						
						for (int l = 0; l < argsElements.size(); l++) {
							args[l] = parseConstants(argsElements.get(l
									).getAttributeValue("value"), constants,
									true);
						}
						
						format.getClass().getMethod("setArgs", Object[].class
								).invoke(format, new Object[]{args});
						
						action.addFormat(paramName, format);
					}
				}
				
				actionMapping.addMapping(actionName, action);
				
				LoggingSystem.getServerLogger().trace(
						"Action registered: '" + actionName + "'.");
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn(
						"Could not load action: '" + actionName + "'.", e);
			}
		}
		
		return actionMapping;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private String parseConstants(String value,
			Map<String, String> constants) {
		return parseConstants(value, constants, false);
	}
	
	private String parseConstants(String value,
			Map<String, String> constants, boolean allowShortcut) {
		if (allowShortcut)
			// Si la valeur vaut une clé, on la remplace par la valeur de la
			// clé
			for (String key : constants.keySet())
				if (value.equals(key))
					return constants.get(key);
		
		for (String key : constants.keySet())
			value = value.replace("${" + key + "}", constants.get(key));
		return value;
	}
}
