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

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class Update {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		TYPE_NEW_MESSAGE		=  1,
		TYPE_AREA				=  2,
		TYPE_PLAYER_FLEETS		=  4,
		TYPE_PLAYER_SYSTEMS		=  5,
		TYPE_XP					=  6,
		TYPE_CHAT				= 10,
		TYPE_NEW_NEWS			= 12,
		TYPE_CONTRACTS_STATE	= 14,
		TYPE_PLAYER_CONTRACTS	= 15,
		TYPE_NEW_EVENT			= 16,
		TYPE_CHAT_CHANNELS		= 17,
		TYPE_ALLY				= 18,
		TYPE_SERVER_SHUTDOWN	= 19,
		TYPE_INFORMATION		= 20,
		TYPE_ADVANCEMENTS		= 21,
		TYPE_EFFECT				= 22,
		TYPE_PLAYER_GENERATORS	= 23,
		TYPE_PRODUCTS			= 24,
		TYPE_PLAYER_FLEET		= 25;
	
	private final static Update
		UPDATE_AREA					= new Update(TYPE_AREA),
		UPDATE_PLAYER_FLEETS		= new Update(TYPE_PLAYER_FLEETS),
		UPDATE_PLAYER_SYSTEMS		= new Update(TYPE_PLAYER_SYSTEMS),
		UPDATE_XP					= new Update(TYPE_XP),
		UPDATE_NEW_NEWS				= new Update(TYPE_NEW_NEWS),
		UPDATE_NEW_MESSAGE			= new Update(TYPE_NEW_MESSAGE),
		UPDATE_NEW_EVENT			= new Update(TYPE_NEW_EVENT),
		UPDATE_ADVANCEMENTS			= new Update(TYPE_ADVANCEMENTS),
		UPDATE_CHAT_CHANNELS		= new Update(TYPE_CHAT_CHANNELS),
		UPDATE_CONTRACTS_STATE		= new Update(TYPE_CONTRACTS_STATE),
		UPDATE_PLAYER_CONTRACTS		= new Update(TYPE_PLAYER_CONTRACTS),
		UPDATE_PLAYER_GENERATORS	= new Update(TYPE_PLAYER_GENERATORS),
		UPDATE_PRODUCTS				= new Update(TYPE_PRODUCTS);
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private Object[] args;
	
	private long date;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Update(int type, Object... args) {
		this.type = type;
		this.args = args;
		this.date = Utilities.now();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getType() {
		return type;
	}
	
	public String getData(Player player) throws StaledUpdateException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\"type\":");
		buffer.append(type);
		buffer.append(",\"date\":");
		buffer.append(date);
		buffer.append(",\"data\":");
		buffer.append(buildUpdate(player));
		buffer.append("}");
		
		return buffer.toString();
	}
	
	public boolean equals(Object object) {
		if (this == object)
			return true;
		
		if (!(object instanceof Update))
			return false;
		
		Update update = (Update) object;
		
		if (update.getType() != getType())
			return false;
		
		if (args.length != update.args.length)
			return false;
		
		for (int i = 0; i < args.length; i++)
			if (!args[i].equals(update.args[i]))
				return false;
		
		return true;
	}
	
	public static Update getAreaUpdate() {
		return UPDATE_AREA;
	}
	
	public static Update getPlayerFleetsUpdate() {
		return UPDATE_PLAYER_FLEETS;
	}
	
	public static Update getPlayerSystemsUpdate() {
		return UPDATE_PLAYER_SYSTEMS;
	}
	
	public static Update getChatUpdate(String data) {
		return new Update(TYPE_CHAT, data);
	}
	
	public static Update getXpUpdate() {
		return UPDATE_XP;
	}
	
	public static Update getNewEventUpdate() {
		return UPDATE_NEW_EVENT;
	}
	
	public static Update getNewNewsUpdate() {
		return UPDATE_NEW_NEWS;
	}
	
	public static Update getNewMessageUpdate() {
		return UPDATE_NEW_MESSAGE;
	}
	
	public static Update getChatChannelsUpdate() {
		return UPDATE_CHAT_CHANNELS;
	}
	
	public static Update getAllyUpdate(long lastUpdate) {
		return new Update(TYPE_ALLY, lastUpdate);
	}
	
	public static Update getServerShutdownUpdate(int time) {
		return new Update(TYPE_SERVER_SHUTDOWN, time);
	}
	
	public static Update getInformationUpdate(String information) {
		return new Update(TYPE_INFORMATION, information);
	}
	
	public static Update getAdvancementsUpdate() {
		return UPDATE_ADVANCEMENTS;
	}
	
	public static Update getEffectUpdate(Effect effect) {
		return new Update(TYPE_EFFECT, effect);
	}
	
	public static Update getContractStateUpdate() {
		return UPDATE_CONTRACTS_STATE;
	}
	
	public static Update getPlayerContractsUpdate() {
		return UPDATE_PLAYER_CONTRACTS;
	}
	
	public static Update getPlayerGeneratorsUpdate() {
		return UPDATE_PLAYER_GENERATORS;
	}
	
	public static Update getProductsUpdate() {
		return UPDATE_PRODUCTS;
	}
	
	public static Update getPlayerFleetUpdate(int idFleet) {
		return new Update(TYPE_PLAYER_FLEET, idFleet);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private String buildUpdate(Player player) throws StaledUpdateException {
		switch (type) {
		case TYPE_ADVANCEMENTS:
			// Mise à jour des avancées
			return AdvancementTools.getPlayerAdvancements(null, player).toString();
		case TYPE_CONTRACTS_STATE:
			// Mise à jour des contrats
			return ContractTools.getContractsState(null, player).toString();
		case TYPE_PLAYER_CONTRACTS:
			// Mise à jour des contrats
			return ContractTools.getPlayerContracts(null, player).toString();
		case TYPE_INFORMATION:
			// Message ingame
			return new JSONStringer().value((String) args[0]).toString();
		case TYPE_EFFECT:
			// Effets spéciaux
			return EffectTools.getEffect(null, (Effect) args[0]).toString();
		case TYPE_PLAYER_FLEETS:
			// Mise à jour des flottes du joueur
			return FleetTools.getPlayerFleets(null, player).toString();
		case TYPE_PLAYER_FLEET:
			Fleet fleet = DataAccess.getFleetById((Integer) args[0]);
			if (fleet == null)
				throw new StaledUpdateException("Fleet '" + args[0] + "' does not exist.");
			return FleetTools.getPlayerFleet(null, (Integer) args[0]).toString();
		case TYPE_PLAYER_SYSTEMS:
			// Mise à jour des systèmes du joueur
			return SystemTools.getPlayerSystems(null, player).toString();
		case TYPE_PLAYER_GENERATORS:
			// Mise à jour des générateurs du joueur
			return StructureTools.getPlayerGenerators(null, player).toString();
		case TYPE_XP:
			// Mise à jour de l'XP
			return XpTools.getPlayerXp(null, player).toString();
		case TYPE_SERVER_SHUTDOWN:
			// Arrêt du serveur
			return String.valueOf(args[0]);
		case TYPE_CHAT_CHANNELS:
			// Mise à jour des canaux de discussion
			return ChatTools.getChannels(null, player).toString();
		case TYPE_NEW_EVENT:
		case TYPE_NEW_NEWS:
		case TYPE_NEW_MESSAGE:
			// Notification
			return "\"\"";
		case TYPE_CHAT:
			// Message sur le chat
			return (String) args[0];
		case TYPE_PRODUCTS:
			return ProductTools.getPlayerProducts(null, player).toString();
		case TYPE_AREA:
			// Mise à jour du secteur sur lequel le joueur est connecté
			try {
				if (player.getIdCurrentArea() != 0)
					return AreaTools.getArea(null, DataAccess.getAreaById(
						player.getIdCurrentArea()), player).toString();
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn(
						"Could not build area update.", e);
			}
			return null;
		case TYPE_ALLY:
			// Mise à jour de l'alliance
			try {
				return AllyTools.getAlly(null, player, (Long) args[0]).toString();
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn(
						"Could not build ally update.", e);
			}
			return null;
		default:
			throw new IllegalStateException(
				"No implementation defined for update: '" + type + "'.");
		}
	}
}
