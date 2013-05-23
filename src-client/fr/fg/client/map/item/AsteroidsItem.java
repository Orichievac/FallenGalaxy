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

package fr.fg.client.map.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import fr.fg.client.core.Client;
import fr.fg.client.data.AsteroidsData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.ToolTipManager;

public class AsteroidsItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private AsteroidsData asteroidsData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AsteroidsItem(AsteroidsData asteroidsData,
			UIItemRenderingHints hints) {
		super(asteroidsData.getX(), asteroidsData.getY(), hints);
		
		this.asteroidsData = asteroidsData;
		
		// Crée le champ d'astéroides
		Element asteroids = getElement();
		DOM.setElementAttribute(asteroids, "unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		DOM.setInnerHTML(asteroids, "<div class=\"asteroidBounds\" " + //$NON-NLS-1$
				"unselectable=\"on\"></div>"); //$NON-NLS-1$
		
		updateData(asteroidsData);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		asteroidsData = null;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		AsteroidsData newAsteroidsData = (AsteroidsData) newData;
		
		if (asteroidsData.getX() != newAsteroidsData.getX() ||
			asteroidsData.getY() != newAsteroidsData.getY())
			setLocation(newAsteroidsData.getX(), newAsteroidsData.getY());
		
		if (!asteroidsData.getType().equals(newAsteroidsData.getType()))
			updateData(newAsteroidsData);
		
		asteroidsData = newAsteroidsData;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(AsteroidsData asteroidsData) {
		IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
		
		setStyleName("asteroid" + (asteroidsData.getType().equals(
			"asteroid") ? "" : " " + asteroidsData.getType()) + " env_" + areaData.getEnvironment()); //$NON-NLS-1$
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		String type = asteroidsData.getType();
		
		if (type.equals("asteroid_dense")) {
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsDense() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsDenseDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>") + "</div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (type.equals("asteroid_low_titanium") ||
				type.equals("asteroid_low_crystal") ||
				type.equals("asteroid_low_andium")) {
			int r = type.equals("asteroid_low_titanium") ?
				0 : type.equals("asteroid_low_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsLow() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsLowDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		} else if (type.equals("asteroid_vein_titanium") ||
				type.equals("asteroid_vein_crystal") ||
				type.equals("asteroid_vein_andium")) {
			int r = type.equals("asteroid_vein_titanium") ?
				0 : type.equals("asteroid_vein_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsVein() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsVeinDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_lowc_titanium") ||
				type.equals("asteroid_lowc_crystal") ||
				type.equals("asteroid_lowc_andium")) {
			int r = type.equals("asteroid_lowc_titanium") ?
				0 : type.equals("asteroid_lowc_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsLowc() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsLowcDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_mediumc_titanium") ||
				type.equals("asteroid_mediumc_crystal") ||
				type.equals("asteroid_mediumc_andium")) {
			int r = type.equals("asteroid_mediumc_titanium") ?
				0 : type.equals("asteroid_mediumc_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsMediumc() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsMediumcDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_important_titanium") ||
				type.equals("asteroid_important_crystal") ||
				type.equals("asteroid_important_andium")) {
			int r = type.equals("asteroid_important_titanium") ?
				0 : type.equals("asteroid_important_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsImportant() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsImportantDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_abondant_titanium") ||
				type.equals("asteroid_abondant_crystal") ||
				type.equals("asteroid_abondant_andium")) {
			int r = type.equals("asteroid_abondant_titanium") ?
				0 : type.equals("asteroid_abondant_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsAbondant() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsAbondantDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_pure_titanium") ||
				type.equals("asteroid_pure_crystal") ||
				type.equals("asteroid_pure_andium")) {
			int r = type.equals("asteroid_pure_titanium") ?
				0 : type.equals("asteroid_pure_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsPure() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsPureDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_concentrate_titanium") ||
				type.equals("asteroid_concentrate_crystal") ||
				type.equals("asteroid_concentrate_andium")) {
			int r = type.equals("asteroid_concentrate_titanium") ?
				0 : type.equals("asteroid_concentrate_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsConcentrate() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsConcentrateDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		}
		else if (type.equals("asteroid_avg_titanium") ||
				type.equals("asteroid_avg_crystal") ||
				type.equals("asteroid_avg_andium")) {
			int r = type.equals("asteroid_avg_titanium") ?
					0 : type.equals("asteroid_avg_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsAvg() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsAvgDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		} else if (type.equals("asteroid_high_titanium") ||
				type.equals("asteroid_high_crystal") ||
				type.equals("asteroid_high_andium")) {
			int r = type.equals("asteroid_high_titanium") ?
					0 : type.equals("asteroid_high_crystal") ? 1 : 2;
			
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroidsHigh() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsHighDesc( //$NON-NLS-1$
					"<span class=\"emphasize\">25%</span>",
					"<img class=\"resource r" + r + "\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/>") +
					"</div>", 200); //$NON-NLS-1$
		} else {
			ToolTipManager.getInstance().register(getElement().getFirstChildElement(),
				"<div class=\"title\">" + messages.asteroids() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.asteroidsDesc( //$NON-NLS-1$
						"<span class=\"emphasize\">10%</span>") + "</div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
