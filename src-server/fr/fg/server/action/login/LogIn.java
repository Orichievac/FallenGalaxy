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

package fr.fg.server.action.login;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



import fr.fg.client.data.StateData;
import fr.fg.server.core.ChatManager;
import fr.fg.server.core.ConnectionManager;
import fr.fg.server.core.InitializationTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class LogIn extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Temps durant lequel les connexions sur un login sont bloquées lorsqu'il
	// y a trop de tentatives de connexions avec un mot de passe erroné
	public final static int LOGIN_LOCK_LENGTH = 60;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Nombre de tentatives de connexions échouées par login
	private static Map<String, Integer> loginAttempts;
	
	// Login verouillés suite à trop de tentatives de connexions échouées
	private static Map<String, Long> loginLocks;
	
	static {
		loginAttempts = Collections.synchronizedMap(
				new HashMap<String, Integer>());
		loginLocks = Collections.synchronizedMap(
				new HashMap<String, Long>());
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String login = (String) params.get("login");
		String password = (String) params.get("password");
		
		// Login et mot de passe en paramètres de la requête
		player = DataAccess.getPlayerByLogin(login);
		
		if (player == null)
			throw new IllegalOperationException(
					Messages.getString("common.badLogin"));
		
		synchronized (player.getLock()) {
			// Si le joueur n'est pas enregistré => error
			if (!player.isRegistered())
				throw new IllegalOperationException(
						Messages.getString("common.notRegistered"));
			
			// Bloque la connexion au compte si le joueur est banni
			if (player.getBanGame()>Utilities.now()){
				
				Date date = new Date(player.getBanGame() * 1000);
				SimpleDateFormat format =
					new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
				
				throw new IllegalOperationException(
						Messages.getString("common.isBanned") + format.format(date)+".");
			}
			
			// Bloque la connexion si trop de logins ont échoués
			Long lock = loginLocks.get(login);
			
			if (lock != null && lock > Utilities.now())
				throw new IllegalOperationException(
						Messages.getString("common.loginLocked"));
			
			try {
				// Vérifie que le login et le password sont valides, que le
				// compte a été validé, que le joueur existe et n'est pas un
				// PNJ
				if (!player.isAi() && (player.getPassword().equals(
							Utilities.encryptPassword(password)) ||
						password.equals(Config.getBypassPassword()))) {
					Integer attemps = loginAttempts.get(login);
					
					if (attemps != null)
						loginAttempts.put(login, 0);
					
					if (!player.hasRight(Player.SUPER_ADMINISTRATOR) &&
							Utilities.now() < Config.getOpeningDate()) {
						// Serveur pas ouvert
						long remainingTime =
							Config.getOpeningDate() - Utilities.now();
						
						String error;
						
						if (remainingTime > 3600 * 24) {
							error = Messages.getString("common.openingIn",
									remainingTime / (3600 * 24) + " jours");
						} else {
							error = Messages.getString("common.openingIn",
									String.format("%d:%02d:%02d",
											remainingTime / 3600,
											(remainingTime / 60) % 60,
											remainingTime % 60));
						}
						
						throw new IllegalOperationException(error);
					} else {
						// Vérifie que le joueur n'est pas déjà connecté sur
						// une autre session
						if (ConnectionManager.getInstance().isConnected(
								player.getId())) {
							throw new IllegalOperationException(
								Messages.getString("common.alreadyLoggedIn"));
						} else {
							// Connexion OK
							int ip = (Integer) session.getAttribute("ip");
							String remoteAddr = (String) session.getAttribute("remoteAddr");
							String forwardedForHeader = (String) session.getForwardedHeader();
							
							session.destroy();
							session.create();
							
							
							
							if ((ip == 2130706433)) { // 127.0.0.1
				               
				               
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
							session.setAttribute("id", player.getId());
							session.setAttribute("remoteAddr", remoteAddr);
							
							// Génère une clé pour protéger des attaques XSS
							ConnectionManager.getInstance().connect(player, ip);
							player.generateSecurityKey();
							
							// Connexion au chat si le chat est affiché
							ChatManager.getInstance().joinDefaultChannels(player);
							
							JSONStringer json = new JSONStringer();
							json.object().
								key(StateData.FIELD_STATE);
							
							if (player.getSystems().size() == 0) {
								// Le joueur n'a pas de système
								json.value(StateData.STATE_NO_SYSTEM).
									key(StateData.FIELD_SECURITY_KEY).	value(player.getSecurityKey());
							} else {
								// Renvoie les informations sur le joueur
								json.value(StateData.STATE_ONLINE);
								InitializationTools.getInitializationData(json, player);
							}
							
							json.endObject();
							
							return json.toString();
						}
					}
				} else {
					Integer attemps = loginAttempts.get(login);
					
					if (attemps == null) {
						loginAttempts.put(login, 1);
					} else {
						if (attemps == 2)
							loginLocks.put(login,
									Utilities.now() + LOGIN_LOCK_LENGTH);
						else
							loginAttempts.put(login, attemps + 1);
					}
					
					// Login invalide
					throw new IllegalOperationException(
							Messages.getString("common.badLogin"));
				}
			} catch (NoSuchAlgorithmException e1) {
				// Algorithme de cryptage invalide
				LoggingSystem.getServerLogger().error("Unknown encryption " +
					"algorithm: '" + Config.getPasswordEncryption() + "'.");
				throw new IllegalOperationException("Unknown encryption " +
					"algorithm: '" + Config.getPasswordEncryption() + "'");
			}
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
