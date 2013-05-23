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

import java.util.Map;


import fr.fg.server.data.Player;
import fr.fg.server.servlet.format.Format;

abstract public class Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		METHOD_POST = 1,
		METHOD_GET  = 2;
	
	public final static int
		CONNECTION_STATUS_LOGIN = 1 << 0,
		CONNECTION_STATUS_LOGOUT = 1 << 1,
		CONNECTION_STATUS_LOGIN_OR_LOGOUT =
			CONNECTION_STATUS_LOGIN | CONNECTION_STATUS_LOGOUT;
	
	public final static String FORWARD_SUCCESS = "\"OK\"";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Méthode (GET ou POST)
	private int method;
	
	// Paramètres de l'action
	private Parameter[] parameters;
	
	// Droits minimum pour pouvoir éxécuter l'action
	private int rights;
	
	// Indique l'état dans lequel doit être le joueur pour pouvoir accéder à
	// l'action (connecté, déconnecté...)
	private int connectionStatus;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Action() {
		this(METHOD_POST);
	}
	
	public Action(int method) {
		this(method, Player.USER);
	}
	
	public Action(int method, int rights) {
		this(method, rights, CONNECTION_STATUS_LOGIN);
	}
	
	public Action(int method, int rights, int connectionStatus) {
		this.method = method;
		this.parameters = new Parameter[0];
		this.rights = rights;
		this.connectionStatus = connectionStatus;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void init() {
		// Méthode pouvant être redéfinie dans les sous-classes
	}
	
	abstract protected String execute(Player player,
			Map<String, Object> params, Session session) throws Exception;
	
	public int getMethod() {
		return method;
	}
	
	/**
	 * Indique si les paramètres de la requête doivent être récupérés par la
	 * méthode GET ou POST. Les paramètres sont récupérés par défaut avec POST.
	 *
	 * @param method La méthode utilisée, parmi les constantes METHOD_POST ou
	 * METHOD_GET.
	 */
	public void setMethod(int method) {
		this.method = method;
	}
	
	/**
	 * Ajoute un paramètre à récupérer dans la requête HTTP.
	 *
	 * @param name Le nom du paramètre à récupérer.
	 * @param required Indique si le paramètre est obligatoire ou non.
	 */
	public void addParam(String name, boolean required) {
		Parameter[] tmp = new Parameter[parameters.length + 1];
		for (int i = 0; i < parameters.length; i++)
			tmp[i] = parameters[i];
		parameters = tmp;
		parameters[parameters.length - 1] = new Parameter(name, required);
	}
	
	/**
	 * Impose un format à la valeur d'un paramètre. Plusieurs formats peuvent
	 * être défini pour un même paramètre, il seront successivement appliqués
	 * dans l'ordre dans lequel ils ont été ajoutés.
	 *
	 * @param param Le nom du paramètre formaté.
	 * @param format Le format que doit respecter la valeur du paramètre.
	 */
	public void addFormat(String param, Format format) {
		for (Parameter parameter : parameters) {
			if (parameter.getName().equals(param)) {
				parameter.addFormat(format);
				return;
			}
		}
		
		throw new IllegalArgumentException(
				"Could not set format for undefined parameter: " + param);
	}
	
	public Parameter[] getParams() {
		return parameters;
	}

	public int getRights() {
		return rights;
	}
	
	public void setRights(int rights) {
		this.rights = rights;
	}

	public int getConnectionStatus() {
		return connectionStatus;
	}
	
	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
