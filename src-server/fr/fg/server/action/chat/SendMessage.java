/*
Copyright 2010 Jeremie Gottero, Romain Prevot, Thierry Chevalier, Nicolas Bosc

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

package fr.fg.server.action.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import fr.fg.client.data.ChatMessageData;
import fr.fg.server.data.Contact;
import fr.fg.server.core.ChatManager;
import fr.fg.server.core.ConnectionManager;
import fr.fg.server.core.MessageTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.core.Ladder;
import fr.fg.server.data.Achievement;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Ban;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.MessageOfTheDay;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.servlet.format.NameStringFormat;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class SendMessage extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TARGET_CHANNEL = 0,
		TARGET_SELF = 1,
		TARGET_WHISPER = 2;
		
	public final static String
		TYPE_DEFAULT = "default",
		TYPE_INFO = "info",
		TYPE_CONNECTION = "connection",
		TYPE_DISCONNECTION = "disconnection",
		TYPE_JOIN_CHANNEL = "join",
		TYPE_LEAVE_CHANNEL = "leave",
		TYPE_WHO = "who",
		TYPE_MODERATOR ="moderator",
		TYPE_ONLINE = "online",
		TYPE_ALLY_MEMBERS = "allyMembers",
		TYPE_PING = "ping",
		TYPE_WHISPER_SENT = "whisperSent",
		TYPE_WHISPER_RECEIVED = "whisperReceived",
		TYPE_ERROR = "error",
		TYPE_MODERATION = "moderation";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String channel = (String) params.get("channel");
		String message = (String) params.get("message");
		
		if (!ChatManager.getInstance().isInChannel(channel, player.getId()))
			throw new IllegalOperationException(
				"Vous ne pouvez pas envoyer de message sur ce canal.");
		
		// Vérifie que le message ne contient pas l'ekey du joueur
		if (player.getEkey().length() > 0 && message.contains(player.getEkey()))
			throw new IllegalOperationException("Ne transmettez pas votre clé d'export à d'autres joueurs.");
		
		// Log le message
		LoggingSystem.getChatLogger().info("[" + channel + "] " +
				player.getLogin() + ": " + message);
		
		// Parse le message
		message = message.replace(new String(new char[]{10, 13}), " ").trim();
		message = MessageTools.tidyHTML(message, MessageTools.KEEP_QUOTES);
		
		StringBuffer content = new StringBuffer();
		String type;
		Player whisperTarget = null;
		int target;
		boolean updateChannels = false;
		Ladder ladder = null;
		
		List<Update> updates = new ArrayList<Update>();

		// Force l'activation du canal si le joueur écrit dedans
		if (!ChatManager.getInstance().isChannelEnabled(player, channel)) {
			ChatManager.getInstance().setChannelEnabled(player, channel, true);
			updateChannels = true;
		}
		
		//commandes utilsables même si le joueur est banni :
		// /time, /?, /ping, /played
		
		if (message.equals("/played") || message.equals("/joué")) {
			// Temps passé sur FG (/played)
			target = TARGET_SELF;
			type = TYPE_INFO;
			
			content.append("Vous avez joué " + (player.getPlayedTime(Player.SCALE_DAY) / 3600) +
				"h sur Fallen Galaxy aujourd'hui, " + (player.getPlayedTime(Player.SCALE_MONTH) / 3600) +
				"h ce mois, et " + (player.getPlayedTime(Player.SCALE_OVERALL) / 3600) + "h en tout !");
		}
		else if (message.startsWith("/ping")) {
			// Ping (/ping)
			target = TARGET_SELF;
			type = TYPE_PING;
			content.append(message.length() > 6 ? message.substring(6) : "");
		}
		else if (message.equals("/online") || message.equals("/enligne") || message.equals("/?")) {
			// Liste des joueurs connectés sur Fallen Galaxy (/online)
			target = TARGET_SELF;
			type = TYPE_ONLINE;
			
			Set<Integer> players = 
				ConnectionManager.getInstance().getConnectedPlayers();
			List<String> playersLogin = new ArrayList<String>(players.size());
			
			synchronized (players) {
				for (Integer idPlayer : players) {
					Player onlinePlayer = DataAccess.getPlayerById(idPlayer);
										
					Contact playersContact = DataAccess.getContactByContact(player.getId(), onlinePlayer.getId());
										
					String line = "";
					if(playersContact != null) /* && playersContact.getType().equals(Contact.TYPE_FRIEND))*/ {
						line += "*";
					}
					
					line += onlinePlayer.getLogin();
					
					if(onlinePlayer.getIdAlly() != 0) {
						line += "|" + onlinePlayer.getAllyTag() + "|" + onlinePlayer.getAllyName();
					}
					
					playersLogin.add(line);
				}
			}
			
			//Collections.sort(playersLogin, String.CASE_INSENSITIVE_ORDER);
			
			int i = 0;
			for (String login : playersLogin)
				content.append((i++ == 0 ? "" : ",") + login);
			
		}
		else if (message.equals("/time") || message.equals("/heure")) {
			// Heure serveur (/time)
			target = TARGET_SELF;
			type = TYPE_INFO;
			
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
			content.append("Nous sommes le ");
			content.append(format.format(date));
		}
		else if(message.startsWith("/motd"))
		{
			target = TARGET_SELF;
			if(player.hasRight(Player.ADMINISTRATOR))
			{
				if (message.contains(" ")) {
					//on récupère les mots (séparés par des espaces)
					String[] split = message.split(" ", 3);
					
					//s'il y a trop peu de mots
					if (split.length < 3) {
						// Syntaxe invalide
						type = TYPE_ERROR;
						content.append("Syntaxe : /motd chat|ingame {message}");
					}
					else
					{
						if(split[1].equals("chat") || split[1].equals("ingame")) //ok
						{
							target = TARGET_CHANNEL;
							type = TYPE_INFO;
							int motdType = split[1].equals("chat")? MessageOfTheDay.CHAT : MessageOfTheDay.INGAME;
							MessageOfTheDay motd = DataAccess.getMessageOfTheDayById(motdType);
							
							
							synchronized(motd.getLock()){
							motd = DataAccess.getEditable(motd);
							motd.setMessage(split[2]);
							motd.save();		
							}
							
							content.append(player.getLogin()+" a changé le message du jour en:<br/>"+split[2]);
						}
						else
						{
							// Syntaxe invalide
							type = TYPE_ERROR;
							content.append("Syntaxe : /motd chat|ingame {message}");
						}
					}
				}
				else
				{
					type = TYPE_ERROR;
					content.append("Syntaxe : /motd chat|ingame {message}");
				}
			}
			else
			{
				type = TYPE_ERROR;
				content.append("Commande réservée aux administrateurs!");
			}
		}
		else if (message.startsWith("/roll")){
			// roll de 0 a 100
			target = TARGET_SELF;
			type = TYPE_INFO;

				int roll;
				type = TYPE_INFO;
				roll=(int)Math.round(Math.random()*100);
				content.append("Vous avez obtenu "+roll+" !");
			}
		//Ajout d'amis
		else if (message.startsWith("/addf") || message.startsWith("/addfriend")){
			target = TARGET_SELF;
			type = TYPE_INFO;
			
			Player commandTarget = null;
			String login = "";
			boolean valid = true;
			
			//si le message contient des espaces
			if (message.contains(" ")) {
				//on récupère les mots (séparés par des espaces)
				String[] split = message.split(" ", 2);
				
				//s'il y'a trop mots
				if (split.length < 2) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /addf joueur");
					valid = false;
				} else {
					// Teste si le pseudo est entre quotes
					login = split[1];
					if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
						login = message.split("\"", 3)[1];
					
					commandTarget = DataAccess.getPlayerByLogin(login);
				}
			} else {
				commandTarget = player;
				login = player.getLogin();
			}//if (message.contains(" "))
			
			if (valid) {
				// Vérifie que le joueur existe
				if(commandTarget == null) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Joueur inconnu : " + login + ".");
				} else if(commandTarget.getLogin().equals(player.getLogin())) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Vous ne pouvez pas vous ajouter vous même à votre liste.");
				} else {
					//récupération du contact
					Contact contact = DataAccess.getContactByContact(player.getId(), commandTarget.getId());
					
					//s'il existe déja un contact
					if (contact != null) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Ce joueur fait déjà partie de vos " +
								(contact.getType().equals("friend") ? "amis." : "joueurs ignorés."));
					} else {
					
						if (!player.hasRight(Player.PREMIUM)) {
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Il faut un compte premium pour pouvoir ajouter un nouvel ami.");
						}
						else {
							DataAccess.save(new Contact(player.getId(), commandTarget.getId(), Contact.TYPE_FRIEND));
							//On ajoute un événement pour le joueur qui ajoute un autre joueur dans sa liste d'amis
							Event event1 = new Event(Event.EVENT_PLAYER_ADD_FRIEND, Event.TARGET_PLAYER,
									player.getId(), 0, -1, -1, commandTarget.getLogin());
							event1.save();
							//Et on ajoute un autre événement pour le joueur ajouté
							Event event2 = new Event(Event.EVENT_PLAYER_ADDED_FRIEND, Event.TARGET_PLAYER,
									commandTarget.getId(), 0, -1, -1, player.getLogin());
							event2.save();
							UpdateTools.queueNewEventUpdate(player.getId());
							UpdateTools.queueNewEventUpdate(commandTarget.getId());
							target = TARGET_SELF;
							type = TYPE_INFO;
							content.append("Vous êtes maintenant ami avec " + commandTarget.getLogin() + ".");
						}
					}//if (contact != null) 
				}//if(commandTarget == null) {
			}//if (valid)
		}//else if (message.startsWith("/addf") || message.startsWith("/addfriend"))
		
		
		else if (message.startsWith("/remf") || message.startsWith("/remfriend") ||
				message.startsWith("/delf") || message.startsWith("/delfriend")
		) {
			
			target = TARGET_SELF;
			type = TYPE_INFO;
			
			Player commandTarget = null;
			String login = "";
			boolean valid = true;
			
			//si le message contient des espaces
			if (message.contains(" ")) {
				//on récupère les mots (séparés par des espaces)
				String[] split = message.split(" ", 2);
				
				//s'il y'a trop mots
				if (split.length < 2) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /delf joueur");
					valid = false;
				} else {
					// Teste si le pseudo est entre quotes
					login = split[1];
					if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
						login = message.split("\"", 3)[1];
					
					commandTarget = DataAccess.getPlayerByLogin(login);
				}
			} else {
				commandTarget = player;
				login = player.getLogin();
			}//if (message.contains(" "))
			
			if (valid) {
				// Vérifie que le joueur existe
				if(commandTarget == null) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Joueur inconnu : " + login + ".");
				} else if(commandTarget.getLogin().equals(player.getLogin())) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Vous ne pouvez pas vous ajouter vous même à votre liste.");
				} else {
					//récupération du contact
					Contact contact = DataAccess.getContactByContact(player.getId(), commandTarget.getId());
					
					//s'il existe déja un contact
					if (contact == null) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Ce joueur ne fais pas partie de vos amis");
					} else if(contact.getType() != Contact.TYPE_FRIEND) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Ce joueur ne fais pas partie de vos amis. Bien au contraire... Il fais partie de votre liste d'ignorés.");
					} else {
						contact.delete();
						//On ajoute un événement pour le joueur qui retire un autre joueur de sa liste d'amis
						Event event1 = new Event(Event.EVENT_PLAYER_REMOVE_FRIEND, Event.TARGET_PLAYER,
								player.getId(), 0, -1, -1, commandTarget.getLogin());
						event1.save();
						//Et on ajoute un autre événement pour le joueur retiré
						Event event2 = new Event(Event.EVENT_PLAYER_REMOVED_FRIEND, Event.TARGET_PLAYER,
								commandTarget.getId(), 0, -1, -1, player.getLogin());
						event2.save();
						UpdateTools.queueNewEventUpdate(player.getId());
						UpdateTools.queueNewEventUpdate(commandTarget.getId());
						target = TARGET_SELF;
						type = TYPE_INFO;
						content.append("Vous n'êtes plus ami avec " + commandTarget.getLogin());
					}//if (contact == null) 
				}//if(commandTarget == null) {
			}//if (valid)
		}//else if (message.startsWith("/addf") || message.startsWith("/addfriend"))
		
		else if(message.startsWith("/mj") || message.startsWith("/modo")) {
			Set<Integer> moderators = ConnectionManager.getInstance().getConnectedModerators();
			List<String> moderatorsLogin = new ArrayList<String>(moderators.size());
			target = TARGET_SELF;
			type = TYPE_MODERATOR;
			synchronized (moderators) {
				for (Integer idModerator : moderators) {
					Player onlineModerator = DataAccess.getPlayerById(idModerator);
										
					Contact playersContact = DataAccess.getContactByContact(player.getId(), onlineModerator.getId());
										
					String line = "";
					if(playersContact != null) /* && playersContact.getType().equals(Contact.TYPE_FRIEND))*/ {
						line += "*";
					}
					
					line += onlineModerator.getLogin();
					
					if(onlineModerator.getIdAlly() != 0) {
						line += "|" + onlineModerator.getAllyTag() + "|" + onlineModerator.getAllyName();
					}
					
					moderatorsLogin.add(line);
				}
			}
			
			int i = 0;
			for (String login : moderatorsLogin)
				content.append((i++ == 0 ? "" : ",") + login);
		} //else if(message.startsWith("/mj") || message.startsWith("/modo"))
		
		else
		/****************************************/
		/* Teste si le joueur est banni du chat */
		/****************************************/
		if (player.getBanChat() > Utilities.now()) {
			Date date = new Date(player.getBanChat() * 1000);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
			
			target = TARGET_SELF;
			type = TYPE_ERROR;
			content.append("Vous avez été banni du chat jusqu'au ");
			content.append(format.format(date));
			content.append(".");
		} else if (message.startsWith("/")) {
			// Vérifie que la commande est valide
			if (message.startsWith("/me ")) {
				// Message d'action (/me)
				content.append(message);
				target = TARGET_CHANNEL;
				type = TYPE_DEFAULT;
			} else if (message.startsWith("/trophee") || message.startsWith("/trophée") ||
					message.startsWith("/t")) {
				Player commandTarget = null;
				String login = "";
				boolean valid = true;
				
				// Voir les trophés d'un joueur
				if (message.contains(" ")) {
					String[] split = message.split(" ", 2);
					
					if (split.length < 2) {
						// Syntaxe invalide
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Syntaxe : /t joueur");
						valid = false;
					} else {
						// Teste si le pseudo est entre quotes
						login = split[1];
						if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
							login = message.split("\"", 3)[1];
						
						commandTarget = DataAccess.getPlayerByLogin(login);
					}
				} else {
					commandTarget = player;
					login = player.getLogin();
				}
				
				// Vérifie que le joueur existe
				if (valid && commandTarget == null) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Joueur inconnu : " + login + ".");
				} else {
					content.append("Trophées acquis par ");
					content.append(login);
					content.append(" : ");
					
					List<Achievement> achievements = commandTarget.getAchievements();
					String[] colors = {"#ffc000", "#ff9100", "#ff5e00", "#ff2f00", "#ff0000"};
					boolean firstLevel = true;
					
					synchronized (achievements) {
						for (int i = 5; i >= 1; i--) {
							StringBuffer buffer = new StringBuffer();
							boolean first = true;
							
							for (Achievement achievement : achievements) {
								int level = achievement.getLevel();
								
								if (level == i) {
									if (first)
										first = false;
									else
										buffer.append(", ");
									
									buffer.append(Messages.getString("achievement" + achievement.getType()));
								}
							}
							
							if (buffer.length() > 0) {
								if (firstLevel)
									firstLevel = false;
								else
									content.append(" ; ");
								
								content.append("<span style=\"color: ");
								content.append(colors[i - 1]);
								content.append("\">");
								content.append(Messages.getString("achievement.level" + i));
								content.append(" ");
								content.append(buffer);
								content.append("</span>");
							}
						}
					}
					
					target = TARGET_SELF;
					type = TYPE_INFO;
				}
			}  else if (message.startsWith("/public") || message.startsWith("/p")) {
				// Rejoindre un canal public
				String value = message.substring(message.indexOf(" ") + 1);
				int index;
				
				try {
					index = Integer.parseInt(value);
				} catch (Exception e) {
					index = -1;
				}
				
				if (index < 1 || index > 999) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /public numéro");
				} else {
					String channelName = ChatManager.PUBLIC_CHANNEL_PREFIX + index;
					
					if (!ChatManager.getInstance().isExistingChannel(channelName)) {
						// Création du canal
						if (player.hasRight(Player.PREMIUM)) {
							target = TARGET_SELF;
							type = TYPE_INFO;
							content.append("Vous avez rejoint le canal public " + index + ".");
							
							updateChannels = true;
							
							Set<String> channels = new HashSet<String>(
								ChatManager.getInstance(
									).getPlayerChannels(player.getId()));
							
							for (String c : channels)
								if (c.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX))
									ChatManager.getInstance().leaveChannel(c, player.getId());
							
							ChatManager.getInstance().joinChannel(channelName, player);
						} else {
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Compte premium requis pour pouvoir créer un canal.");
						}
					} else {
						if (ChatManager.getInstance().getChannelPlayers(channelName).contains(player.getId())) {
							// Déjà dans le canal
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Vous vous trouvez déjà dans le canal public " + index + ".");
						} else {
							// Rejoint le canal
							target = TARGET_SELF;
							type = TYPE_INFO;
							content.append("Vous avez rejoint le canal public " + index + ".");
							
							Set<String> channels = new HashSet<String>(
								ChatManager.getInstance(
									).getPlayerChannels(player.getId()));
							
							updateChannels = true;
							
							for (String c : channels)
								if (c.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX))
									ChatManager.getInstance().leaveChannel(c, player.getId());
							
							ChatManager.getInstance().joinChannel(channelName, player);
						}
					}
				}
			} else if (message.startsWith("/join ") || message.startsWith("/rejoindre ") ||
					message.startsWith("/j ")) {
				// Rejoindre un canal perso
				String value = message.substring(message.indexOf(" ") + 1);
				
				if (value.length() == 0) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /rejoindre nom canal");
				} else {
					NameStringFormat format = new NameStringFormat();
					String channelName = (String) format.format(value);
					
					if (channelName == null || channelName.length() > 20) {
						// Nom invalide
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Le nom du canal doit comporter au " +
							"maximum 20 des caractères alphanumériques, " +
							"espaces ou apostrophes.");
					} else if (Badwords.containsBadwords(channelName)) {
						// Nom censuré
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Le nom du canal n'est pas autorisé.");
					} else {
						if (!ChatManager.getInstance().isExistingChannel(channelName)) {
							// Création du canal
							if (player.hasRight(Player.PREMIUM)) {
								target = TARGET_SELF;
								type = TYPE_INFO;
								content.append("Vous avez rejoint le canal " + channelName + ".");
								
								updateChannels = true;
								
								Set<String> channels = new HashSet<String>(
									ChatManager.getInstance(
										).getPlayerChannels(player.getId()));
								
								for (String c : channels) {
									if (!c.startsWith(ChatManager.ALLY_CHANNEL_PREFIX) &&
											!c.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX) &&
											!c.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX) &&
											!c.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX))
									{
										ChatManager.getInstance().leaveChannel(
												c, player.getId());
									}
								}
								
								ChatManager.getInstance().joinChannel(channelName, player);
							} else {
								target = TARGET_SELF;
								type = TYPE_ERROR;
								content.append("Compte premium requis pour pouvoir créer un canal.");
							}
						} else {
							if (ChatManager.getInstance().getChannelPlayers(channelName).contains(player.getId())) {
								// Déjà dans le canal
								target = TARGET_SELF;
								type = TYPE_ERROR;
								content.append("Vous vous trouvez déjà dans le canal " + channelName + ".");
							} else {
								// Rejoint le canal
								target = TARGET_SELF;
								type = TYPE_INFO;
								content.append("Vous avez rejoint le canal " + channelName + ".");
								
								Set<String> channels = new HashSet<String>(
									ChatManager.getInstance(
										).getPlayerChannels(player.getId()));
								
								updateChannels = true;
								
								for (String c : channels) {
									if (!c.startsWith(ChatManager.ALLY_CHANNEL_PREFIX) &&
											!c.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX) &&
											!c.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX) &&
											!c.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX))
									{
										ChatManager.getInstance().leaveChannel(
												c, player.getId());
									}
								}
								
								ChatManager.getInstance().joinChannel(channelName, player);
							}
						}
					}
				}
			} else if (message.startsWith("/embassy ") || message.startsWith("/ambassade ") ||
					message.startsWith("/a ")) {
				// Rejoindre une ambassade
				String embassy = message.substring(message.indexOf(" ") + 1);
				
				if (embassy.length() == 0) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /ambassade alliance ou /ambassade tag");
				} else {
					Ally ally = DataAccess.getAllyByName(embassy);
					if (ally == null)
						ally = DataAccess.getAllyByTag(embassy);
					
					if (ally == null) {
						// Alliance inconnue
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Alliance inconnue : " + embassy + ".");
					} else if (player.getIdAlly() != 0 &&
							player.getAlly().getTreatyWithAlly(ally).equals(Treaty.ENEMY)) {
						// Alliance ennemi
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous ne pouvez pas accèder à l'ambassade d'une alliance ennemie.");
					} else if (player.getIdAlly() != 0 && embassy.equals(player.getAllyName())) {
						// Alliance du joueur
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous ne pouvez rejoindre votre propre ambassade.");
					} else {
						// Rejoint l'ambassade
						target = TARGET_SELF;
						type = TYPE_INFO;
						content.append("Vous avez rejoint l'ambassade de " + ally.getName() + ".");
						
						/*Notifier l'arivée du joueur à l'ambassade pour ceux qui y sont*/
						StringBuffer embassyIn = new StringBuffer();
						embassyIn.append(player.getLogin()+ " vient de rejoindre le canal ambassade de " + ally.getName() + ".");
						updates.addAll(sendMessage(player, embassyIn, TYPE_INFO, TARGET_CHANNEL, ChatManager.EMBASSY_CHANNEL_PREFIX + ally.getName(),
								false, null));
						
						updateChannels = true;
						
						Set<String> channels = new HashSet<String>(
							ChatManager.getInstance().getPlayerChannels(player.getId()));
						String playerAllyName = player.getAllyName();
						
						for (String channelName : channels) {
							if (channelName.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX) &&
									!ChatManager.getInstance().getChannelName(channelName
										).substring(1).equals(playerAllyName)) {
								ChatManager.getInstance().leaveChannel(channelName, player.getId());
								
								/*Notifier le départ du joueur à l'ambassade pour ceux qui y sont*/
								StringBuffer embassyOut = new StringBuffer();
								embassyOut.append(player.getLogin()+ " vient de quitter le canal embassade de " + ally.getName() + ".");
								updates.addAll(sendMessage(player, embassyOut, TYPE_INFO, TARGET_CHANNEL, ChatManager.EMBASSY_CHANNEL_PREFIX + ally.getName(),
										false, null));
								
								break;
							}
						}
						
						ChatManager.getInstance().joinChannel(
							ChatManager.EMBASSY_CHANNEL_PREFIX + ally.getName(), player);
					}
				}
			} else if (message.startsWith("/eject ") || message.startsWith("/éjecter ")) {
				// Ejecter joueur (/eject)
				if (channel.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX)) {
					if (player.getIdAlly() != 0 && ChatManager.getInstance(
							).getChannelName(Utilities.formatString(channel)).substring(1).equals(player.getAllyName())) {
						String login = message.substring(message.indexOf(" ") + 1);
						login = login.trim();
						
						Player kickedPlayer = DataAccess.getPlayerByLogin(login);
						
						if (kickedPlayer == null) {
							// Joueur inconnu
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Joueur inconnu : " + login + ".");
						} else if (kickedPlayer.getLogin().equals(player.getLogin())) {
							// Soi-même
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Vous ne pouvez pas vous éjecter vous-même !");
						} else if (kickedPlayer.getIdAlly() == player.getIdAlly()) {
							// Membre de l'alliance
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Vous ne pouvez pas éjecter un membre de votre alliance !");
						} else if (!ChatManager.getInstance().isInChannel(
								channel, kickedPlayer.getId())) {
							// Joueur pas dans le canal
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append(login);
							content.append(" n'est pas présent sur le canal.");
						} else {
							// Joueur éjecté du canal
							target = TARGET_CHANNEL;
							type = TYPE_MODERATION;
							
							ChatManager.getInstance().leaveChannel(channel, kickedPlayer.getId());
							UpdateTools.queueChatChannelsUpdate(kickedPlayer.getId());
							
							content.append(kickedPlayer.getLogin());
							content.append(" a été éjecté de l'ambassade de " +
									player.getAllyName() + " par " +
									player.getLogin() + ".");
							
							String kickedPlayerContent =
								"Vous avez été éjecté de l'ambassade de " +
								player.getAllyName() + " par " +
								player.getLogin() + ".";
							String kickedPlayerChannel =
								ChatManager.getInstance().getChannelName(
									ChatManager.getInstance().getPlayerChannels(
										kickedPlayer.getId()).iterator().next());
							
							JSONObject json = new JSONObject();
							json.put(ChatMessageData.FIELD_CONTENT,		kickedPlayerContent);
							json.put(ChatMessageData.FIELD_DATE,		Utilities.now());
							json.put(ChatMessageData.FIELD_TYPE,		TYPE_MODERATION);
							json.put(ChatMessageData.FIELD_CHANNEL,		kickedPlayerChannel);
							json.put(ChatMessageData.FIELD_AUTHOR,		kickedPlayer.getLogin());
							json.put(ChatMessageData.FIELD_RIGHTS,		kickedPlayer.hasRight(Player.MODERATOR) ? "moderator" : "player");
							json.put(ChatMessageData.FIELD_ALLY_TAG,	kickedPlayer.getAllyTag());
							json.put(ChatMessageData.FIELD_ALLY_NAME,	kickedPlayer.getAllyName());
							
							UpdateTools.queueChatUpdate(kickedPlayer.getId(), json.toString());
						}
					} else {
						// Autre ambassade
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous ne pouvez pas éjecter de joueur sur ce canal.");
					}
				} else if (!channel.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX) &&
						!channel.startsWith(ChatManager.ALLY_CHANNEL_PREFIX) &&
						!channel.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX)) {
					// Canal perso
					if (ChatManager.getInstance().getChannelOwner(channel) == player.getId()) {
						String login = message.substring(message.indexOf(" ") + 1);
						login = login.trim();
						
						Player kickedPlayer = DataAccess.getPlayerByLogin(login);
						
						if (kickedPlayer == null) {
							// Joueur inconnu
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Joueur inconnu : " + login + ".");
						} else if (kickedPlayer.getLogin().equals(player.getLogin())) {
							// Soi-même
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Vous ne pouvez pas vous éjecter vous-même !");
						} else if (!ChatManager.getInstance().isInChannel(
								channel, kickedPlayer.getId())) {
							// Joueur pas dans le canal
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append(login);
							content.append(" n'est pas présent sur le canal.");
						} else {
							// Joueur éjecté du canal
							target = TARGET_CHANNEL;
							type = TYPE_MODERATION;
							
							ChatManager.getInstance().leaveChannel(channel, kickedPlayer.getId());
							UpdateTools.queueChatChannelsUpdate(kickedPlayer.getId());
							
							content.append(kickedPlayer.getLogin());
							content.append(" a été éjecté du canal " +
									channel + " par " + player.getLogin() + ".");
							
							String kickedPlayerContent =
								"Vous avez été éjecté du canal " +
								channel + " par " + player.getLogin() + ".";
							String kickedPlayerChannel =
								ChatManager.getInstance().getChannelName(
									ChatManager.getInstance().getPlayerChannels(
										kickedPlayer.getId()).iterator().next());
							
							JSONObject json = new JSONObject();
							json.put(ChatMessageData.FIELD_CONTENT,		kickedPlayerContent);
							json.put(ChatMessageData.FIELD_DATE,		Utilities.now());
							json.put(ChatMessageData.FIELD_TYPE,		TYPE_MODERATION);
							json.put(ChatMessageData.FIELD_CHANNEL,		kickedPlayerChannel);
							json.put(ChatMessageData.FIELD_AUTHOR,		kickedPlayer.getLogin());
							json.put(ChatMessageData.FIELD_RIGHTS,		kickedPlayer.hasRight(Player.MODERATOR) ? "moderator" : "player");
							json.put(ChatMessageData.FIELD_ALLY_TAG,	kickedPlayer.getAllyTag());
							json.put(ChatMessageData.FIELD_ALLY_NAME,	kickedPlayer.getAllyName());
							
							UpdateTools.queueChatUpdate(kickedPlayer.getId(), json.toString());
						}
					} else {
						// Pas le propriétaire
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Seul le créateur du canal peut éjecter des joueurs.");
					}
				} else {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Vous ne pouvez pas éjecter de joueur sur ce canal.");
				}
			} else if (message.equals("/quit") || message.equals("/quitter") ||
					message.equals("/q")) {
				// Quitter (/quit)
				if (channel.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX)) {
					if (player.getIdAlly() != 0 && ChatManager.getInstance(
							).getChannelName(Utilities.formatString(channel)).substring(1).equals(player.getAllyName())) {
						// Ambassade du joueur
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous ne pouvez pas quitter votre ambassade.");
					} else {
						// Quitte l'ambassade
						String channelName = ChatManager.getInstance(
							).getChannelName(Utilities.formatString(channel)).substring(1);
						ChatManager.getInstance().leaveChannel(channel, player.getId());
						
						/*Notifier le départ du joueur à l'ambassade pour ceux qui y sont*/
						String allyName = channel.substring(1);
						StringBuffer embassyOut = new StringBuffer();
						embassyOut.append(player.getLogin()+ " vient de quitter le canal embassade de " + allyName + ".");
						updates.addAll(sendMessage(player, embassyOut, TYPE_INFO, TARGET_CHANNEL, channel,
								false, null));
						
						updateChannels = true;
						
						target = TARGET_SELF;
						type = TYPE_INFO;
						content.append("Vous avez quitté l'ambassade de " + channelName + ".");
					}
				} else if (!channel.startsWith(ChatManager.ALLY_CHANNEL_PREFIX) &&
						!channel.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX)) {
					// Canal custom
					if (ChatManager.getInstance().isExistingChannel(channel) &&
							ChatManager.getInstance().getChannelPlayers(channel).contains(player.getId())) {
						// Quitte le canal
						String channelName = ChatManager.getInstance(
							).getChannelName(Utilities.formatString(channel));
						ChatManager.getInstance().leaveChannel(channel, player.getId());
						
						updateChannels = true;
						
						target = TARGET_SELF;
						type = TYPE_INFO;
						content.append("Vous avez quitté le canal " + channelName + ".");
					} else {
						// Pas présent dans le canal
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous ne pouvez pas quitter ce canal.");
					}
				} else {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Vous ne pouvez pas quitter ce canal.");
				}
			} else if (message.equals("/who") || message.equals("/qui")) {
				// Liste des joueurs connectés (/who)
				target = TARGET_SELF;
				type = TYPE_WHO;
				
				Set<Integer> players =
					ChatManager.getInstance().getChannelPlayers(channel);
				List<String> playersLogin = new ArrayList<String>(players.size());
				
				synchronized (players) {
					for (Integer idPlayer : players) {
						Player onlinePlayer = DataAccess.getPlayerById(idPlayer);
						
						Contact playersContact = DataAccess.getContactByContact(player.getId(), onlinePlayer.getId());
						
						String line = "";
						if(playersContact != null) /* && playersContact.getType().equals(Contact.TYPE_FRIEND))*/ {
							line += "*";
						}
						
						line += onlinePlayer.getLogin();
						
						if(onlinePlayer.getIdAlly() != 0) {
							line += "|" + onlinePlayer.getAllyTag() + "|" + onlinePlayer.getAllyName();
						}
						
						playersLogin.add(line);
					}
				}
				
				Collections.sort(playersLogin, String.CASE_INSENSITIVE_ORDER);
				
				int i = 0;
				for (String login : playersLogin)
					content.append((i++ == 0 ? "" : ",") + login);
			} 
			
			

			
			else if (message.startsWith("/w ") ||
					message.startsWith("/whisper ") ||
					message.startsWith("/m ") ||
					message.startsWith("/murmurer ")) {
				// Message privé (/whisper)
				String[] split = message.split(" ", 3);
				
				if (split.length < 3) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /m joueur message, ou /m \"joueur\" message");
				} else {
					// Teste si le pseudo est entre quotes
					if (split[1].startsWith("\"") && (split[1] + split[2]).lastIndexOf("\"") != 0)
						split = message.split("\"", 3);
					
					whisperTarget = DataAccess.getPlayerByLogin(split[1]);
					
					// Vérifie que le joueur existe, est connecté au chat, et
					// n'a pas été banni du chat
					if (whisperTarget == null) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Joueur inconnu : " + split[1] + ". " +
								"Placez le nom du joueur entre \"...\" si " +
								"son nom comporte des espaces.");
					} else if (!ConnectionManager.getInstance().isConnected(
							whisperTarget.getId())) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append(split[1] + " n'est pas connecté sur le chat.");
					} else if (whisperTarget.getBanChat() > Utilities.now()) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append(split[1] + " a été banni du chat.");
					} else if (whisperTarget.isIgnoring(player.getId())) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append(split[1] + " ne souhaite pas recevoir de messages de votre part.");
					} else {
						target = TARGET_WHISPER;
						type = TYPE_WHISPER_SENT;
						content.append(split[2].trim());
					}
				}
			}		
			else if (message.equals("/ally") || message.equals("/alliance")) {
				// Liste des membres de l'alliance du joueur connectés (/ally)
				target = TARGET_SELF;
				type = TYPE_ALLY_MEMBERS;
				
				Ally ally = player.getAlly();
				
				if (ally == null) {
					type = TYPE_ERROR;
					content.append("Vous n'avez pas d'alliance !");
				} else {
					List<Player> members = ally.getMembers();
					List<String> playersLogin = new ArrayList<String>(members.size());
					
					synchronized (members) {
						for (Player member : members) {
							if (ConnectionManager.getInstance(
									).isConnected(member.getId()))
								playersLogin.add(member.getLogin());
						}
					}
					
					Collections.sort(playersLogin, String.CASE_INSENSITIVE_ORDER);
					
					int i = 0;
					for (String login : playersLogin)
						content.append((i++ == 0 ? "" : ",") + login);
				}
			} else if (message.equals("/help") || message.equals("/aide")) {
				// Aide (/help)
				target = TARGET_SELF;
				type = TYPE_INFO;
				content.append("Liste des commandes :<br/>" +
						"/aide /murmurer (/m) /me /alliance /qui /enligne (/?) /heure /ping /joué /ambassade (/a) /public (/p) /rejoindre (/j) /quitter (/q) /éjecter /trophées (/t) /roll /addf /remf /mj /modo /showinfo /clear");
			}  
			/*****************************************************************/
			/** Kick d'un joueur **/
			/*****************************************************************/
			else if (message.startsWith("/kick")){
				if (!player.hasRight(Player.MODERATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux modérateurs");
				} else {
					String[] parts = message.split(" ");
					if(parts.length != 2){
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("/kick nomDuJoueur");
					} else {
						String login = parts[1];
						if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
							login = message.split("\"", 3)[1];
						
						Player kickedTarget = DataAccess.getPlayerByLogin(login);
											
						if (kickedTarget == null) {
							// Vérifie que le joueur existe
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Joueur inconnu : " + login + ".");
						} else {
							ConnectionManager cm = ConnectionManager.getInstance();
							if(!cm.isConnected(kickedTarget.getId())){
								target = TARGET_SELF;
								type = TYPE_ERROR;
								content.append("Kick : " + login + " est déconnecté.");
							} else if(kickedTarget.hasRight(Player.MODERATOR) && 
									!player.hasRight(Player.ADMINISTRATOR)	) {
								target = TARGET_SELF;
								type = TYPE_ERROR;
								content.append("haha, " + login + " est plus gradé que " +
										"toi, tu peux pas le kicker...");
							} else {								
								target = TARGET_CHANNEL;
								type = TYPE_INFO;
								content.append(login + " à été kické du jeu par " + player.getLogin());
								
								cm.disconnect(kickedTarget);
							}
						}//if (kickedTarget == null) {
					}//if(parts.length != 2){
				}//if (!player.hasRight(Player.MODERATOR)) {
			}
			
			else if (message.startsWith("/showban")) {
				// Affiche si le joueur est banni et la raison
				
				target = TARGET_SELF;
				type = TYPE_INFO;
				
				if (!player.hasRight(Player.MODERATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux modérateurs");
				} 
				else
				{
					String[] split=message.split(" ");
					if(split.length<2)
					{
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("/showban player");
					}
					else
					{
					String login = split[1];
					if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
						login = message.split("\"", 3)[1];
					
					Player bannedTarget = DataAccess.getPlayerByLogin(login);
					
					
					if (bannedTarget == null) {
						// Vérifie que le joueur existe
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Joueur inconnu : " + login + ".");
					}
					else
					{
						if(bannedTarget.getBanGame()>Utilities.now())
						{
							target = TARGET_SELF;
							type = TYPE_INFO;
							Date date = new Date(bannedTarget.getBanGame() * 1000);
							SimpleDateFormat format =
								new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
							content.append(login+" a été banni du jeu jusqu'au");
							content.append(format.format(date));
							Ban lastBan= DataAccess.getLastBanGameByPlayerId(bannedTarget.getId());
							if(lastBan.getBanReason().length()>1){
								content.append(" pour \"");
								content.append(lastBan.getBanReason());
								content.append("\"");
							}
							content.append(".");
						}
						else if(bannedTarget.getBanChat()>Utilities.now())
						{
							target = TARGET_SELF;
							type = TYPE_INFO;
							Date date = new Date(bannedTarget.getBanChat() * 1000);
							SimpleDateFormat format =
								new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
							content.append(login+" a été banni du chat jusqu'au");
							content.append(format.format(date));
							Ban lastBan= DataAccess.getLastBanChatByPlayerId(bannedTarget.getId());
							if(lastBan.getBanReason().length()>1){
								content.append(" pour \"");
								content.append(lastBan.getBanReason());
								content.append("\"");
							}
							content.append(".");
						}
						else
						{
							target = TARGET_SELF;
							type = TYPE_INFO;
							content.append(login+" n'est pas banni.");
						}
					}
				}
				}
				
			} 
			else if (message.startsWith("/archiveban")) {
				// Affiche tout les bans qu'a reçu le joueur
				
				target = TARGET_SELF;
				type = TYPE_INFO;
				
				if (!player.hasRight(Player.MODERATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux modérateurs");
				} 
				else
				{
					String[] split=message.split(" ");
					if(split.length<2)
					{
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Utilisation : /archiveban player");
					}
					else
					{
						String login = split[1];
						if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
							login = message.split("\"", 3)[1];
						
						Player bannedTarget = DataAccess.getPlayerByLogin(login);
						
						if (bannedTarget == null) {
							// Vérifie que le joueur existe
							target = TARGET_SELF;
							type = TYPE_ERROR;
							content.append("Joueur inconnu : " + login + ".");
						}
						else
						{
							List<Ban> lastBan= DataAccess.getBanByPlayerId(bannedTarget.getId());
							String typeBan;
							if(lastBan.size()<1){
								target = TARGET_SELF;
								type = TYPE_ERROR;
								content.append("Ce joueur n'a jamais été banni.");
							}
							else
							{
								for (Ban ban : lastBan)
								{
									if(ban.getBanType()==Ban.CHAT)
										typeBan="<span style=\"color:green\">chat</span>";
									else
										typeBan="<span style=\"color:red\">jeu</span>";
									
									Date date = new Date(ban.getDate() * 1000);
									SimpleDateFormat format =
										new SimpleDateFormat("dd/MM/yy");
									
									content.append(login+" a été banni du " +typeBan+ " le ");
									content.append(format.format(date));
									content.append(" pour \"");
									content.append(ban.getBanReason());
									content.append("\"<br/>");
								}
							}
						}
					}
				}
				
			} 
			
			
			else if (message.startsWith("/gameban") || message.startsWith("/bangame")) {
				// Banissement d'un joueur (/ban)
				
				String reason="";
				String[] split=message.split(" ");
				String bannedP="";
				if(message.length()<9)
				{
					
				}
				else if(message.charAt(9)=='"')
				{
					int lastDquote=message.indexOf("\" ",10);
					bannedP=message.substring(10,lastDquote);
					split = message.substring(lastDquote+2).split(" ");
					if(split.length>1){
					String message2 = message.substring(lastDquote+2);
					reason=message2.substring(split[0].length()+1);
					}
				}
				else
				{
					split = message.split(" ");
					bannedP=split[1];
					if(split.length>3){
					reason=message.substring((split[0]+split[1]+split[2]).length()+3);
					}
				}
				
				if (!player.hasRight(Player.ADMINISTRATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux admins");
				} else if (message.length() < 9) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /gameban joueur durée(m|h|d) [Raison] ; avec " +
							"m = minutes, h = heures, d = jours. Exemple : " +
							"/ban dark 12h banni le joueur dark du jeu pour 12h.");
				} else {

					
					Player bannedTarget = DataAccess.getPlayerByLogin(bannedP);
					String lastChar = "";
					int length = -1;
					
					
					try {
						if(message.charAt(9)!='"'){
							length = Integer.parseInt(split[2].substring(0, split[2].length() - 1));
							lastChar = String.valueOf(split[2].charAt(split[2].length() - 1));
						}
						
						else{
							length = Integer.parseInt(split[0].substring(0, split[0].length() - 1));
							lastChar = String.valueOf(split[0].charAt(split[0].length() - 1));
						}
							
					} catch (Exception e) {
						// Syntaxe invalide
					}
					
					if ((!lastChar.equalsIgnoreCase("m") &&
							!lastChar.equalsIgnoreCase("h") &&
							!lastChar.equalsIgnoreCase("d")) || length < 0) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Syntaxe : /gameban joueur durée(m|h|d) [Raison] ; avec " +
								"m = minutes, h = heures, d = jours. Exemple : " +
								"/ban dark 12h banni le joueur dark du jeu pour 12h.");
					} else if (bannedTarget == null) {
						// Vérifie que le joueur existe
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Joueur inconnu : " + bannedP + ".");
					} else if (bannedTarget.hasRight(Player.ADMINISTRATOR) ||
							bannedTarget.hasRight(Player.SUPER_ADMINISTRATOR)) {
						// Vérifie que le joueur n'est pas un modérateur / administrateur
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous n'êtes pas autorisé à effectuer " +
							"cette opération sur un admin.");
					} else {
						if (lastChar.equalsIgnoreCase("m"))
							length *= 60;
						else if (lastChar.equalsIgnoreCase("h"))
							length *= 60 * 60;
						else if (lastChar.equalsIgnoreCase("d"))
							length *= 60 * 60 * 24;
						
						// Bannissement du joueur
						synchronized (bannedTarget.getLock()) {
							bannedTarget = DataAccess.getEditable(bannedTarget);
							bannedTarget.setBanGame(Utilities.now() + length);
							//bannedTarget.setBanGameReason(reason);

							DataAccess.save(bannedTarget);
						}
						Ban ban = new Ban(bannedTarget.getId(),reason,1);
						DataAccess.save(ban);
						
						target = TARGET_CHANNEL;
						type = TYPE_MODERATION;
						
						Date date = new Date(bannedTarget.getBanGame() * 1000);
						SimpleDateFormat format =
							new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
						content.append(bannedTarget.getLogin());
						content.append(" a été banni ");
						content.append("du jeu par ");
						content.append(player.getLogin());
						content.append(" jusqu'au ");
						content.append(format.format(date));
						if(reason.length()>1){
							content.append(" pour \"");
							content.append(reason);
							content.append("\"");
						}
						content.append(".");
						
						/*Session sessionTarget=bannedTarget.setLastConnection(lastConnection);
						sessionTarget.destroy();*/
						ConnectionManager.getInstance().disconnect(bannedTarget);
						bannedTarget.setSecurityKey(null);
						bannedTarget.setContinuation(null);
					}
				}
				
			}
			
			else if (message.startsWith("/ban")) { 
				// Banissement d'un joueur (/ban)
				
				String reason=""; //raison du ban
				String[] split=message.split(" "); // message splitté
				String bannedP=""; // Nom du joueur à bannir
				if(message.length()<5) // Si le message est "/ban" on ne fait rien, on affichera l'aide.
				{
					
				}
				else if(message.charAt(5)=='"') // Si le pseudo est entre double quote
				{
					int lastDquote=message.indexOf("\" ",6); // on récupère l'index de la seconde double quote
					bannedP=message.substring(6,lastDquote); 
					split = message.substring(lastDquote+2).split(" "); // On recupère la suite de la commande
					if(split.length>1){
					String message2 = message.substring(lastDquote+2);
					reason=message2.substring(split[0].length()+1);
					}
				}
				else
				{
					split = message.split(" ");
					bannedP=split[1];
					if(split.length>3){
					reason=message.substring((split[0]+split[1]+split[2]).length()+3);
					}
				}
				
				if (!player.hasRight(Player.MODERATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux modérateurs.");
				} else if (message.length() < 5) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /ban joueur durée(m|h|d) [Raison] ; avec " +
							"m = minutes, h = heures, d = jours. Exemple : " +
							"/ban dark 12h banni le joueur dark du chat pour 12h.");
				} else {

					
					Player bannedTarget = DataAccess.getPlayerByLogin(bannedP);
					String lastChar = "";
					int length = -1;

					
					try {
						if(message.charAt(5)!='"'){
							length = Integer.parseInt(split[2].substring(0, split[2].length() - 1));
							lastChar = String.valueOf(split[2].charAt(split[2].length() - 1));
						}
						
						else{
							length = Integer.parseInt(split[0].substring(0, split[0].length() - 1));
							lastChar = String.valueOf(split[0].charAt(split[0].length() - 1));
						}
							
					} catch (Exception e) {
						// Syntaxe invalide
					}
					
					if ((!lastChar.equalsIgnoreCase("m") &&
							!lastChar.equalsIgnoreCase("h") &&
							!lastChar.equalsIgnoreCase("d")) || length < 0) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Syntaxe : /ban joueur durée(m|h|d) [Raison] ; avec " +
								"m = minutes, h = heures, d = jours. Exemple : " +
								"/ban dark 12h banni le joueur dark du chat pour 12h.");
					} else if (bannedTarget == null) {
						// Vérifie que le joueur existe
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Joueur inconnu : " + bannedP + ".");
					} else if (bannedTarget.hasRight(Player.MODERATOR) ||
							bannedTarget.hasRight(Player.ADMINISTRATOR) ||
							bannedTarget.hasRight(Player.SUPER_ADMINISTRATOR)) {
						// Vérifie que le joueur n'est pas un modérateur / administrateur
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous n'êtes pas autorisé à effectuer " +
							"cette opération sur un modérateur.");
					} else {
						if (lastChar.equalsIgnoreCase("m"))
							length *= 60;
						else if (lastChar.equalsIgnoreCase("h"))
							length *= 60 * 60;
						else if (lastChar.equalsIgnoreCase("d"))
							length *= 60 * 60 * 24;
						
						// Bannissement du joueur
						synchronized (bannedTarget.getLock()) {
							bannedTarget = DataAccess.getEditable(bannedTarget);
							bannedTarget.setBanChat(Utilities.now() + length);
							//bannedTarget.setBanChatReason(reason);
							DataAccess.save(bannedTarget);
						}
						
						Ban ban = new Ban(bannedTarget.getId(),reason,0);
						DataAccess.save(ban);
						
						target = TARGET_CHANNEL;
						type = TYPE_MODERATION;
						
						Date date = new Date(bannedTarget.getBanChat() * 1000);
						SimpleDateFormat format =
							new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
						content.append(bannedTarget.getLogin());
						content.append(" a été banni ");
						content.append("du chat par ");
						content.append(player.getLogin());
						content.append(" jusqu'au ");
						content.append(format.format(date));
						if(reason.length()>1){
							content.append(" pour \"");
							content.append(reason);
							content.append("\"");
						}
						content.append(".");
					}
				}
				
			} 
			
			else if (message.startsWith("/unbangame") || message.startsWith("/gameunban")) {
				// Lever le banissement d'un joueur du jeu)
				String[] split = message.split(" ", 2);
				
				if (!player.hasRight(Player.SUPER_ADMINISTRATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux admins.");
				} else if (split.length < 2) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /unbangame joueur");
				} else {
					// Teste si le pseudo est entre quotes
					String login = split[1];
					if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
						login = message.split("\"", 3)[1];
					
					Player unbannedTarget = DataAccess.getPlayerByLogin(login);
					
					// Vérifie que le joueur existe et n'est pas un administrateur
					if (unbannedTarget == null) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Joueur inconnu : " + login + ".");
					} else if (unbannedTarget.hasRight(Player.ADMINISTRATOR) ||
							unbannedTarget.hasRight(Player.SUPER_ADMINISTRATOR)) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous n'êtes pas autorisé à effectuer " +
								"cette opération sur un administrateur.");
					} 
					else if(unbannedTarget.getBanGame()<Utilities.now())
					{
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Ce joueur n'est pas banni");
					}
					else {
						// Lève le banissement sur le joueur
						synchronized (unbannedTarget.getLock()) {
							unbannedTarget = DataAccess.getEditable(unbannedTarget);
							unbannedTarget.setBanGame(0);
							DataAccess.save(unbannedTarget);
						}
						
						target = TARGET_CHANNEL;
						type = TYPE_MODERATION;
						
						content.append(player.getLogin());
						content.append(" a levé le bannissement sur ");
						content.append(unbannedTarget.getLogin());
						content.append(".");
					}
				}
			}
			
			else if (message.startsWith("/unban")) {
				// Lever le banissement d'un joueur (/unban)
				String[] split = message.split(" ", 2);
				
				if (!player.hasRight(Player.MODERATOR)) {
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Commande réservée aux modérateurs.");
				} else if (split.length < 2) {
					// Syntaxe invalide
					target = TARGET_SELF;
					type = TYPE_ERROR;
					content.append("Syntaxe : /unban joueur");
				} else {
					// Teste si le pseudo est entre quotes
					String login = split[1];
					if (login.startsWith("\"") && login.lastIndexOf("\"") != 0)
						login = message.split("\"", 3)[1];
					
					Player unbannedTarget = DataAccess.getPlayerByLogin(login);
					
					// Vérifie que le joueur existe et n'est pas un modérateur / administrateur
					if (unbannedTarget == null) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Joueur inconnu : " + login + ".");
					} else if (unbannedTarget.hasRight(Player.MODERATOR) ||
							unbannedTarget.hasRight(Player.ADMINISTRATOR) ||
							unbannedTarget.hasRight(Player.SUPER_ADMINISTRATOR)) {
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Vous n'êtes pas autorisé à effectuer " +
								"cette opération sur un modérateur.");
					} 
					else if(unbannedTarget.getBanChat()<Utilities.now())
					{
						target = TARGET_SELF;
						type = TYPE_ERROR;
						content.append("Ce joueur n'est pas banni");
					}
					else {
						// Lève le banissement sur le joueur
						synchronized (unbannedTarget.getLock()) {
							unbannedTarget = DataAccess.getEditable(unbannedTarget);
							unbannedTarget.setBanChat(0);
							DataAccess.save(unbannedTarget);
						}
						
						target = TARGET_CHANNEL;
						type = TYPE_MODERATION;
						
						content.append(player.getLogin());
						content.append(" a levé le bannissement sur ");
						content.append(unbannedTarget.getLogin());
						content.append(".");
					}
				}
			}
			//voir première partie dans le client (Chat.java)
			else if(message.startsWith("/showinfo")){
				target = TARGET_SELF;
				type = TYPE_ERROR;
				content.append("/showinfo joueur (sans guillemet)");
			}
			
			else {
				// Commande inconnue
				target = TARGET_SELF;
				type = TYPE_ERROR;
				content.append("Commande inconnue. " +
					"Tapez /help pour afficher la liste des commandes.");
			}
		} else {
			// Message normal
			target = TARGET_CHANNEL;
			content.append(message);
			type = TYPE_DEFAULT;
		}
		
		updates.addAll(sendMessage(player, content, type, target, channel,updateChannels, whisperTarget));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	private List<Update> sendMessage(Player player, StringBuffer content, String type, int target, String channel, boolean updateChannels, Player whisperTarget) throws JSONException {
		List<Update> updates = new ArrayList<Update>();
		
		// Construit le message
		//On détermine les droits du joueur
		String right = new String(player.hasRight(Player.ADMINISTRATOR|Player.SUPER_ADMINISTRATOR)?
				"administrator" : (player.hasRight(Player.MODERATOR) ? "moderator" : "player"));
		JSONObject json = new JSONObject();

		json.put(ChatMessageData.FIELD_CONTENT,		content);
		json.put(ChatMessageData.FIELD_DATE,		Utilities.formatDate(Utilities.now()*1000, "HH:mm"));
		json.put(ChatMessageData.FIELD_TYPE,		type);
		json.put(ChatMessageData.FIELD_CHANNEL,		channel);
		json.put(ChatMessageData.FIELD_AUTHOR,		player.getLogin());
		json.put(ChatMessageData.FIELD_RIGHTS,		right);
		json.put(ChatMessageData.FIELD_ALLY_TAG,	player.getAllyTag());
		json.put(ChatMessageData.FIELD_ALLY_NAME,	player.getAllyName());
		json.put(ChatMessageData.FIELD_LEVEL,		player.getLevel());
		json.put(ChatMessageData.FIELD_RANK,		Ladder.getInstance().getPlayerRank(player.getId()));
		String jsonString = json.toString();
		
		if (updateChannels)
			updates.add(Update.getChatChannelsUpdate());
		
		switch (target) {
		case TARGET_CHANNEL:
			// Envoie le message aux joueurs connectés sur le canal
			Set<Integer> players =
				ChatManager.getInstance().getChannelPlayers(channel);
			
			synchronized (players) {
				for (int i = 0; i < 2; i++) {
					for (Integer idPlayer : players) {
						Player chattingPlayer = DataAccess.getPlayerById(idPlayer);
						
						if ((i == 0 && chattingPlayer.isSettingsCensorship()) ||
							(i == 1 && !chattingPlayer.isSettingsCensorship()))
							continue;
						
						if (!ChatManager.getInstance().isChannelEnabled(
								chattingPlayer, channel))
							continue;
						//ne pas afficher le message s'il est ignoré
						
						
						if (idPlayer == player.getId()) {
							updates.add(Update.getChatUpdate(jsonString));
						} else {
							
							Contact contact = DataAccess.getContactByContact(player.getId(), chattingPlayer.getId());
							
							if(contact != null && contact.equals(Contact.TYPE_IGNORE))
								continue;
								
							UpdateTools.queueChatUpdate(chattingPlayer.getId(), jsonString);
						}
					}
					
					if (i == 0) {
						json.put(ChatMessageData.FIELD_CONTENT, Badwords.parse(content.toString()));
						jsonString = json.toString();
					}
				}
			}
			break;
		case TARGET_SELF:
			// Message destiné à l'expéditeur
			updates.add(Update.getChatUpdate(jsonString));
			break;
		case TARGET_WHISPER:
			// Message privé - expéditeur
			if (player.isSettingsCensorship())
				json.put(ChatMessageData.FIELD_CONTENT,	Badwords.parse(content.toString()));
			json.put(ChatMessageData.FIELD_TYPE,		TYPE_WHISPER_SENT);
			json.put(ChatMessageData.FIELD_AUTHOR,		whisperTarget.getLogin());
			json.put(ChatMessageData.FIELD_ALLY_TAG,	whisperTarget.getAllyTag());
			json.put(ChatMessageData.FIELD_ALLY_NAME,	whisperTarget.getAllyName());
			updates.add(Update.getChatUpdate(json.toString()));
			
			// Message privé - destinataire
			if (whisperTarget.isSettingsCensorship())
				json.put(ChatMessageData.FIELD_CONTENT,	Badwords.parse(content.toString()));
			else
				json.put(ChatMessageData.FIELD_CONTENT,	content);
			json.put(ChatMessageData.FIELD_TYPE,		TYPE_WHISPER_RECEIVED);
			json.put(ChatMessageData.FIELD_AUTHOR,		player.getLogin());
			json.put(ChatMessageData.FIELD_ALLY_TAG,	player.getAllyTag());
			json.put(ChatMessageData.FIELD_ALLY_NAME,	player.getAllyName());
			if (whisperTarget.getId() == player.getId())
				updates.add(Update.getChatUpdate(json.toString()));
			else
				UpdateTools.queueChatUpdate(whisperTarget.getId(), json.toString());
			break;
		}
		
		return updates;
		
	}
}
