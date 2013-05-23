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

package fr.fg.client.core.tactics;

import java.util.ArrayList;

import fr.fg.client.core.ResearchManager;
import fr.fg.client.core.Utilities;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.ShipData;
import fr.fg.client.openjwt.ui.JSOptionPane;

public class TacticsTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		MODE_CHECK_SYNTAX = 0,
		MODE_LOAD = 1,
		MODE_LOAD_STRICT = 2,
		MODE_CHECK_COMPATIBILITY = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	static {
		registerTacticsBridge();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static String hashCode(FleetScheme scheme, FleetTactics tactics) {
		// Encodage des slots
		StringBuffer hash = new StringBuffer();
		
		hash.append("[[");
		
		long id = 0;
		for (int i = 0; i < 5; i++)
			id |= (long) (scheme.getShipId(i)) << (i * 8);
		
		hash.append(Utilities.toBaseString(id, 84));
		hash.append(".");
		
		for (int i = 0; i < 5; i++) {
			if (scheme.getShipId(i) != 0) {
				hash.append(Utilities.toBaseString(scheme.getCount(i), 84));
				hash.append(".");
			}
		}
		
		int front = 0;
		for (int i = 0; i < 5; i++)
			if (scheme.isFront(i))
				front |= 1 << i;
		hash.append(Utilities.toBaseString(front, 84));
		
		// Encodage de la tactique - Escarmouche
		long encodedSlots = 0;
		for (int i = 0; i < 5; i++) {
			int shipId = tactics.getShipId(FleetTactics.VIEW_SKIRMISH, i);
			
			if (shipId == -1) {
				encodedSlots |= 5l << (i * 3);
			} else {
				for (int j = 0; j < 5; j++)
					if (shipId == scheme.getShipId(j)) {
						encodedSlots |= ((long) j) << (i * 3);
						break;
					}
			}
		}
		String code = Utilities.toBaseString(encodedSlots, 84);
		while (code.length() < 3)
			code = "0" + code;
		hash.append(code);
		
		long encodedAbilities = 0;
		for (int i = 0; i < 5; i++) {
			int ability = tactics.getAbility(FleetTactics.VIEW_SKIRMISH, i);
			encodedAbilities |= ((long) (ability == - 1 ? 5 : ability)) << (i * 3);
		}
		code = Utilities.toBaseString(encodedAbilities, 84);
		while (code.length() < 3)
			code = "0" + code;
		hash.append(code);
		
		// Bataille
		encodedSlots = 0;
		for (int i = 0; i < 15; i++) {
			int shipId = tactics.getShipId(FleetTactics.VIEW_BATTLE, i);
			
			if (shipId == -1) {
				encodedSlots |= 5l << (i * 3);
			} else {
				for (int j = 0; j < 5; j++)
					if (shipId == scheme.getShipId(j)) {
						encodedSlots |= ((long) j) << (i * 3);
						break;
					}
			}
		}
		code = Utilities.toBaseString(encodedSlots, 84);
		while (code.length() < 7)
			code = "0" + code;
		hash.append(code);
		
		encodedAbilities = 0;
		for (int i = 0; i < 15; i++) {
			int ability = tactics.getAbility(FleetTactics.VIEW_BATTLE, i);
			encodedAbilities |= ((long) (ability == - 1 ? 5 : ability)) << (i * 3);
		}
		code = Utilities.toBaseString(encodedAbilities, 84);
		while (code.length() < 7)
			code = "0" + code;
		hash.append(code);
		hash.append("]]");
		
		return hash.toString();
	}
	
	public static boolean isValidHashCode(String hashCode) {
		try {
			load(hashCode, null, null, MODE_CHECK_SYNTAX, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isCompatible(String hashCode,
			FleetScheme scheme, ResearchManager researchManager) {
		try {
			load(hashCode, scheme, null, MODE_CHECK_COMPATIBILITY, researchManager);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean load(String hashCode, FleetScheme scheme,
			FleetTactics tactics, boolean strict, ResearchManager researchManager) {
		try {
			load(hashCode, scheme, tactics, strict ? MODE_LOAD_STRICT : MODE_LOAD, researchManager);
			return true;
		} catch (Exception e) {
			JSOptionPane.showMessageDialog("Tactique invalide ou " +
				"incompatible avec les vaisseaux de votre flotte / " +
				"vos recherches.", "Erreur",
				JSOptionPane.OK_OPTION, JSOptionPane.WARNING_MESSAGE, null);
			return false;
		}
	}
	
	public static String parseTacticsLinks(String html) {
		ArrayList<String> strings = new ArrayList<String>();
		
		while (html.contains("[[")) {
			int index = html.indexOf("[[");
			
			if (index > 0) {
				strings.add(html.substring(0, index));
				html = html.substring(index);
			} else {
				if (html.contains("]]")) {
					int end = html.indexOf("]]") + 2;
					strings.add(html.substring(0, end));
					html = html.substring(end);
				} else {
					strings.add(html);
					html = "";
					break;
				}
			}
		}
		
		if (html.length() > 0)
			strings.add(html);
		
		String result = "";
		
		for (String string : strings) {
			if (!string.startsWith("[[") || !string.contains("]]")) {
				result += string;
			} else {
				if (TacticsTools.isValidHashCode(string)) {
					result += "<a onclick=\"showTactics('" + string +
						"');\">[Modèle de capacités]</a>";
				} else {
					result += string;
				}
			}
		}
		
		return result;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	

	private static void load(String hashCode, FleetScheme scheme,
			FleetTactics tactics, int mode, ResearchManager researchManager)
			throws Exception {
		if (!hashCode.substring(0, 2).equals("[[") ||
				!hashCode.substring(hashCode.length() - 2).equals("]]") ||
				!hashCode.contains("."))
			throw new Exception("Invalid hash code (Err 01).");
		
		hashCode = hashCode.substring(2, hashCode.length() - 2);
		
		long shipsCode = Utilities.parseBase(hashCode.substring(0, hashCode.indexOf(".")), 84);
		
		// Id des vaisseaux
		int[] shipsId = new int[5];
		int slotsCount = 0;
		for (int i = 0; i < 5; i++) {
			shipsId[i] = (int) ((shipsCode >> (i * 8)) & 0xff);
			
			if (shipsId[i] != 0 && ShipData.SHIPS[shipsId[i]] == null)
				throw new Exception("Invalid hash code (Err 02).");
			
			if (shipsId[i] > 0)
				slotsCount++;
		}
		
		hashCode = hashCode.substring(hashCode.indexOf(".") + 1);
		
		// Nombre de vaisseaux
		long[] shipsCount = new long[5];
		for (int i = 0; i < 5; i++) {
			if (shipsId[i] > 0) {
				if (!hashCode.contains("."))
					throw new Exception("Invalid hash code (Err 03).");
				
				shipsCount[i] = Utilities.parseBase(hashCode.substring(0, hashCode.indexOf(".")), 84);
				
				hashCode = hashCode.substring(hashCode.indexOf(".") + 1);
			}
		}
		
		// Position des vaisseaux
		if (hashCode.length() == 0)
			throw new Exception("Invalid hash code (Err 04).");
		
		boolean[] shipsFront = new boolean[5];
		int frontCode = (int) Utilities.parseBase(hashCode.substring(0, 1), 84);
		for (int i = 0; i < 5; i++) {
			shipsFront[i] = (frontCode & (1 << i)) != 0;
		}
		
		hashCode = hashCode.substring(1);
		if (hashCode.length() != 20)
			throw new Exception("Invalid hash code (Err 05).");
		
		// Tactique - escarmouche
		int[] skirmishSlots = new int[5];
		int[] skirmishAbilities = new int[5];
		
		long slotsCode = Utilities.parseBase(hashCode.substring(0, 3), 84);
		for (int i = 0; i < skirmishSlots.length; i++) {
			int slot = (int) ((slotsCode >> (3 * i)) & 7);
			
			if (slot != 5 && (slot < 0 || slot >= shipsId.length || shipsId[slot] == 0))
				throw new Exception("Invalid hash code (Err 06).");
			
			skirmishSlots[i] = slot == 5 ? -1 : slot;
		}
		
		long abilitiesCode = Utilities.parseBase(hashCode.substring(3, 6), 84);
		for (int i = 0; i < skirmishAbilities.length; i++) {
			int ability = (int) ((abilitiesCode >> (3 * i)) & 7);
			
			if (ability != 5) {
				AbilityData[] abilities = ShipData.SHIPS[shipsId[skirmishSlots[i]]].getAbilities();
				
				if (ability < 0 || ability >= abilities.length ||
						abilities[ability].isPassive())
					throw new Exception("Invalid hash code (Err 07).");
			}
			
			skirmishAbilities[i] = ability == 5 ? -1 : ability;
		}
		
		// Tactique - bataille
		int[] battleSlots = new int[15];
		int[] battleAbilities = new int[15];
		
		slotsCode = Utilities.parseBase(hashCode.substring(6, 13), 84);
		for (int i = 0; i < battleSlots.length; i++) {
			int slot = (int) ((slotsCode >> (3 * i)) & 7);
			
			if (slot != 5 && (slot < 0 || slot >= shipsId.length || shipsId[slot] == 0))
				throw new Exception("Invalid hash code (Err 08).");
			
			battleSlots[i] = slot == 5 ? -1 : slot;
		}
		
		abilitiesCode = Utilities.parseBase(hashCode.substring(13, 20), 84);
		for (int i = 0; i < battleAbilities.length; i++) {
			int ability = (int) ((abilitiesCode >> (3 * i)) & 7);
			
			if (ability != 5) {
				AbilityData[] abilities = ShipData.SHIPS[shipsId[battleSlots[i]]].getAbilities();
				
				if (ability < 0 || ability >= abilities.length ||
						abilities[ability].isPassive())
					throw new Exception("Invalid hash code (Err 09).");
			}
			
			battleAbilities[i] = ability == 5 ? -1 : ability;
		}
		
		// Charge la tactique
		if (mode != MODE_CHECK_SYNTAX) {
			int[] mapping = new int[shipsId.length];
			for (int i = 0; i < mapping.length; i++)
				mapping[i] = -1;
			
			if (mode == MODE_LOAD_STRICT || mode == MODE_CHECK_COMPATIBILITY) {
				// Vérifie que la flotte a des vaisseaux compatibles avec la
				// tactique
				for (int i = 0; i < shipsId.length; i++) {
					if (shipsId[i] != 0) {
						boolean found = false;
						
						for (int j = 0; j < shipsId.length; j++) {
							if (shipsId[i] == scheme.getShipId(j)) {
								found = true;
								mapping[i] = j;
								break;
							}
						}
						
						if (ShipData.SHIPS[shipsId[i]].getShipClass() !=
								ShipData.FREIGHTER && !found)
							throw new Exception("Incompatible ships.");
					}
				}
				
				for (int i = 0; i < shipsId.length; i++) {
					if (scheme.getShipId(i) != 0 &&
							ShipData.SHIPS[scheme.getShipId(i)].getShipClass() !=
								ShipData.FREIGHTER) {
						boolean found = false;
						
						for (int j = 0; j < shipsId.length; j++) {
							if (scheme.getShipId(i) == shipsId[j]) {
								found = true;
								break;
							}
						}
						
						if (!found)
							throw new Exception("Incompatible ships.");
					}
				}
				
				// Vérifie que les capacités ont été recherchées
				if (researchManager != null) {
					for (int i = 0; i < 5; i++) {
						if (skirmishSlots[i] != -1) {
							ShipData ship = ShipData.SHIPS[shipsId[skirmishSlots[i]]];
							
							if (skirmishAbilities[i] != -1) {
								AbilityData ability = ship.getAbilities()[skirmishAbilities[i]];
								
								for (int requirement : ability.getRequirements())
									if (!researchManager.hasResearchedTechnology(requirement))
										throw new Exception("Ability not available.");
							}
						}
					}
					
					for (int i = 0; i < 15; i++) {
						if (battleSlots[i] != -1) {
							ShipData ship = ShipData.SHIPS[shipsId[battleSlots[i]]];
	
							if (battleAbilities[i] != -1) {
								AbilityData ability = ship.getAbilities()[battleAbilities[i]];
								
								for (int requirement : ability.getRequirements())
									if (!researchManager.hasResearchedTechnology(requirement))
										throw new Exception("Ability not available.");
							}
						}
					}
				}
			}
			
			if (mode != MODE_CHECK_COMPATIBILITY) {
				// Force le placement sur la ligne de front
				for (int i = 0; i < 5; i++)
					scheme.setFront(i, true);
				
				// Charge le placement
				for (int i = 0; i < 5; i++) {
					if (mode == MODE_LOAD_STRICT) {
						if (mapping[i] != -1)
							scheme.setFront(mapping[i], shipsFront[i]);
					} else if (mode == MODE_LOAD) {
						scheme.setShip(i, shipsId[i], shipsCount[i], shipsFront[i]);
					}
				}
				
				// Vérifie que les cargos ont été placées en arrière ligne
				if (mode == MODE_LOAD_STRICT) {
					int freightersCount = 0, backFreightersCount = 0, count = 0;
					for (int i = 0; i < 5; i++)
						if (scheme.getShipId(i) != 0) {
							count++;
							
							if (ShipData.SHIPS[scheme.getShipId(i)].getShipClass() == ShipData.FREIGHTER) {
								freightersCount++;
								
								if (!scheme.isFront(i))
									backFreightersCount++;
							}
						}
					
					// Force le placement pour que les cargos se retrouvent
					// derrière
					int expectedBackFreightersCount = Math.min(count / 2, freightersCount);
					
					if (expectedBackFreightersCount != backFreightersCount) {
						for (int i = 0; i < 5; i++)
							scheme.setFront(i, true);
						
						count = 0;
						
						for (int i = 0; i < 5; i++) {
							if (scheme.getShipId(i) != 0 && ShipData.SHIPS[scheme.getShipId(i
									)].getShipClass() == ShipData.FREIGHTER) {
								scheme.setFront(i, false);
								
								if (++count == expectedBackFreightersCount)
									break;
							}
						}
					}
				}
				
				for (int i = 0; i < 5; i++) {
					tactics.setAction(FleetTactics.VIEW_SKIRMISH, i,
						skirmishSlots[i] == -1 ? -1 : shipsId[skirmishSlots[i]],
						skirmishSlots[i] == -1 ? 0 : shipsCount[skirmishSlots[i]],
						skirmishAbilities[i]);
				}
				
				for (int i = 0; i < 15; i++) {
					tactics.setAction(FleetTactics.VIEW_BATTLE, i,
						battleSlots[i] == -1 ? -1 : shipsId[battleSlots[i]],
						battleSlots[i] == -1 ? 0 : shipsCount[battleSlots[i]],
						battleAbilities[i]);
				}
			}
		}
	}
	
	private static native void registerTacticsBridge() /*-{
		$wnd.showTactics = function(tactics) {
			@fr.fg.client.core.tactics.TacticsShowCase::show(Ljava/lang/String;)(tactics);
		};
	}-*/;
}
