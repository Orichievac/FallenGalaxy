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

package fr.fg.server.servlet.export;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.fg.server.data.Ability;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Ship;
import fr.fg.server.data.WeaponGroup;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.LoggingSystem;

@SuppressWarnings("serial")
public class ShipsServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private String outputCache;
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		synchronized (this) {
			if (outputCache == null)
				outputCache = buildOuput();
		}
		
		write(request, response, outputCache);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	public String buildOuput() {
		StringWriter writer = new StringWriter();
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		Document document = new Document(buildXml());
		
		try {
			outputter.output(document, writer);
			
			String result = writer.toString();
			return result;
		} catch (IOException e) {
			LoggingSystem.getServerLogger().warn("Could not serialise xml.", e);
			return "";
		}
	}
	
	private Element buildXml() {
		Element root = new Element("ships");
		
		for (int i = 0; i < Ship.SHIPS.length; i++) {
			if (Ship.SHIPS[i] == null || Ship.SHIPS[i].isAltered())
				continue;
			
			Ship ship = Ship.SHIPS[i];
			
			Element shipElement = new Element("ship");
			root.addContent(shipElement);
			
			addParameter(shipElement, "id", i);
			addParameter(shipElement, "name", Messages.getString("ships" + i + "[one]"));
			addParameter(shipElement, "shipClass", Messages.getString("ship.classes" + ship.getShipClass() + "[one]"));
			addParameter(shipElement, "hull", ship.getHull());
			addParameter(shipElement, "protection", ship.getProtection());
			addParameter(shipElement, "payload", ship.getPayload());
			addParameter(shipElement, "power", ship.getPower());
			addParameter(shipElement, "buildTime", ship.getBuildTime());
			addParameter(shipElement, "credit", ship.getCost()[4]);
			
			for (int j = 0; j < GameConstants.RESOURCES_COUNT; j++)
				addParameter(shipElement, "resource" + j, ship.getCost()[j]);
			
			String targets = "";
			for (int j = 0; j < ship.getTargets().length; j++)
				targets += (j > 0 ? ", " : "") + Messages.getString(
					"ship.classes" + ship.getTargets()[j]);
			addParameter(shipElement, "target", targets);

			Element weaponsElement = new Element("weapons");
			shipElement.addContent(weaponsElement);
			
			for (WeaponGroup weaponGroup : ship.getWeapons()) {
				Element weaponElement = new Element("weapon");
				weaponsElement.addContent(weaponElement);
				
				addParameter(weaponElement, "name", Messages.getString("weapon" + weaponGroup.getIdWeapon()));
				addParameter(weaponElement, "count", weaponGroup.getCount());
				addParameter(weaponElement, "minDamage", weaponGroup.getWeapon().getDamageMin());
				addParameter(weaponElement, "maxDamage", weaponGroup.getWeapon().getDamageMax());
			}
			
			Element abilitiesElement = new Element("abilities");
			shipElement.addContent(abilitiesElement);
			
			for (Ability ability : ship.getAbilities()) {
				Element abilityElement = new Element("ability");
				abilitiesElement.addContent(abilityElement);
				
				addParameter(abilityElement, "type", ability.getType());
				addParameter(abilityElement, "name", Messages.getString("ability" + ability.getType()));
				addParameter(abilityElement, "cooldown", ability.getCooldown());
				addParameter(abilityElement, "passive", ability.isPassive());
				addParameter(abilityElement, "description", ability.getDesc(i), true);
			}
		}
		
		return root;
	}
	
	private void addParameter(Element parentElement, String key, boolean value) {
		addParameter(parentElement, key, String.valueOf(value));
	}
	
	private void addParameter(Element parentElement, String key, double value) {
		addParameter(parentElement, key, String.valueOf(value));
	}
	
	private void addParameter(Element parentElement, String key, int value) {
		addParameter(parentElement, key, String.valueOf(value));
	}
	
	private void addParameter(Element parentElement, String key, String value) {
		addParameter(parentElement, key, value, false);
	}
	
	private void addParameter(Element parentElement, String key, String value, boolean useCDATA) {
		Element parameterElement = new Element("parameter");
		parentElement.addContent(parameterElement);
		
		Element keyElement = new Element("key");
		keyElement.addContent(key);
		parameterElement.addContent(keyElement);
		
		
		Element valueElement = new Element("value");
		if (useCDATA) {
			CDATA cdata = new CDATA(value);
			valueElement.addContent(cdata);
		} else {
			valueElement.addContent(value);
		}
		parameterElement.addContent(valueElement);
	}
}
