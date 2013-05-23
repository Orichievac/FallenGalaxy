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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.fg.client.data.OptionsData;
import fr.fg.client.data.StateData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.data.StarSystem;
import fr.fg.server.servlet.ServerController;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class InitializationTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getInitializationData(JSONStringer json,
			Player player) throws Exception {
		if (json == null)
			json = new JSONStringer();
		
		long shutdownDate = ServerController.getShutdownDate();
		
		json.key(StateData.FIELD_SECURITY_KEY).		value(player.getSecurityKey()).
			key(StateData.FIELD_PLAYER_ID).			value(player.getId()).
			key(StateData.FIELD_PLAYER_LOGIN).		value(player.getLogin()).
			key(StateData.FIELD_EKEY).				value(player.getEkey()).
			key(StateData.FIELD_PREMIUM).			value(player.hasRight(Player.PREMIUM)).
			key(StateData.FIELD_RIGHTS).			value(player.getRights()).
			key(StateData.FIELD_TIME_UNIT).			value(Config.getTimeUnit()).
			key(StateData.FIELD_TUTORIAL).			value(player.getTutorial()).
			key(StateData.FIELD_SERVER_SHUTDOWN).	value(shutdownDate == -1 ? -1 : Math.max(0, shutdownDate - Utilities.now()));
		
		// Cherche si le joueur a des messages non lus
		int unreadMessages = 0;
		List<Message> messages = player.getMessages();

		synchronized (messages) {
			for (Message message : messages) {
				if (!message.isOpened())
					unreadMessages++;
			}
		}
		
		json.key(StateData.FIELD_UNREAD_MESSAGES).	value(unreadMessages);
		
		// Joueurs en ligne
		int onlinePlayers = ConnectionManager.getInstance(
				).getConnectedPlayers().size();
		
		json.key(StateData.FIELD_ONLINE_PLAYERS).	value(onlinePlayers);
		
		// Dernière connexion
		if (player.getLastConnection() == 0) {
			json.key(StateData.FIELD_LAST_CONNECTION).	value("");
		} else {
			Date date = new Date(1000 * player.getLastConnection());
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy à HH:mm:ss");
			json.key(StateData.FIELD_LAST_CONNECTION).	value(format.format(date));
		}
		
		// Recherche le système mère du joueur
		StarSystem firstSystem = player.getFirstSystem();
		
		// Renvoie les informations sur le secteur dans lequel se
		// trouve le système
		int idArea = firstSystem.getIdArea();
		ConnectionManager.getInstance().joinArea(player, idArea);
		
		json.key(StateData.FIELD_AREA);
		AreaTools.getArea(json, DataAccess.getAreaById(idArea), player);
		
		// Informations pour centrer la vue sur le système
		json.key(StateData.FIELD_VIEW_X).				value(firstSystem.getX()).
			key(StateData.FIELD_VIEW_Y).				value(firstSystem.getY());
		
		// Expérience
		json.key(StateData.FIELD_XP).					value(player.getXp()).
			key(StateData.FIELD_COLONIZATION_POINTS).	value(player.getColonizationPoints());
		
		// Recherches du joueur
		player.updateResearch();
		
		json.key(StateData.FIELD_RESEARCH);
		ResearchTools.getResearchData(json, player);
		
		// Empire - met à jour les systèmes du joueur
		List<StarSystem> systems = new ArrayList<StarSystem>(player.getSystems());
		
		for (StarSystem system : systems)
			StarSystem.updateSystem(system);
		
		json.key(StateData.FIELD_PLAYER_SYSTEMS);
		SystemTools.getPlayerSystems(json, player);
		
		json.key(StateData.FIELD_PLAYER_FLEETS);
		FleetTools.getPlayerFleets(json, player);
		
		json.key(StateData.FIELD_PLAYER_GENERATORS);
		StructureTools.getPlayerGenerators(json, player);
		
		// Missions
		json.key(StateData.FIELD_CONTRACT_STATES);
		ContractTools.getContractsState(json, player);
		json.key(StateData.FIELD_PLAYER_CONTRACTS);
		ContractTools.getPlayerContracts(json, player);
		
		// Options
		json.key(StateData.FIELD_OPTIONS).
			object().
			key(OptionsData.FIELD_GRID).				value(player.isSettingsGridVisible()).
			key(OptionsData.FIELD_BRIGHTNESS).			value(player.getSettingsBrightness()).
			key(OptionsData.FIELD_FLEETS_SKIN).			value(player.getSettingsFleetSkin()).
			key(OptionsData.FIELD_THEME).				value(player.getSettingsTheme()).
			key(OptionsData.FIELD_CENSORSHIP).			value(player.isSettingsCensorship()).
			key(OptionsData.FIELD_GENERAL_VOLUME).		value(player.getSettingsGeneralVolume()).
			key(OptionsData.FIELD_SOUND_VOLUME).		value(player.getSettingsSoundVolume()).
			key(OptionsData.FIELD_MUSIC_VOLUME).		value(player.getSettingsMusicVolume()).
			key(OptionsData.FIELD_GRAPHICS_QUALITY).	value(player.getSettingsGraphics()).
			key(OptionsData.FIELD_CONNECTION_OPTIMIZED).value(player.isSettingsOptimizeConnection()).
			key(OptionsData.FIELD_FLEET_PREFIX).		value(player.getSettingsFleetNamePrefix()).
			key(OptionsData.FIELD_FLEET_SUFFIX).		value(player.getSettingsFleetNameSuffix()).
			endObject();
		
		// Evenements
		List<Event> events = player.getEvents();
		boolean newEvents = false;
		
		for (Event event : events)
			if (event.getDate() > player.getEventsReadDate()) {
				newEvents = true;
				break;
			}
		
		json.key(StateData.FIELD_NEW_EVENTS).		value(newEvents);
		
		// Alliance du joueur
		json.key(StateData.FIELD_ALLY);
		AllyTools.getAlly(json, player, 0);
		
		// Canaux sur le chat
		json.key(StateData.FIELD_CHAT_CHANNELS);
		ChatTools.getChannels(json, player);
		
		// Avancées
		json.key(StateData.FIELD_ADVANCEMENTS);
		AdvancementTools.getPlayerAdvancements(json, player);
		
		// Tactiques enregistrées
		json.key(StateData.FIELD_TACTICS);
		TacticTools.getPlayerTactics(json, player.getId());
		
		// Produits
		json.key(StateData.FIELD_PRODUCTS);
		ProductTools.getPlayerProducts(json, player);
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
