/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

package fr.fg.server.contract;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.data.RandomName;
import fr.fg.server.data.Slot;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.Utilities;

public class NpcHelper {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String END_OF_DIALOG = "EOD";
	
	public final static String KEY_REPUTATION = "reputation";
	
	public final static String FACTION_NONE = "none";
	
	public final static String
		FACTION_GDO = "gdo",
		FACTION_BROTHERHOOD = "brotherhood",
		FACTION_INDEPENDANT_NETWORK = "network";
	
	public final static String
		AVATAR_GDO_OFFICER = "gdo_officer",
		AVATAR_BROTHERHOOD_PARIA = "bh_paria",
		AVATAR_NETWORK_CONNECTION = "in_connection";
	
	public final static String[] ALL_FACTIONS = {
		FACTION_GDO, FACTION_BROTHERHOOD, FACTION_INDEPENDANT_NETWORK
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Noms de PNJ aléatoires
	private static List<RandomName> maleFirstNames, femaleFirstNames,
		lastNames;
	
	// Correspondance entre le nom d'une faction et son identifiant
	private static Map<Integer, String> factionsByAlly;
	
	private static int currentMaleFirstName, currentFemaleFirstName,
		currentLastName;
	
	static {
		factionsByAlly = Collections.synchronizedMap(
				new HashMap<Integer, String>());
		
		maleFirstNames = new ArrayList<RandomName>(
			DataAccess.getRandomNamesByType(RandomName.TYPE_MALE_FNAME));
		femaleFirstNames = new ArrayList<RandomName>(
			DataAccess.getRandomNamesByType(RandomName.TYPE_FEMALE_FNAME));
		lastNames = new ArrayList<RandomName>(
			DataAccess.getRandomNamesByType(RandomName.TYPE_LNAME));
		
		Collections.shuffle(maleFirstNames);
		Collections.shuffle(femaleFirstNames);
		Collections.shuffle(lastNames);
		
		currentMaleFirstName = 0;
		currentFemaleFirstName = 0;
		currentLastName = 0;
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static void initializeFactions() {
		for (String faction : ALL_FACTIONS) {
			String name = Messages.getString("contract.faction." + faction + ".name");
			
			Ally ally = DataAccess.getAllyByName(name);
			
			if (ally == null) {
				String tag = Messages.getString("contract.faction." + faction + ".tag");
				
				ally = new Ally(name, tag, Ally.ORGANIZATION_OLIGARCHY, "", 0, 0);
				ally.setAi(true);
				ally.save();
			}
			
			factionsByAlly.put(ally.getId(), faction);
		}
	}
	
	public static String getRandomFaction(String... factions) {
		return factions[(int) (Math.random() * factions.length)];
	}
	
	public static String getFactionByAlly(int idAlly) {
		return factionsByAlly.get(idAlly);
	}
	
	public static Ally getAllyFaction(String faction) {
		String tag = Messages.getString("contract.faction." + faction + ".tag");
		return DataAccess.getAllyByTag(tag);
	}
	
	// Création de PNJ
	
	public static Player createAICharacter(long idContract, String faction) {
		return createAICharacter(idContract, faction, Math.random() < .5, "");
	}
	
	public static Player createAICharacter(long idContract, String faction, boolean male, String avatar, String name) {
		Ally ally = null;
		if (faction != null && faction.length() > 0)
			ally = getAllyFaction(faction);
		
		Player aiPlayer = new Player(name, "", "", "", "");
		aiPlayer.setAi(true);
		aiPlayer.setSex(male ? Player.SEX_MALE : Player.SEX_FEMALE);
		aiPlayer.setIdContract(idContract);
		aiPlayer.setAvatar(avatar);
		aiPlayer.setSettingsFleetSkin(Utilities.random(1, 10));
		if (ally != null) {
			aiPlayer.setIdAlly(ally.getId());
			aiPlayer.setAllyRank(1);
		}
		aiPlayer.save();
		
		return aiPlayer;
	}
	
	public static Player createAICharacter(long idContract, String faction, boolean male, String avatar) {
		String name;
		int attempts = 0;
		do {
			attempts++;
			name = getRandomCharacterName(male);
		} while (DataAccess.getPlayerByLogin(name) != null && attempts < 100);
		
		if (attempts == 100) {
			throw new RuntimeException("Tous les noms possibles " +
					"pour les PNJ semblent être utilisés.");
		}
		
		return createAICharacter(idContract, faction, male, avatar, name);
	}
	
	public static String getRandomIndependantNetworkName() {
		String name;
		do {
			name = Messages.getString("contract.faction.network.npc",
				RandomStringUtils.random(1, true, false).toUpperCase() +
				RandomStringUtils.random(4, false, true));
		} while (DataAccess.getPlayerByLogin(name) != null);
		return name;
	}
	
	/**
	 * Renvoie un nom de personnage aléatoire, sous la forme <code>Prénom
	 * Nom</code>. Le nom renvoyé a 50% de chances d'être un nom masculin.
	 * 
	 * @return Un nom de personnage, de la forme <code>Prénom Nom</code>.
	 */
	public static String getRandomCharacterName() {
		return getRandomCharacterName(Math.random() < .5);
	}
	
	/**
	 * Renvoie un nom aléatoire pour un personnage masculin ou féminin, sous
	 * la forme <code>Prénom Nom</code>.
	 * 
	 * @param male <code>true</code> pour générer un nom masculin,
	 * <code>false</code> pour un nom féminin.
	 * @return Un nom de personnage, de la forme <code>Prénom Nom</code>.
	 */
	public static String getRandomCharacterName(boolean male) {
		String name = (male ? getRandomMaleFirstName() : getRandomFemaleFirstName()) +
			" " + getRandomLastName();
		Player npc = DataAccess.getPlayerByLogin(name);
		int i = 1;
		while(npc !=null)
		{
			name = name+" "+ i;
			i++;
			npc = DataAccess.getPlayerByLogin(name);
		}
		return name;
	}

	/**
	 * Renvoie un prénom masculin aléatoire.
	 * 
	 * @return Un prénom masculin aléatoire.
	 */
	public static String getRandomMaleFirstName() {
		String value = maleFirstNames.get(currentMaleFirstName).getName();
		currentMaleFirstName++;
		
		if (currentMaleFirstName == maleFirstNames.size()) {
			Collections.shuffle(maleFirstNames);
			currentMaleFirstName = 0;
		}
		
		return value;
	}
	
	/**
	 * Renvoie un prénom féminin aléatoire.
	 * 
	 * @return Un prénom féminin aléatoire.
	 */
	public static String getRandomFemaleFirstName() {
		String value = femaleFirstNames.get(currentFemaleFirstName).getName();
		currentFemaleFirstName++;
		
		if (currentFemaleFirstName == femaleFirstNames.size()) {
			Collections.shuffle(femaleFirstNames);
			currentFemaleFirstName = 0;
		}
		
		return value;
	}
	
	/**
	 * Renvoie un nom de famille aléatoire.
	 * 
	 * @return Un nom de famille aléatoire.
	 */
	public static String getRandomLastName() {
		String value = lastNames.get(currentLastName).getName();
		currentLastName++;
		
		if (currentLastName == lastNames.size()) {
			Collections.shuffle(lastNames);
			currentLastName = 0;
		}
		
		return value;
	}
	
	public static Point getNearestFreeTile(Fleet fleet) {
		return getNearestFreeTile(fleet.getArea(),
				new Point(fleet.getX(), fleet.getY()));
	}
	
	public static Point getNearestFreeTile(Area area, Point location) {
    	boolean found = false;
    	int x = 0, y = 0;
		
		for (int step = 0; step < 100 && !found; step++)
    		for (int i = -step; i <= step && !found; i++) {
    			if (area.isFreeTile(location.x + i, location.y - step, Area.NO_FLEETS | Area.NO_SYSTEMS, null)) {
    				x = location.x + i;
    				y = location.y - step;
    				found = true;
    			} else if (area.isFreeTile(location.x - step, location.y + i, Area.NO_FLEETS | Area.NO_SYSTEMS, null)) {
    				x = location.x - step;
    				y = location.y + i;
    				found = true;
    			} else if (area.isFreeTile(location.x + i, location.y + step, Area.NO_FLEETS | Area.NO_SYSTEMS, null)) {
    				x = location.x + i;
    				y = location.y + step;
    				found = true;
    			} else if (area.isFreeTile(location.x + step, location.y + i, Area.NO_FLEETS | Area.NO_SYSTEMS, null)) {
    				x = location.x + step;
    				y = location.y + i;
    				found = true;
    			}
    		}
    	
    	if (!found)
    		return null;

    	return new Point(x, y);
	}
	
	public static Fleet spawnFleet(Player owner, Area area, long idContract,
			String npcType, int shipId, int shipCount, boolean unstuckable) {
		return spawnFleet(owner, area, idContract, npcType, shipId,
				shipCount, unstuckable, owner.getLogin());
	}
	
	public static Fleet spawnFleet(Player owner, Area area, long idContract,
			String npcType, int shipId, int shipCount, boolean unstuckable,
			String fleetName) {
		Point location = getRandomAreaLocation(area);
		return spawnFleet(owner, area, idContract, npcType, location, shipId,
				shipCount, unstuckable, fleetName);
	}
	
	public static Fleet spawnFleet(Player owner, Area area, long idContract,
			String npcType, Point location, int shipId, int shipCount,
			boolean unstuckable) {
		return spawnFleet(owner, area, idContract, npcType, location, shipId,
				shipCount, unstuckable, owner.getLogin());
	}
	
	public static Fleet spawnFleet(Player owner, Area area, long idContract,
			String npcType, Point location, int shipId, int shipCount,
			boolean unstuckable, String fleetName) {
		// Génère la flotte
		Fleet spawnedFleet = new Fleet(
			owner.getLogin(),
			location.x,
			location.y,
			owner.getId(),
			area.getId());
		
		spawnedFleet.setSlot(new Slot(shipId, shipCount, true), 0);
		spawnedFleet.setIdContract(idContract);
		spawnedFleet.setNpcType(npcType);
		spawnedFleet.setUnstuckable(unstuckable);
		spawnedFleet.save();
		
		return spawnedFleet;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static Point getRandomAreaLocation(Area area) {
		// Génère la position de la flotte
		Point location = area.getRandomFreeTiles(
				5, 5, Area.NO_FLEETS | Area.NO_SYSTEMS |
				Area.NO_OBJECTS | Area.EXCEPT_PASSABLE_OBJECTS, null);
		
		if (location == null)
			return null;
		
		location.x += 2;
		location.y += 2;
		
		return location;
	}
}
