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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.fg.server.core.ChatManager;
import fr.fg.server.core.ConnectionManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public abstract class AjaxServlet extends HttpServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Si le serveur est en mode debug, un login peut être défini pour forcer
	// la connexion d'un joueur particulier, en définissant cette propriété
	public final static String LOGIN_PARAMETER = "_login";
	
	// Clé qui doit être renvoyée à chaque appel au serveur, permettant
	// d'éviter les attaques XSS
	public final static String SECURITY_KEY = "seckey";
	
	private static final long serialVersionUID = 828076702123158996L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		preprocess(request, response, Action.METHOD_POST);
	}
	
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		preprocess(request, response, Action.METHOD_GET);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void preprocess(HttpServletRequest request,
			HttpServletResponse response, int method) {
		
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		
		// Adresse ip distante
		String remoteAddress = request.getRemoteAddr();
		int ip = 0;
		if (remoteAddress != null && remoteAddress.split("\\.").length == 4) {
			String[] bytes = remoteAddress.split("\\.");
			ip =
				Integer.parseInt(bytes[0]) << 24 |
				Integer.parseInt(bytes[1]) << 16 |
				Integer.parseInt(bytes[2]) <<  8 |
				Integer.parseInt(bytes[3]);
		}
			
            if (ip == 2130706433 || ip == 0) { // 127.0.0.1
                String forwardedForHeader = request.getHeader("X-Forwarded-For");
               
                if (forwardedForHeader != null) {
                    // S'il y a une virgule, forwaredForHeader = forwaredForHeader.substring(position virgule)
                	if(forwardedForHeader.contains(","))
                	{
                		int positionVirgule=forwardedForHeader.lastIndexOf(",")+1;
                		forwardedForHeader = forwardedForHeader.substring(positionVirgule);
                	}
	                // Faire un forwaredForHeader = forwaredForHeader.trim()
                	forwardedForHeader = forwardedForHeader.trim();
                	
	                // Si forwaredForHeader.split("\\.").length == 4
                	if(forwardedForHeader.split("\\.").length == 4)
                	{
                		//        Faire le même traitement que plus haut pour récupérer l'IP sous forme d'entier
    	                //        Affecter ip avec la valeur
            			String[] bytesForwarded = forwardedForHeader.split("\\.");
            			ip =
            				Integer.parseInt(bytesForwarded[0]) << 24 |
            				Integer.parseInt(bytesForwarded[1]) << 16 |
            				Integer.parseInt(bytesForwarded[2]) <<  8 |
            				Integer.parseInt(bytesForwarded[3]);
            			
                	}
                }
            }
        
		session.setAttribute("ip", ip);
		
		if (session.getAttribute("id") == null) {
			if (Config.isDebug() &&
					request.getParameter(LOGIN_PARAMETER) != null) {
				// Si le serveur est en mode debug, un login peut être défini
				// pour forcer la connexion d'un joueur particulier
				Player player = DataAccess.getPlayerByLogin(
						request.getParameter(LOGIN_PARAMETER));
				
				ConnectionManager.getInstance().connect(player, ip);
				session.setAttribute("id", player.getId());
				player.generateSecurityKey();
				
				// Connexion au chat
				ChatManager.getInstance().joinDefaultChannels(player);
			} else {
				// Joueur non connecté
				process(request, response, method, null);
				return;
			}
		}
		
		// Récupère le joueur connecté
		Player player = DataAccess.getPlayerById(
				(Integer) session.getAttribute("id"));
		
		// Vérifie que l'adresse IP du joueur qui a créé la session n'a pas
		// changée
		if (remoteAddress != null) {
			if (session.getAttribute("remoteAddr") != null) {
				if (!((String) session.getAttribute("remoteAddr")
						).contains(remoteAddress)) {
					if (player != null &&
							ConnectionManager.getInstance().isConnected(player.getId())) {
						// Confirmation de mot de passe
						if (request.getParameter("_password") != null) {
							String password = Utilities.encryptPassword(
								request.getParameter("_password"));
							
							if (password.equals(player.getPassword())) {
								session.setAttribute("remoteAddr",
									session.getAttribute("remoteAddr") +
									"," + remoteAddress);
							} else {
								write(request, response,
									ActionForward.wrongPassword());
								return;
							}
						} else {
							write(request, response,
								ActionForward.confirmPassword());
							return;
						}
					} else {
						session.setAttribute("remoteAddr",
							session.getAttribute("remoteAddr") + "," + remoteAddress);
					}
				}
			} else {
				session.setAttribute("remoteAddr", remoteAddress);
			}
		}
		
		// Vérifie qu'il n'a pas été déconnecté
		if (player == null || !ConnectionManager.getInstance().isConnected(player.getId())) {
			if (Config.isDebug() && player != null &&
					request.getParameter(LOGIN_PARAMETER) != null) {
				ConnectionManager.getInstance().connect(player, ip);
			} else {
				// Joueur déconnecté
				session.removeAttribute("id");
				process(request, response, method, null);
				return;
			}
		}
		
		player.setLastPing(Utilities.now());
		
		process(request, response, method, player);
	}
	
	abstract protected void process(HttpServletRequest request,
			HttpServletResponse response, int method, Player player);
	
	protected void write(HttpServletRequest request,
			HttpServletResponse response, ActionForward forward) {
		try {
			String answer =
				"{\"data\":" + forward.getData().toString() + "," +
				 "\"type\":\"" + ActionForward.TYPES[forward.getType()] + "\"}";

			response.setContentLength(answer.getBytes("UTF-8").length);
			
			PrintWriter out = response.getWriter();
			out.print(answer);
			out.flush();
		} catch (Exception e) {
			// Dump des paramètres
			LoggingSystem.getServerLogger().warn(
					"An exception occured while handling URI '" +
					request.getServletPath() + "' with parameters [" +
					formatParameters(request) + "].", e);
		}
	}

	protected void write(HttpServletRequest request,
			HttpServletResponse response, String text) {
		try {
			response.setContentLength(text.getBytes("UTF-8").length);
			
			PrintWriter out = response.getWriter();
			out.print(text);
			out.flush();
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"An exception occured while handling URI '" +
					request.getServletPath() + "'.", e);
		}
	}
	
	protected String formatParameters(HttpServletRequest request) {
		String parameters = "";
		boolean first = true;
		Enumeration<?> parameterNames = request.getParameterNames();
		
		while (parameterNames.hasMoreElements()) {
			String name = parameterNames.nextElement().toString();
			if (first)
				first = false;
			else
				parameters += ",";
			parameters += name + "=" + (name.toLowerCase().contains(
					"password") ? "***" : request.getParameter(name));
		}
		
		return parameters;
	}
}
