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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

import fr.fg.server.core.UpdateManager;
import fr.fg.server.dao.DataLayer;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.format.Format;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class ActionServlet extends AjaxServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = 371063707322209512L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static ActionMapping actionMapping;
	
	private static List<Watchdog> watchdogs;
	
	static {
		watchdogs = Collections.synchronizedList(new ArrayList<Watchdog>());
		
		try {
			// Charge le fichier de mapping des actions
			ActionMappingParser parser = new ActionMappingParser();
			actionMapping = parser.parse(
					DataLayer.class.getClassLoader().getResource(
							Config.getActionMapping()));
			
			LoggingSystem.getServerLogger().info("Action mapping loaded (" +
					actionMapping.getAllMappings().size() + " actions found).");
		} catch (Exception e) {
			// Echec du chargement du fichier
			LoggingSystem.getServerLogger().error(
					"Could not load action mapping.", e);
			actionMapping = new ActionMapping();
		}
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void process(HttpServletRequest request,
			HttpServletResponse response, int method, Player player) {
		// Supprime le / et l'extension .do
		String path = request.getServletPath().substring(1);
		path = path.substring(0, path.length() - 3);
		Action action = actionMapping.getMapping(path);
		
		if (action != null && action.getMethod() == method) {
			// Vérifie que le joueur est connecté si l'action requiert d'être
			// connecté
			if (action.getConnectionStatus() == Action.CONNECTION_STATUS_LOGIN &&
					player == null) {
				write(request, response, ActionForward.disconnected());
				return;
			}
			
			// Si le joueur est connecté, vérifie que sa clé de sécurité est
			// valide
			if (action.getConnectionStatus() == Action.CONNECTION_STATUS_LOGIN &&
					(!Config.isDebug() && (request.getParameter(SECURITY_KEY) == null ||
					!player.getSecurityKey().equals(request.getParameter(SECURITY_KEY))))) {
				write(request, response, ActionForward.error("Invalid security key."));
				return;
			}
			
			// Vérifie que le joueur est déconnecté si l'action requiert d'être
			// déconnecté
			if (action.getConnectionStatus() == Action.CONNECTION_STATUS_LOGOUT &&
					player != null) {
				write(request, response, ActionForward.error(
					Messages.getString("error.connected")));
				return;
			}
			
			// Vérifie que le joueur a les droits nécessaires pour éxécuter
			// l'action
			if (player != null && !player.hasRight(action.getRights())) {
				LoggingSystem.getServerLogger().info(Messages.getString(
					"error.unauthorizedAccessLog", path, player.getLogin()));
				write(request, response, ActionForward.error(
					Messages.getString("error.unauthorizedAccess")));
				return;
			}
			
			// Parse les paramètres de la requête
			Parameter[] params = action.getParams();
			Map<String, Object> parameterValues =
				new HashMap<String, Object>();
			
			for (Parameter parameter : params) {
				String paramValue = request.getParameter(parameter.getName());
				
				if (paramValue == null) {
					if (parameter.isRequired()) {
						// Paramètre obligatoire manquant
						write(request, response, ActionForward.error(
								Messages.getString("error.missingParameter",
										parameter.getName())));
						return;
					}
				} else {
					Object value = paramValue;
					for (Format format : parameter.getFormats()) {
						value = format.format(value.toString());
						
						if (value == null) {
							// Format du paramètre invalide
							String key = action.getClass().getName() + "." +
								parameter.getName() + ".invalidFormat";
							String error;
							
							// Teste si un message d'erreur personnalisé a été
							// défini pour les erreurs de format du paramètre
							if (Messages.containsString(key))
								error = Messages.getString(key);
							else
								error = Messages.getString(
										"error.invalidFormat",
										parameter.getName());
							write(request, response, ActionForward.error(error));
							return;
						}
					}
					parameterValues.put(parameter.getName(), value);
				}
			}
			
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
			String forwardedForHeader= "";
	            if (ip == 2130706433 || ip == 0) { // 127.0.0.1
	                forwardedForHeader = request.getHeader("X-Forwarded-For");
	               
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
	        
			
			// Log l'action
			LoggingSystem.getActionLogger().info("->     " +
				path + " [" + Thread.currentThread().getName() + "] - player [id=" +
				(player != null ? player.getId() : "???") +
				",login=" + (player != null ? player.getLogin() : "???") + ",ip=" +
				ip + ", forwarded="+forwardedForHeader+"], parameters [" + formatParameters(request) + "].");
			long start = System.currentTimeMillis();
			
			Watchdog watchdog = new Watchdog(20000);
			
			ActionForward forward;
			try {
				watchdog.addTimeoutObserver(new MaxExecutionTimeWatchdog(Thread.currentThread()));
				watchdogs.add(watchdog);
				watchdog.start();
				
				// Execute l'action. Un lock est placé sur le joueur pour
				// éviter qu'il puisse éxécuter plusieurs actions concurrentes
				if (player != null) {
					synchronized (player.getLock()) {
						forward = ActionForward.success(action.execute(
							player, parameterValues, new Session(request)));
					}
				} else {
					forward = ActionForward.success(action.execute(
						player, parameterValues, new Session(request)));
				}
			} catch (IllegalOperationException e) {
				// Erreur normale (toutes les conditions pour éxécuter l'action
				// ne sont pas vérifiées)
				forward = ActionForward.error(Utilities.escape(e.getMessage()));
			} catch (Exception e) {
				// Erreur anormale
				forward = ActionForward.error("Une erreur inattendue s'est " +
						"produite. L'erreur a été enregistrée et sera " +
						"corrigée dans les plus brefs délais.");
				LoggingSystem.getServerLogger().warn(
						"An exception occured while handling URI '" +
						request.getServletPath() + "' with parameters [" +
						formatParameters(request) + "] and player [id=" +
						(player != null ? player.getId() : "???") +
						",login=" + (player != null ? player.getLogin() :
						"???") + ",ip=" + request.getRemoteAddr() + "].", e);
			} finally {
				watchdog.stop();
				watchdogs.remove(watchdog);
			}
			
			// Déclenche les mises à jour en attente sur le thread
			UpdateManager.getInstance().flushUpdates();
			
			// Log l'action
			LoggingSystem.getActionLogger().info((forward.getType() ==
				ActionForward.SUCCESS ? "   <-  " : "   KO  ") + path +
				" [" + Thread.currentThread().getName() + "]" +
				" (+" + (System.currentTimeMillis() - start) + "ms)");
			
			write(request, response, forward);
		} else {
			// Requete invalide
			String error;
			
			if (action == null) {
				// Action non définie
				error = Messages.getString("error.undefinedAction", path);
			} else {
				// Méthode invalide
				String methodString = method == Action.METHOD_GET ? "GET" : "POST";
				error = Messages.getString("error.invalidMethod", methodString, path);
			}
			LoggingSystem.getServerLogger().info(error);
			write(request, response, ActionForward.error(error));
		}
	}
	
	public static void stopWatchdogs() {
		synchronized (watchdogs) {
			for (Watchdog watchdog : watchdogs)
				watchdog.stop();
			watchdogs.clear();
		}
	}
	
	private class MaxExecutionTimeWatchdog implements TimeoutObserver {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private Thread thread;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public MaxExecutionTimeWatchdog(Thread thread) {
			this.thread = thread;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void timeoutOccured(Watchdog watchdog) {
			watchdogs.remove(watchdog);
			
			LoggingSystem.getServerLogger().error("Blocked thread detected [" +
					thread.getName() + "].");
			
			// Dump l'état des threads
			Map<Thread, StackTraceElement[]> allTraces = Thread.getAllStackTraces();
			StringBuffer buffer = new StringBuffer();
			
			for (Thread thread : allTraces.keySet()) {
				StackTraceElement[] trace = allTraces.get(thread);
				buffer.append("Dumping thread stack trace: ");
				buffer.append(thread.getName());
				buffer.append(" [State=");
				buffer.append(thread.getState());
				buffer.append("]\n");
				
		        for (int i = 0; i < trace.length; i++) {
		        	buffer.append("\tat ");
		        	buffer.append(trace[i]);
		        	buffer.append("\n");
		        }
			}
			
			LoggingSystem.getServerLogger().info(buffer.toString());
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
