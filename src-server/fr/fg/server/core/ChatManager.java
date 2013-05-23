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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.util.Utilities;

public class ChatManager {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		PUBLIC_CHANNEL_PREFIX = "%",
		ALLY_CHANNEL_PREFIX = "@",
		EMBASSY_CHANNEL_PREFIX = "$",
		MODERATOR_CHANNEL_PREFIX = "&",
		
		MODERATOR_CHANNEL = "Modération";
	
	public final static int
		MODE_OPEN = 1,
		MODE_ALLIED = 2,
		MODE_CLOSED = 3;
	
	private final static int MAX_PLAYERS_PER_CHANNEL = 40;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static ChatManager instance = new ChatManager();
	
	private Map<String, String> channelNames;
	
	private Map<String, Set<Integer>> playersByChannel;
	
	private Map<Integer, Set<String>> channelsByPlayer;
	
	private Map<String, Integer> channelOwners;
	
	private Map<String, Integer> channelModes;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private ChatManager() {
		this.playersByChannel = Collections.synchronizedMap(
			new HashMap<String, Set<Integer>>());
		this.channelsByPlayer = Collections.synchronizedMap(
			new HashMap<Integer, Set<String>>());
		this.channelNames = Collections.synchronizedMap(
			new HashMap<String, String>());
		this.channelOwners = Collections.synchronizedMap(
			new HashMap<String, Integer>());
		this.channelModes = Collections.synchronizedMap(
			new HashMap<String, Integer>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isInChannel(String channel, int idPlayer) {
		String key = Utilities.formatString(channel);
		Set<Integer> channelPlayers = playersByChannel.get(key);
		
		if (channelPlayers == null)
			return false;
		
		return channelPlayers.contains(idPlayer);
	}
	
	public void joinDefaultChannels(Player player) {
		// Rejoint le canal public
		String publicChannel = getAvailablePublicChannel(player.getId());
		joinChannel(publicChannel, player);
		
		//Rejoint le canal modo/admin
		if(player.isModerator() || player.isAdministrator()) {
			joinChannel(MODERATOR_CHANNEL_PREFIX + MODERATOR_CHANNEL, player);
		}
		
		// Rejoint le canal alliance
		Ally ally = player.getAlly();
		if (ally != null) {
			joinChannel(ALLY_CHANNEL_PREFIX + ally.getName(), player);
			joinChannel(EMBASSY_CHANNEL_PREFIX + ally.getName(), player);
		}

	}
	
	public int getChannelOwner(String channel) {
		return channelOwners.get(Utilities.formatString(channel));
	}
	
	public int getChannelMode(String channel) {
		return channelModes.get(Utilities.formatString(channel));
	}
	
	public void joinChannel(String channel, Player player) {
		String key = Utilities.formatString(channel);
		Set<Integer> channelPlayers = playersByChannel.get(key);
		
		if (channelPlayers == null) {
			channelPlayers = Collections.synchronizedSet(
					new HashSet<Integer>());
			channelNames.put(key, channel);
			playersByChannel.put(key, channelPlayers);
			channelOwners.put(key, player.getId());
			channelModes.put(key, MODE_OPEN);
		}
		
		if (!channelPlayers.contains(player.getId())) {
			channelPlayers.add(player.getId());
			
			Set<String> playerChannels = getPlayerChannels(player.getId());
			playerChannels.add(Utilities.formatString(channel));
		}
	}
	
	public void joinAllyChannels(Player player) {
		if (player.getIdAlly() != 0) {
			String allyName = player.getAllyName();
			joinChannel(ChatManager.ALLY_CHANNEL_PREFIX + allyName, player);
			joinChannel(ChatManager.EMBASSY_CHANNEL_PREFIX + allyName, player);
		}
	}
	
	public void leaveAllyChannels(Player player) {
		if (player.getIdAlly() != 0) {
			String allyName = player.getAllyName();
			leaveChannel(ChatManager.ALLY_CHANNEL_PREFIX + allyName, player.getId());
			leaveChannel(ChatManager.EMBASSY_CHANNEL_PREFIX + allyName, player.getId());
		}
	}
	
	public void leaveChannel(String channel, int idPlayer) {
		String key = Utilities.formatString(channel);
		Set<Integer> channelPlayers = getChannelPlayers(channel);
		Set<String> playerChannels = getPlayerChannels(idPlayer);
		
		channelPlayers.remove(idPlayer);
		playerChannels.remove(key);
		
		if (channelPlayers.size() == 0) {
			playersByChannel.remove(key);
			channelNames.remove(key);
			channelOwners.remove(key);
		}
	}
	
	public boolean isExistingChannel(String channel) {
		return channelNames.get(Utilities.formatString(channel)) != null;
	}
	
	public void leaveAllChannels(int idPlayer) {
		Set<String> playerChannels = new HashSet<String>(getPlayerChannels(idPlayer));
		
		for (String channel : playerChannels)
			leaveChannel(channel, idPlayer);
	}
	
	public Set<Integer> getChannelPlayers(String channel) {
		String key = Utilities.formatString(channel);
		Set<Integer> channelPlayers = playersByChannel.get(key);
		
		if (channelPlayers == null)
			return new HashSet<Integer>();
		
		return channelPlayers;
	}
	
	public boolean isChannelEnabled(Player player, String channel) {
		boolean active = true;
		if (channel.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX))
			active = (player.getSettingsChat() & (1 << 0)) != 0;
		else if (channel.startsWith(ChatManager.ALLY_CHANNEL_PREFIX))
			active = (player.getSettingsChat() & (1 << 1)) != 0;
		else if (channel.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX))
			active = (player.getSettingsChat() & (1 << 2)) != 0;
		else if (channel.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX))
			active = (player.getSettingsChat() & (1 << 3)) != 0;
		return active;
	}
	
	public void setChannelEnabled(Player player, String channel, boolean enable) {
		synchronized (player.getLock()) {
			player = DataAccess.getEditable(player);
			if (enable) {
				if (channel.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() | (1 << 0));
				else if (channel.startsWith(ChatManager.ALLY_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() | (1 << 1));
				else if (channel.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() | (1 << 2));
				else if (channel.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() | (1 << 3));
			} else {
				if (channel.startsWith(ChatManager.PUBLIC_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() & 14);
				else if (channel.startsWith(ChatManager.ALLY_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() & 13);
				else if (channel.startsWith(ChatManager.EMBASSY_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() & 11);
				else if (channel.startsWith(ChatManager.MODERATOR_CHANNEL_PREFIX))
					player.setSettingsChat(player.getSettingsChat() & 7);
			}
			player.save();
		}
	}
	
	public Set<String> getPlayerChannels(int idPlayer) {
		Set<String> playerChannels = channelsByPlayer.get(idPlayer);
		
		if (playerChannels == null) {
			playerChannels = Collections.synchronizedSet(
					new HashSet<String>());
			this.channelsByPlayer.put(idPlayer, playerChannels);
		}
		
		return playerChannels;
	}
	
	public String getChannelName(String key) {
		return channelNames.get(key);
	}
	
	public String getAvailablePublicChannel(int idPlayer) {
		int index = 1;
		
		while (true) {
			Set<Integer> channel = getChannelPlayers(PUBLIC_CHANNEL_PREFIX + index);
			
			if (channel.size() >= MAX_PLAYERS_PER_CHANNEL)
				index++;
			else
				return PUBLIC_CHANNEL_PREFIX + index;
		}
	}
	
	public static ChatManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
